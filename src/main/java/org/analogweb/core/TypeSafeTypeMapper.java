package org.analogweb.core;

import org.analogweb.TypeMapper;

/**
 * 変換前、変換後の型を特定可能な{@link TypeMapper}です。
 * @author snowgoose
 */
public abstract class TypeSafeTypeMapper<F, T> implements TypeMapper {

    @Override
    @SuppressWarnings("unchecked")
    public final Object mapToType(Object from, Class<?> requiredType, String[] formats) {
        return mapToTypeInternal((F) from, (Class<T>) requiredType, formats);
    }

    /**
     * 指定された型に変換を行います。<br/>
     * 型は実装により、予め決められています。
     * @param from 変換される以前の型(存在しない場合はnull)
     * @param requiredType 変換を行う型
     * @param formats 変換を行う際にしていされるフォーマット
     * @return 変換された型の新しいインスタンス
     */
    public abstract T mapToTypeInternal(F from, Class<T> requiredType, String[] formats);
}
