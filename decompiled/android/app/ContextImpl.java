package android.app;

import android.R;
import android.accounts.AccountManager;
import android.accounts.IAccountManager.Stub;
import android.app.admin.DevicePolicyManager;
import android.app.job.IJobScheduler;
import android.app.trust.TrustManager;
import android.app.usage.IUsageStatsManager;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.IRestrictionsManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.RestrictionsManager;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ILauncherApps;
import android.content.pm.IPackageManager;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.ConsumerIrManager;
import android.hardware.ISerialManager;
import android.hardware.SerialManager;
import android.hardware.SystemSensorManager;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.input.InputManager;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbManager;
import android.location.CountryDetector;
import android.location.ICountryDetector;
import android.location.ILocationManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.media.projection.MediaProjectionManager;
import android.media.session.MediaSessionManager;
import android.media.tv.ITvInputManager;
import android.media.tv.TvInputManager;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IConnectivityManager;
import android.net.IEthernetManager;
import android.net.INetworkPolicyManager;
import android.net.NetworkPolicyManager;
import android.net.NetworkScoreManager;
import android.net.ProxyInfo;
import android.net.Uri;
import android.net.http.Headers;
import android.net.nsd.INsdManager;
import android.net.nsd.NsdManager;
import android.net.wifi.IRttManager;
import android.net.wifi.IWifiManager;
import android.net.wifi.IWifiScanner;
import android.net.wifi.RttManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiScanner;
import android.net.wifi.p2p.IWifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.IUserManager;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemVibrator;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.print.IPrintManager;
import android.print.PrintManager;
import android.provider.BrowserContract.Searches;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.Settings.System;
import android.service.fingerprint.FingerprintManager;
import android.service.fingerprint.IFingerprintService;
import android.service.notification.ZenModeConfig;
import android.service.persistentdata.IPersistentDataBlockService;
import android.service.persistentdata.PersistentDataBlockManager;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerImpl;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IAppOpsService;
import com.android.internal.appwidget.IAppWidgetService;
import com.android.internal.os.IDropBoxManagerService;
import com.android.internal.policy.PolicyManager;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

class ContextImpl extends Context {
    private static final boolean DEBUG = false;
    private static final String[] EMPTY_FILE_LIST;
    private static final HashMap<String, ServiceFetcher> SYSTEM_SERVICE_MAP;
    private static final String TAG = "ContextImpl";
    private static ServiceFetcher WALLPAPER_FETCHER;
    private static int sNextPerContextServiceCacheIndex;
    private static ArrayMap<String, ArrayMap<String, SharedPreferencesImpl>> sSharedPrefs;
    private final IBinder mActivityToken;
    private final String mBasePackageName;
    @GuardedBy("mSync")
    private File mCacheDir;
    @GuardedBy("mSync")
    private File mCodeCacheDir;
    private final ApplicationContentResolver mContentResolver;
    @GuardedBy("mSync")
    private File mDatabasesDir;
    private final Display mDisplay;
    private final DisplayAdjustments mDisplayAdjustments;
    @GuardedBy("mSync")
    private File[] mExternalCacheDirs;
    @GuardedBy("mSync")
    private File[] mExternalFilesDirs;
    @GuardedBy("mSync")
    private File[] mExternalMediaDirs;
    @GuardedBy("mSync")
    private File[] mExternalObbDirs;
    @GuardedBy("mSync")
    private File mFilesDir;
    final ActivityThread mMainThread;
    @GuardedBy("mSync")
    private File mNoBackupFilesDir;
    private final String mOpPackageName;
    private Context mOuterContext;
    private final Configuration mOverrideConfiguration;
    final LoadedApk mPackageInfo;
    private PackageManager mPackageManager;
    @GuardedBy("mSync")
    private File mPreferencesDir;
    private Context mReceiverRestrictedContext;
    private final Resources mResources;
    private final ResourcesManager mResourcesManager;
    private final boolean mRestricted;
    final ArrayList<Object> mServiceCache;
    private final Object mSync;
    private Theme mTheme;
    private int mThemeResource;
    private final UserHandle mUser;

    static class ServiceFetcher {
        int mContextCacheIndex;

        ServiceFetcher() {
            this.mContextCacheIndex = -1;
        }

        public Object getService(ContextImpl ctx) {
            ArrayList<Object> cache = ctx.mServiceCache;
            synchronized (cache) {
                Object service;
                if (cache.size() == 0) {
                    for (int i = 0; i < ContextImpl.sNextPerContextServiceCacheIndex; i++) {
                        cache.add(null);
                    }
                } else {
                    service = cache.get(this.mContextCacheIndex);
                    if (service != null) {
                        return service;
                    }
                }
                service = createService(ctx);
                cache.set(this.mContextCacheIndex, service);
                return service;
            }
        }

        public Object createService(ContextImpl ctx) {
            throw new RuntimeException("Not implemented");
        }
    }

    static abstract class StaticServiceFetcher extends ServiceFetcher {
        private Object mCachedInstance;

        public abstract Object createStaticService();

        StaticServiceFetcher() {
        }

        public final Object getService(ContextImpl unused) {
            Object service;
            synchronized (this) {
                service = this.mCachedInstance;
                if (service != null) {
                } else {
                    service = createStaticService();
                    this.mCachedInstance = service;
                }
            }
            return service;
        }
    }

    private static final class ApplicationContentResolver extends ContentResolver {
        private final ActivityThread mMainThread;
        private final UserHandle mUser;

        public ApplicationContentResolver(Context context, ActivityThread mainThread, UserHandle user) {
            super(context);
            this.mMainThread = (ActivityThread) Preconditions.checkNotNull(mainThread);
            this.mUser = (UserHandle) Preconditions.checkNotNull(user);
        }

        protected IContentProvider acquireProvider(Context context, String auth) {
            return this.mMainThread.acquireProvider(context, ContentProvider.getAuthorityWithoutUserId(auth), resolveUserIdFromAuthority(auth), true);
        }

        protected IContentProvider acquireExistingProvider(Context context, String auth) {
            return this.mMainThread.acquireExistingProvider(context, ContentProvider.getAuthorityWithoutUserId(auth), resolveUserIdFromAuthority(auth), true);
        }

        public boolean releaseProvider(IContentProvider provider) {
            return this.mMainThread.releaseProvider(provider, true);
        }

        protected IContentProvider acquireUnstableProvider(Context c, String auth) {
            return this.mMainThread.acquireProvider(c, ContentProvider.getAuthorityWithoutUserId(auth), resolveUserIdFromAuthority(auth), ContextImpl.DEBUG);
        }

        public boolean releaseUnstableProvider(IContentProvider icp) {
            return this.mMainThread.releaseProvider(icp, ContextImpl.DEBUG);
        }

        public void unstableProviderDied(IContentProvider icp) {
            this.mMainThread.handleUnstableProviderDied(icp.asBinder(), true);
        }

        public void appNotRespondingViaProvider(IContentProvider icp) {
            this.mMainThread.appNotRespondingViaProvider(icp.asBinder());
        }

