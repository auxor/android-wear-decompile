package android.location;

import android.content.Context;
import android.location.GpsNavigationMessageEvent.Listener;
import android.location.IGpsNavigationMessageListener.Stub;
import android.os.RemoteException;

class GpsNavigationMessageListenerTransport extends LocalListenerHelper<Listener> {
    private final IGpsNavigationMessageListener mListenerTransport;
    private final ILocationManager mLocationManager;

    private class ListenerTransport extends Stub {
        final /* synthetic */ GpsNavigationMessageListenerTransport this$0;

        /* renamed from: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1 */
        class AnonymousClass1 implements ListenerOperation<Listener> {
            final /* synthetic */ ListenerTransport this$1;
            final /* synthetic */ GpsNavigationMessageEvent val$event;

            AnonymousClass1(android.location.GpsNavigationMessageListenerTransport.ListenerTransport r1, android.location.GpsNavigationMessageEvent r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.<init>(android.location.GpsNavigationMessageListenerTransport$ListenerTransport, android.location.GpsNavigationMessageEvent):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.<init>(android.location.GpsNavigationMessageListenerTransport$ListenerTransport, android.location.GpsNavigationMessageEvent):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.<init>(android.location.GpsNavigationMessageListenerTransport$ListenerTransport, android.location.GpsNavigationMessageEvent):void");
            }

            public void execute(android.location.GpsNavigationMessageEvent.Listener r1) throws android.os.RemoteException {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.execute(android.location.GpsNavigationMessageEvent$Listener):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.execute(android.location.GpsNavigationMessageEvent$Listener):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.execute(android.location.GpsNavigationMessageEvent$Listener):void");
            }

            public /* bridge */ /* synthetic */ void execute(java.lang.Object r1) throws android.os.RemoteException {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.execute(java.lang.Object):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.execute(java.lang.Object):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.1.execute(java.lang.Object):void");
            }
        }

        /* renamed from: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2 */
        class AnonymousClass2 implements ListenerOperation<Listener> {
            final /* synthetic */ ListenerTransport this$1;
            final /* synthetic */ int val$status;

            AnonymousClass2(android.location.GpsNavigationMessageListenerTransport.ListenerTransport r1, int r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.<init>(android.location.GpsNavigationMessageListenerTransport$ListenerTransport, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.<init>(android.location.GpsNavigationMessageListenerTransport$ListenerTransport, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.<init>(android.location.GpsNavigationMessageListenerTransport$ListenerTransport, int):void");
            }

            public void execute(android.location.GpsNavigationMessageEvent.Listener r1) throws android.os.RemoteException {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.execute(android.location.GpsNavigationMessageEvent$Listener):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.execute(android.location.GpsNavigationMessageEvent$Listener):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.execute(android.location.GpsNavigationMessageEvent$Listener):void");
            }

            public /* bridge */ /* synthetic */ void execute(java.lang.Object r1) throws android.os.RemoteException {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.execute(java.lang.Object):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.execute(java.lang.Object):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.location.GpsNavigationMessageListenerTransport.ListenerTransport.2.execute(java.lang.Object):void");
            }
        }

        private ListenerTransport(GpsNavigationMessageListenerTransport gpsNavigationMessageListenerTransport) {
            this.this$0 = gpsNavigationMessageListenerTransport;
        }

        /* synthetic */ ListenerTransport(GpsNavigationMessageListenerTransport x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void onGpsNavigationMessageReceived(GpsNavigationMessageEvent event) {
            this.this$0.foreach(new AnonymousClass1(this, event));
        }

        public void onStatusChanged(int status) {
            this.this$0.foreach(new AnonymousClass2(this, status));
        }
    }

    public GpsNavigationMessageListenerTransport(Context context, ILocationManager locationManager) {
        super(context, "GpsNavigationMessageListenerTransport");
        this.mListenerTransport = new ListenerTransport();
        this.mLocationManager = locationManager;
    }

    protected boolean registerWithServer() throws RemoteException {
        return this.mLocationManager.addGpsNavigationMessageListener(this.mListenerTransport, getContext().getPackageName());
    }

    protected void unregisterFromServer() throws RemoteException {
        this.mLocationManager.removeGpsNavigationMessageListener(this.mListenerTransport);
    }
}
