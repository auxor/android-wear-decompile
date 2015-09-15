package android.service.notification;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public class Condition implements Parcelable {
    public static final Creator<Condition> CREATOR;
    public static final int FLAG_RELEVANT_ALWAYS = 2;
    public static final int FLAG_RELEVANT_NOW = 1;
    public static final String SCHEME = "condition";
    public static final int STATE_ERROR = 3;
    public static final int STATE_FALSE = 0;
    public static final int STATE_TRUE = 1;
    public static final int STATE_UNKNOWN = 2;
    public final int flags;
    public final int icon;
    public final Uri id;
    public final String line1;
    public final String line2;
    public final int state;
    public final String summary;

    public Condition(Uri id, String summary, String line1, String line2, int icon, int state, int flags) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        } else if (summary == null) {
            throw new IllegalArgumentException("summary is required");
        } else if (line1 == null) {
            throw new IllegalArgumentException("line1 is required");
        } else if (line2 == null) {
            throw new IllegalArgumentException("line2 is required");
        } else if (isValidState(state)) {
            this.id = id;
            this.summary = summary;
            this.line1 = line1;
            this.line2 = line2;
            this.icon = icon;
            this.state = state;
            this.flags = flags;
        } else {
            throw new IllegalArgumentException("state is invalid: " + state);
        }
    }

    private Condition(Parcel source) {
        this((Uri) source.readParcelable(Condition.class.getClassLoader()), source.readString(), source.readString(), source.readString(), source.readInt(), source.readInt(), source.readInt());
    }

    private static boolean isValidState(int state) {
        return state >= 0 && state <= STATE_ERROR;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.id, STATE_FALSE);
        dest.writeString(this.summary);
        dest.writeString(this.line1);
        dest.writeString(this.line2);
        dest.writeInt(this.icon);
        dest.writeInt(this.state);
        dest.writeInt(this.flags);
    }

    public String toString() {
        return new StringBuilder(Condition.class.getSimpleName()).append('[').append("id=").append(this.id).append(",summary=").append(this.summary).append(",line1=").append(this.line1).append(",line2=").append(this.line2).append(",icon=").append(this.icon).append(",state=").append(stateToString(this.state)).append(",flags=").append(this.flags).append(']').toString();
    }

    public static String stateToString(int state) {
        if (state == 0) {
            return "STATE_FALSE";
        }
        if (state == STATE_TRUE) {
            return "STATE_TRUE";
        }
        if (state == STATE_UNKNOWN) {
            return "STATE_UNKNOWN";
        }
        if (state == STATE_ERROR) {
            return "STATE_ERROR";
        }
        throw new IllegalArgumentException("state is invalid: " + state);
    }

    public static String relevanceToString(int flags) {
        boolean now;
        boolean always = true;
        if ((flags & STATE_TRUE) != 0) {
            now = true;
        } else {
            now = false;
        }
        if ((flags & STATE_UNKNOWN) == 0) {
            always = false;
        }
        if (!now && !always) {
            return "NONE";
        }
        if (now && always) {
            return "NOW, ALWAYS";
        }
        return now ? "NOW" : "ALWAYS";
    }

    public boolean equals(Object o) {
        if (!(o instanceof Condition)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Condition other = (Condition) o;
        if (Objects.equals(other.id, this.id) && Objects.equals(other.summary, this.summary) && Objects.equals(other.line1, this.line1) && Objects.equals(other.line2, this.line2) && other.icon == this.icon && other.state == this.state && other.flags == this.flags) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.summary, this.line1, this.line2, Integer.valueOf(this.icon), Integer.valueOf(this.state), Integer.valueOf(this.flags)});
    }

    public int describeContents() {
        return STATE_FALSE;
    }

    public Condition copy() {
        Parcel parcel = Parcel.obtain();
        try {
            writeToParcel(parcel, STATE_FALSE);
            parcel.setDataPosition(STATE_FALSE);
            Condition condition = new Condition(parcel);
            return condition;
        } finally {
            parcel.recycle();
        }
    }

    public static Builder newId(Context context) {
        return new Builder().scheme(SCHEME).authority(context.getPackageName());
    }

    public static boolean isValidId(Uri id, String pkg) {
        return id != null && SCHEME.equals(id.getScheme()) && pkg.equals(id.getAuthority());
    }

    static {
        CREATOR = new Creator<Condition>() {
            public Condition createFromParcel(Parcel source) {
                return new Condition(null);
            }

            public Condition[] newArray(int size) {
                return new Condition[size];
            }
        };
    }
}
