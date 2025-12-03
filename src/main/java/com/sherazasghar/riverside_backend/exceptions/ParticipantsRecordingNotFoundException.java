
package com.sherazasghar.riverside_backend.exceptions;

public class ParticipantsRecordingNotFoundException extends RiverSideException {

    public ParticipantsRecordingNotFoundException() {
    }

    public ParticipantsRecordingNotFoundException(String message) {
        super(message);
    }

    public ParticipantsRecordingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParticipantsRecordingNotFoundException(Throwable cause) {
        super(cause);
    }

    public ParticipantsRecordingNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
