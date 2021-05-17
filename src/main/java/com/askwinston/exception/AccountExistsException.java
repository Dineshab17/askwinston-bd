package com.askwinston.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT, reason="Account Already Exist")
public class AccountExistsException extends RuntimeException{
    private String email;
    public AccountExistsException(String message, String email){
        super(message);
        this.email = email;
    }
}
