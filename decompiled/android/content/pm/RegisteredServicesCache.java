package android.content.pm;

import android.accounts.GrantCredentialsPermissionActivity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.os.Handler;
import android.os.UserHandle;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public abstract class RegisteredServicesCache<V> {
    private static final boolean DEBUG = false;
    private static final String TAG = "PackageManager";
    private final String mAttributesName;
    public final Context mContext;
    private final BroadcastReceiver mExternalReceiver;
    private Handler mHandler;
    private final String mInterfaceName;
    private RegisteredServicesCacheListener<V> mListener;
    private final String mMetaDataName;
    private final BroadcastReceiver mPackageReceiver;
    private final AtomicFile mPersistentServicesFile;
    @GuardedBy("mServicesLock")
    private boolean mPersistentServicesFileDidNotExist;
    private final XmlSerializerAndParser<V> mSerializerAndParser;
    private final Object mServicesLock;
    @GuardedBy("mServicesLock")
    private final SparseArray<UserServices<V>> mUserServices;

    /* renamed from: android.content.pm.RegisteredServicesCache.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ RegisteredServicesCache this$0;
        final /* synthetic */ RegisteredServicesCacheListener val$listener2;
        final /* synthetic */ boolean val$removed;
        final /* synthetic */ Object val$type;
        final /* synthetic */ int val$userId;

        AnonymousClass3(android.content.pm.RegisteredServicesCache r1, android.content.pm.RegisteredServicesCacheListener r2, java.lang.Object r3, int r4, boolean r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.content.pm.RegisteredServicesCache.3.<init>(android.content.pm.RegisteredServicesCache, android.content.pm.RegisteredServicesCacheListener, java.lang.Object, int, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.content.pm.RegisteredServicesCache.3.<init>(android.content.pm.RegisteredServicesCache, android.content.pm.RegisteredServicesCacheListener, java.lang.Object, int, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.content.pm.RegisteredServicesCache.3.<init>(android.content.pm.RegisteredServicesCache, android.content.pm.RegisteredServicesCacheListener, java.lang.Object, int, boolean):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.content.pm.RegisteredServicesCache.3.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.content.pm.RegisteredServicesCache.3.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.content.pm.RegisteredServicesCache.3.run():void");
        }
    }

    public static class ServiceInfo<V> {
        public final ComponentName componentName;
        public final V type;
        public final int uid;

        public ServiceInfo(V type, ComponentName componentName, int uid) {
            this.type = type;
            this.componentName = componentName;
            this.uid = uid;
        }

        public String toString() {
            return "ServiceInfo: " + this.type + ", " + this.componentName + ", uid " + this.uid;
        }
    }

    private static class UserServices<V> {
        @GuardedBy("mServicesLock")
        public final Map<V, Integer> persistentServices;
        @GuardedBy("mServicesLock")
        public Map<V, ServiceInfo<V>> services;

        private UserServices() {
            this.persistentServices = Maps.newHashMap();
            this.services = null;
        }

        /* synthetic */ UserServices(AnonymousClass1 x0) {
            this();
        }
    }

    public abstract V parseServiceAttributes(Resources resources, String str, AttributeSet attributeSet);

    private UserServices<V> findOrCreateUserLocked(int userId) {
        UserServices<V> services = (UserServices) this.mUserServices.get(userId);
        if (services != null) {
            return services;
        }
        services = new UserServices();
        this.mUserServices.put(userId, services);
        return services;
    }

    public RegisteredServicesCache(Context context, String interfaceName, String metaDataName, String attributeName, XmlSerializerAndParser<V> serializerAndParser) {
        this.mServicesLock = new Object();
        this.mUserServices = new SparseArray(2);
        this.mPackageReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
                if (uid != -1) {
                    RegisteredServicesCache.this.handlePackageEvent(intent, UserHandle.getUserId(uid));
                }
            }
        };
        this.mExternalReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                RegisteredServicesCache.this.handlePackageEvent(intent, 0);
            }
        };
        this.mContext = context;
        this.mInterfaceName = interfaceName;
        this.mMetaDataName = metaDataName;
        this.mAttributesName = attributeName;
        this.mSerializerAndParser = serializerAndParser;
        this.mPersistentServicesFile = new AtomicFile(new File(new File(new File(Environment.getDataDirectory(), "system"), "registered_services"), interfaceName + ".xml"));
        readPersistentServicesLocked();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mPackageReceiver, UserHandle.ALL, intentFilter, null, null);
        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        this.mContext.registerReceiver(this.mExternalReceiver, sdFilter);
    }

    private final void handlePackageEvent(Intent intent, int userId) {
        boolean isRemoval;
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_REMOVED.equals(action) || Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
            isRemoval = true;
        } else {
            isRemoval = DEBUG;
        }
        boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, DEBUG);
        if (!isRemoval || !replacing) {
            int[] uids = null;
            if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action) || Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                uids = intent.getIntArrayExtra(Intent.EXTRA_CHANGED_UID_LIST);
            } else {
                if (intent.getIntExtra(Intent.EXTRA_UID, -1) > 0) {
                    uids = new int[]{intent.getIntExtra(Intent.EXTRA_UID, -1)};
                }
            }
            generateServicesMap(uids, userId);
        }
    }

    public void invalidateCache(int userId) {
        synchronized (this.mServicesLock) {
            findOrCreateUserLocked(userId).services = null;
        }
    }

    public void dump(FileDescriptor fd, PrintWriter fout, String[] args, int userId) {
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services != null) {
                fout.println("RegisteredServicesCache: " + user.services.size() + " services");
                for (ServiceInfo<?> info : user.services.values()) {
                    fout.println("  " + info);
                }
            } else {
                fout.println("RegisteredServicesCache: services not loaded");
            }
        }
    }

    public RegisteredServicesCacheListener<V> getListener() {
        RegisteredServicesCacheListener<V> registeredServicesCacheListener;
        synchronized (this) {
            registeredServicesCacheListener = this.mListener;
        }
        return registeredServicesCacheListener;
    }

    public void setListener(RegisteredServicesCacheListener<V> listener, Handler handler) {
        if (handler == null) {
            handler = new Handler(this.mContext.getMainLooper());
        }
        synchronized (this) {
            this.mHandler = handler;
            this.mListener = listener;
        }
    }

    private void notifyListener(V type, int userId, boolean removed) {
        synchronized (this) {
            RegisteredServicesCacheListener<V> listener = this.mListener;
            Handler handler = this.mHandler;
        }
        if (listener != null) {
            handler.post(new AnonymousClass3(this, listener, type, userId, removed));
        }
    }

    public ServiceInfo<V> getServiceInfo(V type, int userId) {
        ServiceInfo<V> serviceInfo;
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services == null) {
                generateServicesMap(null, userId);
            }
            serviceInfo = (ServiceInfo) user.services.get(type);
        }
        return serviceInfo;
    }

    public Collection<ServiceInfo<V>> getAllServices(int userId) {
        Collection<ServiceInfo<V>> unmodifiableCollection;
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services == null) {
                generateServicesMap(null, userId);
            }
            unmodifiableCollection = Collections.unmodifiableCollection(new ArrayList(user.services.values()));
        }
        return unmodifiableCollection;
    }

    private boolean inSystemImage(int callerUid) {
        String[] arr$ = this.mContext.getPackageManager().getPackagesForUid(callerUid);
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            try {
                if ((this.mContext.getPackageManager().getPackageInfo(arr$[i$], 0).applicationInfo.flags & 1) != 0) {
                    return true;
                }
                i$++;
            } catch (NameNotFoundException e) {
                return DEBUG;
            }
        }
        return DEBUG;
    }

    private void generateServicesMap(int[] changedUids, int userId) {
        Iterator i$;
        ServiceInfo<V> info;
        PackageManager pm = this.mContext.getPackageManager();
        ArrayList<ServiceInfo<V>> serviceInfos = new ArrayList();
        for (ResolveInfo resolveInfo : pm.queryIntentServicesAsUser(new Intent(this.mInterfaceName), AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS, userId)) {
            try {
                info = parseServiceInfo(resolveInfo);
                if (info == null) {
                    Log.w(TAG, "Unable to load service info " + resolveInfo.toString());
                } else {
                    serviceInfos.add(info);
                }
            } catch (XmlPullParserException e) {
                Log.w(TAG, "Unable to load service info " + resolveInfo.toString(), e);
            } catch (IOException e2) {
                Log.w(TAG, "Unable to load service info " + resolveInfo.toString(), e2);
            }
        }
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            boolean firstScan = user.services == null ? true : DEBUG;
            if (firstScan) {
                user.services = Maps.newHashMap();
            }
            StringBuilder changes = new StringBuilder();
            boolean changed = DEBUG;
            i$ = serviceInfos.iterator();
            while (i$.hasNext()) {
                info = (ServiceInfo) i$.next();
                Integer previousUid = (Integer) user.persistentServices.get(info.type);
                if (previousUid == null) {
                    changed = true;
                    user.services.put(info.type, info);
                    user.persistentServices.put(info.type, Integer.valueOf(info.uid));
                    if (!this.mPersistentServicesFileDidNotExist || !firstScan) {
                        notifyListener(info.type, userId, DEBUG);
                    }
                } else {
                    if (previousUid.intValue() == info.uid) {
                        user.services.put(info.type, info);
                    } else {
                        if (!inSystemImage(info.uid)) {
                            if (containsTypeAndUid(serviceInfos, info.type, previousUid.intValue())) {
                            }
                        }
                        changed = true;
                        user.services.put(info.type, info);
                        user.persistentServices.put(info.type, Integer.valueOf(info.uid));
                        notifyListener(info.type, userId, DEBUG);
                    }
                }
            }
            ArrayList<V> toBeRemoved = Lists.newArrayList();
            for (V v1 : user.persistentServices.keySet()) {
                V v12;
                if (!containsType(serviceInfos, v12)) {
                    if (containsUid(changedUids, ((Integer) user.persistentServices.get(v12)).intValue())) {
                        toBeRemoved.add(v12);
                    }
                }
            }
            i$ = toBeRemoved.iterator();
            while (i$.hasNext()) {
                v12 = i$.next();
                changed = true;
                user.persistentServices.remove(v12);
                user.services.remove(v12);
                notifyListener(v12, userId, true);
            }
            if (changed) {
                writePersistentServicesLocked();
            }
        }
    }

    private boolean containsUid(int[] changedUids, int uid) {
        return (changedUids == null || ArrayUtils.contains(changedUids, uid)) ? true : DEBUG;
    }

    private boolean containsType(ArrayList<ServiceInfo<V>> serviceInfos, V type) {
        int N = serviceInfos.size();
        for (int i = 0; i < N; i++) {
            if (((ServiceInfo) serviceInfos.get(i)).type.equals(type)) {
                return true;
            }
        }
        return DEBUG;
    }

    private boolean containsTypeAndUid(ArrayList<ServiceInfo<V>> serviceInfos, V type, int uid) {
        int N = serviceInfos.size();
        for (int i = 0; i < N; i++) {
            ServiceInfo<V> serviceInfo = (ServiceInfo) serviceInfos.get(i);
            if (serviceInfo.type.equals(type) && serviceInfo.uid == uid) {
                return true;
            }
        }
        return DEBUG;
    }

    private ServiceInfo<V> parseServiceInfo(ResolveInfo service) throws XmlPullParserException, IOException {
        ServiceInfo si = service.serviceInfo;
        ComponentName componentName = new ComponentName(si.packageName, si.name);
        PackageManager pm = this.mContext.getPackageManager();
        XmlResourceParser parser = null;
        try {
            parser = si.loadXmlMetaData(pm, this.mMetaDataName);
            if (parser == null) {
                throw new XmlPullParserException("No " + this.mMetaDataName + " meta-data");
            }
            AttributeSet attrs = Xml.asAttributeSet(parser);
            int type;
            do {
                type = parser.next();
                if (type == 1) {
                    break;
                }
            } while (type != 2);
            if (this.mAttributesName.equals(parser.getName())) {
                ServiceInfo<V> serviceInfo;
                V v = parseServiceAttributes(pm.getResourcesForApplication(si.applicationInfo), si.packageName, attrs);
                if (v == null) {
                    serviceInfo = null;
                    if (parser != null) {
                        parser.close();
                    }
                } else {
                    serviceInfo = new ServiceInfo(v, componentName, service.serviceInfo.applicationInfo.uid);
                    if (parser != null) {
                        parser.close();
                    }
                }
                return serviceInfo;
            }
            throw new XmlPullParserException("Meta-data does not start with " + this.mAttributesName + " tag");
        } catch (NameNotFoundException e) {
            throw new XmlPullParserException("Unable to load resources for pacakge " + si.packageName);
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
        }
    }

    private void readPersistentServicesLocked() {
        this.mUserServices.clear();
        if (this.mSerializerAndParser != null) {
            FileInputStream fis = null;
            try {
                this.mPersistentServicesFileDidNotExist = !this.mPersistentServicesFile.getBaseFile().exists() ? true : DEBUG;
                if (!this.mPersistentServicesFileDidNotExist) {
                    fis = this.mPersistentServicesFile.openRead();
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(fis, null);
                    int eventType = parser.getEventType();
                    while (eventType != 2 && eventType != 1) {
                        eventType = parser.next();
                    }
                    if ("services".equals(parser.getName())) {
                        eventType = parser.next();
                        do {
                            if (eventType == 2 && parser.getDepth() == 2) {
                                if (Notification.CATEGORY_SERVICE.equals(parser.getName())) {
                                    V service = this.mSerializerAndParser.createFromXml(parser);
                                    if (service == null) {
                                        break;
                                    }
                                    int uid = Integer.parseInt(parser.getAttributeValue(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID));
                                    findOrCreateUserLocked(UserHandle.getUserId(uid)).persistentServices.put(service, Integer.valueOf(uid));
                                }
                            }
                            eventType = parser.next();
                        } while (eventType != 1);
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                        }
                    }
                } else if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Exception e3) {
                Log.w(TAG, "Error reading persistent services, starting from scratch", e3);
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e4) {
                    }
                }
            } catch (Throwable th) {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e5) {
                    }
                }
            }
        }
    }

    private void writePersistentServicesLocked() {
        if (this.mSerializerAndParser != null) {
            FileOutputStream fos = null;
            try {
                fos = this.mPersistentServicesFile.startWrite();
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(fos, "utf-8");
                out.startDocument(null, Boolean.valueOf(true));
                out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                out.startTag(null, "services");
                for (int i = 0; i < this.mUserServices.size(); i++) {
                    for (Entry<V, Integer> service : ((UserServices) this.mUserServices.valueAt(i)).persistentServices.entrySet()) {
                        out.startTag(null, Notification.CATEGORY_SERVICE);
                        out.attribute(null, GrantCredentialsPermissionActivity.EXTRAS_REQUESTING_UID, Integer.toString(((Integer) service.getValue()).intValue()));
                        this.mSerializerAndParser.writeAsXml(service.getKey(), out);
                        out.endTag(null, Notification.CATEGORY_SERVICE);
                    }
                }
                out.endTag(null, "services");
                out.endDocument();
                this.mPersistentServicesFile.finishWrite(fos);
            } catch (IOException e1) {
                Log.w(TAG, "Error writing accounts", e1);
                if (fos != null) {
                    this.mPersistentServicesFile.failWrite(fos);
                }
            }
        }
    }
}
