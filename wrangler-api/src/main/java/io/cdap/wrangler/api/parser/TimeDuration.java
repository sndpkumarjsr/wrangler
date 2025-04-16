package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class TimeDuration implements Token {
    private final String original;
    private final long milliseconds;

    public TimeDuration(String value) {
        this.original = value.toLowerCase();
        this.milliseconds = parseMilliseconds(this.original);
    }

    private long parseMilliseconds(String value) {
        if (value.endsWith("ms")) {
            return Long.parseLong(value.replace("ms", ""));
        } else if (value.endsWith("s")) {
            return (long)(Double.parseDouble(value.replace("s", "")) * 1000);
        } else if (value.endsWith("m")) {
            return (long)(Double.parseDouble(value.replace("m", "")) * 60 * 1000);
        } else if (value.endsWith("h")) {
            return (long)(Double.parseDouble(value.replace("h", "")) * 60 * 60 * 1000);
        }
        throw new IllegalArgumentException("Invalid time duration format: " + value);
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    @Override
    public Object value() {
        return milliseconds;
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(milliseconds);
    }
}
