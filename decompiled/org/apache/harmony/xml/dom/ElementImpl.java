package org.apache.harmony.xml.dom;

import java.util.ArrayList;
import java.util.List;
import libcore.util.Objects;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.xmlpull.v1.XmlPullParser;

public class ElementImpl extends InnerNodeImpl implements Element {
    private List<AttrImpl> attributes;
    String localName;
    boolean namespaceAware;
    String namespaceURI;
    String prefix;

    public class ElementAttrNamedNodeMapImpl implements NamedNodeMap {
        final /* synthetic */ ElementImpl this$0;

        public ElementAttrNamedNodeMapImpl(org.apache.harmony.xml.dom.ElementImpl r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.<init>(org.apache.harmony.xml.dom.ElementImpl):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.<init>(org.apache.harmony.xml.dom.ElementImpl):void
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.<init>(org.apache.harmony.xml.dom.ElementImpl):void");
        }

        private int indexOfItem(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.indexOfItem(java.lang.String):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.indexOfItem(java.lang.String):int
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.indexOfItem(java.lang.String):int");
        }

        private int indexOfItemNS(java.lang.String r1, java.lang.String r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.indexOfItemNS(java.lang.String, java.lang.String):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.indexOfItemNS(java.lang.String, java.lang.String):int
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.indexOfItemNS(java.lang.String, java.lang.String):int");
        }

        public int getLength() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getLength():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getLength():int
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getLength():int");
        }

        public org.w3c.dom.Node getNamedItem(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getNamedItem(java.lang.String):org.w3c.dom.Node
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getNamedItem(java.lang.String):org.w3c.dom.Node
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getNamedItem(java.lang.String):org.w3c.dom.Node");
        }

        public org.w3c.dom.Node getNamedItemNS(java.lang.String r1, java.lang.String r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getNamedItemNS(java.lang.String, java.lang.String):org.w3c.dom.Node
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getNamedItemNS(java.lang.String, java.lang.String):org.w3c.dom.Node
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.getNamedItemNS(java.lang.String, java.lang.String):org.w3c.dom.Node");
        }

        public org.w3c.dom.Node item(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.item(int):org.w3c.dom.Node
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.item(int):org.w3c.dom.Node
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.item(int):org.w3c.dom.Node");
        }

