package org.analogweb;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.analogweb.util.ClassCollector;

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
     * アプリケーションがレスポンスする既定の{@link Locale}を指定する指定するキー
     */
    String INIT_PARAMETER_APPLICATION_PROVISION_LOCALE = "application.locale";
    /**
     * アプリケーションを構成するコンポーネントが格納されている
     * 既定のパッケージ名
     */
    String DEFAULT_PACKAGE_NAME = Application.class.getPackage().getName();

    /**
     * 指定された{@link RequestPath}に対応する処理が実行された場合に
     * 返されるステータスです。
     */
    int PROCEEDED = 1;

    /**
     * 指定された{@link RequestPath}が存在しない場合に返される
     * ステータスです。
     */
    int NOT_FOUND = 0;

    /**
     * このアプリケーションインスタンスを起動します。
     * @param resolver {@link ApplicationContextResolver}
     * @param collectors {@link ClassCollector}
     * @param props {@link ApplicationProperties}
     * @param classLoader {@link ClassLoader}
     */
    void run(ApplicationContextResolver resolver, ApplicationProperties props,
            Collection<ClassCollector> collectors, ClassLoader classLoader);

    /**
     * {@link Application}に対する1つのリクエストを処理します。<br/>
     * このメソッドを実行する前に
     * {@link #run(ApplicationContextResolver, ApplicationProperties, Collection, ClassLoader)}
     * が実行され、{@link Application}が起動している必要があります。
     * @param path {@link RequestPath}
     * @param context {@link RequestContext}
     * @param responseContext {@link ResponseContext}
     * @throws IOException
     * @throws WebApplicationException
     */
    int processRequest(RequestPath path, RequestContext context, ResponseContext responseContext)
            throws IOException, WebApplicationException;

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
