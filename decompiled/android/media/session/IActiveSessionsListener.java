package android.media.session;

import android.media.session.MediaSession.Token;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface IActiveSessionsListener extends IInterface {

    public static abstract class Stub extends Binder implements IActiveSessionsListener {
        private static final String DESCRIPTOR = "android.media.session.IActiveSessionsListener";
        static final int TRANSACTION_onActiveSessionsChanged = 1;

        private static class Proxy implements IActiveSessionsListener {
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

            public void onActiveSessionsChanged(List<Token> sessions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(sessions);
                    this.mRemote.transact(Stub.TRANSACTION_onActiveSessionsChanged, _data, null, Stub.TRANSACTION_onActiveSessionsChanged);
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IActiveSessionsListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IActiveSessionsListener)) {
                return new Proxy(obj);
            }
            return (IActiveSessionsListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_onActiveSessionsChanged /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    onActiveSessionsChanged(data.createTypedArrayList(Token.CREATOR));
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void onActiveSessionsChanged(List<Token> list) throws RemoteException;
}
