package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;


import org.analogweb.AttributesHandler;
import org.analogweb.ContainerAdaptor;
import org.analogweb.ContainerAdaptorFactory;
import org.analogweb.DirectionHandler;
import org.analogweb.DirectionResolver;
import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.Modules;
import org.analogweb.RequestAttributesFactory;
import org.analogweb.RequestContextFactory;
import org.analogweb.ResultAttributes;
import org.analogweb.ResultAttributesFactory;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.core.DefaultModulesBuilder;
import org.analogweb.exception.AssertionFailureException;
import org.analogweb.exception.MissingModuleException;
import org.analogweb.exception.MissingModulesProviderException;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author snowgoose
 */
public class DefaultModulesBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static final Log log = Logs.getLog(DefaultModulesBuilderTest.class);

    private DefaultModulesBuilder builder;
    private ServletContext servletContext;
    // relative mocks.
    private static MockModulesProvidingContainerAdaptor adaptor = new MockModulesProvidingContainerAdaptor();
    private InvocationMetadataFactory invocationMetadataFactory;
    private Invoker invoker;
    @SuppressWarnings("rawtypes")
    private ContainerAdaptorFactory containerAdaptorFactory;
    private ContainerAdaptor containerAdaptor;
    private InvocationFactory invocationFactory;
    private DirectionResolver directionResolver;
    private DirectionHandler directiontHandler;
    private InvocationProcessor invocationProcessor;
    private RequestContextFactory requestContextFactory;
    private RequestAttributesFactory requestAttributesFactory;
    private AttributesHandler attributesHandler;
    private ResultAttributesFactory resultAttributesFactory;
    private ExceptionHandler exceptionHandler;
    private TypeMapperContext typeMapperContext;
    private TypeMapper typeMapper;

    @Before
    public void setUp() {

        builder = new DefaultModulesBuilder();

        servletContext = mock(ServletContext.class);

        invocationMetadataFactory = mock(InvocationMetadataFactory.class);
        invoker = mock(Invoker.class);
        containerAdaptorFactory = mock(ContainerAdaptorFactory.class);
        containerAdaptor = mock(ContainerAdaptor.class);
        when(containerAdaptorFactory.createContainerAdaptor(servletContext)).thenReturn(
                containerAdaptor);
        invocationFactory = mock(InvocationFactory.class);
        directionResolver = mock(DirectionResolver.class);
        directiontHandler = mock(DirectionHandler.class);
        invocationProcessor = mock(InvocationProcessor.class);
        requestContextFactory = mock(RequestContextFactory.class);
        requestAttributesFactory = mock(RequestAttributesFactory.class);
        attributesHandler = mock(AttributesHandler.class);
        resultAttributesFactory = mock(ResultAttributesFactory.class);
        exceptionHandler = mock(ExceptionHandler.class);
        typeMapperContext = mock(TypeMapperContext.class);
        typeMapper = mock(TypeMapper.class);

        adaptor.register(invocationMetadataFactory.getClass(), invocationMetadataFactory);
        adaptor.register(invoker.getClass(), invoker);
        adaptor.register(containerAdaptorFactory.getClass(), containerAdaptorFactory);
        adaptor.register(invocationFactory.getClass(), invocationFactory);
        adaptor.register(directionResolver.getClass(), directionResolver);
        adaptor.register(directiontHandler.getClass(), directiontHandler);
        adaptor.register(invocationProcessor.getClass(), invocationProcessor);
        adaptor.register(requestContextFactory.getClass(), requestContextFactory);
        adaptor.register(requestAttributesFactory.getClass(), requestAttributesFactory);
        adaptor.register(attributesHandler.getClass(), attributesHandler);
        adaptor.register(resultAttributesFactory.getClass(), resultAttributesFactory);
        adaptor.register(exceptionHandler.getClass(), exceptionHandler);
        adaptor.register(typeMapperContext.getClass(), typeMapperContext);
        adaptor.register(typeMapper.getClass(), typeMapper);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBuildModules() {
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        builder.setInvocationFactoryClass(invocationFactory.getClass());
        builder.setInvocationInstanceProviderClass((Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>>) containerAdaptorFactory
                .getClass());
        builder.setInvokerClass(invoker.getClass());
        builder.setDirectionHandlerClass(directiontHandler.getClass());
        builder.setDirectionResolverClass(directionResolver.getClass());
        builder.setRequestContextFactoryClass(requestContextFactory.getClass());
        builder.addInvocationProcessorClass(invocationProcessor.getClass());
        builder.addInvocationMetadataFactoriesClass(invocationMetadataFactory.getClass());
        builder.setRequestAttributesFactoryClass(requestAttributesFactory.getClass());
        builder.addAttributesHandlerClass(attributesHandler.getClass());
        builder.setResultAttributesFactoryClass(resultAttributesFactory.getClass());
        builder.setExceptionHandlerClass(exceptionHandler.getClass());
        builder.setTypeMapperContextClass(typeMapperContext.getClass());

        Modules modules = builder.buildModules(servletContext, adaptor);

        assertSame(modules.getInvocationMetadataFactories().get(0), invocationMetadataFactory);
        assertSame(modules.getInvocationInstanceProvider(), containerAdaptor);
        // same instance.
        assertSame(modules.getInvocationInstanceProvider(), containerAdaptor);
        assertSame(modules.getInvoker(), invoker);
        assertSame(modules.getInvocationFactory(), invocationFactory);
        assertSame(modules.getDirectionResolver(), directionResolver);
        assertSame(modules.getDirectionHandler(), directiontHandler);
        assertSame(modules.getInvocationProcessors().get(0), invocationProcessor);
        assertSame(modules.getRequestContextFactory(), requestContextFactory);
        assertSame(modules.getRequestAttributesFactory(), requestAttributesFactory);
        assertSame(modules.getAttributesHandlers().get(0), attributesHandler);
        assertSame(modules.getResultAttributesFactory(), resultAttributesFactory);
        assertSame(modules.getExceptionHandler(), exceptionHandler);
        assertSame(modules.getTypeMapperContext(), typeMapperContext);
        assertSame(modules.findTypeMapper(typeMapper.getClass()), typeMapper);

        when(attributesHandler.getScopeName()).thenReturn("request");
        assertSame(modules.getAttributesHandlersMap().get("request"),
                attributesHandler);
        assertSame(modules.getAttributesHandlersMap().get("request"),
                attributesHandler);
    }

    @Test
    public void testBuildModulesWithNullContainerAdaptorFactory() {
        thrown.expect(MissingModulesProviderException.class);
        builder.setModulesProviderClass(MockNullContainerAdaptorFactory.class);
        builder.buildModules(servletContext, adaptor);

    }

    @Test
    public void testModulesNotFound() {
        thrown.expect(new NoDescribeMatcher<MissingModuleException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof MissingModuleException) {
                    MissingModuleException mm = (MissingModuleException) arg0;
                    assertThat(mm.getRequiredModuleClass().getCanonicalName(),
                            is(Invoker.class.getCanonicalName()));
                    return true;
                }
                return false;
            }
        });
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        builder.setInvokerClass(Invoker.class);
        Modules modules = builder.buildModules(servletContext, adaptor);
        modules.getInvoker();
    }

    @Test
    public void testOptionalModuleFound() {
        ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        Invoker foundInvoker = mock(Invoker.class);
        when(defaultAdaptor.getInstanceOfType(Invoker.class)).thenReturn(foundInvoker);

        builder.setInvokerClass(Invoker.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(servletContext, defaultAdaptor);
        adaptor.unregister(Invoker.class);

        Invoker actual = modules.getInvoker();
        log.debug(actual.toString());
        assertThat(actual, is(foundInvoker));
    }

    @Test
    public void testOptionalModulesFound() {
        ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        InvocationProcessor foundProcessor = mock(InvocationProcessor.class);
        List<InvocationProcessor> processors = Arrays.asList(foundProcessor);
        when(defaultAdaptor.getInstancesOfType(InvocationProcessor.class)).thenReturn(processors);
        builder.addInvocationProcessorClass(InvocationProcessor.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(servletContext, defaultAdaptor);
        adaptor.unregister(InvocationProcessor.class);

        List<InvocationProcessor> actual = modules.getInvocationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(foundProcessor));
    }

    @Test
    public void testOptionalMultiSameTypeModulesFound() {
        ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        InvocationProcessor foundProcessor = mock(InvocationProcessor.class);
        InvocationProcessor foundProcessor2 = mock(InvocationProcessor.class);
        List<InvocationProcessor> processors = Arrays.asList(foundProcessor, foundProcessor2);
        when(defaultAdaptor.getInstancesOfType(InvocationProcessor.class)).thenReturn(processors);
        builder.addInvocationProcessorClass(InvocationProcessor.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(servletContext, defaultAdaptor);
        adaptor.unregister(InvocationProcessor.class);

        List<InvocationProcessor> actual = modules.getInvocationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(foundProcessor));
    }

    @Test
    public void testModuleClassUndefined() {
        thrown.expect(AssertionFailureException.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(servletContext, adaptor);
        modules.getInvoker();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildResultAttribute() {
        builder.setResultAttributesFactoryClass(resultAttributesFactory.getClass());
        final ResultAttributes resultAttributes = mock(ResultAttributes.class);
        doAnswer(new Answer<ResultAttributes>() {
            @Override
            public ResultAttributes answer(InvocationOnMock invocation) throws Throwable {
                Map<String, AttributesHandler> placers = (Map<String, AttributesHandler>) invocation
                        .getArguments()[0];
                AttributesHandler placer = placers.get("someScope");
                assertSame(placer, attributesHandler);
                return resultAttributes;
            }
        }).when(resultAttributesFactory).createResultAttributes(isA(Map.class));
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        builder.addAttributesHandlerClass(attributesHandler.getClass());
        when(attributesHandler.getScopeName()).thenReturn("someScope");
        Modules modules = builder.buildModules(servletContext, adaptor);
        ResultAttributes actual = modules.getResultAttributes();

        assertSame(actual, resultAttributes);
        actual = modules.getResultAttributes();

        // returns same instance again.
        assertSame(actual, resultAttributes);
    }

    @Test
    public void testClearMultipleModules() {
        builder.addInvocationMetadataFactoriesClass(invocationMetadataFactory.getClass());
        builder.addInvocationProcessorClass(invocationProcessor.getClass());
        builder.addAttributesHandlerClass(attributesHandler.getClass());

        assertThat(builder.getInvocationMetadataFactoryClasses().size(), is(1));
        assertThat(builder.getInvocationProcessorClasses().size(), is(1));
        assertThat(builder.getAttributesHandlerClasses().size(), is(1));

        assertSame(builder, builder.clearInvocationMetadataFactoriesClass());
        assertTrue(builder.getInvocationMetadataFactoryClasses().isEmpty());

        assertSame(builder, builder.clearInvocationProcessorClass());
        assertTrue(builder.getInvocationProcessorClasses().isEmpty());

        assertSame(builder, builder.clearAttributesHanderClass());
        assertTrue(builder.getAttributesHandlerClasses().isEmpty());

    }

    public static class MockModulesProvidingContainerAdaptor implements ContainerAdaptor {

        private final Map<Class<Object>, Object> classDefMap = new HashMap<Class<Object>, Object>();


        @Override
        @SuppressWarnings("unchecked")
        public <T> T getInstanceOfType(Class<T> type) {
            return (T) classDefMap.get(type);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> List<T> getInstancesOfType(Class<T> type) {
            T instance = (T) classDefMap.get(type);
            if (instance != null) {
                return Arrays.asList(instance);
            } else {
                return new ArrayList<T>();
            }
        }

        @SuppressWarnings("unchecked")
        void register(Class<?> clazz, Object instance) {
            this.classDefMap.put((Class<Object>) clazz, instance);
        }

        void unregister(Class<?> clazz) {
            this.classDefMap.remove(clazz);
        }
    }

    public static class MockModulesProvidingContainerAdaptorFactory implements
            ContainerAdaptorFactory<MockModulesProvidingContainerAdaptor> {

        @Override
        public MockModulesProvidingContainerAdaptor createContainerAdaptor(
                ServletContext servletContext) {
            return adaptor;
        }

    }

    public static class MockNullContainerAdaptorFactory implements
            ContainerAdaptorFactory<MockModulesProvidingContainerAdaptor> {

        @Override
        public MockModulesProvidingContainerAdaptor createContainerAdaptor(
                ServletContext servletContext) {
            return null;
        }

    }

    public interface Foo {
    }

    public static class FooImpl implements Foo {
    }

}
