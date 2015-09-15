package android.bluetooth;

import android.bluetooth.IBluetoothManagerCallback.Stub;
import android.content.Context;
import android.graphics.Color;
import android.net.ProxyInfo;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public final class BluetoothDevice implements Parcelable {
    public static final int ACCESS_ALLOWED = 1;
    public static final int ACCESS_REJECTED = 2;
    public static final int ACCESS_UNKNOWN = 0;
    public static final String ACTION_ACL_CONNECTED = "android.bluetooth.device.action.ACL_CONNECTED";
    public static final String ACTION_ACL_DISCONNECTED = "android.bluetooth.device.action.ACL_DISCONNECTED";
    public static final String ACTION_ACL_DISCONNECT_REQUESTED = "android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED";
    public static final String ACTION_ALIAS_CHANGED = "android.bluetooth.device.action.ALIAS_CHANGED";
    public static final String ACTION_BOND_STATE_CHANGED = "android.bluetooth.device.action.BOND_STATE_CHANGED";
    public static final String ACTION_CLASS_CHANGED = "android.bluetooth.device.action.CLASS_CHANGED";
    public static final String ACTION_CONNECTION_ACCESS_CANCEL = "android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL";
    public static final String ACTION_CONNECTION_ACCESS_REPLY = "android.bluetooth.device.action.CONNECTION_ACCESS_REPLY";
    public static final String ACTION_CONNECTION_ACCESS_REQUEST = "android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST";
    public static final String ACTION_DISAPPEARED = "android.bluetooth.device.action.DISAPPEARED";
    public static final String ACTION_FOUND = "android.bluetooth.device.action.FOUND";
    public static final String ACTION_MAS_INSTANCE = "android.bluetooth.device.action.MAS_INSTANCE";
    public static final String ACTION_NAME_CHANGED = "android.bluetooth.device.action.NAME_CHANGED";
    public static final String ACTION_NAME_FAILED = "android.bluetooth.device.action.NAME_FAILED";
    public static final String ACTION_PAIRING_CANCEL = "android.bluetooth.device.action.PAIRING_CANCEL";
    public static final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
    public static final String ACTION_UUID = "android.bluetooth.device.action.UUID";
    public static final int BOND_BONDED = 12;
    public static final int BOND_BONDING = 11;
    public static final int BOND_NONE = 10;
    public static final int BOND_SUCCESS = 0;
    public static final int CONNECTION_ACCESS_NO = 2;
    public static final int CONNECTION_ACCESS_YES = 1;
    private static final int CONNECTION_STATE_CONNECTED = 1;
    private static final int CONNECTION_STATE_DISCONNECTED = 0;
    private static final int CONNECTION_STATE_ENCRYPTED_BREDR = 2;
    private static final int CONNECTION_STATE_ENCRYPTED_LE = 4;
    public static final Creator<BluetoothDevice> CREATOR;
    private static final boolean DBG = false;
    public static final int DEVICE_TYPE_CLASSIC = 1;
    public static final int DEVICE_TYPE_DUAL = 3;
    public static final int DEVICE_TYPE_LE = 2;
    public static final int DEVICE_TYPE_UNKNOWN = 0;
    public static final int ERROR = Integer.MIN_VALUE;
    public static final String EXTRA_ACCESS_REQUEST_TYPE = "android.bluetooth.device.extra.ACCESS_REQUEST_TYPE";
    public static final String EXTRA_ALWAYS_ALLOWED = "android.bluetooth.device.extra.ALWAYS_ALLOWED";
    public static final String EXTRA_BOND_STATE = "android.bluetooth.device.extra.BOND_STATE";
    public static final String EXTRA_CLASS = "android.bluetooth.device.extra.CLASS";
    public static final String EXTRA_CLASS_NAME = "android.bluetooth.device.extra.CLASS_NAME";
    public static final String EXTRA_CONNECTION_ACCESS_RESULT = "android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT";
    public static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    public static final String EXTRA_MAS_INSTANCE = "android.bluetooth.device.extra.MAS_INSTANCE";
    public static final String EXTRA_NAME = "android.bluetooth.device.extra.NAME";
    public static final String EXTRA_PACKAGE_NAME = "android.bluetooth.device.extra.PACKAGE_NAME";
    public static final String EXTRA_PAIRING_KEY = "android.bluetooth.device.extra.PAIRING_KEY";
    public static final String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
    public static final String EXTRA_PREVIOUS_BOND_STATE = "android.bluetooth.device.extra.PREVIOUS_BOND_STATE";
    public static final String EXTRA_REASON = "android.bluetooth.device.extra.REASON";
    public static final String EXTRA_RSSI = "android.bluetooth.device.extra.RSSI";
    public static final String EXTRA_UUID = "android.bluetooth.device.extra.UUID";
    public static final int PAIRING_VARIANT_CONSENT = 3;
    public static final int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    public static final int PAIRING_VARIANT_DISPLAY_PIN = 5;
    public static final int PAIRING_VARIANT_OOB_CONSENT = 6;
    public static final int PAIRING_VARIANT_PASSKEY = 1;
    public static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
    public static final int PAIRING_VARIANT_PIN = 0;
    public static final int REQUEST_TYPE_MESSAGE_ACCESS = 3;
    public static final int REQUEST_TYPE_PHONEBOOK_ACCESS = 2;
    public static final int REQUEST_TYPE_PROFILE_CONNECTION = 1;
    private static final String TAG = "BluetoothDevice";
    public static final int TRANSPORT_AUTO = 0;
    public static final int TRANSPORT_BREDR = 1;
    public static final int TRANSPORT_LE = 2;
    public static final int UNBOND_REASON_AUTH_CANCELED = 3;
    public static final int UNBOND_REASON_AUTH_FAILED = 1;
    public static final int UNBOND_REASON_AUTH_REJECTED = 2;
    public static final int UNBOND_REASON_AUTH_TIMEOUT = 6;
    public static final int UNBOND_REASON_DISCOVERY_IN_PROGRESS = 5;
    public static final int UNBOND_REASON_REMOTE_AUTH_CANCELED = 8;
    public static final int UNBOND_REASON_REMOTE_DEVICE_DOWN = 4;
    public static final int UNBOND_REASON_REMOVED = 9;
    public static final int UNBOND_REASON_REPEATED_ATTEMPTS = 7;
    static IBluetoothManagerCallback mStateChangeCallback;
    private static IBluetooth sService;
    private final String mAddress;

    static IBluetooth getService() {
        synchronized (BluetoothDevice.class) {
            if (sService == null) {
                sService = BluetoothAdapter.getDefaultAdapter().getBluetoothService(mStateChangeCallback);
            }
        }
        return sService;
    }

    static {
        mStateChangeCallback = new Stub() {
            public void onBluetoothServiceUp(IBluetooth bluetoothService) throws RemoteException {
                synchronized (BluetoothDevice.class) {
                    BluetoothDevice.sService = bluetoothService;
                }
            }

            public void onBluetoothServiceDown() throws RemoteException {
                synchronized (BluetoothDevice.class) {
                    BluetoothDevice.sService = null;
                }
            }
        };
        CREATOR = new Creator<BluetoothDevice>() {
            public BluetoothDevice createFromParcel(Parcel in) {
                return new BluetoothDevice(in.readString());
            }

            public BluetoothDevice[] newArray(int size) {
                return new BluetoothDevice[size];
            }
        };
    }

    BluetoothDevice(String address) {
        getService();
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            this.mAddress = address;
            return;
        }
        throw new IllegalArgumentException(address + " is not a valid Bluetooth address");
    }

    public boolean equals(Object o) {
        if (o instanceof BluetoothDevice) {
            return this.mAddress.equals(((BluetoothDevice) o).getAddress());
        }
        return DBG;
    }

    public int hashCode() {
        return this.mAddress.hashCode();
    }

    public String toString() {
        return this.mAddress;
    }

    public int describeContents() {
        return TRANSPORT_AUTO;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mAddress);
    }

    public String getAddress() {
        return this.mAddress;
    }

    public String getName() {
        String str = null;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Remote Device name");
        } else {
            try {
                str = sService.getRemoteName(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return str;
    }

    public int getType() {
        int i = TRANSPORT_AUTO;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Remote Device type");
        } else {
            try {
                i = sService.getRemoteType(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return i;
    }

    public String getAlias() {
        String str = null;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Remote Device Alias");
        } else {
            try {
                str = sService.getRemoteAlias(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return str;
    }

    public boolean setAlias(String alias) {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot set Remote Device name");
        } else {
            try {
                z = sService.setRemoteAlias(this, alias);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public String getAliasName() {
        String name = getAlias();
        if (name == null) {
            return getName();
        }
        return name;
    }

    public boolean createBond() {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot create bond to Remote Device");
        } else {
            try {
                z = sService.createBond(this, TRANSPORT_AUTO);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public boolean createBond(int transport) {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot create bond to Remote Device");
        } else if (transport < 0 || transport > UNBOND_REASON_AUTH_REJECTED) {
            throw new IllegalArgumentException(transport + " is not a valid Bluetooth transport");
        } else {
            try {
                z = sService.createBond(this, transport);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public boolean createBondOutOfBand(byte[] hash, byte[] randomizer) {
        return DBG;
    }

    public boolean setDeviceOutOfBandData(byte[] hash, byte[] randomizer) {
        return DBG;
    }

    public boolean cancelBondProcess() {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot cancel Remote Device bond");
        } else {
            try {
                z = sService.cancelBondProcess(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public boolean removeBond() {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot remove Remote Device bond");
        } else {
            try {
                z = sService.removeBond(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public int getBondState() {
        int i = BOND_NONE;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get bond state");
        } else {
            try {
                i = sService.getBondState(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            } catch (NullPointerException npe) {
                Log.e(TAG, "NullPointerException for getBondState() of device (" + getAddress() + ")", npe);
            }
        }
        return i;
    }

    public boolean isConnected() {
        if (sService == null) {
            return DBG;
        }
        try {
            if (sService.getConnectionState(this) != 0) {
                return true;
            }
            return DBG;
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return DBG;
        }
    }

    public boolean isEncrypted() {
        boolean z = true;
        if (sService == null) {
            return DBG;
        }
        try {
            if (sService.getConnectionState(this) <= UNBOND_REASON_AUTH_FAILED) {
                z = DBG;
            }
            return z;
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return DBG;
        }
    }

    public BluetoothClass getBluetoothClass() {
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get Bluetooth Class");
            return null;
        }
        try {
            int classInt = sService.getRemoteClass(this);
            if (classInt != Color.BLACK) {
                return new BluetoothClass(classInt);
            }
            return null;
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return null;
        }
    }

    public ParcelUuid[] getUuids() {
        ParcelUuid[] parcelUuidArr = null;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot get remote device Uuids");
        } else {
            try {
                parcelUuidArr = sService.getRemoteUuids(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return parcelUuidArr;
    }

    public boolean fetchUuidsWithSdp() {
        boolean z = DBG;
        IBluetooth service = sService;
        if (service == null) {
            Log.e(TAG, "BT not enabled. Cannot fetchUuidsWithSdp");
        } else {
            try {
                z = service.fetchRemoteUuids(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public boolean fetchMasInstances() {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot query remote device for MAS instances");
        } else {
            try {
                z = sService.fetchRemoteMasInstances(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public int getServiceChannel(ParcelUuid uuid) {
        return ERROR;
    }

    public boolean setPin(byte[] pin) {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot set Remote Device pin");
        } else {
            try {
                z = sService.setPin(this, true, pin.length, pin);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public boolean setPasskey(int passkey) {
        return DBG;
    }

    public boolean setPairingConfirmation(boolean confirm) {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot set pairing confirmation");
        } else {
            try {
                z = sService.setPairingConfirmation(this, confirm);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public boolean setRemoteOutOfBandData() {
        return DBG;
    }

    public boolean cancelPairingUserInput() {
        boolean z = DBG;
        if (sService == null) {
            Log.e(TAG, "BT not enabled. Cannot create pairing user input");
        } else {
            try {
                z = sService.cancelBondProcess(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public boolean isBluetoothDock() {
        return DBG;
    }

    public int getPhonebookAccessPermission() {
        int i = TRANSPORT_AUTO;
        if (sService != null) {
            try {
                i = sService.getPhonebookAccessPermission(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return i;
    }

    public boolean setPhonebookAccessPermission(int value) {
        boolean z = DBG;
        if (sService != null) {
            try {
                z = sService.setPhonebookAccessPermission(this, value);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public int getMessageAccessPermission() {
        int i = TRANSPORT_AUTO;
        if (sService != null) {
            try {
                i = sService.getMessageAccessPermission(this);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return i;
    }

    public boolean setMessageAccessPermission(int value) {
        boolean z = DBG;
        if (sService != null) {
            try {
                z = sService.setMessageAccessPermission(this, value);
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
        return z;
    }

    public BluetoothSocket createRfcommSocket(int channel) throws IOException {
        return new BluetoothSocket(UNBOND_REASON_AUTH_FAILED, -1, true, true, this, channel, null);
    }

    public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) throws IOException {
        return new BluetoothSocket(UNBOND_REASON_AUTH_FAILED, -1, true, true, this, -1, new ParcelUuid(uuid));
    }

    public BluetoothSocket createInsecureRfcommSocketToServiceRecord(UUID uuid) throws IOException {
        return new BluetoothSocket(UNBOND_REASON_AUTH_FAILED, -1, DBG, DBG, this, -1, new ParcelUuid(uuid));
    }

    public BluetoothSocket createInsecureRfcommSocket(int port) throws IOException {
        return new BluetoothSocket(UNBOND_REASON_AUTH_FAILED, -1, DBG, DBG, this, port, null);
    }

    public BluetoothSocket createScoSocket() throws IOException {
        return new BluetoothSocket(UNBOND_REASON_AUTH_REJECTED, -1, true, true, this, -1, null);
    }

    public static byte[] convertPinToBytes(String pin) {
        if (pin == null) {
            return null;
        }
        try {
            byte[] pinBytes = pin.getBytes("UTF-8");
            if (pinBytes.length <= 0 || pinBytes.length > 16) {
                return null;
            }
            return pinBytes;
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UTF-8 not supported?!?");
            return null;
        }
    }

    public BluetoothGatt connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback) {
        return connectGatt(context, autoConnect, callback, TRANSPORT_AUTO);
    }

    public BluetoothGatt connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback, int transport) {
        try {
            IBluetoothGatt iGatt = BluetoothAdapter.getDefaultAdapter().getBluetoothManager().getBluetoothGatt();
            if (iGatt == null) {
                return null;
            }
            BluetoothGatt gatt = new BluetoothGatt(context, iGatt, this, transport);
            gatt.connect(Boolean.valueOf(autoConnect), callback);
            return gatt;
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return null;
        }
    }
}
