package org.analogweb.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;

import org.analogweb.MediaType;
import org.analogweb.ReadableBuffer;
import org.analogweb.util.IOUtils;
import org.analogweb.util.StringUtils;

public class FormParameters extends QueryParameters {

    public FormParameters(URI requestURI, ReadableBuffer body, MediaType contentType) {
        super(requestURI, body, contentType);
    }

    @Override
    protected String resolveParametersParts(URI requestURI, ReadableBuffer body, MediaType contentType, Charset charset)
            throws IOException {
        if (MediaTypes.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(contentType)) {
            return body.asString(charset);
        }
        return StringUtils.EMPTY;
    }
}
