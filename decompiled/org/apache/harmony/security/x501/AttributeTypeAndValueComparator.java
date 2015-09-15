package org.apache.harmony.security.x501;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.harmony.security.utils.ObjectIdentifier;

public class AttributeTypeAndValueComparator implements Comparator<AttributeTypeAndValue>, Serializable {
    private static final long serialVersionUID = -1286471842007103132L;

    public int compare(AttributeTypeAndValue atav1, AttributeTypeAndValue atav2) {
        if (atav1 == atav2) {
            return 0;
        }
        String kw1 = atav1.getType().getName();
        String kw2 = atav2.getType().getName();
        if (kw1 != null && kw2 == null) {
            return -1;
        }
        if (kw1 == null && kw2 != null) {
            return 1;
        }
        if (kw1 == null || kw2 == null) {
            return compateOids(atav1.getType(), atav2.getType());
        }
        return kw1.compareTo(kw2);
    }

    private static int compateOids(ObjectIdentifier oid1, ObjectIdentifier oid2) {
        if (oid1 == oid2) {
            return 0;
        }
        int[] ioid1 = oid1.getOid();
        int[] ioid2 = oid2.getOid();
        int min = ioid1.length < ioid2.length ? ioid1.length : ioid2.length;
        int i = 0;
        while (i < min) {
            if (ioid1[i] < ioid2[i]) {
                return -1;
            }
            if (ioid1[i] > ioid2[i]) {
                return 1;
            }
            if (i + 1 == ioid1.length && i + 1 < ioid2.length) {
                return -1;
            }
            if (i + 1 < ioid1.length && i + 1 == ioid2.length) {
                return 1;
            }
            i++;
        }
        return 0;
    }
}
