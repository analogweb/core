package org.analogweb;

/**
 * Multiple part request {@link Parameters}.
 * 
 * @author snowgooseyk
 * @param <T>
 */
public interface MultipartParameters<T extends Multipart>
		extends
			Iterable<T>,
			Parameters {

	T[] getMultiparts(String name);
}
