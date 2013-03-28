package org.analogweb.core;

import java.lang.annotation.Annotation;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapperContext;

/**
 * {@link Annotation}に定義されている情報から、適用される
 * 値を解決するユーティリティです。<br/>
 * 例えば、エントリポイントのパラメータから抽出された{@link Annotation}
 * に定義されている情報から、パラメータに適用される値を解決します。
 * @author snowgoose
 */
public interface AnnotatedInvocationParameterValueResolver {

    /**
     * 指定された{@link Annotation}から値を解決します。
     * @param parameterAnnotations 走査対象の{@link Annotation}
     * @param argType 解決される値として期待される{@link Class}
     * @param context {@link RequestContext}
     * @param metadata {@link InvocationMetadata}
     * @param converters {@link TypeMapperContext}
     * @param handlers {@link RequestValueResolvers}
     * @return 解決された値
     */
    <T> T resolve(Annotation[] parameterAnnotations, Class<T> argType, RequestContext context,
            InvocationMetadata metadata, TypeMapperContext converters, RequestValueResolvers handlers);

}
