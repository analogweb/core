package org.analogweb.core.direction;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.analogweb.RequestContext;
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

    static class DefaultFormatter implements ReplaceableFormatWriter {
        @Override
        public void write(RequestContext writeTo, String charset, Object source)
                throws FormatFailureException {
            try {
                JAXBContext jaxb = JAXBContext.newInstance(source.getClass());
                jaxb.createMarshaller().marshal(source, writeTo.getResponse().getOutputStream());
            } catch (IOException e) {
                throw new FormatFailureException(e, source, getClass().getName());
            } catch (JAXBException e) {
                throw new FormatFailureException(e, source, getClass().getName());
            }
        }

    }

    @Override
    protected ReplaceableFormatWriter getDefaultFormatter() {
        return new Xml.DefaultFormatter();
    }

}
