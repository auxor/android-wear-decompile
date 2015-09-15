package com.android.org.conscrypt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import libcore.io.IoUtils;

public final class TrustedCertificateStore {
    private static final CertificateFactory CERT_FACTORY = null;
    private static final String PREFIX_SYSTEM = "system:";
    private static final String PREFIX_USER = "user:";
    private static File defaultCaCertsAddedDir;
    private static File defaultCaCertsDeletedDir;
    private static File defaultCaCertsSystemDir;
    private final File addedDir;
    private final File deletedDir;
    private final File systemDir;

    private interface CertSelector {
        boolean match(X509Certificate x509Certificate);
    }

    /* renamed from: com.android.org.conscrypt.TrustedCertificateStore.1 */
    class AnonymousClass1 implements CertSelector {
        final /* synthetic */ X509Certificate val$x;

        AnonymousClass1(X509Certificate x509Certificate) {
            this.val$x = x509Certificate;
        }

        public boolean match(X509Certificate cert) {
            return cert.equals(this.val$x);
        }
    }

    /* renamed from: com.android.org.conscrypt.TrustedCertificateStore.2 */
    class AnonymousClass2 implements CertSelector {
        final /* synthetic */ X509Certificate val$c;

        AnonymousClass2(X509Certificate x509Certificate) {
            this.val$c = x509Certificate;
        }

        public boolean match(X509Certificate ca) {
            return ca.getPublicKey().equals(this.val$c.getPublicKey());
        }
    }

    /* renamed from: com.android.org.conscrypt.TrustedCertificateStore.3 */
    class AnonymousClass3 implements CertSelector {
        final /* synthetic */ TrustedCertificateStore this$0;
        final /* synthetic */ X509Certificate val$c;

        AnonymousClass3(com.android.org.conscrypt.TrustedCertificateStore r1, java.security.cert.X509Certificate r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.TrustedCertificateStore.3.<init>(com.android.org.conscrypt.TrustedCertificateStore, java.security.cert.X509Certificate):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.TrustedCertificateStore.3.<init>(com.android.org.conscrypt.TrustedCertificateStore, java.security.cert.X509Certificate):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.TrustedCertificateStore.3.<init>(com.android.org.conscrypt.TrustedCertificateStore, java.security.cert.X509Certificate):void");
        }

        public boolean match(java.security.cert.X509Certificate r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.TrustedCertificateStore.3.match(java.security.cert.X509Certificate):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.TrustedCertificateStore.3.match(java.security.cert.X509Certificate):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.TrustedCertificateStore.3.match(java.security.cert.X509Certificate):boolean");
        }
    }

    public static final boolean isSystem(String alias) {
        return alias.startsWith(PREFIX_SYSTEM);
    }

    public static final boolean isUser(String alias) {
        return alias.startsWith(PREFIX_USER);
    }

    static {
        String ANDROID_ROOT = System.getenv("ANDROID_ROOT");
        String ANDROID_DATA = System.getenv("ANDROID_DATA");
        defaultCaCertsSystemDir = new File(ANDROID_ROOT + "/etc/security/cacerts");
        setDefaultUserDirectory(new File(ANDROID_DATA + "/misc/keychain"));
        try {
            CERT_FACTORY = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            throw new AssertionError(e);
        }
    }

    public static void setDefaultUserDirectory(File root) {
        defaultCaCertsAddedDir = new File(root, "cacerts-added");
        defaultCaCertsDeletedDir = new File(root, "cacerts-removed");
    }

    public TrustedCertificateStore() {
        this(defaultCaCertsSystemDir, defaultCaCertsAddedDir, defaultCaCertsDeletedDir);
    }

    public TrustedCertificateStore(File systemDir, File addedDir, File deletedDir) {
        this.systemDir = systemDir;
        this.addedDir = addedDir;
        this.deletedDir = deletedDir;
    }

    public Certificate getCertificate(String alias) {
        return getCertificate(alias, false);
    }

