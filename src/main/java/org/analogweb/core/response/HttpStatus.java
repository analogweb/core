package org.analogweb.core.response;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.Renderable;
import org.analogweb.Headers;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.WebApplicationException;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public enum HttpStatus implements Renderable {
    CONTINUE(100), SWITCHING_PROTOCOLS(101), PROCESSING(102), OK(200), CREATED(201), ACCEPTED(202), NON_AUTHORITATIVE_INFORMATION(
            203), NO_CONTENT(204), RESET_CONTENT(205), PARTIAL_CONTENT(206), MULTI_STATUS(207), ALREADY_REPORTED(
            208), IM_USED(226), MULTIPLE_CHOICES(300), MOVED_PERMANENTLY(301), FOUND(302), SEE_OTHER(
            303), NOT_MODIFIED(304), USE_PROXY(305), TEMPORARY_REDIRECT(307), BAD_REQUEST(400), UNAUTHORIZED(
            401), PAYMENT_REQUIRED(402), FORBIDDEN(403), NOT_FOUND(404), METHOD_NOT_ALLOWED(405), NOT_ACCEPTABLE(
            406), PROXY_AUTHENTICATION_REQUIRED(407), REQUEST_TIMEOUT(408), CONFLICT(409), GONE(410), LENGTH_REQUIRED(
            411), PRECONDITION_FAILED(412), REQUEST_ENTITY_TOO_LARGE(413), REQUEST_URI_TOO_LONG(414), UNSUPPORTED_MEDIA_TYPE(
            415), REQUESTED_RANGE_NOT_SATISFIABLE(416), EXPECTATION_FAILED(417), INSUFFICIENT_SPACE_ON_RESOURCE(
            419), METHOD_FAILURE(420), DESTINATION_LOCKED(421), UNPROCESSABLE_ENTITY(422), LOCKED(
            423), FAILED_DEPENDENCY(424), UPGRADE_REQUIRED(426), INTERNAL_SERVER_ERROR(500), NOT_IMPLEMENTED(
            501), BAD_GATEWAY(502), SERVICE_UNAVAILABLE(503), GATEWAY_TIMEOUT(504), HTTP_VERSION_NOT_SUPPORTED(
            505), VARIANT_ALSO_NEGOTIATES(506), INSUFFICIENT_STORAGE(507), LOOP_DETECTED(508), NOT_EXTENDED(
            510);

    private int statusCode;
    private String reason;
    private Map<String, String> responseHeaders;
    private Renderable preRenderDirection;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void render(RequestContext context, ResponseContext response) throws IOException,
            WebApplicationException {
        String reason = getReason();
        if (StringUtils.isNotEmpty(reason)) {
            Text.with(reason).render(context, response);
        } else {
            Renderable preRenderDirection = getPreRenderResponse();
            if (preRenderDirection != null) {
                preRenderDirection.render(context, response);
            }
        }
        Headers headers = response.getResponseHeaders();
        Map<String, String> headersMap = getResponseHeaders();
        if (headersMap != null) {
            for (Entry<String, String> e : headersMap.entrySet()) {
                headers.putValue(e.getKey(), e.getValue());
            }
        }
        response.setStatus(getStatusCode());
    }

    public static HttpStatus valueOf(int statusCode) {
        for (HttpStatus status : values()) {
            if (statusCode == status.getStatusCode()) {
                return status;
            }
        }
        // TODO replace message code.
        throw new IllegalArgumentException("invalid http status code " + statusCode);
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getReason() {
        return this.reason;
    }

    public Renderable getPreRenderResponse() {
        return this.preRenderDirection;
    }

    public HttpStatus byReasonOf(String reason) {
        this.reason = reason;
        return this;
    }

    public HttpStatus withHeader(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
        return this;
    }

    public Map<String, String> getResponseHeaders() {
        return this.responseHeaders;
    }

    public HttpStatus with(Renderable direction) {
        this.preRenderDirection = direction;
        return this;
    }
}
