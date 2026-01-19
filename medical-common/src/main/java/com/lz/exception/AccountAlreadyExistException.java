package com.lz.exception;

public class AccountAlreadyExistException extends BaseException {

    public AccountAlreadyExistException() {
    }

    public AccountAlreadyExistException(String message) {
        super(message);
    }
}
