package gov.nist.javax.sip.header;

import gov.nist.core.GenericObject;
import gov.nist.core.GenericObjectList;

public class SIPObjectList extends GenericObjectList {
    private static final long serialVersionUID = -3015154738977508905L;

    public void mergeObjects(gov.nist.core.GenericObjectList r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.header.SIPObjectList.mergeObjects(gov.nist.core.GenericObjectList):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.header.SIPObjectList.mergeObjects(gov.nist.core.GenericObjectList):void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.header.SIPObjectList.mergeObjects(gov.nist.core.GenericObjectList):void");
    }

    public SIPObjectList(String lname) {
        super(lname);
    }

    public void concatenate(SIPObjectList otherList) {
        super.concatenate(otherList);
    }

    public void concatenate(SIPObjectList otherList, boolean topFlag) {
        super.concatenate(otherList, topFlag);
    }

    public GenericObject first() {
        return (SIPObject) super.first();
    }

    public GenericObject next() {
        return (SIPObject) super.next();
    }

    public String debugDump(int indent) {
        return super.debugDump(indent);
    }
}
