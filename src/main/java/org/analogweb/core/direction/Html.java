package org.analogweb.core.direction;

import java.util.Map;

import org.analogweb.DirectionFormatter;
import org.analogweb.util.Assertion;

/**
 * @author snowgoose
 */
public class Html extends TextFormattable<Html> {

    private static final String DEFAULT_HTML_CHARSET = "UTF-8";
    private static final String DEFAULT_HTML_CONTENT_TYPE = "text/html";

    public static Html as(String templatePath,Map<String,Object> context) {
        Html html = new Html(new HtmlTemplate(templatePath, context));
        return html;
    }

    public static class HtmlTemplate {
        private String templateResourcePath;
        private Map<String,Object> context;
        public HtmlTemplate(String templateResourcePath,Map<String,Object> context){
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
        super.as(DEFAULT_HTML_CONTENT_TYPE);
        super.withCharset(DEFAULT_HTML_CHARSET);
    }

    protected Html(String input) {
        super(input);
        super.as(DEFAULT_HTML_CONTENT_TYPE);
        super.withCharset(DEFAULT_HTML_CHARSET);
    }

    @Override
    protected DirectionFormatter getDefaultFormatter() {
        // TODO Implement!
        throw new UnsupportedOperationException();
    }

}