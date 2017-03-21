package org.analogweb.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProcessor;
import org.analogweb.AttributesHandler;
import org.analogweb.ContainerAdaptor;
import org.analogweb.ContainerAdaptorFactory;
import org.analogweb.ExceptionHandler;
import org.analogweb.ExceptionMapper;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationMetadataFinder;
import org.analogweb.Invoker;
import org.analogweb.InvokerFactory;
import org.analogweb.Modules;
import org.analogweb.ModulesAware;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesContainerAdaptorAware;
import org.analogweb.MultiModule;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.Renderable;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseHandler;
import org.analogweb.RenderableResolver;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.Assertion;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;

/**
 * @author y2k2mt
 */
public class DefaultModulesBuilder implements ModulesBuilder {

    private ContainerAdaptorFactory<? extends ContainerAdaptor> modulesProvider;
    private Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> modulesProviderClass;
    private ContainerAdaptorFactory<? extends ContainerAdaptor> invocationInstanceProvider;
    private Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> invocationInstanceProviderClass;
    private InvokerFactory invokerFactory;
    private Class<? extends InvokerFactory> invokerFactoryClass;
    private InvocationFactory invocationFactory;
    private Class<? extends InvocationFactory> invocationFactoryClass;
    private RenderableResolver renderableResolver;
    private Class<? extends RenderableResolver> directionResolverClass;
    private ResponseHandler responseHandler;
    private Class<? extends ResponseHandler> directionHandlerClass;
    private ExceptionHandler exceptionHandler;
    private Class<? extends ExceptionHandler> exceptionHandlerClass;
    private TypeMapperContext typeMapperContext;
    private Class<? extends TypeMapperContext> typeMapperContextClass;
    private final List<ApplicationProcessor> applicationProcessors;
    private final List<Class<? extends ApplicationProcessor>> applicationProcessorClasses;
    private final List<InvocationInterceptor> invocationInterceptors;
    private final List<Class<? extends InvocationInterceptor>> invocationInterceptorClasses;
    private final List<InvocationMetadataFactory> invocationMetadataFactories;
    private final List<Class<? extends InvocationMetadataFactory>> invocationMetadataFactoryClasses;
    private final List<InvocationMetadataFinder> invocationMetadataFinders;
    private final List<Class<? extends InvocationMetadataFinder>> invocationMetadataFinderClasses;
    private final List<AttributesHandler> attributesHandlers;
    private final List<Class<? extends AttributesHandler>> attributesHandlerClasses;
    private final List<RequestValueResolver> requestValueResolvers;
    private final List<Class<? extends RequestValueResolver>> requestValueResolverClasses;
    private final List<ExceptionMapper> exceptionMappers;
    private final List<Class<? extends ExceptionMapper>> exceptionMapperClasses;
    private final Map<Class<? extends Renderable>, ResponseFormatter> responseFormatters;
    private final Map<Class<? extends Renderable>, Class<? extends ResponseFormatter>> directionFormatterClasses;
    private final List<Class<? extends MultiModule>> ignoreClasses;
    private final List<MultiModule.Filter> ignoreFilters;

    public DefaultModulesBuilder() {
        this.applicationProcessorClasses = new LinkedList<Class<? extends ApplicationProcessor>>();
        this.invocationInterceptorClasses = new LinkedList<Class<? extends InvocationInterceptor>>();
        this.invocationMetadataFactoryClasses = new LinkedList<Class<? extends InvocationMetadataFactory>>();
        this.invocationMetadataFinderClasses = new LinkedList<Class<? extends InvocationMetadataFinder>>();
        this.attributesHandlerClasses = new LinkedList<Class<? extends AttributesHandler>>();
        this.requestValueResolverClasses = new LinkedList<Class<? extends RequestValueResolver>>();
        this.exceptionMapperClasses = new LinkedList<Class<? extends ExceptionMapper>>();
        this.directionFormatterClasses = Maps.newConcurrentHashMap();
        this.ignoreClasses = new LinkedList<Class<? extends MultiModule>>();
        this.ignoreFilters = new LinkedList<MultiModule.Filter>();
        this.applicationProcessors = new LinkedList<ApplicationProcessor>();
        this.invocationInterceptors = new LinkedList<InvocationInterceptor>();
        this.invocationMetadataFactories = new LinkedList<InvocationMetadataFactory>();
        this.invocationMetadataFinders = new LinkedList<InvocationMetadataFinder>();
        this.attributesHandlers = new LinkedList<AttributesHandler>();
        this.requestValueResolvers = new LinkedList<RequestValueResolver>();
        this.exceptionMappers = new LinkedList<ExceptionMapper>();
        this.responseFormatters = Maps.newConcurrentHashMap();
    }

