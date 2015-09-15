package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

public abstract class CharsetEncoder {
    private static final String END_OF_INPUT = "END_OF_INPUT";
    private static final String FLUSHED = "FLUSHED";
    private static final String ONGOING = "ONGOING";
    private static final String RESET = "RESET";
    private final float averageBytesPerChar;
    private final Charset charset;
    private CharsetDecoder decoder;
    private CodingErrorAction malformedInputAction;
    private final float maxBytesPerChar;
    private byte[] replacementBytes;
    private String state;
    private CodingErrorAction unmappableCharacterAction;

    protected abstract CoderResult encodeLoop(CharBuffer charBuffer, ByteBuffer byteBuffer);

    protected CharsetEncoder(Charset cs, float averageBytesPerChar, float maxBytesPerChar) {
        this(cs, averageBytesPerChar, maxBytesPerChar, new byte[]{(byte) 63});
    }

    protected CharsetEncoder(Charset cs, float averageBytesPerChar, float maxBytesPerChar, byte[] replacement) {
        this(cs, averageBytesPerChar, maxBytesPerChar, replacement, false);
    }

    CharsetEncoder(Charset cs, float averageBytesPerChar, float maxBytesPerChar, byte[] replacement, boolean trusted) {
        this.state = RESET;
        this.malformedInputAction = CodingErrorAction.REPORT;
        this.unmappableCharacterAction = CodingErrorAction.REPORT;
        if (averageBytesPerChar <= 0.0f || maxBytesPerChar <= 0.0f) {
            throw new IllegalArgumentException("averageBytesPerChar and maxBytesPerChar must both be positive");
        } else if (averageBytesPerChar > maxBytesPerChar) {
            throw new IllegalArgumentException("averageBytesPerChar is greater than maxBytesPerChar");
        } else {
            this.charset = cs;
            this.averageBytesPerChar = averageBytesPerChar;
            this.maxBytesPerChar = maxBytesPerChar;
            if (trusted) {
                this.replacementBytes = replacement;
            } else {
                replaceWith(replacement);
            }
        }
    }

    public final float averageBytesPerChar() {
        return this.averageBytesPerChar;
    }

    public boolean canEncode(char c) {
        return canEncode(CharBuffer.wrap(new char[]{c}));
    }

