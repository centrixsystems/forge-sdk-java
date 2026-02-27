# forge-sdk

Java SDK for the [Forge](https://github.com/centrixsystems/forge) rendering engine. Converts HTML/CSS to PDF, PNG, and other formats via a running Forge server.

Uses `java.net.http.HttpClient` (Java 11+) and [Gson](https://github.com/google/gson) for JSON.

## Installation

### Maven

```xml
<dependency>
    <groupId>com.centrix</groupId>
    <artifactId>forge-sdk</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.centrix:forge-sdk:0.1.0'
```

## Quick Start

```java
import com.centrix.forge.*;
import java.nio.file.*;

ForgeClient client = new ForgeClient("http://localhost:3000");

byte[] pdf = client.renderHtml("<h1>Invoice #1234</h1>")
    .format(OutputFormat.PDF)
    .paper("a4")
    .send();

Files.write(Path.of("invoice.pdf"), pdf);
```

## Usage

### Render HTML to PDF

```java
byte[] pdf = client.renderHtml("<h1>Hello</h1>")
    .format(OutputFormat.PDF)
    .paper("a4")
    .orientation(Orientation.PORTRAIT)
    .margins("25.4,25.4,25.4,25.4")
    .flow(Flow.PAGINATE)
    .send();
```

### Render URL to PNG

```java
byte[] png = client.renderUrl("https://example.com")
    .format(OutputFormat.PNG)
    .width(1280)
    .height(800)
    .density(2.0)
    .send();
```

### Color Quantization

Reduce colors for e-ink displays or limited-palette output.

```java
byte[] eink = client.renderHtml("<h1>Dashboard</h1>")
    .format(OutputFormat.PNG)
    .palette(Palette.EINK)
    .dither(DitherMethod.FLOYD_STEINBERG)
    .send();
```

### Custom Palette

```java
import java.util.List;

byte[] img = client.renderHtml("<h1>Brand</h1>")
    .format(OutputFormat.PNG)
    .customPalette(List.of("#000000", "#ffffff", "#ff0000"))
    .dither(DitherMethod.ATKINSON)
    .send();
```

### PDF Metadata

Set PDF document properties like title, author, and bookmarks.

```java
byte[] pdf = client.renderHtml("<h1>Annual Report 2026</h1>")
    .format(OutputFormat.PDF)
    .paper("a4")
    .flow(Flow.PAGINATE)
    .pdfTitle("Annual Report 2026")
    .pdfAuthor("Centrix Systems")
    .pdfSubject("Financial Results")
    .pdfKeywords("annual,report,finance")
    .pdfCreator("Centrix ERP")
    .pdfBookmarks(true)
    .send();
```

### PDF Watermarks

Add text or image watermarks to each page.

```java
byte[] pdf = client.renderHtml("<h1>Draft Report</h1>")
    .pdfWatermarkText("DRAFT")
    .pdfWatermarkOpacity(0.15)
    .pdfWatermarkRotation(-45)
    .pdfWatermarkColor("#888888")
    .pdfWatermarkLayer(WatermarkLayer.OVER)
    .send();
```

### PDF/A Archival Output

Generate PDF/A-compliant documents for long-term archiving.

```java
byte[] pdf = client.renderHtml("<h1>Archival Report</h1>")
    .pdfStandard(PdfStandard.A2B)
    .pdfTitle("Archival Report")
    .send();
```

### Embedded Files (ZUGFeRD/Factur-X)

Attach files to PDF output. Requires PDF/A-3b for embedded file attachments.

```java
import java.util.Base64;

byte[] xmlBytes = Files.readAllBytes(Path.of("factur-x.xml"));
String xmlData = Base64.getEncoder().encodeToString(xmlBytes);

byte[] pdf = client.renderHtml("<h1>Invoice #1234</h1>")
    .pdfStandard(PdfStandard.A3B)
    .pdfAttach("factur-x.xml", xmlData, "text/xml", "Factur-X invoice", EmbedRelationship.ALTERNATIVE)
    .send();
```

### Custom Timeout

```java
import java.time.Duration;

ForgeClient client = new ForgeClient("http://forge:3000", Duration.ofMinutes(5));
```

### Health Check

```java
boolean healthy = client.health();
```

## API Reference

### `ForgeClient`

| Constructor | Description |
|-------------|-------------|
| `ForgeClient(String baseUrl)` | Create with default 120s timeout |
| `ForgeClient(String baseUrl, Duration timeout)` | Create with custom timeout |

| Method | Returns | Description |
|--------|---------|-------------|
| `renderHtml(html)` | `RenderRequestBuilder` | Start a render from HTML |
| `renderUrl(url)` | `RenderRequestBuilder` | Start a render from a URL |
| `health()` | `boolean` | Check server health |

### `RenderRequestBuilder`

All methods return the builder for chaining. Call `.send()` to execute.

| Method | Type | Description |
|--------|------|-------------|
| `format` | `OutputFormat` | Output format (default: `PDF`) |
| `width` | `int` | Viewport width in CSS pixels |
| `height` | `int` | Viewport height in CSS pixels |
| `paper` | `String` | Paper size: a3, a4, a5, b4, b5, letter, legal, ledger |
| `orientation` | `Orientation` | `PORTRAIT` or `LANDSCAPE` |
| `margins` | `String` | Preset (`default`, `none`, `narrow`) or `"T,R,B,L"` in mm |
| `flow` | `Flow` | `AUTO`, `PAGINATE`, or `CONTINUOUS` |
| `density` | `double` | Output DPI (default: 96) |
| `background` | `String` | CSS background color (e.g. `"#ffffff"`) |
| `timeout` | `int` | Page load timeout in seconds |
| `colors` | `int` | Quantization color count (2-256) |
| `palette` | `Palette` | Built-in palette preset |
| `customPalette` | `List<String>` | List of hex color strings |
| `dither` | `DitherMethod` | Dithering algorithm |
| `pdfTitle` | `String` | PDF document title |
| `pdfAuthor` | `String` | PDF document author |
| `pdfSubject` | `String` | PDF document subject |
| `pdfKeywords` | `String` | PDF keywords (comma-separated) |
| `pdfCreator` | `String` | PDF creator application name |
| `pdfBookmarks` | `boolean` | Generate PDF bookmarks from headings |
| `pdfPageNumbers` | `boolean` | Add "Page X of Y" footers to each page |
| `pdfWatermarkText` | `String` | Watermark text on each page |
| `pdfWatermarkImage` | `String` | Base64-encoded PNG/JPEG watermark image |
| `pdfWatermarkOpacity` | `double` | Watermark opacity (0.0-1.0, default: 0.15) |
| `pdfWatermarkRotation` | `double` | Watermark rotation in degrees (default: -45) |
| `pdfWatermarkColor` | `String` | Watermark text color as hex (default: #888888) |
| `pdfWatermarkFontSize` | `double` | Watermark font size in PDF points (default: auto) |
| `pdfWatermarkScale` | `double` | Watermark image scale (0.0-1.0, default: 0.5) |
| `pdfWatermarkLayer` | `WatermarkLayer` | Layer position: `OVER` or `UNDER` |
| `pdfStandard` | `PdfStandard` | PDF standard: `NONE`, `A2B`, `A3B` |
| `pdfAttach` | `String, String, ...` | Embed file: path, base64 data, mime type, description, relationship |

| Terminal Method | Returns | Description |
|-----------------|---------|-------------|
| `send()` | `byte[]` | Execute the render request |

### Enums

| Enum | Values |
|------|--------|
| `OutputFormat` | `PDF`, `PNG`, `JPEG`, `BMP`, `TGA`, `QOI`, `SVG` |
| `Orientation` | `PORTRAIT`, `LANDSCAPE` |
| `Flow` | `AUTO`, `PAGINATE`, `CONTINUOUS` |
| `DitherMethod` | `NONE`, `FLOYD_STEINBERG`, `ATKINSON`, `ORDERED` |
| `Palette` | `AUTO`, `BLACK_WHITE`, `GRAYSCALE`, `EINK` |
| `WatermarkLayer` | `OVER`, `UNDER` |
| `PdfStandard` | `NONE`, `A2B`, `A3B` |
| `EmbedRelationship` | `ALTERNATIVE`, `SUPPLEMENT`, `DATA`, `SOURCE`, `UNSPECIFIED` |

### Exceptions

| Exception | Properties | Description |
|-----------|------------|-------------|
| `ForgeException` | `getMessage()` | Base exception for all SDK errors |
| `ForgeServerException` | `getStatusCode()` | Server returned 4xx/5xx |
| `ForgeConnectionException` | `getCause()` | Network failure |

## Requirements

- Java 11+
- Gson on the classpath
- A running [Forge](https://github.com/centrixsystems/forge) server

## License

MIT
