package com.imustsz.cilent.domain.dto;

import lombok.Data;

@Data
public class TaskSelectDTO {

    private Integer[] status;

    private String task_no;

    private String prod_order_no;

    private String[] craft_no;

    private String process_name;

    private TimeRangeDTO time_range;

    private PaginationParams pagination;

}
