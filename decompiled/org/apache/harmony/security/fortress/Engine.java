package org.apache.harmony.security.fortress;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.Locale;

public final class Engine {
    public static SecurityAccess door;
    private volatile ServiceCacheEntry serviceCache;
    private final String serviceName;

    private static final class ServiceCacheEntry {
        private final String algorithm;
        private final int cacheVersion;
        private final ArrayList<Service> services;

        private ServiceCacheEntry(String algorithm, int cacheVersion, ArrayList<Service> services) {
            this.algorithm = algorithm;
            this.cacheVersion = cacheVersion;
            this.services = services;
        }
    }

    public static final class SpiAndProvider {
        public final Provider provider;
        public final Object spi;

        private SpiAndProvider(Object spi, Provider provider) {
            this.spi = spi;
            this.provider = provider;
        }
    }

    public Engine(String serviceName) {
        this.serviceName = serviceName;
    }

    public SpiAndProvider getInstance(String algorithm, Object param) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NoSuchAlgorithmException("Null algorithm name");
        }
        ArrayList<Service> services = getServices(algorithm);
        if (services != null) {
            return new SpiAndProvider(((Service) services.get(0)).getProvider(), null);
        }
        throw notFound(this.serviceName, algorithm);
    }

    public SpiAndProvider getInstance(Service service, String param) throws NoSuchAlgorithmException {
        return new SpiAndProvider(service.getProvider(), null);
    }

    public ArrayList<Service> getServices(String algorithm) {
        int newCacheVersion = Services.getCacheVersion();
        ServiceCacheEntry cacheEntry = this.serviceCache;
        String algoUC = algorithm.toUpperCase(Locale.US);
        if (cacheEntry != null && cacheEntry.algorithm.equalsIgnoreCase(algoUC) && newCacheVersion == cacheEntry.cacheVersion) {
            return cacheEntry.services;
        }
        ArrayList<Service> services = Services.getServices(this.serviceName + "." + algoUC);
        this.serviceCache = new ServiceCacheEntry(newCacheVersion, services, null);
        return services;
    }

    public Object getInstance(String algorithm, Provider provider, Object param) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NoSuchAlgorithmException("algorithm == null");
        }
        Service service = provider.getService(this.serviceName, algorithm);
        if (service != null) {
            return service.newInstance(param);
        }
        throw notFound(this.serviceName, algorithm);
    }

    private NoSuchAlgorithmException notFound(String serviceName, String algorithm) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException(serviceName + " " + algorithm + " implementation not found");
    }
}
