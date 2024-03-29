package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.analogweb.core.ParameterValueResolver;
import org.analogweb.util.StringUtils;

/**
 * Resolves query ,form, matrix parameter values.
 *
 * @see ParameterValueResolver
 *
 * @author snowgooseyk
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Resolver(ParameterValueResolver.class)
@Valiables
public @interface Param {

    String value() default StringUtils.EMPTY;
}
