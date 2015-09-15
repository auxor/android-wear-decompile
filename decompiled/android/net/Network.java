package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.system.ErrnoException;
import com.android.okhttp.ConnectionPool;
import com.android.okhttp.HostResolver;
import com.android.okhttp.HttpHandler;
import com.android.okhttp.HttpsHandler;
import com.android.okhttp.OkHttpClient;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import javax.net.SocketFactory;

public class Network implements Parcelable {
    public static final Creator<Network> CREATOR;
    private static final boolean httpKeepAlive;
    private static final long httpKeepAliveDurationMs;
    private static final int httpMaxConnections;
    private volatile ConnectionPool mConnectionPool;
    private volatile HostResolver mHostResolver;
    private Object mLock;
    private volatile NetworkBoundSocketFactory mNetworkBoundSocketFactory;
    public final int netId;

    private class NetworkBoundSocketFactory extends SocketFactory {
        private final int mNetId;

        public NetworkBoundSocketFactory(int netId) {
            this.mNetId = netId;
        }

        private Socket connectToHost(String host, int port, SocketAddress localAddress) throws IOException {
            InetAddress[] hostAddresses = Network.this.getAllByName(host);
            int i = 0;
            while (i < hostAddresses.length) {
                try {
                    Socket socket = createSocket();
                    if (localAddress != null) {
                        socket.bind(localAddress);
                    }
                    socket.connect(new InetSocketAddress(hostAddresses[i], port));
                    return socket;
                } catch (IOException e) {
                    if (i == hostAddresses.length - 1) {
                        throw e;
                    }
                    i++;
                }
            }
            throw new UnknownHostException(host);
        }

        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
            return connectToHost(host, port, new InetSocketAddress(localHost, localPort));
        }

        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            Socket socket = createSocket();
            socket.bind(new InetSocketAddress(localAddress, localPort));
            socket.connect(new InetSocketAddress(address, port));
            return socket;
        }

        public Socket createSocket(InetAddress host, int port) throws IOException {
            Socket socket = createSocket();
            socket.connect(new InetSocketAddress(host, port));
            return socket;
        }

        public Socket createSocket(String host, int port) throws IOException {
            return connectToHost(host, port, null);
        }

        public Socket createSocket() throws IOException {
            Socket socket = new Socket();
            Network.this.bindSocket(socket);
            return socket;
        }
    }

    static {
        httpKeepAlive = Boolean.parseBoolean(System.getProperty("http.keepAlive", "true"));
        httpMaxConnections = httpKeepAlive ? Integer.parseInt(System.getProperty("http.maxConnections", "5")) : 0;
        httpKeepAliveDurationMs = Long.parseLong(System.getProperty("http.keepAliveDuration", "300000"));
        CREATOR = new Creator<Network>() {
            public Network createFromParcel(Parcel in) {
                return new Network(in.readInt());
            }

            public Network[] newArray(int size) {
                return new Network[size];
            }
        };
    }

    public Network(int netId) {
        this.mNetworkBoundSocketFactory = null;
        this.mConnectionPool = null;
        this.mHostResolver = null;
        this.mLock = new Object();
        this.netId = netId;
    }

    public Network(Network that) {
        this.mNetworkBoundSocketFactory = null;
        this.mConnectionPool = null;
        this.mHostResolver = null;
        this.mLock = new Object();
        this.netId = that.netId;
    }

    public InetAddress[] getAllByName(String host) throws UnknownHostException {
        return InetAddress.getAllByNameOnNet(host, this.netId);
    }

    public InetAddress getByName(String host) throws UnknownHostException {
        return InetAddress.getByNameOnNet(host, this.netId);
    }

    public SocketFactory getSocketFactory() {
        if (this.mNetworkBoundSocketFactory == null) {
            synchronized (this.mLock) {
                if (this.mNetworkBoundSocketFactory == null) {
                    this.mNetworkBoundSocketFactory = new NetworkBoundSocketFactory(this.netId);
                }
            }
        }
        return this.mNetworkBoundSocketFactory;
    }

    private void maybeInitHttpClient() {
        synchronized (this.mLock) {
            if (this.mHostResolver == null) {
                this.mHostResolver = new HostResolver() {
                    public InetAddress[] getAllByName(String host) throws UnknownHostException {
                        return Network.this.getAllByName(host);
                    }
                };
            }
            if (this.mConnectionPool == null) {
                this.mConnectionPool = new ConnectionPool(httpMaxConnections, httpKeepAliveDurationMs);
            }
        }
    }

    public URLConnection openConnection(URL url) throws IOException {
        Proxy proxy;
        ConnectivityManager cm = ConnectivityManager.getInstance();
        ProxyInfo proxyInfo = cm.getGlobalProxy();
        if (proxyInfo == null) {
            LinkProperties lp = cm.getLinkProperties(this);
            if (lp != null) {
                proxyInfo = lp.getHttpProxy();
            }
        }
        if (proxyInfo != null) {
            proxy = proxyInfo.makeProxy();
        } else {
            proxy = Proxy.NO_PROXY;
        }
        return openConnection(url, proxy);
    }

    public URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy is null");
        }
        OkHttpClient client;
        maybeInitHttpClient();
        String protocol = url.getProtocol();
        if (protocol.equals("http")) {
            client = HttpHandler.createHttpOkHttpClient(proxy);
        } else if (protocol.equals("https")) {
            client = HttpsHandler.createHttpsOkHttpClient(proxy);
        } else {
            throw new MalformedURLException("Invalid URL or unrecognized protocol " + protocol);
        }
        return client.setSocketFactory(getSocketFactory()).setHostResolver(this.mHostResolver).setConnectionPool(this.mConnectionPool).open(url);
    }

    public void bindSocket(DatagramSocket socket) throws IOException {
        if (socket.isConnected()) {
            throw new SocketException("Socket is connected");
        }
        socket.getReuseAddress();
        bindSocketFd(socket.getFileDescriptor$());
    }

    public void bindSocket(Socket socket) throws IOException {
        if (socket.isConnected()) {
            throw new SocketException("Socket is connected");
        }
        socket.getReuseAddress();
        bindSocketFd(socket.getFileDescriptor$());
    }

    private void bindSocketFd(FileDescriptor fd) throws IOException {
        int err = NetworkUtils.bindSocketToNetwork(fd.getInt$(), this.netId);
        if (err != 0) {
            throw new ErrnoException("Binding socket to network " + this.netId, -err).rethrowAsSocketException();
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.netId);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Network)) {
            return false;
        }
        if (this.netId == ((Network) obj).netId) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.netId * 11;
    }

    public String toString() {
        return Integer.toString(this.netId);
    }
}
