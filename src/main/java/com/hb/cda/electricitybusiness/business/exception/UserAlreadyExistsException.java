package com.hb.cda.electricitybusiness.business.exception;

public class UserAlreadyExistsException extends BusinessException{
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
