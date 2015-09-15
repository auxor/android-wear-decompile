package libcore.net;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class UriCodec {
    protected abstract boolean isRetained(char c);

    public final String validate(String uri, int start, int end, String name) throws URISyntaxException {
        int i = start;
        while (i < end) {
            char ch = uri.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || ((ch >= 'A' && ch <= 'Z') || ((ch >= '0' && ch <= '9') || isRetained(ch)))) {
                i++;
            } else if (ch != '%') {
                throw new URISyntaxException(uri, "Illegal character in " + name, i);
            } else if (i + 2 >= end) {
                throw new URISyntaxException(uri, "Incomplete % sequence in " + name, i);
            } else {
                int d1 = hexToInt(uri.charAt(i + 1));
                int d2 = hexToInt(uri.charAt(i + 2));
                if (d1 == -1 || d2 == -1) {
                    throw new URISyntaxException(uri, "Invalid % sequence: " + uri.substring(i, i + 3) + " in " + name, i);
                }
                i += 3;
            }
        }
        return uri.substring(start, end);
    }

    public static void validateSimple(String s, String legal) throws URISyntaxException {
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            if ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ((ch < '0' || ch > '9') && legal.indexOf(ch) <= -1))) {
                throw new URISyntaxException(s, "Illegal character", i);
            }
        }
    }

    private void appendEncoded(StringBuilder builder, String s, Charset charset, boolean isPartiallyEncoded) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }
        int escapeStart = -1;
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if ((c >= 'a' && c <= 'z') || ((c >= 'A' && c <= 'Z') || ((c >= '0' && c <= '9') || isRetained(c) || (c == '%' && isPartiallyEncoded)))) {
                if (escapeStart != -1) {
                    appendHex(builder, s.substring(escapeStart, i), charset);
                    escapeStart = -1;
                }
                if (c == '%' && isPartiallyEncoded) {
                    builder.append((CharSequence) s, i, Math.min(i + 3, s.length()));
                    i += 2;
                } else if (c == ' ') {
                    builder.append('+');
                } else {
                    builder.append(c);
                }
            } else if (escapeStart == -1) {
                escapeStart = i;
            }
            i++;
        }
        if (escapeStart != -1) {
            appendHex(builder, s.substring(escapeStart, s.length()), charset);
        }
    }

    public final String encode(String s, Charset charset) {
        StringBuilder builder = new StringBuilder(s.length() + 16);
        appendEncoded(builder, s, charset, false);
        return builder.toString();
    }

    public final void appendEncoded(StringBuilder builder, String s) {
        appendEncoded(builder, s, StandardCharsets.UTF_8, false);
    }

    public final void appendPartiallyEncoded(StringBuilder builder, String s) {
        appendEncoded(builder, s, StandardCharsets.UTF_8, true);
    }

    public static String decode(String s, boolean convertPlus, Charset charset, boolean throwOnFailure) {
        if (s.indexOf(37) == -1 && (!convertPlus || s.indexOf(43) == -1)) {
            return s;
        }
        StringBuilder result = new StringBuilder(s.length());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '%') {
                do {
                    if (i + 2 < s.length()) {
                        int d1 = hexToInt(s.charAt(i + 1));
                        if (d1 != -1) {
                            int d2 = hexToInt(s.charAt(i + 2));
                            if (d2 != -1) {
                                out.write((byte) ((d1 << 4) + d2));
                                i += 3;
                                if (i < s.length()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (throwOnFailure) {
                        throw new IllegalArgumentException("Invalid % sequence at " + i + ": " + s);
                    }
                    byte[] replacement = "\ufffd".getBytes(charset);
                    out.write(replacement, 0, replacement.length);
                    i += 3;
                    if (i < s.length()) {
                        break;
                    }
                } while (s.charAt(i) == '%');
                result.append(new String(out.toByteArray(), charset));
                out.reset();
            } else {
                if (convertPlus && c == '+') {
                    c = ' ';
                }
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }

    private static int hexToInt(char c) {
        if ('0' <= c && c <= '9') {
            return c - 48;
        }
        if ('a' <= c && c <= 'f') {
            return (c - 97) + 10;
        }
        if ('A' > c || c > 'F') {
            return -1;
        }
        return (c - 65) + 10;
    }

    public static String decode(String s) {
        return decode(s, false, StandardCharsets.UTF_8, true);
    }

    private static void appendHex(StringBuilder builder, String s, Charset charset) {
        for (byte b : s.getBytes(charset)) {
            appendHex(builder, b);
        }
    }

    private static void appendHex(StringBuilder sb, byte b) {
        sb.append('%');
        sb.append(Byte.toHexString(b, true));
    }
}
