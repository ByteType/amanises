package com.bytetype.amanises.exception;

public class CabinetNotFoundException extends Exception {

    public CabinetNotFoundException() {
        super("Error: Cabinet not found!");
    }
}