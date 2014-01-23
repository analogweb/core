package org.analogweb;

/**
 * {@link Invocation}に適用されるメタデータを保持します。
 * @author snowgoose
 */
public interface InvocationMetadata {

    /**
     * 実行対象のエントリポイントを保持する{@link Class}を取得します。
     * @return 実行対象のエントリポイントを保持する{@link Class}
     */
    Class<?> getInvocationClass();

    /**
     * 実行対象のエントリポイントのメソッド名を取得します。
     * @return 実行対象のエントリポイントのメソッド名
     */
    String getMethodName();

    /**
     * 実行対象のエントリポイントのメソッドの引数の型を取得します。
     * @return 実行対象のエントリポイントのメソッドの引数の型
     */
    Class<?>[] getArgumentTypes();

    /**
     * このエントリポイントがマッピングされているパスを表す{@link RequestPathMetadata}を取得します。
     * @return このエントリポイントがマッピングされているパスを表す{@link RequestPathMetadata}
     */
    RequestPathMetadata getDefinedPath();
}
