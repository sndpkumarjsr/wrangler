package io.cdap.wrangler.api.parser;

import junit.framework.TestCase;

public class ByteSizeTest extends TestCase {

    public void testValidByteSizes() {
        assertEquals(10240, new ByteSize("10kb").getBytes());
        assertEquals(1572864, new ByteSize("1.5MB").getBytes());
        assertEquals(1073741824, new ByteSize("1GB").getBytes());
        assertEquals(1, new ByteSize("1b").getBytes());
    }

    public void testCaseInsensitivity() {
        assertEquals(2048, new ByteSize("2KB").getBytes());
        assertEquals(2048, new ByteSize("2kb").getBytes());
    }

    public void testInvalidByteSizes() {
        try {
            new ByteSize("10zebra");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) { }

        try {
            new ByteSize("abc");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) { }
    }
}
