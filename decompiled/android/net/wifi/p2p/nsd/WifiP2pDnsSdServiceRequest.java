package android.net.wifi.p2p.nsd;

public class WifiP2pDnsSdServiceRequest extends WifiP2pServiceRequest {
    public static android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest newInstance(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest.newInstance(java.lang.String):android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest.newInstance(java.lang.String):android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest.newInstance(java.lang.String):android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest");
    }

    public static android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest newInstance(java.lang.String r1, java.lang.String r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest.newInstance(java.lang.String, java.lang.String):android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest.newInstance(java.lang.String, java.lang.String):android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest.newInstance(java.lang.String, java.lang.String):android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest");
    }

    private WifiP2pDnsSdServiceRequest(String query) {
        super(1, query);
    }

    private WifiP2pDnsSdServiceRequest() {
        super(1, null);
    }

    private WifiP2pDnsSdServiceRequest(String dnsQuery, int dnsType, int version) {
        super(1, WifiP2pDnsSdServiceInfo.createRequest(dnsQuery, dnsType, version));
    }

    public static WifiP2pDnsSdServiceRequest newInstance() {
        return new WifiP2pDnsSdServiceRequest();
    }
}
