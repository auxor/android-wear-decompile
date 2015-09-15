package java.util.zip;

import com.android.dex.DexFormat;
import dalvik.system.CloseGuard;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Modifier;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import libcore.io.BufferIterator;
import libcore.io.HeapBufferIterator;
import libcore.io.IoUtils;
import libcore.io.Streams;
import org.w3c.dom.traversal.NodeFilter;

public class ZipFile implements Closeable, ZipConstants {
    static final int GPBF_DATA_DESCRIPTOR_FLAG = 8;
    static final int GPBF_ENCRYPTED_FLAG = 1;
    static final int GPBF_UNSUPPORTED_MASK = 1;
    static final int GPBF_UTF8_FLAG = 2048;
    public static final int OPEN_DELETE = 4;
    public static final int OPEN_READ = 1;
    private String comment;
    private final LinkedHashMap<String, ZipEntry> entries;
    private File fileToDeleteOnClose;
    private final String filename;
    private final CloseGuard guard;
    private RandomAccessFile raf;

    /* renamed from: java.util.zip.ZipFile.1 */
    class AnonymousClass1 implements Enumeration<ZipEntry> {
        final /* synthetic */ Iterator val$iterator;

        AnonymousClass1(Iterator it) {
            this.val$iterator = it;
        }

        public boolean hasMoreElements() {
            ZipFile.this.checkNotClosed();
            return this.val$iterator.hasNext();
        }

        public ZipEntry nextElement() {
            ZipFile.this.checkNotClosed();
            return (ZipEntry) this.val$iterator.next();
        }
    }

    public static class RAFStream extends InputStream {
        private long endOffset;
        private long offset;
        private final RandomAccessFile sharedRaf;

        public RAFStream(RandomAccessFile raf, long initialOffset, long endOffset) {
            this.sharedRaf = raf;
            this.offset = initialOffset;
            this.endOffset = endOffset;
        }

        public RAFStream(RandomAccessFile raf, long initialOffset) throws IOException {
            this(raf, initialOffset, raf.length());
        }

        public int available() throws IOException {
            return this.offset < this.endOffset ? ZipFile.OPEN_READ : 0;
        }

        public int read() throws IOException {
            return Streams.readSingleByte(this);
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            int count;
            synchronized (this.sharedRaf) {
                long length = this.endOffset - this.offset;
                if (((long) byteCount) > length) {
                    byteCount = (int) length;
                }
                this.sharedRaf.seek(this.offset);
                count = this.sharedRaf.read(buffer, byteOffset, byteCount);
                if (count > 0) {
                    this.offset += (long) count;
                } else {
                    count = -1;
                }
            }
            return count;
        }

        public long skip(long byteCount) throws IOException {
            if (byteCount > this.endOffset - this.offset) {
                byteCount = this.endOffset - this.offset;
            }
            this.offset += byteCount;
            return byteCount;
        }

        public int fill(Inflater inflater, int nativeEndBufSize) throws IOException {
            int len;
            synchronized (this.sharedRaf) {
                len = Math.min((int) (this.endOffset - this.offset), nativeEndBufSize);
                skip((long) inflater.setFileInput(this.sharedRaf.getFD(), this.offset, nativeEndBufSize));
            }
            return len;
        }
    }

    public static class ZipInflaterInputStream extends InflaterInputStream {
        private long bytesRead;
        private final ZipEntry entry;

        public ZipInflaterInputStream(InputStream is, Inflater inf, int bsize, ZipEntry entry) {
            super(is, inf, bsize);
            this.bytesRead = 0;
            this.entry = entry;
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            try {
                int i = super.read(buffer, byteOffset, byteCount);
                if (i != -1) {
                    this.bytesRead += (long) i;
                } else if (this.entry.size != this.bytesRead) {
                    throw new IOException("Size mismatch on inflated file: " + this.bytesRead + " vs " + this.entry.size);
                }
                return i;
            } catch (IOException e) {
                throw new IOException("Error reading data for " + this.entry.getName() + " near offset " + this.bytesRead, e);
            }
        }

