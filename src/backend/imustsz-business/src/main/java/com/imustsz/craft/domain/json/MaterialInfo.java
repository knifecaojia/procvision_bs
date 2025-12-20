package com.imustsz.craft.domain.json;

import lombok.Data;

@Data
public class MaterialInfo {
    private String materialNo;
    private String materialName;
    private int materialQuantity;
    private String materialUnit;
    private String errorPreventionMark;
}
