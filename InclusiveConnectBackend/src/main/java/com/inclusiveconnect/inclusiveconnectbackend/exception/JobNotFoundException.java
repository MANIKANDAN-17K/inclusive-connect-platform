package com.inclusiveconnect.inclusiveconnectbackend.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(String message) {
        super(message);
    }
}