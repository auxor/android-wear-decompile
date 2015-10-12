package android.support.v4.media.session;

import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.ResultReceiver;
import android.support.v4.media.MediaDescriptionCompat;

public class MediaSessionCompat {

    public static final class QueueItem implements Parcelable {
        public static final Creator<QueueItem> CREATOR;
        private final MediaDescriptionCompat mDescription;
        private final long mId;

        static {
            CREATOR = new Creator<QueueItem>() {
                public QueueItem createFromParcel(Parcel parcel) {
                    return new QueueItem(null);
                }

                public QueueItem[] newArray(int i) {
                    return new QueueItem[i];
                }
            };
        }

        private QueueItem(Parcel parcel) {
            MediaDescriptionCompat mediaDescriptionCompat = (MediaDescriptionCompat) MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
            throw new VerifyError("bad dex opcode");
        }

        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "MediaSession.QueueItem {Description=" + this.mDescription + ", Id=" + this.mId + " }";
        }

        public void writeToParcel(Parcel parcel, int i) {
            this.mDescription.writeToParcel(parcel, i);
            parcel.writeLong(this.mId);
        }
    }

    static final class ResultReceiverWrapper implements Parcelable {
        public static final Creator<ResultReceiverWrapper> CREATOR;
        private ResultReceiver mResultReceiver;

        static {
            CREATOR = new Creator<ResultReceiverWrapper>() {
                public ResultReceiverWrapper createFromParcel(Parcel parcel) {
                    return new ResultReceiverWrapper(parcel);
                }

                public ResultReceiverWrapper[] newArray(int i) {
                    return new ResultReceiverWrapper[i];
                }
            };
        }

        ResultReceiverWrapper(Parcel parcel) {
            ResultReceiver resultReceiver = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(parcel);
            throw new VerifyError("bad dex opcode");
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            this.mResultReceiver.writeToParcel(parcel, i);
        }
    }

    public static final class Token implements Parcelable {
        public static final Creator<Token> CREATOR;
        private final Object mInner;

        static {
            CREATOR = new Creator<Token>() {
                public Token createFromParcel(Parcel parcel) {
                    return new Token(VERSION.SDK_INT >= 21 ? parcel.readParcelable(null) : parcel.readStrongBinder());
                }

                public Token[] newArray(int i) {
                    return new Token[i];
                }
            };
        }

        Token(Object obj) {
            this.mInner = obj;
            throw new VerifyError("bad dex opcode");
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            if (VERSION.SDK_INT >= 21) {
                throw new VerifyError("bad dex opcode");
            }
            throw new VerifyError("bad dex opcode");
        }
    }
}
