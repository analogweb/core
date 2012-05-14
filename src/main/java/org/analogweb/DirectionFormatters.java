package org.analogweb;

public interface DirectionFormatters {

    DirectionFormatter getFormatter(Class<? extends DirectionFormatterAware<Direction>> awareClass);

}
