package org.analogweb.core;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import org.analogweb.MediaType;
import org.analogweb.ReadableBuffer;
import org.analogweb.util.StringUtils;

/**
 * @author snowgooseyk
 */
public class MatrixParameters extends QueryParameters {

	public MatrixParameters(URI requestURI) {
		super(requestURI);
	}

	@Override
	protected String resolveParametersParts(URI requestURI,
			ReadableBuffer body, MediaType contentType, Charset charset)
			throws IOException {
		String path = requestURI.getRawQuery();
		if (StringUtils.isEmpty(path)) {
			path = requestURI.getRawPath();
		} else {
			path = requestURI.getRawPath() + '?' + path;
		}
		int splitted = path.indexOf(';');
		if (splitted < 0) {
			return StringUtils.EMPTY;
		} else {
			return StringUtils.substring(path, splitted + 1);
		}
	}

	@Override
	protected char getParameterSeparator() {
		return ';';
	}
}
