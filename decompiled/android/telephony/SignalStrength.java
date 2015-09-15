package android.telephony;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.utils.CameraBinderDecorator;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.speech.tts.Voice;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

public class SignalStrength implements Parcelable {
    public static final Creator<SignalStrength> CREATOR;
    private static final boolean DBG = false;
    public static final int INVALID = Integer.MAX_VALUE;
    private static final String LOG_TAG = "SignalStrength";
    public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
    private static final int[] RSRP_THRESH_LENIENT;
    private static final int[] RSRP_THRESH_STRICT;
    private static final int RSRP_THRESH_TYPE_STRICT = 0;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final String[] SIGNAL_STRENGTH_NAMES;
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    private boolean isGsm;
    private int mCdmaDbm;
    private int mCdmaEcio;
    private int mEvdoDbm;
    private int mEvdoEcio;
    private int mEvdoSnr;
    private int mGsmBitErrorRate;
    private int mGsmSignalStrength;
    private int mLteCqi;
    private int mLteRsrp;
    private int mLteRsrq;
    private int mLteRssnr;
    private int mLteSignalStrength;

    static {
        String[] strArr = new String[NUM_SIGNAL_STRENGTH_BINS];
        strArr[SIGNAL_STRENGTH_NONE_OR_UNKNOWN] = Parameters.EFFECT_NONE;
        strArr[SIGNAL_STRENGTH_POOR] = "poor";
        strArr[SIGNAL_STRENGTH_MODERATE] = "moderate";
        strArr[SIGNAL_STRENGTH_GOOD] = "good";
        strArr[SIGNAL_STRENGTH_GREAT] = "great";
        SIGNAL_STRENGTH_NAMES = strArr;
        RSRP_THRESH_STRICT = new int[]{-140, PackageManager.INSTALL_FAILED_ABORTED, PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING, -95, -85, -44};
        RSRP_THRESH_LENIENT = new int[]{-140, -128, -118, PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, -98, -44};
        CREATOR = new Creator() {
            public SignalStrength createFromParcel(Parcel in) {
                return new SignalStrength(in);
            }

            public SignalStrength[] newArray(int size) {
                return new SignalStrength[size];
            }
        };
    }

    public static SignalStrength newFromBundle(Bundle m) {
        SignalStrength ret = new SignalStrength();
        ret.setFromNotifierBundle(m);
        return ret;
    }

    public SignalStrength() {
        this.mGsmSignalStrength = 99;
        this.mGsmBitErrorRate = -1;
        this.mCdmaDbm = -1;
        this.mCdmaEcio = -1;
        this.mEvdoDbm = -1;
        this.mEvdoEcio = -1;
        this.mEvdoSnr = -1;
        this.mLteSignalStrength = 99;
        this.mLteRsrp = INVALID;
        this.mLteRsrq = INVALID;
        this.mLteRssnr = INVALID;
        this.mLteCqi = INVALID;
        this.isGsm = true;
    }

    public SignalStrength(boolean gsmFlag) {
        this.mGsmSignalStrength = 99;
        this.mGsmBitErrorRate = -1;
        this.mCdmaDbm = -1;
        this.mCdmaEcio = -1;
        this.mEvdoDbm = -1;
        this.mEvdoEcio = -1;
        this.mEvdoSnr = -1;
        this.mLteSignalStrength = 99;
        this.mLteRsrp = INVALID;
        this.mLteRsrq = INVALID;
        this.mLteRssnr = INVALID;
        this.mLteCqi = INVALID;
        this.isGsm = gsmFlag;
    }

    public SignalStrength(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, int lteSignalStrength, int lteRsrp, int lteRsrq, int lteRssnr, int lteCqi, boolean gsmFlag) {
        initialize(gsmSignalStrength, gsmBitErrorRate, cdmaDbm, cdmaEcio, evdoDbm, evdoEcio, evdoSnr, lteSignalStrength, lteRsrp, lteRsrq, lteRssnr, lteCqi, gsmFlag);
    }

