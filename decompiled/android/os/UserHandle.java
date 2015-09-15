package android.os;

import android.app.SearchManager;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import android.view.Window;
import java.io.PrintWriter;

public final class UserHandle implements Parcelable {
    public static final UserHandle ALL;
    public static final Creator<UserHandle> CREATOR;
    public static final UserHandle CURRENT;
    public static final UserHandle CURRENT_OR_SELF;
    public static final boolean MU_ENABLED = true;
    public static final UserHandle OWNER;
    public static final int PER_USER_RANGE = 100000;
    public static final int USER_ALL = -1;
    public static final int USER_CURRENT = -2;
    public static final int USER_CURRENT_OR_SELF = -3;
    public static final int USER_NULL = -10000;
    public static final int USER_OWNER = 0;
    private static final SparseArray<UserHandle> userHandles;
    final int mHandle;

    static {
        ALL = new UserHandle((int) USER_ALL);
        CURRENT = new UserHandle((int) USER_CURRENT);
        CURRENT_OR_SELF = new UserHandle((int) USER_CURRENT_OR_SELF);
        OWNER = new UserHandle(0);
        userHandles = new SparseArray();
        CREATOR = new Creator<UserHandle>() {
            public UserHandle createFromParcel(Parcel in) {
                return new UserHandle(in);
            }

            public UserHandle[] newArray(int size) {
                return new UserHandle[size];
            }
        };
    }

    public static final boolean isSameUser(int uid1, int uid2) {
        return getUserId(uid1) == getUserId(uid2) ? MU_ENABLED : false;
    }

    public static final boolean isSameApp(int uid1, int uid2) {
        return getAppId(uid1) == getAppId(uid2) ? MU_ENABLED : false;
    }

    public static final boolean isIsolated(int uid) {
        if (uid <= 0) {
            return false;
        }
        int appId = getAppId(uid);
        if (appId < Process.FIRST_ISOLATED_UID || appId > Process.LAST_ISOLATED_UID) {
            return false;
        }
        return MU_ENABLED;
    }

    public static boolean isApp(int uid) {
        if (uid <= 0) {
            return false;
        }
        int appId = getAppId(uid);
        if (appId < Window.PROGRESS_END || appId > Process.LAST_APPLICATION_UID) {
            return false;
        }
        return MU_ENABLED;
    }

    public static final int getUserId(int uid) {
        return uid / PER_USER_RANGE;
    }

    public static final int getCallingUserId() {
        return getUserId(Binder.getCallingUid());
    }

    public static final UserHandle getCallingUserHandle() {
        int userId = getUserId(Binder.getCallingUid());
        UserHandle userHandle = (UserHandle) userHandles.get(userId);
        if (userHandle != null) {
            return userHandle;
        }
        userHandle = new UserHandle(userId);
        userHandles.put(userId, userHandle);
        return userHandle;
    }

    public static final int getUid(int userId, int appId) {
        return (userId * PER_USER_RANGE) + (appId % PER_USER_RANGE);
    }

    public static final int getAppId(int uid) {
        return uid % PER_USER_RANGE;
    }

    public static final int getUserGid(int userId) {
        return getUid(userId, Process.SHARED_USER_GID);
    }

    public static final int getSharedAppGid(int id) {
        return (Process.FIRST_SHARED_APPLICATION_GID + (id % PER_USER_RANGE)) + USER_NULL;
    }

    public static void formatUid(StringBuilder sb, int uid) {
        if (uid < Window.PROGRESS_END) {
            sb.append(uid);
            return;
        }
        sb.append('u');
        sb.append(getUserId(uid));
        int appId = getAppId(uid);
        if (appId >= Process.FIRST_ISOLATED_UID && appId <= Process.LAST_ISOLATED_UID) {
            sb.append('i');
            sb.append(appId - Process.FIRST_ISOLATED_UID);
        } else if (appId >= Window.PROGRESS_END) {
            sb.append('a');
            sb.append(appId + USER_NULL);
        } else {
            sb.append(SearchManager.MENU_KEY);
            sb.append(appId);
        }
    }

    public static void formatUid(PrintWriter pw, int uid) {
        if (uid < Window.PROGRESS_END) {
            pw.print(uid);
            return;
        }
        pw.print('u');
        pw.print(getUserId(uid));
        int appId = getAppId(uid);
        if (appId >= Process.FIRST_ISOLATED_UID && appId <= Process.LAST_ISOLATED_UID) {
            pw.print('i');
            pw.print(appId - Process.FIRST_ISOLATED_UID);
        } else if (appId >= Window.PROGRESS_END) {
            pw.print('a');
            pw.print(appId + USER_NULL);
        } else {
            pw.print(SearchManager.MENU_KEY);
            pw.print(appId);
        }
    }

    public static final int myUserId() {
        return getUserId(Process.myUid());
    }

    public final boolean isOwner() {
        return equals(OWNER);
    }

    public UserHandle(int h) {
        this.mHandle = h;
    }

    public int getIdentifier() {
        return this.mHandle;
    }

    public String toString() {
        return "UserHandle{" + this.mHandle + "}";
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            if (this.mHandle == ((UserHandle) obj).mHandle) {
                return MU_ENABLED;
            }
            return false;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.mHandle;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mHandle);
    }

    public static void writeToParcel(UserHandle h, Parcel out) {
        if (h != null) {
            h.writeToParcel(out, 0);
        } else {
            out.writeInt(USER_NULL);
        }
    }

    public static UserHandle readFromParcel(Parcel in) {
        int h = in.readInt();
        return h != USER_NULL ? new UserHandle(h) : null;
    }

    public UserHandle(Parcel in) {
        this.mHandle = in.readInt();
    }
}