        public int available() throws IOException {
            if (this.closed || super.available() == 0) {
                return 0;
            }
            return (int) (this.entry.getSize() - this.bytesRead);
        }
    }

    public ZipFile(File file) throws ZipException, IOException {
        this(file, OPEN_READ);
    }

    public ZipFile(String name) throws IOException {
        this(new File(name), OPEN_READ);
    }

    public ZipFile(File file, int mode) throws IOException {
        this.entries = new LinkedHashMap();
        this.guard = CloseGuard.get();
        this.filename = file.getPath();
        if (mode == OPEN_READ || mode == 5) {
            if ((mode & OPEN_DELETE) != 0) {
                this.fileToDeleteOnClose = file;
                this.fileToDeleteOnClose.deleteOnExit();
            } else {
                this.fileToDeleteOnClose = null;
            }
            this.raf = new RandomAccessFile(this.filename, "r");
            boolean mustCloseFile = true;
            try {
                readCentralDir();
                mustCloseFile = false;
                this.guard.open("close");
            } finally {
                if (mustCloseFile) {
                    IoUtils.closeQuietly(this.raf);
                }
            }
        } else {
            throw new IllegalArgumentException("Bad mode: " + mode);
        }
    }

    protected void finalize() throws IOException {
        AssertionError assertionError;
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            try {
                super.finalize();
            } catch (Object t) {
                assertionError = new AssertionError(t);
            }
        } catch (Object t2) {
            assertionError = new AssertionError(t2);
        }
    }

    public void close() throws IOException {
        this.guard.close();
        RandomAccessFile localRaf = this.raf;
        if (localRaf != null) {
            synchronized (localRaf) {
                this.raf = null;
                localRaf.close();
            }
            if (this.fileToDeleteOnClose != null) {
                this.fileToDeleteOnClose.delete();
                this.fileToDeleteOnClose = null;
            }
        }
    }

    private void checkNotClosed() {
        if (this.raf == null) {
            throw new IllegalStateException("Zip file closed");
        }
    }

    public Enumeration<? extends ZipEntry> entries() {
        checkNotClosed();
        return new AnonymousClass1(this.entries.values().iterator());
    }

    public String getComment() {
        checkNotClosed();
        return this.comment;
    }

    public ZipEntry getEntry(String entryName) {
        checkNotClosed();
        if (entryName == null) {
            throw new NullPointerException("entryName == null");
        }
        ZipEntry ze = (ZipEntry) this.entries.get(entryName);
        if (ze == null) {
            return (ZipEntry) this.entries.get(entryName + "/");
        }
        return ze;
    }

    public InputStream getInputStream(ZipEntry entry) throws IOException {
        entry = getEntry(entry.getName());
        if (entry == null) {
            return null;
        }
        RandomAccessFile localRaf = this.raf;
        synchronized (localRaf) {
            InputStream rafStream = new RAFStream(localRaf, entry.localHeaderRelOffset);
            DataInputStream is = new DataInputStream(rafStream);
            int localMagic = Integer.reverseBytes(is.readInt());
            if (((long) localMagic) != ZipConstants.LOCSIG) {
                throwZipException("Local File Header", localMagic);
            }
            is.skipBytes(2);
            int gpbf = Short.reverseBytes(is.readShort()) & DexFormat.MAX_TYPE_IDX;
            if ((gpbf & OPEN_READ) != 0) {
                throw new ZipException("Invalid General Purpose Bit Flag: " + gpbf);
            }
            is.skipBytes(18);
            int fileNameLength = Short.reverseBytes(is.readShort()) & DexFormat.MAX_TYPE_IDX;
            int extraFieldLength = Short.reverseBytes(is.readShort()) & DexFormat.MAX_TYPE_IDX;
            is.close();
            rafStream.skip((long) (fileNameLength + extraFieldLength));
            if (entry.compressionMethod == 0) {
                rafStream.endOffset = rafStream.offset + entry.size;
                return rafStream;
            }
            rafStream.endOffset = rafStream.offset + entry.compressedSize;
            InputStream zipInflaterInputStream = new ZipInflaterInputStream(rafStream, new Inflater(true), Math.max((int) NodeFilter.SHOW_DOCUMENT_FRAGMENT, (int) Math.min(entry.getSize(), 65535)), entry);
            return zipInflaterInputStream;
        }
    }

    public String getName() {
        return this.filename;
    }

    public int size() {
        checkNotClosed();
        return this.entries.size();
    }

    private void readCentralDir() throws IOException {
        long scanOffset = this.raf.length() - 22;
        if (scanOffset < 0) {
            throw new ZipException("File too short to be a zip file: " + this.raf.length());
        }
        this.raf.seek(0);
        int headerMagic = Integer.reverseBytes(this.raf.readInt());
        if (((long) headerMagic) == ZipConstants.ENDSIG) {
            throw new ZipException("Empty zip archive not supported");
        }
        if (((long) headerMagic) != ZipConstants.LOCSIG) {
            throw new ZipException("Not a zip archive");
        }
        long stopOffset = scanOffset - 65536;
        if (stopOffset < 0) {
            stopOffset = 0;
        }
        do {
            this.raf.seek(scanOffset);
            if (((long) Integer.reverseBytes(this.raf.readInt())) == ZipConstants.ENDSIG) {
                byte[] eocd = new byte[18];
                this.raf.readFully(eocd);
                BufferIterator it = HeapBufferIterator.iterator(eocd, 0, eocd.length, ByteOrder.LITTLE_ENDIAN);
                int diskNumber = it.readShort() & DexFormat.MAX_TYPE_IDX;
                int diskWithCentralDir = it.readShort() & DexFormat.MAX_TYPE_IDX;
                int numEntries = it.readShort() & DexFormat.MAX_TYPE_IDX;
                int totalNumEntries = it.readShort() & DexFormat.MAX_TYPE_IDX;
                it.skip(OPEN_DELETE);
                long centralDirOffset = ((long) it.readInt()) & 4294967295L;
                int commentLength = it.readShort() & DexFormat.MAX_TYPE_IDX;
                if (numEntries == totalNumEntries && diskNumber == 0 && diskWithCentralDir == 0) {
                    if (commentLength > 0) {
                        byte[] commentBytes = new byte[commentLength];
                        this.raf.readFully(commentBytes);
                        this.comment = new String(commentBytes, 0, commentBytes.length, StandardCharsets.UTF_8);
                    }
                    BufferedInputStream bufferedStream = new BufferedInputStream(new RAFStream(this.raf, centralDirOffset), Modifier.SYNTHETIC);
                    byte[] hdrBuf = new byte[46];
                    for (int i = 0; i < numEntries; i += OPEN_READ) {
                        ZipEntry zipEntry = new ZipEntry(hdrBuf, bufferedStream, StandardCharsets.UTF_8);
                        if (zipEntry.localHeaderRelOffset >= centralDirOffset) {
                            throw new ZipException("Local file header offset is after central directory");
                        }
                        String entryName = zipEntry.getName();
                        if (this.entries.put(entryName, zipEntry) != null) {
                            throw new ZipException("Duplicate entry name: " + entryName);
                        }
                    }
                    return;
                }
                throw new ZipException("Spanned archives not supported");
            }
            scanOffset--;
        } while (scanOffset >= stopOffset);
        throw new ZipException("End Of Central Directory signature not found");
    }

    static void throwZipException(String msg, int magic) throws ZipException {
        throw new ZipException(msg + " signature not found; was " + IntegralToString.intToHexString(magic, true, GPBF_DATA_DESCRIPTOR_FLAG));
    }
}
