package org.analogweb.util.logging;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import org.analogweb.util.MessageResource;
import org.analogweb.util.logging.Markers.SimpleMarker;
import org.junit.After;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class LogsTest {

    private Log log;

    void loadConfig(String logLevel) throws Exception {
        LogManager manager = LogManager.getLogManager();
        manager.reset();
        Properties props = new Properties();
        props.put("handlers",
                LogsTest.class.getCanonicalName() + "$StubHandler," + ConsoleHandler.class.getCanonicalName());
        props.put(".level", logLevel);
        props.put(LogsTest.class.getCanonicalName() + "StubHandler.level", logLevel);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store(out, "");
        InputStream ins = new ByteArrayInputStream(out.toByteArray());
        manager.readConfiguration(ins);
        StubHandler.clearRecord();
    }

    @After
    public void tearDown() {
        LogManager.getLogManager().reset();
        StubHandler.clearRecord();
    }

    @Test
    public void testGetDefaultLogInstance() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class);
        assertTrue(log instanceof JulLog);
    }

    @Test
    public void testLevelsLog() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class);
        log.trace(SimpleMarker.valueOf("marker"), "trace message!");
        LogRecord actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINEST));
        assertThat(actual.getMessage(), is("trace message!"));
        StubHandler.clearRecord();
        log.debug(SimpleMarker.valueOf("marker"), "debug message!");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINE));
        assertThat(actual.getMessage(), is("debug message!"));
        StubHandler.clearRecord();
        log.info(SimpleMarker.valueOf("marker"), "info message!");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.INFO));
        assertThat(actual.getMessage(), is("info message!"));
        StubHandler.clearRecord();
        log.warn(SimpleMarker.valueOf("marker"), "warn message!");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.WARNING));
        assertThat(actual.getMessage(), is("warn message!"));
        StubHandler.clearRecord();
        log.error(SimpleMarker.valueOf("marker"), "error message!");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.SEVERE));
        assertThat(actual.getMessage(), is("error message!"));
    }

    @Test
    public void testLevelsLogWithArgs() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class);
        log.trace(SimpleMarker.valueOf("marker"), "log message!", "trace");
        LogRecord actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINEST));
        assertThat(actual.getMessage(), is("log message!"));
        assertThat(actual.getParameters()[0].toString(), is("trace"));
        StubHandler.clearRecord();
        log.debug(SimpleMarker.valueOf("marker"), "log message!", "debug");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINE));
        assertThat(actual.getMessage(), is("log message!"));
        assertThat(actual.getParameters()[0].toString(), is("debug"));
        StubHandler.clearRecord();
        log.info(SimpleMarker.valueOf("marker"), "log message!", "info");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.INFO));
        assertThat(actual.getMessage(), is("log message!"));
        assertThat(actual.getParameters()[0].toString(), is("info"));
        StubHandler.clearRecord();
        log.warn(SimpleMarker.valueOf("marker"), "log message!", "warn");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.WARNING));
        assertThat(actual.getMessage(), is("log message!"));
        assertThat(actual.getParameters()[0].toString(), is("warn"));
        StubHandler.clearRecord();
        log.error(SimpleMarker.valueOf("marker"), "log message!", "error");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.SEVERE));
        assertThat(actual.getMessage(), is("log message!"));
        assertThat(actual.getParameters()[0].toString(), is("error"));
    }

    @Test
    public void testLog() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class);
        log.log("T001");
        LogRecord actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINEST));
        assertThat(actual.getMessage(), is("T001"));
        StubHandler.clearRecord();
        log.log("D001");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINE));
        assertThat(actual.getMessage(), is("D001"));
        StubHandler.clearRecord();
        log.log("I001");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.INFO));
        assertThat(actual.getMessage(), is("I001"));
        StubHandler.clearRecord();
        log.log("W001");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.WARNING));
        assertThat(actual.getMessage(), is("W001"));
        StubHandler.clearRecord();
        log.log("E001");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.SEVERE));
        assertThat(actual.getMessage(), is("E001"));
    }

    @Test
    public void testLogUntilWarnDisabled() throws Exception {
        loadConfig("WARNING");
        log = Logs.getLog(LogsTest.class);
        StubHandler.clearRecord();
        log.log("T001");
        LogRecord actual = StubHandler.actualRecord();
        assertNull(actual);
        StubHandler.clearRecord();
        log.log("D001");
        actual = StubHandler.actualRecord();
        assertNull(actual);
        StubHandler.clearRecord();
        log.log("I001");
        actual = StubHandler.actualRecord();
        assertNull(actual);
        StubHandler.clearRecord();
        log.log("W001");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.WARNING));
        assertThat(actual.getMessage(), is("W001"));
        StubHandler.clearRecord();
        log.log("E001");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.SEVERE));
        assertThat(actual.getMessage(), is("E001"));
    }

    @Test
    public void testLogUntilAllDisabled() throws Exception {
        loadConfig("OFF");
        log = Logs.getLog(LogsTest.class);
        StubHandler.clearRecord();
        log.log("T001");
        LogRecord actual = StubHandler.actualRecord();
        assertNull(actual);
        StubHandler.clearRecord();
        log.log("D001");
        actual = StubHandler.actualRecord();
        assertNull(actual);
        StubHandler.clearRecord();
        log.log("I001");
        actual = StubHandler.actualRecord();
        assertNull(actual);
        StubHandler.clearRecord();
        log.log("W001");
        actual = StubHandler.actualRecord();
        assertNull(actual);
        StubHandler.clearRecord();
        log.log("E001");
        actual = StubHandler.actualRecord();
        assertNull(actual);
    }

    @Test
    public void testLogWithThrowable() throws Exception {
        Throwable th = new IllegalStateException();
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class);
        log.log(SimpleMarker.valueOf("marker"), "T001", th);
        LogRecord actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINEST));
        assertThat(actual.getMessage(), is("T001"));
        assertThat(actual.getThrown(), is(th));
        StubHandler.clearRecord();
        log.log("D001", th, "debug!");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.FINE));
        assertThat(actual.getMessage(), is("D001"));
        assertThat(actual.getThrown(), is(th));
        StubHandler.clearRecord();
        log.log("I001", th);
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.INFO));
        assertThat(actual.getMessage(), is("I001"));
        assertThat(actual.getThrown(), is(th));
        StubHandler.clearRecord();
        log.log(SimpleMarker.valueOf("marker"), "W001", th, "warn!");
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.WARNING));
        assertThat(actual.getMessage(), is("W001"));
        assertThat(actual.getThrown(), is(th));
        StubHandler.clearRecord();
        log.log(SimpleMarker.valueOf("marker"), "E001", th);
        actual = StubHandler.actualRecord();
        assertThat(actual.getLevel(), is(Level.SEVERE));
        assertThat(actual.getMessage(), is("E001"));
        assertThat(actual.getThrown(), is(th));
    }

    @Test
    public void testDistinctCustomLogger() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class.getName(), Thread.currentThread().getContextClassLoader(),
                StubLogImpl.class.getName());
        assertTrue(log instanceof StubLogImpl);
    }

    @Test
    public void testNoExistsCustomLogger() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class.getName(), Thread.currentThread().getContextClassLoader(),
                "not.avairable.logClass");
        assertTrue(log instanceof JulLog);
    }

    @Test
    public void testNoCustomLogger() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class.getName(), Thread.currentThread().getContextClassLoader(),
                AbstractLog.class.getName());
        assertTrue(log instanceof JulLog);
    }

    @Test
    public void testNoLogCustomLogger() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class.getName(), Thread.currentThread().getContextClassLoader(),
                LogsTest.class.getName());
        assertTrue(log instanceof JulLog);
    }

    @Test
    public void testNoConstractorCustomLogger() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class.getName(), Thread.currentThread().getContextClassLoader(),
                NoConstractorLogImpl.class.getName());
        assertTrue(log instanceof JulLog);
    }

    @Test
    public void testNotInstanticatableCustomLogger() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class.getName(), Thread.currentThread().getContextClassLoader(),
                NoInstanticatableLogImpl.class.getName());
        assertTrue(log instanceof JulLog);
    }

    @Test
    public void testFailInstanticatableCustomLogger() throws Exception {
        loadConfig("FINEST");
        log = Logs.getLog(LogsTest.class.getName(), Thread.currentThread().getContextClassLoader(),
                FailInstanticatableLogImpl.class.getName());
        assertTrue(log instanceof JulLog);
    }

    public static class NoInstanticatableLogImpl extends StubLogImpl {

        public NoInstanticatableLogImpl() {
            super("", null);
        }
    }

    public static class FailInstanticatableLogImpl extends StubLogImpl {

        public FailInstanticatableLogImpl(String name, ClassLoader l) {
            super(name, l);
            throw new RuntimeException();
        }
    }

    public static class StubLogImpl extends AbstractLog {

        public StubLogImpl(String name, ClassLoader classLoader) {
            super(name, classLoader);
        }

        @Override
        public void trace(String message) {
            // nop.
        }

        @Override
        public void trace(String message, Object... args) {
            // nop.
        }

        @Override
        public void trace(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void trace(Marker marker, String message) {
            // nop.
        }

        @Override
        public void trace(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public void trace(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public boolean isTraceEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void debug(String message) {
            // nop.
        }

        @Override
        public void debug(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void debug(String message, Object... args) {
            // nop.
        }

        @Override
        public void debug(Marker marker, String message) {
            // nop.
        }

        @Override
        public void debug(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void debug(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isDebugEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void info(String message) {
            // nop.
        }

        @Override
        public void info(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void info(String message, Object... args) {
            // nop.
        }

        @Override
        public void info(Marker marker, String message) {
            // nop.
        }

        @Override
        public void info(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void info(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isInfoEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void warn(String message) {
            // nop.
        }

        @Override
        public void warn(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void warn(String message, Object... args) {
            // nop.
        }

        @Override
        public void warn(Marker marker, String message) {
            // nop.
        }

        @Override
        public void warn(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void warn(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isWarnEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void error(String message) {
            // nop.
        }

        @Override
        public void error(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void error(String message, Object... args) {
            // nop.
        }

        @Override
        public void error(Marker marker, String message) {
            // nop.
        }

        @Override
        public void error(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void error(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isErrorEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void log(MessageResource messageResource, String message) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, String message, Object... args) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, String message, Throwable t, Object... args) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, Marker marker, String message) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, Marker marker, String message, Throwable t, Object... args) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, Marker marker, String message, Object... args) {
            // nop.
        }
    }

    public static final class StubHandler extends Handler {

        private static LogRecord publishedRecord;

        @Override
        public void publish(LogRecord record) {
            publishedRecord = record;
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }

        static LogRecord actualRecord() {
            return publishedRecord;
        }

        static void clearRecord() {
            publishedRecord = null;
        }
    }

    public static final class NoConstractorLogImpl extends AbstractLog {

        public NoConstractorLogImpl() {
            super(null, null);
        }

        @Override
        public void trace(String message) {
            // nop.
        }

        @Override
        public void trace(String message, Object... args) {
            // nop.
        }

        @Override
        public void trace(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void trace(Marker marker, String message) {
            // nop.
        }

        @Override
        public void trace(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public void trace(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public boolean isTraceEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void debug(String message) {
            // nop.
        }

        @Override
        public void debug(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void debug(String message, Object... args) {
            // nop.
        }

        @Override
        public void debug(Marker marker, String message) {
            // nop.
        }

        @Override
        public void debug(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void debug(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isDebugEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void info(String message) {
            // nop.
        }

        @Override
        public void info(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void info(String message, Object... args) {
            // nop.
        }

        @Override
        public void info(Marker marker, String message) {
            // nop.
        }

        @Override
        public void info(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void info(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isInfoEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void warn(String message) {
            // nop.
        }

        @Override
        public void warn(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void warn(String message, Object... args) {
            // nop.
        }

        @Override
        public void warn(Marker marker, String message) {
            // nop.
        }

        @Override
        public void warn(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void warn(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isWarnEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void error(String message) {
            // nop.
        }

        @Override
        public void error(String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void error(String message, Object... args) {
            // nop.
        }

        @Override
        public void error(Marker marker, String message) {
            // nop.
        }

        @Override
        public void error(Marker marker, String message, Throwable throwable) {
            // nop.
        }

        @Override
        public void error(Marker marker, String message, Object... args) {
            // nop.
        }

        @Override
        public boolean isErrorEnabled() {
            // nop.
            return false;
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            // nop.
            return false;
        }

        @Override
        public void log(MessageResource messageResource, String message) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, String message, Object... args) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, String message, Throwable t, Object... args) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, Marker marker, String message) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, Marker marker, String message, Throwable t, Object... args) {
            // nop.
        }

        @Override
        public void log(MessageResource messageResource, Marker marker, String message, Object... args) {
            // nop.
        }
    }
}
