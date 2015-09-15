package com.android.internal.os;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.telephony.PhoneConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.khronos.opengles.GL10;
import libcore.io.IoUtils;
import libcore.io.Streams;

public class InstallerConnection {
    private static final boolean LOCAL_DEBUG = false;
    private static final String TAG = "InstallerConnection";
    private final byte[] buf;
    private InputStream mIn;
    private OutputStream mOut;
    private LocalSocket mSocket;

    public InstallerConnection() {
        this.buf = new byte[GL10.GL_STENCIL_BUFFER_BIT];
    }

    public synchronized String transact(String cmd) {
        String str;
        if (connect()) {
            if (!writeCommand(cmd)) {
                Slog.e(TAG, "write command failed? reconnect!");
                if (!(connect() && writeCommand(cmd))) {
                    str = "-1";
                }
            }
            int replyLength = readReply();
            if (replyLength > 0) {
                str = new String(this.buf, 0, replyLength);
            } else {
                str = "-1";
            }
        } else {
            Slog.e(TAG, "connection failed");
            str = "-1";
        }
        return str;
    }

    public int execute(String cmd) {
        try {
            return Integer.parseInt(transact(cmd));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public int dexopt(String apkPath, int uid, boolean isPublic, String instructionSet) {
        return dexopt(apkPath, uid, isPublic, PhoneConstants.APN_TYPE_ALL, instructionSet, LOCAL_DEBUG);
    }

    public int dexopt(String apkPath, int uid, boolean isPublic, String pkgName, String instructionSet, boolean vmSafeMode) {
        StringBuilder builder = new StringBuilder("dexopt");
        builder.append(' ');
        builder.append(apkPath);
        builder.append(' ');
        builder.append(uid);
        builder.append(isPublic ? " 1" : " 0");
        builder.append(' ');
        builder.append(pkgName);
        builder.append(' ');
        builder.append(instructionSet);
        builder.append(' ');
        builder.append(vmSafeMode ? " 1" : " 0");
        return execute(builder.toString());
    }

    public int patchoat(String apkPath, int uid, boolean isPublic, String instructionSet) {
        return patchoat(apkPath, uid, isPublic, PhoneConstants.APN_TYPE_ALL, instructionSet);
    }

    public int patchoat(String apkPath, int uid, boolean isPublic, String pkgName, String instructionSet) {
        StringBuilder builder = new StringBuilder("patchoat");
        builder.append(' ');
        builder.append(apkPath);
        builder.append(' ');
        builder.append(uid);
        builder.append(isPublic ? " 1" : " 0");
        builder.append(' ');
        builder.append(pkgName);
        builder.append(' ');
        builder.append(instructionSet);
        return execute(builder.toString());
    }

    private boolean connect() {
        if (this.mSocket != null) {
            return true;
        }
        Slog.i(TAG, "connecting...");
        try {
            this.mSocket = new LocalSocket();
            this.mSocket.connect(new LocalSocketAddress("installd", Namespace.RESERVED));
            this.mIn = this.mSocket.getInputStream();
            this.mOut = this.mSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            disconnect();
            return LOCAL_DEBUG;
        }
    }

    public void disconnect() {
        Slog.i(TAG, "disconnecting...");
        IoUtils.closeQuietly(this.mSocket);
        IoUtils.closeQuietly(this.mIn);
        IoUtils.closeQuietly(this.mOut);
        this.mSocket = null;
        this.mIn = null;
        this.mOut = null;
    }

    private boolean readFully(byte[] buffer, int len) {
        try {
            Streams.readFully(this.mIn, buffer, 0, len);
            return true;
        } catch (IOException e) {
            Slog.e(TAG, "read exception");
            disconnect();
            return LOCAL_DEBUG;
        }
    }

    private int readReply() {
        if (!readFully(this.buf, 2)) {
            return -1;
        }
        int len = (this.buf[0] & R.styleable.Theme_windowSharedElementReturnTransition) | ((this.buf[1] & R.styleable.Theme_windowSharedElementReturnTransition) << 8);
        if (len < 1 || len > this.buf.length) {
            Slog.e(TAG, "invalid reply length (" + len + ")");
            disconnect();
            return -1;
        } else if (readFully(this.buf, len)) {
            return len;
        } else {
            return -1;
        }
    }

    private boolean writeCommand(String cmdString) {
        byte[] cmd = cmdString.getBytes();
        int len = cmd.length;
        if (len < 1 || len > this.buf.length) {
            return LOCAL_DEBUG;
        }
        this.buf[0] = (byte) (len & R.styleable.Theme_windowSharedElementReturnTransition);
        this.buf[1] = (byte) ((len >> 8) & R.styleable.Theme_windowSharedElementReturnTransition);
        try {
            this.mOut.write(this.buf, 0, 2);
            this.mOut.write(cmd, 0, len);
            return true;
        } catch (IOException e) {
            Slog.e(TAG, "write error");
            disconnect();
            return LOCAL_DEBUG;
        }
    }
}
