package com.imustsz.craft.mapper;

import com.imustsz.craft.domain.Craft;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工艺信息Mapper接口
 * 
 * @author imustsz
 * @date 2025-12-18
 */
public interface CraftMapper 
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
     * 删除工艺信息
     * 
     * @param id 工艺信息主键
     * @return 结果
     */
    public int deleteCraftById(Long id);

    /**
     * 批量删除工艺信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCraftByIds(Long[] ids);

    Long selectCraftIdByCodeAndVersion(@Param("code") String code, @Param("version") String version);
}
