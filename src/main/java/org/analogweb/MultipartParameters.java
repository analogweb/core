package org.analogweb;

/**
 * マルチパートリクエスト時に生成される{@link Parameters}です。
 * @author snowgooseyk
 * @param <T>
 */
public interface MultipartParameters<T extends Multipart> extends Iterable<T>,
		Parameters {

	T[] getMultiparts(String name);

}
