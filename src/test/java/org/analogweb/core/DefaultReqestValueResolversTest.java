package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.annotation.Annotation;
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
		resolvers = new DefaultReqestValueResolvers(Arrays.asList(new A(),
				new B(), new C(), new D(), new E(), new F(), new G()));
		RequestValueResolver a = resolvers.findRequestValueResolver(A.class);
		RequestValueResolver b = resolvers.findRequestValueResolver(B.class);
		RequestValueResolver c = resolvers.findRequestValueResolver(C.class);
		RequestValueResolver d = resolvers.findRequestValueResolver(D.class);
		RequestValueResolver e = resolvers.findRequestValueResolver(E.class);
		RequestValueResolver f = resolvers.findRequestValueResolver(F.class);
		RequestValueResolver g = resolvers.findRequestValueResolver(G.class);
		assertThat(A.class.getCanonicalName(), is(a.getClass().getCanonicalName()));
		assertThat(B.class.getCanonicalName(), is(b.getClass().getCanonicalName()));
		assertThat(C.class.getCanonicalName(), is(c.getClass().getCanonicalName()));
		assertThat(D.class.getCanonicalName(), is(d.getClass().getCanonicalName()));
		assertThat(E.class.getCanonicalName(), is(e.getClass()
				.getCanonicalName()));
		assertThat(F.class.getCanonicalName(), is(f.getClass()
				.getCanonicalName()));
		assertThat(G.class.getCanonicalName(), is(g.getClass()
				.getCanonicalName()));
	}

	class A implements RequestValueResolver {

		@Override
		public Object resolveValue(RequestContext requestContext,
				InvocationMetadata metadata, String key, Class<?> requiredType,
				Annotation[] annotations) {
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
		public void putAttributeValue(RequestContext requestContext,
				String query, Object value) {
			// nop.
		}

		@Override
		public void removeAttribute(RequestContext requestContext, String query) {
			// nop.
		}
	}

	class E implements RequestValueResolver {

		@Override
		public Object resolveValue(RequestContext requestContext,
				InvocationMetadata metadata, String key, Class<?> requiredType,
				Annotation[] annotations) {
			// nop
			return null;
		}
	}

	class F implements RequestValueResolver {

		@Override
		public Object resolveValue(RequestContext requestContext,
				InvocationMetadata metadata, String key, Class<?> requiredType,
				Annotation[] annotations) {
			// nop
			return null;
		}
	}
	
	class G extends F {
		
	}
}
