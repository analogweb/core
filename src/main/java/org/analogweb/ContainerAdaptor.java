package org.analogweb;

import java.util.List;

/**
 * アプリケーションを構成するモジュールのインスタンス管理を行うコンテナを表します。<br/>
 * 　アプリケーションにおけるインスタンスの取得は、全てこのアダプタを通して行われます。
 * モジュールにはリクエストによって起動されるモジュールやアプリケーションを構成するモジュール
 * のインスタンス全てが含まれます。一部を除き、外部のコンテナ(JNDIや、DIコンテナ等の
 * 一般的なインスタンスコンテナ)のファサードとしても振舞います。
 * @author snowgoose
 */
public interface ContainerAdaptor extends Module {

    /**
     * 指定された型に一致する、コンテナ内のインスタンスを返します。<br/>
     * 一致するインスタンスが存在しない場合はnullを返します。
     * @param type インスタンスを取得する型
     * @return 指定した型に一致するインスタンス
     */
    <T> T getInstanceOfType(Class<T> type);

    /**
     * 指定された型に一致する、コンテナ内の全てのインスタンスを返します。<br/>
     * 一致するインスタンスが存在しない場合は空の{@link List}を返します。
     * @param type インスタンスを取得する型
     * @return 指定した型に一致する全てのインスタンス
     */
    <T> List<T> getInstancesOfType(Class<T> type);
}
