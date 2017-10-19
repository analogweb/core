package org.analogweb.core;

import org.analogweb.ReadableBuffer;
import org.analogweb.ResponseEntity;

public class ReadableBufferResponseEntity
		implements
			ResponseEntity<ReadableBuffer> {

	private ReadableBuffer entity;
	private long length;

	public ReadableBufferResponseEntity(ReadableBuffer in) {
		this.entity = in;
		this.length = in.getLength();
	}

	@Override
	public ReadableBuffer entity() {
		return this.entity;
	}

	@Override
	public long getContentLength() {
		return this.length;
	}
}
