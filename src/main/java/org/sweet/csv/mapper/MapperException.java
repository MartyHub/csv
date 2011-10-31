package org.sweet.csv.mapper;

public class MapperException extends RuntimeException {

    private static final long serialVersionUID = 813508088765012256L;

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }
}
