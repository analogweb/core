package org.analogweb.core;

import java.util.List;
import java.util.Map;

import org.analogweb.AttributesHandler;
import org.analogweb.AttributesHandlers;
import org.analogweb.util.Maps;

public class DefaultAttributesHandlers implements AttributesHandlers {

    private Map<String, AttributesHandler> attributesHandlerMap;
    private String defaultHandlerName = "parameter";

    public DefaultAttributesHandlers(List<AttributesHandler> handlers) {
        this.attributesHandlerMap = Maps.newConcurrentHashMap();
        for (AttributesHandler resolver : handlers) {
            attributesHandlerMap.put(resolver.getScopeName(), resolver);
        }
    }

    @Override
    public AttributesHandler get(String name) {
        AttributesHandler ha = attributesHandlerMap.get(name);
        if (ha == null) {
            return this.attributesHandlerMap.get(getDefaultHandlerName());
        }
        return ha;
    }

    protected String getDefaultHandlerName() {
        return this.defaultHandlerName;
    }

}
