package javax.security.auth;

import java.security.Permission;
import org.xmlpull.v1.XmlPullParser;

public final class PrivateCredentialPermission extends Permission {
    public PrivateCredentialPermission(String name, String action) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public String[][] getPrincipals() {
        return null;
    }

    public String getCredentialClass() {
        return null;
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
