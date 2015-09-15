package org.apache.xalan.processor;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.StringToIntTable;
import org.apache.xpath.compiler.PsuedoNames;
import org.xml.sax.SAXException;

public class XSLTAttributeDef {
    static final int ERROR = 1;
    static final int FATAL = 0;
    static final String S_FOREIGNATTR_SETTER = "setForeignAttr";
    static final int T_AVT = 3;
    static final int T_AVT_QNAME = 18;
    static final int T_CDATA = 1;
    static final int T_CHAR = 6;
    static final int T_ENUM = 11;
    static final int T_ENUM_OR_PQNAME = 16;
    static final int T_EXPR = 5;
    static final int T_NCNAME = 17;
    static final int T_NMTOKEN = 13;
    static final int T_NUMBER = 7;
    static final int T_PATTERN = 4;
    static final int T_PREFIXLIST = 20;
    static final int T_PREFIX_URLLIST = 15;
    static final int T_QNAME = 9;
    static final int T_QNAMES = 10;
    static final int T_QNAMES_RESOLVE_NULL = 19;
    static final int T_SIMPLEPATTERNLIST = 12;
    static final int T_STRINGLIST = 14;
    static final int T_URL = 2;
    static final int T_YESNO = 8;
    static final int WARNING = 2;
    static final XSLTAttributeDef m_foreignAttr;
    private String m_default;
    private StringToIntTable m_enums;
    int m_errorType;
    private String m_name;
    private String m_namespace;
    private boolean m_required;
    String m_setterString;
    private boolean m_supportsAVT;
    private int m_type;

