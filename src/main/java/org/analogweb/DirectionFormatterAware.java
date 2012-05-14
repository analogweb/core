package org.analogweb;

/**
 * @author snowgoose
 */
public interface DirectionFormatterAware<T extends Direction>{
    
    T attach(DirectionFormatter formatter);

}
