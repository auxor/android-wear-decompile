package org.apache.harmony.security.asn1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class ASN1GeneralizedTime extends ASN1Time {
    private static final ASN1GeneralizedTime ASN1;
    private static final String GEN_PATTERN = "yyyyMMddHHmmss.SSS";

    static {
        ASN1 = new ASN1GeneralizedTime();
    }

    public ASN1GeneralizedTime() {
        super(24);
    }

    public static ASN1GeneralizedTime getInstance() {
        return ASN1;
    }

    public Object decode(BerInputStream in) throws IOException {
        in.readGeneralizedTime();
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    public void encodeContent(BerOutputStream out) {
        out.encodeGeneralizedTime();
    }

    public void setEncodingContent(BerOutputStream out) {
        SimpleDateFormat sdf = new SimpleDateFormat(GEN_PATTERN, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String temp = sdf.format(out.content);
        while (true) {
            int i;
            int currLength = temp.length() - 1;
            int nullId = temp.lastIndexOf(48, currLength);
            if (nullId != -1) {
                i = 1;
            } else {
                i = 0;
            }
            if (((nullId == currLength ? 1 : 0) & i) == 0) {
                break;
            }
            temp = temp.substring(0, nullId);
        }
        if (temp.charAt(currLength) == '.') {
            temp = temp.substring(0, currLength);
        }
        out.content = (temp + "Z").getBytes(StandardCharsets.UTF_8);
        out.length = ((byte[]) out.content).length;
    }
}