        public org.w3c.dom.Node removeNamedItem(java.lang.String r1) throws org.w3c.dom.DOMException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.removeNamedItem(java.lang.String):org.w3c.dom.Node
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.removeNamedItem(java.lang.String):org.w3c.dom.Node
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.removeNamedItem(java.lang.String):org.w3c.dom.Node");
        }

        public org.w3c.dom.Node removeNamedItemNS(java.lang.String r1, java.lang.String r2) throws org.w3c.dom.DOMException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.removeNamedItemNS(java.lang.String, java.lang.String):org.w3c.dom.Node
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.removeNamedItemNS(java.lang.String, java.lang.String):org.w3c.dom.Node
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.removeNamedItemNS(java.lang.String, java.lang.String):org.w3c.dom.Node");
        }

        public org.w3c.dom.Node setNamedItem(org.w3c.dom.Node r1) throws org.w3c.dom.DOMException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.setNamedItem(org.w3c.dom.Node):org.w3c.dom.Node
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.setNamedItem(org.w3c.dom.Node):org.w3c.dom.Node
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.setNamedItem(org.w3c.dom.Node):org.w3c.dom.Node");
        }

        public org.w3c.dom.Node setNamedItemNS(org.w3c.dom.Node r1) throws org.w3c.dom.DOMException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.setNamedItemNS(org.w3c.dom.Node):org.w3c.dom.Node
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.setNamedItemNS(org.w3c.dom.Node):org.w3c.dom.Node
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
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.xml.dom.ElementImpl.ElementAttrNamedNodeMapImpl.setNamedItemNS(org.w3c.dom.Node):org.w3c.dom.Node");
        }
    }

    public /* bridge */ /* synthetic */ Attr m64getAttributeNode(String x0) {
        return getAttributeNode(x0);
    }

    public /* bridge */ /* synthetic */ Attr m65getAttributeNodeNS(String x0, String x1) throws DOMException {
        return getAttributeNodeNS(x0, x1);
    }

    ElementImpl(DocumentImpl document, String namespaceURI, String qualifiedName) {
        super(document);
        this.attributes = new ArrayList();
        NodeImpl.setNameNS(this, namespaceURI, qualifiedName);
    }

    ElementImpl(DocumentImpl document, String name) {
        super(document);
        this.attributes = new ArrayList();
        NodeImpl.setName(this, name);
    }

    private int indexOfAttribute(String name) {
        for (int i = 0; i < this.attributes.size(); i++) {
            if (Objects.equal(name, ((AttrImpl) this.attributes.get(i)).getNodeName())) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfAttributeNS(String namespaceURI, String localName) {
        for (int i = 0; i < this.attributes.size(); i++) {
            AttrImpl attr = (AttrImpl) this.attributes.get(i);
            if (Objects.equal(namespaceURI, attr.getNamespaceURI()) && Objects.equal(localName, attr.getLocalName())) {
                return i;
            }
        }
        return -1;
    }

    public String getAttribute(String name) {
        Attr attr = getAttributeNode(name);
        if (attr == null) {
            return XmlPullParser.NO_NAMESPACE;
        }
        return attr.getValue();
    }

    public String getAttributeNS(String namespaceURI, String localName) {
        Attr attr = getAttributeNodeNS(namespaceURI, localName);
        if (attr == null) {
            return XmlPullParser.NO_NAMESPACE;
        }
        return attr.getValue();
    }

    public AttrImpl getAttributeNode(String name) {
        int i = indexOfAttribute(name);
        if (i == -1) {
            return null;
        }
        return (AttrImpl) this.attributes.get(i);
    }

    public AttrImpl getAttributeNodeNS(String namespaceURI, String localName) {
        int i = indexOfAttributeNS(namespaceURI, localName);
        if (i == -1) {
            return null;
        }
        return (AttrImpl) this.attributes.get(i);
    }

    public NamedNodeMap getAttributes() {
        return new ElementAttrNamedNodeMapImpl(this);
    }

    Element getElementById(String name) {
        for (Attr attr : this.attributes) {
            if (attr.isId() && name.equals(attr.getValue())) {
                return this;
            }
        }
        if (name.equals(getAttribute("id"))) {
            return this;
        }
        for (NodeImpl node : this.children) {
            if (node.getNodeType() == (short) 1) {
                Element element = ((ElementImpl) node).getElementById(name);
                if (element != null) {
                    return element;
                }
            }
        }
        return null;
    }

    public NodeList getElementsByTagName(String name) {
        NodeListImpl result = new NodeListImpl();
        getElementsByTagName(result, name);
        return result;
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        NodeListImpl result = new NodeListImpl();
        getElementsByTagNameNS(result, namespaceURI, localName);
        return result;
    }

    public String getLocalName() {
        return this.namespaceAware ? this.localName : null;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public String getNodeName() {
        return getTagName();
    }

    public short getNodeType() {
        return (short) 1;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getTagName() {
        return this.prefix != null ? this.prefix + ":" + this.localName : this.localName;
    }

    public boolean hasAttribute(String name) {
        return indexOfAttribute(name) != -1;
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return indexOfAttributeNS(namespaceURI, localName) != -1;
    }

    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    public void removeAttribute(String name) throws DOMException {
        int i = indexOfAttribute(name);
        if (i != -1) {
            this.attributes.remove(i);
        }
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        int i = indexOfAttributeNS(namespaceURI, localName);
        if (i != -1) {
            this.attributes.remove(i);
        }
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        Object oldAttrImpl = (AttrImpl) oldAttr;
        if (oldAttrImpl.getOwnerElement() != this) {
            throw new DOMException((short) 8, null);
        }
        this.attributes.remove(oldAttrImpl);
        oldAttrImpl.ownerElement = null;
        return oldAttrImpl;
    }

    public void setAttribute(String name, String value) throws DOMException {
        Attr attr = getAttributeNode(name);
        if (attr == null) {
            attr = this.document.createAttribute(name);
            setAttributeNode(attr);
        }
        attr.setValue(value);
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        Attr attr = getAttributeNodeNS(namespaceURI, qualifiedName);
        if (attr == null) {
            attr = this.document.createAttributeNS(namespaceURI, qualifiedName);
            setAttributeNodeNS(attr);
        }
        attr.setValue(value);
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        AttrImpl newAttrImpl = (AttrImpl) newAttr;
        if (newAttrImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        } else if (newAttrImpl.getOwnerElement() != null) {
            throw new DOMException((short) 10, null);
        } else {
            AttrImpl oldAttrImpl = null;
            int i = indexOfAttribute(newAttr.getName());
            if (i != -1) {
                oldAttrImpl = (AttrImpl) this.attributes.get(i);
                this.attributes.remove(i);
            }
            this.attributes.add(newAttrImpl);
            newAttrImpl.ownerElement = this;
            return oldAttrImpl;
        }
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        AttrImpl newAttrImpl = (AttrImpl) newAttr;
        if (newAttrImpl.document != this.document) {
            throw new DOMException((short) 4, null);
        } else if (newAttrImpl.getOwnerElement() != null) {
            throw new DOMException((short) 10, null);
        } else {
            AttrImpl oldAttrImpl = null;
            int i = indexOfAttributeNS(newAttr.getNamespaceURI(), newAttr.getLocalName());
            if (i != -1) {
                oldAttrImpl = (AttrImpl) this.attributes.get(i);
                this.attributes.remove(i);
            }
            this.attributes.add(newAttrImpl);
            newAttrImpl.ownerElement = this;
            return oldAttrImpl;
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = NodeImpl.validatePrefix(prefix, this.namespaceAware, this.namespaceURI);
    }

    public TypeInfo getSchemaTypeInfo() {
        return NULL_TYPE_INFO;
    }

    public void setIdAttribute(String name, boolean isId) throws DOMException {
        AttrImpl attr = getAttributeNode(name);
        if (attr == null) {
            throw new DOMException((short) 8, "No such attribute: " + name);
        }
        attr.isId = isId;
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        AttrImpl attr = getAttributeNodeNS(namespaceURI, localName);
        if (attr == null) {
            throw new DOMException((short) 8, "No such attribute: " + namespaceURI + " " + localName);
        }
        attr.isId = isId;
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        ((AttrImpl) idAttr).isId = isId;
    }
}
