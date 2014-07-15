package org.analogweb.core.httpserver;

import static org.analogweb.core.DefaultApplicationProperties.defaultProperties;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProperties;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.ResponseContext;
import org.analogweb.core.DefaultRequestPath;
import org.analogweb.util.ApplicationPropertiesHolder;
import org.analogweb.util.Assertion;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.FileClassCollector;
import org.analogweb.util.JarClassCollector;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;

/**
 * @author snowgoose
 */
public class AnalogHandler implements HttpHandler {

	private final Application app;
	private final ApplicationContext resolver;
	private final ApplicationProperties props;

	public AnalogHandler(Application app) {
		this(app, (ApplicationContext) null);
	}

	public AnalogHandler(Application app, ApplicationContext contextResolver) {
		this(app, contextResolver, ApplicationPropertiesHolder.configure(app,
				defaultProperties()));
	}

	public AnalogHandler(Application app, ApplicationProperties props) {
		this(app, null, props);
	}

	public AnalogHandler(Application app, ApplicationContext contextResolver,
			ApplicationProperties props) {
		Assertion.notNull(app, Application.class.getName());
		this.app = app;
		this.resolver = contextResolver;
		this.props = props;
	}

	public void run() {
		this.app.run(resolver, props, getClassCollectors(), Thread
				.currentThread().getContextClassLoader());
	}

	public void shutdown() {
		this.app.dispose();
	}

	@Override
	public void handle(HttpExchange exc) throws IOException {
		try {
			RequestContext rcontext = createRequestContext(exc);
			ResponseContext response = createResponseContext(exc);
			int proceed = this.app.processRequest(rcontext.getRequestPath(),
					rcontext, response);
			if (proceed == Application.NOT_FOUND) {
				exc.getResponseHeaders().clear();
				exc.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, -1);
				exc.close();
				return;
			}
			response.commmit(rcontext);
		} catch (Exception e) {
			e.printStackTrace();
			exc.getResponseHeaders().clear();
			exc.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			throw new IOException(e);
		}
	}

	protected ResponseContext createResponseContext(HttpExchange exc) {
		return new HttpExchangeResponseContext(exc);
	}

	protected RequestContext createRequestContext(HttpExchange exc)
			throws URISyntaxException {
		RequestPath requestPath = createRequestPath(exc);
		return new HttpExchangeRequestContext(exc, requestPath,
				this.props.getDefaultClientLocale());
	}

	protected RequestPath createRequestPath(HttpExchange exc)
			throws URISyntaxException {
		String basePath = exc.getHttpContext().getPath();
		InetSocketAddress addr = exc.getLocalAddress();
		URI baseURI = new URI(
				(exc instanceof HttpsExchange) ? "https" : "http", null,
				addr.getHostName(), addr.getPort(), basePath, null, null);
		URI requestURI = baseURI.resolve(exc.getRequestURI());
		return new DefaultRequestPath(baseURI, requestURI,
				exc.getRequestMethod());
	}

	protected List<ClassCollector> getClassCollectors() {
		List<ClassCollector> list = new ArrayList<ClassCollector>();
		list.add(new JarClassCollector());
		list.add(new FileClassCollector());
		return Collections.unmodifiableList(list);
	}
}
