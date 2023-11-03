package com.bytetype.amanises.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("Error: User not found!");
    }
}
