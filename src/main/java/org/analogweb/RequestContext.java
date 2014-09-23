package org.analogweb;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

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
     * HTTPリクエストボディに含まれるパラメータを保持する{@link Parameters}を取得します。
     * @return {@link Parameters}
     */
    Parameters getFormParameters();

    /**
     * HTTPリクエストURIに含まれるクエリパラメータを保持する{@link Parameters}を取得します。
     * @return {@link Parameters}
     */
    Parameters getQueryParameters();

    /**
     * HTTPリクエストURIに含まれるマトリクスパラメータを保持する{@link Parameters}を取得します。
     * @return {@link Parameters}
     */
    Parameters getMatrixParameters();

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

    /**
     * 現在のリクエストにおける{@link Locale}を取得します。
     * @return {@link Locale}
     */
    Locale getLocale();

    /**
     * 現在のリクエストにおける{@link Locale}を優先度の順に取得します。
     * @return {@link Locale}
     */
    List<Locale> getLocales();

    /**
     * HTTPリクエストヘッダ Content-Length に含まれるリクエスト長を
     * 取得します。ヘッダが存在しない場合は0を返します。
     * @return リクエスト長
     */
    long getContentLength();

    /**
     * HTTPリクエストの文字エンコーディングを取得します。
     * エンコーディングを特定できない場合はUTF-8を返します。
     * @see RequestContext#getContentType
     * @return リクエストエンコーディング
     */
    String getCharacterEncoding();
}
