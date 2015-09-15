package java.security;

import java.io.Serializable;
import org.xmlpull.v1.XmlPullParser;

public abstract class BasicPermission extends Permission implements Serializable {
    public BasicPermission(String name) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public BasicPermission(String name, String action) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
