package java.nio;

import android.system.ErrnoException;
import android.system.OsConstants;
import android.system.StructFlock;
import android.util.MutableLong;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import libcore.io.Libcore;

final class FileChannelImpl extends FileChannel {
    private static final Comparator<FileLock> LOCK_COMPARATOR;
    private final FileDescriptor fd;
    private final Closeable ioObject;
    private final SortedSet<FileLock> locks;
    private final int mode;

    private static final class FileLockImpl extends FileLock {
        private boolean isReleased;

        public FileLockImpl(java.nio.channels.FileChannel r1, long r2, long r4, boolean r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.FileChannelImpl.FileLockImpl.<init>(java.nio.channels.FileChannel, long, long, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.FileChannelImpl.FileLockImpl.<init>(java.nio.channels.FileChannel, long, long, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.FileLockImpl.<init>(java.nio.channels.FileChannel, long, long, boolean):void");
        }

        public boolean isValid() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.FileChannelImpl.FileLockImpl.isValid():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.FileChannelImpl.FileLockImpl.isValid():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.FileLockImpl.isValid():boolean");
        }

        public void release() throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.FileChannelImpl.FileLockImpl.release():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.FileChannelImpl.FileLockImpl.release():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.FileChannelImpl.FileLockImpl.release():void");
        }
    }

    static {
        LOCK_COMPARATOR = new Comparator<FileLock>() {
            public int compare(FileLock lock1, FileLock lock2) {
                long position1 = lock1.position();
                long position2 = lock2.position();
                if (position1 > position2) {
                    return 1;
                }
                return position1 < position2 ? -1 : 0;
            }
        };
    }

    public FileChannelImpl(Closeable ioObject, FileDescriptor fd, int mode) {
        this.locks = new TreeSet(LOCK_COMPARATOR);
        this.fd = fd;
        this.ioObject = ioObject;
        this.mode = mode;
    }

