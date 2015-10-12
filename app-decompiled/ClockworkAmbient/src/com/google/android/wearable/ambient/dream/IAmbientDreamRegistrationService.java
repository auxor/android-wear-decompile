package com.google.android.wearable.ambient.dream;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAmbientDreamRegistrationService extends IInterface {

    public static abstract class Stub extends Binder implements IAmbientDreamRegistrationService {

        private static class Proxy implements IAmbientDreamRegistrationService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public IAmbientDreamService attach(Bundle bundle, IAmbientDreamCallbacks iAmbientDreamCallbacks) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.android.wearable.ambient.dream.IAmbientDreamRegistrationService");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iAmbientDreamCallbacks != null ? iAmbientDreamCallbacks.asBinder() : null);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    IAmbientDreamService asInterface = com.google.android.wearable.ambient.dream.IAmbientDreamService.Stub.asInterface(obtain2.readStrongBinder());
                    return asInterface;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "com.google.android.wearable.ambient.dream.IAmbientDreamRegistrationService");
        }

        public static IAmbientDreamRegistrationService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.wearable.ambient.dream.IAmbientDreamRegistrationService");
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IAmbientDreamRegistrationService)) ? new Proxy(iBinder) : (IAmbientDreamRegistrationService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    throw new VerifyError("bad dex opcode");
                case 1598968902:
                    throw new VerifyError("bad dex opcode");
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    IAmbientDreamService attach(Bundle bundle, IAmbientDreamCallbacks iAmbientDreamCallbacks) throws RemoteException;
}
