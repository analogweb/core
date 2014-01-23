package org.analogweb.util;

import java.net.URL;
import java.util.Collection;

/**
 * 指定したリソース({@link URL})からクラスを収集するユーティリティです。
 * @author snowgoose
 */
public interface ClassCollector {

    /**
     * 指定したリソース({@link URL})から、{@link ClassLoader}が
     * ロード可能な全てのクラスリソースを収集します。<br/>
     * この時、{@link URL}は必ずクラスパスのルートを示し、パッケージ
     * 階層を表すパスを含む事はできません。
     * （jarファイルであれば、jarファイルそのものを指すURL、
     * ファイルであれば、クラスパスが通じたルートディレクトリ
     * を表すURL、等）<br/>
     * 取得可能なクラスが存在しない場合は空の{@link Collection}
     * を返します。
     * @param source {@link URL}
     * @param classLoader {@link ClassLoader}
     * @return リソースからロード可能な全ての{@link Class}
     */
    Collection<Class<?>> collect(URL source, ClassLoader classLoader);

    /**
     * 指定したリソース({@link URL})から、{@link ClassLoader}が
     * ロード可能であり、且つ指定したパッケージ名に属するクラス
     * リソースを収集します。<br/>
     * 取得可能なクラスが存在しない場合は空の{@link Collection}
     * を返します。
     * @param packageName {@link Class}を取得する対象のパッケージ名
     * @param source {@link URL}
     * @param classLoader {@link ClassLoader}
     * @return リソースからロード可能な全ての{@link Class}
     */
    Collection<Class<?>> collect(String packageName, URL source, ClassLoader classLoader);
}
