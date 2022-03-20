package org.ssau.privatechannel.exception;

public class InvalidAppInstallationModeException extends Exception {

    public InvalidAppInstallationModeException() {
        super();
    }

    public InvalidAppInstallationModeException(String message) {
        super(message);
    }

    public InvalidAppInstallationModeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAppInstallationModeException(Throwable cause) {
        super(cause);
    }
}
