package java.net;

import java.security.BasicPermission;
import java.security.Permission;
import org.xmlpull.v1.XmlPullParser;

public final class NetPermission extends BasicPermission {
    public NetPermission(String name) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public NetPermission(String name, String actions) {
        super(XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
