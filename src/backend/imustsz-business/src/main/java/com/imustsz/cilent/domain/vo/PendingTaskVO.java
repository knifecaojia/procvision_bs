package com.imustsz.cilent.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingTaskVO {

    private String workOrderNo;

    private String processNo;

    private String operationNo;

    private String status;
}
