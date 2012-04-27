package org.analogweb.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	public static int copy(InputStream input, OutputStream output) {
		return copy(new BufferedInputStream(input), new BufferedOutputStream(
				output));
	}

	public static int copy(BufferedInputStream input,
			BufferedOutputStream output) {
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
		} catch (IOException e) {
			log.log("DU000008", e, input);
			return -1;
		} finally {
			closeQuietly(input);
		}
	}
}
