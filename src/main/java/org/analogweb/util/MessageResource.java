package org.analogweb.util;

import java.util.Locale;

/**
 * @author snowgoose
 */
public interface MessageResource {

	/**
	 * 指定されたキーに一致するメッセージを取得する。
	 * 
	 * @param key
	 *            メッセージキー
	 * @return メッセージキーに一致するメッセージ
	 */
	String getMessage(String key);

	/**
	 * 指定されたキーに一致するメッセージを取得する。
	 * 
	 * @param key
	 *            メッセージキー
	 * @param args
	 *            メッセージ引数
	 * @return メッセージキーに一致するメッセージ
	 */
	String getMessage(String key, Object... args);

	/**
	 * 指定されたキーに一致するメッセージを取得する。
	 * 
	 * @param key
	 *            メッセージキー
	 * @param locale
	 *            {@link Locale}
	 * @return メッセージキーに一致するメッセージ
	 */
	String getMessage(String key, Locale locale);

	/**
	 * 指定されたキーに一致するメッセージを取得する。
	 * 
	 * @param key
	 *            メッセージキー
	 * @param locale
	 *            {@link Locale}
	 * @param args
	 *            メッセージ引数
	 * @return メッセージキーに一致するメッセージ
	 */
	String getMessage(String key, Locale locale, Object... args);
}
