package org.analogweb.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author snowgooseyk
 */
public class CountingOutputStream extends FilterOutputStream {

	private long count = 0L;

	public CountingOutputStream(OutputStream out) {
		super(out);
	}

	public void write(int b) throws IOException {
		super.write(b);
		count++;
	}

	public long getCount() {
		return this.count;
	}
}
