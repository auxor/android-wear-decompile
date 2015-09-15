package org.apache.harmony.security.x501;

import org.apache.harmony.security.asn1.ASN1Choice;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;

public final class DirectoryString {
    public static final ASN1Choice ASN1;

    /* renamed from: org.apache.harmony.security.x501.DirectoryString.1 */
    static class AnonymousClass1 extends ASN1Choice {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
        }

        public int getIndex(Object object) {
            return 1;
        }

        public Object getObjectToEncode(Object object) {
            return object;
        }
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1StringType.TELETEXSTRING, ASN1StringType.PRINTABLESTRING, ASN1StringType.UNIVERSALSTRING, ASN1StringType.UTF8STRING, ASN1StringType.BMPSTRING});
    }
}
