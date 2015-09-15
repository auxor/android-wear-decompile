package java.util;

import java.io.Serializable;

public class Random implements Serializable {
    private static final long multiplier = 25214903917L;
    private static volatile long seedBase = 0;
    private static final long serialVersionUID = 3905348978240129619L;
    private boolean haveNextNextGaussian;
    private double nextNextGaussian;
    private long seed;

    static {
        seedBase = 0;
    }

    public Random() {
        setSeed(System.nanoTime() + seedBase);
        seedBase++;
    }

    public Random(long seed) {
        setSeed(seed);
    }

    protected synchronized int next(int bits) {
        this.seed = ((this.seed * multiplier) + 11) & 281474976710655L;
        return (int) (this.seed >>> (48 - bits));
    }

    public boolean nextBoolean() {
        return next(1) != 0;
    }

    public void nextBytes(byte[] buf) {
        int rand = 0;
        int count = 0;
        int loop = 0;
        while (count < buf.length) {
            if (loop == 0) {
                rand = nextInt();
                loop = 3;
            } else {
                loop--;
            }
            int count2 = count + 1;
            buf[count] = (byte) rand;
            rand >>= 8;
            count = count2;
        }
    }

    public double nextDouble() {
        return ((double) ((((long) next(26)) << 27) + ((long) next(27)))) / 9.007199254740992E15d;
    }

    public float nextFloat() {
        return ((float) next(24)) / 1.6777216E7f;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized double nextGaussian() {
        /*
        r12 = this;
        monitor-enter(r12);
        r8 = r12.haveNextNextGaussian;	 Catch:{ all -> 0x004a }
        if (r8 == 0) goto L_0x000c;
    L_0x0005:
        r8 = 0;
        r12.haveNextNextGaussian = r8;	 Catch:{ all -> 0x004a }
        r8 = r12.nextNextGaussian;	 Catch:{ all -> 0x004a }
    L_0x000a:
        monitor-exit(r12);
        return r8;
    L_0x000c:
        r8 = 4611686018427387904; // 0x4000000000000000 float:0.0 double:2.0;
        r10 = r12.nextDouble();	 Catch:{ all -> 0x004a }
        r8 = r8 * r10;
        r10 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r4 = r8 - r10;
        r8 = 4611686018427387904; // 0x4000000000000000 float:0.0 double:2.0;
        r10 = r12.nextDouble();	 Catch:{ all -> 0x004a }
        r8 = r8 * r10;
        r10 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r6 = r8 - r10;
        r8 = r4 * r4;
        r10 = r6 * r6;
        r2 = r8 + r10;
        r8 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r8 >= 0) goto L_0x000c;
    L_0x002e:
        r8 = 0;
        r8 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r8 == 0) goto L_0x000c;
    L_0x0034:
        r8 = -4611686018427387904; // 0xc000000000000000 float:0.0 double:-2.0;
        r10 = java.lang.StrictMath.log(r2);	 Catch:{ all -> 0x004a }
        r8 = r8 * r10;
        r8 = r8 / r2;
        r0 = java.lang.StrictMath.sqrt(r8);	 Catch:{ all -> 0x004a }
        r8 = r6 * r0;
        r12.nextNextGaussian = r8;	 Catch:{ all -> 0x004a }
        r8 = 1;
        r12.haveNextNextGaussian = r8;	 Catch:{ all -> 0x004a }
        r8 = r4 * r0;
        goto L_0x000a;
    L_0x004a:
        r8 = move-exception;
        monitor-exit(r12);
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Random.nextGaussian():double");
    }

    public int nextInt() {
        return next(32);
    }

    public int nextInt(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n <= 0: " + n);
        } else if (((-n) & n) == n) {
            return (int) ((((long) n) * ((long) next(31))) >> 31);
        } else {
            int val;
            int bits;
            do {
                bits = next(31);
                val = bits % n;
            } while ((bits - val) + (n - 1) < 0);
            return val;
        }
    }

    public long nextLong() {
        return (((long) next(32)) << 32) + ((long) next(32));
    }

    public synchronized void setSeed(long seed) {
        this.seed = (multiplier ^ seed) & 281474976710655L;
        this.haveNextNextGaussian = false;
    }
}
