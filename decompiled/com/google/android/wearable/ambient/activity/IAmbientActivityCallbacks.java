package com.google.android.wearable.ambient.activity;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAmbientActivityCallbacks extends IInterface {

    public static abstract class Stub extends Binder implements IAmbientActivityCallbacks {
        private static final String DESCRIPTOR = "com.google.android.wearable.ambient.activity.IAmbientActivityCallbacks";
        static final int TRANSACTION_enterAmbient = 1;
        static final int TRANSACTION_exitAmbient = 3;
        static final int TRANSACTION_updateAmbient = 2;

        private static class Proxy implements IAmbientActivityCallbacks {
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

            public void enterAmbient(Bundle ambientDetails) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (ambientDetails != null) {
                        _data.writeInt(Stub.TRANSACTION_enterAmbient);
                        ambientDetails.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_enterAmbient, _data, null, Stub.TRANSACTION_enterAmbient);
                } finally {
                    _data.recycle();
                }
            }

            public void updateAmbient() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_updateAmbient, _data, null, Stub.TRANSACTION_enterAmbient);
                } finally {
                    _data.recycle();
                }
            }

            public void exitAmbient() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_exitAmbient, _data, null, Stub.TRANSACTION_enterAmbient);
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAmbientActivityCallbacks asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IAmbientActivityCallbacks)) {
                return new Proxy(obj);
            }
            return (IAmbientActivityCallbacks) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_enterAmbient /*1*/:
                    Bundle _arg0;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    enterAmbient(_arg0);
                    return true;
                case TRANSACTION_updateAmbient /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    updateAmbient();
                    return true;
                case TRANSACTION_exitAmbient /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    exitAmbient();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void enterAmbient(Bundle bundle) throws RemoteException;

    void exitAmbient() throws RemoteException;

    void updateAmbient() throws RemoteException;
}
