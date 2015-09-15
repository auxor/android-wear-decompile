package android.text.method;

public class SingleLineTransformationMethod extends ReplacementTransformationMethod {
    private static char[] ORIGINAL;
    private static char[] REPLACEMENT;
    private static SingleLineTransformationMethod sInstance;

    static {
        ORIGINAL = new char[]{'\n', '\r'};
        REPLACEMENT = new char[]{' ', '\ufeff'};
    }

    protected char[] getOriginal() {
        return ORIGINAL;
    }

    protected char[] getReplacement() {
        return REPLACEMENT;
    }

    public static SingleLineTransformationMethod getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        sInstance = new SingleLineTransformationMethod();
        return sInstance;
    }
}
