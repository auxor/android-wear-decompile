package com.android.internal.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import java.util.HashSet;

public abstract class PackageMonitor extends BroadcastReceiver {
    public static final int PACKAGE_PERMANENT_CHANGE = 3;
    public static final int PACKAGE_TEMPORARY_CHANGE = 2;
    public static final int PACKAGE_UNCHANGED = 0;
    public static final int PACKAGE_UPDATING = 1;
    static final IntentFilter sExternalFilt;
    static final IntentFilter sNonDataFilt;
    static final IntentFilter sPackageFilt;
    String[] mAppearingPackages;
    int mChangeType;
    int mChangeUserId;
    String[] mDisappearingPackages;
    String[] mModifiedPackages;
    Context mRegisteredContext;
    Handler mRegisteredHandler;
    boolean mSomePackagesChanged;
    String[] mTempArray;
    final HashSet<String> mUpdatingPackages;

    public PackageMonitor() {
        this.mUpdatingPackages = new HashSet();
        this.mChangeUserId = -10000;
        this.mTempArray = new String[PACKAGE_UPDATING];
    }

    static {
        sPackageFilt = new IntentFilter();
        sNonDataFilt = new IntentFilter();
        sExternalFilt = new IntentFilter();
        sPackageFilt.addAction("android.intent.action.PACKAGE_ADDED");
        sPackageFilt.addAction("android.intent.action.PACKAGE_REMOVED");
        sPackageFilt.addAction("android.intent.action.PACKAGE_CHANGED");
        sPackageFilt.addAction("android.intent.action.QUERY_PACKAGE_RESTART");
        sPackageFilt.addAction("android.intent.action.PACKAGE_RESTARTED");
        sPackageFilt.addAction("android.intent.action.UID_REMOVED");
        sPackageFilt.addDataScheme("package");
        sNonDataFilt.addAction("android.intent.action.UID_REMOVED");
        sNonDataFilt.addAction("android.intent.action.USER_STOPPED");
        sExternalFilt.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        sExternalFilt.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
    }

    public void register(Context context, Looper thread, boolean externalStorage) {
        register(context, thread, null, externalStorage);
    }

    public void register(Context context, Looper thread, UserHandle user, boolean externalStorage) {
        if (this.mRegisteredContext != null) {
            throw new IllegalStateException("Already registered");
        }
        this.mRegisteredContext = context;
        if (thread == null) {
            this.mRegisteredHandler = BackgroundThread.getHandler();
        } else {
            this.mRegisteredHandler = new Handler(thread);
        }
        if (user != null) {
            context.registerReceiverAsUser(this, user, sPackageFilt, null, this.mRegisteredHandler);
            context.registerReceiverAsUser(this, user, sNonDataFilt, null, this.mRegisteredHandler);
            if (externalStorage) {
                context.registerReceiverAsUser(this, user, sExternalFilt, null, this.mRegisteredHandler);
                return;
            }
            return;
        }
        context.registerReceiver(this, sPackageFilt, null, this.mRegisteredHandler);
        context.registerReceiver(this, sNonDataFilt, null, this.mRegisteredHandler);
        if (externalStorage) {
            context.registerReceiver(this, sExternalFilt, null, this.mRegisteredHandler);
        }
    }

    public Handler getRegisteredHandler() {
        return this.mRegisteredHandler;
    }

    public void unregister() {
        if (this.mRegisteredContext == null) {
            throw new IllegalStateException("Not registered");
        }
        this.mRegisteredContext.unregisterReceiver(this);
        this.mRegisteredContext = null;
    }

    boolean isPackageUpdating(String packageName) {
        boolean contains;
        synchronized (this.mUpdatingPackages) {
            contains = this.mUpdatingPackages.contains(packageName);
        }
        return contains;
    }

    public void onBeginPackageChanges() {
    }

    public void onPackageAdded(String packageName, int uid) {
    }

    public void onPackageRemoved(String packageName, int uid) {
    }

    public void onPackageRemovedAllUsers(String packageName, int uid) {
    }

    public void onPackageUpdateStarted(String packageName, int uid) {
    }

