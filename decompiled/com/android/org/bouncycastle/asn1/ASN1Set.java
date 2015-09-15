package com.android.org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public abstract class ASN1Set extends ASN1Primitive {
    private boolean isSorted;
    private Vector set;

    /* renamed from: com.android.org.bouncycastle.asn1.ASN1Set.1 */
    class AnonymousClass1 implements ASN1SetParser {
        private int index;
        private final int max;
        final /* synthetic */ ASN1Set this$0;
        final /* synthetic */ ASN1Set val$outer;

        AnonymousClass1(com.android.org.bouncycastle.asn1.ASN1Set r1, com.android.org.bouncycastle.asn1.ASN1Set r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.asn1.ASN1Set.1.<init>(com.android.org.bouncycastle.asn1.ASN1Set, com.android.org.bouncycastle.asn1.ASN1Set):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.asn1.ASN1Set.1.<init>(com.android.org.bouncycastle.asn1.ASN1Set, com.android.org.bouncycastle.asn1.ASN1Set):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.asn1.ASN1Set.1.<init>(com.android.org.bouncycastle.asn1.ASN1Set, com.android.org.bouncycastle.asn1.ASN1Set):void");
        }

        public com.android.org.bouncycastle.asn1.ASN1Primitive getLoadedObject() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.asn1.ASN1Set.1.getLoadedObject():com.android.org.bouncycastle.asn1.ASN1Primitive
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.asn1.ASN1Set.1.getLoadedObject():com.android.org.bouncycastle.asn1.ASN1Primitive
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.asn1.ASN1Set.1.getLoadedObject():com.android.org.bouncycastle.asn1.ASN1Primitive");
        }

        public com.android.org.bouncycastle.asn1.ASN1Encodable readObject() throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.asn1.ASN1Set.1.readObject():com.android.org.bouncycastle.asn1.ASN1Encodable
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.asn1.ASN1Set.1.readObject():com.android.org.bouncycastle.asn1.ASN1Encodable
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.asn1.ASN1Set.1.readObject():com.android.org.bouncycastle.asn1.ASN1Encodable");
        }

        public com.android.org.bouncycastle.asn1.ASN1Primitive toASN1Primitive() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.asn1.ASN1Set.1.toASN1Primitive():com.android.org.bouncycastle.asn1.ASN1Primitive
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.asn1.ASN1Set.1.toASN1Primitive():com.android.org.bouncycastle.asn1.ASN1Primitive
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.asn1.ASN1Set.1.toASN1Primitive():com.android.org.bouncycastle.asn1.ASN1Primitive");
        }
    }

    abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    public static ASN1Set getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Set)) {
            return (ASN1Set) obj;
        }
        if (obj instanceof ASN1SetParser) {
            return getInstance(((ASN1SetParser) obj).toASN1Primitive());
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct set from byte[]: " + e.getMessage());
            }
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (primitive instanceof ASN1Set) {
                return (ASN1Set) primitive;
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Set getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (explicit) {
            if (obj.isExplicit()) {
                return (ASN1Set) obj.getObject();
            }
            throw new IllegalArgumentException("object implicit - explicit expected.");
        } else if (obj.isExplicit()) {
            if (obj instanceof BERTaggedObject) {
                return new BERSet(obj.getObject());
            }
            return new DLSet(obj.getObject());
        } else if (obj.getObject() instanceof ASN1Set) {
            return (ASN1Set) obj.getObject();
        } else {
            if (obj.getObject() instanceof ASN1Sequence) {
                ASN1Sequence s = (ASN1Sequence) obj.getObject();
                if (obj instanceof BERTaggedObject) {
                    return new BERSet(s.toArray());
                }
                return new DLSet(s.toArray());
            }
            throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
        }
    }

    protected ASN1Set() {
        this.set = new Vector();
        this.isSorted = false;
    }

    protected ASN1Set(ASN1Encodable obj) {
        this.set = new Vector();
        this.isSorted = false;
        this.set.addElement(obj);
    }

    protected ASN1Set(ASN1EncodableVector v, boolean doSort) {
        this.set = new Vector();
        this.isSorted = false;
        for (int i = 0; i != v.size(); i++) {
            this.set.addElement(v.get(i));
        }
        if (doSort) {
            sort();
        }
    }

    protected ASN1Set(ASN1Encodable[] array, boolean doSort) {
        this.set = new Vector();
        this.isSorted = false;
        for (int i = 0; i != array.length; i++) {
            this.set.addElement(array[i]);
        }
        if (doSort) {
            sort();
        }
    }

    public Enumeration getObjects() {
        return this.set.elements();
    }

    public ASN1Encodable getObjectAt(int index) {
        return (ASN1Encodable) this.set.elementAt(index);
    }

    public int size() {
        return this.set.size();
    }

    public ASN1Encodable[] toArray() {
        ASN1Encodable[] values = new ASN1Encodable[size()];
        for (int i = 0; i != size(); i++) {
            values[i] = getObjectAt(i);
        }
        return values;
    }

    public ASN1SetParser parser() {
        return new AnonymousClass1(this, this);
    }

    public int hashCode() {
        Enumeration e = getObjects();
        int hashCode = size();
        while (e.hasMoreElements()) {
            hashCode = (hashCode * 17) ^ getNext(e).hashCode();
        }
        return hashCode;
    }

    ASN1Primitive toDERObject() {
        if (this.isSorted) {
            ASN1Set derSet = new DERSet();
            derSet.set = this.set;
            return derSet;
        }
        Vector v = new Vector();
        for (int i = 0; i != this.set.size(); i++) {
            v.addElement(this.set.elementAt(i));
        }
        derSet = new DERSet();
        derSet.set = v;
        derSet.sort();
        return derSet;
    }

    ASN1Primitive toDLObject() {
        ASN1Set derSet = new DLSet();
        derSet.set = this.set;
        return derSet;
    }

    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1Set)) {
            return false;
        }
        ASN1Set other = (ASN1Set) o;
        if (size() != other.size()) {
            return false;
        }
        Enumeration s1 = getObjects();
        Enumeration s2 = other.getObjects();
        while (s1.hasMoreElements()) {
            ASN1Encodable obj1 = getNext(s1);
            ASN1Encodable obj2 = getNext(s2);
            ASN1Primitive o1 = obj1.toASN1Primitive();
            ASN1Primitive o2 = obj2.toASN1Primitive();
            if (o1 != o2) {
                if (!o1.equals(o2)) {
                    return false;
                }
            }
        }
        return true;
    }

    private ASN1Encodable getNext(Enumeration e) {
        ASN1Encodable encObj = (ASN1Encodable) e.nextElement();
        if (encObj == null) {
            return DERNull.INSTANCE;
        }
        return encObj;
    }

    private boolean lessThanOrEqual(byte[] a, byte[] b) {
        int len = Math.min(a.length, b.length);
        int i = 0;
        while (i != len) {
            if (a[i] == b[i]) {
                i++;
            } else if ((a[i] & 255) < (b[i] & 255)) {
                return true;
            } else {
                return false;
            }
        }
        if (len != a.length) {
            return false;
        }
        return true;
    }

    private byte[] getEncoded(ASN1Encodable obj) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        try {
            new ASN1OutputStream(bOut).writeObject(obj);
            return bOut.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("cannot encode object added to SET");
        }
    }

    protected void sort() {
        if (!this.isSorted) {
            this.isSorted = true;
            if (this.set.size() > 1) {
                boolean swapped = true;
                int lastSwap = this.set.size() - 1;
                while (swapped) {
                    int swapIndex = 0;
                    byte[] a = getEncoded((ASN1Encodable) this.set.elementAt(0));
                    swapped = false;
                    for (int index = 0; index != lastSwap; index++) {
                        byte[] b = getEncoded((ASN1Encodable) this.set.elementAt(index + 1));
                        if (lessThanOrEqual(a, b)) {
                            a = b;
                        } else {
                            Object o = this.set.elementAt(index);
                            this.set.setElementAt(this.set.elementAt(index + 1), index);
                            this.set.setElementAt(o, index + 1);
                            swapped = true;
                            swapIndex = index;
                        }
                    }
                    lastSwap = swapIndex;
                }
            }
        }
    }

    boolean isConstructed() {
        return true;
    }

    public String toString() {
        return this.set.toString();
    }
}
