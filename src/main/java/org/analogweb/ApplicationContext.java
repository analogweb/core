package org.analogweb;

/**
 * アプリケーションインスタンスで唯一のコンテキストから
 * キーを以ってコンテキストが保持しているオブジェクトを
 * 取得するコンポーネントです。
 * @author snowgoose
 */
public interface ApplicationContext {

    /**
     * アプリケーションインスタンスで唯一のコンテキストに
     * よって保持されているオブジェクトを取得します。
     * @param requiredType 取得する対象のオブジェクトの型
     * @param contextKey オブジェクトを取得する為のキー
     * @return コンテキストによって保持されているオブジェクト
     */
    <T> T getAttribute(Class<T> requiredType, String contextKey);

}