    public void onPackageUpdateFinished(String packageName, int uid) {
    }

    public boolean onPackageChanged(String packageName, int uid, String[] components) {
        if (components != null) {
            String[] arr$ = components;
            int len$ = arr$.length;
            for (int i$ = PACKAGE_UNCHANGED; i$ < len$; i$ += PACKAGE_UPDATING) {
                if (packageName.equals(arr$[i$])) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onHandleForceStop(Intent intent, String[] packages, int uid, boolean doit) {
        return false;
    }

    public void onHandleUserStop(Intent intent, int userHandle) {
    }

    public void onUidRemoved(int uid) {
    }

    public void onPackagesAvailable(String[] packages) {
    }

    public void onPackagesUnavailable(String[] packages) {
    }

    public void onPackageDisappeared(String packageName, int reason) {
    }

    public void onPackageAppeared(String packageName, int reason) {
    }

    public void onPackageModified(String packageName) {
    }

    public boolean didSomePackagesChange() {
        return this.mSomePackagesChanged;
    }

    public int isPackageAppearing(String packageName) {
        if (this.mAppearingPackages != null) {
            for (int i = this.mAppearingPackages.length - 1; i >= 0; i--) {
                if (packageName.equals(this.mAppearingPackages[i])) {
                    return this.mChangeType;
                }
            }
        }
        return PACKAGE_UNCHANGED;
    }

    public boolean anyPackagesAppearing() {
        return this.mAppearingPackages != null;
    }

    public int isPackageDisappearing(String packageName) {
        if (this.mDisappearingPackages != null) {
            for (int i = this.mDisappearingPackages.length - 1; i >= 0; i--) {
                if (packageName.equals(this.mDisappearingPackages[i])) {
                    return this.mChangeType;
                }
            }
        }
        return PACKAGE_UNCHANGED;
    }

    public boolean anyPackagesDisappearing() {
        return this.mDisappearingPackages != null;
    }

    public boolean isReplacing() {
        return this.mChangeType == PACKAGE_UPDATING;
    }

    public boolean isPackageModified(String packageName) {
        if (this.mModifiedPackages != null) {
            for (int i = this.mModifiedPackages.length - 1; i >= 0; i--) {
                if (packageName.equals(this.mModifiedPackages[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onSomePackagesChanged() {
    }

    public void onFinishPackageChanges() {
    }

    public int getChangingUserId() {
        return this.mChangeUserId;
    }

    String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        return uri != null ? uri.getSchemeSpecificPart() : null;
    }

    public void onReceive(Context context, Intent intent) {
        int i = PACKAGE_TEMPORARY_CHANGE;
        this.mChangeUserId = intent.getIntExtra("android.intent.extra.user_handle", -10000);
        if (this.mChangeUserId == -10000) {
            Slog.w("PackageMonitor", "Intent broadcast does not contain user handle: " + intent);
            return;
        }
        onBeginPackageChanges();
        this.mAppearingPackages = null;
        this.mDisappearingPackages = null;
        this.mSomePackagesChanged = false;
        String action = intent.getAction();
        String pkg;
        int uid;
        if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
            pkg = getPackageName(intent);
            uid = intent.getIntExtra("android.intent.extra.UID", PACKAGE_UNCHANGED);
            this.mSomePackagesChanged = true;
            if (pkg != null) {
                this.mAppearingPackages = this.mTempArray;
                this.mTempArray[PACKAGE_UNCHANGED] = pkg;
                if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    this.mModifiedPackages = this.mTempArray;
                    this.mChangeType = PACKAGE_UPDATING;
                    onPackageUpdateFinished(pkg, uid);
                    onPackageModified(pkg);
                } else {
                    this.mChangeType = PACKAGE_PERMANENT_CHANGE;
                    onPackageAdded(pkg, uid);
                }
                onPackageAppeared(pkg, this.mChangeType);
                if (this.mChangeType == PACKAGE_UPDATING) {
                    synchronized (this.mUpdatingPackages) {
                        this.mUpdatingPackages.remove(pkg);
                    }
                }
            }
        } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
            pkg = getPackageName(intent);
            uid = intent.getIntExtra("android.intent.extra.UID", PACKAGE_UNCHANGED);
            if (pkg != null) {
                this.mDisappearingPackages = this.mTempArray;
                this.mTempArray[PACKAGE_UNCHANGED] = pkg;
                if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    this.mChangeType = PACKAGE_UPDATING;
                    synchronized (this.mUpdatingPackages) {
                    }
                    onPackageUpdateStarted(pkg, uid);
                } else {
                    this.mChangeType = PACKAGE_PERMANENT_CHANGE;
                    this.mSomePackagesChanged = true;
                    onPackageRemoved(pkg, uid);
                    if (intent.getBooleanExtra("android.intent.extra.REMOVED_FOR_ALL_USERS", false)) {
                        onPackageRemovedAllUsers(pkg, uid);
                    }
                }
                onPackageDisappeared(pkg, this.mChangeType);
            }
        } else if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
            pkg = getPackageName(intent);
            uid = intent.getIntExtra("android.intent.extra.UID", PACKAGE_UNCHANGED);
            String[] components = intent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
            if (pkg != null) {
                this.mModifiedPackages = this.mTempArray;
                this.mTempArray[PACKAGE_UNCHANGED] = pkg;
                this.mChangeType = PACKAGE_PERMANENT_CHANGE;
                if (onPackageChanged(pkg, uid, components)) {
                    this.mSomePackagesChanged = true;
                }
                onPackageModified(pkg);
            }
        } else if ("android.intent.action.QUERY_PACKAGE_RESTART".equals(action)) {
            this.mDisappearingPackages = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
            this.mChangeType = PACKAGE_TEMPORARY_CHANGE;
            if (onHandleForceStop(intent, this.mDisappearingPackages, intent.getIntExtra("android.intent.extra.UID", PACKAGE_UNCHANGED), false)) {
                setResultCode(-1);
            }
        } else if ("android.intent.action.PACKAGE_RESTARTED".equals(action)) {
            String[] strArr = new String[PACKAGE_UPDATING];
            strArr[PACKAGE_UNCHANGED] = getPackageName(intent);
            this.mDisappearingPackages = strArr;
            this.mChangeType = PACKAGE_TEMPORARY_CHANGE;
            onHandleForceStop(intent, this.mDisappearingPackages, intent.getIntExtra("android.intent.extra.UID", PACKAGE_UNCHANGED), true);
        } else if ("android.intent.action.UID_REMOVED".equals(action)) {
            onUidRemoved(intent.getIntExtra("android.intent.extra.UID", PACKAGE_UNCHANGED));
        } else if ("android.intent.action.USER_STOPPED".equals(action)) {
            if (intent.hasExtra("android.intent.extra.user_handle")) {
                onHandleUserStop(intent, intent.getIntExtra("android.intent.extra.user_handle", PACKAGE_UNCHANGED));
            }
        } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action)) {
            pkgList = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
            this.mAppearingPackages = pkgList;
            if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                i = PACKAGE_UPDATING;
            }
            this.mChangeType = i;
            this.mSomePackagesChanged = true;
            if (pkgList != null) {
                onPackagesAvailable(pkgList);
                for (i = PACKAGE_UNCHANGED; i < pkgList.length; i += PACKAGE_UPDATING) {
                    onPackageAppeared(pkgList[i], this.mChangeType);
                }
            }
        } else if ("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
            pkgList = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
            this.mDisappearingPackages = pkgList;
            if (intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                i = PACKAGE_UPDATING;
            }
            this.mChangeType = i;
            this.mSomePackagesChanged = true;
            if (pkgList != null) {
                onPackagesUnavailable(pkgList);
                for (i = PACKAGE_UNCHANGED; i < pkgList.length; i += PACKAGE_UPDATING) {
                    onPackageDisappeared(pkgList[i], this.mChangeType);
                }
            }
        }
        if (this.mSomePackagesChanged) {
            onSomePackagesChanged();
        }
        onFinishPackageChanges();
        this.mChangeUserId = -10000;
    }
}
