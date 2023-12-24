package org.analogweb.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class CountingOutputStreamTest {

    @Test
    @SuppressWarnings("resource")
    public void test() throws IOException {
        byte[] bytes = "i ♥ u".getBytes();// 7 bytes.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CountingOutputStream actual = new CountingOutputStream(out);
        actual.write(bytes);
        assertThat(actual.getCount(), is(7L));
        assertThat(new String(out.toByteArray()), is(new String(bytes)));
    }
}
