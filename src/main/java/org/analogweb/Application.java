package org.analogweb;

import java.io.IOException;

import org.analogweb.exception.WebApplicationException;

/**
 * アプリケーションを表します。
 * @author snowgoose
 */
public interface Application extends Disposable {

    /**
     * アプリケーションを構成するコンポーネントが格納されている
     * パッケージを指定するキー
     */
    String INIT_PARAMETER_ROOT_COMPONENT_PACKAGES = "application.packages";
    /**
     * アプリケーションに対するリクエストであることを特定する為の
     * 識別子を指定するキー
     */
    String INIT_PARAMETER_APPLICATION_SPECIFIER = "application.specifier";
    /**
     * アプリケーションが使用するテンポラリディレクトリを指定する指定するキー
     */
    String INIT_PARAMETER_APPLICATION_TEMPORARY_DIR = "application.tmpdir";
    /**
     * アプリケーションを構成するコンポーネントが格納されている
     * 既定のパッケージ名
     */
    String DEFAULT_PACKAGE_NAME = Application.class.getPackage().getName();

    /**
     * このアプリケーションインスタンスを起動します。
     * @param resolver {@link ApplicationContextResolver}
     * @param props {@link ApplicationProperties}
     * @param classLoader {@link ClassLoader}
     */
    void run(ApplicationContextResolver resolver, ApplicationProperties props,
            ClassLoader classLoader);

    void processRequest(RequestPath path, RequestContext context) throws IOException,
            WebApplicationException;

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