    public Certificate getCertificate(String alias, boolean includeDeletedSystem) {
        File file = fileForAlias(alias);
        if (file == null || (isUser(alias) && isTombstone(file))) {
            return null;
        }
        Certificate cert = readCertificate(file);
        if (cert == null || (isSystem(alias) && !includeDeletedSystem && isDeletedSystemCertificate(cert))) {
            return null;
        }
        return cert;
    }

    private File fileForAlias(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        File file;
        if (isSystem(alias)) {
            file = new File(this.systemDir, alias.substring(PREFIX_SYSTEM.length()));
        } else if (!isUser(alias)) {
            return null;
        } else {
            file = new File(this.addedDir, alias.substring(PREFIX_USER.length()));
        }
        if (!file.exists() || isTombstone(file)) {
            return null;
        }
        return file;
    }

    private boolean isTombstone(File file) {
        return file.length() == 0;
    }

    private X509Certificate readCertificate(File file) {
        Throwable th;
        if (!file.isFile()) {
            return null;
        }
        InputStream is = null;
        try {
            InputStream is2 = new BufferedInputStream(new FileInputStream(file));
            try {
                X509Certificate x509Certificate = (X509Certificate) CERT_FACTORY.generateCertificate(is2);
                IoUtils.closeQuietly(is2);
                return x509Certificate;
            } catch (IOException e) {
                is = is2;
                IoUtils.closeQuietly(is);
                return null;
            } catch (CertificateException e2) {
                is = is2;
                IoUtils.closeQuietly(is);
                return null;
            } catch (Throwable th2) {
                th = th2;
                is = is2;
                IoUtils.closeQuietly(is);
                throw th;
            }
        } catch (IOException e3) {
            IoUtils.closeQuietly(is);
            return null;
        } catch (CertificateException e4) {
            IoUtils.closeQuietly(is);
            return null;
        } catch (Throwable th3) {
            th = th3;
            IoUtils.closeQuietly(is);
            throw th;
        }
    }

