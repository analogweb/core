package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.analogweb.ModulesConfig;
import org.analogweb.PluginModulesConfig;
import org.analogweb.UserModulesConfig;
import org.analogweb.core.ModulesConfigComparator;
import org.analogweb.core.RootModulesConfig;
import org.analogweb.core.AssertionFailureException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author snowgoose
 */
public class ModulesConfigComparatorTest {

	private ModulesConfigComparator comparator;
	private RootModulesConfig modulesConfig;
	private PluginModulesConfig pluginModulesConfig;
	private UserModulesConfig userModulesConfig;
	private ModulesConfig otherModulesConfig;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		comparator = new ModulesConfigComparator();
		modulesConfig = new RootModulesConfig();
		pluginModulesConfig = mock(PluginModulesConfig.class);
		userModulesConfig = mock(UserModulesConfig.class);
		otherModulesConfig = mock(ModulesConfig.class);
	}

	@Test
	public void testCompareModulesConfig() {
		int actual = comparator.compare(modulesConfig, pluginModulesConfig);
		assertThat(actual, is(-1));
		actual = comparator.compare(modulesConfig, modulesConfig);
		assertThat(actual, is(0));
		actual = comparator.compare(modulesConfig, userModulesConfig);
		assertThat(actual, is(-1));
	}

	@Test
	public void testComparePluginModulesConfig() {
		int actual = comparator.compare(pluginModulesConfig, modulesConfig);
		assertThat(actual, is(1));
		actual = comparator.compare(pluginModulesConfig, pluginModulesConfig);
		assertThat(actual, is(0));
		actual = comparator.compare(pluginModulesConfig, userModulesConfig);
		assertThat(actual, is(-1));
	}

	@Test
	public void testCompareUserModulesConfig() {
		int actual = comparator.compare(userModulesConfig, modulesConfig);
		assertThat(actual, is(1));
		actual = comparator.compare(userModulesConfig, userModulesConfig);
		assertThat(actual, is(0));
		actual = comparator.compare(userModulesConfig, pluginModulesConfig);
		assertThat(actual, is(1));
	}

	@Test
	public void testCompareOtherModulesConfig() {
		int actual = comparator.compare(otherModulesConfig, modulesConfig);
		assertThat(actual, is(1));
		actual = comparator.compare(otherModulesConfig, userModulesConfig);
		assertThat(actual, is(1));
		actual = comparator.compare(otherModulesConfig, pluginModulesConfig);
		assertThat(actual, is(1));
	}

	@Test
	public void testCompareModulesConfigIsNull() {
		thrown.expect(AssertionFailureException.class);
		comparator.compare(userModulesConfig, null);
	}

	@Test
	public void testCompareModulesConfigAreNull() {
		thrown.expect(AssertionFailureException.class);
		comparator.compare(null, modulesConfig);
	}

	@Test
	public void testSortModulesConfig() {
		List<ModulesConfig> configs = new ArrayList<ModulesConfig>();
		configs.add(userModulesConfig);
		configs.add(modulesConfig);
		configs.add(pluginModulesConfig);
		configs.add(otherModulesConfig);
		Collections.sort(configs, comparator);
		assertThat(configs.get(0), is((ModulesConfig) modulesConfig));
		assertThat(configs.get(1), is((ModulesConfig) pluginModulesConfig));
		assertThat(configs.get(2), is((ModulesConfig) userModulesConfig));
		assertThat(configs.get(3), is(otherModulesConfig));
	}
}
