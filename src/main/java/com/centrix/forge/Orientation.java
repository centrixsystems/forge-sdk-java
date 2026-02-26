package com.centrix.forge;

/** Page orientation. */
public enum Orientation {
    PORTRAIT("portrait"),
    LANDSCAPE("landscape");

    private final String value;

    Orientation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