        protected int resolveUserIdFromAuthority(String auth) {
            return ContentProvider.getUserIdFromAuthority(auth, this.mUser.getIdentifier());
        }
    }

    static {
        EMPTY_FILE_LIST = new String[0];
        SYSTEM_SERVICE_MAP = new HashMap();
        sNextPerContextServiceCacheIndex = 0;
        WALLPAPER_FETCHER = new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new WallpaperManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        };
        registerService(Context.ACCESSIBILITY_SERVICE, new ServiceFetcher() {
            public Object getService(ContextImpl ctx) {
                return AccessibilityManager.getInstance(ctx);
            }
        });
        registerService(Context.CAPTIONING_SERVICE, new ServiceFetcher() {
            public Object getService(ContextImpl ctx) {
                return new CaptioningManager(ctx);
            }
        });
        registerService(ContentResolver.SYNC_EXTRAS_ACCOUNT, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new AccountManager(ctx, Stub.asInterface(ServiceManager.getService(ContentResolver.SYNC_EXTRAS_ACCOUNT)));
            }
        });
        registerService(Context.ACTIVITY_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new ActivityManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Notification.CATEGORY_ALARM, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new AlarmManager(IAlarmManager.Stub.asInterface(ServiceManager.getService(Notification.CATEGORY_ALARM)), ctx);
            }
        });
        registerService(Context.AUDIO_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new AudioManager(ctx);
            }
        });
        registerService(Context.MEDIA_ROUTER_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new MediaRouter(ctx);
            }
        });
        registerService(System.RADIO_BLUETOOTH, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new BluetoothManager(ctx);
            }
        });
        registerService(Context.HDMI_CONTROL_SERVICE, new StaticServiceFetcher() {
            public Object createStaticService() {
                return new HdmiControlManager(IHdmiControlService.Stub.asInterface(ServiceManager.getService(Context.HDMI_CONTROL_SERVICE)));
            }
        });
        registerService(Context.CLIPBOARD_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new ClipboardManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.CONNECTIVITY_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new ConnectivityManager(IConnectivityManager.Stub.asInterface(ServiceManager.getService(Context.CONNECTIVITY_SERVICE)));
            }
        });
        registerService(Context.COUNTRY_DETECTOR, new StaticServiceFetcher() {
            public Object createStaticService() {
                return new CountryDetector(ICountryDetector.Stub.asInterface(ServiceManager.getService(Context.COUNTRY_DETECTOR)));
            }
        });
        registerService(Context.DEVICE_POLICY_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return DevicePolicyManager.create(ctx, ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.DOWNLOAD_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new DownloadManager(ctx.getContentResolver(), ctx.getPackageName());
            }
        });
        registerService(Context.BATTERY_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new BatteryManager();
            }
        });
        registerService(System.RADIO_NFC, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new NfcManager(ctx);
            }
        });
        registerService(Context.DROPBOX_SERVICE, new StaticServiceFetcher() {
            public Object createStaticService() {
                return ContextImpl.createDropBoxManager();
            }
        });
        registerService(Context.INPUT_SERVICE, new StaticServiceFetcher() {
            public Object createStaticService() {
                return InputManager.getInstance();
            }
        });
        registerService(Context.DISPLAY_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new DisplayManager(ctx.getOuterContext());
            }
        });
        registerService(Context.INPUT_METHOD_SERVICE, new StaticServiceFetcher() {
            public Object createStaticService() {
                return InputMethodManager.getInstance();
            }
        });
        registerService(Context.TEXT_SERVICES_MANAGER_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return TextServicesManager.getInstance();
            }
        });
        registerService(Context.KEYGUARD_SERVICE, new ServiceFetcher() {
            public Object getService(ContextImpl ctx) {
                return new KeyguardManager();
            }
        });
        registerService(Context.LAYOUT_INFLATER_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return PolicyManager.makeNewLayoutInflater(ctx.getOuterContext());
            }
        });
        registerService(Headers.LOCATION, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new LocationManager(ctx, ILocationManager.Stub.asInterface(ServiceManager.getService(Headers.LOCATION)));
            }
        });
        registerService(Context.NETWORK_POLICY_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new NetworkPolicyManager(INetworkPolicyManager.Stub.asInterface(ServiceManager.getService(Context.NETWORK_POLICY_SERVICE)));
            }
        });
        registerService(Context.NOTIFICATION_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                Context outerContext = ctx.getOuterContext();
                return new NotificationManager(new ContextThemeWrapper(outerContext, Resources.selectSystemTheme(0, outerContext.getApplicationInfo().targetSdkVersion, R.style.Theme_Dialog, R.style.Theme_Holo_Dialog, R.style.Theme_DeviceDefault_Dialog, R.style.Theme_DeviceDefault_Light_Dialog)), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.NSD_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new NsdManager(ctx.getOuterContext(), INsdManager.Stub.asInterface(ServiceManager.getService(Context.NSD_SERVICE)));
            }
        });
        registerService(Context.POWER_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                IPowerManager service = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
                if (service == null) {
                    Log.wtf(ContextImpl.TAG, "Failed to get power manager service.");
                }
                return new PowerManager(ctx.getOuterContext(), service, ctx.mMainThread.getHandler());
            }
        });
        registerService(Searches.SEARCH, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new SearchManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.SENSOR_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new SystemSensorManager(ctx.getOuterContext(), ctx.mMainThread.getHandler().getLooper());
            }
        });
        registerService(Context.STATUS_BAR_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new StatusBarManager(ctx.getOuterContext());
            }
        });
        registerService(Context.STORAGE_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                try {
                    return new StorageManager(ctx.getContentResolver(), ctx.mMainThread.getHandler().getLooper());
                } catch (RemoteException rex) {
                    Log.e(ContextImpl.TAG, "Failed to create StorageManager", rex);
                    return null;
                }
            }
        });
        registerService(Insert.PHONE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new TelephonyManager(ctx.getOuterContext());
            }
        });
        registerService(Context.TELEPHONY_SUBSCRIPTION_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new SubscriptionManager(ctx.getOuterContext());
            }
        });
        registerService(Context.TELECOM_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new TelecomManager(ctx.getOuterContext());
            }
        });
        registerService(Context.UI_MODE_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new UiModeManager();
            }
        });
        registerService(Context.USB_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new UsbManager(ctx, IUsbManager.Stub.asInterface(ServiceManager.getService(Context.USB_SERVICE)));
            }
        });
        registerService(Context.SERIAL_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new SerialManager(ctx, ISerialManager.Stub.asInterface(ServiceManager.getService(Context.SERIAL_SERVICE)));
            }
        });
        registerService(Context.VIBRATOR_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new SystemVibrator(ctx);
            }
        });
        registerService(Context.WALLPAPER_SERVICE, WALLPAPER_FETCHER);
        registerService(System.RADIO_WIFI, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new WifiManager(ctx.getOuterContext(), IWifiManager.Stub.asInterface(ServiceManager.getService(System.RADIO_WIFI)));
            }
        });
        registerService(Context.WIFI_P2P_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new WifiP2pManager(IWifiP2pManager.Stub.asInterface(ServiceManager.getService(Context.WIFI_P2P_SERVICE)));
            }
        });
        registerService(Context.WIFI_SCANNING_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new WifiScanner(ctx.getOuterContext(), IWifiScanner.Stub.asInterface(ServiceManager.getService(Context.WIFI_SCANNING_SERVICE)));
            }
        });
        registerService(Context.WIFI_RTT_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new RttManager(ctx.getOuterContext(), IRttManager.Stub.asInterface(ServiceManager.getService(Context.WIFI_RTT_SERVICE)));
            }
        });
        registerService(Context.ETHERNET_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new EthernetManager(ctx.getOuterContext(), IEthernetManager.Stub.asInterface(ServiceManager.getService(Context.ETHERNET_SERVICE)));
            }
        });
        registerService(Context.WINDOW_SERVICE, new ServiceFetcher() {
            Display mDefaultDisplay;

            public Object getService(ContextImpl ctx) {
                Display display = ctx.mDisplay;
                if (display == null) {
                    if (this.mDefaultDisplay == null) {
                        this.mDefaultDisplay = ((DisplayManager) ctx.getOuterContext().getSystemService(Context.DISPLAY_SERVICE)).getDisplay(0);
                    }
                    display = this.mDefaultDisplay;
                }
                return new WindowManagerImpl(display);
            }
        });
        registerService(Context.USER_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new UserManager(ctx, IUserManager.Stub.asInterface(ServiceManager.getService(Context.USER_SERVICE)));
            }
        });
        registerService(Context.APP_OPS_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new AppOpsManager(ctx, IAppOpsService.Stub.asInterface(ServiceManager.getService(Context.APP_OPS_SERVICE)));
            }
        });
        registerService(Context.CAMERA_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new CameraManager(ctx);
            }
        });
        registerService(Context.LAUNCHER_APPS_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new LauncherApps(ctx, ILauncherApps.Stub.asInterface(ServiceManager.getService(Context.LAUNCHER_APPS_SERVICE)));
            }
        });
        registerService(Context.RESTRICTIONS_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new RestrictionsManager(ctx, IRestrictionsManager.Stub.asInterface(ServiceManager.getService(Context.RESTRICTIONS_SERVICE)));
            }
        });
        registerService(Context.PRINT_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new PrintManager(ctx.getOuterContext(), IPrintManager.Stub.asInterface(ServiceManager.getService(Context.PRINT_SERVICE)), UserHandle.myUserId(), UserHandle.getAppId(Process.myUid()));
            }
        });
        registerService(Context.CONSUMER_IR_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new ConsumerIrManager(ctx);
            }
        });
        registerService(Context.MEDIA_SESSION_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new MediaSessionManager(ctx);
            }
        });
        registerService(Context.TRUST_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new TrustManager(ServiceManager.getService(Context.TRUST_SERVICE));
            }
        });
        registerService(Context.FINGERPRINT_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new FingerprintManager(ctx.getOuterContext(), IFingerprintService.Stub.asInterface(ServiceManager.getService(Context.FINGERPRINT_SERVICE)));
            }
        });
        registerService(Context.TV_INPUT_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new TvInputManager(ITvInputManager.Stub.asInterface(ServiceManager.getService(Context.TV_INPUT_SERVICE)), UserHandle.myUserId());
            }
        });
        registerService(Context.NETWORK_SCORE_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new NetworkScoreManager(ctx);
            }
        });
        registerService(Context.USAGE_STATS_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new UsageStatsManager(ctx.getOuterContext(), IUsageStatsManager.Stub.asInterface(ServiceManager.getService(Context.USAGE_STATS_SERVICE)));
            }
        });
        registerService(Context.JOB_SCHEDULER_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new JobSchedulerImpl(IJobScheduler.Stub.asInterface(ServiceManager.getService(Context.JOB_SCHEDULER_SERVICE)));
            }
        });
        registerService(Context.PERSISTENT_DATA_BLOCK_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                IPersistentDataBlockService persistentDataBlockService = IPersistentDataBlockService.Stub.asInterface(ServiceManager.getService(Context.PERSISTENT_DATA_BLOCK_SERVICE));
                if (persistentDataBlockService != null) {
                    return new PersistentDataBlockManager(persistentDataBlockService);
                }
                return null;
            }
        });
        registerService(Context.MEDIA_PROJECTION_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new MediaProjectionManager(ctx);
            }
        });
        registerService(Context.APPWIDGET_SERVICE, new ServiceFetcher() {
            public Object createService(ContextImpl ctx) {
                return new AppWidgetManager(ctx, IAppWidgetService.Stub.asInterface(ServiceManager.getService(Context.APPWIDGET_SERVICE)));
            }
        });
    }

    private static void registerService(String serviceName, ServiceFetcher fetcher) {
        if (!(fetcher instanceof StaticServiceFetcher)) {
            int i = sNextPerContextServiceCacheIndex;
            sNextPerContextServiceCacheIndex = i + 1;
            fetcher.mContextCacheIndex = i;
        }
        SYSTEM_SERVICE_MAP.put(serviceName, fetcher);
    }

    static ContextImpl getImpl(Context context) {
        while (context instanceof ContextWrapper) {
            Context nextContext = ((ContextWrapper) context).getBaseContext();
            if (nextContext == null) {
                break;
            }
            context = nextContext;
        }
        return (ContextImpl) context;
    }

    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    public Resources getResources() {
        return this.mResources;
    }

    public PackageManager getPackageManager() {
        if (this.mPackageManager != null) {
            return this.mPackageManager;
        }
        IPackageManager pm = ActivityThread.getPackageManager();
        if (pm == null) {
            return null;
        }
        PackageManager applicationPackageManager = new ApplicationPackageManager(this, pm);
        this.mPackageManager = applicationPackageManager;
        return applicationPackageManager;
    }

    public ContentResolver getContentResolver() {
        return this.mContentResolver;
    }

    public Looper getMainLooper() {
        return this.mMainThread.getLooper();
    }

    public Context getApplicationContext() {
        return this.mPackageInfo != null ? this.mPackageInfo.getApplication() : this.mMainThread.getApplication();
    }

    public void setTheme(int resid) {
        this.mThemeResource = resid;
    }

    public int getThemeResId() {
        return this.mThemeResource;
    }

    public Theme getTheme() {
        if (this.mTheme == null) {
            this.mThemeResource = Resources.selectDefaultTheme(this.mThemeResource, getOuterContext().getApplicationInfo().targetSdkVersion);
            this.mTheme = this.mResources.newTheme();
            this.mTheme.applyStyle(this.mThemeResource, true);
        }
        return this.mTheme;
    }

    public ClassLoader getClassLoader() {
        return this.mPackageInfo != null ? this.mPackageInfo.getClassLoader() : ClassLoader.getSystemClassLoader();
    }

    public String getPackageName() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getPackageName();
        }
        return ZenModeConfig.SYSTEM_AUTHORITY;
    }

    public String getBasePackageName() {
        return this.mBasePackageName != null ? this.mBasePackageName : getPackageName();
    }

    public String getOpPackageName() {
        return this.mOpPackageName != null ? this.mOpPackageName : getBasePackageName();
    }

    public ApplicationInfo getApplicationInfo() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getApplicationInfo();
        }
        throw new RuntimeException("Not supported in system context");
    }

    public String getPackageResourcePath() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getResDir();
        }
        throw new RuntimeException("Not supported in system context");
    }

    public String getPackageCodePath() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getAppDir();
        }
        throw new RuntimeException("Not supported in system context");
    }

    public File getSharedPrefsFile(String name) {
        return makeFilename(getPreferencesDir(), name + ".xml");
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        synchronized (ContextImpl.class) {
            if (sSharedPrefs == null) {
                sSharedPrefs = new ArrayMap();
            }
            String packageName = getPackageName();
            ArrayMap<String, SharedPreferencesImpl> packagePrefs = (ArrayMap) sSharedPrefs.get(packageName);
            if (packagePrefs == null) {
                packagePrefs = new ArrayMap();
                sSharedPrefs.put(packageName, packagePrefs);
            }
            if (this.mPackageInfo.getApplicationInfo().targetSdkVersion < 19 && name == null) {
                name = "null";
            }
            SharedPreferencesImpl sp = (SharedPreferencesImpl) packagePrefs.get(name);
            if (sp == null) {
                sp = new SharedPreferencesImpl(getSharedPrefsFile(name), mode);
                packagePrefs.put(name, sp);
                return sp;
            }
            if ((mode & 4) != 0 || getApplicationInfo().targetSdkVersion < 11) {
                sp.startReloadIfChangedUnexpectedly();
            }
            return sp;
        }
    }

    private File getPreferencesDir() {
        File file;
        synchronized (this.mSync) {
            if (this.mPreferencesDir == null) {
                this.mPreferencesDir = new File(getDataDirFile(), "shared_prefs");
            }
            file = this.mPreferencesDir;
        }
        return file;
    }

    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return new FileInputStream(makeFilename(getFilesDir(), name));
    }

    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        boolean append;
        if ((AccessibilityNodeInfo.ACTION_PASTE & mode) != 0) {
            append = true;
        } else {
            append = DEBUG;
        }
        File f = makeFilename(getFilesDir(), name);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f, append);
            setFilePermissionsFromMode(f.getPath(), mode, 0);
            return fos;
        } catch (FileNotFoundException e) {
            File parent = f.getParentFile();
            parent.mkdir();
            FileUtils.setPermissions(parent.getPath(), 505, -1, -1);
            fos = new FileOutputStream(f, append);
            setFilePermissionsFromMode(f.getPath(), mode, 0);
            return fos;
        }
    }

    public boolean deleteFile(String name) {
        return makeFilename(getFilesDir(), name).delete();
    }

    private static File createFilesDirLocked(File file) {
        if (file.exists()) {
            return file;
        }
        if (file.mkdirs()) {
            FileUtils.setPermissions(file.getPath(), 505, -1, -1);
            return file;
        } else if (file.exists()) {
            return file;
        } else {
            Log.w(TAG, "Unable to create files subdir " + file.getPath());
            return null;
        }
    }

    public File getFilesDir() {
        File createFilesDirLocked;
        synchronized (this.mSync) {
            if (this.mFilesDir == null) {
                this.mFilesDir = new File(getDataDirFile(), "files");
            }
            createFilesDirLocked = createFilesDirLocked(this.mFilesDir);
        }
        return createFilesDirLocked;
    }

    public File getNoBackupFilesDir() {
        File createFilesDirLocked;
        synchronized (this.mSync) {
            if (this.mNoBackupFilesDir == null) {
                this.mNoBackupFilesDir = new File(getDataDirFile(), "no_backup");
            }
            createFilesDirLocked = createFilesDirLocked(this.mNoBackupFilesDir);
        }
        return createFilesDirLocked;
    }

    public File getExternalFilesDir(String type) {
        return getExternalFilesDirs(type)[0];
    }

    public File[] getExternalFilesDirs(String type) {
        File[] ensureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalFilesDirs == null) {
                this.mExternalFilesDirs = Environment.buildExternalStorageAppFilesDirs(getPackageName());
            }
            File[] dirs = this.mExternalFilesDirs;
            if (type != null) {
                dirs = Environment.buildPaths(dirs, type);
            }
            ensureDirsExistOrFilter = ensureDirsExistOrFilter(dirs);
        }
        return ensureDirsExistOrFilter;
    }

    public File getObbDir() {
        return getObbDirs()[0];
    }

    public File[] getObbDirs() {
        File[] ensureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalObbDirs == null) {
                this.mExternalObbDirs = Environment.buildExternalStorageAppObbDirs(getPackageName());
            }
            ensureDirsExistOrFilter = ensureDirsExistOrFilter(this.mExternalObbDirs);
        }
        return ensureDirsExistOrFilter;
    }

    public File getCacheDir() {
        File createFilesDirLocked;
        synchronized (this.mSync) {
            if (this.mCacheDir == null) {
                this.mCacheDir = new File(getDataDirFile(), "cache");
            }
            createFilesDirLocked = createFilesDirLocked(this.mCacheDir);
        }
        return createFilesDirLocked;
    }

    public File getCodeCacheDir() {
        File createFilesDirLocked;
        synchronized (this.mSync) {
            if (this.mCodeCacheDir == null) {
                this.mCodeCacheDir = new File(getDataDirFile(), "code_cache");
            }
            createFilesDirLocked = createFilesDirLocked(this.mCodeCacheDir);
        }
        return createFilesDirLocked;
    }

    public File getExternalCacheDir() {
        return getExternalCacheDirs()[0];
    }

    public File[] getExternalCacheDirs() {
        File[] ensureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalCacheDirs == null) {
                this.mExternalCacheDirs = Environment.buildExternalStorageAppCacheDirs(getPackageName());
            }
            ensureDirsExistOrFilter = ensureDirsExistOrFilter(this.mExternalCacheDirs);
        }
        return ensureDirsExistOrFilter;
    }

    public File[] getExternalMediaDirs() {
        File[] ensureDirsExistOrFilter;
        synchronized (this.mSync) {
            if (this.mExternalMediaDirs == null) {
                this.mExternalMediaDirs = Environment.buildExternalStorageAppMediaDirs(getPackageName());
            }
            ensureDirsExistOrFilter = ensureDirsExistOrFilter(this.mExternalMediaDirs);
        }
        return ensureDirsExistOrFilter;
    }

    public File getFileStreamPath(String name) {
        return makeFilename(getFilesDir(), name);
    }

    public String[] fileList() {
        String[] list = getFilesDir().list();
        return list != null ? list : EMPTY_FILE_LIST;
    }

    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
        return openOrCreateDatabase(name, mode, factory, null);
    }

    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {
        File f = validateFilePath(name, true);
        int flags = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        if ((mode & 8) != 0) {
            flags = EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION;
        }
        SQLiteDatabase db = SQLiteDatabase.openDatabase(f.getPath(), factory, flags, errorHandler);
        setFilePermissionsFromMode(f.getPath(), mode, 0);
        return db;
    }

    public boolean deleteDatabase(String name) {
        boolean z = DEBUG;
        try {
            z = SQLiteDatabase.deleteDatabase(validateFilePath(name, DEBUG));
        } catch (Exception e) {
        }
        return z;
    }

    public File getDatabasePath(String name) {
        return validateFilePath(name, DEBUG);
    }

    public String[] databaseList() {
        String[] list = getDatabasesDir().list();
        return list != null ? list : EMPTY_FILE_LIST;
    }

    private File getDatabasesDir() {
        File file;
        synchronized (this.mSync) {
            if (this.mDatabasesDir == null) {
                this.mDatabasesDir = new File(getDataDirFile(), "databases");
            }
            if (this.mDatabasesDir.getPath().equals("databases")) {
                this.mDatabasesDir = new File("/data/system");
            }
            file = this.mDatabasesDir;
        }
        return file;
    }

    public Drawable getWallpaper() {
        return getWallpaperManager().getDrawable();
    }

    public Drawable peekWallpaper() {
        return getWallpaperManager().peekDrawable();
    }

    public int getWallpaperDesiredMinimumWidth() {
        return getWallpaperManager().getDesiredMinimumWidth();
    }

    public int getWallpaperDesiredMinimumHeight() {
        return getWallpaperManager().getDesiredMinimumHeight();
    }

    public void setWallpaper(Bitmap bitmap) throws IOException {
        getWallpaperManager().setBitmap(bitmap);
    }

    public void setWallpaper(InputStream data) throws IOException {
        getWallpaperManager().setStream(data);
    }

    public void clearWallpaper() throws IOException {
        getWallpaperManager().clear();
    }

    public void startActivity(Intent intent) {
        warnIfCallingFromSystemProcess();
        startActivity(intent, null);
    }

    public void startActivityAsUser(Intent intent, UserHandle user) {
        startActivityAsUser(intent, null, user);
    }

    public void startActivity(Intent intent, Bundle options) {
        warnIfCallingFromSystemProcess();
        if ((intent.getFlags() & EditorInfo.IME_FLAG_NO_EXTRACT_UI) == 0) {
            throw new AndroidRuntimeException("Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivity(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity) null, intent, -1, options);
    }

    public void startActivityAsUser(Intent intent, Bundle options, UserHandle user) {
        try {
            ActivityManagerNative.getDefault().startActivityAsUser(this.mMainThread.getApplicationThread(), getBasePackageName(), intent, intent.resolveTypeIfNeeded(getContentResolver()), null, null, 0, EditorInfo.IME_FLAG_NO_EXTRACT_UI, null, options, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public void startActivities(Intent[] intents) {
        warnIfCallingFromSystemProcess();
        startActivities(intents, null);
    }

    public void startActivitiesAsUser(Intent[] intents, Bundle options, UserHandle userHandle) {
        if ((intents[0].getFlags() & EditorInfo.IME_FLAG_NO_EXTRACT_UI) == 0) {
            throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivitiesAsUser(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity) null, intents, options, userHandle.getIdentifier());
    }

    public void startActivities(Intent[] intents, Bundle options) {
        warnIfCallingFromSystemProcess();
        if ((intents[0].getFlags() & EditorInfo.IME_FLAG_NO_EXTRACT_UI) == 0) {
            throw new AndroidRuntimeException("Calling startActivities() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag on first Intent. Is this really what you want?");
        }
        this.mMainThread.getInstrumentation().execStartActivities(getOuterContext(), this.mMainThread.getApplicationThread(), null, (Activity) null, intents, options);
    }

    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws SendIntentException {
        startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws SendIntentException {
        String resolvedType = null;
        if (fillInIntent != null) {
            try {
                fillInIntent.migrateExtraStreamToClipData();
                fillInIntent.prepareToLeaveProcess();
                resolvedType = fillInIntent.resolveTypeIfNeeded(getContentResolver());
            } catch (RemoteException e) {
                return;
            }
        }
        int result = ActivityManagerNative.getDefault().startActivityIntentSender(this.mMainThread.getApplicationThread(), intent, fillInIntent, resolvedType, null, null, 0, flagsMask, flagsValues, options);
        if (result == -6) {
            throw new SendIntentException();
        }
        Instrumentation.checkStartActivityResult(result, null);
    }

    public void sendBroadcast(Intent intent) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, DEBUG, DEBUG, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void sendBroadcast(Intent intent, String receiverPermission) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, -1, DEBUG, DEBUG, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void sendBroadcast(Intent intent, String receiverPermission, int appOp) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, appOp, DEBUG, DEBUG, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, -1, true, DEBUG, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        sendOrderedBroadcast(intent, receiverPermission, -1, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    public void sendOrderedBroadcast(Intent intent, String receiverPermission, int appOp, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        warnIfCallingFromSystemProcess();
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), DEBUG);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, DEBUG).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, receiverPermission, appOp, true, DEBUG, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, DEBUG, DEBUG, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, receiverPermission, -1, DEBUG, DEBUG, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        sendOrderedBroadcastAsUser(intent, user, receiverPermission, -1, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, int appOp, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), DEBUG);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, DEBUG).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, receiverPermission, appOp, true, DEBUG, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public void sendStickyBroadcast(Intent intent) {
        warnIfCallingFromSystemProcess();
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, DEBUG, true, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        warnIfCallingFromSystemProcess();
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), DEBUG);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, DEBUG).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, null, -1, true, true, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void removeStickyBroadcast(Intent intent) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        if (resolvedType != null) {
            Intent intent2 = new Intent(intent);
            intent2.setDataAndType(intent2.getData(), resolvedType);
            intent = intent2;
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), intent, getUserId());
        } catch (RemoteException e) {
        }
    }

    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, null, -1, null, null, null, -1, DEBUG, true, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        IIntentReceiver rd = null;
        if (resultReceiver != null) {
            if (this.mPackageInfo != null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, this.mMainThread.getInstrumentation(), DEBUG);
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new ReceiverDispatcher(resultReceiver, getOuterContext(), scheduler, null, DEBUG).getIIntentReceiver();
            }
        }
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().broadcastIntent(this.mMainThread.getApplicationThread(), intent, resolvedType, rd, initialCode, initialData, initialExtras, null, -1, true, true, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        String resolvedType = intent.resolveTypeIfNeeded(getContentResolver());
        if (resolvedType != null) {
            Intent intent2 = new Intent(intent);
            intent2.setDataAndType(intent2.getData(), resolvedType);
            intent = intent2;
        }
        try {
            intent.prepareToLeaveProcess();
            ActivityManagerNative.getDefault().unbroadcastIntent(this.mMainThread.getApplicationThread(), intent, user.getIdentifier());
        } catch (RemoteException e) {
        }
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return registerReceiver(receiver, filter, null, null);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return registerReceiverInternal(receiver, getUserId(), filter, broadcastPermission, scheduler, getOuterContext());
    }

    public Intent registerReceiverAsUser(BroadcastReceiver receiver, UserHandle user, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return registerReceiverInternal(receiver, user.getIdentifier(), filter, broadcastPermission, scheduler, getOuterContext());
    }

    private Intent registerReceiverInternal(BroadcastReceiver receiver, int userId, IntentFilter filter, String broadcastPermission, Handler scheduler, Context context) {
        IIntentReceiver rd = null;
        if (receiver != null) {
            if (this.mPackageInfo == null || context == null) {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = new ReceiverDispatcher(receiver, context, scheduler, null, true).getIIntentReceiver();
            } else {
                if (scheduler == null) {
                    scheduler = this.mMainThread.getHandler();
                }
                rd = this.mPackageInfo.getReceiverDispatcher(receiver, context, scheduler, this.mMainThread.getInstrumentation(), true);
            }
        }
        try {
            return ActivityManagerNative.getDefault().registerReceiver(this.mMainThread.getApplicationThread(), this.mBasePackageName, rd, filter, broadcastPermission, userId);
        } catch (RemoteException e) {
            return null;
        }
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (this.mPackageInfo != null) {
            try {
                ActivityManagerNative.getDefault().unregisterReceiver(this.mPackageInfo.forgetReceiverDispatcher(getOuterContext(), receiver));
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        throw new RuntimeException("Not supported in system context");
    }

    private void validateServiceIntent(Intent service) {
        if (service.getComponent() != null || service.getPackage() != null) {
            return;
        }
        if (getApplicationInfo().targetSdkVersion >= 21) {
            throw new IllegalArgumentException("Service Intent must be explicit: " + service);
        }
        Log.w(TAG, "Implicit intents with startService are not safe: " + service + " " + Debug.getCallers(2, 3));
    }

    public ComponentName startService(Intent service) {
        warnIfCallingFromSystemProcess();
        return startServiceCommon(service, this.mUser);
    }

    public boolean stopService(Intent service) {
        warnIfCallingFromSystemProcess();
        return stopServiceCommon(service, this.mUser);
    }

    public ComponentName startServiceAsUser(Intent service, UserHandle user) {
        return startServiceCommon(service, user);
    }

    private ComponentName startServiceCommon(Intent service, UserHandle user) {
        try {
            validateServiceIntent(service);
            service.prepareToLeaveProcess();
            ComponentName cn = ActivityManagerNative.getDefault().startService(this.mMainThread.getApplicationThread(), service, service.resolveTypeIfNeeded(getContentResolver()), user.getIdentifier());
            if (cn == null) {
                return cn;
            }
            if (cn.getPackageName().equals("!")) {
                throw new SecurityException("Not allowed to start service " + service + " without permission " + cn.getClassName());
            } else if (!cn.getPackageName().equals("!!")) {
                return cn;
            } else {
                throw new SecurityException("Unable to start service " + service + ": " + cn.getClassName());
            }
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean stopServiceAsUser(Intent service, UserHandle user) {
        return stopServiceCommon(service, user);
    }

    private boolean stopServiceCommon(Intent service, UserHandle user) {
        try {
            validateServiceIntent(service);
            service.prepareToLeaveProcess();
            int res = ActivityManagerNative.getDefault().stopService(this.mMainThread.getApplicationThread(), service, service.resolveTypeIfNeeded(getContentResolver()), user.getIdentifier());
            if (res < 0) {
                throw new SecurityException("Not allowed to stop service " + service);
            } else if (res != 0) {
                return true;
            } else {
                return DEBUG;
            }
        } catch (RemoteException e) {
            return DEBUG;
        }
    }

    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        warnIfCallingFromSystemProcess();
        return bindServiceCommon(service, conn, flags, Process.myUserHandle());
    }

    public boolean bindServiceAsUser(Intent service, ServiceConnection conn, int flags, UserHandle user) {
        return bindServiceCommon(service, conn, flags, user);
    }

    private boolean bindServiceCommon(Intent service, ServiceConnection conn, int flags, UserHandle user) {
        if (conn == null) {
            throw new IllegalArgumentException("connection is null");
        } else if (this.mPackageInfo != null) {
            IServiceConnection sd = this.mPackageInfo.getServiceDispatcher(conn, getOuterContext(), this.mMainThread.getHandler(), flags);
            validateServiceIntent(service);
            try {
                if (getActivityToken() == null && (flags & 1) == 0 && this.mPackageInfo != null && this.mPackageInfo.getApplicationInfo().targetSdkVersion < 14) {
                    flags |= 32;
                }
                service.prepareToLeaveProcess();
                int res = ActivityManagerNative.getDefault().bindService(this.mMainThread.getApplicationThread(), getActivityToken(), service, service.resolveTypeIfNeeded(getContentResolver()), sd, flags, user.getIdentifier());
                if (res >= 0) {
                    return res != 0 ? true : DEBUG;
                } else {
                    throw new SecurityException("Not allowed to bind to service " + service);
                }
            } catch (RemoteException e) {
                return DEBUG;
            }
        } else {
            throw new RuntimeException("Not supported in system context");
        }
    }

    public void unbindService(ServiceConnection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("connection is null");
        } else if (this.mPackageInfo != null) {
            try {
                ActivityManagerNative.getDefault().unbindService(this.mPackageInfo.forgetServiceDispatcher(getOuterContext(), conn));
            } catch (RemoteException e) {
            }
        } else {
            throw new RuntimeException("Not supported in system context");
        }
    }

    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        if (arguments != null) {
            try {
                arguments.setAllowFds(DEBUG);
            } catch (RemoteException e) {
                return DEBUG;
            }
        }
        return ActivityManagerNative.getDefault().startInstrumentation(className, profileFile, 0, arguments, null, null, getUserId(), null);
    }

    public Object getSystemService(String name) {
        ServiceFetcher fetcher = (ServiceFetcher) SYSTEM_SERVICE_MAP.get(name);
        return fetcher == null ? null : fetcher.getService(this);
    }

    private WallpaperManager getWallpaperManager() {
        return (WallpaperManager) WALLPAPER_FETCHER.getService(this);
    }

    static DropBoxManager createDropBoxManager() {
        IDropBoxManagerService service = IDropBoxManagerService.Stub.asInterface(ServiceManager.getService(Context.DROPBOX_SERVICE));
        if (service == null) {
            return null;
        }
        return new DropBoxManager(service);
    }

    public int checkPermission(String permission, int pid, int uid) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        try {
            return ActivityManagerNative.getDefault().checkPermission(permission, pid, uid);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public int checkPermission(String permission, int pid, int uid, IBinder callerToken) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        try {
            return ActivityManagerNative.getDefault().checkPermissionWithToken(permission, pid, uid, callerToken);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public int checkCallingPermission(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }
        int pid = Binder.getCallingPid();
        if (pid != Process.myPid()) {
            return checkPermission(permission, pid, Binder.getCallingUid());
        }
        return -1;
    }

    public int checkCallingOrSelfPermission(String permission) {
        if (permission != null) {
            return checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid());
        }
        throw new IllegalArgumentException("permission is null");
    }

    private void enforce(String permission, int resultOfCheck, boolean selfToo, int uid, String message) {
        if (resultOfCheck != 0) {
            throw new SecurityException((message != null ? message + ": " : ProxyInfo.LOCAL_EXCL_LIST) + (selfToo ? "Neither user " + uid + " nor current process has " : "uid " + uid + " does not have ") + permission + ".");
        }
    }

    public void enforcePermission(String permission, int pid, int uid, String message) {
        enforce(permission, checkPermission(permission, pid, uid), DEBUG, uid, message);
    }

    public void enforceCallingPermission(String permission, String message) {
        enforce(permission, checkCallingPermission(permission), DEBUG, Binder.getCallingUid(), message);
    }

    public void enforceCallingOrSelfPermission(String permission, String message) {
        enforce(permission, checkCallingOrSelfPermission(permission), true, Binder.getCallingUid(), message);
    }

    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        try {
            ActivityManagerNative.getDefault().grantUriPermission(this.mMainThread.getApplicationThread(), toPackage, ContentProvider.getUriWithoutUserId(uri), modeFlags, resolveUserId(uri));
        } catch (RemoteException e) {
        }
    }

    public void revokeUriPermission(Uri uri, int modeFlags) {
        try {
            ActivityManagerNative.getDefault().revokeUriPermission(this.mMainThread.getApplicationThread(), ContentProvider.getUriWithoutUserId(uri), modeFlags, resolveUserId(uri));
        } catch (RemoteException e) {
        }
    }

    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        try {
            return ActivityManagerNative.getDefault().checkUriPermission(ContentProvider.getUriWithoutUserId(uri), pid, uid, modeFlags, resolveUserId(uri), null);
        } catch (RemoteException e) {
            return -1;
        }
    }

    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags, IBinder callerToken) {
        try {
            return ActivityManagerNative.getDefault().checkUriPermission(ContentProvider.getUriWithoutUserId(uri), pid, uid, modeFlags, resolveUserId(uri), callerToken);
        } catch (RemoteException e) {
            return -1;
        }
    }

    private int resolveUserId(Uri uri) {
        return ContentProvider.getUserIdFromUri(uri, getUserId());
    }

    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        int pid = Binder.getCallingPid();
        if (pid != Process.myPid()) {
            return checkUriPermission(uri, pid, Binder.getCallingUid(), modeFlags);
        }
        return -1;
    }

    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return checkUriPermission(uri, Binder.getCallingPid(), Binder.getCallingUid(), modeFlags);
    }

    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
        if ((modeFlags & 1) != 0 && (readPermission == null || checkPermission(readPermission, pid, uid) == 0)) {
            return 0;
        }
        if ((modeFlags & 2) == 0 || (writePermission != null && checkPermission(writePermission, pid, uid) != 0)) {
            return uri != null ? checkUriPermission(uri, pid, uid, modeFlags) : -1;
        } else {
            return 0;
        }
    }

    private String uriModeFlagToString(int uriModeFlags) {
        StringBuilder builder = new StringBuilder();
        if ((uriModeFlags & 1) != 0) {
            builder.append("read and ");
        }
        if ((uriModeFlags & 2) != 0) {
            builder.append("write and ");
        }
        if ((uriModeFlags & 64) != 0) {
            builder.append("persistable and ");
        }
        if ((uriModeFlags & AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) != 0) {
            builder.append("prefix and ");
        }
        if (builder.length() > 5) {
            builder.setLength(builder.length() - 5);
            return builder.toString();
        }
        throw new IllegalArgumentException("Unknown permission mode flags: " + uriModeFlags);
    }

    private void enforceForUri(int modeFlags, int resultOfCheck, boolean selfToo, int uid, Uri uri, String message) {
        if (resultOfCheck != 0) {
            throw new SecurityException((message != null ? message + ": " : ProxyInfo.LOCAL_EXCL_LIST) + (selfToo ? "Neither user " + uid + " nor current process has " : "User " + uid + " does not have ") + uriModeFlagToString(modeFlags) + " permission on " + uri + ".");
        }
    }

    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        enforceForUri(modeFlags, checkUriPermission(uri, pid, uid, modeFlags), DEBUG, uid, uri, message);
    }

    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        enforceForUri(modeFlags, checkCallingUriPermission(uri, modeFlags), DEBUG, Binder.getCallingUid(), uri, message);
    }

    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        enforceForUri(modeFlags, checkCallingOrSelfUriPermission(uri, modeFlags), true, Binder.getCallingUid(), uri, message);
    }

    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {
        enforceForUri(modeFlags, checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags), DEBUG, uid, uri, message);
    }

    private void warnIfCallingFromSystemProcess() {
        if (Process.myUid() == LayoutParams.TYPE_APPLICATION_PANEL) {
            Slog.w(TAG, "Calling a method in the system process without a qualified user: " + Debug.getCallers(5));
        }
    }

    public Context createApplicationContext(ApplicationInfo application, int flags) throws NameNotFoundException {
        LoadedApk pi = this.mMainThread.getPackageInfo(application, this.mResources.getCompatibilityInfo(), EditorInfo.IME_FLAG_NO_ENTER_ACTION | flags);
        if (pi != null) {
            ContextImpl c = new ContextImpl(this, this.mMainThread, pi, this.mActivityToken, new UserHandle(UserHandle.getUserId(application.uid)), (flags & 4) == 4 ? true : DEBUG, this.mDisplay, this.mOverrideConfiguration);
            if (c.mResources != null) {
                return c;
            }
        }
        throw new NameNotFoundException("Application package " + application.packageName + " not found");
    }

    public Context createPackageContext(String packageName, int flags) throws NameNotFoundException {
        return createPackageContextAsUser(packageName, flags, this.mUser != null ? this.mUser : Process.myUserHandle());
    }

    public Context createPackageContextAsUser(String packageName, int flags, UserHandle user) throws NameNotFoundException {
        boolean restricted = (flags & 4) == 4 ? true : DEBUG;
        if (packageName.equals("system") || packageName.equals(ZenModeConfig.SYSTEM_AUTHORITY)) {
            return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, user, restricted, this.mDisplay, this.mOverrideConfiguration);
        }
        LoadedApk pi = this.mMainThread.getPackageInfo(packageName, this.mResources.getCompatibilityInfo(), EditorInfo.IME_FLAG_NO_ENTER_ACTION | flags, user.getIdentifier());
        if (pi != null) {
            Context c = new ContextImpl(this, this.mMainThread, pi, this.mActivityToken, user, restricted, this.mDisplay, this.mOverrideConfiguration);
            if (c.mResources != null) {
                return c;
            }
        }
        throw new NameNotFoundException("Application package " + packageName + " not found");
    }

    public Context createConfigurationContext(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, this.mUser, this.mRestricted, this.mDisplay, overrideConfiguration);
        }
        throw new IllegalArgumentException("overrideConfiguration must not be null");
    }

    public Context createDisplayContext(Display display) {
        if (display == null) {
            throw new IllegalArgumentException("display must not be null");
        }
        return new ContextImpl(this, this.mMainThread, this.mPackageInfo, this.mActivityToken, this.mUser, this.mRestricted, display, this.mOverrideConfiguration);
    }

    private int getDisplayId() {
        return this.mDisplay != null ? this.mDisplay.getDisplayId() : 0;
    }

    public boolean isRestricted() {
        return this.mRestricted;
    }

    public DisplayAdjustments getDisplayAdjustments(int displayId) {
        return this.mDisplayAdjustments;
    }

    private File getDataDirFile() {
        if (this.mPackageInfo != null) {
            return this.mPackageInfo.getDataDirFile();
        }
        throw new RuntimeException("Not supported in system context");
    }

    public File getDir(String name, int mode) {
        File file = makeFilename(getDataDirFile(), "app_" + name);
        if (!file.exists()) {
            file.mkdir();
            setFilePermissionsFromMode(file.getPath(), mode, 505);
        }
        return file;
    }

    public int getUserId() {
        return this.mUser.getIdentifier();
    }

    static ContextImpl createSystemContext(ActivityThread mainThread) {
        ContextImpl context = new ContextImpl(null, mainThread, new LoadedApk(mainThread), null, null, DEBUG, null, null);
        context.mResources.updateConfiguration(context.mResourcesManager.getConfiguration(), context.mResourcesManager.getDisplayMetricsLocked(0));
        return context;
    }

    static ContextImpl createAppContext(ActivityThread mainThread, LoadedApk packageInfo) {
        if (packageInfo != null) {
            return new ContextImpl(null, mainThread, packageInfo, null, null, DEBUG, null, null);
        }
        throw new IllegalArgumentException("packageInfo");
    }

    static ContextImpl createActivityContext(ActivityThread mainThread, LoadedApk packageInfo, IBinder activityToken) {
        if (packageInfo == null) {
            throw new IllegalArgumentException("packageInfo");
        } else if (activityToken != null) {
            return new ContextImpl(null, mainThread, packageInfo, activityToken, null, DEBUG, null, null);
        } else {
            throw new IllegalArgumentException("activityInfo");
        }
    }

    private ContextImpl(ContextImpl container, ActivityThread mainThread, LoadedApk packageInfo, IBinder activityToken, UserHandle user, boolean restricted, Display display, Configuration overrideConfiguration) {
        this.mDisplayAdjustments = new DisplayAdjustments();
        this.mThemeResource = 0;
        this.mTheme = null;
        this.mReceiverRestrictedContext = null;
        this.mSync = new Object();
        this.mServiceCache = new ArrayList();
        this.mOuterContext = this;
        this.mMainThread = mainThread;
        this.mActivityToken = activityToken;
        this.mRestricted = restricted;
        if (user == null) {
            user = Process.myUserHandle();
        }
        this.mUser = user;
        this.mPackageInfo = packageInfo;
        this.mResourcesManager = ResourcesManager.getInstance();
        this.mDisplay = display;
        this.mOverrideConfiguration = overrideConfiguration;
        int displayId = getDisplayId();
        CompatibilityInfo compatInfo = null;
        if (container != null) {
            compatInfo = container.getDisplayAdjustments(displayId).getCompatibilityInfo();
        }
        if (compatInfo == null && displayId == 0) {
            compatInfo = packageInfo.getCompatibilityInfo();
        }
        this.mDisplayAdjustments.setCompatibilityInfo(compatInfo);
        this.mDisplayAdjustments.setActivityToken(activityToken);
        Resources resources = packageInfo.getResources(mainThread);
        if (!(resources == null || (activityToken == null && displayId == 0 && overrideConfiguration == null && (compatInfo == null || compatInfo.applicationScale == resources.getCompatibilityInfo().applicationScale)))) {
            resources = this.mResourcesManager.getTopLevelResources(packageInfo.getResDir(), packageInfo.getSplitResDirs(), packageInfo.getOverlayDirs(), packageInfo.getApplicationInfo().sharedLibraryFiles, displayId, overrideConfiguration, compatInfo, activityToken);
        }
        this.mResources = resources;
        if (container != null) {
            this.mBasePackageName = container.mBasePackageName;
            this.mOpPackageName = container.mOpPackageName;
        } else {
            this.mBasePackageName = packageInfo.mPackageName;
            ApplicationInfo ainfo = packageInfo.getApplicationInfo();
            if (ainfo.uid != LayoutParams.TYPE_APPLICATION_PANEL || ainfo.uid == Process.myUid()) {
                this.mOpPackageName = this.mBasePackageName;
            } else {
                this.mOpPackageName = ActivityThread.currentPackageName();
            }
        }
        this.mContentResolver = new ApplicationContentResolver(this, mainThread, user);
    }

    void installSystemApplicationInfo(ApplicationInfo info, ClassLoader classLoader) {
        this.mPackageInfo.installSystemApplicationInfo(info, classLoader);
    }

    final void scheduleFinalCleanup(String who, String what) {
        this.mMainThread.scheduleContextCleanup(this, who, what);
    }

    final void performFinalCleanup(String who, String what) {
        this.mPackageInfo.removeContextRegistrations(getOuterContext(), who, what);
    }

    final Context getReceiverRestrictedContext() {
        if (this.mReceiverRestrictedContext != null) {
            return this.mReceiverRestrictedContext;
        }
        Context receiverRestrictedContext = new ReceiverRestrictedContext(getOuterContext());
        this.mReceiverRestrictedContext = receiverRestrictedContext;
        return receiverRestrictedContext;
    }

    final void setOuterContext(Context context) {
        this.mOuterContext = context;
    }

    final Context getOuterContext() {
        return this.mOuterContext;
    }

    final IBinder getActivityToken() {
        return this.mActivityToken;
    }

    static void setFilePermissionsFromMode(String name, int mode, int extraPermissions) {
        int perms = extraPermissions | 432;
        if ((mode & 1) != 0) {
            perms |= 4;
        }
        if ((mode & 2) != 0) {
            perms |= 2;
        }
        FileUtils.setPermissions(name, perms, -1, -1);
    }

    private File validateFilePath(String name, boolean createDirectory) {
        File dir;
        File f;
        if (name.charAt(0) == File.separatorChar) {
            dir = new File(name.substring(0, name.lastIndexOf(File.separatorChar)));
            f = new File(dir, name.substring(name.lastIndexOf(File.separatorChar)));
        } else {
            dir = getDatabasesDir();
            f = makeFilename(dir, name);
        }
        if (createDirectory && !dir.isDirectory() && dir.mkdir()) {
            FileUtils.setPermissions(dir.getPath(), 505, -1, -1);
        }
        return f;
    }

    private File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException("File " + name + " contains a path separator");
    }

    private File[] ensureDirsExistOrFilter(File[] dirs) {
        File[] result = new File[dirs.length];
        for (int i = 0; i < dirs.length; i++) {
            File dir = dirs[i];
            if (!(dir.exists() || dir.mkdirs() || dir.exists())) {
                int res = -1;
                try {
                    res = IMountService.Stub.asInterface(ServiceManager.getService("mount")).mkdirs(getPackageName(), dir.getAbsolutePath());
                } catch (Exception e) {
                }
                if (res != 0) {
                    Log.w(TAG, "Failed to ensure directory: " + dir);
                    dir = null;
                }
            }
            result[i] = dir;
        }
        return result;
    }
}
