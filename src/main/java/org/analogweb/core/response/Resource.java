package org.analogweb.core.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.analogweb.ReadableBuffer;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseEntity;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.DefaultReadableBuffer;
import org.analogweb.core.ReadableBufferResponseEntity;
import org.analogweb.util.Assertion;
import org.analogweb.util.StringUtils;

/**
 * @author y2k2mt
 */
public class Resource extends BuildAndRenderableResponse<Resource> {

	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
	protected static final String CONTENT_DISPOSITION = "Content-Disposition";
	// private String contentType = DEFAULT_CONTENT_TYPE;
	private String charset = DEFAULT_CHARSET;
	private final String fileName;
	private String disposition = "attachment";
	private final ReadableBuffer input;
	private boolean withoutContentDisposition = false;

	protected Resource(ReadableBuffer input, String fileName) {
		this(input, fileName, DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET);
	}

	protected Resource(ReadableBuffer input, String fileName, String contentType) {
		this(input, fileName, contentType, DEFAULT_CHARSET);
	}

	protected Resource(ReadableBuffer input, String fileName,
			String contentType, String charset) {
		this.input = input;
		this.fileName = fileName;
		header("Content-Type", contentType);
		this.charset = charset;
	}

	public static Resource as(ReadableBuffer input) {
		return as(input, StringUtils.EMPTY);
	}

	public static Resource as(ReadableBuffer input, String fileName) {
		Assertion.notNull(input, ReadableBuffer.class.getName());
		return new Resource(input, fileName);
	}

	public static Resource as(ReadableBuffer input, String fileName,
			String contentType) {
		Assertion.notNull(input, ReadableBuffer.class.getName());
		return new Resource(input, fileName, contentType);
	}

	public static Resource asFilePath(String filePath) {
		Assertion.notNull(filePath, "FilePath");
		return as(new File(filePath));
	}

	public static Resource as(File file) {
		Assertion.notNull(file, File.class.getName());
		try {
			return new Resource(
					DefaultReadableBuffer.readBuffer(new FileInputStream(file)
							.getChannel()), file.getName());
		} catch (FileNotFoundException e) {
			throw new ApplicationRuntimeException(e) {

				private static final long serialVersionUID = 1L;
			};
		}
	}

	@Override
	protected void mergeHeaders(RequestContext request,
			ResponseContext response, Map<String, String> headers,
			ResponseEntity entity) {
		try {
			String contentDisposition = createContentDisposition();
			if (StringUtils.isNotEmpty(contentDisposition)) {
				headers.put(CONTENT_DISPOSITION, createContentDisposition());
			}
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationRuntimeException(e) {

				// TODO
				private static final long serialVersionUID = 1L;
			};
		}
		super.mergeHeaders(request, response, headers, entity);
	}

	@Override
	protected ResponseEntity extractResponseEntity(RequestContext request,
			ResponseContext response) {
		return new ReadableBufferResponseEntity(getInputStream());
	}

	protected String createContentDisposition()
			throws UnsupportedEncodingException {
		StringBuilder buffer = new StringBuilder();
		if (withContentDisposition()) {
			buffer.append(getDisposition());
			String fileName = getFileName();
			if (StringUtils.isNotEmpty(fileName)) {
				buffer.append("; filename=");
				buffer.append(URLEncoder.encode(fileName, getCharset()));
			}
		}
		return buffer.toString();
	}

	protected String getFileName() {
		return this.fileName;
	}

	protected ReadableBuffer getInputStream() {
		return this.input;
	}

	protected String getDisposition() {
		return this.disposition;
	}

	protected boolean withContentDisposition() {
		return !this.withoutContentDisposition;
	}

	public String getCharset() {
		return this.charset;
	}

	public Resource inline() {
		this.disposition = "inline";
		return this;
	}

	public Resource withoutContentDisposition() {
		this.withoutContentDisposition = true;
		return this;
	}
}
