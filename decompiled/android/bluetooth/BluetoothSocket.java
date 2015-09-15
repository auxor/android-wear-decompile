package android.bluetooth;

import android.net.LocalSocket;
import android.os.ParcelFileDescriptor;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.UUID;

public final class BluetoothSocket implements Closeable {
    private static final boolean DBG;
    static final int EADDRINUSE = 98;
    static final int EBADFD = 77;
    public static final int MAX_RFCOMM_CHANNEL = 30;
    private static int PROXY_CONNECTION_TIMEOUT = 0;
    static final int SEC_FLAG_AUTH = 2;
    static final int SEC_FLAG_ENCRYPT = 1;
    private static int SOCK_SIGNAL_SIZE = 0;
    private static final String TAG = "BluetoothSocket";
    static final int TYPE_L2CAP = 3;
    static final int TYPE_RFCOMM = 1;
    static final int TYPE_SCO = 2;
    private static final boolean VDBG;
    private String mAddress;
    private final boolean mAuth;
    private BluetoothDevice mDevice;
    private final boolean mEncrypt;
    private int mFd;
    private final BluetoothInputStream mInputStream;
    private final BluetoothOutputStream mOutputStream;
    private ParcelFileDescriptor mPfd;
    private int mPort;
    private String mServiceName;
    private LocalSocket mSocket;
    private InputStream mSocketIS;
    private OutputStream mSocketOS;
    private volatile SocketState mSocketState;
    private final int mType;
    private final ParcelUuid mUuid;

    private enum SocketState {
        INIT,
        CONNECTED,
        LISTENING,
        CLOSED
    }

    static {
        DBG = Log.isLoggable(TAG, TYPE_L2CAP);
        VDBG = Log.isLoggable(TAG, TYPE_SCO);
        PROXY_CONNECTION_TIMEOUT = BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED;
        SOCK_SIGNAL_SIZE = 16;
    }

    BluetoothSocket(int type, int fd, boolean auth, boolean encrypt, BluetoothDevice device, int port, ParcelUuid uuid) throws IOException {
        if (type == TYPE_RFCOMM && uuid == null && fd == -1 && (port < TYPE_RFCOMM || port > MAX_RFCOMM_CHANNEL)) {
            throw new IOException("Invalid RFCOMM channel: " + port);
        }
        if (uuid != null) {
            this.mUuid = uuid;
        } else {
            this.mUuid = new ParcelUuid(new UUID(0, 0));
        }
        this.mType = type;
        this.mAuth = auth;
        this.mEncrypt = encrypt;
        this.mDevice = device;
        this.mPort = port;
        this.mFd = fd;
        this.mSocketState = SocketState.INIT;
        if (device == null) {
            this.mAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
        } else {
            this.mAddress = device.getAddress();
        }
        this.mInputStream = new BluetoothInputStream(this);
        this.mOutputStream = new BluetoothOutputStream(this);
    }

    private BluetoothSocket(BluetoothSocket s) {
        this.mUuid = s.mUuid;
        this.mType = s.mType;
        this.mAuth = s.mAuth;
        this.mEncrypt = s.mEncrypt;
        this.mPort = s.mPort;
        this.mInputStream = new BluetoothInputStream(this);
        this.mOutputStream = new BluetoothOutputStream(this);
        this.mServiceName = s.mServiceName;
    }

    private BluetoothSocket acceptSocket(String RemoteAddr) throws IOException {
        BluetoothSocket as = new BluetoothSocket(this);
        as.mSocketState = SocketState.CONNECTED;
        FileDescriptor[] fds = this.mSocket.getAncillaryFileDescriptors();
        if (DBG) {
            Log.d(TAG, "socket fd passed by stack  fds: " + fds);
        }
        if (fds == null || fds.length != TYPE_RFCOMM) {
            Log.e(TAG, "socket fd passed from stack failed, fds: " + fds);
            as.close();
            throw new IOException("bt socket acept failed");
        }
        as.mSocket = new LocalSocket(fds[0]);
        as.mSocketIS = as.mSocket.getInputStream();
        as.mSocketOS = as.mSocket.getOutputStream();
        as.mAddress = RemoteAddr;
        as.mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(RemoteAddr);
        return as;
    }

