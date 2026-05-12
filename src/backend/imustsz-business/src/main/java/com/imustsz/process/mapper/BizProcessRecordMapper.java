package com.imustsz.process.mapper;

import java.util.List;
import java.util.Map;

import com.imustsz.framework.aspectj.AutoFill;
import com.imustsz.process.domain.BizProcessRecord;
import com.imustsz.process.domain.UniqueRecordParams;
import org.apache.ibatis.annotations.Param;

/**
 * 过程记录Mapper接口
 * 
 * @author imustsz
 * @date 2025-12-22
 */
public interface BizProcessRecordMapper 
{
    /**
     * 查询过程记录
     * 
     * @param id 过程记录主键
     * @return 过程记录
     */
    public BizProcessRecord selectBizProcessRecordById(Long id);

    /**
     * 查询过程记录列表
     * 
     * @param bizProcessRecord 过程记录
     * @return 过程记录集合
     */
    public List<BizProcessRecord> selectBizProcessRecordList(BizProcessRecord bizProcessRecord);

    /**
     * 新增过程记录
     * 
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    @AutoFill("insert")
    public int insertBizProcessRecord(BizProcessRecord bizProcessRecord);

    /**
     * 修改过程记录
     * 
     * @param bizProcessRecord 过程记录
     * @return 结果
     */
    @AutoFill("update")
    public int updateBizProcessRecord(BizProcessRecord bizProcessRecord);

    /**
     * 删除过程记录
     * 
     * @param id 过程记录主键
     * @return 结果
     */
    public int deleteBizProcessRecordById(Long id);

    /**
     * 批量删除过程记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizProcessRecordByIds(Long[] ids);

    List<BizProcessRecord> selectRecordByTaskNo(String taskNo);

    BizProcessRecord selectRecordByTaskNoAndProcessCodeAndStepCode(@Param("taskNo") String taskNo,@Param("processCode")String processCode, @Param("stepCode") String stepCode);

    BizProcessRecord selectBizProcessRecordByTaskNoAndStepCode(@Param("taskNo") String taskNo, @Param("stepCode")String stepCode);

    int deleteBizProcessRecordByTaskNo(String workOrderCode);

    List<UniqueRecordParams> selectBizProcessRecords(@Param("workOrderCode") String taskNo);

    List<BizProcessRecord> selectBizProcessRecordByOrderAndProcessCode(@Param("workOrderCode") String workOrderCode, @Param("processCode") String processCode, @Param("stepStatus") Integer stepStatus);

    BizProcessRecord selectBizProcessRecordByOrderAndProcessCodeAndStepNo(@Param("workOrderCode") String workOrderCode, @Param("processCode") String processCode, @Param("stepNo") String stepNo);

    List<BizProcessRecord> selectRecordByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectAlgResultData(@Param("startDate") String startDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectNgStepStats(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
