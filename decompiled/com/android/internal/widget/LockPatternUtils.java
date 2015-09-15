package com.android.internal.widget;

import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IMountService;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.IWindowManager;
import android.widget.Button;
import com.android.internal.R;
import com.android.internal.util.Protocol;
import com.android.internal.widget.ILockSettings.Stub;
import com.android.internal.widget.LockPatternView.Cell;
import com.google.android.collect.Lists;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.microedition.khronos.opengles.GL10;

public class LockPatternUtils {
    public static final String BIOMETRIC_WEAK_EVER_CHOSEN_KEY = "lockscreen.biometricweakeverchosen";
    private static final boolean DEBUG = false;
    public static final String DISABLE_LOCKSCREEN_KEY = "lockscreen.disabled";
    private static final String ENABLED_TRUST_AGENTS = "lockscreen.enabledtrustagents";
    public static final int FAILED_ATTEMPTS_BEFORE_RESET = 20;
    public static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 5;
    public static final int FAILED_ATTEMPTS_BEFORE_WIPE_GRACE = 5;
    public static final long FAILED_ATTEMPT_COUNTDOWN_INTERVAL_MS = 1000;
    public static final long FAILED_ATTEMPT_TIMEOUT_MS = 30000;
    public static final int FLAG_BIOMETRIC_WEAK_LIVELINESS = 1;
    public static final int ID_DEFAULT_STATUS_WIDGET = -2;
    public static final String KEYGUARD_SHOW_APPWIDGET = "showappwidget";
    public static final String KEYGUARD_SHOW_SECURITY_CHALLENGE = "showsecuritychallenge";
    public static final String KEYGUARD_SHOW_USER_SWITCHER = "showuserswitcher";
    public static final String LOCKOUT_ATTEMPT_DEADLINE = "lockscreen.lockoutattemptdeadline";
    public static final String LOCKOUT_PERMANENT_KEY = "lockscreen.lockedoutpermanently";
    public static final String LOCKSCREEN_BIOMETRIC_WEAK_FALLBACK = "lockscreen.biometric_weak_fallback";
    public static final String LOCKSCREEN_OPTIONS = "lockscreen.options";
    public static final String LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS = "lockscreen.power_button_instantly_locks";
    public static final String LOCKSCREEN_WIDGETS_ENABLED = "lockscreen.widgets_enabled";
    public static final String LOCK_PASSWORD_SALT_KEY = "lockscreen.password_salt";
    private static final String LOCK_SCREEN_OWNER_INFO = "lock_screen_owner_info";
    private static final String LOCK_SCREEN_OWNER_INFO_ENABLED = "lock_screen_owner_info_enabled";
    public static final int MAX_ALLOWED_SEQUENCE = 3;
    public static final int MIN_LOCK_PATTERN_SIZE = 4;
    public static final int MIN_PATTERN_REGISTER_FAIL = 4;
    public static final String PASSWORD_HISTORY_KEY = "lockscreen.passwordhistory";
    public static final String PASSWORD_TYPE_ALTERNATE_KEY = "lockscreen.password_type_alternate";
    public static final String PASSWORD_TYPE_KEY = "lockscreen.password_type";
    public static final String PATTERN_EVER_CHOSEN_KEY = "lockscreen.patterneverchosen";
    private static final String TAG = "LockPatternUtils";
    private static volatile int sCurrentUserId;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private DevicePolicyManager mDevicePolicyManager;
    private ILockSettings mLockSettingsService;
    private final boolean mMultiUserMode;

    /* renamed from: com.android.internal.widget.LockPatternUtils.1 */
    class AnonymousClass1 extends AsyncTask<Void, Void, Void> {
        final /* synthetic */ LockPatternUtils this$0;
        final /* synthetic */ String val$password;
        final /* synthetic */ IBinder val$service;
        final /* synthetic */ int val$type;

        AnonymousClass1(com.android.internal.widget.LockPatternUtils r1, android.os.IBinder r2, int r3, java.lang.String r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.widget.LockPatternUtils.1.<init>(com.android.internal.widget.LockPatternUtils, android.os.IBinder, int, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.widget.LockPatternUtils.1.<init>(com.android.internal.widget.LockPatternUtils, android.os.IBinder, int, java.lang.String):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.widget.LockPatternUtils.1.<init>(com.android.internal.widget.LockPatternUtils, android.os.IBinder, int, java.lang.String):void");
        }

        protected /* bridge */ /* synthetic */ java.lang.Object doInBackground(java.lang.Object[] r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.widget.LockPatternUtils.1.doInBackground(java.lang.Object[]):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.widget.LockPatternUtils.1.doInBackground(java.lang.Object[]):java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.widget.LockPatternUtils.1.doInBackground(java.lang.Object[]):java.lang.Object");
        }

        protected java.lang.Void doInBackground(java.lang.Void... r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.widget.LockPatternUtils.1.doInBackground(java.lang.Void[]):java.lang.Void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.widget.LockPatternUtils.1.doInBackground(java.lang.Void[]):java.lang.Void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.widget.LockPatternUtils.1.doInBackground(java.lang.Void[]):java.lang.Void");
        }
    }

    static {
        sCurrentUserId = -10000;
    }

    public DevicePolicyManager getDevicePolicyManager() {
        if (this.mDevicePolicyManager == null) {
            this.mDevicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
            if (this.mDevicePolicyManager == null) {
                Log.e(TAG, "Can't get DevicePolicyManagerService: is it running?", new IllegalStateException("Stack trace:"));
            }
        }
        return this.mDevicePolicyManager;
    }

    private TrustManager getTrustManager() {
        TrustManager trust = (TrustManager) this.mContext.getSystemService("trust");
        if (trust == null) {
            Log.e(TAG, "Can't get TrustManagerService: is it running?", new IllegalStateException("Stack trace:"));
        }
        return trust;
    }

