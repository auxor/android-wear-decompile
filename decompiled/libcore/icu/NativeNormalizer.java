package libcore.icu;

import java.text.Normalizer.Form;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public final class NativeNormalizer {

    /* renamed from: libcore.icu.NativeNormalizer.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$text$Normalizer$Form;

        static {
            $SwitchMap$java$text$Normalizer$Form = new int[Form.values().length];
            try {
                $SwitchMap$java$text$Normalizer$Form[Form.NFC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$text$Normalizer$Form[Form.NFD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$text$Normalizer$Form[Form.NFKC.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$text$Normalizer$Form[Form.NFKD.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private static native boolean isNormalizedImpl(String str, int i);

    private static native String normalizeImpl(String str, int i);

    public static boolean isNormalized(CharSequence src, Form form) {
        return isNormalizedImpl(src.toString(), toUNormalizationMode(form));
    }

    public static String normalize(CharSequence src, Form form) {
        return normalizeImpl(src.toString(), toUNormalizationMode(form));
    }

    private static int toUNormalizationMode(Form form) {
        switch (AnonymousClass1.$SwitchMap$java$text$Normalizer$Form[form.ordinal()]) {
            case NodeFilter.SHOW_ELEMENT /*1*/:
                return 4;
            case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                return 2;
            case XmlPullParser.END_TAG /*3*/:
                return 5;
            case NodeFilter.SHOW_TEXT /*4*/:
                return 3;
            default:
                throw new AssertionError("unknown Normalizer.Form " + form);
        }
    }

    private NativeNormalizer() {
    }
}
