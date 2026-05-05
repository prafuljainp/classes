package com.salesinsight.exception;

/**
 * Custom exception for API-related errors.
 */
public class APIException extends RuntimeException {
    private final int statusCode;

    public APIException(String message) {
        super(message);
        this.statusCode = -1;
    }

    public APIException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
    }

    public APIException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
