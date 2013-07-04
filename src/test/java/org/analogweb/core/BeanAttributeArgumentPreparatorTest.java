package org.analogweb.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.analogweb.InvocationArguments;
import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;
import org.analogweb.RequestValueResolver;
import org.analogweb.RequestValueResolvers;
import org.analogweb.TypeMapper;
import org.analogweb.TypeMapperContext;
import org.analogweb.annotation.Bean;
import org.analogweb.annotation.Param;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

//TODO write test.
public class BeanAttributeArgumentPreparatorTest {
    
    private BeanAttributeArgumentPreparator preparator;

    @Test
    public void test() throws Exception {
        preparator = new BeanAttributeArgumentPreparator();
        InvocationArguments args = mock(InvocationArguments.class);
        InvocationMetadata metadata = mock(InvocationMetadata.class);
        when(metadata.getArgumentTypes()).thenReturn(new Class[]{SomeBean.class});

        RequestContext context = mock(RequestContext.class);
        TypeMapperContext converters = mock(TypeMapperContext.class);
        RequestValueResolvers resolvers = mock(RequestValueResolvers.class);
        RequestValueResolver resolver = mock(RequestValueResolver.class);
        when(resolvers.findRequestValueResolver(ParameterValueResolver.class)).thenReturn(resolver);
        when(resolver.resolveValue(context, metadata, "name", String.class)).thenReturn("Name");
        when(resolver.resolveValue(context, metadata, "birthDay", Date.class)).thenReturn("2013-01-01");
        when(resolver.resolveValue(context, metadata, "number", Integer.class)).thenReturn("5");

        Date expectedDate = new Date();
        when(converters.mapToType(eq(TypeMapper.class), eq("Name"), eq(String.class), isA(String[].class))).thenReturn("Name");
        when(converters.mapToType(eq(TypeMapper.class), eq("2013-01-01"), eq(Date.class), isA(String[].class))).thenReturn(expectedDate);
        when(converters.mapToType(eq(TypeMapper.class), eq("5"), eq(Integer.class), isA(String[].class))).thenReturn(5);
        preparator.prepareInvoke(Resource.class.getDeclaredMethod("doSomething",SomeBean.class), args, metadata, context, converters, resolvers);
        
        ArgumentCaptor<SomeBean> captor = ArgumentCaptor.forClass(SomeBean.class);
        verify(args).putInvocationArgument(eq(0), captor.capture());
        SomeBean actual = captor.getValue();
        assertThat(actual.getName(),is("Name"));
        assertThat(actual.getBirthDay(),is(expectedDate));
        assertThat(actual.getNumber(),is(5));
    }

    public static class Resource {
        public String doSomething(@Bean SomeBean bean){
            return "something!";
        }
    }
    public static class SomeBean {

        @Param
        private String name;
        @Param
        private Date birthDay;
        @Param
        private Integer number;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(Date birthDay) {
            this.birthDay = birthDay;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }
    }
}
