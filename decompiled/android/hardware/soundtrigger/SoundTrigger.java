package android.hardware.soundtrigger;

import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.net.ProxyInfo;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class SoundTrigger {
    public static final int RECOGNITION_MODE_USER_AUTHENTICATION = 4;
    public static final int RECOGNITION_MODE_USER_IDENTIFICATION = 2;
    public static final int RECOGNITION_MODE_VOICE_TRIGGER = 1;
    public static final int RECOGNITION_STATUS_ABORT = 1;
    public static final int RECOGNITION_STATUS_FAILURE = 2;
    public static final int RECOGNITION_STATUS_SUCCESS = 0;
    public static final int SERVICE_STATE_DISABLED = 1;
    public static final int SERVICE_STATE_ENABLED = 0;
    public static final int SOUNDMODEL_STATUS_UPDATED = 0;
    public static final int STATUS_BAD_VALUE = -22;
    public static final int STATUS_DEAD_OBJECT = -32;
    public static final int STATUS_ERROR = Integer.MIN_VALUE;
    public static final int STATUS_INVALID_OPERATION = -38;
    public static final int STATUS_NO_INIT = -19;
    public static final int STATUS_OK = 0;
    public static final int STATUS_PERMISSION_DENIED = -1;

    public static class ConfidenceLevel implements Parcelable {
        public static final Creator<ConfidenceLevel> CREATOR;
        public final int confidenceLevel;
        public final int userId;

        public ConfidenceLevel(int userId, int confidenceLevel) {
            this.userId = userId;
            this.confidenceLevel = confidenceLevel;
        }

        static {
            CREATOR = new Creator<ConfidenceLevel>() {
                public ConfidenceLevel createFromParcel(Parcel in) {
                    return ConfidenceLevel.fromParcel(in);
                }

                public ConfidenceLevel[] newArray(int size) {
                    return new ConfidenceLevel[size];
                }
            };
        }

        private static ConfidenceLevel fromParcel(Parcel in) {
            return new ConfidenceLevel(in.readInt(), in.readInt());
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.userId);
            dest.writeInt(this.confidenceLevel);
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public int hashCode() {
            return ((this.confidenceLevel + 31) * 31) + this.userId;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ConfidenceLevel other = (ConfidenceLevel) obj;
            if (this.confidenceLevel != other.confidenceLevel) {
                return false;
            }
            if (this.userId != other.userId) {
                return false;
            }
            return true;
        }

        public String toString() {
            return "ConfidenceLevel [userId=" + this.userId + ", confidenceLevel=" + this.confidenceLevel + "]";
        }
    }

    public static class Keyphrase implements Parcelable {
        public static final Creator<Keyphrase> CREATOR;
        public final int id;
        public final String locale;
        public final int recognitionModes;
        public final String text;
        public final int[] users;

        public Keyphrase(int id, int recognitionModes, String locale, String text, int[] users) {
            this.id = id;
            this.recognitionModes = recognitionModes;
            this.locale = locale;
            this.text = text;
            this.users = users;
        }

        static {
            CREATOR = new Creator<Keyphrase>() {
                public Keyphrase createFromParcel(Parcel in) {
                    return Keyphrase.fromParcel(in);
                }

                public Keyphrase[] newArray(int size) {
                    return new Keyphrase[size];
                }
            };
        }

        private static Keyphrase fromParcel(Parcel in) {
            int id = in.readInt();
            int recognitionModes = in.readInt();
            String locale = in.readString();
            String text = in.readString();
            int[] users = null;
            int numUsers = in.readInt();
            if (numUsers >= 0) {
                users = new int[numUsers];
                in.readIntArray(users);
            }
            return new Keyphrase(id, recognitionModes, locale, text, users);
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.recognitionModes);
            dest.writeString(this.locale);
            dest.writeString(this.text);
            if (this.users != null) {
                dest.writeInt(this.users.length);
                dest.writeIntArray(this.users);
                return;
            }
            dest.writeInt(SoundTrigger.STATUS_PERMISSION_DENIED);
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public int hashCode() {
            int i = SoundTrigger.STATUS_OK;
            int hashCode = ((((this.text == null ? SoundTrigger.STATUS_OK : this.text.hashCode()) + 31) * 31) + this.id) * 31;
            if (this.locale != null) {
                i = this.locale.hashCode();
            }
            return ((((hashCode + i) * 31) + this.recognitionModes) * 31) + Arrays.hashCode(this.users);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Keyphrase other = (Keyphrase) obj;
            if (this.text == null) {
                if (other.text != null) {
                    return false;
                }
            } else if (!this.text.equals(other.text)) {
                return false;
            }
            if (this.id != other.id) {
                return false;
            }
            if (this.locale == null) {
                if (other.locale != null) {
                    return false;
                }
            } else if (!this.locale.equals(other.locale)) {
                return false;
            }
            if (this.recognitionModes != other.recognitionModes) {
                return false;
            }
            if (Arrays.equals(this.users, other.users)) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "Keyphrase [id=" + this.id + ", recognitionModes=" + this.recognitionModes + ", locale=" + this.locale + ", text=" + this.text + ", users=" + Arrays.toString(this.users) + "]";
        }
    }

    public static class RecognitionEvent implements Parcelable {
        public static final Creator<RecognitionEvent> CREATOR;
        public final boolean captureAvailable;
        public final int captureDelayMs;
        public AudioFormat captureFormat;
        public final int capturePreambleMs;
        public final int captureSession;
        public final byte[] data;
        public final int soundModelHandle;
        public final int status;
        public final boolean triggerInData;

        public RecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data) {
            this.status = status;
            this.soundModelHandle = soundModelHandle;
            this.captureAvailable = captureAvailable;
            this.captureSession = captureSession;
            this.captureDelayMs = captureDelayMs;
            this.capturePreambleMs = capturePreambleMs;
            this.triggerInData = triggerInData;
            this.captureFormat = captureFormat;
            this.data = data;
        }

        static {
            CREATOR = new Creator<RecognitionEvent>() {
                public RecognitionEvent createFromParcel(Parcel in) {
                    return RecognitionEvent.fromParcel(in);
                }

                public RecognitionEvent[] newArray(int size) {
                    return new RecognitionEvent[size];
                }
            };
        }

        private static RecognitionEvent fromParcel(Parcel in) {
            boolean captureAvailable;
            boolean triggerInData;
            int status = in.readInt();
            int soundModelHandle = in.readInt();
            if (in.readByte() == (byte) 1) {
                captureAvailable = true;
            } else {
                captureAvailable = false;
            }
            int captureSession = in.readInt();
            int captureDelayMs = in.readInt();
            int capturePreambleMs = in.readInt();
            if (in.readByte() == (byte) 1) {
                triggerInData = true;
            } else {
                triggerInData = false;
            }
            AudioFormat captureFormat = null;
            if (in.readByte() == (byte) 1) {
                captureFormat = new Builder().setChannelMask(in.readInt()).setEncoding(in.readInt()).setSampleRate(in.readInt()).build();
            }
            return new RecognitionEvent(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, in.readBlob());
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            dest.writeInt(this.status);
            dest.writeInt(this.soundModelHandle);
            dest.writeByte((byte) (this.captureAvailable ? SoundTrigger.SERVICE_STATE_DISABLED : SoundTrigger.STATUS_OK));
            dest.writeInt(this.captureSession);
            dest.writeInt(this.captureDelayMs);
            dest.writeInt(this.capturePreambleMs);
            if (this.triggerInData) {
                i = SoundTrigger.SERVICE_STATE_DISABLED;
            } else {
                i = SoundTrigger.STATUS_OK;
            }
            dest.writeByte((byte) i);
            if (this.captureFormat != null) {
                dest.writeByte((byte) 1);
                dest.writeInt(this.captureFormat.getSampleRate());
                dest.writeInt(this.captureFormat.getEncoding());
                dest.writeInt(this.captureFormat.getChannelMask());
            } else {
                dest.writeByte((byte) 0);
            }
            dest.writeBlob(this.data);
        }

        public int hashCode() {
            int i = 1231;
            int i2 = ((((((((this.captureAvailable ? 1231 : 1237) + 31) * 31) + this.captureDelayMs) * 31) + this.capturePreambleMs) * 31) + this.captureSession) * 31;
            if (!this.triggerInData) {
                i = 1237;
            }
            int result = i2 + i;
            if (this.captureFormat != null) {
                result = (((((result * 31) + this.captureFormat.getSampleRate()) * 31) + this.captureFormat.getEncoding()) * 31) + this.captureFormat.getChannelMask();
            }
            return (((((result * 31) + Arrays.hashCode(this.data)) * 31) + this.soundModelHandle) * 31) + this.status;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            RecognitionEvent other = (RecognitionEvent) obj;
            if (this.captureAvailable != other.captureAvailable) {
                return false;
            }
            if (this.captureDelayMs != other.captureDelayMs) {
                return false;
            }
            if (this.capturePreambleMs != other.capturePreambleMs) {
                return false;
            }
            if (this.captureSession != other.captureSession) {
                return false;
            }
            if (!Arrays.equals(this.data, other.data)) {
                return false;
            }
            if (this.soundModelHandle != other.soundModelHandle) {
                return false;
            }
            if (this.status != other.status) {
                return false;
            }
            if (this.triggerInData != other.triggerInData) {
                return false;
            }
            if (this.captureFormat.getSampleRate() != other.captureFormat.getSampleRate()) {
                return false;
            }
            if (this.captureFormat.getEncoding() != other.captureFormat.getEncoding()) {
                return false;
            }
            if (this.captureFormat.getChannelMask() != other.captureFormat.getChannelMask()) {
                return false;
            }
            return true;
        }

        public String toString() {
            return "RecognitionEvent [status=" + this.status + ", soundModelHandle=" + this.soundModelHandle + ", captureAvailable=" + this.captureAvailable + ", captureSession=" + this.captureSession + ", captureDelayMs=" + this.captureDelayMs + ", capturePreambleMs=" + this.capturePreambleMs + ", triggerInData=" + this.triggerInData + (this.captureFormat == null ? ProxyInfo.LOCAL_EXCL_LIST : ", sampleRate=" + this.captureFormat.getSampleRate()) + (this.captureFormat == null ? ProxyInfo.LOCAL_EXCL_LIST : ", encoding=" + this.captureFormat.getEncoding()) + (this.captureFormat == null ? ProxyInfo.LOCAL_EXCL_LIST : ", channelMask=" + this.captureFormat.getChannelMask()) + ", data=" + (this.data == null ? SoundTrigger.STATUS_OK : this.data.length) + "]";
        }
    }

    public static class KeyphraseRecognitionEvent extends RecognitionEvent {
        public static final Creator<KeyphraseRecognitionEvent> CREATOR;
        public final KeyphraseRecognitionExtra[] keyphraseExtras;

        public KeyphraseRecognitionEvent(int status, int soundModelHandle, boolean captureAvailable, int captureSession, int captureDelayMs, int capturePreambleMs, boolean triggerInData, AudioFormat captureFormat, byte[] data, KeyphraseRecognitionExtra[] keyphraseExtras) {
            super(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, data);
            this.keyphraseExtras = keyphraseExtras;
        }

        static {
            CREATOR = new Creator<KeyphraseRecognitionEvent>() {
                public KeyphraseRecognitionEvent createFromParcel(Parcel in) {
                    return KeyphraseRecognitionEvent.fromParcel(in);
                }

                public KeyphraseRecognitionEvent[] newArray(int size) {
                    return new KeyphraseRecognitionEvent[size];
                }
            };
        }

        private static KeyphraseRecognitionEvent fromParcel(Parcel in) {
            int status = in.readInt();
            int soundModelHandle = in.readInt();
            boolean captureAvailable = in.readByte() == SoundTrigger.SERVICE_STATE_DISABLED;
            int captureSession = in.readInt();
            int captureDelayMs = in.readInt();
            int capturePreambleMs = in.readInt();
            boolean triggerInData = in.readByte() == SoundTrigger.SERVICE_STATE_DISABLED;
            AudioFormat captureFormat = null;
            if (in.readByte() == SoundTrigger.SERVICE_STATE_DISABLED) {
                captureFormat = new Builder().setChannelMask(in.readInt()).setEncoding(in.readInt()).setSampleRate(in.readInt()).build();
            }
            return new KeyphraseRecognitionEvent(status, soundModelHandle, captureAvailable, captureSession, captureDelayMs, capturePreambleMs, triggerInData, captureFormat, in.readBlob(), (KeyphraseRecognitionExtra[]) in.createTypedArray(KeyphraseRecognitionExtra.CREATOR));
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            dest.writeInt(this.status);
            dest.writeInt(this.soundModelHandle);
            dest.writeByte((byte) (this.captureAvailable ? SoundTrigger.SERVICE_STATE_DISABLED : SoundTrigger.STATUS_OK));
            dest.writeInt(this.captureSession);
            dest.writeInt(this.captureDelayMs);
            dest.writeInt(this.capturePreambleMs);
            if (this.triggerInData) {
                i = SoundTrigger.SERVICE_STATE_DISABLED;
            } else {
                i = SoundTrigger.STATUS_OK;
            }
            dest.writeByte((byte) i);
            if (this.captureFormat != null) {
                dest.writeByte((byte) 1);
                dest.writeInt(this.captureFormat.getSampleRate());
                dest.writeInt(this.captureFormat.getEncoding());
                dest.writeInt(this.captureFormat.getChannelMask());
            } else {
                dest.writeByte((byte) 0);
            }
            dest.writeBlob(this.data);
            dest.writeTypedArray(this.keyphraseExtras, flags);
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public int hashCode() {
            return (super.hashCode() * 31) + Arrays.hashCode(this.keyphraseExtras);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            if (Arrays.equals(this.keyphraseExtras, ((KeyphraseRecognitionEvent) obj).keyphraseExtras)) {
                return true;
            }
            return false;
        }

        public String toString() {
            return "KeyphraseRecognitionEvent [keyphraseExtras=" + Arrays.toString(this.keyphraseExtras) + ", status=" + this.status + ", soundModelHandle=" + this.soundModelHandle + ", captureAvailable=" + this.captureAvailable + ", captureSession=" + this.captureSession + ", captureDelayMs=" + this.captureDelayMs + ", capturePreambleMs=" + this.capturePreambleMs + ", triggerInData=" + this.triggerInData + (this.captureFormat == null ? ProxyInfo.LOCAL_EXCL_LIST : ", sampleRate=" + this.captureFormat.getSampleRate()) + (this.captureFormat == null ? ProxyInfo.LOCAL_EXCL_LIST : ", encoding=" + this.captureFormat.getEncoding()) + (this.captureFormat == null ? ProxyInfo.LOCAL_EXCL_LIST : ", channelMask=" + this.captureFormat.getChannelMask()) + ", data=" + (this.data == null ? SoundTrigger.STATUS_OK : this.data.length) + "]";
        }
    }

    public static class KeyphraseRecognitionExtra implements Parcelable {
        public static final Creator<KeyphraseRecognitionExtra> CREATOR;
        public final int coarseConfidenceLevel;
        public final ConfidenceLevel[] confidenceLevels;
        public final int id;
        public final int recognitionModes;

        public KeyphraseRecognitionExtra(int id, int recognitionModes, int coarseConfidenceLevel, ConfidenceLevel[] confidenceLevels) {
            this.id = id;
            this.recognitionModes = recognitionModes;
            this.coarseConfidenceLevel = coarseConfidenceLevel;
            this.confidenceLevels = confidenceLevels;
        }

        static {
            CREATOR = new Creator<KeyphraseRecognitionExtra>() {
                public KeyphraseRecognitionExtra createFromParcel(Parcel in) {
                    return KeyphraseRecognitionExtra.fromParcel(in);
                }

                public KeyphraseRecognitionExtra[] newArray(int size) {
                    return new KeyphraseRecognitionExtra[size];
                }
            };
        }

        private static KeyphraseRecognitionExtra fromParcel(Parcel in) {
            return new KeyphraseRecognitionExtra(in.readInt(), in.readInt(), in.readInt(), (ConfidenceLevel[]) in.createTypedArray(ConfidenceLevel.CREATOR));
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeInt(this.recognitionModes);
            dest.writeInt(this.coarseConfidenceLevel);
            dest.writeTypedArray(this.confidenceLevels, flags);
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public int hashCode() {
            return ((((((Arrays.hashCode(this.confidenceLevels) + 31) * 31) + this.id) * 31) + this.recognitionModes) * 31) + this.coarseConfidenceLevel;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            KeyphraseRecognitionExtra other = (KeyphraseRecognitionExtra) obj;
            if (!Arrays.equals(this.confidenceLevels, other.confidenceLevels)) {
                return false;
            }
            if (this.id != other.id) {
                return false;
            }
            if (this.recognitionModes != other.recognitionModes) {
                return false;
            }
            if (this.coarseConfidenceLevel != other.coarseConfidenceLevel) {
                return false;
            }
            return true;
        }

        public String toString() {
            return "KeyphraseRecognitionExtra [id=" + this.id + ", recognitionModes=" + this.recognitionModes + ", coarseConfidenceLevel=" + this.coarseConfidenceLevel + ", confidenceLevels=" + Arrays.toString(this.confidenceLevels) + "]";
        }
    }

    public static class SoundModel {
        public static final int TYPE_KEYPHRASE = 0;
        public static final int TYPE_UNKNOWN = -1;
        public final byte[] data;
        public final int type;
        public final UUID uuid;
        public final UUID vendorUuid;

        public SoundModel(UUID uuid, UUID vendorUuid, int type, byte[] data) {
            this.uuid = uuid;
            this.vendorUuid = vendorUuid;
            this.type = type;
            this.data = data;
        }

        public int hashCode() {
            int i = TYPE_KEYPHRASE;
            int hashCode = (((((Arrays.hashCode(this.data) + 31) * 31) + this.type) * 31) + (this.uuid == null ? TYPE_KEYPHRASE : this.uuid.hashCode())) * 31;
            if (this.vendorUuid != null) {
                i = this.vendorUuid.hashCode();
            }
            return hashCode + i;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof SoundModel)) {
                return false;
            }
            SoundModel other = (SoundModel) obj;
            if (!Arrays.equals(this.data, other.data)) {
                return false;
            }
            if (this.type != other.type) {
                return false;
            }
            if (this.uuid == null) {
                if (other.uuid != null) {
                    return false;
                }
            } else if (!this.uuid.equals(other.uuid)) {
                return false;
            }
            if (this.vendorUuid == null) {
                if (other.vendorUuid != null) {
                    return false;
                }
                return true;
            } else if (this.vendorUuid.equals(other.vendorUuid)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class KeyphraseSoundModel extends SoundModel implements Parcelable {
        public static final Creator<KeyphraseSoundModel> CREATOR;
        public final Keyphrase[] keyphrases;

        public KeyphraseSoundModel(UUID uuid, UUID vendorUuid, byte[] data, Keyphrase[] keyphrases) {
            super(uuid, vendorUuid, SoundTrigger.STATUS_OK, data);
            this.keyphrases = keyphrases;
        }

        static {
            CREATOR = new Creator<KeyphraseSoundModel>() {
                public KeyphraseSoundModel createFromParcel(Parcel in) {
                    return KeyphraseSoundModel.fromParcel(in);
                }

                public KeyphraseSoundModel[] newArray(int size) {
                    return new KeyphraseSoundModel[size];
                }
            };
        }

        private static KeyphraseSoundModel fromParcel(Parcel in) {
            UUID uuid = UUID.fromString(in.readString());
            UUID vendorUuid = null;
            if (in.readInt() >= 0) {
                vendorUuid = UUID.fromString(in.readString());
            }
            return new KeyphraseSoundModel(uuid, vendorUuid, in.readBlob(), (Keyphrase[]) in.createTypedArray(Keyphrase.CREATOR));
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.uuid.toString());
            if (this.vendorUuid == null) {
                dest.writeInt(SoundTrigger.STATUS_PERMISSION_DENIED);
            } else {
                dest.writeInt(this.vendorUuid.toString().length());
                dest.writeString(this.vendorUuid.toString());
            }
            dest.writeBlob(this.data);
            dest.writeTypedArray(this.keyphrases, flags);
        }

        public String toString() {
            return "KeyphraseSoundModel [keyphrases=" + Arrays.toString(this.keyphrases) + ", uuid=" + this.uuid + ", vendorUuid=" + this.vendorUuid + ", type=" + this.type + ", data=" + (this.data == null ? SoundTrigger.STATUS_OK : this.data.length) + "]";
        }

        public int hashCode() {
            return (super.hashCode() * 31) + Arrays.hashCode(this.keyphrases);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (!(obj instanceof KeyphraseSoundModel)) {
                return false;
            }
            if (Arrays.equals(this.keyphrases, ((KeyphraseSoundModel) obj).keyphrases)) {
                return true;
            }
            return false;
        }
    }

    public static class ModuleProperties implements Parcelable {
        public static final Creator<ModuleProperties> CREATOR;
        public final String description;
        public final int id;
        public final String implementor;
        public final int maxBufferMs;
        public final int maxKeyphrases;
        public final int maxSoundModels;
        public final int maxUsers;
        public final int powerConsumptionMw;
        public final int recognitionModes;
        public final boolean returnsTriggerInEvent;
        public final boolean supportsCaptureTransition;
        public final boolean supportsConcurrentCapture;
        public final UUID uuid;
        public final int version;

        ModuleProperties(int id, String implementor, String description, String uuid, int version, int maxSoundModels, int maxKeyphrases, int maxUsers, int recognitionModes, boolean supportsCaptureTransition, int maxBufferMs, boolean supportsConcurrentCapture, int powerConsumptionMw, boolean returnsTriggerInEvent) {
            this.id = id;
            this.implementor = implementor;
            this.description = description;
            this.uuid = UUID.fromString(uuid);
            this.version = version;
            this.maxSoundModels = maxSoundModels;
            this.maxKeyphrases = maxKeyphrases;
            this.maxUsers = maxUsers;
            this.recognitionModes = recognitionModes;
            this.supportsCaptureTransition = supportsCaptureTransition;
            this.maxBufferMs = maxBufferMs;
            this.supportsConcurrentCapture = supportsConcurrentCapture;
            this.powerConsumptionMw = powerConsumptionMw;
            this.returnsTriggerInEvent = returnsTriggerInEvent;
        }

        static {
            CREATOR = new Creator<ModuleProperties>() {
                public ModuleProperties createFromParcel(Parcel in) {
                    return ModuleProperties.fromParcel(in);
                }

                public ModuleProperties[] newArray(int size) {
                    return new ModuleProperties[size];
                }
            };
        }

        private static ModuleProperties fromParcel(Parcel in) {
            return new ModuleProperties(in.readInt(), in.readString(), in.readString(), in.readString(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readByte() == SoundTrigger.SERVICE_STATE_DISABLED, in.readInt(), in.readByte() == SoundTrigger.SERVICE_STATE_DISABLED, in.readInt(), in.readByte() == SoundTrigger.SERVICE_STATE_DISABLED);
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            int i2 = SoundTrigger.SERVICE_STATE_DISABLED;
            dest.writeInt(this.id);
            dest.writeString(this.implementor);
            dest.writeString(this.description);
            dest.writeString(this.uuid.toString());
            dest.writeInt(this.version);
            dest.writeInt(this.maxSoundModels);
            dest.writeInt(this.maxKeyphrases);
            dest.writeInt(this.maxUsers);
            dest.writeInt(this.recognitionModes);
            dest.writeByte((byte) (this.supportsCaptureTransition ? SoundTrigger.SERVICE_STATE_DISABLED : SoundTrigger.STATUS_OK));
            dest.writeInt(this.maxBufferMs);
            if (this.supportsConcurrentCapture) {
                i = SoundTrigger.SERVICE_STATE_DISABLED;
            } else {
                i = SoundTrigger.STATUS_OK;
            }
            dest.writeByte((byte) i);
            dest.writeInt(this.powerConsumptionMw);
            if (!this.returnsTriggerInEvent) {
                i2 = SoundTrigger.STATUS_OK;
            }
            dest.writeByte((byte) i2);
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public String toString() {
            return "ModuleProperties [id=" + this.id + ", implementor=" + this.implementor + ", description=" + this.description + ", uuid=" + this.uuid + ", version=" + this.version + ", maxSoundModels=" + this.maxSoundModels + ", maxKeyphrases=" + this.maxKeyphrases + ", maxUsers=" + this.maxUsers + ", recognitionModes=" + this.recognitionModes + ", supportsCaptureTransition=" + this.supportsCaptureTransition + ", maxBufferMs=" + this.maxBufferMs + ", supportsConcurrentCapture=" + this.supportsConcurrentCapture + ", powerConsumptionMw=" + this.powerConsumptionMw + ", returnsTriggerInEvent=" + this.returnsTriggerInEvent + "]";
        }
    }

    public static class RecognitionConfig implements Parcelable {
        public static final Creator<RecognitionConfig> CREATOR;
        public final boolean allowMultipleTriggers;
        public final boolean captureRequested;
        public final byte[] data;
        public final KeyphraseRecognitionExtra[] keyphrases;

        public RecognitionConfig(boolean captureRequested, boolean allowMultipleTriggers, KeyphraseRecognitionExtra[] keyphrases, byte[] data) {
            this.captureRequested = captureRequested;
            this.allowMultipleTriggers = allowMultipleTriggers;
            this.keyphrases = keyphrases;
            this.data = data;
        }

        static {
            CREATOR = new Creator<RecognitionConfig>() {
                public RecognitionConfig createFromParcel(Parcel in) {
                    return RecognitionConfig.fromParcel(in);
                }

                public RecognitionConfig[] newArray(int size) {
                    return new RecognitionConfig[size];
                }
            };
        }

        private static RecognitionConfig fromParcel(Parcel in) {
            boolean captureRequested;
            boolean allowMultipleTriggers;
            if (in.readByte() == (byte) 1) {
                captureRequested = true;
            } else {
                captureRequested = false;
            }
            if (in.readByte() == (byte) 1) {
                allowMultipleTriggers = true;
            } else {
                allowMultipleTriggers = false;
            }
            return new RecognitionConfig(captureRequested, allowMultipleTriggers, (KeyphraseRecognitionExtra[]) in.createTypedArray(KeyphraseRecognitionExtra.CREATOR), in.readBlob());
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            int i2 = SoundTrigger.SERVICE_STATE_DISABLED;
            if (this.captureRequested) {
                i = SoundTrigger.SERVICE_STATE_DISABLED;
            } else {
                i = SoundTrigger.STATUS_OK;
            }
            dest.writeByte((byte) i);
            if (!this.allowMultipleTriggers) {
                i2 = SoundTrigger.STATUS_OK;
            }
            dest.writeByte((byte) i2);
            dest.writeTypedArray(this.keyphrases, flags);
            dest.writeBlob(this.data);
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public String toString() {
            return "RecognitionConfig [captureRequested=" + this.captureRequested + ", allowMultipleTriggers=" + this.allowMultipleTriggers + ", keyphrases=" + Arrays.toString(this.keyphrases) + ", data=" + Arrays.toString(this.data) + "]";
        }
    }

    public static class SoundModelEvent implements Parcelable {
        public static final Creator<SoundModelEvent> CREATOR;
        public final byte[] data;
        public final int soundModelHandle;
        public final int status;

        SoundModelEvent(int status, int soundModelHandle, byte[] data) {
            this.status = status;
            this.soundModelHandle = soundModelHandle;
            this.data = data;
        }

        static {
            CREATOR = new Creator<SoundModelEvent>() {
                public SoundModelEvent createFromParcel(Parcel in) {
                    return SoundModelEvent.fromParcel(in);
                }

                public SoundModelEvent[] newArray(int size) {
                    return new SoundModelEvent[size];
                }
            };
        }

        private static SoundModelEvent fromParcel(Parcel in) {
            return new SoundModelEvent(in.readInt(), in.readInt(), in.readBlob());
        }

        public int describeContents() {
            return SoundTrigger.STATUS_OK;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.status);
            dest.writeInt(this.soundModelHandle);
            dest.writeBlob(this.data);
        }

        public int hashCode() {
            return ((((Arrays.hashCode(this.data) + 31) * 31) + this.soundModelHandle) * 31) + this.status;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SoundModelEvent other = (SoundModelEvent) obj;
            if (!Arrays.equals(this.data, other.data)) {
                return false;
            }
            if (this.soundModelHandle != other.soundModelHandle) {
                return false;
            }
            if (this.status != other.status) {
                return false;
            }
            return true;
        }

        public String toString() {
            return "SoundModelEvent [status=" + this.status + ", soundModelHandle=" + this.soundModelHandle + ", data=" + (this.data == null ? SoundTrigger.STATUS_OK : this.data.length) + "]";
        }
    }

    public interface StatusListener {
        void onRecognition(RecognitionEvent recognitionEvent);

        void onServiceDied();

        void onServiceStateChange(int i);

        void onSoundModelUpdate(SoundModelEvent soundModelEvent);
    }

    public static native int listModules(ArrayList<ModuleProperties> arrayList);

    public static SoundTriggerModule attachModule(int moduleId, StatusListener listener, Handler handler) {
        if (listener == null) {
            return null;
        }
        return new SoundTriggerModule(moduleId, listener, handler);
    }
}
