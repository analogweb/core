package org.analogweb.core;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.analogweb.Application;
import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProcessor;
import org.analogweb.ApplicationProperties;
import org.analogweb.ContainerAdaptor;
import org.analogweb.ExceptionHandler;
import org.analogweb.ExceptionMapper;
import org.analogweb.Invocation;
import org.analogweb.InvocationArguments;
import org.analogweb.InvocationFactory;
import org.analogweb.InvocationInterceptor;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.InvocationMetadataFinder;
import org.analogweb.Invoker;
import org.analogweb.Module;
import org.analogweb.Modules;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;
import org.analogweb.MutableRequestContext;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.Response;
import org.analogweb.ResponseContext;
import org.analogweb.ResponseHandler;
import org.analogweb.RenderableResolver;
import org.analogweb.RouteRegistry;
import org.analogweb.TypeMapperContext;
import org.analogweb.WebApplicationException;
import org.analogweb.util.*;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.analogweb.util.logging.Markers;

/**
 * @author y2k2mt
 */
public class WebApplication implements Application {

    private static final Log log = Logs.getLog(WebApplication.class);
    private List<ModulesConfig> modulesConfigs;
    private Modules modules;
    private RouteRegistry routes;
    private ClassLoader classLoader;
    private ApplicationContext resolver;

    public WebApplication() {
        this(Collections.<ModulesConfig> emptyList());
    }

    public WebApplication(List<ModulesConfig> modulesConfigs) {
        Assertion.notNull(modulesConfigs, "ModulesConfig");
        this.modulesConfigs = modulesConfigs;
    }

    @Override
    public void run(ApplicationContext resolver, ApplicationProperties props, Collection<ClassCollector> collectors,
            ClassLoader classLoader) {
        StopWatch sw = new StopWatch();
        sw.start();
        this.resolver = resolver;
        this.classLoader = classLoader;
        ApplicationPropertiesHolder.configure(this, props);
        log.log(Markers.BOOT_APPLICATION, "IB000001");
        List<Version> versions = Version.load(classLoader);
        if (versions.isEmpty() == false && log.isInfoEnabled(Markers.BOOT_APPLICATION)) {
            log.log(Markers.BOOT_APPLICATION, "IB000008");
            for (Version v : versions) {
                log.info("   " + v.getVersion() + " : " + v.getArtifactId());
            }
        }
        Collection<String> invocationPackageNames = props.getComponentPackageNames();
        log.log(Markers.BOOT_APPLICATION, "DB000001", invocationPackageNames);
        Set<String> modulesPackageNames = new HashSet<String>();
        if (CollectionUtils.isNotEmpty(invocationPackageNames)) {
            modulesPackageNames.addAll(invocationPackageNames);
        }
        modulesPackageNames.add(DEFAULT_PACKAGE_NAME);
        log.log(Markers.BOOT_APPLICATION, "DB000002", modulesPackageNames);
        initApplication(collectors, modulesPackageNames, props);
        log.log(Markers.BOOT_APPLICATION, "IB000002", sw.stop());
    }

