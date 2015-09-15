package android.bluetooth;

import android.os.Handler;
import android.os.ParcelUuid;
import java.io.Closeable;
import java.io.IOException;

public final class BluetoothServerSocket implements Closeable {
    private final int mChannel;
    private Handler mHandler;
    private int mMessage;
    final BluetoothSocket mSocket;

    BluetoothServerSocket(int type, boolean auth, boolean encrypt, int port) throws IOException {
        this.mChannel = port;
        this.mSocket = new BluetoothSocket(type, -1, auth, encrypt, null, port, null);
    }

    BluetoothServerSocket(int type, boolean auth, boolean encrypt, ParcelUuid uuid) throws IOException {
        this.mSocket = new BluetoothSocket(type, -1, auth, encrypt, null, -1, uuid);
        this.mChannel = this.mSocket.getPort();
    }

    public BluetoothSocket accept() throws IOException {
        return accept(-1);
    }

    public BluetoothSocket accept(int timeout) throws IOException {
        return this.mSocket.accept(timeout);
    }

    public void close() throws IOException {
        synchronized (this) {
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(this.mMessage).sendToTarget();
            }
        }
        this.mSocket.close();
    }

    synchronized void setCloseHandler(Handler handler, int message) {
        this.mHandler = handler;
        this.mMessage = message;
    }

    void setServiceName(String ServiceName) {
        this.mSocket.setServiceName(ServiceName);
    }

    public int getChannel() {
        return this.mChannel;
    }
}
