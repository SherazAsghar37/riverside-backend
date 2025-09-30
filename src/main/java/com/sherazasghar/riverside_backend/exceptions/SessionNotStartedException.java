
package com.sherazasghar.riverside_backend.exceptions;

public class SessionNotStartedException extends RiverSideException {

    public SessionNotStartedException() {
    }

    public SessionNotStartedException(String message) {
        super(message);
    }

    public SessionNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionNotStartedException(Throwable cause) {
        super(cause);
    }

    public SessionNotStartedException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
