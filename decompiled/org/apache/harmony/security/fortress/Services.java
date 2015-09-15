package org.apache.harmony.security.fortress;

import java.security.Provider;
import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class Services {
    private static int cacheVersion;
    private static Service cachedSecureRandomService;
    private static boolean needRefresh;
    private static final ArrayList<Provider> providers = null;
    private static final HashMap<String, Provider> providersNames = null;
    private static final HashMap<String, ArrayList<Service>> services = null;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.security.fortress.Services.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.security.fortress.Services.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.security.fortress.Services.<clinit>():void");
    }

    public static synchronized ArrayList<Provider> getProviders() {
        ArrayList<Provider> arrayList;
        synchronized (Services.class) {
            arrayList = providers;
        }
        return arrayList;
    }

    public static synchronized Provider getProvider(String name) {
        Provider provider;
        synchronized (Services.class) {
            if (name == null) {
                provider = null;
            } else {
                provider = (Provider) providersNames.get(name);
            }
        }
        return provider;
    }

    public static synchronized int insertProviderAt(Provider provider, int position) {
        synchronized (Services.class) {
            int size = providers.size();
            if (position < 1 || position > size) {
                position = size + 1;
            }
            providers.add(position - 1, provider);
            providersNames.put(provider.getName(), provider);
            setNeedRefresh();
        }
        return position;
    }

    public static synchronized void removeProvider(int providerNumber) {
        synchronized (Services.class) {
            providersNames.remove(((Provider) providers.remove(providerNumber - 1)).getName());
            setNeedRefresh();
        }
    }

    public static synchronized void initServiceInfo(Provider p) {
        synchronized (Services.class) {
            for (Service service : p.getServices()) {
                String type = service.getType();
                if (cachedSecureRandomService == null && type.equals("SecureRandom")) {
                    cachedSecureRandomService = service;
                }
                appendServiceLocked(type + "." + service.getAlgorithm().toUpperCase(Locale.US), service);
                for (String alias : Engine.door.getAliases(service)) {
                    appendServiceLocked(type + "." + alias.toUpperCase(Locale.US), service);
                }
            }
        }
    }

    private static void appendServiceLocked(String key, Service service) {
        ArrayList<Service> serviceList = (ArrayList) services.get(key);
        if (serviceList == null) {
            serviceList = new ArrayList(1);
            services.put(key, serviceList);
        }
        serviceList.add(service);
    }

    public static synchronized boolean isEmpty() {
        boolean isEmpty;
        synchronized (Services.class) {
            isEmpty = services.isEmpty();
        }
        return isEmpty;
    }

    public static synchronized ArrayList<Service> getServices(String key) {
        ArrayList<Service> arrayList;
        synchronized (Services.class) {
            arrayList = (ArrayList) services.get(key);
        }
        return arrayList;
    }

    public static synchronized Service getSecureRandomService() {
        Service service;
        synchronized (Services.class) {
            getCacheVersion();
            service = cachedSecureRandomService;
        }
        return service;
    }

    public static synchronized void setNeedRefresh() {
        synchronized (Services.class) {
            needRefresh = true;
        }
    }

    public static synchronized int getCacheVersion() {
        int i;
        synchronized (Services.class) {
            if (needRefresh) {
                cacheVersion++;
                synchronized (services) {
                    services.clear();
                }
                cachedSecureRandomService = null;
                Iterator i$ = providers.iterator();
                while (i$.hasNext()) {
                    initServiceInfo((Provider) i$.next());
                }
                needRefresh = false;
            }
            i = cacheVersion;
        }
        return i;
    }
}
