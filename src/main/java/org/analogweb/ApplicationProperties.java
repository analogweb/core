package org.analogweb;

import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Global settings properties for {@link Application}。
 * 
 * @author snowgoose
 */
public interface ApplicationProperties {

	String PACKAGES = "analogweb.packages";
	String TEMP_DIR = "analogweb.tmpdir";
	String LOCALE = "analogweb.default.locale";

	Collection<String> getComponentPackageNames();

	File getTempDir();

	Locale getDefaultClientLocale();

	Map<String, Object> getProperties();

	String getStringProperty(String key);

	Object getProperty(String key);
}
