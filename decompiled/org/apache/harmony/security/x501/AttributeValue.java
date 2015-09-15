package org.apache.harmony.security.x501;

import dalvik.bytecode.Opcodes;
import java.io.IOException;
import java.sql.Types;
import java.util.Collection;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.ASN1StringType;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.DerInputStream;
import org.apache.harmony.security.utils.ObjectIdentifier;
import org.w3c.dom.traversal.NodeFilter;

public final class AttributeValue {
    public byte[] bytes;
    public byte[] encoded;
    public final String escapedString;
    private boolean hasConsecutiveSpaces;
    public boolean hasQE;
    private String hexString;
    public final String rawString;
    private String rfc2253String;
    private final int tag;
    public boolean wasEncoded;

    public AttributeValue(String parsedString, boolean hasQorE, ObjectIdentifier oid) {
        int tag;
        this.wasEncoded = false;
        this.hasQE = hasQorE;
        this.rawString = parsedString;
        this.escapedString = makeEscaped(this.rawString);
        if (oid == AttributeTypeAndValue.EMAILADDRESS || oid == AttributeTypeAndValue.DC) {
            tag = ASN1StringType.IA5STRING.id;
        } else if (isPrintableString(this.rawString)) {
            tag = ASN1StringType.PRINTABLESTRING.id;
        } else {
            tag = ASN1StringType.UTF8STRING.id;
        }
        this.tag = tag;
    }

    public AttributeValue(String hexString, byte[] encoded) {
        this.wasEncoded = true;
        this.hexString = hexString;
        this.encoded = encoded;
        try {
            DerInputStream in = new DerInputStream(encoded);
            this.tag = in.tag;
            if (DirectoryString.ASN1.checkTag(this.tag)) {
                this.rawString = (String) DirectoryString.ASN1.decode(in);
                this.escapedString = makeEscaped(this.rawString);
                return;
            }
            this.rawString = hexString;
            this.escapedString = hexString;
        } catch (IOException e) {
            IllegalArgumentException iae = new IllegalArgumentException();
            iae.initCause(e);
            throw iae;
        }
    }

    public AttributeValue(String rawString, byte[] encoded, int tag) {
        this.wasEncoded = true;
        this.encoded = encoded;
        this.tag = tag;
        if (rawString == null) {
            this.rawString = getHexString();
            this.escapedString = this.hexString;
            return;
        }
        this.rawString = rawString;
        this.escapedString = makeEscaped(rawString);
    }

    private static boolean isPrintableString(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch != ' ' && ((ch < '\'' || ch > ')') && ((ch < '+' || ch > ':') && ch != '=' && ch != '?' && ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z'))))) {
                return false;
            }
        }
        return true;
    }

    public int getTag() {
        return this.tag;
    }

    public String getHexString() {
        if (this.hexString == null) {
            if (!this.wasEncoded) {
                if (this.tag == ASN1StringType.IA5STRING.id) {
                    this.encoded = ASN1StringType.IA5STRING.encode(this.rawString);
                } else if (this.tag == ASN1StringType.PRINTABLESTRING.id) {
                    this.encoded = ASN1StringType.PRINTABLESTRING.encode(this.rawString);
                } else {
                    this.encoded = ASN1StringType.UTF8STRING.encode(this.rawString);
                }
                this.wasEncoded = true;
            }
            StringBuilder buf = new StringBuilder((this.encoded.length * 2) + 1);
            buf.append('#');
            for (int i = 0; i < this.encoded.length; i++) {
                int c = (this.encoded[i] >> 4) & 15;
                if (c < 10) {
                    buf.append((char) (c + 48));
                } else {
                    buf.append((char) (c + 87));
                }
                c = this.encoded[i] & 15;
                if (c < 10) {
                    buf.append((char) (c + 48));
                } else {
                    buf.append((char) (c + 87));
                }
            }
            this.hexString = buf.toString();
        }
        return this.hexString;
    }

    public Collection<?> getValues(ASN1Type type) throws IOException {
        return (Collection) new ASN1SetOf(type).decode(this.encoded);
    }

    public void appendQEString(StringBuilder sb) {
        sb.append('\"');
        if (this.hasQE) {
            for (int i = 0; i < this.rawString.length(); i++) {
                char c = this.rawString.charAt(i);
                if (c == '\"' || c == '\\') {
                    sb.append('\\');
                }
                sb.append(c);
            }
        } else {
            sb.append(this.rawString);
        }
        sb.append('\"');
    }

