package org.analogweb;

/**
 * {@link AttributesHandler}を管理します。<br/>
 * 名称({@link AttributesHandler#getScopeName()})に一致する
 * {@link AttributesHandler}を探索することが可能です。
 * @author snowgoose
 */
public interface AttributesHandlers {

    /**
     * 名称({@link AttributesHandler#getScopeName()})に一致する
     * {@link AttributesHandler}を探索します。<br/>
     * 名称に該当する{@link AttributesHandler}が管理下にない場合
     * は{@code null}を返します。
     * @param name 探索する対象の{@link AttributesHandler}の名称
     * @return 指定した名称に一致する{@link AttributesHandler}
     */
    AttributesHandler get(String name);

}
