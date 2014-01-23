package org.analogweb.core.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.DefaultResponseEntity;
import org.analogweb.util.Assertion;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public class Resource extends BuildableResponse<Resource> {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    protected static final String CONTENT_DISPOSITION = "Content-Disposition";
    private String contentType = DEFAULT_CONTENT_TYPE;
    private String charset = DEFAULT_CHARSET;
    private final String fileName;
    private String disposition = "attachment";
    private final InputStream input;

    protected Resource(InputStream input, String fileName) {
        this(input, fileName, DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET);
    }

    protected Resource(InputStream input, String fileName, String contentType, String charset) {
        this.input = input;
        this.fileName = fileName;
        this.contentType = contentType;
        this.charset = charset;
    }

    public static Resource as(InputStream input) {
        return as(input, StringUtils.EMPTY);
    }

    public static Resource as(InputStream input, String fileName) {
        Assertion.notNull(input, InputStream.class.getName());
        return new Resource(input, fileName);
    }

    public static Resource asFilePath(String filePath) {
        Assertion.notNull(filePath, "FilePath");
        return as(new File(filePath));
    }

    public static Resource as(File file) {
        Assertion.notNull(file, File.class.getName());
        try {
            return new Resource(new FileInputStream(file), file.getName());
        } catch (FileNotFoundException e) {
            throw new ApplicationRuntimeException(e) {

                private static final long serialVersionUID = 1L;
            };
        }
    }

    @Override
    protected void mergeHeaders(RequestContext request, ResponseContext response,
            Map<String, String> headers, ResponseEntity entity) {
        headers.put("Content-Type", getContentType());
        try {
            headers.put(CONTENT_DISPOSITION, createContentDisposition());
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationRuntimeException(e) {

                // TODO 
                private static final long serialVersionUID = 1L;
            };
        }
        super.mergeHeaders(request, response, headers, entity);
    }

    @Override
    protected ResponseEntity extractResponseEntity(RequestContext request, ResponseContext response) {
        return new DefaultResponseEntity(getInputStream());
    }

    protected String createContentDisposition() throws UnsupportedEncodingException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getDisposition());
        String fileName = getFileName();
        if (StringUtils.isNotEmpty(fileName)) {
            buffer.append("; filename=");
            buffer.append(URLEncoder.encode(fileName, getCharset()));
        }
        return buffer.toString();
    }

    protected String getFileName() {
        return this.fileName;
    }

    protected InputStream getInputStream() {
        return this.input;
    }

    protected String getDisposition() {
        return this.disposition;
    }

    public String getCharset() {
        return this.charset;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Resource inline() {
        this.disposition = "inline";
        return this;
    }
}
