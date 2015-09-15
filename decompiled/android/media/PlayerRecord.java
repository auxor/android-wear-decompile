package android.media;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

class PlayerRecord implements DeathRecipient {
    private static final boolean DEBUG = false;
    private static final String TAG = "MediaFocusControl";
    public static MediaFocusControl sController;
    private static int sLastRccId;
    private String mCallingPackageName;
    private int mCallingUid;
    private final PendingIntent mMediaIntent;
    public RccPlaybackState mPlaybackState;
    public int mPlaybackStream;
    public int mPlaybackType;
    public int mPlaybackVolume;
    public int mPlaybackVolumeHandling;
    public int mPlaybackVolumeMax;
    private IRemoteControlClient mRcClient;
    private RcClientDeathHandler mRcClientDeathHandler;
    private int mRccId;
    private final ComponentName mReceiverComponent;
    public IRemoteVolumeObserver mRemoteVolumeObs;
    private IBinder mToken;

    private class RcClientDeathHandler implements DeathRecipient {
        private final IBinder mCb;
        private final PendingIntent mMediaIntent;
        final /* synthetic */ PlayerRecord this$0;

        RcClientDeathHandler(android.media.PlayerRecord r1, android.os.IBinder r2, android.app.PendingIntent r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RcClientDeathHandler.<init>(android.media.PlayerRecord, android.os.IBinder, android.app.PendingIntent):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RcClientDeathHandler.<init>(android.media.PlayerRecord, android.os.IBinder, android.app.PendingIntent):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RcClientDeathHandler.<init>(android.media.PlayerRecord, android.os.IBinder, android.app.PendingIntent):void");
        }

        static /* synthetic */ android.os.IBinder access$000(android.media.PlayerRecord.RcClientDeathHandler r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RcClientDeathHandler.access$000(android.media.PlayerRecord$RcClientDeathHandler):android.os.IBinder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RcClientDeathHandler.access$000(android.media.PlayerRecord$RcClientDeathHandler):android.os.IBinder
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RcClientDeathHandler.access$000(android.media.PlayerRecord$RcClientDeathHandler):android.os.IBinder");
        }

        public void binderDied() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RcClientDeathHandler.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RcClientDeathHandler.binderDied():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RcClientDeathHandler.binderDied():void");
        }

