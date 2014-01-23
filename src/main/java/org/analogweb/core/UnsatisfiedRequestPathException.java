package org.analogweb.core;

import org.analogweb.RequestPathMetadata;

/**
 * エントリポイントに対するリクエストが、エントリポイントが定義する要件を満たさない
 * 場合に送出される例外です。
 * @author snowgoose
 */
public class UnsatisfiedRequestPathException extends ApplicationRuntimeException {

    private static final long serialVersionUID = -5701810553477314954L;
    private RequestPathMetadata metadata;

    public UnsatisfiedRequestPathException(RequestPathMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * {@link RequestPathMetadata}を取得します。
     * @return {@link RequestPathMetadata}
     */
    public RequestPathMetadata getMetadata() {
        return this.metadata;
    }
}
