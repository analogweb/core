package org.analogweb.core.fake;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.analogweb.*;
import org.analogweb.core.*;
import org.analogweb.util.*;

/**
 * {@link Application} for test stub.
 * 
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
		return request(path, method,
				DefaultReadableBuffer.readBuffer(new byte[0]));
	}

	public ResponseResult request(String path, String method, final String body) {
		return request(path, method,
				Maps.<String, List<String>> newEmptyHashMap(),
				DefaultReadableBuffer.readBuffer(body.getBytes()));
	}

	public ResponseResult request(String path, String method,
			final ReadableBuffer body) {
		return request(path, method,
				Maps.<String, List<String>> newEmptyHashMap(), body);
	}

	public ResponseResult request(String path, String method,
			final Map<String, List<String>> headers) {
		return request(path, method, headers,
				DefaultReadableBuffer.readBuffer(new byte[0]));
	}

	public ResponseResult request(String path, final String method,
			final Map<String, List<String>> headers, final ReadableBuffer body) {
		RequestPath requestPath = new DefaultRequestPath(URI.create("/"),
				URI.create(path), method);
		RequestContext request = new AbstractRequestContext(requestPath,
				Locale.getDefault()) {

			@Override
			public Headers getRequestHeaders() {
				return new MapHeaders(headers);
			}

			@Override
			public ReadableBuffer getRequestBody() throws IOException {
				return body;
			}

			@Override
			public String getRequestMethod() {
				return method;
			}
		};
		final ResponseResult result = new ResponseResult();
		ResponseContext response = new AbstractResponseContext() {

			@Override
			public void commit(RequestContext context, Response response) {
				commitHeadersAndStatus(result, context, response);
				Headers headers = getResponseHeaders();
				if (headers instanceof MapHeaders) {
					result.setResponseHeader(((MapHeaders) headers).toMap());
				}
				try {
					OutputStream out = result.getResponseBody();
					ResponseEntity entity = response.getEntity();
					if (entity != null) {
						Object entityBody = entity.entity();
						if (entityBody instanceof byte[]) {
							out.write((byte[]) entityBody);
						} else if (entityBody instanceof InputStream) {
							IOUtils.copy((InputStream) entityBody, out);
						} else if (entityBody instanceof ReadableBuffer) {
							DefaultWritableBuffer.writeBuffer(out).from(
									(ReadableBuffer) entityBody);
						}
					}
					result.getResponseBody().flush();
				} catch (IOException e) {
					throw new ApplicationRuntimeException(e) {

						private static final long serialVersionUID = 1L;
					};
				}
			}

			private void commitHeadersAndStatus(ResponseResult ex,
					RequestContext context, Response response) {
				int status = getStatus();
				if (status == 204) {
					ex.add("Content-Length", "0");
				} else {
					ex.add("Content-Length",
							String.valueOf(response.getContentLength()));
				}
				ex.setStatus(status);
			}
		};
		if (app == null) {
			app = new WebApplication();
			app.run(resolver, props, getClassCollectors(), Thread
					.currentThread().getContextClassLoader());
		}
		try {
			Response resultCode = app.processRequest(requestPath, request,
					response);
			if (resultCode == WebApplication.NOT_FOUND) {
				result.setStatus(404);
				return result;
			}
			response.commit(request, resultCode);
		} catch (Exception e) {
			throw new ApplicationRuntimeException(e) {

				private static final long serialVersionUID = 1L;
			};
		}
		return result;
	}

	public void shutdown() {
		app.dispose();
	}

	protected List<ClassCollector> getClassCollectors() {
		List<ClassCollector> list = new ArrayList<ClassCollector>();
		list.add(new JarClassCollector());
		list.add(new FileClassCollector());
		return Collections.unmodifiableList(list);
	}
}
