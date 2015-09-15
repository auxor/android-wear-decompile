package com.android.org.bouncycastle.x509.extension;

import com.android.org.bouncycastle.asn1.ASN1OctetString;
import com.android.org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.PublicKey;

public class SubjectKeyIdentifierStructure extends SubjectKeyIdentifier {
    private static com.android.org.bouncycastle.asn1.ASN1OctetString fromPublicKey(java.security.PublicKey r1) throws java.security.InvalidKeyException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure.fromPublicKey(java.security.PublicKey):com.android.org.bouncycastle.asn1.ASN1OctetString
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure.fromPublicKey(java.security.PublicKey):com.android.org.bouncycastle.asn1.ASN1OctetString
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure.fromPublicKey(java.security.PublicKey):com.android.org.bouncycastle.asn1.ASN1OctetString");
    }

    public SubjectKeyIdentifierStructure(byte[] encodedValue) throws IOException {
        super((ASN1OctetString) X509ExtensionUtil.fromExtensionValue(encodedValue));
    }

    public SubjectKeyIdentifierStructure(PublicKey pubKey) throws InvalidKeyException {
        super(fromPublicKey(pubKey));
    }
}
