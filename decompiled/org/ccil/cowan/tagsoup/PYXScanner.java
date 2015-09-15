package org.ccil.cowan.tagsoup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.http.protocol.HTTP;
import org.xml.sax.SAXException;

public class PYXScanner implements Scanner {
    public void scan(java.io.Reader r1, org.ccil.cowan.tagsoup.ScanHandler r2) throws java.io.IOException, org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.ccil.cowan.tagsoup.PYXScanner.scan(java.io.Reader, org.ccil.cowan.tagsoup.ScanHandler):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.ccil.cowan.tagsoup.PYXScanner.scan(java.io.Reader, org.ccil.cowan.tagsoup.ScanHandler):void
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
        throw new UnsupportedOperationException("Method not decompiled: org.ccil.cowan.tagsoup.PYXScanner.scan(java.io.Reader, org.ccil.cowan.tagsoup.ScanHandler):void");
    }

    public void resetDocumentLocator(String publicid, String systemid) {
    }

    public void startCDATA() {
    }

    public static void main(String[] argv) throws IOException, SAXException {
        new PYXScanner().scan(new InputStreamReader(System.in, HTTP.UTF_8), new PYXWriter(new BufferedWriter(new OutputStreamWriter(System.out, HTTP.UTF_8))));
    }
}
