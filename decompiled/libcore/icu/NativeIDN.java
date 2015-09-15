package libcore.icu;

public final class NativeIDN {
    private static native String convertImpl(String str, int i, boolean z);

    public static String toASCII(String s, int flags) {
        return convert(s, flags, true);
    }

    public static String toUnicode(String s, int flags) {
        try {
            s = convert(s, flags, false);
        } catch (IllegalArgumentException e) {
        }
        return s;
    }

    private static String convert(String s, int flags, boolean toAscii) {
        if (s != null) {
            return convertImpl(s, flags, toAscii);
        }
        throw new NullPointerException("s == null");
    }

    private NativeIDN() {
    }
}
