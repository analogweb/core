package org.analogweb;

/**
 * エントリポイントの任意の実行結果を、レスポンスにレンダリング可能な{@link Direction}に
 * 変換します。<br/>
 * 例えば、文字列などから遷移先を表す情報を特定し、遷移先を表す{@link Direction}を
 * 生成すること等が可能です。
 * @author snowgoose
 */
public interface ResponseResolver extends Module {

    /**
     * エントリポイントの任意の実行結果を、レスポンスにレンダリング可能な{@link Direction}に変換します。
     * TODO returns null when Direction unresolved.
     * @param invocationResult エントリポイントの実行結果
     * @param metadata {@link InvocationMetadata}
     * @param context {@link RequestContext}
     * @param responseContext {@link ResponseContext}
     * @return 実行結果から特定された{@link Direction}
     */
    Response resolve(Object invocationResult, InvocationMetadata metadata, RequestContext context, ResponseContext responseContext);

}
