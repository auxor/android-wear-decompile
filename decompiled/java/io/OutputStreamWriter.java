package java.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import libcore.icu.DateIntervalFormat;

public class OutputStreamWriter extends Writer {
    private ByteBuffer bytes;
    private CharsetEncoder encoder;
    private final OutputStream out;

    public OutputStreamWriter(OutputStream out) {
        this(out, Charset.defaultCharset());
    }

    public OutputStreamWriter(OutputStream out, String charsetName) throws UnsupportedEncodingException {
        super(out);
        this.bytes = ByteBuffer.allocate(DateIntervalFormat.FORMAT_UTC);
        if (charsetName == null) {
            throw new NullPointerException("charsetName == null");
        }
        this.out = out;
        try {
            this.encoder = Charset.forName(charsetName).newEncoder();
            this.encoder.onMalformedInput(CodingErrorAction.REPLACE);
            this.encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        } catch (Exception e) {
            throw new UnsupportedEncodingException(charsetName);
        }
    }

    public OutputStreamWriter(OutputStream out, Charset cs) {
        super(out);
        this.bytes = ByteBuffer.allocate(DateIntervalFormat.FORMAT_UTC);
        this.out = out;
        this.encoder = cs.newEncoder();
        this.encoder.onMalformedInput(CodingErrorAction.REPLACE);
        this.encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
    }

    public OutputStreamWriter(OutputStream out, CharsetEncoder charsetEncoder) {
        super(out);
        this.bytes = ByteBuffer.allocate(DateIntervalFormat.FORMAT_UTC);
        charsetEncoder.charset();
        this.out = out;
        this.encoder = charsetEncoder;
    }

    public void close() throws IOException {
        synchronized (this.lock) {
            if (this.encoder != null) {
                drainEncoder();
                flushBytes(false);
                this.out.close();
                this.encoder = null;
                this.bytes = null;
            }
        }
    }

    public void flush() throws IOException {
        flushBytes(true);
    }

    private void flushBytes(boolean flushUnderlyingStream) throws IOException {
        synchronized (this.lock) {
            checkStatus();
            int position = this.bytes.position();
            if (position > 0) {
                this.bytes.flip();
                this.out.write(this.bytes.array(), this.bytes.arrayOffset(), position);
                this.bytes.clear();
            }
            if (flushUnderlyingStream) {
                this.out.flush();
            }
        }
    }

    private void convert(CharBuffer chars) throws IOException {
        while (true) {
            CoderResult result = this.encoder.encode(chars, this.bytes, false);
            if (!result.isOverflow()) {
                break;
            }
            flushBytes(false);
        }
        if (result.isError()) {
            result.throwException();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void drainEncoder() throws java.io.IOException {
        /*
        r6 = this;
        r5 = 0;
        r0 = java.nio.CharBuffer.allocate(r5);
    L_0x0005:
        r2 = r6.encoder;
        r3 = r6.bytes;
        r4 = 1;
        r1 = r2.encode(r0, r3, r4);
        r2 = r1.isError();
        if (r2 == 0) goto L_0x0037;
    L_0x0014:
        r1.throwException();
    L_0x0017:
        r2 = r6.encoder;
        r3 = r6.bytes;
        r1 = r2.flush(r3);
    L_0x001f:
        r2 = r1.isUnderflow();
        if (r2 != 0) goto L_0x0045;
    L_0x0025:
        r2 = r1.isOverflow();
        if (r2 == 0) goto L_0x0041;
    L_0x002b:
        r6.flushBytes(r5);
        r2 = r6.encoder;
        r3 = r6.bytes;
        r1 = r2.flush(r3);
        goto L_0x001f;
    L_0x0037:
        r2 = r1.isOverflow();
        if (r2 == 0) goto L_0x0017;
    L_0x003d:
        r6.flushBytes(r5);
        goto L_0x0005;
    L_0x0041:
        r1.throwException();
        goto L_0x001f;
    L_0x0045:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.io.OutputStreamWriter.drainEncoder():void");
    }

    private void checkStatus() throws IOException {
        if (this.encoder == null) {
            throw new IOException("OutputStreamWriter is closed");
        }
    }

    public String getEncoding() {
        if (this.encoder == null) {
            return null;
        }
        return this.encoder.charset().name();
    }

    public void write(char[] buffer, int offset, int count) throws IOException {
        synchronized (this.lock) {
            checkStatus();
            Arrays.checkOffsetAndCount(buffer.length, offset, count);
            convert(CharBuffer.wrap(buffer, offset, count));
        }
    }

    public void write(int oneChar) throws IOException {
        synchronized (this.lock) {
            checkStatus();
            convert(CharBuffer.wrap(new char[]{(char) oneChar}));
        }
    }

    public void write(String str, int offset, int count) throws IOException {
        synchronized (this.lock) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(str, offset, count);
            } else if (str == null) {
                throw new NullPointerException("str == null");
            } else if ((offset | count) < 0 || offset > str.length() - count) {
                throw new StringIndexOutOfBoundsException(str, offset, count);
            } else {
                checkStatus();
                convert(CharBuffer.wrap((CharSequence) str, offset, count + offset));
            }
        }
    }

    boolean checkError() {
        return this.out.checkError();
    }
}
