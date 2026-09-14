package org.example.exception;

public class RetakeLimitExceededException extends RuntimeException {
    public RetakeLimitExceededException(String message) {
        super(message);
    }
}
