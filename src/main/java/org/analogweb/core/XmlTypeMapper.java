package org.analogweb.core;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapper;

/**
 * JAXBによる変換により、リクエストされたXMLを任意のオブジェクト
 * のインスタンスに変換する{@link TypeMapper}の実装です。<br/>
 * 変換元の値として、リクエストされたXMLを保持する{@link InputStream}
 * (リクエストボディ)が指定されている必要があります。
 * @author snowgoose
 */
public class XmlTypeMapper extends AbstractAttributesHandler implements
        SpecificMediaTypeAttirbutesHandler {

    @Override
    public String getScopeName() {
        return "xml";
    }

    @Override
    public Object resolveAttributeValue(RequestContext context, InvocationMetadata metadata,
            String key, Class<?> requiredType) {
        try {
            return unmershall(createUnmarshaller(requiredType), context.getRequestBody());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean supports(MediaType mediaType) {
        return MediaTypes.valueOf("*/xml").isCompatible(mediaType)
                || mediaType.getSubType().endsWith("+xml");
    }

    private Unmarshaller createUnmarshaller(Class<?> requiredType) {
        try {
            JAXBContext jaxb = JAXBContext.newInstance(requiredType);
            Unmarshaller un = jaxb.createUnmarshaller();
            return un;
        } catch (JAXBException e) {
            return null;
        }
    }

    private Object unmershall(Unmarshaller unmarshaller, InputStream in) {
        try {
            return unmarshaller.unmarshal(in);
        } catch (JAXBException e) {
            return null;
        }
    }

}
