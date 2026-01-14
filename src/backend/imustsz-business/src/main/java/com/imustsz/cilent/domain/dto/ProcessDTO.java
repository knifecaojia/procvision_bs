package com.imustsz.cilent.domain.dto;

import lombok.Data;

@Data
public class ProcessDTO {
    private String task_no;
    private String step_code;
    private Integer step_status;
    private String object_name;
    private String data;
}
