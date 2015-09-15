package android.content.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface IPackageManager extends IInterface {

    public static abstract class Stub extends Binder implements IPackageManager {
        private static final String DESCRIPTOR = "android.content.pm.IPackageManager";
        static final int TRANSACTION_activitySupportsIntent = 13;
        static final int TRANSACTION_addCrossProfileIntentFilter = 68;
        static final int TRANSACTION_addPackageToPreferred = 56;
        static final int TRANSACTION_addPermission = 19;
        static final int TRANSACTION_addPermissionAsync = 94;
        static final int TRANSACTION_addPersistentPreferredActivity = 66;
        static final int TRANSACTION_addPreferredActivity = 62;
        static final int TRANSACTION_canForwardTo = 33;
        static final int TRANSACTION_canonicalToCurrentPackageNames = 6;
        static final int TRANSACTION_checkPermission = 17;
        static final int TRANSACTION_checkSignatures = 24;
        static final int TRANSACTION_checkUidPermission = 18;
        static final int TRANSACTION_checkUidSignatures = 25;
        static final int TRANSACTION_clearApplicationUserData = 79;
        static final int TRANSACTION_clearCrossProfileIntentFilters = 69;
        static final int TRANSACTION_clearPackagePersistentPreferredActivities = 67;
        static final int TRANSACTION_clearPackagePreferredActivities = 64;
        static final int TRANSACTION_currentToCanonicalPackageNames = 5;
        static final int TRANSACTION_deleteApplicationCacheFiles = 78;
        static final int TRANSACTION_deletePackage = 54;
        static final int TRANSACTION_deletePackageAsUser = 53;
        static final int TRANSACTION_enterSafeMode = 84;
        static final int TRANSACTION_extendVerificationTimeout = 99;
        static final int TRANSACTION_finishPackageInstall = 51;
        static final int TRANSACTION_forceDexOpt = 90;
        static final int TRANSACTION_freeStorage = 77;
        static final int TRANSACTION_freeStorageAndNotify = 76;
        static final int TRANSACTION_getActivityInfo = 12;
        static final int TRANSACTION_getAllPermissionGroups = 10;
        static final int TRANSACTION_getAppOpPermissionPackages = 31;
        static final int TRANSACTION_getApplicationEnabledSetting = 74;
        static final int TRANSACTION_getApplicationHiddenSettingAsUser = 108;
        static final int TRANSACTION_getApplicationInfo = 11;
        static final int TRANSACTION_getBlockUninstallForUser = 111;
        static final int TRANSACTION_getComponentEnabledSetting = 72;
        static final int TRANSACTION_getFlagsForUid = 29;
        static final int TRANSACTION_getHomeActivities = 70;
        static final int TRANSACTION_getInstallLocation = 96;
        static final int TRANSACTION_getInstalledApplications = 42;
        static final int TRANSACTION_getInstalledPackages = 40;
        static final int TRANSACTION_getInstallerPackageName = 55;
        static final int TRANSACTION_getInstrumentationInfo = 47;
        static final int TRANSACTION_getKeySetByAlias = 112;
        static final int TRANSACTION_getLastChosenActivity = 60;
        static final int TRANSACTION_getNameForUid = 27;
        static final int TRANSACTION_getPackageGids = 4;
        static final int TRANSACTION_getPackageInfo = 2;
        static final int TRANSACTION_getPackageInstaller = 109;
        static final int TRANSACTION_getPackageSizeInfo = 80;
        static final int TRANSACTION_getPackageUid = 3;
        static final int TRANSACTION_getPackagesForUid = 26;
        static final int TRANSACTION_getPackagesHoldingPermissions = 41;
        static final int TRANSACTION_getPermissionGroupInfo = 9;
        static final int TRANSACTION_getPermissionInfo = 7;
        static final int TRANSACTION_getPersistentApplications = 43;
        static final int TRANSACTION_getPreferredActivities = 65;
        static final int TRANSACTION_getPreferredPackages = 58;
        static final int TRANSACTION_getProviderInfo = 16;
        static final int TRANSACTION_getReceiverInfo = 14;
        static final int TRANSACTION_getServiceInfo = 15;
        static final int TRANSACTION_getSigningKeySet = 113;
        static final int TRANSACTION_getSystemAvailableFeatures = 82;
        static final int TRANSACTION_getSystemSharedLibraryNames = 81;
        static final int TRANSACTION_getUidForSharedUser = 28;
        static final int TRANSACTION_getVerifierDeviceIdentity = 100;
        static final int TRANSACTION_grantPermission = 21;
        static final int TRANSACTION_hasSystemFeature = 83;
        static final int TRANSACTION_hasSystemUidErrors = 87;
        static final int TRANSACTION_installExistingPackageAsUser = 97;
        static final int TRANSACTION_installPackage = 49;
        static final int TRANSACTION_installPackageAsUser = 50;
        static final int TRANSACTION_isFirstBoot = 101;
        static final int TRANSACTION_isOnlyCoreApps = 102;
        static final int TRANSACTION_isPackageAvailable = 1;
        static final int TRANSACTION_isPackageSignedByKeySet = 114;
        static final int TRANSACTION_isPackageSignedByKeySetExactly = 115;
        static final int TRANSACTION_isPermissionEnforced = 105;
        static final int TRANSACTION_isProtectedBroadcast = 23;
        static final int TRANSACTION_isSafeMode = 85;
        static final int TRANSACTION_isStorageLow = 106;
        static final int TRANSACTION_isUidPrivileged = 30;
        static final int TRANSACTION_isUpgrade = 103;
        static final int TRANSACTION_movePackage = 93;
        static final int TRANSACTION_nextPackageToClean = 92;
        static final int TRANSACTION_performBootDexOpt = 88;
        static final int TRANSACTION_performDexOptIfNeeded = 89;
        static final int TRANSACTION_queryContentProviders = 46;
        static final int TRANSACTION_queryInstrumentation = 48;
        static final int TRANSACTION_queryIntentActivities = 34;
        static final int TRANSACTION_queryIntentActivityOptions = 35;
        static final int TRANSACTION_queryIntentContentProviders = 39;
        static final int TRANSACTION_queryIntentReceivers = 36;
        static final int TRANSACTION_queryIntentServices = 38;
        static final int TRANSACTION_queryPermissionsByGroup = 8;
        static final int TRANSACTION_querySyncProviders = 45;
        static final int TRANSACTION_removePackageFromPreferred = 57;
        static final int TRANSACTION_removePermission = 20;
        static final int TRANSACTION_replacePreferredActivity = 63;
        static final int TRANSACTION_resetPreferredActivities = 59;
        static final int TRANSACTION_resolveContentProvider = 44;
        static final int TRANSACTION_resolveIntent = 32;
        static final int TRANSACTION_resolveService = 37;
        static final int TRANSACTION_revokePermission = 22;
        static final int TRANSACTION_setApplicationEnabledSetting = 73;
        static final int TRANSACTION_setApplicationHiddenSettingAsUser = 107;
        static final int TRANSACTION_setBlockUninstallForUser = 110;
        static final int TRANSACTION_setComponentEnabledSetting = 71;
        static final int TRANSACTION_setInstallLocation = 95;
        static final int TRANSACTION_setInstallerPackageName = 52;
        static final int TRANSACTION_setLastChosenActivity = 61;
        static final int TRANSACTION_setPackageStoppedState = 75;
        static final int TRANSACTION_setPermissionEnforced = 104;
        static final int TRANSACTION_systemReady = 86;
        static final int TRANSACTION_updateExternalMediaStatus = 91;
        static final int TRANSACTION_verifyPendingInstall = 98;

        private static class Proxy implements IPackageManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public boolean isPackageAvailable(String packageName, int userId) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_isPackageAvailable, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public PackageInfo getPackageInfo(String packageName, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    PackageInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getPackageInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (PackageInfo) PackageInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getPackageUid(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getPackageUid, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int[] getPackageGids(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_getPackageGids, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] currentToCanonicalPackageNames(String[] names) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(names);
                    this.mRemote.transact(Stub.TRANSACTION_currentToCanonicalPackageNames, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] canonicalToCurrentPackageNames(String[] names) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(names);
                    this.mRemote.transact(Stub.TRANSACTION_canonicalToCurrentPackageNames, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public PermissionInfo getPermissionInfo(String name, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    PermissionInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_getPermissionInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (PermissionInfo) PermissionInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(group);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_queryPermissionsByGroup, _data, _reply, 0);
                    _reply.readException();
                    List<PermissionInfo> _result = _reply.createTypedArrayList(PermissionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    PermissionGroupInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_getPermissionGroupInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (PermissionGroupInfo) PermissionGroupInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<PermissionGroupInfo> getAllPermissionGroups(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_getAllPermissionGroups, _data, _reply, 0);
                    _reply.readException();
                    List<PermissionGroupInfo> _result = _reply.createTypedArrayList(PermissionGroupInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ApplicationInfo getApplicationInfo(String packageName, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ApplicationInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getApplicationInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ApplicationInfo) ApplicationInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ActivityInfo getActivityInfo(ComponentName className, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ActivityInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getActivityInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ActivityInfo) ActivityInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean activitySupportsIntent(ComponentName className, Intent intent, String resolvedType) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    this.mRemote.transact(Stub.TRANSACTION_activitySupportsIntent, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ActivityInfo getReceiverInfo(ComponentName className, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ActivityInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getReceiverInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ActivityInfo) ActivityInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ServiceInfo getServiceInfo(ComponentName className, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ServiceInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getServiceInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ServiceInfo) ServiceInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ProviderInfo getProviderInfo(ComponentName className, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ProviderInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getProviderInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ProviderInfo) ProviderInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int checkPermission(String permName, String pkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permName);
                    _data.writeString(pkgName);
                    this.mRemote.transact(Stub.TRANSACTION_checkPermission, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int checkUidPermission(String permName, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permName);
                    _data.writeInt(uid);
                    this.mRemote.transact(Stub.TRANSACTION_checkUidPermission, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean addPermission(PermissionInfo info) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_addPermission, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removePermission(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(Stub.TRANSACTION_removePermission, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void grantPermission(String packageName, String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    this.mRemote.transact(Stub.TRANSACTION_grantPermission, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void revokePermission(String packageName, String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(permissionName);
                    this.mRemote.transact(Stub.TRANSACTION_revokePermission, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isProtectedBroadcast(String actionName) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(actionName);
                    this.mRemote.transact(Stub.TRANSACTION_isProtectedBroadcast, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int checkSignatures(String pkg1, String pkg2) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkg1);
                    _data.writeString(pkg2);
                    this.mRemote.transact(Stub.TRANSACTION_checkSignatures, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int checkUidSignatures(int uid1, int uid2) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid1);
                    _data.writeInt(uid2);
                    this.mRemote.transact(Stub.TRANSACTION_checkUidSignatures, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getPackagesForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(Stub.TRANSACTION_getPackagesForUid, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getNameForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(Stub.TRANSACTION_getNameForUid, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getUidForSharedUser(String sharedUserName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sharedUserName);
                    this.mRemote.transact(Stub.TRANSACTION_getUidForSharedUser, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getFlagsForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(Stub.TRANSACTION_getFlagsForUid, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isUidPrivileged(int uid) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(Stub.TRANSACTION_isUidPrivileged, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getAppOpPermissionPackages(String permissionName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permissionName);
                    this.mRemote.transact(Stub.TRANSACTION_getAppOpPermissionPackages, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ResolveInfo resolveIntent(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ResolveInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_resolveIntent, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ResolveInfo) ResolveInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean canForwardTo(Intent intent, String resolvedType, int sourceUserId, int targetUserId) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(sourceUserId);
                    _data.writeInt(targetUserId);
                    this.mRemote.transact(Stub.TRANSACTION_canForwardTo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ResolveInfo> queryIntentActivities(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_queryIntentActivities, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, String[] specificTypes, Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (caller != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        caller.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeTypedArray(specifics, 0);
                    _data.writeStringArray(specificTypes);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_queryIntentActivityOptions, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ResolveInfo> queryIntentReceivers(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_queryIntentReceivers, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ResolveInfo resolveService(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ResolveInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_resolveService, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ResolveInfo) ResolveInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ResolveInfo> queryIntentServices(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_queryIntentServices, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ResolveInfo> queryIntentContentProviders(Intent intent, String resolvedType, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_queryIntentContentProviders, _data, _reply, 0);
                    _reply.readException();
                    List<ResolveInfo> _result = _reply.createTypedArrayList(ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParceledListSlice getInstalledPackages(int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ParceledListSlice _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getInstalledPackages, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParceledListSlice getPackagesHoldingPermissions(String[] permissions, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ParceledListSlice _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringArray(permissions);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getPackagesHoldingPermissions, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParceledListSlice getInstalledApplications(int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ParceledListSlice _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getInstalledApplications, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ParceledListSlice) ParceledListSlice.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ApplicationInfo> getPersistentApplications(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_getPersistentApplications, _data, _reply, 0);
                    _reply.readException();
                    List<ApplicationInfo> _result = _reply.createTypedArrayList(ApplicationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ProviderInfo resolveContentProvider(String name, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ProviderInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_resolveContentProvider, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ProviderInfo) ProviderInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void querySyncProviders(List<String> outNames, List<ProviderInfo> outInfo) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStringList(outNames);
                    _data.writeTypedList(outInfo);
                    this.mRemote.transact(Stub.TRANSACTION_querySyncProviders, _data, _reply, 0);
                    _reply.readException();
                    _reply.readStringList(outNames);
                    _reply.readTypedList(outInfo, ProviderInfo.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(processName);
                    _data.writeInt(uid);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_queryContentProviders, _data, _reply, 0);
                    _reply.readException();
                    List<ProviderInfo> _result = _reply.createTypedArrayList(ProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    InstrumentationInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (className != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        className.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_getInstrumentationInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (InstrumentationInfo) InstrumentationInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_queryInstrumentation, _data, _reply, 0);
                    _reply.readException();
                    List<InstrumentationInfo> _result = _reply.createTypedArrayList(InstrumentationInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void installPackage(String originPath, IPackageInstallObserver2 observer, int flags, String installerPackageName, VerificationParams verificationParams, String packageAbiOverride) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(originPath);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeString(installerPackageName);
                    if (verificationParams != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        verificationParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(packageAbiOverride);
                    this.mRemote.transact(Stub.TRANSACTION_installPackage, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void installPackageAsUser(String originPath, IPackageInstallObserver2 observer, int flags, String installerPackageName, VerificationParams verificationParams, String packageAbiOverride, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(originPath);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(flags);
                    _data.writeString(installerPackageName);
                    if (verificationParams != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        verificationParams.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(packageAbiOverride);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_installPackageAsUser, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void finishPackageInstall(int token) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(token);
                    this.mRemote.transact(Stub.TRANSACTION_finishPackageInstall, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setInstallerPackageName(String targetPackage, String installerPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(targetPackage);
                    _data.writeString(installerPackageName);
                    this.mRemote.transact(Stub.TRANSACTION_setInstallerPackageName, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void deletePackageAsUser(String packageName, IPackageDeleteObserver observer, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_deletePackageAsUser, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void deletePackage(String packageName, IPackageDeleteObserver2 observer, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_deletePackage, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getInstallerPackageName(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_getInstallerPackageName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addPackageToPreferred(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_addPackageToPreferred, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removePackageFromPreferred(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_removePackageFromPreferred, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<PackageInfo> getPreferredPackages(int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_getPreferredPackages, _data, _reply, 0);
                    _reply.readException();
                    List<PackageInfo> _result = _reply.createTypedArrayList(PackageInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void resetPreferredActivities(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_resetPreferredActivities, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ResolveInfo getLastChosenActivity(Intent intent, String resolvedType, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ResolveInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_getLastChosenActivity, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ResolveInfo) ResolveInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setLastChosenActivity(Intent intent, String resolvedType, int flags, IntentFilter filter, int match, ComponentName activity) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(resolvedType);
                    _data.writeInt(flags);
                    if (filter != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(match);
                    if (activity != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        activity.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setLastChosenActivity, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (filter != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(match);
                    _data.writeTypedArray(set, 0);
                    if (activity != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        activity.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_addPreferredActivity, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void replacePreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (filter != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(match);
                    _data.writeTypedArray(set, 0);
                    if (activity != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        activity.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_replacePreferredActivity, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearPackagePreferredActivities(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_clearPackagePreferredActivities, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_getPreferredActivities, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    _reply.readTypedList(outFilters, IntentFilter.CREATOR);
                    _reply.readTypedList(outActivities, ComponentName.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addPersistentPreferredActivity(IntentFilter filter, ComponentName activity, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (filter != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        filter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (activity != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        activity.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_addPersistentPreferredActivity, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearPackagePersistentPreferredActivities(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_clearPackagePersistentPreferredActivities, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addCrossProfileIntentFilter(IntentFilter intentFilter, String ownerPackage, int ownerUserId, int sourceUserId, int targetUserId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intentFilter != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        intentFilter.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(ownerPackage);
                    _data.writeInt(ownerUserId);
                    _data.writeInt(sourceUserId);
                    _data.writeInt(targetUserId);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_addCrossProfileIntentFilter, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearCrossProfileIntentFilters(int sourceUserId, String ownerPackage, int ownerUserId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sourceUserId);
                    _data.writeString(ownerPackage);
                    _data.writeInt(ownerUserId);
                    this.mRemote.transact(Stub.TRANSACTION_clearCrossProfileIntentFilters, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ComponentName getHomeActivities(List<ResolveInfo> outHomeCandidates) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ComponentName _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getHomeActivities, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ComponentName) ComponentName.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.readTypedList(outHomeCandidates, ResolveInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newState);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_setComponentEnabledSetting, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getComponentEnabledSetting(ComponentName componentName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        componentName.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getComponentEnabledSetting, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setApplicationEnabledSetting(String packageName, int newState, int flags, int userId, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(newState);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(Stub.TRANSACTION_setApplicationEnabledSetting, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getApplicationEnabledSetting(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getApplicationEnabledSetting, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setPackageStoppedState(String packageName, boolean stopped, int userId) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (stopped) {
                        i = Stub.TRANSACTION_isPackageAvailable;
                    }
                    _data.writeInt(i);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_setPackageStoppedState, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(freeStorageSize);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_freeStorageAndNotify, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void freeStorage(long freeStorageSize, IntentSender pi) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(freeStorageSize);
                    if (pi != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        pi.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_freeStorage, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void deleteApplicationCacheFiles(String packageName, IPackageDataObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_deleteApplicationCacheFiles, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearApplicationUserData(String packageName, IPackageDataObserver observer, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_clearApplicationUserData, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void getPackageSizeInfo(String packageName, int userHandle, IPackageStatsObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_getPackageSizeInfo, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getSystemSharedLibraryNames() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getSystemSharedLibraryNames, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public FeatureInfo[] getSystemAvailableFeatures() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getSystemAvailableFeatures, _data, _reply, 0);
                    _reply.readException();
                    FeatureInfo[] _result = (FeatureInfo[]) _reply.createTypedArray(FeatureInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean hasSystemFeature(String name) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(Stub.TRANSACTION_hasSystemFeature, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void enterSafeMode() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_enterSafeMode, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isSafeMode() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isSafeMode, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void systemReady() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_systemReady, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean hasSystemUidErrors() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_hasSystemUidErrors, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void performBootDexOpt() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_performBootDexOpt, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean performDexOptIfNeeded(String packageName, String instructionSet) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(instructionSet);
                    this.mRemote.transact(Stub.TRANSACTION_performDexOptIfNeeded, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void forceDexOpt(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_forceDexOpt, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void updateExternalMediaStatus(boolean mounted, boolean reportStatus) throws RemoteException {
                int i = Stub.TRANSACTION_isPackageAvailable;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mounted ? Stub.TRANSACTION_isPackageAvailable : 0);
                    if (!reportStatus) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_updateExternalMediaStatus, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public PackageCleanItem nextPackageToClean(PackageCleanItem lastPackage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    PackageCleanItem _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (lastPackage != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        lastPackage.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_nextPackageToClean, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (PackageCleanItem) PackageCleanItem.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void movePackage(String packageName, IPackageMoveObserver observer, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_movePackage, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean addPermissionAsync(PermissionInfo info) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_addPermissionAsync, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setInstallLocation(int loc) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(loc);
                    this.mRemote.transact(Stub.TRANSACTION_setInstallLocation, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getInstallLocation() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getInstallLocation, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int installExistingPackageAsUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_installExistingPackageAsUser, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void verifyPendingInstall(int id, int verificationCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(verificationCode);
                    this.mRemote.transact(Stub.TRANSACTION_verifyPendingInstall, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(verificationCodeAtTimeout);
                    _data.writeLong(millisecondsToDelay);
                    this.mRemote.transact(Stub.TRANSACTION_extendVerificationTimeout, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    VerifierDeviceIdentity _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getVerifierDeviceIdentity, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (VerifierDeviceIdentity) VerifierDeviceIdentity.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isFirstBoot() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isFirstBoot, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isOnlyCoreApps() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isOnlyCoreApps, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isUpgrade() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isUpgrade, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setPermissionEnforced(String permission, boolean enforced) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    if (enforced) {
                        i = Stub.TRANSACTION_isPackageAvailable;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_setPermissionEnforced, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isPermissionEnforced(String permission) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    this.mRemote.transact(Stub.TRANSACTION_isPermissionEnforced, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isStorageLow() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isStorageLow, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setApplicationHiddenSettingAsUser(String packageName, boolean hidden, int userId) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    int i;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (hidden) {
                        i = Stub.TRANSACTION_isPackageAvailable;
                    } else {
                        i = 0;
                    }
                    _data.writeInt(i);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_setApplicationHiddenSettingAsUser, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getApplicationHiddenSettingAsUser(String packageName, int userId) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getApplicationHiddenSettingAsUser, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public IPackageInstaller getPackageInstaller() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getPackageInstaller, _data, _reply, 0);
                    _reply.readException();
                    IPackageInstaller _result = android.content.pm.IPackageInstaller.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setBlockUninstallForUser(String packageName, boolean blockUninstall, int userId) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    int i;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (blockUninstall) {
                        i = Stub.TRANSACTION_isPackageAvailable;
                    } else {
                        i = 0;
                    }
                    _data.writeInt(i);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_setBlockUninstallForUser, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean getBlockUninstallForUser(String packageName, int userId) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getBlockUninstallForUser, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public KeySet getKeySetByAlias(String packageName, String alias) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    KeySet _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeString(alias);
                    this.mRemote.transact(Stub.TRANSACTION_getKeySetByAlias, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (KeySet) KeySet.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public KeySet getSigningKeySet(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    KeySet _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_getSigningKeySet, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (KeySet) KeySet.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isPackageSignedByKeySet(String packageName, KeySet ks) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (ks != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        ks.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_isPackageSignedByKeySet, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isPackageSignedByKeySetExactly(String packageName, KeySet ks) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (ks != null) {
                        _data.writeInt(Stub.TRANSACTION_isPackageAvailable);
                        ks.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_isPackageSignedByKeySetExactly, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPackageManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IPackageManager)) {
                return new Proxy(obj);
            }
            return (IPackageManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            boolean _result;
            int _result2;
            String[] _result3;
            ComponentName _arg0;
            ActivityInfo _result4;
            ProviderInfo _result5;
            PermissionInfo _arg02;
            String _result6;
            Intent _arg03;
            ResolveInfo _result7;
            List<ResolveInfo> _result8;
            ParceledListSlice _result9;
            String _arg04;
            IPackageInstallObserver2 _arg1;
            int _arg2;
            String _arg3;
            VerificationParams _arg4;
            IntentFilter _arg05;
            int _arg12;
            ComponentName[] _arg22;
            ComponentName _arg32;
            KeySet _result10;
            KeySet _arg13;
            switch (code) {
                case TRANSACTION_isPackageAvailable /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isPackageAvailable(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getPackageInfo /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    PackageInfo _result11 = getPackageInfo(data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result11 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result11.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getPackageUid /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getPackageUid(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_getPackageGids /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    int[] _result12 = getPackageGids(data.readString());
                    reply.writeNoException();
                    reply.writeIntArray(_result12);
                    return true;
                case TRANSACTION_currentToCanonicalPackageNames /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = currentToCanonicalPackageNames(data.createStringArray());
                    reply.writeNoException();
                    reply.writeStringArray(_result3);
                    return true;
                case TRANSACTION_canonicalToCurrentPackageNames /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = canonicalToCurrentPackageNames(data.createStringArray());
                    reply.writeNoException();
                    reply.writeStringArray(_result3);
                    return true;
                case TRANSACTION_getPermissionInfo /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    PermissionInfo _result13 = getPermissionInfo(data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result13 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result13.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_queryPermissionsByGroup /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<PermissionInfo> _result14 = queryPermissionsByGroup(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result14);
                    return true;
                case TRANSACTION_getPermissionGroupInfo /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    PermissionGroupInfo _result15 = getPermissionGroupInfo(data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result15 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result15.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getAllPermissionGroups /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<PermissionGroupInfo> _result16 = getAllPermissionGroups(data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result16);
                    return true;
                case TRANSACTION_getApplicationInfo /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    ApplicationInfo _result17 = getApplicationInfo(data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result17 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result17.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getActivityInfo /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result4 = getActivityInfo(_arg0, data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result4 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result4.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_activitySupportsIntent /*13*/:
                    Intent _arg14;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg14 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    _result = activitySupportsIntent(_arg0, _arg14, data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getReceiverInfo /*14*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result4 = getReceiverInfo(_arg0, data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result4 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result4.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getServiceInfo /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    ServiceInfo _result18 = getServiceInfo(_arg0, data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result18 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result18.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getProviderInfo /*16*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result5 = getProviderInfo(_arg0, data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result5.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_checkPermission /*17*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = checkPermission(data.readString(), data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_checkUidPermission /*18*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = checkUidPermission(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_addPermission /*19*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = (PermissionInfo) PermissionInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    _result = addPermission(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_removePermission /*20*/:
                    data.enforceInterface(DESCRIPTOR);
                    removePermission(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_grantPermission /*21*/:
                    data.enforceInterface(DESCRIPTOR);
                    grantPermission(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_revokePermission /*22*/:
                    data.enforceInterface(DESCRIPTOR);
                    revokePermission(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_isProtectedBroadcast /*23*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isProtectedBroadcast(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_checkSignatures /*24*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = checkSignatures(data.readString(), data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_checkUidSignatures /*25*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = checkUidSignatures(data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_getPackagesForUid /*26*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = getPackagesForUid(data.readInt());
                    reply.writeNoException();
                    reply.writeStringArray(_result3);
                    return true;
                case TRANSACTION_getNameForUid /*27*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result6 = getNameForUid(data.readInt());
                    reply.writeNoException();
                    reply.writeString(_result6);
                    return true;
                case TRANSACTION_getUidForSharedUser /*28*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getUidForSharedUser(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_getFlagsForUid /*29*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getFlagsForUid(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_isUidPrivileged /*30*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isUidPrivileged(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getAppOpPermissionPackages /*31*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = getAppOpPermissionPackages(data.readString());
                    reply.writeNoException();
                    reply.writeStringArray(_result3);
                    return true;
                case TRANSACTION_resolveIntent /*32*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result7 = resolveIntent(_arg03, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result7 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result7.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_canForwardTo /*33*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result = canForwardTo(_arg03, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_queryIntentActivities /*34*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result8 = queryIntentActivities(_arg03, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result8);
                    return true;
                case TRANSACTION_queryIntentActivityOptions /*35*/:
                    Intent _arg33;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Intent[] _arg15 = (Intent[]) data.createTypedArray(Intent.CREATOR);
                    String[] _arg23 = data.createStringArray();
                    if (data.readInt() != 0) {
                        _arg33 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg33 = null;
                    }
                    _result8 = queryIntentActivityOptions(_arg0, _arg15, _arg23, _arg33, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result8);
                    return true;
                case TRANSACTION_queryIntentReceivers /*36*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result8 = queryIntentReceivers(_arg03, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result8);
                    return true;
                case TRANSACTION_resolveService /*37*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result7 = resolveService(_arg03, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result7 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result7.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_queryIntentServices /*38*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result8 = queryIntentServices(_arg03, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result8);
                    return true;
                case TRANSACTION_queryIntentContentProviders /*39*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result8 = queryIntentContentProviders(_arg03, data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result8);
                    return true;
                case TRANSACTION_getInstalledPackages /*40*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result9 = getInstalledPackages(data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result9 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result9.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getPackagesHoldingPermissions /*41*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result9 = getPackagesHoldingPermissions(data.createStringArray(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result9 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result9.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getInstalledApplications /*42*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result9 = getInstalledApplications(data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result9 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result9.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getPersistentApplications /*43*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<ApplicationInfo> _result19 = getPersistentApplications(data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result19);
                    return true;
                case TRANSACTION_resolveContentProvider /*44*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = resolveContentProvider(data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result5.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_querySyncProviders /*45*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<String> _arg06 = data.createStringArrayList();
                    List<ProviderInfo> _arg16 = data.createTypedArrayList(ProviderInfo.CREATOR);
                    querySyncProviders(_arg06, _arg16);
                    reply.writeNoException();
                    reply.writeStringList(_arg06);
                    reply.writeTypedList(_arg16);
                    return true;
                case TRANSACTION_queryContentProviders /*46*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<ProviderInfo> _result20 = queryContentProviders(data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result20);
                    return true;
                case TRANSACTION_getInstrumentationInfo /*47*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    InstrumentationInfo _result21 = getInstrumentationInfo(_arg0, data.readInt());
                    reply.writeNoException();
                    if (_result21 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result21.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_queryInstrumentation /*48*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<InstrumentationInfo> _result22 = queryInstrumentation(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result22);
                    return true;
                case TRANSACTION_installPackage /*49*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg04 = data.readString();
                    _arg1 = android.content.pm.IPackageInstallObserver2.Stub.asInterface(data.readStrongBinder());
                    _arg2 = data.readInt();
                    _arg3 = data.readString();
                    if (data.readInt() != 0) {
                        _arg4 = (VerificationParams) VerificationParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    installPackage(_arg04, _arg1, _arg2, _arg3, _arg4, data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_installPackageAsUser /*50*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg04 = data.readString();
                    _arg1 = android.content.pm.IPackageInstallObserver2.Stub.asInterface(data.readStrongBinder());
                    _arg2 = data.readInt();
                    _arg3 = data.readString();
                    if (data.readInt() != 0) {
                        _arg4 = (VerificationParams) VerificationParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg4 = null;
                    }
                    installPackageAsUser(_arg04, _arg1, _arg2, _arg3, _arg4, data.readString(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_finishPackageInstall /*51*/:
                    data.enforceInterface(DESCRIPTOR);
                    finishPackageInstall(data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setInstallerPackageName /*52*/:
                    data.enforceInterface(DESCRIPTOR);
                    setInstallerPackageName(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_deletePackageAsUser /*53*/:
                    data.enforceInterface(DESCRIPTOR);
                    deletePackageAsUser(data.readString(), android.content.pm.IPackageDeleteObserver.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_deletePackage /*54*/:
                    data.enforceInterface(DESCRIPTOR);
                    deletePackage(data.readString(), android.content.pm.IPackageDeleteObserver2.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getInstallerPackageName /*55*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result6 = getInstallerPackageName(data.readString());
                    reply.writeNoException();
                    reply.writeString(_result6);
                    return true;
                case TRANSACTION_addPackageToPreferred /*56*/:
                    data.enforceInterface(DESCRIPTOR);
                    addPackageToPreferred(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_removePackageFromPreferred /*57*/:
                    data.enforceInterface(DESCRIPTOR);
                    removePackageFromPreferred(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getPreferredPackages /*58*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<PackageInfo> _result23 = getPreferredPackages(data.readInt());
                    reply.writeNoException();
                    reply.writeTypedList(_result23);
                    return true;
                case TRANSACTION_resetPreferredActivities /*59*/:
                    data.enforceInterface(DESCRIPTOR);
                    resetPreferredActivities(data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getLastChosenActivity /*60*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result7 = getLastChosenActivity(_arg03, data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result7 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result7.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_setLastChosenActivity /*61*/:
                    IntentFilter _arg34;
                    ComponentName _arg5;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    String _arg17 = data.readString();
                    _arg2 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg34 = (IntentFilter) IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg34 = null;
                    }
                    int _arg42 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg5 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    setLastChosenActivity(_arg03, _arg17, _arg2, _arg34, _arg42, _arg5);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_addPreferredActivity /*62*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (IntentFilter) IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    _arg12 = data.readInt();
                    _arg22 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                    if (data.readInt() != 0) {
                        _arg32 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg32 = null;
                    }
                    addPreferredActivity(_arg05, _arg12, _arg22, _arg32, data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_replacePreferredActivity /*63*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (IntentFilter) IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    _arg12 = data.readInt();
                    _arg22 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                    if (data.readInt() != 0) {
                        _arg32 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg32 = null;
                    }
                    replacePreferredActivity(_arg05, _arg12, _arg22, _arg32, data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_clearPackagePreferredActivities /*64*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearPackagePreferredActivities(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getPreferredActivities /*65*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<IntentFilter> _arg07 = new ArrayList();
                    List<ComponentName> _arg18 = new ArrayList();
                    _result2 = getPreferredActivities(_arg07, _arg18, data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    reply.writeTypedList(_arg07);
                    reply.writeTypedList(_arg18);
                    return true;
                case TRANSACTION_addPersistentPreferredActivity /*66*/:
                    ComponentName _arg19;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (IntentFilter) IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg19 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg19 = null;
                    }
                    addPersistentPreferredActivity(_arg05, _arg19, data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_clearPackagePersistentPreferredActivities /*67*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearPackagePersistentPreferredActivities(data.readString(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_addCrossProfileIntentFilter /*68*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (IntentFilter) IntentFilter.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    addCrossProfileIntentFilter(_arg05, data.readString(), data.readInt(), data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_clearCrossProfileIntentFilters /*69*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearCrossProfileIntentFilters(data.readInt(), data.readString(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getHomeActivities /*70*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<ResolveInfo> _arg08 = new ArrayList();
                    ComponentName _result24 = getHomeActivities(_arg08);
                    reply.writeNoException();
                    if (_result24 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result24.writeToParcel(reply, (int) TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    reply.writeTypedList(_arg08);
                    return true;
                case TRANSACTION_setComponentEnabledSetting /*71*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    setComponentEnabledSetting(_arg0, data.readInt(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getComponentEnabledSetting /*72*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result2 = getComponentEnabledSetting(_arg0, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_setApplicationEnabledSetting /*73*/:
                    data.enforceInterface(DESCRIPTOR);
                    setApplicationEnabledSetting(data.readString(), data.readInt(), data.readInt(), data.readInt(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getApplicationEnabledSetting /*74*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getApplicationEnabledSetting(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_setPackageStoppedState /*75*/:
                    data.enforceInterface(DESCRIPTOR);
                    setPackageStoppedState(data.readString(), data.readInt() != 0, data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_freeStorageAndNotify /*76*/:
                    data.enforceInterface(DESCRIPTOR);
                    freeStorageAndNotify(data.readLong(), android.content.pm.IPackageDataObserver.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_freeStorage /*77*/:
                    IntentSender _arg110;
                    data.enforceInterface(DESCRIPTOR);
                    long _arg09 = data.readLong();
                    if (data.readInt() != 0) {
                        _arg110 = (IntentSender) IntentSender.CREATOR.createFromParcel(data);
                    } else {
                        _arg110 = null;
                    }
                    freeStorage(_arg09, _arg110);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_deleteApplicationCacheFiles /*78*/:
                    data.enforceInterface(DESCRIPTOR);
                    deleteApplicationCacheFiles(data.readString(), android.content.pm.IPackageDataObserver.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_clearApplicationUserData /*79*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearApplicationUserData(data.readString(), android.content.pm.IPackageDataObserver.Stub.asInterface(data.readStrongBinder()), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getPackageSizeInfo /*80*/:
                    data.enforceInterface(DESCRIPTOR);
                    getPackageSizeInfo(data.readString(), data.readInt(), android.content.pm.IPackageStatsObserver.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getSystemSharedLibraryNames /*81*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = getSystemSharedLibraryNames();
                    reply.writeNoException();
                    reply.writeStringArray(_result3);
                    return true;
                case TRANSACTION_getSystemAvailableFeatures /*82*/:
                    data.enforceInterface(DESCRIPTOR);
                    FeatureInfo[] _result25 = getSystemAvailableFeatures();
                    reply.writeNoException();
                    reply.writeTypedArray(_result25, TRANSACTION_isPackageAvailable);
                    return true;
                case TRANSACTION_hasSystemFeature /*83*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = hasSystemFeature(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_enterSafeMode /*84*/:
                    data.enforceInterface(DESCRIPTOR);
                    enterSafeMode();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_isSafeMode /*85*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isSafeMode();
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_systemReady /*86*/:
                    data.enforceInterface(DESCRIPTOR);
                    systemReady();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_hasSystemUidErrors /*87*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = hasSystemUidErrors();
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_performBootDexOpt /*88*/:
                    data.enforceInterface(DESCRIPTOR);
                    performBootDexOpt();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_performDexOptIfNeeded /*89*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = performDexOptIfNeeded(data.readString(), data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_forceDexOpt /*90*/:
                    data.enforceInterface(DESCRIPTOR);
                    forceDexOpt(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_updateExternalMediaStatus /*91*/:
                    data.enforceInterface(DESCRIPTOR);
                    updateExternalMediaStatus(data.readInt() != 0, data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_nextPackageToClean /*92*/:
                    PackageCleanItem _arg010;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg010 = (PackageCleanItem) PackageCleanItem.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    PackageCleanItem _result26 = nextPackageToClean(_arg010);
                    reply.writeNoException();
                    if (_result26 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result26.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_movePackage /*93*/:
                    data.enforceInterface(DESCRIPTOR);
                    movePackage(data.readString(), android.content.pm.IPackageMoveObserver.Stub.asInterface(data.readStrongBinder()), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_addPermissionAsync /*94*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = (PermissionInfo) PermissionInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    _result = addPermissionAsync(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_setInstallLocation /*95*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = setInstallLocation(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getInstallLocation /*96*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getInstallLocation();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_installExistingPackageAsUser /*97*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = installExistingPackageAsUser(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_verifyPendingInstall /*98*/:
                    data.enforceInterface(DESCRIPTOR);
                    verifyPendingInstall(data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_extendVerificationTimeout /*99*/:
                    data.enforceInterface(DESCRIPTOR);
                    extendVerificationTimeout(data.readInt(), data.readInt(), data.readLong());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getVerifierDeviceIdentity /*100*/:
                    data.enforceInterface(DESCRIPTOR);
                    VerifierDeviceIdentity _result27 = getVerifierDeviceIdentity();
                    reply.writeNoException();
                    if (_result27 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result27.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_isFirstBoot /*101*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isFirstBoot();
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_isOnlyCoreApps /*102*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isOnlyCoreApps();
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_isUpgrade /*103*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isUpgrade();
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_setPermissionEnforced /*104*/:
                    data.enforceInterface(DESCRIPTOR);
                    setPermissionEnforced(data.readString(), data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_isPermissionEnforced /*105*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isPermissionEnforced(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_isStorageLow /*106*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isStorageLow();
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_setApplicationHiddenSettingAsUser /*107*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = setApplicationHiddenSettingAsUser(data.readString(), data.readInt() != 0, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getApplicationHiddenSettingAsUser /*108*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getApplicationHiddenSettingAsUser(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getPackageInstaller /*109*/:
                    data.enforceInterface(DESCRIPTOR);
                    IPackageInstaller _result28 = getPackageInstaller();
                    reply.writeNoException();
                    reply.writeStrongBinder(_result28 != null ? _result28.asBinder() : null);
                    return true;
                case TRANSACTION_setBlockUninstallForUser /*110*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = setBlockUninstallForUser(data.readString(), data.readInt() != 0, data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getBlockUninstallForUser /*111*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getBlockUninstallForUser(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_getKeySetByAlias /*112*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result10 = getKeySetByAlias(data.readString(), data.readString());
                    reply.writeNoException();
                    if (_result10 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result10.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getSigningKeySet /*113*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result10 = getSigningKeySet(data.readString());
                    reply.writeNoException();
                    if (_result10 != null) {
                        reply.writeInt(TRANSACTION_isPackageAvailable);
                        _result10.writeToParcel(reply, TRANSACTION_isPackageAvailable);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_isPackageSignedByKeySet /*114*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg04 = data.readString();
                    if (data.readInt() != 0) {
                        _arg13 = (KeySet) KeySet.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    _result = isPackageSignedByKeySet(_arg04, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case TRANSACTION_isPackageSignedByKeySetExactly /*115*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg04 = data.readString();
                    if (data.readInt() != 0) {
                        _arg13 = (KeySet) KeySet.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    _result = isPackageSignedByKeySetExactly(_arg04, _arg13);
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_isPackageAvailable : 0);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /*1598968902*/:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    boolean activitySupportsIntent(ComponentName componentName, Intent intent, String str) throws RemoteException;

    void addCrossProfileIntentFilter(IntentFilter intentFilter, String str, int i, int i2, int i3, int i4) throws RemoteException;

    void addPackageToPreferred(String str) throws RemoteException;

    boolean addPermission(PermissionInfo permissionInfo) throws RemoteException;

    boolean addPermissionAsync(PermissionInfo permissionInfo) throws RemoteException;

    void addPersistentPreferredActivity(IntentFilter intentFilter, ComponentName componentName, int i) throws RemoteException;

    void addPreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2) throws RemoteException;

    boolean canForwardTo(Intent intent, String str, int i, int i2) throws RemoteException;

    String[] canonicalToCurrentPackageNames(String[] strArr) throws RemoteException;

    int checkPermission(String str, String str2) throws RemoteException;

    int checkSignatures(String str, String str2) throws RemoteException;

    int checkUidPermission(String str, int i) throws RemoteException;

    int checkUidSignatures(int i, int i2) throws RemoteException;

    void clearApplicationUserData(String str, IPackageDataObserver iPackageDataObserver, int i) throws RemoteException;

    void clearCrossProfileIntentFilters(int i, String str, int i2) throws RemoteException;

    void clearPackagePersistentPreferredActivities(String str, int i) throws RemoteException;

    void clearPackagePreferredActivities(String str) throws RemoteException;

    String[] currentToCanonicalPackageNames(String[] strArr) throws RemoteException;

    void deleteApplicationCacheFiles(String str, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    void deletePackage(String str, IPackageDeleteObserver2 iPackageDeleteObserver2, int i, int i2) throws RemoteException;

    void deletePackageAsUser(String str, IPackageDeleteObserver iPackageDeleteObserver, int i, int i2) throws RemoteException;

    void enterSafeMode() throws RemoteException;

    void extendVerificationTimeout(int i, int i2, long j) throws RemoteException;

    void finishPackageInstall(int i) throws RemoteException;

    void forceDexOpt(String str) throws RemoteException;

    void freeStorage(long j, IntentSender intentSender) throws RemoteException;

    void freeStorageAndNotify(long j, IPackageDataObserver iPackageDataObserver) throws RemoteException;

    ActivityInfo getActivityInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    List<PermissionGroupInfo> getAllPermissionGroups(int i) throws RemoteException;

    String[] getAppOpPermissionPackages(String str) throws RemoteException;

    int getApplicationEnabledSetting(String str, int i) throws RemoteException;

    boolean getApplicationHiddenSettingAsUser(String str, int i) throws RemoteException;

    ApplicationInfo getApplicationInfo(String str, int i, int i2) throws RemoteException;

    boolean getBlockUninstallForUser(String str, int i) throws RemoteException;

    int getComponentEnabledSetting(ComponentName componentName, int i) throws RemoteException;

    int getFlagsForUid(int i) throws RemoteException;

    ComponentName getHomeActivities(List<ResolveInfo> list) throws RemoteException;

    int getInstallLocation() throws RemoteException;

    ParceledListSlice getInstalledApplications(int i, int i2) throws RemoteException;

    ParceledListSlice getInstalledPackages(int i, int i2) throws RemoteException;

    String getInstallerPackageName(String str) throws RemoteException;

    InstrumentationInfo getInstrumentationInfo(ComponentName componentName, int i) throws RemoteException;

    KeySet getKeySetByAlias(String str, String str2) throws RemoteException;

    ResolveInfo getLastChosenActivity(Intent intent, String str, int i) throws RemoteException;

    String getNameForUid(int i) throws RemoteException;

    int[] getPackageGids(String str) throws RemoteException;

    PackageInfo getPackageInfo(String str, int i, int i2) throws RemoteException;

    IPackageInstaller getPackageInstaller() throws RemoteException;

    void getPackageSizeInfo(String str, int i, IPackageStatsObserver iPackageStatsObserver) throws RemoteException;

    int getPackageUid(String str, int i) throws RemoteException;

    String[] getPackagesForUid(int i) throws RemoteException;

    ParceledListSlice getPackagesHoldingPermissions(String[] strArr, int i, int i2) throws RemoteException;

    PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws RemoteException;

    PermissionInfo getPermissionInfo(String str, int i) throws RemoteException;

    List<ApplicationInfo> getPersistentApplications(int i) throws RemoteException;

    int getPreferredActivities(List<IntentFilter> list, List<ComponentName> list2, String str) throws RemoteException;

    List<PackageInfo> getPreferredPackages(int i) throws RemoteException;

    ProviderInfo getProviderInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    ActivityInfo getReceiverInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    ServiceInfo getServiceInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    KeySet getSigningKeySet(String str) throws RemoteException;

    FeatureInfo[] getSystemAvailableFeatures() throws RemoteException;

    String[] getSystemSharedLibraryNames() throws RemoteException;

    int getUidForSharedUser(String str) throws RemoteException;

    VerifierDeviceIdentity getVerifierDeviceIdentity() throws RemoteException;

    void grantPermission(String str, String str2) throws RemoteException;

    boolean hasSystemFeature(String str) throws RemoteException;

    boolean hasSystemUidErrors() throws RemoteException;

    int installExistingPackageAsUser(String str, int i) throws RemoteException;

    void installPackage(String str, IPackageInstallObserver2 iPackageInstallObserver2, int i, String str2, VerificationParams verificationParams, String str3) throws RemoteException;

    void installPackageAsUser(String str, IPackageInstallObserver2 iPackageInstallObserver2, int i, String str2, VerificationParams verificationParams, String str3, int i2) throws RemoteException;

    boolean isFirstBoot() throws RemoteException;

    boolean isOnlyCoreApps() throws RemoteException;

    boolean isPackageAvailable(String str, int i) throws RemoteException;

    boolean isPackageSignedByKeySet(String str, KeySet keySet) throws RemoteException;

    boolean isPackageSignedByKeySetExactly(String str, KeySet keySet) throws RemoteException;

    boolean isPermissionEnforced(String str) throws RemoteException;

    boolean isProtectedBroadcast(String str) throws RemoteException;

    boolean isSafeMode() throws RemoteException;

    boolean isStorageLow() throws RemoteException;

    boolean isUidPrivileged(int i) throws RemoteException;

    boolean isUpgrade() throws RemoteException;

    void movePackage(String str, IPackageMoveObserver iPackageMoveObserver, int i) throws RemoteException;

    PackageCleanItem nextPackageToClean(PackageCleanItem packageCleanItem) throws RemoteException;

    void performBootDexOpt() throws RemoteException;

    boolean performDexOptIfNeeded(String str, String str2) throws RemoteException;

    List<ProviderInfo> queryContentProviders(String str, int i, int i2) throws RemoteException;

    List<InstrumentationInfo> queryInstrumentation(String str, int i) throws RemoteException;

    List<ResolveInfo> queryIntentActivities(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentActivityOptions(ComponentName componentName, Intent[] intentArr, String[] strArr, Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentContentProviders(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentReceivers(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentServices(Intent intent, String str, int i, int i2) throws RemoteException;

    List<PermissionInfo> queryPermissionsByGroup(String str, int i) throws RemoteException;

    void querySyncProviders(List<String> list, List<ProviderInfo> list2) throws RemoteException;

    void removePackageFromPreferred(String str) throws RemoteException;

    void removePermission(String str) throws RemoteException;

    void replacePreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNameArr, ComponentName componentName, int i2) throws RemoteException;

    void resetPreferredActivities(int i) throws RemoteException;

    ProviderInfo resolveContentProvider(String str, int i, int i2) throws RemoteException;

    ResolveInfo resolveIntent(Intent intent, String str, int i, int i2) throws RemoteException;

    ResolveInfo resolveService(Intent intent, String str, int i, int i2) throws RemoteException;

    void revokePermission(String str, String str2) throws RemoteException;

    void setApplicationEnabledSetting(String str, int i, int i2, int i3, String str2) throws RemoteException;

    boolean setApplicationHiddenSettingAsUser(String str, boolean z, int i) throws RemoteException;

    boolean setBlockUninstallForUser(String str, boolean z, int i) throws RemoteException;

    void setComponentEnabledSetting(ComponentName componentName, int i, int i2, int i3) throws RemoteException;

    boolean setInstallLocation(int i) throws RemoteException;

    void setInstallerPackageName(String str, String str2) throws RemoteException;

    void setLastChosenActivity(Intent intent, String str, int i, IntentFilter intentFilter, int i2, ComponentName componentName) throws RemoteException;

    void setPackageStoppedState(String str, boolean z, int i) throws RemoteException;

    void setPermissionEnforced(String str, boolean z) throws RemoteException;

    void systemReady() throws RemoteException;

    void updateExternalMediaStatus(boolean z, boolean z2) throws RemoteException;

    void verifyPendingInstall(int i, int i2) throws RemoteException;
}
