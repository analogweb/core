package jp.acme.test.additionalcomponents;

import java.lang.reflect.Method;


import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestAttributes;
import org.analogweb.RequestContext;
import org.analogweb.TypeMapperContext;
import org.analogweb.core.AbstractInvocationProcessor;


/**
 * @author snowgoose
 */
public class StubPreProcessor extends AbstractInvocationProcessor {

    @Override
    public Invocation prepareInvoke(Method method, Invocation invocation,
            InvocationMetadata metadata, RequestContext context, RequestAttributes attributes,
            TypeMapperContext converters) {
        return null;
    }

}
