package java.lang;

import android.system.ErrnoException;
import android.system.OsConstants;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import libcore.io.IoUtils;
import libcore.io.Libcore;

final class ProcessManager {
    private static final ProcessManager instance;
    private final Map<Integer, ProcessReference> processReferences;
    private final ProcessReferenceQueue referenceQueue;

    /* renamed from: java.lang.ProcessManager.1 */
    class AnonymousClass1 extends Thread {
        AnonymousClass1(String x0) {
            super(x0);
        }

        public void run() {
            ProcessManager.this.watchChildren();
        }
    }

    static class ProcessImpl extends Process {
        private final InputStream errorStream;
        private Integer exitValue;
        private final Object exitValueMutex;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final int pid;

        ProcessImpl(int pid, FileDescriptor in, FileDescriptor out, FileDescriptor err) {
            this.exitValue = null;
            this.exitValueMutex = new Object();
            this.pid = pid;
            this.errorStream = new ProcessInputStream(null);
            this.inputStream = new ProcessInputStream(null);
            this.outputStream = new ProcessOutputStream(null);
        }

        public void destroy() {
            synchronized (this.exitValueMutex) {
                if (this.exitValue == null) {
                    try {
                        Libcore.os.kill(this.pid, OsConstants.SIGKILL);
                    } catch (ErrnoException e) {
                        System.logI("Failed to destroy process " + this.pid, e);
                    }
                }
            }
            IoUtils.closeQuietly(this.inputStream);
            IoUtils.closeQuietly(this.errorStream);
            IoUtils.closeQuietly(this.outputStream);
        }

        public int exitValue() {
            int intValue;
            synchronized (this.exitValueMutex) {
                if (this.exitValue == null) {
                    throw new IllegalThreadStateException("Process has not yet terminated: " + this.pid);
                }
                intValue = this.exitValue.intValue();
            }
            return intValue;
        }

        public InputStream getErrorStream() {
            return this.errorStream;
        }

        public InputStream getInputStream() {
            return this.inputStream;
        }

