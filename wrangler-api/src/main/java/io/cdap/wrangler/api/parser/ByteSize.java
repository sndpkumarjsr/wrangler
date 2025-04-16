package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ByteSize implements Token {
    private final String original;
    private final long bytes;

    public ByteSize(String value) {
        this.original = value.toUpperCase();
        this.bytes = parseBytes(this.original);
    }

    private long parseBytes(String value) {
        if (value.endsWith("KB")) {
            return (long)(Double.parseDouble(value.replace("KB", "")) * 1024);
        } else if (value.endsWith("MB")) {
            return (long)(Double.parseDouble(value.replace("MB", "")) * 1024 * 1024);
        } else if (value.endsWith("GB")) {
            return (long)(Double.parseDouble(value.replace("GB", "")) * 1024 * 1024 * 1024);
        } else if (value.endsWith("TB")) {
            return (long)(Double.parseDouble(value.replace("TB", "")) * 1024L * 1024 * 1024 * 1024);
        } else if (value.endsWith("B")) {
            return Long.parseLong(value.replace("B", ""));
        }
        throw new IllegalArgumentException("Invalid byte size format: " + value);
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public Object value() {
        return bytes;
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(bytes);
    }
}
