package com.imustsz.process.domain;

import lombok.Data;

@Data
public class AlgResultJson {
    private String status;
    private String message;
    private AlgDataJson data;
}
