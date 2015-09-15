package org.kxml2.io;

import dalvik.bytecode.Opcodes;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.sql.Types;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import libcore.icu.DateIntervalFormat;
import libcore.internal.StringPool;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.helpers.NamespaceSupport;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class KXmlParser implements XmlPullParser, Closeable {
    private static final char[] ANY;
    private static final int ATTLISTDECL = 13;
    private static final char[] COMMENT_DOUBLE_DASH;
    private static final Map<String, String> DEFAULT_ENTITIES;
    private static final char[] DOUBLE_QUOTE;
    private static final int ELEMENTDECL = 11;
    private static final char[] EMPTY;
    private static final char[] END_CDATA;
    private static final char[] END_COMMENT;
    private static final char[] END_PROCESSING_INSTRUCTION;
    private static final int ENTITYDECL = 12;
    private static final String FEATURE_RELAXED = "http://xmlpull.org/v1/doc/features.html#relaxed";
    private static final char[] FIXED;
    private static final String ILLEGAL_TYPE = "Wrong event type";
    private static final char[] IMPLIED;
    private static final char[] NDATA;
    private static final char[] NOTATION;
    private static final int NOTATIONDECL = 14;
    private static final int PARAMETER_ENTITY_REF = 15;
    private static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
    private static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
    private static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
    private static final char[] PUBLIC;
    private static final char[] REQUIRED;
    private static final char[] SINGLE_QUOTE;
    private static final char[] START_ATTLIST;
    private static final char[] START_CDATA;
    private static final char[] START_COMMENT;
    private static final char[] START_DOCTYPE;
    private static final char[] START_ELEMENT;
    private static final char[] START_ENTITY;
    private static final char[] START_NOTATION;
    private static final char[] START_PROCESSING_INSTRUCTION;
    private static final char[] SYSTEM;
    private static final String UNEXPECTED_EOF = "Unexpected EOF";
    private static final int XML_DECLARATION = 998;
    private int attributeCount;
    private String[] attributes;
    private char[] buffer;
    private StringBuilder bufferCapture;
    private int bufferStartColumn;
    private int bufferStartLine;
    private Map<String, Map<String, String>> defaultAttributes;
    private boolean degenerated;
    private int depth;
    private Map<String, char[]> documentEntities;
    private String[] elementStack;
    private String encoding;
    private String error;
    private boolean isWhitespace;
    private boolean keepNamespaceAttributes;
    private int limit;
    private String location;
    private String name;
    private String namespace;
    private ContentSource nextContentSource;
    private int[] nspCounts;
    private String[] nspStack;
    private boolean parsedTopLevelStartTag;
    private int position;
    private String prefix;
    private boolean processDocDecl;
    private boolean processNsp;
    private String publicId;
    private Reader reader;
    private boolean relaxed;
    private String rootElementName;
    private Boolean standalone;
    public final StringPool stringPool;
    private String systemId;
    private String text;
    private int type;
    private boolean unresolved;
    private String version;

    static class ContentSource {
        private final char[] buffer;
        private final int limit;
        private final ContentSource next;
        private final int position;

        ContentSource(org.kxml2.io.KXmlParser.ContentSource r1, char[] r2, int r3, int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.kxml2.io.KXmlParser.ContentSource.<init>(org.kxml2.io.KXmlParser$ContentSource, char[], int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.kxml2.io.KXmlParser.ContentSource.<init>(org.kxml2.io.KXmlParser$ContentSource, char[], int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.ContentSource.<init>(org.kxml2.io.KXmlParser$ContentSource, char[], int, int):void");
        }

        static /* synthetic */ char[] access$000(org.kxml2.io.KXmlParser.ContentSource r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.kxml2.io.KXmlParser.ContentSource.access$000(org.kxml2.io.KXmlParser$ContentSource):char[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.kxml2.io.KXmlParser.ContentSource.access$000(org.kxml2.io.KXmlParser$ContentSource):char[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.ContentSource.access$000(org.kxml2.io.KXmlParser$ContentSource):char[]");
        }

        static /* synthetic */ int access$100(org.kxml2.io.KXmlParser.ContentSource r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.kxml2.io.KXmlParser.ContentSource.access$100(org.kxml2.io.KXmlParser$ContentSource):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.kxml2.io.KXmlParser.ContentSource.access$100(org.kxml2.io.KXmlParser$ContentSource):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.ContentSource.access$100(org.kxml2.io.KXmlParser$ContentSource):int");
        }

        static /* synthetic */ int access$200(org.kxml2.io.KXmlParser.ContentSource r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.kxml2.io.KXmlParser.ContentSource.access$200(org.kxml2.io.KXmlParser$ContentSource):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.kxml2.io.KXmlParser.ContentSource.access$200(org.kxml2.io.KXmlParser$ContentSource):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.ContentSource.access$200(org.kxml2.io.KXmlParser$ContentSource):int");
        }

        static /* synthetic */ org.kxml2.io.KXmlParser.ContentSource access$300(org.kxml2.io.KXmlParser.ContentSource r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.kxml2.io.KXmlParser.ContentSource.access$300(org.kxml2.io.KXmlParser$ContentSource):org.kxml2.io.KXmlParser$ContentSource
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.kxml2.io.KXmlParser.ContentSource.access$300(org.kxml2.io.KXmlParser$ContentSource):org.kxml2.io.KXmlParser$ContentSource
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.ContentSource.access$300(org.kxml2.io.KXmlParser$ContentSource):org.kxml2.io.KXmlParser$ContentSource");
        }
    }

    enum ValueContext {
        ATTRIBUTE,
        TEXT,
        ENTITY_DECLARATION
    }

    public KXmlParser() {
        this.elementStack = new String[16];
        this.nspStack = new String[8];
        this.nspCounts = new int[4];
        this.buffer = new char[DateIntervalFormat.FORMAT_UTC];
        this.position = 0;
        this.limit = 0;
        this.attributes = new String[16];
        this.stringPool = new StringPool();
    }

    static {
        DEFAULT_ENTITIES = new HashMap();
        DEFAULT_ENTITIES.put("lt", "<");
        DEFAULT_ENTITIES.put("gt", ">");
        DEFAULT_ENTITIES.put("amp", "&");
        DEFAULT_ENTITIES.put("apos", "'");
        DEFAULT_ENTITIES.put("quot", "\"");
        START_COMMENT = new char[]{'<', '!', '-', '-'};
        END_COMMENT = new char[]{'-', '-', '>'};
        COMMENT_DOUBLE_DASH = new char[]{'-', '-'};
        START_CDATA = new char[]{'<', '!', '[', 'C', 'D', 'A', 'T', 'A', '['};
        END_CDATA = new char[]{']', ']', '>'};
        START_PROCESSING_INSTRUCTION = new char[]{'<', '?'};
        END_PROCESSING_INSTRUCTION = new char[]{'?', '>'};
        START_DOCTYPE = new char[]{'<', '!', 'D', 'O', 'C', 'T', 'Y', 'P', 'E'};
        SYSTEM = new char[]{'S', 'Y', 'S', 'T', 'E', 'M'};
        PUBLIC = new char[]{'P', 'U', 'B', 'L', 'I', 'C'};
        START_ELEMENT = new char[]{'<', '!', 'E', 'L', 'E', 'M', 'E', 'N', 'T'};
        START_ATTLIST = new char[]{'<', '!', 'A', 'T', 'T', 'L', 'I', 'S', 'T'};
        START_ENTITY = new char[]{'<', '!', 'E', 'N', 'T', 'I', 'T', 'Y'};
        START_NOTATION = new char[]{'<', '!', 'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N'};
        EMPTY = new char[]{'E', 'M', 'P', 'T', 'Y'};
        ANY = new char[]{'A', 'N', 'Y'};
        NDATA = new char[]{'N', 'D', 'A', 'T', 'A'};
        NOTATION = new char[]{'N', 'O', 'T', 'A', 'T', 'I', 'O', 'N'};
        REQUIRED = new char[]{'R', 'E', 'Q', 'U', 'I', 'R', 'E', 'D'};
        IMPLIED = new char[]{'I', 'M', 'P', 'L', 'I', 'E', 'D'};
        FIXED = new char[]{'F', 'I', 'X', 'E', 'D'};
        SINGLE_QUOTE = new char[]{'\''};
        DOUBLE_QUOTE = new char[]{'\"'};
    }

    public void keepNamespaceAttributes() {
        this.keepNamespaceAttributes = true;
    }

    private boolean adjustNsp() throws XmlPullParserException {
        int cut;
        boolean any = false;
        int i = 0;
        while (i < (this.attributeCount << 2)) {
            String prefix;
            String attrName = this.attributes[i + 2];
            cut = attrName.indexOf(58);
            if (cut != -1) {
                prefix = attrName.substring(0, cut);
                attrName = attrName.substring(cut + 1);
            } else if (attrName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                prefix = attrName;
                attrName = null;
            } else {
                i += 4;
            }
            if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                int[] iArr = this.nspCounts;
                int i2 = this.depth;
                int i3 = iArr[i2];
                iArr[i2] = i3 + 1;
                int j = i3 << 1;
                this.nspStack = ensureCapacity(this.nspStack, j + 2);
                this.nspStack[j] = attrName;
                this.nspStack[j + 1] = this.attributes[i + 3];
                if (attrName != null && this.attributes[i + 3].isEmpty()) {
                    checkRelaxed("illegal empty namespace");
                }
                if (this.keepNamespaceAttributes) {
                    this.attributes[i] = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
                    any = true;
                } else {
                    Object obj = this.attributes;
                    i2 = i + 4;
                    Object obj2 = this.attributes;
                    int i4 = this.attributeCount - 1;
                    this.attributeCount = i4;
                    System.arraycopy(obj, i2, obj2, i, (i4 << 2) - i);
                    i -= 4;
                }
            } else {
                any = true;
            }
            i += 4;
        }
        if (any) {
            i = (this.attributeCount << 2) - 4;
            while (i >= 0) {
                attrName = this.attributes[i + 2];
                cut = attrName.indexOf(58);
                if (cut != 0 || this.relaxed) {
                    if (cut != -1) {
                        String attrPrefix = attrName.substring(0, cut);
                        attrName = attrName.substring(cut + 1);
                        String attrNs = getNamespace(attrPrefix);
                        if (attrNs != null || this.relaxed) {
                            this.attributes[i] = attrNs;
                            this.attributes[i + 1] = attrPrefix;
                            this.attributes[i + 2] = attrName;
                        } else {
                            throw new RuntimeException("Undefined Prefix: " + attrPrefix + " in " + this);
                        }
                    }
                    i -= 4;
                } else {
                    throw new RuntimeException("illegal attribute name: " + attrName + " at " + this);
                }
            }
        }
        cut = this.name.indexOf(58);
        if (cut == 0) {
            checkRelaxed("illegal tag name: " + this.name);
        }
        if (cut != -1) {
            this.prefix = this.name.substring(0, cut);
            this.name = this.name.substring(cut + 1);
        }
        this.namespace = getNamespace(this.prefix);
        if (this.namespace == null) {
            if (this.prefix != null) {
                checkRelaxed("undefined prefix: " + this.prefix);
            }
            this.namespace = XmlPullParser.NO_NAMESPACE;
        }
        return any;
    }

    private String[] ensureCapacity(String[] arr, int required) {
        if (arr.length >= required) {
            return arr;
        }
        Object bigger = new String[(required + 16)];
        System.arraycopy((Object) arr, 0, bigger, 0, arr.length);
        return bigger;
    }

    private void checkRelaxed(String errorMessage) throws XmlPullParserException {
        if (!this.relaxed) {
            throw new XmlPullParserException(errorMessage, this, null);
        } else if (this.error == null) {
            this.error = "Error: " + errorMessage;
        }
    }

    public int next() throws XmlPullParserException, IOException {
        return next(false);
    }

    public int nextToken() throws XmlPullParserException, IOException {
        return next(true);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int next(boolean r13) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
        r12 = this;
        r8 = 3;
        r11 = 4;
        r5 = 1;
        r6 = 0;
        r10 = 0;
        r7 = r12.reader;
        if (r7 != 0) goto L_0x0011;
    L_0x0009:
        r5 = new org.xmlpull.v1.XmlPullParserException;
        r6 = "setInput() must be called first.";
        r5.<init>(r6, r12, r10);
        throw r5;
    L_0x0011:
        r7 = r12.type;
        if (r7 != r8) goto L_0x001b;
    L_0x0015:
        r7 = r12.depth;
        r7 = r7 + -1;
        r12.depth = r7;
    L_0x001b:
        r7 = r12.degenerated;
        if (r7 == 0) goto L_0x0026;
    L_0x001f:
        r12.degenerated = r6;
        r12.type = r8;
        r5 = r12.type;
    L_0x0025:
        return r5;
    L_0x0026:
        r7 = r12.error;
        if (r7 == 0) goto L_0x003b;
    L_0x002a:
        if (r13 == 0) goto L_0x0039;
    L_0x002c:
        r5 = r12.error;
        r12.text = r5;
        r5 = 9;
        r12.type = r5;
        r12.error = r10;
        r5 = r12.type;
        goto L_0x0025;
    L_0x0039:
        r12.error = r10;
    L_0x003b:
        r7 = r12.peekType(r6);
        r12.type = r7;
        r7 = r12.type;
        r8 = 998; // 0x3e6 float:1.398E-42 double:4.93E-321;
        if (r7 != r8) goto L_0x0050;
    L_0x0047:
        r12.readXmlDeclaration();
        r7 = r12.peekType(r6);
        r12.type = r7;
    L_0x0050:
        r12.text = r10;
        r12.isWhitespace = r5;
        r12.prefix = r10;
        r12.name = r10;
        r12.namespace = r10;
        r7 = -1;
        r12.attributeCount = r7;
        if (r13 != 0) goto L_0x006d;
    L_0x005f:
        r4 = r5;
    L_0x0060:
        r7 = r12.type;
        switch(r7) {
            case 1: goto L_0x007b;
            case 2: goto L_0x006f;
            case 3: goto L_0x0075;
            case 4: goto L_0x00aa;
            case 5: goto L_0x00c5;
            case 6: goto L_0x007e;
            case 7: goto L_0x0065;
            case 8: goto L_0x00dc;
            case 9: goto L_0x00d3;
            case 10: goto L_0x00ec;
            default: goto L_0x0065;
        };
    L_0x0065:
        r5 = new org.xmlpull.v1.XmlPullParserException;
        r6 = "Unexpected token";
        r5.<init>(r6, r12, r10);
        throw r5;
    L_0x006d:
        r4 = r6;
        goto L_0x0060;
    L_0x006f:
        r12.parseStartTag(r6, r4);
        r5 = r12.type;
        goto L_0x0025;
    L_0x0075:
        r12.readEndTag();
        r5 = r12.type;
        goto L_0x0025;
    L_0x007b:
        r5 = r12.type;
        goto L_0x0025;
    L_0x007e:
        if (r13 == 0) goto L_0x00aa;
    L_0x0080:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r7 = org.kxml2.io.KXmlParser.ValueContext.TEXT;
        r12.readEntity(r1, r5, r4, r7);
        r7 = r1.toString();
        r12.text = r7;
    L_0x0090:
        r7 = r12.depth;
        if (r7 != 0) goto L_0x00fb;
    L_0x0094:
        r7 = r12.type;
        r8 = 6;
        if (r7 == r8) goto L_0x00a2;
    L_0x0099:
        r7 = r12.type;
        if (r7 == r11) goto L_0x00a2;
    L_0x009d:
        r7 = r12.type;
        r8 = 5;
        if (r7 != r8) goto L_0x00fb;
    L_0x00a2:
        r5 = new org.xmlpull.v1.XmlPullParserException;
        r6 = "Unexpected token";
        r5.<init>(r6, r12, r10);
        throw r5;
    L_0x00aa:
        r8 = 60;
        if (r13 != 0) goto L_0x00c3;
    L_0x00ae:
        r7 = r5;
    L_0x00af:
        r9 = org.kxml2.io.KXmlParser.ValueContext.TEXT;
        r7 = r12.readValue(r8, r7, r4, r9);
        r12.text = r7;
        r7 = r12.depth;
        if (r7 != 0) goto L_0x0090;
    L_0x00bb:
        r7 = r12.isWhitespace;
        if (r7 == 0) goto L_0x0090;
    L_0x00bf:
        r7 = 7;
        r12.type = r7;
        goto L_0x0090;
    L_0x00c3:
        r7 = r6;
        goto L_0x00af;
    L_0x00c5:
        r7 = START_CDATA;
        r12.read(r7);
        r7 = END_CDATA;
        r7 = r12.readUntil(r7, r5);
        r12.text = r7;
        goto L_0x0090;
    L_0x00d3:
        r0 = r12.readComment(r13);
        if (r13 == 0) goto L_0x0090;
    L_0x00d9:
        r12.text = r0;
        goto L_0x0090;
    L_0x00dc:
        r7 = START_PROCESSING_INSTRUCTION;
        r12.read(r7);
        r7 = END_PROCESSING_INSTRUCTION;
        r3 = r12.readUntil(r7, r13);
        if (r13 == 0) goto L_0x0090;
    L_0x00e9:
        r12.text = r3;
        goto L_0x0090;
    L_0x00ec:
        r12.readDoctype(r13);
        r7 = r12.parsedTopLevelStartTag;
        if (r7 == 0) goto L_0x0090;
    L_0x00f3:
        r5 = new org.xmlpull.v1.XmlPullParserException;
        r6 = "Unexpected token";
        r5.<init>(r6, r12, r10);
        throw r5;
    L_0x00fb:
        if (r13 == 0) goto L_0x0101;
    L_0x00fd:
        r5 = r12.type;
        goto L_0x0025;
    L_0x0101:
        r7 = r12.type;
        r8 = 7;
        if (r7 != r8) goto L_0x0108;
    L_0x0106:
        r12.text = r10;
    L_0x0108:
        r2 = r12.peekType(r6);
        r7 = r12.text;
        if (r7 == 0) goto L_0x0120;
    L_0x0110:
        r7 = r12.text;
        r7 = r7.isEmpty();
        if (r7 != 0) goto L_0x0120;
    L_0x0118:
        if (r2 >= r11) goto L_0x0120;
    L_0x011a:
        r12.type = r11;
        r5 = r12.type;
        goto L_0x0025;
    L_0x0120:
        r12.type = r2;
        goto L_0x0060;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.next(boolean):int");
    }

    private String readUntil(char[] delimiter, boolean returnText) throws IOException, XmlPullParserException {
        int start = this.position;
        StringBuilder result = null;
        if (returnText && this.text != null) {
            result = new StringBuilder();
            result.append(this.text);
        }
        loop0:
        while (true) {
            if (this.position + delimiter.length > this.limit) {
                if (start < this.position && returnText) {
                    if (result == null) {
                        result = new StringBuilder();
                    }
                    result.append(this.buffer, start, this.position - start);
                }
                if (fillBuffer(delimiter.length)) {
                    start = this.position;
                } else {
                    checkRelaxed(UNEXPECTED_EOF);
                    this.type = 9;
                    return null;
                }
            }
            int i = 0;
            while (i < delimiter.length) {
                if (this.buffer[this.position + i] != delimiter[i]) {
                    this.position++;
                } else {
                    i++;
                }
            }
            break loop0;
        }
        int end = this.position;
        this.position += delimiter.length;
        if (!returnText) {
            return null;
        }
        if (result == null) {
            return this.stringPool.get(this.buffer, start, end - start);
        }
        result.append(this.buffer, start, end - start);
        return result.toString();
    }

    private void readXmlDeclaration() throws IOException, XmlPullParserException {
        if (!(this.bufferStartLine == 0 && this.bufferStartColumn == 0 && this.position == 0)) {
            checkRelaxed("processing instructions must not start with xml");
        }
        read(START_PROCESSING_INSTRUCTION);
        parseStartTag(true, true);
        if (this.attributeCount < 1 || !OutputKeys.VERSION.equals(this.attributes[2])) {
            checkRelaxed("version expected");
        }
        this.version = this.attributes[3];
        int pos = 1;
        if (1 < this.attributeCount && OutputKeys.ENCODING.equals(this.attributes[6])) {
            this.encoding = this.attributes[7];
            pos = 1 + 1;
        }
        if (pos < this.attributeCount && OutputKeys.STANDALONE.equals(this.attributes[(pos * 4) + 2])) {
            String st = this.attributes[(pos * 4) + 3];
            if ("yes".equals(st)) {
                this.standalone = Boolean.TRUE;
            } else if ("no".equals(st)) {
                this.standalone = Boolean.FALSE;
            } else {
                checkRelaxed("illegal standalone value: " + st);
            }
            pos++;
        }
        if (pos != this.attributeCount) {
            checkRelaxed("unexpected attributes in XML declaration");
        }
        this.isWhitespace = true;
        this.text = null;
    }

    private String readComment(boolean returnText) throws IOException, XmlPullParserException {
        read(START_COMMENT);
        if (this.relaxed) {
            return readUntil(END_COMMENT, returnText);
        }
        String commentText = readUntil(COMMENT_DOUBLE_DASH, returnText);
        if (peekCharacter() != 62) {
            throw new XmlPullParserException("Comments may not contain --", this, null);
        }
        this.position++;
        return commentText;
    }

    private void readDoctype(boolean saveDtdText) throws IOException, XmlPullParserException {
        read(START_DOCTYPE);
        int startPosition = -1;
        if (saveDtdText) {
            this.bufferCapture = new StringBuilder();
            startPosition = this.position;
        }
        try {
            skip();
            this.rootElementName = readName();
            readExternalId(true, true);
            skip();
            if (peekCharacter() == 91) {
                readInternalSubset();
            }
            skip();
            read('>');
        } finally {
            if (saveDtdText) {
                this.bufferCapture.append(this.buffer, 0, this.position);
                this.bufferCapture.delete(0, startPosition);
                this.text = this.bufferCapture.toString();
                this.bufferCapture = null;
            }
        }
    }

    private boolean readExternalId(boolean requireSystemName, boolean assignFields) throws IOException, XmlPullParserException {
        skip();
        int c = peekCharacter();
        if (c == 83) {
            read(SYSTEM);
        } else if (c != 80) {
            return false;
        } else {
            read(PUBLIC);
            skip();
            if (assignFields) {
                this.publicId = readQuotedId(true);
            } else {
                readQuotedId(false);
            }
        }
        skip();
        if (!requireSystemName) {
            int delimiter = peekCharacter();
            if (!(delimiter == 34 || delimiter == 39)) {
                return true;
            }
        }
        if (assignFields) {
            this.systemId = readQuotedId(true);
        } else {
            readQuotedId(false);
        }
        return true;
    }

    private String readQuotedId(boolean returnText) throws IOException, XmlPullParserException {
        char[] delimiter;
        int quote = peekCharacter();
        if (quote == 34) {
            delimiter = DOUBLE_QUOTE;
        } else if (quote == 39) {
            delimiter = SINGLE_QUOTE;
        } else {
            throw new XmlPullParserException("Expected a quoted string", this, null);
        }
        this.position++;
        return readUntil(delimiter, returnText);
    }

    private void readInternalSubset() throws IOException, XmlPullParserException {
        read('[');
        while (true) {
            skip();
            if (peekCharacter() == 93) {
                this.position++;
                return;
            }
            switch (peekType(true)) {
                case NodeFilter.SHOW_CDATA_SECTION /*8*/:
                    read(START_PROCESSING_INSTRUCTION);
                    readUntil(END_PROCESSING_INSTRUCTION, false);
                    break;
                case XmlPullParser.COMMENT /*9*/:
                    readComment(false);
                    break;
                case ELEMENTDECL /*11*/:
                    readElementDeclaration();
                    break;
                case ENTITYDECL /*12*/:
                    readEntityDeclaration();
                    break;
                case ATTLISTDECL /*13*/:
                    readAttributeListDeclaration();
                    break;
                case NOTATIONDECL /*14*/:
                    readNotationDeclaration();
                    break;
                case PARAMETER_ENTITY_REF /*15*/:
                    throw new XmlPullParserException("Parameter entity references are not supported", this, null);
                default:
                    throw new XmlPullParserException("Unexpected token", this, null);
            }
        }
    }

    private void readElementDeclaration() throws IOException, XmlPullParserException {
        read(START_ELEMENT);
        skip();
        readName();
        readContentSpec();
        skip();
        read('>');
    }

    private void readContentSpec() throws IOException, XmlPullParserException {
        skip();
        char c = peekCharacter();
        if (c == '(') {
            int c2;
            int depth = 0;
            do {
                if (c2 == 40) {
                    depth++;
                } else if (c2 == 41) {
                    depth--;
                } else if (c2 == -1) {
                    throw new XmlPullParserException("Unterminated element content spec", this, null);
                }
                this.position++;
                c2 = peekCharacter();
            } while (depth > 0);
            if (c2 == 42 || c2 == 63 || c2 == 43) {
                this.position++;
            }
        } else if (c == EMPTY[0]) {
            read(EMPTY);
        } else if (c == ANY[0]) {
            read(ANY);
        } else {
            throw new XmlPullParserException("Expected element content spec", this, null);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readAttributeListDeclaration() throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
        r9 = this;
        r8 = 0;
        r7 = 1;
        r4 = START_ATTLIST;
        r9.read(r4);
        r9.skip();
        r2 = r9.readName();
    L_0x000e:
        r9.skip();
        r1 = r9.peekCharacter();
        r4 = 62;
        if (r1 != r4) goto L_0x0020;
    L_0x0019:
        r4 = r9.position;
        r4 = r4 + 1;
        r9.position = r4;
        return;
    L_0x0020:
        r0 = r9.readName();
        r9.skip();
        r4 = r9.position;
        r4 = r4 + 1;
        r5 = r9.limit;
        if (r4 < r5) goto L_0x003e;
    L_0x002f:
        r4 = 2;
        r4 = r9.fillBuffer(r4);
        if (r4 != 0) goto L_0x003e;
    L_0x0036:
        r4 = new org.xmlpull.v1.XmlPullParserException;
        r5 = "Malformed attribute list";
        r4.<init>(r5, r9, r8);
        throw r4;
    L_0x003e:
        r4 = r9.buffer;
        r5 = r9.position;
        r4 = r4[r5];
        r5 = NOTATION;
        r6 = 0;
        r5 = r5[r6];
        if (r4 != r5) goto L_0x0061;
    L_0x004b:
        r4 = r9.buffer;
        r5 = r9.position;
        r5 = r5 + 1;
        r4 = r4[r5];
        r5 = NOTATION;
        r5 = r5[r7];
        if (r4 != r5) goto L_0x0061;
    L_0x0059:
        r4 = NOTATION;
        r9.read(r4);
        r9.skip();
    L_0x0061:
        r1 = r9.peekCharacter();
        r4 = 40;
        if (r1 != r4) goto L_0x00e4;
    L_0x0069:
        r4 = r9.position;
        r4 = r4 + 1;
        r9.position = r4;
    L_0x006f:
        r9.skip();
        r9.readName();
        r9.skip();
        r1 = r9.peekCharacter();
        r4 = 41;
        if (r1 != r4) goto L_0x00d1;
    L_0x0080:
        r4 = r9.position;
        r4 = r4 + 1;
        r9.position = r4;
    L_0x0086:
        r9.skip();
        r1 = r9.peekCharacter();
        r4 = 35;
        if (r1 != r4) goto L_0x00ab;
    L_0x0091:
        r4 = r9.position;
        r4 = r4 + 1;
        r9.position = r4;
        r1 = r9.peekCharacter();
        r4 = 82;
        if (r1 != r4) goto L_0x00e8;
    L_0x009f:
        r4 = REQUIRED;
        r9.read(r4);
    L_0x00a4:
        r9.skip();
        r1 = r9.peekCharacter();
    L_0x00ab:
        r4 = 34;
        if (r1 == r4) goto L_0x00b3;
    L_0x00af:
        r4 = 39;
        if (r1 != r4) goto L_0x000e;
    L_0x00b3:
        r4 = r9.position;
        r4 = r4 + 1;
        r9.position = r4;
        r4 = (char) r1;
        r5 = org.kxml2.io.KXmlParser.ValueContext.ATTRIBUTE;
        r3 = r9.readValue(r4, r7, r7, r5);
        r4 = r9.peekCharacter();
        if (r4 != r1) goto L_0x00cc;
    L_0x00c6:
        r4 = r9.position;
        r4 = r4 + 1;
        r9.position = r4;
    L_0x00cc:
        r9.defineAttributeDefault(r2, r0, r3);
        goto L_0x000e;
    L_0x00d1:
        r4 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        if (r1 != r4) goto L_0x00dc;
    L_0x00d5:
        r4 = r9.position;
        r4 = r4 + 1;
        r9.position = r4;
        goto L_0x006f;
    L_0x00dc:
        r4 = new org.xmlpull.v1.XmlPullParserException;
        r5 = "Malformed attribute type";
        r4.<init>(r5, r9, r8);
        throw r4;
    L_0x00e4:
        r9.readName();
        goto L_0x0086;
    L_0x00e8:
        r4 = 73;
        if (r1 != r4) goto L_0x00f2;
    L_0x00ec:
        r4 = IMPLIED;
        r9.read(r4);
        goto L_0x00a4;
    L_0x00f2:
        r4 = 70;
        if (r1 != r4) goto L_0x00fc;
    L_0x00f6:
        r4 = FIXED;
        r9.read(r4);
        goto L_0x00a4;
    L_0x00fc:
        r4 = new org.xmlpull.v1.XmlPullParserException;
        r5 = "Malformed attribute type";
        r4.<init>(r5, r9, r8);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.readAttributeListDeclaration():void");
    }

    private void defineAttributeDefault(String elementName, String attributeName, String value) {
        if (this.defaultAttributes == null) {
            this.defaultAttributes = new HashMap();
        }
        Map<String, String> elementAttributes = (Map) this.defaultAttributes.get(elementName);
        if (elementAttributes == null) {
            elementAttributes = new HashMap();
            this.defaultAttributes.put(elementName, elementAttributes);
        }
        elementAttributes.put(attributeName, value);
    }

    private void readEntityDeclaration() throws IOException, XmlPullParserException {
        String entityValue;
        read(START_ENTITY);
        boolean generalEntity = true;
        skip();
        if (peekCharacter() == 37) {
            generalEntity = false;
            this.position++;
            skip();
        }
        String name = readName();
        skip();
        int quote = peekCharacter();
        if (quote == 34 || quote == 39) {
            this.position++;
            entityValue = readValue((char) quote, true, false, ValueContext.ENTITY_DECLARATION);
            if (peekCharacter() == quote) {
                this.position++;
            }
        } else if (readExternalId(true, false)) {
            entityValue = XmlPullParser.NO_NAMESPACE;
            skip();
            if (peekCharacter() == NDATA[0]) {
                read(NDATA);
                skip();
                readName();
            }
        } else {
            throw new XmlPullParserException("Expected entity value or external ID", this, null);
        }
        if (generalEntity && this.processDocDecl) {
            if (this.documentEntities == null) {
                this.documentEntities = new HashMap();
            }
            this.documentEntities.put(name, entityValue.toCharArray());
        }
        skip();
        read('>');
    }

    private void readNotationDeclaration() throws IOException, XmlPullParserException {
        read(START_NOTATION);
        skip();
        readName();
        if (readExternalId(false, false)) {
            skip();
            read('>');
            return;
        }
        throw new XmlPullParserException("Expected external ID or public ID for notation", this, null);
    }

    private void readEndTag() throws IOException, XmlPullParserException {
        read('<');
        read('/');
        this.name = readName();
        skip();
        read('>');
        int sp = (this.depth - 1) * 4;
        if (this.depth == 0) {
            checkRelaxed("read end tag " + this.name + " with no tags open");
            this.type = 9;
        } else if (this.name.equals(this.elementStack[sp + 3])) {
            this.namespace = this.elementStack[sp];
            this.prefix = this.elementStack[sp + 1];
            this.name = this.elementStack[sp + 2];
        } else if (!this.relaxed) {
            throw new XmlPullParserException("expected: /" + this.elementStack[sp + 3] + " read: " + this.name, this, null);
        }
    }

    private int peekType(boolean inDeclaration) throws IOException, XmlPullParserException {
        if (this.position >= this.limit && !fillBuffer(1)) {
            return 1;
        }
        switch (this.buffer[this.position]) {
            case Opcodes.OP_FILLED_NEW_ARRAY_RANGE /*37*/:
                if (inDeclaration) {
                    return PARAMETER_ENTITY_REF;
                }
                return 4;
            case ZipConstants.CENATX /*38*/:
                return 6;
            case Opcodes.OP_IF_GTZ /*60*/:
                if (this.position + 3 < this.limit || fillBuffer(4)) {
                    switch (this.buffer[this.position + 1]) {
                        case Opcodes.OP_ARRAY_LENGTH /*33*/:
                            switch (this.buffer[this.position + 2]) {
                                case Opcodes.OP_CMPL_FLOAT /*45*/:
                                    return 9;
                                case 'A':
                                    return ATTLISTDECL;
                                case Opcodes.OP_AGET /*68*/:
                                    return 10;
                                case Opcodes.OP_AGET_WIDE /*69*/:
                                    switch (this.buffer[this.position + 3]) {
                                        case Opcodes.OP_APUT_WIDE /*76*/:
                                            return ELEMENTDECL;
                                        case Opcodes.OP_APUT_BOOLEAN /*78*/:
                                            return ENTITYDECL;
                                        default:
                                            break;
                                    }
                                case Opcodes.OP_APUT_BOOLEAN /*78*/:
                                    return NOTATIONDECL;
                                case Types.DATE /*91*/:
                                    return 5;
                            }
                            throw new XmlPullParserException("Unexpected <!", this, null);
                        case Opcodes.OP_CMPL_DOUBLE /*47*/:
                            return 3;
                        case '?':
                            if ((this.position + 5 < this.limit || fillBuffer(6)) && ((this.buffer[this.position + 2] == Locale.PRIVATE_USE_EXTENSION || this.buffer[this.position + 2] == 'X') && ((this.buffer[this.position + 3] == 'm' || this.buffer[this.position + 3] == 'M') && ((this.buffer[this.position + 4] == 'l' || this.buffer[this.position + 4] == 'L') && this.buffer[this.position + 5] == ' ')))) {
                                return XML_DECLARATION;
                            }
                            return 8;
                        default:
                            return 2;
                    }
                }
                throw new XmlPullParserException("Dangling <", this, null);
            default:
                return 4;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseStartTag(boolean r14, boolean r15) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
        r13 = this;
        if (r14 != 0) goto L_0x0007;
    L_0x0002:
        r9 = 60;
        r13.read(r9);
    L_0x0007:
        r9 = r13.readName();
        r13.name = r9;
        r9 = 0;
        r13.attributeCount = r9;
    L_0x0010:
        r13.skip();
        r9 = r13.position;
        r10 = r13.limit;
        if (r9 < r10) goto L_0x0026;
    L_0x0019:
        r9 = 1;
        r9 = r13.fillBuffer(r9);
        if (r9 != 0) goto L_0x0026;
    L_0x0020:
        r9 = "Unexpected EOF";
        r13.checkRelaxed(r9);
    L_0x0025:
        return;
    L_0x0026:
        r9 = r13.buffer;
        r10 = r13.position;
        r2 = r9[r10];
        if (r14 == 0) goto L_0x003e;
    L_0x002e:
        r9 = 63;
        if (r2 != r9) goto L_0x0118;
    L_0x0032:
        r9 = r13.position;
        r9 = r9 + 1;
        r13.position = r9;
        r9 = 62;
        r13.read(r9);
        goto L_0x0025;
    L_0x003e:
        r9 = 47;
        if (r2 != r9) goto L_0x010c;
    L_0x0042:
        r9 = 1;
        r13.degenerated = r9;
        r9 = r13.position;
        r9 = r9 + 1;
        r13.position = r9;
        r13.skip();
        r9 = 62;
        r13.read(r9);
    L_0x0053:
        r9 = r13.depth;
        r10 = r9 + 1;
        r13.depth = r10;
        r8 = r9 * 4;
        r9 = r13.depth;
        r10 = 1;
        if (r9 != r10) goto L_0x0063;
    L_0x0060:
        r9 = 1;
        r13.parsedTopLevelStartTag = r9;
    L_0x0063:
        r9 = r13.elementStack;
        r10 = r8 + 4;
        r9 = r13.ensureCapacity(r9, r10);
        r13.elementStack = r9;
        r9 = r13.elementStack;
        r10 = r8 + 3;
        r11 = r13.name;
        r9[r10] = r11;
        r9 = r13.depth;
        r10 = r13.nspCounts;
        r10 = r10.length;
        if (r9 < r10) goto L_0x008e;
    L_0x007c:
        r9 = r13.depth;
        r9 = r9 + 4;
        r1 = new int[r9];
        r9 = r13.nspCounts;
        r10 = 0;
        r11 = 0;
        r12 = r13.nspCounts;
        r12 = r12.length;
        java.lang.System.arraycopy(r9, r10, r1, r11, r12);
        r13.nspCounts = r1;
    L_0x008e:
        r9 = r13.nspCounts;
        r10 = r13.depth;
        r11 = r13.nspCounts;
        r12 = r13.depth;
        r12 = r12 + -1;
        r11 = r11[r12];
        r9[r10] = r11;
        r9 = r13.processNsp;
        if (r9 == 0) goto L_0x01ec;
    L_0x00a0:
        r13.adjustNsp();
    L_0x00a3:
        r9 = r13.defaultAttributes;
        if (r9 == 0) goto L_0x01f2;
    L_0x00a7:
        r9 = r13.defaultAttributes;
        r10 = r13.name;
        r4 = r9.get(r10);
        r4 = (java.util.Map) r4;
        if (r4 == 0) goto L_0x01f2;
    L_0x00b3:
        r9 = r4.entrySet();
        r7 = r9.iterator();
    L_0x00bb:
        r9 = r7.hasNext();
        if (r9 == 0) goto L_0x01f2;
    L_0x00c1:
        r5 = r7.next();
        r5 = (java.util.Map.Entry) r5;
        r10 = 0;
        r9 = r5.getKey();
        r9 = (java.lang.String) r9;
        r9 = r13.getAttributeValue(r10, r9);
        if (r9 != 0) goto L_0x00bb;
    L_0x00d4:
        r9 = r13.attributeCount;
        r10 = r9 + 1;
        r13.attributeCount = r10;
        r6 = r9 * 4;
        r9 = r13.attributes;
        r10 = r6 + 4;
        r9 = r13.ensureCapacity(r9, r10);
        r13.attributes = r9;
        r9 = r13.attributes;
        r10 = "";
        r9[r6] = r10;
        r9 = r13.attributes;
        r10 = r6 + 1;
        r11 = 0;
        r9[r10] = r11;
        r10 = r13.attributes;
        r11 = r6 + 2;
        r9 = r5.getKey();
        r9 = (java.lang.String) r9;
        r10[r11] = r9;
        r10 = r13.attributes;
        r11 = r6 + 3;
        r9 = r5.getValue();
        r9 = (java.lang.String) r9;
        r10[r11] = r9;
        goto L_0x00bb;
    L_0x010c:
        r9 = 62;
        if (r2 != r9) goto L_0x0118;
    L_0x0110:
        r9 = r13.position;
        r9 = r9 + 1;
        r13.position = r9;
        goto L_0x0053;
    L_0x0118:
        r0 = r13.readName();
        r9 = r13.attributeCount;
        r10 = r9 + 1;
        r13.attributeCount = r10;
        r6 = r9 * 4;
        r9 = r13.attributes;
        r10 = r6 + 4;
        r9 = r13.ensureCapacity(r9, r10);
        r13.attributes = r9;
        r9 = r13.attributes;
        r10 = "";
        r9[r6] = r10;
        r9 = r13.attributes;
        r10 = r6 + 1;
        r11 = 0;
        r9[r10] = r11;
        r9 = r13.attributes;
        r10 = r6 + 2;
        r9[r10] = r0;
        r13.skip();
        r9 = r13.position;
        r10 = r13.limit;
        if (r9 < r10) goto L_0x0158;
    L_0x014a:
        r9 = 1;
        r9 = r13.fillBuffer(r9);
        if (r9 != 0) goto L_0x0158;
    L_0x0151:
        r9 = "Unexpected EOF";
        r13.checkRelaxed(r9);
        goto L_0x0025;
    L_0x0158:
        r9 = r13.buffer;
        r10 = r13.position;
        r9 = r9[r10];
        r10 = 61;
        if (r9 != r10) goto L_0x01c2;
    L_0x0162:
        r9 = r13.position;
        r9 = r9 + 1;
        r13.position = r9;
        r13.skip();
        r9 = r13.position;
        r10 = r13.limit;
        if (r9 < r10) goto L_0x017f;
    L_0x0171:
        r9 = 1;
        r9 = r13.fillBuffer(r9);
        if (r9 != 0) goto L_0x017f;
    L_0x0178:
        r9 = "Unexpected EOF";
        r13.checkRelaxed(r9);
        goto L_0x0025;
    L_0x017f:
        r9 = r13.buffer;
        r10 = r13.position;
        r3 = r9[r10];
        r9 = 39;
        if (r3 == r9) goto L_0x018d;
    L_0x0189:
        r9 = 34;
        if (r3 != r9) goto L_0x01b2;
    L_0x018d:
        r9 = r13.position;
        r9 = r9 + 1;
        r13.position = r9;
    L_0x0193:
        r9 = r13.attributes;
        r10 = r6 + 3;
        r11 = 1;
        r12 = org.kxml2.io.KXmlParser.ValueContext.ATTRIBUTE;
        r11 = r13.readValue(r3, r11, r15, r12);
        r9[r10] = r11;
        r9 = 32;
        if (r3 == r9) goto L_0x0010;
    L_0x01a4:
        r9 = r13.peekCharacter();
        if (r9 != r3) goto L_0x0010;
    L_0x01aa:
        r9 = r13.position;
        r9 = r9 + 1;
        r13.position = r9;
        goto L_0x0010;
    L_0x01b2:
        r9 = r13.relaxed;
        if (r9 == 0) goto L_0x01b9;
    L_0x01b6:
        r3 = 32;
        goto L_0x0193;
    L_0x01b9:
        r9 = new org.xmlpull.v1.XmlPullParserException;
        r10 = "attr value delimiter missing!";
        r11 = 0;
        r9.<init>(r10, r13, r11);
        throw r9;
    L_0x01c2:
        r9 = r13.relaxed;
        if (r9 == 0) goto L_0x01ce;
    L_0x01c6:
        r9 = r13.attributes;
        r10 = r6 + 3;
        r9[r10] = r0;
        goto L_0x0010;
    L_0x01ce:
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Attr.value missing f. ";
        r9 = r9.append(r10);
        r9 = r9.append(r0);
        r9 = r9.toString();
        r13.checkRelaxed(r9);
        r9 = r13.attributes;
        r10 = r6 + 3;
        r9[r10] = r0;
        goto L_0x0010;
    L_0x01ec:
        r9 = "";
        r13.namespace = r9;
        goto L_0x00a3;
    L_0x01f2:
        r9 = r13.elementStack;
        r10 = r13.namespace;
        r9[r8] = r10;
        r9 = r13.elementStack;
        r10 = r8 + 1;
        r11 = r13.prefix;
        r9[r10] = r11;
        r9 = r13.elementStack;
        r10 = r8 + 2;
        r11 = r13.name;
        r9[r10] = r11;
        goto L_0x0025;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.parseStartTag(boolean, boolean):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readEntity(java.lang.StringBuilder r11, boolean r12, boolean r13, org.kxml2.io.KXmlParser.ValueContext r14) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
        r10 = this;
        r6 = r11.length();
        r7 = r10.buffer;
        r8 = r10.position;
        r9 = r8 + 1;
        r10.position = r9;
        r7 = r7[r8];
        r8 = 38;
        if (r7 == r8) goto L_0x0018;
    L_0x0012:
        r7 = new java.lang.AssertionError;
        r7.<init>();
        throw r7;
    L_0x0018:
        r7 = 38;
        r11.append(r7);
    L_0x001d:
        r0 = r10.peekCharacter();
        r7 = 59;
        if (r0 != r7) goto L_0x0069;
    L_0x0025:
        r7 = 59;
        r11.append(r7);
        r7 = r10.position;
        r7 = r7 + 1;
        r10.position = r7;
        r7 = r6 + 1;
        r8 = r11.length();
        r8 = r8 + -1;
        r1 = r11.substring(r7, r8);
        if (r12 == 0) goto L_0x0040;
    L_0x003e:
        r10.name = r1;
    L_0x0040:
        r7 = "#";
        r7 = r1.startsWith(r7);
        if (r7 == 0) goto L_0x00e7;
    L_0x0048:
        r7 = "#x";
        r7 = r1.startsWith(r7);	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
        if (r7 == 0) goto L_0x00a9;
    L_0x0050:
        r7 = 2;
        r7 = r1.substring(r7);	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
        r8 = 16;
        r0 = java.lang.Integer.parseInt(r7, r8);	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
    L_0x005b:
        r7 = r11.length();	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
        r11.delete(r6, r7);	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
        r11.appendCodePoint(r0);	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
        r7 = 0;
        r10.unresolved = r7;	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
    L_0x0068:
        return;
    L_0x0069:
        r7 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        if (r0 >= r7) goto L_0x0091;
    L_0x006d:
        r7 = 48;
        if (r0 < r7) goto L_0x0075;
    L_0x0071:
        r7 = 57;
        if (r0 <= r7) goto L_0x0091;
    L_0x0075:
        r7 = 97;
        if (r0 < r7) goto L_0x007d;
    L_0x0079:
        r7 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        if (r0 <= r7) goto L_0x0091;
    L_0x007d:
        r7 = 65;
        if (r0 < r7) goto L_0x0085;
    L_0x0081:
        r7 = 90;
        if (r0 <= r7) goto L_0x0091;
    L_0x0085:
        r7 = 95;
        if (r0 == r7) goto L_0x0091;
    L_0x0089:
        r7 = 45;
        if (r0 == r7) goto L_0x0091;
    L_0x008d:
        r7 = 35;
        if (r0 != r7) goto L_0x009c;
    L_0x0091:
        r7 = r10.position;
        r7 = r7 + 1;
        r10.position = r7;
        r7 = (char) r0;
        r11.append(r7);
        goto L_0x001d;
    L_0x009c:
        r7 = r10.relaxed;
        if (r7 != 0) goto L_0x0068;
    L_0x00a0:
        r7 = new org.xmlpull.v1.XmlPullParserException;
        r8 = "unterminated entity ref";
        r9 = 0;
        r7.<init>(r8, r10, r9);
        throw r7;
    L_0x00a9:
        r7 = 1;
        r7 = r1.substring(r7);	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
        r0 = java.lang.Integer.parseInt(r7);	 Catch:{ NumberFormatException -> 0x00b3, IllegalArgumentException -> 0x00cd }
        goto L_0x005b;
    L_0x00b3:
        r4 = move-exception;
        r7 = new org.xmlpull.v1.XmlPullParserException;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "Invalid character reference: &";
        r8 = r8.append(r9);
        r8 = r8.append(r1);
        r8 = r8.toString();
        r7.<init>(r8);
        throw r7;
    L_0x00cd:
        r3 = move-exception;
        r7 = new org.xmlpull.v1.XmlPullParserException;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "Invalid character reference: &";
        r8 = r8.append(r9);
        r8 = r8.append(r1);
        r8 = r8.toString();
        r7.<init>(r8);
        throw r7;
    L_0x00e7:
        r7 = org.kxml2.io.KXmlParser.ValueContext.ENTITY_DECLARATION;
        if (r14 == r7) goto L_0x0068;
    L_0x00eb:
        r7 = DEFAULT_ENTITIES;
        r2 = r7.get(r1);
        r2 = (java.lang.String) r2;
        if (r2 == 0) goto L_0x0104;
    L_0x00f5:
        r7 = r11.length();
        r11.delete(r6, r7);
        r7 = 0;
        r10.unresolved = r7;
        r11.append(r2);
        goto L_0x0068;
    L_0x0104:
        r7 = r10.documentEntities;
        if (r7 == 0) goto L_0x012a;
    L_0x0108:
        r7 = r10.documentEntities;
        r5 = r7.get(r1);
        r5 = (char[]) r5;
        if (r5 == 0) goto L_0x012a;
    L_0x0112:
        r7 = r11.length();
        r11.delete(r6, r7);
        r7 = 0;
        r10.unresolved = r7;
        r7 = r10.processDocDecl;
        if (r7 == 0) goto L_0x0125;
    L_0x0120:
        r10.pushContentSource(r5);
        goto L_0x0068;
    L_0x0125:
        r11.append(r5);
        goto L_0x0068;
    L_0x012a:
        r7 = r10.systemId;
        if (r7 == 0) goto L_0x0137;
    L_0x012e:
        r7 = r11.length();
        r11.delete(r6, r7);
        goto L_0x0068;
    L_0x0137:
        r7 = 1;
        r10.unresolved = r7;
        if (r13 == 0) goto L_0x0068;
    L_0x013c:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "unresolved: &";
        r7 = r7.append(r8);
        r7 = r7.append(r1);
        r8 = ";";
        r7 = r7.append(r8);
        r7 = r7.toString();
        r10.checkRelaxed(r7);
        goto L_0x0068;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.readEntity(java.lang.StringBuilder, boolean, boolean, org.kxml2.io.KXmlParser$ValueContext):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String readValue(char r11, boolean r12, boolean r13, org.kxml2.io.KXmlParser.ValueContext r14) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
        r10 = this;
        r9 = 93;
        r8 = 38;
        r5 = 32;
        r6 = 10;
        r4 = 0;
        r2 = r10.position;
        r1 = 0;
        r3 = org.kxml2.io.KXmlParser.ValueContext.TEXT;
        if (r14 != r3) goto L_0x001e;
    L_0x0010:
        r3 = r10.text;
        if (r3 == 0) goto L_0x001e;
    L_0x0014:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r3 = r10.text;
        r1.append(r3);
    L_0x001e:
        r3 = r10.position;
        r7 = r10.limit;
        if (r3 < r7) goto L_0x004a;
    L_0x0024:
        r3 = r10.position;
        if (r2 >= r3) goto L_0x0037;
    L_0x0028:
        if (r1 != 0) goto L_0x002f;
    L_0x002a:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
    L_0x002f:
        r3 = r10.buffer;
        r7 = r10.position;
        r7 = r7 - r2;
        r1.append(r3, r2, r7);
    L_0x0037:
        r3 = 1;
        r3 = r10.fillBuffer(r3);
        if (r3 != 0) goto L_0x0048;
    L_0x003e:
        if (r1 == 0) goto L_0x0045;
    L_0x0040:
        r3 = r1.toString();
    L_0x0044:
        return r3;
    L_0x0045:
        r3 = "";
        goto L_0x0044;
    L_0x0048:
        r2 = r10.position;
    L_0x004a:
        r3 = r10.buffer;
        r7 = r10.position;
        r0 = r3[r7];
        if (r0 == r11) goto L_0x005e;
    L_0x0052:
        if (r11 != r5) goto L_0x005a;
    L_0x0054:
        if (r0 <= r5) goto L_0x005e;
    L_0x0056:
        r3 = 62;
        if (r0 == r3) goto L_0x005e;
    L_0x005a:
        if (r0 != r8) goto L_0x006c;
    L_0x005c:
        if (r12 != 0) goto L_0x006c;
    L_0x005e:
        if (r1 != 0) goto L_0x0143;
    L_0x0060:
        r3 = r10.stringPool;
        r4 = r10.buffer;
        r5 = r10.position;
        r5 = r5 - r2;
        r3 = r3.get(r4, r2, r5);
        goto L_0x0044;
    L_0x006c:
        r3 = 13;
        if (r0 == r3) goto L_0x009b;
    L_0x0070:
        if (r0 != r6) goto L_0x0076;
    L_0x0072:
        r3 = org.kxml2.io.KXmlParser.ValueContext.ATTRIBUTE;
        if (r14 == r3) goto L_0x009b;
    L_0x0076:
        if (r0 == r8) goto L_0x009b;
    L_0x0078:
        r3 = 60;
        if (r0 == r3) goto L_0x009b;
    L_0x007c:
        if (r0 != r9) goto L_0x0082;
    L_0x007e:
        r3 = org.kxml2.io.KXmlParser.ValueContext.TEXT;
        if (r14 == r3) goto L_0x009b;
    L_0x0082:
        r3 = 37;
        if (r0 != r3) goto L_0x008a;
    L_0x0086:
        r3 = org.kxml2.io.KXmlParser.ValueContext.ENTITY_DECLARATION;
        if (r14 == r3) goto L_0x009b;
    L_0x008a:
        r7 = r10.isWhitespace;
        if (r0 > r5) goto L_0x0099;
    L_0x008e:
        r3 = 1;
    L_0x008f:
        r3 = r3 & r7;
        r10.isWhitespace = r3;
        r3 = r10.position;
        r3 = r3 + 1;
        r10.position = r3;
        goto L_0x001e;
    L_0x0099:
        r3 = r4;
        goto L_0x008f;
    L_0x009b:
        if (r1 != 0) goto L_0x00a2;
    L_0x009d:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
    L_0x00a2:
        r3 = r10.buffer;
        r7 = r10.position;
        r7 = r7 - r2;
        r1.append(r3, r2, r7);
        r3 = 13;
        if (r0 != r3) goto L_0x00e1;
    L_0x00ae:
        r3 = r10.position;
        r3 = r3 + 1;
        r7 = r10.limit;
        if (r3 < r7) goto L_0x00bd;
    L_0x00b6:
        r3 = 2;
        r3 = r10.fillBuffer(r3);
        if (r3 == 0) goto L_0x00cd;
    L_0x00bd:
        r3 = r10.buffer;
        r7 = r10.position;
        r7 = r7 + 1;
        r3 = r3[r7];
        if (r3 != r6) goto L_0x00cd;
    L_0x00c7:
        r3 = r10.position;
        r3 = r3 + 1;
        r10.position = r3;
    L_0x00cd:
        r3 = org.kxml2.io.KXmlParser.ValueContext.ATTRIBUTE;
        if (r14 != r3) goto L_0x00df;
    L_0x00d1:
        r0 = r5;
    L_0x00d2:
        r3 = r10.position;
        r3 = r3 + 1;
        r10.position = r3;
        r1.append(r0);
        r2 = r10.position;
        goto L_0x001e;
    L_0x00df:
        r0 = r6;
        goto L_0x00d2;
    L_0x00e1:
        if (r0 != r6) goto L_0x00e6;
    L_0x00e3:
        r0 = 32;
        goto L_0x00d2;
    L_0x00e6:
        if (r0 != r8) goto L_0x00f1;
    L_0x00e8:
        r10.isWhitespace = r4;
        r10.readEntity(r1, r4, r13, r14);
        r2 = r10.position;
        goto L_0x001e;
    L_0x00f1:
        r3 = 60;
        if (r0 != r3) goto L_0x0101;
    L_0x00f5:
        r3 = org.kxml2.io.KXmlParser.ValueContext.ATTRIBUTE;
        if (r14 != r3) goto L_0x00fe;
    L_0x00f9:
        r3 = "Illegal: \"<\" inside attribute value";
        r10.checkRelaxed(r3);
    L_0x00fe:
        r10.isWhitespace = r4;
        goto L_0x00d2;
    L_0x0101:
        if (r0 != r9) goto L_0x0130;
    L_0x0103:
        r3 = r10.position;
        r3 = r3 + 2;
        r7 = r10.limit;
        if (r3 < r7) goto L_0x0112;
    L_0x010b:
        r3 = 3;
        r3 = r10.fillBuffer(r3);
        if (r3 == 0) goto L_0x012d;
    L_0x0112:
        r3 = r10.buffer;
        r7 = r10.position;
        r7 = r7 + 1;
        r3 = r3[r7];
        if (r3 != r9) goto L_0x012d;
    L_0x011c:
        r3 = r10.buffer;
        r7 = r10.position;
        r7 = r7 + 2;
        r3 = r3[r7];
        r7 = 62;
        if (r3 != r7) goto L_0x012d;
    L_0x0128:
        r3 = "Illegal: \"]]>\" outside CDATA section";
        r10.checkRelaxed(r3);
    L_0x012d:
        r10.isWhitespace = r4;
        goto L_0x00d2;
    L_0x0130:
        r3 = 37;
        if (r0 != r3) goto L_0x013d;
    L_0x0134:
        r3 = new org.xmlpull.v1.XmlPullParserException;
        r4 = "This parser doesn't support parameter entities";
        r5 = 0;
        r3.<init>(r4, r10, r5);
        throw r3;
    L_0x013d:
        r3 = new java.lang.AssertionError;
        r3.<init>();
        throw r3;
    L_0x0143:
        r3 = r10.buffer;
        r4 = r10.position;
        r4 = r4 - r2;
        r1.append(r3, r2, r4);
        r3 = r1.toString();
        goto L_0x0044;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.readValue(char, boolean, boolean, org.kxml2.io.KXmlParser$ValueContext):java.lang.String");
    }

    private void read(char expected) throws IOException, XmlPullParserException {
        char c = peekCharacter();
        if (c != expected) {
            checkRelaxed("expected: '" + expected + "' actual: '" + ((char) c) + "'");
            if (c == '\uffff') {
                return;
            }
        }
        this.position++;
    }

    private void read(char[] chars) throws IOException, XmlPullParserException {
        if (this.position + chars.length <= this.limit || fillBuffer(chars.length)) {
            for (int i = 0; i < chars.length; i++) {
                if (this.buffer[this.position + i] != chars[i]) {
                    checkRelaxed("expected: \"" + new String(chars) + "\" but was \"" + new String(this.buffer, this.position, chars.length) + "...\"");
                }
            }
            this.position += chars.length;
            return;
        }
        checkRelaxed("expected: '" + new String(chars) + "' but was EOF");
    }

    private int peekCharacter() throws IOException, XmlPullParserException {
        if (this.position < this.limit || fillBuffer(1)) {
            return this.buffer[this.position];
        }
        return -1;
    }

    private boolean fillBuffer(int minimum) throws IOException, XmlPullParserException {
        while (this.nextContentSource != null) {
            if (this.position < this.limit) {
                throw new XmlPullParserException("Unbalanced entity!", this, null);
            }
            popContentSource();
            if (this.limit - this.position >= minimum) {
                return true;
            }
        }
        for (int i = 0; i < this.position; i++) {
            if (this.buffer[i] == '\n') {
                this.bufferStartLine++;
                this.bufferStartColumn = 0;
            } else {
                this.bufferStartColumn++;
            }
        }
        if (this.bufferCapture != null) {
            this.bufferCapture.append(this.buffer, 0, this.position);
        }
        if (this.limit != this.position) {
            this.limit -= this.position;
            System.arraycopy(this.buffer, this.position, this.buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.position = 0;
        do {
            int total = this.reader.read(this.buffer, this.limit, this.buffer.length - this.limit);
            if (total == -1) {
                return false;
            }
            this.limit += total;
        } while (this.limit < minimum);
        return true;
    }

    private String readName() throws IOException, XmlPullParserException {
        if (this.position < this.limit || fillBuffer(1)) {
            int start = this.position;
            StringBuilder result = null;
            char c = this.buffer[this.position];
            if ((c < 'a' || c > 'z') && !((c >= 'A' && c <= 'Z') || c == '_' || c == ':' || c >= '\u00c0' || this.relaxed)) {
                checkRelaxed("name expected");
                return XmlPullParser.NO_NAMESPACE;
            }
            this.position++;
            while (true) {
                if (this.position >= this.limit) {
                    if (result == null) {
                        result = new StringBuilder();
                    }
                    result.append(this.buffer, start, this.position - start);
                    if (!fillBuffer(1)) {
                        return result.toString();
                    }
                    start = this.position;
                }
                c = this.buffer[this.position];
                if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && !((c >= '0' && c <= '9') || c == '_' || c == '-' || c == ':' || c == '.' || c >= '\u00b7'))) {
                    break;
                }
                this.position++;
            }
            if (result == null) {
                return this.stringPool.get(this.buffer, start, this.position - start);
            }
            result.append(this.buffer, start, this.position - start);
            return result.toString();
        }
        checkRelaxed("name expected");
        return XmlPullParser.NO_NAMESPACE;
    }

    private void skip() throws IOException, XmlPullParserException {
        while (true) {
            if ((this.position < this.limit || fillBuffer(1)) && this.buffer[this.position] <= 32) {
                this.position++;
            } else {
                return;
            }
        }
    }

    public void setInput(Reader reader) throws XmlPullParserException {
        this.reader = reader;
        this.type = 0;
        this.name = null;
        this.namespace = null;
        this.degenerated = false;
        this.attributeCount = -1;
        this.encoding = null;
        this.version = null;
        this.standalone = null;
        if (reader != null) {
            this.position = 0;
            this.limit = 0;
            this.bufferStartLine = 0;
            this.bufferStartColumn = 0;
            this.depth = 0;
            this.documentEntities = null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setInput(java.io.InputStream r18, java.lang.String r19) throws org.xmlpull.v1.XmlPullParserException {
        /*
        r17 = this;
        r12 = 0;
        r0 = r17;
        r0.position = r12;
        r12 = 0;
        r0 = r17;
        r0.limit = r12;
        if (r19 != 0) goto L_0x0017;
    L_0x000c:
        r3 = 1;
    L_0x000d:
        if (r18 != 0) goto L_0x0019;
    L_0x000f:
        r12 = new java.lang.IllegalArgumentException;
        r13 = "is == null";
        r12.<init>(r13);
        throw r12;
    L_0x0017:
        r3 = 0;
        goto L_0x000d;
    L_0x0019:
        if (r3 == 0) goto L_0x005c;
    L_0x001b:
        r5 = 0;
    L_0x001c:
        r0 = r17;
        r12 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r13 = 4;
        if (r12 >= r13) goto L_0x002a;
    L_0x0023:
        r6 = r18.read();	 Catch:{ Exception -> 0x00bc }
        r12 = -1;
        if (r6 != r12) goto L_0x00a5;
    L_0x002a:
        r0 = r17;
        r12 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r13 = 4;
        if (r12 != r13) goto L_0x005c;
    L_0x0031:
        switch(r5) {
            case -131072: goto L_0x00e1;
            case 60: goto L_0x00ea;
            case 65279: goto L_0x00d8;
            case 3932223: goto L_0x010e;
            case 1006632960: goto L_0x00fc;
            case 1006649088: goto L_0x0129;
            case 1010792557: goto L_0x0144;
            default: goto L_0x0034;
        };	 Catch:{ Exception -> 0x00bc }
    L_0x0034:
        r12 = -65536; // 0xffffffffffff0000 float:NaN double:NaN;
        r12 = r12 & r5;
        r13 = -16842752; // 0xfffffffffeff0000 float:-1.6947657E38 double:NaN;
        if (r12 != r13) goto L_0x019c;
    L_0x003b:
        r19 = "UTF-16BE";
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r0 = r17;
        r14 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r15 = 2;
        r14 = r14[r15];	 Catch:{ Exception -> 0x00bc }
        r14 = r14 << 8;
        r0 = r17;
        r15 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r16 = 3;
        r15 = r15[r16];	 Catch:{ Exception -> 0x00bc }
        r14 = r14 | r15;
        r14 = (char) r14;	 Catch:{ Exception -> 0x00bc }
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 1;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
    L_0x005c:
        if (r19 != 0) goto L_0x0060;
    L_0x005e:
        r19 = "UTF-8";
    L_0x0060:
        r0 = r17;
        r11 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r12 = new java.io.InputStreamReader;	 Catch:{ Exception -> 0x00bc }
        r0 = r18;
        r1 = r19;
        r12.<init>(r0, r1);	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r0.setInput(r12);	 Catch:{ Exception -> 0x00bc }
        r0 = r19;
        r1 = r17;
        r1.encoding = r0;	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r0.limit = r11;	 Catch:{ Exception -> 0x00bc }
        if (r3 != 0) goto L_0x00a4;
    L_0x007e:
        r12 = r17.peekCharacter();	 Catch:{ Exception -> 0x00bc }
        r13 = 65279; // 0xfeff float:9.1475E-41 double:3.2252E-319;
        if (r12 != r13) goto L_0x00a4;
    L_0x0087:
        r0 = r17;
        r12 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r12 = r12 + -1;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 1;
        r0 = r17;
        r14 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r15 = 0;
        r0 = r17;
        r0 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r16 = r0;
        java.lang.System.arraycopy(r12, r13, r14, r15, r16);	 Catch:{ Exception -> 0x00bc }
    L_0x00a4:
        return;
    L_0x00a5:
        r12 = r5 << 8;
        r5 = r12 | r6;
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r13 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r14 = r13 + 1;
        r0 = r17;
        r0.limit = r14;	 Catch:{ Exception -> 0x00bc }
        r14 = (char) r6;	 Catch:{ Exception -> 0x00bc }
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        goto L_0x001c;
    L_0x00bc:
        r4 = move-exception;
        r12 = new org.xmlpull.v1.XmlPullParserException;
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r14 = "Invalid stream or encoding: ";
        r13 = r13.append(r14);
        r13 = r13.append(r4);
        r13 = r13.toString();
        r0 = r17;
        r12.<init>(r13, r0, r4);
        throw r12;
    L_0x00d8:
        r19 = "UTF-32BE";
        r12 = 0;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x00e1:
        r19 = "UTF-32LE";
        r12 = 0;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x00ea:
        r19 = "UTF-32BE";
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r14 = 60;
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 1;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x00fc:
        r19 = "UTF-32LE";
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r14 = 60;
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 1;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x010e:
        r19 = "UTF-16BE";
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r14 = 60;
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 1;
        r14 = 63;
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 2;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x0129:
        r19 = "UTF-16LE";
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r14 = 60;
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 1;
        r14 = 63;
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 2;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x0144:
        r6 = r18.read();	 Catch:{ Exception -> 0x00bc }
        r12 = -1;
        if (r6 == r12) goto L_0x005c;
    L_0x014b:
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r13 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r14 = r13 + 1;
        r0 = r17;
        r0.limit = r14;	 Catch:{ Exception -> 0x00bc }
        r14 = (char) r6;	 Catch:{ Exception -> 0x00bc }
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 62;
        if (r6 != r12) goto L_0x0144;
    L_0x0160:
        r10 = new java.lang.String;	 Catch:{ Exception -> 0x00bc }
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r0 = r17;
        r14 = r0.limit;	 Catch:{ Exception -> 0x00bc }
        r10.<init>(r12, r13, r14);	 Catch:{ Exception -> 0x00bc }
        r12 = "encoding";
        r7 = r10.indexOf(r12);	 Catch:{ Exception -> 0x00bc }
        r12 = -1;
        if (r7 == r12) goto L_0x005c;
    L_0x0177:
        r8 = r7;
    L_0x0178:
        r12 = r10.charAt(r8);	 Catch:{ Exception -> 0x00bc }
        r13 = 34;
        if (r12 == r13) goto L_0x018c;
    L_0x0180:
        r12 = r10.charAt(r8);	 Catch:{ Exception -> 0x00bc }
        r13 = 39;
        if (r12 == r13) goto L_0x018c;
    L_0x0188:
        r7 = r8 + 1;
        r8 = r7;
        goto L_0x0178;
    L_0x018c:
        r7 = r8 + 1;
        r2 = r10.charAt(r8);	 Catch:{ Exception -> 0x00bc }
        r9 = r10.indexOf(r2, r7);	 Catch:{ Exception -> 0x00bc }
        r19 = r10.substring(r7, r9);	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x019c:
        r12 = -65536; // 0xffffffffffff0000 float:NaN double:NaN;
        r12 = r12 & r5;
        r13 = -131072; // 0xfffffffffffe0000 float:NaN double:NaN;
        if (r12 != r13) goto L_0x01c6;
    L_0x01a3:
        r19 = "UTF-16LE";
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r0 = r17;
        r14 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r15 = 3;
        r14 = r14[r15];	 Catch:{ Exception -> 0x00bc }
        r14 = r14 << 8;
        r0 = r17;
        r15 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r16 = 2;
        r15 = r15[r16];	 Catch:{ Exception -> 0x00bc }
        r14 = r14 | r15;
        r14 = (char) r14;	 Catch:{ Exception -> 0x00bc }
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 1;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
    L_0x01c6:
        r12 = r5 & -256;
        r13 = -272908544; // 0xffffffffefbbbf00 float:-1.162092E29 double:NaN;
        if (r12 != r13) goto L_0x005c;
    L_0x01cd:
        r19 = "UTF-8";
        r0 = r17;
        r12 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r13 = 0;
        r0 = r17;
        r14 = r0.buffer;	 Catch:{ Exception -> 0x00bc }
        r15 = 3;
        r14 = r14[r15];	 Catch:{ Exception -> 0x00bc }
        r12[r13] = r14;	 Catch:{ Exception -> 0x00bc }
        r12 = 1;
        r0 = r17;
        r0.limit = r12;	 Catch:{ Exception -> 0x00bc }
        goto L_0x005c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.kxml2.io.KXmlParser.setInput(java.io.InputStream, java.lang.String):void");
    }

    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }

    public boolean getFeature(String feature) {
        if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(feature)) {
            return this.processNsp;
        }
        if (FEATURE_RELAXED.equals(feature)) {
            return this.relaxed;
        }
        if (XmlPullParser.FEATURE_PROCESS_DOCDECL.equals(feature)) {
            return this.processDocDecl;
        }
        return false;
    }

    public String getInputEncoding() {
        return this.encoding;
    }

    public void defineEntityReplacementText(String entity, String value) throws XmlPullParserException {
        if (this.processDocDecl) {
            throw new IllegalStateException("Entity replacement text may not be defined with DOCTYPE processing enabled.");
        } else if (this.reader == null) {
            throw new IllegalStateException("Entity replacement text must be defined after setInput()");
        } else {
            if (this.documentEntities == null) {
                this.documentEntities = new HashMap();
            }
            this.documentEntities.put(entity, value.toCharArray());
        }
    }

    public Object getProperty(String property) {
        if (property.equals(PROPERTY_XMLDECL_VERSION)) {
            return this.version;
        }
        if (property.equals(PROPERTY_XMLDECL_STANDALONE)) {
            return this.standalone;
        }
        if (property.equals(PROPERTY_LOCATION)) {
            return this.location != null ? this.location : this.reader.toString();
        } else {
            return null;
        }
    }

    public String getRootElementName() {
        return this.rootElementName;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public int getNamespaceCount(int depth) {
        if (depth <= this.depth) {
            return this.nspCounts[depth];
        }
        throw new IndexOutOfBoundsException();
    }

    public String getNamespacePrefix(int pos) {
        return this.nspStack[pos * 2];
    }

    public String getNamespaceUri(int pos) {
        return this.nspStack[(pos * 2) + 1];
    }

    public String getNamespace(String prefix) {
        if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
            return NamespaceSupport.XMLNS;
        }
        if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        for (int i = (getNamespaceCount(this.depth) << 1) - 2; i >= 0; i -= 2) {
            if (prefix == null) {
                if (this.nspStack[i] == null) {
                    return this.nspStack[i + 1];
                }
            } else if (prefix.equals(this.nspStack[i])) {
                return this.nspStack[i + 1];
            }
        }
        return null;
    }

    public int getDepth() {
        return this.depth;
    }

    public String getPositionDescription() {
        StringBuilder buf = new StringBuilder(this.type < TYPES.length ? TYPES[this.type] : "unknown");
        buf.append(' ');
        if (this.type == 2 || this.type == 3) {
            if (this.degenerated) {
                buf.append("(empty) ");
            }
            buf.append('<');
            if (this.type == 3) {
                buf.append('/');
            }
            if (this.prefix != null) {
                buf.append("{" + this.namespace + "}" + this.prefix + ":");
            }
            buf.append(this.name);
            int cnt = this.attributeCount * 4;
            for (int i = 0; i < cnt; i += 4) {
                buf.append(' ');
                if (this.attributes[i + 1] != null) {
                    buf.append("{" + this.attributes[i] + "}" + this.attributes[i + 1] + ":");
                }
                buf.append(this.attributes[i + 2] + "='" + this.attributes[i + 3] + "'");
            }
            buf.append('>');
        } else if (this.type != 7) {
            if (this.type != 4) {
                buf.append(getText());
            } else if (this.isWhitespace) {
                buf.append("(whitespace)");
            } else {
                String text = getText();
                if (text.length() > 16) {
                    text = text.substring(0, 16) + "...";
                }
                buf.append(text);
            }
        }
        buf.append("@" + getLineNumber() + ":" + getColumnNumber());
        if (this.location != null) {
            buf.append(" in ");
            buf.append(this.location);
        } else if (this.reader != null) {
            buf.append(" in ");
            buf.append(this.reader.toString());
        }
        return buf.toString();
    }

    public int getLineNumber() {
        int result = this.bufferStartLine;
        for (int i = 0; i < this.position; i++) {
            if (this.buffer[i] == '\n') {
                result++;
            }
        }
        return result + 1;
    }

    public int getColumnNumber() {
        int result = this.bufferStartColumn;
        for (int i = 0; i < this.position; i++) {
            if (this.buffer[i] == '\n') {
                result = 0;
            } else {
                result++;
            }
        }
        return result + 1;
    }

    public boolean isWhitespace() throws XmlPullParserException {
        if (this.type == 4 || this.type == 7 || this.type == 5) {
            return this.isWhitespace;
        }
        throw new XmlPullParserException(ILLEGAL_TYPE, this, null);
    }

    public String getText() {
        if (this.type < 4 || (this.type == 6 && this.unresolved)) {
            return null;
        }
        if (this.text == null) {
            return XmlPullParser.NO_NAMESPACE;
        }
        return this.text;
    }

    public char[] getTextCharacters(int[] poslen) {
        String text = getText();
        if (text == null) {
            poslen[0] = -1;
            poslen[1] = -1;
            return null;
        }
        char[] result = text.toCharArray();
        poslen[0] = 0;
        poslen[1] = result.length;
        return result;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public boolean isEmptyElementTag() throws XmlPullParserException {
        if (this.type == 2) {
            return this.degenerated;
        }
        throw new XmlPullParserException(ILLEGAL_TYPE, this, null);
    }

    public int getAttributeCount() {
        return this.attributeCount;
    }

    public String getAttributeType(int index) {
        return "CDATA";
    }

    public boolean isAttributeDefault(int index) {
        return false;
    }

    public String getAttributeNamespace(int index) {
        if (index < this.attributeCount) {
            return this.attributes[index * 4];
        }
        throw new IndexOutOfBoundsException();
    }

    public String getAttributeName(int index) {
        if (index < this.attributeCount) {
            return this.attributes[(index * 4) + 2];
        }
        throw new IndexOutOfBoundsException();
    }

    public String getAttributePrefix(int index) {
        if (index < this.attributeCount) {
            return this.attributes[(index * 4) + 1];
        }
        throw new IndexOutOfBoundsException();
    }

    public String getAttributeValue(int index) {
        if (index < this.attributeCount) {
            return this.attributes[(index * 4) + 3];
        }
        throw new IndexOutOfBoundsException();
    }

    public String getAttributeValue(String namespace, String name) {
        int i = (this.attributeCount * 4) - 4;
        while (i >= 0) {
            if (this.attributes[i + 2].equals(name) && (namespace == null || this.attributes[i].equals(namespace))) {
                return this.attributes[i + 3];
            }
            i -= 4;
        }
        return null;
    }

    public int getEventType() throws XmlPullParserException {
        return this.type;
    }

    public int nextTag() throws XmlPullParserException, IOException {
        next();
        if (this.type == 4 && this.isWhitespace) {
            next();
        }
        if (this.type == 3 || this.type == 2) {
            return this.type;
        }
        throw new XmlPullParserException("unexpected type", this, null);
    }

    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        if (type != this.type || ((namespace != null && !namespace.equals(getNamespace())) || (name != null && !name.equals(getName())))) {
            throw new XmlPullParserException("expected: " + TYPES[type] + " {" + namespace + "}" + name, this, null);
        }
    }

    public String nextText() throws XmlPullParserException, IOException {
        if (this.type != 2) {
            throw new XmlPullParserException("precondition: START_TAG", this, null);
        }
        String result;
        next();
        if (this.type == 4) {
            result = getText();
            next();
        } else {
            result = XmlPullParser.NO_NAMESPACE;
        }
        if (this.type == 3) {
            return result;
        }
        throw new XmlPullParserException("END_TAG expected", this, null);
    }

    public void setFeature(String feature, boolean value) throws XmlPullParserException {
        if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(feature)) {
            this.processNsp = value;
        } else if (XmlPullParser.FEATURE_PROCESS_DOCDECL.equals(feature)) {
            this.processDocDecl = value;
        } else if (FEATURE_RELAXED.equals(feature)) {
            this.relaxed = value;
        } else {
            throw new XmlPullParserException("unsupported feature: " + feature, this, null);
        }
    }

    public void setProperty(String property, Object value) throws XmlPullParserException {
        if (property.equals(PROPERTY_LOCATION)) {
            this.location = String.valueOf(value);
            return;
        }
        throw new XmlPullParserException("unsupported property: " + property);
    }

    private void pushContentSource(char[] newBuffer) {
        this.nextContentSource = new ContentSource(this.nextContentSource, this.buffer, this.position, this.limit);
        this.buffer = newBuffer;
        this.position = 0;
        this.limit = newBuffer.length;
    }

    private void popContentSource() {
        this.buffer = ContentSource.access$000(this.nextContentSource);
        this.position = ContentSource.access$100(this.nextContentSource);
        this.limit = ContentSource.access$200(this.nextContentSource);
        this.nextContentSource = ContentSource.access$300(this.nextContentSource);
    }
}
