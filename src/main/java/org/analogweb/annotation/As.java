package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * リクエストメソッドおよびその結果として表示されるビューのスコープ内で操作可能な属性を表す注釈です。<br/>
 * {@link Route}が付与されたメソッドの引数に付与する事で、指定された属性名を持つ、スコープに格納
 * されている値を取得する事ができます。
 * @author snowgoose
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER,ElementType.ANNOTATION_TYPE })
public @interface As {

    /**
     * 属性名を設定します。
     * @return 属性名
     */
    String value() default "";

}
