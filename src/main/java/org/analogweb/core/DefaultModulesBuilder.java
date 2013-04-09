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

import org.analogweb.ApplicationContextResolver;
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
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesContainerAdaptorAware;
import org.analogweb.MultiModule;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.Response;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseHandler;
import org.analogweb.ResponseResolver;
import org.analogweb.TypeMapperContext;
import org.analogweb.util.Assertion;
import org.analogweb.util.Maps;
import org.analogweb.util.ReflectionUtils;

/**
 * @author snowgoose
 */
public class DefaultModulesBuilder implements ModulesBuilder {

    private Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> modulesProviderClass;
    private Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> invocationInstanceProviderClass;
    private Class<? extends InvokerFactory> invokerFactoryClass;
    private Class<? extends InvocationFactory> invocationFactoryClass;
    private Class<? extends ResponseResolver> directionResolverClass;
    private Class<? extends ResponseHandler> directionHandlerClass;
    private Class<? extends ExceptionHandler> exceptionHandlerClass;
    private Class<? extends TypeMapperContext> typeMapperContextClass;
    private final List<Class<? extends ApplicationProcessor>> applicationProcessorClasses;
    private final List<Class<? extends InvocationInterceptor>> invocationInterceptorClasses;
    private final List<Class<? extends InvocationMetadataFactory>> invocationMetadataFactoryClasses;
    private final List<Class<? extends AttributesHandler>> attributesHandlerClasses;
    private final List<Class<? extends RequestValueResolver>> requestValueResolverClasses;
    private final Map<Class<? extends Response>, Class<? extends ResponseFormatter>> directionFormatterClasses;
    private final List<Class<? extends MultiModule>> ignoreClasses;
    private final List<MultiModule.Filter> ignoreFilters;

    public DefaultModulesBuilder() {
        this.applicationProcessorClasses = new LinkedList<Class<? extends ApplicationProcessor>>();
        this.invocationInterceptorClasses = new LinkedList<Class<? extends InvocationInterceptor>>();
        this.invocationMetadataFactoryClasses = new LinkedList<Class<? extends InvocationMetadataFactory>>();
        this.attributesHandlerClasses = new LinkedList<Class<? extends AttributesHandler>>();
        this.requestValueResolverClasses = new LinkedList<Class<? extends RequestValueResolver>>();
        this.directionFormatterClasses = Maps.newConcurrentHashMap();
        this.ignoreClasses = new LinkedList<Class<? extends MultiModule>>();
        this.ignoreFilters = new LinkedList<MultiModule.Filter>();
    }

