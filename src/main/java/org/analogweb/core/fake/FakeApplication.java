package org.analogweb.core.fake;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProperties;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.ResponseContext;
import org.analogweb.core.AbstractRequestContext;
import org.analogweb.core.AbstractResponseContext;
import org.analogweb.core.ApplicationRuntimeException;
import org.analogweb.core.DefaultApplicationContext;
import org.analogweb.core.DefaultApplicationProperties;
import org.analogweb.core.DefaultRequestPath;
import org.analogweb.core.MapHeaders;
import org.analogweb.core.WebApplication;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.FileClassCollector;
import org.analogweb.util.JarClassCollector;
import org.analogweb.util.Maps;

/**
 * @author snowgooseyk
 */
public class FakeApplication {

	private Application app;
	private final ApplicationContext resolver;
	private final ApplicationProperties props;

	public static FakeApplication fakeApplication() {
		return new FakeApplication();
	}

	public static FakeApplication fakeApplication(ApplicationProperties props) {
		return new FakeApplication(props);
	}

	public FakeApplication() {
		this(DefaultApplicationContext.context(Maps
				.<String, Object> newEmptyHashMap()));
	}

	public FakeApplication(ApplicationContext contextResolver) {
		this(contextResolver, DefaultApplicationProperties.defaultProperties());
	}

	public FakeApplication(ApplicationProperties props) {
		this(DefaultApplicationContext.context(Maps
				.<String, Object> newEmptyHashMap()), props);
	}

	public FakeApplication(ApplicationContext contextResolver,
			ApplicationProperties props) {
		this.resolver = contextResolver;
		this.props = props;
	}

	public ResponseResult request(String path, String method) {
		return request(path, method, new ByteArrayInputStream(new byte[0]));
	}

	public ResponseResult request(String path, String method, final String body) {
		return request(path, method,
				Maps.<String, List<String>> newEmptyHashMap(),
				new ByteArrayInputStream(body.getBytes()));
	}

	public ResponseResult request(String path, String method,
			final InputStream body) {
		return request(path, method,
				Maps.<String, List<String>> newEmptyHashMap(), body);
	}

	public ResponseResult request(String path, String method,
			final Map<String, List<String>> headers, final InputStream body) {
		RequestPath requestPath = new DefaultRequestPath(URI.create("/"),
				URI.create(path), method);
		RequestContext request = new AbstractRequestContext(requestPath,
				Locale.getDefault()) {
			@Override
			public Headers getRequestHeaders() {
				return new MapHeaders(headers);
			}

			@Override
			public InputStream getRequestBody() throws IOException {
				return body;
			}
		};
		final ResponseResult result = new ResponseResult();
		ResponseContext response = new AbstractResponseContext() {
			@Override
			public void commmit(RequestContext context) {
				commitHeadersAndStatus(result, context);
				Headers headers = getResponseHeaders();
				if(headers instanceof MapHeaders){
					result.setResponseHeader(((MapHeaders)headers).toMap());
				}
				try {
					ResponseEntity entity = getResponseWriter().getEntity();
					// no content.
					if (entity != null) {
						entity.writeInto(result.getResponseBody());
					}
					result.getResponseBody().flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private void commitHeadersAndStatus(ResponseResult ex,
					RequestContext context) {
				int status = getStatus();
				if (status == 204) {
					ex.setStatus(204);
					ex.add("Content-Length", "0");
				} else {
					long length = getContentLength();
					if (length == 0) {
						ex.setStatus(204);
						ex.add("Content-Length", "0");
					} else {
						ex.setStatus(status);
						ex.add("Content-Length",
								String.valueOf(length));
					}
				}
			}
		};
		if (app == null) {
			app = new WebApplication();
			app.run(resolver, props, getClassCollectors(), Thread
					.currentThread().getContextClassLoader());
		}
		try {
			int resultCode = app.processRequest(requestPath, request, response);
			if (resultCode == WebApplication.NOT_FOUND) {
				result.setStatus(404);
				return result;
			}
			response.commmit(request);
		} catch (Exception e) {
			throw new ApplicationRuntimeException(e) {
				private static final long serialVersionUID = 1L;
			};
		}
		return result;
	}

	protected List<ClassCollector> getClassCollectors() {
		List<ClassCollector> list = new ArrayList<ClassCollector>();
		list.add(new JarClassCollector());
		list.add(new FileClassCollector());
		return Collections.unmodifiableList(list);
	}
}
