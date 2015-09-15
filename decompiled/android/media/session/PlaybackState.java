package android.media.session;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.text.TextUtils;
import com.android.internal.telephony.RILConstants;
import com.android.internal.widget.PasswordEntryKeyboard;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.opengles.GL10;

public final class PlaybackState implements Parcelable {
    public static final long ACTION_FAST_FORWARD = 64;
    public static final long ACTION_PAUSE = 2;
    public static final long ACTION_PLAY = 4;
    public static final long ACTION_PLAY_FROM_MEDIA_ID = 1024;
    public static final long ACTION_PLAY_FROM_SEARCH = 2048;
    public static final long ACTION_PLAY_PAUSE = 512;
    public static final long ACTION_REWIND = 8;
    public static final long ACTION_SEEK_TO = 256;
    public static final long ACTION_SET_RATING = 128;
    public static final long ACTION_SKIP_TO_NEXT = 32;
    public static final long ACTION_SKIP_TO_PREVIOUS = 16;
    public static final long ACTION_SKIP_TO_QUEUE_ITEM = 4096;
    public static final long ACTION_STOP = 1;
    public static final Creator<PlaybackState> CREATOR;
    public static final long PLAYBACK_POSITION_UNKNOWN = -1;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_CONNECTING = 8;
    public static final int STATE_ERROR = 7;
    public static final int STATE_FAST_FORWARDING = 4;
    public static final int STATE_NONE = 0;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_REWINDING = 5;
    public static final int STATE_SKIPPING_TO_NEXT = 10;
    public static final int STATE_SKIPPING_TO_PREVIOUS = 9;
    public static final int STATE_SKIPPING_TO_QUEUE_ITEM = 11;
    public static final int STATE_STOPPED = 1;
    private static final String TAG = "PlaybackState";
    private final long mActions;
    private final long mActiveItemId;
    private final long mBufferedPosition;
    private List<CustomAction> mCustomActions;
    private final CharSequence mErrorMessage;
    private final Bundle mExtras;
    private final long mPosition;
    private final float mSpeed;
    private final int mState;
    private final long mUpdateTime;

    public static final class Builder {
        private long mActions;
        private long mActiveItemId;
        private long mBufferedPosition;
        private final List<CustomAction> mCustomActions;
        private CharSequence mErrorMessage;
        private Bundle mExtras;
        private long mPosition;
        private float mSpeed;
        private int mState;
        private long mUpdateTime;

        public Builder() {
            this.mCustomActions = new ArrayList();
            this.mActiveItemId = PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        }

        public Builder(PlaybackState from) {
            this.mCustomActions = new ArrayList();
            this.mActiveItemId = PlaybackState.PLAYBACK_POSITION_UNKNOWN;
            if (from != null) {
                this.mState = from.mState;
                this.mPosition = from.mPosition;
                this.mBufferedPosition = from.mBufferedPosition;
                this.mSpeed = from.mSpeed;
                this.mActions = from.mActions;
                if (from.mCustomActions != null) {
                    this.mCustomActions.addAll(from.mCustomActions);
                }
                this.mErrorMessage = from.mErrorMessage;
                this.mUpdateTime = from.mUpdateTime;
                this.mActiveItemId = from.mActiveItemId;
                this.mExtras = from.mExtras;
            }
        }

        public Builder setState(int state, long position, float playbackSpeed, long updateTime) {
            this.mState = state;
            this.mPosition = position;
            this.mUpdateTime = updateTime;
            this.mSpeed = playbackSpeed;
            return this;
        }

        public Builder setState(int state, long position, float playbackSpeed) {
            return setState(state, position, playbackSpeed, SystemClock.elapsedRealtime());
        }

        public Builder setActions(long actions) {
            this.mActions = actions;
            return this;
        }

        public Builder addCustomAction(String action, String name, int icon) {
            return addCustomAction(new CustomAction(name, icon, null, null));
        }

        public Builder addCustomAction(CustomAction customAction) {
            if (customAction == null) {
                throw new IllegalArgumentException("You may not add a null CustomAction to PlaybackState.");
            }
            this.mCustomActions.add(customAction);
            return this;
        }

