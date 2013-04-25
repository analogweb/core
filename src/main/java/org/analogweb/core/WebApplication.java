package org.analogweb.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.analogweb.Application;
import org.analogweb.ApplicationContextResolver;
import org.analogweb.ApplicationProcessor;
import org.analogweb.ApplicationProperties;
import org.analogweb.ContainerAdaptor;
import org.analogweb.ExceptionHandler;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.Module;
import org.analogweb.Modules;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMapping;
import org.analogweb.RequestValueResolvers;
import org.analogweb.Renderable;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseHandler;
import org.analogweb.ResponseResolver;
import org.analogweb.TypeMapperContext;
import org.analogweb.WebApplicationException;
import org.analogweb.util.ApplicationPropertiesHolder;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.CollectionUtils;
import org.analogweb.util.ReflectionUtils;
import org.analogweb.util.ResourceUtils;
import org.analogweb.util.StopWatch;
import org.analogweb.util.StringUtils;
import org.analogweb.util.SystemProperties;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author snowgoose
 */
public class WebApplication implements Application {

    private static final Log log = Logs.getLog(WebApplication.class);
    private Modules modules;
    private RequestPathMapping requestPathMapping;
    private String applicationSpecifier;
    private ClassLoader classLoader;
    private ApplicationContextResolver resolver;

