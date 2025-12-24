package com.imustsz.cilent.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkOrderVO {
    private String work_order_code;
    private String craft_code;
    private String craft_version;
    private String craft_name;
    private String process_code;
    private String process_name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date start_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date end_time;
    private String worker_code;
    private String worker_name;
    private String status;
    private String algorithm_code;
    private String algorithm_name;
    private String algorithm_version;
    private List<StepVO> step_infos;
}
