package com.bytetype.amanises.exception;

public class RoleNotFoundException extends Exception {
    public RoleNotFoundException() {
        super("Error: Role not found!");
    }
}
