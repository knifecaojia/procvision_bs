package com.imustsz.collect.domain;
import lombok.Data;
import java.util.List;
@Data
public class CollectionExportRequest {
    private List<Long> ids;
    private CollectionManageQuery query;
}
