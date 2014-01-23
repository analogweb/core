package org.analogweb;

/**
 * {@link Invocation#invoke()}実行前のパラメータを保持します。<br/>
 * 主に{@link ApplicationProcessor}によって、パラメータの値が決定されます。
 * @author snowgoose
 */
public interface InvocationArguments extends PreparedInvocationArguments {

    /**
     * エンドポイントとなるインスタンスを任意のオブジェクトに置き換えます。
     * @param newInvocationInstance エンドポイントとなるメソッドを持つインスタンス
     */
    void replace(Object newInvocationInstance);

    /**
     * エンドポイントとなるメソッドの引数に適用される値を設定します。
     * @param index メソッドの引数に一致する(0から始まる)索引
     * @param arg エンドポイントとなるメソッドの引数として適用する値
     */
    void putInvocationArgument(int index, Object arg);
}