    public boolean canEncode(CharSequence sequence) {
        CharBuffer cb;
        if (sequence instanceof CharBuffer) {
            cb = ((CharBuffer) sequence).duplicate();
        } else {
            cb = CharBuffer.wrap(sequence);
        }
        if (this.state == FLUSHED) {
            reset();
        }
        if (this.state != RESET) {
            throw illegalStateException();
        }
        boolean z;
        CodingErrorAction originalMalformedInputAction = this.malformedInputAction;
        CodingErrorAction originalUnmappableCharacterAction = this.unmappableCharacterAction;
        onMalformedInput(CodingErrorAction.REPORT);
        onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            encode(cb);
            z = true;
        } catch (CharacterCodingException e) {
            z = false;
        } finally {
            onMalformedInput(originalMalformedInputAction);
            onUnmappableCharacter(originalUnmappableCharacterAction);
            reset();
        }
        return z;
    }

    public final Charset charset() {
        return this.charset;
    }

    public final ByteBuffer encode(CharBuffer in) throws CharacterCodingException {
        ByteBuffer out = ByteBuffer.allocate((int) (((float) in.remaining()) * this.averageBytesPerChar));
        reset();
        while (this.state != FLUSHED) {
            CoderResult result = encode(in, out, true);
            if (result == CoderResult.OVERFLOW) {
                out = allocateMore(out);
            } else {
                checkCoderResult(result);
                result = flush(out);
                if (result == CoderResult.OVERFLOW) {
                    out = allocateMore(out);
                } else {
                    checkCoderResult(result);
                }
            }
        }
        out.flip();
        return out;
    }

    private void checkCoderResult(CoderResult result) throws CharacterCodingException {
        if (this.malformedInputAction == CodingErrorAction.REPORT && result.isMalformed()) {
            throw new MalformedInputException(result.length());
        } else if (this.unmappableCharacterAction == CodingErrorAction.REPORT && result.isUnmappable()) {
            throw new UnmappableCharacterException(result.length());
        }
    }

    private ByteBuffer allocateMore(ByteBuffer output) {
        if (output.capacity() == 0) {
            return ByteBuffer.allocate(1);
        }
        ByteBuffer result = ByteBuffer.allocate(output.capacity() * 2);
        output.flip();
        result.put(output);
        return result;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.nio.charset.CoderResult encode(java.nio.CharBuffer r6, java.nio.ByteBuffer r7, boolean r8) {
        /*
        r5 = this;
        r3 = r5.state;
        r4 = "RESET";
        if (r3 == r4) goto L_0x0019;
    L_0x0006:
        r3 = r5.state;
        r4 = "ONGOING";
        if (r3 == r4) goto L_0x0019;
    L_0x000c:
        if (r8 == 0) goto L_0x0014;
    L_0x000e:
        r3 = r5.state;
        r4 = "END_OF_INPUT";
        if (r3 == r4) goto L_0x0019;
    L_0x0014:
        r3 = r5.illegalStateException();
        throw r3;
    L_0x0019:
        if (r8 == 0) goto L_0x0045;
    L_0x001b:
        r3 = "END_OF_INPUT";
    L_0x001d:
        r5.state = r3;
    L_0x001f:
        r2 = r5.encodeLoop(r6, r7);	 Catch:{ BufferOverflowException -> 0x0048, BufferUnderflowException -> 0x004f }
        r3 = java.nio.charset.CoderResult.UNDERFLOW;
        if (r2 != r3) goto L_0x0058;
    L_0x0027:
        if (r8 == 0) goto L_0x0056;
    L_0x0029:
        r3 = r6.hasRemaining();
        if (r3 == 0) goto L_0x0056;
    L_0x002f:
        r3 = r6.remaining();
        r2 = java.nio.charset.CoderResult.malformedForLength(r3);
    L_0x0037:
        r3 = r2.isUnmappable();
        if (r3 == 0) goto L_0x005e;
    L_0x003d:
        r0 = r5.unmappableCharacterAction;
    L_0x003f:
        r3 = java.nio.charset.CodingErrorAction.REPORT;
        if (r0 != r3) goto L_0x0061;
    L_0x0043:
        r3 = r2;
    L_0x0044:
        return r3;
    L_0x0045:
        r3 = "ONGOING";
        goto L_0x001d;
    L_0x0048:
        r1 = move-exception;
        r3 = new java.nio.charset.CoderMalfunctionError;
        r3.<init>(r1);
        throw r3;
    L_0x004f:
        r1 = move-exception;
        r3 = new java.nio.charset.CoderMalfunctionError;
        r3.<init>(r1);
        throw r3;
    L_0x0056:
        r3 = r2;
        goto L_0x0044;
    L_0x0058:
        r3 = java.nio.charset.CoderResult.OVERFLOW;
        if (r2 != r3) goto L_0x0037;
    L_0x005c:
        r3 = r2;
        goto L_0x0044;
    L_0x005e:
        r0 = r5.malformedInputAction;
        goto L_0x003f;
    L_0x0061:
        r3 = java.nio.charset.CodingErrorAction.REPLACE;
        if (r0 != r3) goto L_0x0076;
    L_0x0065:
        r3 = r7.remaining();
        r4 = r5.replacementBytes;
        r4 = r4.length;
        if (r3 >= r4) goto L_0x0071;
    L_0x006e:
        r3 = java.nio.charset.CoderResult.OVERFLOW;
        goto L_0x0044;
    L_0x0071:
        r3 = r5.replacementBytes;
        r7.put(r3);
    L_0x0076:
        r3 = r6.position();
        r4 = r2.length();
        r3 = r3 + r4;
        r6.position(r3);
        goto L_0x001f;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.charset.CharsetEncoder.encode(java.nio.CharBuffer, java.nio.ByteBuffer, boolean):java.nio.charset.CoderResult");
    }

    public final CoderResult flush(ByteBuffer out) {
        if (this.state == FLUSHED || this.state == END_OF_INPUT) {
            CoderResult result = implFlush(out);
            if (result == CoderResult.UNDERFLOW) {
                this.state = FLUSHED;
            }
            return result;
        }
        throw illegalStateException();
    }

    protected CoderResult implFlush(ByteBuffer out) {
        return CoderResult.UNDERFLOW;
    }

    protected void implOnMalformedInput(CodingErrorAction newAction) {
    }

    protected void implOnUnmappableCharacter(CodingErrorAction newAction) {
    }

    protected void implReplaceWith(byte[] newReplacement) {
    }

    protected void implReset() {
    }

    public boolean isLegalReplacement(byte[] replacement) {
        if (this.decoder == null) {
            this.decoder = this.charset.newDecoder();
            this.decoder.onMalformedInput(CodingErrorAction.REPORT);
            this.decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        }
        if (this.decoder.decode(ByteBuffer.wrap(replacement), CharBuffer.allocate((int) (((float) replacement.length) * this.decoder.maxCharsPerByte())), true).isError()) {
            return false;
        }
        return true;
    }

    public CodingErrorAction malformedInputAction() {
        return this.malformedInputAction;
    }

    public final float maxBytesPerChar() {
        return this.maxBytesPerChar;
    }

    public final CharsetEncoder onMalformedInput(CodingErrorAction newAction) {
        if (newAction == null) {
            throw new IllegalArgumentException("newAction == null");
        }
        this.malformedInputAction = newAction;
        implOnMalformedInput(newAction);
        return this;
    }

    public final CharsetEncoder onUnmappableCharacter(CodingErrorAction newAction) {
        if (newAction == null) {
            throw new IllegalArgumentException("newAction == null");
        }
        this.unmappableCharacterAction = newAction;
        implOnUnmappableCharacter(newAction);
        return this;
    }

    public final byte[] replacement() {
        return this.replacementBytes;
    }

    public final CharsetEncoder replaceWith(byte[] replacement) {
        if (replacement == null) {
            throw new IllegalArgumentException("replacement == null");
        } else if (replacement.length == 0) {
            throw new IllegalArgumentException("replacement.length == 0");
        } else if (((float) replacement.length) > maxBytesPerChar()) {
            throw new IllegalArgumentException("replacement.length > maxBytesPerChar: " + replacement.length + " > " + maxBytesPerChar());
        } else if (isLegalReplacement(replacement)) {
            this.replacementBytes = replacement;
            implReplaceWith(this.replacementBytes);
            return this;
        } else {
            throw new IllegalArgumentException("Bad replacement: " + Arrays.toString(replacement));
        }
    }

    public final CharsetEncoder reset() {
        this.state = RESET;
        implReset();
        return this;
    }

    public CodingErrorAction unmappableCharacterAction() {
        return this.unmappableCharacterAction;
    }

    private IllegalStateException illegalStateException() {
        throw new IllegalStateException("State: " + this.state);
    }
}
