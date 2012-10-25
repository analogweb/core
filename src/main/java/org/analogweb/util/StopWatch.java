package org.analogweb.util;

/**
 * @author snowgoose
 */
public class StopWatch {

    private static final long DIVISION = 1000 * 1000;
    private long startTime;
    private long stopTime;
    private boolean running;

    public void start() {
        if (running == false) {
            this.startTime = System.nanoTime();
            this.running = true;
        }
    }

    public long stop() {
        if (running == false) {
            throw new IllegalStateException("not run yet.");
        }
        this.stopTime = System.nanoTime();
        this.running = false;
        return (this.stopTime - this.startTime) / DIVISION;
    }

}
