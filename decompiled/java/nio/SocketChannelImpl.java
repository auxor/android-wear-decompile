package java.nio;

import android.system.ErrnoException;
import android.system.OsConstants;
import java.io.FileDescriptor;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PlainSocketImpl;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketUtils;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import libcore.io.IoBridge;
import libcore.io.IoUtils;
import libcore.io.Libcore;

class SocketChannelImpl extends SocketChannel implements FileDescriptorChannel {
    private static final int SOCKET_STATUS_CLOSED = 3;
    private static final int SOCKET_STATUS_CONNECTED = 2;
    private static final int SOCKET_STATUS_PENDING = 1;
    private static final int SOCKET_STATUS_UNCONNECTED = 0;
    private static final int SOCKET_STATUS_UNINITIALIZED = -1;
    private InetSocketAddress connectAddress;
    private final FileDescriptor fd;
    private volatile boolean isBound;
    private InetAddress localAddress;
    private int localPort;
    private final Object readLock;
    private SocketAdapter socket;
    private int status;
    private final Object writeLock;

    private static class BlockingCheckInputStream extends FilterInputStream {
        private final SocketChannel channel;

        public BlockingCheckInputStream(java.io.InputStream r1, java.nio.channels.SocketChannel r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.<init>(java.io.InputStream, java.nio.channels.SocketChannel):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.<init>(java.io.InputStream, java.nio.channels.SocketChannel):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckInputStream.<init>(java.io.InputStream, java.nio.channels.SocketChannel):void");
        }

        private void checkBlocking() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.checkBlocking():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.checkBlocking():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckInputStream.checkBlocking():void");
        }

