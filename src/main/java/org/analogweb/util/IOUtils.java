package org.analogweb.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.analogweb.util.logging.Log;
import org.analogweb.util.logging.Logs;

/**
 * @author y2k2mt
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
            return copy(input, output, 8192);
        } catch (IOException e) {
            log.log("DU000008", e, input);
            return -1;
        }
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, 8192);
    }

    public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        Assertion.notNull(input, InputStream.class.getName());
        Assertion.notNull(output, OutputStream.class.getName());
        try {
            byte[] buffer = new byte[bufferSize];
            int read = input.read(buffer, 0, bufferSize);
            int count = 0;
            while (read > -1) {
                output.write(buffer, 0, read);
                count += read;
                read = input.read(buffer, 0, bufferSize);
            }
            output.flush();
            return count;
        } finally {
            closeQuietly(input);
        }
    }

    public static String toString(InputStream in) throws IOException {
        return toString(new InputStreamReader(in));
    }

    public static String toString(Reader reader) throws IOException {
        return toString(reader, 4096);
    }

    public static String toString(Reader reader, int bufferSize) throws IOException {
        StringBuilder sb = new StringBuilder();
        CharBuffer buffer = CharBuffer.allocate(bufferSize);
        while (-1 != reader.read(buffer)) {
            buffer.flip();
            sb.append(buffer);
            buffer.clear();
        }
        return sb.toString();
    }

    public static final int DEFAULT_BUFFER_SIZE = 16 * 1024;

    public static void copy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        copy(src, dest, 16 * 1024);
    }

    public static void copy(final ReadableByteChannel src, final WritableByteChannel dest, final int bufferSize)
            throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }
}
