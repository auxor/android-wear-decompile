package android.os;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ProxyInfo;
import android.net.wifi.WifiEnterpriseConfig;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    public static final String DISALLOW_ADD_USER = "no_add_user";
    public static final String DISALLOW_ADJUST_VOLUME = "no_adjust_volume";
    public static final String DISALLOW_APPS_CONTROL = "no_control_apps";
    public static final String DISALLOW_CONFIG_BLUETOOTH = "no_config_bluetooth";
    public static final String DISALLOW_CONFIG_CELL_BROADCASTS = "no_config_cell_broadcasts";
    public static final String DISALLOW_CONFIG_CREDENTIALS = "no_config_credentials";
    public static final String DISALLOW_CONFIG_MOBILE_NETWORKS = "no_config_mobile_networks";
    public static final String DISALLOW_CONFIG_TETHERING = "no_config_tethering";
    public static final String DISALLOW_CONFIG_VPN = "no_config_vpn";
    public static final String DISALLOW_CONFIG_WIFI = "no_config_wifi";
    public static final String DISALLOW_CREATE_WINDOWS = "no_create_windows";
    public static final String DISALLOW_CROSS_PROFILE_COPY_PASTE = "no_cross_profile_copy_paste";
    public static final String DISALLOW_DEBUGGING_FEATURES = "no_debugging_features";
    public static final String DISALLOW_FACTORY_RESET = "no_factory_reset";
    public static final String DISALLOW_INSTALL_APPS = "no_install_apps";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources";
    public static final String DISALLOW_MODIFY_ACCOUNTS = "no_modify_accounts";
    public static final String DISALLOW_MOUNT_PHYSICAL_MEDIA = "no_physical_media";
    public static final String DISALLOW_OUTGOING_BEAM = "no_outgoing_beam";
    public static final String DISALLOW_OUTGOING_CALLS = "no_outgoing_calls";
    public static final String DISALLOW_REMOVE_USER = "no_remove_user";
    public static final String DISALLOW_SHARE_LOCATION = "no_share_location";
    public static final String DISALLOW_SMS = "no_sms";
    public static final String DISALLOW_UNINSTALL_APPS = "no_uninstall_apps";
    public static final String DISALLOW_UNMUTE_MICROPHONE = "no_unmute_microphone";
    public static final String DISALLOW_USB_FILE_TRANSFER = "no_usb_file_transfer";
    public static final String ENSURE_VERIFY_APPS = "ensure_verify_apps";
    public static final String KEY_RESTRICTIONS_PENDING = "restrictions_pending";
    public static final int PIN_VERIFICATION_FAILED_INCORRECT = -3;
    public static final int PIN_VERIFICATION_FAILED_NOT_SET = -2;
    public static final int PIN_VERIFICATION_SUCCESS = -1;
    private static String TAG;
    private static UserManager sInstance;
    private final Context mContext;
    private final IUserManager mService;

    static {
        TAG = "UserManager";
        sInstance = null;
    }

    public static synchronized UserManager get(Context context) {
        UserManager userManager;
        synchronized (UserManager.class) {
            if (sInstance == null) {
                sInstance = (UserManager) context.getSystemService(Context.USER_SERVICE);
            }
            userManager = sInstance;
        }
        return userManager;
    }

    public UserManager(Context context, IUserManager service) {
        this.mService = service;
        this.mContext = context;
    }

    public static boolean supportsMultipleUsers() {
        return getMaxSupportedUsers() > 1 && SystemProperties.getBoolean("fw.show_multiuserui", Resources.getSystem().getBoolean(17956971));
    }

    public int getUserHandle() {
        return UserHandle.myUserId();
    }

    public String getUserName() {
        try {
            return this.mService.getUserInfo(getUserHandle()).name;
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user name", re);
            return ProxyInfo.LOCAL_EXCL_LIST;
        }
    }

    public boolean isUserAGoat() {
        return this.mContext.getPackageManager().isPackageAvailable("com.coffeestainstudios.goatsimulator");
    }

    public boolean isLinkedUser() {
        try {
            return this.mService.isRestricted();
        } catch (RemoteException re) {
            Log.w(TAG, "Could not check if user is limited ", re);
            return false;
        }
    }

    public boolean isGuestUser() {
        UserInfo user = getUserInfo(UserHandle.myUserId());
        return user != null ? user.isGuest() : false;
    }

    public boolean isManagedProfile() {
        UserInfo user = getUserInfo(UserHandle.myUserId());
        return user != null ? user.isManagedProfile() : false;
    }

    public boolean isUserRunning(UserHandle user) {
        boolean z = false;
        try {
            z = ActivityManagerNative.getDefault().isUserRunning(user.getIdentifier(), false);
        } catch (RemoteException e) {
        }
        return z;
    }

    public boolean isUserRunningOrStopping(UserHandle user) {
        try {
            return ActivityManagerNative.getDefault().isUserRunning(user.getIdentifier(), true);
        } catch (RemoteException e) {
            return false;
        }
    }

    public UserInfo getUserInfo(int userHandle) {
        try {
            return this.mService.getUserInfo(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user info", re);
            return null;
        }
    }

    public Bundle getUserRestrictions() {
        return getUserRestrictions(Process.myUserHandle());
    }

    public Bundle getUserRestrictions(UserHandle userHandle) {
        try {
            return this.mService.getUserRestrictions(userHandle.getIdentifier());
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user restrictions", re);
            return Bundle.EMPTY;
        }
    }

    @Deprecated
    public void setUserRestrictions(Bundle restrictions) {
        setUserRestrictions(restrictions, Process.myUserHandle());
    }

    @Deprecated
    public void setUserRestrictions(Bundle restrictions, UserHandle userHandle) {
        try {
            this.mService.setUserRestrictions(restrictions, userHandle.getIdentifier());
        } catch (RemoteException re) {
            Log.w(TAG, "Could not set user restrictions", re);
        }
    }

    @Deprecated
    public void setUserRestriction(String key, boolean value) {
        Bundle bundle = getUserRestrictions();
        bundle.putBoolean(key, value);
        setUserRestrictions(bundle);
    }

    @Deprecated
    public void setUserRestriction(String key, boolean value, UserHandle userHandle) {
        Bundle bundle = getUserRestrictions(userHandle);
        bundle.putBoolean(key, value);
        setUserRestrictions(bundle, userHandle);
    }

    public boolean hasUserRestriction(String restrictionKey) {
        return hasUserRestriction(restrictionKey, Process.myUserHandle());
    }

    public boolean hasUserRestriction(String restrictionKey, UserHandle userHandle) {
        try {
            return this.mService.hasUserRestriction(restrictionKey, userHandle.getIdentifier());
        } catch (RemoteException re) {
            Log.w(TAG, "Could not check user restrictions", re);
            return false;
        }
    }

    public long getSerialNumberForUser(UserHandle user) {
        return (long) getUserSerialNumber(user.getIdentifier());
    }

    public UserHandle getUserForSerialNumber(long serialNumber) {
        int ident = getUserHandle((int) serialNumber);
        return ident >= 0 ? new UserHandle(ident) : null;
    }

    public UserInfo createUser(String name, int flags) {
        try {
            return this.mService.createUser(name, flags);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not create a user", re);
            return null;
        }
    }

    public UserInfo createGuest(Context context, String name) {
        UserInfo guest = createUser(name, 4);
        if (guest != null) {
            Secure.putStringForUser(context.getContentResolver(), Secure.SKIP_FIRST_USE_HINTS, WifiEnterpriseConfig.ENGINE_ENABLE, guest.id);
            try {
                Bundle guestRestrictions = this.mService.getDefaultGuestRestrictions();
                guestRestrictions.putBoolean(DISALLOW_SMS, true);
                guestRestrictions.putBoolean(DISALLOW_INSTALL_UNKNOWN_SOURCES, true);
                this.mService.setUserRestrictions(guestRestrictions, guest.id);
            } catch (RemoteException e) {
                Log.w(TAG, "Could not update guest restrictions");
            }
        }
        return guest;
    }

    public UserInfo createSecondaryUser(String name, int flags) {
        try {
            UserInfo user = this.mService.createUser(name, flags);
            if (user == null) {
                return null;
            }
            Bundle userRestrictions = this.mService.getUserRestrictions(user.id);
            addDefaultUserRestrictions(userRestrictions);
            this.mService.setUserRestrictions(userRestrictions, user.id);
            return user;
        } catch (RemoteException re) {
            Log.w(TAG, "Could not create a user", re);
            return null;
        }
    }

    private static void addDefaultUserRestrictions(Bundle restrictions) {
        restrictions.putBoolean(DISALLOW_OUTGOING_CALLS, true);
        restrictions.putBoolean(DISALLOW_SMS, true);
    }

    public UserInfo createProfileForUser(String name, int flags, int userHandle) {
        try {
            return this.mService.createProfileForUser(name, flags, userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not create a user", re);
            return null;
        }
    }

    public boolean markGuestForDeletion(int userHandle) {
        try {
            return this.mService.markGuestForDeletion(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not mark guest for deletion", re);
            return false;
        }
    }

    public void setUserEnabled(int userHandle) {
        try {
            this.mService.setUserEnabled(userHandle);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not enable the profile", e);
        }
    }

    public int getUserCount() {
        List<UserInfo> users = getUsers();
        return users != null ? users.size() : 1;
    }

    public List<UserInfo> getUsers() {
        try {
            return this.mService.getUsers(false);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user list", re);
            return null;
        }
    }

    public boolean canAddMoreUsers() {
        List<UserInfo> users = getUsers(true);
        int totalUserCount = users.size();
        int aliveUserCount = 0;
        for (int i = 0; i < totalUserCount; i++) {
            if (!((UserInfo) users.get(i)).isGuest()) {
                aliveUserCount++;
            }
        }
        if (aliveUserCount < getMaxSupportedUsers()) {
            return true;
        }
        return false;
    }

    public List<UserInfo> getProfiles(int userHandle) {
        try {
            return this.mService.getProfiles(userHandle, false);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user list", re);
            return null;
        }
    }

    public List<UserHandle> getUserProfiles() {
        ArrayList<UserHandle> profiles = new ArrayList();
        List<UserInfo> users = new ArrayList();
        try {
            for (UserInfo info : this.mService.getProfiles(UserHandle.myUserId(), true)) {
                profiles.add(new UserHandle(info.id));
            }
            return profiles;
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user list", re);
            return null;
        }
    }

    public UserInfo getProfileParent(int userHandle) {
        try {
            return this.mService.getProfileParent(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get profile parent", re);
            return null;
        }
    }

    public Drawable getBadgedIconForUser(Drawable icon, UserHandle user) {
        return this.mContext.getPackageManager().getUserBadgedIcon(icon, user);
    }

    public Drawable getBadgedDrawableForUser(Drawable badgedDrawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return this.mContext.getPackageManager().getUserBadgedDrawableForDensity(badgedDrawable, user, badgeLocation, badgeDensity);
    }

    public CharSequence getBadgedLabelForUser(CharSequence label, UserHandle user) {
        return this.mContext.getPackageManager().getUserBadgedLabel(label, user);
    }

    public List<UserInfo> getUsers(boolean excludeDying) {
        try {
            return this.mService.getUsers(excludeDying);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get user list", re);
            return null;
        }
    }

    public boolean removeUser(int userHandle) {
        try {
            return this.mService.removeUser(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not remove user ", re);
            return false;
        }
    }

    public void setUserName(int userHandle, String name) {
        try {
            this.mService.setUserName(userHandle, name);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not set the user name ", re);
        }
    }

    public void setUserIcon(int userHandle, Bitmap icon) {
        try {
            this.mService.setUserIcon(userHandle, icon);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not set the user icon ", re);
        }
    }

    public Bitmap getUserIcon(int userHandle) {
        try {
            return this.mService.getUserIcon(userHandle);
        } catch (RemoteException re) {
            Log.w(TAG, "Could not get the user icon ", re);
            return null;
        }
    }

    public static int getMaxSupportedUsers() {
        if (Build.ID.startsWith("JVP") || ActivityManager.isLowRamDeviceStatic()) {
            return 1;
        }
        return SystemProperties.getInt("fw.max_users", Resources.getSystem().getInteger(17694841));
    }

    public boolean isUserSwitcherEnabled() {
        List<UserInfo> users = getUsers(true);
        if (users == null) {
            return false;
        }
        int switchableUserCount = 0;
        for (UserInfo user : users) {
            if (user.supportsSwitchTo()) {
                switchableUserCount++;
            }
        }
        boolean guestEnabled;
        if (Global.getInt(this.mContext.getContentResolver(), Global.GUEST_USER_ENABLED, 0) == 1) {
            guestEnabled = true;
        } else {
            guestEnabled = false;
        }
        if (switchableUserCount > 1 || guestEnabled) {
            return true;
        }
        return false;
    }

    public int getUserSerialNumber(int userHandle) {
        try {
            return this.mService.getUserSerialNumber(userHandle);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get serial number for user " + userHandle);
            return PIN_VERIFICATION_SUCCESS;
        }
    }

    public int getUserHandle(int userSerialNumber) {
        try {
            return this.mService.getUserHandle(userSerialNumber);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get userHandle for user " + userSerialNumber);
            return PIN_VERIFICATION_SUCCESS;
        }
    }

    public Bundle getApplicationRestrictions(String packageName) {
        try {
            return this.mService.getApplicationRestrictions(packageName);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get application restrictions for package " + packageName);
            return null;
        }
    }

    public Bundle getApplicationRestrictions(String packageName, UserHandle user) {
        try {
            return this.mService.getApplicationRestrictionsForUser(packageName, user.getIdentifier());
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get application restrictions for user " + user.getIdentifier());
            return null;
        }
    }

    public void setApplicationRestrictions(String packageName, Bundle restrictions, UserHandle user) {
        try {
            this.mService.setApplicationRestrictions(packageName, restrictions, user.getIdentifier());
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set application restrictions for user " + user.getIdentifier());
        }
    }

    public boolean setRestrictionsChallenge(String newPin) {
        try {
            return this.mService.setRestrictionsChallenge(newPin);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not change restrictions pin");
            return false;
        }
    }

    public int checkRestrictionsChallenge(String pin) {
        try {
            return this.mService.checkRestrictionsChallenge(pin);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not check restrictions pin");
            return PIN_VERIFICATION_FAILED_INCORRECT;
        }
    }

    public boolean hasRestrictionsChallenge() {
        try {
            return this.mService.hasRestrictionsChallenge();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not change restrictions pin");
            return false;
        }
    }

    public void removeRestrictions() {
        try {
            this.mService.removeRestrictions();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not change restrictions pin");
        }
    }

    public void setDefaultGuestRestrictions(Bundle restrictions) {
        try {
            this.mService.setDefaultGuestRestrictions(restrictions);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set guest restrictions");
        }
    }

    public Bundle getDefaultGuestRestrictions() {
        try {
            return this.mService.getDefaultGuestRestrictions();
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set guest restrictions");
            return new Bundle();
        }
    }
}
