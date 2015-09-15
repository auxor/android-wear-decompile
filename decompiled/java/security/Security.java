package java.security;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.SecurityAccess;
import org.apache.harmony.security.fortress.Services;
import org.xmlpull.v1.XmlPullParser;

public final class Security {
    private static final Properties secprops;

    private static class SecurityDoor implements SecurityAccess {
        private SecurityDoor() {
        }

        public void renumProviders() {
            Security.renumProviders();
        }

        public List<String> getAliases(Service s) {
            return s.getAliases();
        }

        public Service getService(Provider p, String type) {
            return p.getService(type);
        }
    }

    static {
        secprops = new Properties();
        boolean loaded = false;
        try {
            InputStream configStream = Security.class.getResourceAsStream("security.properties");
            secprops.load(new BufferedInputStream(configStream));
            loaded = true;
            configStream.close();
        } catch (Exception ex) {
            System.logE("Could not load 'security.properties'", ex);
        }
        if (!loaded) {
            registerDefaultProviders();
        }
        Engine.door = new SecurityDoor();
    }

    private Security() {
    }

    private static void registerDefaultProviders() {
        secprops.put("security.provider.1", "com.android.org.conscrypt.OpenSSLProvider");
        secprops.put("security.provider.2", "com.android.org.bouncycastle.jce.provider.BouncyCastleProvider");
        secprops.put("security.provider.3", "org.apache.harmony.security.provider.crypto.CryptoProvider");
        secprops.put("security.provider.4", "com.android.org.conscrypt.JSSEProvider");
    }

    @Deprecated
    public static String getAlgorithmProperty(String algName, String propName) {
        if (algName == null || propName == null) {
            return null;
        }
        String prop = "Alg." + propName + "." + algName;
        for (Provider provider : getProviders()) {
            Enumeration<?> e = provider.propertyNames();
            while (e.hasMoreElements()) {
                String propertyName = (String) e.nextElement();
                if (propertyName.equalsIgnoreCase(prop)) {
                    return provider.getProperty(propertyName);
                }
            }
        }
        return null;
    }

    public static synchronized int insertProviderAt(Provider provider, int position) {
        int i;
        synchronized (Security.class) {
            if (getProvider(provider.getName()) != null) {
                i = -1;
            } else {
                i = Services.insertProviderAt(provider, position);
                renumProviders();
            }
        }
        return i;
    }

    public static int addProvider(Provider provider) {
        return insertProviderAt(provider, 0);
    }

    public static synchronized void removeProvider(String name) {
        synchronized (Security.class) {
            if (name != null) {
                if (name.length() != 0) {
                    Provider p = getProvider(name);
                    if (p != null) {
                        Services.removeProvider(p.getProviderNumber());
                        renumProviders();
                        p.setProviderNumber(-1);
                    }
                }
            }
        }
    }

    public static synchronized Provider[] getProviders() {
        Provider[] providerArr;
        synchronized (Security.class) {
            ArrayList<Provider> providers = Services.getProviders();
            providerArr = (Provider[]) providers.toArray(new Provider[providers.size()]);
        }
        return providerArr;
    }

    public static synchronized Provider getProvider(String name) {
        Provider provider;
        synchronized (Security.class) {
            provider = Services.getProvider(name);
        }
        return provider;
    }

    public static Provider[] getProviders(String filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        } else if (filter.length() == 0) {
            throw new InvalidParameterException();
        } else {
            Map hm = new HashMap();
            int i = filter.indexOf(58);
            if (i == filter.length() - 1 || i == 0) {
                throw new InvalidParameterException();
            }
            if (i < 1) {
                hm.put(filter, XmlPullParser.NO_NAMESPACE);
            } else {
                hm.put(filter.substring(0, i), filter.substring(i + 1));
            }
            return getProviders(hm);
        }
    }

    public static synchronized Provider[] getProviders(Map<String, String> filter) {
        Provider[] providerArr = null;
        synchronized (Security.class) {
            if (filter == null) {
                throw new NullPointerException("filter == null");
            }
            if (!filter.isEmpty()) {
                ArrayList<Provider> result = new ArrayList(Services.getProviders());
                for (Entry<String, String> entry : filter.entrySet()) {
                    String key = (String) entry.getKey();
                    String val = (String) entry.getValue();
                    String attribute = null;
                    int i = key.indexOf(32);
                    int j = key.indexOf(46);
                    if (j == -1) {
                        throw new InvalidParameterException();
                    }
                    if (i == -1) {
                        if (val.length() != 0) {
                            throw new InvalidParameterException();
                        }
                    } else if (val.length() == 0) {
                        throw new InvalidParameterException();
                    } else {
                        attribute = key.substring(i + 1);
                        if (attribute.trim().length() == 0) {
                            throw new InvalidParameterException();
                        }
                        key = key.substring(0, i);
                    }
                    String serv = key.substring(0, j);
                    String alg = key.substring(j + 1);
                    if (serv.length() == 0 || alg.length() == 0) {
                        throw new InvalidParameterException();
                    }
                    filterProviders(result, serv, alg, attribute, val);
                }
                if (result.size() > 0) {
                    providerArr = (Provider[]) result.toArray(new Provider[result.size()]);
                }
            }
        }
        return providerArr;
    }

    private static void filterProviders(ArrayList<Provider> providers, String service, String algorithm, String attribute, String attrValue) {
        Iterator<Provider> it = providers.iterator();
        while (it.hasNext()) {
            if (!((Provider) it.next()).implementsAlg(service, algorithm, attribute, attrValue)) {
                it.remove();
            }
        }
    }

    public static String getProperty(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        String property = secprops.getProperty(key);
        if (property != null) {
            return property.trim();
        }
        return property;
    }

    public static void setProperty(String key, String value) {
        Services.setNeedRefresh();
        secprops.put(key, value);
    }

    public static Set<String> getAlgorithms(String serviceName) {
        Set<String> result = new HashSet();
        if (serviceName != null) {
            for (Provider provider : getProviders()) {
                for (Service service : provider.getServices()) {
                    if (service.getType().equalsIgnoreCase(serviceName)) {
                        result.add(service.getAlgorithm());
                    }
                }
            }
        }
        return result;
    }

    private static void renumProviders() {
        ArrayList<Provider> providers = Services.getProviders();
        for (int i = 0; i < providers.size(); i++) {
            ((Provider) providers.get(i)).setProviderNumber(i + 1);
        }
    }
}
