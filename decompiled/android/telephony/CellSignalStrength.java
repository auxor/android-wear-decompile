package android.telephony;

import android.hardware.Camera.Parameters;

public abstract class CellSignalStrength {
    public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final String[] SIGNAL_STRENGTH_NAMES;
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;

    public abstract CellSignalStrength copy();

    public abstract boolean equals(Object obj);

    public abstract int getAsuLevel();

    public abstract int getDbm();

    public abstract int getLevel();

    public abstract int hashCode();

    public abstract void setDefaultValues();

    static {
        String[] strArr = new String[NUM_SIGNAL_STRENGTH_BINS];
        strArr[SIGNAL_STRENGTH_NONE_OR_UNKNOWN] = Parameters.EFFECT_NONE;
        strArr[SIGNAL_STRENGTH_POOR] = "poor";
        strArr[SIGNAL_STRENGTH_MODERATE] = "moderate";
        strArr[SIGNAL_STRENGTH_GOOD] = "good";
        strArr[SIGNAL_STRENGTH_GREAT] = "great";
        SIGNAL_STRENGTH_NAMES = strArr;
    }

    protected CellSignalStrength() {
    }
}
