package org.analogweb;

import java.util.List;

/**
 * @author snowgoose
 */
public interface InvokerFactory extends Module {

    /**
     * 新しい{@link Invoker}のインスタンスを生成します。
     * @param converters {@link TypeMapperContext}
     * @param processors 生成される{@link Invocation}に適用される全ての{@link InvocationProcessor}
     * @param handlers {@link AttributesHandlers}
     * @return　生成された{@link Invoker}
     */
    Invoker createInvoker(TypeMapperContext converters,
            List<InvocationProcessor> processors, AttributesHandlers handlers);
}