        public Builder setBufferedPosition(long bufferedPosition) {
            this.mBufferedPosition = bufferedPosition;
            return this;
        }

        public Builder setActiveQueueItemId(long id) {
            this.mActiveItemId = id;
            return this;
        }

        public Builder setErrorMessage(CharSequence error) {
            this.mErrorMessage = error;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public PlaybackState build() {
            return new PlaybackState(this.mPosition, this.mUpdateTime, this.mSpeed, this.mBufferedPosition, this.mActions, this.mCustomActions, this.mActiveItemId, this.mErrorMessage, this.mExtras, null);
        }
    }

    public static final class CustomAction implements Parcelable {
        public static final Creator<CustomAction> CREATOR;
        private final String mAction;
        private final Bundle mExtras;
        private final int mIcon;
        private final CharSequence mName;

        public static final class Builder {
            private final String mAction;
            private Bundle mExtras;
            private final int mIcon;
            private final CharSequence mName;

            public Builder(java.lang.String r1, java.lang.CharSequence r2, int r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.session.PlaybackState.CustomAction.Builder.<init>(java.lang.String, java.lang.CharSequence, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.session.PlaybackState.CustomAction.Builder.<init>(java.lang.String, java.lang.CharSequence, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.session.PlaybackState.CustomAction.Builder.<init>(java.lang.String, java.lang.CharSequence, int):void");
            }

            public android.media.session.PlaybackState.CustomAction build() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.session.PlaybackState.CustomAction.Builder.build():android.media.session.PlaybackState$CustomAction
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.session.PlaybackState.CustomAction.Builder.build():android.media.session.PlaybackState$CustomAction
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.session.PlaybackState.CustomAction.Builder.build():android.media.session.PlaybackState$CustomAction");
            }

            public android.media.session.PlaybackState.CustomAction.Builder setExtras(android.os.Bundle r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.session.PlaybackState.CustomAction.Builder.setExtras(android.os.Bundle):android.media.session.PlaybackState$CustomAction$Builder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.session.PlaybackState.CustomAction.Builder.setExtras(android.os.Bundle):android.media.session.PlaybackState$CustomAction$Builder
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.session.PlaybackState.CustomAction.Builder.setExtras(android.os.Bundle):android.media.session.PlaybackState$CustomAction$Builder");
            }
        }

        private CustomAction(String action, CharSequence name, int icon, Bundle extras) {
            this.mAction = action;
            this.mName = name;
            this.mIcon = icon;
            this.mExtras = extras;
        }

        private CustomAction(Parcel in) {
            this.mAction = in.readString();
            this.mName = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.mIcon = in.readInt();
            this.mExtras = in.readBundle();
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mAction);
            TextUtils.writeToParcel(this.mName, dest, flags);
            dest.writeInt(this.mIcon);
            dest.writeBundle(this.mExtras);
        }

        public int describeContents() {
            return PlaybackState.STATE_NONE;
        }

        static {
            CREATOR = new Creator<CustomAction>() {
                public CustomAction createFromParcel(Parcel p) {
                    return new CustomAction(null);
                }

                public CustomAction[] newArray(int size) {
                    return new CustomAction[size];
                }
            };
        }

        public String getAction() {
            return this.mAction;
        }

        public CharSequence getName() {
            return this.mName;
        }

        public int getIcon() {
            return this.mIcon;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public String toString() {
            return "Action:mName='" + this.mName + ", mIcon=" + this.mIcon + ", mExtras=" + this.mExtras;
        }
    }

    private PlaybackState(int state, long position, long updateTime, float speed, long bufferedPosition, long transportControls, List<CustomAction> customActions, long activeItemId, CharSequence error, Bundle extras) {
        this.mState = state;
        this.mPosition = position;
        this.mSpeed = speed;
        this.mUpdateTime = updateTime;
        this.mBufferedPosition = bufferedPosition;
        this.mActions = transportControls;
        this.mCustomActions = new ArrayList(customActions);
        this.mActiveItemId = activeItemId;
        this.mErrorMessage = error;
        this.mExtras = extras;
    }

