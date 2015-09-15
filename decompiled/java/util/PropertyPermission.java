package java.util;

import java.security.BasicPermission;
import java.security.Permission;
import org.xmlpull.v1.XmlPullParser;

public final class PropertyPermission extends BasicPermission {
    public PropertyPermission(String name, String actions) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
