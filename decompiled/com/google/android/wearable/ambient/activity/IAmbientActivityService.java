package com.google.android.wearable.ambient.activity;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAmbientActivityService extends IInterface {

    public static abstract class Stub extends Binder implements IAmbientActivityService {
        private static final String DESCRIPTOR = "com.google.android.wearable.ambient.activity.IAmbientActivityService";
        static final int TRANSACTION_detach = 8;
        static final int TRANSACTION_onAmbientEntered = 5;
        static final int TRANSACTION_onAmbientExited = 7;
        static final int TRANSACTION_onAmbientUpdated = 6;
        static final int TRANSACTION_onPaused = 3;
        static final int TRANSACTION_onResumed = 2;
        static final int TRANSACTION_onStopped = 4;
        static final int TRANSACTION_setConfiguration = 1;

        private static class Proxy implements IAmbientActivityService {
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

            public void setConfiguration(Bundle b) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (b != null) {
                        _data.writeInt(Stub.TRANSACTION_setConfiguration);
                        b.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setConfiguration, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onResumed() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onResumed, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onPaused() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onPaused, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onStopped() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onStopped, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onAmbientEntered() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onAmbientEntered, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onAmbientUpdated() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onAmbientUpdated, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onAmbientExited() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onAmbientExited, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void detach() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_detach, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAmbientActivityService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IAmbientActivityService)) {
                return new Proxy(obj);
            }
            return (IAmbientActivityService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_setConfiguration /*1*/:
                    Bundle _arg0;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    setConfiguration(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onResumed /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    onResumed();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onPaused /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    onPaused();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onStopped /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    onStopped();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onAmbientEntered /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    onAmbientEntered();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onAmbientUpdated /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    onAmbientUpdated();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_onAmbientExited /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    onAmbientExited();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_detach /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    detach();
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void detach() throws RemoteException;

    void onAmbientEntered() throws RemoteException;

    void onAmbientExited() throws RemoteException;

    void onAmbientUpdated() throws RemoteException;

    void onPaused() throws RemoteException;

    void onResumed() throws RemoteException;

    void onStopped() throws RemoteException;

    void setConfiguration(Bundle bundle) throws RemoteException;
}
