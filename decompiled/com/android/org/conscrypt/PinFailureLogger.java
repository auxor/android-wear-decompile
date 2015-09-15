package com.android.org.conscrypt;

import java.security.cert.X509Certificate;
import java.util.List;

public class PinFailureLogger {
    private static final long LOG_INTERVAL_NANOS = 817405952;
    private static long lastLoggedNanos;

    protected static synchronized void writeToLog(java.lang.String r1, boolean r2, boolean r3, java.util.List<java.security.cert.X509Certificate> r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.PinFailureLogger.writeToLog(java.lang.String, boolean, boolean, java.util.List):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.PinFailureLogger.writeToLog(java.lang.String, boolean, boolean, java.util.List):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.PinFailureLogger.writeToLog(java.lang.String, boolean, boolean, java.util.List):void");
    }

    static {
        lastLoggedNanos = 0;
    }

    public static synchronized void log(String cn, boolean chainContainsUserCert, boolean pinIsEnforcing, List<X509Certificate> chain) {
        synchronized (PinFailureLogger.class) {
            if (timeToLog()) {
                writeToLog(cn, chainContainsUserCert, pinIsEnforcing, chain);
                lastLoggedNanos = System.nanoTime();
            }
        }
    }

    protected static boolean timeToLog() {
        return System.nanoTime() - lastLoggedNanos > LOG_INTERVAL_NANOS;
    }
}
