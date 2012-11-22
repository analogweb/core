package org.analogweb;

/**
 * 指定された{@link TypeMapper}の型から{@link TypeMapper}のインスタンスを選択し
 * {@link TypeMapper}によって変換された型を返します。
 * @author snowgoose
 */
public interface TypeMapperContext extends Module {

    /**
     * 指定された型に変換を行います。<br/>
     * 変換には指定された{@link TypeMapper}の型を持つインスタンスが使用されます。
     * @param typeMapperClass 変換を行う{@link TypeMapper}の型
     * @param from 変換する前のインスタンス
     * @param requiredType 変換する対象の型
     * @param mappingFormats 変換を行う際に使用されるフォーマット
     * @return 変換後の新しいインスタンス
     */
    <T> T mapToType(Class<? extends TypeMapper> typeMapperClass, Object from,
            Class<T> requiredType, String[] mappingFormats);

}
