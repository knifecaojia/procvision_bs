package com.imustsz.cilent.domain.dto;

import lombok.Data;

@Data
public class ProcessDTO {
    private String work_order_code;
    private String step_code;
    private String step_status;
    private String object_name;
    private String data;
}
