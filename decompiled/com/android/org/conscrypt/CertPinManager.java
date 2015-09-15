package com.android.org.conscrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import libcore.io.IoUtils;
import libcore.util.BasicLruCache;

public class CertPinManager {
    private static final boolean DEBUG = false;
    private final TrustedCertificateStore certStore;
    private final Map<String, PinListEntry> entries;
    private final BasicLruCache<String, String> hostnameCache;
    private boolean initialized;
    private long lastModified;
    private final File pinFile;

    public CertPinManager(TrustedCertificateStore store) throws PinManagerException {
        this.entries = new HashMap();
        this.hostnameCache = new BasicLruCache(10);
        this.initialized = false;
        this.pinFile = new File("/data/misc/keychain/pins");
        this.certStore = store;
    }

    public CertPinManager(String path, TrustedCertificateStore store) throws PinManagerException {
        this.entries = new HashMap();
        this.hostnameCache = new BasicLruCache(10);
        this.initialized = false;
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        this.pinFile = new File(path);
        this.certStore = store;
    }

    public boolean isChainValid(String hostname, List<X509Certificate> chain) throws PinManagerException {
        PinListEntry entry = lookup(hostname);
        if (entry == null) {
            return true;
        }
        return entry.isChainValid(chain);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean ensureInitialized() throws com.android.org.conscrypt.PinManagerException {
        /*
        r10 = this;
        r7 = 1;
        monitor-enter(r10);
        r8 = r10.initialized;	 Catch:{ all -> 0x0049 }
        if (r8 == 0) goto L_0x000e;
    L_0x0006:
        r8 = r10.isCacheValid();	 Catch:{ all -> 0x0049 }
        if (r8 == 0) goto L_0x000e;
    L_0x000c:
        monitor-exit(r10);
        return r7;
    L_0x000e:
        r6 = r10.readPinFile();	 Catch:{ all -> 0x0049 }
        if (r6 == 0) goto L_0x005c;
    L_0x0014:
        r0 = getPinFileEntries(r6);	 Catch:{ all -> 0x0049 }
        r4 = r0.length;	 Catch:{ all -> 0x0049 }
        r3 = 0;
    L_0x001a:
        if (r3 >= r4) goto L_0x004c;
    L_0x001c:
        r2 = r0[r3];	 Catch:{ all -> 0x0049 }
        r5 = new com.android.org.conscrypt.PinListEntry;	 Catch:{ PinEntryException -> 0x0031 }
        r7 = r10.certStore;	 Catch:{ PinEntryException -> 0x0031 }
        r5.<init>(r2, r7);	 Catch:{ PinEntryException -> 0x0031 }
        r7 = r10.entries;	 Catch:{ PinEntryException -> 0x0031 }
        r8 = r5.getCommonName();	 Catch:{ PinEntryException -> 0x0031 }
        r7.put(r8, r5);	 Catch:{ PinEntryException -> 0x0031 }
    L_0x002e:
        r3 = r3 + 1;
        goto L_0x001a;
    L_0x0031:
        r1 = move-exception;
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0049 }
        r7.<init>();	 Catch:{ all -> 0x0049 }
        r8 = "Pinlist contains a malformed pin: ";
        r7 = r7.append(r8);	 Catch:{ all -> 0x0049 }
        r7 = r7.append(r2);	 Catch:{ all -> 0x0049 }
        r7 = r7.toString();	 Catch:{ all -> 0x0049 }
        log(r7, r1);	 Catch:{ all -> 0x0049 }
        goto L_0x002e;
    L_0x0049:
        r7 = move-exception;
        monitor-exit(r10);
        throw r7;
    L_0x004c:
        r7 = r10.hostnameCache;	 Catch:{ all -> 0x0049 }
        r7.evictAll();	 Catch:{ all -> 0x0049 }
        r7 = r10.pinFile;	 Catch:{ all -> 0x0049 }
        r8 = r7.lastModified();	 Catch:{ all -> 0x0049 }
        r10.lastModified = r8;	 Catch:{ all -> 0x0049 }
        r7 = 1;
        r10.initialized = r7;	 Catch:{ all -> 0x0049 }
    L_0x005c:
        r7 = r10.initialized;	 Catch:{ all -> 0x0049 }
        goto L_0x000c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.CertPinManager.ensureInitialized():boolean");
    }

    private String readPinFile() throws PinManagerException {
        try {
            return IoUtils.readFileAsString(this.pinFile.getPath());
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e2) {
            throw new PinManagerException("Unexpected error reading pin list; failing.", e2);
        }
    }

    private static String[] getPinFileEntries(String pinFileContents) {
        return pinFileContents.split("\n");
    }

    private synchronized PinListEntry lookup(String hostname) throws PinManagerException {
        PinListEntry pinListEntry = null;
        synchronized (this) {
            if (ensureInitialized()) {
                String cn = (String) this.hostnameCache.get(hostname);
                if (cn != null) {
                    pinListEntry = (PinListEntry) this.entries.get(cn);
                } else {
                    cn = getMatchingCN(hostname);
                    if (cn != null) {
                        this.hostnameCache.put(hostname, cn);
                        pinListEntry = (PinListEntry) this.entries.get(cn);
                    }
                }
            }
        }
        return pinListEntry;
    }

    private boolean isCacheValid() {
        return this.pinFile.lastModified() == this.lastModified;
    }

    private String getMatchingCN(String hostname) {
        String bestMatch = "";
        for (String cn : this.entries.keySet()) {
            if (cn.length() >= bestMatch.length() && isHostnameMatchedBy(hostname, cn)) {
                bestMatch = cn;
            }
        }
        return bestMatch;
    }

    private static boolean isHostnameMatchedBy(String hostName, String cn) {
        if (hostName == null || hostName.isEmpty() || cn == null || cn.isEmpty()) {
            return false;
        }
        cn = cn.toLowerCase(Locale.US);
        if (!cn.contains("*")) {
            return hostName.equals(cn);
        }
        if (cn.startsWith("*.") && hostName.regionMatches(0, cn, 2, cn.length() - 2)) {
            return true;
        }
        int asterisk = cn.indexOf(42);
        if (asterisk > cn.indexOf(46)) {
            return false;
        }
        if (!hostName.regionMatches(0, cn, 0, asterisk)) {
            return false;
        }
        int suffixLength = cn.length() - (asterisk + 1);
        int suffixStart = hostName.length() - suffixLength;
        if (hostName.indexOf(46, asterisk) < suffixStart) {
            return false;
        }
        if (hostName.regionMatches(suffixStart, cn, asterisk + 1, suffixLength)) {
            return true;
        }
        return false;
    }

    private static void log(String s, Exception e) {
    }
}
