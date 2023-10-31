package com.bytetype.amanises.exception;

public class NameExistException extends Exception {
    public NameExistException() {
        super("Error: Username is already taken!");
    }
}
