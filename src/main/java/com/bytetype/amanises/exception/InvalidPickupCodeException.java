package com.bytetype.amanises.exception;

public class InvalidPickupCodeException extends Exception {

    public InvalidPickupCodeException() {
        super("Error: Invalid pickup code.");
    }
}
