package org.analogweb.core.direction;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.Direction;
import org.analogweb.RequestContext;
import org.analogweb.ServletRequestContext;
import org.analogweb.exception.MissingRequirmentsException;
import org.analogweb.exception.WebApplicationException;
import org.analogweb.util.Assertion;
import org.analogweb.util.Maps;
import org.analogweb.util.StringUtils;

/**
 * リクエストをフォワードする{@link Direction}です。
 * @see HttpServletRequest#getRequestDispatcher(String)
 * @see RequestDispatcher#forward(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
 * @author snowgoose
 */
public class Forward extends ContextSpecifiedDirection<ServletRequestContext> {

    private final String forwardTo;
    private final Map<String, Object> extractContext;

    protected Forward(String forwardTo) {
        this.forwardTo = forwardTo;
        this.extractContext = Maps.newEmptyHashMap();
    }

    @Override
    protected void renderInternal(ServletRequestContext context) throws IOException, WebApplicationException {
        Assertion.notNull(context, RequestContext.class.getCanonicalName());

        String to = getForwardTo();
        HttpServletRequest request = context.getServletRequest();
        extractContextToRequest(request);
        RequestDispatcher dispatcher = request.getRequestDispatcher(to);
        dispatcher.forward(request, context.getServletResponse());
    }

    protected void extractContextToRequest(HttpServletRequest request) {
        Map<String, Object> context = getExtractContext();
        if (context != null) {
            for (Entry<String, Object> entry : getExtractContext().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    protected Map<String, Object> getExtractContext() {
        return this.extractContext;
    }

    protected String getForwardTo() {
        return this.forwardTo;
    }

    public static Forward to(String path) {
        if (StringUtils.isEmpty(path) || path.startsWith("/") == false) {
            throw new MissingRequirmentsException("forward path", path);
        }
        return new Forward(path);
    }

    public Forward with(Object serializable) {
        if (serializable != null) {
            String name = serializable.getClass().getSimpleName();
            StringBuilder attributeName = new StringBuilder();
            attributeName.append(name.substring(0, 1).toLowerCase());
            if (name.length() > 1) {
                attributeName.append(StringUtils.substring(name, 1));
            }
            return with(attributeName.toString(), serializable);
        }
        return this;
    }

    public Forward with(String name, Object serializable) {
        if (StringUtils.isNotEmpty(name)) {
            getExtractContext().put(name, serializable);
        }
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Forward) {
            return forwardTo.equals(((Forward) o).forwardTo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int multiplier = 37;
        int result = 17;
        int hash = forwardTo == null ? 0 : forwardTo.hashCode();
        result = multiplier * result + hash;
        return result;
    }

    public Forward with(Map<String, Object> extractToRequest) {
        if (extractToRequest != null) {
            getExtractContext().putAll(extractToRequest);
        }
        return this;
    }
}