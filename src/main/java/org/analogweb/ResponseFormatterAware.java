package org.analogweb;

/**
 * @author snowgoose
 */
public interface ResponseFormatterAware<T extends Renderable>
		extends
			Renderable {

	T attach(ResponseFormatter formatter);
}
