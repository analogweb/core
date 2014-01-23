package org.analogweb;

/**
 * {@link ContainerAdaptor}を生成するファクトリです。
 * @param <T> 生成する{@link ContainerAdaptor}の型
 * @author snowgoose
 */
public interface ContainerAdaptorFactory<T extends ContainerAdaptor> extends Module {

    /**
     * 新しい{@link ContainerAdaptor}を生成します。
     * @param resolver {@link ApplicationContext}
     * @return 新しい{@link ContainerAdaptor}のインスタンス。
     */
    T createContainerAdaptor(ApplicationContext resolver);
}
