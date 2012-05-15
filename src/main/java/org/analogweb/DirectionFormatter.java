package org.analogweb;

public interface DirectionFormatter extends MultiModule {

    void formatAndWriteInto(RequestContext writeTo, String charset, Object source);

}
