package java.net;

import android.system.OsConstants;
import dalvik.bytecode.Opcodes;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.util.Arrays;
import java.util.Enumeration;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.w3c.dom.traversal.NodeFilter;

public final class Inet6Address extends InetAddress {
    public static final InetAddress ANY;
    public static final InetAddress LOOPBACK;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 6880410070516793377L;
    private String ifname;
    private int scope_id;
    private boolean scope_id_set;
    private boolean scope_ifname_set;

    static {
        ANY = new Inet6Address(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0}, null, 0);
        LOOPBACK = new Inet6Address(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 1}, "localhost", 0);
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("ipaddress", byte[].class), new ObjectStreamField("scope_id", Integer.TYPE), new ObjectStreamField("scope_id_set", Boolean.TYPE), new ObjectStreamField("scope_ifname_set", Boolean.TYPE), new ObjectStreamField("ifname", String.class)};
    }

    Inet6Address(byte[] ipaddress, String hostName, int scope_id) {
        super(OsConstants.AF_INET6, ipaddress, hostName);
        this.scope_id = scope_id;
        this.scope_id_set = scope_id != 0;
    }

    public static Inet6Address getByAddress(String host, byte[] addr, int scope_id) throws UnknownHostException {
        if (addr == null || addr.length != 16) {
            throw new UnknownHostException("Not an IPv6 address: " + Arrays.toString(addr));
        }
        if (scope_id < 0) {
            scope_id = 0;
        }
        return new Inet6Address(addr, host, scope_id);
    }

    public static Inet6Address getByAddress(String host, byte[] addr, NetworkInterface nif) throws UnknownHostException {
        Inet6Address address = getByAddress(host, addr, 0);
        if (nif != null) {
            Enumeration<InetAddress> addressList = nif.getInetAddresses();
            while (addressList.hasMoreElements()) {
                InetAddress ia = (InetAddress) addressList.nextElement();
                if (ia.getAddress().length == 16) {
                    Inet6Address v6ia = (Inet6Address) ia;
                    if (v6ia.compareLocalType(address)) {
                        address.scope_id_set = true;
                        address.scope_id = v6ia.scope_id;
                        address.scope_ifname_set = true;
                        address.ifname = nif.getName();
                        break;
                    }
                }
            }
            if (!address.scope_id_set) {
                throw new UnknownHostException("Scope id not found for address: " + Arrays.toString(addr));
            }
        }
        return address;
    }

    private boolean compareLocalType(Inet6Address ia) {
        if (ia.isSiteLocalAddress() && isSiteLocalAddress()) {
            return true;
        }
        if (ia.isLinkLocalAddress() && isLinkLocalAddress()) {
            return true;
        }
        if (ia.isSiteLocalAddress() || ia.isLinkLocalAddress()) {
            return false;
        }
        return true;
    }

    public boolean isAnyLocalAddress() {
        return Arrays.equals(this.ipaddress, ANY.ipaddress);
    }

    public boolean isIPv4CompatibleAddress() {
        for (int i = 0; i < 12; i++) {
            if (this.ipaddress[i] != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isLinkLocalAddress() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == 254 && (this.ipaddress[1] & ASN1Constants.CLASS_PRIVATE) == NodeFilter.SHOW_COMMENT;
    }

    public boolean isLoopbackAddress() {
        return Arrays.equals(this.ipaddress, LOOPBACK.ipaddress);
    }

    public boolean isMCGlobal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_CONST_CLASS_JUMBO && (this.ipaddress[1] & 15) == 14;
    }

    public boolean isMCLinkLocal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_CONST_CLASS_JUMBO && (this.ipaddress[1] & 15) == 2;
    }

    public boolean isMCNodeLocal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_CONST_CLASS_JUMBO && (this.ipaddress[1] & 15) == 1;
    }

    public boolean isMCOrgLocal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_CONST_CLASS_JUMBO && (this.ipaddress[1] & 15) == 8;
    }

    public boolean isMCSiteLocal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_CONST_CLASS_JUMBO && (this.ipaddress[1] & 15) == 5;
    }

    public boolean isMulticastAddress() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_CONST_CLASS_JUMBO;
    }

    public boolean isSiteLocalAddress() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == 254 && (this.ipaddress[1] & ASN1Constants.CLASS_PRIVATE) == ASN1Constants.CLASS_PRIVATE;
    }

    public int getScopeId() {
        return this.scope_id_set ? this.scope_id : 0;
    }

    public NetworkInterface getScopedInterface() {
        NetworkInterface networkInterface = null;
        try {
            if (this.scope_ifname_set && this.ifname != null) {
                networkInterface = NetworkInterface.getByName(this.ifname);
            }
        } catch (SocketException e) {
        }
        return networkInterface;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        if (this.ipaddress == null) {
            fields.put("ipaddress", null);
        } else {
            fields.put("ipaddress", this.ipaddress);
        }
        fields.put("scope_id", this.scope_id);
        fields.put("scope_id_set", this.scope_id_set);
        fields.put("scope_ifname_set", this.scope_ifname_set);
        fields.put("ifname", this.ifname);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        this.ipaddress = (byte[]) fields.get("ipaddress", null);
        this.scope_id = fields.get("scope_id", 0);
        this.scope_id_set = fields.get("scope_id_set", false);
        this.ifname = (String) fields.get("ifname", null);
        this.scope_ifname_set = fields.get("scope_ifname_set", false);
    }

    public String toString() {
        if (this.ifname != null) {
            return super.toString() + "%" + this.ifname;
        }
        if (this.scope_id != 0) {
            return super.toString() + "%" + this.scope_id;
        }
        return super.toString();
    }
}
