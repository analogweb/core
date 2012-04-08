package org.analogweb;

/**
 * アプリケーションを表します。
 * @author snowgoose
 */
public interface Application extends Disposable {

    /**
     * アプリケーションを構成している{@link Modules}を取得します。
     * @return {@link Modules}
     */
    Modules getModules();

    /**
     * アプリケーションに定義されたエントリポイントの一覧を表す
     * {@link RequestPathMapping}を取得します。
     * @return {@link RequestPathMapping}
     */
    RequestPathMapping getRequestPathMapping();

    /**
     * アプリケーションに対するリクエストであることを識別する識別子を取得します。
     * @return アプリケーションに対するリクエストであることを識別する識別子
     */
    String getApplicationSpecifier();

}
