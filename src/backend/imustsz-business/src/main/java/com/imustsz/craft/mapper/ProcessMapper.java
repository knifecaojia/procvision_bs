package com.imustsz.craft.mapper;

import com.imustsz.craft.domain.Process;

import java.util.List;

/**
 * 工序信息Mapper接口
 * 
 * @author imustsz
 * @date 2025-12-18
 */
public interface ProcessMapper 
{
    /**
     * 查询工序信息
     * 
     * @param id 工序信息主键
     * @return 工序信息
     */
    public Process selectProcessById(Long id);

    /**
     * 查询工序信息列表
     * 
     * @param process 工序信息
     * @return 工序信息集合
     */
    public List<Process> selectProcessList(Process process);

    /**
     * 新增工序信息
     * 
     * @param process 工序信息
     * @return 结果
     */
    public int insertProcess(Process process);

    /**
     * 修改工序信息
     * 
     * @param process 工序信息
     * @return 结果
     */
    public int updateProcess(Process process);

    /**
     * 删除工序信息
     * 
     * @param id 工序信息主键
     * @return 结果
     */
    public int deleteProcessById(Long id);

    /**
     * 批量删除工序信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteProcessByIds(Long[] ids);
}
