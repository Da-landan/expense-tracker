package com.dalandan.expense_tracker.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message){
        super(message);
    }
}
