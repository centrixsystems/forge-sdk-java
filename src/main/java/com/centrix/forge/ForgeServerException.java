package com.centrix.forge;

/** The server returned a 4xx/5xx response. */
public class ForgeServerException extends ForgeException {
    private final int statusCode;

    public ForgeServerException(int statusCode, String message) {
        super("server error (" + statusCode + "): " + message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
