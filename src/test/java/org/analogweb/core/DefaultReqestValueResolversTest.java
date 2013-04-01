package org.analogweb.core;

import java.util.Arrays;

import org.analogweb.AttributesHandler;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.junit.Test;

public class DefaultReqestValueResolversTest {

    private DefaultReqestValueResolvers resolvers;

    @Test
    public void test() {
        resolvers = new DefaultReqestValueResolvers(Arrays.asList(new A(), new B(), new C(),
                new D(), new E(),new F()));
        RequestValueResolver a = resolvers.findRequestValueResolver(A.class);
        RequestValueResolver b = resolvers.findRequestValueResolver(B.class);
        RequestValueResolver c = resolvers.findRequestValueResolver(C.class);
        RequestValueResolver d = resolvers.findRequestValueResolver(D.class);
        RequestValueResolver e = resolvers.findRequestValueResolver(E.class);
        RequestValueResolver f = resolvers.findRequestValueResolver(F.class);
        System.out.println(a.getClass().getCanonicalName());
        System.out.println(b.getClass().getCanonicalName());
        System.out.println(c.getClass().getCanonicalName());
        System.out.println(d.getClass().getCanonicalName());
        System.out.println(e.getClass().getCanonicalName());
        System.out.println(f.getClass().getCanonicalName());
    }

    class A implements RequestValueResolver {

        @Override
        public Object resolveValue(RequestContext requestContext, InvocationMetadata metadata,
                String key, Class<?> requiredType) {
            // nop
            return null;
        }
    }

    class B extends A {
    }

    class C extends B {
    }

    class D extends B implements AttributesHandler {

        @Override
        public void putAttributeValue(RequestContext requestContext, String query, Object value) {
            // nop.
        }

        @Override
        public void removeAttribute(RequestContext requestContext, String query) {
            // nop.
        }
    }
    class E implements RequestValueResolver {

        @Override
        public Object resolveValue(RequestContext requestContext, InvocationMetadata metadata,
                String key, Class<?> requiredType) {
            // nop
            return null;
        }
    }
    class F implements RequestValueResolver {

        @Override
        public Object resolveValue(RequestContext requestContext, InvocationMetadata metadata,
                String key, Class<?> requiredType) {
            // nop
            return null;
        }
    }
}