    @Override
    public Modules buildModules(final ApplicationContext resolver,
            final ContainerAdaptor defaultContainer) {
        final ContainerAdaptor moduleContainerAdaptor = createModuleContainerAdaptor(resolver,
                defaultContainer);
        if (moduleContainerAdaptor == null) {
            throw new MissingModulesProviderException();
        }
        return new Modules() {

            @Override
            public List<InvocationMetadataFactory> getInvocationMetadataFactories() {
                List<InvocationMetadataFactory> factories = getComponentInstances(moduleContainerAdaptor,
                        getInvocationMetadataFactoryClasses());
                        factories.addAll(invocationMetadataFactories);
                return factories;
            }

            private List<InvocationMetadataFinder> metadataFinders;

            @Override
            public List<InvocationMetadataFinder> getInvocationMetadataFinders() {
                if (metadataFinders == null) {
                    metadataFinders = getComponentInstances(moduleContainerAdaptor,
                            getInvocationMetadataFinderClasses());
                    metadataFinders.addAll(invocationMetadataFinders);
                }
                return this.metadataFinders;
            }

            private Invoker invoker;

            @Override
            public Invoker getInvoker() {
                if (invoker == null) {
                    InvokerFactory factory;
                    if(invokerFactory == null) {
                        factory = getComponentInstance(moduleContainerAdaptor,
                                getInvokerFactoryClass());
                    } else {
                        factory = invokerFactory;
                    }
                    invoker = factory.createInvoker(getInvocationInterceptors());
                }
                return invoker;
            }

            private ContainerAdaptor instanceProvider;

            @Override
            public ContainerAdaptor getInvocationInstanceProvider() {
                if (this.instanceProvider == null) {
                    ContainerAdaptorFactory<?> factory;
                    if(invocationInstanceProvider == null){
                        factory = moduleContainerAdaptor
                                .getInstanceOfType(getInvocationInstanceProviderClass());
                    } else {
                        factory = invocationInstanceProvider;
                    }
                    this.instanceProvider = factory.createContainerAdaptor(resolver);
                }
                return this.instanceProvider;
            }

            private List<ApplicationProcessor> processors;

            @Override
            public List<ApplicationProcessor> getApplicationProcessors() {
                if (this.processors == null) {
                        this.processors = getComponentInstances(moduleContainerAdaptor,
                                getApplicationProcessorClasses(),
                                new PrecedenceComparator<ApplicationProcessor>());
                        this.processors.addAll(applicationProcessors);
                }
                return this.processors;
            }

            private List<InvocationInterceptor> interceptors;

            @Override
            public List<InvocationInterceptor> getInvocationInterceptors() {
                if (this.interceptors == null) {
                        this.interceptors = getComponentInstances(moduleContainerAdaptor,
                                getInvocationInterceptorClasses(),
                                new PrecedenceComparator<InvocationInterceptor>());
                        this.interceptors.addAll(invocationInterceptors);
                }
                return this.interceptors;
            }

            @Override
            public InvocationFactory getInvocationFactory() {
                if(invocationFactory == null){
                    return getComponentInstance(moduleContainerAdaptor, getInvocationFactoryClass());
                } else {
                    return invocationFactory;
                }
            }

            @Override
            public RenderableResolver getResponseResolver() {
                if(renderableResolver == null){
                    return getComponentInstance(moduleContainerAdaptor, getResponseResolverClass());
                } else {
                    return renderableResolver;
                }
            }

            @Override
            public ResponseHandler getResponseHandler() {
                if(responseHandler == null){
                    return getComponentInstance(moduleContainerAdaptor, getResponseHandlerClass());
                } else {
                    return responseHandler;
                }
            }

            @Override
            public ExceptionHandler getExceptionHandler() {
                if(exceptionHandler == null){
                    return getComponentInstance(moduleContainerAdaptor, getExceptionHandlerClass());
                } else {
                    return exceptionHandler;
                }
            }

            @Override
            public TypeMapperContext getTypeMapperContext() {
                if(typeMapperContext == null){
                    return getComponentInstance(moduleContainerAdaptor, getTypeMapperContextClass());
                } else {
                    return typeMapperContext;
                }
            }

            @Override
            public ContainerAdaptor getModulesContainerAdaptor() {
                return moduleContainerAdaptor;
            }

            private ContainerAdaptor getOptionalContainerAdaptor() {
                return defaultContainer;
            }

            @Override
            public ResponseFormatter findResponseFormatter(Class<? extends Renderable> mapToResponse) {
                ResponseFormatter formatter = responseFormatters.get(mapToResponse);
                if(formatter != null){
                    return formatter;
                }
                Class<? extends ResponseFormatter> formatterClass = getResponseFormatterClass(mapToResponse);
                if (formatterClass != null) {
                    return getComponentInstance(moduleContainerAdaptor, formatterClass);
                }
                return null;
            }

            // prevent cyclic reference.
            private Set<String> alreadyInjected;

            private Set<String> getAlreadyInjectedTypeNames() {
                if (this.alreadyInjected == null) {
                    this.alreadyInjected = new HashSet<String>();
                }
                return this.alreadyInjected;
            }

            private <T> T getComponentInstance(ContainerAdaptor adaptor, Class<T> componentClass) {
                Assertion.notNull(componentClass, "component-class");
                T instance = adaptor.getInstanceOfType(componentClass);
                if (instance == null) {
                    instance = getOptionalContainerAdaptor().getInstanceOfType(componentClass);
                    if (instance == null) {
                        throw new MissingModuleException(componentClass);
                    }
                }
                if (instance instanceof ModulesContainerAdaptorAware) {
                    ((ModulesContainerAdaptorAware) instance)
                            .setModulesContainerAdaptor(getModulesContainerAdaptor());
                }
                Set<String> ai = getAlreadyInjectedTypeNames();
                if (ai.contains(componentClass.getCanonicalName()) == false
                        && instance instanceof ModulesAware) {
                    ai.add(componentClass.getCanonicalName());
                    ((ModulesAware) instance).setModules(this);
                }
                return instance;
            }

            private <T extends MultiModule> List<T> getComponentInstances(ContainerAdaptor adaptor,
                    List<Class<? extends T>> componentClasses, Comparator<T> comparator) {
                List<T> result = getComponentInstances(moduleContainerAdaptor, componentClasses);
                Collections.sort(result, comparator);
                return result;
            }

            private <T extends MultiModule> List<T> getComponentInstances(ContainerAdaptor adaptor,
                    List<Class<? extends T>> componentClasses) {
                List<T> instances = new ArrayList<T>();
                Set<String> instanceFQDNs = new HashSet<String>();
                for (Class<? extends T> clazz : componentClasses) {
                    for (List<? extends T> list : Arrays.asList(adaptor.getInstancesOfType(clazz),
                            getOptionalContainerAdaptor().getInstancesOfType(clazz))) {
                        for (T clazzInstance : list) {
                            // filter same FQDN's instance.
                            String FQDN = clazzInstance.getClass().getCanonicalName();
                            if (instanceFQDNs.contains(FQDN) == false) {
                                if (clazzInstance instanceof ModulesContainerAdaptorAware) {
                                    ((ModulesContainerAdaptorAware) clazzInstance)
                                            .setModulesContainerAdaptor(getModulesContainerAdaptor());
                                }
                                Set<String> ai = getAlreadyInjectedTypeNames();
                                if (ai.contains(clazz.getCanonicalName()) == false
                                        && clazzInstance instanceof ModulesAware) {
                                    ai.add(clazz.getCanonicalName());
                                    ((ModulesAware) clazzInstance).setModules(this);
                                }
                                instances.add(clazzInstance);
                            }
                            instanceFQDNs.add(FQDN);
                        }
                    }
                }
                Iterator<T> itr = instances.iterator();
                while (itr.hasNext()) {
                    T next = itr.next();
                    for (MultiModule.Filter moduleClass : getIgnoringFilters()) {
                        if (moduleClass.isAppreciable(next) == false) {
                            itr.remove();
                        }
                    }
                }
                return instances;
            }

            @Override
            public void dispose() {
                setResponseHandlerClass(null);
                setRenderableResolverClass(null);
                setExceptionHandlerClass(null);
                setInvocationFactoryClass(null);
                setInvocationInstanceProviderClass(null);
                setInvokerFactoryClass(null);
                setModulesProviderClass(null);
                setTypeMapperContextClass(null);
                if (this.processors != null) {
                    this.processors.clear();
                    this.processors = null;
                }
                if (this.interceptors != null) {
                    this.interceptors.clear();
                    this.interceptors = null;
                }
                if (this.alreadyInjected != null) {
                    this.alreadyInjected.clear();
                    this.alreadyInjected = null;
                }
            }

            private RequestValueResolvers resolvers;

            @Override
            public RequestValueResolvers getRequestValueResolvers() {
                if (this.resolvers == null) {
                    List<RequestValueResolver> resolverList = new LinkedList<RequestValueResolver>();
                    resolverList.addAll(requestValueResolvers);
                    resolverList.addAll(getComponentInstances(moduleContainerAdaptor,
                            getAttributesHandlerClasses()));
                    resolverList.addAll(getComponentInstances(moduleContainerAdaptor,
                            getRequestValueResolverClasses()));
                    this.resolvers = new DefaultReqestValueResolvers(resolverList);
                }
                return resolvers;
            }

            private List<ExceptionMapper> exMappers;

            @Override
            public List<ExceptionMapper> getExceptionMappers() {
                if (this.exMappers == null) {
                    this.exMappers = getComponentInstances(moduleContainerAdaptor,
                            getExceptionMapperClasses());
                    this.exMappers.addAll(exceptionMappers);
                }
                return this.exMappers;
            }
        };
    }

