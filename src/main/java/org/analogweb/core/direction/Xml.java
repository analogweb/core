package org.analogweb.core.direction;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.analogweb.exception.FormatFailureException;

/**
 * オブジェクトをXMLにフォーマットしてレスポンスする
 * {@link org.analogweb.Direction}の実装です。<br/>
 * デフォルトのContent-Typeは「application/xml; charset=UTF-8」です。
 * @author snowgoose
 */
public class Xml extends TextFormattable {

    private static final String DEFAULT_CONTENT_TYPE = "application/xml";
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static Xml as(Object source) {
        return new Xml(source);
    }

    protected Xml(Object source) {
        super(source);
        super.as(DEFAULT_CONTENT_TYPE);
        super.withCharset(DEFAULT_CHARSET);
    }

    static class DefaultFormatter implements ReplaceableFormatter {
        @Override
        public void format(OutputStream writeTo, String charset, Object source)
                throws FormatFailureException {
            try {
                JAXBContext jaxb = JAXBContext.newInstance(source.getClass());
                jaxb.createMarshaller().marshal(source, writeTo);
            } catch (JAXBException e) {
                throw new FormatFailureException(e, source, getClass().getName());
            }
        }

    }

    @Override
    protected ReplaceableFormatter getDefaultFormatter() {
        return new Xml.DefaultFormatter();
    }

}
