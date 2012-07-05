package org.analogweb;

/**
 * リクエストされた{@link RequestPathMetadata}です。
 * @author snowgoose
 */
public interface RequestPath extends RequestPathMetadata {

    /**
     * リクエストされたパスの一部が指定した識別子と合致する
     * （このコンポーネントが扱うべきリクエストであると判別される）場合は{@code false}を返します。
     * 合致しない(このコンポーネントが扱うべきリクエストでない)場合は{@code true}を返します。
     * @param specifier 識別子
     * @return このコンポーネントが扱うべきリクエストでない場合は{@code true}
     */
    boolean pathThrowgh(String specifier);

    /**
     * リクエストされたパスを実行するメソッドを取得します。
     * @return リクエストされたパスを実行するメソッド
     */
    String getMethod();

}
