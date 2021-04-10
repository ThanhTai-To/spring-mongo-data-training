package com.pycogroup.superblog.exception;

import lombok.Getter;

import java.util.Date;

public class ExceptionResponse {
    @Getter
    private Date timestamp;
    @Getter
    private String message;
    @Getter
    private String details;
    @Getter
    private Integer responseCode;

    public ExceptionResponse(Date timestamp, String message, String details, Integer responseCode) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.responseCode = responseCode;
    }
}
