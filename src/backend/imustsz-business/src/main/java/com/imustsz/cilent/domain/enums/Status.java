package com.imustsz.cilent.domain.enums;

public enum Status {
    PENDING(1, "PENDING"),
    RUNNING(2, "PROCESSING"),
    COMPLETED(3, "COMPLETED"),
    FAILED(4, "FAILED");

    private final int code;
    private final String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getByCode(int code) {
        for (Status status : Status.values()) {
            if (status.getCode() == code) {
                return status.getMessage();
            }
        }

        throw new IllegalArgumentException("无效的状态码：" + code);
    }
}
