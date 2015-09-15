package java.net;

import android.system.ErrnoException;
import android.system.GaiException;
import android.system.OsConstants;
import android.system.StructAddrinfo;
import dalvik.system.BlockGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import libcore.io.IoBridge;
import libcore.io.Libcore;
import libcore.io.Memory;
import org.xmlpull.v1.XmlPullParser;

public class InetAddress implements Serializable {
    private static final int NETID_UNSET = 0;
    public static final InetAddress UNSPECIFIED = null;
    private static final AddressCache addressCache = null;
    private static final ObjectStreamField[] serialPersistentFields = null;
    private static final long serialVersionUID = 3286316764910316507L;
    private int family;
    String hostName;
    byte[] ipaddress;

    /* renamed from: java.net.InetAddress.1 */
    class AnonymousClass1 extends Thread {
        final /* synthetic */ InetAddress this$0;
        final /* synthetic */ InetAddress val$destinationAddress;
        final /* synthetic */ AtomicBoolean val$isReachable;
        final /* synthetic */ CountDownLatch val$latch;
        final /* synthetic */ InetAddress val$sourceAddress;
        final /* synthetic */ int val$timeout;

        AnonymousClass1(java.net.InetAddress r1, java.net.InetAddress r2, java.net.InetAddress r3, int r4, java.util.concurrent.atomic.AtomicBoolean r5, java.util.concurrent.CountDownLatch r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.InetAddress.1.<init>(java.net.InetAddress, java.net.InetAddress, java.net.InetAddress, int, java.util.concurrent.atomic.AtomicBoolean, java.util.concurrent.CountDownLatch):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.InetAddress.1.<init>(java.net.InetAddress, java.net.InetAddress, java.net.InetAddress, int, java.util.concurrent.atomic.AtomicBoolean, java.util.concurrent.CountDownLatch):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.InetAddress.1.<init>(java.net.InetAddress, java.net.InetAddress, java.net.InetAddress, int, java.util.concurrent.atomic.AtomicBoolean, java.util.concurrent.CountDownLatch):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.InetAddress.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.InetAddress.1.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.net.InetAddress.1.run():void");
        }
    }

