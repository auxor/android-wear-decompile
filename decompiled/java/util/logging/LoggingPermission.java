package java.util.logging;

import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Guard;
import java.security.Permission;
import org.xmlpull.v1.XmlPullParser;

public final class LoggingPermission extends BasicPermission implements Guard, Serializable {
    public LoggingPermission(String name, String actions) {
        super(XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
