package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProcessor;
import org.analogweb.AttributesHandler;
import org.analogweb.ContainerAdaptor;
import org.analogweb.ContainerAdaptorFactory;
import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.Invoker;
import org.analogweb.InvokerFactory;
import org.analogweb.Modules;
import org.analogweb.MultiModule;
import org.analogweb.Renderable;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseHandler;
import org.analogweb.RenderableResolver;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class DefaultModulesBuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private static final Log log = Logs.getLog(DefaultModulesBuilderTest.class);
    private DefaultModulesBuilder builder;
    private ApplicationContext resolver;
    // relative mocks.
    private static MockModulesProvidingContainerAdaptor adaptor = new MockModulesProvidingContainerAdaptor();
    private InvocationMetadataFactory invocationMetadataFactory;
    private InvokerFactory invokerFactory;
    @SuppressWarnings("rawtypes")
    private ContainerAdaptorFactory containerAdaptorFactory;
    private ContainerAdaptor containerAdaptor;
    private InvocationFactory invocationFactory;
    private RenderableResolver directionResolver;
    private ResponseHandler directiontHandler;
    private ApplicationProcessor invocationProcessor;
    private InvocationInterceptor invocationInterceptor;
    private AttributesHandler attributesHandler;
    private ExceptionHandler exceptionHandler;
    private TypeMapperContext typeMapperContext;
    private TypeMapper typeMapper;
    private ResponseFormatter directionFormatter;

    @Before
    public void setUp() {
        builder = new DefaultModulesBuilder();
        resolver = mock(ApplicationContext.class);
        invocationMetadataFactory = mock(InvocationMetadataFactory.class);
        invokerFactory = mock(InvokerFactory.class);
        containerAdaptorFactory = mock(ContainerAdaptorFactory.class);
        containerAdaptor = mock(ContainerAdaptor.class);
        when(containerAdaptorFactory.createContainerAdaptor(resolver)).thenReturn(containerAdaptor);
        invocationFactory = mock(InvocationFactory.class);
        directionResolver = mock(RenderableResolver.class);
        directiontHandler = mock(ResponseHandler.class);
        invocationProcessor = mock(ApplicationProcessor.class);
        invocationInterceptor = mock(InvocationInterceptor.class);
        attributesHandler = mock(AttributesHandler.class);
        exceptionHandler = mock(ExceptionHandler.class);
        typeMapperContext = mock(TypeMapperContext.class);
        typeMapper = mock(TypeMapper.class);
        directionFormatter = mock(ResponseFormatter.class);
        adaptor.register(invocationMetadataFactory.getClass(), invocationMetadataFactory);
        adaptor.register(invokerFactory.getClass(), invokerFactory);
        adaptor.register(containerAdaptorFactory.getClass(), containerAdaptorFactory);
        adaptor.register(invocationFactory.getClass(), invocationFactory);
        adaptor.register(directionResolver.getClass(), directionResolver);
        adaptor.register(directiontHandler.getClass(), directiontHandler);
        adaptor.register(invocationProcessor.getClass(), invocationProcessor);
        adaptor.register(invocationInterceptor.getClass(), invocationInterceptor);
        adaptor.register(attributesHandler.getClass(), attributesHandler);
        adaptor.register(exceptionHandler.getClass(), exceptionHandler);
        adaptor.register(typeMapperContext.getClass(), typeMapperContext);
        adaptor.register(typeMapper.getClass(), typeMapper);
        adaptor.register(directionFormatter.getClass(), directionFormatter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBuildModules() {
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        builder.setInvocationFactoryClass(invocationFactory.getClass());
        builder.setInvocationInstanceProviderClass(
                (Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>>) containerAdaptorFactory
                        .getClass());
        builder.setInvokerFactoryClass(invokerFactory.getClass());
        builder.setResponseHandlerClass(directiontHandler.getClass());
        builder.setRenderableResolverClass(directionResolver.getClass());
        builder.addApplicationProcessorClass(invocationProcessor.getClass());
        builder.addInvocationInterceptorClass(invocationInterceptor.getClass());
        builder.addInvocationMetadataFactoriesClass(invocationMetadataFactory.getClass());
        builder.addAttributesHandlerClass(attributesHandler.getClass());
        builder.setExceptionHandlerClass(exceptionHandler.getClass());
        builder.setTypeMapperContextClass(typeMapperContext.getClass());
        Renderable mapToResponse = mock(Renderable.class);
        builder.addResponseFormatterClass(mapToResponse.getClass(), directionFormatter.getClass());
        Modules modules = builder.buildModules(resolver, adaptor);
        assertSame(modules.getInvocationMetadataFactories().get(0), invocationMetadataFactory);
        assertSame(modules.getInvocationInstanceProvider(), containerAdaptor);
        // same instance.
        assertSame(modules.getInvocationInstanceProvider(), containerAdaptor);
        assertSame(modules.getInvocationFactory(), invocationFactory);
        assertSame(modules.getResponseResolver(), directionResolver);
        assertSame(modules.getResponseHandler(), directiontHandler);
        assertSame(modules.getApplicationProcessors().get(0), invocationProcessor);
        assertSame(modules.getInvocationInterceptors().get(0), invocationInterceptor);
        assertSame(modules.getExceptionHandler(), exceptionHandler);
        assertSame(modules.getTypeMapperContext(), typeMapperContext);
        assertSame(modules.findResponseFormatter(mapToResponse.getClass()), directionFormatter);
    }

    @Test
    public void testBuildModulesWithNullContainerAdaptorFactory() {
        thrown.expect(MissingModulesProviderException.class);
        builder.setModulesProviderClass(MockNullContainerAdaptorFactory.class);
        builder.buildModules(resolver, adaptor);
    }

    @Test
    public void testModulesNotFound() {
        thrown.expect(new NoDescribeMatcher<MissingModuleException>() {

            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof MissingModuleException) {
                    MissingModuleException mm = (MissingModuleException) arg0;
                    assertThat(mm.getRequiredModuleClass().getCanonicalName(),
                            is(InvokerFactory.class.getCanonicalName()));
                    return true;
                }
                return false;
            }
        });
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        builder.setInvokerFactoryClass(InvokerFactory.class);
        Modules modules = builder.buildModules(resolver, adaptor);
        modules.getInvoker();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOptionalModuleFound() {
        ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        Invoker foundInvoker = mock(Invoker.class);
        InvokerFactory factory = mock(InvokerFactory.class);
        when(factory.createInvoker(isA(List.class))).thenReturn(foundInvoker);
        when(defaultAdaptor.getInstanceOfType(InvokerFactory.class)).thenReturn(factory);
        builder.setInvokerFactoryClass(InvokerFactory.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(resolver, defaultAdaptor);
        adaptor.unregister(Invoker.class);
        builder.setTypeMapperContextClass(typeMapperContext.getClass());
        Invoker actual = modules.getInvoker();
        log.debug(actual.toString());
        assertThat(actual, is(foundInvoker));
    }

    @Test
    public void testOptionalModulesFound() {
        ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        ApplicationProcessor foundProcessor = mock(ApplicationProcessor.class);
        List<ApplicationProcessor> processors = Arrays.asList(foundProcessor);
        when(defaultAdaptor.getInstancesOfType(ApplicationProcessor.class)).thenReturn(processors);
        builder.addApplicationProcessorClass(ApplicationProcessor.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(resolver, defaultAdaptor);
        adaptor.unregister(ApplicationProcessor.class);
        List<ApplicationProcessor> actual = modules.getApplicationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(foundProcessor));
    }

    @Test
    public void testOptionalMultiSameTypeModulesFound() {
        ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        ApplicationProcessor foundProcessor = mock(ApplicationProcessor.class);
        ApplicationProcessor foundProcessor2 = mock(ApplicationProcessor.class);
        List<ApplicationProcessor> processors = Arrays.asList(foundProcessor, foundProcessor2);
        when(defaultAdaptor.getInstancesOfType(ApplicationProcessor.class)).thenReturn(processors);
        builder.addApplicationProcessorClass(ApplicationProcessor.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(resolver, defaultAdaptor);
        adaptor.unregister(ApplicationProcessor.class);
        List<ApplicationProcessor> actual = modules.getApplicationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is(foundProcessor));
    }

    @Test
    public void testModuleClassUndefined() {
        thrown.expect(AssertionFailureException.class);
        builder.setModulesProviderClass(MockModulesProvidingContainerAdaptorFactory.class);
        Modules modules = builder.buildModules(resolver, adaptor);
        modules.getInvoker();
    }

    @Test
    public void testClearMultipleModules() {
        builder.addInvocationMetadataFactoriesClass(invocationMetadataFactory.getClass());
        builder.addApplicationProcessorClass(invocationProcessor.getClass());
        builder.addAttributesHandlerClass(attributesHandler.getClass());
        assertThat(builder.getInvocationMetadataFactoryClasses().size(), is(1));
        assertThat(builder.getApplicationProcessorClasses().size(), is(1));
        assertThat(builder.getAttributesHandlerClasses().size(), is(1));
        assertSame(builder, builder.clearInvocationMetadataFactoriesClass());
        assertTrue(builder.getInvocationMetadataFactoryClasses().isEmpty());
        assertSame(builder, builder.clearApplicationProcessorClass());
        assertTrue(builder.getApplicationProcessorClasses().isEmpty());
        assertSame(builder, builder.clearAttributesHanderClass());
        assertTrue(builder.getAttributesHandlerClasses().isEmpty());
    }

    @Test
    public void testIgnoreModules() {
        final ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        List<ApplicationProcessor> processors = new ArrayList<ApplicationProcessor>();
        processors.add(new ProcessorA());
        processors.add(new ProcessorB());
        processors.add(new ProcessorC());
        when(defaultAdaptor.getInstancesOfType(ApplicationProcessor.class)).thenReturn(processors);
        ca = defaultAdaptor;
        builder.setModulesProviderClass(MockContainerAdaptorFactory.class);
        builder.addApplicationProcessorClass(ApplicationProcessor.class);
        Modules modules = builder.buildModules(resolver, defaultAdaptor);
        List<ApplicationProcessor> actual = modules.getApplicationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(3));
        builder.ignore(ProcessorB.class);
        modules = builder.buildModules(resolver, defaultAdaptor);
        actual = modules.getApplicationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(1));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testIgnoreTypesUndefined() {
        thrown.expect(AssertionFailureException.class);
        builder.ignore((Class) null);
    }

    private static ContainerAdaptor ca;

    public static class MockContainerAdaptorFactory implements ContainerAdaptorFactory<ContainerAdaptor> {

        @Override
        public ContainerAdaptor createContainerAdaptor(ApplicationContext resolver) {
            return ca;
        }
    }

    @Test
    public void testIgnoreModulesByFilter() {
        final ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        ProcessorA a = new ProcessorA();
        final ProcessorB b = new ProcessorB();
        ProcessorC c = new ProcessorC();
        List<ApplicationProcessor> processors = new ArrayList<ApplicationProcessor>();
        processors.add(a);
        processors.add(b);
        processors.add(c);
        when(defaultAdaptor.getInstancesOfType(ApplicationProcessor.class)).thenReturn(processors);
        ca = defaultAdaptor;
        builder.setModulesProviderClass(MockContainerAdaptorFactory.class);
        builder.addApplicationProcessorClass(ApplicationProcessor.class);
        MultiModule.Filter filter = new MultiModule.Filter() {

            @Override
            public boolean isAppreciable(MultiModule aMultiModule) {
                if (aMultiModule == b) {
                    return false;
                }
                return true;
            }
        };
        builder.filter(filter);
        Modules modules = builder.buildModules(resolver, defaultAdaptor);
        List<ApplicationProcessor> actual = modules.getApplicationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(2));
    }

    @Test
    public void testIgnoreModulesByMultiFilter() {
        final ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);
        final ProcessorA a = new ProcessorA();
        ProcessorB b = new ProcessorB();
        final ProcessorC c = new ProcessorC();
        List<ApplicationProcessor> processors = new ArrayList<ApplicationProcessor>();
        processors.add(a);
        processors.add(b);
        processors.add(c);
        when(defaultAdaptor.getInstancesOfType(ApplicationProcessor.class)).thenReturn(processors);
        ca = defaultAdaptor;
        builder.setModulesProviderClass(MockContainerAdaptorFactory.class);
        builder.addApplicationProcessorClass(ApplicationProcessor.class);
        MultiModule.Filter filtera = new MultiModule.Filter() {

            @Override
            public boolean isAppreciable(MultiModule aMultiModule) {
                if (aMultiModule == a) {
                    return false;
                }
                return true;
            }
        };
        MultiModule.Filter filterc = new MultiModule.Filter() {

            @Override
            public boolean isAppreciable(MultiModule aMultiModule) {
                if (aMultiModule == c) {
                    return false;
                }
                return true;
            }
        };
        builder.filter(filtera);
        builder.filter(filterc);
        Modules modules = builder.buildModules(resolver, defaultAdaptor);
        List<ApplicationProcessor> actual = modules.getApplicationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is((ApplicationProcessor) b));
    }

    private static class ProcessorA extends AbstractApplicationProcessor {
    }

    private static class ProcessorB extends ProcessorA {
    }

    private static class ProcessorC extends ProcessorB {
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

    public static class MockModulesProvidingContainerAdaptorFactory
            implements ContainerAdaptorFactory<MockModulesProvidingContainerAdaptor> {

        @Override
        public MockModulesProvidingContainerAdaptor createContainerAdaptor(ApplicationContext resolver) {
            return adaptor;
        }
    }

    public static class MockNullContainerAdaptorFactory
            implements ContainerAdaptorFactory<MockModulesProvidingContainerAdaptor> {

        @Override
        public MockModulesProvidingContainerAdaptor createContainerAdaptor(ApplicationContext resolver) {
            return null;
        }
    }

    public interface Foo {
    }

    public static class FooImpl implements Foo {
    }
}
