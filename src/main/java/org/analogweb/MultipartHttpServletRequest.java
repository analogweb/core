package org.analogweb;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * マルチパートによるリクエストを表す{@link HttpServletRequest}
 * @author snowgoose
 */
public interface MultipartHttpServletRequest extends HttpServletRequest {

    /**
     * 現在のリクエストにおける{@link MultipartParameters}を取得する。
     * @return {@link MultipartParameters}
     */
    MultipartParameters getMultipartParameters();

    /**
     * 指定されたキーに一致する{@link MultipartFile}を取得する。
     * @param name {@link MultipartFile}パラメータを表すキー
     * @return {@link MultipartFile}
     */
    MultipartFile getFileParameter(String name);

    /**
     * {@link MultipartFile}を取得する事が可能な、全てのキー名
     * を取得する．
     * @return {@link MultipartFile}を取得する事が可能な全てのキー名
     */
    Collection<String> getFileParameterNames();

    /**
     * 現在のリクエストに存在する全ての{@link MultipartFile}で取得可能な
     * 値を取得する。
     * @param name キー名
     * @return 全ての{@link MultipartFile}で取得可能な値
     */
    List<MultipartFile> getFileParameterValues(String name);

    /**
     * 指定したキーに一致する全ての{@link MultipartFile}で取得可能な
     * 値を取得する。
     * @return キーに一致する全ての{@link MultipartFile}で取得可能な値
     */
    Map<String,MultipartFile[]> getFileParameterMap();

}
