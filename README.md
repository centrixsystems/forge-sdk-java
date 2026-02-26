# forge-sdk

Java SDK for the [Forge](https://github.com/centrixsystems/forge) rendering engine. Converts HTML/CSS to PDF, PNG, and other formats via a running Forge server.

Uses `java.net.http.HttpClient` (Java 11+) and Gson for JSON.

## Installation

### Maven

```xml
<dependency>
    <groupId>com.centrix</groupId>
    <artifactId>forge-sdk</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Quick Start

```java
import com.centrix.forge.*;

ForgeClient client = new ForgeClient("http://localhost:3000");

byte[] pdf = client.renderHtml("<h1>Invoice #1234</h1>")
    .format(OutputFormat.PDF)
    .paper("a4")
    .send();

Files.write(Path.of("invoice.pdf"), pdf);
```

## Usage

### Render URL to PNG

```java
byte[] png = client.renderUrl("https://example.com")
    .format(OutputFormat.PNG)
    .width(1280)
    .height(800)
    .send();
```

### Color Quantization

```java
byte[] eink = client.renderHtml("<h1>Dashboard</h1>")
    .format(OutputFormat.PNG)
    .palette(Palette.EINK)
    .dither(DitherMethod.FLOYD_STEINBERG)
    .send();
```

### Health Check

```java
boolean healthy = client.health();
```

## API Reference

### Types

- `OutputFormat`: PDF, PNG, JPEG, BMP, TGA, QOI, SVG
- `Orientation`: PORTRAIT, LANDSCAPE
- `Flow`: AUTO, PAGINATE, CONTINUOUS
- `DitherMethod`: NONE, FLOYD_STEINBERG, ATKINSON, ORDERED
- `Palette`: AUTO, BLACK_WHITE, GRAYSCALE, EINK

### Errors

- `ForgeException` — base exception
- `ForgeServerException` — 4xx/5xx (has `getStatusCode()`)

## License

MIT
