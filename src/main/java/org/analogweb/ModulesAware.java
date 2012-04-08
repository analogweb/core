package org.analogweb;

/**
 * {@link Modules}を保持しているコンポーネントを表します。
 * @author snowgoose
 */
public interface ModulesAware {

    /**
     * {@link Modules}を設定します。
     * @param modules {@link Modules}
     */
    void setModules(Modules modules);

}
