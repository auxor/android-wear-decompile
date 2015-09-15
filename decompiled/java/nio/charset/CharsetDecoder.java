package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public abstract class CharsetDecoder {
    private static final String END_OF_INPUT = "END_OF_INPUT";
    private static final String FLUSHED = "FLUSHED";
    private static final String ONGOING = "ONGOING";
    private static final String RESET = "RESET";
    private final float averageCharsPerByte;
    private final Charset charset;
    private CodingErrorAction malformedInputAction;
    private final float maxCharsPerByte;
    private String replacementChars;
    private String state;
    private CodingErrorAction unmappableCharacterAction;

    protected abstract CoderResult decodeLoop(ByteBuffer byteBuffer, CharBuffer charBuffer);

    protected CharsetDecoder(Charset charset, float averageCharsPerByte, float maxCharsPerByte) {
        this.replacementChars = "\ufffd";
        this.state = RESET;
        this.malformedInputAction = CodingErrorAction.REPORT;
        this.unmappableCharacterAction = CodingErrorAction.REPORT;
        if (averageCharsPerByte <= 0.0f || maxCharsPerByte <= 0.0f) {
            throw new IllegalArgumentException("averageCharsPerByte and maxCharsPerByte must be positive");
        } else if (averageCharsPerByte > maxCharsPerByte) {
            throw new IllegalArgumentException("averageCharsPerByte is greater than maxCharsPerByte");
        } else {
            this.averageCharsPerByte = averageCharsPerByte;
            this.maxCharsPerByte = maxCharsPerByte;
            this.charset = charset;
        }
    }

    public final float averageCharsPerByte() {
        return this.averageCharsPerByte;
    }

    public final Charset charset() {
        return this.charset;
    }

    public final CharBuffer decode(ByteBuffer in) throws CharacterCodingException {
        CharBuffer out = CharBuffer.allocate((int) (((float) in.remaining()) * this.averageCharsPerByte));
        reset();
        while (this.state != FLUSHED) {
            CoderResult result = decode(in, out, true);
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
        if (result.isMalformed() && this.malformedInputAction == CodingErrorAction.REPORT) {
            throw new MalformedInputException(result.length());
        } else if (result.isUnmappable() && this.unmappableCharacterAction == CodingErrorAction.REPORT) {
            throw new UnmappableCharacterException(result.length());
        }
    }

    private CharBuffer allocateMore(CharBuffer output) {
        if (output.capacity() == 0) {
            return CharBuffer.allocate(1);
        }
        CharBuffer result = CharBuffer.allocate(output.capacity() * 2);
        output.flip();
        result.put(output);
        return result;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.nio.charset.CoderResult decode(java.nio.ByteBuffer r6, java.nio.CharBuffer r7, boolean r8) {
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
        r2 = r5.decodeLoop(r6, r7);	 Catch:{ BufferOverflowException -> 0x0048, BufferUnderflowException -> 0x004f }
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
        if (r0 != r3) goto L_0x0079;
    L_0x0065:
        r3 = r7.remaining();
        r4 = r5.replacementChars;
        r4 = r4.length();
        if (r3 >= r4) goto L_0x0074;
    L_0x0071:
        r3 = java.nio.charset.CoderResult.OVERFLOW;
        goto L_0x0044;
    L_0x0074:
        r3 = r5.replacementChars;
        r7.put(r3);
    L_0x0079:
        r3 = r6.position();
        r4 = r2.length();
        r3 = r3 + r4;
        r6.position(r3);
        goto L_0x001f;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.charset.CharsetDecoder.decode(java.nio.ByteBuffer, java.nio.CharBuffer, boolean):java.nio.charset.CoderResult");
    }

    public Charset detectedCharset() {
        throw new UnsupportedOperationException();
    }

    public final CoderResult flush(CharBuffer out) {
        if (this.state == FLUSHED || this.state == END_OF_INPUT) {
            CoderResult result = implFlush(out);
            if (result == CoderResult.UNDERFLOW) {
                this.state = FLUSHED;
            }
            return result;
        }
        throw illegalStateException();
    }

    protected CoderResult implFlush(CharBuffer out) {
        return CoderResult.UNDERFLOW;
    }

    protected void implOnMalformedInput(CodingErrorAction newAction) {
    }

    protected void implOnUnmappableCharacter(CodingErrorAction newAction) {
    }

    protected void implReplaceWith(String newReplacement) {
    }

    protected void implReset() {
    }

    public boolean isAutoDetecting() {
        return false;
    }

    public boolean isCharsetDetected() {
        throw new UnsupportedOperationException();
    }

    public CodingErrorAction malformedInputAction() {
        return this.malformedInputAction;
    }

    public final float maxCharsPerByte() {
        return this.maxCharsPerByte;
    }

    public final CharsetDecoder onMalformedInput(CodingErrorAction newAction) {
        if (newAction == null) {
            throw new IllegalArgumentException("newAction == null");
        }
        this.malformedInputAction = newAction;
        implOnMalformedInput(newAction);
        return this;
    }

    public final CharsetDecoder onUnmappableCharacter(CodingErrorAction newAction) {
        if (newAction == null) {
            throw new IllegalArgumentException("newAction == null");
        }
        this.unmappableCharacterAction = newAction;
        implOnUnmappableCharacter(newAction);
        return this;
    }

    public final String replacement() {
        return this.replacementChars;
    }

    public final CharsetDecoder replaceWith(String replacement) {
        if (replacement == null) {
            throw new IllegalArgumentException("replacement == null");
        } else if (replacement.isEmpty()) {
            throw new IllegalArgumentException("replacement.isEmpty()");
        } else if (((float) replacement.length()) > maxCharsPerByte()) {
            throw new IllegalArgumentException("replacement length > maxCharsPerByte: " + replacement.length() + " > " + maxCharsPerByte());
        } else {
            this.replacementChars = replacement;
            implReplaceWith(replacement);
            return this;
        }
    }

    public final CharsetDecoder reset() {
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
