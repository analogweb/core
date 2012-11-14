package org.analogweb.core;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.analogweb.RequestContext;
import org.analogweb.annotation.Formats;
import org.analogweb.util.Maps;
import org.junit.Before;
import org.junit.Test;

/**
 * @author snowgoose
 */
public class ParametersTypeMapperTest {

    private ParametersTypeMapper mapper;
    private RequestContext context;

    @Before
    public void setUp() throws Exception {
        mapper = new ParametersTypeMapper();
        context = mock(RequestContext.class);
    }

    @Test
    public void testMapToType() throws Exception {
        Map<String, Object> parameters = Maps.newHashMap("foo", (Object) "baa!");
        parameters.put("baa", new String[] { "11" });
        parameters.put("baz", new String[] { "2011/11/11" });
        String[] expectedArray = { "fuga", "baa" };
        parameters.put("hoge", expectedArray);
        BigDecimal expectedDecimal = new BigDecimal("123456");
        parameters.put("fuga", new String[] { "123,456" });
        Date expectedDate = new SimpleDateFormat("yyyy/MM/dd").parse("2011/11/11");
        Bean actual = (Bean) mapper.mapToType(context, parameters, Bean.class, null);
        assertThat(actual.getFoo(), is("baa!"));
        assertThat(actual.getBaa(), is(11));
        assertThat(actual.getBaz(), is(expectedDate));
        assertThat(actual.getHoge(), is(expectedArray));
        assertThat(actual.getFuga(), is(expectedDecimal));
        assertThat(actual.getBoo(), is(nullValue()));
    }

    @Test
    public void testMapToTypeNotRequestParameterMap() throws Exception {

        Bean actual = (Bean) mapper.mapToType(context, new Object(), Bean.class, null);

        assertThat(actual, is(nullValue()));
    }

    @Test
    public void testMapToTypeNotInstanticatable() throws Exception {
        Map<String, Object> parameters = Maps.newHashMap("foo", (Object) "baa!");
        parameters.put("baa", "11");
        parameters.put("baz", "2011/11/11");

        BeanNotInstanticatable actual = (BeanNotInstanticatable) mapper.mapToType(context,
                parameters, BeanNotInstanticatable.class, null);

        assertThat(actual, is(nullValue()));
    }

    public static class Bean {
        private String foo;
        private Integer baa;
        private Date baz;
        private String[] hoge;
        @Formats("###,0")
        private BigDecimal fuga;
        private Integer boo;

        public Bean() {

        }

        public String getFoo() {
            return foo;
        }

        public Integer getBaa() {
            return baa;
        }

        public Date getBaz() {
            return baz;
        }

        public String[] getHoge() {
            return hoge;
        }

        public BigDecimal getFuga() {
            return this.fuga;
        }

        public Integer getBoo() {
            return this.boo;
        }
    }

    public static class BeanNotInstanticatable {
        private String foo;
        private Integer baa;
        private Date baz;
        private String[] hoge;

        public BeanNotInstanticatable(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }

        public Integer getBaa() {
            return baa;
        }

        public Date getBaz() {
            return baz;
        }

        public String[] getHoge() {
            return hoge;
        }
    }

}