    protected ContainerAdaptor createModuleContainerAdaptor(final ApplicationContext resolver,
            final ContainerAdaptor defaultContainer) {
        if(modulesProvider != null){
            return modulesProvider.createContainerAdaptor(resolver);
        }
        Assertion.notNull(getModulesProviderClass(), "ModulesProviderClass");
        if (getModulesProviderClass().equals(StaticMappingContainerAdaptorFactory.class)) {
            return defaultContainer;
        } else {
            ContainerAdaptorFactory<? extends ContainerAdaptor> factory = ReflectionUtils
                    .getInstanceQuietly(getModulesProviderClass());
            return factory.createContainerAdaptor(resolver);
        }
    }

    @Override
    public ModulesBuilder setModulesProviderClass(
            Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> modulesProviderClass) {
        this.modulesProviderClass = modulesProviderClass;
        return this;
    }

    //TODO:implement.
    @Override
    public ModulesBuilder setModulesProvider(ContainerAdaptorFactory<? extends ContainerAdaptor> modulesProvider) {
        this.modulesProvider = modulesProvider;
        return this;
    }

    @Override
    public ModulesBuilder addInvocationMetadataFactoriesClass(
            Class<? extends InvocationMetadataFactory> actionMethodMetadataFactoryClass) {
        this.invocationMetadataFactoryClasses.add(actionMethodMetadataFactoryClass);
        return this;
    }

