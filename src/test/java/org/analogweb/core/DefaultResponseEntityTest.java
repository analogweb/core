package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class DefaultResponseEntityTest {

    @Test
    public void test() throws IOException {
        DefaultResponseEntity actual = new DefaultResponseEntity("ResponseEntity!");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        actual.writeInto(out);
        assertThat(new String(out.toByteArray()), is("ResponseEntity!"));
        assertThat(actual.getContentLength(), is(15L));
    }
}
