package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.analogweb.Application;
import org.analogweb.ApplicationProperties;
import org.analogweb.core.ApplicationConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class ApplicationPropertiesHolderTest {

	private Application app;
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() {
		app = mock(Application.class);
	}

	@After
	public void tearDown() {
		ApplicationPropertiesHolder.dispose(app);
	}

	@Test
	public void testConfigure() {
		ApplicationProperties config = mock(ApplicationProperties.class);
		ApplicationProperties actual = ApplicationPropertiesHolder.configure(
				app, config);
		assertThat(actual, is(config));
		assertThat(ApplicationPropertiesHolder.current(), is(config));
	}

	@Test
	public void testNoConfigure() {
		thrown.expect(ApplicationConfigurationException.class);
		ApplicationPropertiesHolder.current();
	}

	@Test
	public void testReConfigure() {
		thrown.expect(ApplicationConfigurationException.class);
		ApplicationProperties config = mock(ApplicationProperties.class);
		ApplicationPropertiesHolder.configure(app, config);
		ApplicationPropertiesHolder.configure(app, config);
	}

	@Test
	public void testDisposeAndGet() {
		thrown.expect(ApplicationConfigurationException.class);
		ApplicationProperties config = mock(ApplicationProperties.class);
		ApplicationPropertiesHolder.configure(app, config);
		ApplicationPropertiesHolder.dispose(app);
		ApplicationPropertiesHolder.current();
	}
}
