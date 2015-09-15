package java.net;

import java.io.Serializable;
import java.security.Permission;
import org.xmlpull.v1.XmlPullParser;

public final class SocketPermission extends Permission implements Serializable {
    public SocketPermission(String host, String action) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
