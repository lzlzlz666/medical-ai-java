package com.lz.exception;

public class AccountPasswordError extends RuntimeException {
    public AccountPasswordError() {}

    public AccountPasswordError(String message) {
        super(message);
    }
}
