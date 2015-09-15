package java.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import libcore.net.UriCodec;

public class URLDecoder {
    @Deprecated
    public static String decode(String s) {
        return UriCodec.decode(s, true, Charset.defaultCharset(), true);
    }

    public static String decode(String s, String charsetName) throws UnsupportedEncodingException {
        return UriCodec.decode(s, true, Charset.forName(charsetName), true);
    }
}
