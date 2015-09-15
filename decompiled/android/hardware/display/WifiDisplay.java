package android.hardware.display;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import libcore.util.Objects;

public final class WifiDisplay implements Parcelable {
    public static final Creator<WifiDisplay> CREATOR;
    public static final WifiDisplay[] EMPTY_ARRAY;
    private final boolean mCanConnect;
    private final String mDeviceAddress;
    private final String mDeviceAlias;
    private final String mDeviceName;
    private final boolean mIsAvailable;
    private final boolean mIsRemembered;

    static {
        EMPTY_ARRAY = new WifiDisplay[0];
        CREATOR = new Creator<WifiDisplay>() {
            public WifiDisplay createFromParcel(Parcel in) {
                boolean isAvailable;
                boolean canConnect;
                boolean isRemembered;
                String deviceAddress = in.readString();
                String deviceName = in.readString();
                String deviceAlias = in.readString();
                if (in.readInt() != 0) {
                    isAvailable = true;
                } else {
                    isAvailable = false;
                }
                if (in.readInt() != 0) {
                    canConnect = true;
                } else {
                    canConnect = false;
                }
                if (in.readInt() != 0) {
                    isRemembered = true;
                } else {
                    isRemembered = false;
                }
                return new WifiDisplay(deviceAddress, deviceName, deviceAlias, isAvailable, canConnect, isRemembered);
            }

            public WifiDisplay[] newArray(int size) {
                return size == 0 ? WifiDisplay.EMPTY_ARRAY : new WifiDisplay[size];
            }
        };
    }

    public WifiDisplay(String deviceAddress, String deviceName, String deviceAlias, boolean available, boolean canConnect, boolean remembered) {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress must not be null");
        } else if (deviceName == null) {
            throw new IllegalArgumentException("deviceName must not be null");
        } else {
            this.mDeviceAddress = deviceAddress;
            this.mDeviceName = deviceName;
            this.mDeviceAlias = deviceAlias;
            this.mIsAvailable = available;
            this.mCanConnect = canConnect;
            this.mIsRemembered = remembered;
        }
    }

    public String getDeviceAddress() {
        return this.mDeviceAddress;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public String getDeviceAlias() {
        return this.mDeviceAlias;
    }

    public boolean isAvailable() {
        return this.mIsAvailable;
    }

    public boolean canConnect() {
        return this.mCanConnect;
    }

    public boolean isRemembered() {
        return this.mIsRemembered;
    }

    public String getFriendlyDisplayName() {
        return this.mDeviceAlias != null ? this.mDeviceAlias : this.mDeviceName;
    }

    public boolean equals(Object o) {
        return (o instanceof WifiDisplay) && equals((WifiDisplay) o);
    }

    public boolean equals(WifiDisplay other) {
        return other != null && this.mDeviceAddress.equals(other.mDeviceAddress) && this.mDeviceName.equals(other.mDeviceName) && Objects.equal(this.mDeviceAlias, other.mDeviceAlias);
    }

    public boolean hasSameAddress(WifiDisplay other) {
        return other != null && this.mDeviceAddress.equals(other.mDeviceAddress);
    }

    public int hashCode() {
        return this.mDeviceAddress.hashCode();
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        dest.writeString(this.mDeviceAddress);
        dest.writeString(this.mDeviceName);
        dest.writeString(this.mDeviceAlias);
        if (this.mIsAvailable) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (this.mCanConnect) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.mIsRemembered) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        String result = this.mDeviceName + " (" + this.mDeviceAddress + ")";
        if (this.mDeviceAlias != null) {
            result = result + ", alias " + this.mDeviceAlias;
        }
        return result + ", isAvailable " + this.mIsAvailable + ", canConnect " + this.mCanConnect + ", isRemembered " + this.mIsRemembered;
    }
}
