package org.analogweb.core.response;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.analogweb.Renderable;
import org.analogweb.Headers;
import org.analogweb.RenderableHolder;
import org.analogweb.RequestContext;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.WebApplicationException;
import org.analogweb.util.StringUtils;

/**
 * @author snowgoose
 */
public enum HttpStatus implements Renderable, RenderableHolder {
    CONTINUE(100, "Continue"), SWITCHING_PROTOCOLS(101, "Switching Protocols"), PROCESSING(102, "Processing"),
    OK(200, "OK"), CREATED(201, "Created"), ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"), NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"), PARTIAL_CONTENT(206, "Partial Content"), MULTI_STATUS(207, "Multi Status"),
    ALREADY_REPORTED(208, "Already Reported"), IM_USED(226, "IM Used"), MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"), FOUND(302, "Found"), SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"), USE_PROXY(305, "Use Proxy"), TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    BAD_REQUEST(400, "Bad Request"), UNAUTHORIZED(401, "Unauthorized"), PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"), NOT_FOUND(404, "Not Found"), METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"), PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"), CONFLICT(409, "Conflict"), GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"), PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Payload Too Large"), REQUEST_URI_TOO_LONG(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"), EXPECTATION_FAILED(417, "Expectation Failed"),
    INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource"), METHOD_FAILURE(420, "Method Failure"),
    DESTINATION_LOCKED(421, "Destination Locked"), UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    LOCKED(423, "Locked"), FAILED_DEPENDENCY(424, "Failed Dependency"), UPGRADE_REQUIRED(426, "Upgrade Required"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"), NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"), SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"), HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"), INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"), NOT_EXTENDED(510, "Not Extended");

    private int statusCode;
    private String phrase;
    private String reason;
    private Map<String, String> responseHeaders;
    private Renderable actuallyRenderable;

    HttpStatus(final int statusCode, final String phrase) {
        this.statusCode = statusCode;
        this.phrase = phrase;
    }

    @Override
    public Response render(RequestContext context, ResponseContext responseContext)
            throws IOException, WebApplicationException {
        String reason = getReason();
        Response response = Response.EMPTY;
        if (StringUtils.isNotEmpty(reason)) {
            response = Text.with(reason).render(context, responseContext);
        } else {
            Renderable preRenderDirection = getRenderable();
            if (preRenderDirection != null) {
                response = preRenderDirection.render(context, responseContext);
            }
        }
        Headers headers = responseContext.getResponseHeaders();
        Map<String, String> headersMap = getResponseHeaders();
        if (headersMap != null) {
            for (Entry<String, String> e : headersMap.entrySet()) {
                headers.putValue(e.getKey(), e.getValue());
            }
        }
        responseContext.setStatus(getStatusCode());
        return response;
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

    public String getPhrase() {
        return this.phrase;
    }

    public String getReason() {
        return this.reason;
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
        this.actuallyRenderable = direction;
        return this;
    }

    @Override
    public Renderable getRenderable() {
        return this.actuallyRenderable;
    }
}
