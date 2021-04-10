package com.pycogroup.superblog.exception;

public class AlreadyExistedException extends RuntimeException {
    public AlreadyExistedException(String exception) {
        super(exception);
    }
}
