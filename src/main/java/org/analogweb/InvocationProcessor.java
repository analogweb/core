package org.analogweb;

import java.lang.reflect.Method;

/**
 * {@link Invocation}を実行する様々なフェーズ(実行前、実行後、例外発生、実行完了後)
 * において、任意の処理を付加するコンポーネント<br/>
 * @author snowgoose
 */
public interface InvocationProcessor extends MultiModule {

    /**
     * {@link Invocation}の実行前に処理を追加します。<br/>
     * 通常は引数に指定された{@link Invocation}をそのまま返します。
     * @param method 実行対象の{@link Method}
     * @param invocation {@link Invocation}
     * @param metadata {@link InvocationMetadata}
     * @param context {@link RequestContext}
     * @param attributes {@link RequestAttributes}
     * @param converters {@link TypeMapperContext}
     * @return 処理された{@link Invocation}
     */
    Invocation prepareInvoke(Method method, Invocation invocation, InvocationMetadata metadata,
            RequestContext context, RequestAttributes attributes, TypeMapperContext converters);

    /**
     * 全ての{@link InvocationProcessor}の#prepareInvoke実行後、
     * {@link Invocation#invoke()}が実行される直前に行う処理を追加します。<br/>
     * {@link Invocation}に引き渡される、確定されたパラメータを変更することは
     * できません。未確定(null)であるパラメータに対する値の追加を行うことが
     * 可能です。{@link Invocation}を引き続き実行する場合はnullを返します。
     * {@code null}以外の値が返される場合、それを実行結果として処理し、
     * {@link Invocation}および続く{@link InvocationProcessor}の処理は
     * 中断されます。
     * @param method 実行対象の{@link Method}
     * @param metadata {@link InvocationMetadata}
     * @param args {@link InvocationArguments}
     * @return 実行処理を中断する結果({@link Direction}など。)
     */
    Object onInvoke(Method method,InvocationMetadata metadata, InvocationArguments args);
    
    /**
     * {@link Invocation}実行時に例外が発生した場合に、処理を追加します。
     * @param ex {@link Invocation}実行時に発生した例外。
     * @param request {@link RequestContext}
     * @param invocation 例外が発生した対象の{@link Invocation}
     * @param metadata {@link InvocationMetadata}
     * @return 実行処理を中断する結果({@link Direction}など。)
     */
    Object processException(Exception ex, RequestContext request, Invocation invocation,
            InvocationMetadata metadata);

    /**
     * {@link Invocation}を正常に実行した場合に、処理を追加します。
     * @param invocationResult {@link Invocation}の実行結果
     * @param invocation 例外が発生した対象の{@link Invocation}
     * @param metadata {@link InvocationMetadata}
     * @param context {@link RequestContext}
     * @param attributes {@link RequestAttributes}
     * @param resultAttributes {@link ResultAttributes}
     * @return {@link Invocation}の実行結果
     */
    Object postInvoke(Object invocationResult, Invocation invocation, InvocationMetadata metadata,
            RequestContext context, RequestAttributes attributes, ResultAttributes resultAttributes);

    /**
     * {@link Invocation}実行後に処理を追加します。<br/>
     * このメソッドは{@link Invocation}の実行において例外が発生した場合でも
     * {@link #processException(Exception, RequestContext, Invocation, InvocationMetadata)}
     * で結果が買えされない場合実行されます。
     * @param request {@link RequestContext}
     * @param invocation {@link Invocation}
     * @param metadata {@link InvocationMetadata}
     * @param invocationResult {@link Invocation}の実行結果(例外が発生するなどして実行結果が存在しない場合はnull)
     */
    void afterCompletion(RequestContext request, Invocation invocation,
            InvocationMetadata metadata, Object invocationResult);

    /**
     * {@link Invocation#invoke()}実行前に確定したパラメータ
     * を保持します。
     * @author snowgoose
     */
    public static interface InvocationArguments {
        /**
         * {@link Invocation}に引き渡すパラメータのうち、指定した
         * 索引に一致するパラメータの値を更新します。
         * 確定した(nullでない)パラメータに対する値の更新を行うことは
         * できません。
         * @param index (0から始まる)パラメータの索引
         * @param value 更新する値
         */
        void set(int index,Object value);
        /**
         * 実行時に必要なパラメータの配列を生成します。
         * @return パラメータの配列
         */
        Object[] toArray();
    }

}
