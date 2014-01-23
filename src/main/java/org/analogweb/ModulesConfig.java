package org.analogweb;

/**
 * {@link Modules}を構成するコンポーネントを定義する設定です。<br/>
 * 構成するコンポーネント（の型）を{@link ModulesBuilder}を通じて設定します。
 * @author snowgoose
 */
public interface ModulesConfig {

    /**
     * {@link Modules}を構成するコンポーネントを設定します。
     * @param builder 設定を行う{@link ModulesBuilder}
     * @return 設定を行った{@link ModulesBuilder}
     */
    ModulesBuilder prepare(ModulesBuilder builder);
}
