package org.analogweb.core;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.analogweb.InvocationMetadata;
import org.analogweb.MediaType;
import org.analogweb.RequestContext;

/**
 * @author snowgoose
 */
public class XmlValueResolver implements SpecificMediaTypeRequestValueResolver {

    @Override
    public Object resolveValue(RequestContext context, InvocationMetadata metadata, String key,
            Class<?> requiredType, Annotation[] annotations) {
        try {
            return unmershall(createUnmarshaller(requiredType), context.getRequestBody().asInputStream());
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
