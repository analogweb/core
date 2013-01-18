package org.analogweb.util;

/**
 * @author snowgoose
 */
public class StopWatch {

    private static final long DIVISION = 1000 * 1000;
    private long startTime;
    private long stopTime;
    private boolean running;
    private Ticker ticker = Ticker.SYSTEM;

    public StopWatch() {
        // nop.
    }

    public StopWatch(Ticker ticker) {
        this.ticker = ticker;
    }

    public void start() {
        if (running == false) {
            this.startTime = ticker.now();
            this.running = true;
        }
    }

    public long stop() {
        if (running == false) {
            throw new IllegalStateException("not run yet.");
        }
        this.stopTime = ticker.now();
        this.running = false;
        return (this.stopTime - this.startTime) / DIVISION;
    }

    public static interface Ticker {
        Ticker SYSTEM = new Ticker() {
            @Override
            public long now() {
                return System.nanoTime();
            }
        };

        long now();
    }

}
