package org.analogweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Servlet APIに関連するリクエストモジュールを保持します。<br/>
 * このインスタンスはリクエスト毎に生成されます。
 * @author snowgoose
 */
public interface RequestContext {

    Cookies getCookies();
    Headers getRequestHeaders();
    Headers getResponseHeaders();
    Parameters getParameters();
    InputStream getRequestBody() throws IOException;
    OutputStream getResponseBody() throws IOException;
    void setResponseStatus(int status);

    /**
     * 現在のリクエストにおける{@link RequestPath}を取得します。
     * @return {@link RequestPath}
     */
    RequestPath getRequestPath();

}
