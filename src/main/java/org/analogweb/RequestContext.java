package org.analogweb;

import java.io.IOException;
import java.io.InputStream;

/**
 * 1つのHTTPリクエストをライフサイクルとする全てのモジュールを保持します。<br/>
 * @author snowgoose
 */
public interface RequestContext {

    /**
     * HTTPクッキーを保持する{@link Cookies}を取得します。
     * @return {@link Cookies}
     */
    Cookies getCookies();

    /**
     * HTTPリクエストヘッダを保持する{@link Headers}を取得します。
     * @return {@link Headers}
     */
    Headers getRequestHeaders();

    /**
     * HTTPリクエストに含まれるパラメータを保持する{@link Parameters}を取得します。
     * @return {@link Parameters}
     */
    Parameters getParameters();

    /**
     * リクエストボディを保持する{@link InputStream}を取得します。
     * @return {@link InputStream}
     * @throws IOException I/Oエラーが発生した場合
     */
    InputStream getRequestBody() throws IOException;

    /**
     * HTTPリクエストヘッダ Content-Type に含まれるメディアタイプを
     * {@link MediaType}として取得します。ヘッダが存在しない場合は
     * nullを返します。
     * @return {@link MediaType}
     */
    MediaType getContentType();

    /**
     * 現在のリクエストにおける{@link RequestPath}を取得します。
     * @return {@link RequestPath}
     */
    RequestPath getRequestPath();

}
