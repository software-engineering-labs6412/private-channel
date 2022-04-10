package org.ssau.privatechannel.exception;

public class HeaderKeyNotActualException extends Exception {
    public HeaderKeyNotActualException() {
        super();
    }

    public HeaderKeyNotActualException(String message) {
        super(message);
    }

    public HeaderKeyNotActualException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeaderKeyNotActualException(Throwable cause) {
        super(cause);
    }
}