    private void writeCertificate(File file, X509Certificate cert) throws IOException, CertificateException {
        Throwable th;
        File dir = file.getParentFile();
        dir.mkdirs();
        dir.setReadable(true, false);
        dir.setExecutable(true, false);
        OutputStream os = null;
        try {
            OutputStream os2 = new FileOutputStream(file);
            try {
                os2.write(cert.getEncoded());
                IoUtils.closeQuietly(os2);
                file.setReadable(true, false);
            } catch (Throwable th2) {
                th = th2;
                os = os2;
                IoUtils.closeQuietly(os);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            IoUtils.closeQuietly(os);
            throw th;
        }
    }

    private boolean isDeletedSystemCertificate(X509Certificate x) {
        return getCertificateFile(this.deletedDir, x).exists();
    }

    public Date getCreationDate(String alias) {
        if (!containsAlias(alias)) {
            return null;
        }
        File file = fileForAlias(alias);
        if (file == null) {
            return null;
        }
        long time = file.lastModified();
        if (time != 0) {
            return new Date(time);
        }
        return null;
    }

    public Set<String> aliases() {
        Set<String> result = new HashSet();
        addAliases(result, PREFIX_USER, this.addedDir);
        addAliases(result, PREFIX_SYSTEM, this.systemDir);
        return result;
    }

    public Set<String> userAliases() {
        Set<String> result = new HashSet();
        addAliases(result, PREFIX_USER, this.addedDir);
        return result;
    }

    private void addAliases(Set<String> result, String prefix, File dir) {
        String[] files = dir.list();
        if (files != null) {
            for (String filename : files) {
                String alias = prefix + filename;
                if (containsAlias(alias)) {
                    result.add(alias);
                }
            }
        }
    }

    public Set<String> allSystemAliases() {
        Set<String> result = new HashSet();
        String[] files = this.systemDir.list();
        if (files != null) {
            for (String filename : files) {
                String alias = PREFIX_SYSTEM + filename;
                if (containsAlias(alias, true)) {
                    result.add(alias);
                }
            }
        }
        return result;
    }

    public boolean containsAlias(String alias) {
        return containsAlias(alias, false);
    }

    private boolean containsAlias(String alias, boolean includeDeletedSystem) {
        return getCertificate(alias, includeDeletedSystem) != null;
    }

    public String getCertificateAlias(Certificate c) {
        return getCertificateAlias(c, false);
    }

    public String getCertificateAlias(Certificate c, boolean includeDeletedSystem) {
        if (c == null || !(c instanceof X509Certificate)) {
            return null;
        }
        X509Certificate x = (X509Certificate) c;
        File user = getCertificateFile(this.addedDir, x);
        if (user.exists()) {
            return PREFIX_USER + user.getName();
        }
        if (!includeDeletedSystem && isDeletedSystemCertificate(x)) {
            return null;
        }
        File system = getCertificateFile(this.systemDir, x);
        if (system.exists()) {
            return PREFIX_SYSTEM + system.getName();
        }
        return null;
    }

    public boolean isUserAddedCertificate(X509Certificate cert) {
        return getCertificateFile(this.addedDir, cert).exists();
    }

    private File getCertificateFile(File dir, X509Certificate x) {
        return (File) findCert(dir, x.getSubjectX500Principal(), new AnonymousClass1(x), File.class);
    }

    public X509Certificate getTrustAnchor(X509Certificate c) {
        CertSelector selector = new AnonymousClass2(c);
        X509Certificate user = (X509Certificate) findCert(this.addedDir, c.getSubjectX500Principal(), selector, X509Certificate.class);
        if (user != null) {
            return user;
        }
        X509Certificate system = (X509Certificate) findCert(this.systemDir, c.getSubjectX500Principal(), selector, X509Certificate.class);
        if (system == null || isDeletedSystemCertificate(system)) {
            return null;
        }
        return system;
    }

    public X509Certificate findIssuer(X509Certificate c) {
        CertSelector selector = new AnonymousClass3(this, c);
        X500Principal issuer = c.getIssuerX500Principal();
        X509Certificate user = (X509Certificate) findCert(this.addedDir, issuer, selector, X509Certificate.class);
        if (user != null) {
            return user;
        }
        X509Certificate system = (X509Certificate) findCert(this.systemDir, issuer, selector, X509Certificate.class);
        if (system == null || isDeletedSystemCertificate(system)) {
            return null;
        }
        return system;
    }

    private static boolean isSelfIssuedCertificate(OpenSSLX509Certificate cert) {
        long ctx = cert.getContext();
        return NativeCrypto.X509_check_issued(ctx, ctx) == 0;
    }

    private static OpenSSLX509Certificate convertToOpenSSLIfNeeded(X509Certificate cert) throws CertificateException {
        if (cert == null) {
            return null;
        }
        if (cert instanceof OpenSSLX509Certificate) {
            return (OpenSSLX509Certificate) cert;
        }
        try {
            return OpenSSLX509Certificate.fromX509Der(cert.getEncoded());
        } catch (Exception e) {
            throw new CertificateException(e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<java.security.cert.X509Certificate> getCertificateChain(java.security.cert.X509Certificate r4) throws java.security.cert.CertificateException {
        /*
        r3 = this;
        r1 = new java.util.LinkedHashSet;
        r1.<init>();
        r0 = convertToOpenSSLIfNeeded(r4);
        r1.add(r0);
    L_0x000c:
        r2 = isSelfIssuedCertificate(r0);
        if (r2 == 0) goto L_0x0018;
    L_0x0012:
        r2 = new java.util.ArrayList;
        r2.<init>(r1);
        return r2;
    L_0x0018:
        r2 = r3.findIssuer(r0);
        r0 = convertToOpenSSLIfNeeded(r2);
        if (r0 == 0) goto L_0x0012;
    L_0x0022:
        r2 = r1.contains(r0);
        if (r2 != 0) goto L_0x0012;
    L_0x0028:
        r1.add(r0);
        goto L_0x000c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.TrustedCertificateStore.getCertificateChain(java.security.cert.X509Certificate):java.util.List<java.security.cert.X509Certificate>");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private <T> T findCert(java.io.File r6, javax.security.auth.x500.X500Principal r7, com.android.org.conscrypt.TrustedCertificateStore.CertSelector r8, java.lang.Class<T> r9) {
        /*
        r5 = this;
        r2 = r5.hash(r7);
        r3 = 0;
    L_0x0005:
        r1 = r5.file(r6, r2, r3);
        r4 = r1.isFile();
        if (r4 != 0) goto L_0x001c;
    L_0x000f:
        r4 = java.lang.Boolean.class;
        if (r9 != r4) goto L_0x0016;
    L_0x0013:
        r1 = java.lang.Boolean.FALSE;
    L_0x0015:
        return r1;
    L_0x0016:
        r4 = java.io.File.class;
        if (r9 == r4) goto L_0x0015;
    L_0x001a:
        r1 = 0;
        goto L_0x0015;
    L_0x001c:
        r4 = r5.isTombstone(r1);
        if (r4 == 0) goto L_0x0025;
    L_0x0022:
        r3 = r3 + 1;
        goto L_0x0005;
    L_0x0025:
        r0 = r5.readCertificate(r1);
        if (r0 == 0) goto L_0x0022;
    L_0x002b:
        r4 = r8.match(r0);
        if (r4 == 0) goto L_0x0022;
    L_0x0031:
        r4 = java.security.cert.X509Certificate.class;
        if (r9 != r4) goto L_0x0037;
    L_0x0035:
        r1 = r0;
        goto L_0x0015;
    L_0x0037:
        r4 = java.lang.Boolean.class;
        if (r9 != r4) goto L_0x003e;
    L_0x003b:
        r1 = java.lang.Boolean.TRUE;
        goto L_0x0015;
    L_0x003e:
        r4 = java.io.File.class;
        if (r9 == r4) goto L_0x0015;
    L_0x0042:
        r4 = new java.lang.AssertionError;
        r4.<init>();
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.TrustedCertificateStore.findCert(java.io.File, javax.security.auth.x500.X500Principal, com.android.org.conscrypt.TrustedCertificateStore$CertSelector, java.lang.Class):T");
    }

    private String hash(X500Principal name) {
        return IntegralToString.intToHexString(NativeCrypto.X509_NAME_hash_old(name), false, 8);
    }

    private File file(File dir, String hash, int index) {
        return new File(dir, hash + '.' + index);
    }

    public void installCertificate(X509Certificate cert) throws IOException, CertificateException {
        if (cert == null) {
            throw new NullPointerException("cert == null");
        } else if (getCertificateFile(this.systemDir, cert).exists()) {
            File deleted = getCertificateFile(this.deletedDir, cert);
            if (deleted.exists() && !deleted.delete()) {
                throw new IOException("Could not remove " + deleted);
            }
        } else {
            File user = getCertificateFile(this.addedDir, cert);
            if (!user.exists()) {
                writeCertificate(user, cert);
            }
        }
    }

    public void deleteCertificateEntry(String alias) throws IOException, CertificateException {
        if (alias != null) {
            File file = fileForAlias(alias);
            if (file == null) {
                return;
            }
            if (isSystem(alias)) {
                X509Certificate cert = readCertificate(file);
                if (cert != null) {
                    File deleted = getCertificateFile(this.deletedDir, cert);
                    if (!deleted.exists()) {
                        writeCertificate(deleted, cert);
                    }
                }
            } else if (isUser(alias)) {
                new FileOutputStream(file).close();
                removeUnnecessaryTombstones(alias);
            }
        }
    }

    private void removeUnnecessaryTombstones(String alias) throws IOException {
        if (isUser(alias)) {
            int dotIndex = alias.lastIndexOf(46);
            if (dotIndex == -1) {
                throw new AssertionError(alias);
            }
            String hash = alias.substring(PREFIX_USER.length(), dotIndex);
            int lastTombstoneIndex = Integer.parseInt(alias.substring(dotIndex + 1));
            if (!file(this.addedDir, hash, lastTombstoneIndex + 1).exists()) {
                while (lastTombstoneIndex >= 0) {
                    File file = file(this.addedDir, hash, lastTombstoneIndex);
                    if (!isTombstone(file)) {
                        return;
                    }
                    if (file.delete()) {
                        lastTombstoneIndex--;
                    } else {
                        throw new IOException("Could not remove " + file);
                    }
                }
                return;
            }
            return;
        }
        throw new AssertionError(alias);
    }
}
