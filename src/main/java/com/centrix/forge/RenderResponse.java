package com.centrix.forge;

import java.util.List;

/** Response from a render request, including any CSS compatibility warnings. */
public class RenderResponse {
    private final byte[] data;
    private final List<String> warnings;

    public RenderResponse(byte[] data, List<String> warnings) {
        this.data = data;
        this.warnings = warnings;
    }

    /** The rendered output bytes (PDF, PNG, etc.). */
    public byte[] getData() { return data; }

    /** CSS compatibility warnings from the Forge server. */
    public List<String> getWarnings() { return warnings; }
}
