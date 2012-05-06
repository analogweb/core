package org.analogweb;

/**
 * 複数のコンポーネントをアプリケーションに定義可能な{@link Module}です。
 * @author snowgoose
 */
public interface MultiModule extends Module {

    /**
     * 定義した{@link MultiModule}から、実行するインスタンスを
     * 選別するフィルタです。
     * @author snowgoose
     */
    static interface Filter {
        void doFilter(Class<? extends MultiModule> clazz);
    }

}
