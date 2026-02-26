package com.centrix.forge;

/** Output format for rendered content. */
public enum OutputFormat {
    PDF("pdf"),
    PNG("png"),
    JPEG("jpeg"),
    BMP("bmp"),
    TGA("tga"),
    QOI("qoi"),
    SVG("svg");

    private final String value;

    OutputFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
