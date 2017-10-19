package org.analogweb.core.response;

import java.net.URI;
import java.nio.charset.Charset;

import org.analogweb.ReadableBuffer;
import org.analogweb.ResponseEntity;
import org.analogweb.core.DefaultResponseEntity;

public class RenderableResponses
		extends
			BuildAndRenderableResponse<RenderableResponses> {

	private RenderableResponses() {
		// not instantiate.
	}

	public static RenderableResponses ok() {
		return new RenderableResponses().status(HttpStatus.OK);
	}

	public static RenderableResponses ok(ResponseEntity entity) {
		return ok().entity(entity);
	}

	public static RenderableResponses ok(String entity, Charset charset) {
		return ok(new DefaultResponseEntity(entity, charset));
	}

	public static RenderableResponses locates(URI location) {
		return new RenderableResponses().status(HttpStatus.FOUND).header(
				"Location", location.toString());
	}
}
