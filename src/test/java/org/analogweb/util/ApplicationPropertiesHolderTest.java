package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;

import org.analogweb.Application;
import org.analogweb.ApplicationProperties;
import org.analogweb.exception.ApplicationConfigurationException;
import org.analogweb.util.ApplicationPropertiesHolder.Creator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ApplicationPropertiesHolderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConfigure() {
        Creator config = mock(Creator.class);
        ApplicationProperties expected = mock(ApplicationProperties.class);
        when(config.create()).thenReturn(expected);
        Application app = mock(Application.class);
        ApplicationProperties actual = ApplicationPropertiesHolder.configure(app, config);
        assertThat(actual, is(expected));
        assertThat(ApplicationPropertiesHolder.current(), is(expected));
    }

    @Test
    public void testReConfigure() {
        thrown.expect(ApplicationConfigurationException.class);
        Creator config = mock(Creator.class);
        ApplicationProperties expected = mock(ApplicationProperties.class);
        when(config.create()).thenReturn(expected);
        Application app = mock(Application.class);
        ApplicationProperties actual = ApplicationPropertiesHolder.configure(app, config);
        assertThat(actual, is(expected));
        ApplicationPropertiesHolder.configure(app, config);
    }

    @Test
    public void testDisposeAndGet() {
        thrown.expect(ApplicationConfigurationException.class);
        Creator config = mock(Creator.class);
        ApplicationProperties expected = mock(ApplicationProperties.class);
        when(config.create()).thenReturn(expected);
        Application app = mock(Application.class);
        ApplicationProperties actual = ApplicationPropertiesHolder.configure(app, config);
        assertThat(actual, is(expected));
        ApplicationPropertiesHolder.dispose(app);
        ApplicationPropertiesHolder.current();
    }

}
