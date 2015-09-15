package java.net;

import libcore.util.BasicLruCache;

class AddressCache {
    private static final int MAX_ENTRIES = 16;
    private static final long TTL_NANOS = 2000000000;
    private final BasicLruCache<AddressCacheKey, AddressCacheEntry> cache;

    static class AddressCacheEntry {
        final long expiryNanos;
        final Object value;

        AddressCacheEntry(Object value) {
            this.value = value;
            this.expiryNanos = System.nanoTime() + AddressCache.TTL_NANOS;
        }
    }

    static class AddressCacheKey {
        private final String mHostname;
        private final int mNetId;

        AddressCacheKey(String hostname, int netId) {
            this.mHostname = hostname;
            this.mNetId = netId;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof AddressCacheKey)) {
                return false;
            }
            AddressCacheKey lhs = (AddressCacheKey) o;
            if (this.mHostname.equals(lhs.mHostname) && this.mNetId == lhs.mNetId) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((this.mNetId + 527) * 31) + this.mHostname.hashCode();
        }
    }

    AddressCache() {
        this.cache = new BasicLruCache(MAX_ENTRIES);
    }

    public void clear() {
        this.cache.evictAll();
    }

    public Object get(String hostname, int netId) {
        AddressCacheEntry entry = (AddressCacheEntry) this.cache.get(new AddressCacheKey(hostname, netId));
        if (entry == null || entry.expiryNanos < System.nanoTime()) {
            return null;
        }
        return entry.value;
    }

    public void put(String hostname, int netId, InetAddress[] addresses) {
        this.cache.put(new AddressCacheKey(hostname, netId), new AddressCacheEntry(addresses));
    }

    public void putUnknownHost(String hostname, int netId, String detailMessage) {
        this.cache.put(new AddressCacheKey(hostname, netId), new AddressCacheEntry(detailMessage));
    }
}
