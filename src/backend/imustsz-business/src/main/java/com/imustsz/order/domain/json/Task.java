package com.imustsz.order.domain.json;

import lombok.Data;

@Data
public class Task {
    private String prod_order_no;
    private String prod_batch_no;
    private String worker_code;
    private String worker_name;
    private String project_no;
    private String craft_no;
    private String craft_version;
    private String proceress_no;
    private String proceress_name;
    private String planned_start_time;
    private String planned_end_time;
    private String material_no;
    private String material_name;
}
