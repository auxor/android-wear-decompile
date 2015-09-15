package java.net;

import android.system.OsConstants;
import java.nio.charset.StandardCharsets;

public final class InetUnixAddress extends InetAddress {
    public InetUnixAddress(String path) {
        this(path.getBytes(StandardCharsets.UTF_8));
    }

    public InetUnixAddress(byte[] path) {
        super(OsConstants.AF_UNIX, path, null);
    }

    public String toString() {
        return "InetUnixAddress[" + new String(this.ipaddress, StandardCharsets.UTF_8) + "]";
    }
}
