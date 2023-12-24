package org.analogweb.junit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ExceptionCauseMatchers {

    public static As as(Class<? extends Throwable> t) {
        return as(t, Causes.EMPTY);
    }

    public static As as(Class<? extends Throwable> t, Causes causes) {
        return new As(t, causes);
    }

    @SuppressWarnings("unchecked")
    public static Causes causedBy(Class<? extends Throwable> t) {
        return causedBy(new Class[] { t });
    }

    @SuppressWarnings("unchecked")
    public static Causes causedBy(Class<? extends Throwable>... t) {
        return new Causes(t);
    }

    static class As extends BaseMatcher<Throwable> {

        private Causes causes;
        private Class<? extends Throwable> t;

        As(Class<? extends Throwable> t, Causes causes) {
            this.t = t;
            this.causes = causes;
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof Throwable) {
                Throwable e = (Throwable) item;
                if (t.isInstance(e)) {
                    if (causes.causes().isEmpty()) {
                        return true;
                    }
                    Throwable current = e.getCause();
                    for (Class<? extends Throwable> t : causes.causes()) {
                        if (current == null || t.isInstance(current) == false) {
                            this.t = t;
                            return false;
                        }
                        current = current.getCause();
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendValue(t);
        }
    }

    static class Causes {

        @SuppressWarnings("unchecked")
        static final Causes EMPTY = new Causes();
        private List<Class<? extends Throwable>> causes;

        @SuppressWarnings("unchecked")
        Causes(Class<? extends Throwable>... classes) {
            if (classes != null) {
                this.causes = Arrays.asList(classes);
            } else {
                this.causes = Collections.emptyList();
            }
        }

        public List<Class<? extends Throwable>> causes() {
            return this.causes;
        }
    }
}
