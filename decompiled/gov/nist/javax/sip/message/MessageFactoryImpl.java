package gov.nist.javax.sip.message;

import gov.nist.javax.sip.parser.ParseExceptionListener;
import javax.sip.header.ServerHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.MessageFactory;
import org.apache.http.protocol.HTTP;

public class MessageFactoryImpl implements MessageFactory, MessageFactoryExt {
    private static String defaultContentEncodingCharset;
    private static ServerHeader server;
    private static UserAgentHeader userAgent;
    private boolean strict;
    private boolean testing;

    /* renamed from: gov.nist.javax.sip.message.MessageFactoryImpl.1 */
    class AnonymousClass1 implements ParseExceptionListener {
        final /* synthetic */ MessageFactoryImpl this$0;

        AnonymousClass1(gov.nist.javax.sip.message.MessageFactoryImpl r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.1.<init>(gov.nist.javax.sip.message.MessageFactoryImpl):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.1.<init>(gov.nist.javax.sip.message.MessageFactoryImpl):void
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
            throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.1.<init>(gov.nist.javax.sip.message.MessageFactoryImpl):void");
        }

        public void handleException(java.text.ParseException r1, gov.nist.javax.sip.message.SIPMessage r2, java.lang.Class r3, java.lang.String r4, java.lang.String r5) throws java.text.ParseException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.1.handleException(java.text.ParseException, gov.nist.javax.sip.message.SIPMessage, java.lang.Class, java.lang.String, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.1.handleException(java.text.ParseException, gov.nist.javax.sip.message.SIPMessage, java.lang.Class, java.lang.String, java.lang.String):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.1.handleException(java.text.ParseException, gov.nist.javax.sip.message.SIPMessage, java.lang.Class, java.lang.String, java.lang.String):void");
        }
    }

