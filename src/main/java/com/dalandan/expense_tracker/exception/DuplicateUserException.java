package com.dalandan.expense_tracker.exception;

public class DuplicateUserException extends RuntimeException{

    public DuplicateUserException (String message){
        super(message);
    }

}
