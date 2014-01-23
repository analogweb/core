package org.analogweb;

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
     * {@link ApplicationProcessor}により評価された内容を保持した状態で実行されます。
     * @see ApplicationProcessor
     * @return エンドポイントの実行結果
     */
    Object invoke();

    /**
     * エントリポイントとして実行されるインスタンスを返します。
     * @return エントリポイントとして実行されるインスタンス
     */
    Object getInvocationInstance();

    /**
     * エントリポイントの実行時に指定される{@link InvocationArguments}
     * を取得します。
     * @return {@link InvocationArguments}
     */
    InvocationArguments getInvocationArguments();
}
