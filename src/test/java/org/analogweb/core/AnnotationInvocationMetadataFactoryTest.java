package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;


import org.analogweb.Direction;
import org.analogweb.InvocationMetadata;
import org.analogweb.InvocationMetadataFactory;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Delete;
import org.analogweb.annotation.Get;
import org.analogweb.annotation.On;
import org.analogweb.annotation.Post;
import org.analogweb.annotation.Put;
import org.analogweb.core.AnnotationInvocationMetadataFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AnnotationInvocationMetadataFactoryTest {

    private InvocationMetadataFactory factory;

    @Before
    public void setUp() {
        factory = new AnnotationInvocationMetadataFactory();
    }

    @Test
    public void testDetectAnnotatedAction() throws Exception {
        Class<?> actionsClass = FooActions.class;
        Method actionMethod = FooActions.class.getMethod("doSomething", (Class[]) null);

        assertTrue(factory.containsInvocationClass(FooActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(FooActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomething"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/foo/something/done"));
        List<String> requestMethods = actionMethodMetadata.getDefinedPath().getRequestMethods();
        assertThat(requestMethods.size(), is(2));
        assertTrue(requestMethods.contains("GET"));
        assertTrue(requestMethods.contains("POST"));
    }

    @Test
    public void testDetectAnnotatedActionWithoutTypePathMapping() throws Exception {
        Class<?> actionsClass = BazActions.class;
        Method actionMethod = FooActions.class.getMethod("doSomething", (Class[]) null);

        assertTrue(factory.containsInvocationClass(FooActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertNull(actionMethodMetadata);
    }

    @Test
    public void testDetectAnnotatedActionWithPostMethod() throws Exception {
        Class<?> actionsClass = BaaActions.class;
        Method actionMethod = BaaActions.class.getMethod("doSomethingWithPostMethod",
                (Class[]) null);

        assertTrue(factory.containsInvocationClass(BaaActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithPostMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/baa/something/post"));
        List<String> requestMethods = actionMethodMetadata.getDefinedPath().getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("POST"));
    }

    @Test
    public void testDetectAnnotatedActionWithGetMethod() throws Exception {
        Class<?> actionsClass = BaaActions.class;
        Method actionMethod = BaaActions.class
                .getMethod("doSomethingWithGetMethod", (Class[]) null);

        assertTrue(factory.containsInvocationClass(BaaActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithGetMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/baa/something/get"));
        List<String> requestMethods = actionMethodMetadata.getDefinedPath().getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("GET"));
    }

    @Test
    public void testDetectAnnotatedActionWithPutMethod() throws Exception {
        Class<?> actionsClass = BaaActions.class;
        Method actionMethod = BaaActions.class
                .getMethod("doSomethingWithPutMethod", (Class[]) null);

        assertTrue(factory.containsInvocationClass(BaaActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithPutMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/baa/something/put"));
        List<String> requestMethods = actionMethodMetadata.getDefinedPath().getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("PUT"));
    }

    @Test
    public void testDetectAnnotatedActionWithDeleteMethod() throws Exception {
        Class<?> actionsClass = BaaActions.class;
        Method actionMethod = BaaActions.class.getMethod("doSomethingWithDeleteMethod",
                new Class[] { String.class });

        assertTrue(factory.containsInvocationClass(BaaActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithDeleteMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/baa/something/delete"));
        List<String> requestMethods = actionMethodMetadata.getDefinedPath().getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("DELETE"));
    }

    @Test
    public void testDetectAnnotatedActionWithArg() throws Exception {
        Class<?> actionsClass = FooActions.class;
        Method actionMethod = FooActions.class.getMethod("doSomethingWithArg",
                new Class<?>[] { String.class });

        assertTrue(factory.containsInvocationClass(FooActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(FooActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getArgumentTypes()[0].getCanonicalName(),
                is(String.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithArg"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/foo/something/donewitharg"));
    }

    @Test
    public void testDetectAnnotatedActionNoDetectable() throws Exception {

        Class<?> actionsClass = FooActions.class;
        Method actionMethod = FooActions.class.getMethod("doSomethingNoDetectable", new Class<?>[] {
                String.class, Integer.class });

        assertTrue(factory.containsInvocationClass(FooActions.class));

        assertNull(factory.createInvocationMetadata(actionsClass, actionMethod));
    }

    @Test
    public void testDetectAnnotatedActionWithArgAndPathAutoDetection() throws Exception {
        Class<?> actionsClass = FooActions.class;
        Method actionMethod = FooActions.class.getMethod("doSomethingNameBased",
                new Class<?>[] { String.class });

        assertTrue(factory.containsInvocationClass(FooActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(FooActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getArgumentTypes()[0].getCanonicalName(),
                is(String.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingNameBased"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/foo/doSomethingNameBased"));
    }

    @Test
    public void testDetectAnnotatedActionWithRootPathAutoDetection() throws Exception {
        Class<?> actionsClass = BaaActions.class;
        Method actionMethod = BaaActions.class.getMethod("doSomethingNameBased",
                new Class<?>[] { String.class });

        assertTrue(factory.containsInvocationClass(FooActions.class));

        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaActions.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getArgumentTypes()[0].getCanonicalName(),
                is(String.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingNameBased"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/baa/doSomethingNameBased"));
    }

    @On("/foo")
    public static class FooActions {

        @On("/something/done")
        public Direction doSomething() {
            return null;
        }

        @On("something/donewitharg")
        public Direction doSomethingWithArg(@As("arg") String arg) {
            return null;
        }

        public Direction doSomethingNoDetectable(String arg, @As("foo") Integer foo) {
            return null;
        }

        @On
        public Direction doSomethingNameBased(@As("arg") String arg) {
            return null;
        }

    }

    @On
    public static class BaaActions {

        @On("/something/done")
        public Direction doSomething() {
            return null;
        }

        @On("something/donewitharg")
        public Direction doSomethingWithArg(@As("arg") String arg) {
            return null;
        }

        public Direction doSomethingNoDetectable(String arg, @As("foo") Integer foo) {
            return null;
        }

        @On
        public Direction doSomethingNameBased(@As("arg") String arg) {
            return null;
        }

        @On("/something/post")
        @Post
        public Direction doSomethingWithPostMethod() {
            return null;
        }

        @On("/something/get")
        @Get
        public Direction doSomethingWithGetMethod() {
            return null;
        }

        @On("/something/delete")
        @Delete
        public Direction doSomethingWithDeleteMethod(@As("arg") String arg) {
            return null;
        }

        @On("/something/put")
        @Put
        public Direction doSomethingWithPutMethod() {
            return null;
        }

    }

    public static class BazActions {

        @On("/something/done")
        public Direction doSomething() {
            return null;
        }
    }

}
