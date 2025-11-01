package com.plango.server;

public class DataNotFoundException extends RuntimeException{
    String message;
    public DataNotFoundException(String message) {
        this.message = message;
    }
}
