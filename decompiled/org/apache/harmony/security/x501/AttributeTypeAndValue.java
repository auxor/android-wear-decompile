package org.apache.harmony.security.x501;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BerOutputStream;
import org.apache.harmony.security.utils.ObjectIdentifier;

public final class AttributeTypeAndValue {
    public static final ASN1Sequence ASN1 = null;
    private static final ObjectIdentifier C = null;
    private static final int CAPACITY = 10;
    private static final ObjectIdentifier CN = null;
    public static final ObjectIdentifier DC = null;
    private static final ObjectIdentifier DNQ = null;
    private static final ObjectIdentifier DNQUALIFIER = null;
    public static final ObjectIdentifier EMAILADDRESS = null;
    private static final ObjectIdentifier GENERATION = null;
    private static final ObjectIdentifier GIVENNAME = null;
    private static final ObjectIdentifier INITIALS = null;
    private static final HashMap<String, ObjectIdentifier> KNOWN_NAMES = null;
    private static final ObjectIdentifier[][] KNOWN_OIDS = null;
    private static final ObjectIdentifier L = null;
    private static final ObjectIdentifier O = null;
    private static final ObjectIdentifier OU = null;
    private static final HashMap<String, ObjectIdentifier> RFC1779_NAMES = null;
    private static final HashMap<String, ObjectIdentifier> RFC2253_NAMES = null;
    private static final HashMap<String, ObjectIdentifier> RFC2459_NAMES = null;
    private static final ObjectIdentifier SERIALNUMBER = null;
    private static final int SIZE = 10;
    private static final ObjectIdentifier ST = null;
    private static final ObjectIdentifier STREET = null;
    private static final ObjectIdentifier SURNAME = null;
    private static final ObjectIdentifier T = null;
    private static final ObjectIdentifier UID = null;
    public static final ASN1Type attributeValue = null;
    private final ObjectIdentifier oid;
    private final AttributeValue value;

    /* renamed from: org.apache.harmony.security.x501.AttributeTypeAndValue.1 */
    static class AnonymousClass1 extends ASN1Type {
        AnonymousClass1(int x0) {
            super(x0);
        }

        public boolean checkTag(int tag) {
            return true;
        }

        public Object decode(BerInputStream in) throws IOException {
            String str = null;
            if (DirectoryString.ASN1.checkTag(in.tag)) {
                str = (String) DirectoryString.ASN1.decode(in);
            } else {
                in.readContent();
            }
            byte[] bytesEncoded = new byte[(in.getOffset() - in.getTagOffset())];
            System.arraycopy(in.getBuffer(), in.getTagOffset(), bytesEncoded, 0, bytesEncoded.length);
            return new AttributeValue(str, bytesEncoded, in.tag);
        }

        public Object getDecodedObject(BerInputStream in) throws IOException {
            throw new RuntimeException("AttributeValue getDecodedObject MUST NOT be invoked");
        }

        public void encodeASN(BerOutputStream out) {
            AttributeValue av = out.content;
            if (av.encoded != null) {
                out.content = av.encoded;
                out.encodeANY();
                return;
            }
            out.encodeTag(av.getTag());
            out.content = av.bytes;
            out.encodeString();
        }

        public void setEncodingContent(BerOutputStream out) {
            AttributeValue av = out.content;
            if (av.encoded != null) {
                out.length = av.encoded.length;
            } else if (av.getTag() == 12) {
                out.content = av.rawString;
                ASN1StringType.UTF8STRING.setEncodingContent(out);
                av.bytes = (byte[]) out.content;
                out.content = av;
            } else {
                av.bytes = av.rawString.getBytes(StandardCharsets.UTF_8);
                out.length = av.bytes.length;
            }
        }

        public void encodeContent(BerOutputStream out) {
            throw new RuntimeException("AttributeValue encodeContent MUST NOT be invoked");
        }

        public int getEncodedLength(BerOutputStream out) {
            if (out.content.encoded != null) {
                return out.length;
            }
            return super.getEncodedLength(out);
        }
    }

    /* renamed from: org.apache.harmony.security.x501.AttributeTypeAndValue.2 */
    static class AnonymousClass2 extends ASN1Sequence {
        AnonymousClass2(ASN1Type[] x0) {
            super(x0);
        }

        protected Object getDecodedObject(BerInputStream in) throws IOException {
            Object[] values = (Object[]) in.content;
            return new AttributeTypeAndValue((AttributeValue) values[1], null);
        }

