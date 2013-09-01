package org.analogweb.core;

import java.io.Serializable;

import org.analogweb.util.StringUtils;


/**
 * @author snowgoose
 */
@Deprecated
public class ApplicationSpecifier implements Serializable {

    private static final long serialVersionUID = 8224555329789644389L;
    public static final ApplicationSpecifier NONE = new ApplicationSpecifier("");
    public static final ApplicationSpecifier DEFAULT = new ApplicationSpecifier(".rn");
    private final String suffix;
    private boolean suffixNone;

    public static ApplicationSpecifier valueOf(String suffix) {
        // TODO cache
        return new ApplicationSpecifier(suffix);
    }

    public ApplicationSpecifier(String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            this.suffixNone = true;
        }
        if (notStartWithComma(suffix) && this.suffixNone == false) {
            throw new RuntimeException();
        }
        this.suffix = suffix;
    }

    private boolean notStartWithComma(String suffix) {
        return (suffix.indexOf('.') != 0);
    }

    public String getSuffix() {
        return this.suffix;
    }

    public boolean match(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (isRootPath(path)) {
            return false;
        }
        String tmp = path.substring(path.lastIndexOf('/'));
        if (suffixNone) {
            return (tmp.indexOf('.') == -1);
        } else {
            return tmp.endsWith(getSuffix());
        }
    }

    private boolean isRootPath(String path) {
        return "/".equals(path);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ApplicationSpecifier) {
            ApplicationSpecifier other = ApplicationSpecifier.class.cast(obj);
            return (other.suffixNone == suffixNone && other.suffix.equals(suffix));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return suffix.hashCode();
    }

    @Override
    public String toString() {
        return suffix;
    }

}
