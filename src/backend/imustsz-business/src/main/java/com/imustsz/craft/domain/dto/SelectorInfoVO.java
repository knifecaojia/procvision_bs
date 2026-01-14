package com.imustsz.craft.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class SelectorInfoVO {
    private String value;
    private String label;
    private List<SelectorInfoVO> children;
}
