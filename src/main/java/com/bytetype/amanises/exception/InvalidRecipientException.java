package com.bytetype.amanises.exception;

public class InvalidRecipientException extends Exception {
    public InvalidRecipientException() {
        super("Error: Invalid recipient.");
    }
}