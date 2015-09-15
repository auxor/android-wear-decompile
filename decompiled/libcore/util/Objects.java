package libcore.util;

public final class Objects {
    private Objects() {
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String toString(java.lang.Object r12) {
        /*
        r1 = r12.getClass();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r10 = r1.getSimpleName();
        r10 = r7.append(r10);
        r11 = 91;
        r10.append(r11);
        r3 = 0;
        r0 = r1.getDeclaredFields();
        r6 = r0.length;
        r5 = 0;
        r4 = r3;
    L_0x001e:
        if (r5 >= r6) goto L_0x0146;
    L_0x0020:
        r2 = r0[r5];
        r10 = r2.getModifiers();
        r10 = r10 & 136;
        if (r10 == 0) goto L_0x002f;
    L_0x002a:
        r3 = r4;
    L_0x002b:
        r5 = r5 + 1;
        r4 = r3;
        goto L_0x001e;
    L_0x002f:
        r10 = 1;
        r2.setAccessible(r10);
        r9 = r2.get(r12);	 Catch:{ IllegalAccessException -> 0x0150 }
        r3 = r4 + 1;
        if (r4 <= 0) goto L_0x0040;
    L_0x003b:
        r10 = 44;
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
    L_0x0040:
        r10 = r2.getName();	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = 61;
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = r10.isArray();	 Catch:{ IllegalAccessException -> 0x006a }
        if (r10 == 0) goto L_0x010f;
    L_0x0056:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = boolean[].class;
        if (r10 != r11) goto L_0x0071;
    L_0x005e:
        r9 = (boolean[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (boolean[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x006a:
        r8 = move-exception;
    L_0x006b:
        r10 = new java.lang.AssertionError;
        r10.<init>(r8);
        throw r10;
    L_0x0071:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = byte[].class;
        if (r10 != r11) goto L_0x0085;
    L_0x0079:
        r9 = (byte[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (byte[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x0085:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = char[].class;
        if (r10 != r11) goto L_0x0099;
    L_0x008d:
        r9 = (char[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (char[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x0099:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = double[].class;
        if (r10 != r11) goto L_0x00ae;
    L_0x00a1:
        r9 = (double[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (double[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x00ae:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = float[].class;
        if (r10 != r11) goto L_0x00c3;
    L_0x00b6:
        r9 = (float[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (float[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x00c3:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = int[].class;
        if (r10 != r11) goto L_0x00d8;
    L_0x00cb:
        r9 = (int[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (int[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x00d8:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = long[].class;
        if (r10 != r11) goto L_0x00ed;
    L_0x00e0:
        r9 = (long[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (long[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x00ed:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = short[].class;
        if (r10 != r11) goto L_0x0102;
    L_0x00f5:
        r9 = (short[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (short[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x0102:
        r9 = (java.lang.Object[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r9 = (java.lang.Object[]) r9;	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = java.util.Arrays.toString(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x010f:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = java.lang.Character.class;
        if (r10 != r11) goto L_0x0128;
    L_0x0117:
        r10 = 39;
        r10 = r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = r10.append(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = 39;
        r10.append(r11);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x0128:
        r10 = r9.getClass();	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = java.lang.String.class;
        if (r10 != r11) goto L_0x0141;
    L_0x0130:
        r10 = 34;
        r10 = r7.append(r10);	 Catch:{ IllegalAccessException -> 0x006a }
        r10 = r10.append(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        r11 = 34;
        r10.append(r11);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x0141:
        r7.append(r9);	 Catch:{ IllegalAccessException -> 0x006a }
        goto L_0x002b;
    L_0x0146:
        r10 = "]";
        r7.append(r10);
        r10 = r7.toString();
        return r10;
    L_0x0150:
        r8 = move-exception;
        r3 = r4;
        goto L_0x006b;
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.util.Objects.toString(java.lang.Object):java.lang.String");
    }
}
