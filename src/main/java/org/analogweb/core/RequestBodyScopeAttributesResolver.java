package org.analogweb.core;

import java.io.IOException;

import org.analogweb.InvocationMetadata;
import org.analogweb.RequestContext;

/**
 * リクエストボディをストリームで取得する{@link AbstractAttributesHandler}の実装です。<br/>
 * クエリの内容に関わらす、常に{@link RequestContext#getRequestBody()}から得られる結果を
 * 返します。既にリクエストボディの読み込みを行っている等、リクエストボディの取得に失敗した場合は
 * nullを返します。
 * @author snowgoose
 */
public class RequestBodyScopeAttributesResolver extends AbstractAttributesHandler {

    public static final String NAME = "body";

    @Override
    public String getScopeName() {
        return NAME;
    }

    @Override
    public Object resolveAttributeValue(RequestContext requestContext, InvocationMetadata metadata,
            String query, Class<?> type) {
        try {
            return requestContext.getRequestBody();
        } catch (IOException e) {
            return null;
        }
    }

}
