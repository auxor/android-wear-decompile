package com.android.org.conscrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.net.ssl.SSLSession;

public class FileClientSessionCache {
    public static final int MAX_SIZE = 12;
    static final Map<File, Impl> caches;

    static class CacheFile extends File {
        long lastModified;
        final String name;

        CacheFile(java.io.File r1, java.lang.String r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.FileClientSessionCache.CacheFile.<init>(java.io.File, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.FileClientSessionCache.CacheFile.<init>(java.io.File, java.lang.String):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e7
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.FileClientSessionCache.CacheFile.<init>(java.io.File, java.lang.String):void");
        }

        public int compareTo(java.io.File r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.FileClientSessionCache.CacheFile.compareTo(java.io.File):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.FileClientSessionCache.CacheFile.compareTo(java.io.File):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.FileClientSessionCache.CacheFile.compareTo(java.io.File):int");
        }

        public long lastModified() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.FileClientSessionCache.CacheFile.lastModified():long
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.FileClientSessionCache.CacheFile.lastModified():long
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.FileClientSessionCache.CacheFile.lastModified():long");
        }
    }

    static class Impl implements SSLClientSessionCache {
        Map<String, File> accessOrder;
        final File directory;
        String[] initialFiles;
        int size;

        Impl(File directory) throws IOException {
            this.accessOrder = newAccessOrder();
            boolean exists = directory.exists();
            if (!exists || directory.isDirectory()) {
                if (exists) {
                    this.initialFiles = directory.list();
                    if (this.initialFiles == null) {
                        throw new IOException(directory + " exists but cannot list contents.");
                    }
                    Arrays.sort(this.initialFiles);
                    this.size = this.initialFiles.length;
                } else if (directory.mkdirs()) {
                    this.size = 0;
                } else {
                    throw new IOException("Creation of " + directory + " directory failed.");
                }
                this.directory = directory;
                return;
            }
            throw new IOException(directory + " exists but is not a directory.");
        }

        private static Map<String, File> newAccessOrder() {
            return new LinkedHashMap(FileClientSessionCache.MAX_SIZE, 0.75f, true);
        }

        private static String fileName(String host, int port) {
            if (host != null) {
                return host + "." + port;
            }
            throw new NullPointerException("host == null");
        }

