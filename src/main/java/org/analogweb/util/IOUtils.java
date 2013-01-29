package org.analogweb.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author snowgoose
 */
public final class IOUtils {

    private static final Log log = Logs.getLog(IOUtils.class);

    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            // nop.
            log.log("DU000007", e, closeable);
        }
    }

    public static int copyQuietly(InputStream input, OutputStream output) {
        try {
            return copyInternal(new BufferedInputStream(input), new BufferedOutputStream(output));
        } catch (IOException e) {
            log.log("DU000008", e, input);
            return -1;
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        return copyInternal(new BufferedInputStream(input), new BufferedOutputStream(output));
    }

    private static int copyInternal(InputStream input, OutputStream output) throws IOException {
        Assertion.notNull(input, InputStream.class.getName());
        Assertion.notNull(output, OutputStream.class.getName());
        try {
            int count = 0;
            int i;
            while ((i = input.read()) != -1) {
                output.write(i);
                count++;
            }
            output.flush();
            return count;
        } finally {
            closeQuietly(input);
        }
    }

    public static String toString(Reader reader) throws IOException {
        return toString(reader, 4096);
    }

    public static String toString(Reader reader, int bufferSize) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[bufferSize];
        int n = 0;
        while (-1 != (n = reader.read(buffer))) {
            sb.append(buffer, 0, n);
        }
        return sb.toString();
    }

}
