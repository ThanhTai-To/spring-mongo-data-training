package com.pycogroup.superblog.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String exception) {
        super(exception);
    }
}