    XSLTAttributeDef(java.lang.String r1, java.lang.String r2, int r3, boolean r4, int r5, java.lang.String r6) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, int, boolean, int, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, int, boolean, int, java.lang.String):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, int, boolean, int, java.lang.String):void");
    }

    XSLTAttributeDef(java.lang.String r1, java.lang.String r2, int r3, boolean r4, boolean r5, int r6) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, int, boolean, boolean, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, int, boolean, boolean, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, int, boolean, boolean, int):void");
    }

    XSLTAttributeDef(java.lang.String r1, java.lang.String r2, boolean r3, boolean r4, boolean r5, int r6, java.lang.String r7, int r8, java.lang.String r9, int r10) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int):void");
    }

    XSLTAttributeDef(java.lang.String r1, java.lang.String r2, boolean r3, boolean r4, boolean r5, int r6, java.lang.String r7, int r8, java.lang.String r9, int r10, java.lang.String r11, int r12) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int, java.lang.String, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int, java.lang.String, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int, java.lang.String, int):void");
    }

    XSLTAttributeDef(java.lang.String r1, java.lang.String r2, boolean r3, boolean r4, boolean r5, int r6, java.lang.String r7, int r8, java.lang.String r9, int r10, java.lang.String r11, int r12, java.lang.String r13, int r14) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int, java.lang.String, int, java.lang.String, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int, java.lang.String, int, java.lang.String, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.<init>(java.lang.String, java.lang.String, boolean, boolean, boolean, int, java.lang.String, int, java.lang.String, int, java.lang.String, int, java.lang.String, int):void");
    }

    private int getEnum(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getEnum(java.lang.String):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getEnum(java.lang.String):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getEnum(java.lang.String):int");
    }

    private java.lang.String[] getEnumNames() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getEnumNames():java.lang.String[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getEnumNames():java.lang.String[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getEnumNames():java.lang.String[]");
    }

    private java.lang.StringBuffer getListOfEnums() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getListOfEnums():java.lang.StringBuffer
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getListOfEnums():java.lang.StringBuffer
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getListOfEnums():java.lang.StringBuffer");
    }

    private java.lang.Class getPrimativeClass(java.lang.Object r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getPrimativeClass(java.lang.Object):java.lang.Class
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getPrimativeClass(java.lang.Object):java.lang.Class
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getPrimativeClass(java.lang.Object):java.lang.Class");
    }

    private void handleError(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.Object[] r3, java.lang.Exception r4) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.handleError(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.Object[], java.lang.Exception):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.handleError(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.Object[], java.lang.Exception):void
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.handleError(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.Object[], java.lang.Exception):void");
    }

    private java.lang.Boolean processYESNO(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processYESNO(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.lang.Boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processYESNO(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.lang.Boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processYESNO(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.lang.Boolean");
    }

    java.lang.String getDefault() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getDefault():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getDefault():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getDefault():java.lang.String");
    }

    int getErrorType() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getErrorType():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getErrorType():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getErrorType():int");
    }

    java.lang.String getName() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getName():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getName():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getName():java.lang.String");
    }

    java.lang.String getNamespace() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getNamespace():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getNamespace():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getNamespace():java.lang.String");
    }

    public java.lang.String getSetterMethodName() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getSetterMethodName():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getSetterMethodName():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getSetterMethodName():java.lang.String");
    }

    int getType() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.getType():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.getType():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.getType():int");
    }

    java.lang.Object processAVT_QNAME(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processAVT_QNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processAVT_QNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processAVT_QNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processCDATA(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processCDATA(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processCDATA(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processCDATA(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processCHAR(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processCHAR(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processCHAR(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processCHAR(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processENUM(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processENUM(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processENUM(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processENUM(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processENUM_OR_PQNAME(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processENUM_OR_PQNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processENUM_OR_PQNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processENUM_OR_PQNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processEXPR(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processEXPR(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processEXPR(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processEXPR(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processNCNAME(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processNCNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processNCNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processNCNAME(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processNMTOKEN(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processNMTOKEN(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processNMTOKEN(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processNMTOKEN(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processNUMBER(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processNUMBER(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processNUMBER(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processNUMBER(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processPATTERN(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processPATTERN(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processPATTERN(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processPATTERN(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    org.apache.xml.utils.StringVector processPREFIX_LIST(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processPREFIX_LIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processPREFIX_LIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processPREFIX_LIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector");
    }

    org.apache.xml.utils.StringVector processPREFIX_URLLIST(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processPREFIX_URLLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processPREFIX_URLLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processPREFIX_URLLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector");
    }

    java.util.Vector processQNAMES(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processQNAMES(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.util.Vector
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processQNAMES(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.util.Vector
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processQNAMES(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.util.Vector");
    }

    final java.util.Vector processQNAMESRNU(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processQNAMESRNU(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.util.Vector
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processQNAMESRNU(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.util.Vector
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processQNAMESRNU(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):java.util.Vector");
    }

    java.util.Vector processSIMPLEPATTERNLIST(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processSIMPLEPATTERNLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.util.Vector
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processSIMPLEPATTERNLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.util.Vector
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processSIMPLEPATTERNLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.util.Vector");
    }

    org.apache.xml.utils.StringVector processSTRINGLIST(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processSTRINGLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processSTRINGLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processSTRINGLIST(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String):org.apache.xml.utils.StringVector");
    }

    java.lang.Object processURL(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processURL(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processURL(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processURL(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    java.lang.Object processValue(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.processValue(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.processValue(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.processValue(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):java.lang.Object");
    }

    boolean setAttrValue(org.apache.xalan.processor.StylesheetHandler r1, java.lang.String r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, org.apache.xalan.templates.ElemTemplateElement r6) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.setAttrValue(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.setAttrValue(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.setAttrValue(org.apache.xalan.processor.StylesheetHandler, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.apache.xalan.templates.ElemTemplateElement):boolean");
    }

    void setDefAttrValue(org.apache.xalan.processor.StylesheetHandler r1, org.apache.xalan.templates.ElemTemplateElement r2) throws org.xml.sax.SAXException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.setDefAttrValue(org.apache.xalan.processor.StylesheetHandler, org.apache.xalan.templates.ElemTemplateElement):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.setDefAttrValue(org.apache.xalan.processor.StylesheetHandler, org.apache.xalan.templates.ElemTemplateElement):void
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.setDefAttrValue(org.apache.xalan.processor.StylesheetHandler, org.apache.xalan.templates.ElemTemplateElement):void");
    }

    void setDefault(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xalan.processor.XSLTAttributeDef.setDefault(java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xalan.processor.XSLTAttributeDef.setDefault(java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xalan.processor.XSLTAttributeDef.setDefault(java.lang.String):void");
    }

    static {
        m_foreignAttr = new XSLTAttributeDef(PsuedoNames.PSEUDONAME_OTHER, PsuedoNames.PSEUDONAME_OTHER, (int) T_CDATA, false, false, (int) WARNING);
    }

    boolean getRequired() {
        return this.m_required;
    }

    boolean getSupportsAVT() {
        return this.m_supportsAVT;
    }

    AVT processAVT(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        try {
            return new AVT(handler, uri, name, rawName, value, owner);
        } catch (TransformerException te) {
            throw new SAXException(te);
        }
    }

    Object processQNAME(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        String str;
        Object[] objArr;
        try {
            return new QName(value, (PrefixResolver) handler, true);
        } catch (IllegalArgumentException ie) {
            str = XSLTErrorResources.INVALID_QNAME;
            objArr = new Object[WARNING];
            objArr[FATAL] = name;
            objArr[T_CDATA] = value;
            handleError(handler, str, objArr, ie);
            return null;
        } catch (RuntimeException re) {
            str = XSLTErrorResources.INVALID_QNAME;
            objArr = new Object[WARNING];
            objArr[FATAL] = name;
            objArr[T_CDATA] = value;
            handleError(handler, str, objArr, re);
            return null;
        }
    }
}