    private void checkOpen() throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
    }

    private void checkReadable() {
        if ((this.mode & OsConstants.O_ACCMODE) == OsConstants.O_WRONLY) {
            throw new NonReadableChannelException();
        }
    }

    private void checkWritable() {
        if ((this.mode & OsConstants.O_ACCMODE) == OsConstants.O_RDONLY) {
            throw new NonWritableChannelException();
        }
    }

    protected void implCloseChannel() throws IOException {
        this.ioObject.close();
    }

    private FileLock basicLock(long position, long size, boolean shared, boolean wait) throws IOException {
        int accessMode = this.mode & OsConstants.O_ACCMODE;
        if (accessMode == OsConstants.O_RDONLY) {
            if (!shared) {
                throw new NonWritableChannelException();
            }
        } else if (accessMode == OsConstants.O_WRONLY && shared) {
            throw new NonReadableChannelException();
        }
        if (position < 0 || size < 0) {
            throw new IllegalArgumentException("position=" + position + " size=" + size);
        }
        FileLock pendingLock = new FileLockImpl(this, position, size, shared);
        addLock(pendingLock);
        StructFlock flock = new StructFlock();
        flock.l_type = (short) (shared ? OsConstants.F_RDLCK : OsConstants.F_WRLCK);
        flock.l_whence = (short) OsConstants.SEEK_SET;
        flock.l_start = position;
        flock.l_len = translateLockLength(size);
        try {
            boolean success = Libcore.os.fcntlFlock(this.fd, wait ? OsConstants.F_SETLKW64 : OsConstants.F_SETLK64, flock) != -1;
            if (!success) {
                removeLock(pendingLock);
            }
            return success ? pendingLock : null;
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        } catch (Throwable th) {
            if (!false) {
                removeLock(pendingLock);
            }
        }
    }

    private static long translateLockLength(long byteCount) {
        return byteCount == Long.MAX_VALUE ? 0 : byteCount;
    }

    public final FileLock lock(long position, long size, boolean shared) throws IOException {
        checkOpen();
        FileLock resultLock = null;
        boolean completed = false;
        try {
            begin();
            resultLock = basicLock(position, size, shared, true);
            completed = true;
            return resultLock;
        } finally {
            try {
                end(completed);
            } catch (ClosedByInterruptException e) {
                throw new FileLockInterruptionException();
            }
        }
    }

    public final FileLock tryLock(long position, long size, boolean shared) throws IOException {
        checkOpen();
        return basicLock(position, size, shared, false);
    }

    public void release(FileLock lock) throws IOException {
        checkOpen();
        StructFlock flock = new StructFlock();
        flock.l_type = (short) OsConstants.F_UNLCK;
        flock.l_whence = (short) OsConstants.SEEK_SET;
        flock.l_start = lock.position();
        flock.l_len = translateLockLength(lock.size());
        try {
            Libcore.os.fcntlFlock(this.fd, OsConstants.F_SETLKW64, flock);
            removeLock(lock);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public void force(boolean metadata) throws IOException {
        checkOpen();
        if ((this.mode & OsConstants.O_ACCMODE) == OsConstants.O_RDONLY) {
            return;
        }
        if (metadata) {
            try {
                Libcore.os.fsync(this.fd);
                return;
            } catch (ErrnoException errnoException) {
                throw errnoException.rethrowAsIOException();
            }
        }
        Libcore.os.fdatasync(this.fd);
    }

    public final MappedByteBuffer map(MapMode mapMode, long position, long size) throws IOException {
        checkOpen();
        if (mapMode == null) {
            throw new NullPointerException("mapMode == null");
        } else if (position < 0 || size < 0 || size > 2147483647L) {
            throw new IllegalArgumentException("position=" + position + " size=" + size);
        } else {
            boolean z;
            int accessMode = this.mode & OsConstants.O_ACCMODE;
            if (accessMode == OsConstants.O_RDONLY) {
                if (mapMode != MapMode.READ_ONLY) {
                    throw new NonWritableChannelException();
                }
            } else if (accessMode == OsConstants.O_WRONLY) {
                throw new NonReadableChannelException();
            }
            if (position + size > size()) {
                try {
                    Libcore.os.ftruncate(this.fd, position + size);
                } catch (ErrnoException ftruncateException) {
                    if (OsConstants.S_ISREG(Libcore.os.fstat(this.fd).st_mode) || ftruncateException.errno != OsConstants.EINVAL) {
                        throw ftruncateException.rethrowAsIOException();
                    }
                } catch (ErrnoException fstatException) {
                    throw fstatException.rethrowAsIOException();
                }
            }
            long alignment = position - (position % Libcore.os.sysconf(OsConstants._SC_PAGE_SIZE));
            int offset = (int) (position - alignment);
            MemoryBlock block = MemoryBlock.mmap(this.fd, alignment, size + ((long) offset), mapMode);
            int i = (int) size;
            if (mapMode == MapMode.READ_ONLY) {
                z = true;
            } else {
                z = false;
            }
            return new DirectByteBuffer(block, i, offset, z, mapMode);
        }
    }

    public long position() throws IOException {
        checkOpen();
        try {
            return Libcore.os.lseek(this.fd, 0, OsConstants.SEEK_CUR);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public FileChannel position(long newPosition) throws IOException {
        checkOpen();
        if (newPosition < 0) {
            throw new IllegalArgumentException("position: " + newPosition);
        }
        try {
            Libcore.os.lseek(this.fd, newPosition, OsConstants.SEEK_SET);
            return this;
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public int read(ByteBuffer buffer, long position) throws IOException {
        if (position >= 0) {
            return readImpl(buffer, position);
        }
        throw new IllegalArgumentException("position: " + position);
    }

    public int read(ByteBuffer buffer) throws IOException {
        return readImpl(buffer, -1);
    }

    private int readImpl(ByteBuffer buffer, long position) throws IOException {
        boolean z = true;
        buffer.checkWritable();
        checkOpen();
        checkReadable();
        if (!buffer.hasRemaining()) {
            return 0;
        }
        int bytesRead = 0;
        try {
            begin();
            if (position == -1) {
                bytesRead = Libcore.os.read(this.fd, buffer);
            } else {
                bytesRead = Libcore.os.pread(this.fd, buffer, position);
            }
            if (bytesRead == 0) {
                bytesRead = -1;
            }
        } catch (ErrnoException errnoException) {
            if (errnoException.errno == OsConstants.EAGAIN) {
                bytesRead = 0;
            } else {
                throw errnoException.rethrowAsIOException();
            }
        } catch (Throwable th) {
            if (!false || bytesRead < 0) {
                z = false;
            }
            end(z);
        }
        if (!true || bytesRead < 0) {
            z = false;
        }
        end(z);
        return bytesRead;
    }

    private int transferIoVec(IoVec ioVec) throws IOException {
        if (ioVec.init() == 0) {
            return 0;
        }
        int bytesTransferred = 0;
        boolean completed = false;
        try {
            begin();
            bytesTransferred = ioVec.doTransfer(this.fd);
            completed = true;
            return bytesTransferred;
        } finally {
            end(completed);
        }
    }

    public long read(ByteBuffer[] buffers, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(buffers.length, offset, length);
        checkOpen();
        checkReadable();
        return (long) transferIoVec(new IoVec(buffers, offset, length, Direction.READV));
    }

    public long size() throws IOException {
        checkOpen();
        try {
            return Libcore.os.fstat(this.fd).st_size;
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        checkOpen();
        if (src.isOpen()) {
            checkWritable();
            if (position < 0 || count < 0 || count > 2147483647L) {
                throw new IllegalArgumentException("position=" + position + " count=" + count);
            } else if (position > size()) {
                return 0;
            } else {
                ByteBuffer buffer;
                if (src instanceof FileChannel) {
                    FileChannel fileSrc = (FileChannel) src;
                    long size = fileSrc.size();
                    long filePosition = fileSrc.position();
                    count = Math.min(count, size - filePosition);
                    buffer = fileSrc.map(MapMode.READ_ONLY, filePosition, count);
                    long j = filePosition + count;
                    try {
                        fileSrc.position(j);
                        j = (long) write(buffer, position);
                        return j;
                    } finally {
                        NioUtils.freeDirectBuffer(buffer);
                    }
                } else {
                    buffer = ByteBuffer.allocate((int) count);
                    src.read(buffer);
                    buffer.flip();
                    return (long) write(buffer, position);
                }
            }
        }
        throw new ClosedChannelException();
    }

    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        checkOpen();
        if (target.isOpen()) {
            checkReadable();
            if (target instanceof FileChannelImpl) {
                ((FileChannelImpl) target).checkWritable();
            }
            if (position < 0 || count < 0) {
                throw new IllegalArgumentException("position=" + position + " count=" + count);
            } else if (count == 0 || position >= size()) {
                return 0;
            } else {
                long rc;
                count = Math.min(count, size() - position);
                if (target instanceof SocketChannelImpl) {
                    FileDescriptor outFd = ((SocketChannelImpl) target).getFD();
                    try {
                        begin();
                        rc = Libcore.os.sendfile(outFd, this.fd, new MutableLong(position), count);
                        end(true);
                        return rc;
                    } catch (ErrnoException errnoException) {
                        if (errnoException.errno == OsConstants.ENOSYS || errnoException.errno == OsConstants.EINVAL) {
                            end(false);
                        } else {
                            throw errnoException.rethrowAsIOException();
                        }
                    } catch (Throwable th) {
                        end(false);
                    }
                }
                ByteBuffer buffer = null;
                try {
                    buffer = map(MapMode.READ_ONLY, position, count);
                    rc = (long) target.write(buffer);
                    return rc;
                } finally {
                    NioUtils.freeDirectBuffer(buffer);
                }
            }
        } else {
            throw new ClosedChannelException();
        }
    }

    public FileChannel truncate(long size) throws IOException {
        checkOpen();
        if (size < 0) {
            throw new IllegalArgumentException("size < 0: " + size);
        }
        checkWritable();
        if (size < size()) {
            try {
                Libcore.os.ftruncate(this.fd, size);
            } catch (ErrnoException errnoException) {
                throw errnoException.rethrowAsIOException();
            }
        }
        if (position() > size) {
            position(size);
        }
        return this;
    }

    public int write(ByteBuffer buffer, long position) throws IOException {
        if (position >= 0) {
            return writeImpl(buffer, position);
        }
        throw new IllegalArgumentException("position < 0: " + position);
    }

    public int write(ByteBuffer buffer) throws IOException {
        return writeImpl(buffer, -1);
    }

    private int writeImpl(ByteBuffer buffer, long position) throws IOException {
        checkOpen();
        checkWritable();
        if (buffer == null) {
            throw new NullPointerException("buffer == null");
        } else if (!buffer.hasRemaining()) {
            return 0;
        } else {
            try {
                int bytesWritten;
                begin();
                if (position == -1) {
                    bytesWritten = Libcore.os.write(this.fd, buffer);
                } else {
                    bytesWritten = Libcore.os.pwrite(this.fd, buffer, position);
                }
                end(true);
                return bytesWritten;
            } catch (ErrnoException errnoException) {
                throw errnoException.rethrowAsIOException();
            } catch (Throwable th) {
                end(false);
            }
        }
    }

    public long write(ByteBuffer[] buffers, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(buffers.length, offset, length);
        checkOpen();
        checkWritable();
        return (long) transferIoVec(new IoVec(buffers, offset, length, Direction.WRITEV));
    }

    static int calculateTotalRemaining(ByteBuffer[] buffers, int offset, int length, boolean copyingIn) {
        int count = 0;
        for (int i = offset; i < offset + length; i++) {
            count += buffers[i].remaining();
            if (copyingIn) {
                buffers[i].checkWritable();
            }
        }
        return count;
    }

    public FileDescriptor getFD() {
        return this.fd;
    }

    private synchronized void addLock(FileLock lock) throws OverlappingFileLockException {
        long lockEnd = lock.position() + lock.size();
        for (FileLock existingLock : this.locks) {
            if (existingLock.position() > lockEnd) {
                break;
            } else if (existingLock.overlaps(lock.position(), lock.size())) {
                throw new OverlappingFileLockException();
            }
        }
        this.locks.add(lock);
    }

    private synchronized void removeLock(FileLock lock) {
        this.locks.remove(lock);
    }
}
