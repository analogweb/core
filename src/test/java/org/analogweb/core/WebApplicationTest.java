package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jp.acme.test.additionalcomponents.StubPreProcessor;

import org.analogweb.ApplicationContext;
import org.analogweb.ApplicationProcessor;
import org.analogweb.ApplicationProperties;
import org.analogweb.InvocationMetadata;
import org.analogweb.Modules;
import org.analogweb.RequestContext;
import org.analogweb.RequestPath;
import org.analogweb.RouteRegistry;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.ClassCollector;
import org.analogweb.util.FileClassCollector;
import org.analogweb.util.JarClassCollector;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * TODO rename test to ApplicationPropertiesTest!
 *
 * @author snowgoose
 */
public class WebApplicationTest {

    private static final Log log = Logs.getLog(WebApplicationTest.class);
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private WebApplication application;
    private ClassLoader classLoader;
    private ApplicationContext resolver;
    private Collection<ClassCollector> collectors;
    private RequestContext context;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        classLoader = Thread.currentThread().getContextClassLoader();
        resolver = mock(ApplicationContext.class);
        context = mock(RequestContext.class);
        List<ClassCollector> collectors = new ArrayList<ClassCollector>();
        collectors.add(new JarClassCollector());
        collectors.add(new FileClassCollector());
        this.collectors = collectors;
    }

    @After
    public void tearDown() {
        application.dispose();
    }

    @Test
    public void testInitApplication() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getComponentPackageNames()).thenReturn(Arrays.asList("jp.acme.test.actionsonly"));
        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);
        application = new WebApplication();
        application.run(resolver, props, collectors, classLoader);
        RouteRegistry mapping = application.getRouteRegistry();
        RequestPath pathAnyThing = mock(RequestPath.class);
        when(pathAnyThing.getActualPath()).thenReturn("/baa/anything");
        when(pathAnyThing.getRequestMethod()).thenReturn("POST");
        when(context.getRequestPath()).thenReturn(pathAnyThing);
        InvocationMetadata metadataAnyThing = mapping.findInvocationMetadata(context,
                application.getModules().getInvocationMetadataFinders());
        log.debug(metadataAnyThing.toString());
    }

    @Test
    public void testInitApplicationWithoutMetadata() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        // when(props.getApplicationSpecifier()).thenReturn(null);
        when(props.getComponentPackageNames()).thenReturn(Arrays.asList("jp.acme.test.actionsonly"));
        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);
        application = new WebApplication();
        application.run(resolver, props, collectors, classLoader);
        RouteRegistry mapping = application.getRouteRegistry();
        RequestPath pathAnyThing = mock(RequestPath.class);
        when(pathAnyThing.getActualPath()).thenReturn("/baa/anything");
        when(pathAnyThing.getRequestMethod()).thenReturn("POST");
        when(context.getRequestPath()).thenReturn(pathAnyThing);
        InvocationMetadata metadataAnyThing = mapping.findInvocationMetadata(context,
                application.getModules().getInvocationMetadataFinders());
        log.debug(metadataAnyThing.toString());
    }

    @Test
    public void testInitApplicationWithoutRootComponentPackages() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getComponentPackageNames()).thenReturn(null);
        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);
        application = new WebApplication();
        application.run(resolver, props, collectors, classLoader);
    }

    @Test
    public void testInitApplicationWithAdditionalComponents() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getComponentPackageNames()).thenReturn(Arrays.asList("jp.acme.test.additionalcomponents"));
        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);
        application = new WebApplication();
        application.run(resolver, props, collectors, classLoader);
        Modules modules = application.getModules();
        final List<ApplicationProcessor> processors = modules.getApplicationProcessors();
        assertThat(processors, new NoDescribeMatcher<List<ApplicationProcessor>>() {

            @Override
            @SuppressWarnings("unchecked")
            public boolean matches(Object arg0) {
                if (processors.getClass().isInstance(arg0)) {
                    for (ApplicationProcessor processor : (List<ApplicationProcessor>) arg0) {
                        if (processor instanceof StubPreProcessor) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Test
    public void testDispose() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getComponentPackageNames()).thenReturn(Arrays.asList("jp.acme.test.actionsonly"));
        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);
        application = new WebApplication();
        application.run(resolver, props, collectors, classLoader);
        application.dispose();
        assertThat(application.getModules(), is(nullValue()));
        assertThat(application.getRouteRegistry(), is(nullValue()));
    }
}
