package io.cdap.wrangler.api.parser;

import junit.framework.TestCase;

public class TimeDurationTest extends TestCase {

    public void testValidDurations() {
        assertEquals(5, new TimeDuration("5ms").getMilliseconds());
        assertEquals(2100, new TimeDuration("2.1s").getMilliseconds());
        assertEquals(60000, new TimeDuration("1m").getMilliseconds());
        assertEquals(3600000, new TimeDuration("1h").getMilliseconds());
    }

    public void testCaseInsensitivity() {
        assertEquals(3000, new TimeDuration("3S").getMilliseconds());
    }

    public void testInvalidDurations() {
        try {
            new TimeDuration("5lightyears");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) { }

        try {
            new TimeDuration("xyz");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) { }
    }
}