    @Override
    public void run(ApplicationContextResolver resolver, ApplicationProperties props,
            Collection<ClassCollector> collectors, ClassLoader classLoader) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.resolver = resolver;
        this.classLoader = classLoader;
        log.log(Markers.BOOT_APPLICATION, "IB000001");
        Collection<String> invocationPackageNames = props.getComponentPackageNames();
        log.log(Markers.BOOT_APPLICATION, "DB000001", invocationPackageNames);
        Set<String> modulesPackageNames = new HashSet<String>();
        if (CollectionUtils.isNotEmpty(invocationPackageNames)) {
            modulesPackageNames.addAll(invocationPackageNames);
        }
        modulesPackageNames.add(DEFAULT_PACKAGE_NAME);
        log.log(Markers.BOOT_APPLICATION, "DB000002", modulesPackageNames);
        initApplication(collectors, modulesPackageNames, invocationPackageNames,
                props.getApplicationSpecifier());
        log.log(Markers.BOOT_APPLICATION, "IB000002", sw.stop());
    }

    @Override
    public int processRequest(RequestPath requestedPath, RequestContext context,
            ResponseContext responseContext) throws IOException, WebApplicationException {
        InvocationMetadata metadata = null;
        Modules mod = null;
        List<ApplicationProcessor> processors = null;
        try {
            RequestPathMapping mapping = getRequestPathMapping();
            log.log(Markers.LIFECYCLE, "DL000004", requestedPath);
            metadata = mapping.findInvocationMetadata(requestedPath);
            if (metadata == null) {
                log.log(Markers.LIFECYCLE, "DL000005", requestedPath);
                return NOT_FOUND;
            }
            log.log(Markers.LIFECYCLE, "DL000006", requestedPath, metadata);
            mod = getModules();
            ContainerAdaptor invocationInstances = mod.getInvocationInstanceProvider();
            RequestValueResolvers resolvers = mod.getRequestValueResolvers();
            TypeMapperContext typeMapperContext = mod.getTypeMapperContext();
            Invocation invocation = mod.getInvocationFactory().createInvocation(
                    invocationInstances, metadata, context, responseContext, typeMapperContext,
                    resolvers);
            InvocationArguments arguments = invocation.getInvocationArguments();
            processors = mod.getApplicationProcessors();
            prepareInvoke(processors, arguments, metadata, context, resolvers, typeMapperContext);
            try {
                Object invocationResult = mod.getInvoker().invoke(invocation, metadata, context,
                        responseContext);
                log.log(Markers.LIFECYCLE, "DL000007", invocation.getInvocationInstance(),
                        invocationResult);
                postInvoke(processors, invocationResult, arguments, metadata, context, resolvers);
                handleResponse(mod, invocationResult, metadata, context, responseContext);
            } catch (Exception e) {
                log.log(Markers.LIFECYCLE, "DL000012", invocation.getInvocationInstance(), e);
                onException(processors, e, arguments, metadata, context);
                List<Object> args = arguments.asList();
                throw new InvocationFailureException(e, metadata, args.toArray(new Object[args
                        .size()]));
            }
            afterCompletion(processors, context, responseContext, null);
        } catch (InvokeInterruptedException e) {
            handleResponse(mod, e.getInterruption(), metadata, context, responseContext);
            afterCompletion(processors, context, responseContext, e);
        } catch (Exception e) {
            ExceptionHandler handler = mod.getExceptionHandler();
            log.log(Markers.LIFECYCLE, "DL000009", (Object) e, handler);
            Object exceptionResult = handler.handleException(e);
            if (exceptionResult != null) {
                handleResponse(mod, exceptionResult, metadata, context, responseContext);
                afterCompletion(processors, context, responseContext, e);
            } else {
                afterCompletion(processors, context, responseContext, e);
                throw new WebApplicationException(e);
            }
        }
        return PROCEEDED;
    }

    protected Object prepareInvoke(List<ApplicationProcessor> processors, InvocationArguments args,
            InvocationMetadata metadata, RequestContext request,
            RequestValueResolvers attributesHandlers, TypeMapperContext typeMapperContext) {
        log.log(Markers.LIFECYCLE, "DL000013");
        Object interruption = ApplicationProcessor.NO_INTERRUPTION;
        Method method = ReflectionUtils.getInvocationMethod(metadata);
        for (ApplicationProcessor processor : processors) {
            interruption = processor.prepareInvoke(method, args, metadata, request,
                    typeMapperContext, attributesHandlers);
            if (interruption != ApplicationProcessor.NO_INTERRUPTION) {
                throw new InvokeInterruptedException(interruption);
            }
        }
        return interruption;
    }

    protected void postInvoke(List<ApplicationProcessor> processors, Object invocationResult,
            InvocationArguments args, InvocationMetadata metadata, RequestContext request,
            RequestValueResolvers attributesHandlers) {
        log.log(Markers.LIFECYCLE, "DL000014");
        for (ApplicationProcessor processor : processors) {
            processor.postInvoke(invocationResult, args, metadata, request, attributesHandlers);
        }
    }

    protected Object onException(List<ApplicationProcessor> processors, Exception thrown,
            InvocationArguments args, InvocationMetadata metadata, RequestContext request) {
        log.log(Markers.LIFECYCLE, "DL000015");
        Object interruption = ApplicationProcessor.NO_INTERRUPTION;
        for (ApplicationProcessor processor : processors) {
            interruption = processor.processException(thrown, request, args, metadata);
            if (interruption != ApplicationProcessor.NO_INTERRUPTION) {
                throw new InvokeInterruptedException(interruption);
            }
        }
        return interruption;
    }

    protected void afterCompletion(List<ApplicationProcessor> processors, RequestContext context,
            ResponseContext responseContext, Exception e) {
        log.log(Markers.LIFECYCLE, "DL000016");
        for (ApplicationProcessor processor : processors) {
            processor.afterCompletion(context, responseContext, e);
        }
    }

    protected void handleResponse(Modules modules, Object result, InvocationMetadata metadata,
            RequestContext context, ResponseContext responseContext) throws IOException,
            WebApplicationException {
        ResponseResolver resultResolver = modules.getResponseResolver();
        Renderable resolved = resultResolver.resolve(result, metadata, context, responseContext);
        log.log(Markers.LIFECYCLE, "DL000008", result, result);
        ResponseFormatter resultFormatter = modules.findResponseFormatter(resolved.getClass());
        if (resultFormatter != null) {
            log.log(Markers.LIFECYCLE, "DL000010", result, resultFormatter);
        } else {
            log.log(Markers.LIFECYCLE, "DL000011", result);
        }
        ResponseHandler resultHandler = modules.getResponseHandler();
        resultHandler.handleResult(resolved, resultFormatter, context, responseContext);
    }

    protected void initApplication(Collection<ClassCollector> collectors,
            Set<String> modulePackageNames, Collection<String> invocationPackageNames,
            String specifier) {
        Collection<Class<?>> moduleClasses = collectClasses(modulePackageNames, collectors);
        ModulesBuilder modulesBuilder = processConfigPreparation(ReflectionUtils
                .filterClassAsImplementsInterface(ModulesConfig.class, moduleClasses));
        ApplicationContextResolver resolver = getApplicationContextResolver();
        ContainerAdaptor defaultContainer = setUpDefaultContainer(resolver, moduleClasses);
        Modules modules = modulesBuilder.buildModules(resolver, defaultContainer);
        setModules(modules);
        log.log(Markers.BOOT_APPLICATION, "DB000003", modules);
        Collection<Class<?>> collectedInvocationClasses;
        if (CollectionUtils.isEmpty(invocationPackageNames)) {
            collectedInvocationClasses = collectAllClasses(collectors);
        } else {
            collectedInvocationClasses = collectClasses(invocationPackageNames, collectors);
        }
        setRequestPathMapping(createRequestPathMapping(collectedInvocationClasses,
                modules.getInvocationMetadataFactories()));
        setApplicationSpecifier(specifier);
    }

    protected ModulesBuilder processConfigPreparation(List<Class<ModulesConfig>> configs) {
        ModulesBuilder modulesBuilder = new DefaultModulesBuilder();
        List<ModulesConfig> moduleConfigInstances = new ArrayList<ModulesConfig>();
        for (Class<ModulesConfig> configClass : configs) {
            ModulesConfig config = ReflectionUtils.getInstanceQuietly(configClass);
            if (config != null) {
                moduleConfigInstances.add(config);
            }
        }
        Collections.sort(moduleConfigInstances, getModulesConfigComparator());
        for (ModulesConfig config : moduleConfigInstances) {
            log.log(Markers.BOOT_APPLICATION, "IB000003", config);
            modulesBuilder = config.prepare(modulesBuilder);
        }
        return modulesBuilder;
    }

    protected Comparator<ModulesConfig> getModulesConfigComparator() {
        return new ModulesConfigComparator();
    }

    protected ContainerAdaptor setUpDefaultContainer(ApplicationContextResolver resolver,
            Collection<Class<?>> rootModuleClasses) {
        StaticMappingContainerAdaptorFactory factory = new StaticMappingContainerAdaptorFactory();
        StaticMappingContainerAdaptor adaptor = factory.createContainerAdaptor(resolver);
        for (Class<?> moduleClass : rootModuleClasses) {
            if (Module.class.isAssignableFrom(moduleClass)) {
                adaptor.register(moduleClass);
            }
        }
        return adaptor;
    }

    protected RequestPathMapping createRequestPathMapping(Collection<Class<?>> collectedClasses,
            List<InvocationMetadataFactory> factories) {
        RequestPathMapping mapping = new DefaultRequestPathMapping();
        for (Class<?> clazz : collectedClasses) {
            for (InvocationMetadataFactory factory : factories) {
                if (factory.containsInvocationClass(clazz)) {
                    Method[] methods = ReflectionUtils.getMethods(clazz);
                    for (Method method : methods) {
                        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(
                                clazz, method);
                        if (actionMethodMetadata != null) {
                            log.log(Markers.BOOT_APPLICATION, "IB000004",
                                    actionMethodMetadata.getDefinedPath(),
                                    actionMethodMetadata.getInvocationClass(),
                                    actionMethodMetadata.getMethodName());
                            mapping.mapInvocationMetadata(actionMethodMetadata.getDefinedPath(),
                                    actionMethodMetadata);
                        }
                    }
                }
            }
        }
        return mapping;
    }

    protected Collection<Class<?>> collectAllClasses(Collection<ClassCollector> collectors) {
        Collection<Class<?>> collectedClasses = new HashSet<Class<?>>();
        for (String resourceName : SystemProperties.classPathes()) {
            URL resourceURL = ResourceUtils.findResource(resourceName);
            for (ClassCollector collector : collectors) {
                collectedClasses.addAll(collector.collect(StringUtils.EMPTY, resourceURL,
                        classLoader));
            }
        }
        return collectedClasses;
    }

    protected Collection<Class<?>> collectClasses(Collection<String> rootPackageNames,
            Collection<ClassCollector> collectors) {
        Collection<Class<?>> collectedClasses = new HashSet<Class<?>>();
        for (String packageName : rootPackageNames) {
            for (URL resourceURL : ResourceUtils.findPackageResources(packageName, classLoader)) {
                for (ClassCollector collector : collectors) {
                    collectedClasses.addAll(collector
                            .collect(packageName, resourceURL, classLoader));
                }
            }
        }
        return collectedClasses;
    }

    @Override
    public RequestPathMapping getRequestPathMapping() {
        return requestPathMapping;
    }

    protected void setRequestPathMapping(RequestPathMapping requestPathMapping) {
        this.requestPathMapping = requestPathMapping;
    }

    @Override
    public Modules getModules() {
        return this.modules;
    }

    protected void setModules(Modules modules) {
        this.modules = modules;
    }

    @Override
    public String getApplicationSpecifier() {
        return applicationSpecifier;
    }

    protected void setApplicationSpecifier(String suffix) {
        this.applicationSpecifier = suffix;
    }

    protected final ApplicationContextResolver getApplicationContextResolver() {
        return this.resolver;
    }

    @Override
    public void dispose() {
        this.classLoader = null;
        this.resolver = null;
        this.applicationSpecifier = null;
        if (this.modules != null) {
            this.modules.dispose();
            this.modules = null;
        }
        if (this.requestPathMapping != null) {
            this.requestPathMapping.dispose();
            this.requestPathMapping = null;
        }
        ApplicationPropertiesHolder.dispose(this);
    }
}
