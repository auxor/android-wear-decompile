package java.util.jar;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;

public class JarEntry extends ZipEntry {
    private Attributes attributes;
    private CertificateFactory factory;
    private boolean isFactoryChecked;
    final JarFile parentJar;
    CodeSigner[] signers;

    public JarEntry(String name) {
        super(name);
        this.isFactoryChecked = false;
        this.parentJar = null;
    }

    public JarEntry(ZipEntry entry) {
        this(entry, null);
    }

    JarEntry(ZipEntry entry, JarFile parentJar) {
        super(entry);
        this.isFactoryChecked = false;
        this.parentJar = parentJar;
    }

    public JarEntry(JarEntry je) {
        super((ZipEntry) je);
        this.isFactoryChecked = false;
        this.parentJar = je.parentJar;
        this.attributes = je.attributes;
        this.signers = je.signers;
    }

    public Attributes getAttributes() throws IOException {
        if (this.attributes != null || this.parentJar == null) {
            return this.attributes;
        }
        Manifest manifest = this.parentJar.getManifest();
        if (manifest == null) {
            return null;
        }
        Attributes attributes = manifest.getAttributes(getName());
        this.attributes = attributes;
        return attributes;
    }

    public Certificate[] getCertificates() {
        Certificate[] certificateArr = null;
        if (this.parentJar != null) {
            JarVerifier jarVerifier = this.parentJar.verifier;
            if (jarVerifier != null) {
                Certificate[][] certChains = jarVerifier.getCertificateChains(getName());
                if (certChains != null) {
                    int count = 0;
                    for (Certificate[] chain : certChains) {
                        count += chain.length;
                    }
                    Object certs = new Certificate[count];
                    int i = 0;
                    for (Object chain2 : certChains) {
                        System.arraycopy(chain2, 0, certs, i, chain2.length);
                        i += chain2.length;
                    }
                }
            }
        }
        return certificateArr;
    }

    void setAttributes(Attributes attrib) {
        this.attributes = attrib;
    }

    public CodeSigner[] getCodeSigners() {
        if (this.parentJar == null) {
            return null;
        }
        JarVerifier jarVerifier = this.parentJar.verifier;
        if (jarVerifier == null) {
            return null;
        }
        if (this.signers == null) {
            this.signers = getCodeSigners(jarVerifier.getCertificateChains(getName()));
        }
        if (this.signers != null) {
            return (CodeSigner[]) this.signers.clone();
        }
        return null;
    }

    private CodeSigner[] getCodeSigners(Certificate[][] certChains) {
        if (certChains == null) {
            return null;
        }
        ArrayList<CodeSigner> asigners = new ArrayList(certChains.length);
        for (Certificate[] chain : certChains) {
            addCodeSigner(asigners, chain);
        }
        CodeSigner[] tmp = new CodeSigner[asigners.size()];
        asigners.toArray(tmp);
        return tmp;
    }

    private void addCodeSigner(ArrayList<CodeSigner> asigners, Certificate[] certs) {
        Certificate[] arr$ = certs;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            if (arr$[i$] instanceof X509Certificate) {
                i$++;
            } else {
                return;
            }
        }
        CertPath certPath = null;
        if (!this.isFactoryChecked) {
            try {
                this.factory = CertificateFactory.getInstance("X.509");
            } catch (CertificateException e) {
            } finally {
                this.isFactoryChecked = true;
            }
        }
        if (this.factory != null) {
            try {
                certPath = this.factory.generateCertPath(Arrays.asList(certs));
            } catch (CertificateException e2) {
            }
            if (certPath != null) {
                asigners.add(new CodeSigner(certPath, null));
            }
        }
    }
}
