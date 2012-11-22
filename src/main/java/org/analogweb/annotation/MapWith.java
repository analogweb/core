package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.analogweb.TypeMapper;

/**
 * リクエストメソッドにおいて、パラメータとして指定される値を
 * 特定の型に変換するルールを指定します。
 * @author snowgoose
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER,ElementType.ANNOTATION_TYPE })
public @interface MapWith {

    /**
     * 取得した値を特定の型に変換するルール({@link TypeMapper})を設定します。
     * @return {@link TypeMapper}
     */
    Class<? extends TypeMapper> value() default TypeMapper.class;

}
