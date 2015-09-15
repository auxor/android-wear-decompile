package android.media;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.content.PackageHelper;
import com.android.internal.os.PowerProfile;
import com.android.internal.widget.LockPatternUtils;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

class FocusRequester {
    private static final boolean DEBUG = false;
    private static final String TAG = "MediaFocusControl";
    private final AudioAttributes mAttributes;
    private final int mCallingUid;
    private final String mClientId;
    private AudioFocusDeathHandler mDeathHandler;
    private final MediaFocusControl mFocusController;
    private final IAudioFocusDispatcher mFocusDispatcher;
    private final int mFocusGainRequest;
    private int mFocusLossReceived;
    private final int mGrantFlags;
    private final String mPackageName;
    private final IBinder mSourceRef;

    FocusRequester(AudioAttributes aa, int focusRequest, int grantFlags, IAudioFocusDispatcher afl, IBinder source, String id, AudioFocusDeathHandler hdlr, String pn, int uid, MediaFocusControl ctlr) {
        this.mAttributes = aa;
        this.mFocusDispatcher = afl;
        this.mSourceRef = source;
        this.mClientId = id;
        this.mDeathHandler = hdlr;
        this.mPackageName = pn;
        this.mCallingUid = uid;
        this.mFocusGainRequest = focusRequest;
        this.mGrantFlags = grantFlags;
        this.mFocusLossReceived = 0;
        this.mFocusController = ctlr;
    }

    boolean hasSameClient(String otherClient) {
        try {
            return this.mClientId.compareTo(otherClient) == 0 ? true : DEBUG;
        } catch (NullPointerException e) {
            return DEBUG;
        }
    }

    boolean isLockedFocusOwner() {
        return (this.mGrantFlags & 4) != 0 ? true : DEBUG;
    }

    boolean hasSameBinder(IBinder ib) {
        return (this.mSourceRef == null || !this.mSourceRef.equals(ib)) ? DEBUG : true;
    }

    boolean hasSamePackage(String pack) {
        try {
            return this.mPackageName.compareTo(pack) == 0 ? true : DEBUG;
        } catch (NullPointerException e) {
            return DEBUG;
        }
    }

    boolean hasSameUid(int uid) {
        return this.mCallingUid == uid ? true : DEBUG;
    }

    String getClientId() {
        return this.mClientId;
    }

    int getGainRequest() {
        return this.mFocusGainRequest;
    }

    int getGrantFlags() {
        return this.mGrantFlags;
    }

    AudioAttributes getAudioAttributes() {
        return this.mAttributes;
    }

    private static String focusChangeToString(int focus) {
        switch (focus) {
            case PackageHelper.RECOMMEND_FAILED_INVALID_LOCATION /*-3*/:
                return "LOSS_TRANSIENT_CAN_DUCK";
            case LockPatternUtils.ID_DEFAULT_STATUS_WIDGET /*-2*/:
                return "LOSS_TRANSIENT";
            case GL11ExtensionPack.GL_STR /*-1*/:
                return "LOSS";
            case GL10.GL_POINTS /*0*/:
                return PowerProfile.POWER_NONE;
            case GL10.GL_TRUE /*1*/:
                return "GAIN";
            case GL10.GL_LINE_LOOP /*2*/:
                return "GAIN_TRANSIENT";
            case GL10.GL_LINE_STRIP /*3*/:
                return "GAIN_TRANSIENT_MAY_DUCK";
            case GL10.GL_TRIANGLES /*4*/:
                return "GAIN_TRANSIENT_EXCLUSIVE";
            default:
                return "[invalid focus change" + focus + "]";
        }
    }

    private String focusGainToString() {
        return focusChangeToString(this.mFocusGainRequest);
    }

    private String focusLossToString() {
        return focusChangeToString(this.mFocusLossReceived);
    }

    private static String flagsToString(int flags) {
        String msg = new String();
        if ((flags & 1) != 0) {
            msg = msg + "DELAY_OK";
        }
        if ((flags & 4) != 0) {
            if (!msg.isEmpty()) {
                msg = msg + "|";
            }
            msg = msg + "LOCK";
        }
        if ((flags & 2) == 0) {
            return msg;
        }
        if (!msg.isEmpty()) {
            msg = msg + "|";
        }
        return msg + "PAUSES_ON_DUCKABLE_LOSS";
    }

