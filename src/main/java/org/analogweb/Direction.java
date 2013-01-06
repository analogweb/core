package org.analogweb;

import java.io.IOException;

import org.analogweb.exception.WebApplicationException;

/**
 * リクエストによって起動されたエントリポイントを実行した結果とその振る舞いを定義します。<br/>
 * 定義には、フォワードやリダイレクト等が含まれます。通常、起動されたエントリポイントが
 * 戻り値として返します。エントリポイントがこの型を戻り値として返さない場合でも、
 * レスポンスが生成される前には、この型のインスタンスとして評価され、レスポンスへの
 * レンダリングが行われます。
 * @author snowgoose
 */
public interface Direction {

    /**
     * エントリポイントを実行したの結果を、レスポンスにレンダリングします。
     * @param context {@link RequestContext}
     * @throws IOException レスポンスへのレンダリング時にI/Oエラーが発生した場合。
     * @throws WebApplicationException レスポンスへのレンダリング時に任意の例外が発生した場合。
     */
    void render(RequestContext context) throws IOException, WebApplicationException;

}
