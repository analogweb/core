package org.analogweb;

import java.util.Map;

/**
 * リクエストされたエントリポイントとなるメソッド(実行する対象)を表すコンポーネントです。<br/>
 * このコンポーネントはアクションメソッドの実行毎にインスタンスが生成され、 
 * エンドポイントとなるメソッドが存在するオブジェクトのインスタンスや、エンドポイントに
 * 適用される引数等の状態を持ちます。
 * @author snowgoose
 */
public interface Invocation {

    /**
     * リクエストされたエンドポイントとなるメソッドを実行します。<br/>
     * {@link InvocationProcessor}により評価された内容を保持した状態で実行されます。
     * @see InvocationProcessor
     * @return エンドポイントの実行結果
     */
    Object invoke();

    /**
     * エンドポイントとなるメソッドに適用される引数を取得します。<br/>
     * キーは引数の索引であり、値は引数となるインスタンスです。
     * @return エンドポイントとなるメソッドに適用される引数を保持する{@link Map}
     */
    Map<Integer, Object> getPreparedArgs();

    /**
     * エントリポイントとして実行されるインスタンスを返します。
     * @return エントリポイントとして実行されるインスタンス
     */
    Object getInvocationInstance();

    /**
     * エンドポイントとなるメソッドの引数に適用される値を設定します。
     * @param index メソッドの引数に一致する(0から始まる)索引
     * @param arg エンドポイントとなるメソッドの引数として適用する値
     */
    void putPreparedArg(int index, Object arg);

}
