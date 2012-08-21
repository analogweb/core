package org.analogweb;

/**
 * 複数のコンポーネントをアプリケーションに定義可能な{@link Module}です。
 * @author snowgoose
 */
public interface MultiModule extends Module {
    
    interface Filter {
        <T extends MultiModule> boolean isAppreciable(T aMultiModule);
    }

}
