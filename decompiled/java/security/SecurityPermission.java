package java.security;

import org.xmlpull.v1.XmlPullParser;

public final class SecurityPermission extends BasicPermission {
    public SecurityPermission(String name) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public SecurityPermission(String name, String action) {
        super(XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
