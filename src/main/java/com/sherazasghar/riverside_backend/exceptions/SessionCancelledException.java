
package com.sherazasghar.riverside_backend.exceptions;

public class SessionCancelledException extends RiverSideException {

    public SessionCancelledException() {
    }

    public SessionCancelledException(String message) {
        super(message);
    }

    public SessionCancelledException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionCancelledException(Throwable cause) {
        super(cause);
    }

    public SessionCancelledException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
