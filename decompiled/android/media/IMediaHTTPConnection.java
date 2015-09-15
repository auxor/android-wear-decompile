package android.media;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

public interface IMediaHTTPConnection extends IInterface {
    IBinder connect(String str, String str2) throws RemoteException;

    void disconnect() throws RemoteException;

    String getMIMEType() throws RemoteException;

    long getSize() throws RemoteException;

    String getUri() throws RemoteException;

    int readAt(long j, int i) throws RemoteException;
}
