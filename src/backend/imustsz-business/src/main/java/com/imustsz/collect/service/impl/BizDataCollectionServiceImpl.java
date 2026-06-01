package com.imustsz.collect.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.imustsz.common.utils.bean.MinioUtils;
import com.imustsz.order.domain.dto.FinishedOrderDTO;
import com.imustsz.order.service.IBizWorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.imustsz.collect.mapper.BizDataCollectionMapper;
import com.imustsz.collect.domain.BizDataCollection;
import com.imustsz.collect.service.IBizDataCollectionService;

/**
 * 数据采集Service业务层处理
 *
 * @author imustsz
 * @date 2026-01-20
 */
@Service
public class BizDataCollectionServiceImpl implements IBizDataCollectionService {

    @Autowired
    private BizDataCollectionMapper bizDataCollectionMapper;

    @Autowired
    private IBizWorkOrderService bizWorkOrderService;

    @Autowired
    private MinioUtils minioUtils;

    /**
     * 查询数据采集
     *
     * @param id 数据采集主键
     * @return 数据采集
     */
    @Override
    public BizDataCollection selectBizDataCollectionById(Long id) {
        return bizDataCollectionMapper.selectBizDataCollectionById(id);
    }

    /**
     * 查询数据采集列表
     *
     * @param bizDataCollection 数据采集
     * @return 数据采集
     */
    @Override
    public List<BizDataCollection> selectBizDataCollectionList(BizDataCollection bizDataCollection) {
        bizDataCollection.setType(1);
        List<BizDataCollection> bizDataCollections = bizDataCollectionMapper.selectBizDataCollectionList(bizDataCollection);
        return bizDataCollections.stream().peek(data -> {
            try {
                String[] split = data.getImagePath().replace("\"", "").replace("[", "").replace("]", "").split(",");
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < split.length; i++) {

                    if (i != split.length - 1) {
                        sb.append(minioUtils.getPresignedUrl(split[i])).append(",");
                    } else sb.append(minioUtils.getPresignedUrl(split[i]));
                }
                data.setImagePath(sb.toString());
                if (data.getLabelImage() != null)
                    data.setLabelImage(minioUtils.getPresignedUrl(data.getLabelImage()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * 新增数据采集
     *
     * @param bizDataCollection 数据采集
     * @return 结果
     */
    @Override
    public int insertBizDataCollection(BizDataCollection bizDataCollection) {
        BizDataCollection data = bizDataCollectionMapper.checkData(bizDataCollection.getData());
        if (data != null) throw new RuntimeException("产品信息已存在");
        return bizDataCollectionMapper.insertBizDataCollection(bizDataCollection);
    }

    /**
     * 修改数据采集
     *
     * @param bizDataCollection 数据采集
     * @return 结果
     */
    @Override
    public int updateBizDataCollection(BizDataCollection bizDataCollection) {
        return bizDataCollectionMapper.updateBizDataCollection(bizDataCollection);
    }

    /**
     * 批量删除数据采集
     *
     * @param ids 需要删除的数据采集主键
     * @return 结果
     */
    @Override
    public int deleteBizDataCollectionByIds(Long[] ids) {
        return bizDataCollectionMapper.deleteBizDataCollectionByIds(ids);
    }

    /**
     * 删除数据采集信息
     *
     * @param id 数据采集主键
     * @return 结果
     */
    @Override
    public int deleteBizDataCollectionById(Long id) {
        return bizDataCollectionMapper.deleteBizDataCollectionById(id);
    }

    @Override
    public BizDataCollection checkProduction(String productionInfo) {
        return bizDataCollectionMapper.checkData(productionInfo);
    }
}
