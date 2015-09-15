package java.net;

import libcore.icu.NativeIDN;

public final class IDN {
    public static final int ALLOW_UNASSIGNED = 1;
    public static final int USE_STD3_ASCII_RULES = 2;

    private IDN() {
    }

    public static String toASCII(String input, int flags) {
        return NativeIDN.toASCII(input, flags);
    }

    public static String toASCII(String input) {
        return toASCII(input, 0);
    }

    public static String toUnicode(String input, int flags) {
        return NativeIDN.toUnicode(input, flags);
    }

    public static String toUnicode(String input) {
        return NativeIDN.toUnicode(input, 0);
    }
}
