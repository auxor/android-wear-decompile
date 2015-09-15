package android.text.method;

public class HideReturnsTransformationMethod extends ReplacementTransformationMethod {
    private static char[] ORIGINAL;
    private static char[] REPLACEMENT;
    private static HideReturnsTransformationMethod sInstance;

    static {
        ORIGINAL = new char[]{'\r'};
        REPLACEMENT = new char[]{'\ufeff'};
    }

    protected char[] getOriginal() {
        return ORIGINAL;
    }

    protected char[] getReplacement() {
        return REPLACEMENT;
    }

    public static HideReturnsTransformationMethod getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        sInstance = new HideReturnsTransformationMethod();
        return sInstance;
    }
}
