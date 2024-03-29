package org.analogweb.core.response;

import java.util.Map;

/**
 * @author y2k2mt
 */
public final class BasicResponses {

    private BasicResponses() {
        // nop.
    }

    public static Acceptable acceptable(Object object) {
        return Acceptable.as(object);
    }

    public static Text text(String text) {
        return Text.with(text);
    }

    public static Html htmlText(String text) {
        return Html.with(text);
    }

    public static Html html(String template, Map<String, Object> parameters) {
        return Html.as(template, parameters);
    }

    public static Html html(String template) {
        return Html.as(template);
    }

    public static Redirect redirect(String to) {
        return Redirect.to(to);
    }

    public static Json json(String json) {
        return Json.with(json);
    }

    public static Json json(Object obj) {
        return Json.as(obj);
    }

    public static HttpStatus status(int statusCode) {
        return HttpStatus.valueOf(statusCode);
    }

    public static HttpStatus status(String status) {
        return HttpStatus.valueOf(status);
    }
}
