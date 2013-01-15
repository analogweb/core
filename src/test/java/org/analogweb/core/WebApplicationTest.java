package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import jp.acme.test.additionalcomponents.StubPreProcessor;

import org.analogweb.ApplicationContextResolver;
import org.analogweb.ApplicationProperties;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationProcessor;
import org.analogweb.Modules;
import org.analogweb.RequestPath;
import org.analogweb.RequestPathMapping;
import org.analogweb.exception.MissingRequiredParameterException;
import org.analogweb.junit.NoDescribeMatcher;
import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * TODO rename test to ApplicationPropertiesTest!
 * @author snowgoose
 */
public class WebApplicationTest {

    private static final Log log = Logs.getLog(WebApplicationTest.class);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private WebApplication application;
    private ClassLoader classLoader;
    private ApplicationContextResolver resolver;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        classLoader = Thread.currentThread().getContextClassLoader();
        resolver = mock(ApplicationContextResolver.class);
    }

    @Test
    public void testInitApplication() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getApplicationSpecifier()).thenReturn(".rn");
        when(props.getComponentPackageNames())
                .thenReturn(Arrays.asList("jp.acme.test.actionsonly"));

        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);

        application = new WebApplication();
        application.run(resolver, props, classLoader);

        RequestPathMapping mapping = application.getRequestPathMapping();
        RequestPath pathAnyThing = mock(RequestPath.class);
        when(pathAnyThing.getActualPath()).thenReturn("/baa/anything");
        when(pathAnyThing.getMethod()).thenReturn("POST");
        InvocationMetadata metadataAnyThing = mapping.findInvocationMetadata(pathAnyThing);
        log.debug(metadataAnyThing.toString());

        assertThat(application.getApplicationSpecifier(), is(".rn"));
    }

    @Test
    public void testInitApplicationWithoutMetadata() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getApplicationSpecifier()).thenReturn(null);
        when(props.getComponentPackageNames())
                .thenReturn(Arrays.asList("jp.acme.test.actionsonly"));

        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);

        application = new WebApplication();
        application.run(resolver, props, classLoader);

        RequestPathMapping mapping = application.getRequestPathMapping();
        RequestPath pathAnyThing = mock(RequestPath.class);
        when(pathAnyThing.getActualPath()).thenReturn("/baa/anything");
        when(pathAnyThing.getMethod()).thenReturn("POST");
        InvocationMetadata metadataAnyThing = mapping.findInvocationMetadata(pathAnyThing);
        log.debug(metadataAnyThing.toString());

        assertThat(application.getApplicationSpecifier(), is(nullValue()));
    }

    @Test
    public void testInitApplicationWithoutRootComponentPackages() throws Exception {

        thrown.expect(new NoDescribeMatcher<MissingRequiredParameterException>() {
            @Override
            public boolean matches(Object arg0) {
                if (arg0 instanceof MissingRequiredParameterException) {
                    MissingRequiredParameterException mrq = (MissingRequiredParameterException) arg0;
                    assertThat(mrq.getMissedParameterName(),
                            is(WebApplication.INIT_PARAMETER_ROOT_COMPONENT_PACKAGES));
                    return true;
                }
                return false;
            }
        });
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getApplicationSpecifier()).thenReturn(".do");
        when(props.getComponentPackageNames()).thenReturn(null);

        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);

        application = new WebApplication();
        application.run(resolver, props, classLoader);
    }

    @Test
    public void testInitApplicationWithAdditionalComponents() throws Exception {
        ApplicationProperties props = mock(ApplicationProperties.class);
        when(props.getApplicationSpecifier()).thenReturn(null);
        when(props.getComponentPackageNames()).thenReturn(
                Arrays.asList("jp.acme.test.additionalcomponents"));

        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);

        application = new WebApplication();
        application.run(resolver, props, classLoader);

        Modules modules = application.getModules();
        final List<InvocationProcessor> processors = modules.getInvocationProcessors();
        assertThat(processors, new NoDescribeMatcher<List<InvocationProcessor>>() {
            @Override
            @SuppressWarnings("unchecked")
            public boolean matches(Object arg0) {
                if (processors.getClass().isInstance(arg0)) {
                    for (InvocationProcessor processor : (List<InvocationProcessor>) arg0) {
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
        when(props.getApplicationSpecifier()).thenReturn(null);
        when(props.getComponentPackageNames())
                .thenReturn(Arrays.asList("jp.acme.test.actionsonly"));

        File tempFolder = folder.newFolder("test");
        when(props.getTempDir()).thenReturn(tempFolder);

        application = new WebApplication();
        application.run(resolver, props, classLoader);

        application.dispose();

        assertThat(application.getApplicationSpecifier(), is(nullValue()));
        assertThat(application.getModules(), is(nullValue()));
        assertThat(application.getRequestPathMapping(), is(nullValue()));
    }

}
