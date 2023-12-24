package org.analogweb.util.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author snowgoose
 */
public class JulLogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        String currentTime = dateFormat.format(new Date(record.getMillis()));
        return String.format("%s %s %s {%s} - %s\n", currentTime, record.getLoggerName(), record.getLevel(),
                record.getThreadID(), record.getMessage());
    }
}