    @Override
    public Response processRequest(RequestPath requestedPath, RequestContext requestContext,
            ResponseContext responseContext) throws IOException, WebApplicationException {
        InvocationMetadata metadata = null;
        Modules mod = null;
        List<ApplicationProcessor> processors = null;
        RequestContext context = requestContext;
        Response response = null;
        try {
            mod = getModules();
            processors = mod.getApplicationProcessors();
            MutableRequestContext mutableContext = new DefaultMutableRequestContext(context);
            preMatching(processors, mutableContext, requestedPath);
            context = mutableContext.unwrap();
            RouteRegistry mapping = getRouteRegistry();
            log.log(Markers.LIFECYCLE, "DL000004", requestedPath);
            metadata = mapping.findInvocationMetadata(context, mod.getInvocationMetadataFinders());
            if (metadata == null) {
                log.log(Markers.LIFECYCLE, "DL000005", requestedPath);
                return NOT_FOUND;
            }
            log.log(Markers.LIFECYCLE, "DL000006", requestedPath, metadata);
            ContainerAdaptor invocationInstances = mod.getInvocationInstanceProvider();
            RequestValueResolvers resolvers = mod.getRequestValueResolvers();
            TypeMapperContext typeMapperContext = mod.getTypeMapperContext();
            Invocation invocation = mod.getInvocationFactory().createInvocation(invocationInstances, metadata, context,
                    responseContext, typeMapperContext, resolvers);
            InvocationArguments arguments = invocation.getInvocationArguments();
            prepareInvoke(processors, arguments, metadata, context, resolvers, typeMapperContext);
            try {
                Object invocationResult = mod.getInvoker().invoke(invocation, metadata, context, responseContext);
                log.log(Markers.LIFECYCLE, "DL000007", invocation.getInvocationInstance(), invocationResult);
                postInvoke(processors, invocationResult, arguments, metadata, context, resolvers);
                response = handleResponse(mod, invocationResult, metadata, context, responseContext);
            } catch (Exception e) {
                log.log(Markers.LIFECYCLE, "DL000012", invocation.getInvocationInstance(), e);
                onException(processors, e, arguments, metadata, context);
                List<Object> args = arguments.asList();
                throw new InvocationFailureException(e, metadata, args.toArray(new Object[args.size()]));
            }
            afterCompletion(processors, context, responseContext, null);
        } catch (InvokeInterruptedException e) {
            response = handleResponse(mod, e.getInterruption(), metadata, context, responseContext);
            afterCompletion(processors, context, responseContext, e);
        } catch (Exception e) {
            ExceptionHandler handler = mod.getExceptionHandler();
            log.log(Markers.LIFECYCLE, "DL000009", (Object) e, handler);
            Object exceptionResult = handler.handleException(e);
            if (exceptionResult != null) {
                response = handleResponse(mod, exceptionResult, metadata, context, responseContext);
                afterCompletion(processors, context, responseContext, e);
            } else {
                afterCompletion(processors, context, responseContext, e);
                throw new WebApplicationException(e);
            }
        }
        return response;
    }

    protected void preMatching(List<ApplicationProcessor> processors, MutableRequestContext request,
            RequestPath requestedPath) {
        log.log(Markers.LIFECYCLE, "DL000017");
        Object interruption = ApplicationProcessor.NO_INTERRUPTION;
        for (ApplicationProcessor processor : processors) {
            interruption = processor.preMatching(request, requestedPath);
            if (interruption != ApplicationProcessor.NO_INTERRUPTION) {
                throw new InvokeInterruptedException(interruption);
            }
        }
    }

    protected void prepareInvoke(List<ApplicationProcessor> processors, InvocationArguments args,
            InvocationMetadata metadata, RequestContext request, RequestValueResolvers attributesHandlers,
            TypeMapperContext typeMapperContext) {
        log.log(Markers.LIFECYCLE, "DL000013");
        Object interruption = ApplicationProcessor.NO_INTERRUPTION;
        for (ApplicationProcessor processor : processors) {
            interruption = processor.prepareInvoke(args, metadata, request, typeMapperContext, attributesHandlers);
            if (interruption != ApplicationProcessor.NO_INTERRUPTION) {
                throw new InvokeInterruptedException(interruption);
            }
        }
    }

    protected void postInvoke(List<ApplicationProcessor> processors, Object invocationResult, InvocationArguments args,
            InvocationMetadata metadata, RequestContext request, RequestValueResolvers attributesHandlers) {
        log.log(Markers.LIFECYCLE, "DL000014");
        for (ApplicationProcessor processor : processors) {
            processor.postInvoke(invocationResult, args, metadata, request, attributesHandlers);
        }
    }

    protected void onException(List<ApplicationProcessor> processors, Exception thrown, InvocationArguments args,
            InvocationMetadata metadata, RequestContext request) {
        log.log(Markers.LIFECYCLE, "DL000015");
        Object interruption = ApplicationProcessor.NO_INTERRUPTION;
        for (ApplicationProcessor processor : processors) {
            interruption = processor.processException(thrown, request, args, metadata);
            if (interruption != ApplicationProcessor.NO_INTERRUPTION) {
                throw new InvokeInterruptedException(interruption);
            }
        }
    }

    protected void afterCompletion(List<ApplicationProcessor> processors, RequestContext context,
            ResponseContext responseContext, Exception e) {
        log.log(Markers.LIFECYCLE, "DL000016");
        for (ApplicationProcessor processor : processors) {
            processor.afterCompletion(context, responseContext, e);
        }
    }

    protected Response handleResponse(Modules modules, Object result, InvocationMetadata metadata,
            RequestContext context, ResponseContext responseContext) throws IOException, WebApplicationException {
        ResponseHandler resultHandler = modules.getResponseHandler();
        return resultHandler.handleResult(result, metadata, modules.getResponseResolver(), context, responseContext,
                modules.getExceptionHandler(), modules);
    }

