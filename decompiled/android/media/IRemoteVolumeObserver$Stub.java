package android.media;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class IRemoteVolumeObserver$Stub extends Binder implements IRemoteVolumeObserver {
    private static final String DESCRIPTOR = "android.media.IRemoteVolumeObserver";
    static final int TRANSACTION_dispatchRemoteVolumeUpdate = 1;

    public IRemoteVolumeObserver$Stub() {
        attachInterface(this, DESCRIPTOR);
    }

    public static IRemoteVolumeObserver asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if (iin == null || !(iin instanceof IRemoteVolumeObserver)) {
            return new Proxy(obj);
        }
        return (IRemoteVolumeObserver) iin;
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case TRANSACTION_dispatchRemoteVolumeUpdate /*1*/:
                data.enforceInterface(DESCRIPTOR);
                dispatchRemoteVolumeUpdate(data.readInt(), data.readInt());
                return true;
            case IBinder.INTERFACE_TRANSACTION /*1598968902*/:
                reply.writeString(DESCRIPTOR);
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }
}
