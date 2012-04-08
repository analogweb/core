package org.analogweb.util.logging;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JulLogConfigTest extends JulLogConfig {

    private JulLogConfig config;
    private Logger grobalLogger;

    @Before
    public void setUp() throws Exception {
        config = new JulLogConfig() {
        };
        grobalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    @After
    public void tearDown() {
        LogManager.getLogManager().addLogger(grobalLogger);
    }

    @Test
    public void testConfigure() {
        config.configure(Thread.currentThread().getContextClassLoader());
        Logger actual = config.createLogger(getClass().getCanonicalName());

        // assert default configuration.
        assertThat(actual.getLevel(), is(Level.INFO));
        assertThat(actual.getHandlers().length, is(1));
        assertThat(ConsoleHandler.class.isInstance(actual.getHandlers()[0]), is(true));
        ConsoleHandler handler = (ConsoleHandler) actual.getHandlers()[0];
        assertThat(handler.getLevel(), is(Level.INFO));
        assertThat(JulLogFormatter.class.isInstance(handler.getFormatter()), is(true));
    }

}
