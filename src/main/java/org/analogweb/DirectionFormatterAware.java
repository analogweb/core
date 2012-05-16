package org.analogweb;

/**
 * {@link DirectionFormatter}が適用可能な{@link Direction}である事を
 * 表します。
 * @author snowgoose
 */
public interface DirectionFormatterAware<T extends Direction> extends Direction {
    
    /**
     * {@link DirectionFormatter}を適用します。
     * @param formatter {@link DirectionFormatter}
     * @return フォーマッタが適用された{@link Direction}の自身のインスタンス
     */
    T attach(DirectionFormatter formatter);

}
