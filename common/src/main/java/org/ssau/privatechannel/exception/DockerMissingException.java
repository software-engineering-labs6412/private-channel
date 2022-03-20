package org.ssau.privatechannel.exception;

public class DockerMissingException extends Exception {

    public DockerMissingException() {
        super();
    }

    public DockerMissingException(String message) {
        super(message);
    }

    public DockerMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerMissingException(Throwable cause) {
        super(cause);
    }
}
