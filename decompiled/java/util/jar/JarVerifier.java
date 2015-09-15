package java.util.jar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.jar.Attributes.Name;
import libcore.io.Base64;
import org.apache.harmony.security.utils.JarUtils;

class JarVerifier {
    private static final String[] DIGEST_ALGORITHMS;
    private final Hashtable<String, Certificate[]> certificates;
    private final String jarName;
    private final int mainAttributesEnd;
    private final Manifest manifest;
    private final HashMap<String, byte[]> metaEntries;
    private final Hashtable<String, HashMap<String, Attributes>> signatures;
    private final Hashtable<String, Certificate[][]> verifiedEntries;

    static class VerifierEntry extends OutputStream {
        private final Certificate[][] certChains;
        private final MessageDigest digest;
        private final byte[] hash;
        private final String name;
        private final Hashtable<String, Certificate[][]> verifiedEntries;

        VerifierEntry(String name, MessageDigest digest, byte[] hash, Certificate[][] certChains, Hashtable<String, Certificate[][]> verifedEntries) {
            this.name = name;
            this.digest = digest;
            this.hash = hash;
            this.certChains = certChains;
            this.verifiedEntries = verifedEntries;
        }

        public void write(int value) {
            this.digest.update((byte) value);
        }

        public void write(byte[] buf, int off, int nbytes) {
            this.digest.update(buf, off, nbytes);
        }

        void verify() {
            if (MessageDigest.isEqual(this.digest.digest(), Base64.decode(this.hash))) {
                this.verifiedEntries.put(this.name, this.certChains);
                return;
            }
            throw JarVerifier.invalidDigest(JarFile.MANIFEST_NAME, this.name, this.name);
        }
    }

    static {
        DIGEST_ALGORITHMS = new String[]{"SHA-512", "SHA-384", "SHA-256", "SHA1"};
    }

    private static SecurityException invalidDigest(String signatureFile, String name, String jarName) {
        throw new SecurityException(signatureFile + " has invalid digest for " + name + " in " + jarName);
    }

    private static SecurityException failedVerification(String jarName, String signatureFile) {
        throw new SecurityException(jarName + " failed verification of " + signatureFile);
    }

    JarVerifier(String name, Manifest manifest, HashMap<String, byte[]> metaEntries) {
        this.signatures = new Hashtable(5);
        this.certificates = new Hashtable(5);
        this.verifiedEntries = new Hashtable();
        this.jarName = name;
        this.manifest = manifest;
        this.metaEntries = metaEntries;
        this.mainAttributesEnd = manifest.getMainAttributesEnd();
    }

    VerifierEntry initEntry(String name) {
        if (this.manifest == null || this.signatures.isEmpty()) {
            return null;
        }
        Attributes attributes = this.manifest.getAttributes(name);
        if (attributes == null) {
            return null;
        }
        ArrayList<Certificate[]> certChains = new ArrayList();
        for (Entry<String, HashMap<String, Attributes>> entry : this.signatures.entrySet()) {
            if (((HashMap) entry.getValue()).get(name) != null) {
                Certificate[] certChain = (Certificate[]) this.certificates.get((String) entry.getKey());
                if (certChain != null) {
                    certChains.add(certChain);
                }
            }
        }
        if (certChains.isEmpty()) {
            return null;
        }
        Certificate[][] certChainsArray = (Certificate[][]) certChains.toArray(new Certificate[certChains.size()][]);
        for (String algorithm : DIGEST_ALGORITHMS) {
            String hash = attributes.getValue(algorithm + "-Digest");
            if (hash != null) {
                try {
                    return new VerifierEntry(name, MessageDigest.getInstance(algorithm), hash.getBytes(StandardCharsets.ISO_8859_1), certChainsArray, this.verifiedEntries);
                } catch (NoSuchAlgorithmException e) {
                }
            }
        }
        return null;
    }

    void addMetaEntry(String name, byte[] buf) {
        this.metaEntries.put(name.toUpperCase(Locale.US), buf);
    }

