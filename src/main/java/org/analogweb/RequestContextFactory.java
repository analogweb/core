package org.analogweb;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link RequestContext}を生成するファクトリです。
 * @author snowgoose
 */
public interface RequestContextFactory  extends Module {

    /**
     * 新しい{@link RequestContext}のインスタンスを生成します。
     * @param context {@link ServletContext}
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {@link RequestContext}
     */
    RequestContext createRequestContext(ServletContext context, HttpServletRequest request,
            HttpServletResponse response);

}
