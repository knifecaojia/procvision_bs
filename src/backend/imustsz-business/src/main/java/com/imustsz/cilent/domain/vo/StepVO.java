package com.imustsz.cilent.domain.vo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

@Data
public class StepVO {
    private static final ObjectMapper GUIDE_INFO_MAPPER = new ObjectMapper();
    private String step_code;
    private String step_name;
    private String step_content;
    private String guide_url;
    private String guide_info;

    // 对外工步数据沿用原坐标格式，兼容此前写入的界面内部标识。
    public String getGuide_info() {
        if (guide_info == null || !guide_info.contains("\"sourceBoxId\"")) {
            return guide_info;
        }
        try {
            JsonNode groups = GUIDE_INFO_MAPPER.readTree(guide_info);
            if (!groups.isArray()) return guide_info;
            for (JsonNode group : groups) {
                for (JsonNode pos : group.path("posList")) {
                    if (pos instanceof ObjectNode) ((ObjectNode) pos).remove("sourceBoxId");
                }
            }
            return GUIDE_INFO_MAPPER.writeValueAsString(groups);
        } catch (Exception e) {
            return guide_info;
        }
    }
}
