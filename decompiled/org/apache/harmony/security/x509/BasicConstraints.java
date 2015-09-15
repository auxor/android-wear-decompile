package org.apache.harmony.security.x509;

import java.io.IOException;
import java.math.BigInteger;
import org.apache.harmony.security.asn1.ASN1Boolean;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

public final class BasicConstraints extends ExtensionValue {
    public static final ASN1Type ASN1;
    private boolean ca;
    private int pathLenConstraint;

    /* renamed from: org.apache.harmony.security.x509.BasicConstraints.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
            setDefault(Boolean.FALSE, 0);
            setOptional(1);
        }

        public Object getDecodedObject(BerInputStream in) throws IOException {
            return in.content;
        }

        protected void getValues(Object object, Object[] values) {
            Object[] array = (Object[]) object;
            values[0] = array[0];
            values[1] = ((BigInteger) array[1]).toByteArray();
        }
    }

    public BasicConstraints(byte[] encoding) throws IOException {
        super(encoding);
        this.ca = false;
        this.pathLenConstraint = Integer.MAX_VALUE;
        Object[] values = (Object[]) ASN1.decode(encoding);
        this.ca = ((Boolean) values[0]).booleanValue();
        if (values[1] != null) {
            this.pathLenConstraint = new BigInteger((byte[]) values[1]).intValue();
        }
    }

    public boolean getCa() {
        return this.ca;
    }

    public int getPathLenConstraint() {
        return this.pathLenConstraint;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(new Object[]{Boolean.valueOf(this.ca), BigInteger.valueOf((long) this.pathLenConstraint)});
        }
        return this.encoding;
    }

    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("BasicConstraints [\n").append(prefix).append("  CA: ").append(this.ca).append("\n  ").append(prefix).append("pathLenConstraint: ").append(this.pathLenConstraint).append('\n').append(prefix).append("]\n");
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1Boolean.getInstance(), ASN1Integer.getInstance()});
    }
}
