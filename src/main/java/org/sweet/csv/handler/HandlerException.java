package org.sweet.csv.handler;

public class HandlerException extends RuntimeException {

    private static final long serialVersionUID = 2809071728492294596L;

    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(Throwable cause) {
        super(cause);
    }
}
