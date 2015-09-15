package org.apache.harmony.security.asn1;

public abstract class ASN1TypeCollection extends ASN1Constructed {
    public final Object[] DEFAULT;
    public final boolean[] OPTIONAL;
    public final ASN1Type[] type;

    protected ASN1TypeCollection(int tagNumber, ASN1Type[] type) {
        super(tagNumber);
        this.type = type;
        this.OPTIONAL = new boolean[type.length];
        this.DEFAULT = new Object[type.length];
    }

    protected final void setOptional(int index) {
        this.OPTIONAL[index] = true;
    }

    protected final void setDefault(Object object, int index) {
        this.OPTIONAL[index] = true;
        this.DEFAULT[index] = object;
    }

    protected void getValues(Object object, Object[] values) {
        throw new RuntimeException("ASN.1 type is not designed to be encoded: " + getClass().getName());
    }
}