        public void close() throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.close():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.close():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckInputStream.close():void");
        }

        public int read() throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.read():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.read():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckInputStream.read():int");
        }

        public int read(byte[] r1) throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.read(byte[]):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.read(byte[]):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckInputStream.read(byte[]):int");
        }

        public int read(byte[] r1, int r2, int r3) throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.read(byte[], int, int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckInputStream.read(byte[], int, int):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckInputStream.read(byte[], int, int):int");
        }
    }

    private static class BlockingCheckOutputStream extends FilterOutputStream {
        private final SocketChannel channel;

        public BlockingCheckOutputStream(java.io.OutputStream r1, java.nio.channels.SocketChannel r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.<init>(java.io.OutputStream, java.nio.channels.SocketChannel):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.<init>(java.io.OutputStream, java.nio.channels.SocketChannel):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckOutputStream.<init>(java.io.OutputStream, java.nio.channels.SocketChannel):void");
        }

        private void checkBlocking() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.checkBlocking():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.checkBlocking():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckOutputStream.checkBlocking():void");
        }

        public void close() throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.close():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.close():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckOutputStream.close():void");
        }

        public void write(int r1) throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(int):void");
        }

        public void write(byte[] r1) throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(byte[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(byte[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(byte[]):void");
        }

        public void write(byte[] r1, int r2, int r3) throws java.io.IOException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(byte[], int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(byte[], int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.nio.SocketChannelImpl.BlockingCheckOutputStream.write(byte[], int, int):void");
        }
    }

    private static class SocketAdapter extends Socket {
        private final SocketChannelImpl channel;
        private final PlainSocketImpl socketImpl;

        SocketAdapter(PlainSocketImpl socketImpl, SocketChannelImpl channel) throws SocketException {
            super((SocketImpl) socketImpl);
            this.socketImpl = socketImpl;
            this.channel = channel;
            SocketUtils.setCreated(this);
            if (channel.isBound) {
                onBind(channel.localAddress, channel.localPort);
            }
            if (channel.isConnected()) {
                onConnect(channel.connectAddress.getAddress(), channel.connectAddress.getPort());
            }
            if (!channel.isOpen()) {
                onClose();
            }
        }

        public SocketChannel getChannel() {
            return this.channel;
        }

        public void connect(SocketAddress remoteAddr, int timeout) throws IOException {
            if (!this.channel.isBlocking()) {
                throw new IllegalBlockingModeException();
            } else if (isConnected()) {
                throw new AlreadyConnectedException();
            } else {
                super.connect(remoteAddr, timeout);
                this.channel.onBind(false);
                if (super.isConnected()) {
                    this.channel.onConnectStatusChanged((InetSocketAddress) remoteAddr, SocketChannelImpl.SOCKET_STATUS_CONNECTED, false);
                }
            }
        }

        public void bind(SocketAddress localAddr) throws IOException {
            if (this.channel.isConnected()) {
                throw new AlreadyConnectedException();
            } else if (SocketChannelImpl.SOCKET_STATUS_PENDING == this.channel.status) {
                throw new ConnectionPendingException();
            } else {
                super.bind(localAddr);
                this.channel.onBind(false);
            }
        }

        public void close() throws IOException {
            synchronized (this.channel) {
                super.close();
                if (this.channel.isOpen()) {
                    this.channel.close();
                }
            }
        }

        public OutputStream getOutputStream() throws IOException {
            return new BlockingCheckOutputStream(super.getOutputStream(), this.channel);
        }

        public InputStream getInputStream() throws IOException {
            return new BlockingCheckInputStream(super.getInputStream(), this.channel);
        }

        public FileDescriptor getFileDescriptor$() {
            return this.socketImpl.getFD$();
        }
    }

    public SocketChannelImpl(SelectorProvider selectorProvider) throws IOException {
        this(selectorProvider, true);
    }

    public SocketChannelImpl(SelectorProvider selectorProvider, boolean connect) throws IOException {
        super(selectorProvider);
        this.socket = null;
        this.connectAddress = null;
        this.localAddress = null;
        this.status = SOCKET_STATUS_UNINITIALIZED;
        this.isBound = false;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.status = SOCKET_STATUS_UNCONNECTED;
        this.fd = connect ? IoBridge.socket(true) : new FileDescriptor();
    }

    public SocketChannelImpl(SelectorProvider selectorProvider, FileDescriptor existingFd) throws IOException {
        super(selectorProvider);
        this.socket = null;
        this.connectAddress = null;
        this.localAddress = null;
        this.status = SOCKET_STATUS_UNINITIALIZED;
        this.isBound = false;
        this.readLock = new Object();
        this.writeLock = new Object();
        this.status = SOCKET_STATUS_CONNECTED;
        this.fd = existingFd;
    }

    public synchronized Socket socket() {
        Socket socket;
        if (this.socket == null) {
            InetAddress addr = null;
            int port = SOCKET_STATUS_UNCONNECTED;
            try {
                if (this.connectAddress != null) {
                    addr = this.connectAddress.getAddress();
                    port = this.connectAddress.getPort();
                }
                this.socket = new SocketAdapter(new PlainSocketImpl(this.fd, this.localPort, addr, port), this);
            } catch (SocketException e) {
                socket = null;
            }
        }
        socket = this.socket;
        return socket;
    }

    void onBind(boolean updateSocketState) {
        try {
            SocketAddress sa = Libcore.os.getsockname(this.fd);
            this.isBound = true;
            InetSocketAddress localSocketAddress = (InetSocketAddress) sa;
            this.localAddress = localSocketAddress.getAddress();
            this.localPort = localSocketAddress.getPort();
            if (updateSocketState && this.socket != null) {
                this.socket.onBind(this.localAddress, this.localPort);
            }
        } catch (Object errnoException) {
            throw new AssertionError(errnoException);
        }
    }

    public synchronized boolean isConnected() {
        return this.status == SOCKET_STATUS_CONNECTED;
    }

    public synchronized boolean isConnectionPending() {
        boolean z = true;
        synchronized (this) {
            if (this.status != SOCKET_STATUS_PENDING) {
                z = false;
            }
        }
        return z;
    }

    public boolean connect(SocketAddress socketAddress) throws IOException {
        int newStatus;
        checkUnconnected();
        InetSocketAddress inetSocketAddress = validateAddress(socketAddress);
        InetAddress normalAddr = inetSocketAddress.getAddress();
        int port = inetSocketAddress.getPort();
        if (normalAddr.isAnyLocalAddress()) {
            normalAddr = InetAddress.getLocalHost();
        }
        boolean isBlocking = isBlocking();
        if (isBlocking) {
            try {
                begin();
            } catch (IOException e) {
                if (isEINPROGRESS(e)) {
                    newStatus = SOCKET_STATUS_PENDING;
                    if (isBlocking) {
                        end(false);
                    }
                } else {
                    if (isOpen()) {
                        close();
                    }
                    throw e;
                }
            } catch (Throwable th) {
                if (isBlocking) {
                    end(false);
                }
            }
        }
        IoBridge.connect(this.fd, normalAddr, port);
        if (isBlocking) {
            newStatus = SOCKET_STATUS_CONNECTED;
        } else {
            newStatus = SOCKET_STATUS_PENDING;
        }
        if (isBlocking) {
            end(true);
        }
        if (!this.isBound) {
            onBind(true);
        }
        onConnectStatusChanged(inetSocketAddress, newStatus, true);
        if (this.status == SOCKET_STATUS_CONNECTED) {
            return true;
        }
        return false;
    }

    void onConnectStatusChanged(InetSocketAddress address, int status, boolean updateSocketState) {
        this.status = status;
        this.connectAddress = address;
        if (status == SOCKET_STATUS_CONNECTED && updateSocketState && this.socket != null) {
            this.socket.onConnect(this.connectAddress.getAddress(), this.connectAddress.getPort());
        }
    }

    private boolean isEINPROGRESS(IOException e) {
        if (isBlocking() || !(e instanceof ConnectException)) {
            return false;
        }
        Throwable cause = e.getCause();
        if ((cause instanceof ErrnoException) && ((ErrnoException) cause).errno == OsConstants.EINPROGRESS) {
            return true;
        }
        return false;
    }

    public boolean finishConnect() throws IOException {
        int i = SOCKET_STATUS_CONNECTED;
        boolean z = true;
        synchronized (this) {
            if (!isOpen()) {
                throw new ClosedChannelException();
            } else if (this.status == SOCKET_STATUS_CONNECTED) {
            } else if (this.status != SOCKET_STATUS_PENDING) {
                throw new NoConnectionPendingException();
            } else {
                z = false;
                try {
                    begin();
                    z = IoBridge.isConnected(this.fd, this.connectAddress.getAddress(), this.connectAddress.getPort(), SOCKET_STATUS_UNCONNECTED, SOCKET_STATUS_UNCONNECTED);
                    end(z);
                    synchronized (this) {
                        if (!z) {
                            i = this.status;
                        }
                        this.status = i;
                        if (z && this.socket != null) {
                            this.socket.onConnect(this.connectAddress.getAddress(), this.connectAddress.getPort());
                        }
                    }
                } catch (ConnectException e) {
                    if (isOpen()) {
                        close();
                    }
                    throw e;
                } catch (Throwable th) {
                    end(z);
                }
            }
        }
        return z;
    }

    public int read(ByteBuffer dst) throws IOException {
        dst.checkWritable();
        checkOpenConnected();
        if (dst.hasRemaining()) {
            return readImpl(dst);
        }
        return SOCKET_STATUS_UNCONNECTED;
    }

    public long read(ByteBuffer[] targets, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(targets.length, offset, length);
        checkOpenConnected();
        int totalCount = FileChannelImpl.calculateTotalRemaining(targets, offset, length, true);
        if (totalCount == 0) {
            return 0;
        }
        byte[] readArray = new byte[totalCount];
        ByteBuffer readBuffer = ByteBuffer.wrap(readArray);
        int readCount = readImpl(readBuffer);
        readBuffer.flip();
        if (readCount > 0) {
            int left = readCount;
            int index = offset;
            while (left > 0) {
                int putLength = Math.min(targets[index].remaining(), left);
                targets[index].put(readArray, readCount - left, putLength);
                index += SOCKET_STATUS_PENDING;
                left -= putLength;
            }
        }
        return (long) readCount;
    }

    private int readImpl(ByteBuffer dst) throws IOException {
        int readCount;
        boolean z = true;
        synchronized (this.readLock) {
            try {
                if (isBlocking()) {
                    begin();
                }
                readCount = IoBridge.recvfrom(true, this.fd, dst, SOCKET_STATUS_UNCONNECTED, null, false);
                if (isBlocking()) {
                    if (readCount <= 0) {
                        z = false;
                    }
                    end(z);
                }
            } catch (Throwable th) {
                if (isBlocking()) {
                    boolean z2;
                    if (SOCKET_STATUS_UNCONNECTED > 0) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    end(z2);
                }
            }
        }
        return readCount;
    }

    public int write(ByteBuffer src) throws IOException {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        checkOpenConnected();
        if (src.hasRemaining()) {
            return writeImpl(src);
        }
        return SOCKET_STATUS_UNCONNECTED;
    }

    public long write(ByteBuffer[] sources, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(sources.length, offset, length);
        checkOpenConnected();
        int count = FileChannelImpl.calculateTotalRemaining(sources, offset, length, false);
        if (count == 0) {
            return 0;
        }
        int val;
        ByteBuffer writeBuf = ByteBuffer.allocate(count);
        for (val = offset; val < length + offset; val += SOCKET_STATUS_PENDING) {
            ByteBuffer source = sources[val];
            int oldPosition = source.position();
            writeBuf.put(source);
            source.position(oldPosition);
        }
        writeBuf.flip();
        int result = writeImpl(writeBuf);
        val = offset;
        int written = result;
        while (result > 0) {
            source = sources[val];
            int gap = Math.min(result, source.remaining());
            source.position(source.position() + gap);
            val += SOCKET_STATUS_PENDING;
            result -= gap;
        }
        return (long) written;
    }

    private int writeImpl(ByteBuffer src) throws IOException {
        boolean z = true;
        boolean z2 = false;
        synchronized (this.writeLock) {
            if (src.hasRemaining()) {
                try {
                    if (isBlocking()) {
                        begin();
                    }
                    int writeCount = IoBridge.sendto(this.fd, src, SOCKET_STATUS_UNCONNECTED, null, SOCKET_STATUS_UNCONNECTED);
                    if (isBlocking()) {
                        if (writeCount < 0) {
                            z = false;
                        }
                        end(z);
                    }
                    return writeCount;
                } catch (Throwable th) {
                    if (isBlocking()) {
                        if (SOCKET_STATUS_UNCONNECTED >= 0) {
                            z2 = true;
                        }
                        end(z2);
                    }
                }
            } else {
                return SOCKET_STATUS_UNCONNECTED;
            }
        }
    }

    private synchronized void checkOpenConnected() throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        } else if (!isConnected()) {
            throw new NotYetConnectedException();
        }
    }

    private synchronized void checkUnconnected() throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        } else if (this.status == SOCKET_STATUS_CONNECTED) {
            throw new AlreadyConnectedException();
        } else if (this.status == SOCKET_STATUS_PENDING) {
            throw new ConnectionPendingException();
        }
    }

    static InetSocketAddress validateAddress(SocketAddress socketAddress) {
        if (socketAddress == null) {
            throw new IllegalArgumentException("socketAddress == null");
        } else if (socketAddress instanceof InetSocketAddress) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
            if (!inetSocketAddress.isUnresolved()) {
                return inetSocketAddress;
            }
            throw new UnresolvedAddressException();
        } else {
            throw new UnsupportedAddressTypeException();
        }
    }

    protected synchronized void implCloseSelectableChannel() throws IOException {
        if (this.status != SOCKET_STATUS_CLOSED) {
            this.status = SOCKET_STATUS_CLOSED;
            IoBridge.closeAndSignalBlockedThreads(this.fd);
            if (!(this.socket == null || this.socket.isClosed())) {
                this.socket.onClose();
            }
        }
    }

    protected void implConfigureBlocking(boolean blocking) throws IOException {
        IoUtils.setBlocking(this.fd, blocking);
    }

    public FileDescriptor getFD() {
        return this.fd;
    }

    public void onAccept(InetSocketAddress remoteAddress, boolean updateSocketState) {
        onBind(updateSocketState);
        onConnectStatusChanged(remoteAddress, SOCKET_STATUS_CONNECTED, updateSocketState);
    }
}
