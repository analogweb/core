package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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
import org.analogweb.Direction;
import org.analogweb.DirectionFormatter;
import org.analogweb.DirectionHandler;
import org.analogweb.DirectionResolver;
import org.analogweb.ExceptionHandler;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationProcessor;
import org.analogweb.Invoker;
import org.analogweb.Modules;
import org.analogweb.MultiModule;
import org.analogweb.RequestContextFactory;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
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
    private AttributesHandler attributesHandler;
    private ExceptionHandler exceptionHandler;
    private TypeMapperContext typeMapperContext;
    private TypeMapper typeMapper;
    private DirectionFormatter directionFormatter;

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
        attributesHandler = mock(AttributesHandler.class);
        exceptionHandler = mock(ExceptionHandler.class);
        typeMapperContext = mock(TypeMapperContext.class);
        typeMapper = mock(TypeMapper.class);
        directionFormatter = mock(DirectionFormatter.class);

        adaptor.register(invocationMetadataFactory.getClass(), invocationMetadataFactory);
        adaptor.register(invoker.getClass(), invoker);
        adaptor.register(containerAdaptorFactory.getClass(), containerAdaptorFactory);
        adaptor.register(invocationFactory.getClass(), invocationFactory);
        adaptor.register(directionResolver.getClass(), directionResolver);
        adaptor.register(directiontHandler.getClass(), directiontHandler);
        adaptor.register(invocationProcessor.getClass(), invocationProcessor);
        adaptor.register(requestContextFactory.getClass(), requestContextFactory);
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
        builder.setInvocationInstanceProviderClass((Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>>) containerAdaptorFactory
                .getClass());
        builder.setInvokerClass(invoker.getClass());
        builder.setDirectionHandlerClass(directiontHandler.getClass());
        builder.setDirectionResolverClass(directionResolver.getClass());
        builder.setRequestContextFactoryClass(requestContextFactory.getClass());
        builder.addInvocationProcessorClass(invocationProcessor.getClass());
        builder.addInvocationMetadataFactoriesClass(invocationMetadataFactory.getClass());
        builder.addAttributesHandlerClass(attributesHandler.getClass());
        builder.setExceptionHandlerClass(exceptionHandler.getClass());
        builder.setTypeMapperContextClass(typeMapperContext.getClass());
        Direction mapToDirection = mock(Direction.class);
        builder.addDirectionFormatterClass(mapToDirection.getClass(), directionFormatter.getClass());

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
        assertSame(modules.getExceptionHandler(), exceptionHandler);
        assertSame(modules.getTypeMapperContext(), typeMapperContext);
        assertSame(modules.findTypeMapper(typeMapper.getClass()), typeMapper);
        assertSame(modules.findDirectionFormatter(mapToDirection.getClass()), directionFormatter);

        when(attributesHandler.getScopeName()).thenReturn("request");
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

    @Test
    public void testIgnoreModules() {
        final ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);

        List<InvocationProcessor> processors = new ArrayList<InvocationProcessor>();
        processors.add(new ProcessorA());
        processors.add(new ProcessorB());
        processors.add(new ProcessorC());

        when(defaultAdaptor.getInstancesOfType(InvocationProcessor.class)).thenReturn(processors);

        ca = defaultAdaptor;
        builder.setModulesProviderClass(MockContainerAdaptorFactory.class);
        builder.addInvocationProcessorClass(InvocationProcessor.class);

        Modules modules = builder.buildModules(servletContext, defaultAdaptor);

        List<InvocationProcessor> actual = modules.getInvocationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(3));

        builder.ignore(ProcessorB.class);
        modules = builder.buildModules(servletContext, defaultAdaptor);
        actual = modules.getInvocationProcessors();
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

    public static class MockContainerAdaptorFactory implements
            ContainerAdaptorFactory<ContainerAdaptor> {

        @Override
        public ContainerAdaptor createContainerAdaptor(ServletContext servletContext) {
            return ca;
        }

    }

    @Test
    public void testIgnoreModulesByFilter() {
        final ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);

        ProcessorA a = new ProcessorA();
        final ProcessorB b = new ProcessorB();
        ProcessorC c = new ProcessorC();

        List<InvocationProcessor> processors = new ArrayList<InvocationProcessor>();
        processors.add(a);
        processors.add(b);
        processors.add(c);

        when(defaultAdaptor.getInstancesOfType(InvocationProcessor.class)).thenReturn(processors);

        ca = defaultAdaptor;
        builder.setModulesProviderClass(MockContainerAdaptorFactory.class);
        builder.addInvocationProcessorClass(InvocationProcessor.class);

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
        Modules modules = builder.buildModules(servletContext, defaultAdaptor);

        List<InvocationProcessor> actual = modules.getInvocationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(2));
    }

    @Test
    public void testIgnoreModulesByMultiFilter() {
        final ContainerAdaptor defaultAdaptor = mock(ContainerAdaptor.class);

        final ProcessorA a = new ProcessorA();
        ProcessorB b = new ProcessorB();
        final ProcessorC c = new ProcessorC();

        List<InvocationProcessor> processors = new ArrayList<InvocationProcessor>();
        processors.add(a);
        processors.add(b);
        processors.add(c);

        when(defaultAdaptor.getInstancesOfType(InvocationProcessor.class)).thenReturn(processors);

        ca = defaultAdaptor;
        builder.setModulesProviderClass(MockContainerAdaptorFactory.class);
        builder.addInvocationProcessorClass(InvocationProcessor.class);

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
        Modules modules = builder.buildModules(servletContext, defaultAdaptor);

        List<InvocationProcessor> actual = modules.getInvocationProcessors();
        log.debug(actual.toString());
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0), is((InvocationProcessor) b));
    }

    private static class ProcessorA extends AbstractInvocationProcessor {
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
