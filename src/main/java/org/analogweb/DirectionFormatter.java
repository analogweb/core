package org.analogweb;

public interface DirectionFormatter {

    void formatAndWriteInto(RequestContext writeTo, String charset, Object source);

}