        public android.os.IBinder getBinder() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RcClientDeathHandler.getBinder():android.os.IBinder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RcClientDeathHandler.getBinder():android.os.IBinder
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RcClientDeathHandler.getBinder():android.os.IBinder");
        }
    }

    protected static class RccPlaybackState {
        public long mPositionMs;
        public float mSpeed;
        public int mState;

        public RccPlaybackState(int r1, long r2, float r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RccPlaybackState.<init>(int, long, float):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RccPlaybackState.<init>(int, long, float):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RccPlaybackState.<init>(int, long, float):void");
        }

        private java.lang.String posToString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RccPlaybackState.posToString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RccPlaybackState.posToString():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RccPlaybackState.posToString():java.lang.String");
        }

        private java.lang.String stateToString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RccPlaybackState.stateToString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RccPlaybackState.stateToString():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RccPlaybackState.stateToString():java.lang.String");
        }

        public void reset() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RccPlaybackState.reset():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RccPlaybackState.reset():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RccPlaybackState.reset():void");
        }

        public java.lang.String toString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.PlayerRecord.RccPlaybackState.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.PlayerRecord.RccPlaybackState.toString():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.PlayerRecord.RccPlaybackState.toString():java.lang.String");
        }
    }

    protected static class RemotePlaybackState {
        int mRccId;
        int mVolume;
        int mVolumeHandling;
        int mVolumeMax;

        protected RemotePlaybackState(int id, int vol, int volMax) {
            this.mRccId = id;
            this.mVolume = vol;
            this.mVolumeMax = volMax;
            this.mVolumeHandling = 1;
        }
    }

    static {
        sLastRccId = 0;
    }

    void dump(PrintWriter pw, boolean registrationInfo) {
        if (registrationInfo) {
            pw.println("  pi: " + this.mMediaIntent + " -- pack: " + this.mCallingPackageName + "  -- ercvr: " + this.mReceiverComponent + "  -- client: " + this.mRcClient + "  -- uid: " + this.mCallingUid + "  -- type: " + this.mPlaybackType + "  state: " + this.mPlaybackState);
        } else {
            pw.println("  uid: " + this.mCallingUid + "  -- id: " + this.mRccId + "  -- type: " + this.mPlaybackType + "  -- state: " + this.mPlaybackState + "  -- vol handling: " + this.mPlaybackVolumeHandling + "  -- vol: " + this.mPlaybackVolume + "  -- volMax: " + this.mPlaybackVolumeMax + "  -- volObs: " + this.mRemoteVolumeObs);
        }
    }

    protected static void setMediaFocusControl(MediaFocusControl mfc) {
        sController = mfc;
    }

    protected PlayerRecord(PendingIntent mediaIntent, ComponentName eventReceiver, IBinder token) {
        this.mRccId = -1;
        this.mMediaIntent = mediaIntent;
        this.mReceiverComponent = eventReceiver;
        this.mToken = token;
        this.mCallingUid = -1;
        this.mRcClient = null;
        int i = sLastRccId + 1;
        sLastRccId = i;
        this.mRccId = i;
        this.mPlaybackState = new RccPlaybackState(1, -1, RemoteControlClient.PLAYBACK_SPEED_1X);
        resetPlaybackInfo();
        if (this.mToken != null) {
            try {
                this.mToken.linkToDeath(this, 0);
            } catch (RemoteException e) {
                sController.unregisterMediaButtonIntentAsync(this.mMediaIntent);
            }
        }
    }

    protected int getRccId() {
        return this.mRccId;
    }

    protected IRemoteControlClient getRcc() {
        return this.mRcClient;
    }

    protected ComponentName getMediaButtonReceiver() {
        return this.mReceiverComponent;
    }

    protected PendingIntent getMediaButtonIntent() {
        return this.mMediaIntent;
    }

    protected boolean hasMatchingMediaButtonIntent(PendingIntent pi) {
        if (this.mToken != null) {
            return this.mMediaIntent.equals(pi);
        }
        if (this.mReceiverComponent != null) {
            return this.mReceiverComponent.equals(pi.getIntent().getComponent());
        }
        return DEBUG;
    }

    protected boolean isPlaybackActive() {
        return MediaFocusControl.isPlaystateActive(this.mPlaybackState.mState);
    }

    protected void resetControllerInfoForRcc(IRemoteControlClient rcClient, String callingPackageName, int uid) {
        if (this.mRcClientDeathHandler != null) {
            unlinkToRcClientDeath();
        }
        this.mRcClient = rcClient;
        this.mCallingPackageName = callingPackageName;
        this.mCallingUid = uid;
        if (rcClient == null) {
            resetPlaybackInfo();
            return;
        }
        IBinder b = this.mRcClient.asBinder();
        RcClientDeathHandler rcdh = new RcClientDeathHandler(this, b, this.mMediaIntent);
        try {
            b.linkToDeath(rcdh, 0);
        } catch (RemoteException e) {
            Log.w(TAG, "registerRemoteControlClient() has a dead client " + b);
            this.mRcClient = null;
        }
        this.mRcClientDeathHandler = rcdh;
    }

    protected void resetControllerInfoForNoRcc() {
        unlinkToRcClientDeath();
        this.mRcClient = null;
        this.mCallingPackageName = null;
    }

    public void resetPlaybackInfo() {
        this.mPlaybackType = 0;
        this.mPlaybackVolume = 15;
        this.mPlaybackVolumeMax = 15;
        this.mPlaybackVolumeHandling = 1;
        this.mPlaybackStream = 3;
        this.mPlaybackState.reset();
        this.mRemoteVolumeObs = null;
    }

    public void unlinkToRcClientDeath() {
        if (this.mRcClientDeathHandler != null && RcClientDeathHandler.access$000(this.mRcClientDeathHandler) != null) {
            try {
                RcClientDeathHandler.access$000(this.mRcClientDeathHandler).unlinkToDeath(this.mRcClientDeathHandler, 0);
                this.mRcClientDeathHandler = null;
            } catch (NoSuchElementException e) {
                Log.e(TAG, "Error in unlinkToRcClientDeath()", e);
            }
        }
    }

    public void destroy() {
        unlinkToRcClientDeath();
        if (this.mToken != null) {
            this.mToken.unlinkToDeath(this, 0);
            this.mToken = null;
        }
    }

    public void binderDied() {
        sController.unregisterMediaButtonIntentAsync(this.mMediaIntent);
    }

    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }
}
