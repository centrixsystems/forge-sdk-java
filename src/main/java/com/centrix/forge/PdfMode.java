package com.centrix.forge;

/** PDF rendering mode. */
public enum PdfMode {
    AUTO("auto"),
    VECTOR("vector"),
    RASTER("raster");

    private final String value;

    PdfMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
