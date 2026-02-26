package com.centrix.forge;

/** Base exception for the Forge SDK. */
public class ForgeException extends Exception {
    public ForgeException(String message) {
        super(message);
    }

    public ForgeException(String message, Throwable cause) {
        super(message, cause);
    }
}
