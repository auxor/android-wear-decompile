package com.google.android.wearable.ambient.activity;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAmbientActivityRegistrationService extends IInterface {

    public static abstract class Stub extends Binder implements IAmbientActivityRegistrationService {
        private static final String DESCRIPTOR = "com.google.android.wearable.ambient.activity.IAmbientActivityRegistrationService";
        static final int TRANSACTION_attach = 1;

        private static class Proxy implements IAmbientActivityRegistrationService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public IAmbientActivityService attach(Bundle config, IAmbientActivityCallbacks callbacks) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (config != null) {
                        _data.writeInt(Stub.TRANSACTION_attach);
                        config.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callbacks != null ? callbacks.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_attach, _data, _reply, 0);
                    _reply.readException();
                    IAmbientActivityService _result = com.google.android.wearable.ambient.activity.IAmbientActivityService.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAmbientActivityRegistrationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IAmbientActivityRegistrationService)) {
                return new Proxy(obj);
            }
            return (IAmbientActivityRegistrationService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_attach /*1*/:
                    Bundle _arg0;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    IAmbientActivityService _result = attach(_arg0, com.google.android.wearable.ambient.activity.IAmbientActivityCallbacks.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeStrongBinder(_result != null ? _result.asBinder() : null);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    IAmbientActivityService attach(Bundle bundle, IAmbientActivityCallbacks iAmbientActivityCallbacks) throws RemoteException;
}
