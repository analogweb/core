package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * リクエストされたエンティティにするフォーマット（メディアタイプ）を指定します。<br/>
 * リクエストが指定されたフォーマットに該当しない場合は、
 * {@link org.analogweb.core.response.HttpStatus#UNSUPPORTED_MEDIA_TYPE}
 * が返されます。
 * @author snowgoose
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,ElementType.TYPE })
public @interface RequestFormats {

    /**
     * リクエストを受け付けるフォーマット（メディアタイプを表す文字列）
     * を指定します。何も指定しない場合は、引数のオブジェクトがサポート
     * するフォーマットにより、リクエストの適合性が判定されます。
     * @return リクエストを受け付ける全てのフォーマット（メディアタイプを表す文字列）
     */
    String[] value() default {};

}
