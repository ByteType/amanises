package com.bytetype.amanises.exception;

public class IncorrectLockerException extends Exception {

    public IncorrectLockerException(String lockerLocation) {
        super("Error: Incorrect locker. You should visit locker at: " + lockerLocation);
    }
}
