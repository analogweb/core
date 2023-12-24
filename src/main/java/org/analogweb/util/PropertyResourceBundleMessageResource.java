package org.analogweb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * @author snowgoose
 */
public class PropertyResourceBundleMessageResource implements MessageResource {

    protected static final String DEFAULT_CHARSET = "UTF-8";
    private final String baseName;
    private final ClassLoader bundleClassLoader;
    private final String charsetName;

    public PropertyResourceBundleMessageResource(String baseName) {
        this(baseName, DEFAULT_CHARSET);
    }

    public PropertyResourceBundleMessageResource(String baseName, String bundleCharsetName) {
        this(baseName, bundleCharsetName, Thread.currentThread().getContextClassLoader());
    }

    public PropertyResourceBundleMessageResource(String baseName, ClassLoader classLoader) {
        this(baseName, DEFAULT_CHARSET, classLoader);
    }

    public PropertyResourceBundleMessageResource(String baseName, String bundleCharserName, ClassLoader classLoader) {
        this.baseName = baseName;
        this.charsetName = bundleCharserName;
        this.bundleClassLoader = classLoader;
    }

    @Override
    public String getMessage(String code) {
        return getMessage(code, Locale.getDefault());
    }

    @Override
    public String getMessage(String code, Object... args) {
        return getMessage(code, Locale.getDefault(), args);
    }

    @Override
    public String getMessage(String code, Locale locale) {
        return getMessage(code, locale, new Object[0]);
    }

    private final Map<MessageFormatKey, MessageFormat> formatterCache = Maps.newConcurrentHashMap();
    private final Map<Locale, ResourceBundle> bundleCache = Maps.newConcurrentHashMap();

    @Override
    public String getMessage(String code, Locale locale, Object... args) {
        if (!bundleCache.containsKey(locale)) {
            bundleCache.put(locale, getBundle(baseName, locale, getControl()));
        }
        ResourceBundle bundle = bundleCache.get(locale);
        if (bundle.containsKey(code) == false) {
            return code;
        }
        if (ArrayUtils.isEmpty(args)) {
            return bundle.getString(code);
        } else {
            String message = bundle.getString(code);
            MessageFormatKey key = MessageFormatKey.create(message, locale);
            if (!formatterCache.containsKey(key)) {
                formatterCache.put(key, new MessageFormat(message, locale));
            }
            MessageFormat formatter = formatterCache.get(key);
            synchronized (formatter) {
                return formatter.format(args);
            }
        }
    }

    protected ClassLoader getBundleClassLoader() {
        return this.bundleClassLoader;
    }

    protected String getBundleResourceCharsetName() {
        return this.charsetName;
    }

    private Control control;

    protected Control getControl() {
        if (control == null) {
            control = new PropertyResourceBundleControl(getBundleResourceCharsetName());
        }
        return control;
    }

    protected ResourceBundle getBundle(String baseName, Locale locale, Control control) {
        if (control == null) {
            return PropertyResourceBundle.getBundle(baseName, locale, getBundleClassLoader());
        } else {
            return PropertyResourceBundle.getBundle(baseName, locale, getBundleClassLoader(), control);
        }
    }

    protected static final class MessageFormatKey implements Serializable {

        private static final long serialVersionUID = 1L;
        private final Locale locale;
        private final String format;

        public static MessageFormatKey create(String formate, Locale locale) {
            return new MessageFormatKey(formate, locale);
        }

        protected MessageFormatKey(String format, Locale locale) {
            this.locale = locale;
            this.format = format;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MessageFormatKey) {
                MessageFormatKey other = (MessageFormatKey) obj;
                return other.format.equals(format) && other.locale.equals(locale);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int base = 17;
            int result = 0;
            result = base * 37 + locale.hashCode();
            result = result * 37 + format.hashCode();
            return result;
        }
    }

    protected static class PropertyResourceBundleControl extends Control {

        private final String charsetName;

        protected PropertyResourceBundleControl(String charsetName) {
            super();
            this.charsetName = charsetName;
        }

        @Override
        @SuppressWarnings("unchecked")
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            ResourceBundle bundle = null;
            if (format.equals("java.class")) {
                try {
                    Class<? extends ResourceBundle> bundleClass = (Class<? extends ResourceBundle>) loader
                            .loadClass(bundleName);
                    // If the class isn't a ResourceBundle subclass, throw a
                    // ClassCastException.
                    if (ResourceBundle.class.isAssignableFrom(bundleClass)) {
                        bundle = bundleClass.newInstance();
                    } else {
                        throw new ClassCastException(bundleClass.getName() + " cannot be cast to ResourceBundle");
                    }
                } catch (ClassNotFoundException e) {
                    return null;
                }
            } else if (format.equals("java.properties")) {
                final String resourceName = toResourceName(bundleName, "properties");
                final ClassLoader classLoader = loader;
                final boolean reloadFlag = reload;
                InputStream stream = null;
                try {
                    stream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {

                        public InputStream run() throws IOException {
                            InputStream is = null;
                            if (reloadFlag) {
                                URL url = classLoader.getResource(resourceName);
                                if (url != null) {
                                    URLConnection connection = url.openConnection();
                                    if (connection != null) {
                                        // Disable caches to get
                                        // fresh
                                        // data
                                        // for
                                        // reloading.
                                        connection.setUseCaches(false);
                                        is = connection.getInputStream();
                                    }
                                }
                            } else {
                                is = classLoader.getResourceAsStream(resourceName);
                            }
                            return is;
                        }
                    });
                } catch (PrivilegedActionException e) {
                    throw (IOException) e.getException();
                }
                if (stream != null) {
                    BufferedReader br = null;
                    try {
                        if (charsetName != null) {
                            br = new BufferedReader(new InputStreamReader(stream, charsetName));
                            bundle = new PropertyResourceBundle(br);
                        } else {
                            bundle = new PropertyResourceBundle(stream);
                        }
                    } finally {
                        if (br != null) {
                            br.close();
                        }
                        stream.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("unknown format: " + format);
            }
            return bundle;
        }
    }
}
