package org.apache.harmony.security.x509;

import java.io.IOException;
import org.apache.harmony.security.asn1.ASN1BitString.ASN1NamedBitList;
import org.apache.harmony.security.asn1.ASN1Type;

public final class KeyUsage extends ExtensionValue {
    private static final ASN1Type ASN1;
    private static final String[] USAGES;
    private final boolean[] keyUsage;

    static {
        USAGES = new String[]{"digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly"};
        ASN1 = new ASN1NamedBitList(9);
    }

    public KeyUsage(byte[] encoding) throws IOException {
        super(encoding);
        this.keyUsage = (boolean[]) ASN1.decode(encoding);
    }

    public boolean[] getKeyUsage() {
        return this.keyUsage;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this.keyUsage);
        }
        return this.encoding;
    }

    public void dumpValue(StringBuilder sb, String prefix) {
        sb.append(prefix).append("KeyUsage [\n");
        for (int i = 0; i < this.keyUsage.length; i++) {
            if (this.keyUsage[i]) {
                sb.append(prefix).append("  ").append(USAGES[i]).append('\n');
            }
        }
        sb.append(prefix).append("]\n");
    }
}
