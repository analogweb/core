package org.analogweb;

import java.util.Map;

/**
 * {@link RequestAttributes}を生成するファクトリです。
 * @author snowgoose
 */
public interface RequestAttributesFactory extends Module {

    /**
     * 新しい{@link RequestAttributes}のインスタンスを生成します。
     * @param resolvers スコープ名をキーとした{@link AttributesHandler}のマップ
     * @param metadata {@link InvocationMetadata}
     * @return {@link RequestAttributes}
     */
    RequestAttributes createRequestAttributes(Map<String, AttributesHandler> resolvers,InvocationMetadata metadata);

}
