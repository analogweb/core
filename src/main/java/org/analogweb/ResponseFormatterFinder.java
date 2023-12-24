package org.analogweb;

public interface ResponseFormatterFinder {
    ResponseFormatter findResponseFormatter(Class<? extends Renderable> clazz);
}
