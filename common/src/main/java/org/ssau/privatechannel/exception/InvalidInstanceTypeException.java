package org.ssau.privatechannel.exception;

public class InvalidInstanceTypeException extends Exception {

    public InvalidInstanceTypeException() {
        super();
    }

    public InvalidInstanceTypeException(String message) {
        super(message);
    }

    public InvalidInstanceTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInstanceTypeException(Throwable cause) {
        super(cause);
    }
}
