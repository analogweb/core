package org.analogweb;

import java.util.Collection;

/**
 * {@link InvocationMetadata}を生成するファクトリです。<br/>
 * クラス等のメタデータから、{@link InvocationMetadata}を生成する方法を定義します。
 * {@link InvocationMetadata}はアプリケーションの初期化時に生成され
 * 対応するパス({@link RequestPathMetadata})と共にマッピングされます。
 * @author snowgoose
 */
public interface InvocationMetadataFactory extends MultiModule {

    /**
     * 指定されたクラスがリクエストによって起動可能なメソッドを含む場合は{@code true}を返します。
     * @param clazz 任意の型
     * @return 指定されたクラスがリクエストによって起動可能なメソッドを含む場合は{@code true}
     */
    boolean containsInvocationClass(Class<?> clazz);

    /**
     * 新しい{@link InvocationMetadata}のインスタンスを生成します。
     * @param clazz 任意の型
     * @return {@link InvocationMetadata}
     */
    Collection<InvocationMetadata> createInvocationMetadatas(Class<?> clazz);

}
