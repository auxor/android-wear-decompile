package com.android.org.bouncycastle.asn1;

import com.android.org.bouncycastle.util.Arrays;
import java.io.IOException;

public class DERBoolean extends ASN1Primitive {
    public static final ASN1Boolean FALSE;
    private static final byte[] FALSE_VALUE;
    public static final ASN1Boolean TRUE;
    private static final byte[] TRUE_VALUE;
    private final byte[] value;

    static {
        TRUE_VALUE = new byte[]{(byte) -1};
        FALSE_VALUE = new byte[]{(byte) 0};
        FALSE = new ASN1Boolean(false);
        TRUE = new ASN1Boolean(true);
    }

    public static ASN1Boolean getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Boolean)) {
            return (ASN1Boolean) obj;
        }
        if (obj instanceof DERBoolean) {
            return ((DERBoolean) obj).isTrue() ? TRUE : FALSE;
        } else {
            throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
        }
    }

    public static ASN1Boolean getInstance(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static ASN1Boolean getInstance(int value) {
        return value != 0 ? TRUE : FALSE;
    }

    public static DERBoolean getInstance(byte[] octets) {
        return octets[0] != null ? TRUE : FALSE;
    }

    public static ASN1Boolean getInstance(ASN1TaggedObject obj, boolean explicit) {
        Object o = obj.getObject();
        if (explicit || (o instanceof DERBoolean)) {
            return getInstance(o);
        }
        return fromOctetString(((ASN1OctetString) o).getOctets());
    }

    protected DERBoolean(byte[] value) {
        if (value.length != 1) {
            throw new IllegalArgumentException("byte value should have 1 byte in it");
        } else if (value[0] == null) {
            this.value = FALSE_VALUE;
        } else if (value[0] == 255) {
            this.value = TRUE_VALUE;
        } else {
            this.value = Arrays.clone(value);
        }
    }

    protected DERBoolean(boolean value) {
        this.value = value ? TRUE_VALUE : FALSE_VALUE;
    }

    public boolean isTrue() {
        return this.value[0] != null;
    }

    boolean isConstructed() {
        return false;
    }

    int encodedLength() {
        return 3;
    }

    void encode(ASN1OutputStream out) throws IOException {
        out.writeEncoded(1, this.value);
    }

    protected boolean asn1Equals(ASN1Primitive o) {
        if (o != null && (o instanceof DERBoolean) && this.value[0] == ((DERBoolean) o).value[0]) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.value[0];
    }

    public String toString() {
        return this.value[0] != null ? "TRUE" : "FALSE";
    }

    static ASN1Boolean fromOctetString(byte[] value) {
        if (value.length != 1) {
            throw new IllegalArgumentException("BOOLEAN value should have 1 byte in it");
        } else if (value[0] == null) {
            return FALSE;
        } else {
            if (value[0] == 255) {
                return TRUE;
            }
            return new ASN1Boolean(value);
        }
    }
}
