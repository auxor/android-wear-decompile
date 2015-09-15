package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;

@Deprecated
public class DefaultTargetAuthenticationHandler extends AbstractAuthenticationHandler {
    public java.util.Map<java.lang.String, org.apache.http.Header> getChallenges(org.apache.http.HttpResponse r1, org.apache.http.protocol.HttpContext r2) throws org.apache.http.auth.MalformedChallengeException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.http.impl.client.DefaultTargetAuthenticationHandler.getChallenges(org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext):java.util.Map<java.lang.String, org.apache.http.Header>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.http.impl.client.DefaultTargetAuthenticationHandler.getChallenges(org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext):java.util.Map<java.lang.String, org.apache.http.Header>
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.client.DefaultTargetAuthenticationHandler.getChallenges(org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext):java.util.Map<java.lang.String, org.apache.http.Header>");
    }

    public boolean isAuthenticationRequested(HttpResponse response, HttpContext context) {
        if (response != null) {
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED;
        } else {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
    }
}
