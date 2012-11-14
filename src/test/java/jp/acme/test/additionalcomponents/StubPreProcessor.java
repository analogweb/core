package jp.acme.test.additionalcomponents;

import java.lang.reflect.Method;

import org.analogweb.AttributesHandlers;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.core.AbstractInvocationProcessor;

/**
 * @author snowgoose
 */
public class StubPreProcessor extends AbstractInvocationProcessor {

    @Override
    public Invocation prepareInvoke(Method method, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context,
            TypeMapperContext converters,AttributesHandlers handlers) {
        return null;
    }

}
