package org.analogweb.core.direction;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.analogweb.DirectionFormatter;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.exception.FormatFailureException;

/**
 * オブジェクトをXMLにフォーマットしてレスポンスする
 * {@link org.analogweb.Direction}の実装です。<br/>
 * デフォルトのContent-Typeは「application/xml; charset=UTF-8」です。
 * @author snowgoose
 */
public class Xml extends TextFormattable<Xml> {

    private static final String DEFAULT_CONTENT_TYPE = "application/xml";
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static Xml as(Object source) {
        return new Xml(source);
    }

    protected Xml(Object source) {
        super(source);
        super.typeAs(DEFAULT_CONTENT_TYPE);
        super.withCharset(DEFAULT_CHARSET);
    }

    static class DefaultFormatter implements DirectionFormatter {
        @Override
        public void formatAndWriteInto(RequestContext context, ResponseContext writeTo,
                String charset, final Object source) throws FormatFailureException {
            try {
                final JAXBContext jaxb = JAXBContext.newInstance(source.getClass());
                writeTo.getResponseWriter().writeEntity(new ResponseEntity() {
                    @Override
                    public void writeInto(OutputStream responseBody) throws IOException {
                        try {
                            jaxb.createMarshaller().marshal(source, responseBody);
                        } catch (JAXBException e) {
                            throw new FormatFailureException(e, source, getClass().getName());
                        }
                    }
                });
            } catch (JAXBException e) {
                throw new FormatFailureException(e, source, getClass().getName());
            }
        }

    }

    @Override
    protected DirectionFormatter getDefaultFormatter() {
        return new Xml.DefaultFormatter();
    }

}
