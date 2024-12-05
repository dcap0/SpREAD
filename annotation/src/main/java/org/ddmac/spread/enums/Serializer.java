package org.ddmac.spread.enums;

/**
 * Enum for user to provide the serializer they're using.
 */
public enum Serializer {
    /**
     * com.google.code.gson
     */
    GSON,
    /**
     * com.fasterxml.jackson.core:jackson-databind
     */
    JACKSON,
    /**
     * kotlinx.serialize
     */
    KOTLIN
}