    private PlaybackState(Parcel in) {
        this.mState = in.readInt();
        this.mPosition = in.readLong();
        this.mSpeed = in.readFloat();
        this.mUpdateTime = in.readLong();
        this.mBufferedPosition = in.readLong();
        this.mActions = in.readLong();
        this.mCustomActions = in.createTypedArrayList(CustomAction.CREATOR);
        this.mActiveItemId = in.readLong();
        this.mErrorMessage = in.readCharSequence();
        this.mExtras = in.readBundle();
    }

    public String toString() {
        StringBuilder bob = new StringBuilder("PlaybackState {");
        bob.append("state=").append(this.mState);
        bob.append(", position=").append(this.mPosition);
        bob.append(", buffered position=").append(this.mBufferedPosition);
        bob.append(", speed=").append(this.mSpeed);
        bob.append(", updated=").append(this.mUpdateTime);
        bob.append(", actions=").append(this.mActions);
        bob.append(", custom actions=").append(this.mCustomActions);
        bob.append(", active item id=").append(this.mActiveItemId);
        bob.append(", error=").append(this.mErrorMessage);
        bob.append("}");
        return bob.toString();
    }

    public int describeContents() {
        return STATE_NONE;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mState);
        dest.writeLong(this.mPosition);
        dest.writeFloat(this.mSpeed);
        dest.writeLong(this.mUpdateTime);
        dest.writeLong(this.mBufferedPosition);
        dest.writeLong(this.mActions);
        dest.writeTypedList(this.mCustomActions);
        dest.writeLong(this.mActiveItemId);
        dest.writeCharSequence(this.mErrorMessage);
        dest.writeBundle(this.mExtras);
    }

    public int getState() {
        return this.mState;
    }

    public long getPosition() {
        return this.mPosition;
    }

    public long getBufferedPosition() {
        return this.mBufferedPosition;
    }

    public float getPlaybackSpeed() {
        return this.mSpeed;
    }

    public long getActions() {
        return this.mActions;
    }

    public List<CustomAction> getCustomActions() {
        return this.mCustomActions;
    }

    public CharSequence getErrorMessage() {
        return this.mErrorMessage;
    }

    public long getLastPositionUpdateTime() {
        return this.mUpdateTime;
    }

