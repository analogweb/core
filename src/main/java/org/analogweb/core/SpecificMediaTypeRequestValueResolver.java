package org.analogweb.core;

import org.analogweb.AttributesHandler;
import org.analogweb.MediaType;
import org.analogweb.RequestValueResolver;

/**
 * 値の取得時に、特定のメディアタイプを指定する
 * {@link AttributesHandler}です。
 * @author snowgoose
 */
public interface SpecificMediaTypeRequestValueResolver extends RequestValueResolver {

    /**
     * 指定された{@link MediaType}が、このコンポーネントで
     * 値が取得可能であるときに{@code true}を返します。
     * @param mediaType {@link MediaType}
     * @return 値が取得可能な場合は{@code true}
     */
    boolean supports(MediaType mediaType);

}
