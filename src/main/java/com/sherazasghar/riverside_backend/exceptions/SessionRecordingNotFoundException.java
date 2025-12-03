
package com.sherazasghar.riverside_backend.exceptions;

public class SessionRecordingNotFoundException extends RiverSideException {

    public SessionRecordingNotFoundException() {
    }

    public SessionRecordingNotFoundException(String message) {
        super(message);
    }

    public SessionRecordingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionRecordingNotFoundException(Throwable cause) {
        super(cause);
    }

    public SessionRecordingNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
