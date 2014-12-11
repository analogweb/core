package org.analogweb;

import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * アプリケーションインスタンスで共有される唯一のプロパティです。<br/>
 * 通常はアプリケーションインスタンス生成時に初期化され、その後プロパティの値が
 * 変更される事はありません。
 * @author snowgoose
 */
public interface ApplicationProperties {

    String PACKAGES = "analogweb.packages";
    String TEMP_DIR = "analogweb.templory.directory";
    String LOCALE = "analogweb.default.locale";

    Collection<String> getComponentPackageNames();

    File getTempDir();

    Locale getDefaultClientLocale();

    Map<String, Object> getProperties();

    String getStringProperty(String key);
}
