package org.analogweb;

/**
 * 内部的な状態を破棄する事が可能なコンポーネントを表します。
 * @author snowgoose
 */
public interface Disposable {

    /**
     * 内部的な状態やリソースを全て破棄します。
     */
    void dispose();
}
