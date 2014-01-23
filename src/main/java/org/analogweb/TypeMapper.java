package org.analogweb;

/**
 * 指定された任意の型に変換を行うコンポーネントです。<br/>
 * 主にリクエストされたデータをエントリポイント内のメソッドの引数に変換する場合に用いられます。
 * @author snowgoose
 */
public interface TypeMapper extends Module {

    /**
     * 指定された型に変換を行います。
     * @param from 変換される以前の型(存在しない場合はnull)
     * @param requiredType 変換を行う型
     * @param formats 変換を行う際にしていされるフォーマット
     * @return 変換された型の新しいインスタンス
     */
    Object mapToType(Object from, Class<?> requiredType, String[] formats);
}
