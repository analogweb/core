package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 値を取得可能な特定のスコープを指定します。<br/>
 * 値を特定のスコープから取得する際に使用する、
 * {@link org.analogweb.AttributesHandler}を特定するキーになります。
 * @author snowgoose
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER,ElementType.ANNOTATION_TYPE })
public @interface Scope {

    /**
     * 値を取得するスコープを設定します。
     * @return 値を設定するスコープ
     */
    String value() default "";

}
