package com.imustsz.cilent.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imustsz.craft.domain.json.MaterialInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkOrderVO {
    private String task_no;
    private String craft_no;
    private String craft_version;
    private String craft_name;
    private String process_code;
    private String process_name;
    private String process_desc;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date start_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date end_time;
    private String worker_code;
    private String worker_name;
    private Integer status;
    private Long algorithm_id;
    private String prod_order_no;
    private List<StepVO> step_infos;
    private List<MaterialInfo> material_list;
}
