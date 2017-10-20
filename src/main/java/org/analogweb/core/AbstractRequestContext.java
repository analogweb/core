package org.analogweb.core;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.analogweb.Cookies;
import org.analogweb.MediaType;
import org.analogweb.Parameters;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.core.AcceptLanguages;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.EmptyCookies;
import org.analogweb.core.FormParameters;
import org.analogweb.core.MatrixParameters;
import org.analogweb.core.MediaTypes;
import org.analogweb.core.QueryParameters;
import org.analogweb.core.RequestCookies;
import org.analogweb.util.CollectionUtils;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public abstract class AbstractRequestContext implements RequestContext {

	private static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";
	private final RequestPath requestPath;
	private final Parameters params;
	private final Parameters matrixParams;
	private Parameters formParams;
	private final AcceptLanguages langs;
	private final Locale defaultLocale;
	private final Map<String, Object> attributes;

	protected AbstractRequestContext(RequestPath requestPath,
			Locale defaultLocale) {
		this.requestPath = requestPath;
		this.params = new QueryParameters(getRequestPath().getRequestURI());
		this.matrixParams = new MatrixParameters(getRequestPath()
				.getRequestURI());
		this.langs = new AcceptLanguages(this);
		this.defaultLocale = defaultLocale;
		this.attributes = Maps.newEmptyHashMap();
	}

	@Override
	public MediaType getContentType() {
		List<String> header = getRequestHeaders().getValues("Content-Type");
		if (CollectionUtils.isEmpty(header)) {
			return null;
		}
		return MediaTypes.valueOf(header.get(0));
	}

	@Override
	public Cookies getCookies() {
		List<String> cookieHeader = getRequestHeaders().getValues("Cookie");
		if (CollectionUtils.isEmpty(cookieHeader)) {
			return new EmptyCookies();
		}
		return new RequestCookies(cookieHeader.get(0));
	}

	@Override
	public Parameters getQueryParameters() {
		return this.params;
	}

	@Override
	public Parameters getMatrixParameters() {
		return this.matrixParams;
	}

	@Override
	public Parameters getFormParameters() {
		if (this.formParams == null) {
			try {
				this.formParams = new FormParameters(getRequestPath()
						.getRequestURI(), getRequestBody(), getContentType());
			} catch (IOException e) {
				throw new ApplicationRuntimeException(e) {

					// TODO
					private static final long serialVersionUID = 1L;
				};
			}
		}
		return this.formParams;
	}

	@Override
	public RequestPath getRequestPath() {
		return this.requestPath;
	}

	@Override
	public List<Locale> getLocales() {
		return this.langs.getLocales();
	}

	@Override
	public Locale getLocale() {
		return CollectionUtils.indexOf(getLocales(), 0, this.defaultLocale);
	}

	@Override
	public long getContentLength() {
		List<String> contentLength = getRequestHeaders().getValues(
				"Content-Length");
		if (contentLength.isEmpty() == false) {
			try {
				return Long.valueOf(contentLength.get(0));
			} catch (NumberFormatException e) {
				return 0;
			}
		}
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		MediaType contentType = getContentType();
		if (contentType == null) {
			return getDefaultCharacterEncoding();
		}
		Map<String, String> params = contentType.getParameters();
		if (params.containsKey("charset")) {
			return params.get("charset");
		}
		return getDefaultCharacterEncoding();
	}

	protected String getDefaultCharacterEncoding() {
		return DEFAULT_CHARACTER_ENCODING;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T) this.attributes.get(name);
	}

	@Override
	public <T> void setAttribute(String name, T value) {
		this.attributes.put(name, value);
	}
}
