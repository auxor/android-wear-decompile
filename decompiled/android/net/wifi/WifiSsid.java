package android.net.wifi;

import android.net.ProxyInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.SpellChecker;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Locale;

public class WifiSsid implements Parcelable {
    public static final Creator<WifiSsid> CREATOR;
    private static final int HEX_RADIX = 16;
    public static final String NONE = "<unknown ssid>";
    private static final String TAG = "WifiSsid";
    public final ByteArrayOutputStream octets;

    private WifiSsid() {
        this.octets = new ByteArrayOutputStream(32);
    }

    public static WifiSsid createFromAsciiEncoded(String asciiEncoded) {
        WifiSsid a = new WifiSsid();
        a.convertToBytes(asciiEncoded);
        return a;
    }

    public static WifiSsid createFromHex(String hexStr) {
        WifiSsid a = new WifiSsid();
        if (hexStr != null) {
            if (hexStr.startsWith("0x") || hexStr.startsWith("0X")) {
                hexStr = hexStr.substring(2);
            }
            for (int i = 0; i < hexStr.length() - 1; i += 2) {
                int val;
                try {
                    val = Integer.parseInt(hexStr.substring(i, i + 2), HEX_RADIX);
                } catch (NumberFormatException e) {
                    val = 0;
                }
                a.octets.write(val);
            }
        }
        return a;
    }

    private void convertToBytes(String asciiEncoded) {
        int i = 0;
        while (i < asciiEncoded.length()) {
            char c = asciiEncoded.charAt(i);
            switch (c) {
                case KeyEvent.KEYCODE_PAGE_UP /*92*/:
                    i++;
                    int val;
                    switch (asciiEncoded.charAt(i)) {
                        case MotionEvent.AXIS_GENERIC_3 /*34*/:
                            this.octets.write(34);
                            i++;
                            break;
                        case LayoutParams.SOFT_INPUT_ADJUST_NOTHING /*48*/:
                        case KeyEvent.KEYCODE_U /*49*/:
                        case SpellChecker.MAX_NUMBER_OF_WORDS /*50*/:
                        case KeyEvent.KEYCODE_W /*51*/:
                        case KeyEvent.KEYCODE_X /*52*/:
                        case KeyEvent.KEYCODE_Y /*53*/:
                        case KeyEvent.KEYCODE_Z /*54*/:
                        case KeyEvent.KEYCODE_COMMA /*55*/:
                            val = asciiEncoded.charAt(i) - 48;
                            i++;
                            if (asciiEncoded.charAt(i) >= '0' && asciiEncoded.charAt(i) <= '7') {
                                val = ((val * 8) + asciiEncoded.charAt(i)) - 48;
                                i++;
                            }
                            if (asciiEncoded.charAt(i) >= '0' && asciiEncoded.charAt(i) <= '7') {
                                val = ((val * 8) + asciiEncoded.charAt(i)) - 48;
                                i++;
                            }
                            this.octets.write(val);
                            break;
                        case KeyEvent.KEYCODE_PAGE_UP /*92*/:
                            this.octets.write(92);
                            i++;
                            break;
                        case KeyEvent.KEYCODE_BUTTON_Z /*101*/:
                            this.octets.write(27);
                            i++;
                            break;
                        case KeyEvent.KEYCODE_BUTTON_MODE /*110*/:
                            this.octets.write(10);
                            i++;
                            break;
                        case KeyEvent.KEYCODE_CTRL_RIGHT /*114*/:
                            this.octets.write(13);
                            i++;
                            break;
                        case KeyEvent.KEYCODE_SCROLL_LOCK /*116*/:
                            this.octets.write(9);
                            i++;
                            break;
                        case KeyEvent.KEYCODE_SYSRQ /*120*/:
                            i++;
                            try {
                                val = Integer.parseInt(asciiEncoded.substring(i, i + 2), HEX_RADIX);
                            } catch (NumberFormatException e) {
                                val = -1;
                            }
                            if (val >= 0) {
                                this.octets.write(val);
                                i += 2;
                                break;
                            }
                            val = Character.digit(asciiEncoded.charAt(i), HEX_RADIX);
                            if (val < 0) {
                                break;
                            }
                            this.octets.write(val);
                            i++;
                            break;
                        default:
                            break;
                    }
                default:
                    this.octets.write(c);
                    i++;
                    break;
            }
        }
    }

    public String toString() {
        byte[] ssidBytes = this.octets.toByteArray();
        if (this.octets.size() <= 0 || isArrayAllZeroes(ssidBytes)) {
            return ProxyInfo.LOCAL_EXCL_LIST;
        }
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        CharBuffer out = CharBuffer.allocate(32);
        CoderResult result = decoder.decode(ByteBuffer.wrap(ssidBytes), out, true);
        out.flip();
        if (result.isError()) {
            return NONE;
        }
        return out.toString();
    }

    private boolean isArrayAllZeroes(byte[] ssidBytes) {
        for (byte b : ssidBytes) {
            if (b != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isHidden() {
        return isArrayAllZeroes(this.octets.toByteArray());
    }

    public byte[] getOctets() {
        return this.octets.toByteArray();
    }

    public String getHexString() {
        String out = "0x";
        byte[] ssidbytes = getOctets();
        for (int i = 0; i < this.octets.size(); i++) {
            out = out + String.format(Locale.US, "%02x", new Object[]{Byte.valueOf(ssidbytes[i])});
        }
        return out;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.octets.size());
        dest.writeByteArray(this.octets.toByteArray());
    }

    static {
        CREATOR = new Creator<WifiSsid>() {
            public android.net.wifi.WifiSsid createFromParcel(android.os.Parcel r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.wifi.WifiSsid.1.createFromParcel(android.os.Parcel):android.net.wifi.WifiSsid
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.wifi.WifiSsid.1.createFromParcel(android.os.Parcel):android.net.wifi.WifiSsid
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.WifiSsid.1.createFromParcel(android.os.Parcel):android.net.wifi.WifiSsid");
            }

            public WifiSsid[] newArray(int size) {
                return new WifiSsid[size];
            }
        };
    }
}
