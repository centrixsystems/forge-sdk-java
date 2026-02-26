package com.centrix.forge;

/** Dithering algorithm for color quantization. */
public enum DitherMethod {
    NONE("none"),
    FLOYD_STEINBERG("floyd-steinberg"),
    ATKINSON("atkinson"),
    ORDERED("ordered");

    private final String value;

    DitherMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