    private BluetoothSocket(int type, int fd, boolean auth, boolean encrypt, String address, int port) throws IOException {
        this(type, fd, auth, encrypt, new BluetoothDevice(address), port, null);
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private int getSecurityFlags() {
        int flags = 0;
        if (this.mAuth) {
            flags = 0 | TYPE_SCO;
        }
        if (this.mEncrypt) {
            return flags | TYPE_RFCOMM;
        }
        return flags;
    }

    public BluetoothDevice getRemoteDevice() {
        return this.mDevice;
    }

    public InputStream getInputStream() throws IOException {
        return this.mInputStream;
    }

    public OutputStream getOutputStream() throws IOException {
        return this.mOutputStream;
    }

    public boolean isConnected() {
        return this.mSocketState == SocketState.CONNECTED ? true : DBG;
    }

    void setServiceName(String name) {
        this.mServiceName = name;
    }

    public void connect() throws IOException {
        if (this.mDevice == null) {
            throw new IOException("Connect is called on null device");
        }
        try {
            if (this.mSocketState == SocketState.CLOSED) {
                throw new IOException("socket closed");
            }
            IBluetooth bluetoothProxy = BluetoothAdapter.getDefaultAdapter().getBluetoothService(null);
            if (bluetoothProxy == null) {
                throw new IOException("Bluetooth is off");
            }
            this.mPfd = bluetoothProxy.connectSocket(this.mDevice, this.mType, this.mUuid, this.mPort, getSecurityFlags());
            synchronized (this) {
                if (DBG) {
                    Log.d(TAG, "connect(), SocketState: " + this.mSocketState + ", mPfd: " + this.mPfd);
                }
                if (this.mSocketState == SocketState.CLOSED) {
                    throw new IOException("socket closed");
                } else if (this.mPfd == null) {
                    throw new IOException("bt socket connect failed");
                } else {
                    this.mSocket = new LocalSocket(this.mPfd.getFileDescriptor());
                    this.mSocketIS = this.mSocket.getInputStream();
                    this.mSocketOS = this.mSocket.getOutputStream();
                }
            }
            int channel = readInt(this.mSocketIS);
            if (channel <= 0) {
                throw new IOException("bt socket connect failed");
            }
            this.mPort = channel;
            waitSocketSignal(this.mSocketIS);
            synchronized (this) {
                if (this.mSocketState == SocketState.CLOSED) {
                    throw new IOException("bt socket closed");
                }
                this.mSocketState = SocketState.CONNECTED;
            }
        } catch (RemoteException e) {
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
            throw new IOException("unable to send RPC: " + e.getMessage());
        }
    }

    int bindListen() {
        if (this.mSocketState == SocketState.CLOSED) {
            return EBADFD;
        }
        IBluetooth bluetoothProxy = BluetoothAdapter.getDefaultAdapter().getBluetoothService(null);
        if (bluetoothProxy == null) {
            Log.e(TAG, "bindListen fail, reason: bluetooth is off");
            return -1;
        }
        try {
            this.mPfd = bluetoothProxy.createSocketChannel(this.mType, this.mServiceName, this.mUuid, this.mPort, getSecurityFlags());
            try {
                synchronized (this) {
                    if (DBG) {
                        Log.d(TAG, "bindListen(), SocketState: " + this.mSocketState + ", mPfd: " + this.mPfd);
                    }
                    if (this.mSocketState != SocketState.INIT) {
                        return EBADFD;
                    } else if (this.mPfd == null) {
                        return -1;
                    } else {
                        FileDescriptor fd = this.mPfd.getFileDescriptor();
                        if (DBG) {
                            Log.d(TAG, "bindListen(), new LocalSocket ");
                        }
                        this.mSocket = new LocalSocket(fd);
                        if (DBG) {
                            Log.d(TAG, "bindListen(), new LocalSocket.getInputStream() ");
                        }
                        this.mSocketIS = this.mSocket.getInputStream();
                        this.mSocketOS = this.mSocket.getOutputStream();
                        if (DBG) {
                            Log.d(TAG, "bindListen(), readInt mSocketIS: " + this.mSocketIS);
                        }
                        int channel = readInt(this.mSocketIS);
                        synchronized (this) {
                            if (this.mSocketState == SocketState.INIT) {
                                this.mSocketState = SocketState.LISTENING;
                            }
                        }
                        if (DBG) {
                            Log.d(TAG, "channel: " + channel);
                        }
                        if (this.mPort == -1) {
                            this.mPort = channel;
                        }
                        return 0;
                    }
                }
            } catch (IOException e) {
                if (this.mPfd != null) {
                    try {
                        this.mPfd.close();
                    } catch (IOException e1) {
                        Log.e(TAG, "bindListen, close mPfd: " + e1);
                    }
                    this.mPfd = null;
                }
                Log.e(TAG, "bindListen, fail to get port number, exception: " + e);
                return -1;
            }
        } catch (RemoteException e2) {
            Log.e(TAG, Log.getStackTraceString(new Throwable()));
            return -1;
        }
    }

    BluetoothSocket accept(int timeout) throws IOException {
        if (this.mSocketState != SocketState.LISTENING) {
            throw new IOException("bt socket is not in listen state");
        }
        BluetoothSocket acceptedSocket;
        if (timeout > 0) {
            Log.d(TAG, "accept() set timeout (ms):" + timeout);
            this.mSocket.setSoTimeout(timeout);
        }
        String RemoteAddr = waitSocketSignal(this.mSocketIS);
        if (timeout > 0) {
            this.mSocket.setSoTimeout(0);
        }
        synchronized (this) {
            if (this.mSocketState != SocketState.LISTENING) {
                throw new IOException("bt socket is not in listen state");
            }
            acceptedSocket = acceptSocket(RemoteAddr);
        }
        return acceptedSocket;
    }

    int available() throws IOException {
        if (VDBG) {
            Log.d(TAG, "available: " + this.mSocketIS);
        }
        return this.mSocketIS.available();
    }

    void flush() throws IOException {
        if (this.mSocketOS == null) {
            throw new IOException("flush is called on null OutputStream");
        }
        if (VDBG) {
            Log.d(TAG, "flush: " + this.mSocketOS);
        }
        this.mSocketOS.flush();
    }

    int read(byte[] b, int offset, int length) throws IOException {
        if (this.mSocketIS == null) {
            throw new IOException("read is called on null InputStream");
        }
        if (VDBG) {
            Log.d(TAG, "read in:  " + this.mSocketIS + " len: " + length);
        }
        int ret = this.mSocketIS.read(b, offset, length);
        if (ret < 0) {
            throw new IOException("bt socket closed, read return: " + ret);
        }
        if (VDBG) {
            Log.d(TAG, "read out:  " + this.mSocketIS + " ret: " + ret);
        }
        return ret;
    }

    int write(byte[] b, int offset, int length) throws IOException {
        if (this.mSocketOS == null) {
            throw new IOException("write is called on null OutputStream");
        }
        if (VDBG) {
            Log.d(TAG, "write: " + this.mSocketOS + " length: " + length);
        }
        this.mSocketOS.write(b, offset, length);
        if (VDBG) {
            Log.d(TAG, "write out: " + this.mSocketOS + " length: " + length);
        }
        return length;
    }

    public void close() throws IOException {
        if (DBG) {
            Log.d(TAG, "close() in, this: " + this + ", channel: " + this.mPort + ", state: " + this.mSocketState);
        }
        if (this.mSocketState != SocketState.CLOSED) {
            synchronized (this) {
                if (this.mSocketState == SocketState.CLOSED) {
                    return;
                }
                this.mSocketState = SocketState.CLOSED;
                if (DBG) {
                    Log.d(TAG, "close() this: " + this + ", channel: " + this.mPort + ", mSocketIS: " + this.mSocketIS + ", mSocketOS: " + this.mSocketOS + "mSocket: " + this.mSocket);
                }
                if (this.mSocket != null) {
                    if (DBG) {
                        Log.d(TAG, "Closing mSocket: " + this.mSocket);
                    }
                    this.mSocket.shutdownInput();
                    this.mSocket.shutdownOutput();
                    this.mSocket.close();
                    this.mSocket = null;
                }
                if (this.mPfd != null) {
                    this.mPfd.close();
                    this.mPfd = null;
                }
            }
        }
    }

    void removeChannel() {
    }

    int getPort() {
        return this.mPort;
    }

    private String convertAddr(byte[] addr) {
        return String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", new Object[]{Byte.valueOf(addr[0]), Byte.valueOf(addr[TYPE_RFCOMM]), Byte.valueOf(addr[TYPE_SCO]), Byte.valueOf(addr[TYPE_L2CAP]), Byte.valueOf(addr[4]), Byte.valueOf(addr[5])});
    }

    private String waitSocketSignal(InputStream is) throws IOException {
        byte[] sig = new byte[SOCK_SIGNAL_SIZE];
        int ret = readAll(is, sig);
        if (VDBG) {
            Log.d(TAG, "waitSocketSignal read 16 bytes signal ret: " + ret);
        }
        ByteBuffer bb = ByteBuffer.wrap(sig);
        bb.order(ByteOrder.nativeOrder());
        int size = bb.getShort();
        if (size != SOCK_SIGNAL_SIZE) {
            throw new IOException("Connection failure, wrong signal size: " + size);
        }
        byte[] addr = new byte[6];
        bb.get(addr);
        int channel = bb.getInt();
        int status = bb.getInt();
        String RemoteAddr = convertAddr(addr);
        if (VDBG) {
            Log.d(TAG, "waitSocketSignal: sig size: " + size + ", remote addr: " + RemoteAddr + ", channel: " + channel + ", status: " + status);
        }
        if (status == 0) {
            return RemoteAddr;
        }
        throw new IOException("Connection failure, status: " + status);
    }

    private int readAll(InputStream is, byte[] b) throws IOException {
        int left = b.length;
        while (left > 0) {
            int ret = is.read(b, b.length - left, left);
            if (ret <= 0) {
                throw new IOException("read failed, socket might closed or timeout, read ret: " + ret);
            }
            left -= ret;
            if (left != 0) {
                Log.w(TAG, "readAll() looping, read partial size: " + (b.length - left) + ", expect size: " + b.length);
            }
        }
        return b.length;
    }

    private int readInt(InputStream is) throws IOException {
        byte[] ibytes = new byte[4];
        int ret = readAll(is, ibytes);
        if (VDBG) {
            Log.d(TAG, "inputStream.read ret: " + ret);
        }
        ByteBuffer bb = ByteBuffer.wrap(ibytes);
        bb.order(ByteOrder.nativeOrder());
        return bb.getInt();
    }
}
