package android.net.wifi.p2p.nsd;

public class WifiP2pUpnpServiceRequest extends WifiP2pServiceRequest {
    public static android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest newInstance(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest.newInstance(java.lang.String):android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest.newInstance(java.lang.String):android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest.newInstance(java.lang.String):android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest");
    }

    protected WifiP2pUpnpServiceRequest(String query) {
        super(2, query);
    }

    protected WifiP2pUpnpServiceRequest() {
        super(2, null);
    }

    public static WifiP2pUpnpServiceRequest newInstance() {
        return new WifiP2pUpnpServiceRequest();
    }
}
