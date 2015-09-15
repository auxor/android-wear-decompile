package java.security;

import java.io.Serializable;
import java.security.cert.Certificate;
import org.xmlpull.v1.XmlPullParser;

public final class UnresolvedPermission extends Permission implements Serializable {
    public UnresolvedPermission(String type, String name, String actions, Certificate[] certs) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public String getUnresolvedName() {
        return null;
    }

    public String getUnresolvedActions() {
        return null;
    }

    public String getUnresolvedType() {
        return null;
    }

    public Certificate[] getUnresolvedCerts() {
        return null;
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
