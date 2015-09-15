package android.os;

import android.os.IPermissionController.Stub;
import android.widget.Toast;

public abstract class ServiceManagerNative extends Binder implements IServiceManager {
    public static IServiceManager asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IServiceManager in = (IServiceManager) obj.queryLocalInterface(IServiceManager.descriptor);
        return in == null ? new ServiceManagerProxy(obj) : in;
    }

    public ServiceManagerNative() {
        attachInterface(this, IServiceManager.descriptor);
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
        switch (code) {
            case Toast.LENGTH_LONG /*1*/:
                try {
                    data.enforceInterface(IServiceManager.descriptor);
                    reply.writeStrongBinder(getService(data.readString()));
                    return true;
                } catch (RemoteException e) {
                    break;
                }
            case Action.MERGE_IGNORE /*2*/:
                data.enforceInterface(IServiceManager.descriptor);
                reply.writeStrongBinder(checkService(data.readString()));
                return true;
            case SetDrawableParameters.TAG /*3*/:
                boolean allowIsolated;
                data.enforceInterface(IServiceManager.descriptor);
                String name = data.readString();
                IBinder service = data.readStrongBinder();
                if (data.readInt() != 0) {
                    allowIsolated = true;
                } else {
                    allowIsolated = false;
                }
                addService(name, service, allowIsolated);
                return true;
            case ViewGroupAction.TAG /*4*/:
                data.enforceInterface(IServiceManager.descriptor);
                reply.writeStringArray(listServices());
                return true;
            case SetEmptyView.TAG /*6*/:
                data.enforceInterface(IServiceManager.descriptor);
                setPermissionController(Stub.asInterface(data.readStrongBinder()));
                return true;
        }
        return false;
    }

    public IBinder asBinder() {
        return this;
    }
}