    public MessageFactoryImpl() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.<init>():void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.<init>():void");
    }

    public gov.nist.javax.sip.message.MultipartMimeContent createMultipartMimeContent(javax.sip.header.ContentTypeHeader r1, java.lang.String[] r2, java.lang.String[] r3, java.lang.String[] r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createMultipartMimeContent(javax.sip.header.ContentTypeHeader, java.lang.String[], java.lang.String[], java.lang.String[]):gov.nist.javax.sip.message.MultipartMimeContent
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createMultipartMimeContent(javax.sip.header.ContentTypeHeader, java.lang.String[], java.lang.String[], java.lang.String[]):gov.nist.javax.sip.message.MultipartMimeContent
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createMultipartMimeContent(javax.sip.header.ContentTypeHeader, java.lang.String[], java.lang.String[], java.lang.String[]):gov.nist.javax.sip.message.MultipartMimeContent");
    }

    public javax.sip.message.Request createRequest(java.lang.String r1) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(java.lang.String):javax.sip.message.Request
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(java.lang.String):javax.sip.message.Request
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(java.lang.String):javax.sip.message.Request");
    }

    public javax.sip.message.Request createRequest(javax.sip.address.URI r1, java.lang.String r2, javax.sip.header.CallIdHeader r3, javax.sip.header.CSeqHeader r4, javax.sip.header.FromHeader r5, javax.sip.header.ToHeader r6, java.util.List r7, javax.sip.header.MaxForwardsHeader r8) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader):javax.sip.message.Request
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader):javax.sip.message.Request
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader):javax.sip.message.Request");
    }

    public javax.sip.message.Request createRequest(javax.sip.address.URI r1, java.lang.String r2, javax.sip.header.CallIdHeader r3, javax.sip.header.CSeqHeader r4, javax.sip.header.FromHeader r5, javax.sip.header.ToHeader r6, java.util.List r7, javax.sip.header.MaxForwardsHeader r8, javax.sip.header.ContentTypeHeader r9, java.lang.Object r10) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Request
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Request
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Request");
    }

    public javax.sip.message.Request createRequest(javax.sip.address.URI r1, java.lang.String r2, javax.sip.header.CallIdHeader r3, javax.sip.header.CSeqHeader r4, javax.sip.header.FromHeader r5, javax.sip.header.ToHeader r6, java.util.List r7, javax.sip.header.MaxForwardsHeader r8, javax.sip.header.ContentTypeHeader r9, byte[] r10) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Request
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Request
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Request");
    }

    public javax.sip.message.Request createRequest(javax.sip.address.URI r1, java.lang.String r2, javax.sip.header.CallIdHeader r3, javax.sip.header.CSeqHeader r4, javax.sip.header.FromHeader r5, javax.sip.header.ToHeader r6, java.util.List r7, javax.sip.header.MaxForwardsHeader r8, byte[] r9, javax.sip.header.ContentTypeHeader r10) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, byte[], javax.sip.header.ContentTypeHeader):javax.sip.message.Request
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, byte[], javax.sip.header.ContentTypeHeader):javax.sip.message.Request
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createRequest(javax.sip.address.URI, java.lang.String, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, byte[], javax.sip.header.ContentTypeHeader):javax.sip.message.Request");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.header.CallIdHeader r2, javax.sip.header.CSeqHeader r3, javax.sip.header.FromHeader r4, javax.sip.header.ToHeader r5, java.util.List r6, javax.sip.header.MaxForwardsHeader r7) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.header.CallIdHeader r2, javax.sip.header.CSeqHeader r3, javax.sip.header.FromHeader r4, javax.sip.header.ToHeader r5, java.util.List r6, javax.sip.header.MaxForwardsHeader r7, java.lang.Object r8, javax.sip.header.ContentTypeHeader r9) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, java.lang.Object, javax.sip.header.ContentTypeHeader):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, java.lang.Object, javax.sip.header.ContentTypeHeader):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, java.lang.Object, javax.sip.header.ContentTypeHeader):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.header.CallIdHeader r2, javax.sip.header.CSeqHeader r3, javax.sip.header.FromHeader r4, javax.sip.header.ToHeader r5, java.util.List r6, javax.sip.header.MaxForwardsHeader r7, javax.sip.header.ContentTypeHeader r8, java.lang.Object r9) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.header.CallIdHeader r2, javax.sip.header.CSeqHeader r3, javax.sip.header.FromHeader r4, javax.sip.header.ToHeader r5, java.util.List r6, javax.sip.header.MaxForwardsHeader r7, javax.sip.header.ContentTypeHeader r8, byte[] r9) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.header.CallIdHeader r2, javax.sip.header.CSeqHeader r3, javax.sip.header.FromHeader r4, javax.sip.header.ToHeader r5, java.util.List r6, javax.sip.header.MaxForwardsHeader r7, byte[] r8, javax.sip.header.ContentTypeHeader r9) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, byte[], javax.sip.header.ContentTypeHeader):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, byte[], javax.sip.header.ContentTypeHeader):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.header.CallIdHeader, javax.sip.header.CSeqHeader, javax.sip.header.FromHeader, javax.sip.header.ToHeader, java.util.List, javax.sip.header.MaxForwardsHeader, byte[], javax.sip.header.ContentTypeHeader):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.message.Request r2) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.message.Request r2, javax.sip.header.ContentTypeHeader r3, java.lang.Object r4) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request, javax.sip.header.ContentTypeHeader, java.lang.Object):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(int r1, javax.sip.message.Request r2, javax.sip.header.ContentTypeHeader r3, byte[] r4) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(int, javax.sip.message.Request, javax.sip.header.ContentTypeHeader, byte[]):javax.sip.message.Response");
    }

    public javax.sip.message.Response createResponse(java.lang.String r1) throws java.text.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(java.lang.String):javax.sip.message.Response
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(java.lang.String):javax.sip.message.Response
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.createResponse(java.lang.String):javax.sip.message.Response");
    }

    public void setStrict(boolean r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.setStrict(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.setStrict(boolean):void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.setStrict(boolean):void");
    }

    public void setTest(boolean r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.message.MessageFactoryImpl.setTest(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.message.MessageFactoryImpl.setTest(boolean):void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.message.MessageFactoryImpl.setTest(boolean):void");
    }

    static {
        defaultContentEncodingCharset = HTTP.UTF_8;
    }

    public void setDefaultUserAgentHeader(UserAgentHeader userAgent) {
        userAgent = userAgent;
    }

    public void setDefaultServerHeader(ServerHeader server) {
        server = server;
    }

    public static UserAgentHeader getDefaultUserAgentHeader() {
        return userAgent;
    }

    public static ServerHeader getDefaultServerHeader() {
        return server;
    }

    public void setDefaultContentEncodingCharset(String charset) throws NullPointerException, IllegalArgumentException {
        if (charset == null) {
            throw new NullPointerException("Null argument!");
        }
        defaultContentEncodingCharset = charset;
    }

    public static String getDefaultContentEncodingCharset() {
        return defaultContentEncodingCharset;
    }
}
