package com.imustsz.craft.service;

import com.imustsz.craft.domain.Craft;
import com.imustsz.craft.domain.json.ProductProcess;

import java.util.List;

/**
 * 工艺信息Service接口
 * 
 * @author imustsz
 * @date 2025-12-18
 */
public interface ICraftService 
{
    /**
     * 查询工艺信息
     * 
     * @param id 工艺信息主键
     * @return 工艺信息
     */
    public Craft selectCraftById(Long id);

    /**
     * 查询工艺信息列表
     * 
     * @param craft 工艺信息
     * @return 工艺信息集合
     */
    public List<Craft> selectCraftList(Craft craft);

    /**
     * 新增工艺信息
     * 
     * @param craft 工艺信息
     * @return 结果
     */
    public int insertCraft(Craft craft);

    /**
     * 修改工艺信息
     * 
     * @param craft 工艺信息
     * @return 结果
     */
    public int updateCraft(Craft craft);

    /**
     * 批量删除工艺信息
     * 
     * @param ids 需要删除的工艺信息主键集合
     * @return 结果
     */
    public int deleteCraftByIds(Long[] ids);

    /**
     * 删除工艺信息信息
     * 
     * @param id 工艺信息主键
     * @return 结果
     */
    public int deleteCraftById(Long id);

    void importCraftFromMMo(ProductProcess productProcess);
}
