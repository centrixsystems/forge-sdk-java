package com.centrix.forge;

/** Barcode symbology type. */
public enum BarcodeType {
    QR("qr"),
    CODE128("code128"),
    EAN13("ean13"),
    UPCA("upca"),
    CODE39("code39");

    private final String value;

    BarcodeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
