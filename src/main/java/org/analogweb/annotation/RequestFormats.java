package org.analogweb.annotation;

/**
 * リクエストされたエンティティにするフォーマット（メディアタイプ）を指定します。<br/>
 * リクエストが指定されたフォーマットに該当しない場合は、
 * {@link org.analogweb.core.direction.HttpStatus.HttpStatus#UNSUPPORTED_MEDIA_TYPE}
 * が返されます。
 * @author snowgoose
 */
public @interface RequestFormats {
    String[] value();
}
