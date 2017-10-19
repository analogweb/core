package org.analogweb.core;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.analogweb.ApplicationContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class StaticMappingContainerAdaptorFactoryTest {

	private StaticMappingContainerAdaptorFactory factory;
	private ApplicationContext resolver;

	@Before
	public void setUp() throws Exception {
		factory = new StaticMappingContainerAdaptorFactory();
		resolver = mock(ApplicationContext.class);
	}

	@Test
	public void testCreateContainerAdaptor() {
		StaticMappingContainerAdaptor adaptor = factory
				.createContainerAdaptor(resolver);
		StaticMappingContainerAdaptor adaptor2 = factory
				.createContainerAdaptor(resolver);
		assertSame(adaptor, adaptor2);
	}
}
