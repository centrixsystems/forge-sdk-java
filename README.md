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
