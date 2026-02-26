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
import java.util.List;

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

            return p;
        }

        /** Send the render request and return raw output bytes. */
        public byte[] send() throws ForgeException {
            return client.send(buildPayload());
        }
    }
}
