package org.analogweb.core;

import java.nio.charset.Charset;

import org.analogweb.ResponseEntity;

/**
 * @author y2k2mt
 */
public class DefaultResponseEntity implements ResponseEntity<byte[]> {

	private byte[] body;
	private int length;

	public DefaultResponseEntity(String entity) {
		this(entity, Charset.defaultCharset());
	}

	public DefaultResponseEntity(String entity, Charset charset) {
		this(entity.getBytes(charset));
	}

	public DefaultResponseEntity(byte[] entity) {
		this.body = entity;
		this.length = entity.length;
	}

	public byte[] entity() {
		return this.body;
	}

	@Override
	public long getContentLength() {
		if (this.length < 0) {
			length = -1;
		}
		return length;
	}
}
