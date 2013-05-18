package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * リクエストされたパスによって起動される、エントリポイントである事を表す注釈です。<br/>
 * {@link #value()}属性値により、起動されるエントリポイント(メソッド)であるパスを指定します。
 * @author snowgoose
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,ElementType.TYPE })
public @interface Route {

    /**
     * リクエストメソッドを起動するパスを設定します。
     * @return リクエストメソッドを起動するパス
     */
    String value() default "";

}
