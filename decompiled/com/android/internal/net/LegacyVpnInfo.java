package com.android.internal.net;

import android.app.PendingIntent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public class LegacyVpnInfo implements Parcelable {
    public static final Creator<LegacyVpnInfo> CREATOR;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_FAILED = 5;
    public static final int STATE_INITIALIZING = 1;
    public static final int STATE_TIMEOUT = 4;
    private static final String TAG = "LegacyVpnInfo";
    public PendingIntent intent;
    public String key;
    public int state;

    /* renamed from: com.android.internal.net.LegacyVpnInfo.2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkInfo$DetailedState;

        static {
            $SwitchMap$android$net$NetworkInfo$DetailedState = new int[DetailedState.values().length];
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[DetailedState.CONNECTING.ordinal()] = LegacyVpnInfo.STATE_INITIALIZING;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[DetailedState.CONNECTED.ordinal()] = LegacyVpnInfo.STATE_CONNECTING;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[DetailedState.DISCONNECTED.ordinal()] = LegacyVpnInfo.STATE_CONNECTED;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$net$NetworkInfo$DetailedState[DetailedState.FAILED.ordinal()] = LegacyVpnInfo.STATE_TIMEOUT;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public LegacyVpnInfo() {
        this.state = -1;
    }

    public int describeContents() {
        return STATE_DISCONNECTED;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.key);
        out.writeInt(this.state);
        out.writeParcelable(this.intent, flags);
    }

    static {
        CREATOR = new Creator<LegacyVpnInfo>() {
            public com.android.internal.net.LegacyVpnInfo createFromParcel(android.os.Parcel r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.net.LegacyVpnInfo.1.createFromParcel(android.os.Parcel):com.android.internal.net.LegacyVpnInfo
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.net.LegacyVpnInfo.1.createFromParcel(android.os.Parcel):com.android.internal.net.LegacyVpnInfo
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.net.LegacyVpnInfo.1.createFromParcel(android.os.Parcel):com.android.internal.net.LegacyVpnInfo");
            }

            public LegacyVpnInfo[] newArray(int size) {
                return new LegacyVpnInfo[size];
            }
        };
    }

    public static int stateFromNetworkInfo(NetworkInfo info) {
        switch (AnonymousClass2.$SwitchMap$android$net$NetworkInfo$DetailedState[info.getDetailedState().ordinal()]) {
            case STATE_INITIALIZING /*1*/:
                return STATE_CONNECTING;
            case STATE_CONNECTING /*2*/:
                return STATE_CONNECTED;
            case STATE_CONNECTED /*3*/:
                return STATE_DISCONNECTED;
            case STATE_TIMEOUT /*4*/:
                return STATE_FAILED;
            default:
                Log.w(TAG, "Unhandled state " + info.getDetailedState() + " ; treating as disconnected");
                return STATE_DISCONNECTED;
        }
    }
}
