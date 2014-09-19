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
     * アプリケーション全般で利用可能なテンポラリディレクトリ
     * を取得します。
     * @return テンポラリディレクトリを指し示す{@link File}
     */
    File getTempDir();

    /**
     * アプリケーションに対するリクエストに適用される
     * 既定の{@link Locale}を取得します。
     * @return テンポラリディレクトリを指し示す{@link File}
     */
    Locale getDefaultClientLocale();
}
