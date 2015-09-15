package org.apache.xalan.transformer;

import java.text.Collator;
import java.util.Locale;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;

class NodeSortKey {
    boolean m_caseOrderUpper;
    Collator m_col;
    boolean m_descending;
    Locale m_locale;
    PrefixResolver m_namespaceContext;
    TransformerImpl m_processor;
    XPath m_selectPat;
    boolean m_treatAsNumbers;

    NodeSortKey(org.apache.xalan.transformer.TransformerImpl r1, org.apache.xpath.XPath r2, boolean r3, boolean r4, java.lang.String r5, boolean r6, org.apache.xml.utils.PrefixResolver r7) throws javax.xml.transform.TransformerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.transformer.NodeSortKey.<init>(org.apache.xalan.transformer.TransformerImpl, org.apache.xpath.XPath, boolean, boolean, java.lang.String, boolean, org.apache.xml.utils.PrefixResolver):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.transformer.NodeSortKey.<init>(org.apache.xalan.transformer.TransformerImpl, org.apache.xpath.XPath, boolean, boolean, java.lang.String, boolean, org.apache.xml.utils.PrefixResolver):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.transformer.NodeSortKey.<init>(org.apache.xalan.transformer.TransformerImpl, org.apache.xpath.XPath, boolean, boolean, java.lang.String, boolean, org.apache.xml.utils.PrefixResolver):void");
    }
}
