package java.security;

import org.xmlpull.v1.XmlPullParser;

public final class AllPermission extends Permission {
    public AllPermission(String name, String actions) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public AllPermission() {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
