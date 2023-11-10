package com.bytetype.amanises.exception;

public class ParcelNotFoundException extends Exception {

    public ParcelNotFoundException() {
        super("Error: Parcel not found!");
    }
}
