package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.analogweb.RequestValueResolver;

/**
 * 値を取得可能な特定の{@link RequestValueResolver}を指定します。<br/>
 * @author snowgoose
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER,ElementType.ANNOTATION_TYPE })
public @interface Resolver {

	Class<? extends RequestValueResolver> value();

}
