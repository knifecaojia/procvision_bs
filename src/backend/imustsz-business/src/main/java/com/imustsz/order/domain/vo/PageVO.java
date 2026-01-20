package com.imustsz.order.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO {
    List<?> list;
    int total;
}
