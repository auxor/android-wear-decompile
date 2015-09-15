package java.nio;

import android.system.ErrnoException;
import android.system.OsConstants;
import java.nio.channels.FileChannel.MapMode;
import libcore.io.Libcore;

public abstract class MappedByteBuffer extends ByteBuffer {
    final MemoryBlock block;
    final MapMode mapMode;

    MappedByteBuffer(MemoryBlock block, int capacity, MapMode mapMode, long effectiveDirectAddress) {
        super(capacity, effectiveDirectAddress);
        this.mapMode = mapMode;
        this.block = block;
    }

    public final boolean isLoaded() {
        checkIsMapped();
        long address = this.block.toLong();
        long size = this.block.getSize();
        if (size == 0) {
            return true;
        }
        try {
            int pageSize = (int) Libcore.os.sysconf(OsConstants._SC_PAGE_SIZE);
            int pageOffset = (int) (address % ((long) pageSize));
            size += (long) pageOffset;
            byte[] vector = new byte[((int) (((((long) pageSize) + size) - 1) / ((long) pageSize)))];
            Libcore.os.mincore(address - ((long) pageOffset), size, vector);
            for (byte b : vector) {
                if ((b & 1) != 1) {
                    return false;
                }
            }
            return true;
        } catch (ErrnoException e) {
            return false;
        }
    }

    public final MappedByteBuffer load() {
        checkIsMapped();
        try {
            Libcore.os.mlock(this.block.toLong(), this.block.getSize());
            Libcore.os.munlock(this.block.toLong(), this.block.getSize());
        } catch (ErrnoException e) {
        }
        return this;
    }

    public final MappedByteBuffer force() {
        checkIsMapped();
        if (this.mapMode == MapMode.READ_WRITE) {
            try {
                Libcore.os.msync(this.block.toLong(), this.block.getSize(), OsConstants.MS_SYNC);
            } catch (Object errnoException) {
                throw new AssertionError(errnoException);
            }
        }
        return this;
    }

    private void checkIsMapped() {
        if (this.mapMode == null) {
            throw new UnsupportedOperationException();
        }
    }
}
