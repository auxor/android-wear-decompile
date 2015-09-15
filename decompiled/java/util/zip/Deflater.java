package java.util.zip;

import dalvik.system.CloseGuard;
import java.util.Arrays;
import libcore.util.EmptyArray;

public class Deflater {
    public static final int BEST_COMPRESSION = 9;
    public static final int BEST_SPEED = 1;
    public static final int DEFAULT_COMPRESSION = -1;
    public static final int DEFAULT_STRATEGY = 0;
    public static final int DEFLATED = 8;
    public static final int FILTERED = 1;
    private static final int FINISH = 4;
    public static final int FULL_FLUSH = 3;
    public static final int HUFFMAN_ONLY = 2;
    public static final int NO_COMPRESSION = 0;
    public static final int NO_FLUSH = 0;
    public static final int SYNC_FLUSH = 2;
    private int compressLevel;
    private boolean finished;
    private int flushParm;
    private final CloseGuard guard;
    private int inLength;
    private int inRead;
    private byte[] inputBuffer;
    private int strategy;
    private long streamHandle;

    private native long createStream(int i, int i2, boolean z);

    private native int deflateImpl(byte[] bArr, int i, int i2, long j, int i3);

    private native void endImpl(long j);

    private native int getAdlerImpl(long j);

    private native long getTotalInImpl(long j);

    private native long getTotalOutImpl(long j);

    private native void resetImpl(long j);

    private native void setDictionaryImpl(byte[] bArr, int i, int i2, long j);

    private native void setInputImpl(byte[] bArr, int i, int i2, long j);

    private native void setLevelsImpl(int i, int i2, long j);

    protected void finalize() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0015 in {3, 9, 11, 13, 16, 18} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r2 = this;
        r1 = r2.guard;	 Catch:{ all -> 0x0018 }
        if (r1 == 0) goto L_0x0009;	 Catch:{ all -> 0x0018 }
    L_0x0004:
        r1 = r2.guard;	 Catch:{ all -> 0x0018 }
        r1.warnIfOpen();	 Catch:{ all -> 0x0018 }
    L_0x0009:
        monitor-enter(r2);	 Catch:{ all -> 0x0018 }
        r2.end();	 Catch:{ all -> 0x0018 }
        r2.endImpl();	 Catch:{ all -> 0x0018 }
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        super.finalize();
        return;
    L_0x0015:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        throw r1;	 Catch:{ all -> 0x0018 }
    L_0x0018:
        r1 = move-exception;
        super.finalize();	 Catch:{ Throwable -> 0x0024 }
        throw r1;
    L_0x001d:
        r0 = move-exception;
        r1 = new java.lang.AssertionError;
        r1.<init>(r0);
        throw r1;
    L_0x0024:
        r0 = move-exception;
        r1 = new java.lang.AssertionError;
        r1.<init>(r0);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.zip.Deflater.finalize():void");
    }

    public Deflater() {
        this(DEFAULT_COMPRESSION, false);
    }

    public Deflater(int level) {
        this(level, false);
    }

    public Deflater(int level, boolean noHeader) {
        this.flushParm = NO_FLUSH;
        this.compressLevel = DEFAULT_COMPRESSION;
        this.strategy = NO_FLUSH;
        this.streamHandle = -1;
        this.guard = CloseGuard.get();
        if (level < DEFAULT_COMPRESSION || level > BEST_COMPRESSION) {
            throw new IllegalArgumentException("Bad level: " + level);
        }
        this.compressLevel = level;
        this.streamHandle = createStream(this.compressLevel, this.strategy, noHeader);
        this.guard.open("end");
    }

    public int deflate(byte[] buf) {
        return deflate(buf, NO_FLUSH, buf.length);
    }

    public synchronized int deflate(byte[] buf, int offset, int byteCount) {
        return deflateImpl(buf, offset, byteCount, this.flushParm);
    }

    public synchronized int deflate(byte[] buf, int offset, int byteCount, int flush) {
        if (flush == 0 || flush == SYNC_FLUSH || flush == FULL_FLUSH) {
        } else {
            throw new IllegalArgumentException("Bad flush value: " + flush);
        }
        return deflateImpl(buf, offset, byteCount, flush);
    }

    private synchronized int deflateImpl(byte[] buf, int offset, int byteCount, int flush) {
        checkOpen();
        Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
        if (this.inputBuffer == null) {
            setInput(EmptyArray.BYTE);
        }
        return deflateImpl(buf, offset, byteCount, this.streamHandle, flush);
    }

    public synchronized void end() {
        this.guard.close();
        endImpl();
    }

    private void endImpl() {
        if (this.streamHandle != -1) {
            endImpl(this.streamHandle);
            this.inputBuffer = null;
            this.streamHandle = -1;
        }
    }

    public synchronized void finish() {
        this.flushParm = FINISH;
    }

    public synchronized boolean finished() {
        return this.finished;
    }

    public synchronized int getAdler() {
        checkOpen();
        return getAdlerImpl(this.streamHandle);
    }

    public synchronized int getTotalIn() {
        checkOpen();
        return (int) getTotalInImpl(this.streamHandle);
    }

    public synchronized int getTotalOut() {
        checkOpen();
        return (int) getTotalOutImpl(this.streamHandle);
    }

    public synchronized boolean needsInput() {
        boolean z = true;
        synchronized (this) {
            if (this.inputBuffer != null) {
                if (this.inRead != this.inLength) {
                    z = false;
                }
            }
        }
        return z;
    }

    public synchronized void reset() {
        checkOpen();
        this.flushParm = NO_FLUSH;
        this.finished = false;
        resetImpl(this.streamHandle);
        this.inputBuffer = null;
    }

    public void setDictionary(byte[] dictionary) {
        setDictionary(dictionary, NO_FLUSH, dictionary.length);
    }

    public synchronized void setDictionary(byte[] buf, int offset, int byteCount) {
        checkOpen();
        Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
        setDictionaryImpl(buf, offset, byteCount, this.streamHandle);
    }

    public void setInput(byte[] buf) {
        setInput(buf, NO_FLUSH, buf.length);
    }

    public synchronized void setInput(byte[] buf, int offset, int byteCount) {
        checkOpen();
        Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
        this.inLength = byteCount;
        this.inRead = NO_FLUSH;
        if (this.inputBuffer == null) {
            setLevelsImpl(this.compressLevel, this.strategy, this.streamHandle);
        }
        this.inputBuffer = buf;
        setInputImpl(buf, offset, byteCount, this.streamHandle);
    }

    public synchronized void setLevel(int level) {
        if (level < DEFAULT_COMPRESSION || level > BEST_COMPRESSION) {
            throw new IllegalArgumentException("Bad level: " + level);
        } else if (this.inputBuffer != null) {
            throw new IllegalStateException("setLevel cannot be called after setInput");
        } else {
            this.compressLevel = level;
        }
    }

    public synchronized void setStrategy(int strategy) {
        if (strategy < 0 || strategy > SYNC_FLUSH) {
            throw new IllegalArgumentException("Bad strategy: " + strategy);
        } else if (this.inputBuffer != null) {
            throw new IllegalStateException("setStrategy cannot be called after setInput");
        } else {
            this.strategy = strategy;
        }
    }

    public synchronized long getBytesRead() {
        checkOpen();
        return getTotalInImpl(this.streamHandle);
    }

    public synchronized long getBytesWritten() {
        checkOpen();
        return getTotalOutImpl(this.streamHandle);
    }

    private void checkOpen() {
        if (this.streamHandle == -1) {
            throw new IllegalStateException("attempt to use Deflater after calling end");
        }
    }
}
