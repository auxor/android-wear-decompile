package com.google.android.wearable.ambient;

class BrightnessInterpolator {
    private final int mDefaultBrightness;
    private final int[] mPoints;

    BrightnessInterpolator(int[] iArr, int i) {
        this.mPoints = iArr;
        this.mDefaultBrightness = i;
        throw new VerifyError("bad dex opcode");
    }

    int getValue(int i) {
        if (this.mPoints == null) {
            return this.mDefaultBrightness;
        }
        int i2 = Integer.MAX_VALUE;
        int i3 = -1;
        int i4 = -1;
        int i5 = Integer.MIN_VALUE;
        for (int i6 = 0; i6 < this.mPoints.length - 1; i6 += 2) {
            int i7 = this.mPoints[i6];
            int i8 = this.mPoints[i6 + 1];
            if (i7 <= i && i5 < i7) {
                i5 = i7;
                i4 = i8;
            }
            if (i <= i7 && i7 < i2) {
                i3 = i8;
                i2 = i7;
            }
        }
        if (i5 == Integer.MIN_VALUE && i2 == Integer.MAX_VALUE) {
            return this.mDefaultBrightness;
        }
        if (i5 == Integer.MIN_VALUE) {
            return i3;
        }
        if (i2 == Integer.MAX_VALUE) {
            return i4;
        }
        float f = (float) (i2 - i5);
        if (f == 0.0f) {
            return i4;
        }
        float f2 = ((float) (i - i5)) / f;
        return (int) ((f2 * ((float) (i3 - i4))) + ((float) i4));
    }
}
