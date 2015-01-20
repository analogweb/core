package org.analogweb.util.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Configurable class for java.util.logging without logging.properties.
 * @author snowgoose
 */
public abstract class JulLogConfig {

    /**
     * Configuration for creating {@link Logger} rely on default JVM settings.
     */
    public static final JulLogConfig SIMPLE = new JulLogConfig() {

        @Override
        public void configure(ClassLoader classLoader) {
            // nop.
        }

        @Override
        public Logger createLogger(String name) {
            return Logger.getLogger(name);
        }
    };

    public JulLogConfig() {
        // nop.
    }

    public void configure(ClassLoader classLoader) {
        LogManager manager = LogManager.getLogManager();
        resetLogManager(manager);
        configureInternal(manager);
    }

    protected void configureInternal(LogManager manager) {
        // only reset LogManager.
    }

    protected void resetLogManager(LogManager manager) {
        manager.reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        for (Handler handler : globalLogger.getHandlers()) {
            globalLogger.removeHandler(handler);
        }
    }

    public Logger createLogger(String name) {
        LogManager manager = LogManager.getLogManager();
        return createLoggerInternal(name, manager);
    }

    protected Logger createLoggerInternal(String name, LogManager manager) {
        Logger logger = Logger.getLogger(name);
        logger.setLevel(Level.INFO);
        logger.addHandler(createConsoleHandler());
        manager.addLogger(logger);
        return logger;
    }

    private ConsoleHandler createConsoleHandler() {
        ConsoleHandler console = new ConsoleHandler();
        console.setFormatter(new JulLogFormatter());
        console.setLevel(Level.INFO);
        return console;
    }
}
