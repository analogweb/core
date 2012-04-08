package org.analogweb;

import javax.servlet.ServletContext;

/**
 * {@link ContainerAdaptor}を生成するファクトリです。
 * @param <T> 生成する{@link ContainerAdaptor}の型
 * @author snowgoose
 */
public interface ContainerAdaptorFactory<T extends ContainerAdaptor> extends Module {

    /**
     * 新しい{@link ContainerAdaptor}を生成します。
     * @param servletContext {@link ServletContext}
     * @return 新しい{@link ContainerAdaptor}のインスタンス。
     */
    T createContainerAdaptor(ServletContext servletContext);

}