    public LockPatternUtils(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mMultiUserMode = context.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0 ? true : DEBUG;
    }

    private ILockSettings getLockSettings() {
        if (this.mLockSettingsService == null) {
            this.mLockSettingsService = Stub.asInterface(ServiceManager.getService("lock_settings"));
        }
        return this.mLockSettingsService;
    }

    public int getRequestedMinimumPasswordLength() {
        return getDevicePolicyManager().getPasswordMinimumLength(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordQuality() {
        return getDevicePolicyManager().getPasswordQuality(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordHistoryLength() {
        return getDevicePolicyManager().getPasswordHistoryLength(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordMinimumLetters() {
        return getDevicePolicyManager().getPasswordMinimumLetters(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordMinimumUpperCase() {
        return getDevicePolicyManager().getPasswordMinimumUpperCase(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordMinimumLowerCase() {
        return getDevicePolicyManager().getPasswordMinimumLowerCase(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordMinimumNumeric() {
        return getDevicePolicyManager().getPasswordMinimumNumeric(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordMinimumSymbols() {
        return getDevicePolicyManager().getPasswordMinimumSymbols(null, getCurrentOrCallingUserId());
    }

    public int getRequestedPasswordMinimumNonLetter() {
        return getDevicePolicyManager().getPasswordMinimumNonLetter(null, getCurrentOrCallingUserId());
    }

    public void reportFailedPasswordAttempt() {
        int userId = getCurrentOrCallingUserId();
        getDevicePolicyManager().reportFailedPasswordAttempt(userId);
        getTrustManager().reportUnlockAttempt(DEBUG, userId);
        getTrustManager().reportRequireCredentialEntry(userId);
    }

    public void reportSuccessfulPasswordAttempt() {
        getDevicePolicyManager().reportSuccessfulPasswordAttempt(getCurrentOrCallingUserId());
        getTrustManager().reportUnlockAttempt(true, getCurrentOrCallingUserId());
    }

    public void setCurrentUser(int userId) {
        sCurrentUserId = userId;
    }

    public int getCurrentUser() {
        if (sCurrentUserId != -10000) {
            return sCurrentUserId;
        }
        try {
            return ActivityManagerNative.getDefault().getCurrentUser().id;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public void removeUser(int userId) {
        try {
            getLockSettings().removeUser(userId);
        } catch (RemoteException e) {
            Log.e(TAG, "Couldn't remove lock settings for user " + userId);
        }
    }

    private int getCurrentOrCallingUserId() {
        if (this.mMultiUserMode) {
            return getCurrentUser();
        }
        return UserHandle.getCallingUserId();
    }

    public boolean checkPattern(List<Cell> pattern) {
        try {
            return getLockSettings().checkPattern(patternToString(pattern), getCurrentOrCallingUserId());
        } catch (RemoteException e) {
            return true;
        }
    }

    public boolean checkPassword(String password) {
        try {
            return getLockSettings().checkPassword(password, getCurrentOrCallingUserId());
        } catch (RemoteException e) {
            return true;
        }
    }

    public boolean checkVoldPassword() {
        try {
            return getLockSettings().checkVoldPassword(getCurrentOrCallingUserId());
        } catch (RemoteException e) {
            return DEBUG;
        }
    }

    public boolean checkPasswordHistory(String password) {
        String passwordHashString = new String(passwordToHash(password, getCurrentOrCallingUserId()));
        String passwordHistory = getString(PASSWORD_HISTORY_KEY);
        if (passwordHistory == null) {
            return DEBUG;
        }
        int passwordHashLength = passwordHashString.length();
        int passwordHistoryLength = getRequestedPasswordHistoryLength();
        if (passwordHistoryLength == 0) {
            return DEBUG;
        }
        int neededPasswordHistoryLength = ((passwordHashLength * passwordHistoryLength) + passwordHistoryLength) - 1;
        if (passwordHistory.length() > neededPasswordHistoryLength) {
            passwordHistory = passwordHistory.substring(0, neededPasswordHistoryLength);
        }
        return passwordHistory.contains(passwordHashString);
    }

    public boolean savedPatternExists() {
        return savedPatternExists(getCurrentOrCallingUserId());
    }

    public boolean savedPatternExists(int userId) {
        try {
            return getLockSettings().havePattern(userId);
        } catch (RemoteException e) {
            return DEBUG;
        }
    }

    public boolean savedPasswordExists() {
        return savedPasswordExists(getCurrentOrCallingUserId());
    }

    public boolean savedPasswordExists(int userId) {
        try {
            return getLockSettings().havePassword(userId);
        } catch (RemoteException e) {
            return DEBUG;
        }
    }

    public boolean isPatternEverChosen() {
        return getBoolean(PATTERN_EVER_CHOSEN_KEY, DEBUG);
    }

    public boolean isBiometricWeakEverChosen() {
        return getBoolean(BIOMETRIC_WEAK_EVER_CHOSEN_KEY, DEBUG);
    }

    public int getActivePasswordQuality() {
        switch ((int) getLong(PASSWORD_TYPE_KEY, 65536)) {
            case DateUtils.FORMAT_ABBREV_WEEKDAY /*32768*/:
                if (isBiometricWeakInstalled()) {
                    return DateUtils.FORMAT_ABBREV_WEEKDAY;
                }
                return 0;
            case Protocol.BASE_SYSTEM_RESERVED /*65536*/:
                if (isLockPatternEnabled()) {
                    return Protocol.BASE_SYSTEM_RESERVED;
                }
                return 0;
            case Protocol.BASE_WIFI /*131072*/:
                if (isLockPasswordEnabled()) {
                    return Protocol.BASE_WIFI;
                }
                return 0;
            case Protocol.BASE_DHCP /*196608*/:
                if (isLockPasswordEnabled()) {
                    return Protocol.BASE_DHCP;
                }
                return 0;
            case Protocol.BASE_DATA_CONNECTION /*262144*/:
                if (isLockPasswordEnabled()) {
                    return Protocol.BASE_DATA_CONNECTION;
                }
                return 0;
            case Protocol.BASE_DNS_PINGER /*327680*/:
                if (isLockPasswordEnabled()) {
                    return Protocol.BASE_DNS_PINGER;
                }
                return 0;
            case Protocol.BASE_NSD_MANAGER /*393216*/:
                if (isLockPasswordEnabled()) {
                    return Protocol.BASE_NSD_MANAGER;
                }
                return 0;
            default:
                return 0;
        }
    }

    public void clearLock(boolean isFallback) {
        clearLock(isFallback, getCurrentOrCallingUserId());
    }

    public void clearLock(boolean isFallback, int userHandle) {
        if (!isFallback) {
            deleteGallery(userHandle);
        }
        saveLockPassword(null, Protocol.BASE_SYSTEM_RESERVED, isFallback, userHandle);
        setLockPatternEnabled(DEBUG, userHandle);
        saveLockPattern(null, isFallback, userHandle);
        setLong(PASSWORD_TYPE_KEY, 0, userHandle);
        setLong(PASSWORD_TYPE_ALTERNATE_KEY, 0, userHandle);
        onAfterChangingPassword(userHandle);
    }

    public void setLockScreenDisabled(boolean disable) {
        setLong(DISABLE_LOCKSCREEN_KEY, disable ? 1 : 0);
    }

    public boolean isLockScreenDisabled() {
        if (isSecure() || getLong(DISABLE_LOCKSCREEN_KEY, 0) == 0) {
            return DEBUG;
        }
        List<UserInfo> users = UserManager.get(this.mContext).getUsers(true);
        int userCount = users.size();
        int switchableUsers = 0;
        for (int i = 0; i < userCount; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            if (((UserInfo) users.get(i)).supportsSwitchTo()) {
                switchableUsers += FLAG_BIOMETRIC_WEAK_LIVELINESS;
            }
        }
        if (switchableUsers < 2) {
            return true;
        }
        return DEBUG;
    }

    public void deleteTempGallery() {
        Intent intent = new Intent().setAction("com.android.facelock.DELETE_GALLERY");
        intent.putExtra("deleteTempGallery", true);
        this.mContext.sendBroadcast(intent);
    }

    void deleteGallery(int userId) {
        if (usingBiometricWeak(userId)) {
            Intent intent = new Intent().setAction("com.android.facelock.DELETE_GALLERY");
            intent.putExtra("deleteGallery", true);
            this.mContext.sendBroadcastAsUser(intent, new UserHandle(userId));
        }
    }

    public void saveLockPattern(List<Cell> pattern) {
        saveLockPattern(pattern, DEBUG);
    }

    public void saveLockPattern(List<Cell> pattern, boolean isFallback) {
        saveLockPattern(pattern, isFallback, getCurrentOrCallingUserId());
    }

    public void saveLockPattern(List<Cell> pattern, boolean isFallback, int userId) {
        try {
            getLockSettings().setLockPattern(patternToString(pattern), userId);
            DevicePolicyManager dpm = getDevicePolicyManager();
            if (pattern != null) {
                if (userId == 0 && isDeviceEncryptionEnabled()) {
                    if (isCredentialRequiredToDecrypt(true)) {
                        updateEncryptionPassword(2, patternToString(pattern));
                    } else {
                        clearEncryptionPassword();
                    }
                }
                setBoolean(PATTERN_EVER_CHOSEN_KEY, true, userId);
                if (isFallback) {
                    setLong(PASSWORD_TYPE_KEY, 32768, userId);
                    setLong(PASSWORD_TYPE_ALTERNATE_KEY, 65536, userId);
                    finishBiometricWeak(userId);
                    dpm.setActivePasswordState(DateUtils.FORMAT_ABBREV_WEEKDAY, 0, 0, 0, 0, 0, 0, 0, userId);
                } else {
                    deleteGallery(userId);
                    setLong(PASSWORD_TYPE_KEY, 65536, userId);
                    dpm.setActivePasswordState(Protocol.BASE_SYSTEM_RESERVED, pattern.size(), 0, 0, 0, 0, 0, 0, userId);
                }
            } else {
                dpm.setActivePasswordState(0, 0, 0, 0, 0, 0, 0, 0, userId);
            }
            onAfterChangingPassword(userId);
        } catch (RemoteException re) {
            Log.e(TAG, "Couldn't save lock pattern " + re);
        }
    }

    private void updateCryptoUserInfo() {
        int userId = getCurrentOrCallingUserId();
        if (userId == 0) {
            String ownerInfo = isOwnerInfoEnabled() ? getOwnerInfo(userId) : "";
            IBinder service = ServiceManager.getService("mount");
            if (service == null) {
                Log.e(TAG, "Could not find the mount service to update the user info");
                return;
            }
            IMountService mountService = IMountService.Stub.asInterface(service);
            try {
                Log.d(TAG, "Setting owner info");
                mountService.setField("OwnerInfo", ownerInfo);
            } catch (RemoteException e) {
                Log.e(TAG, "Error changing user info", e);
            }
        }
    }

    public void setOwnerInfo(String info, int userId) {
        setString(LOCK_SCREEN_OWNER_INFO, info, userId);
        updateCryptoUserInfo();
    }

    public void setOwnerInfoEnabled(boolean enabled) {
        setBoolean(LOCK_SCREEN_OWNER_INFO_ENABLED, enabled);
        updateCryptoUserInfo();
    }

    public String getOwnerInfo(int userId) {
        return getString(LOCK_SCREEN_OWNER_INFO);
    }

    public boolean isOwnerInfoEnabled() {
        return getBoolean(LOCK_SCREEN_OWNER_INFO_ENABLED, DEBUG);
    }

    public static int computePasswordQuality(String password) {
        boolean hasDigit = DEBUG;
        boolean hasNonDigit = DEBUG;
        int len = password.length();
        for (int i = 0; i < len; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
            } else {
                hasNonDigit = true;
            }
        }
        if (hasNonDigit && hasDigit) {
            return Protocol.BASE_DNS_PINGER;
        }
        if (hasNonDigit) {
            return Protocol.BASE_DATA_CONNECTION;
        }
        if (hasDigit) {
            return maxLengthSequence(password) > MAX_ALLOWED_SEQUENCE ? Protocol.BASE_WIFI : Protocol.BASE_DHCP;
        } else {
            return 0;
        }
    }

    private static int categoryChar(char c) {
        if (DateFormat.AM_PM <= c && c <= DateFormat.TIME_ZONE) {
            return 0;
        }
        if (DateFormat.CAPITAL_AM_PM <= c && c <= 'Z') {
            return FLAG_BIOMETRIC_WEAK_LIVELINESS;
        }
        if ('0' > c || c > '9') {
            return MAX_ALLOWED_SEQUENCE;
        }
        return 2;
    }

    private static int maxDiffCategory(int category) {
        if (category == 0 || category == FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            return FLAG_BIOMETRIC_WEAK_LIVELINESS;
        }
        if (category == 2) {
            return 10;
        }
        return 0;
    }

    public static int maxLengthSequence(String string) {
        if (string.length() == 0) {
            return 0;
        }
        char previousChar = string.charAt(0);
        int category = categoryChar(previousChar);
        int diff = 0;
        boolean hasDiff = DEBUG;
        int maxLength = 0;
        int startSequence = 0;
        for (int current = FLAG_BIOMETRIC_WEAK_LIVELINESS; current < string.length(); current += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            char currentChar = string.charAt(current);
            int categoryCurrent = categoryChar(currentChar);
            int currentDiff = currentChar - previousChar;
            if (categoryCurrent != category || Math.abs(currentDiff) > maxDiffCategory(category)) {
                maxLength = Math.max(maxLength, current - startSequence);
                startSequence = current;
                hasDiff = DEBUG;
                category = categoryCurrent;
            } else {
                if (hasDiff && currentDiff != diff) {
                    maxLength = Math.max(maxLength, current - startSequence);
                    startSequence = current - 1;
                }
                diff = currentDiff;
                hasDiff = true;
            }
            previousChar = currentChar;
        }
        return Math.max(maxLength, string.length() - startSequence);
    }

    private void updateEncryptionPassword(int type, String password) {
        if (isDeviceEncryptionEnabled()) {
            IBinder service = ServiceManager.getService("mount");
            if (service == null) {
                Log.e(TAG, "Could not find the mount service to update the encryption password");
            } else {
                new AnonymousClass1(this, service, type, password).execute(new Void[0]);
            }
        }
    }

    public void saveLockPassword(String password, int quality) {
        saveLockPassword(password, quality, DEBUG, getCurrentOrCallingUserId());
    }

    public void saveLockPassword(String password, int quality, boolean isFallback) {
        saveLockPassword(password, quality, isFallback, getCurrentOrCallingUserId());
    }

    public void saveLockPassword(String password, int quality, boolean isFallback, int userHandle) {
        try {
            DevicePolicyManager dpm = getDevicePolicyManager();
            if (TextUtils.isEmpty(password)) {
                getLockSettings().setLockPassword(null, userHandle);
                if (userHandle == 0) {
                    updateEncryptionPassword(FLAG_BIOMETRIC_WEAK_LIVELINESS, null);
                }
                dpm.setActivePasswordState(0, 0, 0, 0, 0, 0, 0, 0, userHandle);
            } else {
                getLockSettings().setLockPassword(password, userHandle);
                int computedQuality = computePasswordQuality(password);
                if (userHandle == 0 && isDeviceEncryptionEnabled()) {
                    if (isCredentialRequiredToDecrypt(true)) {
                        int type = ((computedQuality == 131072 ? true : DEBUG) || (computedQuality == 196608 ? true : DEBUG)) ? MAX_ALLOWED_SEQUENCE : 0;
                        updateEncryptionPassword(type, password);
                    } else {
                        clearEncryptionPassword();
                    }
                }
                if (isFallback) {
                    setLong(PASSWORD_TYPE_KEY, 32768, userHandle);
                    setLong(PASSWORD_TYPE_ALTERNATE_KEY, (long) Math.max(quality, computedQuality), userHandle);
                    finishBiometricWeak(userHandle);
                    dpm.setActivePasswordState(DateUtils.FORMAT_ABBREV_WEEKDAY, 0, 0, 0, 0, 0, 0, 0, userHandle);
                } else {
                    deleteGallery(userHandle);
                    setLong(PASSWORD_TYPE_KEY, (long) Math.max(quality, computedQuality), userHandle);
                    if (computedQuality != 0) {
                        int letters = 0;
                        int uppercase = 0;
                        int lowercase = 0;
                        int numbers = 0;
                        int symbols = 0;
                        int nonletter = 0;
                        for (int i = 0; i < password.length(); i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
                            char c = password.charAt(i);
                            if (c >= 'A' && c <= 'Z') {
                                letters += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                                uppercase += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                            } else if (c >= 'a' && c <= 'z') {
                                letters += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                                lowercase += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                            } else if (c < '0' || c > '9') {
                                symbols += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                                nonletter += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                            } else {
                                numbers += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                                nonletter += FLAG_BIOMETRIC_WEAK_LIVELINESS;
                            }
                        }
                        dpm.setActivePasswordState(Math.max(quality, computedQuality), password.length(), letters, uppercase, lowercase, numbers, symbols, nonletter, userHandle);
                    } else {
                        dpm.setActivePasswordState(0, 0, 0, 0, 0, 0, 0, 0, userHandle);
                    }
                }
                String passwordHistory = getString(PASSWORD_HISTORY_KEY, userHandle);
                if (passwordHistory == null) {
                    passwordHistory = "";
                }
                int passwordHistoryLength = getRequestedPasswordHistoryLength();
                if (passwordHistoryLength == 0) {
                    passwordHistory = "";
                } else {
                    byte[] hash = passwordToHash(password, userHandle);
                    passwordHistory = new String(hash) + "," + passwordHistory;
                    passwordHistory = passwordHistory.substring(0, Math.min(((hash.length * passwordHistoryLength) + passwordHistoryLength) - 1, passwordHistory.length()));
                }
                setString(PASSWORD_HISTORY_KEY, passwordHistory, userHandle);
            }
            onAfterChangingPassword(userHandle);
        } catch (RemoteException re) {
            Log.e(TAG, "Unable to save lock password " + re);
        }
    }

    public static boolean isDeviceEncrypted() {
        IMountService mountService = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
        try {
            if (mountService.getEncryptionState() == FLAG_BIOMETRIC_WEAK_LIVELINESS || mountService.getPasswordType() == FLAG_BIOMETRIC_WEAK_LIVELINESS) {
                return DEBUG;
            }
            return true;
        } catch (RemoteException re) {
            Log.e(TAG, "Error getting encryption state", re);
            return true;
        }
    }

    public static boolean isDeviceEncryptionEnabled() {
        return "encrypted".equalsIgnoreCase(SystemProperties.get("ro.crypto.state", "unsupported"));
    }

    public void clearEncryptionPassword() {
        updateEncryptionPassword(FLAG_BIOMETRIC_WEAK_LIVELINESS, null);
    }

    public int getKeyguardStoredPasswordQuality() {
        return getKeyguardStoredPasswordQuality(getCurrentOrCallingUserId());
    }

    public int getKeyguardStoredPasswordQuality(int userHandle) {
        int quality = (int) getLong(PASSWORD_TYPE_KEY, 0, userHandle);
        if (quality == DateUtils.FORMAT_ABBREV_WEEKDAY) {
            return (int) getLong(PASSWORD_TYPE_ALTERNATE_KEY, 0, userHandle);
        }
        return quality;
    }

    public boolean usingBiometricWeak() {
        return usingBiometricWeak(getCurrentOrCallingUserId());
    }

    public boolean usingBiometricWeak(int userId) {
        return ((int) getLong(PASSWORD_TYPE_KEY, 0, userId)) == DateUtils.FORMAT_ABBREV_WEEKDAY ? true : DEBUG;
    }

    public static List<Cell> stringToPattern(String string) {
        List<Cell> result = Lists.newArrayList();
        byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            byte b = bytes[i];
            result.add(Cell.of(b / MAX_ALLOWED_SEQUENCE, b % MAX_ALLOWED_SEQUENCE));
        }
        return result;
    }

    public static String patternToString(List<Cell> pattern) {
        if (pattern == null) {
            return "";
        }
        int patternSize = pattern.size();
        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            Cell cell = (Cell) pattern.get(i);
            res[i] = (byte) ((cell.getRow() * MAX_ALLOWED_SEQUENCE) + cell.getColumn());
        }
        return new String(res);
    }

    public static byte[] patternToHash(List<Cell> pattern) {
        if (pattern == null) {
            return null;
        }
        int patternSize = pattern.size();
        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            Cell cell = (Cell) pattern.get(i);
            res[i] = (byte) ((cell.getRow() * MAX_ALLOWED_SEQUENCE) + cell.getColumn());
        }
        try {
            return MessageDigest.getInstance("SHA-1").digest(res);
        } catch (NoSuchAlgorithmException e) {
            return res;
        }
    }

    private String getSalt(int userId) {
        long salt = getLong(LOCK_PASSWORD_SALT_KEY, 0, userId);
        if (salt == 0) {
            try {
                salt = SecureRandom.getInstance("SHA1PRNG").nextLong();
                setLong(LOCK_PASSWORD_SALT_KEY, salt, userId);
                Log.v(TAG, "Initialized lock password salt for user: " + userId);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Couldn't get SecureRandom number", e);
            }
        }
        return Long.toHexString(salt);
    }

    public byte[] passwordToHash(String password, int userId) {
        if (password == null) {
            return null;
        }
        String algo = null;
        byte[] hashed = null;
        try {
            byte[] saltedPassword = (password + getSalt(userId)).getBytes();
            algo = "MD5";
            return (toHex(MessageDigest.getInstance("SHA-1").digest(saltedPassword)) + toHex(MessageDigest.getInstance(algo).digest(saltedPassword))).getBytes();
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Failed to encode string because of missing algorithm: " + algo);
            return hashed;
        }
    }

    private static String toHex(byte[] ary) {
        String hex = "0123456789ABCDEF";
        String ret = "";
        for (int i = 0; i < ary.length; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            ret = (ret + "0123456789ABCDEF".charAt((ary[i] >> MIN_PATTERN_REGISTER_FAIL) & 15)) + "0123456789ABCDEF".charAt(ary[i] & 15);
        }
        return ret;
    }

    public boolean isLockPasswordEnabled() {
        long mode = getLong(PASSWORD_TYPE_KEY, 0);
        long backupMode = getLong(PASSWORD_TYPE_ALTERNATE_KEY, 0);
        boolean passwordEnabled = (mode == 262144 || mode == 131072 || mode == 196608 || mode == 327680 || mode == 393216) ? true : DEBUG;
        boolean backupEnabled = (backupMode == 262144 || backupMode == 131072 || backupMode == 196608 || backupMode == 327680 || backupMode == 393216) ? true : DEBUG;
        if (savedPasswordExists() && (passwordEnabled || (usingBiometricWeak() && backupEnabled))) {
            return true;
        }
        return DEBUG;
    }

    public boolean isLockPatternEnabled() {
        return isLockPatternEnabled(getCurrentOrCallingUserId());
    }

    public boolean isLockPatternEnabled(int userId) {
        boolean backupEnabled;
        if (getLong(PASSWORD_TYPE_ALTERNATE_KEY, 0, userId) == 65536) {
            backupEnabled = true;
        } else {
            backupEnabled = DEBUG;
        }
        if (getBoolean("lock_pattern_autolock", DEBUG, userId)) {
            if (getLong(PASSWORD_TYPE_KEY, 0, userId) == 65536) {
                return true;
            }
            if (usingBiometricWeak(userId) && backupEnabled) {
                return true;
            }
        }
        return DEBUG;
    }

    public boolean isBiometricWeakInstalled() {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            pm.getPackageInfo("com.android.facelock", FLAG_BIOMETRIC_WEAK_LIVELINESS);
            if (pm.hasSystemFeature("android.hardware.camera.front") && getDevicePolicyManager().getCameraDisabled(null, getCurrentOrCallingUserId())) {
            }
        } catch (NameNotFoundException e) {
        }
        return DEBUG;
    }

    public void setBiometricWeakLivelinessEnabled(boolean enabled) {
        long newFlag;
        long currentFlag = getLong("lock_biometric_weak_flags", 0);
        if (enabled) {
            newFlag = currentFlag | 1;
        } else {
            newFlag = currentFlag & -2;
        }
        setLong("lock_biometric_weak_flags", newFlag);
    }

    public boolean isBiometricWeakLivelinessEnabled() {
        return (1 & getLong("lock_biometric_weak_flags", 0)) != 0 ? true : DEBUG;
    }

    public void setLockPatternEnabled(boolean enabled) {
        setLockPatternEnabled(enabled, getCurrentOrCallingUserId());
    }

    public void setLockPatternEnabled(boolean enabled, int userHandle) {
        setBoolean("lock_pattern_autolock", enabled, userHandle);
    }

    public boolean isVisiblePatternEnabled() {
        return getBoolean("lock_pattern_visible_pattern", DEBUG);
    }

    public void setVisiblePatternEnabled(boolean enabled) {
        setBoolean("lock_pattern_visible_pattern", enabled);
        if (getCurrentOrCallingUserId() == 0) {
            IBinder service = ServiceManager.getService("mount");
            if (service == null) {
                Log.e(TAG, "Could not find the mount service to update the user info");
                return;
            }
            try {
                IMountService.Stub.asInterface(service).setField("PatternVisible", enabled ? "1" : "0");
            } catch (RemoteException e) {
                Log.e(TAG, "Error changing pattern visible state", e);
            }
        }
    }

    public boolean isTactileFeedbackEnabled() {
        return System.getIntForUser(this.mContentResolver, "haptic_feedback_enabled", FLAG_BIOMETRIC_WEAK_LIVELINESS, ID_DEFAULT_STATUS_WIDGET) != 0 ? true : DEBUG;
    }

    public long setLockoutAttemptDeadline() {
        long deadline = SystemClock.elapsedRealtime() + FAILED_ATTEMPT_TIMEOUT_MS;
        setLong(LOCKOUT_ATTEMPT_DEADLINE, deadline);
        return deadline;
    }

    public long getLockoutAttemptDeadline() {
        long deadline = getLong(LOCKOUT_ATTEMPT_DEADLINE, 0);
        long now = SystemClock.elapsedRealtime();
        if (deadline < now || deadline > FAILED_ATTEMPT_TIMEOUT_MS + now) {
            return 0;
        }
        return deadline;
    }

    public boolean isPermanentlyLocked() {
        return getBoolean(LOCKOUT_PERMANENT_KEY, DEBUG);
    }

    public void setPermanentlyLocked(boolean locked) {
        setBoolean(LOCKOUT_PERMANENT_KEY, locked);
    }

    public boolean isEmergencyCallCapable() {
        return this.mContext.getResources().getBoolean(R.bool.config_voice_capable);
    }

    public boolean isPukUnlockScreenEnable() {
        return this.mContext.getResources().getBoolean(R.bool.config_enable_puk_unlock_screen);
    }

    public boolean isEmergencyCallEnabledWhileSimLocked() {
        return this.mContext.getResources().getBoolean(R.bool.config_enable_emergency_call_while_sim_locked);
    }

    public AlarmClockInfo getNextAlarm() {
        return ((AlarmManager) this.mContext.getSystemService("alarm")).getNextAlarmClock(ID_DEFAULT_STATUS_WIDGET);
    }

    private boolean getBoolean(String secureSettingKey, boolean defaultValue, int userId) {
        try {
            defaultValue = getLockSettings().getBoolean(secureSettingKey, defaultValue, userId);
        } catch (RemoteException e) {
        }
        return defaultValue;
    }

    private boolean getBoolean(String secureSettingKey, boolean defaultValue) {
        return getBoolean(secureSettingKey, defaultValue, getCurrentOrCallingUserId());
    }

    private void setBoolean(String secureSettingKey, boolean enabled, int userId) {
        try {
            getLockSettings().setBoolean(secureSettingKey, enabled, userId);
        } catch (RemoteException re) {
            Log.e(TAG, "Couldn't write boolean " + secureSettingKey + re);
        }
    }

    private void setBoolean(String secureSettingKey, boolean enabled) {
        setBoolean(secureSettingKey, enabled, getCurrentOrCallingUserId());
    }

    public int[] getAppWidgets() {
        return getAppWidgets(ID_DEFAULT_STATUS_WIDGET);
    }

    private int[] getAppWidgets(int userId) {
        String appWidgetIdString = Secure.getStringForUser(this.mContentResolver, "lock_screen_appwidget_ids", userId);
        String delims = ",";
        if (appWidgetIdString == null || appWidgetIdString.length() <= 0) {
            return new int[0];
        }
        String[] appWidgetStringIds = appWidgetIdString.split(delims);
        int[] appWidgetIds = new int[appWidgetStringIds.length];
        int i = 0;
        while (i < appWidgetStringIds.length) {
            String appWidget = appWidgetStringIds[i];
            try {
                appWidgetIds[i] = Integer.decode(appWidget).intValue();
                i += FLAG_BIOMETRIC_WEAK_LIVELINESS;
            } catch (NumberFormatException e) {
                Log.d(TAG, "Error when parsing widget id " + appWidget);
                return null;
            }
        }
        return appWidgetIds;
    }

    private static String combineStrings(int[] list, String separator) {
        int listLength = list.length;
        switch (listLength) {
            case GL10.GL_POINTS /*0*/:
                return "";
            case FLAG_BIOMETRIC_WEAK_LIVELINESS /*1*/:
                return Integer.toString(list[0]);
            default:
                int i;
                int strLength = 0;
                int separatorLength = separator.length();
                String[] stringList = new String[list.length];
                for (i = 0; i < listLength; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
                    stringList[i] = Integer.toString(list[i]);
                    strLength += stringList[i].length();
                    if (i < listLength - 1) {
                        strLength += separatorLength;
                    }
                }
                StringBuilder sb = new StringBuilder(strLength);
                for (i = 0; i < listLength; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
                    sb.append(list[i]);
                    if (i < listLength - 1) {
                        sb.append(separator);
                    }
                }
                return sb.toString();
        }
    }

    public void writeFallbackAppWidgetId(int appWidgetId) {
        Secure.putIntForUser(this.mContentResolver, "lock_screen_fallback_appwidget_id", appWidgetId, ID_DEFAULT_STATUS_WIDGET);
    }

    public int getFallbackAppWidgetId() {
        return Secure.getIntForUser(this.mContentResolver, "lock_screen_fallback_appwidget_id", 0, ID_DEFAULT_STATUS_WIDGET);
    }

    private void writeAppWidgets(int[] appWidgetIds) {
        Secure.putStringForUser(this.mContentResolver, "lock_screen_appwidget_ids", combineStrings(appWidgetIds, ","), ID_DEFAULT_STATUS_WIDGET);
    }

    public boolean addAppWidget(int widgetId, int index) {
        int[] widgets = getAppWidgets();
        if (widgets == null || index < 0 || index > widgets.length) {
            return DEBUG;
        }
        int[] newWidgets = new int[(widgets.length + FLAG_BIOMETRIC_WEAK_LIVELINESS)];
        int i = 0;
        int j = 0;
        while (i < newWidgets.length) {
            if (index == i) {
                newWidgets[i] = widgetId;
                i += FLAG_BIOMETRIC_WEAK_LIVELINESS;
            }
            if (i < newWidgets.length) {
                newWidgets[i] = widgets[j];
                j += FLAG_BIOMETRIC_WEAK_LIVELINESS;
            }
            i += FLAG_BIOMETRIC_WEAK_LIVELINESS;
        }
        writeAppWidgets(newWidgets);
        return true;
    }

    public boolean removeAppWidget(int widgetId) {
        int[] widgets = getAppWidgets();
        if (widgets.length == 0) {
            return DEBUG;
        }
        int[] newWidgets = new int[(widgets.length - 1)];
        int j = 0;
        for (int i = 0; i < widgets.length; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            if (widgets[i] != widgetId) {
                if (j >= newWidgets.length) {
                    return DEBUG;
                }
                newWidgets[j] = widgets[i];
                j += FLAG_BIOMETRIC_WEAK_LIVELINESS;
            }
        }
        writeAppWidgets(newWidgets);
        return true;
    }

    private long getLong(String secureSettingKey, long defaultValue, int userHandle) {
        try {
            defaultValue = getLockSettings().getLong(secureSettingKey, defaultValue, userHandle);
        } catch (RemoteException e) {
        }
        return defaultValue;
    }

    private long getLong(String secureSettingKey, long defaultValue) {
        try {
            defaultValue = getLockSettings().getLong(secureSettingKey, defaultValue, getCurrentOrCallingUserId());
        } catch (RemoteException e) {
        }
        return defaultValue;
    }

    private void setLong(String secureSettingKey, long value) {
        setLong(secureSettingKey, value, getCurrentOrCallingUserId());
    }

    private void setLong(String secureSettingKey, long value, int userHandle) {
        try {
            getLockSettings().setLong(secureSettingKey, value, userHandle);
        } catch (RemoteException re) {
            Log.e(TAG, "Couldn't write long " + secureSettingKey + re);
        }
    }

    private String getString(String secureSettingKey) {
        return getString(secureSettingKey, getCurrentOrCallingUserId());
    }

    private String getString(String secureSettingKey, int userHandle) {
        String str = null;
        try {
            str = getLockSettings().getString(secureSettingKey, null, userHandle);
        } catch (RemoteException e) {
        }
        return str;
    }

    private void setString(String secureSettingKey, String value, int userHandle) {
        try {
            getLockSettings().setString(secureSettingKey, value, userHandle);
        } catch (RemoteException re) {
            Log.e(TAG, "Couldn't write string " + secureSettingKey + re);
        }
    }

    public boolean isSecure() {
        return isSecure(getCurrentOrCallingUserId());
    }

    public boolean isSecure(int userId) {
        boolean isPattern;
        boolean isPassword;
        long mode = (long) getKeyguardStoredPasswordQuality(userId);
        if (mode == 65536) {
            isPattern = true;
        } else {
            isPattern = DEBUG;
        }
        if (mode == 131072 || mode == 196608 || mode == 262144 || mode == 327680 || mode == 393216) {
            isPassword = true;
        } else {
            isPassword = DEBUG;
        }
        if (isPattern && isLockPatternEnabled(userId) && savedPatternExists(userId)) {
            return true;
        }
        if (isPassword && savedPasswordExists(userId)) {
            return true;
        }
        return DEBUG;
    }

    public void updateEmergencyCallButtonState(Button button, boolean shown, boolean showIcon) {
        if (isEmergencyCallCapable() && shown) {
            int textId;
            button.setVisibility(0);
            if (isInCall()) {
                int phoneCallIcon;
                textId = R.string.lockscreen_return_to_call;
                if (showIcon) {
                    phoneCallIcon = R.drawable.stat_sys_phone_call;
                } else {
                    phoneCallIcon = 0;
                }
                button.setCompoundDrawablesWithIntrinsicBounds(phoneCallIcon, 0, 0, 0);
            } else {
                int emergencyIcon;
                textId = R.string.lockscreen_emergency_call;
                if (showIcon) {
                    emergencyIcon = R.drawable.ic_emergency;
                } else {
                    emergencyIcon = 0;
                }
                button.setCompoundDrawablesWithIntrinsicBounds(emergencyIcon, 0, 0, 0);
            }
            button.setText(textId);
            return;
        }
        button.setVisibility(8);
    }

    public void resumeCall() {
        getTelecommManager().showInCallScreen(DEBUG);
    }

    public boolean isInCall() {
        return getTelecommManager().isInCall();
    }

    private TelecomManager getTelecommManager() {
        return (TelecomManager) this.mContext.getSystemService("telecom");
    }

    private void finishBiometricWeak(int userId) {
        setBoolean(BIOMETRIC_WEAK_EVER_CHOSEN_KEY, true, userId);
        Intent intent = new Intent();
        intent.setClassName("com.android.facelock", "com.android.facelock.SetupEndScreen");
        this.mContext.startActivityAsUser(intent, new UserHandle(userId));
    }

    public void setPowerButtonInstantlyLocks(boolean enabled) {
        setBoolean(LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS, enabled);
    }

    public boolean getPowerButtonInstantlyLocks() {
        return getBoolean(LOCKSCREEN_POWER_BUTTON_INSTANTLY_LOCKS, true);
    }

    public static boolean isSafeModeEnabled() {
        try {
            return IWindowManager.Stub.asInterface(ServiceManager.getService("window")).isSafeModeEnabled();
        } catch (RemoteException e) {
            return DEBUG;
        }
    }

    public boolean hasWidgetsEnabledInKeyguard(int userid) {
        int[] widgets = getAppWidgets(userid);
        for (int i = 0; i < widgets.length; i += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            if (widgets[i] > 0) {
                return true;
            }
        }
        return DEBUG;
    }

    public boolean getWidgetsEnabled() {
        return getWidgetsEnabled(getCurrentOrCallingUserId());
    }

    public boolean getWidgetsEnabled(int userId) {
        return getBoolean(LOCKSCREEN_WIDGETS_ENABLED, DEBUG, userId);
    }

    public void setWidgetsEnabled(boolean enabled) {
        setWidgetsEnabled(enabled, getCurrentOrCallingUserId());
    }

    public void setWidgetsEnabled(boolean enabled, int userId) {
        setBoolean(LOCKSCREEN_WIDGETS_ENABLED, enabled, userId);
    }

    public void setEnabledTrustAgents(Collection<ComponentName> activeTrustAgents) {
        setEnabledTrustAgents(activeTrustAgents, getCurrentOrCallingUserId());
    }

    public List<ComponentName> getEnabledTrustAgents() {
        return getEnabledTrustAgents(getCurrentOrCallingUserId());
    }

    public void setEnabledTrustAgents(Collection<ComponentName> activeTrustAgents, int userId) {
        StringBuilder sb = new StringBuilder();
        for (ComponentName cn : activeTrustAgents) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(cn.flattenToShortString());
        }
        setString(ENABLED_TRUST_AGENTS, sb.toString(), userId);
        getTrustManager().reportEnabledTrustAgentsChanged(getCurrentOrCallingUserId());
    }

    public List<ComponentName> getEnabledTrustAgents(int userId) {
        String serialized = getString(ENABLED_TRUST_AGENTS, userId);
        if (TextUtils.isEmpty(serialized)) {
            return null;
        }
        String[] split = serialized.split(",");
        List<ComponentName> activeTrustAgents = new ArrayList(split.length);
        String[] arr$ = split;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += FLAG_BIOMETRIC_WEAK_LIVELINESS) {
            String s = arr$[i$];
            if (!TextUtils.isEmpty(s)) {
                activeTrustAgents.add(ComponentName.unflattenFromString(s));
            }
        }
        return activeTrustAgents;
    }

    public void requireCredentialEntry(int userId) {
        getTrustManager().reportRequireCredentialEntry(userId);
    }

    private void onAfterChangingPassword(int userHandle) {
        getTrustManager().reportEnabledTrustAgentsChanged(userHandle);
    }

    public boolean isCredentialRequiredToDecrypt(boolean defaultValue) {
        int value = Global.getInt(this.mContentResolver, "require_password_to_decrypt", -1);
        if (value == -1) {
            return defaultValue;
        }
        return value != 0 ? true : DEBUG;
    }

    public void setCredentialRequiredToDecrypt(boolean required) {
        if (getCurrentUser() != 0) {
            Log.w(TAG, "Only device owner may call setCredentialRequiredForDecrypt()");
        } else {
            Global.putInt(this.mContext.getContentResolver(), "require_password_to_decrypt", required ? FLAG_BIOMETRIC_WEAK_LIVELINESS : 0);
        }
    }
}