        public OutputStream getOutputStream() {
            return this.outputStream;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int waitFor() throws java.lang.InterruptedException {
            /*
            r2 = this;
            r1 = r2.exitValueMutex;
            monitor-enter(r1);
        L_0x0003:
            r0 = r2.exitValue;	 Catch:{ all -> 0x000d }
            if (r0 != 0) goto L_0x0010;
        L_0x0007:
            r0 = r2.exitValueMutex;	 Catch:{ all -> 0x000d }
            r0.wait();	 Catch:{ all -> 0x000d }
            goto L_0x0003;
        L_0x000d:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x000d }
            throw r0;
        L_0x0010:
            r0 = r2.exitValue;	 Catch:{ all -> 0x000d }
            r0 = r0.intValue();	 Catch:{ all -> 0x000d }
            monitor-exit(r1);	 Catch:{ all -> 0x000d }
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: java.lang.ProcessManager.ProcessImpl.waitFor():int");
        }

        void setExitValue(int exitValue) {
            synchronized (this.exitValueMutex) {
                this.exitValue = Integer.valueOf(exitValue);
                this.exitValueMutex.notifyAll();
            }
        }

        public String toString() {
            return "Process[pid=" + this.pid + "]";
        }
    }

    private static class ProcessInputStream extends FileInputStream {
        private FileDescriptor fd;

        private ProcessInputStream(FileDescriptor fd) {
            super(fd);
            this.fd = fd;
        }

        public void close() throws IOException {
            try {
                super.close();
                synchronized (this) {
                    try {
                        IoUtils.close(this.fd);
                        this.fd = null;
                    } catch (Throwable th) {
                        this.fd = null;
                    }
                }
            } catch (Throwable th2) {
                this.fd = null;
            }
        }
    }

    private static class ProcessOutputStream extends FileOutputStream {
        private FileDescriptor fd;

        private ProcessOutputStream(FileDescriptor fd) {
            super(fd);
            this.fd = fd;
        }

        public void close() throws IOException {
            try {
                super.close();
                synchronized (this) {
                    try {
                        IoUtils.close(this.fd);
                        this.fd = null;
                    } catch (Throwable th) {
                        this.fd = null;
                    }
                }
            } catch (Throwable th2) {
                this.fd = null;
            }
        }
    }

    static class ProcessReference extends WeakReference<ProcessImpl> {
        final int processId;

        public ProcessReference(ProcessImpl referent, ProcessReferenceQueue referenceQueue) {
            super(referent, referenceQueue);
            this.processId = referent.pid;
        }
    }

    static class ProcessReferenceQueue extends ReferenceQueue<ProcessImpl> {
        ProcessReferenceQueue() {
        }

        public ProcessReference poll() {
            return (ProcessReference) super.poll();
        }
    }

    private static native int exec(String[] strArr, String[] strArr2, String str, FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, boolean z) throws IOException;

    private ProcessManager() {
        this.processReferences = new HashMap();
        this.referenceQueue = new ProcessReferenceQueue();
        Thread reaperThread = new AnonymousClass1(ProcessManager.class.getName());
        reaperThread.setDaemon(true);
        reaperThread.start();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void cleanUp() {
        /*
        r4 = this;
    L_0x0000:
        r1 = r4.referenceQueue;
        r0 = r1.poll();
        if (r0 == 0) goto L_0x001b;
    L_0x0008:
        r2 = r4.processReferences;
        monitor-enter(r2);
        r1 = r4.processReferences;	 Catch:{ all -> 0x0018 }
        r3 = r0.processId;	 Catch:{ all -> 0x0018 }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ all -> 0x0018 }
        r1.remove(r3);	 Catch:{ all -> 0x0018 }
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        goto L_0x0000;
    L_0x0018:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0018 }
        throw r1;
    L_0x001b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.ProcessManager.cleanUp():void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void watchChildren() {
        /*
        r7 = this;
        r3 = new android.util.MutableInt;
        r4 = -1;
        r3.<init>(r4);
    L_0x0006:
        r4 = libcore.io.Libcore.os;	 Catch:{ ErrnoException -> 0x0020 }
        r5 = 0;
        r6 = 0;
        r2 = r4.waitpid(r5, r3, r6);	 Catch:{ ErrnoException -> 0x0020 }
        r4 = r3.value;	 Catch:{ ErrnoException -> 0x0020 }
        r4 = android.system.OsConstants.WIFEXITED(r4);	 Catch:{ ErrnoException -> 0x0020 }
        if (r4 == 0) goto L_0x002b;
    L_0x0016:
        r4 = r3.value;	 Catch:{ ErrnoException -> 0x0020 }
        r1 = android.system.OsConstants.WEXITSTATUS(r4);	 Catch:{ ErrnoException -> 0x0020 }
    L_0x001c:
        r7.onExit(r2, r1);	 Catch:{ ErrnoException -> 0x0020 }
        goto L_0x0006;
    L_0x0020:
        r0 = move-exception;
        r4 = r0.errno;
        r5 = android.system.OsConstants.ECHILD;
        if (r4 != r5) goto L_0x0064;
    L_0x0027:
        r7.waitForMoreChildren();
        goto L_0x0006;
    L_0x002b:
        r4 = r3.value;	 Catch:{ ErrnoException -> 0x0020 }
        r4 = android.system.OsConstants.WIFSIGNALED(r4);	 Catch:{ ErrnoException -> 0x0020 }
        if (r4 == 0) goto L_0x003a;
    L_0x0033:
        r4 = r3.value;	 Catch:{ ErrnoException -> 0x0020 }
        r1 = android.system.OsConstants.WTERMSIG(r4);	 Catch:{ ErrnoException -> 0x0020 }
        goto L_0x001c;
    L_0x003a:
        r4 = r3.value;	 Catch:{ ErrnoException -> 0x0020 }
        r4 = android.system.OsConstants.WIFSTOPPED(r4);	 Catch:{ ErrnoException -> 0x0020 }
        if (r4 == 0) goto L_0x0049;
    L_0x0042:
        r4 = r3.value;	 Catch:{ ErrnoException -> 0x0020 }
        r1 = android.system.OsConstants.WSTOPSIG(r4);	 Catch:{ ErrnoException -> 0x0020 }
        goto L_0x001c;
    L_0x0049:
        r4 = new java.lang.AssertionError;	 Catch:{ ErrnoException -> 0x0020 }
        r5 = new java.lang.StringBuilder;	 Catch:{ ErrnoException -> 0x0020 }
        r5.<init>();	 Catch:{ ErrnoException -> 0x0020 }
        r6 = "unexpected status from waitpid: ";
        r5 = r5.append(r6);	 Catch:{ ErrnoException -> 0x0020 }
        r6 = r3.value;	 Catch:{ ErrnoException -> 0x0020 }
        r5 = r5.append(r6);	 Catch:{ ErrnoException -> 0x0020 }
        r5 = r5.toString();	 Catch:{ ErrnoException -> 0x0020 }
        r4.<init>(r5);	 Catch:{ ErrnoException -> 0x0020 }
        throw r4;	 Catch:{ ErrnoException -> 0x0020 }
    L_0x0064:
        r4 = new java.lang.AssertionError;
        r4.<init>(r0);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.ProcessManager.watchChildren():void");
    }

    private void onExit(int pid, int exitValue) {
        synchronized (this.processReferences) {
            cleanUp();
            ProcessReference processReference = (ProcessReference) this.processReferences.remove(Integer.valueOf(pid));
        }
        if (processReference != null) {
            ProcessImpl process = (ProcessImpl) processReference.get();
            if (process != null) {
                process.setExitValue(exitValue);
            }
        }
    }

    private void waitForMoreChildren() {
        synchronized (this.processReferences) {
            if (this.processReferences.isEmpty()) {
                try {
                    this.processReferences.wait();
                } catch (InterruptedException e) {
                    throw new AssertionError((Object) "unexpected interrupt");
                }
            }
        }
    }

    public Process exec(String[] taintedCommand, String[] taintedEnvironment, File workingDirectory, boolean redirectErrorStream) throws IOException {
        if (taintedCommand == null) {
            throw new NullPointerException("taintedCommand == null");
        } else if (taintedCommand.length == 0) {
            throw new IndexOutOfBoundsException("taintedCommand.length == 0");
        } else {
            int i;
            ProcessImpl process;
            Object[] command = (String[]) taintedCommand.clone();
            Object[] environment = taintedEnvironment != null ? (String[]) taintedEnvironment.clone() : null;
            for (i = 0; i < command.length; i++) {
                if (command[i] == null) {
                    throw new NullPointerException("taintedCommand[" + i + "] == null");
                }
            }
            if (environment != null) {
                for (i = 0; i < environment.length; i++) {
                    if (environment[i] == null) {
                        throw new NullPointerException("taintedEnvironment[" + i + "] == null");
                    }
                }
            }
            FileDescriptor in = new FileDescriptor();
            FileDescriptor out = new FileDescriptor();
            FileDescriptor err = new FileDescriptor();
            String workingPath = workingDirectory == null ? null : workingDirectory.getPath();
            synchronized (this.processReferences) {
                try {
                    int pid = exec(command, environment, workingPath, in, out, err, redirectErrorStream);
                    process = new ProcessImpl(pid, in, out, err);
                    this.processReferences.put(Integer.valueOf(pid), new ProcessReference(process, this.referenceQueue));
                    this.processReferences.notifyAll();
                } catch (IOException e) {
                    IOException wrapper = new IOException("Error running exec(). Command: " + Arrays.toString(command) + " Working Directory: " + workingDirectory + " Environment: " + Arrays.toString(environment));
                    wrapper.initCause(e);
                    throw wrapper;
                }
            }
            return process;
        }
    }

    static {
        instance = new ProcessManager();
    }

    public static ProcessManager getInstance() {
        return instance;
    }
}
