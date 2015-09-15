package java.util;

import java.util.Map.Entry;

class MapEntry<K, V> implements Entry<K, V>, Cloneable {
    K key;
    V value;

    interface Type<RT, KT, VT> {
        RT get(MapEntry<KT, VT> mapEntry);
    }

    MapEntry(K theKey) {
        this.key = theKey;
    }

    MapEntry(K theKey, V theValue) {
        this.key = theKey;
        this.value = theValue;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
        r5 = this;
        r1 = 1;
        r2 = 0;
        if (r5 != r6) goto L_0x0005;
    L_0x0004:
        return r1;
    L_0x0005:
        r3 = r6 instanceof java.util.Map.Entry;
        if (r3 == 0) goto L_0x003c;
    L_0x0009:
        r0 = r6;
        r0 = (java.util.Map.Entry) r0;
        r3 = r5.key;
        if (r3 != 0) goto L_0x0022;
    L_0x0010:
        r3 = r0.getKey();
        if (r3 != 0) goto L_0x0020;
    L_0x0016:
        r3 = r5.value;
        if (r3 != 0) goto L_0x002f;
    L_0x001a:
        r3 = r0.getValue();
        if (r3 == 0) goto L_0x0004;
    L_0x0020:
        r1 = r2;
        goto L_0x0004;
    L_0x0022:
        r3 = r5.key;
        r4 = r0.getKey();
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0020;
    L_0x002e:
        goto L_0x0016;
    L_0x002f:
        r3 = r5.value;
        r4 = r0.getValue();
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0020;
    L_0x003b:
        goto L_0x0004;
    L_0x003c:
        r1 = r2;
        goto L_0x0004;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.MapEntry.equals(java.lang.Object):boolean");
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = this.key == null ? 0 : this.key.hashCode();
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return hashCode ^ i;
    }

    public V setValue(V object) {
        V result = this.value;
        this.value = object;
        return result;
    }

    public String toString() {
        return this.key + "=" + this.value;
    }
}
