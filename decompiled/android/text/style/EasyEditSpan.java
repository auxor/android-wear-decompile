package android.text.style;

import android.app.PendingIntent;
import android.os.Parcel;
import android.text.ParcelableSpan;

public class EasyEditSpan implements ParcelableSpan {
    public static final String EXTRA_TEXT_CHANGED_TYPE = "android.text.style.EXTRA_TEXT_CHANGED_TYPE";
    public static final int TEXT_DELETED = 1;
    public static final int TEXT_MODIFIED = 2;
    private boolean mDeleteEnabled;
    private final PendingIntent mPendingIntent;

    public EasyEditSpan() {
        this.mPendingIntent = null;
        this.mDeleteEnabled = true;
    }

    public EasyEditSpan(PendingIntent pendingIntent) {
        this.mPendingIntent = pendingIntent;
        this.mDeleteEnabled = true;
    }

    public EasyEditSpan(Parcel source) {
        this.mPendingIntent = (PendingIntent) source.readParcelable(null);
        this.mDeleteEnabled = source.readByte() == (byte) 1;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i = 0;
        dest.writeParcelable(this.mPendingIntent, 0);
        if (this.mDeleteEnabled) {
            i = TEXT_DELETED;
        }
        dest.writeByte((byte) i);
    }

    public int getSpanTypeId() {
        return 22;
    }

    public boolean isDeleteEnabled() {
        return this.mDeleteEnabled;
    }

    public void setDeleteEnabled(boolean value) {
        this.mDeleteEnabled = value;
    }

    public PendingIntent getPendingIntent() {
        return this.mPendingIntent;
    }
}
