package org.apache.xpath.objects;

import org.apache.xpath.XPathContext;

public class XNull extends XNodeSet {
    static final long serialVersionUID = -6841683711458983005L;

    public boolean equals(org.apache.xpath.objects.XObject r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xpath.objects.XNull.equals(org.apache.xpath.objects.XObject):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xpath.objects.XNull.equals(org.apache.xpath.objects.XObject):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xpath.objects.XNull.equals(org.apache.xpath.objects.XObject):boolean");
    }

    public int getType() {
        return -1;
    }

    public String getTypeString() {
        return "#CLASS_NULL";
    }

    public double num() {
        return 0.0d;
    }

    public boolean bool() {
        return false;
    }

    public String str() {
        return SerializerConstants.EMPTYSTRING;
    }

    public int rtf(XPathContext support) {
        return -1;
    }
}
