package android.support.v4.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

final class FragmentManagerState implements Parcelable {
    public static final Creator<FragmentManagerState> CREATOR;
    FragmentState[] mActive;
    int[] mAdded;
    BackStackState[] mBackStack;

    static {
        CREATOR = new Creator<FragmentManagerState>() {
            public FragmentManagerState createFromParcel(Parcel parcel) {
                return new FragmentManagerState(parcel);
            }

            public FragmentManagerState[] newArray(int i) {
                return new FragmentManagerState[i];
            }
        };
    }

    public FragmentManagerState(Parcel parcel) {
        Creator creator = FragmentState.CREATOR;
        throw new VerifyError("bad dex opcode");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedArray(this.mActive, i);
        parcel.writeIntArray(this.mAdded);
        parcel.writeTypedArray(this.mBackStack, i);
    }
}
