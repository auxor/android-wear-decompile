package android.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.MediaStore;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.telecom.ITelecomService;
import com.android.internal.telephony.IPhoneSubInfo;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephonyRegistry;
import com.android.internal.telephony.ITelephonyRegistry.Stub;
import com.android.internal.telephony.PhoneConstants.State;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelephonyManager {
    public static final String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
    public static final String ACTION_PRECISE_CALL_STATE_CHANGED = "android.intent.action.PRECISE_CALL_STATE";
    public static final String ACTION_PRECISE_DATA_CONNECTION_STATE_CHANGED = "android.intent.action.PRECISE_DATA_CONNECTION_STATE_CHANGED";
    public static final String ACTION_RESPOND_VIA_MESSAGE = "android.intent.action.RESPOND_VIA_MESSAGE";
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_OFFHOOK = 2;
    public static final int CALL_STATE_RINGING = 1;
    public static final int CARRIER_PRIVILEGE_STATUS_ERROR_LOADING_RULES = -2;
    public static final int CARRIER_PRIVILEGE_STATUS_HAS_ACCESS = 1;
    public static final int CARRIER_PRIVILEGE_STATUS_NO_ACCESS = 0;
    public static final int CARRIER_PRIVILEGE_STATUS_RULES_NOT_LOADED = -1;
    public static final int DATA_ACTIVITY_DORMANT = 4;
    public static final int DATA_ACTIVITY_IN = 1;
    public static final int DATA_ACTIVITY_INOUT = 3;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_OUT = 2;
    public static final int DATA_CONNECTED = 2;
    public static final int DATA_CONNECTING = 1;
    public static final int DATA_DISCONNECTED = 0;
    public static final int DATA_SUSPENDED = 3;
    public static final int DATA_UNKNOWN = -1;
    public static final String EXTRA_BACKGROUND_CALL_STATE = "background_state";
    public static final String EXTRA_DATA_APN = "apn";
    public static final String EXTRA_DATA_APN_TYPE = "apnType";
    public static final String EXTRA_DATA_CHANGE_REASON = "reason";
    public static final String EXTRA_DATA_FAILURE_CAUSE = "failCause";
    public static final String EXTRA_DATA_LINK_PROPERTIES_KEY = "linkProperties";
    public static final String EXTRA_DATA_NETWORK_TYPE = "networkType";
    public static final String EXTRA_DATA_STATE = "state";
    public static final String EXTRA_DISCONNECT_CAUSE = "disconnect_cause";
    public static final String EXTRA_FOREGROUND_CALL_STATE = "foreground_state";
    public static final String EXTRA_INCOMING_NUMBER = "incoming_number";
    public static final String EXTRA_PRECISE_DISCONNECT_CAUSE = "precise_disconnect_cause";
    public static final String EXTRA_RINGING_CALL_STATE = "ringing_state";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_STATE_IDLE;
    public static final String EXTRA_STATE_OFFHOOK;
    public static final String EXTRA_STATE_RINGING;
    public static final int NETWORK_CLASS_2_G = 1;
    public static final int NETWORK_CLASS_3_G = 2;
    public static final int NETWORK_CLASS_4_G = 3;
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_GSM = 16;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int PHONE_TYPE_CDMA = 2;
    public static final int PHONE_TYPE_GSM = 1;
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_SIP = 3;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_CARD_IO_ERROR = 8;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_NOT_READY = 6;
    public static final int SIM_STATE_PERM_DISABLED = 7;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_READY = 5;
    public static final int SIM_STATE_UNKNOWN = 0;
    private static final String TAG = "TelephonyManager";
    private static String multiSimConfig;
    private static TelephonyManager sInstance;
    private static final String sKernelCmdLine;
    private static final String sLteOnCdmaProductType;
    private static final Pattern sProductTypePattern;
    private static ITelephonyRegistry sRegistry;
    private final Context mContext;
    private SubscriptionManager mSubscriptionManager;

    /* renamed from: android.telephony.TelephonyManager.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$telephony$TelephonyManager$MultiSimVariants;

        static {
            $SwitchMap$android$telephony$TelephonyManager$MultiSimVariants = new int[MultiSimVariants.values().length];
            try {
                $SwitchMap$android$telephony$TelephonyManager$MultiSimVariants[MultiSimVariants.UNKNOWN.ordinal()] = TelephonyManager.SIM_STATE_ABSENT;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$telephony$TelephonyManager$MultiSimVariants[MultiSimVariants.DSDS.ordinal()] = TelephonyManager.SIM_STATE_PIN_REQUIRED;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$telephony$TelephonyManager$MultiSimVariants[MultiSimVariants.DSDA.ordinal()] = TelephonyManager.SIM_STATE_PUK_REQUIRED;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$telephony$TelephonyManager$MultiSimVariants[MultiSimVariants.TSTS.ordinal()] = TelephonyManager.SIM_STATE_NETWORK_LOCKED;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public enum MultiSimVariants {
        DSDS,
        DSDA,
        TSTS,
        UNKNOWN
    }

    public interface WifiCallingChoices {
        public static final int ALWAYS_USE = 0;
        public static final int ASK_EVERY_TIME = 1;
        public static final int NEVER_USE = 2;
    }

    static {
        multiSimConfig = SystemProperties.get("persist.radio.multisim.config");
        sInstance = new TelephonyManager();
        EXTRA_STATE_IDLE = State.IDLE.toString();
        EXTRA_STATE_RINGING = State.RINGING.toString();
        EXTRA_STATE_OFFHOOK = State.OFFHOOK.toString();
        sKernelCmdLine = getProcCmdLine();
        sProductTypePattern = Pattern.compile("\\sproduct_type\\s*=\\s*(\\w+)");
        sLteOnCdmaProductType = SystemProperties.get("telephony.lteOnCdmaProductType", ProxyInfo.LOCAL_EXCL_LIST);
    }

    public TelephonyManager(Context context) {
        Context appContext = context.getApplicationContext();
        if (appContext != null) {
            this.mContext = appContext;
        } else {
            this.mContext = context;
        }
        this.mSubscriptionManager = SubscriptionManager.from(this.mContext);
        if (sRegistry == null) {
            sRegistry = Stub.asInterface(ServiceManager.getService("telephony.registry"));
        }
    }

    private TelephonyManager() {
        this.mContext = null;
    }

    public static TelephonyManager getDefault() {
        return sInstance;
    }

    public MultiSimVariants getMultiSimConfiguration() {
        String mSimConfig = SystemProperties.get("persist.radio.multisim.config");
        if (mSimConfig.equals("dsds")) {
            return MultiSimVariants.DSDS;
        }
        if (mSimConfig.equals("dsda")) {
            return MultiSimVariants.DSDA;
        }
        if (mSimConfig.equals("tsts")) {
            return MultiSimVariants.TSTS;
        }
        return MultiSimVariants.UNKNOWN;
    }

    public int getPhoneCount() {
        switch (AnonymousClass1.$SwitchMap$android$telephony$TelephonyManager$MultiSimVariants[getMultiSimConfiguration().ordinal()]) {
            case SIM_STATE_ABSENT /*1*/:
                return SIM_STATE_ABSENT;
            case SIM_STATE_PIN_REQUIRED /*2*/:
            case SIM_STATE_PUK_REQUIRED /*3*/:
                return SIM_STATE_PIN_REQUIRED;
            case SIM_STATE_NETWORK_LOCKED /*4*/:
                return SIM_STATE_PUK_REQUIRED;
            default:
                return SIM_STATE_ABSENT;
        }
    }

    public static TelephonyManager from(Context context) {
        return (TelephonyManager) context.getSystemService(Insert.PHONE);
    }

    public boolean isMultiSimEnabled() {
        return multiSimConfig.equals("dsds") || multiSimConfig.equals("dsda") || multiSimConfig.equals("tsts");
    }

    public String getDeviceSoftwareVersion() {
        return getDeviceSoftwareVersion(getDefaultSim());
    }

    public String getDeviceSoftwareVersion(int slotId) {
        String str = null;
        int[] subId = SubscriptionManager.getSubId(slotId);
        if (!(subId == null || subId.length == 0)) {
            try {
                IPhoneSubInfo info = getSubscriberInfo();
                if (info != null) {
                    str = info.getDeviceSvnUsingSubId(subId[SIM_STATE_UNKNOWN]);
                }
            } catch (RemoteException e) {
            } catch (NullPointerException e2) {
            }
        }
        return str;
    }

    public String getDeviceId() {
        String str = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                str = telephony.getDeviceId();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getDeviceId(int slotId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getDeviceIdForPhone(slotId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getImei() {
        return getImei(getDefaultSim());
    }

    public String getImei(int slotId) {
        String str = null;
        int[] subId = SubscriptionManager.getSubId(slotId);
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getImeiForSubscriber(subId[SIM_STATE_UNKNOWN]);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getNai() {
        return getNai(getDefaultSim());
    }

    public String getNai(int slotId) {
        int[] subId = SubscriptionManager.getSubId(slotId);
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info == null) {
                return null;
            }
            String nai = info.getNaiForSubscriber(subId[SIM_STATE_UNKNOWN]);
            if (!Log.isLoggable(TAG, SIM_STATE_PIN_REQUIRED)) {
                return nai;
            }
            Rlog.v(TAG, "Nai = " + nai);
            return nai;
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public CellLocation getCellLocation() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony == null) {
                return null;
            }
            Bundle bundle = telephony.getCellLocation();
            if (bundle.isEmpty()) {
                return null;
            }
            CellLocation cl = CellLocation.newFromBundle(bundle);
            if (cl.isEmpty()) {
                return null;
            }
            return cl;
        } catch (RemoteException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    public void enableLocationUpdates() {
        enableLocationUpdates(getDefaultSubscription());
    }

    public void enableLocationUpdates(int subId) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.enableLocationUpdatesForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public void disableLocationUpdates() {
        disableLocationUpdates(getDefaultSubscription());
    }

    public void disableLocationUpdates(int subId) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.disableLocationUpdatesForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public List<NeighboringCellInfo> getNeighboringCellInfo() {
        List<NeighboringCellInfo> list = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                list = telephony.getNeighboringCellInfo(this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return list;
    }

    public int getCurrentPhoneType() {
        return getCurrentPhoneType(getDefaultSubscription());
    }

    public int getCurrentPhoneType(int subId) {
        int phoneId = SubscriptionManager.getPhoneId(subId);
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getActivePhoneTypeForSubscriber(subId);
            }
            return getPhoneTypeFromProperty(phoneId);
        } catch (RemoteException e) {
            return getPhoneTypeFromProperty(phoneId);
        } catch (NullPointerException e2) {
            return getPhoneTypeFromProperty(phoneId);
        }
    }

    public int getPhoneType() {
        if (isVoiceCapable()) {
            return getCurrentPhoneType();
        }
        return SIM_STATE_UNKNOWN;
    }

    private int getPhoneTypeFromProperty() {
        return getPhoneTypeFromProperty(getDefaultPhone());
    }

    private int getPhoneTypeFromProperty(int phoneId) {
        String type = getTelephonyProperty(phoneId, "gsm.current.phone-type", null);
        if (type == null || type.equals(ProxyInfo.LOCAL_EXCL_LIST)) {
            return getPhoneTypeFromNetworkType(phoneId);
        }
        return Integer.parseInt(type);
    }

    private int getPhoneTypeFromNetworkType() {
        return getPhoneTypeFromNetworkType(getDefaultPhone());
    }

    private int getPhoneTypeFromNetworkType(int phoneId) {
        String mode = getTelephonyProperty(phoneId, "ro.telephony.default_network", null);
        if (mode != null) {
            return getPhoneType(Integer.parseInt(mode));
        }
        return SIM_STATE_UNKNOWN;
    }

    public static int getPhoneType(int networkMode) {
        switch (networkMode) {
            case SIM_STATE_UNKNOWN /*0*/:
            case SIM_STATE_ABSENT /*1*/:
            case SIM_STATE_PIN_REQUIRED /*2*/:
            case SIM_STATE_PUK_REQUIRED /*3*/:
            case NETWORK_TYPE_HSUPA /*9*/:
            case NETWORK_TYPE_HSPA /*10*/:
            case NETWORK_TYPE_EVDO_B /*12*/:
                return SIM_STATE_ABSENT;
            case SIM_STATE_NETWORK_LOCKED /*4*/:
            case SIM_STATE_READY /*5*/:
            case SIM_STATE_NOT_READY /*6*/:
            case SIM_STATE_PERM_DISABLED /*7*/:
            case SIM_STATE_CARD_IO_ERROR /*8*/:
                return SIM_STATE_PIN_REQUIRED;
            case NETWORK_TYPE_IDEN /*11*/:
                if (getLteOnCdmaModeStatic() != SIM_STATE_ABSENT) {
                    return SIM_STATE_ABSENT;
                }
                return SIM_STATE_PIN_REQUIRED;
            default:
                return SIM_STATE_ABSENT;
        }
    }

    private static String getProcCmdLine() {
        IOException e;
        Throwable th;
        String cmdline = ProxyInfo.LOCAL_EXCL_LIST;
        FileInputStream is = null;
        try {
            FileInputStream is2 = new FileInputStream("/proc/cmdline");
            try {
                byte[] buffer = new byte[AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT];
                int count = is2.read(buffer);
                if (count > 0) {
                    cmdline = new String(buffer, SIM_STATE_UNKNOWN, count);
                }
                if (is2 != null) {
                    try {
                        is2.close();
                        is = is2;
                    } catch (IOException e2) {
                        is = is2;
                    }
                }
            } catch (IOException e3) {
                e = e3;
                is = is2;
                try {
                    Rlog.d(TAG, "No /proc/cmdline exception=" + e);
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e4) {
                        }
                    }
                    Rlog.d(TAG, "/proc/cmdline=" + cmdline);
                    return cmdline;
                } catch (Throwable th2) {
                    th = th2;
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e5) {
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                is = is2;
                if (is != null) {
                    is.close();
                }
                throw th;
            }
        } catch (IOException e6) {
            e = e6;
            Rlog.d(TAG, "No /proc/cmdline exception=" + e);
            if (is != null) {
                is.close();
            }
            Rlog.d(TAG, "/proc/cmdline=" + cmdline);
            return cmdline;
        }
        Rlog.d(TAG, "/proc/cmdline=" + cmdline);
        return cmdline;
    }

    public static int getLteOnCdmaModeStatic() {
        String productType = ProxyInfo.LOCAL_EXCL_LIST;
        int curVal = SystemProperties.getInt("telephony.lteOnCdmaDevice", DATA_UNKNOWN);
        int retVal = curVal;
        if (retVal == DATA_UNKNOWN) {
            Matcher matcher = sProductTypePattern.matcher(sKernelCmdLine);
            if (matcher.find()) {
                productType = matcher.group(SIM_STATE_ABSENT);
                if (sLteOnCdmaProductType.equals(productType)) {
                    retVal = SIM_STATE_ABSENT;
                } else {
                    retVal = SIM_STATE_UNKNOWN;
                }
            } else {
                retVal = SIM_STATE_UNKNOWN;
            }
        }
        Rlog.d(TAG, "getLteOnCdmaMode=" + retVal + " curVal=" + curVal + " product_type='" + productType + "' lteOnCdmaProductType='" + sLteOnCdmaProductType + "'");
        return retVal;
    }

    public String getNetworkOperatorName() {
        return getNetworkOperatorName(getDefaultSubscription());
    }

    public String getNetworkOperatorName(int subId) {
        return getTelephonyProperty(SubscriptionManager.getPhoneId(subId), "gsm.operator.alpha", ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getNetworkOperator() {
        return getNetworkOperatorForPhone(getDefaultPhone());
    }

    public String getNetworkOperatorForSubscription(int subId) {
        return getNetworkOperatorForPhone(SubscriptionManager.getPhoneId(subId));
    }

    public String getNetworkOperatorForPhone(int phoneId) {
        return getTelephonyProperty(phoneId, "gsm.operator.numeric", ProxyInfo.LOCAL_EXCL_LIST);
    }

    public boolean isNetworkRoaming() {
        return isNetworkRoaming(getDefaultSubscription());
    }

    public boolean isNetworkRoaming(int subId) {
        return Boolean.parseBoolean(getTelephonyProperty(SubscriptionManager.getPhoneId(subId), "gsm.operator.isroaming", null));
    }

    public String getNetworkCountryIso() {
        return getNetworkCountryIsoForPhone(getDefaultPhone());
    }

    public String getNetworkCountryIsoForSubscription(int subId) {
        return getNetworkCountryIsoForPhone(SubscriptionManager.getPhoneId(subId));
    }

    public String getNetworkCountryIsoForPhone(int phoneId) {
        return getTelephonyProperty(phoneId, "gsm.operator.iso-country", ProxyInfo.LOCAL_EXCL_LIST);
    }

    public int getNetworkType() {
        return getDataNetworkType();
    }

    public int getNetworkType(int subId) {
        int i = SIM_STATE_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getNetworkTypeForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public int getDataNetworkType() {
        return getDataNetworkType(getDefaultSubscription());
    }

    public int getDataNetworkType(int subId) {
        int i = SIM_STATE_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getDataNetworkTypeForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public int getVoiceNetworkType() {
        return getVoiceNetworkType(getDefaultSubscription());
    }

    public int getVoiceNetworkType(int subId) {
        int i = SIM_STATE_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getVoiceNetworkTypeForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case SIM_STATE_ABSENT /*1*/:
            case SIM_STATE_PIN_REQUIRED /*2*/:
            case SIM_STATE_NETWORK_LOCKED /*4*/:
            case SIM_STATE_PERM_DISABLED /*7*/:
            case NETWORK_TYPE_IDEN /*11*/:
            case NETWORK_TYPE_GSM /*16*/:
                return SIM_STATE_ABSENT;
            case SIM_STATE_PUK_REQUIRED /*3*/:
            case SIM_STATE_READY /*5*/:
            case SIM_STATE_NOT_READY /*6*/:
            case SIM_STATE_CARD_IO_ERROR /*8*/:
            case NETWORK_TYPE_HSUPA /*9*/:
            case NETWORK_TYPE_HSPA /*10*/:
            case NETWORK_TYPE_EVDO_B /*12*/:
            case NETWORK_TYPE_EHRPD /*14*/:
            case NETWORK_TYPE_HSPAP /*15*/:
                return SIM_STATE_PIN_REQUIRED;
            case NETWORK_TYPE_LTE /*13*/:
                return SIM_STATE_PUK_REQUIRED;
            default:
                return SIM_STATE_UNKNOWN;
        }
    }

    public String getNetworkTypeName() {
        return getNetworkTypeName(getNetworkType());
    }

    public static String getNetworkTypeName(int type) {
        switch (type) {
            case SIM_STATE_ABSENT /*1*/:
                return "GPRS";
            case SIM_STATE_PIN_REQUIRED /*2*/:
                return "EDGE";
            case SIM_STATE_PUK_REQUIRED /*3*/:
                return "UMTS";
            case SIM_STATE_NETWORK_LOCKED /*4*/:
                return "CDMA";
            case SIM_STATE_READY /*5*/:
                return "CDMA - EvDo rev. 0";
            case SIM_STATE_NOT_READY /*6*/:
                return "CDMA - EvDo rev. A";
            case SIM_STATE_PERM_DISABLED /*7*/:
                return "CDMA - 1xRTT";
            case SIM_STATE_CARD_IO_ERROR /*8*/:
                return "HSDPA";
            case NETWORK_TYPE_HSUPA /*9*/:
                return "HSUPA";
            case NETWORK_TYPE_HSPA /*10*/:
                return "HSPA";
            case NETWORK_TYPE_IDEN /*11*/:
                return "iDEN";
            case NETWORK_TYPE_EVDO_B /*12*/:
                return "CDMA - EvDo rev. B";
            case NETWORK_TYPE_LTE /*13*/:
                return "LTE";
            case NETWORK_TYPE_EHRPD /*14*/:
                return "CDMA - eHRPD";
            case NETWORK_TYPE_HSPAP /*15*/:
                return "HSPA+";
            case NETWORK_TYPE_GSM /*16*/:
                return "GSM";
            default:
                return "UNKNOWN";
        }
    }

    public boolean hasIccCard() {
        return hasIccCard(getDefaultSim());
    }

    public boolean hasIccCard(int slotId) {
        boolean z = false;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                z = telephony.hasIccCardUsingSlotId(slotId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return z;
    }

    public int getSimState() {
        return getSimState(getDefaultSim());
    }

    public int getSimState(int slotIdx) {
        int[] subId = SubscriptionManager.getSubId(slotIdx);
        if (subId == null || subId.length == 0) {
            Rlog.d(TAG, "getSimState:- empty subId return SIM_STATE_ABSENT");
            return SIM_STATE_UNKNOWN;
        }
        int simState = SubscriptionManager.getSimStateForSubscriber(subId[SIM_STATE_UNKNOWN]);
        Rlog.d(TAG, "getSimState: simState=" + simState + " slotIdx=" + slotIdx);
        return simState;
    }

    public String getSimOperator() {
        return getSimOperatorNumeric();
    }

    public String getSimOperator(int subId) {
        return getSimOperatorNumericForSubscription(subId);
    }

    public String getSimOperatorNumeric() {
        int subId = SubscriptionManager.getDefaultDataSubId();
        if (!SubscriptionManager.isUsableSubIdValue(subId)) {
            subId = SubscriptionManager.getDefaultSmsSubId();
            if (!SubscriptionManager.isUsableSubIdValue(subId)) {
                subId = SubscriptionManager.getDefaultVoiceSubId();
                if (!SubscriptionManager.isUsableSubIdValue(subId)) {
                    subId = SubscriptionManager.getDefaultSubId();
                }
            }
        }
        Rlog.d(TAG, "getSimOperatorNumeric(): default subId=" + subId);
        return getSimOperatorNumericForSubscription(subId);
    }

    public String getSimOperatorNumericForSubscription(int subId) {
        return getSimOperatorNumericForPhone(SubscriptionManager.getPhoneId(subId));
    }

    public String getSimOperatorNumericForPhone(int phoneId) {
        return getTelephonyProperty(phoneId, "gsm.sim.operator.numeric", ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getSimOperatorName() {
        return getSimOperatorNameForPhone(getDefaultPhone());
    }

    public String getSimOperatorNameForSubscription(int subId) {
        return getSimOperatorNameForPhone(SubscriptionManager.getPhoneId(subId));
    }

    public String getSimOperatorNameForPhone(int phoneId) {
        return getTelephonyProperty(phoneId, "gsm.sim.operator.alpha", ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getSimCountryIso() {
        return getSimCountryIsoForPhone(getDefaultPhone());
    }

    public String getSimCountryIso(int subId) {
        return getSimCountryIsoForSubscription(subId);
    }

    public String getSimCountryIsoForSubscription(int subId) {
        return getSimCountryIsoForPhone(SubscriptionManager.getPhoneId(subId));
    }

    public String getSimCountryIsoForPhone(int phoneId) {
        return getTelephonyProperty(phoneId, "gsm.sim.operator.iso-country", ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getSimSerialNumber() {
        return getSimSerialNumber(getDefaultSubscription());
    }

    public String getSimSerialNumber(int subId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getIccSerialNumberForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public int getLteOnCdmaMode() {
        return getLteOnCdmaMode(getDefaultSubscription());
    }

    public int getLteOnCdmaMode(int subId) {
        int i = DATA_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getLteOnCdmaModeForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public String getSubscriberId() {
        return getSubscriberId(getDefaultSubscription());
    }

    public String getSubscriberId(int subId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getSubscriberIdForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getGroupIdLevel1() {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getGroupIdLevel1();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getGroupIdLevel1(int subId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getGroupIdLevel1ForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getLine1Number() {
        return getLine1NumberForSubscriber(getDefaultSubscription());
    }

    public String getLine1NumberForSubscriber(int subId) {
        String number = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                number = telephony.getLine1NumberForDisplay(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        if (number != null) {
            return number;
        }
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info == null) {
                return null;
            }
            return info.getLine1NumberForSubscriber(subId);
        } catch (RemoteException e3) {
            return null;
        } catch (NullPointerException e4) {
            return null;
        }
    }

    public boolean setLine1NumberForDisplay(String alphaTag, String number) {
        return setLine1NumberForDisplayForSubscriber(getDefaultSubscription(), alphaTag, number);
    }

    public boolean setLine1NumberForDisplayForSubscriber(int subId, String alphaTag, String number) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.setLine1NumberForDisplayForSubscriber(subId, alphaTag, number);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return false;
    }

    public String getLine1AlphaTag() {
        return getLine1AlphaTagForSubscriber(getDefaultSubscription());
    }

    public String getLine1AlphaTagForSubscriber(int subId) {
        String alphaTag = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                alphaTag = telephony.getLine1AlphaTagForDisplay(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        if (alphaTag != null) {
            return alphaTag;
        }
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info == null) {
                return null;
            }
            return info.getLine1AlphaTagForSubscriber(subId);
        } catch (RemoteException e3) {
            return null;
        } catch (NullPointerException e4) {
            return null;
        }
    }

    public String[] getMergedSubscriberIds() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getMergedSubscriberIds();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return null;
    }

    public String getMsisdn() {
        return getMsisdn(getDefaultSubscription());
    }

    public String getMsisdn(int subId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getMsisdnForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getVoiceMailNumber() {
        return getVoiceMailNumber(getDefaultSubscription());
    }

    public String getVoiceMailNumber(int subId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getVoiceMailNumberForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getCompleteVoiceMailNumber() {
        return getCompleteVoiceMailNumber(getDefaultSubscription());
    }

    public String getCompleteVoiceMailNumber(int subId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getCompleteVoiceMailNumberForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public boolean setVoiceMailNumber(String alphaTag, String number) {
        return setVoiceMailNumber(getDefaultSubscription(), alphaTag, number);
    }

    public boolean setVoiceMailNumber(int subId, String alphaTag, String number) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.setVoiceMailNumber(subId, alphaTag, number);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return false;
    }

    public int getVoiceMessageCount() {
        return getVoiceMessageCount(getDefaultSubscription());
    }

    public int getVoiceMessageCount(int subId) {
        int i = SIM_STATE_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getVoiceMessageCountForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public String getVoiceMailAlphaTag() {
        return getVoiceMailAlphaTag(getDefaultSubscription());
    }

    public String getVoiceMailAlphaTag(int subId) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getVoiceMailAlphaTagForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getIsimImpi() {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getIsimImpi();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getIsimDomain() {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getIsimDomain();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String[] getIsimImpu() {
        String[] strArr = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                strArr = info.getIsimImpu();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return strArr;
    }

    private IPhoneSubInfo getSubscriberInfo() {
        return IPhoneSubInfo.Stub.asInterface(ServiceManager.getService("iphonesubinfo"));
    }

    public int getCallState() {
        try {
            return getTelecomService().getCallState();
        } catch (RemoteException e) {
            return SIM_STATE_UNKNOWN;
        } catch (NullPointerException e2) {
            return SIM_STATE_UNKNOWN;
        }
    }

    public int getCallState(int subId) {
        int i = SIM_STATE_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getCallStateForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public int getDataActivity() {
        int i = SIM_STATE_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getDataActivity();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public int getDataState() {
        int i = SIM_STATE_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getDataState();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    private ITelephony getITelephony() {
        return ITelephony.Stub.asInterface(ServiceManager.getService(Insert.PHONE));
    }

    private ITelecomService getTelecomService() {
        return ITelecomService.Stub.asInterface(ServiceManager.getService(Context.TELECOM_SERVICE));
    }

    public void listen(PhoneStateListener listener, int events) {
        try {
            sRegistry.listenForSubscriber(listener.mSubId, this.mContext != null ? this.mContext.getPackageName() : MediaStore.UNKNOWN_STRING, listener.callback, events, Boolean.valueOf(getITelephony() != null).booleanValue());
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public int getCdmaEriIconIndex() {
        return getCdmaEriIconIndex(getDefaultSubscription());
    }

    public int getCdmaEriIconIndex(int subId) {
        int i = DATA_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getCdmaEriIconIndexForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public int getCdmaEriIconMode() {
        return getCdmaEriIconMode(getDefaultSubscription());
    }

    public int getCdmaEriIconMode(int subId) {
        int i = DATA_UNKNOWN;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                i = telephony.getCdmaEriIconModeForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return i;
    }

    public String getCdmaEriText() {
        return getCdmaEriText(getDefaultSubscription());
    }

    public String getCdmaEriText(int subId) {
        String str = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                str = telephony.getCdmaEriTextForSubscriber(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public boolean isVoiceCapable() {
        if (this.mContext == null) {
            return true;
        }
        return this.mContext.getResources().getBoolean(17956944);
    }

    public boolean isSmsCapable() {
        if (this.mContext == null) {
            return true;
        }
        return this.mContext.getResources().getBoolean(17956946);
    }

    public List<CellInfo> getAllCellInfo() {
        List<CellInfo> list = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                list = telephony.getAllCellInfo();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return list;
    }

    public void setCellInfoListRate(int rateInMillis) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.setCellInfoListRate(rateInMillis);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
    }

    public String getMmsUserAgent() {
        if (this.mContext == null) {
            return null;
        }
        return this.mContext.getResources().getString(17039411);
    }

    public String getMmsUAProfUrl() {
        if (this.mContext == null) {
            return null;
        }
        return this.mContext.getResources().getString(17039412);
    }

    public IccOpenLogicalChannelResponse iccOpenLogicalChannel(String AID) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.iccOpenLogicalChannel(AID);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return null;
    }

    public boolean iccCloseLogicalChannel(int channel) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.iccCloseLogicalChannel(channel);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return false;
    }

    public String iccTransmitApduLogicalChannel(int channel, int cla, int instruction, int p1, int p2, int p3, String data) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.iccTransmitApduLogicalChannel(channel, cla, instruction, p1, p2, p3, data);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return ProxyInfo.LOCAL_EXCL_LIST;
    }

    public String iccTransmitApduBasicChannel(int cla, int instruction, int p1, int p2, int p3, String data) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.iccTransmitApduBasicChannel(cla, instruction, p1, p2, p3, data);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return ProxyInfo.LOCAL_EXCL_LIST;
    }

    public byte[] iccExchangeSimIO(int fileID, int command, int p1, int p2, int p3, String filePath) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.iccExchangeSimIO(fileID, command, p1, p2, p3, filePath);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return null;
    }

    public String sendEnvelopeWithStatus(String content) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.sendEnvelopeWithStatus(content);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return ProxyInfo.LOCAL_EXCL_LIST;
    }

    public String nvReadItem(int itemID) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.nvReadItem(itemID);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "nvReadItem RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "nvReadItem NPE", ex2);
        }
        return ProxyInfo.LOCAL_EXCL_LIST;
    }

    public boolean nvWriteItem(int itemID, String itemValue) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.nvWriteItem(itemID, itemValue);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "nvWriteItem RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "nvWriteItem NPE", ex2);
        }
        return false;
    }

    public boolean nvWriteCdmaPrl(byte[] preferredRoamingList) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.nvWriteCdmaPrl(preferredRoamingList);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "nvWriteCdmaPrl RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "nvWriteCdmaPrl NPE", ex2);
        }
        return false;
    }

    public boolean nvResetConfig(int resetType) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.nvResetConfig(resetType);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "nvResetConfig RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "nvResetConfig NPE", ex2);
        }
        return false;
    }

    private static int getDefaultSubscription() {
        return SubscriptionManager.getDefaultSubId();
    }

    private static int getDefaultPhone() {
        return SubscriptionManager.getPhoneId(SubscriptionManager.getDefaultSubId());
    }

    public int getDefaultSim() {
        return SubscriptionManager.getSlotId(SubscriptionManager.getDefaultSubId());
    }

    public static void setTelephonyProperty(int phoneId, String property, String value) {
        String propVal = ProxyInfo.LOCAL_EXCL_LIST;
        String[] p = null;
        String prop = SystemProperties.get(property);
        if (value == null) {
            value = ProxyInfo.LOCAL_EXCL_LIST;
        }
        if (prop != null) {
            p = prop.split(",");
        }
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            int i = SIM_STATE_UNKNOWN;
            while (i < phoneId) {
                String str = ProxyInfo.LOCAL_EXCL_LIST;
                if (p != null && i < p.length) {
                    str = p[i];
                }
                propVal = propVal + str + ",";
                i += SIM_STATE_ABSENT;
            }
            propVal = propVal + value;
            if (p != null) {
                for (i = phoneId + SIM_STATE_ABSENT; i < p.length; i += SIM_STATE_ABSENT) {
                    propVal = propVal + "," + p[i];
                }
            }
            if (property.length() > 31 || propVal.length() > 91) {
                Rlog.d(TAG, "setTelephonyProperty: property to long phoneId=" + phoneId + " property=" + property + " value: " + value + " propVal=" + propVal);
                return;
            }
            Rlog.d(TAG, "setTelephonyProperty: success phoneId=" + phoneId + " property=" + property + " value: " + value + " propVal=" + propVal);
            SystemProperties.set(property, propVal);
            return;
        }
        Rlog.d(TAG, "setTelephonyProperty: invalid phoneId=" + phoneId + " property=" + property + " value: " + value + " prop=" + prop);
    }

    public static int getIntAtIndex(ContentResolver cr, String name, int index) throws SettingNotFoundException {
        String v = Global.getString(cr, name);
        if (v != null) {
            String[] valArray = v.split(",");
            if (index >= 0 && index < valArray.length && valArray[index] != null) {
                try {
                    return Integer.parseInt(valArray[index]);
                } catch (NumberFormatException e) {
                }
            }
        }
        throw new SettingNotFoundException(name);
    }

    public static boolean putIntAtIndex(ContentResolver cr, String name, int index, int value) {
        String data = ProxyInfo.LOCAL_EXCL_LIST;
        String[] valArray = null;
        String v = Global.getString(cr, name);
        if (index == ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
            throw new RuntimeException("putIntAtIndex index == MAX_VALUE index=" + index);
        } else if (index < 0) {
            throw new RuntimeException("putIntAtIndex index < 0 index=" + index);
        } else {
            if (v != null) {
                valArray = v.split(",");
            }
            int i = SIM_STATE_UNKNOWN;
            while (i < index) {
                String str = ProxyInfo.LOCAL_EXCL_LIST;
                if (valArray != null && i < valArray.length) {
                    str = valArray[i];
                }
                data = data + str + ",";
                i += SIM_STATE_ABSENT;
            }
            data = data + value;
            if (valArray != null) {
                for (i = index + SIM_STATE_ABSENT; i < valArray.length; i += SIM_STATE_ABSENT) {
                    data = data + "," + valArray[i];
                }
            }
            return Global.putString(cr, name, data);
        }
    }

    public static String getTelephonyProperty(int phoneId, String property, String defaultVal) {
        String propVal = null;
        String prop = SystemProperties.get(property);
        if (prop != null && prop.length() > 0) {
            String[] values = prop.split(",");
            if (phoneId >= 0 && phoneId < values.length && values[phoneId] != null) {
                propVal = values[phoneId];
            }
        }
        Rlog.d(TAG, "getTelephonyProperty: return propVal='" + propVal + "' phoneId=" + phoneId + " property='" + property + "' defaultVal='" + defaultVal + "' prop=" + prop);
        return propVal == null ? defaultVal : propVal;
    }

    public int getSimCount() {
        if (isMultiSimEnabled()) {
            return SIM_STATE_PIN_REQUIRED;
        }
        return SIM_STATE_ABSENT;
    }

    public String getIsimIst() {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getIsimIst();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String[] getIsimPcscf() {
        String[] strArr = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                strArr = info.getIsimPcscf();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return strArr;
    }

    public String getIsimChallengeResponse(String nonce) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getIsimChallengeResponse(nonce);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getIccSimChallengeResponse(int subId, int appType, String data) {
        String str = null;
        try {
            IPhoneSubInfo info = getSubscriberInfo();
            if (info != null) {
                str = info.getIccSimChallengeResponse(subId, appType, data);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getIccSimChallengeResponse(int appType, String data) {
        return getIccSimChallengeResponse(getDefaultSubscription(), appType, data);
    }

    public String[] getPcscfAddress(String apnType) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony == null) {
                return new String[SIM_STATE_UNKNOWN];
            }
            return telephony.getPcscfAddress(apnType);
        } catch (RemoteException e) {
            return new String[SIM_STATE_UNKNOWN];
        }
    }

    public void setImsRegistrationState(boolean registered) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.setImsRegistrationState(registered);
            }
        } catch (RemoteException e) {
        }
    }

    public int getPreferredNetworkType() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getPreferredNetworkType();
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "getPreferredNetworkType RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "getPreferredNetworkType NPE", ex2);
        }
        return DATA_UNKNOWN;
    }

    public boolean setPreferredNetworkType(int networkType) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.setPreferredNetworkType(networkType);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "setPreferredNetworkType RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "setPreferredNetworkType NPE", ex2);
        }
        return false;
    }

    public boolean setPreferredNetworkTypeToGlobal() {
        return setPreferredNetworkType(NETWORK_TYPE_HSPA);
    }

    public int getTetherApnRequired() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getTetherApnRequired();
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "hasMatchedTetherApnSetting RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "hasMatchedTetherApnSetting NPE", ex2);
        }
        return SIM_STATE_PIN_REQUIRED;
    }

    public boolean hasCarrierPrivileges() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                if (telephony.getCarrierPrivilegeStatus() == SIM_STATE_ABSENT) {
                    return true;
                }
                return false;
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "hasCarrierPrivileges RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "hasCarrierPrivileges NPE", ex2);
        }
        return false;
    }

    public boolean setOperatorBrandOverride(String brand) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.setOperatorBrandOverride(brand);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "setOperatorBrandOverride RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "setOperatorBrandOverride NPE", ex2);
        }
        return false;
    }

    public boolean setRoamingOverride(List<String> gsmRoamingList, List<String> gsmNonRoamingList, List<String> cdmaRoamingList, List<String> cdmaNonRoamingList) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.setRoamingOverride(gsmRoamingList, gsmNonRoamingList, cdmaRoamingList, cdmaNonRoamingList);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "setRoamingOverride RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "setRoamingOverride NPE", ex2);
        }
        return false;
    }

    public String getCdmaMdn() {
        return getCdmaMdn(getDefaultSubscription());
    }

    public String getCdmaMdn(int subId) {
        String str = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                str = telephony.getCdmaMdn(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public String getCdmaMin() {
        return getCdmaMin(getDefaultSubscription());
    }

    public String getCdmaMin(int subId) {
        String str = null;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                str = telephony.getCdmaMin(subId);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return str;
    }

    public int checkCarrierPrivilegesForPackage(String pkgname) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.checkCarrierPrivilegesForPackage(pkgname);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "checkCarrierPrivilegesForPackage RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "checkCarrierPrivilegesForPackage NPE", ex2);
        }
        return SIM_STATE_UNKNOWN;
    }

    public List<String> getCarrierPackageNamesForIntent(Intent intent) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.getCarrierPackageNamesForIntent(intent);
            }
        } catch (RemoteException ex) {
            Rlog.e(TAG, "getCarrierPackageNamesForIntent RemoteException", ex);
        } catch (NullPointerException ex2) {
            Rlog.e(TAG, "getCarrierPackageNamesForIntent NPE", ex2);
        }
        return null;
    }

    public void dial(String number) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.dial(number);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#dial", e);
        }
    }

    public void call(String callingPackage, String number) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.call(callingPackage, number);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#call", e);
        }
    }

    public boolean endCall() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.endCall();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#endCall", e);
        }
        return false;
    }

    public void answerRingingCall() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.answerRingingCall();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#answerRingingCall", e);
        }
    }

    public void silenceRinger() {
        try {
            getTelecomService().silenceRinger();
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelecomService#silenceRinger", e);
        }
    }

    public boolean isOffhook() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.isOffhook();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#isOffhook", e);
        }
        return false;
    }

    public boolean isRinging() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.isRinging();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#isRinging", e);
        }
        return false;
    }

    public boolean isIdle() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.isIdle();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#isIdle", e);
        }
        return true;
    }

    public boolean isRadioOn() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.isRadioOn();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#isRadioOn", e);
        }
        return false;
    }

    public boolean isSimPinEnabled() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.isSimPinEnabled();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#isSimPinEnabled", e);
        }
        return false;
    }

    public boolean supplyPin(String pin) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.supplyPin(pin);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#supplyPin", e);
        }
        return false;
    }

    public boolean supplyPuk(String puk, String pin) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.supplyPuk(puk, pin);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#supplyPuk", e);
        }
        return false;
    }

    public int[] supplyPinReportResult(String pin) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.supplyPinReportResult(pin);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#supplyPinReportResult", e);
        }
        return new int[SIM_STATE_UNKNOWN];
    }

    public int[] supplyPukReportResult(String puk, String pin) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.supplyPukReportResult(puk, pin);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#]", e);
        }
        return new int[SIM_STATE_UNKNOWN];
    }

    public boolean handlePinMmi(String dialString) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.handlePinMmi(dialString);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#handlePinMmi", e);
        }
        return false;
    }

    public boolean handlePinMmiForSubscriber(int subId, String dialString) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.handlePinMmiForSubscriber(subId, dialString);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#handlePinMmi", e);
        }
        return false;
    }

    public void toggleRadioOnOff() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.toggleRadioOnOff();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#toggleRadioOnOff", e);
        }
    }

    public boolean setRadio(boolean turnOn) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.setRadio(turnOn);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#setRadio", e);
        }
        return false;
    }

    public boolean setRadioPower(boolean turnOn) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.setRadioPower(turnOn);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#setRadioPower", e);
        }
        return false;
    }

    public void updateServiceLocation() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.updateServiceLocation();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#updateServiceLocation", e);
        }
    }

    public boolean enableDataConnectivity() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.enableDataConnectivity();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#enableDataConnectivity", e);
        }
        return false;
    }

    public boolean disableDataConnectivity() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.disableDataConnectivity();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#disableDataConnectivity", e);
        }
        return false;
    }

    public boolean isDataConnectivityPossible() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.isDataConnectivityPossible();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#isDataConnectivityPossible", e);
        }
        return false;
    }

    public boolean needsOtaServiceProvisioning() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.needsOtaServiceProvisioning();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#needsOtaServiceProvisioning", e);
        }
        return false;
    }

    public void setDataEnabled(boolean enable) {
        setDataEnabled(SubscriptionManager.getDefaultDataSubId(), enable);
    }

    public void setDataEnabled(int subId, boolean enable) {
        try {
            Log.d(TAG, "setDataEnabled: enabled=" + enable);
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.setDataEnabled(subId, enable);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#setDataEnabled", e);
        }
    }

    public boolean getDataEnabled() {
        return getDataEnabled(SubscriptionManager.getDefaultDataSubId());
    }

    public boolean getDataEnabled(int subId) {
        boolean retVal = false;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                retVal = telephony.getDataEnabled(subId);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#getDataEnabled", e);
        } catch (NullPointerException e2) {
        }
        Log.d(TAG, "getDataEnabled: retVal=" + retVal);
        return retVal;
    }

    public int invokeOemRilRequestRaw(byte[] oemReq, byte[] oemResp) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.invokeOemRilRequestRaw(oemReq, oemResp);
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return DATA_UNKNOWN;
    }

    public void enableVideoCalling(boolean enable) {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                telephony.enableVideoCalling(enable);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#enableVideoCalling", e);
        }
    }

    public boolean isVideoCallingEnabled() {
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                return telephony.isVideoCallingEnabled();
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error calling ITelephony#isVideoCallingEnabled", e);
        }
        return false;
    }

    public static int getIntWithSubId(ContentResolver cr, String name, int subId, int def) {
        return Global.getInt(cr, name + subId, Global.getInt(cr, name, def));
    }

    public static int getIntWithSubId(ContentResolver cr, String name, int subId) throws SettingNotFoundException {
        int i;
        try {
            i = Global.getInt(cr, name + subId);
        } catch (SettingNotFoundException e) {
            try {
                i = Global.getInt(cr, name);
                Global.putInt(cr, name + subId, i);
                int default_val = i;
                if (name.equals(Global.MOBILE_DATA)) {
                    if ("true".equalsIgnoreCase(SystemProperties.get("ro.com.android.mobiledata", "true"))) {
                        default_val = SIM_STATE_ABSENT;
                    } else {
                        default_val = SIM_STATE_UNKNOWN;
                    }
                } else if (name.equals(SubscriptionManager.DATA_ROAMING)) {
                    default_val = "true".equalsIgnoreCase(SystemProperties.get("ro.com.android.dataroaming", "false")) ? SIM_STATE_ABSENT : SIM_STATE_UNKNOWN;
                }
                if (default_val != i) {
                    Global.putInt(cr, name, default_val);
                }
            } catch (SettingNotFoundException e2) {
                throw new SettingNotFoundException(name);
            }
        }
        return i;
    }

    public boolean isImsRegistered() {
        boolean z = false;
        try {
            ITelephony telephony = getITelephony();
            if (telephony != null) {
                z = telephony.isImsRegistered();
            }
        } catch (RemoteException e) {
        } catch (NullPointerException e2) {
        }
        return z;
    }

    public void setSimOperatorNumeric(String numeric) {
        setSimOperatorNumericForPhone(getDefaultPhone(), numeric);
    }

    public void setSimOperatorNumericForPhone(int phoneId, String numeric) {
        setTelephonyProperty(phoneId, "gsm.sim.operator.numeric", numeric);
    }

    public void setSimOperatorName(String name) {
        setSimOperatorNameForPhone(getDefaultPhone(), name);
    }

    public void setSimOperatorNameForPhone(int phoneId, String name) {
        setTelephonyProperty(phoneId, "gsm.sim.operator.alpha", name);
    }

    public void setSimCountryIso(String iso) {
        setSimCountryIsoForPhone(getDefaultPhone(), iso);
    }

    public void setSimCountryIsoForPhone(int phoneId, String iso) {
        setTelephonyProperty(phoneId, "gsm.sim.operator.iso-country", iso);
    }

    public void setSimState(String state) {
        setSimStateForPhone(getDefaultPhone(), state);
    }

    public void setSimStateForPhone(int phoneId, String state) {
        setTelephonyProperty(phoneId, "gsm.sim.state", state);
    }

    public void setBasebandVersion(String version) {
        setBasebandVersionForPhone(getDefaultPhone(), version);
    }

    public void setBasebandVersionForPhone(int phoneId, String version) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            SystemProperties.set("gsm.version.baseband" + (phoneId == 0 ? ProxyInfo.LOCAL_EXCL_LIST : Integer.toString(phoneId)), version);
        }
    }

    public void setPhoneType(int type) {
        setPhoneType(getDefaultPhone(), type);
    }

    public void setPhoneType(int phoneId, int type) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            setTelephonyProperty(phoneId, "gsm.current.phone-type", String.valueOf(type));
        }
    }

    public String getOtaSpNumberSchema(String defaultValue) {
        return getOtaSpNumberSchemaForPhone(getDefaultPhone(), defaultValue);
    }

    public String getOtaSpNumberSchemaForPhone(int phoneId, String defaultValue) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            return getTelephonyProperty(phoneId, "ro.cdma.otaspnumschema", defaultValue);
        }
        return defaultValue;
    }

    public boolean getSmsReceiveCapable(boolean defaultValue) {
        return getSmsReceiveCapableForPhone(getDefaultPhone(), defaultValue);
    }

    public boolean getSmsReceiveCapableForPhone(int phoneId, boolean defaultValue) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            return Boolean.valueOf(getTelephonyProperty(phoneId, "telephony.sms.receive", String.valueOf(defaultValue))).booleanValue();
        }
        return defaultValue;
    }

    public boolean getSmsSendCapable(boolean defaultValue) {
        return getSmsSendCapableForPhone(getDefaultPhone(), defaultValue);
    }

    public boolean getSmsSendCapableForPhone(int phoneId, boolean defaultValue) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            return Boolean.valueOf(getTelephonyProperty(phoneId, "telephony.sms.send", String.valueOf(defaultValue))).booleanValue();
        }
        return defaultValue;
    }

    public void setNetworkOperatorName(String name) {
        setNetworkOperatorNameForPhone(getDefaultPhone(), name);
    }

    public void setNetworkOperatorNameForPhone(int phoneId, String name) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            setTelephonyProperty(phoneId, "gsm.operator.alpha", name);
        }
    }

    public void setNetworkOperatorNumeric(String numeric) {
        setNetworkOperatorNumericForPhone(getDefaultPhone(), numeric);
    }

    public void setNetworkOperatorNumericForPhone(int phoneId, String numeric) {
        setTelephonyProperty(phoneId, "gsm.operator.numeric", numeric);
    }

    public void setNetworkRoaming(boolean isRoaming) {
        setNetworkRoamingForPhone(getDefaultPhone(), isRoaming);
    }

    public void setNetworkRoamingForPhone(int phoneId, boolean isRoaming) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            setTelephonyProperty(phoneId, "gsm.operator.isroaming", isRoaming ? "true" : "false");
        }
    }

    public void setNetworkCountryIso(String iso) {
        setNetworkCountryIsoForPhone(getDefaultPhone(), iso);
    }

    public void setNetworkCountryIsoForPhone(int phoneId, String iso) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            setTelephonyProperty(phoneId, "gsm.operator.iso-country", iso);
        }
    }

    public void setDataNetworkType(int type) {
        setDataNetworkTypeForPhone(getDefaultPhone(), type);
    }

    public void setDataNetworkTypeForPhone(int phoneId, int type) {
        if (SubscriptionManager.isValidPhoneId(phoneId)) {
            setTelephonyProperty(phoneId, "gsm.network.type", ServiceState.rilRadioTechnologyToString(type));
        }
    }
}
