package com.centrix.forge;

/** Barcode symbology type. */
public enum BarcodeType {
    // 2D types
    QR("qr"),
    DATA_MATRIX("datamatrix"),
    PDF417("pdf417"),
    AZTEC("aztec"),
    // 1D types
    CODE128("code128"),
    EAN13("ean13"),
    EAN8("ean8"),
    UPCA("upca"),
    CODE39("code39"),
    CODE93("code93"),
    CODABAR("codabar"),
    ITF("itf"),
    CODE11("code11");

    private final String value;

    BarcodeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
