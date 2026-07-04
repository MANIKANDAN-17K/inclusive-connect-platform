package com.inclusiveconnect.inclusiveconnectbackend.exception;

public class ConnectionAlreadyExistsException extends RuntimeException {
    public ConnectionAlreadyExistsException(String message) {
        super(message);
    }
}