    public long getActiveQueueItemId() {
        return this.mActiveItemId;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public static int getStateFromRccState(int rccState) {
        switch (rccState) {
            case STATE_NONE /*0*/:
                return STATE_NONE;
            case STATE_STOPPED /*1*/:
                return STATE_STOPPED;
            case STATE_PAUSED /*2*/:
                return STATE_PAUSED;
            case STATE_PLAYING /*3*/:
                return STATE_PLAYING;
            case STATE_FAST_FORWARDING /*4*/:
                return STATE_FAST_FORWARDING;
            case STATE_REWINDING /*5*/:
                return STATE_REWINDING;
            case STATE_BUFFERING /*6*/:
                return STATE_SKIPPING_TO_NEXT;
            case STATE_ERROR /*7*/:
                return STATE_SKIPPING_TO_PREVIOUS;
            case STATE_CONNECTING /*8*/:
                return STATE_BUFFERING;
            case STATE_SKIPPING_TO_PREVIOUS /*9*/:
                return STATE_ERROR;
            default:
                return -1;
        }
    }

    public static int getRccStateFromState(int state) {
        switch (state) {
            case STATE_NONE /*0*/:
                return STATE_NONE;
            case STATE_STOPPED /*1*/:
                return STATE_STOPPED;
            case STATE_PAUSED /*2*/:
                return STATE_PAUSED;
            case STATE_PLAYING /*3*/:
                return STATE_PLAYING;
            case STATE_FAST_FORWARDING /*4*/:
                return STATE_FAST_FORWARDING;
            case STATE_REWINDING /*5*/:
                return STATE_REWINDING;
            case STATE_BUFFERING /*6*/:
                return STATE_CONNECTING;
            case STATE_ERROR /*7*/:
                return STATE_SKIPPING_TO_PREVIOUS;
            case STATE_SKIPPING_TO_PREVIOUS /*9*/:
                return STATE_ERROR;
            case STATE_SKIPPING_TO_NEXT /*10*/:
                return STATE_BUFFERING;
            default:
                return -1;
        }
    }

    public static long getActionsFromRccControlFlags(int rccFlags) {
        long actions = 0;
        for (long flag = ACTION_STOP; flag <= ((long) rccFlags); flag <<= STATE_STOPPED) {
            if ((((long) rccFlags) & flag) != 0) {
                actions |= getActionForRccFlag((int) flag);
            }
        }
        return actions;
    }

    public static int getRccControlFlagsFromActions(long actions) {
        int rccFlags = STATE_NONE;
        long action = ACTION_STOP;
        while (action <= actions && action < 2147483647L) {
            if ((action & actions) != 0) {
                rccFlags |= getRccFlagForAction(action);
            }
            action <<= STATE_STOPPED;
        }
        return rccFlags;
    }

    private static long getActionForRccFlag(int flag) {
        switch (flag) {
            case STATE_STOPPED /*1*/:
                return ACTION_SKIP_TO_PREVIOUS;
            case STATE_PAUSED /*2*/:
                return ACTION_REWIND;
            case STATE_FAST_FORWARDING /*4*/:
                return ACTION_PLAY;
            case STATE_CONNECTING /*8*/:
                return ACTION_PLAY_PAUSE;
            case RILConstants.RIL_RESTRICTED_STATE_PS_ALL /*16*/:
                return ACTION_PAUSE;
            case PasswordEntryKeyboard.KEYCODE_SPACE /*32*/:
                return ACTION_STOP;
            case RILConstants.RIL_REQUEST_DELETE_SMS_ON_SIM /*64*/:
                return ACTION_FAST_FORWARD;
            case RILConstants.RIL_REQUEST_SET_DATA_PROFILE /*128*/:
                return ACTION_SKIP_TO_NEXT;
            case GL10.GL_DEPTH_BUFFER_BIT /*256*/:
                return ACTION_SEEK_TO;
            case GL10.GL_NEVER /*512*/:
                return ACTION_SET_RATING;
            default:
                return 0;
        }
    }

    private static int getRccFlagForAction(long action) {
        int testAction;
        if (action < 2147483647L) {
            testAction = (int) action;
        } else {
            testAction = STATE_NONE;
        }
        switch (testAction) {
            case STATE_STOPPED /*1*/:
                return 32;
            case STATE_PAUSED /*2*/:
                return 16;
            case STATE_FAST_FORWARDING /*4*/:
                return STATE_FAST_FORWARDING;
            case STATE_CONNECTING /*8*/:
                return STATE_PAUSED;
            case RILConstants.RIL_RESTRICTED_STATE_PS_ALL /*16*/:
                return STATE_STOPPED;
            case PasswordEntryKeyboard.KEYCODE_SPACE /*32*/:
                return RILConstants.RIL_REQUEST_SET_DATA_PROFILE;
            case RILConstants.RIL_REQUEST_DELETE_SMS_ON_SIM /*64*/:
                return 64;
            case RILConstants.RIL_REQUEST_SET_DATA_PROFILE /*128*/:
                return GL10.GL_NEVER;
            case GL10.GL_DEPTH_BUFFER_BIT /*256*/:
                return GL10.GL_DEPTH_BUFFER_BIT;
            case GL10.GL_NEVER /*512*/:
                return STATE_CONNECTING;
            default:
                return STATE_NONE;
        }
    }

    static {
        CREATOR = new Creator<PlaybackState>() {
            public PlaybackState createFromParcel(Parcel in) {
                return new PlaybackState(null);
            }

            public PlaybackState[] newArray(int size) {
                return new PlaybackState[size];
            }
        };
    }
}
