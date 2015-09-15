package android.media;

import android.os.IInterface;
import android.os.RemoteException;

public interface IRemoteVolumeObserver extends IInterface {
    void dispatchRemoteVolumeUpdate(int i, int i2) throws RemoteException;
}
