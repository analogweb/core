package org.analogweb;

/**
 * アプリケーションへの配備及び、アプリケーションからのロード時
 * に優先度が存在するコンポーネントであることを表します。<br/>
 * {@link #getPrecedence()}から取得される値によって、序列が決定
 * し。取得された値が高い方がより優先度が高くなります。優先度が
 * 高いコンポーネントは他のコンポーネントよりも先に評価、実行
 * されます。
 * @author snowgoose
 */
public interface Precedence {

    /**
     * 最も高い優先度を表す定数です。
     */
    int HIGHEST = Integer.MAX_VALUE;
    /**
     * 最も低い優先度を表す定数です。
     */
    int LOWEST = Integer.MIN_VALUE;

    /**
     * このコンポーネントの優先度を取得します。
     * @return このコンポーネントの優先度
     */
    int getPrecedence();

}
