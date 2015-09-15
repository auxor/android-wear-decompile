package android.database;

import android.database.IContentObserver.Stub;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.widget.SpellChecker;
import android.widget.Toast;

public abstract class BulkCursorNative extends Binder implements IBulkCursor {
    public BulkCursorNative() {
        attachInterface(this, IBulkCursor.descriptor);
    }

    public static IBulkCursor asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IBulkCursor in = (IBulkCursor) obj.queryLocalInterface(IBulkCursor.descriptor);
        return in == null ? new BulkCursorProxy(obj) : in;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        switch (code) {
            case Toast.LENGTH_LONG /*1*/:
                try {
                    data.enforceInterface(IBulkCursor.descriptor);
                    CursorWindow window = getWindow(data.readInt());
                    reply.writeNoException();
                    if (window == null) {
                        reply.writeInt(0);
                        return true;
                    }
                    reply.writeInt(1);
                    window.writeToParcel(reply, 1);
                    return true;
                } catch (Exception e) {
                    DatabaseUtils.writeExceptionToParcel(reply, e);
                    return true;
                }
            case Action.MERGE_IGNORE /*2*/:
                data.enforceInterface(IBulkCursor.descriptor);
                deactivate();
                reply.writeNoException();
                return true;
            case SetDrawableParameters.TAG /*3*/:
                data.enforceInterface(IBulkCursor.descriptor);
                int count = requery(Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(count);
                reply.writeBundle(getExtras());
                return true;
            case ViewGroupAction.TAG /*4*/:
                data.enforceInterface(IBulkCursor.descriptor);
                onMove(data.readInt());
                reply.writeNoException();
                return true;
            case ReflectionActionWithoutParams.TAG /*5*/:
                data.enforceInterface(IBulkCursor.descriptor);
                Bundle extras = getExtras();
                reply.writeNoException();
                reply.writeBundle(extras);
                return true;
            case SetEmptyView.TAG /*6*/:
                data.enforceInterface(IBulkCursor.descriptor);
                Bundle returnExtras = respond(data.readBundle());
                reply.writeNoException();
                reply.writeBundle(returnExtras);
                return true;
            case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                data.enforceInterface(IBulkCursor.descriptor);
                close();
                reply.writeNoException();
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }

    public IBinder asBinder() {
        return this;
    }
}
