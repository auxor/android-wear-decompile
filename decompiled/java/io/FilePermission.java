package java.io;

import java.security.Permission;
import org.xmlpull.v1.XmlPullParser;

public final class FilePermission extends Permission implements Serializable {
    public FilePermission(String path, String actions) {
        super(XmlPullParser.NO_NAMESPACE);
    }

    public String getActions() {
        return null;
    }

    public boolean implies(Permission permission) {
        return true;
    }
}