        public synchronized byte[] getSessionData(java.lang.String r11, int r12) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.android.org.conscrypt.FileClientSessionCache.Impl.getSessionData(java.lang.String, int):byte[]. bs: [B:20:0x0043, B:37:0x0058, B:45:0x0062]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:57)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r10 = this;
            r7 = 0;
            monitor-enter(r10);
            r4 = fileName(r11, r12);	 Catch:{ all -> 0x0049 }
            r8 = r10.accessOrder;	 Catch:{ all -> 0x0049 }
            r2 = r8.get(r4);	 Catch:{ all -> 0x0049 }
            r2 = (java.io.File) r2;	 Catch:{ all -> 0x0049 }
            if (r2 != 0) goto L_0x002d;	 Catch:{ all -> 0x0049 }
        L_0x0010:
            r8 = r10.initialFiles;	 Catch:{ all -> 0x0049 }
            if (r8 != 0) goto L_0x0017;
        L_0x0014:
            r0 = r7;
        L_0x0015:
            monitor-exit(r10);
            return r0;
        L_0x0017:
            r8 = r10.initialFiles;	 Catch:{ all -> 0x0049 }
            r8 = java.util.Arrays.binarySearch(r8, r4);	 Catch:{ all -> 0x0049 }
            if (r8 >= 0) goto L_0x0021;	 Catch:{ all -> 0x0049 }
        L_0x001f:
            r0 = r7;	 Catch:{ all -> 0x0049 }
            goto L_0x0015;	 Catch:{ all -> 0x0049 }
        L_0x0021:
            r2 = new java.io.File;	 Catch:{ all -> 0x0049 }
            r8 = r10.directory;	 Catch:{ all -> 0x0049 }
            r2.<init>(r8, r4);	 Catch:{ all -> 0x0049 }
            r8 = r10.accessOrder;	 Catch:{ all -> 0x0049 }
            r8.put(r4, r2);	 Catch:{ all -> 0x0049 }
        L_0x002d:
            r3 = new java.io.FileInputStream;	 Catch:{ FileNotFoundException -> 0x004c }
            r3.<init>(r2);	 Catch:{ FileNotFoundException -> 0x004c }
            r8 = r2.length();	 Catch:{ IOException -> 0x0052 }
            r6 = (int) r8;	 Catch:{ IOException -> 0x0052 }
            r0 = new byte[r6];	 Catch:{ IOException -> 0x0052 }
            r8 = new java.io.DataInputStream;	 Catch:{ IOException -> 0x0052 }
            r8.<init>(r3);	 Catch:{ IOException -> 0x0052 }
            r8.readFully(r0);	 Catch:{ IOException -> 0x0052 }
            if (r3 == 0) goto L_0x0015;
        L_0x0043:
            r3.close();	 Catch:{ RuntimeException -> 0x0047, Exception -> 0x0068 }
            goto L_0x0015;
        L_0x0047:
            r5 = move-exception;
            throw r5;	 Catch:{ all -> 0x0049 }
        L_0x0049:
            r7 = move-exception;
            monitor-exit(r10);
            throw r7;
        L_0x004c:
            r1 = move-exception;
            logReadError(r11, r2, r1);	 Catch:{ all -> 0x0049 }
            r0 = r7;
            goto L_0x0015;
        L_0x0052:
            r1 = move-exception;
            logReadError(r11, r2, r1);	 Catch:{ all -> 0x005f }
            if (r3 == 0) goto L_0x005b;
        L_0x0058:
            r3.close();	 Catch:{ RuntimeException -> 0x005d, Exception -> 0x006a }
        L_0x005b:
            r0 = r7;
            goto L_0x0015;
        L_0x005d:
            r5 = move-exception;
            throw r5;	 Catch:{ all -> 0x0049 }
        L_0x005f:
            r7 = move-exception;
            if (r3 == 0) goto L_0x0065;
        L_0x0062:
            r3.close();	 Catch:{ RuntimeException -> 0x0066, Exception -> 0x006c }
        L_0x0065:
            throw r7;	 Catch:{ all -> 0x0049 }
        L_0x0066:
            r5 = move-exception;	 Catch:{ all -> 0x0049 }
            throw r5;	 Catch:{ all -> 0x0049 }
        L_0x0068:
            r7 = move-exception;
            goto L_0x0015;
        L_0x006a:
            r8 = move-exception;
            goto L_0x005b;
        L_0x006c:
            r8 = move-exception;
            goto L_0x0065;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.FileClientSessionCache.Impl.getSessionData(java.lang.String, int):byte[]");
        }

        static void logReadError(String host, File file, Throwable t) {
            System.err.println("FileClientSessionCache: Error reading session data for " + host + " from " + file + ".");
            t.printStackTrace();
        }

        public synchronized void putSessionData(SSLSession session, byte[] sessionData) {
            String host = session.getPeerHost();
            if (sessionData == null) {
                throw new NullPointerException("sessionData == null");
            }
            String name = fileName(host, session.getPeerPort());
            File file = new File(this.directory, name);
            boolean existedBefore = file.exists();
            try {
                FileOutputStream out = new FileOutputStream(file);
                if (!existedBefore) {
                    this.size++;
                    makeRoom();
                }
                boolean writeSuccessful = false;
                try {
                    out.write(sessionData);
                    writeSuccessful = true;
                    try {
                        out.close();
                        if (writeSuccessful && true) {
                            this.accessOrder.put(name, file);
                        } else {
                            delete(file);
                        }
                    } catch (IOException e) {
                        logWriteError(host, file, e);
                        if (!writeSuccessful || null == null) {
                            delete(file);
                        } else {
                            this.accessOrder.put(name, file);
                        }
                    } catch (Throwable th) {
                        if (!writeSuccessful || null == null) {
                            delete(file);
                        } else {
                            this.accessOrder.put(name, file);
                        }
                    }
                } catch (IOException e2) {
                    logWriteError(host, file, e2);
                    try {
                        out.close();
                        if (writeSuccessful && true) {
                            this.accessOrder.put(name, file);
                        } else {
                            delete(file);
                        }
                    } catch (IOException e22) {
                        logWriteError(host, file, e22);
                        if (!writeSuccessful || null == null) {
                            delete(file);
                        } else {
                            this.accessOrder.put(name, file);
                        }
                    } catch (Throwable th2) {
                        if (!writeSuccessful || null == null) {
                            delete(file);
                        } else {
                            this.accessOrder.put(name, file);
                        }
                    }
                } finally {
                    try {
                        out.close();
                        if (writeSuccessful && true) {
                            this.accessOrder.put(name, file);
                        } else {
                            delete(file);
                        }
                    } catch (IOException e222) {
                        logWriteError(host, file, e222);
                        if (!writeSuccessful || null == null) {
                            delete(file);
                        } else {
                            this.accessOrder.put(name, file);
                        }
                    } catch (Throwable th3) {
                        if (!writeSuccessful || null == null) {
                            delete(file);
                        } else {
                            this.accessOrder.put(name, file);
                        }
                    }
                }
            } catch (FileNotFoundException e3) {
                logWriteError(host, file, e3);
            }
        }

        private void makeRoom() {
            if (this.size > FileClientSessionCache.MAX_SIZE) {
                indexFiles();
                int removals = this.size - 12;
                Iterator<File> i = this.accessOrder.values().iterator();
                do {
                    delete((File) i.next());
                    i.remove();
                    removals--;
                } while (removals > 0);
            }
        }

        private void indexFiles() {
            String[] initialFiles = this.initialFiles;
            if (initialFiles != null) {
                this.initialFiles = null;
                Set<CacheFile> diskOnly = new TreeSet();
                for (String name : initialFiles) {
                    if (!this.accessOrder.containsKey(name)) {
                        diskOnly.add(new CacheFile(this.directory, name));
                    }
                }
                if (!diskOnly.isEmpty()) {
                    Map<String, File> newOrder = newAccessOrder();
                    for (CacheFile cacheFile : diskOnly) {
                        newOrder.put(cacheFile.name, cacheFile);
                    }
                    newOrder.putAll(this.accessOrder);
                    this.accessOrder = newOrder;
                }
            }
        }

        private void delete(File file) {
            if (!file.delete()) {
                new IOException("FileClientSessionCache: Failed to delete " + file + ".").printStackTrace();
            }
            this.size--;
        }

        static void logWriteError(String host, File file, Throwable t) {
            System.err.println("FileClientSessionCache: Error writing session data for " + host + " to " + file + ".");
            t.printStackTrace();
        }
    }

    private FileClientSessionCache() {
    }

    static {
        caches = new HashMap();
    }

    public static synchronized SSLClientSessionCache usingDirectory(File directory) throws IOException {
        Impl cache;
        synchronized (FileClientSessionCache.class) {
            cache = (Impl) caches.get(directory);
            if (cache == null) {
                cache = new Impl(directory);
                caches.put(directory, cache);
            }
        }
        return cache;
    }

    static synchronized void reset() {
        synchronized (FileClientSessionCache.class) {
            caches.clear();
        }
    }
}
