package org.analogweb.util;

import java.util.ArrayList;

public final class Lists {

    private Lists(){
        // nop.
    }

    public static <T> ArrayList<T> array() {
        return new ArrayList<T>();
    }

}
