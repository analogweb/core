package org.analogweb.util.logging;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.analogweb.util.logging.JulLogFormatter;
import org.junit.Before;
import org.junit.Test;

public class JulLogFormatterTest extends JulLogFormatter {

    private JulLogFormatter formatter;

    @Before
    public void setUp() throws Exception {
        formatter = new JulLogFormatter();
    }

    @Test
    public void test() throws Exception {
        Date currentTime = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse("2012/01/22 10:01:11");
        LogRecord record = new LogRecord(Level.INFO, "this is test message.");
        record.setLoggerName("testLogger");
        record.setMillis(currentTime.getTime());
        record.setThreadID(10);
        String actual = formatter.format(record);
        assertThat(actual, is("2012-01-22 10:01:11.000 testLogger INFO {10} - this is test message.\n"));
    }
}