    public SignalStrength(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, boolean gsmFlag) {
        initialize(gsmSignalStrength, gsmBitErrorRate, cdmaDbm, cdmaEcio, evdoDbm, evdoEcio, evdoSnr, 99, INVALID, INVALID, INVALID, INVALID, gsmFlag);
    }

    public SignalStrength(SignalStrength s) {
        copyFrom(s);
    }

    public void initialize(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, boolean gsm) {
        initialize(gsmSignalStrength, gsmBitErrorRate, cdmaDbm, cdmaEcio, evdoDbm, evdoEcio, evdoSnr, 99, INVALID, INVALID, INVALID, INVALID, gsm);
    }

    public void initialize(int gsmSignalStrength, int gsmBitErrorRate, int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr, int lteSignalStrength, int lteRsrp, int lteRsrq, int lteRssnr, int lteCqi, boolean gsm) {
        this.mGsmSignalStrength = gsmSignalStrength;
        this.mGsmBitErrorRate = gsmBitErrorRate;
        this.mCdmaDbm = cdmaDbm;
        this.mCdmaEcio = cdmaEcio;
        this.mEvdoDbm = evdoDbm;
        this.mEvdoEcio = evdoEcio;
        this.mEvdoSnr = evdoSnr;
        this.mLteSignalStrength = lteSignalStrength;
        this.mLteRsrp = lteRsrp;
        this.mLteRsrq = lteRsrq;
        this.mLteRssnr = lteRssnr;
        this.mLteCqi = lteCqi;
        this.isGsm = gsm;
    }

    protected void copyFrom(SignalStrength s) {
        this.mGsmSignalStrength = s.mGsmSignalStrength;
        this.mGsmBitErrorRate = s.mGsmBitErrorRate;
        this.mCdmaDbm = s.mCdmaDbm;
        this.mCdmaEcio = s.mCdmaEcio;
        this.mEvdoDbm = s.mEvdoDbm;
        this.mEvdoEcio = s.mEvdoEcio;
        this.mEvdoSnr = s.mEvdoSnr;
        this.mLteSignalStrength = s.mLteSignalStrength;
        this.mLteRsrp = s.mLteRsrp;
        this.mLteRsrq = s.mLteRsrq;
        this.mLteRssnr = s.mLteRssnr;
        this.mLteCqi = s.mLteCqi;
        this.isGsm = s.isGsm;
    }

    public SignalStrength(Parcel in) {
        this.mGsmSignalStrength = in.readInt();
        this.mGsmBitErrorRate = in.readInt();
        this.mCdmaDbm = in.readInt();
        this.mCdmaEcio = in.readInt();
        this.mEvdoDbm = in.readInt();
        this.mEvdoEcio = in.readInt();
        this.mEvdoSnr = in.readInt();
        this.mLteSignalStrength = in.readInt();
        this.mLteRsrp = in.readInt();
        this.mLteRsrq = in.readInt();
        this.mLteRssnr = in.readInt();
        this.mLteCqi = in.readInt();
        this.isGsm = in.readInt() != 0 ? true : DBG;
    }

