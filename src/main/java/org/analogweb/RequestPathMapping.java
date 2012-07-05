package org.analogweb;

/**
 * {@link RequestPathMetadata}をキーとして、{@link InvocationMetadata}を保持するハッシュです。
 * @author snowgoose
 */
public interface RequestPathMapping extends Disposable {

    /**
     * リクエストされたパス({@link RequestPath})に一致する{@link InvocationMetadata}を取得します。<br/>
     * キーに該当する{@link InvocationMetadata}が存在しない場合はnullを返します。
     * @param requestPath {@link RequestPathMetadata}
     * @return キーにより特定された{@link InvocationMetadata}
     */
    InvocationMetadata findInvocationMetadata(RequestPath requestPath);

    /**
     * {@link RequestPathMetadata}をキーとして、{@link InvocationMetadata}をマッピングします。
     * @param requestPath {@link RequestPathMetadata}
     * @param invocationMetadata {@link InvocationMetadata}
     */
    void mapInvocationMetadata(RequestPathMetadata requestPath,
            InvocationMetadata invocationMetadata);

}
