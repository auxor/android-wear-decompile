package com.google.android.wearable.ambient.dream;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAmbientDreamCallbacks extends IInterface {

    public static abstract class Stub extends Binder implements IAmbientDreamCallbacks {

        private static class Proxy implements IAmbientDreamCallbacks {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void doze() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks");
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void exitAmbient() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks");
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks");
        }

        public static IAmbientDreamCallbacks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IAmbientDreamCallbacks)) ? new Proxy(iBinder) : (IAmbientDreamCallbacks) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks");
                    doze();
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface("com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks");
                    exitAmbient();
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    void doze() throws RemoteException;

    void exitAmbient() throws RemoteException;
}
