package com.centrix.forge;

/** Document flow mode. */
public enum Flow {
    AUTO("auto"),
    PAGINATE("paginate"),
    CONTINUOUS("continuous");

    private final String value;

    Flow(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
