package com.imustsz.craft.mapper;

import org.apache.ibatis.annotations.*;
import com.imustsz.craft.domain.BizStep;

/**
 * Independent trial persistence: atomically replace image pair and original-pixel coordinates.
 */
public interface LabelTrialMapper {
    @Select("SELECT id, code, content, guide_map_url AS guideMapUrl, guide_info AS guideInfo FROM biz_step WHERE id=#{id}")
    BizStep read(Long id);

    @Update("UPDATE biz_step SET guide_map_url=#{images}, guide_info=#{coords} WHERE id=#{id} AND guide_map_url <=> #{oldImages} AND guide_info <=> #{oldCoords}")
    int save(@Param("id") Long id, @Param("images") String images, @Param("coords") String coords, @Param("oldImages") String oldImages, @Param("oldCoords") String oldCoords);
}
