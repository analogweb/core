package org.analogweb;

import java.util.List;

/**
 * {@link Invocation#invoke()}実行前に確定したパラメータ
 * を保持します。
 * @author snowgoose
 */
public interface PreparedInvocationArguments {

    /**
     * 実行時に必要なパラメータのリストのビューを取得します。
     * @return パラメータのリスト
     */
    List<Object> asList();

}