    @Override
    public ModulesBuilder addInvocationMetadataFactories(InvocationMetadataFactory... invocationMetadataFactories) {
        this.invocationMetadataFactories.addAll(Arrays.asList(invocationMetadataFactories));
        return this;
    }

    @Override
    public ModulesBuilder addInvocationMetadataFinderClass(
            Class<? extends InvocationMetadataFinder> actionMethodMetadataFinderClass) {
        this.invocationMetadataFinderClasses.add(actionMethodMetadataFinderClass);
        return this;
    }

    @Override
    public ModulesBuilder addInvocationMetadataFinder(InvocationMetadataFinder... invocationMetadataFinder) {
        this.invocationMetadataFinders.addAll(Arrays.asList(invocationMetadataFinder));
        return this;
    }

    @Override
    public ModulesBuilder setInvokerFactoryClass(Class<? extends InvokerFactory> invokerFactoryClass) {
        this.invokerFactoryClass = invokerFactoryClass;
        return this;
    }

    @Override
    public ModulesBuilder setInvokerFactory(InvokerFactory invokerFactory) {
        this.invokerFactory = invokerFactory;
        return this;
    }

    @Override
    public ModulesBuilder setInvocationInstanceProviderClass(
            Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> actionInstanceProviderClass) {
        this.invocationInstanceProviderClass = actionInstanceProviderClass;
        return this;
    }

    @Override
    public ModulesBuilder setInvocationInstanceProvider(ContainerAdaptorFactory<? extends ContainerAdaptor> invocationInstanceProvider) {
        this.invocationInstanceProvider = invocationInstanceProvider;
        return this;
    }

