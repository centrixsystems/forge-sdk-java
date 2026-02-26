package com.centrix.forge;

/** Failed to connect to the Forge server. */
public class ForgeConnectionException extends ForgeException {
    public ForgeConnectionException(Throwable cause) {
        super("connection error: " + cause.getMessage(), cause);
    }
}