    void dump(PrintWriter pw) {
        pw.println("  source:" + this.mSourceRef + " -- pack: " + this.mPackageName + " -- client: " + this.mClientId + " -- gain: " + focusGainToString() + " -- flags: " + flagsToString(this.mGrantFlags) + " -- loss: " + focusLossToString() + " -- uid: " + this.mCallingUid + " -- attr: " + this.mAttributes);
    }

    void release() {
        try {
            if (this.mSourceRef != null && this.mDeathHandler != null) {
                this.mSourceRef.unlinkToDeath(this.mDeathHandler, 0);
                this.mDeathHandler = null;
            }
        } catch (NoSuchElementException e) {
            Log.e(TAG, "FocusRequester.release() hit ", e);
        }
    }

    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int focusLossForGainRequest(int r4) {
        /*
        r3 = this;
        r1 = -2;
        r0 = -1;
        switch(r4) {
            case 1: goto L_0x001f;
            case 2: goto L_0x0024;
            case 3: goto L_0x0029;
            case 4: goto L_0x0024;
            default: goto L_0x0005;
        };
    L_0x0005:
        r0 = "MediaFocusControl";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "focusLossForGainRequest() for invalid focus request ";
        r1 = r1.append(r2);
        r1 = r1.append(r4);
        r1 = r1.toString();
        android.util.Log.e(r0, r1);
        r0 = 0;
    L_0x001e:
        return r0;
    L_0x001f:
        r2 = r3.mFocusLossReceived;
        switch(r2) {
            case -3: goto L_0x001e;
            case -2: goto L_0x001e;
            case -1: goto L_0x001e;
            case 0: goto L_0x001e;
            default: goto L_0x0024;
        };
    L_0x0024:
        r2 = r3.mFocusLossReceived;
        switch(r2) {
            case -3: goto L_0x0031;
            case -2: goto L_0x0031;
            case -1: goto L_0x001e;
            case 0: goto L_0x0031;
            default: goto L_0x0029;
        };
    L_0x0029:
        r2 = r3.mFocusLossReceived;
        switch(r2) {
            case -3: goto L_0x002f;
            case -2: goto L_0x0033;
            case -1: goto L_0x001e;
            case 0: goto L_0x002f;
            default: goto L_0x002e;
        };
    L_0x002e:
        goto L_0x0005;
    L_0x002f:
        r0 = -3;
        goto L_0x001e;
    L_0x0031:
        r0 = r1;
        goto L_0x001e;
    L_0x0033:
        r0 = r1;
        goto L_0x001e;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.FocusRequester.focusLossForGainRequest(int):int");
    }

    void handleExternalFocusGain(int focusGain) {
        handleFocusLoss(focusLossForGainRequest(focusGain));
    }

    void handleFocusGain(int focusGain) {
        try {
            this.mFocusLossReceived = 0;
            this.mFocusController.notifyExtPolicyFocusGrant_syncAf(toAudioFocusInfo(), 1);
            if (this.mFocusDispatcher != null) {
                this.mFocusDispatcher.dispatchAudioFocusChange(focusGain, this.mClientId);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failure to signal gain of audio focus due to: ", e);
        }
    }

    void handleFocusLoss(int focusLoss) {
        try {
            if (focusLoss != this.mFocusLossReceived) {
                this.mFocusLossReceived = focusLoss;
                if (!this.mFocusController.mustNotifyFocusOwnerOnDuck() && this.mFocusLossReceived == -3 && (this.mGrantFlags & 2) == 0) {
                    this.mFocusController.notifyExtPolicyFocusLoss_syncAf(toAudioFocusInfo(), DEBUG);
                } else if (this.mFocusDispatcher != null) {
                    this.mFocusController.notifyExtPolicyFocusLoss_syncAf(toAudioFocusInfo(), true);
                    this.mFocusDispatcher.dispatchAudioFocusChange(this.mFocusLossReceived, this.mClientId);
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Failure to signal loss of audio focus due to:", e);
        }
    }

    AudioFocusInfo toAudioFocusInfo() {
        return new AudioFocusInfo(this.mAttributes, this.mClientId, this.mPackageName, this.mFocusGainRequest, this.mFocusLossReceived, this.mGrantFlags);
    }
}