    protected void initApplication(Collection<ClassCollector> collectors, Set<String> modulePackageNames,
            ApplicationProperties properties) {
        Collection<Class<?>> moduleClasses = collectClasses(modulePackageNames, collectors);
        ModulesBuilder modulesBuilder = processConfigPreparation(moduleClasses);
        ApplicationContext resolver = getApplicationContextResolver();
        ContainerAdaptor defaultContainer = setUpDefaultContainer(resolver, moduleClasses);
        Modules modules = modulesBuilder.buildModules(resolver, defaultContainer);
        monitorModules(modules);
        setModules(modules);
        log.log(Markers.BOOT_APPLICATION, "DB000003", modules);
        setRouteRegistry(createRouteRegistry(modules.getInvocationMetadataFactories(), properties,
                modules.getInvocationInstanceProvider()));
    }

    private void monitorModules(Modules modules) {
        if (log.isDebugEnabled(Markers.MONITOR_MODULES)) {
            log.log(Markers.MONITOR_MODULES, "IB000009");
            log.debug(Markers.MONITOR_MODULES, "++++++++++++++++++++++++++++");
            log.debug(Markers.MONITOR_MODULES, "ApplicationProcessor");
            log.debug(Markers.MONITOR_MODULES, "===");
            for (ApplicationProcessor m : modules.getApplicationProcessors()) {
                log.debug(Markers.MONITOR_MODULES, " - " + m.getClass().getCanonicalName());
            }
            log.debug(Markers.MONITOR_MODULES, "");
            ExceptionHandler eh = modules.getExceptionHandler();
            log.debug(Markers.MONITOR_MODULES, "ExceptionHandler");
            log.debug(Markers.MONITOR_MODULES, "===");
            log.debug(Markers.MONITOR_MODULES, " - " + eh.getClass().getCanonicalName());
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "ExceptionMapper");
            log.debug(Markers.MONITOR_MODULES, "===");
            for (ExceptionMapper m : modules.getExceptionMappers()) {
                log.debug(Markers.MONITOR_MODULES, " - " + m.getClass().getCanonicalName());
            }
            log.debug(Markers.MONITOR_MODULES, "");
            InvocationFactory f = modules.getInvocationFactory();
            log.debug(Markers.MONITOR_MODULES, "InvocationFactory");
            log.debug(Markers.MONITOR_MODULES, "===");
            log.debug(Markers.MONITOR_MODULES, " - " + f.getClass().getCanonicalName());
            log.debug(Markers.MONITOR_MODULES, "");
            ContainerAdaptor ii = modules.getInvocationInstanceProvider();
            log.debug(Markers.MONITOR_MODULES, "InvocationInstanceProvider");
            log.debug(Markers.MONITOR_MODULES, "===");
            log.debug(Markers.MONITOR_MODULES, " - " + ii.getClass().getCanonicalName());
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "InvocationInterceptor");
            log.debug(Markers.MONITOR_MODULES, "===");
            for (InvocationInterceptor m : modules.getInvocationInterceptors()) {
                log.debug(Markers.MONITOR_MODULES, " - " + m.getClass().getCanonicalName());
            }
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "InvocationMetadataFactory");
            log.debug(Markers.MONITOR_MODULES, "===");
            for (InvocationMetadataFactory m : modules.getInvocationMetadataFactories()) {
                log.debug(Markers.MONITOR_MODULES, " - " + m.getClass().getCanonicalName());
            }
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "InvocationMetadataFinder");
            log.debug(Markers.MONITOR_MODULES, "===");
            for (InvocationMetadataFinder m : modules.getInvocationMetadataFinders()) {
                log.debug(Markers.MONITOR_MODULES, " - " + m.getClass().getCanonicalName());
            }
            Invoker ik = modules.getInvoker();
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "Invoker");
            log.debug(Markers.MONITOR_MODULES, "===");
            log.debug(Markers.MONITOR_MODULES, " - " + ik.getClass().getCanonicalName());
            ContainerAdaptor mca = modules.getModulesContainerAdaptor();
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "ModulesContainerAdaptor");
            log.debug(Markers.MONITOR_MODULES, "===");
            log.debug(Markers.MONITOR_MODULES, " - " + mca.getClass().getCanonicalName());
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "RequestValueResolver");
            log.debug(Markers.MONITOR_MODULES, "===");
            for (RequestValueResolver m : modules.getRequestValueResolvers().all()) {
                log.debug(Markers.MONITOR_MODULES, " - " + m.getClass().getCanonicalName());
            }
            ResponseHandler rh = modules.getResponseHandler();
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "ResponseHandler");
            log.debug(Markers.MONITOR_MODULES, "===");
            log.debug(Markers.MONITOR_MODULES, " - " + rh.getClass().getCanonicalName());
            RenderableResolver rr = modules.getResponseResolver();
            log.debug(Markers.MONITOR_MODULES, "");
            log.debug(Markers.MONITOR_MODULES, "RenderableResolver");
            log.debug(Markers.MONITOR_MODULES, "===");
            log.debug(Markers.MONITOR_MODULES, " - " + rr.getClass().getCanonicalName());
            log.debug(Markers.MONITOR_MODULES, "++++++++++++++++++++++++++++");
        }
    }

    protected ModulesBuilder processConfigPreparation(Collection<Class<?>> moduleClasses) {
        List<ModulesConfig> reflectiveModuleConfigInstances = new ArrayList<ModulesConfig>();
        List<Class<ModulesConfig>> clazzes = ReflectionUtils.filterClassAsImplementsInterface(ModulesConfig.class,
                moduleClasses);
        for (Class<ModulesConfig> configClass : clazzes) {
            ModulesConfig config = ReflectionUtils.getInstanceQuietly(configClass);
            if (config != null) {
                reflectiveModuleConfigInstances.add(config);
            }
        }
        List<ModulesConfig> moduleConfigInstances = new ArrayList<ModulesConfig>(getModulesConfigs());
        for (ModulesConfig reflectiveModule : reflectiveModuleConfigInstances) {
            if (!moduleConfigInstances.contains(reflectiveModule)) {
                moduleConfigInstances.add(reflectiveModule);
            }
        }
        Collections.sort(moduleConfigInstances, getModulesConfigComparator());
        ModulesBuilder modulesBuilder = new DefaultModulesBuilder();
        for (ModulesConfig config : moduleConfigInstances) {
            log.log(Markers.BOOT_APPLICATION, "IB000003", config);
            modulesBuilder = config.prepare(modulesBuilder);
        }
        return modulesBuilder;
    }

    protected Comparator<ModulesConfig> getModulesConfigComparator() {
        return new ModulesConfigComparator();
    }

    protected ContainerAdaptor setUpDefaultContainer(ApplicationContext resolver,
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

    protected RouteRegistry createRouteRegistry(List<InvocationMetadataFactory> factories,
            ApplicationProperties properties, ContainerAdaptor instanceProvider) {
        RouteRegistry mapping = new DefaultRouteRegistry();
        for (InvocationMetadataFactory factory : factories) {
            for (InvocationMetadata actionMethodMetadata : factory.createInvocationMetadatas(properties,
                    instanceProvider)) {
                log.log(Markers.BOOT_APPLICATION, "IB000004", actionMethodMetadata.getDefinedPath(),
                        actionMethodMetadata.getInvocationClass(), actionMethodMetadata.getMethodName());
                mapping.register(actionMethodMetadata);
            }
        }
        return mapping;
    }

    protected Collection<Class<?>> collectAllClasses(Collection<ClassCollector> collectors) {
        Collection<Class<?>> collectedClasses = new HashSet<Class<?>>();
        for (String resourceName : SystemProperties.classPathes()) {
            URL resourceURL = ResourceUtils.findResource(resourceName);
            for (ClassCollector collector : collectors) {
                collectedClasses.addAll(collector.collect(StringUtils.EMPTY, resourceURL, classLoader));
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
                    collectedClasses.addAll(collector.collect(packageName, resourceURL, classLoader));
                }
            }
        }
        return collectedClasses;
    }

    @Override
    public RouteRegistry getRouteRegistry() {
        return routes;
    }

    protected void setRouteRegistry(RouteRegistry registry) {
        this.routes = registry;
    }

    protected List<ModulesConfig> getModulesConfigs() {
        return this.modulesConfigs;
    }

    @Override
    public Modules getModules() {
        return this.modules;
    }

    protected void setModules(Modules modules) {
        this.modules = modules;
    }

    protected final ApplicationContext getApplicationContextResolver() {
        return this.resolver;
    }

    @Override
    public void dispose() {
        this.classLoader = null;
        this.resolver = null;
        if (this.modules != null) {
            this.modules.dispose();
            this.modules = null;
        }
        if (this.routes != null) {
            this.routes.dispose();
            this.routes = null;
        }
        ApplicationPropertiesHolder.dispose(this);
    }
}
