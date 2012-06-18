package org.analogweb;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet APIに関連するリクエストモジュールを保持します。<br/>
 * このインスタンスはリクエスト毎に生成されます。
 * @author snowgoose
 */
public interface RequestContext {

    /**
     * {@link HttpServletRequest}を取得します。
     * @return {@link HttpServletRequest}
     */
    HttpServletRequest getRequest();

    /**
     * {@link HttpServletResponse}を取得します。
     * @return {@link HttpServletResponse}
     */
    HttpServletResponse getResponse();

    /**
     * {@link ServletContext}を取得します。
     * @return {@link ServletContext}
     */
    ServletContext getContext();

    /**
     * 現在のリクエストにおける{@link RequestPath}を取得します。
     * @return {@link RequestPath}
     */
    RequestPath getRequestPath();

    /**
     * 現在のリクエストにおける{@link RequestAttributes}を取得します。
     * @param factory {@link RequestAttributesFactory}
     * @param metadata {@link InvocationMetadata}
     * @param resolversMap スコープ名をキーとした{@link AttributesHandler}の{@link Map}
     * @return {@link RequestAttributes}
     */
    RequestAttributes resolveRequestAttributes(RequestAttributesFactory factory,InvocationMetadata metadata,
            Map<String, AttributesHandler> resolversMap);

}
