package com.chriniko.searchadsservice.error;

// Note: this error serve as a critical service internal error and it should never happen.
public class ServiceProcessingException extends RuntimeException {

    public ServiceProcessingException(String message, Throwable error) {
        super(message, error);
    }
}
