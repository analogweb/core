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
import org.analogweb.ApplicationProperties;
import org.analogweb.ContainerAdaptor;
import org.analogweb.Response;
import org.analogweb.ResponseFormatter;
import org.analogweb.ResponseHandler;
import org.analogweb.ResponseResolver;
import org.analogweb.ExceptionHandler;
import org.analogweb.Invocation;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.Module;
import org.analogweb.Modules;
import org.analogweb.ModulesBuilder;
import org.analogweb.ModulesConfig;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMapping;
import org.analogweb.ResponseContext;
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

            Invocation invocation = mod.getInvocationFactory().createInvocation(
                    invocationInstances, metadata, context, responseContext,
                    mod.getTypeMapperContext(),
                    mod.getRequestValueResolvers());

            Object invocationResult = mod.getInvoker().invoke(invocation, metadata, context,responseContext);

            log.log(Markers.LIFECYCLE, "DL000007", invocation.getInvocationInstance(),
                    invocationResult);

            handleResponse(mod, invocationResult, metadata, context, responseContext);
        } catch (Exception e) {
            ExceptionHandler handler = mod.getExceptionHandler();
            log.log(Markers.LIFECYCLE, "DL000009", (Object) e, handler);
            Object exceptionResult = handler.handleException(e);
            if (exceptionResult != null) {
                handleResponse(mod, exceptionResult, metadata, context, responseContext);
            }
        }
        return PROCEEDED;
    }

    protected void handleResponse(Modules modules, Object result, InvocationMetadata metadata,
            RequestContext context, ResponseContext responseContext) throws IOException,
            WebApplicationException {
        ResponseResolver resultResolver = modules.getResponseResolver();
        Response resolved = resultResolver.resolve(result, metadata, context, responseContext);
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
