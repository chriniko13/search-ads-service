package com.chriniko.searchadsservice.error;

//TODO handle it in RestControllerAdvice...
public class InvalidAdIdException extends RuntimeException {

    public InvalidAdIdException(String message) {
        super(message);
    }
}
