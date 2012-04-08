package org.analogweb.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Test;

/**
 * @author snowgoose
 */
public class AnnotationUtilsTest {

    @Test
    public void testFindAnnotation() {
        SomeAnnotation actual = AnnotationUtils.findAnnotation(SomeAnnotation.class,
                SomeClass.class.getAnnotations());
        assertNotNull(actual);
    }

    @Test
    public void testFindAnnotationInclude() {
        SomeAnnotation actual = AnnotationUtils.findAnnotation(SomeAnnotation.class,
                SomeIncludeClass.class.getAnnotations());
        assertNotNull(actual);
    }

    @Test
    public void testFindAnnotationViaType() {
        SomeAnnotation actual = AnnotationUtils.findAnnotation(SomeAnnotation.class,
                SomeClass.class);
        assertNotNull(actual);
    }

    @Test
    public void testFindAnnotationViaIncludedClass() {
        SomeAnnotation actual = AnnotationUtils.findAnnotation(SomeAnnotation.class,
                SomeIncludeClass.class);
        assertNotNull(actual);
    }

    @Test
    public void testFindAnnotationViaImplementsClass() {
        SomeAnnotation actual = AnnotationUtils.findAnnotation(SomeAnnotation.class,
                SomeClassImpl.class);
        assertNotNull(actual);
    }

    @Test
    public void testFindAnnotationNotFound() {
        SomeAnnotation actual = AnnotationUtils.findAnnotation(SomeAnnotation.class,
                AnyClass.class.getAnnotations());
        assertNull(actual);
    }

    @Test
    public void testFindAnnotationNotFound2() {
        AnyAnnotation actual = AnnotationUtils.findAnnotation(AnyAnnotation.class,
                AnyClass.class.getAnnotations());
        assertNull(actual);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE,ElementType.TYPE})
    private @interface SomeAnnotation {
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @SomeAnnotation
    private @interface SomeAnnotationInclude {
        
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE,ElementType.TYPE})
    private @interface AnyAnnotation {
    }

    @SomeAnnotation
    private final class SomeClass {

    }

    @SomeAnnotation
    private interface SomeClassIf {

    }

    private final class SomeClassImpl implements SomeClassIf {

    }

    @SomeAnnotationInclude
    private final class SomeIncludeClass {

    }

    private final class AnyClass {

    }

}
