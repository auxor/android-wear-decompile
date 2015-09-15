package android.renderscript;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.os.Trace;
import android.renderscript.Element.DataKind;
import android.renderscript.Element.DataType;
import android.renderscript.Type.Builder;
import android.renderscript.Type.CubemapFace;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Surface;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Allocation extends BaseObj {
    public static final int USAGE_GRAPHICS_CONSTANTS = 8;
    public static final int USAGE_GRAPHICS_RENDER_TARGET = 16;
    public static final int USAGE_GRAPHICS_TEXTURE = 2;
    public static final int USAGE_GRAPHICS_VERTEX = 4;
    public static final int USAGE_IO_INPUT = 32;
    public static final int USAGE_IO_OUTPUT = 64;
    public static final int USAGE_SCRIPT = 1;
    public static final int USAGE_SHARED = 128;
    static HashMap<Long, Allocation> mAllocationMap;
    static Options mBitmapOptions;
    Allocation mAdaptedAllocation;
    Bitmap mBitmap;
    OnBufferAvailableListener mBufferNotifier;
    boolean mConstrainedFace;
    boolean mConstrainedLOD;
    boolean mConstrainedY;
    boolean mConstrainedZ;
    int mCurrentCount;
    int mCurrentDimX;
    int mCurrentDimY;
    int mCurrentDimZ;
    boolean mReadAllowed;
    CubemapFace mSelectedFace;
    int mSelectedLOD;
    int mSelectedY;
    int mSelectedZ;
    int mSize;
    Type mType;
    int mUsage;
    boolean mWriteAllowed;

    /* renamed from: android.renderscript.Allocation.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config;

        static {
            $SwitchMap$android$graphics$Bitmap$Config = new int[Config.values().length];
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ALPHA_8.ordinal()] = Allocation.USAGE_SCRIPT;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_8888.ordinal()] = Allocation.USAGE_GRAPHICS_TEXTURE;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.RGB_565.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_4444.ordinal()] = Allocation.USAGE_GRAPHICS_VERTEX;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public enum MipmapControl {
        MIPMAP_NONE(0),
        MIPMAP_FULL(Allocation.USAGE_SCRIPT),
        MIPMAP_ON_SYNC_TO_TEXTURE(Allocation.USAGE_GRAPHICS_TEXTURE);
        
        int mID;

        private MipmapControl(int id) {
            this.mID = id;
        }
    }

    public interface OnBufferAvailableListener {
        void onBufferAvailable(Allocation allocation);
    }

    static {
        mAllocationMap = new HashMap();
        mBitmapOptions = new Options();
        mBitmapOptions.inScaled = false;
    }

    private DataType validateObjectIsPrimitiveArray(Object d, boolean checkType) {
        Class c = d.getClass();
        if (c.isArray()) {
            Class cmp = c.getComponentType();
            if (!cmp.isPrimitive()) {
                throw new RSIllegalArgumentException("Object passed is not an Array of primitives.");
            } else if (cmp == Long.TYPE) {
                if (!checkType) {
                    return DataType.SIGNED_64;
                }
                validateIsInt64();
                return this.mType.mElement.mType;
            } else if (cmp == Integer.TYPE) {
                if (!checkType) {
                    return DataType.SIGNED_32;
                }
                validateIsInt32();
                return this.mType.mElement.mType;
            } else if (cmp == Short.TYPE) {
                if (!checkType) {
                    return DataType.SIGNED_16;
                }
                validateIsInt16();
                return this.mType.mElement.mType;
            } else if (cmp == Byte.TYPE) {
                if (!checkType) {
                    return DataType.SIGNED_8;
                }
                validateIsInt8();
                return this.mType.mElement.mType;
            } else if (cmp == Float.TYPE) {
                if (checkType) {
                    validateIsFloat32();
                }
                return DataType.FLOAT_32;
            } else if (cmp != Double.TYPE) {
                return null;
            } else {
                if (checkType) {
                    validateIsFloat64();
                }
                return DataType.FLOAT_64;
            }
        }
        throw new RSIllegalArgumentException("Object passed is not an array of primitives.");
    }

    private long getIDSafe() {
        if (this.mAdaptedAllocation != null) {
            return this.mAdaptedAllocation.getID(this.mRS);
        }
        return getID(this.mRS);
    }

    public Element getElement() {
        return this.mType.getElement();
    }

    public int getUsage() {
        return this.mUsage;
    }

    public int getBytesSize() {
        if (this.mType.mDimYuv != 0) {
            return (int) Math.ceil(((double) (this.mType.getCount() * this.mType.getElement().getBytesSize())) * 1.5d);
        }
        return this.mType.getCount() * this.mType.getElement().getBytesSize();
    }

    private void updateCacheInfo(Type t) {
        this.mCurrentDimX = t.getX();
        this.mCurrentDimY = t.getY();
        this.mCurrentDimZ = t.getZ();
        this.mCurrentCount = this.mCurrentDimX;
        if (this.mCurrentDimY > USAGE_SCRIPT) {
            this.mCurrentCount *= this.mCurrentDimY;
        }
        if (this.mCurrentDimZ > USAGE_SCRIPT) {
            this.mCurrentCount *= this.mCurrentDimZ;
        }
    }

    private void setBitmap(Bitmap b) {
        this.mBitmap = b;
    }

    Allocation(long id, RenderScript rs, Type t, int usage) {
        super(id, rs);
        this.mReadAllowed = true;
        this.mWriteAllowed = true;
        this.mSelectedFace = CubemapFace.POSITIVE_X;
        if ((usage & InputDevice.SOURCE_ANY) != 0) {
            throw new RSIllegalArgumentException("Unknown usage specified.");
        }
        if ((usage & USAGE_IO_INPUT) != 0) {
            this.mWriteAllowed = false;
            if ((usage & -36) != 0) {
                throw new RSIllegalArgumentException("Invalid usage combination.");
            }
        }
        this.mType = t;
        this.mUsage = usage;
        if (t != null) {
            this.mSize = this.mType.getCount() * this.mType.getElement().getBytesSize();
            updateCacheInfo(t);
        }
        try {
            Method method = RenderScript.registerNativeAllocation;
            Object obj = RenderScript.sRuntime;
            Object[] objArr = new Object[USAGE_SCRIPT];
            objArr[0] = Integer.valueOf(this.mSize);
            method.invoke(obj, objArr);
        } catch (Exception e) {
            Log.e("RenderScript_jni", "Couldn't invoke registerNativeAllocation:" + e);
            throw new RSRuntimeException("Couldn't invoke registerNativeAllocation:" + e);
        }
    }

    protected void finalize() throws Throwable {
        Method method = RenderScript.registerNativeFree;
        Object obj = RenderScript.sRuntime;
        Object[] objArr = new Object[USAGE_SCRIPT];
        objArr[0] = Integer.valueOf(this.mSize);
        method.invoke(obj, objArr);
        super.finalize();
    }

    private void validateIsInt64() {
        if (this.mType.mElement.mType != DataType.SIGNED_64 && this.mType.mElement.mType != DataType.UNSIGNED_64) {
            throw new RSIllegalArgumentException("64 bit integer source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsInt32() {
        if (this.mType.mElement.mType != DataType.SIGNED_32 && this.mType.mElement.mType != DataType.UNSIGNED_32) {
            throw new RSIllegalArgumentException("32 bit integer source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsInt16() {
        if (this.mType.mElement.mType != DataType.SIGNED_16 && this.mType.mElement.mType != DataType.UNSIGNED_16) {
            throw new RSIllegalArgumentException("16 bit integer source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsInt8() {
        if (this.mType.mElement.mType != DataType.SIGNED_8 && this.mType.mElement.mType != DataType.UNSIGNED_8) {
            throw new RSIllegalArgumentException("8 bit integer source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsFloat32() {
        if (this.mType.mElement.mType != DataType.FLOAT_32) {
            throw new RSIllegalArgumentException("32 bit float source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsFloat64() {
        if (this.mType.mElement.mType != DataType.FLOAT_64) {
            throw new RSIllegalArgumentException("64 bit float source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    private void validateIsObject() {
        if (this.mType.mElement.mType != DataType.RS_ELEMENT && this.mType.mElement.mType != DataType.RS_TYPE && this.mType.mElement.mType != DataType.RS_ALLOCATION && this.mType.mElement.mType != DataType.RS_SAMPLER && this.mType.mElement.mType != DataType.RS_SCRIPT && this.mType.mElement.mType != DataType.RS_MESH && this.mType.mElement.mType != DataType.RS_PROGRAM_FRAGMENT && this.mType.mElement.mType != DataType.RS_PROGRAM_VERTEX && this.mType.mElement.mType != DataType.RS_PROGRAM_RASTER && this.mType.mElement.mType != DataType.RS_PROGRAM_STORE) {
            throw new RSIllegalArgumentException("Object source does not match allocation type " + this.mType.mElement.mType);
        }
    }

    void updateFromNative() {
        super.updateFromNative();
        long typeID = this.mRS.nAllocationGetType(getID(this.mRS));
        if (typeID != 0) {
            this.mType = new Type(typeID, this.mRS);
            this.mType.updateFromNative();
            updateCacheInfo(this.mType);
        }
    }

    public Type getType() {
        return this.mType;
    }

    public void syncAll(int srcLocation) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "syncAll");
        switch (srcLocation) {
            case USAGE_SCRIPT /*1*/:
            case USAGE_GRAPHICS_TEXTURE /*2*/:
                if ((this.mUsage & USAGE_SHARED) != 0) {
                    copyFrom(this.mBitmap);
                    break;
                }
                break;
            case USAGE_GRAPHICS_VERTEX /*4*/:
            case USAGE_GRAPHICS_CONSTANTS /*8*/:
                break;
            case USAGE_SHARED /*128*/:
                if ((this.mUsage & USAGE_SHARED) != 0) {
                    copyTo(this.mBitmap);
                    break;
                }
                break;
            default:
                throw new RSIllegalArgumentException("Source must be exactly one usage type.");
        }
        this.mRS.validate();
        this.mRS.nAllocationSyncAll(getIDSafe(), srcLocation);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void ioSend() {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "ioSend");
        if ((this.mUsage & USAGE_IO_OUTPUT) == 0) {
            throw new RSIllegalArgumentException("Can only send buffer if IO_OUTPUT usage specified.");
        }
        this.mRS.validate();
        this.mRS.nAllocationIoSend(getID(this.mRS));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void ioReceive() {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "ioReceive");
        if ((this.mUsage & USAGE_IO_INPUT) == 0) {
            throw new RSIllegalArgumentException("Can only receive if IO_INPUT usage specified.");
        }
        this.mRS.validate();
        this.mRS.nAllocationIoReceive(getID(this.mRS));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(BaseObj[] d) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        validateIsObject();
        if (d.length != this.mCurrentCount) {
            throw new RSIllegalArgumentException("Array size mismatch, allocation sizeX = " + this.mCurrentCount + ", array length = " + d.length);
        }
        int ct;
        if (RenderScript.sPointerSize == USAGE_GRAPHICS_CONSTANTS) {
            Object i = new long[(d.length * USAGE_GRAPHICS_VERTEX)];
            for (ct = 0; ct < d.length; ct += USAGE_SCRIPT) {
                i[ct * USAGE_GRAPHICS_VERTEX] = d[ct].getID(this.mRS);
            }
            copy1DRangeFromUnchecked(0, this.mCurrentCount, i);
        } else {
            int[] i2 = new int[d.length];
            for (ct = 0; ct < d.length; ct += USAGE_SCRIPT) {
                i2[ct] = (int) d[ct].getID(this.mRS);
            }
            copy1DRangeFromUnchecked(0, this.mCurrentCount, i2);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    private void validateBitmapFormat(Bitmap b) {
        Config bc = b.getConfig();
        if (bc == null) {
            throw new RSIllegalArgumentException("Bitmap has an unsupported format for this operation");
        }
        switch (AnonymousClass1.$SwitchMap$android$graphics$Bitmap$Config[bc.ordinal()]) {
            case USAGE_SCRIPT /*1*/:
                if (this.mType.getElement().mKind != DataKind.PIXEL_A) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
            case USAGE_GRAPHICS_TEXTURE /*2*/:
                if (this.mType.getElement().mKind != DataKind.PIXEL_RGBA || this.mType.getElement().getBytesSize() != USAGE_GRAPHICS_VERTEX) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
            case SetDrawableParameters.TAG /*3*/:
                if (this.mType.getElement().mKind != DataKind.PIXEL_RGB || this.mType.getElement().getBytesSize() != USAGE_GRAPHICS_TEXTURE) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
            case USAGE_GRAPHICS_VERTEX /*4*/:
                if (this.mType.getElement().mKind != DataKind.PIXEL_RGBA || this.mType.getElement().getBytesSize() != USAGE_GRAPHICS_TEXTURE) {
                    throw new RSIllegalArgumentException("Allocation kind is " + this.mType.getElement().mKind + ", type " + this.mType.getElement().mType + " of " + this.mType.getElement().getBytesSize() + " bytes, passed bitmap was " + bc);
                }
            default:
        }
    }

    private void validateBitmapSize(Bitmap b) {
        if (this.mCurrentDimX != b.getWidth() || this.mCurrentDimY != b.getHeight()) {
            throw new RSIllegalArgumentException("Cannot update allocation from bitmap, sizes mismatch");
        }
    }

    private void copyFromUnchecked(Object array, DataType dt, int arrayLen) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        this.mRS.validate();
        if (this.mCurrentDimZ > 0) {
            copy3DRangeFromUnchecked(0, 0, 0, this.mCurrentDimX, this.mCurrentDimY, this.mCurrentDimZ, array, dt, arrayLen);
        } else if (this.mCurrentDimY > 0) {
            copy2DRangeFromUnchecked(0, 0, this.mCurrentDimX, this.mCurrentDimY, array, dt, arrayLen);
        } else {
            copy1DRangeFromUnchecked(0, this.mCurrentCount, array, dt, arrayLen);
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(Object array) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFromUnchecked");
        copyFromUnchecked(array, validateObjectIsPrimitiveArray(array, false), Array.getLength(array));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFromUnchecked(int[] d) {
        copyFromUnchecked(d, DataType.SIGNED_32, d.length);
    }

    public void copyFromUnchecked(short[] d) {
        copyFromUnchecked(d, DataType.SIGNED_16, d.length);
    }

    public void copyFromUnchecked(byte[] d) {
        copyFromUnchecked(d, DataType.SIGNED_8, d.length);
    }

    public void copyFromUnchecked(float[] d) {
        copyFromUnchecked(d, DataType.FLOAT_32, d.length);
    }

    public void copyFrom(Object array) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        copyFromUnchecked(array, validateObjectIsPrimitiveArray(array, true), Array.getLength(array));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(int[] d) {
        validateIsInt32();
        copyFromUnchecked(d, DataType.SIGNED_32, d.length);
    }

    public void copyFrom(short[] d) {
        validateIsInt16();
        copyFromUnchecked(d, DataType.SIGNED_16, d.length);
    }

    public void copyFrom(byte[] d) {
        validateIsInt8();
        copyFromUnchecked(d, DataType.SIGNED_8, d.length);
    }

    public void copyFrom(float[] d) {
        validateIsFloat32();
        copyFromUnchecked(d, DataType.FLOAT_32, d.length);
    }

    public void copyFrom(Bitmap b) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (b.getConfig() == null) {
            Bitmap newBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Config.ARGB_8888);
            new Canvas(newBitmap).drawBitmap(b, 0.0f, 0.0f, null);
            copyFrom(newBitmap);
            return;
        }
        validateBitmapSize(b);
        validateBitmapFormat(b);
        this.mRS.nAllocationCopyFromBitmap(getID(this.mRS), b);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyFrom(Allocation a) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyFrom");
        this.mRS.validate();
        if (this.mType.equals(a.getType())) {
            copy2DRangeFrom(0, 0, this.mCurrentDimX, this.mCurrentDimY, a, 0, 0);
            Trace.traceEnd(Trace.TRACE_TAG_RS);
            return;
        }
        throw new RSIllegalArgumentException("Types of allocations must match.");
    }

    public void setFromFieldPacker(int xoff, FieldPacker fp) {
        this.mRS.validate();
        int eSize = this.mType.mElement.getBytesSize();
        byte[] data = fp.getData();
        int data_length = fp.getPos();
        int count = data_length / eSize;
        if (eSize * count != data_length) {
            throw new RSIllegalArgumentException("Field packer length " + data_length + " not divisible by element size " + eSize + ".");
        }
        copy1DRangeFromUnchecked(xoff, count, data);
    }

    public void setFromFieldPacker(int xoff, int component_number, FieldPacker fp) {
        this.mRS.validate();
        if (component_number >= this.mType.mElement.mElements.length) {
            throw new RSIllegalArgumentException("Component_number " + component_number + " out of range.");
        } else if (xoff < 0) {
            throw new RSIllegalArgumentException("Offset must be >= 0.");
        } else {
            byte[] data = fp.getData();
            int data_length = fp.getPos();
            int eSize = this.mType.mElement.mElements[component_number].getBytesSize() * this.mType.mElement.mArraySizes[component_number];
            if (data_length != eSize) {
                throw new RSIllegalArgumentException("Field packer sizelength " + data_length + " does not match component size " + eSize + ".");
            }
            this.mRS.nAllocationElementData1D(getIDSafe(), xoff, this.mSelectedLOD, component_number, data, data_length);
        }
    }

    private void data1DChecks(int off, int count, int len, int dataSize) {
        this.mRS.validate();
        if (off < 0) {
            throw new RSIllegalArgumentException("Offset must be >= 0.");
        } else if (count < USAGE_SCRIPT) {
            throw new RSIllegalArgumentException("Count must be >= 1.");
        } else if (off + count > this.mCurrentCount) {
            throw new RSIllegalArgumentException("Overflow, Available count " + this.mCurrentCount + ", got " + count + " at offset " + off + ".");
        } else if (len < dataSize) {
            throw new RSIllegalArgumentException("Array too small for allocation type.");
        }
    }

    public void generateMipmaps() {
        this.mRS.nAllocationGenerateMipmaps(getID(this.mRS));
    }

    private void copy1DRangeFromUnchecked(int off, int count, Object array, DataType dt, int arrayLen) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFromUnchecked");
        int dataSize = this.mType.mElement.getBytesSize() * count;
        data1DChecks(off, count, dt.mSize * arrayLen, dataSize);
        this.mRS.nAllocationData1D(getIDSafe(), off, this.mSelectedLOD, count, array, dataSize, dt);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy1DRangeFromUnchecked(int off, int count, Object array) {
        copy1DRangeFromUnchecked(off, count, array, validateObjectIsPrimitiveArray(array, false), Array.getLength(array));
    }

    public void copy1DRangeFromUnchecked(int off, int count, int[] d) {
        copy1DRangeFromUnchecked(off, count, d, DataType.SIGNED_32, d.length);
    }

    public void copy1DRangeFromUnchecked(int off, int count, short[] d) {
        copy1DRangeFromUnchecked(off, count, d, DataType.SIGNED_16, d.length);
    }

    public void copy1DRangeFromUnchecked(int off, int count, byte[] d) {
        copy1DRangeFromUnchecked(off, count, d, DataType.SIGNED_8, d.length);
    }

    public void copy1DRangeFromUnchecked(int off, int count, float[] d) {
        copy1DRangeFromUnchecked(off, count, d, DataType.FLOAT_32, d.length);
    }

    public void copy1DRangeFrom(int off, int count, Object array) {
        copy1DRangeFromUnchecked(off, count, array, validateObjectIsPrimitiveArray(array, true), Array.getLength(array));
    }

    public void copy1DRangeFrom(int off, int count, int[] d) {
        validateIsInt32();
        copy1DRangeFromUnchecked(off, count, d, DataType.SIGNED_32, d.length);
    }

    public void copy1DRangeFrom(int off, int count, short[] d) {
        validateIsInt16();
        copy1DRangeFromUnchecked(off, count, d, DataType.SIGNED_16, d.length);
    }

    public void copy1DRangeFrom(int off, int count, byte[] d) {
        validateIsInt8();
        copy1DRangeFromUnchecked(off, count, d, DataType.SIGNED_8, d.length);
    }

    public void copy1DRangeFrom(int off, int count, float[] d) {
        validateIsFloat32();
        copy1DRangeFromUnchecked(off, count, d, DataType.FLOAT_32, d.length);
    }

    public void copy1DRangeFrom(int off, int count, Allocation data, int dataOff) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy1DRangeFrom");
        RenderScript renderScript = this.mRS;
        long iDSafe = getIDSafe();
        int i = this.mSelectedLOD;
        int i2 = this.mSelectedFace.mID;
        RenderScript renderScript2 = this.mRS;
        int i3 = off;
        int i4 = count;
        int i5 = dataOff;
        renderScript.nAllocationData2D(iDSafe, i3, 0, i, i2, i4, USAGE_SCRIPT, data.getID(renderScript2), i5, 0, data.mSelectedLOD, data.mSelectedFace.mID);
    }

    private void validate2DRange(int xoff, int yoff, int w, int h) {
        if (this.mAdaptedAllocation == null) {
            if (xoff < 0 || yoff < 0) {
                throw new RSIllegalArgumentException("Offset cannot be negative.");
            } else if (h < 0 || w < 0) {
                throw new RSIllegalArgumentException("Height or width cannot be negative.");
            } else if (xoff + w > this.mCurrentDimX || yoff + h > this.mCurrentDimY) {
                throw new RSIllegalArgumentException("Updated region larger than allocation.");
            }
        }
    }

    void copy2DRangeFromUnchecked(int xoff, int yoff, int w, int h, Object array, DataType dt, int arrayLen) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFromUnchecked");
        this.mRS.validate();
        validate2DRange(xoff, yoff, w, h);
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, w, h, array, arrayLen * dt.mSize, dt);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, Object array) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        copy2DRangeFromUnchecked(xoff, yoff, w, h, array, validateObjectIsPrimitiveArray(array, true), Array.getLength(array));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, byte[] data) {
        validateIsInt8();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data, DataType.SIGNED_8, data.length);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, short[] data) {
        validateIsInt16();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data, DataType.SIGNED_16, data.length);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, int[] data) {
        validateIsInt32();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data, DataType.SIGNED_32, data.length);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, float[] data) {
        validateIsFloat32();
        copy2DRangeFromUnchecked(xoff, yoff, w, h, data, DataType.FLOAT_32, data.length);
    }

    public void copy2DRangeFrom(int xoff, int yoff, int w, int h, Allocation data, int dataXoff, int dataYoff) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        this.mRS.validate();
        validate2DRange(xoff, yoff, w, h);
        RenderScript renderScript = this.mRS;
        long iDSafe = getIDSafe();
        int i = this.mSelectedLOD;
        int i2 = this.mSelectedFace.mID;
        RenderScript renderScript2 = this.mRS;
        int i3 = xoff;
        int i4 = yoff;
        int i5 = w;
        int i6 = h;
        int i7 = dataXoff;
        int i8 = dataYoff;
        renderScript.nAllocationData2D(iDSafe, i3, i4, i, i2, i5, i6, data.getID(renderScript2), i7, i8, data.mSelectedLOD, data.mSelectedFace.mID);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy2DRangeFrom(int xoff, int yoff, Bitmap data) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy2DRangeFrom");
        this.mRS.validate();
        if (data.getConfig() == null) {
            Bitmap newBitmap = Bitmap.createBitmap(data.getWidth(), data.getHeight(), Config.ARGB_8888);
            new Canvas(newBitmap).drawBitmap(data, 0.0f, 0.0f, null);
            copy2DRangeFrom(xoff, yoff, newBitmap);
            return;
        }
        validateBitmapFormat(data);
        validate2DRange(xoff, yoff, data.getWidth(), data.getHeight());
        this.mRS.nAllocationData2D(getIDSafe(), xoff, yoff, this.mSelectedLOD, this.mSelectedFace.mID, data);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    private void validate3DRange(int xoff, int yoff, int zoff, int w, int h, int d) {
        if (this.mAdaptedAllocation == null) {
            if (xoff < 0 || yoff < 0 || zoff < 0) {
                throw new RSIllegalArgumentException("Offset cannot be negative.");
            } else if (h < 0 || w < 0 || d < 0) {
                throw new RSIllegalArgumentException("Height or width cannot be negative.");
            } else if (xoff + w > this.mCurrentDimX || yoff + h > this.mCurrentDimY || zoff + d > this.mCurrentDimZ) {
                throw new RSIllegalArgumentException("Updated region larger than allocation.");
            }
        }
    }

    private void copy3DRangeFromUnchecked(int xoff, int yoff, int zoff, int w, int h, int d, Object array, DataType dt, int arrayLen) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy3DRangeFromUnchecked");
        this.mRS.validate();
        validate3DRange(xoff, yoff, zoff, w, h, d);
        this.mRS.nAllocationData3D(getIDSafe(), xoff, yoff, zoff, this.mSelectedLOD, w, h, d, array, arrayLen * dt.mSize, dt);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy3DRangeFrom(int xoff, int yoff, int zoff, int w, int h, int d, Object array) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copy3DRangeFrom");
        copy3DRangeFromUnchecked(xoff, yoff, zoff, w, h, d, array, validateObjectIsPrimitiveArray(array, true), Array.getLength(array));
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copy3DRangeFrom(int xoff, int yoff, int zoff, int w, int h, int d, Allocation data, int dataXoff, int dataYoff, int dataZoff) {
        this.mRS.validate();
        validate3DRange(xoff, yoff, zoff, w, h, d);
        RenderScript renderScript = this.mRS;
        long iDSafe = getIDSafe();
        int i = this.mSelectedLOD;
        RenderScript renderScript2 = this.mRS;
        int i2 = xoff;
        int i3 = yoff;
        int i4 = zoff;
        int i5 = w;
        int i6 = h;
        int i7 = d;
        int i8 = dataXoff;
        int i9 = dataYoff;
        int i10 = dataZoff;
        renderScript.nAllocationData3D(iDSafe, i2, i3, i4, i, i5, i6, i7, data.getID(renderScript2), i8, i9, i10, data.mSelectedLOD);
    }

    public void copyTo(Bitmap b) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        this.mRS.validate();
        validateBitmapFormat(b);
        validateBitmapSize(b);
        this.mRS.nAllocationCopyToBitmap(getID(this.mRS), b);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    private void copyTo(Object array, DataType dt, int arrayLen) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "copyTo");
        if (dt.mSize * arrayLen < this.mSize) {
            throw new RSIllegalArgumentException("Size of output array cannot be smaller than size of allocation.");
        }
        this.mRS.validate();
        this.mRS.nAllocationRead(getID(this.mRS), array, dt);
        Trace.traceEnd(Trace.TRACE_TAG_RS);
    }

    public void copyTo(Object array) {
        copyTo(array, validateObjectIsPrimitiveArray(array, true), Array.getLength(array));
    }

    public void copyTo(byte[] d) {
        validateIsInt8();
        copyTo(d, DataType.SIGNED_8, d.length);
    }

    public void copyTo(short[] d) {
        validateIsInt16();
        copyTo(d, DataType.SIGNED_16, d.length);
    }

    public void copyTo(int[] d) {
        validateIsInt32();
        copyTo(d, DataType.SIGNED_32, d.length);
    }

    public void copyTo(float[] d) {
        validateIsFloat32();
        copyTo(d, DataType.FLOAT_32, d.length);
    }

    public synchronized void resize(int dimX) {
        if (this.mRS.getApplicationContext().getApplicationInfo().targetSdkVersion >= 21) {
            throw new RSRuntimeException("Resize is not allowed in API 21+.");
        } else if (this.mType.getY() > 0 || this.mType.getZ() > 0 || this.mType.hasFaces() || this.mType.hasMipmaps()) {
            throw new RSInvalidStateException("Resize only support for 1D allocations at this time.");
        } else {
            this.mRS.nAllocationResize1D(getID(this.mRS), dimX);
            this.mRS.finish();
            this.mType = new Type(this.mRS.nAllocationGetType(getID(this.mRS)), this.mRS);
            this.mType.updateFromNative();
            updateCacheInfo(this.mType);
        }
    }

    public static Allocation createTyped(RenderScript rs, Type type, MipmapControl mips, int usage) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createTyped");
        rs.validate();
        if (type.getID(rs) == 0) {
            throw new RSInvalidStateException("Bad Type");
        }
        long id = rs.nAllocationCreateTyped(type.getID(rs), mips.mID, usage, 0);
        if (id == 0) {
            throw new RSRuntimeException("Allocation creation failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(id, rs, type, usage);
    }

    public static Allocation createTyped(RenderScript rs, Type type, int usage) {
        return createTyped(rs, type, MipmapControl.MIPMAP_NONE, usage);
    }

    public static Allocation createTyped(RenderScript rs, Type type) {
        return createTyped(rs, type, MipmapControl.MIPMAP_NONE, USAGE_SCRIPT);
    }

    public static Allocation createSized(RenderScript rs, Element e, int count, int usage) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createSized");
        rs.validate();
        Builder b = new Builder(rs, e);
        b.setX(count);
        Type t = b.create();
        long id = rs.nAllocationCreateTyped(t.getID(rs), MipmapControl.MIPMAP_NONE.mID, usage, 0);
        if (id == 0) {
            throw new RSRuntimeException("Allocation creation failed.");
        }
        Trace.traceEnd(Trace.TRACE_TAG_RS);
        return new Allocation(id, rs, t, usage);
    }

    public static Allocation createSized(RenderScript rs, Element e, int count) {
        return createSized(rs, e, count, USAGE_SCRIPT);
    }

    static Element elementFromBitmap(RenderScript rs, Bitmap b) {
        Config bc = b.getConfig();
        if (bc == Config.ALPHA_8) {
            return Element.A_8(rs);
        }
        if (bc == Config.ARGB_4444) {
            return Element.RGBA_4444(rs);
        }
        if (bc == Config.ARGB_8888) {
            return Element.RGBA_8888(rs);
        }
        if (bc == Config.RGB_565) {
            return Element.RGB_565(rs);
        }
        throw new RSInvalidStateException("Bad bitmap type: " + bc);
    }

    static Type typeFromBitmap(RenderScript rs, Bitmap b, MipmapControl mip) {
        Builder tb = new Builder(rs, elementFromBitmap(rs, b));
        tb.setX(b.getWidth());
        tb.setY(b.getHeight());
        tb.setMipmaps(mip == MipmapControl.MIPMAP_FULL);
        return tb.create();
    }

    public static Allocation createFromBitmap(RenderScript rs, Bitmap b, MipmapControl mips, int usage) {
        Trace.traceBegin(Trace.TRACE_TAG_RS, "createFromBitmap");
        rs.validate();
        if (b.getConfig() != null) {
            Type t = typeFromBitmap(rs, b, mips);
            long id;
            if (mips == MipmapControl.MIPMAP_NONE && t.getElement().isCompatible(Element.RGBA_8888(rs)) && usage == 131) {
                id = rs.nAllocationCreateBitmapBackedAllocation(t.getID(rs), mips.mID, b, usage);
                if (id == 0) {
                    throw new RSRuntimeException("Load failed.");
                }
                Allocation alloc = new Allocation(id, rs, t, usage);
                alloc.setBitmap(b);
                return alloc;
            }
            id = rs.nAllocationCreateFromBitmap(t.getID(rs), mips.mID, b, usage);
            if (id == 0) {
                throw new RSRuntimeException("Load failed.");
            }
            Trace.traceEnd(Trace.TRACE_TAG_RS);
            return new Allocation(id, rs, t, usage);
        } else if ((usage & USAGE_SHARED) != 0) {
            throw new RSIllegalArgumentException("USAGE_SHARED cannot be used with a Bitmap that has a null config.");
        } else {
            Bitmap newBitmap = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Config.ARGB_8888);
            new Canvas(newBitmap).drawBitmap(b, 0.0f, 0.0f, null);
            return createFromBitmap(rs, newBitmap, mips, usage);
        }
    }

    public Surface getSurface() {
        if ((this.mUsage & USAGE_IO_INPUT) != 0) {
            return this.mRS.nAllocationGetSurface(getID(this.mRS));
        }
        throw new RSInvalidStateException("Allocation is not a surface texture.");
    }

    public void setSurface(Surface sur) {
        this.mRS.validate();
        if ((this.mUsage & USAGE_IO_OUTPUT) == 0) {
            throw new RSInvalidStateException("Allocation is not USAGE_IO_OUTPUT.");
        }
        this.mRS.nAllocationSetSurface(getID(this.mRS), sur);
    }

    public static Allocation createFromBitmap(RenderScript rs, Bitmap b) {
        if (rs.getApplicationContext().getApplicationInfo().targetSdkVersion >= 18) {
            return createFromBitmap(rs, b, MipmapControl.MIPMAP_NONE, KeyEvent.KEYCODE_F1);
        }
        return createFromBitmap(rs, b, MipmapControl.MIPMAP_NONE, USAGE_GRAPHICS_TEXTURE);
    }

    public static Allocation createCubemapFromBitmap(RenderScript rs, Bitmap b, MipmapControl mips, int usage) {
        boolean z = true;
        rs.validate();
        int height = b.getHeight();
        int width = b.getWidth();
        if (width % 6 != 0) {
            throw new RSIllegalArgumentException("Cubemap height must be multiple of 6");
        } else if (width / 6 != height) {
            throw new RSIllegalArgumentException("Only square cube map faces supported");
        } else {
            boolean isPow2;
            if (((height - 1) & height) == 0) {
                isPow2 = true;
            } else {
                isPow2 = false;
            }
            if (isPow2) {
                Element e = elementFromBitmap(rs, b);
                Builder tb = new Builder(rs, e);
                tb.setX(height);
                tb.setY(height);
                tb.setFaces(true);
                if (mips != MipmapControl.MIPMAP_FULL) {
                    z = false;
                }
                tb.setMipmaps(z);
                Type t = tb.create();
                long id = rs.nAllocationCubeCreateFromBitmap(t.getID(rs), mips.mID, b, usage);
                if (id != 0) {
                    return new Allocation(id, rs, t, usage);
                }
                throw new RSRuntimeException("Load failed for bitmap " + b + " element " + e);
            }
            throw new RSIllegalArgumentException("Only power of 2 cube faces supported");
        }
    }

    public static Allocation createCubemapFromBitmap(RenderScript rs, Bitmap b) {
        return createCubemapFromBitmap(rs, b, MipmapControl.MIPMAP_NONE, USAGE_GRAPHICS_TEXTURE);
    }

    public static Allocation createCubemapFromCubeFaces(RenderScript rs, Bitmap xpos, Bitmap xneg, Bitmap ypos, Bitmap yneg, Bitmap zpos, Bitmap zneg, MipmapControl mips, int usage) {
        int height = xpos.getHeight();
        if (xpos.getWidth() == height && xneg.getWidth() == height && xneg.getHeight() == height && ypos.getWidth() == height && ypos.getHeight() == height && yneg.getWidth() == height && yneg.getHeight() == height && zpos.getWidth() == height && zpos.getHeight() == height && zneg.getWidth() == height && zneg.getHeight() == height) {
            if (((height + -1) & height) == 0) {
                Builder tb = new Builder(rs, elementFromBitmap(rs, xpos));
                tb.setX(height);
                tb.setY(height);
                tb.setFaces(true);
                tb.setMipmaps(mips == MipmapControl.MIPMAP_FULL);
                Allocation cubemap = createTyped(rs, tb.create(), mips, usage);
                AllocationAdapter adapter = AllocationAdapter.create2D(rs, cubemap);
                adapter.setFace(CubemapFace.POSITIVE_X);
                adapter.copyFrom(xpos);
                adapter.setFace(CubemapFace.NEGATIVE_X);
                adapter.copyFrom(xneg);
                adapter.setFace(CubemapFace.POSITIVE_Y);
                adapter.copyFrom(ypos);
                adapter.setFace(CubemapFace.NEGATIVE_Y);
                adapter.copyFrom(yneg);
                adapter.setFace(CubemapFace.POSITIVE_Z);
                adapter.copyFrom(zpos);
                adapter.setFace(CubemapFace.NEGATIVE_Z);
                adapter.copyFrom(zneg);
                return cubemap;
            }
            throw new RSIllegalArgumentException("Only power of 2 cube faces supported");
        }
        throw new RSIllegalArgumentException("Only square cube map faces supported");
    }

    public static Allocation createCubemapFromCubeFaces(RenderScript rs, Bitmap xpos, Bitmap xneg, Bitmap ypos, Bitmap yneg, Bitmap zpos, Bitmap zneg) {
        return createCubemapFromCubeFaces(rs, xpos, xneg, ypos, yneg, zpos, zneg, MipmapControl.MIPMAP_NONE, USAGE_GRAPHICS_TEXTURE);
    }

    public static Allocation createFromBitmapResource(RenderScript rs, Resources res, int id, MipmapControl mips, int usage) {
        rs.validate();
        if ((usage & KeyEvent.KEYCODE_WAKEUP) != 0) {
            throw new RSIllegalArgumentException("Unsupported usage specified.");
        }
        Bitmap b = BitmapFactory.decodeResource(res, id);
        Allocation alloc = createFromBitmap(rs, b, mips, usage);
        b.recycle();
        return alloc;
    }

    public static Allocation createFromBitmapResource(RenderScript rs, Resources res, int id) {
        if (rs.getApplicationContext().getApplicationInfo().targetSdkVersion >= 18) {
            return createFromBitmapResource(rs, res, id, MipmapControl.MIPMAP_NONE, 3);
        }
        return createFromBitmapResource(rs, res, id, MipmapControl.MIPMAP_NONE, USAGE_GRAPHICS_TEXTURE);
    }

    public static Allocation createFromString(RenderScript rs, String str, int usage) {
        rs.validate();
        try {
            byte[] allocArray = str.getBytes("UTF-8");
            Allocation alloc = createSized(rs, Element.U8(rs), allocArray.length, usage);
            alloc.copyFrom(allocArray);
            return alloc;
        } catch (Exception e) {
            throw new RSRuntimeException("Could not convert string to utf-8.");
        }
    }

    public void setOnBufferAvailableListener(OnBufferAvailableListener callback) {
        synchronized (mAllocationMap) {
            mAllocationMap.put(new Long(getID(this.mRS)), this);
            this.mBufferNotifier = callback;
        }
    }

    static void sendBufferNotification(long id) {
        synchronized (mAllocationMap) {
            Allocation a = (Allocation) mAllocationMap.get(new Long(id));
            if (!(a == null || a.mBufferNotifier == null)) {
                a.mBufferNotifier.onBufferAvailable(a);
            }
        }
    }
}
