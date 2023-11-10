package com.bytetype.amanises.exception;

public class UserExistException extends Exception {

    public UserExistException() {
        super("Error: User is existed!");
    }
}
