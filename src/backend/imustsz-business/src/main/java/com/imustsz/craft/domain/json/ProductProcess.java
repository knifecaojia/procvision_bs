package com.imustsz.craft.domain.json;

import lombok.Data;

import java.util.List;

@Data
public class ProductProcess {
    private ProcessInfo processInfo;
    private List<Operation> operationList;
}
