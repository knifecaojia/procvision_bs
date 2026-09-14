package com.imustsz.collect.domain;
import com.imustsz.common.exception.ServiceException;
import lombok.Data;
import java.util.Locale;
@Data
public class CollectionStorageRequest {
    private Long id;
    private String format;
    private Integer quality;
    public void validate() {
        if (id == null || id <= 0) throw new ServiceException("请选择采集记录");
        format = format == null ? "" : format.trim().toLowerCase(Locale.ROOT);
        if ("jpg".equals(format)) format = "jpeg";
        if (!"jpeg".equals(format) && !"png".equals(format)) throw new ServiceException("目标格式仅支持JPEG或PNG");
        if ("jpeg".equals(format) && (quality == null || quality < 1 || quality > 100)) throw new ServiceException("JPEG质量必须为1至100");
        if ("png".equals(format)) quality = null;
    }
}
