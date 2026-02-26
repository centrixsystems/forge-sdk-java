package com.centrix.forge;

/** Anchor position for barcode placement on a page. */
public enum BarcodeAnchor {
    TOP_LEFT("top-left"),
    TOP_RIGHT("top-right"),
    BOTTOM_LEFT("bottom-left"),
    BOTTOM_RIGHT("bottom-right");

    private final String value;

    BarcodeAnchor(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
