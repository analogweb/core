package org.analogweb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 文字列を加工する為のユーティリティです。
 * @author snowgoose
 */
public final class StringUtils {

    public static final String EMPTY = "";

    public static char charAt(int position, String s) {
        if (isNotEmpty(s) && -1 < position && s.length() > position) {
            return s.charAt(position);
        }
        return Character.MIN_VALUE;
    }

    public static boolean isNotEmpty(String str) {
        return (str != null && str.trim().length() != 0);
    }

    public static boolean isEmpty(String str) {
        return isNotEmpty(str) == false;
    }

    public static List<String> partition(int partitionPoint, Character partitioner, String target) {
        List<String> splitted = split(target, partitioner);
        StringBuilder buffer = new StringBuilder();
        Iterator<String> itr = splitted.iterator();
        buffer.append(itr.next());
        if (splitted.size() == 2) {
            return splitted;
        } else if (splitted.size() < 2) {
            return Arrays.asList(buffer.toString(), null);
        } else {
            int index = 1;
            while (index < partitionPoint) {
                buffer.append(partitioner).append(itr.next());
                index++;
            }
            StringBuilder afterBuffer = new StringBuilder();
            afterBuffer.append(itr.next());
            while (itr.hasNext()) {
                afterBuffer.append(partitioner).append(itr.next());
            }
            return Arrays.asList(buffer.toString(), afterBuffer.toString());
        }
    }

    public static List<String> split(String target, Character splitter) {
        List<String> result = new ArrayList<String>();
        if (isEmpty(target)) {
            return result;
        }
        int length = target.length();
        int lastIndex = 0;
        int position = 0;
        boolean matches = false;
        if (splitter == null) {
            while (position < length) {
                if (Character.isWhitespace(target.charAt(position))) {
                    if (matches) {
                        lastIndex = ++position;
                        continue;
                    }
                    result.add(target.substring(lastIndex, position));
                    lastIndex = ++position;
                    matches = true;
                    continue;
                }
                position++;
                matches = false;
            }
        } else {
            while (position < length) {
                if (target.charAt(position) == splitter) {
                    if (matches) {
                        lastIndex = ++position;
                        continue;
                    }
                    result.add(target.substring(lastIndex, position));
                    lastIndex = ++position;
                    matches = true;
                    continue;
                }
                position++;
                matches = false;
            }
        }
        result.add(target.substring(lastIndex, position));
        return result;
    }

    /**
     * 指定した索引から文字列を切り出します。 文字列の長さより大きい索引が指定された場合は
     * @param value 切り出す対象の文字列
     * @param beginIndex 文字列を切り出す索引
     * @return 索引により切り出された文字列
     */
    public static String substring(String value, int beginIndex) {
        if (value == null) {
            return null;
        }
        return substring(value, beginIndex, value.length());
    }

    /**
     * 指定した索引から文字列を切り出します。 文字列の長さより大きい索引が指定された場合は
     * @param value 切り出す対象の文字列
     * @param beginIndex 文字列を切り出す始めの索引
     * @param endIndex 文字列を切り出す終わりの索引
     * @return 索引により切り出された文字列
     */
    public static String substring(String value, int beginIndex, int endIndex) {
        if (value == null) {
            return null;
        }
        if (beginIndex > endIndex) {
            return null;
        }
        if (isEmpty(value)) {
            return EMPTY;
        }
        if (endIndex > value.length()) {
            return value.substring(beginIndex, value.length());
        } else {
            return value.substring(beginIndex, endIndex);
        }
    }

    public static String trimToEmpty(String value) {
        return isEmpty(value) ? EMPTY : value.trim();
    }

}
