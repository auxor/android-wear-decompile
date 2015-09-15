package java.security;

import java.io.Serializable;
import java.net.URL;
import java.security.cert.Certificate;

public class CodeSource implements Serializable {
    public CodeSource(URL location, Certificate[] certs) {
    }

    public CodeSource(URL location, CodeSigner[] signers) {
    }

    public final Certificate[] getCertificates() {
        return null;
    }

    public final CodeSigner[] getCodeSigners() {
        return null;
    }

    public final URL getLocation() {
        return null;
    }

    public boolean implies(CodeSource cs) {
        return true;
    }
}
