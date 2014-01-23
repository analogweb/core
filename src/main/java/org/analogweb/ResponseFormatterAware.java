package org.analogweb;

/**
 * {@link ResponseFormatter}が適用可能な{@link Renderable}である事を
 * 表します。
 * @author snowgoose
 */
public interface ResponseFormatterAware<T extends Renderable> extends Renderable {

    /**
     * {@link ResponseFormatter}を適用します。
     * @param formatter {@link ResponseFormatter}
     * @return フォーマッタが適用された{@link Renderable}の自身のインスタンス
     */
    T attach(ResponseFormatter formatter);
}
