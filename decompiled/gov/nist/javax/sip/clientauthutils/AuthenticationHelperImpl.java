package gov.nist.javax.sip.clientauthutils;

import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.header.Via;
import java.util.Timer;
import javax.sip.header.HeaderFactory;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

public class AuthenticationHelperImpl implements AuthenticationHelper {
    private Object accountManager;
    private CredentialsCache cachedCredentials;
    private HeaderFactory headerFactory;
    private SipStackImpl sipStack;
    Timer timer;

    public AuthenticationHelperImpl(gov.nist.javax.sip.SipStackImpl r1, gov.nist.javax.sip.clientauthutils.AccountManager r2, javax.sip.header.HeaderFactory r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.<init>(gov.nist.javax.sip.SipStackImpl, gov.nist.javax.sip.clientauthutils.AccountManager, javax.sip.header.HeaderFactory):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.<init>(gov.nist.javax.sip.SipStackImpl, gov.nist.javax.sip.clientauthutils.AccountManager, javax.sip.header.HeaderFactory):void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.<init>(gov.nist.javax.sip.SipStackImpl, gov.nist.javax.sip.clientauthutils.AccountManager, javax.sip.header.HeaderFactory):void");
    }

    public AuthenticationHelperImpl(gov.nist.javax.sip.SipStackImpl r1, gov.nist.javax.sip.clientauthutils.SecureAccountManager r2, javax.sip.header.HeaderFactory r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.<init>(gov.nist.javax.sip.SipStackImpl, gov.nist.javax.sip.clientauthutils.SecureAccountManager, javax.sip.header.HeaderFactory):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.<init>(gov.nist.javax.sip.SipStackImpl, gov.nist.javax.sip.clientauthutils.SecureAccountManager, javax.sip.header.HeaderFactory):void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.<init>(gov.nist.javax.sip.SipStackImpl, gov.nist.javax.sip.clientauthutils.SecureAccountManager, javax.sip.header.HeaderFactory):void");
    }

    private javax.sip.header.AuthorizationHeader getAuthorization(java.lang.String r1, java.lang.String r2, java.lang.String r3, javax.sip.header.WWWAuthenticateHeader r4, gov.nist.javax.sip.clientauthutils.UserCredentialHash r5) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.getAuthorization(java.lang.String, java.lang.String, java.lang.String, javax.sip.header.WWWAuthenticateHeader, gov.nist.javax.sip.clientauthutils.UserCredentialHash):javax.sip.header.AuthorizationHeader
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.getAuthorization(java.lang.String, java.lang.String, java.lang.String, javax.sip.header.WWWAuthenticateHeader, gov.nist.javax.sip.clientauthutils.UserCredentialHash):javax.sip.header.AuthorizationHeader
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.getAuthorization(java.lang.String, java.lang.String, java.lang.String, javax.sip.header.WWWAuthenticateHeader, gov.nist.javax.sip.clientauthutils.UserCredentialHash):javax.sip.header.AuthorizationHeader");
    }

    private javax.sip.header.AuthorizationHeader getAuthorization(java.lang.String r1, java.lang.String r2, java.lang.String r3, javax.sip.header.WWWAuthenticateHeader r4, gov.nist.javax.sip.clientauthutils.UserCredentials r5) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.getAuthorization(java.lang.String, java.lang.String, java.lang.String, javax.sip.header.WWWAuthenticateHeader, gov.nist.javax.sip.clientauthutils.UserCredentials):javax.sip.header.AuthorizationHeader
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.getAuthorization(java.lang.String, java.lang.String, java.lang.String, javax.sip.header.WWWAuthenticateHeader, gov.nist.javax.sip.clientauthutils.UserCredentials):javax.sip.header.AuthorizationHeader
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.getAuthorization(java.lang.String, java.lang.String, java.lang.String, javax.sip.header.WWWAuthenticateHeader, gov.nist.javax.sip.clientauthutils.UserCredentials):javax.sip.header.AuthorizationHeader");
    }

    public javax.sip.ClientTransaction handleChallenge(javax.sip.message.Response r1, javax.sip.ClientTransaction r2, javax.sip.SipProvider r3, int r4) throws javax.sip.SipException, java.lang.NullPointerException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.handleChallenge(javax.sip.message.Response, javax.sip.ClientTransaction, javax.sip.SipProvider, int):javax.sip.ClientTransaction
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.handleChallenge(javax.sip.message.Response, javax.sip.ClientTransaction, javax.sip.SipProvider, int):javax.sip.ClientTransaction
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.handleChallenge(javax.sip.message.Response, javax.sip.ClientTransaction, javax.sip.SipProvider, int):javax.sip.ClientTransaction");
    }

    public void removeCachedAuthenticationHeaders(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.removeCachedAuthenticationHeaders(java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.removeCachedAuthenticationHeaders(java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.removeCachedAuthenticationHeaders(java.lang.String):void");
    }

    public void setAuthenticationHeaders(javax.sip.message.Request r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.setAuthenticationHeaders(javax.sip.message.Request):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.setAuthenticationHeaders(javax.sip.message.Request):void
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
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.clientauthutils.AuthenticationHelperImpl.setAuthenticationHeaders(javax.sip.message.Request):void");
    }

    private void removeBranchID(Request request) {
        ((ViaHeader) request.getHeader(ViaHeader.NAME)).removeParameter(Via.BRANCH);
    }
}
