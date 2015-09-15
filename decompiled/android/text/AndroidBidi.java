package android.text;

import com.android.internal.widget.LockPatternUtils;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

class AndroidBidi {
    private static native int runBidi(int i, char[] cArr, byte[] bArr, int i2, boolean z);

    AndroidBidi() {
    }

    public static int bidi(int dir, char[] chs, byte[] chInfo, int n, boolean haveInfo) {
        if (chs == null || chInfo == null) {
            throw new NullPointerException();
        } else if (n < 0 || chs.length < n || chInfo.length < n) {
            throw new IndexOutOfBoundsException();
        } else {
            switch (dir) {
                case LockPatternUtils.ID_DEFAULT_STATUS_WIDGET /*-2*/:
                    dir = -1;
                    break;
                case GL11ExtensionPack.GL_STR /*-1*/:
                    dir = 1;
                    break;
                case GL10.GL_TRUE /*1*/:
                    dir = 0;
                    break;
                case GL10.GL_LINE_LOOP /*2*/:
                    dir = -2;
                    break;
                default:
                    dir = 0;
                    break;
            }
            return (runBidi(dir, chs, chInfo, n, haveInfo) & 1) == 0 ? 1 : -1;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.text.Layout.Directions directions(int r23, byte[] r24, int r25, char[] r26, int r27, int r28) {
        /*
        if (r28 != 0) goto L_0x0005;
    L_0x0002:
        r21 = android.text.Layout.DIRS_ALL_LEFT_TO_RIGHT;
    L_0x0004:
        return r21;
    L_0x0005:
        r21 = 1;
        r0 = r23;
        r1 = r21;
        if (r0 != r1) goto L_0x0023;
    L_0x000d:
        r2 = 0;
    L_0x000e:
        r4 = r24[r25];
        r13 = r4;
        r17 = 1;
        r7 = r25 + 1;
        r5 = r25 + r28;
    L_0x0017:
        if (r7 >= r5) goto L_0x0025;
    L_0x0019:
        r9 = r24[r7];
        if (r9 == r4) goto L_0x0020;
    L_0x001d:
        r4 = r9;
        r17 = r17 + 1;
    L_0x0020:
        r7 = r7 + 1;
        goto L_0x0017;
    L_0x0023:
        r2 = 1;
        goto L_0x000e;
    L_0x0025:
        r19 = r28;
        r21 = r4 & 1;
        r22 = r2 & 1;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x004b;
    L_0x0031:
        r19 = r19 + -1;
        if (r19 < 0) goto L_0x0041;
    L_0x0035:
        r21 = r27 + r19;
        r3 = r26[r21];
        r21 = 10;
        r0 = r21;
        if (r3 != r0) goto L_0x005c;
    L_0x003f:
        r19 = r19 + -1;
    L_0x0041:
        r19 = r19 + 1;
        r0 = r19;
        r1 = r28;
        if (r0 == r1) goto L_0x004b;
    L_0x0049:
        r17 = r17 + 1;
    L_0x004b:
        r21 = 1;
        r0 = r17;
        r1 = r21;
        if (r0 != r1) goto L_0x006c;
    L_0x0053:
        if (r13 != r2) goto L_0x006c;
    L_0x0055:
        r21 = r13 & 1;
        if (r21 == 0) goto L_0x0069;
    L_0x0059:
        r21 = android.text.Layout.DIRS_ALL_RIGHT_TO_LEFT;
        goto L_0x0004;
    L_0x005c:
        r21 = 32;
        r0 = r21;
        if (r3 == r0) goto L_0x0031;
    L_0x0062:
        r21 = 9;
        r0 = r21;
        if (r3 == r0) goto L_0x0031;
    L_0x0068:
        goto L_0x0041;
    L_0x0069:
        r21 = android.text.Layout.DIRS_ALL_LEFT_TO_RIGHT;
        goto L_0x0004;
    L_0x006c:
        r21 = r17 * 2;
        r0 = r21;
        r8 = new int[r0];
        r12 = r13;
        r10 = r13 << 26;
        r14 = 1;
        r16 = r25;
        r4 = r13;
        r7 = r25;
        r5 = r25 + r19;
        r15 = r14;
    L_0x007e:
        if (r7 >= r5) goto L_0x00a3;
    L_0x0080:
        r9 = r24[r7];
        if (r9 == r4) goto L_0x013b;
    L_0x0084:
        r4 = r9;
        if (r9 <= r12) goto L_0x009f;
    L_0x0087:
        r12 = r9;
    L_0x0088:
        r14 = r15 + 1;
        r21 = r7 - r16;
        r21 = r21 | r10;
        r8[r15] = r21;
        r15 = r14 + 1;
        r21 = r7 - r25;
        r8[r14] = r21;
        r10 = r4 << 26;
        r16 = r7;
        r14 = r15;
    L_0x009b:
        r7 = r7 + 1;
        r15 = r14;
        goto L_0x007e;
    L_0x009f:
        if (r9 >= r13) goto L_0x0088;
    L_0x00a1:
        r13 = r9;
        goto L_0x0088;
    L_0x00a3:
        r21 = r25 + r19;
        r21 = r21 - r16;
        r21 = r21 | r10;
        r8[r15] = r21;
        r0 = r19;
        r1 = r28;
        if (r0 >= r1) goto L_0x0139;
    L_0x00b1:
        r14 = r15 + 1;
        r8[r14] = r19;
        r14 = r14 + 1;
        r21 = r28 - r19;
        r22 = r2 << 26;
        r21 = r21 | r22;
        r8[r14] = r21;
    L_0x00bf:
        r21 = r13 & 1;
        r0 = r21;
        if (r0 != r2) goto L_0x00f8;
    L_0x00c5:
        r13 = r13 + 1;
        if (r12 <= r13) goto L_0x00f5;
    L_0x00c9:
        r18 = 1;
    L_0x00cb:
        if (r18 == 0) goto L_0x0130;
    L_0x00cd:
        r9 = r12 + -1;
    L_0x00cf:
        if (r9 < r13) goto L_0x0130;
    L_0x00d1:
        r7 = 0;
    L_0x00d2:
        r0 = r8.length;
        r21 = r0;
        r0 = r21;
        if (r7 >= r0) goto L_0x012d;
    L_0x00d9:
        r21 = r8[r7];
        r21 = r24[r21];
        r0 = r21;
        if (r0 < r9) goto L_0x012a;
    L_0x00e1:
        r5 = r7 + 2;
    L_0x00e3:
        r0 = r8.length;
        r21 = r0;
        r0 = r21;
        if (r5 >= r0) goto L_0x0106;
    L_0x00ea:
        r21 = r8[r5];
        r21 = r24[r21];
        r0 = r21;
        if (r0 < r9) goto L_0x0106;
    L_0x00f2:
        r5 = r5 + 2;
        goto L_0x00e3;
    L_0x00f5:
        r18 = 0;
        goto L_0x00cb;
    L_0x00f8:
        r21 = 1;
        r0 = r17;
        r1 = r21;
        if (r0 <= r1) goto L_0x0103;
    L_0x0100:
        r18 = 1;
    L_0x0102:
        goto L_0x00cb;
    L_0x0103:
        r18 = 0;
        goto L_0x0102;
    L_0x0106:
        r11 = r7;
        r6 = r5 + -2;
    L_0x0109:
        if (r11 >= r6) goto L_0x0128;
    L_0x010b:
        r20 = r8[r11];
        r21 = r8[r6];
        r8[r11] = r21;
        r8[r6] = r20;
        r21 = r11 + 1;
        r20 = r8[r21];
        r21 = r11 + 1;
        r22 = r6 + 1;
        r22 = r8[r22];
        r8[r21] = r22;
        r21 = r6 + 1;
        r8[r21] = r20;
        r11 = r11 + 2;
        r6 = r6 + -2;
        goto L_0x0109;
    L_0x0128:
        r7 = r5 + 2;
    L_0x012a:
        r7 = r7 + 2;
        goto L_0x00d2;
    L_0x012d:
        r9 = r9 + -1;
        goto L_0x00cf;
    L_0x0130:
        r21 = new android.text.Layout$Directions;
        r0 = r21;
        r0.<init>(r8);
        goto L_0x0004;
    L_0x0139:
        r14 = r15;
        goto L_0x00bf;
    L_0x013b:
        r14 = r15;
        goto L_0x009b;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.AndroidBidi.directions(int, byte[], int, char[], int, int):android.text.Layout$Directions");
    }
}
