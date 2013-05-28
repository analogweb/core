package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.analogweb.core.HeaderValueResolver;
import org.analogweb.util.StringUtils;

/**
 * @author snowgooseyk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER,ElementType.ANNOTATION_TYPE })
@Resolver(HeaderValueResolver.class)
@Valiables
public @interface Header {
    
    String value() default StringUtils.EMPTY;

}
