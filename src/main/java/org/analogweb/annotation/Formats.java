package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 取得した値を特定のフォーマットに変換する際のルールを表す文字列を設定します。<br/>
 * リクエストパラメータなどの文字列をエントリポイントにて特定の型に変換可能な
 * フォーマット文字列を指定します。
 * @author snowgoose
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface Formats {

    /**
     * 取得した値を特定のフォーマットに変換する際のルールを表す文字列を設定します。
     * @return 取得した値を特定のフォーマットに変換する際のルールを表す文字列
     */
    String[] value() default {};
}
