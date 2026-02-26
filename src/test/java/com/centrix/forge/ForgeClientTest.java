package com.centrix.forge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ForgeClientTest {

    private ForgeClient client() {
        return new ForgeClient("http://localhost:8080");
    }

    // --- Barcode enum tests ---

    @Test
    public void barcodeTypeValues() {
        assertEquals("qr", BarcodeType.QR.getValue());
        assertEquals("code128", BarcodeType.CODE128.getValue());
        assertEquals("ean13", BarcodeType.EAN13.getValue());
        assertEquals("upca", BarcodeType.UPCA.getValue());
        assertEquals("code39", BarcodeType.CODE39.getValue());
    }

    @Test
    public void barcodeAnchorValues() {
        assertEquals("top-left", BarcodeAnchor.TOP_LEFT.getValue());
        assertEquals("top-right", BarcodeAnchor.TOP_RIGHT.getValue());
        assertEquals("bottom-left", BarcodeAnchor.BOTTOM_LEFT.getValue());
        assertEquals("bottom-right", BarcodeAnchor.BOTTOM_RIGHT.getValue());
    }

    // --- Simple barcode in payload ---

    @Test
    public void simpleBarcodePayload() {
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfBarcode(BarcodeType.QR, "https://example.com")
                .buildPayload();

        assertTrue(p.has("pdf"));
        JsonObject pdf = p.getAsJsonObject("pdf");
        assertTrue(pdf.has("barcodes"));
        JsonArray barcodes = pdf.getAsJsonArray("barcodes");
        assertEquals(1, barcodes.size());

        JsonObject bc = barcodes.get(0).getAsJsonObject();
        assertEquals("qr", bc.get("type").getAsString());
        assertEquals("https://example.com", bc.get("data").getAsString());
        // Simple overload should not have optional fields
        assertFalse(bc.has("x"));
        assertFalse(bc.has("anchor"));
        assertFalse(bc.has("pages"));
    }

    // --- Full barcode with all options ---

    @Test
    public void fullBarcodePayload() {
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfBarcode(BarcodeType.CODE128, "ABC-123",
                        10.0, 20.0, 100.0, 50.0,
                        BarcodeAnchor.BOTTOM_RIGHT, "#000000", "#FFFFFF",
                        true, "1,3-5")
                .buildPayload();

        JsonObject bc = p.getAsJsonObject("pdf")
                .getAsJsonArray("barcodes").get(0).getAsJsonObject();

        assertEquals("code128", bc.get("type").getAsString());
        assertEquals("ABC-123", bc.get("data").getAsString());
        assertEquals(10.0, bc.get("x").getAsDouble(), 0.001);
        assertEquals(20.0, bc.get("y").getAsDouble(), 0.001);
        assertEquals(100.0, bc.get("width").getAsDouble(), 0.001);
        assertEquals(50.0, bc.get("height").getAsDouble(), 0.001);
        assertEquals("bottom-right", bc.get("anchor").getAsString());
        assertEquals("#000000", bc.get("foreground").getAsString());
        assertEquals("#FFFFFF", bc.get("background").getAsString());
        assertTrue(bc.get("draw_background").getAsBoolean());
        assertEquals("1,3-5", bc.get("pages").getAsString());
    }

    // --- Multiple barcodes ---

    @Test
    public void multipleBarcodes() {
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfBarcode(BarcodeType.QR, "data1")
                .pdfBarcode(BarcodeType.EAN13, "5901234123457")
                .buildPayload();

        JsonArray barcodes = p.getAsJsonObject("pdf").getAsJsonArray("barcodes");
        assertEquals(2, barcodes.size());
        assertEquals("qr", barcodes.get(0).getAsJsonObject().get("type").getAsString());
        assertEquals("ean13", barcodes.get(1).getAsJsonObject().get("type").getAsString());
    }

    // --- Barcode with null optional fields ---

    @Test
    public void barcodeNullOptionalsOmitted() {
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfBarcode(BarcodeType.UPCA, "012345678905",
                        null, null, null, null, null, null, null, null, null)
                .buildPayload();

        JsonObject bc = p.getAsJsonObject("pdf")
                .getAsJsonArray("barcodes").get(0).getAsJsonObject();

        assertEquals("upca", bc.get("type").getAsString());
        assertEquals("012345678905", bc.get("data").getAsString());
        assertFalse(bc.has("x"));
        assertFalse(bc.has("y"));
        assertFalse(bc.has("width"));
        assertFalse(bc.has("height"));
        assertFalse(bc.has("anchor"));
        assertFalse(bc.has("foreground"));
        assertFalse(bc.has("background"));
        assertFalse(bc.has("draw_background"));
        assertFalse(bc.has("pages"));
    }

    // --- Watermark pages ---

    @Test
    public void watermarkPagesPayload() {
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfWatermarkText("DRAFT")
                .pdfWatermarkPages("1,3-5")
                .buildPayload();

        JsonObject wm = p.getAsJsonObject("pdf").getAsJsonObject("watermark");
        assertEquals("DRAFT", wm.get("text").getAsString());
        assertEquals("1,3-5", wm.get("pages").getAsString());
    }

    @Test
    public void watermarkPagesOnly() {
        // Setting only pages should still create the watermark object
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfWatermarkPages("2-4")
                .buildPayload();

        assertTrue(p.has("pdf"));
        JsonObject wm = p.getAsJsonObject("pdf").getAsJsonObject("watermark");
        assertNotNull(wm);
        assertEquals("2-4", wm.get("pages").getAsString());
    }

    // --- Combined barcode + watermark pages ---

    @Test
    public void barcodeAndWatermarkPagesCombined() {
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfWatermarkText("CONFIDENTIAL")
                .pdfWatermarkPages("1")
                .pdfBarcode(BarcodeType.QR, "https://verify.example.com")
                .buildPayload();

        JsonObject pdf = p.getAsJsonObject("pdf");

        // Watermark with pages
        JsonObject wm = pdf.getAsJsonObject("watermark");
        assertEquals("CONFIDENTIAL", wm.get("text").getAsString());
        assertEquals("1", wm.get("pages").getAsString());

        // Barcode
        JsonArray barcodes = pdf.getAsJsonArray("barcodes");
        assertEquals(1, barcodes.size());
        assertEquals("qr", barcodes.get(0).getAsJsonObject().get("type").getAsString());
    }

    // --- No pdf section when nothing set ---

    @Test
    public void noPdfSectionWhenEmpty() {
        JsonObject p = client().renderHtml("<h1>Test</h1>").buildPayload();
        assertFalse(p.has("pdf"));
    }

    // --- Barcode combined with other PDF options ---

    @Test
    public void barcodeWithMetadata() {
        JsonObject p = client().renderHtml("<h1>Test</h1>")
                .pdfTitle("Invoice")
                .pdfBarcode(BarcodeType.CODE39, "INV-001")
                .buildPayload();

        JsonObject pdf = p.getAsJsonObject("pdf");
        assertEquals("Invoice", pdf.get("title").getAsString());
        assertEquals(1, pdf.getAsJsonArray("barcodes").size());
    }
}
