package android.text;

import com.android.internal.telephony.RILConstants;
import java.nio.CharBuffer;
import java.util.Locale;

public class TextDirectionHeuristics {
    public static final TextDirectionHeuristic ANYRTL_LTR;
    public static final TextDirectionHeuristic FIRSTSTRONG_LTR;
    public static final TextDirectionHeuristic FIRSTSTRONG_RTL;
    public static final TextDirectionHeuristic LOCALE;
    public static final TextDirectionHeuristic LTR;
    public static final TextDirectionHeuristic RTL;
    private static final int STATE_FALSE = 1;
    private static final int STATE_TRUE = 0;
    private static final int STATE_UNKNOWN = 2;

    private interface TextDirectionAlgorithm {
        int checkRtl(CharSequence charSequence, int i, int i2);
    }

    private static class AnyStrong implements TextDirectionAlgorithm {
        public static final AnyStrong INSTANCE_LTR;
        public static final AnyStrong INSTANCE_RTL;
        private final boolean mLookForRtl;

        public int checkRtl(CharSequence cs, int start, int count) {
            boolean haveUnlookedFor = false;
            int e = start + count;
            for (int i = start; i < e; i += TextDirectionHeuristics.STATE_FALSE) {
                switch (TextDirectionHeuristics.isRtlText(Character.getDirectionality(cs.charAt(i)))) {
                    case TextDirectionHeuristics.STATE_TRUE /*0*/:
                        if (!this.mLookForRtl) {
                            haveUnlookedFor = true;
                            break;
                        }
                        return TextDirectionHeuristics.STATE_TRUE;
                    case TextDirectionHeuristics.STATE_FALSE /*1*/:
                        if (this.mLookForRtl) {
                            haveUnlookedFor = true;
                            break;
                        }
                        return TextDirectionHeuristics.STATE_FALSE;
                    default:
                        break;
                }
            }
            if (!haveUnlookedFor) {
                return TextDirectionHeuristics.STATE_UNKNOWN;
            }
            if (this.mLookForRtl) {
                return TextDirectionHeuristics.STATE_FALSE;
            }
            return TextDirectionHeuristics.STATE_TRUE;
        }

        private AnyStrong(boolean lookForRtl) {
            this.mLookForRtl = lookForRtl;
        }

        static {
            INSTANCE_RTL = new AnyStrong(true);
            INSTANCE_LTR = new AnyStrong(false);
        }
    }

    private static class FirstStrong implements TextDirectionAlgorithm {
        public static final FirstStrong INSTANCE;

        public int checkRtl(CharSequence cs, int start, int count) {
            int result = TextDirectionHeuristics.STATE_UNKNOWN;
            int e = start + count;
            for (int i = start; i < e && result == TextDirectionHeuristics.STATE_UNKNOWN; i += TextDirectionHeuristics.STATE_FALSE) {
                result = TextDirectionHeuristics.isRtlTextOrFormat(Character.getDirectionality(cs.charAt(i)));
            }
            return result;
        }

        private FirstStrong() {
        }

        static {
            INSTANCE = new FirstStrong();
        }
    }

    private static abstract class TextDirectionHeuristicImpl implements TextDirectionHeuristic {
        private final TextDirectionAlgorithm mAlgorithm;

        protected abstract boolean defaultIsRtl();

        public TextDirectionHeuristicImpl(TextDirectionAlgorithm algorithm) {
            this.mAlgorithm = algorithm;
        }

        public boolean isRtl(char[] array, int start, int count) {
            return isRtl(CharBuffer.wrap(array), start, count);
        }

        public boolean isRtl(CharSequence cs, int start, int count) {
            if (cs == null || start < 0 || count < 0 || cs.length() - count < start) {
                throw new IllegalArgumentException();
            } else if (this.mAlgorithm == null) {
                return defaultIsRtl();
            } else {
                return doCheck(cs, start, count);
            }
        }

        private boolean doCheck(CharSequence cs, int start, int count) {
            switch (this.mAlgorithm.checkRtl(cs, start, count)) {
                case TextDirectionHeuristics.STATE_TRUE /*0*/:
                    return true;
                case TextDirectionHeuristics.STATE_FALSE /*1*/:
                    return false;
                default:
                    return defaultIsRtl();
            }
        }
    }

    private static class TextDirectionHeuristicInternal extends TextDirectionHeuristicImpl {
        private final boolean mDefaultIsRtl;

        private TextDirectionHeuristicInternal(TextDirectionAlgorithm algorithm, boolean defaultIsRtl) {
            super(algorithm);
            this.mDefaultIsRtl = defaultIsRtl;
        }

        protected boolean defaultIsRtl() {
            return this.mDefaultIsRtl;
        }
    }

    private static class TextDirectionHeuristicLocale extends TextDirectionHeuristicImpl {
        public static final TextDirectionHeuristicLocale INSTANCE;

        public TextDirectionHeuristicLocale() {
            super(null);
        }

        protected boolean defaultIsRtl() {
            if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == TextDirectionHeuristics.STATE_FALSE) {
                return true;
            }
            return false;
        }

        static {
            INSTANCE = new TextDirectionHeuristicLocale();
        }
    }

    static {
        LTR = new TextDirectionHeuristicInternal(false, null);
        RTL = new TextDirectionHeuristicInternal(true, null);
        FIRSTSTRONG_LTR = new TextDirectionHeuristicInternal(false, null);
        FIRSTSTRONG_RTL = new TextDirectionHeuristicInternal(true, null);
        ANYRTL_LTR = new TextDirectionHeuristicInternal(false, null);
        LOCALE = TextDirectionHeuristicLocale.INSTANCE;
    }

    private static int isRtlText(int directionality) {
        switch (directionality) {
            case STATE_TRUE /*0*/:
                return STATE_FALSE;
            case STATE_FALSE /*1*/:
            case STATE_UNKNOWN /*2*/:
                return STATE_TRUE;
            default:
                return STATE_UNKNOWN;
        }
    }

    private static int isRtlTextOrFormat(int directionality) {
        switch (directionality) {
            case STATE_TRUE /*0*/:
            case RILConstants.RIL_REQUEST_HANGUP_FOREGROUND_RESUME_BACKGROUND /*14*/:
            case RILConstants.RIL_REQUEST_SWITCH_WAITING_OR_HOLDING_AND_ACTIVE /*15*/:
                return STATE_FALSE;
            case STATE_FALSE /*1*/:
            case STATE_UNKNOWN /*2*/:
            case RILConstants.RIL_RESTRICTED_STATE_PS_ALL /*16*/:
            case RILConstants.RIL_REQUEST_UDUB /*17*/:
                return STATE_TRUE;
            default:
                return STATE_UNKNOWN;
        }
    }
}
