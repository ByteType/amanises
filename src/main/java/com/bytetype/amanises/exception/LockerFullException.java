package com.bytetype.amanises.exception;

public class LockerFullException extends Exception {

    public LockerFullException() {
        super("Error: Cabinet is full!");
    }
}
