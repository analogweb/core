package org.analogweb.exception;

/**
 * 任意のインスタンスから、特定のフォーマットへの変換に失敗した場合にスローされる例外です。
 * @author snowgoose
 */
public class FormatFailureException extends ApplicationRuntimeException {

    private static final long serialVersionUID = -9139072067367686900L;
    private String format;
    private Object formattingObject;

    /**
     * 新しい{@link FormatFailureException}を生成します。
     * @param cause 原因となった例外
     * @param formattingObject フォーマットする対象のインスタンス
     * @param format 変換するフォーマット
     */
    public FormatFailureException(Throwable cause,Object formattingObject,String format) {
        super(cause);
        this.format = format;
        this.formattingObject = formattingObject;
    }

    /**
     * 変換しようとしていたフォーマットを取得します。
     * @return 変換しようとしていたフォーマット
     */
    public String getFormat() {
        return format;
    }

    /**
     * フォーマットする対象のインスタンスを取得します。
     * @return フォーマットする対象のインスタンス
     */
    public Object getFormattingObject() {
        return formattingObject;
    }

}
