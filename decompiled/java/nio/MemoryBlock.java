package java.nio;

import android.system.ErrnoException;
import android.system.OsConstants;
import dalvik.system.VMRuntime;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.FileChannel.MapMode;
import libcore.io.Memory;

class MemoryBlock {
    private boolean accessible;
    protected long address;
    private boolean freed;
    protected final long size;

    private static class MemoryMappedBlock extends MemoryBlock {
        protected void finalize() throws java.lang.Throwable {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.MemoryBlock.MemoryMappedBlock.finalize():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.MemoryBlock.MemoryMappedBlock.finalize():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.MemoryBlock.MemoryMappedBlock.finalize():void");
        }

        public void free() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.MemoryBlock.MemoryMappedBlock.free():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.MemoryBlock.MemoryMappedBlock.free():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.MemoryBlock.MemoryMappedBlock.free():void");
        }

        private MemoryMappedBlock(long address, long byteCount) {
            super(byteCount, null);
        }
    }

    private static class NonMovableHeapBlock extends MemoryBlock {
        private byte[] array;

        private NonMovableHeapBlock(byte[] array, long address, long byteCount) {
            super(byteCount, null);
            this.array = array;
        }

        public byte[] array() {
            return this.array;
        }

        public void free() {
            this.array = null;
            super.free();
        }
    }

    private static class UnmanagedBlock extends MemoryBlock {
        private UnmanagedBlock(long address, long byteCount) {
            super(byteCount, null);
        }
    }

    public static MemoryBlock mmap(FileDescriptor fd, long offset, long size, MapMode mapMode) throws IOException {
        if (size == 0) {
            return new MemoryBlock(0, 0);
        }
        if (offset < 0 || size < 0 || offset > 2147483647L || size > 2147483647L) {
            throw new IllegalArgumentException("offset=" + offset + " size=" + size);
        }
        int prot;
        int flags;
        if (mapMode == MapMode.PRIVATE) {
            prot = OsConstants.PROT_READ | OsConstants.PROT_WRITE;
            flags = OsConstants.MAP_PRIVATE;
        } else if (mapMode == MapMode.READ_ONLY) {
            prot = OsConstants.PROT_READ;
            flags = OsConstants.MAP_SHARED;
        } else {
            prot = OsConstants.PROT_READ | OsConstants.PROT_WRITE;
            flags = OsConstants.MAP_SHARED;
        }
        try {
            return new MemoryMappedBlock(size, null);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public static MemoryBlock allocate(int byteCount) {
        VMRuntime runtime = VMRuntime.getRuntime();
        byte[] array = (byte[]) runtime.newNonMovableArray(Byte.TYPE, byteCount);
        return new NonMovableHeapBlock(runtime.addressOf(array), (long) byteCount, null);
    }

    public static MemoryBlock wrapFromJni(long address, long byteCount) {
        return new UnmanagedBlock(byteCount, null);
    }

    private MemoryBlock(long address, long size) {
        this.address = address;
        this.size = size;
        this.accessible = true;
        this.freed = false;
    }

    public byte[] array() {
        return null;
    }

    public void free() {
        this.address = 0;
        this.freed = true;
    }

    public boolean isFreed() {
        return this.freed;
    }

    public boolean isAccessible() {
        return !isFreed() && this.accessible;
    }

    public final void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public final void pokeByte(int offset, byte value) {
        Memory.pokeByte(this.address + ((long) offset), value);
    }

    public final void pokeByteArray(int offset, byte[] src, int srcOffset, int byteCount) {
        Memory.pokeByteArray(this.address + ((long) offset), src, srcOffset, byteCount);
    }

    public final void pokeCharArray(int offset, char[] src, int srcOffset, int charCount, boolean swap) {
        Memory.pokeCharArray(this.address + ((long) offset), src, srcOffset, charCount, swap);
    }

    public final void pokeDoubleArray(int offset, double[] src, int srcOffset, int doubleCount, boolean swap) {
        Memory.pokeDoubleArray(this.address + ((long) offset), src, srcOffset, doubleCount, swap);
    }

    public final void pokeFloatArray(int offset, float[] src, int srcOffset, int floatCount, boolean swap) {
        Memory.pokeFloatArray(this.address + ((long) offset), src, srcOffset, floatCount, swap);
    }

    public final void pokeIntArray(int offset, int[] src, int srcOffset, int intCount, boolean swap) {
        Memory.pokeIntArray(this.address + ((long) offset), src, srcOffset, intCount, swap);
    }

    public final void pokeLongArray(int offset, long[] src, int srcOffset, int longCount, boolean swap) {
        Memory.pokeLongArray(this.address + ((long) offset), src, srcOffset, longCount, swap);
    }

    public final void pokeShortArray(int offset, short[] src, int srcOffset, int shortCount, boolean swap) {
        Memory.pokeShortArray(this.address + ((long) offset), src, srcOffset, shortCount, swap);
    }

    public final byte peekByte(int offset) {
        return Memory.peekByte(this.address + ((long) offset));
    }

    public final void peekByteArray(int offset, byte[] dst, int dstOffset, int byteCount) {
        Memory.peekByteArray(this.address + ((long) offset), dst, dstOffset, byteCount);
    }

    public final void peekCharArray(int offset, char[] dst, int dstOffset, int charCount, boolean swap) {
        Memory.peekCharArray(this.address + ((long) offset), dst, dstOffset, charCount, swap);
    }

    public final void peekDoubleArray(int offset, double[] dst, int dstOffset, int doubleCount, boolean swap) {
        Memory.peekDoubleArray(this.address + ((long) offset), dst, dstOffset, doubleCount, swap);
    }

    public final void peekFloatArray(int offset, float[] dst, int dstOffset, int floatCount, boolean swap) {
        Memory.peekFloatArray(this.address + ((long) offset), dst, dstOffset, floatCount, swap);
    }

    public final void peekIntArray(int offset, int[] dst, int dstOffset, int intCount, boolean swap) {
        Memory.peekIntArray(this.address + ((long) offset), dst, dstOffset, intCount, swap);
    }

    public final void peekLongArray(int offset, long[] dst, int dstOffset, int longCount, boolean swap) {
        Memory.peekLongArray(this.address + ((long) offset), dst, dstOffset, longCount, swap);
    }

    public final void peekShortArray(int offset, short[] dst, int dstOffset, int shortCount, boolean swap) {
        Memory.peekShortArray(this.address + ((long) offset), dst, dstOffset, shortCount, swap);
    }

    public final void pokeShort(int offset, short value, ByteOrder order) {
        Memory.pokeShort(this.address + ((long) offset), value, order.needsSwap);
    }

    public final short peekShort(int offset, ByteOrder order) {
        return Memory.peekShort(this.address + ((long) offset), order.needsSwap);
    }

    public final void pokeInt(int offset, int value, ByteOrder order) {
        Memory.pokeInt(this.address + ((long) offset), value, order.needsSwap);
    }

    public final int peekInt(int offset, ByteOrder order) {
        return Memory.peekInt(this.address + ((long) offset), order.needsSwap);
    }

    public final void pokeLong(int offset, long value, ByteOrder order) {
        Memory.pokeLong(this.address + ((long) offset), value, order.needsSwap);
    }

    public final long peekLong(int offset, ByteOrder order) {
        return Memory.peekLong(this.address + ((long) offset), order.needsSwap);
    }

    public final long toLong() {
        return this.address;
    }

    public final String toString() {
        return getClass().getName() + "[" + this.address + "]";
    }

    public final long getSize() {
        return this.size;
    }
}
