package java.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import libcore.net.UriCodec;

public class URLEncoder {
    static UriCodec ENCODER;

    private URLEncoder() {
    }

    static {
        ENCODER = new UriCodec() {
            protected boolean isRetained(char c) {
                return " .-*_".indexOf((int) c) != -1;
            }
        };
    }

    @Deprecated
    public static String encode(String s) {
        return ENCODER.encode(s, StandardCharsets.UTF_8);
    }

    public static String encode(String s, String charsetName) throws UnsupportedEncodingException {
        return ENCODER.encode(s, Charset.forName(charsetName));
    }
}
