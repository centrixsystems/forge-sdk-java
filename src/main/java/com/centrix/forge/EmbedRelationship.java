package com.centrix.forge;

/** Relationship of an embedded file to the PDF document. */
public enum EmbedRelationship {
    ALTERNATIVE("alternative"),
    SUPPLEMENT("supplement"),
    DATA("data"),
    SOURCE("source"),
    UNSPECIFIED("unspecified");

    private final String value;

    EmbedRelationship(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
