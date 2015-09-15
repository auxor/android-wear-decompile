package android.util;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.StringPart;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import org.apache.harmony.xml.ExpatReader;
import org.kxml2.io.KXmlParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class Xml {
    public static String FEATURE_RELAXED;

    public enum Encoding {
        US_ASCII(StringPart.DEFAULT_CHARSET),
        UTF_8("UTF-8"),
        UTF_16("UTF-16"),
        ISO_8859_1(FilePart.DEFAULT_CHARSET);
        
        final String expatName;

        private Encoding(java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.util.Xml.Encoding.<init>(java.lang.String, int, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.util.Xml.Encoding.<init>(java.lang.String, int, java.lang.String):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.util.Xml.Encoding.<init>(java.lang.String, int, java.lang.String):void");
        }
    }

    static class XmlSerializerFactory {
        static final String TYPE = "org.kxml2.io.KXmlParser,org.kxml2.io.KXmlSerializer";
        static final XmlPullParserFactory instance;

        XmlSerializerFactory() {
        }

        static {
            try {
                instance = XmlPullParserFactory.newInstance(TYPE, null);
            } catch (XmlPullParserException e) {
                throw new AssertionError(e);
            }
        }
    }

    static {
        FEATURE_RELAXED = "http://xmlpull.org/v1/doc/features.html#relaxed";
    }

    public static void parse(String xml, ContentHandler contentHandler) throws SAXException {
        try {
            XMLReader reader = new ExpatReader();
            reader.setContentHandler(contentHandler);
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static void parse(Reader in, ContentHandler contentHandler) throws IOException, SAXException {
        XMLReader reader = new ExpatReader();
        reader.setContentHandler(contentHandler);
        reader.parse(new InputSource(in));
    }

    public static void parse(InputStream in, Encoding encoding, ContentHandler contentHandler) throws IOException, SAXException {
        XMLReader reader = new ExpatReader();
        reader.setContentHandler(contentHandler);
        InputSource source = new InputSource(in);
        source.setEncoding(encoding.expatName);
        reader.parse(source);
    }

    public static XmlPullParser newPullParser() {
        try {
            KXmlParser parser = new KXmlParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-docdecl", true);
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
            return parser;
        } catch (XmlPullParserException e) {
            throw new AssertionError();
        }
    }

    public static XmlSerializer newSerializer() {
        try {
            return XmlSerializerFactory.instance.newSerializer();
        } catch (XmlPullParserException e) {
            throw new AssertionError(e);
        }
    }

    public static Encoding findEncodingByName(String encodingName) throws UnsupportedEncodingException {
        if (encodingName == null) {
            return Encoding.UTF_8;
        }
        for (Encoding encoding : Encoding.values()) {
            if (encoding.expatName.equalsIgnoreCase(encodingName)) {
                return encoding;
            }
        }
        throw new UnsupportedEncodingException(encodingName);
    }

    public static AttributeSet asAttributeSet(XmlPullParser parser) {
        return parser instanceof AttributeSet ? (AttributeSet) parser : new XmlPullAttributes(parser);
    }
}