    @Override
    public ModulesBuilder setInvocationFactoryClass(
            Class<? extends InvocationFactory> actionInvocationFactoryClass) {
        this.invocationFactoryClass = actionInvocationFactoryClass;
        return this;
    }

    @Override
    public ModulesBuilder setInvocationFactory(InvocationFactory invocationFactory){
        this.invocationFactory = invocationFactory;
        return this;
    }

    @Override
    public ModulesBuilder setRenderableResolverClass(
            Class<? extends RenderableResolver> actionResultResolverClass) {
        this.directionResolverClass = actionResultResolverClass;
        return this;
    }

    @Override
    public ModulesBuilder setRenderableResolver(RenderableResolver responseResolver) {
        this.renderableResolver = renderableResolver;
        return this;
    }

    @Override
    public ModulesBuilder setResponseHandlerClass(
            Class<? extends ResponseHandler> actionResultHandlerClass) {
        this.directionHandlerClass = actionResultHandlerClass;
        return this;
    }

    @Override
    public ModulesBuilder setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    @Override
    public ModulesBuilder setTypeMapperContextClass(
            Class<? extends TypeMapperContext> typeMapperContextClass) {
        this.typeMapperContextClass = typeMapperContextClass;
        return this;
    }

    @Override
    public ModulesBuilder setTypeMapperContext(TypeMapperContext typeMapperContext) {
        this.typeMapperContext = typeMapperContext;
        return this;
    }

    @Override
    public ModulesBuilder setExceptionHandlerClass(
            Class<? extends ExceptionHandler> exceptionHandlerClass) {
        this.exceptionHandlerClass = exceptionHandlerClass;
        return this;
    }

    @Override
    public ModulesBuilder setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    @Override
    public ModulesBuilder addApplicationProcessorClass(
            Class<? extends ApplicationProcessor> applicationProcessorClass) {
        this.applicationProcessorClasses.add(applicationProcessorClass);
        return this;
    }

    @Override
    public ModulesBuilder addApplicationProcessor(ApplicationProcessor... applicationProcessors) {
        this.applicationProcessors.addAll(Arrays.asList(applicationProcessors));
        return this;
    }

    @Override
    public ModulesBuilder addInvocationInterceptorClass(
            Class<? extends InvocationInterceptor> invocationInterceptorClass) {
        this.invocationInterceptorClasses.add(invocationInterceptorClass);
        return this;
    }

    @Override
    public ModulesBuilder addInvocationInterceptor(InvocationInterceptor... invocationInterceptors) {
        this.invocationInterceptors.addAll(Arrays.asList(invocationInterceptors));
        return this;
    }

