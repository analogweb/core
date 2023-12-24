package jp.acme.test.additionalcomponents;

import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;
import org.analogweb.core.AbstractApplicationProcessor;

/**
 * @author snowgoose
 */
public class StubPreProcessor extends AbstractApplicationProcessor {

    @Override
    public Invocation prepareInvoke(InvocationArguments args, InvocationMetadata metadata, RequestContext context,
            TypeMapperContext converters, RequestValueResolvers handlers) {
        return null;
    }
}
