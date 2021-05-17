package com.askwinston.exception;

import lombok.Data;

@Data
public class AccountExistsException extends RuntimeException{
    private String email;
    public AccountExistsException(String message, String email){
        super(message);
        this.email = email;
    }
}
