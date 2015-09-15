package android.os;

import android.net.ProxyInfo;
import android.text.TextUtils;
import android.util.Slog;
import dalvik.system.VMRuntime;
import java.util.Objects;

public class Build {
    public static final String BOARD;
    public static final String BOOTLOADER;
    public static final String BRAND;
    @Deprecated
    public static final String CPU_ABI;
    @Deprecated
    public static final String CPU_ABI2;
    public static final String DEVICE;
    public static final String DISPLAY;
    public static final String FINGERPRINT;
    public static final String HARDWARE;
    public static final String HOST;
    public static final String ID;
    public static final boolean IS_DEBUGGABLE;
    public static final String MANUFACTURER;
    public static final String MODEL;
    public static final String PRODUCT;
    @Deprecated
    public static final String RADIO;
    public static final String SERIAL;
    public static final String[] SUPPORTED_32_BIT_ABIS;
    public static final String[] SUPPORTED_64_BIT_ABIS;
    public static final String[] SUPPORTED_ABIS;
    private static final String TAG = "Build";
    public static final String TAGS;
    public static final long TIME;
    public static final String TYPE;
    public static final String UNKNOWN = "unknown";
    public static final String USER;

    public static class VERSION {
        public static final String[] ACTIVE_CODENAMES;
        private static final String[] ALL_CODENAMES;
        public static final String CODENAME;
        public static final String INCREMENTAL;
        public static final String RELEASE;
        public static final int RESOURCES_SDK_INT;
        @Deprecated
        public static final String SDK;
        public static final int SDK_INT;

        static {
            INCREMENTAL = Build.getString("ro.build.version.incremental");
            RELEASE = Build.getString("ro.build.version.release");
            SDK = Build.getString("ro.build.version.sdk");
            SDK_INT = SystemProperties.getInt("ro.build.version.sdk", 0);
            CODENAME = Build.getString("ro.build.version.codename");
            ALL_CODENAMES = Build.getStringList("ro.build.version.all_codenames", ",");
            ACTIVE_CODENAMES = "REL".equals(ALL_CODENAMES[0]) ? new String[0] : ALL_CODENAMES;
            RESOURCES_SDK_INT = SDK_INT + ACTIVE_CODENAMES.length;
        }
    }

    public static class VERSION_CODES {
        public static final int BASE = 1;
        public static final int BASE_1_1 = 2;
        public static final int CUPCAKE = 3;
        public static final int CUR_DEVELOPMENT = 10000;
        public static final int DONUT = 4;
        public static final int ECLAIR = 5;
        public static final int ECLAIR_0_1 = 6;
        public static final int ECLAIR_MR1 = 7;
        public static final int FROYO = 8;
        public static final int GINGERBREAD = 9;
        public static final int GINGERBREAD_MR1 = 10;
        public static final int HONEYCOMB = 11;
        public static final int HONEYCOMB_MR1 = 12;
        public static final int HONEYCOMB_MR2 = 13;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int JELLY_BEAN_MR2 = 18;
        public static final int KITKAT = 19;
        public static final int KITKAT_WATCH = 20;
        public static final int L = 21;
        public static final int LOLLIPOP = 21;
        public static final int LOLLIPOP_MR1 = 22;
    }

    static {
        String[] abiList;
        boolean z = true;
        ID = getString("ro.build.id");
        DISPLAY = getString("ro.build.display.id");
        PRODUCT = getString("ro.product.name");
        DEVICE = getString("ro.product.device");
        BOARD = getString("ro.product.board");
        MANUFACTURER = getString("ro.product.manufacturer");
        BRAND = getString("ro.product.brand");
        MODEL = getString("ro.product.model");
        BOOTLOADER = getString("ro.bootloader");
        RADIO = getString("gsm.version.baseband");
        HARDWARE = getString("ro.hardware");
        SERIAL = getString("ro.serialno");
        SUPPORTED_ABIS = getStringList("ro.product.cpu.abilist", ",");
        SUPPORTED_32_BIT_ABIS = getStringList("ro.product.cpu.abilist32", ",");
        SUPPORTED_64_BIT_ABIS = getStringList("ro.product.cpu.abilist64", ",");
        if (VMRuntime.getRuntime().is64Bit()) {
            abiList = SUPPORTED_64_BIT_ABIS;
        } else {
            abiList = SUPPORTED_32_BIT_ABIS;
        }
        CPU_ABI = abiList[0];
        if (abiList.length > 1) {
            CPU_ABI2 = abiList[1];
        } else {
            CPU_ABI2 = ProxyInfo.LOCAL_EXCL_LIST;
        }
        TYPE = getString("ro.build.type");
        TAGS = getString("ro.build.tags");
        FINGERPRINT = deriveFingerprint();
        TIME = getLong("ro.build.date.utc") * 1000;
        USER = getString("ro.build.user");
        HOST = getString("ro.build.host");
        if (SystemProperties.getInt("ro.debuggable", 0) != 1) {
            z = IS_DEBUGGABLE;
        }
        IS_DEBUGGABLE = z;
    }

    private static String deriveFingerprint() {
        String finger = SystemProperties.get("ro.build.fingerprint");
        if (TextUtils.isEmpty(finger)) {
            return getString("ro.product.brand") + '/' + getString("ro.product.name") + '/' + getString("ro.product.device") + ':' + getString("ro.build.version.release") + '/' + getString("ro.build.id") + '/' + getString("ro.build.version.incremental") + ':' + getString("ro.build.type") + '/' + getString("ro.build.tags");
        }
        return finger;
    }

    public static void ensureFingerprintProperty() {
        if (TextUtils.isEmpty(SystemProperties.get("ro.build.fingerprint"))) {
            try {
                SystemProperties.set("ro.build.fingerprint", FINGERPRINT);
            } catch (IllegalArgumentException e) {
                Slog.e(TAG, "Failed to set fingerprint property", e);
            }
        }
    }

    public static boolean isFingerprintConsistent() {
        String system = SystemProperties.get("ro.build.fingerprint");
        String vendor = SystemProperties.get("ro.vendor.build.fingerprint");
        if (TextUtils.isEmpty(system)) {
            Slog.e(TAG, "Required ro.build.fingerprint is empty!");
            return IS_DEBUGGABLE;
        } else if (TextUtils.isEmpty(vendor) || Objects.equals(system, vendor)) {
            return true;
        } else {
            Slog.e(TAG, "Mismatched fingerprints; system reported " + system + " but vendor reported " + vendor);
            return IS_DEBUGGABLE;
        }
    }

    public static String getRadioVersion() {
        return SystemProperties.get("gsm.version.baseband", null);
    }

    private static String getString(String property) {
        return SystemProperties.get(property, UNKNOWN);
    }

    private static String[] getStringList(String property, String separator) {
        String value = SystemProperties.get(property);
        if (value.isEmpty()) {
            return new String[0];
        }
        return value.split(separator);
    }

    private static long getLong(String property) {
        try {
            return Long.parseLong(SystemProperties.get(property));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
