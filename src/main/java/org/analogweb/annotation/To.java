package org.analogweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.analogweb.AttributesHandler;

/**
 * スコープ内で、操作可能な属性を保持するモデルマップである事を表す注釈です。<br/>
 * 指定されたスコープに設定された属性のマップを展開するアウトプット・パラメータとして
 * 機能します。この注釈が付与されるパラメータは{@link java.util.Map}インターフェース
 * である必要があります。
 * @author snowgoose
 */
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface To {

    /**
     * このマップを展開する{@link AttributesHandler}を指定します。
     * @return このマップを展開する{@link AttributesHandler}
     */
    Class<? extends AttributesHandler> value();
}