    public static SignalStrength makeSignalStrengthFromRilParcel(Parcel in) {
        SignalStrength ss = new SignalStrength();
        ss.mGsmSignalStrength = in.readInt();
        ss.mGsmBitErrorRate = in.readInt();
        ss.mCdmaDbm = in.readInt();
        ss.mCdmaEcio = in.readInt();
        ss.mEvdoDbm = in.readInt();
        ss.mEvdoEcio = in.readInt();
        ss.mEvdoSnr = in.readInt();
        ss.mLteSignalStrength = in.readInt();
        ss.mLteRsrp = in.readInt();
        ss.mLteRsrq = in.readInt();
        ss.mLteRssnr = in.readInt();
        ss.mLteCqi = in.readInt();
        return ss;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mGsmSignalStrength);
        out.writeInt(this.mGsmBitErrorRate);
        out.writeInt(this.mCdmaDbm);
        out.writeInt(this.mCdmaEcio);
        out.writeInt(this.mEvdoDbm);
        out.writeInt(this.mEvdoEcio);
        out.writeInt(this.mEvdoSnr);
        out.writeInt(this.mLteSignalStrength);
        out.writeInt(this.mLteRsrp);
        out.writeInt(this.mLteRsrq);
        out.writeInt(this.mLteRssnr);
        out.writeInt(this.mLteCqi);
        out.writeInt(this.isGsm ? SIGNAL_STRENGTH_POOR : SIGNAL_STRENGTH_NONE_OR_UNKNOWN);
    }

    public int describeContents() {
        return SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
    }

    public void validateInput() {
        int i;
        int i2 = 99;
        int i3 = -1;
        int i4 = -120;
        int i5 = INVALID;
        this.mGsmSignalStrength = this.mGsmSignalStrength >= 0 ? this.mGsmSignalStrength : 99;
        if (this.mCdmaDbm > 0) {
            i = -this.mCdmaDbm;
        } else {
            i = -120;
        }
        this.mCdmaDbm = i;
        this.mCdmaEcio = this.mCdmaEcio > 0 ? -this.mCdmaEcio : -160;
        if (this.mEvdoDbm > 0) {
            i4 = -this.mEvdoDbm;
        }
        this.mEvdoDbm = i4;
        if (this.mEvdoEcio >= 0) {
            i = -this.mEvdoEcio;
        } else {
            i = -1;
        }
        this.mEvdoEcio = i;
        if (this.mEvdoSnr > 0 && this.mEvdoSnr <= 8) {
            i3 = this.mEvdoSnr;
        }
        this.mEvdoSnr = i3;
        if (this.mLteSignalStrength >= 0) {
            i2 = this.mLteSignalStrength;
        }
        this.mLteSignalStrength = i2;
        if (this.mLteRsrp < 44 || this.mLteRsrp > KeyEvent.KEYCODE_F10) {
            i = INVALID;
        } else {
            i = -this.mLteRsrp;
        }
        this.mLteRsrp = i;
        if (this.mLteRsrq < SIGNAL_STRENGTH_GOOD || this.mLteRsrq > 20) {
            i = INVALID;
        } else {
            i = -this.mLteRsrq;
        }
        this.mLteRsrq = i;
        if (this.mLteRssnr >= -200 && this.mLteRssnr <= Voice.QUALITY_NORMAL) {
            i5 = this.mLteRssnr;
        }
        this.mLteRssnr = i5;
    }

    public void setGsm(boolean gsmFlag) {
        this.isGsm = gsmFlag;
    }

    public int getGsmSignalStrength() {
        return this.mGsmSignalStrength;
    }

    public int getGsmBitErrorRate() {
        return this.mGsmBitErrorRate;
    }

    public int getCdmaDbm() {
        return this.mCdmaDbm;
    }

    public int getCdmaEcio() {
        return this.mCdmaEcio;
    }

    public int getEvdoDbm() {
        return this.mEvdoDbm;
    }

    public int getEvdoEcio() {
        return this.mEvdoEcio;
    }

    public int getEvdoSnr() {
        return this.mEvdoSnr;
    }

    public int getLteSignalStrength() {
        return this.mLteSignalStrength;
    }

    public int getLteRsrp() {
        return this.mLteRsrp;
    }

    public int getLteRsrq() {
        return this.mLteRsrq;
    }

    public int getLteRssnr() {
        return this.mLteRssnr;
    }

    public int getLteCqi() {
        return this.mLteCqi;
    }

    public int getLevel() {
        if (this.isGsm) {
            int level = getLteLevel();
            if (level == 0) {
                return getGsmLevel();
            }
            return level;
        }
        int cdmaLevel = getCdmaLevel();
        int evdoLevel = getEvdoLevel();
        if (evdoLevel == 0) {
            return cdmaLevel;
        }
        if (cdmaLevel == 0) {
            return evdoLevel;
        }
        return cdmaLevel < evdoLevel ? cdmaLevel : evdoLevel;
    }

    public int getAsuLevel() {
        if (!this.isGsm) {
            int cdmaAsuLevel = getCdmaAsuLevel();
            int evdoAsuLevel = getEvdoAsuLevel();
            if (evdoAsuLevel == 0) {
                return cdmaAsuLevel;
            }
            if (cdmaAsuLevel == 0) {
                return evdoAsuLevel;
            }
            return cdmaAsuLevel < evdoAsuLevel ? cdmaAsuLevel : evdoAsuLevel;
        } else if (getLteLevel() == 0) {
            return getGsmAsuLevel();
        } else {
            return getLteAsuLevel();
        }
    }

    public int getDbm() {
        if (isGsm()) {
            int dBm = getLteDbm();
            if (dBm == INVALID) {
                return getGsmDbm();
            }
            return dBm;
        }
        int cdmaDbm = getCdmaDbm();
        int evdoDbm = getEvdoDbm();
        if (evdoDbm != -120) {
            if (cdmaDbm == -120) {
                cdmaDbm = evdoDbm;
            } else if (cdmaDbm >= evdoDbm) {
                cdmaDbm = evdoDbm;
            }
        }
        return cdmaDbm;
    }

    public int getGsmDbm() {
        int asu;
        int gsmSignalStrength = getGsmSignalStrength();
        if (gsmSignalStrength == 99) {
            asu = -1;
        } else {
            asu = gsmSignalStrength;
        }
        if (asu != -1) {
            return (asu * SIGNAL_STRENGTH_MODERATE) + PackageManager.INSTALL_FAILED_NO_MATCHING_ABIS;
        }
        return -1;
    }

    public int getGsmLevel() {
        int asu = getGsmSignalStrength();
        if (asu <= SIGNAL_STRENGTH_MODERATE || asu == 99) {
            return SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        if (asu >= 12) {
            return SIGNAL_STRENGTH_GREAT;
        }
        if (asu >= 8) {
            return SIGNAL_STRENGTH_GOOD;
        }
        if (asu >= NUM_SIGNAL_STRENGTH_BINS) {
            return SIGNAL_STRENGTH_MODERATE;
        }
        return SIGNAL_STRENGTH_POOR;
    }

    public int getGsmAsuLevel() {
        return getGsmSignalStrength();
    }

    public int getCdmaLevel() {
        int levelDbm;
        int levelEcio;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        if (cdmaDbm >= -75) {
            levelDbm = SIGNAL_STRENGTH_GREAT;
        } else if (cdmaDbm >= -85) {
            levelDbm = SIGNAL_STRENGTH_GOOD;
        } else if (cdmaDbm >= -95) {
            levelDbm = SIGNAL_STRENGTH_MODERATE;
        } else if (cdmaDbm >= -100) {
            levelDbm = SIGNAL_STRENGTH_POOR;
        } else {
            levelDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        if (cdmaEcio >= -90) {
            levelEcio = SIGNAL_STRENGTH_GREAT;
        } else if (cdmaEcio >= CameraBinderDecorator.TIMED_OUT) {
            levelEcio = SIGNAL_STRENGTH_GOOD;
        } else if (cdmaEcio >= -130) {
            levelEcio = SIGNAL_STRENGTH_MODERATE;
        } else if (cdmaEcio >= -150) {
            levelEcio = SIGNAL_STRENGTH_POOR;
        } else {
            levelEcio = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        if (levelDbm < levelEcio) {
            return levelDbm;
        }
        return levelEcio;
    }

    public int getCdmaAsuLevel() {
        int cdmaAsuLevel;
        int ecioAsuLevel;
        int cdmaDbm = getCdmaDbm();
        int cdmaEcio = getCdmaEcio();
        if (cdmaDbm >= -75) {
            cdmaAsuLevel = 16;
        } else if (cdmaDbm >= -82) {
            cdmaAsuLevel = 8;
        } else if (cdmaDbm >= -90) {
            cdmaAsuLevel = SIGNAL_STRENGTH_GREAT;
        } else if (cdmaDbm >= -95) {
            cdmaAsuLevel = SIGNAL_STRENGTH_MODERATE;
        } else if (cdmaDbm >= -100) {
            cdmaAsuLevel = SIGNAL_STRENGTH_POOR;
        } else {
            cdmaAsuLevel = 99;
        }
        if (cdmaEcio >= -90) {
            ecioAsuLevel = 16;
        } else if (cdmaEcio >= -100) {
            ecioAsuLevel = 8;
        } else if (cdmaEcio >= PackageManager.INSTALL_FAILED_ABORTED) {
            ecioAsuLevel = SIGNAL_STRENGTH_GREAT;
        } else if (cdmaEcio >= -130) {
            ecioAsuLevel = SIGNAL_STRENGTH_MODERATE;
        } else if (cdmaEcio >= -150) {
            ecioAsuLevel = SIGNAL_STRENGTH_POOR;
        } else {
            ecioAsuLevel = 99;
        }
        if (cdmaAsuLevel < ecioAsuLevel) {
            return cdmaAsuLevel;
        }
        return ecioAsuLevel;
    }

    public int getEvdoLevel() {
        int levelEvdoDbm;
        int levelEvdoSnr;
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        if (evdoDbm >= -65) {
            levelEvdoDbm = SIGNAL_STRENGTH_GREAT;
        } else if (evdoDbm >= -75) {
            levelEvdoDbm = SIGNAL_STRENGTH_GOOD;
        } else if (evdoDbm >= -90) {
            levelEvdoDbm = SIGNAL_STRENGTH_MODERATE;
        } else if (evdoDbm >= PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING) {
            levelEvdoDbm = SIGNAL_STRENGTH_POOR;
        } else {
            levelEvdoDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        if (evdoSnr >= 7) {
            levelEvdoSnr = SIGNAL_STRENGTH_GREAT;
        } else if (evdoSnr >= NUM_SIGNAL_STRENGTH_BINS) {
            levelEvdoSnr = SIGNAL_STRENGTH_GOOD;
        } else if (evdoSnr >= SIGNAL_STRENGTH_GOOD) {
            levelEvdoSnr = SIGNAL_STRENGTH_MODERATE;
        } else if (evdoSnr >= SIGNAL_STRENGTH_POOR) {
            levelEvdoSnr = SIGNAL_STRENGTH_POOR;
        } else {
            levelEvdoSnr = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        if (levelEvdoDbm < levelEvdoSnr) {
            return levelEvdoDbm;
        }
        return levelEvdoSnr;
    }

    public int getEvdoAsuLevel() {
        int levelEvdoDbm;
        int levelEvdoSnr;
        int evdoDbm = getEvdoDbm();
        int evdoSnr = getEvdoSnr();
        if (evdoDbm >= -65) {
            levelEvdoDbm = 16;
        } else if (evdoDbm >= -75) {
            levelEvdoDbm = 8;
        } else if (evdoDbm >= -85) {
            levelEvdoDbm = SIGNAL_STRENGTH_GREAT;
        } else if (evdoDbm >= -95) {
            levelEvdoDbm = SIGNAL_STRENGTH_MODERATE;
        } else if (evdoDbm >= PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING) {
            levelEvdoDbm = SIGNAL_STRENGTH_POOR;
        } else {
            levelEvdoDbm = 99;
        }
        if (evdoSnr >= 7) {
            levelEvdoSnr = 16;
        } else if (evdoSnr >= 6) {
            levelEvdoSnr = 8;
        } else if (evdoSnr >= NUM_SIGNAL_STRENGTH_BINS) {
            levelEvdoSnr = SIGNAL_STRENGTH_GREAT;
        } else if (evdoSnr >= SIGNAL_STRENGTH_GOOD) {
            levelEvdoSnr = SIGNAL_STRENGTH_MODERATE;
        } else if (evdoSnr >= SIGNAL_STRENGTH_POOR) {
            levelEvdoSnr = SIGNAL_STRENGTH_POOR;
        } else {
            levelEvdoSnr = 99;
        }
        if (levelEvdoDbm < levelEvdoSnr) {
            return levelEvdoDbm;
        }
        return levelEvdoSnr;
    }

    public int getLteDbm() {
        return this.mLteRsrp;
    }

    public int getLteLevel() {
        int[] threshRsrp;
        int rssiIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        int rsrpIconLevel = -1;
        int snrIconLevel = -1;
        if (Resources.getSystem().getInteger(17694850) == 0) {
            threshRsrp = RSRP_THRESH_STRICT;
        } else {
            threshRsrp = RSRP_THRESH_LENIENT;
        }
        if (this.mLteRsrp > threshRsrp[NUM_SIGNAL_STRENGTH_BINS]) {
            rsrpIconLevel = -1;
        } else if (this.mLteRsrp >= threshRsrp[SIGNAL_STRENGTH_GREAT]) {
            rsrpIconLevel = SIGNAL_STRENGTH_GREAT;
        } else if (this.mLteRsrp >= threshRsrp[SIGNAL_STRENGTH_GOOD]) {
            rsrpIconLevel = SIGNAL_STRENGTH_GOOD;
        } else if (this.mLteRsrp >= threshRsrp[SIGNAL_STRENGTH_MODERATE]) {
            rsrpIconLevel = SIGNAL_STRENGTH_MODERATE;
        } else if (this.mLteRsrp >= threshRsrp[SIGNAL_STRENGTH_POOR]) {
            rsrpIconLevel = SIGNAL_STRENGTH_POOR;
        } else if (this.mLteRsrp >= threshRsrp[SIGNAL_STRENGTH_NONE_OR_UNKNOWN]) {
            rsrpIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        if (this.mLteRssnr > Voice.QUALITY_NORMAL) {
            snrIconLevel = -1;
        } else if (this.mLteRssnr >= KeyEvent.KEYCODE_MEDIA_RECORD) {
            snrIconLevel = SIGNAL_STRENGTH_GREAT;
        } else if (this.mLteRssnr >= 45) {
            snrIconLevel = SIGNAL_STRENGTH_GOOD;
        } else if (this.mLteRssnr >= 10) {
            snrIconLevel = SIGNAL_STRENGTH_MODERATE;
        } else if (this.mLteRssnr >= -30) {
            snrIconLevel = SIGNAL_STRENGTH_POOR;
        } else if (this.mLteRssnr >= -200) {
            snrIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
        }
        if (snrIconLevel == -1 || rsrpIconLevel == -1) {
            if (snrIconLevel != -1) {
                return snrIconLevel;
            }
            if (rsrpIconLevel != -1) {
                return rsrpIconLevel;
            }
            if (this.mLteSignalStrength > 63) {
                rssiIconLevel = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            } else if (this.mLteSignalStrength >= 12) {
                rssiIconLevel = SIGNAL_STRENGTH_GREAT;
            } else if (this.mLteSignalStrength >= 8) {
                rssiIconLevel = SIGNAL_STRENGTH_GOOD;
            } else if (this.mLteSignalStrength >= NUM_SIGNAL_STRENGTH_BINS) {
                rssiIconLevel = SIGNAL_STRENGTH_MODERATE;
            } else if (this.mLteSignalStrength >= 0) {
                rssiIconLevel = SIGNAL_STRENGTH_POOR;
            }
            return rssiIconLevel;
        } else if (rsrpIconLevel < snrIconLevel) {
            return rsrpIconLevel;
        } else {
            return snrIconLevel;
        }
    }

    public int getLteAsuLevel() {
        int lteDbm = getLteDbm();
        if (lteDbm == INVALID) {
            return EditorInfo.IME_MASK_ACTION;
        }
        return lteDbm + KeyEvent.KEYCODE_F10;
    }

    public boolean isGsm() {
        return this.isGsm;
    }

    public int hashCode() {
        return (this.isGsm ? SIGNAL_STRENGTH_POOR : SIGNAL_STRENGTH_NONE_OR_UNKNOWN) + ((this.mLteCqi * 31) + (((((((((((this.mGsmSignalStrength * 31) + (this.mGsmBitErrorRate * 31)) + (this.mCdmaDbm * 31)) + (this.mCdmaEcio * 31)) + (this.mEvdoDbm * 31)) + (this.mEvdoEcio * 31)) + (this.mEvdoSnr * 31)) + (this.mLteSignalStrength * 31)) + (this.mLteRsrp * 31)) + (this.mLteRsrq * 31)) + (this.mLteRssnr * 31)));
    }

    public boolean equals(Object o) {
        try {
            SignalStrength s = (SignalStrength) o;
            if (o != null && this.mGsmSignalStrength == s.mGsmSignalStrength && this.mGsmBitErrorRate == s.mGsmBitErrorRate && this.mCdmaDbm == s.mCdmaDbm && this.mCdmaEcio == s.mCdmaEcio && this.mEvdoDbm == s.mEvdoDbm && this.mEvdoEcio == s.mEvdoEcio && this.mEvdoSnr == s.mEvdoSnr && this.mLteSignalStrength == s.mLteSignalStrength && this.mLteRsrp == s.mLteRsrp && this.mLteRsrq == s.mLteRsrq && this.mLteRssnr == s.mLteRssnr && this.mLteCqi == s.mLteCqi && this.isGsm == s.isGsm) {
                return true;
            }
            return DBG;
        } catch (ClassCastException e) {
            return DBG;
        }
    }

    public String toString() {
        return "SignalStrength: " + this.mGsmSignalStrength + " " + this.mGsmBitErrorRate + " " + this.mCdmaDbm + " " + this.mCdmaEcio + " " + this.mEvdoDbm + " " + this.mEvdoEcio + " " + this.mEvdoSnr + " " + this.mLteSignalStrength + " " + this.mLteRsrp + " " + this.mLteRsrq + " " + this.mLteRssnr + " " + this.mLteCqi + " " + (this.isGsm ? "gsm|lte" : "cdma");
    }

    private void setFromNotifierBundle(Bundle m) {
        this.mGsmSignalStrength = m.getInt("GsmSignalStrength");
        this.mGsmBitErrorRate = m.getInt("GsmBitErrorRate");
        this.mCdmaDbm = m.getInt("CdmaDbm");
        this.mCdmaEcio = m.getInt("CdmaEcio");
        this.mEvdoDbm = m.getInt("EvdoDbm");
        this.mEvdoEcio = m.getInt("EvdoEcio");
        this.mEvdoSnr = m.getInt("EvdoSnr");
        this.mLteSignalStrength = m.getInt("LteSignalStrength");
        this.mLteRsrp = m.getInt("LteRsrp");
        this.mLteRsrq = m.getInt("LteRsrq");
        this.mLteRssnr = m.getInt("LteRssnr");
        this.mLteCqi = m.getInt("LteCqi");
        this.isGsm = m.getBoolean("isGsm");
    }

    public void fillInNotifierBundle(Bundle m) {
        m.putInt("GsmSignalStrength", this.mGsmSignalStrength);
        m.putInt("GsmBitErrorRate", this.mGsmBitErrorRate);
        m.putInt("CdmaDbm", this.mCdmaDbm);
        m.putInt("CdmaEcio", this.mCdmaEcio);
        m.putInt("EvdoDbm", this.mEvdoDbm);
        m.putInt("EvdoEcio", this.mEvdoEcio);
        m.putInt("EvdoSnr", this.mEvdoSnr);
        m.putInt("LteSignalStrength", this.mLteSignalStrength);
        m.putInt("LteRsrp", this.mLteRsrp);
        m.putInt("LteRsrq", this.mLteRsrq);
        m.putInt("LteRssnr", this.mLteRssnr);
        m.putInt("LteCqi", this.mLteCqi);
        m.putBoolean("isGsm", Boolean.valueOf(this.isGsm).booleanValue());
    }

    private static void log(String s) {
        Rlog.w(LOG_TAG, s);
    }
}
