package com.sherazasghar.riverside_backend.exceptions;

public class RiverSideException extends RuntimeException {

    public RiverSideException() {
    }

    public RiverSideException(String message) {
        super(message);
    }

    public RiverSideException(String message, Throwable cause) {
        super(message, cause);
    }

    public RiverSideException(Throwable cause) {
        super(cause);
    }

    public RiverSideException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
