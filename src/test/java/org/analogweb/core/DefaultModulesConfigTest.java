package org.analogweb.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.analogweb.ApplicationProcessor;
import org.analogweb.AttributesHandler;
import org.analogweb.ExceptionHandler;
import org.analogweb.ExceptionMapper;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationMetadataFinder;
import org.analogweb.InvokerFactory;
import org.analogweb.ModulesBuilder;
import org.analogweb.RequestValueResolver;
import org.analogweb.ResponseHandler;
import org.analogweb.RenderableResolver;
import org.analogweb.TypeMapperContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultModulesConfigTest {

    private RootModulesConfig config;
    private ModulesBuilder modulesBuilder;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        config = new RootModulesConfig();
        modulesBuilder = mock(ModulesBuilder.class);
    }

    @Test
	public void testPrepare() {
		when(
				modulesBuilder
						.setInvocationInstanceProviderClass(SingletonInstanceContainerAdaptorFactory.class))
				.thenReturn(modulesBuilder);
		when(modulesBuilder.setInvocationFactoryClass(InvocationFactory.class))
				.thenReturn(modulesBuilder);
		when(modulesBuilder.setInvokerFactoryClass(InvokerFactory.class))
				.thenReturn(modulesBuilder);
		when(modulesBuilder.setResponseHandlerClass(ResponseHandler.class))
				.thenReturn(modulesBuilder);
		when(
				modulesBuilder
						.setRenderableResolverClass(RenderableResolver.class))
				.thenReturn(modulesBuilder);
		when(
				modulesBuilder
						.setModulesProviderClass(StaticMappingContainerAdaptorFactory.class))
				.thenReturn(modulesBuilder);
		when(modulesBuilder.setExceptionHandlerClass(ExceptionHandler.class))
				.thenReturn(modulesBuilder);
		when(modulesBuilder.setTypeMapperContextClass(TypeMapperContext.class))
				.thenReturn(modulesBuilder);
		when(
				modulesBuilder
						.addApplicationProcessorClass(ApplicationProcessor.class))
				.thenReturn(modulesBuilder);
		when(
				modulesBuilder
						.addInvocationMetadataFactoriesClass(InvocationMetadataFactory.class))
				.thenReturn(modulesBuilder);
		when(modulesBuilder.addAttributesHandlerClass(AttributesHandler.class))
				.thenReturn(modulesBuilder);
		when(
				modulesBuilder
						.addInvocationMetadataFinderClass(InvocationMetadataFinder.class))
				.thenReturn(modulesBuilder);
		when(
				modulesBuilder
						.addRequestValueResolverClass(RequestValueResolver.class))
				.thenReturn(modulesBuilder);
		when(modulesBuilder.addExceptionMapperClass(ExceptionMapper.class))
				.thenReturn(modulesBuilder);
		config.prepare(modulesBuilder);
	}

    @Test
    public void testPrepareWithNullBuilder() {
        thrown.expect(AssertionFailureException.class);
        config.prepare(null);
    }
}
