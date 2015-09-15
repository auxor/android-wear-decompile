package java.net;

import android.system.OsConstants;
import dalvik.bytecode.Opcodes;
import java.io.ObjectStreamException;
import java.nio.ByteOrder;
import libcore.io.Memory;
import org.apache.harmony.security.asn1.ASN1Constants;

public final class Inet4Address extends InetAddress {
    public static final InetAddress ALL;
    public static final InetAddress ANY;
    public static final InetAddress LOOPBACK;
    private static final long serialVersionUID = 3286316764910316507L;

    static {
        ANY = new Inet4Address(new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0}, null);
        ALL = new Inet4Address(new byte[]{(byte) -1, (byte) -1, (byte) -1, (byte) -1}, null);
        LOOPBACK = new Inet4Address(new byte[]{Byte.MAX_VALUE, (byte) 0, (byte) 0, (byte) 1}, "localhost");
    }

    Inet4Address(byte[] ipaddress, String hostName) {
        super(OsConstants.AF_INET, ipaddress, hostName);
    }

    public boolean isAnyLocalAddress() {
        return this.ipaddress[0] == null && this.ipaddress[1] == null && this.ipaddress[2] == null && this.ipaddress[3] == null;
    }

    public boolean isLinkLocalAddress() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_DIV_FLOAT && (this.ipaddress[1] & Opcodes.OP_CONST_CLASS_JUMBO) == 254;
    }

    public boolean isLoopbackAddress() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Float.MAX_EXPONENT;
    }

    public boolean isMCGlobal() {
        if (!isMulticastAddress()) {
            return false;
        }
        int address = Memory.peekInt(this.ipaddress, 0, ByteOrder.BIG_ENDIAN);
        if ((address >>> 8) < 14680065 || (address >>> 24) > Opcodes.OP_EXECUTE_INLINE) {
            return false;
        }
        return true;
    }

    public boolean isMCLinkLocal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_SHL_INT_LIT8 && this.ipaddress[1] == null && this.ipaddress[2] == null;
    }

    public boolean isMCNodeLocal() {
        return false;
    }

    public boolean isMCOrgLocal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_EXECUTE_INLINE_RANGE && (this.ipaddress[1] & 252) == ASN1Constants.CLASS_PRIVATE;
    }

    public boolean isMCSiteLocal() {
        return (this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_EXECUTE_INLINE_RANGE && (this.ipaddress[1] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_CONST_CLASS_JUMBO;
    }

    public boolean isMulticastAddress() {
        return (this.ipaddress[0] & Opcodes.OP_INVOKE_DIRECT_EMPTY) == Opcodes.OP_SHL_INT_LIT8;
    }

    public boolean isSiteLocalAddress() {
        if ((this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == 10) {
            return true;
        }
        if ((this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_SUB_DOUBLE && (this.ipaddress[1] & Opcodes.OP_INVOKE_DIRECT_EMPTY) == 16) {
            return true;
        }
        if ((this.ipaddress[0] & Opcodes.OP_CONST_CLASS_JUMBO) == ASN1Constants.CLASS_PRIVATE && (this.ipaddress[1] & Opcodes.OP_CONST_CLASS_JUMBO) == Opcodes.OP_MUL_FLOAT) {
            return true;
        }
        return false;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new Inet4Address(this.ipaddress, this.hostName);
    }
}
