package org.analogweb.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.analogweb.MediaType;
import org.analogweb.RequestContext;
import org.analogweb.util.IOUtils;
import org.analogweb.util.StringUtils;

public class FormParameters extends QueryParameters {

    public FormParameters(RequestContext context) {
        super(context);
    }

    @Override
    protected String resolveEncodedParameters(RequestContext context, Charset charset)
            throws IOException {
        MediaType contentType = context.getContentType();
        if (MediaTypes.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(contentType)) {
            return IOUtils.toString(new InputStreamReader(context.getRequestBody(), charset));
        }
        return StringUtils.EMPTY;
    }

}