    @Override
    public Modules buildModules(final ApplicationContextResolver resolver,
            final ContainerAdaptor defaultContainer) {
        Assertion.notNull(getModulesProviderClass(), "ModulesProviderClass");
        final ContainerAdaptor moduleContainerAdaptor = createModuleContainerAdaptor(resolver,
                defaultContainer);
        if (moduleContainerAdaptor == null) {
            throw new MissingModulesProviderException();
        }
        return new Modules() {

            @Override
            public List<InvocationMetadataFactory> getInvocationMetadataFactories() {
                return getComponentInstances(moduleContainerAdaptor,
                        getInvocationMetadataFactoryClasses());
            }

            private Invoker invoker;

            @Override
            public Invoker getInvoker() {
                if (invoker == null) {
                    InvokerFactory factory = getComponentInstance(moduleContainerAdaptor,
                            getInvokerFactoryClass());
                    invoker = factory.createInvoker(getInvocationInterceptors());
                }
                return invoker;
            }

            private ContainerAdaptor invocationInstanceProvider;

            @Override
            public ContainerAdaptor getInvocationInstanceProvider() {
                if (this.invocationInstanceProvider == null) {
                    ContainerAdaptorFactory<?> factory = moduleContainerAdaptor
                            .getInstanceOfType(getInvocationInstanceProviderClass());
                    this.invocationInstanceProvider = factory.createContainerAdaptor(resolver);
                }
                return this.invocationInstanceProvider;
            }

            private List<ApplicationProcessor> applicationProcessors;

            @Override
            public List<ApplicationProcessor> getApplicationProcessors() {
                if (this.applicationProcessors == null) {
                    this.applicationProcessors = getComponentInstances(moduleContainerAdaptor,
                            getApplicationProcessorClasses(),
                            new PrecedenceComparator<ApplicationProcessor>());
                }
                return this.applicationProcessors;
            }

            private List<InvocationInterceptor> invocationInterceptors;

            @Override
            public List<InvocationInterceptor> getInvocationInterceptors() {
                if (this.invocationInterceptors == null) {
                    this.invocationInterceptors = getComponentInstances(moduleContainerAdaptor,
                            getInvocationInterceptorClasses(),
                            new PrecedenceComparator<InvocationInterceptor>());
                }
                return this.invocationInterceptors;
            }

            @Override
            public InvocationFactory getInvocationFactory() {
                return getComponentInstance(moduleContainerAdaptor, getInvocationFactoryClass());
            }

            @Override
            public ResponseResolver getResponseResolver() {
                return getComponentInstance(moduleContainerAdaptor, getResponseResolverClass());
            }

            @Override
            public ResponseHandler getResponseHandler() {
                return getComponentInstance(moduleContainerAdaptor, getResponseHandlerClass());
            }

            @Override
            public ExceptionHandler getExceptionHandler() {
                return getComponentInstance(moduleContainerAdaptor, getExceptionHandlerClass());
            }

            @Override
            public TypeMapperContext getTypeMapperContext() {
                return getComponentInstance(moduleContainerAdaptor, getTypeMapperContextClass());
            }

            @Override
            public ContainerAdaptor getModulesContainerAdaptor() {
                return moduleContainerAdaptor;
            }

            private ContainerAdaptor getOptionalContainerAdaptor() {
                return defaultContainer;
            }

            @Override
            public ResponseFormatter findResponseFormatter(Class<? extends Response> mapToResponse) {
                Class<? extends ResponseFormatter> formatterClass = getResponseFormatterClass(mapToResponse);
                if (formatterClass != null) {
                    return getComponentInstance(moduleContainerAdaptor, formatterClass);
                }
                return null;
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
                return instance;
            }

            private <T extends MultiModule> List<T> getComponentInstances(ContainerAdaptor adaptor,
                    List<Class<? extends T>> componentClasses, Comparator<T> comparator) {
                List<T> result = getComponentInstances(moduleContainerAdaptor, componentClasses);
                Collections.sort(result, comparator);
                return result;
            }

            @SuppressWarnings("unchecked")
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
                setResponseResolverClass(null);
                setExceptionHandlerClass(null);
                setInvocationFactoryClass(null);
                setInvocationInstanceProviderClass(null);
                setInvokerFactoryClass(null);
                setModulesProviderClass(null);
                setTypeMapperContextClass(null);
                if (this.applicationProcessors != null) {
                    this.applicationProcessors.clear();
                    this.applicationProcessors = null;
                }
                if (this.invocationInterceptors != null) {
                    this.invocationInterceptors.clear();
                    this.invocationInterceptors = null;
                }
            }

            private RequestValueResolvers resolvers;

            @Override
            public RequestValueResolvers getRequestValueResolvers() {
                if (this.resolvers == null) {
                    List<RequestValueResolver> resolverList = new LinkedList<RequestValueResolver>();
                    resolverList.addAll(getComponentInstances(moduleContainerAdaptor,
                            getAttributesHandlerClasses()));
                    resolverList.addAll(getComponentInstances(moduleContainerAdaptor,
                            getRequestValueResolverClasses()));
                    this.resolvers = new DefaultReqestValueResolvers(resolverList);
                }
                return resolvers;
            }
        };
    }

    protected ContainerAdaptor createModuleContainerAdaptor(
            final ApplicationContextResolver resolver, final ContainerAdaptor defaultContainer) {
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

    @Override
    public ModulesBuilder addInvocationMetadataFactoriesClass(
            Class<? extends InvocationMetadataFactory> actionMethodMetadataFactoryClass) {
        this.invocationMetadataFactoryClasses.add(actionMethodMetadataFactoryClass);
        return this;
    }

    @Override
    public ModulesBuilder setInvokerFactoryClass(Class<? extends InvokerFactory> invokerFactoryClass) {
        this.invokerFactoryClass = invokerFactoryClass;
        return this;
    }

    @Override
    public ModulesBuilder setInvocationInstanceProviderClass(
            Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> actionInstanceProviderClass) {
        this.invocationInstanceProviderClass = actionInstanceProviderClass;
        return this;
    }

    @Override
    public ModulesBuilder setInvocationFactoryClass(
            Class<? extends InvocationFactory> actionInvocationFactoryClass) {
        this.invocationFactoryClass = actionInvocationFactoryClass;
        return this;
    }

    @Override
    public ModulesBuilder setResponseResolverClass(
            Class<? extends ResponseResolver> actionResultResolverClass) {
        this.directionResolverClass = actionResultResolverClass;
        return this;
    }

    @Override
    public ModulesBuilder setResponseHandlerClass(
            Class<? extends ResponseHandler> actionResultHandlerClass) {
        this.directionHandlerClass = actionResultHandlerClass;
        return this;
    }

    @Override
    public ModulesBuilder setTypeMapperContextClass(
            Class<? extends TypeMapperContext> typeMapperContextClass) {
        this.typeMapperContextClass = typeMapperContextClass;
        return this;
    }

    @Override
    public ModulesBuilder setExceptionHandlerClass(
            Class<? extends ExceptionHandler> exceptionHandlerClass) {
        this.exceptionHandlerClass = exceptionHandlerClass;
        return this;
    }

    @Override
    public ModulesBuilder addApplicationProcessorClass(
            Class<? extends ApplicationProcessor> applicationProcessorClass) {
        this.applicationProcessorClasses.add(applicationProcessorClass);
        return this;
    }

    @Override
    public ModulesBuilder addInvocationInterceptorClass(
            Class<? extends InvocationInterceptor> invocationInterceptorClass) {
        this.invocationInterceptorClasses.add(invocationInterceptorClass);
        return this;
    }

    @Override
    public ModulesBuilder addAttributesHandlerClass(
            Class<? extends AttributesHandler> requestAttributesResolverClass) {
        this.attributesHandlerClasses.add(requestAttributesResolverClass);
        return this;
    }

    protected Class<? extends ContainerAdaptorFactory<? extends ContainerAdaptor>> getModulesProviderClass() {
        return this.modulesProviderClass;
    }

    protected List<Class<? extends InvocationMetadataFactory>> getInvocationMetadataFactoryClasses() {
        return this.invocationMetadataFactoryClasses;
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

    protected Class<? extends ResponseResolver> getResponseResolverClass() {
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
            Class<? extends Response> mapToResponse) {
        return this.directionFormatterClasses.get(mapToResponse);
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
    public ModulesBuilder clearApplicationProcessorClass() {
        this.applicationProcessorClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder clearAttributesHanderClass() {
        this.attributesHandlerClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder addRequestValueResolverClass(
            Class<? extends RequestValueResolver> requestValueResolverClass) {
        this.requestValueResolverClasses.add(requestValueResolverClass);
        return this;
    }

    @Override
    public ModulesBuilder clearRequestValueResolverClass() {
        this.requestValueResolverClasses.clear();
        return this;
    }

    @Override
    public ModulesBuilder addResponseFormatterClass(Class<? extends Response> mapToResponseClass,
            Class<? extends ResponseFormatter> directionFormatterClass) {
        this.directionFormatterClasses.put(mapToResponseClass, directionFormatterClass);
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
