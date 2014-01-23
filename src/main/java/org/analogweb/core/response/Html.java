package org.analogweb.core.response;

import java.util.Map;

import org.analogweb.ResponseFormatter;
import org.analogweb.util.Assertion;
import org.analogweb.util.Maps;

/**
 * @author snowgoose
 */
public class Html extends TextFormattable<Html> {

    private static final String DEFAULT_HTML_CHARSET = "UTF-8";
    private static final String DEFAULT_HTML_CONTENT_TYPE = "text/html";

    public static Html as(String templatePath) {
        Html html = new Html(
                new HtmlTemplate(templatePath, Maps.<String, Object> newEmptyHashMap()));
        return html;
    }

    public static Html as(String templatePath, Map<String, Object> context) {
        Html html = new Html(new HtmlTemplate(templatePath, context));
        return html;
    }

    public static class HtmlTemplate {

        private String templateResourcePath;
        private Map<String, Object> context;

        public HtmlTemplate(String templateResourcePath, Map<String, Object> context) {
            Assertion.notNull(templateResourcePath, "Template resource path");
            this.templateResourcePath = templateResourcePath;
            this.context = context;
        }

        public String getTemplateResource() {
            return templateResourcePath;
        }

        public Map<String, Object> getContext() {
            return context;
        }
    }

    @SuppressWarnings("unchecked")
    public static Html with(String str) {
        Html html = new Html(str);
        return html;
    }

    protected Html(Object source) {
        super(source);
        super.typeAs(DEFAULT_HTML_CONTENT_TYPE);
        super.withCharset(DEFAULT_HTML_CHARSET);
    }

    protected Html(String input) {
        super(input);
        super.typeAs(DEFAULT_HTML_CONTENT_TYPE);
        super.withCharset(DEFAULT_HTML_CHARSET);
    }

    @Override
    protected ResponseFormatter getDefaultFormatter() {
        // TODO Implement!
        throw new UnsupportedOperationException();
    }
}
