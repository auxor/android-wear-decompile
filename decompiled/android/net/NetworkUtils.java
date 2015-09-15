package android.net;

import android.os.Parcel;
import android.util.Log;
import android.util.Pair;
import android.view.inputmethod.EditorInfo;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Locale;

public class NetworkUtils {
    public static final int RESET_ALL_ADDRESSES = 3;
    public static final int RESET_IPV4_ADDRESSES = 1;
    public static final int RESET_IPV6_ADDRESSES = 2;
    private static final String TAG = "NetworkUtils";

    public static native boolean bindProcessToNetwork(int i);

    public static native boolean bindProcessToNetworkForHostResolution(int i);

    public static native int bindSocketToNetwork(int i, int i2);

    public static native String getDhcpError();

    public static native int getNetworkBoundToProcess();

    public static native boolean protectFromVpn(int i);

    public static native boolean releaseDhcpLease(String str);

    public static native int resetConnections(String str, int i);

    public static native boolean runDhcp(String str, DhcpResults dhcpResults);

    public static native boolean runDhcpRenew(String str, DhcpResults dhcpResults);

    public static native boolean stopDhcp(String str);

    public static InetAddress intToInetAddress(int hostAddress) {
        try {
            return InetAddress.getByAddress(new byte[]{(byte) (hostAddress & EditorInfo.IME_MASK_ACTION), (byte) ((hostAddress >> 8) & EditorInfo.IME_MASK_ACTION), (byte) ((hostAddress >> 16) & EditorInfo.IME_MASK_ACTION), (byte) ((hostAddress >> 24) & EditorInfo.IME_MASK_ACTION)});
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    public static int inetAddressToInt(Inet4Address inetAddr) throws IllegalArgumentException {
        byte[] addr = inetAddr.getAddress();
        return ((((addr[RESET_ALL_ADDRESSES] & EditorInfo.IME_MASK_ACTION) << 24) | ((addr[RESET_IPV6_ADDRESSES] & EditorInfo.IME_MASK_ACTION) << 16)) | ((addr[RESET_IPV4_ADDRESSES] & EditorInfo.IME_MASK_ACTION) << 8)) | (addr[0] & EditorInfo.IME_MASK_ACTION);
    }

    public static int prefixLengthToNetmaskInt(int prefixLength) throws IllegalArgumentException {
        if (prefixLength >= 0 && prefixLength <= 32) {
            return Integer.reverseBytes(-1 << (32 - prefixLength));
        }
        throw new IllegalArgumentException("Invalid prefix length (0 <= prefix <= 32)");
    }

    public static int netmaskIntToPrefixLength(int netmask) {
        return Integer.bitCount(netmask);
    }

    public static InetAddress numericToInetAddress(String addrString) throws IllegalArgumentException {
        return InetAddress.parseNumericAddress(addrString);
    }

    protected static void parcelInetAddress(Parcel parcel, InetAddress address, int flags) {
        parcel.writeByteArray(address != null ? address.getAddress() : null);
    }

    protected static InetAddress unparcelInetAddress(Parcel in) {
        InetAddress inetAddress = null;
        byte[] addressArray = in.createByteArray();
        if (addressArray != null) {
            try {
                inetAddress = InetAddress.getByAddress(addressArray);
            } catch (UnknownHostException e) {
            }
        }
        return inetAddress;
    }

    public static void maskRawAddress(byte[] array, int prefixLength) {
        if (prefixLength < 0 || prefixLength > array.length * 8) {
            throw new RuntimeException("IP address with " + array.length + " bytes has invalid prefix length " + prefixLength);
        }
        int offset = prefixLength / 8;
        byte mask = (byte) (EditorInfo.IME_MASK_ACTION << (8 - (prefixLength % 8)));
        if (offset < array.length) {
            array[offset] = (byte) (array[offset] & mask);
        }
        for (offset += RESET_IPV4_ADDRESSES; offset < array.length; offset += RESET_IPV4_ADDRESSES) {
            array[offset] = (byte) 0;
        }
    }

    public static InetAddress getNetworkPart(InetAddress address, int prefixLength) {
        byte[] array = address.getAddress();
        maskRawAddress(array, prefixLength);
        try {
            return InetAddress.getByAddress(array);
        } catch (UnknownHostException e) {
            throw new RuntimeException("getNetworkPart error - " + e.toString());
        }
    }

    public static Pair<InetAddress, Integer> parseIpAndMask(String ipAndMaskString) {
        InetAddress address = null;
        int prefixLength = -1;
        try {
            String[] pieces = ipAndMaskString.split("/", RESET_IPV6_ADDRESSES);
            prefixLength = Integer.parseInt(pieces[RESET_IPV4_ADDRESSES]);
            address = InetAddress.parseNumericAddress(pieces[0]);
        } catch (NullPointerException e) {
        } catch (ArrayIndexOutOfBoundsException e2) {
        } catch (NumberFormatException e3) {
        } catch (IllegalArgumentException e4) {
        }
        if (address != null && prefixLength != -1) {
            return new Pair(address, Integer.valueOf(prefixLength));
        }
        throw new IllegalArgumentException("Invalid IP address and mask " + ipAndMaskString);
    }

    public static boolean addressTypeMatches(InetAddress left, InetAddress right) {
        return ((left instanceof Inet4Address) && (right instanceof Inet4Address)) || ((left instanceof Inet6Address) && (right instanceof Inet6Address));
    }

    public static InetAddress hexToInet6Address(String addrHexString) throws IllegalArgumentException {
        try {
            return numericToInetAddress(String.format(Locale.US, "%s:%s:%s:%s:%s:%s:%s:%s", new Object[]{addrHexString.substring(0, 4), addrHexString.substring(4, 8), addrHexString.substring(8, 12), addrHexString.substring(12, 16), addrHexString.substring(16, 20), addrHexString.substring(20, 24), addrHexString.substring(24, 28), addrHexString.substring(28, 32)}));
        } catch (Exception e) {
            Log.e(TAG, "error in hexToInet6Address(" + addrHexString + "): " + e);
            throw new IllegalArgumentException(e);
        }
    }

    public static String[] makeStrings(Collection<InetAddress> addrs) {
        String[] result = new String[addrs.size()];
        int i = 0;
        for (InetAddress addr : addrs) {
            int i2 = i + RESET_IPV4_ADDRESSES;
            result[i] = addr.getHostAddress();
            i = i2;
        }
        return result;
    }

    public static String trimV4AddrZeros(String addr) {
        if (addr == null) {
            return null;
        }
        String[] octets = addr.split("\\.");
        if (octets.length != 4) {
            return addr;
        }
        StringBuilder builder = new StringBuilder(16);
        int i = 0;
        while (i < 4) {
            try {
                if (octets[i].length() > RESET_ALL_ADDRESSES) {
                    return addr;
                }
                builder.append(Integer.parseInt(octets[i]));
                if (i < RESET_ALL_ADDRESSES) {
                    builder.append('.');
                }
                i += RESET_IPV4_ADDRESSES;
            } catch (NumberFormatException e) {
                return addr;
            }
        }
        return builder.toString();
    }
}
