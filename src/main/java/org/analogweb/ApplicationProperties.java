package org.analogweb;

import java.io.File;
import java.util.Collection;
import java.util.Locale;

/**
 * アプリケーションインスタンスで共有される唯一のプロパティです。<br/>
 * 通常はアプリケーションインスタンス生成時に初期化され、その後プロパティの値が
 * 変更される事はありません。
 * @author snowgoose
 */
public interface ApplicationProperties {
    
    /**
     * アプリケーションを構成するコンポーネントが存在する
     * すべてのパッケージ名を取得します。
     * @return すべてのパッケージ名
     */
    Collection<String> getComponentPackageNames();

    /**
     * アプリケーションに対するリクエストを特定する識別子を
     * 取得します。
     * @return 識別子
     */
    String getApplicationSpecifier();

    /**
     * アプリケーション全般で利用可能なテンポラリディレクトリ
     * を取得します。
     * @return テンポラリディレクトリを指し示す{@link File}
     */
    File getTempDir();

    Locale getDefaultClientLocale();
}
