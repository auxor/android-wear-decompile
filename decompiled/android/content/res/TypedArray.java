package android.content.res;

import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import java.util.Arrays;

public class TypedArray {
    private final AssetManager mAssets;
    int[] mData;
    int[] mIndices;
    int mLength;
    private final DisplayMetrics mMetrics;
    private boolean mRecycled;
    private final Resources mResources;
    Theme mTheme;
    TypedValue mValue;
    Parser mXml;

    static TypedArray obtain(Resources res, int len) {
        TypedArray attrs = (TypedArray) res.mTypedArrayPool.acquire();
        if (attrs == null) {
            return new TypedArray(res, new int[(len * 6)], new int[(len + 1)], len);
        }
        attrs.mLength = len;
        attrs.mRecycled = false;
        int fullLen = len * 6;
        if (attrs.mData.length >= fullLen) {
            return attrs;
        }
        attrs.mData = new int[fullLen];
        attrs.mIndices = new int[(len + 1)];
        return attrs;
    }

    public int length() {
        if (!this.mRecycled) {
            return this.mLength;
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public int getIndexCount() {
        if (!this.mRecycled) {
            return this.mIndices[0];
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public int getIndex(int at) {
        if (!this.mRecycled) {
            return this.mIndices[at + 1];
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public Resources getResources() {
        if (!this.mRecycled) {
            return this.mResources;
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public CharSequence getText(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int type = this.mData[index + 0];
        if (type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index);
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            Log.w("Resources", "Converting to string: " + v);
            return v.coerceToString();
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(type));
        return null;
    }

    public String getString(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int type = this.mData[index + 0];
        if (type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index).toString();
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            Log.w("Resources", "Converting to string: " + v);
            CharSequence cs = v.coerceToString();
            if (cs != null) {
                return cs.toString();
            }
            return null;
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(type));
        return null;
    }

    public String getNonResourceString(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        if (data[index + 0] != 3 || data[index + 2] >= 0) {
            return null;
        }
        return this.mXml.getPooledString(data[index + 1]).toString();
    }

    public String getNonConfigurationString(int index, int allowedChangingConfigs) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if ((data[index + 4] & (allowedChangingConfigs ^ -1)) != 0 || type == 0) {
            return null;
        }
        if (type == 3) {
            return loadStringValueAt(index).toString();
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            Log.w("Resources", "Converting to string: " + v);
            CharSequence cs = v.coerceToString();
            if (cs != null) {
                return cs.toString();
            }
            return null;
        }
        Log.w("Resources", "getString of bad type: 0x" + Integer.toHexString(type));
        return null;
    }

    public boolean getBoolean(int index, boolean defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type < 16 || type > 31) {
            TypedValue v = this.mValue;
            if (getValueAt(index, v)) {
                Log.w("Resources", "Converting to boolean: " + v);
                return XmlUtils.convertValueToBoolean(v.coerceToString(), defValue);
            }
            Log.w("Resources", "getBoolean of bad type: 0x" + Integer.toHexString(type));
            return defValue;
        }
        return data[index + 1] != 0;
    }

    public int getInt(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            Log.w("Resources", "Converting to int: " + v);
            return XmlUtils.convertValueToInt(v.coerceToString(), defValue);
        }
        Log.w("Resources", "getInt of bad type: 0x" + Integer.toHexString(type));
        return defValue;
    }

    public float getFloat(int index, float defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 4) {
            return Float.intBitsToFloat(data[index + 1]);
        }
        if (type >= 16 && type <= 31) {
            return (float) data[index + 1];
        }
        TypedValue v = this.mValue;
        if (getValueAt(index, v)) {
            Log.w("Resources", "Converting to float: " + v);
            CharSequence str = v.coerceToString();
            if (str != null) {
                return Float.parseFloat(str.toString());
            }
        }
        Log.w("Resources", "getFloat of bad type: 0x" + Integer.toHexString(type));
        return defValue;
    }

    public int getColor(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        if (type == 3) {
            TypedValue value = this.mValue;
            if (getValueAt(index, value)) {
                return this.mResources.loadColorStateList(value, value.resourceId).getDefaultColor();
            }
            return defValue;
        } else if (type == 2) {
            throw new RuntimeException("Failed to resolve attribute at index " + index);
        } else {
            throw new UnsupportedOperationException("Can't convert to color: type=0x" + Integer.toHexString(type));
        }
    }

    public ColorStateList getColorStateList(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (!getValueAt(index * 6, value)) {
            return null;
        }
        if (value.type != 2) {
            return this.mResources.loadColorStateList(value, value.resourceId);
        }
        throw new RuntimeException("Failed to resolve attribute at index " + index);
    }

    public int getInteger(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        if (type == 2) {
            throw new RuntimeException("Failed to resolve attribute at index " + index);
        }
        throw new UnsupportedOperationException("Can't convert to integer: type=0x" + Integer.toHexString(type));
    }

    public float getDimension(int index, float defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimension(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            throw new RuntimeException("Failed to resolve attribute at index " + index);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(type));
    }

    public int getDimensionPixelOffset(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelOffset(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            throw new RuntimeException("Failed to resolve attribute at index " + index);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(type));
    }

    public int getDimensionPixelSize(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            throw new RuntimeException("Failed to resolve attribute at index " + index);
        }
        throw new UnsupportedOperationException("Can't convert to dimension: type=0x" + Integer.toHexString(type));
    }

    public int getLayoutDimension(int index, String name) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index + 1], this.mMetrics);
        }
        if (type == 2) {
            throw new RuntimeException("Failed to resolve attribute at index " + index);
        }
        throw new RuntimeException(getPositionDescription() + ": You must supply a " + name + " attribute.");
    }

    public int getLayoutDimension(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type >= 16 && type <= 31) {
            return data[index + 1];
        }
        if (type == 5) {
            return TypedValue.complexToDimensionPixelSize(data[index + 1], this.mMetrics);
        }
        return defValue;
    }

    public float getFraction(int index, int base, int pbase, float defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return defValue;
        }
        if (type == 6) {
            return TypedValue.complexToFraction(data[index + 1], (float) base, (float) pbase);
        }
        if (type == 2) {
            throw new RuntimeException("Failed to resolve attribute at index " + index);
        }
        throw new UnsupportedOperationException("Can't convert to fraction: type=0x" + Integer.toHexString(type));
    }

    public int getResourceId(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        if (data[index + 0] != 0) {
            int resid = data[index + 3];
            if (resid != 0) {
                return resid;
            }
        }
        return defValue;
    }

    public int getThemeAttributeId(int index, int defValue) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        if (data[index + 0] == 2) {
            return data[index + 1];
        }
        return defValue;
    }

    public Drawable getDrawable(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (!getValueAt(index * 6, value)) {
            return null;
        }
        if (value.type != 2) {
            return this.mResources.loadDrawable(value, value.resourceId, this.mTheme);
        }
        throw new RuntimeException("Failed to resolve attribute at index " + index);
    }

    public CharSequence[] getTextArray(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        if (getValueAt(index * 6, value)) {
            return this.mResources.getTextArray(value.resourceId);
        }
        return null;
    }

    public boolean getValue(int index, TypedValue outValue) {
        if (!this.mRecycled) {
            return getValueAt(index * 6, outValue);
        }
        throw new RuntimeException("Cannot make calls to a recycled instance!");
    }

    public int getType(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        return this.mData[(index * 6) + 0];
    }

    public boolean hasValue(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        return this.mData[(index * 6) + 0] != 0;
    }

    public boolean hasValueOrEmpty(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        index *= 6;
        int[] data = this.mData;
        if (data[index + 0] != 0 || data[index + 1] == 1) {
            return true;
        }
        return false;
    }

    public TypedValue peekValue(int index) {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        TypedValue value = this.mValue;
        return getValueAt(index * 6, value) ? value : null;
    }

    public String getPositionDescription() {
        if (!this.mRecycled) {
            return this.mXml != null ? this.mXml.getPositionDescription() : "<internal>";
        } else {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
    }

    public void recycle() {
        if (this.mRecycled) {
            throw new RuntimeException(toString() + " recycled twice!");
        }
        this.mRecycled = true;
        this.mXml = null;
        this.mTheme = null;
        this.mResources.mTypedArrayPool.release(this);
    }

    public int[] extractThemeAttrs() {
        if (this.mRecycled) {
            throw new RuntimeException("Cannot make calls to a recycled instance!");
        }
        int[] attrs = null;
        int[] data = this.mData;
        int N = length();
        for (int i = 0; i < N; i++) {
            int index = i * 6;
            if (data[index + 0] == 2) {
                data[index + 0] = 0;
                int attr = data[index + 1];
                if (attr != 0) {
                    if (attrs == null) {
                        attrs = new int[N];
                    }
                    attrs[i] = attr;
                }
            }
        }
        return attrs;
    }

    public int getChangingConfigurations() {
        int changingConfig = 0;
        int[] data = this.mData;
        int N = length();
        for (int i = 0; i < N; i++) {
            int index = i * 6;
            if (data[index + 0] != 0) {
                changingConfig |= data[index + 4];
            }
        }
        return changingConfig;
    }

    private boolean getValueAt(int index, TypedValue outValue) {
        int[] data = this.mData;
        int type = data[index + 0];
        if (type == 0) {
            return false;
        }
        outValue.type = type;
        outValue.data = data[index + 1];
        outValue.assetCookie = data[index + 2];
        outValue.resourceId = data[index + 3];
        outValue.changingConfigurations = data[index + 4];
        outValue.density = data[index + 5];
        outValue.string = type == 3 ? loadStringValueAt(index) : null;
        return true;
    }

    private CharSequence loadStringValueAt(int index) {
        int[] data = this.mData;
        int cookie = data[index + 2];
        if (cookie >= 0) {
            return this.mAssets.getPooledStringForCookie(cookie, data[index + 1]);
        }
        if (this.mXml != null) {
            return this.mXml.getPooledString(data[index + 1]);
        }
        return null;
    }

    TypedArray(Resources resources, int[] data, int[] indices, int len) {
        this.mValue = new TypedValue();
        this.mResources = resources;
        this.mMetrics = this.mResources.mMetrics;
        this.mAssets = this.mResources.mAssets;
        this.mData = data;
        this.mIndices = indices;
        this.mLength = len;
    }

    public String toString() {
        return Arrays.toString(this.mData);
    }
}
