package com.centrix.forge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Client for a Forge rendering server. */
public class ForgeClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private static final Gson GSON = new Gson();

    public ForgeClient(String baseUrl) {
        this(baseUrl, Duration.ofSeconds(120));
    }

    public ForgeClient(String baseUrl, Duration timeout) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    /** Start a render request from an HTML string. */
    public RenderRequestBuilder renderHtml(String html) {
        return new RenderRequestBuilder(this, html, null);
    }

    /** Start a render request from a URL. */
    public RenderRequestBuilder renderUrl(String url) {
        return new RenderRequestBuilder(this, null, url);
    }

    /** Check if the server is healthy. */
    public boolean health() {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/health"))
                .GET()
                .build();
        try {
            HttpResponse<Void> resp = httpClient.send(req, HttpResponse.BodyHandlers.discarding());
            return resp.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    byte[] send(JsonObject payload) throws ForgeException {
        String body = GSON.toJson(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/render"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<byte[]> resp;
        try {
            resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            throw new ForgeConnectionException(e);
        }

        if (resp.statusCode() != 200) {
            String message;
            try {
                JsonObject errBody = JsonParser.parseString(new String(resp.body()))
                        .getAsJsonObject();
                message = errBody.get("error").getAsString();
            } catch (Exception e) {
                message = "HTTP " + resp.statusCode();
            }
            throw new ForgeServerException(resp.statusCode(), message);
        }

        return resp.body();
    }

    /** Builder for a render request. */
    public static class RenderRequestBuilder {
        private final ForgeClient client;
        private final String html;
        private final String url;
        private OutputFormat format = OutputFormat.PDF;
        private Integer width;
        private Integer height;
        private String paper;
        private Orientation orientation;
        private String margins;
        private Flow flow;
        private Double density;
        private String background;
        private Integer timeout;
        private Integer colors;
        private Object palette; // Palette enum or List<String>
        private DitherMethod dither;
        private String pdfTitle;
        private String pdfAuthor;
        private String pdfSubject;
        private String pdfKeywords;
        private String pdfCreator;
        private Boolean pdfBookmarks;
        private String pdfWatermarkText;
        private String pdfWatermarkImage; // base64-encoded
        private Double pdfWatermarkOpacity;
        private Double pdfWatermarkRotation;
        private String pdfWatermarkColor;
        private Double pdfWatermarkFontSize;
        private Double pdfWatermarkScale;
        private WatermarkLayer pdfWatermarkLayer;
        private PdfStandard pdfStandard;
        private List<Object[]> pdfEmbeddedFiles; // [path, data, mimeType, description, relationship]
        private String pdfWatermarkPages;
        private List<Map<String, Object>> pdfBarcodes;

        RenderRequestBuilder(ForgeClient client, String html, String url) {
            this.client = client;
            this.html = html;
            this.url = url;
        }

        public RenderRequestBuilder format(OutputFormat f) { this.format = f; return this; }
        public RenderRequestBuilder width(int px) { this.width = px; return this; }
        public RenderRequestBuilder height(int px) { this.height = px; return this; }
        public RenderRequestBuilder paper(String size) { this.paper = size; return this; }
        public RenderRequestBuilder orientation(Orientation o) { this.orientation = o; return this; }
        public RenderRequestBuilder margins(String m) { this.margins = m; return this; }
        public RenderRequestBuilder flow(Flow f) { this.flow = f; return this; }
        public RenderRequestBuilder density(double dpi) { this.density = dpi; return this; }
        public RenderRequestBuilder background(String color) { this.background = color; return this; }
        public RenderRequestBuilder timeout(int seconds) { this.timeout = seconds; return this; }
        public RenderRequestBuilder colors(int n) { this.colors = n; return this; }
        public RenderRequestBuilder palette(Palette p) { this.palette = p; return this; }
        public RenderRequestBuilder customPalette(List<String> colors) { this.palette = colors; return this; }
        public RenderRequestBuilder dither(DitherMethod method) { this.dither = method; return this; }
        public RenderRequestBuilder pdfTitle(String title) { this.pdfTitle = title; return this; }
        public RenderRequestBuilder pdfAuthor(String author) { this.pdfAuthor = author; return this; }
        public RenderRequestBuilder pdfSubject(String subject) { this.pdfSubject = subject; return this; }
        public RenderRequestBuilder pdfKeywords(String keywords) { this.pdfKeywords = keywords; return this; }
        public RenderRequestBuilder pdfCreator(String creator) { this.pdfCreator = creator; return this; }
        public RenderRequestBuilder pdfBookmarks(boolean bookmarks) { this.pdfBookmarks = bookmarks; return this; }
        public RenderRequestBuilder pdfWatermarkText(String text) { this.pdfWatermarkText = text; return this; }
        public RenderRequestBuilder pdfWatermarkImage(String base64Data) { this.pdfWatermarkImage = base64Data; return this; }
        public RenderRequestBuilder pdfWatermarkOpacity(double opacity) { this.pdfWatermarkOpacity = opacity; return this; }
        public RenderRequestBuilder pdfWatermarkRotation(double degrees) { this.pdfWatermarkRotation = degrees; return this; }
        public RenderRequestBuilder pdfWatermarkColor(String hex) { this.pdfWatermarkColor = hex; return this; }
        public RenderRequestBuilder pdfWatermarkFontSize(double size) { this.pdfWatermarkFontSize = size; return this; }
        public RenderRequestBuilder pdfWatermarkScale(double scale) { this.pdfWatermarkScale = scale; return this; }
        public RenderRequestBuilder pdfWatermarkLayer(WatermarkLayer layer) { this.pdfWatermarkLayer = layer; return this; }
        public RenderRequestBuilder pdfStandard(PdfStandard standard) { this.pdfStandard = standard; return this; }
        public RenderRequestBuilder pdfAttach(String path, String base64Data) { return pdfAttach(path, base64Data, null, null, null); }
        public RenderRequestBuilder pdfAttach(String path, String base64Data, String mimeType, String description, EmbedRelationship relationship) {
            if (this.pdfEmbeddedFiles == null) this.pdfEmbeddedFiles = new ArrayList<>();
            this.pdfEmbeddedFiles.add(new Object[]{path, base64Data, mimeType, description, relationship});
            return this;
        }
        public RenderRequestBuilder pdfWatermarkPages(String pages) { this.pdfWatermarkPages = pages; return this; }
        public RenderRequestBuilder pdfBarcode(BarcodeType type, String data) {
            Map<String, Object> bc = new LinkedHashMap<>();
            bc.put("type", type.getValue());
            bc.put("data", data);
            if (this.pdfBarcodes == null) this.pdfBarcodes = new ArrayList<>();
            this.pdfBarcodes.add(bc);
            return this;
        }
        public RenderRequestBuilder pdfBarcode(BarcodeType type, String data, Double x, Double y,
                Double width, Double height, BarcodeAnchor anchor, String foreground,
                String background, Boolean drawBackground, String pages) {
            Map<String, Object> bc = new LinkedHashMap<>();
            bc.put("type", type.getValue());
            bc.put("data", data);
            if (x != null) bc.put("x", x);
            if (y != null) bc.put("y", y);
            if (width != null) bc.put("width", width);
            if (height != null) bc.put("height", height);
            if (anchor != null) bc.put("anchor", anchor.getValue());
            if (foreground != null) bc.put("foreground", foreground);
            if (background != null) bc.put("background", background);
            if (drawBackground != null) bc.put("draw_background", drawBackground);
            if (pages != null) bc.put("pages", pages);
            if (this.pdfBarcodes == null) this.pdfBarcodes = new ArrayList<>();
            this.pdfBarcodes.add(bc);
            return this;
        }

        /** Build the JSON payload. */
        public JsonObject buildPayload() {
            JsonObject p = new JsonObject();
            p.addProperty("format", format.getValue());

            if (html != null) p.addProperty("html", html);
            if (url != null) p.addProperty("url", url);
            if (width != null) p.addProperty("width", width);
            if (height != null) p.addProperty("height", height);
            if (paper != null) p.addProperty("paper", paper);
            if (orientation != null) p.addProperty("orientation", orientation.getValue());
            if (margins != null) p.addProperty("margins", margins);
            if (flow != null) p.addProperty("flow", flow.getValue());
            if (density != null) p.addProperty("density", density);
            if (background != null) p.addProperty("background", background);
            if (timeout != null) p.addProperty("timeout", timeout);

            if (colors != null || palette != null || dither != null) {
                JsonObject q = new JsonObject();
                if (colors != null) q.addProperty("colors", colors);
                if (palette instanceof Palette) {
                    q.addProperty("palette", ((Palette) palette).getValue());
                } else if (palette instanceof List) {
                    JsonArray arr = new JsonArray();
                    for (Object c : (List<?>) palette) {
                        arr.add(c.toString());
                    }
                    q.add("palette", arr);
                }
                if (dither != null) q.addProperty("dither", dither.getValue());
                p.add("quantize", q);
            }

            if (pdfTitle != null || pdfAuthor != null || pdfSubject != null
                    || pdfKeywords != null || pdfCreator != null || pdfBookmarks != null
                    || pdfWatermarkText != null || pdfWatermarkImage != null || pdfWatermarkOpacity != null
                    || pdfWatermarkRotation != null || pdfWatermarkColor != null || pdfWatermarkFontSize != null
                    || pdfWatermarkScale != null || pdfWatermarkLayer != null || pdfWatermarkPages != null
                    || pdfStandard != null || pdfEmbeddedFiles != null || pdfBarcodes != null) {
                JsonObject pdf = new JsonObject();
                if (pdfTitle != null) pdf.addProperty("title", pdfTitle);
                if (pdfAuthor != null) pdf.addProperty("author", pdfAuthor);
                if (pdfSubject != null) pdf.addProperty("subject", pdfSubject);
                if (pdfKeywords != null) pdf.addProperty("keywords", pdfKeywords);
                if (pdfCreator != null) pdf.addProperty("creator", pdfCreator);
                if (pdfBookmarks != null) pdf.addProperty("bookmarks", pdfBookmarks);
                if (pdfStandard != null) pdf.addProperty("standard", pdfStandard.getValue());
                if (pdfWatermarkText != null || pdfWatermarkImage != null || pdfWatermarkOpacity != null
                        || pdfWatermarkRotation != null || pdfWatermarkColor != null || pdfWatermarkFontSize != null
                        || pdfWatermarkScale != null || pdfWatermarkLayer != null || pdfWatermarkPages != null) {
                    JsonObject wm = new JsonObject();
                    if (pdfWatermarkText != null) wm.addProperty("text", pdfWatermarkText);
                    if (pdfWatermarkImage != null) wm.addProperty("image_data", pdfWatermarkImage);
                    if (pdfWatermarkOpacity != null) wm.addProperty("opacity", pdfWatermarkOpacity);
                    if (pdfWatermarkRotation != null) wm.addProperty("rotation", pdfWatermarkRotation);
                    if (pdfWatermarkColor != null) wm.addProperty("color", pdfWatermarkColor);
                    if (pdfWatermarkFontSize != null) wm.addProperty("font_size", pdfWatermarkFontSize);
                    if (pdfWatermarkScale != null) wm.addProperty("scale", pdfWatermarkScale);
                    if (pdfWatermarkLayer != null) wm.addProperty("layer", pdfWatermarkLayer.getValue());
                    if (pdfWatermarkPages != null) wm.addProperty("pages", pdfWatermarkPages);
                    pdf.add("watermark", wm);
                }
                if (pdfEmbeddedFiles != null) {
                    JsonArray arr = new JsonArray();
                    for (Object[] ef : pdfEmbeddedFiles) {
                        JsonObject e = new JsonObject();
                        e.addProperty("path", (String) ef[0]);
                        e.addProperty("data", (String) ef[1]);
                        if (ef[2] != null) e.addProperty("mime_type", (String) ef[2]);
                        if (ef[3] != null) e.addProperty("description", (String) ef[3]);
                        if (ef[4] != null) e.addProperty("relationship", ((EmbedRelationship) ef[4]).getValue());
                        arr.add(e);
                    }
                    pdf.add("embedded_files", arr);
                }
                if (pdfBarcodes != null) {
                    JsonArray barcodeArr = new JsonArray();
                    for (Map<String, Object> bc : pdfBarcodes) {
                        JsonObject b = new JsonObject();
                        for (Map.Entry<String, Object> entry : bc.entrySet()) {
                            Object val = entry.getValue();
                            if (val instanceof String) b.addProperty(entry.getKey(), (String) val);
                            else if (val instanceof Number) b.addProperty(entry.getKey(), (Number) val);
                            else if (val instanceof Boolean) b.addProperty(entry.getKey(), (Boolean) val);
                        }
                        barcodeArr.add(b);
                    }
                    pdf.add("barcodes", barcodeArr);
                }
                p.add("pdf", pdf);
            }

            return p;
        }

        /** Send the render request and return raw output bytes. */
        public byte[] send() throws ForgeException {
            return client.send(buildPayload());
        }
    }
}
