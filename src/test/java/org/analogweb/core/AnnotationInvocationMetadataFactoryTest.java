package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.analogweb.InvocationMetadata;
import org.analogweb.Renderable;
import org.analogweb.annotation.As;
import org.analogweb.annotation.Delete;
import org.analogweb.annotation.Get;
import org.analogweb.annotation.Post;
import org.analogweb.annotation.Put;
import org.analogweb.annotation.Route;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class AnnotationInvocationMetadataFactoryTest {

    private AnnotationInvocationMetadataFactory factory;

    @Before
    public void setUp() {
        factory = new AnnotationInvocationMetadataFactory();
    }

    @Test
    public void testDetectAnnotatedAction() throws Exception {
        Class<?> actionsClass = FooResource.class;
        Method actionMethod = FooResource.class.getMethod("doSomething", (Class[]) null);
        assertTrue(factory.containsInvocationClass(FooResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(FooResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomething"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/foo/something/done"));
        List<String> requestMethods = ((RequestPathDefinition) actionMethodMetadata
                .getDefinedPath()).getRequestMethods();
        assertThat(requestMethods.size(), is(2));
        assertTrue(requestMethods.contains("GET"));
        assertTrue(requestMethods.contains("POST"));
    }

    @Test
    public void testDetectAnnotatedActionWithoutTypePathMapping() throws Exception {
        Class<?> actionsClass = BazResource.class;
        Method actionMethod = FooResource.class.getMethod("doSomething", (Class[]) null);
        assertTrue(factory.containsInvocationClass(FooResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata, is(nullValue()));
    }

    @Test
    public void testDetectAnnotatedActionWithPostMethod() throws Exception {
        Class<?> actionsClass = BaaResource.class;
        Method actionMethod = BaaResource.class.getMethod("doSomethingWithPostMethod",
                (Class[]) null);
        assertTrue(factory.containsInvocationClass(BaaResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithPostMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/baa/something/post"));
        List<String> requestMethods = ((RequestPathDefinition) actionMethodMetadata
                .getDefinedPath()).getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("POST"));
    }

    @Test
    public void testDetectAnnotatedActionWithGetMethod() throws Exception {
        Class<?> actionsClass = BaaResource.class;
        Method actionMethod = BaaResource.class.getMethod("doSomethingWithGetMethod",
                (Class[]) null);
        assertTrue(factory.containsInvocationClass(BaaResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithGetMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/baa/something/get"));
        List<String> requestMethods = ((RequestPathDefinition) actionMethodMetadata
                .getDefinedPath()).getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("GET"));
    }

    @Test
    public void testDetectAnnotatedActionWithPutMethod() throws Exception {
        Class<?> actionsClass = BaaResource.class;
        Method actionMethod = BaaResource.class.getMethod("doSomethingWithPutMethod",
                (Class[]) null);
        assertTrue(factory.containsInvocationClass(BaaResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(0));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithPutMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/baa/something/put"));
        List<String> requestMethods = ((RequestPathDefinition) actionMethodMetadata
                .getDefinedPath()).getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("PUT"));
    }

    @Test
    public void testDetectAnnotatedActionWithDeleteMethod() throws Exception {
        Class<?> actionsClass = BaaResource.class;
        Method actionMethod = BaaResource.class.getMethod("doSomethingWithDeleteMethod",
                new Class[] { String.class });
        assertTrue(factory.containsInvocationClass(BaaResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithDeleteMethod"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/baa/something/delete"));
        List<String> requestMethods = ((RequestPathDefinition) actionMethodMetadata
                .getDefinedPath()).getRequestMethods();
        assertThat(requestMethods.size(), is(1));
        assertTrue(requestMethods.contains("DELETE"));
    }

    @Test
    public void testDetectAnnotatedActionWithArg() throws Exception {
        Class<?> actionsClass = FooResource.class;
        Method actionMethod = FooResource.class.getMethod("doSomethingWithArg",
                new Class<?>[] { String.class });
        assertTrue(factory.containsInvocationClass(FooResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(FooResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getArgumentTypes()[0].getCanonicalName(),
                is(String.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingWithArg"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/foo/something/donewitharg"));
    }

    @Test
    public void testDetectAnnotatedActionNoDetectable() throws Exception {
        Class<?> actionsClass = FooResource.class;
        Method actionMethod = FooResource.class.getMethod("doSomethingNoDetectable",
                new Class<?>[] { String.class, Integer.class });
        assertTrue(factory.containsInvocationClass(FooResource.class));
        assertThat(factory.createInvocationMetadata(actionsClass, actionMethod), is(nullValue()));
    }

    @Test
    public void testDetectAnnotatedActionWithArgAndPathAutoDetection() throws Exception {
        Class<?> actionsClass = FooResource.class;
        Method actionMethod = FooResource.class.getMethod("doSomethingNameBased",
                new Class<?>[] { String.class });
        assertTrue(factory.containsInvocationClass(FooResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(FooResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getArgumentTypes()[0].getCanonicalName(),
                is(String.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingNameBased"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/foo/doSomethingNameBased"));
    }

    @Test
    public void testDetectAnnotatedActionWithRootPathAutoDetection() throws Exception {
        Class<?> actionsClass = BaaResource.class;
        Method actionMethod = BaaResource.class.getMethod("doSomethingNameBased",
                new Class<?>[] { String.class });
        assertTrue(factory.containsInvocationClass(FooResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(BaaResource.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes().length, is(1));
        assertThat(actionMethodMetadata.getArgumentTypes()[0].getCanonicalName(),
                is(String.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getMethodName(), is("doSomethingNameBased"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(),
                is("/baa/doSomethingNameBased"));
    }

    @Test
    public void testDetectAnnotatedActionInhelitance() throws Exception {
        Class<?> actionsClass = Child.class;
        Method actionMethod = Child.class.getMethod("resolve", String.class);
        assertTrue(factory.containsInvocationClass(FooResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata.getInvocationClass().getCanonicalName(),
                is(Child.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getArgumentTypes()[0].getCanonicalName(),
                is(String.class.getCanonicalName()));
        assertThat(actionMethodMetadata.getMethodName(), is("resolve"));
        assertThat(actionMethodMetadata.getDefinedPath().getActualPath(), is("/child/resolve"));
        List<String> requestMethods = ((RequestPathDefinition) actionMethodMetadata
                .getDefinedPath()).getRequestMethods();
        assertThat(requestMethods.size(), is(2));
        assertTrue(requestMethods.contains("GET"));
        assertTrue(requestMethods.contains("POST"));
    }

    @Test
    @Ignore("Java8 resolves annotation via bridge methods.")
    public void testDetectAnnotatedActionWithBridgeMethod() throws Exception {
        Class<?> actionsClass = Child.class;
        Method actionMethod = Child.class.getMethod("resolve", Object.class);
        assertTrue(factory.containsInvocationClass(FooResource.class));
        InvocationMetadata actionMethodMetadata = factory.createInvocationMetadata(actionsClass,
                actionMethod);
        assertThat(actionMethodMetadata, is(nullValue()));
    }

    @Route("/foo")
    public static class FooResource {

        @Route("/something/done")
        public Renderable doSomething() {
            return null;
        }

        @Route("something/donewitharg")
        public Renderable doSomethingWithArg(@As("arg") String arg) {
            return null;
        }

        public Renderable doSomethingNoDetectable(String arg, @As("foo") Integer foo) {
            return null;
        }

        @Route
        public Renderable doSomethingNameBased(@As("arg") String arg) {
            return null;
        }
    }

    @Route
    public static class BaaResource {

        @Route("/something/done")
        public Renderable doSomething() {
            return null;
        }

        @Route("something/donewitharg")
        public Renderable doSomethingWithArg(@As("arg") String arg) {
            return null;
        }

        public Renderable doSomethingNoDetectable(String arg, @As("foo") Integer foo) {
            return null;
        }

        @Route
        public Renderable doSomethingNameBased(@As("arg") String arg) {
            return null;
        }

        @Route("/something/post")
        @Post
        public Renderable doSomethingWithPostMethod() {
            return null;
        }

        @Route("/something/get")
        @Get
        public Renderable doSomethingWithGetMethod() {
            return null;
        }

        @Route("/something/delete")
        @Delete
        public Renderable doSomethingWithDeleteMethod(@As("arg") String arg) {
            return null;
        }

        @Route("/something/put")
        @Put
        public Renderable doSomethingWithPutMethod() {
            return null;
        }
    }

    public static class BazResource {

        @Route("/something/done")
        public Renderable doSomething() {
            return null;
        }
    }

    @Route
    public static class Parent<T> {

        @Route
        public T resolve(T foo) {
            return null;
        }
    }

    public static class Child extends Parent<String> {

        // generate bridge method.
        @Override
        @Route
        public String resolve(String foo) {
            return "resolved!";
        }
    }
}
