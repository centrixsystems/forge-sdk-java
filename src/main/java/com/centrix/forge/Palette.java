package com.centrix.forge;

/** Built-in color palette presets. */
public enum Palette {
    AUTO("auto"),
    BLACK_WHITE("bw"),
    GRAYSCALE("grayscale"),
    EINK("eink");

    private final String value;

    Palette(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
