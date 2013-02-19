package org.analogweb.core;

import java.util.List;

import org.analogweb.RequestPathMetadata;

/**
 * リクエストメソッドが、あるエントリポイントに於いて実行不可である場合に送出される例外です。
 * @author snowgoose
 */
public class RequestMethodUnsupportedException extends UnsatisfiedRequestPathException {

    private static final long serialVersionUID = -5103029925778697441L;
    private List<String> definedMethods;
    private String requestedMethod;

    public RequestMethodUnsupportedException(RequestPathMetadata metadata, List<String> definedMethods,
            String requestedMethod) {
        super(metadata);
        this.definedMethods = definedMethods;
        this.requestedMethod = requestedMethod;
    }

    /**
     * エントリポイントに定義されている、実行可能なリクエストメソッドのリストを取得します。
     * @return リクエストメソッドのリスト
     */
    public List<String> getDefinedMethods() {
        return definedMethods;
    }

    /**
     * エントリポイントにリクエストされたメソッドを取得します。
     * @return リクエストされたメソッド
     */
    public String getRequestedMethod() {
        return requestedMethod;
    }

}
