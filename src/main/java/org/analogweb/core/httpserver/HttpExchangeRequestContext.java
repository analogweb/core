package org.analogweb.core.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.analogweb.Cookies;
import org.analogweb.Headers;
import org.analogweb.MediaType;
import org.analogweb.Parameters;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.core.EmptyCookies;
import org.analogweb.core.FormParameters;
import org.analogweb.core.MediaTypes;
import org.analogweb.core.QueryParameters;
import org.analogweb.core.RequestCookies;
import org.analogweb.util.CollectionUtils;

import com.sun.net.httpserver.HttpExchange;

/**
 * @author snowgoose
 */
public class HttpExchangeRequestContext implements RequestContext {

	private final HttpExchange ex;
	private final RequestPath requestPath;
	private final Parameters params;
	private final Parameters formParams;

	HttpExchangeRequestContext(HttpExchange ex, RequestPath requestPath) {
		this.ex = ex;
		this.requestPath = requestPath;
		this.params = new QueryParameters(this);
		this.formParams = new FormParameters(this);
	}

	protected HttpExchange getHttpExchange() {
		return this.ex;
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
	public Parameters getFormParameters() {
		return this.formParams;
	}

	@Override
	public InputStream getRequestBody() throws IOException {
		return getHttpExchange().getRequestBody();
	}

	@Override
	public Headers getRequestHeaders() {
		return new HttpExchangeHeaders(getHttpExchange().getRequestHeaders());
	}

	@Override
	public RequestPath getRequestPath() {
		return this.requestPath;
	}

}
