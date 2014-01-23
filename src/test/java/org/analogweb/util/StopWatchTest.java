package org.analogweb.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.analogweb.util.StopWatch.Ticker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StopWatchTest {

    private StopWatch sw;
    private Ticker ticker;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        this.ticker = mock(Ticker.class);
        sw = new StopWatch(ticker);
    }

    @Test
    public void test() throws Exception {
        when(ticker.now()).thenReturn(nanosec(2), nanosec(3));
        sw.start();
        long actual = sw.stop();
        assertThat(actual, is(1L));
    }

    @Test
    public void testStopFirst() throws Exception {
        thrown.expect(IllegalStateException.class);
        sw.stop();
    }

    @Test
    public void testStartTwice() throws Exception {
        when(ticker.now()).thenReturn(nanosec(2), nanosec(3));
        sw.start();
        // continues silently.
        sw.start();
        TimeUnit.MILLISECONDS.sleep(100L);
        long actual = sw.stop();
        assertThat(actual, is(1L));
    }

    private long nanosec(long millsec) {
        return millsec * 1000 * 1000;
    }
}