    @Override
    public ModulesBuilder clearInvocationInterceptorClass() {
        this.invocationInterceptorClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearInvocationInterceptors() {
        this.invocationInterceptors.clear();
        return this;
    }

    @Override
    public ModulesBuilder addAttributesHandlerClass(
            Class<? extends AttributesHandler> requestAttributesResolverClass) {
        this.attributesHandlerClasses.add(requestAttributesResolverClass);
        return this;
    }

    @Override
    public ModulesBuilder addAttributesHandler(AttributesHandler... attributesHandler) {
        this.attributesHandlers.addAll(Arrays.asList(attributesHandler));
        return this;
    }

    protected Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> getModulesProviderClass() {
        return this.modulesProviderClass;
    }

    protected List<Class<? extends InvocationMetadataFactory>> getInvocationMetadataFactoryClasses() {
        return this.invocationMetadataFactoryClasses;
    }

    protected List<Class<? extends InvocationMetadataFinder>> getInvocationMetadataFinderClasses() {
        return this.invocationMetadataFinderClasses;
    }

    protected Class<? extends InvokerFactory> getInvokerFactoryClass() {
        return this.invokerFactoryClass;
    }

    protected Class<? extends ContainerAdaptorFactory<?>> getInvocationInstanceProviderClass() {
        return this.invocationInstanceProviderClass;
    }

    protected Class<? extends InvocationFactory> getInvocationFactoryClass() {
        return this.invocationFactoryClass;
    }

    protected Class<? extends RenderableResolver> getResponseResolverClass() {
        return this.directionResolverClass;
    }

    protected Class<? extends ResponseHandler> getResponseHandlerClass() {
        return this.directionHandlerClass;
    }

    protected Class<? extends ExceptionHandler> getExceptionHandlerClass() {
        return this.exceptionHandlerClass;
    }

    protected Class<? extends TypeMapperContext> getTypeMapperContextClass() {
        return this.typeMapperContextClass;
    }

    protected List<Class<? extends InvocationInterceptor>> getInvocationInterceptorClasses() {
        return this.invocationInterceptorClasses;
    }

    protected List<Class<? extends ApplicationProcessor>> getApplicationProcessorClasses() {
        return this.applicationProcessorClasses;
    }

    protected List<Class<? extends AttributesHandler>> getAttributesHandlerClasses() {
        return attributesHandlerClasses;
    }

    protected List<Class<? extends RequestValueResolver>> getRequestValueResolverClasses() {
        return this.requestValueResolverClasses;
    }

    protected Class<? extends ResponseFormatter> getResponseFormatterClass(
            Class<? extends Renderable> mapToResponse) {
        return this.directionFormatterClasses.get(mapToResponse);
    }

    protected List<Class<? extends ExceptionMapper>> getExceptionMapperClasses() {
        return this.exceptionMapperClasses;
    }

    protected List<Class<? extends MultiModule>> getIgnoringClasses() {
        return this.ignoreClasses;
    }

    protected List<MultiModule.Filter> getIgnoringFilters() {
        return this.ignoreFilters;
    }

    @Override
    public ModulesBuilder clearInvocationMetadataFactoriesClass() {
        this.invocationMetadataFactoryClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearInvocationMetadataFactories() {
        this.invocationMetadataFactories.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearInvocationMetadataFinderClass() {
        this.invocationMetadataFinderClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearInvocationMetadataFinder() {
        this.invocationMetadataFinders.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearApplicationProcessorClass() {
        this.applicationProcessorClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearApplicationProcessors() {
        this.applicationProcessors.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearAttributesHanderClass() {
        this.attributesHandlerClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearAttributesHanders() {
        this.attributesHandlers.clear();
        return this;
    }

    @Override
    public ModulesBuilder addRequestValueResolverClass(
            Class<? extends RequestValueResolver> requestValueResolverClass) {
        this.requestValueResolverClasses.add(requestValueResolverClass);
        return this;
    }

    @Override
    public ModulesBuilder addRequestValueResolver(RequestValueResolver... requestValueResolver) {
        this.requestValueResolvers.addAll(Arrays.asList(requestValueResolver));
        return this;
    }

    @Override
    public ModulesBuilder clearRequestValueResolverClass() {
        this.requestValueResolverClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearRequestValueResolvers() {
        this.requestValueResolvers.clear();
        return this;
    }

    @Override
    public ModulesBuilder addResponseFormatterClass(Class<? extends Renderable> mapToResponseClass,
            Class<? extends ResponseFormatter> directionFormatterClass) {
        this.directionFormatterClasses.put(mapToResponseClass, directionFormatterClass);
        return this;
    }

    @Override
    public ModulesBuilder addResponseFormatters(Class<? extends Renderable> mapToResponseClass, ResponseFormatter... responseFormatters) {
        for(ResponseFormatter responseFormatter:responseFormatters) {
            this.responseFormatters.put(mapToResponseClass,responseFormatter);
        }
        return this;
    }

    @Override
    public ModulesBuilder addExceptionMapperClass(
            Class<? extends ExceptionMapper> exceptionMapperClass) {
        this.exceptionMapperClasses.add(exceptionMapperClass);
        return this;
    }

    @Override
    public ModulesBuilder addExceptionMapper(ExceptionMapper... exceptionMappers) {
        this.exceptionMappers.addAll(Arrays.asList(exceptionMappers));
        return this;
    }

    @Override
    public ModulesBuilder clearDirectionFormatterClass() {
        this.directionFormatterClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder ignore(final Class<? extends MultiModule> multiModuleClass) {
        Assertion.notNull(multiModuleClass, MultiModule.class.getCanonicalName());
        return filter(new MultiModule.Filter() {

            @Override
            public <T extends MultiModule> boolean isAppreciable(T aMultiModule) {
                if (multiModuleClass.isInstance(aMultiModule)) {
                    return false;
                }
                return true;
            }
        });
    }

    @Override
    public ModulesBuilder filter(MultiModule.Filter multiModuleFilter) {
        Assertion.notNull(multiModuleFilter, MultiModule.Filter.class.getCanonicalName());
        this.ignoreFilters.add(multiModuleFilter);
        return this;
    }
}
