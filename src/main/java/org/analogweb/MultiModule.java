package org.analogweb;

/**
 * A Definable multiple {@link Module}.
 *
 * @author snowgoose
 */
public interface MultiModule extends Module {

    interface Filter {

        <T extends MultiModule> boolean isAppreciable(T aMultiModule);
    }
}
