package com.centrix.forge;

/** PDF standard compliance level. */
public enum PdfStandard {
    NONE("none"),
    A2B("pdf/a-2b"),
    A3B("pdf/a-3b");

    private final String value;

    PdfStandard(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