    static {
        addressCache = new AddressCache();
        UNSPECIFIED = new InetAddress(OsConstants.AF_UNSPEC, null, null);
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("address", Integer.TYPE), new ObjectStreamField("family", Integer.TYPE), new ObjectStreamField("hostName", String.class)};
    }

    InetAddress(int family, byte[] ipaddress, String hostName) {
        this.family = family;
        this.ipaddress = ipaddress;
        this.hostName = hostName;
    }

    public boolean equals(Object obj) {
        if (obj instanceof InetAddress) {
            return Arrays.equals(this.ipaddress, ((InetAddress) obj).ipaddress);
        }
        return false;
    }

    public byte[] getAddress() {
        return (byte[]) this.ipaddress.clone();
    }

    private static InetAddress[] bytesToInetAddresses(byte[][] rawAddresses, String hostName) throws UnknownHostException {
        InetAddress[] returnedAddresses = new InetAddress[rawAddresses.length];
        for (int i = NETID_UNSET; i < rawAddresses.length; i++) {
            returnedAddresses[i] = makeInetAddress(rawAddresses[i], hostName);
        }
        return returnedAddresses;
    }

    public static InetAddress[] getAllByName(String host) throws UnknownHostException {
        return (InetAddress[]) getAllByNameImpl(host, NETID_UNSET).clone();
    }

    public static InetAddress[] getAllByNameOnNet(String host, int netId) throws UnknownHostException {
        return (InetAddress[]) getAllByNameImpl(host, netId).clone();
    }

    private static InetAddress[] getAllByNameImpl(String host, int netId) throws UnknownHostException {
        if (host == null || host.isEmpty()) {
            return loopbackAddresses();
        }
        InetAddress result = parseNumericAddressNoThrow(host);
        if (result == null) {
            return (InetAddress[]) lookupHostByName(host, netId).clone();
        }
        if (disallowDeprecatedFormats(host, result) == null) {
            throw new UnknownHostException("Deprecated IPv4 address format: " + host);
        }
        return new InetAddress[]{disallowDeprecatedFormats(host, result)};
    }

    private static InetAddress makeInetAddress(byte[] bytes, String hostName) throws UnknownHostException {
        if (bytes.length == 4) {
            return new Inet4Address(bytes, hostName);
        }
        if (bytes.length == 16) {
            return new Inet6Address(bytes, hostName, NETID_UNSET);
        }
        throw badAddressLength(bytes);
    }

    private static InetAddress disallowDeprecatedFormats(String address, InetAddress inetAddress) {
        return ((inetAddress instanceof Inet4Address) && address.indexOf(58) == -1) ? Libcore.os.inet_pton(OsConstants.AF_INET, address) : inetAddress;
    }

    private static InetAddress parseNumericAddressNoThrow(String address) {
        if (address.startsWith("[") && address.endsWith("]") && address.indexOf(58) != -1) {
            address = address.substring(1, address.length() - 1);
        }
        StructAddrinfo hints = new StructAddrinfo();
        hints.ai_flags = OsConstants.AI_NUMERICHOST;
        InetAddress[] addresses = null;
        try {
            addresses = Libcore.os.android_getaddrinfo(address, hints, NETID_UNSET);
        } catch (GaiException e) {
        }
        return addresses != null ? addresses[NETID_UNSET] : null;
    }

    public static InetAddress getByName(String host) throws UnknownHostException {
        return getAllByNameImpl(host, NETID_UNSET)[NETID_UNSET];
    }

    public static InetAddress getByNameOnNet(String host, int netId) throws UnknownHostException {
        return getAllByNameImpl(host, netId)[NETID_UNSET];
    }

    public String getHostAddress() {
        return Libcore.os.getnameinfo(this, OsConstants.NI_NUMERICHOST);
    }

    public String getHostName() {
        if (this.hostName == null) {
            try {
                this.hostName = getHostByAddrImpl(this).hostName;
            } catch (UnknownHostException e) {
                this.hostName = getHostAddress();
            }
        }
        return this.hostName;
    }

    public String getCanonicalHostName() {
        try {
            return getHostByAddrImpl(this).hostName;
        } catch (UnknownHostException e) {
            return getHostAddress();
        }
    }

    public static InetAddress getLocalHost() throws UnknownHostException {
        return lookupHostByName(Libcore.os.uname().nodename, NETID_UNSET)[NETID_UNSET];
    }

    public int hashCode() {
        return Arrays.hashCode(this.ipaddress);
    }

    private static InetAddress[] lookupHostByName(String host, int netId) throws UnknownHostException {
        BlockGuard.getThreadPolicy().onNetwork();
        Object cachedResult = addressCache.get(host, netId);
        if (cachedResult == null) {
            try {
                StructAddrinfo hints = new StructAddrinfo();
                hints.ai_flags = OsConstants.AI_ADDRCONFIG;
                hints.ai_family = OsConstants.AF_UNSPEC;
                hints.ai_socktype = OsConstants.SOCK_STREAM;
                InetAddress[] addresses = Libcore.os.android_getaddrinfo(host, hints, netId);
                InetAddress[] arr$ = addresses;
                int len$ = arr$.length;
                for (int i$ = NETID_UNSET; i$ < len$; i$++) {
                    arr$[i$].hostName = host;
                }
                addressCache.put(host, netId, addresses);
                return addresses;
            } catch (GaiException gaiException) {
                if ((gaiException.getCause() instanceof ErrnoException) && ((ErrnoException) gaiException.getCause()).errno == OsConstants.EACCES) {
                    throw new SecurityException("Permission denied (missing INTERNET permission?)", gaiException);
                }
                String detailMessage = "Unable to resolve host \"" + host + "\": " + Libcore.os.gai_strerror(gaiException.error);
                addressCache.putUnknownHost(host, netId, detailMessage);
                throw gaiException.rethrowAsUnknownHostException(detailMessage);
            }
        } else if (cachedResult instanceof InetAddress[]) {
            return (InetAddress[]) cachedResult;
        } else {
            throw new UnknownHostException((String) cachedResult);
        }
    }

    public static void clearDnsCache() {
        addressCache.clear();
    }

    private static InetAddress getHostByAddrImpl(InetAddress address) throws UnknownHostException {
        BlockGuard.getThreadPolicy().onNetwork();
        try {
            return makeInetAddress((byte[]) address.ipaddress.clone(), Libcore.os.getnameinfo(address, OsConstants.NI_NAMEREQD));
        } catch (GaiException gaiException) {
            throw gaiException.rethrowAsUnknownHostException();
        }
    }

    public String toString() {
        return (this.hostName == null ? XmlPullParser.NO_NAMESPACE : this.hostName) + "/" + getHostAddress();
    }

    public static boolean isNumeric(String address) {
        InetAddress inetAddress = parseNumericAddressNoThrow(address);
        return (inetAddress == null || disallowDeprecatedFormats(address, inetAddress) == null) ? false : true;
    }

    public static InetAddress parseNumericAddress(String numericAddress) {
        if (numericAddress == null || numericAddress.isEmpty()) {
            return Inet6Address.LOOPBACK;
        }
        InetAddress result = disallowDeprecatedFormats(numericAddress, parseNumericAddressNoThrow(numericAddress));
        if (result != null) {
            return result;
        }
        throw new IllegalArgumentException("Not a numeric address: " + numericAddress);
    }

    private static InetAddress[] loopbackAddresses() {
        return new InetAddress[]{Inet6Address.LOOPBACK, Inet4Address.LOOPBACK};
    }

    public static InetAddress getLoopbackAddress() {
        return Inet6Address.LOOPBACK;
    }

    public boolean isAnyLocalAddress() {
        return false;
    }

    public boolean isLinkLocalAddress() {
        return false;
    }

    public boolean isLoopbackAddress() {
        return false;
    }

    public boolean isMCGlobal() {
        return false;
    }

    public boolean isMCLinkLocal() {
        return false;
    }

    public boolean isMCNodeLocal() {
        return false;
    }

    public boolean isMCOrgLocal() {
        return false;
    }

    public boolean isMCSiteLocal() {
        return false;
    }

    public boolean isMulticastAddress() {
        return false;
    }

    public boolean isSiteLocalAddress() {
        return false;
    }

    public boolean isReachable(int timeout) throws IOException {
        return isReachable(null, (int) NETID_UNSET, timeout);
    }

    public boolean isReachable(NetworkInterface networkInterface, int ttl, int timeout) throws IOException {
        if (ttl < 0 || timeout < 0) {
            throw new IllegalArgumentException("ttl < 0 || timeout < 0");
        } else if (networkInterface == null) {
            return isReachable(this, null, timeout);
        } else {
            List<InetAddress> sourceAddresses = Collections.list(networkInterface.getInetAddresses());
            if (sourceAddresses.isEmpty()) {
                return false;
            }
            InetAddress destinationAddress = this;
            CountDownLatch latch = new CountDownLatch(sourceAddresses.size());
            AtomicBoolean isReachable = new AtomicBoolean(false);
            for (InetAddress sourceAddress : sourceAddresses) {
                new AnonymousClass1(this, destinationAddress, sourceAddress, timeout, isReachable, latch).start();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return isReachable.get();
        }
    }

    private boolean isReachable(InetAddress destination, InetAddress source, int timeout) throws IOException {
        FileDescriptor fd = IoBridge.socket(true);
        boolean reached = false;
        if (source != null) {
            try {
                IoBridge.bind(fd, source, NETID_UNSET);
            } catch (IOException e) {
                if (e.getCause() instanceof ErrnoException) {
                    reached = ((ErrnoException) e.getCause()).errno == OsConstants.ECONNREFUSED;
                }
            }
        }
        IoBridge.connect(fd, destination, 7, timeout);
        reached = true;
        IoBridge.closeAndSignalBlockedThreads(fd);
        return reached;
    }

    public static InetAddress getByAddress(byte[] ipAddress) throws UnknownHostException {
        return getByAddress(null, ipAddress, NETID_UNSET);
    }

    public static InetAddress getByAddress(String hostName, byte[] ipAddress) throws UnknownHostException {
        return getByAddress(hostName, ipAddress, NETID_UNSET);
    }

    private static InetAddress getByAddress(String hostName, byte[] ipAddress, int scopeId) throws UnknownHostException {
        if (ipAddress == null) {
            throw new UnknownHostException("ipAddress == null");
        } else if (ipAddress.length == 4) {
            return new Inet4Address((byte[]) ipAddress.clone(), hostName);
        } else {
            if (ipAddress.length != 16) {
                throw badAddressLength(ipAddress);
            } else if (isIPv4MappedAddress(ipAddress)) {
                return new Inet4Address(ipv4MappedToIPv4(ipAddress), hostName);
            } else {
                return new Inet6Address((byte[]) ipAddress.clone(), hostName, scopeId);
            }
        }
    }

    private static UnknownHostException badAddressLength(byte[] bytes) throws UnknownHostException {
        throw new UnknownHostException("Address is neither 4 or 16 bytes: " + Arrays.toString(bytes));
    }

    private static boolean isIPv4MappedAddress(byte[] ipAddress) {
        if (ipAddress == null || ipAddress.length != 16) {
            return false;
        }
        for (int i = NETID_UNSET; i < 10; i++) {
            if (ipAddress[i] != null) {
                return false;
            }
        }
        if (ipAddress[10] == (byte) -1 && ipAddress[11] == (byte) -1) {
            return true;
        }
        return false;
    }

    private static byte[] ipv4MappedToIPv4(byte[] mappedAddress) {
        byte[] ipv4Address = new byte[4];
        for (int i = NETID_UNSET; i < 4; i++) {
            ipv4Address[i] = mappedAddress[i + 12];
        }
        return ipv4Address;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        if (this.ipaddress == null) {
            fields.put("address", (int) NETID_UNSET);
        } else {
            fields.put("address", Memory.peekInt(this.ipaddress, NETID_UNSET, ByteOrder.BIG_ENDIAN));
        }
        fields.put("family", this.family);
        fields.put("hostName", this.hostName);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        int addr = fields.get("address", (int) NETID_UNSET);
        this.ipaddress = new byte[4];
        Memory.pokeInt(this.ipaddress, NETID_UNSET, addr, ByteOrder.BIG_ENDIAN);
        this.hostName = (String) fields.get("hostName", null);
        this.family = fields.get("family", 2);
    }

    private Object readResolve() throws ObjectStreamException {
        return new Inet4Address(this.ipaddress, this.hostName);
    }
}
