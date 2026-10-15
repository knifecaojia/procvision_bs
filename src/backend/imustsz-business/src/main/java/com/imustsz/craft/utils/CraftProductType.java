package com.imustsz.craft.utils;

import java.util.Locale;

/** 工艺和任务导入共用产品类型识别，沿用工艺编号中的 TX/BJ/MZ 标识。 */
public final class CraftProductType {

    private CraftProductType() { }

    public static int fromCraftCode(String craftCode) {
        if (craftCode == null) return -1;
        String code = craftCode.toUpperCase(Locale.ROOT);
        if (code.contains("TX")) return 0;
        if (code.contains("BJ")) return 1;
        if (code.contains("MZ")) return 2;
        return -1;
    }
}
