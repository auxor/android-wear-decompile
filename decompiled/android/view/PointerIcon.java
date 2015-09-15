package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;

public final class PointerIcon implements Parcelable {
    public static final Creator<PointerIcon> CREATOR;
    public static final int STYLE_ARROW = 1000;
    public static final int STYLE_CUSTOM = -1;
    private static final int STYLE_DEFAULT = 1000;
    public static final int STYLE_NULL = 0;
    private static final int STYLE_OEM_FIRST = 10000;
    public static final int STYLE_SPOT_ANCHOR = 2002;
    public static final int STYLE_SPOT_HOVER = 2000;
    public static final int STYLE_SPOT_TOUCH = 2001;
    private static final String TAG = "PointerIcon";
    private static final PointerIcon gNullIcon;
    private Bitmap mBitmap;
    private float mHotSpotX;
    private float mHotSpotY;
    private final int mStyle;
    private int mSystemIconResourceId;

    static {
        gNullIcon = new PointerIcon(STYLE_NULL);
        CREATOR = new Creator<PointerIcon>() {
            public PointerIcon createFromParcel(Parcel in) {
                int style = in.readInt();
                if (style == 0) {
                    return PointerIcon.getNullIcon();
                }
                int systemIconResourceId = in.readInt();
                if (systemIconResourceId == 0) {
                    return PointerIcon.createCustomIcon((Bitmap) Bitmap.CREATOR.createFromParcel(in), in.readFloat(), in.readFloat());
                }
                PointerIcon icon = new PointerIcon(null);
                icon.mSystemIconResourceId = systemIconResourceId;
                return icon;
            }

            public PointerIcon[] newArray(int size) {
                return new PointerIcon[size];
            }
        };
    }

    private PointerIcon(int style) {
        this.mStyle = style;
    }

    public static PointerIcon getNullIcon() {
        return gNullIcon;
    }

    public static PointerIcon getDefaultIcon(Context context) {
        return getSystemIcon(context, STYLE_DEFAULT);
    }

