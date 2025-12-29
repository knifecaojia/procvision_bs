package com.imustsz.cilent.domain.dto;

import lombok.Data;

@Data
public class ResultDTO {
    private String work_order_code;
    private Integer result_status;
    private String object_name;
    private String summary_data;
}
