package com.imustsz.cilent.domain.dto;

import lombok.Data;

@Data
public class RecordPageDTO {
    Integer pageNum;
    Integer pageSize;
    Integer status;
}
