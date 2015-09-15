package java.io;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import libcore.icu.DateIntervalFormat;

public class InputStreamReader extends Reader {
    private final ByteBuffer bytes;
    private CharsetDecoder decoder;
    private boolean endOfInput;
    private InputStream in;

    public InputStreamReader(InputStream in) {
        this(in, Charset.defaultCharset());
    }

    public InputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        super(in);
        this.endOfInput = false;
        this.bytes = ByteBuffer.allocate(DateIntervalFormat.FORMAT_UTC);
        if (charsetName == null) {
            throw new NullPointerException("charsetName == null");
        }
        this.in = in;
        try {
            this.decoder = Charset.forName(charsetName).newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            this.bytes.limit(0);
        } catch (IllegalArgumentException e) {
            throw ((UnsupportedEncodingException) new UnsupportedEncodingException(charsetName).initCause(e));
        }
    }

    public InputStreamReader(InputStream in, CharsetDecoder dec) {
        super(in);
        this.endOfInput = false;
        this.bytes = ByteBuffer.allocate(DateIntervalFormat.FORMAT_UTC);
        dec.averageCharsPerByte();
        this.in = in;
        this.decoder = dec;
        this.bytes.limit(0);
    }

    public InputStreamReader(InputStream in, Charset charset) {
        super(in);
        this.endOfInput = false;
        this.bytes = ByteBuffer.allocate(DateIntervalFormat.FORMAT_UTC);
        this.in = in;
        this.decoder = charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        this.bytes.limit(0);
    }

    public void close() throws IOException {
        synchronized (this.lock) {
            if (this.decoder != null) {
                this.decoder.reset();
            }
            this.decoder = null;
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
        }
    }

    public String getEncoding() {
        if (isOpen()) {
            return this.decoder.charset().name();
        }
        return null;
    }

    public int read() throws IOException {
        int i = -1;
        synchronized (this.lock) {
            if (isOpen()) {
                char[] buf = new char[1];
                if (read(buf, 0, 1) != -1) {
                    i = buf[0];
                }
            } else {
                throw new IOException("InputStreamReader is closed");
            }
        }
        return i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(char[] r12, int r13, int r14) throws java.io.IOException {
        /*
        r11 = this;
        r7 = -1;
        r2 = 1;
        r6 = 0;
        r8 = r11.lock;
        monitor-enter(r8);
        r9 = r11.isOpen();	 Catch:{ all -> 0x0014 }
        if (r9 != 0) goto L_0x0017;
    L_0x000c:
        r6 = new java.io.IOException;	 Catch:{ all -> 0x0014 }
        r7 = "InputStreamReader is closed";
        r6.<init>(r7);	 Catch:{ all -> 0x0014 }
        throw r6;	 Catch:{ all -> 0x0014 }
    L_0x0014:
        r6 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x0014 }
        throw r6;
    L_0x0017:
        r9 = r12.length;	 Catch:{ all -> 0x0014 }
        java.util.Arrays.checkOffsetAndCount(r9, r13, r14);	 Catch:{ all -> 0x0014 }
        if (r14 != 0) goto L_0x001f;
    L_0x001d:
        monitor-exit(r8);	 Catch:{ all -> 0x0014 }
    L_0x001e:
        return r6;
    L_0x001f:
        r4 = java.nio.CharBuffer.wrap(r12, r13, r14);	 Catch:{ all -> 0x0014 }
        r5 = java.nio.charset.CoderResult.UNDERFLOW;	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r9.hasRemaining();	 Catch:{ all -> 0x0014 }
        if (r9 != 0) goto L_0x007c;
    L_0x002d:
        r6 = r4.hasRemaining();	 Catch:{ all -> 0x0014 }
        if (r6 == 0) goto L_0x0043;
    L_0x0033:
        if (r2 == 0) goto L_0x00bc;
    L_0x0035:
        r6 = r11.in;	 Catch:{ IOException -> 0x007e }
        r6 = r6.available();	 Catch:{ IOException -> 0x007e }
        if (r6 != 0) goto L_0x007f;
    L_0x003d:
        r6 = r4.position();	 Catch:{ IOException -> 0x007e }
        if (r6 <= r13) goto L_0x007f;
    L_0x0043:
        r6 = java.nio.charset.CoderResult.UNDERFLOW;	 Catch:{ all -> 0x0014 }
        if (r5 != r6) goto L_0x0063;
    L_0x0047:
        r6 = r11.endOfInput;	 Catch:{ all -> 0x0014 }
        if (r6 == 0) goto L_0x0063;
    L_0x004b:
        r6 = r11.decoder;	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r10 = 1;
        r5 = r6.decode(r9, r4, r10);	 Catch:{ all -> 0x0014 }
        r6 = java.nio.charset.CoderResult.UNDERFLOW;	 Catch:{ all -> 0x0014 }
        if (r5 != r6) goto L_0x005e;
    L_0x0058:
        r6 = r11.decoder;	 Catch:{ all -> 0x0014 }
        r5 = r6.flush(r4);	 Catch:{ all -> 0x0014 }
    L_0x005e:
        r6 = r11.decoder;	 Catch:{ all -> 0x0014 }
        r6.reset();	 Catch:{ all -> 0x0014 }
    L_0x0063:
        r6 = r5.isMalformed();	 Catch:{ all -> 0x0014 }
        if (r6 != 0) goto L_0x006f;
    L_0x0069:
        r6 = r5.isUnmappable();	 Catch:{ all -> 0x0014 }
        if (r6 == 0) goto L_0x0072;
    L_0x006f:
        r5.throwException();	 Catch:{ all -> 0x0014 }
    L_0x0072:
        r6 = r4.position();	 Catch:{ all -> 0x0014 }
        r6 = r6 - r13;
        if (r6 != 0) goto L_0x00f2;
    L_0x0079:
        r6 = r7;
    L_0x007a:
        monitor-exit(r8);	 Catch:{ all -> 0x0014 }
        goto L_0x001e;
    L_0x007c:
        r2 = r6;
        goto L_0x002d;
    L_0x007e:
        r6 = move-exception;
    L_0x007f:
        r6 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r6 = r6.capacity();	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r9.limit();	 Catch:{ all -> 0x0014 }
        r1 = r6 - r9;
        r6 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r6 = r6.arrayOffset();	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r9.limit();	 Catch:{ all -> 0x0014 }
        r3 = r6 + r9;
        r6 = r11.in;	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r9.array();	 Catch:{ all -> 0x0014 }
        r0 = r6.read(r9, r3, r1);	 Catch:{ all -> 0x0014 }
        if (r0 != r7) goto L_0x00ad;
    L_0x00a9:
        r6 = 1;
        r11.endOfInput = r6;	 Catch:{ all -> 0x0014 }
        goto L_0x0043;
    L_0x00ad:
        if (r0 == 0) goto L_0x0043;
    L_0x00af:
        r6 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r9.limit();	 Catch:{ all -> 0x0014 }
        r9 = r9 + r0;
        r6.limit(r9);	 Catch:{ all -> 0x0014 }
        r2 = 0;
    L_0x00bc:
        r6 = r11.decoder;	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r10 = 0;
        r5 = r6.decode(r9, r4, r10);	 Catch:{ all -> 0x0014 }
        r6 = r5.isUnderflow();	 Catch:{ all -> 0x0014 }
        if (r6 == 0) goto L_0x0043;
    L_0x00cb:
        r6 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r6 = r6.limit();	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r9.capacity();	 Catch:{ all -> 0x0014 }
        if (r6 != r9) goto L_0x00ef;
    L_0x00d9:
        r6 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r6.compact();	 Catch:{ all -> 0x0014 }
        r6 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = r9.position();	 Catch:{ all -> 0x0014 }
        r6.limit(r9);	 Catch:{ all -> 0x0014 }
        r6 = r11.bytes;	 Catch:{ all -> 0x0014 }
        r9 = 0;
        r6.position(r9);	 Catch:{ all -> 0x0014 }
    L_0x00ef:
        r2 = 1;
        goto L_0x002d;
    L_0x00f2:
        r6 = r4.position();	 Catch:{ all -> 0x0014 }
        r6 = r6 - r13;
        goto L_0x007a;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.io.InputStreamReader.read(char[], int, int):int");
    }

    private boolean isOpen() {
        return this.in != null;
    }

    public boolean ready() throws IOException {
        boolean z = false;
        synchronized (this.lock) {
            if (this.in == null) {
                throw new IOException("InputStreamReader is closed");
            }
            try {
                if (this.bytes.hasRemaining() || this.in.available() > 0) {
                    z = true;
                }
            } catch (IOException e) {
            }
        }
        return z;
    }
}
