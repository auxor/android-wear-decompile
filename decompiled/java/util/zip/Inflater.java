package java.util.zip;

import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.util.Arrays;

public class Inflater {
    private boolean finished;
    private final CloseGuard guard;
    private int inLength;
    private int inRead;
    private boolean needsDictionary;
    private long streamHandle;

    private native long createStream(boolean z);

    private native void endImpl(long j);

    private native int getAdlerImpl(long j);

    private native long getTotalInImpl(long j);

    private native long getTotalOutImpl(long j);

    private native int inflateImpl(byte[] bArr, int i, int i2, long j);

    private native void resetImpl(long j);

    private native void setDictionaryImpl(byte[] bArr, int i, int i2, long j);

    private native int setFileInputImpl(FileDescriptor fileDescriptor, long j, int i, long j2);

    private native void setInputImpl(byte[] bArr, int i, int i2, long j);

    public Inflater() {
        this(false);
    }

    public Inflater(boolean noHeader) {
        this.streamHandle = -1;
        this.guard = CloseGuard.get();
        this.streamHandle = createStream(noHeader);
        this.guard.open("end");
    }

    public synchronized void end() {
        this.guard.close();
        if (this.streamHandle != -1) {
            endImpl(this.streamHandle);
            this.inRead = 0;
            this.inLength = 0;
            this.streamHandle = -1;
        }
    }

    protected void finalize() {
        AssertionError assertionError;
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            end();
            try {
                super.finalize();
            } catch (Object t) {
                assertionError = new AssertionError(t);
            }
        } catch (Object t2) {
            assertionError = new AssertionError(t2);
        }
    }

    public synchronized boolean finished() {
        return this.finished;
    }

    public synchronized int getAdler() {
        checkOpen();
        return getAdlerImpl(this.streamHandle);
    }

    public synchronized long getBytesRead() {
        checkOpen();
        return getTotalInImpl(this.streamHandle);
    }

    public synchronized long getBytesWritten() {
        checkOpen();
        return getTotalOutImpl(this.streamHandle);
    }

    public synchronized int getRemaining() {
        return this.inLength - this.inRead;
    }

    synchronized int getCurrentOffset() {
        return this.inRead;
    }

    public synchronized int getTotalIn() {
        checkOpen();
        return (int) Math.min(getTotalInImpl(this.streamHandle), 2147483647L);
    }

    public synchronized int getTotalOut() {
        checkOpen();
        return (int) Math.min(getTotalOutImpl(this.streamHandle), 2147483647L);
    }

    public int inflate(byte[] buf) throws DataFormatException {
        return inflate(buf, 0, buf.length);
    }

    public synchronized int inflate(byte[] buf, int offset, int byteCount) throws DataFormatException {
        int i = 0;
        synchronized (this) {
            Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
            checkOpen();
            if (!needsInput()) {
                boolean neededDict = this.needsDictionary;
                this.needsDictionary = false;
                i = inflateImpl(buf, offset, byteCount, this.streamHandle);
                if (this.needsDictionary && neededDict) {
                    throw new DataFormatException("Needs dictionary");
                }
            }
        }
        return i;
    }

    public synchronized boolean needsDictionary() {
        return this.needsDictionary;
    }

    public synchronized boolean needsInput() {
        return this.inRead == this.inLength;
    }

    public synchronized void reset() {
        checkOpen();
        this.finished = false;
        this.needsDictionary = false;
        this.inRead = 0;
        this.inLength = 0;
        resetImpl(this.streamHandle);
    }

    public synchronized void setDictionary(byte[] dictionary) {
        setDictionary(dictionary, 0, dictionary.length);
    }

    public synchronized void setDictionary(byte[] dictionary, int offset, int byteCount) {
        checkOpen();
        Arrays.checkOffsetAndCount(dictionary.length, offset, byteCount);
        setDictionaryImpl(dictionary, offset, byteCount, this.streamHandle);
    }

    public synchronized void setInput(byte[] buf) {
        setInput(buf, 0, buf.length);
    }

    public synchronized void setInput(byte[] buf, int offset, int byteCount) {
        checkOpen();
        Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
        this.inRead = 0;
        this.inLength = byteCount;
        setInputImpl(buf, offset, byteCount, this.streamHandle);
    }

    synchronized int setFileInput(FileDescriptor fd, long offset, int byteCount) {
        checkOpen();
        this.inRead = 0;
        this.inLength = setFileInputImpl(fd, offset, byteCount, this.streamHandle);
        return this.inLength;
    }

    private void checkOpen() {
        if (this.streamHandle == -1) {
            throw new IllegalStateException("attempt to use Inflater after calling end");
        }
    }
}
