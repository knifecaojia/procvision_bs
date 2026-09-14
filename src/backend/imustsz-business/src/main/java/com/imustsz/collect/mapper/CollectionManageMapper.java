package com.imustsz.collect.mapper;
import com.imustsz.collect.domain.BizDataCollection;
import com.imustsz.collect.domain.CollectionManageQuery;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface CollectionManageMapper {
    List<BizDataCollection> selectManagedList(@Param("q") CollectionManageQuery query);
    List<BizDataCollection> selectManagedExport(@Param("q") CollectionManageQuery query, @Param("ids") List<Long> ids);
    BizDataCollection selectManagedById(@Param("id") Long id);
    int replaceManagedImage(@Param("id") Long id, @Param("oldPath") String oldPath,
        @Param("newPath") String newPath, @Param("username") String username);
}
