package android.media;

import android.os.IInterface;
import android.os.RemoteException;

public interface IMediaScannerService extends IInterface {
    void requestScanFile(String str, String str2, IMediaScannerListener iMediaScannerListener) throws RemoteException;

    void scanFile(String str, String str2) throws RemoteException;
}
