package org.analogweb.junit;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author snowgoose
 */
public abstract class NoDescribeMatcher<T> extends BaseMatcher<T> {

    @Override
    public void describeTo(Description arg0) {
        // nop.
    }

}
