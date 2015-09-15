package org.apache.harmony.security.asn1;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public abstract class ASN1Choice extends ASN1Type {
    private final int[][] identifiers;
    public final ASN1Type[] type;

    public abstract int getIndex(Object obj);

    public abstract Object getObjectToEncode(Object obj);

    public ASN1Choice(ASN1Type[] type) {
        super(0);
        if (type.length == 0) {
            throw new IllegalArgumentException("ASN.1 choice type MUST have at least one alternative: " + getClass().getName());
        }
        TreeMap<BigInteger, BigInteger> map = new TreeMap();
        for (int index = 0; index < type.length; index++) {
            ASN1Type t = type[index];
            if (t instanceof ASN1Any) {
                throw new IllegalArgumentException("ASN.1 choice type MUST have alternatives with distinct tags: " + getClass().getName());
            }
            if (t instanceof ASN1Choice) {
                int[][] choiceToAdd = ((ASN1Choice) t).identifiers;
                for (int addIdentifier : choiceToAdd[0]) {
                    addIdentifier(map, addIdentifier, index);
                }
            } else {
                if (t.checkTag(t.id)) {
                    addIdentifier(map, t.id, index);
                }
                if (t.checkTag(t.constrId)) {
                    addIdentifier(map, t.constrId, index);
                }
            }
        }
        int size = map.size();
        this.identifiers = (int[][]) Array.newInstance(Integer.TYPE, 2, size);
        Iterator<Entry<BigInteger, BigInteger>> it = map.entrySet().iterator();
        for (int i = 0; i < size; i++) {
            Entry<BigInteger, BigInteger> entry = (Entry) it.next();
            this.identifiers[0][i] = ((BigInteger) entry.getKey()).intValue();
            this.identifiers[1][i] = ((BigInteger) entry.getValue()).intValue();
        }
        this.type = type;
    }

    private void addIdentifier(TreeMap<BigInteger, BigInteger> map, int identifier, int index) {
        if (map.put(BigInteger.valueOf((long) identifier), BigInteger.valueOf((long) index)) != null) {
            throw new IllegalArgumentException("ASN.1 choice type MUST have alternatives with distinct tags: " + getClass().getName());
        }
    }

    public final boolean checkTag(int identifier) {
        return Arrays.binarySearch(this.identifiers[0], identifier) >= 0;
    }

    public Object decode(BerInputStream in) throws IOException {
        int index = Arrays.binarySearch(this.identifiers[0], in.tag);
        if (index < 0) {
            throw new ASN1Exception("Failed to decode ASN.1 choice type.  No alternatives were found for " + getClass().getName());
        }
        index = this.identifiers[1][index];
        in.content = this.type[index].decode(in);
        in.choiceIndex = index;
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    public void encodeASN(BerOutputStream out) {
        encodeContent(out);
    }

    public final void encodeContent(BerOutputStream out) {
        out.encodeChoice(this);
    }

    public final void setEncodingContent(BerOutputStream out) {
        out.getChoiceLength(this);
    }
}
