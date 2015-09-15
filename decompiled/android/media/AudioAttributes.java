package android.media;

import android.media.MediaRecorder.AudioSource;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class AudioAttributes implements Parcelable {
    private static final int ALL_PARCEL_FLAGS = 1;
    public static final int CONTENT_TYPE_MOVIE = 3;
    public static final int CONTENT_TYPE_MUSIC = 2;
    public static final int CONTENT_TYPE_SONIFICATION = 4;
    public static final int CONTENT_TYPE_SPEECH = 1;
    public static final int CONTENT_TYPE_UNKNOWN = 0;
    public static final Creator<AudioAttributes> CREATOR;
    private static final int FLAG_ALL = 63;
    private static final int FLAG_ALL_PUBLIC = 17;
    public static final int FLAG_AUDIBILITY_ENFORCED = 1;
    public static final int FLAG_BEACON = 8;
    public static final int FLAG_HW_AV_SYNC = 16;
    public static final int FLAG_HW_HOTWORD = 32;
    public static final int FLAG_SCO = 4;
    public static final int FLAG_SECURE = 2;
    public static final int FLATTEN_TAGS = 1;
    private static final String TAG = "AudioAttributes";
    public static final int USAGE_ALARM = 4;
    public static final int USAGE_ASSISTANCE_ACCESSIBILITY = 11;
    public static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12;
    public static final int USAGE_ASSISTANCE_SONIFICATION = 13;
    public static final int USAGE_GAME = 14;
    public static final int USAGE_MEDIA = 1;
    public static final int USAGE_NOTIFICATION = 5;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
    public static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
    public static final int USAGE_NOTIFICATION_EVENT = 10;
    public static final int USAGE_NOTIFICATION_RINGTONE = 6;
    public static final int USAGE_UNKNOWN = 0;
    public static final int USAGE_VIRTUAL_SOURCE = 15;
    public static final int USAGE_VOICE_COMMUNICATION = 2;
    public static final int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3;
    private int mContentType;
    private int mFlags;
    private String mFormattedTags;
    private int mSource;
    private HashSet<String> mTags;
    private int mUsage;

    public static class Builder {
        private int mContentType;
        private int mFlags;
        private int mSource;
        private HashSet<String> mTags;
        private int mUsage;

        public Builder() {
            this.mUsage = AudioAttributes.USAGE_UNKNOWN;
            this.mContentType = AudioAttributes.USAGE_UNKNOWN;
            this.mSource = -1;
            this.mFlags = AudioAttributes.USAGE_UNKNOWN;
            this.mTags = new HashSet();
        }

        public Builder(AudioAttributes aa) {
            this.mUsage = AudioAttributes.USAGE_UNKNOWN;
            this.mContentType = AudioAttributes.USAGE_UNKNOWN;
            this.mSource = -1;
            this.mFlags = AudioAttributes.USAGE_UNKNOWN;
            this.mTags = new HashSet();
            this.mUsage = aa.mUsage;
            this.mContentType = aa.mContentType;
            this.mFlags = aa.mFlags;
            this.mTags = (HashSet) aa.mTags.clone();
        }

        public AudioAttributes build() {
            AudioAttributes aa = new AudioAttributes();
            aa.mContentType = this.mContentType;
            aa.mUsage = this.mUsage;
            aa.mSource = this.mSource;
            aa.mFlags = this.mFlags;
            aa.mTags = (HashSet) this.mTags.clone();
            aa.mFormattedTags = TextUtils.join((CharSequence) ";", this.mTags);
            return aa;
        }

        public Builder setUsage(int usage) {
            switch (usage) {
                case AudioAttributes.USAGE_UNKNOWN /*0*/:
                case AudioAttributes.USAGE_MEDIA /*1*/:
                case AudioAttributes.USAGE_VOICE_COMMUNICATION /*2*/:
                case AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING /*3*/:
                case AudioAttributes.USAGE_ALARM /*4*/:
                case AudioAttributes.USAGE_NOTIFICATION /*5*/:
                case AudioAttributes.USAGE_NOTIFICATION_RINGTONE /*6*/:
                case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST /*7*/:
                case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT /*8*/:
                case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED /*9*/:
                case AudioAttributes.USAGE_NOTIFICATION_EVENT /*10*/:
                case AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY /*11*/:
                case AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE /*12*/:
                case AudioAttributes.USAGE_ASSISTANCE_SONIFICATION /*13*/:
                case AudioAttributes.USAGE_GAME /*14*/:
                case AudioAttributes.USAGE_VIRTUAL_SOURCE /*15*/:
                    this.mUsage = usage;
                    break;
                default:
                    this.mUsage = AudioAttributes.USAGE_UNKNOWN;
                    break;
            }
            return this;
        }

        public Builder setContentType(int contentType) {
            switch (contentType) {
                case AudioAttributes.USAGE_UNKNOWN /*0*/:
                case AudioAttributes.USAGE_MEDIA /*1*/:
                case AudioAttributes.USAGE_VOICE_COMMUNICATION /*2*/:
                case AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING /*3*/:
                case AudioAttributes.USAGE_ALARM /*4*/:
                    this.mContentType = contentType;
                    break;
                default:
                    this.mUsage = AudioAttributes.USAGE_UNKNOWN;
                    break;
            }
            return this;
        }

        public Builder setFlags(int flags) {
            this.mFlags |= flags & AudioAttributes.FLAG_ALL;
            return this;
        }

        public Builder addTag(String tag) {
            this.mTags.add(tag);
            return this;
        }

        public Builder setLegacyStreamType(int streamType) {
            return setInternalLegacyStreamType(streamType);
        }

        public Builder setInternalLegacyStreamType(int streamType) {
            switch (streamType) {
                case AudioAttributes.USAGE_UNKNOWN /*0*/:
                    this.mContentType = AudioAttributes.USAGE_MEDIA;
                    break;
                case AudioAttributes.USAGE_MEDIA /*1*/:
                    break;
                case AudioAttributes.USAGE_VOICE_COMMUNICATION /*2*/:
                    this.mContentType = AudioAttributes.USAGE_ALARM;
                    break;
                case AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING /*3*/:
                    this.mContentType = AudioAttributes.USAGE_VOICE_COMMUNICATION;
                    break;
                case AudioAttributes.USAGE_ALARM /*4*/:
                    this.mContentType = AudioAttributes.USAGE_ALARM;
                    break;
                case AudioAttributes.USAGE_NOTIFICATION /*5*/:
                    this.mContentType = AudioAttributes.USAGE_ALARM;
                    break;
                case AudioAttributes.USAGE_NOTIFICATION_RINGTONE /*6*/:
                    this.mContentType = AudioAttributes.USAGE_MEDIA;
                    this.mFlags |= AudioAttributes.USAGE_ALARM;
                    break;
                case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST /*7*/:
                    this.mFlags |= AudioAttributes.USAGE_MEDIA;
                    break;
                case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT /*8*/:
                    this.mContentType = AudioAttributes.USAGE_ALARM;
                    break;
                case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED /*9*/:
                    this.mContentType = AudioAttributes.USAGE_MEDIA;
                    break;
                default:
                    Log.e(AudioAttributes.TAG, "Invalid stream type " + streamType + " for AudioAttributes");
                    break;
            }
            this.mContentType = AudioAttributes.USAGE_ALARM;
            this.mUsage = AudioAttributes.usageForLegacyStreamType(streamType);
            return this;
        }

        public Builder setCapturePreset(int preset) {
            switch (preset) {
                case AudioAttributes.USAGE_UNKNOWN /*0*/:
                case AudioAttributes.USAGE_MEDIA /*1*/:
                case AudioAttributes.USAGE_NOTIFICATION /*5*/:
                case AudioAttributes.USAGE_NOTIFICATION_RINGTONE /*6*/:
                case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST /*7*/:
                    this.mSource = preset;
                    break;
                default:
                    Log.e(AudioAttributes.TAG, "Invalid capture preset " + preset + " for AudioAttributes");
                    break;
            }
            return this;
        }

        public Builder setInternalCapturePreset(int preset) {
            if (preset == 1999 || preset == AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT || preset == AudioSource.FM_TUNER) {
                this.mSource = preset;
            } else {
                setCapturePreset(preset);
            }
            return this;
        }
    }

    private AudioAttributes() {
        this.mUsage = USAGE_UNKNOWN;
        this.mContentType = USAGE_UNKNOWN;
        this.mSource = -1;
        this.mFlags = USAGE_UNKNOWN;
    }

    public int getContentType() {
        return this.mContentType;
    }

    public int getUsage() {
        return this.mUsage;
    }

    public int getCapturePreset() {
        return this.mSource;
    }

    public int getFlags() {
        return this.mFlags & FLAG_ALL_PUBLIC;
    }

    public int getAllFlags() {
        return this.mFlags & FLAG_ALL;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(this.mTags);
    }

    public int describeContents() {
        return USAGE_UNKNOWN;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUsage);
        dest.writeInt(this.mContentType);
        dest.writeInt(this.mSource);
        dest.writeInt(this.mFlags);
        dest.writeInt(flags & USAGE_MEDIA);
        if ((flags & USAGE_MEDIA) == 0) {
            String[] tagsArray = new String[this.mTags.size()];
            this.mTags.toArray(tagsArray);
            dest.writeStringArray(tagsArray);
        } else if ((flags & USAGE_MEDIA) == USAGE_MEDIA) {
            dest.writeString(this.mFormattedTags);
        }
    }

    private AudioAttributes(Parcel in) {
        boolean hasFlattenedTags = true;
        this.mUsage = USAGE_UNKNOWN;
        this.mContentType = USAGE_UNKNOWN;
        this.mSource = -1;
        this.mFlags = USAGE_UNKNOWN;
        this.mUsage = in.readInt();
        this.mContentType = in.readInt();
        this.mSource = in.readInt();
        this.mFlags = in.readInt();
        if ((in.readInt() & USAGE_MEDIA) != USAGE_MEDIA) {
            hasFlattenedTags = false;
        }
        this.mTags = new HashSet();
        if (hasFlattenedTags) {
            this.mFormattedTags = new String(in.readString());
            this.mTags.add(this.mFormattedTags);
            return;
        }
        String[] tagsArray = in.readStringArray();
        for (int i = tagsArray.length - 1; i >= 0; i--) {
            this.mTags.add(tagsArray[i]);
        }
        this.mFormattedTags = TextUtils.join((CharSequence) ";", this.mTags);
    }

    static {
        CREATOR = new 1();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioAttributes that = (AudioAttributes) o;
        if (this.mContentType == that.mContentType && this.mFlags == that.mFlags && this.mSource == that.mSource && this.mUsage == that.mUsage && this.mFormattedTags.equals(that.mFormattedTags)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        Object[] objArr = new Object[USAGE_NOTIFICATION];
        objArr[USAGE_UNKNOWN] = Integer.valueOf(this.mContentType);
        objArr[USAGE_MEDIA] = Integer.valueOf(this.mFlags);
        objArr[USAGE_VOICE_COMMUNICATION] = Integer.valueOf(this.mSource);
        objArr[USAGE_VOICE_COMMUNICATION_SIGNALLING] = Integer.valueOf(this.mUsage);
        objArr[USAGE_ALARM] = this.mFormattedTags;
        return Objects.hash(objArr);
    }

    public String toString() {
        return new String("AudioAttributes: usage=" + this.mUsage + " content=" + this.mContentType + " flags=0x" + Integer.toHexString(this.mFlags).toUpperCase() + " tags=" + this.mFormattedTags);
    }

    public String usageToString() {
        return usageToString(this.mUsage);
    }

    public static String usageToString(int usage) {
        switch (usage) {
            case USAGE_UNKNOWN /*0*/:
                return new String("USAGE_UNKNOWN");
            case USAGE_MEDIA /*1*/:
                return new String("USAGE_MEDIA");
            case USAGE_VOICE_COMMUNICATION /*2*/:
                return new String("USAGE_VOICE_COMMUNICATION");
            case USAGE_VOICE_COMMUNICATION_SIGNALLING /*3*/:
                return new String("USAGE_VOICE_COMMUNICATION");
            case USAGE_ALARM /*4*/:
                return new String("USAGE_ALARM");
            case USAGE_NOTIFICATION /*5*/:
                return new String("USAGE_NOTIFICATION");
            case USAGE_NOTIFICATION_RINGTONE /*6*/:
                return new String("USAGE_NOTIFICATION");
            case USAGE_NOTIFICATION_COMMUNICATION_REQUEST /*7*/:
                return new String("USAGE_NOTIFICATION");
            case USAGE_NOTIFICATION_COMMUNICATION_INSTANT /*8*/:
                return new String("USAGE_NOTIFICATION_COMMUNICATION_INSTANT");
            case USAGE_NOTIFICATION_COMMUNICATION_DELAYED /*9*/:
                return new String("USAGE_NOTIFICATION_COMMUNICATION_DELAYED");
            case USAGE_NOTIFICATION_EVENT /*10*/:
                return new String("USAGE_NOTIFICATION_EVENT");
            case USAGE_ASSISTANCE_ACCESSIBILITY /*11*/:
                return new String("USAGE_ASSISTANCE_ACCESSIBILITY");
            case USAGE_ASSISTANCE_NAVIGATION_GUIDANCE /*12*/:
                return new String("USAGE_ASSISTANCE_NAVIGATION_GUIDANCE");
            case USAGE_ASSISTANCE_SONIFICATION /*13*/:
                return new String("USAGE_ASSISTANCE_SONIFICATION");
            case USAGE_GAME /*14*/:
                return new String("USAGE_GAME");
            default:
                return new String("unknown usage " + usage);
        }
    }

    public static int usageForLegacyStreamType(int streamType) {
        switch (streamType) {
            case USAGE_UNKNOWN /*0*/:
            case USAGE_NOTIFICATION_RINGTONE /*6*/:
                return USAGE_VOICE_COMMUNICATION;
            case USAGE_MEDIA /*1*/:
            case USAGE_NOTIFICATION_COMMUNICATION_REQUEST /*7*/:
                return USAGE_ASSISTANCE_SONIFICATION;
            case USAGE_VOICE_COMMUNICATION /*2*/:
                return USAGE_NOTIFICATION_RINGTONE;
            case USAGE_VOICE_COMMUNICATION_SIGNALLING /*3*/:
                return USAGE_MEDIA;
            case USAGE_ALARM /*4*/:
                return USAGE_ALARM;
            case USAGE_NOTIFICATION /*5*/:
                return USAGE_NOTIFICATION;
            case USAGE_NOTIFICATION_COMMUNICATION_INSTANT /*8*/:
                return USAGE_VOICE_COMMUNICATION_SIGNALLING;
            case USAGE_NOTIFICATION_COMMUNICATION_DELAYED /*9*/:
                return USAGE_ASSISTANCE_ACCESSIBILITY;
            default:
                return USAGE_UNKNOWN;
        }
    }

    public static int toLegacyStreamType(AudioAttributes aa) {
        if ((aa.getFlags() & USAGE_MEDIA) == USAGE_MEDIA) {
            return USAGE_NOTIFICATION_COMMUNICATION_REQUEST;
        }
        if ((aa.getFlags() & USAGE_ALARM) == USAGE_ALARM) {
            return USAGE_NOTIFICATION_RINGTONE;
        }
        switch (aa.getUsage()) {
            case USAGE_MEDIA /*1*/:
            case USAGE_ASSISTANCE_ACCESSIBILITY /*11*/:
            case USAGE_ASSISTANCE_NAVIGATION_GUIDANCE /*12*/:
            case USAGE_GAME /*14*/:
                return USAGE_VOICE_COMMUNICATION_SIGNALLING;
            case USAGE_VOICE_COMMUNICATION /*2*/:
                return USAGE_UNKNOWN;
            case USAGE_VOICE_COMMUNICATION_SIGNALLING /*3*/:
                return USAGE_NOTIFICATION_COMMUNICATION_INSTANT;
            case USAGE_ALARM /*4*/:
                return USAGE_ALARM;
            case USAGE_NOTIFICATION /*5*/:
            case USAGE_NOTIFICATION_COMMUNICATION_REQUEST /*7*/:
            case USAGE_NOTIFICATION_COMMUNICATION_INSTANT /*8*/:
            case USAGE_NOTIFICATION_COMMUNICATION_DELAYED /*9*/:
            case USAGE_NOTIFICATION_EVENT /*10*/:
                return USAGE_NOTIFICATION;
            case USAGE_NOTIFICATION_RINGTONE /*6*/:
                return USAGE_VOICE_COMMUNICATION;
            case USAGE_ASSISTANCE_SONIFICATION /*13*/:
                return USAGE_MEDIA;
            default:
                return USAGE_VOICE_COMMUNICATION_SIGNALLING;
        }
    }
}
