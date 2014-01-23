package org.analogweb.core.response;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.analogweb.ResponseContext;
import org.analogweb.ResponseFormatter;
import org.analogweb.RequestContext;
import org.analogweb.ResponseContext.ResponseEntity;
import org.analogweb.core.FormatFailureException;

/**
 * オブジェクトをXMLにフォーマットしてレスポンスする
 * {@link org.analogweb.Renderable}の実装です。<br/>
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

    static class DefaultFormatter implements ResponseFormatter {

        @Override
        public ResponseEntity formatAndWriteInto(RequestContext request, ResponseContext response,
                String charset, final Object source) throws FormatFailureException {
            return new ResponseEntity() {

                @Override
                public void writeInto(OutputStream responseBody) throws IOException {
                    try {
                        final JAXBContext jaxb = JAXBContext.newInstance(source.getClass());
                        try {
                            jaxb.createMarshaller().marshal(source, responseBody);
                        } catch (JAXBException e) {
                            throw new FormatFailureException(e, source, getClass().getName());
                        }
                    } catch (JAXBException e) {
                        throw new FormatFailureException(e, source, getClass().getName());
                    }
                }

                @Override
                public long getContentLength() {
                    return -1;
                }
            };
        }
    }

    @Override
    protected ResponseFormatter getDefaultFormatter() {
        return new Xml.DefaultFormatter();
    }
}
