package android.content.res;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.StateSet;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ColorStateList implements Parcelable {
    public static final Creator<ColorStateList> CREATOR;
    private static final int[][] EMPTY;
    private static final SparseArray<WeakReference<ColorStateList>> sCache;
    private int[] mColors;
    private int mDefaultColor;
    private int[][] mStateSpecs;

    static {
        EMPTY = new int[][]{new int[0]};
        sCache = new SparseArray();
        CREATOR = new Creator<ColorStateList>() {
            public ColorStateList[] newArray(int size) {
                return new ColorStateList[size];
            }

            public ColorStateList createFromParcel(Parcel source) {
                int N = source.readInt();
                int[][] stateSpecs = new int[N][];
                for (int i = 0; i < N; i++) {
                    stateSpecs[i] = source.createIntArray();
                }
                return new ColorStateList(stateSpecs, source.createIntArray());
            }
        };
    }

    private ColorStateList() {
        this.mDefaultColor = Menu.CATEGORY_MASK;
    }

    public ColorStateList(int[][] states, int[] colors) {
        this.mDefaultColor = Menu.CATEGORY_MASK;
        this.mStateSpecs = states;
        this.mColors = colors;
        if (states.length > 0) {
            this.mDefaultColor = colors[0];
            for (int i = 0; i < states.length; i++) {
                if (states[i].length == 0) {
                    this.mDefaultColor = colors[i];
                }
            }
        }
    }

    public static ColorStateList valueOf(int color) {
        synchronized (sCache) {
            WeakReference<ColorStateList> ref = (WeakReference) sCache.get(color);
            ColorStateList csl = ref != null ? (ColorStateList) ref.get() : null;
            if (csl != null) {
                return csl;
            }
            csl = new ColorStateList(EMPTY, new int[]{color});
            sCache.put(color, new WeakReference(csl));
            return csl;
        }
    }

    public static ColorStateList createFromXml(Resources r, XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type == 2) {
            return createFromXmlInner(r, parser, attrs);
        }
        throw new XmlPullParserException("No start tag found");
    }

    private static ColorStateList createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (name.equals("selector")) {
            ColorStateList colorStateList = new ColorStateList();
            colorStateList.inflate(r, parser, attrs);
            return colorStateList;
        }
        throw new XmlPullParserException(parser.getPositionDescription() + ": invalid drawable tag " + name);
    }

    public ColorStateList withAlpha(int alpha) {
        int[] colors = new int[this.mColors.length];
        int len = colors.length;
        for (int i = 0; i < len; i++) {
            colors[i] = (this.mColors[i] & View.MEASURED_SIZE_MASK) | (alpha << 24);
        }
        return new ColorStateList(this.mStateSpecs, colors);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void inflate(android.content.res.Resources r27, org.xmlpull.v1.XmlPullParser r28, android.util.AttributeSet r29) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r26 = this;
        r23 = r28.getDepth();
        r14 = r23 + 1;
        r23 = int[].class;
        r24 = 20;
        r21 = com.android.internal.util.ArrayUtils.newUnpaddedArray(r23, r24);
        r21 = (int[][]) r21;
        r0 = r21;
        r0 = r0.length;
        r23 = r0;
        r0 = r23;
        r9 = new int[r0];
        r17 = 0;
    L_0x001b:
        r22 = r28.next();
        r23 = 1;
        r0 = r22;
        r1 = r23;
        if (r0 == r1) goto L_0x0144;
    L_0x0027:
        r11 = r28.getDepth();
        if (r11 >= r14) goto L_0x0035;
    L_0x002d:
        r23 = 3;
        r0 = r22;
        r1 = r23;
        if (r0 == r1) goto L_0x0144;
    L_0x0035:
        r23 = 2;
        r0 = r22;
        r1 = r23;
        if (r0 != r1) goto L_0x001b;
    L_0x003d:
        if (r11 > r14) goto L_0x001b;
    L_0x003f:
        r23 = r28.getName();
        r24 = "item";
        r23 = r23.equals(r24);
        if (r23 == 0) goto L_0x001b;
    L_0x004b:
        r7 = 0;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r10 = 0;
        r8 = -65536; // 0xffffffffffff0000 float:NaN double:NaN;
        r12 = 0;
        r15 = 0;
        r18 = r29.getAttributeCount();
        r0 = r18;
        r0 = new int[r0];
        r20 = r0;
        r13 = 0;
        r16 = r15;
    L_0x0060:
        r0 = r18;
        if (r13 >= r0) goto L_0x006c;
    L_0x0064:
        r0 = r29;
        r19 = r0.getAttributeNameResource(r13);
        if (r19 != 0) goto L_0x00c8;
    L_0x006c:
        r0 = r20;
        r1 = r16;
        r20 = android.util.StateSet.trimStateSet(r0, r1);
        if (r10 == 0) goto L_0x0125;
    L_0x0076:
        r0 = r27;
        r8 = r0.getColor(r10);
    L_0x007c:
        if (r7 == 0) goto L_0x0084;
    L_0x007e:
        r0 = r27;
        r5 = r0.getFloat(r7);
    L_0x0084:
        r23 = android.graphics.Color.alpha(r8);
        r0 = r23;
        r0 = (float) r0;
        r23 = r0;
        r23 = r23 * r5;
        r0 = r23;
        r0 = (int) r0;
        r23 = r0;
        r24 = 0;
        r25 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r6 = android.util.MathUtils.constrain(r23, r24, r25);
        r23 = 16777215; // 0xffffff float:2.3509886E-38 double:8.2890456E-317;
        r23 = r23 & r8;
        r24 = r6 << 24;
        r8 = r23 | r24;
        if (r17 == 0) goto L_0x00ae;
    L_0x00a7:
        r0 = r20;
        r0 = r0.length;
        r23 = r0;
        if (r23 != 0) goto L_0x00b2;
    L_0x00ae:
        r0 = r26;
        r0.mDefaultColor = r8;
    L_0x00b2:
        r0 = r17;
        r9 = com.android.internal.util.GrowingArrayUtils.append(r9, r0, r8);
        r0 = r21;
        r1 = r17;
        r2 = r20;
        r21 = com.android.internal.util.GrowingArrayUtils.append(r0, r1, r2);
        r21 = (int[][]) r21;
        r17 = r17 + 1;
        goto L_0x001b;
    L_0x00c8:
        r23 = 16843551; // 0x101031f float:2.3695797E-38 double:8.32182E-317;
        r0 = r19;
        r1 = r23;
        if (r0 != r1) goto L_0x00ef;
    L_0x00d1:
        r23 = 0;
        r0 = r29;
        r1 = r23;
        r7 = r0.getAttributeResourceValue(r13, r1);
        if (r7 != 0) goto L_0x0189;
    L_0x00dd:
        r23 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r29;
        r1 = r23;
        r5 = r0.getAttributeFloatValue(r13, r1);
        r15 = r16;
    L_0x00e9:
        r13 = r13 + 1;
        r16 = r15;
        goto L_0x0060;
    L_0x00ef:
        r23 = 16843173; // 0x10101a5 float:2.3694738E-38 double:8.321633E-317;
        r0 = r19;
        r1 = r23;
        if (r0 != r1) goto L_0x010e;
    L_0x00f8:
        r23 = 0;
        r0 = r29;
        r1 = r23;
        r10 = r0.getAttributeResourceValue(r13, r1);
        if (r10 != 0) goto L_0x0189;
    L_0x0104:
        r0 = r29;
        r8 = r0.getAttributeIntValue(r13, r8);
        r12 = 1;
        r15 = r16;
        goto L_0x00e9;
    L_0x010e:
        r15 = r16 + 1;
        r23 = 0;
        r0 = r29;
        r1 = r23;
        r23 = r0.getAttributeBooleanValue(r13, r1);
        if (r23 == 0) goto L_0x011f;
    L_0x011c:
        r20[r16] = r19;
        goto L_0x00e9;
    L_0x011f:
        r0 = r19;
        r0 = -r0;
        r19 = r0;
        goto L_0x011c;
    L_0x0125:
        if (r12 != 0) goto L_0x007c;
    L_0x0127:
        r23 = new org.xmlpull.v1.XmlPullParserException;
        r24 = new java.lang.StringBuilder;
        r24.<init>();
        r25 = r28.getPositionDescription();
        r24 = r24.append(r25);
        r25 = ": <item> tag requires a 'android:color' attribute.";
        r24 = r24.append(r25);
        r24 = r24.toString();
        r23.<init>(r24);
        throw r23;
    L_0x0144:
        r0 = r17;
        r0 = new int[r0];
        r23 = r0;
        r0 = r23;
        r1 = r26;
        r1.mColors = r0;
        r0 = r17;
        r0 = new int[r0][];
        r23 = r0;
        r0 = r23;
        r1 = r26;
        r1.mStateSpecs = r0;
        r23 = 0;
        r0 = r26;
        r0 = r0.mColors;
        r24 = r0;
        r25 = 0;
        r0 = r23;
        r1 = r24;
        r2 = r25;
        r3 = r17;
        java.lang.System.arraycopy(r9, r0, r1, r2, r3);
        r23 = 0;
        r0 = r26;
        r0 = r0.mStateSpecs;
        r24 = r0;
        r25 = 0;
        r0 = r21;
        r1 = r23;
        r2 = r24;
        r3 = r25;
        r4 = r17;
        java.lang.System.arraycopy(r0, r1, r2, r3, r4);
        return;
    L_0x0189:
        r15 = r16;
        goto L_0x00e9;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.res.ColorStateList.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):void");
    }

    public boolean isStateful() {
        return this.mStateSpecs.length > 1;
    }

    public boolean isOpaque() {
        for (int alpha : this.mColors) {
            if (Color.alpha(alpha) != EditorInfo.IME_MASK_ACTION) {
                return false;
            }
        }
        return true;
    }

    public int getColorForState(int[] stateSet, int defaultColor) {
        int setLength = this.mStateSpecs.length;
        for (int i = 0; i < setLength; i++) {
            if (StateSet.stateSetMatches(this.mStateSpecs[i], stateSet)) {
                return this.mColors[i];
            }
        }
        return defaultColor;
    }

    public int getDefaultColor() {
        return this.mDefaultColor;
    }

    public int[][] getStates() {
        return this.mStateSpecs;
    }

    public int[] getColors() {
        return this.mColors;
    }

    public static ColorStateList addFirstIfMissing(ColorStateList colorStateList, int state, int color) {
        int[][] inputStates = colorStateList.getStates();
        for (int[] inputState : inputStates) {
            for (int i : inputState) {
                if (i == state) {
                    return colorStateList;
                }
            }
        }
        int[][] outputStates = new int[(inputStates.length + 1)][];
        System.arraycopy(inputStates, 0, outputStates, 1, inputStates.length);
        outputStates[0] = new int[]{state};
        int[] inputColors = colorStateList.getColors();
        int[] outputColors = new int[(inputColors.length + 1)];
        System.arraycopy(inputColors, 0, outputColors, 1, inputColors.length);
        outputColors[0] = color;
        return new ColorStateList(outputStates, outputColors);
    }

    public String toString() {
        return "ColorStateList{mStateSpecs=" + Arrays.deepToString(this.mStateSpecs) + "mColors=" + Arrays.toString(this.mColors) + "mDefaultColor=" + this.mDefaultColor + '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(N);
        for (int[] writeIntArray : this.mStateSpecs) {
            dest.writeIntArray(writeIntArray);
        }
        dest.writeIntArray(this.mColors);
    }
}
