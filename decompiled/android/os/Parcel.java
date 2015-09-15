package android.os;

import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.inputmethod.EditorInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Parcel {
    private static final boolean DEBUG_ARRAY_MAP = false;
    private static final boolean DEBUG_RECYCLE = false;
    private static final int EX_BAD_PARCELABLE = -2;
    private static final int EX_HAS_REPLY_HEADER = -128;
    private static final int EX_ILLEGAL_ARGUMENT = -3;
    private static final int EX_ILLEGAL_STATE = -5;
    private static final int EX_NETWORK_MAIN_THREAD = -6;
    private static final int EX_NULL_POINTER = -4;
    private static final int EX_SECURITY = -1;
    private static final int EX_UNSUPPORTED_OPERATION = -7;
    private static final int POOL_SIZE = 6;
    public static final Creator<String> STRING_CREATOR = null;
    private static final String TAG = "Parcel";
    private static final int VAL_BOOLEAN = 9;
    private static final int VAL_BOOLEANARRAY = 23;
    private static final int VAL_BUNDLE = 3;
    private static final int VAL_BYTE = 20;
    private static final int VAL_BYTEARRAY = 13;
    private static final int VAL_CHARSEQUENCE = 10;
    private static final int VAL_CHARSEQUENCEARRAY = 24;
    private static final int VAL_DOUBLE = 8;
    private static final int VAL_FLOAT = 7;
    private static final int VAL_IBINDER = 15;
    private static final int VAL_INTARRAY = 18;
    private static final int VAL_INTEGER = 1;
    private static final int VAL_LIST = 11;
    private static final int VAL_LONG = 6;
    private static final int VAL_LONGARRAY = 19;
    private static final int VAL_MAP = 2;
    private static final int VAL_NULL = -1;
    private static final int VAL_OBJECTARRAY = 17;
    private static final int VAL_PARCELABLE = 4;
    private static final int VAL_PARCELABLEARRAY = 16;
    private static final int VAL_PERSISTABLEBUNDLE = 25;
    private static final int VAL_SERIALIZABLE = 21;
    private static final int VAL_SHORT = 5;
    private static final int VAL_SIZE = 26;
    private static final int VAL_SIZEF = 27;
    private static final int VAL_SPARSEARRAY = 12;
    private static final int VAL_SPARSEBOOLEANARRAY = 22;
    private static final int VAL_STRING = 0;
    private static final int VAL_STRINGARRAY = 14;
    private static final HashMap<ClassLoader, HashMap<String, Creator>> mCreators = null;
    private static final Parcel[] sHolderPool = null;
    private static final Parcel[] sOwnedPool = null;
    private long mNativePtr;
    private boolean mOwnsNativeParcelObject;
    private RuntimeException mStack;

    /* renamed from: android.os.Parcel.2 */
    class AnonymousClass2 extends ObjectInputStream {
        final /* synthetic */ Parcel this$0;
        final /* synthetic */ ClassLoader val$loader;

        AnonymousClass2(android.os.Parcel r1, java.io.InputStream r2, java.lang.ClassLoader r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.Parcel.2.<init>(android.os.Parcel, java.io.InputStream, java.lang.ClassLoader):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.Parcel.2.<init>(android.os.Parcel, java.io.InputStream, java.lang.ClassLoader):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.Parcel.2.<init>(android.os.Parcel, java.io.InputStream, java.lang.ClassLoader):void");
        }

        protected java.lang.Class<?> resolveClass(java.io.ObjectStreamClass r1) throws java.io.IOException, java.lang.ClassNotFoundException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.Parcel.2.resolveClass(java.io.ObjectStreamClass):java.lang.Class<?>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.Parcel.2.resolveClass(java.io.ObjectStreamClass):java.lang.Class<?>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.Parcel.2.resolveClass(java.io.ObjectStreamClass):java.lang.Class<?>");
        }
    }

    static native void clearFileDescriptor(FileDescriptor fileDescriptor);

    static native void closeFileDescriptor(FileDescriptor fileDescriptor) throws IOException;

    static native FileDescriptor dupFileDescriptor(FileDescriptor fileDescriptor) throws IOException;

    public static native long getGlobalAllocCount();

    public static native long getGlobalAllocSize();

    private static native void nativeAppendFrom(long j, long j2, int i, int i2);

    private static native long nativeCreate();

    private static native byte[] nativeCreateByteArray(long j);

    private static native int nativeDataAvail(long j);

    private static native int nativeDataCapacity(long j);

    private static native int nativeDataPosition(long j);

    private static native int nativeDataSize(long j);

    private static native void nativeDestroy(long j);

    private static native void nativeEnforceInterface(long j, String str);

    private static native void nativeFreeBuffer(long j);

    private static native boolean nativeHasFileDescriptors(long j);

    private static native byte[] nativeMarshall(long j);

    private static native boolean nativePushAllowFds(long j, boolean z);

    private static native byte[] nativeReadBlob(long j);

    private static native double nativeReadDouble(long j);

    private static native FileDescriptor nativeReadFileDescriptor(long j);

    private static native float nativeReadFloat(long j);

    private static native int nativeReadInt(long j);

    private static native long nativeReadLong(long j);

    private static native String nativeReadString(long j);

    private static native IBinder nativeReadStrongBinder(long j);

    private static native void nativeRestoreAllowFds(long j, boolean z);

    private static native void nativeSetDataCapacity(long j, int i);

    private static native void nativeSetDataPosition(long j, int i);

    private static native void nativeSetDataSize(long j, int i);

    private static native void nativeUnmarshall(long j, byte[] bArr, int i, int i2);

    private static native void nativeWriteBlob(long j, byte[] bArr, int i, int i2);

    private static native void nativeWriteByteArray(long j, byte[] bArr, int i, int i2);

    private static native void nativeWriteDouble(long j, double d);

    private static native void nativeWriteFileDescriptor(long j, FileDescriptor fileDescriptor);

    private static native void nativeWriteFloat(long j, float f);

    private static native void nativeWriteInt(long j, int i);

    private static native void nativeWriteInterfaceToken(long j, String str);

    private static native void nativeWriteLong(long j, long j2);

    private static native void nativeWriteString(long j, String str);

    private static native void nativeWriteStrongBinder(long j, IBinder iBinder);

    static native FileDescriptor openFileDescriptor(String str, int i) throws FileNotFoundException;

    static {
        sOwnedPool = new Parcel[VAL_LONG];
        sHolderPool = new Parcel[VAL_LONG];
        STRING_CREATOR = new Creator<String>() {
            public String createFromParcel(Parcel source) {
                return source.readString();
            }

            public String[] newArray(int size) {
                return new String[size];
            }
        };
        mCreators = new HashMap();
    }

    public static Parcel obtain() {
        Parcel[] pool = sOwnedPool;
        synchronized (pool) {
            for (int i = VAL_STRING; i < VAL_LONG; i += VAL_INTEGER) {
                Parcel p = pool[i];
                if (p != null) {
                    pool[i] = null;
                    return p;
                }
            }
            return new Parcel(0);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void recycle() {
        /*
        r4 = this;
        r4.freeBuffer();
        r2 = r4.mOwnsNativeParcelObject;
        if (r2 == 0) goto L_0x0016;
    L_0x0007:
        r1 = sOwnedPool;
    L_0x0009:
        monitor-enter(r1);
        r0 = 0;
    L_0x000b:
        r2 = 6;
        if (r0 >= r2) goto L_0x0020;
    L_0x000e:
        r2 = r1[r0];	 Catch:{ all -> 0x0022 }
        if (r2 != 0) goto L_0x001d;
    L_0x0012:
        r1[r0] = r4;	 Catch:{ all -> 0x0022 }
        monitor-exit(r1);	 Catch:{ all -> 0x0022 }
    L_0x0015:
        return;
    L_0x0016:
        r2 = 0;
        r4.mNativePtr = r2;
        r1 = sHolderPool;
        goto L_0x0009;
    L_0x001d:
        r0 = r0 + 1;
        goto L_0x000b;
    L_0x0020:
        monitor-exit(r1);	 Catch:{ all -> 0x0022 }
        goto L_0x0015;
    L_0x0022:
        r2 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0022 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.Parcel.recycle():void");
    }

    public final int dataSize() {
        return nativeDataSize(this.mNativePtr);
    }

    public final int dataAvail() {
        return nativeDataAvail(this.mNativePtr);
    }

    public final int dataPosition() {
        return nativeDataPosition(this.mNativePtr);
    }

    public final int dataCapacity() {
        return nativeDataCapacity(this.mNativePtr);
    }

    public final void setDataSize(int size) {
        nativeSetDataSize(this.mNativePtr, size);
    }

    public final void setDataPosition(int pos) {
        nativeSetDataPosition(this.mNativePtr, pos);
    }

    public final void setDataCapacity(int size) {
        nativeSetDataCapacity(this.mNativePtr, size);
    }

    public final boolean pushAllowFds(boolean allowFds) {
        return nativePushAllowFds(this.mNativePtr, allowFds);
    }

    public final void restoreAllowFds(boolean lastValue) {
        nativeRestoreAllowFds(this.mNativePtr, lastValue);
    }

    public final byte[] marshall() {
        return nativeMarshall(this.mNativePtr);
    }

    public final void unmarshall(byte[] data, int offset, int length) {
        nativeUnmarshall(this.mNativePtr, data, offset, length);
    }

    public final void appendFrom(Parcel parcel, int offset, int length) {
        nativeAppendFrom(this.mNativePtr, parcel.mNativePtr, offset, length);
    }

    public final boolean hasFileDescriptors() {
        return nativeHasFileDescriptors(this.mNativePtr);
    }

    public final void writeInterfaceToken(String interfaceName) {
        nativeWriteInterfaceToken(this.mNativePtr, interfaceName);
    }

    public final void enforceInterface(String interfaceName) {
        nativeEnforceInterface(this.mNativePtr, interfaceName);
    }

    public final void writeByteArray(byte[] b) {
        int length;
        if (b != null) {
            length = b.length;
        } else {
            length = VAL_STRING;
        }
        writeByteArray(b, VAL_STRING, length);
    }

    public final void writeByteArray(byte[] b, int offset, int len) {
        if (b == null) {
            writeInt(VAL_NULL);
            return;
        }
        Arrays.checkOffsetAndCount(b.length, offset, len);
        nativeWriteByteArray(this.mNativePtr, b, offset, len);
    }

    public final void writeBlob(byte[] b) {
        int length;
        long j = this.mNativePtr;
        if (b != null) {
            length = b.length;
        } else {
            length = VAL_STRING;
        }
        nativeWriteBlob(j, b, VAL_STRING, length);
    }

    public final void writeInt(int val) {
        nativeWriteInt(this.mNativePtr, val);
    }

    public final void writeLong(long val) {
        nativeWriteLong(this.mNativePtr, val);
    }

    public final void writeFloat(float val) {
        nativeWriteFloat(this.mNativePtr, val);
    }

    public final void writeDouble(double val) {
        nativeWriteDouble(this.mNativePtr, val);
    }

    public final void writeString(String val) {
        nativeWriteString(this.mNativePtr, val);
    }

    public final void writeCharSequence(CharSequence val) {
        TextUtils.writeToParcel(val, this, VAL_STRING);
    }

    public final void writeStrongBinder(IBinder val) {
        nativeWriteStrongBinder(this.mNativePtr, val);
    }

    public final void writeStrongInterface(IInterface val) {
        writeStrongBinder(val == null ? null : val.asBinder());
    }

    public final void writeFileDescriptor(FileDescriptor val) {
        nativeWriteFileDescriptor(this.mNativePtr, val);
    }

    public final void writeByte(byte val) {
        writeInt(val);
    }

    public final void writeMap(Map val) {
        writeMapInternal(val);
    }

    void writeMapInternal(Map<String, Object> val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        Set<Entry<String, Object>> entries = val.entrySet();
        writeInt(entries.size());
        for (Entry<String, Object> e : entries) {
            writeValue(e.getKey());
            writeValue(e.getValue());
        }
    }

    void writeArrayMapInternal(ArrayMap<String, Object> val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            writeString((String) val.keyAt(i));
            writeValue(val.valueAt(i));
        }
    }

    public void writeArrayMap(ArrayMap<String, Object> val) {
        writeArrayMapInternal(val);
    }

    public final void writeBundle(Bundle val) {
        if (val == null) {
            writeInt(VAL_NULL);
        } else {
            val.writeToParcel(this, VAL_STRING);
        }
    }

    public final void writePersistableBundle(PersistableBundle val) {
        if (val == null) {
            writeInt(VAL_NULL);
        } else {
            val.writeToParcel(this, VAL_STRING);
        }
    }

    public final void writeSize(Size val) {
        writeInt(val.getWidth());
        writeInt(val.getHeight());
    }

    public final void writeSizeF(SizeF val) {
        writeFloat(val.getWidth());
        writeFloat(val.getHeight());
    }

    public final void writeList(List val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            writeValue(val.get(i));
        }
    }

    public final void writeArray(Object[] val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.length;
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            writeValue(val[i]);
        }
    }

    public final void writeSparseArray(SparseArray<Object> val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            writeInt(val.keyAt(i));
            writeValue(val.valueAt(i));
        }
    }

    public final void writeSparseBooleanArray(SparseBooleanArray val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            writeInt(val.keyAt(i));
            writeByte((byte) (val.valueAt(i) ? VAL_INTEGER : VAL_STRING));
        }
    }

    public final void writeBooleanArray(boolean[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeInt(val[i] ? VAL_INTEGER : VAL_STRING);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final boolean[] createBooleanArray() {
        int N = readInt();
        if (N < 0 || N > (dataAvail() >> VAL_MAP)) {
            return null;
        }
        boolean[] zArr = new boolean[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            zArr[i] = readInt() != 0 ? true : DEBUG_RECYCLE;
        }
        return zArr;
    }

    public final void readBooleanArray(boolean[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = readInt() != 0 ? true : DEBUG_RECYCLE;
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeCharArray(char[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeInt(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final char[] createCharArray() {
        int N = readInt();
        if (N < 0 || N > (dataAvail() >> VAL_MAP)) {
            return null;
        }
        char[] cArr = new char[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            cArr[i] = (char) readInt();
        }
        return cArr;
    }

    public final void readCharArray(char[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = (char) readInt();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeIntArray(int[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeInt(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final int[] createIntArray() {
        int N = readInt();
        if (N < 0 || N > (dataAvail() >> VAL_MAP)) {
            return null;
        }
        int[] iArr = new int[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            iArr[i] = readInt();
        }
        return iArr;
    }

    public final void readIntArray(int[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = readInt();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeLongArray(long[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeLong(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final long[] createLongArray() {
        int N = readInt();
        if (N < 0 || N > (dataAvail() >> VAL_BUNDLE)) {
            return null;
        }
        long[] jArr = new long[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            jArr[i] = readLong();
        }
        return jArr;
    }

    public final void readLongArray(long[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = readLong();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeFloatArray(float[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeFloat(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final float[] createFloatArray() {
        int N = readInt();
        if (N < 0 || N > (dataAvail() >> VAL_MAP)) {
            return null;
        }
        float[] fArr = new float[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            fArr[i] = readFloat();
        }
        return fArr;
    }

    public final void readFloatArray(float[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = readFloat();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeDoubleArray(double[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeDouble(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final double[] createDoubleArray() {
        int N = readInt();
        if (N < 0 || N > (dataAvail() >> VAL_BUNDLE)) {
            return null;
        }
        double[] dArr = new double[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            dArr[i] = readDouble();
        }
        return dArr;
    }

    public final void readDoubleArray(double[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = readDouble();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeStringArray(String[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeString(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final String[] createStringArray() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        String[] strArr = new String[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            strArr[i] = readString();
        }
        return strArr;
    }

    public final void readStringArray(String[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = readString();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final void writeBinderArray(IBinder[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeStrongBinder(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final void writeCharSequenceArray(CharSequence[] val) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeCharSequence(val[i]);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final IBinder[] createBinderArray() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        IBinder[] iBinderArr = new IBinder[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            iBinderArr[i] = readStrongBinder();
        }
        return iBinderArr;
    }

    public final void readBinderArray(IBinder[] val) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                val[i] = readStrongBinder();
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final <T extends Parcelable> void writeTypedList(List<T> val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            Parcelable item = (Parcelable) val.get(i);
            if (item != null) {
                writeInt(VAL_INTEGER);
                item.writeToParcel(this, VAL_STRING);
            } else {
                writeInt(VAL_STRING);
            }
        }
    }

    public final void writeStringList(List<String> val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            writeString((String) val.get(i));
        }
    }

    public final void writeBinderList(List<IBinder> val) {
        if (val == null) {
            writeInt(VAL_NULL);
            return;
        }
        int N = val.size();
        writeInt(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            writeStrongBinder((IBinder) val.get(i));
        }
    }

    public final <T extends Parcelable> void writeTypedArray(T[] val, int parcelableFlags) {
        if (val != null) {
            int N = val.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                T item = val[i];
                if (item != null) {
                    writeInt(VAL_INTEGER);
                    item.writeToParcel(this, parcelableFlags);
                } else {
                    writeInt(VAL_STRING);
                }
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final void writeValue(Object v) {
        int i = VAL_INTEGER;
        if (v == null) {
            writeInt(VAL_NULL);
        } else if (v instanceof String) {
            writeInt(VAL_STRING);
            writeString((String) v);
        } else if (v instanceof Integer) {
            writeInt(VAL_INTEGER);
            writeInt(((Integer) v).intValue());
        } else if (v instanceof Map) {
            writeInt(VAL_MAP);
            writeMap((Map) v);
        } else if (v instanceof Bundle) {
            writeInt(VAL_BUNDLE);
            writeBundle((Bundle) v);
        } else if (v instanceof Parcelable) {
            writeInt(VAL_PARCELABLE);
            writeParcelable((Parcelable) v, VAL_STRING);
        } else if (v instanceof Short) {
            writeInt(VAL_SHORT);
            writeInt(((Short) v).intValue());
        } else if (v instanceof Long) {
            writeInt(VAL_LONG);
            writeLong(((Long) v).longValue());
        } else if (v instanceof Float) {
            writeInt(VAL_FLOAT);
            writeFloat(((Float) v).floatValue());
        } else if (v instanceof Double) {
            writeInt(VAL_DOUBLE);
            writeDouble(((Double) v).doubleValue());
        } else if (v instanceof Boolean) {
            writeInt(VAL_BOOLEAN);
            if (!((Boolean) v).booleanValue()) {
                i = VAL_STRING;
            }
            writeInt(i);
        } else if (v instanceof CharSequence) {
            writeInt(VAL_CHARSEQUENCE);
            writeCharSequence((CharSequence) v);
        } else if (v instanceof List) {
            writeInt(VAL_LIST);
            writeList((List) v);
        } else if (v instanceof SparseArray) {
            writeInt(VAL_SPARSEARRAY);
            writeSparseArray((SparseArray) v);
        } else if (v instanceof boolean[]) {
            writeInt(VAL_BOOLEANARRAY);
            writeBooleanArray((boolean[]) v);
        } else if (v instanceof byte[]) {
            writeInt(VAL_BYTEARRAY);
            writeByteArray((byte[]) v);
        } else if (v instanceof String[]) {
            writeInt(VAL_STRINGARRAY);
            writeStringArray((String[]) v);
        } else if (v instanceof CharSequence[]) {
            writeInt(VAL_CHARSEQUENCEARRAY);
            writeCharSequenceArray((CharSequence[]) v);
        } else if (v instanceof IBinder) {
            writeInt(VAL_IBINDER);
            writeStrongBinder((IBinder) v);
        } else if (v instanceof Parcelable[]) {
            writeInt(VAL_PARCELABLEARRAY);
            writeParcelableArray((Parcelable[]) v, VAL_STRING);
        } else if (v instanceof int[]) {
            writeInt(VAL_INTARRAY);
            writeIntArray((int[]) v);
        } else if (v instanceof long[]) {
            writeInt(VAL_LONGARRAY);
            writeLongArray((long[]) v);
        } else if (v instanceof Byte) {
            writeInt(VAL_BYTE);
            writeInt(((Byte) v).byteValue());
        } else if (v instanceof PersistableBundle) {
            writeInt(VAL_PERSISTABLEBUNDLE);
            writePersistableBundle((PersistableBundle) v);
        } else if (v instanceof Size) {
            writeInt(VAL_SIZE);
            writeSize((Size) v);
        } else if (v instanceof SizeF) {
            writeInt(VAL_SIZEF);
            writeSizeF((SizeF) v);
        } else {
            Class<?> clazz = v.getClass();
            if (clazz.isArray() && clazz.getComponentType() == Object.class) {
                writeInt(VAL_OBJECTARRAY);
                writeArray((Object[]) v);
            } else if (v instanceof Serializable) {
                writeInt(VAL_SERIALIZABLE);
                writeSerializable((Serializable) v);
            } else {
                throw new RuntimeException("Parcel: unable to marshal value " + v);
            }
        }
    }

    public final void writeParcelable(Parcelable p, int parcelableFlags) {
        if (p == null) {
            writeString(null);
            return;
        }
        writeString(p.getClass().getName());
        p.writeToParcel(this, parcelableFlags);
    }

    public final void writeParcelableCreator(Parcelable p) {
        writeString(p.getClass().getName());
    }

    public final void writeSerializable(Serializable s) {
        if (s == null) {
            writeString(null);
            return;
        }
        String name = s.getClass().getName();
        writeString(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            oos.close();
            writeByteArray(baos.toByteArray());
        } catch (IOException ioe) {
            throw new RuntimeException("Parcelable encountered IOException writing serializable object (name = " + name + ")", ioe);
        }
    }

    public final void writeException(Exception e) {
        int code = VAL_STRING;
        if (e instanceof SecurityException) {
            code = VAL_NULL;
        } else if (e instanceof BadParcelableException) {
            code = EX_BAD_PARCELABLE;
        } else if (e instanceof IllegalArgumentException) {
            code = EX_ILLEGAL_ARGUMENT;
        } else if (e instanceof NullPointerException) {
            code = EX_NULL_POINTER;
        } else if (e instanceof IllegalStateException) {
            code = EX_ILLEGAL_STATE;
        } else if (e instanceof NetworkOnMainThreadException) {
            code = EX_NETWORK_MAIN_THREAD;
        } else if (e instanceof UnsupportedOperationException) {
            code = EX_UNSUPPORTED_OPERATION;
        }
        writeInt(code);
        StrictMode.clearGatheredViolations();
        if (code != 0) {
            writeString(e.getMessage());
        } else if (e instanceof RuntimeException) {
            throw ((RuntimeException) e);
        } else {
            throw new RuntimeException(e);
        }
    }

    public final void writeNoException() {
        if (StrictMode.hasGatheredViolations()) {
            writeInt(EX_HAS_REPLY_HEADER);
            int sizePosition = dataPosition();
            writeInt(VAL_STRING);
            StrictMode.writeGatheredViolationsToParcel(this);
            int payloadPosition = dataPosition();
            setDataPosition(sizePosition);
            writeInt(payloadPosition - sizePosition);
            setDataPosition(payloadPosition);
            return;
        }
        writeInt(VAL_STRING);
    }

    public final void readException() {
        int code = readExceptionCode();
        if (code != 0) {
            readException(code, readString());
        }
    }

    public final int readExceptionCode() {
        int code = readInt();
        if (code != EX_HAS_REPLY_HEADER) {
            return code;
        }
        if (readInt() == 0) {
            Log.e(TAG, "Unexpected zero-sized Parcel reply header.");
        } else {
            StrictMode.readAndHandleBinderCallViolations(this);
        }
        return VAL_STRING;
    }

    public final void readException(int code, String msg) {
        switch (code) {
            case EX_UNSUPPORTED_OPERATION /*-7*/:
                throw new UnsupportedOperationException(msg);
            case EX_NETWORK_MAIN_THREAD /*-6*/:
                throw new NetworkOnMainThreadException();
            case EX_ILLEGAL_STATE /*-5*/:
                throw new IllegalStateException(msg);
            case EX_NULL_POINTER /*-4*/:
                throw new NullPointerException(msg);
            case EX_ILLEGAL_ARGUMENT /*-3*/:
                throw new IllegalArgumentException(msg);
            case EX_BAD_PARCELABLE /*-2*/:
                throw new BadParcelableException(msg);
            case VAL_NULL /*-1*/:
                throw new SecurityException(msg);
            default:
                throw new RuntimeException("Unknown exception code: " + code + " msg " + msg);
        }
    }

    public final int readInt() {
        return nativeReadInt(this.mNativePtr);
    }

    public final long readLong() {
        return nativeReadLong(this.mNativePtr);
    }

    public final float readFloat() {
        return nativeReadFloat(this.mNativePtr);
    }

    public final double readDouble() {
        return nativeReadDouble(this.mNativePtr);
    }

    public final String readString() {
        return nativeReadString(this.mNativePtr);
    }

    public final CharSequence readCharSequence() {
        return (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this);
    }

    public final IBinder readStrongBinder() {
        return nativeReadStrongBinder(this.mNativePtr);
    }

    public final ParcelFileDescriptor readFileDescriptor() {
        FileDescriptor fd = nativeReadFileDescriptor(this.mNativePtr);
        return fd != null ? new ParcelFileDescriptor(fd) : null;
    }

    public final FileDescriptor readRawFileDescriptor() {
        return nativeReadFileDescriptor(this.mNativePtr);
    }

    public final byte readByte() {
        return (byte) (readInt() & EditorInfo.IME_MASK_ACTION);
    }

    public final void readMap(Map outVal, ClassLoader loader) {
        readMapInternal(outVal, readInt(), loader);
    }

    public final void readList(List outVal, ClassLoader loader) {
        readListInternal(outVal, readInt(), loader);
    }

    public final HashMap readHashMap(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        HashMap m = new HashMap(N);
        readMapInternal(m, N, loader);
        return m;
    }

    public final Bundle readBundle() {
        return readBundle(null);
    }

    public final Bundle readBundle(ClassLoader loader) {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        Bundle bundle = new Bundle(this, length);
        if (loader == null) {
            return bundle;
        }
        bundle.setClassLoader(loader);
        return bundle;
    }

    public final PersistableBundle readPersistableBundle() {
        return readPersistableBundle(null);
    }

    public final PersistableBundle readPersistableBundle(ClassLoader loader) {
        int length = readInt();
        if (length < 0) {
            return null;
        }
        PersistableBundle bundle = new PersistableBundle(this, length);
        if (loader == null) {
            return bundle;
        }
        bundle.setClassLoader(loader);
        return bundle;
    }

    public final Size readSize() {
        return new Size(readInt(), readInt());
    }

    public final SizeF readSizeF() {
        return new SizeF(readFloat(), readFloat());
    }

    public final byte[] createByteArray() {
        return nativeCreateByteArray(this.mNativePtr);
    }

    public final void readByteArray(byte[] val) {
        byte[] ba = createByteArray();
        if (ba.length == val.length) {
            System.arraycopy(ba, VAL_STRING, val, VAL_STRING, ba.length);
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    public final byte[] readBlob() {
        return nativeReadBlob(this.mNativePtr);
    }

    public final String[] readStringArray() {
        String[] array = null;
        int length = readInt();
        if (length >= 0) {
            array = new String[length];
            for (int i = VAL_STRING; i < length; i += VAL_INTEGER) {
                array[i] = readString();
            }
        }
        return array;
    }

    public final CharSequence[] readCharSequenceArray() {
        CharSequence[] array = null;
        int length = readInt();
        if (length >= 0) {
            array = new CharSequence[length];
            for (int i = VAL_STRING; i < length; i += VAL_INTEGER) {
                array[i] = readCharSequence();
            }
        }
        return array;
    }

    public final ArrayList readArrayList(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList l = new ArrayList(N);
        readListInternal(l, N, loader);
        return l;
    }

    public final Object[] readArray(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        Object[] l = new Object[N];
        readArrayInternal(l, N, loader);
        return l;
    }

    public final SparseArray readSparseArray(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        SparseArray sa = new SparseArray(N);
        readSparseArrayInternal(sa, N, loader);
        return sa;
    }

    public final SparseBooleanArray readSparseBooleanArray() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        SparseBooleanArray sa = new SparseBooleanArray(N);
        readSparseBooleanArrayInternal(sa, N);
        return sa;
    }

    public final <T> ArrayList<T> createTypedArrayList(Creator<T> c) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<T> l = new ArrayList(N);
        while (N > 0) {
            if (readInt() != 0) {
                l.add(c.createFromParcel(this));
            } else {
                l.add(null);
            }
            N += VAL_NULL;
        }
        return l;
    }

    public final <T> void readTypedList(List<T> list, Creator<T> c) {
        int M = list.size();
        int N = readInt();
        int i = VAL_STRING;
        while (i < M && i < N) {
            if (readInt() != 0) {
                list.set(i, c.createFromParcel(this));
            } else {
                list.set(i, null);
            }
            i += VAL_INTEGER;
        }
        while (i < N) {
            if (readInt() != 0) {
                list.add(c.createFromParcel(this));
            } else {
                list.add(null);
            }
            i += VAL_INTEGER;
        }
        while (i < M) {
            list.remove(N);
            i += VAL_INTEGER;
        }
    }

    public final ArrayList<String> createStringArrayList() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<String> l = new ArrayList(N);
        while (N > 0) {
            l.add(readString());
            N += VAL_NULL;
        }
        return l;
    }

    public final ArrayList<IBinder> createBinderArrayList() {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        ArrayList<IBinder> l = new ArrayList(N);
        while (N > 0) {
            l.add(readStrongBinder());
            N += VAL_NULL;
        }
        return l;
    }

    public final void readStringList(List<String> list) {
        int M = list.size();
        int N = readInt();
        int i = VAL_STRING;
        while (i < M && i < N) {
            list.set(i, readString());
            i += VAL_INTEGER;
        }
        while (i < N) {
            list.add(readString());
            i += VAL_INTEGER;
        }
        while (i < M) {
            list.remove(N);
            i += VAL_INTEGER;
        }
    }

    public final void readBinderList(List<IBinder> list) {
        int M = list.size();
        int N = readInt();
        int i = VAL_STRING;
        while (i < M && i < N) {
            list.set(i, readStrongBinder());
            i += VAL_INTEGER;
        }
        while (i < N) {
            list.add(readStrongBinder());
            i += VAL_INTEGER;
        }
        while (i < M) {
            list.remove(N);
            i += VAL_INTEGER;
        }
    }

    public final <T> T[] createTypedArray(Creator<T> c) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        T[] l = c.newArray(N);
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            if (readInt() != 0) {
                l[i] = c.createFromParcel(this);
            }
        }
        return l;
    }

    public final <T> void readTypedArray(T[] val, Creator<T> c) {
        int N = readInt();
        if (N == val.length) {
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                if (readInt() != 0) {
                    val[i] = c.createFromParcel(this);
                } else {
                    val[i] = null;
                }
            }
            return;
        }
        throw new RuntimeException("bad array lengths");
    }

    @Deprecated
    public final <T> T[] readTypedArray(Creator<T> c) {
        return createTypedArray(c);
    }

    public final <T extends Parcelable> void writeParcelableArray(T[] value, int parcelableFlags) {
        if (value != null) {
            int N = value.length;
            writeInt(N);
            for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
                writeParcelable(value[i], parcelableFlags);
            }
            return;
        }
        writeInt(VAL_NULL);
    }

    public final Object readValue(ClassLoader loader) {
        boolean z = true;
        int type = readInt();
        switch (type) {
            case VAL_NULL /*-1*/:
                return null;
            case VAL_STRING /*0*/:
                return readString();
            case VAL_INTEGER /*1*/:
                return Integer.valueOf(readInt());
            case VAL_MAP /*2*/:
                return readHashMap(loader);
            case VAL_BUNDLE /*3*/:
                return readBundle(loader);
            case VAL_PARCELABLE /*4*/:
                return readParcelable(loader);
            case VAL_SHORT /*5*/:
                return Short.valueOf((short) readInt());
            case VAL_LONG /*6*/:
                return Long.valueOf(readLong());
            case VAL_FLOAT /*7*/:
                return Float.valueOf(readFloat());
            case VAL_DOUBLE /*8*/:
                return Double.valueOf(readDouble());
            case VAL_BOOLEAN /*9*/:
                if (readInt() != VAL_INTEGER) {
                    z = DEBUG_RECYCLE;
                }
                return Boolean.valueOf(z);
            case VAL_CHARSEQUENCE /*10*/:
                return readCharSequence();
            case VAL_LIST /*11*/:
                return readArrayList(loader);
            case VAL_SPARSEARRAY /*12*/:
                return readSparseArray(loader);
            case VAL_BYTEARRAY /*13*/:
                return createByteArray();
            case VAL_STRINGARRAY /*14*/:
                return readStringArray();
            case VAL_IBINDER /*15*/:
                return readStrongBinder();
            case VAL_PARCELABLEARRAY /*16*/:
                return readParcelableArray(loader);
            case VAL_OBJECTARRAY /*17*/:
                return readArray(loader);
            case VAL_INTARRAY /*18*/:
                return createIntArray();
            case VAL_LONGARRAY /*19*/:
                return createLongArray();
            case VAL_BYTE /*20*/:
                return Byte.valueOf(readByte());
            case VAL_SERIALIZABLE /*21*/:
                return readSerializable(loader);
            case VAL_SPARSEBOOLEANARRAY /*22*/:
                return readSparseBooleanArray();
            case VAL_BOOLEANARRAY /*23*/:
                return createBooleanArray();
            case VAL_CHARSEQUENCEARRAY /*24*/:
                return readCharSequenceArray();
            case VAL_PERSISTABLEBUNDLE /*25*/:
                return readPersistableBundle(loader);
            case VAL_SIZE /*26*/:
                return readSize();
            case VAL_SIZEF /*27*/:
                return readSizeF();
            default:
                throw new RuntimeException("Parcel " + this + ": Unmarshalling unknown type code " + type + " at offset " + (dataPosition() + EX_NULL_POINTER));
        }
    }

    public final <T extends Parcelable> T readParcelable(ClassLoader loader) {
        Creator<T> creator = readParcelableCreator(loader);
        if (creator == null) {
            return null;
        }
        if (creator instanceof ClassLoaderCreator) {
            return (Parcelable) ((ClassLoaderCreator) creator).createFromParcel(this, loader);
        }
        return (Parcelable) creator.createFromParcel(this);
    }

    public final <T extends Parcelable> T readCreator(Creator<T> creator, ClassLoader loader) {
        if (creator instanceof ClassLoaderCreator) {
            return (Parcelable) ((ClassLoaderCreator) creator).createFromParcel(this, loader);
        }
        return (Parcelable) creator.createFromParcel(this);
    }

    public final <T extends Parcelable> Creator<T> readParcelableCreator(ClassLoader loader) {
        Creator<T> creator = null;
        String name = readString();
        if (name != null) {
            synchronized (mCreators) {
                HashMap<String, Creator> map = (HashMap) mCreators.get(loader);
                if (map == null) {
                    map = new HashMap();
                    mCreators.put(loader, map);
                }
                creator = (Creator) map.get(name);
                if (creator == null) {
                    Class c;
                    if (loader == null) {
                        try {
                            c = Class.forName(name);
                        } catch (IllegalAccessException e) {
                            Log.e(TAG, "Illegal access when unmarshalling: " + name, e);
                            throw new BadParcelableException("IllegalAccessException when unmarshalling: " + name);
                        } catch (ClassNotFoundException e2) {
                            Log.e(TAG, "Class not found when unmarshalling: " + name, e2);
                            throw new BadParcelableException("ClassNotFoundException when unmarshalling: " + name);
                        } catch (ClassCastException e3) {
                            throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + name);
                        } catch (NoSuchFieldException e4) {
                            throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + name);
                        } catch (NullPointerException e5) {
                            throw new BadParcelableException("Parcelable protocol requires the CREATOR object to be static on class " + name);
                        }
                    }
                    c = Class.forName(name, true, loader);
                    creator = (Creator) c.getField("CREATOR").get(null);
                    if (creator == null) {
                        throw new BadParcelableException("Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class " + name);
                    }
                    map.put(name, creator);
                }
            }
        }
        return creator;
    }

    public final Parcelable[] readParcelableArray(ClassLoader loader) {
        int N = readInt();
        if (N < 0) {
            return null;
        }
        Parcelable[] p = new Parcelable[N];
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            p[i] = readParcelable(loader);
        }
        return p;
    }

    public final Serializable readSerializable() {
        return readSerializable(null);
    }

    private final Serializable readSerializable(ClassLoader loader) {
        String name = readString();
        if (name == null) {
            return null;
        }
        try {
            return (Serializable) new AnonymousClass2(this, new ByteArrayInputStream(createByteArray()), loader).readObject();
        } catch (IOException ioe) {
            throw new RuntimeException("Parcelable encountered IOException reading a Serializable object (name = " + name + ")", ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("Parcelable encountered ClassNotFoundException reading a Serializable object (name = " + name + ")", cnfe);
        }
    }

    protected static final Parcel obtain(int obj) {
        throw new UnsupportedOperationException();
    }

    protected static final Parcel obtain(long obj) {
        Parcel[] pool = sHolderPool;
        synchronized (pool) {
            for (int i = VAL_STRING; i < VAL_LONG; i += VAL_INTEGER) {
                Parcel p = pool[i];
                if (p != null) {
                    pool[i] = null;
                    p.init(obj);
                    return p;
                }
            }
            return new Parcel(obj);
        }
    }

    private Parcel(long nativePtr) {
        init(nativePtr);
    }

    private void init(long nativePtr) {
        if (nativePtr != 0) {
            this.mNativePtr = nativePtr;
            this.mOwnsNativeParcelObject = DEBUG_RECYCLE;
            return;
        }
        this.mNativePtr = nativeCreate();
        this.mOwnsNativeParcelObject = true;
    }

    private void freeBuffer() {
        if (this.mOwnsNativeParcelObject) {
            nativeFreeBuffer(this.mNativePtr);
        }
    }

    private void destroy() {
        if (this.mNativePtr != 0) {
            if (this.mOwnsNativeParcelObject) {
                nativeDestroy(this.mNativePtr);
            }
            this.mNativePtr = 0;
        }
    }

    protected void finalize() throws Throwable {
        destroy();
    }

    void readMapInternal(Map outVal, int N, ClassLoader loader) {
        while (N > 0) {
            outVal.put(readValue(loader), readValue(loader));
            N += VAL_NULL;
        }
    }

    void readArrayMapInternal(ArrayMap outVal, int N, ClassLoader loader) {
        while (N > 0) {
            outVal.append(readString(), readValue(loader));
            N += VAL_NULL;
        }
        outVal.validate();
    }

    void readArrayMapSafelyInternal(ArrayMap outVal, int N, ClassLoader loader) {
        while (N > 0) {
            outVal.put(readString(), readValue(loader));
            N += VAL_NULL;
        }
    }

    public void readArrayMap(ArrayMap outVal, ClassLoader loader) {
        int N = readInt();
        if (N >= 0) {
            readArrayMapInternal(outVal, N, loader);
        }
    }

    private void readListInternal(List outVal, int N, ClassLoader loader) {
        while (N > 0) {
            outVal.add(readValue(loader));
            N += VAL_NULL;
        }
    }

    private void readArrayInternal(Object[] outVal, int N, ClassLoader loader) {
        for (int i = VAL_STRING; i < N; i += VAL_INTEGER) {
            outVal[i] = readValue(loader);
        }
    }

    private void readSparseArrayInternal(SparseArray outVal, int N, ClassLoader loader) {
        while (N > 0) {
            outVal.append(readInt(), readValue(loader));
            N += VAL_NULL;
        }
    }

    private void readSparseBooleanArrayInternal(SparseBooleanArray outVal, int N) {
        while (N > 0) {
            outVal.append(readInt(), readByte() == (byte) 1 ? true : DEBUG_RECYCLE);
            N += VAL_NULL;
        }
    }
}
