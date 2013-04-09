package org.analogweb;

import java.lang.reflect.Method;

/**
 * {@link Application}を実行する様々なフェーズ(実行前、実行後、例外発生、実行完了後)
 * において、任意の処理を付加するコンポーネント<br/>
 * @author snowgoose
 */
public interface ApplicationProcessor extends MultiModule,Precedence {

    /**
     * {@link ApplicationProcessor}の各ライフサイクルにおいて
     * 処理の結果、{@link Application}及び、後続の{@link ApplicationProcessor}
     * の処理を中断しないことを表すフラグメントです。
     */
    Object NO_INTERRUPTION = new Object();

    /**
     * {@link Application}の実行前に処理を追加します。<br/>
     * 通常は、{@link #NO_INTERRUPTION}を返します。それ以外の
     * 値をかえす場合は、処理の結果その戻り値を以って
     * {@link Application}の処理を終了します。
     * @param method 実行対象の{@link Method}
     * @param args {@link InvocationArguments}
     * @param metadata {@link InvocationMetadata}
     * @param context {@link RequestContext}
     * @param converters {@link TypeMapperContext}
     * @param resolvers {@link RequestValueResolvers}
     * @return 実行処理を中断する結果({@link Direction}など。)
     */
    Object prepareInvoke(Method method, InvocationArguments args, InvocationMetadata metadata,
            RequestContext context, TypeMapperContext converters, RequestValueResolvers resolvers);

    /**
     * {@link Application}実行時に例外が発生した場合に、処理を追加します。<br/>
     * 通常は、{@link #NO_INTERRUPTION}を返します。それ以外の
     * 値をかえす場合は、その戻り値が処理結果となります。
     * @param ex {@link Application}実行時に発生した例外。
     * @param request {@link RequestContext}
     * @param args 例外が発生した対象の{@link InvocationArguments}
     * @param metadata {@link InvocationMetadata}
     * @return 実行処理を中断する結果({@link Direction}など。)
     */
    Object processException(Exception ex, RequestContext request, PreparedInvocationArguments args,
            InvocationMetadata metadata);

    /**
     * {@link Application}を正常に実行した場合に、処理を追加します。<br/>
     * 通常は、引数に指定された{@code invocationResult}の値({@link Application}
     * の実行結果)をそのまま返します。それ以外の値を返す(実行結果を挿げ替える)
     * 事も可能です。
     * @param invocationResult {@link Application}の実行結果
     * @param args 例外が発生した対象の{@link InvocationArguments}
     * @param metadata {@link InvocationMetadata}
     * @param context {@link RequestContext}
     * @param resolvers {@link RequestValueResolvers}
     */
    void postInvoke(Object invocationResult, InvocationArguments args,
            InvocationMetadata metadata, RequestContext context, RequestValueResolvers resolvers);

    /**
     * {@link Application}実行後に処理を追加します。<br/>
     * このメソッドは{@link Application}の実行において例外が発生した場合でも
     * {@link #processException(Exception, RequestContext, PreparedInvocationArguments, InvocationMetadata)}
     * で結果が買えされない場合実行されます。
     * @param request {@link RequestContext}
     * @param response {@link ResponseContext}
     * @param e {@link Exception}
     */
    void afterCompletion(RequestContext request, ResponseContext response,Exception e);

}
