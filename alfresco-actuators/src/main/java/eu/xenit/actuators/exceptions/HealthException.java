package eu.xenit.actuators.exceptions;

/**
 * Actuator specific exception to distinguish a logical DOWN from a systemic or runtime DOWN
 */
public class HealthException extends RuntimeException {

    public HealthException() {
    }

    public HealthException(String message) {
        super(message);
    }

    public HealthException(String message, Throwable cause) {
        super(message, cause);
    }

    public HealthException(Throwable cause) {
        super(cause);
    }

    public HealthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
