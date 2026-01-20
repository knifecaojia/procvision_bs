package com.imustsz.craft.domain.json;

import lombok.Data;

import java.util.List;

@Data
public class CrackProcess {
    private CrackInfo crackInfo;
    private List<ProcessMMO> processList;
}
