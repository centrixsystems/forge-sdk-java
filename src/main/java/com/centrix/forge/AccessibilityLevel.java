package com.centrix.forge;

/** PDF accessibility compliance level. */
public enum AccessibilityLevel {
    NONE("none"),
    BASIC("basic"),
    PDF_UA_1("pdf/ua-1");

    private final String value;

    AccessibilityLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
