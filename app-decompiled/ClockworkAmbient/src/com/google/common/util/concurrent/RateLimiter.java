package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;

public abstract class RateLimiter {
    double maxPermits;
    private final Object mutex;
    private long nextFreeTicketMicros;
    private final long offsetNanos;
    volatile double stableIntervalMicros;
    double storedPermits;
    private final SleepingTicker ticker;

    private static class Bursty extends RateLimiter {
        final double maxBurstSeconds;

        Bursty(SleepingTicker sleepingTicker, double d) {
            super(null);
            this.maxBurstSeconds = d;
            throw new VerifyError("bad dex opcode");
        }

        void doSetRate(double d, double d2) {
            double d3 = 0.0d;
            double d4 = this.maxPermits;
            this.maxPermits = this.maxBurstSeconds * d;
            if (d4 != 0.0d) {
                d3 = (this.storedPermits * this.maxPermits) / d4;
            }
            this.storedPermits = d3;
        }
    }

    static abstract class SleepingTicker extends Ticker {
        static final SleepingTicker SYSTEM_TICKER;

        static {
            SYSTEM_TICKER = new SleepingTicker() {
                public long read() {
                    return Ticker.systemTicker().read();
                }
            };
        }

        SleepingTicker() {
        }
    }

    private static class WarmingUp extends RateLimiter {
        private double halfPermits;
        private double slope;
        final long warmupPeriodMicros;

        WarmingUp(SleepingTicker sleepingTicker, long j, TimeUnit timeUnit) {
            super(null);
            this.warmupPeriodMicros = timeUnit.toMicros(j);
            throw new VerifyError("bad dex opcode");
        }

        void doSetRate(double d, double d2) {
            double d3 = this.maxPermits;
            this.maxPermits = ((double) this.warmupPeriodMicros) / d2;
            this.halfPermits = this.maxPermits / 2.0d;
            this.slope = ((3.0d * d2) - d2) / this.halfPermits;
            if (d3 == Double.POSITIVE_INFINITY) {
                this.storedPermits = 0.0d;
            } else {
                this.storedPermits = d3 == 0.0d ? this.maxPermits : (this.storedPermits * this.maxPermits) / d3;
            }
        }
    }

    private RateLimiter(SleepingTicker sleepingTicker) {
        this.mutex = new Object();
        this.nextFreeTicketMicros = 0;
        this.ticker = sleepingTicker;
        this.offsetNanos = sleepingTicker.read();
        throw new VerifyError("bad dex opcode");
    }

    static RateLimiter create(SleepingTicker sleepingTicker, double d) {
        RateLimiter bursty = new Bursty(sleepingTicker, 1.0d);
        bursty.setRate(d);
        return bursty;
    }

    static RateLimiter create(SleepingTicker sleepingTicker, double d, long j, TimeUnit timeUnit) {
        RateLimiter warmingUp = new WarmingUp(sleepingTicker, j, timeUnit);
        warmingUp.setRate(d);
        return warmingUp;
    }

    static RateLimiter createWithCapacity(SleepingTicker sleepingTicker, double d, long j, TimeUnit timeUnit) {
        RateLimiter bursty = new Bursty(sleepingTicker, ((double) timeUnit.toNanos(j)) / 1.0E9d);
        bursty.setRate(d);
        return bursty;
    }

    private long readSafeMicros() {
        return TimeUnit.NANOSECONDS.toMicros(this.ticker.read() - this.offsetNanos);
    }

    private void resync(long j) {
        if (j > this.nextFreeTicketMicros) {
            this.storedPermits = Math.min(this.maxPermits, this.storedPermits + (((double) (j - this.nextFreeTicketMicros)) / this.stableIntervalMicros));
            this.nextFreeTicketMicros = j;
        }
    }

    abstract void doSetRate(double d, double d2);

    public final void setRate(double d) {
        boolean z = d > 0.0d && !Double.isNaN(d);
        Preconditions.checkArgument(z, "rate must be positive");
        synchronized (this.mutex) {
            resync(readSafeMicros());
            double toMicros = ((double) TimeUnit.SECONDS.toMicros(1)) / d;
            this.stableIntervalMicros = toMicros;
            doSetRate(d, toMicros);
        }
    }

    public String toString() {
        return String.format("RateLimiter[stableRate=%3.1fqps]", new Object[]{Double.valueOf(1000000.0d / this.stableIntervalMicros)});
    }
}
