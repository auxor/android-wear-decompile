package java.lang;

import java.security.BasicPermission;
import java.security.Permission;
import org.xmlpull.v1.XmlPullParser;

public final class RuntimePermission extends BasicPermission {
    public RuntimePermission(String permissionName) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public RuntimePermission(String name, String actions) {
        super(XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
