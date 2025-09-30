
package com.sherazasghar.riverside_backend.exceptions;

public class SessionCompletedException extends RiverSideException {

  public SessionCompletedException() {
  }

  public SessionCompletedException(String message) {
    super(message);
  }

  public SessionCompletedException(String message, Throwable cause) {
    super(message, cause);
  }

  public SessionCompletedException(Throwable cause) {
    super(cause);
  }

  public SessionCompletedException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
