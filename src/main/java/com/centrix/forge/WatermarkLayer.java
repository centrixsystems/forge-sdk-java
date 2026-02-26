package com.centrix.forge;

/** Watermark layer position. */
public enum WatermarkLayer {
    OVER("over"),
    UNDER("under");

    private final String value;

    WatermarkLayer(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
