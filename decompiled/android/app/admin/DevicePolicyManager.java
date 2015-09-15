package android.app.admin;

import android.app.admin.IDevicePolicyManager.Stub;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.security.Credentials;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.org.conscrypt.TrustedCertificateStore;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DevicePolicyManager {
    public static final String ACTION_ADD_DEVICE_ADMIN = "android.app.action.ADD_DEVICE_ADMIN";
    public static final String ACTION_DEVICE_POLICY_MANAGER_STATE_CHANGED = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED";
    public static final String ACTION_PROVISION_MANAGED_PROFILE = "android.app.action.PROVISION_MANAGED_PROFILE";
    public static final String ACTION_SET_NEW_PASSWORD = "android.app.action.SET_NEW_PASSWORD";
    public static final String ACTION_SET_PROFILE_OWNER = "android.app.action.SET_PROFILE_OWNER";
    public static final String ACTION_START_ENCRYPTION = "android.app.action.START_ENCRYPTION";
    public static final int ENCRYPTION_STATUS_ACTIVATING = 2;
    public static final int ENCRYPTION_STATUS_ACTIVE = 3;
    public static final int ENCRYPTION_STATUS_INACTIVE = 1;
    public static final int ENCRYPTION_STATUS_UNSUPPORTED = 0;
    public static final String EXTRA_ADD_EXPLANATION = "android.app.extra.ADD_EXPLANATION";
    public static final String EXTRA_DEVICE_ADMIN = "android.app.extra.DEVICE_ADMIN";
    public static final String EXTRA_PROFILE_OWNER_NAME = "android.app.extra.PROFILE_OWNER_NAME";
    public static final String EXTRA_PROVISIONING_ACCOUNT_TO_MIGRATE = "android.app.extra.PROVISIONING_ACCOUNT_TO_MIGRATE";
    public static final String EXTRA_PROVISIONING_ADMIN_EXTRAS_BUNDLE = "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_COOKIE_HEADER";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION";
    public static final String EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME = "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME";
    public static final String EXTRA_PROVISIONING_EMAIL_ADDRESS = "android.app.extra.PROVISIONING_EMAIL_ADDRESS";
    public static final String EXTRA_PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED = "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED";
    public static final String EXTRA_PROVISIONING_LOCALE = "android.app.extra.PROVISIONING_LOCALE";
    public static final String EXTRA_PROVISIONING_LOCAL_TIME = "android.app.extra.PROVISIONING_LOCAL_TIME";
    public static final String EXTRA_PROVISIONING_TIME_ZONE = "android.app.extra.PROVISIONING_TIME_ZONE";
    public static final String EXTRA_PROVISIONING_WIFI_HIDDEN = "android.app.extra.PROVISIONING_WIFI_HIDDEN";
    public static final String EXTRA_PROVISIONING_WIFI_PAC_URL = "android.app.extra.PROVISIONING_WIFI_PAC_URL";
    public static final String EXTRA_PROVISIONING_WIFI_PASSWORD = "android.app.extra.PROVISIONING_WIFI_PASSWORD";
    public static final String EXTRA_PROVISIONING_WIFI_PROXY_BYPASS = "android.app.extra.PROVISIONING_WIFI_PROXY_BYPASS";
    public static final String EXTRA_PROVISIONING_WIFI_PROXY_HOST = "android.app.extra.PROVISIONING_WIFI_PROXY_HOST";
    public static final String EXTRA_PROVISIONING_WIFI_PROXY_PORT = "android.app.extra.PROVISIONING_WIFI_PROXY_PORT";
    public static final String EXTRA_PROVISIONING_WIFI_SECURITY_TYPE = "android.app.extra.PROVISIONING_WIFI_SECURITY_TYPE";
    public static final String EXTRA_PROVISIONING_WIFI_SSID = "android.app.extra.PROVISIONING_WIFI_SSID";
    public static final int FLAG_MANAGED_CAN_ACCESS_PARENT = 2;
    public static final int FLAG_PARENT_CAN_ACCESS_MANAGED = 1;
    public static final int KEYGUARD_DISABLE_FEATURES_ALL = Integer.MAX_VALUE;
    public static final int KEYGUARD_DISABLE_FEATURES_NONE = 0;
    public static final int KEYGUARD_DISABLE_FINGERPRINT = 32;
    public static final int KEYGUARD_DISABLE_SECURE_CAMERA = 2;
    public static final int KEYGUARD_DISABLE_SECURE_NOTIFICATIONS = 4;
    public static final int KEYGUARD_DISABLE_TRUST_AGENTS = 16;
    public static final int KEYGUARD_DISABLE_UNREDACTED_NOTIFICATIONS = 8;
    public static final int KEYGUARD_DISABLE_WIDGETS_ALL = 1;
    public static final String MIME_TYPE_PROVISIONING_NFC = "application/com.android.managedprovisioning";
    public static final int PASSWORD_QUALITY_ALPHABETIC = 262144;
    public static final int PASSWORD_QUALITY_ALPHANUMERIC = 327680;
    public static final int PASSWORD_QUALITY_BIOMETRIC_WEAK = 32768;
    public static final int PASSWORD_QUALITY_COMPLEX = 393216;
    public static final int PASSWORD_QUALITY_NUMERIC = 131072;
    public static final int PASSWORD_QUALITY_NUMERIC_COMPLEX = 196608;
    public static final int PASSWORD_QUALITY_SOMETHING = 65536;
    public static final int PASSWORD_QUALITY_UNSPECIFIED = 0;
    public static final int RESET_PASSWORD_REQUIRE_ENTRY = 1;
    private static String TAG = null;
    public static final int WIPE_EXTERNAL_STORAGE = 1;
    public static final int WIPE_RESET_PROTECTION_DATA = 2;
    private final Context mContext;
    private final IDevicePolicyManager mService;

    static {
        TAG = "DevicePolicyManager";
    }

    private DevicePolicyManager(Context context, Handler handler) {
        this.mContext = context;
        this.mService = Stub.asInterface(ServiceManager.getService(Context.DEVICE_POLICY_SERVICE));
    }

    public static DevicePolicyManager create(Context context, Handler handler) {
        DevicePolicyManager me = new DevicePolicyManager(context, handler);
        return me.mService != null ? me : null;
    }

    public boolean isAdminActive(ComponentName who) {
        return isAdminActiveAsUser(who, UserHandle.myUserId());
    }

    public boolean isAdminActiveAsUser(ComponentName who, int userId) {
        if (this.mService != null) {
            try {
                return this.mService.isAdminActive(who, userId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public boolean isRemovingAdmin(ComponentName who, int userId) {
        if (this.mService != null) {
            try {
                return this.mService.isRemovingAdmin(who, userId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public List<ComponentName> getActiveAdmins() {
        return getActiveAdminsAsUser(UserHandle.myUserId());
    }

    public List<ComponentName> getActiveAdminsAsUser(int userId) {
        if (this.mService != null) {
            try {
                return this.mService.getActiveAdmins(userId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public boolean packageHasActiveAdmins(String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.packageHasActiveAdmins(packageName, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void removeActiveAdmin(ComponentName who) {
        if (this.mService != null) {
            try {
                this.mService.removeActiveAdmin(who, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean hasGrantedPolicy(ComponentName admin, int usesPolicy) {
        if (this.mService != null) {
            try {
                return this.mService.hasGrantedPolicy(admin, usesPolicy, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void setPasswordQuality(ComponentName admin, int quality) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordQuality(admin, quality, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordQuality(ComponentName admin) {
        return getPasswordQuality(admin, UserHandle.myUserId());
    }

    public int getPasswordQuality(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordQuality(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordMinimumLength(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumLength(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLength(ComponentName admin) {
        return getPasswordMinimumLength(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumLength(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumLength(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordMinimumUpperCase(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumUpperCase(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumUpperCase(ComponentName admin) {
        return getPasswordMinimumUpperCase(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumUpperCase(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumUpperCase(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordMinimumLowerCase(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumLowerCase(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLowerCase(ComponentName admin) {
        return getPasswordMinimumLowerCase(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumLowerCase(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumLowerCase(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordMinimumLetters(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumLetters(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumLetters(ComponentName admin) {
        return getPasswordMinimumLetters(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumLetters(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumLetters(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordMinimumNumeric(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumNumeric(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumNumeric(ComponentName admin) {
        return getPasswordMinimumNumeric(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumNumeric(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumNumeric(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordMinimumSymbols(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumSymbols(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumSymbols(ComponentName admin) {
        return getPasswordMinimumSymbols(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumSymbols(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumSymbols(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordMinimumNonLetter(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordMinimumNonLetter(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getPasswordMinimumNonLetter(ComponentName admin) {
        return getPasswordMinimumNonLetter(admin, UserHandle.myUserId());
    }

    public int getPasswordMinimumNonLetter(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordMinimumNonLetter(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setPasswordHistoryLength(ComponentName admin, int length) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordHistoryLength(admin, length, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setPasswordExpirationTimeout(ComponentName admin, long timeout) {
        if (this.mService != null) {
            try {
                this.mService.setPasswordExpirationTimeout(admin, timeout, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public long getPasswordExpirationTimeout(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordExpirationTimeout(admin, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return 0;
    }

    public long getPasswordExpiration(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordExpiration(admin, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return 0;
    }

    public int getPasswordHistoryLength(ComponentName admin) {
        return getPasswordHistoryLength(admin, UserHandle.myUserId());
    }

    public int getPasswordHistoryLength(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getPasswordHistoryLength(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public int getPasswordMaximumLength(int quality) {
        return KEYGUARD_DISABLE_TRUST_AGENTS;
    }

    public boolean isActivePasswordSufficient() {
        if (this.mService != null) {
            try {
                return this.mService.isActivePasswordSufficient(UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public int getCurrentFailedPasswordAttempts() {
        if (this.mService != null) {
            try {
                return this.mService.getCurrentFailedPasswordAttempts(UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return -1;
    }

    public void setMaximumFailedPasswordsForWipe(ComponentName admin, int num) {
        if (this.mService != null) {
            try {
                this.mService.setMaximumFailedPasswordsForWipe(admin, num, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName admin) {
        return getMaximumFailedPasswordsForWipe(admin, UserHandle.myUserId());
    }

    public int getMaximumFailedPasswordsForWipe(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getMaximumFailedPasswordsForWipe(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public int getProfileWithMinimumFailedPasswordsForWipe(int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getProfileWithMinimumFailedPasswordsForWipe(userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return UserHandle.USER_NULL;
    }

    public boolean resetPassword(String password, int flags) {
        if (this.mService != null) {
            try {
                return this.mService.resetPassword(password, flags, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void setMaximumTimeToLock(ComponentName admin, long timeMs) {
        if (this.mService != null) {
            try {
                this.mService.setMaximumTimeToLock(admin, timeMs, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public long getMaximumTimeToLock(ComponentName admin) {
        return getMaximumTimeToLock(admin, UserHandle.myUserId());
    }

    public long getMaximumTimeToLock(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getMaximumTimeToLock(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return 0;
    }

    public void lockNow() {
        if (this.mService != null) {
            try {
                this.mService.lockNow();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void wipeData(int flags) {
        if (this.mService != null) {
            try {
                this.mService.wipeData(flags, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public ComponentName setGlobalProxy(ComponentName admin, Proxy proxySpec, List<String> exclusionList) {
        if (proxySpec == null) {
            throw new NullPointerException();
        }
        if (this.mService != null) {
            try {
                String hostSpec;
                String exclSpec;
                if (proxySpec.equals(Proxy.NO_PROXY)) {
                    hostSpec = null;
                    exclSpec = null;
                } else if (proxySpec.type().equals(Type.HTTP)) {
                    InetSocketAddress sa = (InetSocketAddress) proxySpec.address();
                    String hostName = sa.getHostName();
                    int port = sa.getPort();
                    hostSpec = hostName + ":" + Integer.toString(port);
                    if (exclusionList == null) {
                        exclSpec = ProxyInfo.LOCAL_EXCL_LIST;
                    } else {
                        StringBuilder listBuilder = new StringBuilder();
                        boolean firstDomain = true;
                        for (String exclDomain : exclusionList) {
                            if (firstDomain) {
                                firstDomain = false;
                            } else {
                                listBuilder = listBuilder.append(",");
                            }
                            listBuilder = listBuilder.append(exclDomain.trim());
                        }
                        exclSpec = listBuilder.toString();
                    }
                    if (android.net.Proxy.validate(hostName, Integer.toString(port), exclSpec) != 0) {
                        throw new IllegalArgumentException();
                    }
                } else {
                    throw new IllegalArgumentException();
                }
                return this.mService.setGlobalProxy(admin, hostSpec, exclSpec, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public void setRecommendedGlobalProxy(ComponentName admin, ProxyInfo proxyInfo) {
        if (this.mService != null) {
            try {
                this.mService.setRecommendedGlobalProxy(admin, proxyInfo);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public ComponentName getGlobalProxyAdmin() {
        if (this.mService != null) {
            try {
                return this.mService.getGlobalProxyAdmin(UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public int setStorageEncryption(ComponentName admin, boolean encrypt) {
        if (this.mService != null) {
            try {
                return this.mService.setStorageEncryption(admin, encrypt, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public boolean getStorageEncryption(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getStorageEncryption(admin, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public int getStorageEncryptionStatus() {
        return getStorageEncryptionStatus(UserHandle.myUserId());
    }

    public int getStorageEncryptionStatus(int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getStorageEncryptionStatus(userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public boolean installCaCert(ComponentName admin, byte[] certBuffer) {
        if (this.mService != null) {
            try {
                return this.mService.installCaCert(admin, certBuffer);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void uninstallCaCert(ComponentName admin, byte[] certBuffer) {
        if (this.mService != null) {
            try {
                this.mService.uninstallCaCert(admin, getCaCertAlias(certBuffer));
            } catch (CertificateException e) {
                Log.w(TAG, "Unable to parse certificate", e);
            } catch (RemoteException e2) {
                Log.w(TAG, "Failed talking with device policy service", e2);
            }
        }
    }

    public List<byte[]> getInstalledCaCerts(ComponentName admin) {
        List<byte[]> certs = new ArrayList();
        if (this.mService != null) {
            try {
                this.mService.enforceCanManageCaCerts(admin);
                TrustedCertificateStore certStore = new TrustedCertificateStore();
                for (String alias : certStore.userAliases()) {
                    try {
                        certs.add(certStore.getCertificate(alias).getEncoded());
                    } catch (CertificateException ce) {
                        Log.w(TAG, "Could not encode certificate: " + alias, ce);
                    }
                }
            } catch (RemoteException re) {
                Log.w(TAG, "Failed talking with device policy service", re);
            }
        }
        return certs;
    }

    public void uninstallAllUserCaCerts(ComponentName admin) {
        if (this.mService != null) {
            for (String alias : new TrustedCertificateStore().userAliases()) {
                try {
                    this.mService.uninstallCaCert(admin, alias);
                } catch (RemoteException re) {
                    Log.w(TAG, "Failed talking with device policy service", re);
                }
            }
        }
    }

    public boolean hasCaCertInstalled(ComponentName admin, byte[] certBuffer) {
        if (this.mService == null) {
            return false;
        }
        try {
            this.mService.enforceCanManageCaCerts(admin);
            if (getCaCertAlias(certBuffer) != null) {
                return true;
            }
            return false;
        } catch (RemoteException re) {
            Log.w(TAG, "Failed talking with device policy service", re);
            return false;
        } catch (CertificateException ce) {
            Log.w(TAG, "Could not parse certificate", ce);
            return false;
        }
    }

    public boolean installKeyPair(ComponentName who, PrivateKey privKey, Certificate cert, String alias) {
        boolean z = false;
        try {
            Certificate[] certificateArr = new Certificate[WIPE_EXTERNAL_STORAGE];
            certificateArr[PASSWORD_QUALITY_UNSPECIFIED] = cert;
            z = this.mService.installKeyPair(who, privKey.getEncoded(), Credentials.convertToPem(certificateArr), alias);
        } catch (CertificateException e) {
            Log.w(TAG, "Error encoding certificate", e);
        } catch (IOException e2) {
            Log.w(TAG, "Error writing certificate", e2);
        } catch (RemoteException e3) {
            Log.w(TAG, "Failed talking with device policy service", e3);
        }
        return z;
    }

    private static String getCaCertAlias(byte[] certBuffer) throws CertificateException {
        return new TrustedCertificateStore().getCertificateAlias((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certBuffer)));
    }

    public void setCameraDisabled(ComponentName admin, boolean disabled) {
        if (this.mService != null) {
            try {
                this.mService.setCameraDisabled(admin, disabled, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean getCameraDisabled(ComponentName admin) {
        return getCameraDisabled(admin, UserHandle.myUserId());
    }

    public boolean getCameraDisabled(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getCameraDisabled(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void setScreenCaptureDisabled(ComponentName admin, boolean disabled) {
        if (this.mService != null) {
            try {
                this.mService.setScreenCaptureDisabled(admin, UserHandle.myUserId(), disabled);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean getScreenCaptureDisabled(ComponentName admin) {
        return getScreenCaptureDisabled(admin, UserHandle.myUserId());
    }

    public boolean getScreenCaptureDisabled(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getScreenCaptureDisabled(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void setAutoTimeRequired(ComponentName admin, boolean required) {
        if (this.mService != null) {
            try {
                this.mService.setAutoTimeRequired(admin, UserHandle.myUserId(), required);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean getAutoTimeRequired() {
        if (this.mService != null) {
            try {
                return this.mService.getAutoTimeRequired();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void setKeyguardDisabledFeatures(ComponentName admin, int which) {
        if (this.mService != null) {
            try {
                this.mService.setKeyguardDisabledFeatures(admin, which, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public int getKeyguardDisabledFeatures(ComponentName admin) {
        return getKeyguardDisabledFeatures(admin, UserHandle.myUserId());
    }

    public int getKeyguardDisabledFeatures(ComponentName admin, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getKeyguardDisabledFeatures(admin, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing, int userHandle) {
        if (this.mService != null) {
            try {
                this.mService.setActiveAdmin(policyReceiver, refreshing, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setActiveAdmin(ComponentName policyReceiver, boolean refreshing) {
        setActiveAdmin(policyReceiver, refreshing, UserHandle.myUserId());
    }

    public DeviceAdminInfo getAdminInfo(ComponentName cn) {
        try {
            ActivityInfo ai = this.mContext.getPackageManager().getReceiverInfo(cn, AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
            ResolveInfo ri = new ResolveInfo();
            ri.activityInfo = ai;
            try {
                return new DeviceAdminInfo(this.mContext, ri);
            } catch (XmlPullParserException e) {
                Log.w(TAG, "Unable to parse device policy " + cn, e);
                return null;
            } catch (IOException e2) {
                Log.w(TAG, "Unable to parse device policy " + cn, e2);
                return null;
            }
        } catch (NameNotFoundException e3) {
            Log.w(TAG, "Unable to retrieve device policy " + cn, e3);
            return null;
        }
    }

    public void getRemoveWarning(ComponentName admin, RemoteCallback result) {
        if (this.mService != null) {
            try {
                this.mService.getRemoveWarning(admin, result, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setActivePasswordState(int quality, int length, int letters, int uppercase, int lowercase, int numbers, int symbols, int nonletter, int userHandle) {
        if (this.mService != null) {
            try {
                this.mService.setActivePasswordState(quality, length, letters, uppercase, lowercase, numbers, symbols, nonletter, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void reportFailedPasswordAttempt(int userHandle) {
        if (this.mService != null) {
            try {
                this.mService.reportFailedPasswordAttempt(userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void reportSuccessfulPasswordAttempt(int userHandle) {
        if (this.mService != null) {
            try {
                this.mService.reportSuccessfulPasswordAttempt(userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean setDeviceOwner(String packageName) throws IllegalArgumentException, IllegalStateException {
        return setDeviceOwner(packageName, null);
    }

    public boolean setDeviceOwner(String packageName, String ownerName) throws IllegalArgumentException, IllegalStateException {
        if (this.mService != null) {
            try {
                return this.mService.setDeviceOwner(packageName, ownerName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to set device owner");
            }
        }
        return false;
    }

    public boolean isDeviceOwnerApp(String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.isDeviceOwner(packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to check device owner");
            }
        }
        return false;
    }

    public boolean isDeviceOwner(String packageName) {
        return isDeviceOwnerApp(packageName);
    }

    public void clearDeviceOwnerApp(String packageName) {
        if (this.mService != null) {
            try {
                this.mService.clearDeviceOwner(packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to clear device owner");
            }
        }
    }

    public String getDeviceOwner() {
        if (this.mService != null) {
            try {
                return this.mService.getDeviceOwner();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to get device owner");
            }
        }
        return null;
    }

    public String getDeviceOwnerName() {
        if (this.mService != null) {
            try {
                return this.mService.getDeviceOwnerName();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to get device owner");
            }
        }
        return null;
    }

    public boolean setActiveProfileOwner(ComponentName admin, String ownerName) throws IllegalArgumentException {
        boolean z = false;
        if (this.mService != null) {
            try {
                int myUserId = UserHandle.myUserId();
                this.mService.setActiveAdmin(admin, false, myUserId);
                z = this.mService.setProfileOwner(admin, ownerName, myUserId);
            } catch (RemoteException re) {
                Log.w(TAG, "Failed to set profile owner " + re);
                throw new IllegalArgumentException("Couldn't set profile owner.", re);
            }
        }
        return z;
    }

    public void clearProfileOwner(ComponentName admin) {
        if (this.mService != null) {
            try {
                this.mService.clearProfileOwner(admin);
            } catch (RemoteException re) {
                Log.w(TAG, "Failed to clear profile owner " + admin + re);
            }
        }
    }

    public boolean hasUserSetupCompleted() {
        if (this.mService != null) {
            try {
                return this.mService.hasUserSetupCompleted();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to check if user setup has completed");
            }
        }
        return true;
    }

    public boolean setProfileOwner(String packageName, String ownerName, int userHandle) throws IllegalArgumentException {
        if (packageName != null) {
            return setProfileOwner(new ComponentName(packageName, ProxyInfo.LOCAL_EXCL_LIST), ownerName, userHandle);
        }
        throw new NullPointerException("packageName cannot be null");
    }

    public boolean setProfileOwner(ComponentName admin, String ownerName, int userHandle) throws IllegalArgumentException {
        if (admin == null) {
            throw new NullPointerException("admin cannot be null");
        } else if (this.mService == null) {
            return false;
        } else {
            if (ownerName == null) {
                try {
                    ownerName = ProxyInfo.LOCAL_EXCL_LIST;
                } catch (RemoteException re) {
                    Log.w(TAG, "Failed to set profile owner", re);
                    throw new IllegalArgumentException("Couldn't set profile owner.", re);
                }
            }
            return this.mService.setProfileOwner(admin, ownerName, userHandle);
        }
    }

    public void setProfileEnabled(ComponentName admin) {
        if (this.mService != null) {
            try {
                this.mService.setProfileEnabled(admin);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setProfileName(ComponentName who, String profileName) {
        if (this.mService != null) {
            try {
                this.mService.setProfileName(who, profileName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean isProfileOwnerApp(String packageName) {
        if (this.mService == null) {
            return false;
        }
        try {
            ComponentName profileOwner = this.mService.getProfileOwner(Process.myUserHandle().getIdentifier());
            if (profileOwner == null || !profileOwner.getPackageName().equals(packageName)) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to check profile owner");
            return false;
        }
    }

    public ComponentName getProfileOwner() throws IllegalArgumentException {
        return getProfileOwnerAsUser(Process.myUserHandle().getIdentifier());
    }

    public ComponentName getProfileOwnerAsUser(int userId) throws IllegalArgumentException {
        if (this.mService == null) {
            return null;
        }
        try {
            return this.mService.getProfileOwner(userId);
        } catch (RemoteException re) {
            Log.w(TAG, "Failed to get profile owner");
            throw new IllegalArgumentException("Requested profile owner for invalid userId", re);
        }
    }

    public String getProfileOwnerName() throws IllegalArgumentException {
        if (this.mService == null) {
            return null;
        }
        try {
            return this.mService.getProfileOwnerName(Process.myUserHandle().getIdentifier());
        } catch (RemoteException re) {
            Log.w(TAG, "Failed to get profile owner");
            throw new IllegalArgumentException("Requested profile owner for invalid userId", re);
        }
    }

    public String getProfileOwnerNameAsUser(int userId) throws IllegalArgumentException {
        if (this.mService == null) {
            return null;
        }
        try {
            return this.mService.getProfileOwnerName(userId);
        } catch (RemoteException re) {
            Log.w(TAG, "Failed to get profile owner");
            throw new IllegalArgumentException("Requested profile owner for invalid userId", re);
        }
    }

    public void addPersistentPreferredActivity(ComponentName admin, IntentFilter filter, ComponentName activity) {
        if (this.mService != null) {
            try {
                this.mService.addPersistentPreferredActivity(admin, filter, activity);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void clearPackagePersistentPreferredActivities(ComponentName admin, String packageName) {
        if (this.mService != null) {
            try {
                this.mService.clearPackagePersistentPreferredActivities(admin, packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setApplicationRestrictions(ComponentName admin, String packageName, Bundle settings) {
        if (this.mService != null) {
            try {
                this.mService.setApplicationRestrictions(admin, packageName, settings);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setTrustAgentConfiguration(ComponentName admin, ComponentName target, PersistableBundle configuration) {
        if (this.mService != null) {
            try {
                this.mService.setTrustAgentConfiguration(admin, target, configuration, UserHandle.myUserId());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public List<PersistableBundle> getTrustAgentConfiguration(ComponentName admin, ComponentName agent) {
        return getTrustAgentConfiguration(admin, agent, UserHandle.myUserId());
    }

    public List<PersistableBundle> getTrustAgentConfiguration(ComponentName admin, ComponentName agent, int userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getTrustAgentConfiguration(admin, agent, userHandle);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return new ArrayList();
    }

    public void setCrossProfileCallerIdDisabled(ComponentName who, boolean disabled) {
        if (this.mService != null) {
            try {
                this.mService.setCrossProfileCallerIdDisabled(who, disabled);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean getCrossProfileCallerIdDisabled(ComponentName who) {
        if (this.mService != null) {
            try {
                return this.mService.getCrossProfileCallerIdDisabled(who);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public boolean getCrossProfileCallerIdDisabled(UserHandle userHandle) {
        if (this.mService != null) {
            try {
                return this.mService.getCrossProfileCallerIdDisabledForUser(userHandle.getIdentifier());
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void addCrossProfileIntentFilter(ComponentName admin, IntentFilter filter, int flags) {
        if (this.mService != null) {
            try {
                this.mService.addCrossProfileIntentFilter(admin, filter, flags);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void clearCrossProfileIntentFilters(ComponentName admin) {
        if (this.mService != null) {
            try {
                this.mService.clearCrossProfileIntentFilters(admin);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean setPermittedAccessibilityServices(ComponentName admin, List<String> packageNames) {
        if (this.mService != null) {
            try {
                return this.mService.setPermittedAccessibilityServices(admin, packageNames);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public List<String> getPermittedAccessibilityServices(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getPermittedAccessibilityServices(admin);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public List<String> getPermittedAccessibilityServices(int userId) {
        if (this.mService != null) {
            try {
                return this.mService.getPermittedAccessibilityServicesForUser(userId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public boolean setPermittedInputMethods(ComponentName admin, List<String> packageNames) {
        if (this.mService != null) {
            try {
                return this.mService.setPermittedInputMethods(admin, packageNames);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public List<String> getPermittedInputMethods(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getPermittedInputMethods(admin);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public List<String> getPermittedInputMethodsForCurrentUser() {
        if (this.mService != null) {
            try {
                return this.mService.getPermittedInputMethodsForCurrentUser();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public UserHandle createUser(ComponentName admin, String name) {
        try {
            return this.mService.createUser(admin, name);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not create a user", re);
            return null;
        }
    }

    public UserHandle createAndInitializeUser(ComponentName admin, String name, String ownerName, ComponentName profileOwnerComponent, Bundle adminExtras) {
        try {
            return this.mService.createAndInitializeUser(admin, name, ownerName, profileOwnerComponent, adminExtras);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not create a user", re);
            return null;
        }
    }

    public boolean removeUser(ComponentName admin, UserHandle userHandle) {
        try {
            return this.mService.removeUser(admin, userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not remove user ", re);
            return false;
        }
    }

    public boolean switchUser(ComponentName admin, UserHandle userHandle) {
        try {
            return this.mService.switchUser(admin, userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not switch user ", re);
            return false;
        }
    }

    public Bundle getApplicationRestrictions(ComponentName admin, String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.getApplicationRestrictions(admin, packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public void addUserRestriction(ComponentName admin, String key) {
        if (this.mService != null) {
            try {
                this.mService.setUserRestriction(admin, key, true);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void clearUserRestriction(ComponentName admin, String key) {
        if (this.mService != null) {
            try {
                this.mService.setUserRestriction(admin, key, false);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public boolean setApplicationHidden(ComponentName admin, String packageName, boolean hidden) {
        if (this.mService != null) {
            try {
                return this.mService.setApplicationHidden(admin, packageName, hidden);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public boolean isApplicationHidden(ComponentName admin, String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.isApplicationHidden(admin, packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void enableSystemApp(ComponentName admin, String packageName) {
        if (this.mService != null) {
            try {
                this.mService.enableSystemApp(admin, packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to install package: " + packageName);
            }
        }
    }

    public int enableSystemApp(ComponentName admin, Intent intent) {
        if (this.mService != null) {
            try {
                return this.mService.enableSystemAppWithIntent(admin, intent);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to install packages matching filter: " + intent);
            }
        }
        return PASSWORD_QUALITY_UNSPECIFIED;
    }

    public void setAccountManagementDisabled(ComponentName admin, String accountType, boolean disabled) {
        if (this.mService != null) {
            try {
                this.mService.setAccountManagementDisabled(admin, accountType, disabled);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public String[] getAccountTypesWithManagementDisabled() {
        return getAccountTypesWithManagementDisabledAsUser(UserHandle.myUserId());
    }

    public String[] getAccountTypesWithManagementDisabledAsUser(int userId) {
        if (this.mService != null) {
            try {
                return this.mService.getAccountTypesWithManagementDisabledAsUser(userId);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public void setLockTaskPackages(ComponentName admin, String[] packages) throws SecurityException {
        if (this.mService != null) {
            try {
                this.mService.setLockTaskPackages(admin, packages);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public String[] getLockTaskPackages(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.getLockTaskPackages(admin);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return null;
    }

    public boolean isLockTaskPermitted(String pkg) {
        if (this.mService != null) {
            try {
                return this.mService.isLockTaskPermitted(pkg);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
        return false;
    }

    public void setGlobalSetting(ComponentName admin, String setting, String value) {
        if (this.mService != null) {
            try {
                this.mService.setGlobalSetting(admin, setting, value);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setSecureSetting(ComponentName admin, String setting, String value) {
        if (this.mService != null) {
            try {
                this.mService.setSecureSetting(admin, setting, value);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed talking with device policy service", e);
            }
        }
    }

    public void setRestrictionsProvider(ComponentName admin, ComponentName provider) {
        if (this.mService != null) {
            try {
                this.mService.setRestrictionsProvider(admin, provider);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to set permission provider on device policy service");
            }
        }
    }

    public void setMasterVolumeMuted(ComponentName admin, boolean on) {
        if (this.mService != null) {
            try {
                this.mService.setMasterVolumeMuted(admin, on);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to setMasterMute on device policy service");
            }
        }
    }

    public boolean isMasterVolumeMuted(ComponentName admin) {
        if (this.mService != null) {
            try {
                return this.mService.isMasterVolumeMuted(admin);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to get isMasterMute on device policy service");
            }
        }
        return false;
    }

    public void setUninstallBlocked(ComponentName admin, String packageName, boolean uninstallBlocked) {
        if (this.mService != null) {
            try {
                this.mService.setUninstallBlocked(admin, packageName, uninstallBlocked);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to call block uninstall on device policy service");
            }
        }
    }

    public boolean isUninstallBlocked(ComponentName admin, String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.isUninstallBlocked(admin, packageName);
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to call block uninstall on device policy service");
            }
        }
        return false;
    }

    public boolean addCrossProfileWidgetProvider(ComponentName admin, String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.addCrossProfileWidgetProvider(admin, packageName);
            } catch (RemoteException re) {
                Log.w(TAG, "Error calling addCrossProfileWidgetProvider", re);
            }
        }
        return false;
    }

    public boolean removeCrossProfileWidgetProvider(ComponentName admin, String packageName) {
        if (this.mService != null) {
            try {
                return this.mService.removeCrossProfileWidgetProvider(admin, packageName);
            } catch (RemoteException re) {
                Log.w(TAG, "Error calling removeCrossProfileWidgetProvider", re);
            }
        }
        return false;
    }

    public List<String> getCrossProfileWidgetProviders(ComponentName admin) {
        if (this.mService != null) {
            try {
                List<String> providers = this.mService.getCrossProfileWidgetProviders(admin);
                if (providers != null) {
                    return providers;
                }
            } catch (RemoteException re) {
                Log.w(TAG, "Error calling getCrossProfileWidgetProviders", re);
            }
        }
        return Collections.emptyList();
    }
}
