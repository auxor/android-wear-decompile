package java.lang;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

public class SecurityManager {
    @Deprecated
    protected boolean inCheck;

    public java.lang.ThreadGroup getThreadGroup() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.SecurityManager.getThreadGroup():java.lang.ThreadGroup
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.SecurityManager.getThreadGroup():java.lang.ThreadGroup
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.SecurityManager.getThreadGroup():java.lang.ThreadGroup");
    }

    public void checkAccept(String host, int port) {
    }

    public void checkAccess(Thread thread) {
    }

    public void checkAccess(ThreadGroup group) {
    }

    public void checkConnect(String host, int port) {
    }

    public void checkConnect(String host, int port, Object context) {
    }

    public void checkCreateClassLoader() {
    }

    public void checkDelete(String file) {
    }

    public void checkExec(String cmd) {
    }

    public void checkExit(int status) {
    }

    public void checkLink(String libName) {
    }

    public void checkListen(int port) {
    }

    public void checkMemberAccess(Class<?> cls, int type) {
    }

    public void checkMulticast(InetAddress maddr) {
    }

    @Deprecated
    public void checkMulticast(InetAddress maddr, byte ttl) {
    }

    public void checkPackageAccess(String packageName) {
    }

    public void checkPackageDefinition(String packageName) {
    }

    public void checkPropertiesAccess() {
    }

    public void checkPropertyAccess(String key) {
    }

    public void checkRead(FileDescriptor fd) {
    }

    public void checkRead(String file) {
    }

    public void checkRead(String file, Object context) {
    }

    public void checkSecurityAccess(String target) {
    }

    public void checkSetFactory() {
    }

    public boolean checkTopLevelWindow(Object window) {
        return true;
    }

    public void checkSystemClipboardAccess() {
    }

    public void checkAwtEventQueueAccess() {
    }

    public void checkPrintJobAccess() {
    }

    public void checkWrite(FileDescriptor fd) {
    }

    public void checkWrite(String file) {
    }

    @Deprecated
    public boolean getInCheck() {
        return this.inCheck;
    }

    protected Class[] getClassContext() {
        return null;
    }

    @Deprecated
    protected ClassLoader currentClassLoader() {
        return null;
    }

    @Deprecated
    protected int classLoaderDepth() {
        return -1;
    }

    @Deprecated
    protected Class<?> currentLoadedClass() {
        return null;
    }

    @Deprecated
    protected int classDepth(String name) {
        return -1;
    }

    @Deprecated
    protected boolean inClass(String name) {
        return false;
    }

    @Deprecated
    protected boolean inClassLoader() {
        return false;
    }

    public Object getSecurityContext() {
        return null;
    }

    public void checkPermission(Permission permission) {
    }

    public void checkPermission(Permission permission, Object context) {
    }
}