    synchronized boolean readCertificates() {
        boolean z;
        if (this.metaEntries.isEmpty()) {
            z = false;
        } else {
            Iterator<String> it = this.metaEntries.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.endsWith(".DSA") || key.endsWith(".RSA") || key.endsWith(".EC")) {
                    verifyCertificate(key);
                    it.remove();
                }
            }
            z = true;
        }
        return z;
    }

    private void verifyCertificate(String certFile) {
        String signatureFile = certFile.substring(0, certFile.lastIndexOf(46)) + ".SF";
        byte[] sfBytes = (byte[]) this.metaEntries.get(signatureFile);
        if (sfBytes != null) {
            byte[] manifestBytes = (byte[]) this.metaEntries.get(JarFile.MANIFEST_NAME);
            if (manifestBytes != null) {
                try {
                    Object signerCertChain = JarUtils.verifySignature(new ByteArrayInputStream(sfBytes), new ByteArrayInputStream((byte[]) this.metaEntries.get(certFile)));
                    if (signerCertChain != null) {
                        this.certificates.put(signatureFile, signerCertChain);
                    }
                    Attributes attributes = new Attributes();
                    HashMap<String, Attributes> entries = new HashMap();
                    try {
                        new ManifestReader(sfBytes, attributes).readEntries(entries, null);
                        if (attributes.get(Name.SIGNATURE_VERSION) != null) {
                            boolean createdBySigntool = false;
                            String createdBy = attributes.getValue("Created-By");
                            if (createdBy != null) {
                                createdBySigntool = createdBy.indexOf("signtool") != -1;
                            }
                            if (this.mainAttributesEnd <= 0 || createdBySigntool || verify(attributes, "-Digest-Manifest-Main-Attributes", manifestBytes, 0, this.mainAttributesEnd, false, true)) {
                                if (!verify(attributes, createdBySigntool ? "-Digest" : "-Digest-Manifest", manifestBytes, 0, manifestBytes.length, false, false)) {
                                    for (Entry<String, Attributes> entry : entries.entrySet()) {
                                        Chunk chunk = this.manifest.getChunk((String) entry.getKey());
                                        if (chunk != null) {
                                            if (!verify((Attributes) entry.getValue(), "-Digest", manifestBytes, chunk.start, chunk.end, createdBySigntool, false)) {
                                                throw invalidDigest(signatureFile, (String) entry.getKey(), this.jarName);
                                            }
                                        }
                                        return;
                                    }
                                }
                                this.metaEntries.put(signatureFile, null);
                                this.signatures.put(signatureFile, entries);
                                return;
                            }
                            throw failedVerification(this.jarName, signatureFile);
                        }
                    } catch (IOException e) {
                    }
                } catch (IOException e2) {
                } catch (GeneralSecurityException e3) {
                    throw failedVerification(this.jarName, signatureFile);
                }
            }
        }
    }

    boolean isSignedJar() {
        return this.certificates.size() > 0;
    }

    private boolean verify(Attributes attributes, String entry, byte[] data, int start, int end, boolean ignoreSecondEndline, boolean ignorable) {
        for (String algorithm : DIGEST_ALGORITHMS) {
            String hash = attributes.getValue(algorithm + entry);
            if (hash != null) {
                try {
                    MessageDigest md = MessageDigest.getInstance(algorithm);
                    if (ignoreSecondEndline && data[end - 1] == 10 && data[end - 2] == 10) {
                        md.update(data, start, (end - 1) - start);
                    } else {
                        md.update(data, start, end - start);
                    }
                    return MessageDigest.isEqual(md.digest(), Base64.decode(hash.getBytes(StandardCharsets.ISO_8859_1)));
                } catch (NoSuchAlgorithmException e) {
                }
            }
        }
        return ignorable;
    }

    Certificate[][] getCertificateChains(String name) {
        return (Certificate[][]) this.verifiedEntries.get(name);
    }

    void removeMetaEntries() {
        this.metaEntries.clear();
    }
}
