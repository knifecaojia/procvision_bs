package com.imustsz.utils;

import com.imustsz.collect.domain.BizDataCollection;
import com.imustsz.collect.mapper.BizDataCollectionMapper;
import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.domain.dto.FinishedOrderDTO;
import com.imustsz.order.service.IBizWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AutoUploadSchedule {

    @Autowired
    BizDataCollectionMapper dataCollectionMapper;

    @Autowired
    IBizWorkOrderService bizWorkOrderService;
    @Autowired
    private MinioUtils minioUtils;

    @Scheduled(cron = "0 */1 * * * *")
    public void testTask(){
        log.info("准备上传图片给MOM...");

        List<BizDataCollection> data = dataCollectionMapper.selectUploadData();

        if (data.isEmpty()){
            log.info("当前没有照片需要上传...");
            return;
        }

        data.forEach(col -> {
            String[] split = col.getData().split("_");
            FinishedOrderDTO finishedOrderDTO = new FinishedOrderDTO();
            finishedOrderDTO.setWorkOrderCode(split[0]);
            finishedOrderDTO.setProcessCode(split[1]);

            finishedOrderDTO.setObjectName(col.getImagePath());

            log.info("图片信息:\n{}",finishedOrderDTO);

            int status = 0;
            try {
                status = bizWorkOrderService.uploadToMOM(finishedOrderDTO);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (status == 1) {
                col.setUploaded(0);
                dataCollectionMapper.updateBizDataCollection(col);
                log.info("图片上传成功");
            }

        });
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void executeCleanup() {
        log.info("==== 开始执行 MinIO 大图片及 DB 记录清理任务 ====");

        int batchSize = 200; // 由于每张图 50MB，批次不宜过大，200条=10GB的物理清理动作
        boolean hasMore = true;
        int totalDeleted = 0;

        while (hasMore) {
            try {
                // 1. MyBatis 查询待清理记录
                List<BizDataCollection> pendingList = dataCollectionMapper.selectDeleteData(batchSize);

                if (pendingList == null || pendingList.isEmpty()) {
                    hasMore = false;
                    continue;
                }

                // 2. 提取需要删除的 MinIO objectName
                List<String> objectNames = pendingList.stream()
                        .map(BizDataCollection::getImagePath)
                        .collect(Collectors.toList());

                // 3. 请求 MinIO 进行批量物理删除
                List<String> failedObjects = minioUtils.deleteObjectsBatch(objectNames);

                // 4. 过滤出成功在 MinIO 端删除的图片 ID
                List<Long> successIds = pendingList.stream()
                        .filter(img -> !failedObjects.contains(img.getImagePath()))
                        .map(BizDataCollection::getId)
                        .collect(Collectors.toList());

                // 5. 将 MinIO 中删除成功的记录，从数据库中做物理删除 (硬删除)
                if (!successIds.isEmpty()) {
                    int rows = dataCollectionMapper.deleteBizDataCollectionByIds(successIds.toArray(new Long[0]));
                    totalDeleted += rows;
                    log.info("本批次成功清理 MinIO 图片及 DB 记录 {} 条", rows);
                }

                if (!failedObjects.isEmpty()) {
                    log.warn("本批次有 {} 个文件在 MinIO 端删除失败，跳过 DB 删除等待下次重试", failedObjects.size());
                }

                // 6. 核心安全防护：休眠机制
                // 每次清理 10GB 数据后休眠 500 毫秒，防止 MinIO 磁盘 IO 打满和网络带宽被榨干
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                log.error("清理任务被中断", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("执行清理任务时发生异常", e);
                // 发生未知异常退出循环，等待第二天再跑，防止死循环导致内存溢出
                break;
            }
        }

        log.info("==== 图片清理任务执行完毕，本次共清理数据 {} 条 ====", totalDeleted);
    }

}