    public static PointerIcon getSystemIcon(Context context, int style) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        } else if (style == 0) {
            return gNullIcon;
        } else {
            int styleIndex = getSystemIconStyleIndex(style);
            if (styleIndex == 0) {
                styleIndex = getSystemIconStyleIndex(STYLE_DEFAULT);
            }
            TypedArray a = context.obtainStyledAttributes(null, R.styleable.Pointer, 18219048, STYLE_NULL);
            int resourceId = a.getResourceId(styleIndex, STYLE_CUSTOM);
            a.recycle();
            if (resourceId == STYLE_CUSTOM) {
                Log.w(TAG, "Missing theme resources for pointer icon style " + style);
                return style == STYLE_DEFAULT ? gNullIcon : getSystemIcon(context, STYLE_DEFAULT);
            } else {
                PointerIcon icon = new PointerIcon(style);
                if ((Color.BLACK & resourceId) == WindowManagerPolicy.FLAG_INJECTED) {
                    icon.mSystemIconResourceId = resourceId;
                } else {
                    icon.loadResource(context, context.getResources(), resourceId);
                }
                return icon;
            }
        }
    }

    public static PointerIcon createCustomIcon(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap must not be null");
        }
        validateHotSpot(bitmap, hotSpotX, hotSpotY);
        PointerIcon icon = new PointerIcon(STYLE_CUSTOM);
        icon.mBitmap = bitmap;
        icon.mHotSpotX = hotSpotX;
        icon.mHotSpotY = hotSpotY;
        return icon;
    }

    public static PointerIcon loadCustomIcon(Resources resources, int resourceId) {
        if (resources == null) {
            throw new IllegalArgumentException("resources must not be null");
        }
        PointerIcon icon = new PointerIcon(STYLE_CUSTOM);
        icon.loadResource(null, resources, resourceId);
        return icon;
    }

    public PointerIcon load(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        } else if (this.mSystemIconResourceId == 0 || this.mBitmap != null) {
            return this;
        } else {
            PointerIcon result = new PointerIcon(this.mStyle);
            result.mSystemIconResourceId = this.mSystemIconResourceId;
            result.loadResource(context, context.getResources(), this.mSystemIconResourceId);
            return result;
        }
    }

    public boolean isNullIcon() {
        return this.mStyle == 0;
    }

    public boolean isLoaded() {
        return this.mBitmap != null || this.mStyle == 0;
    }

    public int getStyle() {
        return this.mStyle;
    }

    public Bitmap getBitmap() {
        throwIfIconIsNotLoaded();
        return this.mBitmap;
    }

    public float getHotSpotX() {
        throwIfIconIsNotLoaded();
        return this.mHotSpotX;
    }

    public float getHotSpotY() {
        throwIfIconIsNotLoaded();
        return this.mHotSpotY;
    }

    private void throwIfIconIsNotLoaded() {
        if (!isLoaded()) {
            throw new IllegalStateException("The icon is not loaded.");
        }
    }

    public int describeContents() {
        return STYLE_NULL;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mStyle);
        if (this.mStyle != 0) {
            out.writeInt(this.mSystemIconResourceId);
            if (this.mSystemIconResourceId == 0) {
                this.mBitmap.writeToParcel(out, flags);
                out.writeFloat(this.mHotSpotX);
                out.writeFloat(this.mHotSpotY);
            }
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof PointerIcon)) {
            return false;
        }
        PointerIcon otherIcon = (PointerIcon) other;
        if (this.mStyle != otherIcon.mStyle || this.mSystemIconResourceId != otherIcon.mSystemIconResourceId) {
            return false;
        }
        if (this.mSystemIconResourceId != 0) {
            return true;
        }
        if (this.mBitmap == otherIcon.mBitmap && this.mHotSpotX == otherIcon.mHotSpotX && this.mHotSpotY == otherIcon.mHotSpotY) {
            return true;
        }
        return false;
    }

    private void loadResource(Context context, Resources resources, int resourceId) {
        XmlResourceParser parser = resources.getXml(resourceId);
        try {
            XmlUtils.beginDocument(parser, "pointer-icon");
            TypedArray a = resources.obtainAttributes(parser, R.styleable.PointerIcon);
            int bitmapRes = a.getResourceId(STYLE_NULL, STYLE_NULL);
            float hotSpotX = a.getDimension(1, 0.0f);
            float hotSpotY = a.getDimension(2, 0.0f);
            a.recycle();
            parser.close();
            if (bitmapRes == 0) {
                throw new IllegalArgumentException("<pointer-icon> is missing bitmap attribute.");
            }
            Drawable drawable;
            if (context == null) {
                drawable = resources.getDrawable(bitmapRes);
            } else {
                drawable = context.getDrawable(bitmapRes);
            }
            if (drawable instanceof BitmapDrawable) {
                this.mBitmap = ((BitmapDrawable) drawable).getBitmap();
                this.mHotSpotX = hotSpotX;
                this.mHotSpotY = hotSpotY;
                return;
            }
            throw new IllegalArgumentException("<pointer-icon> bitmap attribute must refer to a bitmap drawable.");
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception parsing pointer icon resource.", ex);
        } catch (Throwable th) {
            parser.close();
        }
    }

    private static void validateHotSpot(Bitmap bitmap, float hotSpotX, float hotSpotY) {
        if (hotSpotX < 0.0f || hotSpotX >= ((float) bitmap.getWidth())) {
            throw new IllegalArgumentException("x hotspot lies outside of the bitmap area");
        } else if (hotSpotY < 0.0f || hotSpotY >= ((float) bitmap.getHeight())) {
            throw new IllegalArgumentException("y hotspot lies outside of the bitmap area");
        }
    }

    private static int getSystemIconStyleIndex(int style) {
        switch (style) {
            case STYLE_SPOT_HOVER /*2000*/:
                return 1;
            case STYLE_SPOT_TOUCH /*2001*/:
                return 2;
            case STYLE_SPOT_ANCHOR /*2002*/:
                return 3;
            default:
                return STYLE_NULL;
        }
    }
}
