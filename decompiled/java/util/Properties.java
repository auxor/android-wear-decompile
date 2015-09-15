package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import libcore.icu.ICU;
import org.apache.harmony.security.asn1.ASN1UTCTime;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xmlpull.v1.XmlPullParser;

public class Properties extends Hashtable<Object, Object> {
    private static final int CONTINUE = 3;
    private static final int IGNORE = 5;
    private static final int KEY_DONE = 4;
    private static final int NONE = 0;
    private static final String PROP_DTD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>    <!ELEMENT properties (comment?, entry*) >    <!ATTLIST properties version CDATA #FIXED \"1.0\" >    <!ELEMENT comment (#PCDATA) >    <!ELEMENT entry (#PCDATA) >    <!ATTLIST entry key CDATA #REQUIRED >";
    private static final String PROP_DTD_NAME = "http://java.sun.com/dtd/properties.dtd";
    private static final int SLASH = 1;
    private static final int UNICODE = 2;
    private static final long serialVersionUID = 4112578634029874840L;
    private transient DocumentBuilder builder;
    protected Properties defaults;

    /* renamed from: java.util.Properties.1 */
    class AnonymousClass1 implements ErrorHandler {
        final /* synthetic */ Properties this$0;

        AnonymousClass1(java.util.Properties r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Properties.1.<init>(java.util.Properties):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Properties.1.<init>(java.util.Properties):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Properties.1.<init>(java.util.Properties):void");
        }

        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }

        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }
    }

    /* renamed from: java.util.Properties.2 */
    class AnonymousClass2 implements EntityResolver {
        final /* synthetic */ Properties this$0;

        AnonymousClass2(java.util.Properties r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Properties.2.<init>(java.util.Properties):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Properties.2.<init>(java.util.Properties):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Properties.2.<init>(java.util.Properties):void");
        }

        public org.xml.sax.InputSource resolveEntity(java.lang.String r1, java.lang.String r2) throws org.xml.sax.SAXException, java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Properties.2.resolveEntity(java.lang.String, java.lang.String):org.xml.sax.InputSource
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Properties.2.resolveEntity(java.lang.String, java.lang.String):org.xml.sax.InputSource
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Properties.2.resolveEntity(java.lang.String, java.lang.String):org.xml.sax.InputSource");
        }
    }

    public Properties() {
        this.builder = null;
    }

    public Properties(Properties properties) {
        this.builder = null;
        this.defaults = properties;
    }

    private void dumpString(StringBuilder buffer, String string, boolean key) {
        int i = NONE;
        if (!key && NONE < string.length() && string.charAt(NONE) == ' ') {
            buffer.append("\\ ");
            i = NONE + SLASH;
        }
        while (i < string.length()) {
            char ch = string.charAt(i);
            switch (ch) {
                case XmlPullParser.COMMENT /*9*/:
                    buffer.append("\\t");
                    break;
                case XmlPullParser.DOCDECL /*10*/:
                    buffer.append("\\n");
                    break;
                case ICU.U_ILLEGAL_CHAR_FOUND /*12*/:
                    buffer.append("\\f");
                    break;
                case ASN1UTCTime.UTC_HMS /*13*/:
                    buffer.append("\\r");
                    break;
                default:
                    if ("\\#!=:".indexOf((int) ch) >= 0 || (key && ch == ' ')) {
                        buffer.append('\\');
                    }
                    if (ch >= ' ' && ch <= '~') {
                        buffer.append(ch);
                        break;
                    }
                    String hex = Integer.toHexString(ch);
                    buffer.append("\\u");
                    for (int j = NONE; j < 4 - hex.length(); j += SLASH) {
                        buffer.append("0");
                    }
                    buffer.append(hex);
                    break;
                    break;
            }
            i += SLASH;
        }
    }

    public String getProperty(String name) {
        Object result = super.get(name);
        String property = result instanceof String ? (String) result : null;
        if (property != null || this.defaults == null) {
            return property;
        }
        return this.defaults.getProperty(name);
    }

    public String getProperty(String name, String defaultValue) {
        Object result = super.get(name);
        String property = result instanceof String ? (String) result : null;
        if (property == null && this.defaults != null) {
            property = this.defaults.getProperty(name);
        }
        if (property == null) {
            return defaultValue;
        }
        return property;
    }

    public void list(PrintStream out) {
        listToAppendable(out);
    }

    public void list(PrintWriter out) {
        listToAppendable(out);
    }

    private void listToAppendable(Appendable out) {
        if (out == null) {
            try {
                throw new NullPointerException("out == null");
            } catch (Object ex) {
                throw new AssertionError(ex);
            }
        }
        StringBuilder sb = new StringBuilder(80);
        Enumeration<?> keys = propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            sb.append(key);
            sb.append('=');
            String property = (String) super.get(key);
            Properties def = this.defaults;
            while (property == null) {
                property = (String) def.get(key);
                def = def.defaults;
            }
            if (property.length() > 40) {
                sb.append(property.substring(NONE, 37));
                sb.append("...");
            } else {
                sb.append(property);
            }
            sb.append(System.lineSeparator());
            out.append(sb.toString());
            sb.setLength(NONE);
        }
    }

    public synchronized void load(InputStream in) throws IOException {
        if (in == null) {
            throw new NullPointerException("in == null");
        }
        load(new InputStreamReader(in, "ISO-8859-1"));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void load(java.io.Reader r22) throws java.io.IOException {
        /*
        r21 = this;
        monitor-enter(r21);
        if (r22 != 0) goto L_0x000e;
    L_0x0003:
        r19 = new java.lang.NullPointerException;	 Catch:{ all -> 0x000b }
        r20 = "in == null";
        r19.<init>(r20);	 Catch:{ all -> 0x000b }
        throw r19;	 Catch:{ all -> 0x000b }
    L_0x000b:
        r19 = move-exception;
        monitor-exit(r21);
        throw r19;
    L_0x000e:
        r11 = 0;
        r17 = 0;
        r5 = 0;
        r19 = 40;
        r0 = r19;
        r4 = new char[r0];	 Catch:{ all -> 0x000b }
        r14 = 0;
        r10 = -1;
        r7 = 1;
        r3 = new java.io.BufferedReader;	 Catch:{ all -> 0x000b }
        r0 = r22;
        r3.<init>(r0);	 Catch:{ all -> 0x000b }
        r15 = r14;
    L_0x0023:
        r8 = r3.read();	 Catch:{ all -> 0x000b }
        r19 = -1;
        r0 = r19;
        if (r8 != r0) goto L_0x0041;
    L_0x002d:
        r19 = 2;
        r0 = r19;
        if (r11 != r0) goto L_0x0172;
    L_0x0033:
        r19 = 4;
        r0 = r19;
        if (r5 > r0) goto L_0x0172;
    L_0x0039:
        r19 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x000b }
        r20 = "Invalid Unicode sequence: expected format \\uxxxx";
        r19.<init>(r20);	 Catch:{ all -> 0x000b }
        throw r19;	 Catch:{ all -> 0x000b }
    L_0x0041:
        r13 = (char) r8;	 Catch:{ all -> 0x000b }
        r0 = r4.length;	 Catch:{ all -> 0x000b }
        r19 = r0;
        r0 = r19;
        if (r15 != r0) goto L_0x005e;
    L_0x0049:
        r0 = r4.length;	 Catch:{ all -> 0x000b }
        r19 = r0;
        r19 = r19 * 2;
        r0 = r19;
        r12 = new char[r0];	 Catch:{ all -> 0x000b }
        r19 = 0;
        r20 = 0;
        r0 = r19;
        r1 = r20;
        java.lang.System.arraycopy(r4, r0, r12, r1, r15);	 Catch:{ all -> 0x000b }
        r4 = r12;
    L_0x005e:
        r19 = 2;
        r0 = r19;
        if (r11 != r0) goto L_0x009b;
    L_0x0064:
        r19 = 16;
        r0 = r19;
        r6 = java.lang.Character.digit(r13, r0);	 Catch:{ all -> 0x000b }
        if (r6 < 0) goto L_0x008c;
    L_0x006e:
        r19 = r17 << 4;
        r17 = r19 + r6;
        r5 = r5 + 1;
        r19 = 4;
        r0 = r19;
        if (r5 < r0) goto L_0x0023;
    L_0x007a:
        r11 = 0;
        r14 = r15 + 1;
        r0 = r17;
        r0 = (char) r0;	 Catch:{ all -> 0x000b }
        r19 = r0;
        r4[r15] = r19;	 Catch:{ all -> 0x000b }
        r19 = 10;
        r0 = r19;
        if (r13 == r0) goto L_0x009a;
    L_0x008a:
        r15 = r14;
        goto L_0x0023;
    L_0x008c:
        r19 = 4;
        r0 = r19;
        if (r5 > r0) goto L_0x007a;
    L_0x0092:
        r19 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x000b }
        r20 = "Invalid Unicode sequence: illegal character";
        r19.<init>(r20);	 Catch:{ all -> 0x000b }
        throw r19;	 Catch:{ all -> 0x000b }
    L_0x009a:
        r15 = r14;
    L_0x009b:
        r19 = 1;
        r0 = r19;
        if (r11 != r0) goto L_0x00d0;
    L_0x00a1:
        r11 = 0;
        switch(r13) {
            case 10: goto L_0x00b8;
            case 13: goto L_0x00b5;
            case 98: goto L_0x00bb;
            case 102: goto L_0x00be;
            case 110: goto L_0x00c1;
            case 114: goto L_0x00c4;
            case 116: goto L_0x00c7;
            case 117: goto L_0x00ca;
            default: goto L_0x00a5;
        };	 Catch:{ all -> 0x000b }
    L_0x00a5:
        r7 = 0;
        r19 = 4;
        r0 = r19;
        if (r11 != r0) goto L_0x00ae;
    L_0x00ac:
        r10 = r15;
        r11 = 0;
    L_0x00ae:
        r14 = r15 + 1;
        r4[r15] = r13;	 Catch:{ all -> 0x000b }
        r15 = r14;
        goto L_0x0023;
    L_0x00b5:
        r11 = 3;
        goto L_0x0023;
    L_0x00b8:
        r11 = 5;
        goto L_0x0023;
    L_0x00bb:
        r13 = 8;
        goto L_0x00a5;
    L_0x00be:
        r13 = 12;
        goto L_0x00a5;
    L_0x00c1:
        r13 = 10;
        goto L_0x00a5;
    L_0x00c4:
        r13 = 13;
        goto L_0x00a5;
    L_0x00c7:
        r13 = 9;
        goto L_0x00a5;
    L_0x00ca:
        r11 = 2;
        r5 = 0;
        r17 = r5;
        goto L_0x0023;
    L_0x00d0:
        switch(r13) {
            case 10: goto L_0x010e;
            case 13: goto L_0x0117;
            case 33: goto L_0x00f3;
            case 35: goto L_0x00f3;
            case 58: goto L_0x0159;
            case 61: goto L_0x0159;
            case 92: goto L_0x014f;
            default: goto L_0x00d3;
        };	 Catch:{ all -> 0x000b }
    L_0x00d3:
        r19 = java.lang.Character.isWhitespace(r13);	 Catch:{ all -> 0x000b }
        if (r19 == 0) goto L_0x0163;
    L_0x00d9:
        r19 = 3;
        r0 = r19;
        if (r11 != r0) goto L_0x00e0;
    L_0x00df:
        r11 = 5;
    L_0x00e0:
        if (r15 == 0) goto L_0x0023;
    L_0x00e2:
        if (r15 == r10) goto L_0x0023;
    L_0x00e4:
        r19 = 5;
        r0 = r19;
        if (r11 == r0) goto L_0x0023;
    L_0x00ea:
        r19 = -1;
        r0 = r19;
        if (r10 != r0) goto L_0x0163;
    L_0x00f0:
        r11 = 4;
        goto L_0x0023;
    L_0x00f3:
        if (r7 == 0) goto L_0x00d3;
    L_0x00f5:
        r8 = r3.read();	 Catch:{ all -> 0x000b }
        r19 = -1;
        r0 = r19;
        if (r8 == r0) goto L_0x0023;
    L_0x00ff:
        r13 = (char) r8;	 Catch:{ all -> 0x000b }
        r19 = 13;
        r0 = r19;
        if (r13 == r0) goto L_0x0023;
    L_0x0106:
        r19 = 10;
        r0 = r19;
        if (r13 != r0) goto L_0x00f5;
    L_0x010c:
        goto L_0x0023;
    L_0x010e:
        r19 = 3;
        r0 = r19;
        if (r11 != r0) goto L_0x0117;
    L_0x0114:
        r11 = 5;
        goto L_0x0023;
    L_0x0117:
        r11 = 0;
        r7 = 1;
        if (r15 > 0) goto L_0x011f;
    L_0x011b:
        if (r15 != 0) goto L_0x014a;
    L_0x011d:
        if (r10 != 0) goto L_0x014a;
    L_0x011f:
        r19 = -1;
        r0 = r19;
        if (r10 != r0) goto L_0x0126;
    L_0x0125:
        r10 = r15;
    L_0x0126:
        r16 = new java.lang.String;	 Catch:{ all -> 0x000b }
        r19 = 0;
        r0 = r16;
        r1 = r19;
        r0.<init>(r4, r1, r15);	 Catch:{ all -> 0x000b }
        r19 = 0;
        r0 = r16;
        r1 = r19;
        r19 = r0.substring(r1, r10);	 Catch:{ all -> 0x000b }
        r0 = r16;
        r20 = r0.substring(r10);	 Catch:{ all -> 0x000b }
        r0 = r21;
        r1 = r19;
        r2 = r20;
        r0.put(r1, r2);	 Catch:{ all -> 0x000b }
    L_0x014a:
        r10 = -1;
        r14 = 0;
        r15 = r14;
        goto L_0x0023;
    L_0x014f:
        r19 = 4;
        r0 = r19;
        if (r11 != r0) goto L_0x0156;
    L_0x0155:
        r10 = r15;
    L_0x0156:
        r11 = 1;
        goto L_0x0023;
    L_0x0159:
        r19 = -1;
        r0 = r19;
        if (r10 != r0) goto L_0x00d3;
    L_0x015f:
        r11 = 0;
        r10 = r15;
        goto L_0x0023;
    L_0x0163:
        r19 = 5;
        r0 = r19;
        if (r11 == r0) goto L_0x016f;
    L_0x0169:
        r19 = 3;
        r0 = r19;
        if (r11 != r0) goto L_0x00a5;
    L_0x016f:
        r11 = 0;
        goto L_0x00a5;
    L_0x0172:
        r19 = -1;
        r0 = r19;
        if (r10 != r0) goto L_0x017b;
    L_0x0178:
        if (r15 <= 0) goto L_0x017b;
    L_0x017a:
        r10 = r15;
    L_0x017b:
        if (r10 < 0) goto L_0x01bc;
    L_0x017d:
        r16 = new java.lang.String;	 Catch:{ all -> 0x000b }
        r19 = 0;
        r0 = r16;
        r1 = r19;
        r0.<init>(r4, r1, r15);	 Catch:{ all -> 0x000b }
        r19 = 0;
        r0 = r16;
        r1 = r19;
        r9 = r0.substring(r1, r10);	 Catch:{ all -> 0x000b }
        r0 = r16;
        r18 = r0.substring(r10);	 Catch:{ all -> 0x000b }
        r19 = 1;
        r0 = r19;
        if (r11 != r0) goto L_0x01b5;
    L_0x019e:
        r19 = new java.lang.StringBuilder;	 Catch:{ all -> 0x000b }
        r19.<init>();	 Catch:{ all -> 0x000b }
        r0 = r19;
        r1 = r18;
        r19 = r0.append(r1);	 Catch:{ all -> 0x000b }
        r20 = "\u0000";
        r19 = r19.append(r20);	 Catch:{ all -> 0x000b }
        r18 = r19.toString();	 Catch:{ all -> 0x000b }
    L_0x01b5:
        r0 = r21;
        r1 = r18;
        r0.put(r9, r1);	 Catch:{ all -> 0x000b }
    L_0x01bc:
        monitor-exit(r21);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Properties.load(java.io.Reader):void");
    }

    public Enumeration<?> propertyNames() {
        Hashtable<Object, Object> selected = new Hashtable();
        selectProperties(selected, false);
        return selected.keys();
    }

    public Set<String> stringPropertyNames() {
        Hashtable<String, Object> stringProperties = new Hashtable();
        selectProperties(stringProperties, true);
        return Collections.unmodifiableSet(stringProperties.keySet());
    }

    private <K> void selectProperties(Hashtable<K, Object> selectProperties, boolean isStringOnly) {
        if (this.defaults != null) {
            this.defaults.selectProperties(selectProperties, isStringOnly);
        }
        Enumeration<Object> keys = keys();
        while (keys.hasMoreElements()) {
            K key = keys.nextElement();
            if (!isStringOnly || (key instanceof String)) {
                selectProperties.put(key, get(key));
            }
        }
    }

    @Deprecated
    public void save(OutputStream out, String comment) {
        try {
            store(out, comment);
        } catch (IOException e) {
        }
    }

    public Object setProperty(String name, String value) {
        return put(name, value);
    }

    public synchronized void store(OutputStream out, String comment) throws IOException {
        store(new OutputStreamWriter(out, "ISO-8859-1"), comment);
    }

    public synchronized void store(Writer writer, String comment) throws IOException {
        if (comment != null) {
            writer.write("#");
            writer.write(comment);
            writer.write(System.lineSeparator());
        }
        writer.write("#");
        writer.write(new Date().toString());
        writer.write(System.lineSeparator());
        StringBuilder sb = new StringBuilder((int) HttpURLConnection.HTTP_OK);
        for (Entry<Object, Object> entry : entrySet()) {
            dumpString(sb, (String) entry.getKey(), true);
            sb.append('=');
            dumpString(sb, (String) entry.getValue(), false);
            sb.append(System.lineSeparator());
            writer.write(sb.toString());
            sb.setLength(NONE);
        }
        writer.flush();
    }

    public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
        if (in == null) {
            throw new NullPointerException("in == null");
        }
        if (this.builder == null) {
            try {
                this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                this.builder.setErrorHandler(new AnonymousClass1(this));
                this.builder.setEntityResolver(new AnonymousClass2(this));
            } catch (IOException e) {
                throw e;
            } catch (Throwable e2) {
                throw new InvalidPropertiesFormatException(e2);
            } catch (Throwable e22) {
                throw new Error(e22);
            }
        }
        NodeList entries = this.builder.parse(in).getElementsByTagName("entry");
        if (entries != null) {
            int entriesListLength = entries.getLength();
            for (int i = NONE; i < entriesListLength; i += SLASH) {
                Element entry = (Element) entries.item(i);
                put(entry.getAttribute("key"), entry.getTextContent());
            }
        }
    }

    public void storeToXML(OutputStream os, String comment) throws IOException {
        storeToXML(os, comment, "UTF-8");
    }

    public synchronized void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        if (os == null) {
            throw new NullPointerException("os == null");
        } else if (encoding == null) {
            throw new NullPointerException("encoding == null");
        } else {
            String encodingCanonicalName;
            try {
                encodingCanonicalName = Charset.forName(encoding).name();
            } catch (IllegalCharsetNameException e) {
                System.out.println("Warning: encoding name " + encoding + " is illegal, using UTF-8 as default encoding");
                encodingCanonicalName = "UTF-8";
            } catch (UnsupportedCharsetException e2) {
                System.out.println("Warning: encoding " + encoding + " is not supported, using UTF-8 as default encoding");
                encodingCanonicalName = "UTF-8";
            }
            PrintStream printStream = new PrintStream(os, false, encodingCanonicalName);
            printStream.print("<?xml version=\"1.0\" encoding=\"");
            printStream.print(encodingCanonicalName);
            printStream.println("\"?>");
            printStream.print("<!DOCTYPE properties SYSTEM \"");
            printStream.print(PROP_DTD_NAME);
            printStream.println("\">");
            printStream.println("<properties>");
            if (comment != null) {
                printStream.print("<comment>");
                printStream.print(substitutePredefinedEntries(comment));
                printStream.println("</comment>");
            }
            for (Entry<Object, Object> entry : entrySet()) {
                String keyValue = (String) entry.getKey();
                String entryValue = (String) entry.getValue();
                printStream.print("<entry key=\"");
                printStream.print(substitutePredefinedEntries(keyValue));
                printStream.print("\">");
                printStream.print(substitutePredefinedEntries(entryValue));
                printStream.println("</entry>");
            }
            printStream.println("</properties>");
            printStream.flush();
        }
    }

    private String substitutePredefinedEntries(String s) {
        return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
    }
}