        protected void getValues(Object object, Object[] values) {
            AttributeTypeAndValue atav = (AttributeTypeAndValue) object;
            values[0] = atav.oid.getOid();
            values[1] = atav.value;
        }
    }

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.security.x501.AttributeTypeAndValue.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.security.x501.AttributeTypeAndValue.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.security.x501.AttributeTypeAndValue.<clinit>():void");
    }

    public static ObjectIdentifier getObjectIdentifier(String sOid) throws IOException {
        ObjectIdentifier thisOid;
        if (sOid.charAt(0) < '0' || sOid.charAt(0) > '9') {
            thisOid = (ObjectIdentifier) KNOWN_NAMES.get(sOid.toUpperCase(Locale.US));
            if (thisOid != null) {
                return thisOid;
            }
            throw new IOException("Unrecognizable attribute name: " + sOid);
        }
        int[] array = org.apache.harmony.security.asn1.ObjectIdentifier.toIntArray(sOid);
        thisOid = getOID(array);
        if (thisOid == null) {
            return new ObjectIdentifier(array);
        }
        return thisOid;
    }

    private AttributeTypeAndValue(int[] oid, AttributeValue value) throws IOException {
        ObjectIdentifier thisOid = getOID(oid);
        if (thisOid == null) {
            thisOid = new ObjectIdentifier(oid);
        }
        this.oid = thisOid;
        this.value = value;
    }

    public AttributeTypeAndValue(ObjectIdentifier oid, AttributeValue value) throws IOException {
        this.oid = oid;
        this.value = value;
    }

    public void appendName(String attrFormat, StringBuilder sb) {
        boolean hexFormat = false;
        if (X500Principal.RFC1779.equals(attrFormat)) {
            if (RFC1779_NAMES == this.oid.getGroup()) {
                sb.append(this.oid.getName());
            } else {
                sb.append(this.oid.toOIDString());
            }
            sb.append('=');
            if (this.value.escapedString == this.value.getHexString()) {
                sb.append(this.value.getHexString().toUpperCase(Locale.US));
                return;
            } else if (this.value.escapedString.length() != this.value.rawString.length()) {
                this.value.appendQEString(sb);
                return;
            } else {
                sb.append(this.value.escapedString);
                return;
            }
        }
        HashMap group = this.oid.getGroup();
        if (RFC1779_NAMES == group || RFC2253_NAMES == group) {
            sb.append(this.oid.getName());
            if (X500Principal.CANONICAL.equals(attrFormat)) {
                int tag = this.value.getTag();
                if (!(ASN1StringType.UTF8STRING.checkTag(tag) || ASN1StringType.PRINTABLESTRING.checkTag(tag) || ASN1StringType.TELETEXSTRING.checkTag(tag))) {
                    hexFormat = true;
                }
            }
        } else {
            sb.append(this.oid.toString());
            hexFormat = true;
        }
        sb.append('=');
        if (hexFormat) {
            sb.append(this.value.getHexString());
        } else if (X500Principal.CANONICAL.equals(attrFormat)) {
            sb.append(this.value.makeCanonical());
        } else if (X500Principal.RFC2253.equals(attrFormat)) {
            sb.append(this.value.getRFC2253String());
        } else {
            sb.append(this.value.escapedString);
        }
    }

    public ObjectIdentifier getType() {
        return this.oid;
    }

    public AttributeValue getValue() {
        return this.value;
    }

    private static ObjectIdentifier getOID(int[] oid) {
        ObjectIdentifier[] list = KNOWN_OIDS[hashIntArray(oid) % SIZE];
        for (int i = 0; list[i] != null; i++) {
            if (Arrays.equals(oid, list[i].getOid())) {
                return list[i];
            }
        }
        return null;
    }

    private static void addOID(ObjectIdentifier oid) {
        int[] newOid = oid.getOid();
        ObjectIdentifier[] list = KNOWN_OIDS[hashIntArray(newOid) % SIZE];
        int i = 0;
        while (list[i] != null) {
            if (Arrays.equals(newOid, list[i].getOid())) {
                throw new Error("ObjectIdentifier: invalid static initialization; duplicate OIDs: " + oid.getName() + " " + list[i].getName());
            }
            i++;
        }
        if (i == 9) {
            throw new Error("ObjectIdentifier: invalid static initialization; small OID pool capacity");
        }
        list[i] = oid;
    }

    private static int hashIntArray(int[] oid) {
        int intHash = 0;
        int i = 0;
        while (i < oid.length && i < 4) {
            intHash += oid[i] << (i * 8);
            i++;
        }
        return Integer.MAX_VALUE & intHash;
    }
}
