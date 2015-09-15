package org.apache.harmony.security.x509;

import java.util.Date;
import org.apache.harmony.security.asn1.ASN1Choice;
import org.apache.harmony.security.asn1.ASN1GeneralizedTime;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.ASN1UTCTime;

public final class Time {
    public static final ASN1Choice ASN1;
    private static final long JAN_01_2050 = 2524608000000L;

    /* renamed from: org.apache.harmony.security.x509.Time.1 */
    static class AnonymousClass1 extends ASN1Choice {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
        }

        public int getIndex(Object object) {
            if (((Date) object).getTime() < Time.JAN_01_2050) {
                return 1;
            }
            return 0;
        }

        public Object getObjectToEncode(Object object) {
            return object;
        }
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1GeneralizedTime.getInstance(), ASN1UTCTime.getInstance()});
    }
}