    private String makeEscaped(String name) {
        int length = name.length();
        if (length == 0) {
            return name;
        }
        StringBuilder buf = new StringBuilder(length * 2);
        boolean escapeSpaces = false;
        int index = 0;
        while (index < length) {
            char ch = name.charAt(index);
            switch (ch) {
                case NodeFilter.SHOW_ENTITY /*32*/:
                    if (index < length - 1) {
                        boolean nextIsSpace = name.charAt(index + 1) == ' ';
                        if (escapeSpaces || nextIsSpace || index == 0) {
                            escapeSpaces = true;
                        } else {
                            escapeSpaces = false;
                        }
                        this.hasConsecutiveSpaces |= nextIsSpace;
                    } else {
                        escapeSpaces = true;
                    }
                    if (escapeSpaces) {
                        buf.append('\\');
                    }
                    buf.append(' ');
                    break;
                case ZipConstants.CENDSK /*34*/:
                case Types.TIME /*92*/:
                    this.hasQE = true;
                    buf.append('\\');
                    buf.append(ch);
                    break;
                case ASN1Constants.TAG_C_BITSTRING /*35*/:
                case Opcodes.OP_PACKED_SWITCH /*43*/:
                case ASN1Constants.TAG_C_UTF8STRING /*44*/:
                case Opcodes.OP_IF_GEZ /*59*/:
                case Opcodes.OP_IF_GTZ /*60*/:
                case Opcodes.OP_IF_LEZ /*61*/:
                case '>':
                    buf.append('\\');
                    buf.append(ch);
                    break;
                default:
                    buf.append(ch);
                    break;
            }
            if (escapeSpaces && ch != ' ') {
                escapeSpaces = false;
            }
            index++;
        }
        return buf.toString();
    }

    public String makeCanonical() {
        int length = this.rawString.length();
        if (length == 0) {
            return this.rawString;
        }
        int bufLength;
        StringBuilder buf = new StringBuilder(length * 2);
        int index = 0;
        if (this.rawString.charAt(0) == '#') {
            buf.append('\\');
            buf.append('#');
            index = 0 + 1;
        }
        while (index < length) {
            char ch = this.rawString.charAt(index);
            switch (ch) {
                case NodeFilter.SHOW_ENTITY /*32*/:
                    bufLength = buf.length();
                    if (!(bufLength == 0 || buf.charAt(bufLength - 1) == ' ')) {
                        buf.append(' ');
                        break;
                    }
                case ZipConstants.CENDSK /*34*/:
                case Opcodes.OP_PACKED_SWITCH /*43*/:
                case ASN1Constants.TAG_C_UTF8STRING /*44*/:
                case Opcodes.OP_IF_GEZ /*59*/:
                case Opcodes.OP_IF_GTZ /*60*/:
                case '>':
                case Types.TIME /*92*/:
                    buf.append('\\');
                    break;
            }
            buf.append(ch);
            index++;
        }
        bufLength = buf.length() - 1;
        while (bufLength > -1 && buf.charAt(bufLength) == ' ') {
            bufLength--;
        }
        buf.setLength(bufLength + 1);
        return buf.toString();
    }

    public String getRFC2253String() {
        if (!this.hasConsecutiveSpaces) {
            return this.escapedString;
        }
        if (this.rfc2253String == null) {
            int lastIndex = this.escapedString.length() - 2;
            int i = lastIndex;
            while (i > 0) {
                if (this.escapedString.charAt(i) == '\\' && this.escapedString.charAt(i + 1) == ' ') {
                    lastIndex = i - 1;
                }
                i -= 2;
            }
            boolean beginning = true;
            StringBuilder sb = new StringBuilder(this.escapedString.length());
            i = 0;
            while (i < this.escapedString.length()) {
                char ch = this.escapedString.charAt(i);
                if (ch != '\\') {
                    sb.append(ch);
                    beginning = false;
                } else {
                    char nextCh = this.escapedString.charAt(i + 1);
                    if (nextCh == ' ') {
                        if (beginning || i > lastIndex) {
                            sb.append(ch);
                        }
                        sb.append(nextCh);
                    } else {
                        sb.append(ch);
                        sb.append(nextCh);
                        beginning = false;
                    }
                    i++;
                }
                i++;
            }
            this.rfc2253String = sb.toString();
        }
        return this.rfc2253String;
    }
}
