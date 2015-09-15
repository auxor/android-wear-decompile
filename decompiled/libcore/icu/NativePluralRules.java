package libcore.icu;

import java.util.Locale;

public final class NativePluralRules {
    public static final int FEW = 3;
    public static final int MANY = 4;
    public static final int ONE = 1;
    public static final int OTHER = 5;
    public static final int TWO = 2;
    public static final int ZERO = 0;
    private final long address;

    private static native void finalizeImpl(long j);

    private static native long forLocaleImpl(String str);

    private static native int quantityForIntImpl(long j, int i);

    private NativePluralRules(long address) {
        this.address = address;
    }

    protected void finalize() throws Throwable {
        try {
            finalizeImpl(this.address);
        } finally {
            super.finalize();
        }
    }

    public static NativePluralRules forLocale(Locale locale) {
        return new NativePluralRules(forLocaleImpl(locale.toString()));
    }

    public int quantityForInt(int value) {
        if (value < 0) {
            return OTHER;
        }
        return quantityForIntImpl(this.address, value);
    }
}
