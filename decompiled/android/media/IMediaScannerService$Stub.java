package android.media;

import android.media.IMediaScannerListener.Stub;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class IMediaScannerService$Stub extends Binder implements IMediaScannerService {
    private static final String DESCRIPTOR = "android.media.IMediaScannerService";
    static final int TRANSACTION_requestScanFile = 1;
    static final int TRANSACTION_scanFile = 2;

    public IMediaScannerService$Stub() {
        attachInterface(this, DESCRIPTOR);
    }

    public static IMediaScannerService asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if (iin == null || !(iin instanceof IMediaScannerService)) {
            return new Proxy(obj);
        }
        return (IMediaScannerService) iin;
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case TRANSACTION_requestScanFile /*1*/:
                data.enforceInterface(DESCRIPTOR);
                requestScanFile(data.readString(), data.readString(), Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case TRANSACTION_scanFile /*2*/:
                data.enforceInterface(DESCRIPTOR);
                scanFile(data.readString(), data.readString());
                reply.writeNoException();
                return true;
            case IBinder.INTERFACE_TRANSACTION /*1598968902*/:
                reply.writeString(DESCRIPTOR);
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }
}
