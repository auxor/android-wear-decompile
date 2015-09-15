package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import libcore.icu.ICU;
import libcore.icu.NativeConverter;
import libcore.util.EmptyArray;

final class CharsetEncoderICU extends CharsetEncoder {
    private static final Map<String, byte[]> DEFAULT_REPLACEMENTS;
    private static final int INPUT_OFFSET = 0;
    private static final int INVALID_CHAR_COUNT = 2;
    private static final int OUTPUT_OFFSET = 1;
    private char[] allocatedInput;
    private byte[] allocatedOutput;
    private long converterHandle;
    private int[] data;
    private int inEnd;
    private char[] input;
    private int outEnd;
    private byte[] output;

    static {
        DEFAULT_REPLACEMENTS = new HashMap();
        byte[] questionMark = new byte[OUTPUT_OFFSET];
        questionMark[INPUT_OFFSET] = (byte) 63;
        DEFAULT_REPLACEMENTS.put("UTF-8", questionMark);
        DEFAULT_REPLACEMENTS.put("ISO-8859-1", questionMark);
        DEFAULT_REPLACEMENTS.put("US-ASCII", questionMark);
    }

    public static CharsetEncoderICU newInstance(Charset cs, String icuCanonicalName) {
        long j = 0;
        try {
            j = NativeConverter.openConverter(icuCanonicalName);
            CharsetEncoderICU result = new CharsetEncoderICU(cs, NativeConverter.getAveBytesPerChar(j), (float) NativeConverter.getMaxBytesPerChar(j), makeReplacement(icuCanonicalName, j), j);
            j = 0;
            return result;
        } finally {
            if (j != 0) {
                NativeConverter.closeConverter(j);
            }
        }
    }

    private static byte[] makeReplacement(String icuCanonicalName, long address) {
        byte[] replacement = (byte[]) DEFAULT_REPLACEMENTS.get(icuCanonicalName);
        if (replacement != null) {
            return (byte[]) replacement.clone();
        }
        return NativeConverter.getSubstitutionBytes(address);
    }

    private CharsetEncoderICU(Charset cs, float averageBytesPerChar, float maxBytesPerChar, byte[] replacement, long address) {
        super(cs, averageBytesPerChar, maxBytesPerChar, replacement, true);
        this.data = new int[3];
        this.converterHandle = 0;
        this.input = null;
        this.output = null;
        this.allocatedInput = null;
        this.allocatedOutput = null;
        this.converterHandle = address;
        updateCallback();
    }

    protected void implReplaceWith(byte[] newReplacement) {
        updateCallback();
    }

    protected void implOnMalformedInput(CodingErrorAction newAction) {
        updateCallback();
    }

    protected void implOnUnmappableCharacter(CodingErrorAction newAction) {
        updateCallback();
    }

    private void updateCallback() {
        NativeConverter.setCallbackEncode(this.converterHandle, this);
    }

    protected void implReset() {
        NativeConverter.resetCharToByte(this.converterHandle);
        this.data[INPUT_OFFSET] = INPUT_OFFSET;
        this.data[OUTPUT_OFFSET] = INPUT_OFFSET;
        this.data[INVALID_CHAR_COUNT] = INPUT_OFFSET;
        this.output = null;
        this.input = null;
        this.allocatedInput = null;
        this.allocatedOutput = null;
        this.inEnd = INPUT_OFFSET;
        this.outEnd = INPUT_OFFSET;
    }

    protected CoderResult implFlush(ByteBuffer out) {
        try {
            CoderResult coderResult;
            this.input = EmptyArray.CHAR;
            this.inEnd = INPUT_OFFSET;
            this.data[INPUT_OFFSET] = INPUT_OFFSET;
            this.data[OUTPUT_OFFSET] = getArray(out);
            this.data[INVALID_CHAR_COUNT] = INPUT_OFFSET;
            int error = NativeConverter.encode(this.converterHandle, this.input, this.inEnd, this.output, this.outEnd, this.data, true);
            if (ICU.U_FAILURE(error)) {
                if (error == 15) {
                    coderResult = CoderResult.OVERFLOW;
                } else if (error == 11) {
                    if (this.data[INVALID_CHAR_COUNT] > 0) {
                        coderResult = CoderResult.malformedForLength(this.data[INVALID_CHAR_COUNT]);
                        setPosition(out);
                        implReset();
                    }
                }
                return coderResult;
            }
            coderResult = CoderResult.UNDERFLOW;
            setPosition(out);
            implReset();
            return coderResult;
        } finally {
            setPosition(out);
            implReset();
        }
    }

    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        if (!in.hasRemaining()) {
            return CoderResult.UNDERFLOW;
        }
        this.data[INPUT_OFFSET] = getArray(in);
        this.data[OUTPUT_OFFSET] = getArray(out);
        this.data[INVALID_CHAR_COUNT] = INPUT_OFFSET;
        try {
            int error = NativeConverter.encode(this.converterHandle, this.input, this.inEnd, this.output, this.outEnd, this.data, false);
            CoderResult coderResult;
            if (!ICU.U_FAILURE(error)) {
                coderResult = CoderResult.UNDERFLOW;
                setPosition(in);
                setPosition(out);
                return coderResult;
            } else if (error == 15) {
                coderResult = CoderResult.OVERFLOW;
                return coderResult;
            } else if (error == 10) {
                coderResult = CoderResult.unmappableForLength(this.data[INVALID_CHAR_COUNT]);
                setPosition(in);
                setPosition(out);
                return coderResult;
            } else if (error == 12) {
                coderResult = CoderResult.malformedForLength(this.data[INVALID_CHAR_COUNT]);
                setPosition(in);
                setPosition(out);
                return coderResult;
            } else {
                throw new AssertionError(error);
            }
        } finally {
            setPosition(in);
            setPosition(out);
        }
    }

    protected void finalize() throws Throwable {
        try {
            NativeConverter.closeConverter(this.converterHandle);
            this.converterHandle = 0;
        } finally {
            super.finalize();
        }
    }

    private int getArray(ByteBuffer out) {
        if (out.hasArray()) {
            this.output = out.array();
            this.outEnd = out.arrayOffset() + out.limit();
            return out.arrayOffset() + out.position();
        }
        this.outEnd = out.remaining();
        if (this.allocatedOutput == null || this.outEnd > this.allocatedOutput.length) {
            this.allocatedOutput = new byte[this.outEnd];
        }
        this.output = this.allocatedOutput;
        return INPUT_OFFSET;
    }

    private int getArray(CharBuffer in) {
        if (in.hasArray()) {
            this.input = in.array();
            this.inEnd = in.arrayOffset() + in.limit();
            return in.arrayOffset() + in.position();
        }
        this.inEnd = in.remaining();
        if (this.allocatedInput == null || this.inEnd > this.allocatedInput.length) {
            this.allocatedInput = new char[this.inEnd];
        }
        int pos = in.position();
        in.get(this.allocatedInput, INPUT_OFFSET, this.inEnd);
        in.position(pos);
        this.input = this.allocatedInput;
        return INPUT_OFFSET;
    }

    private void setPosition(ByteBuffer out) {
        if (out.hasArray()) {
            out.position(this.data[OUTPUT_OFFSET] - out.arrayOffset());
        } else {
            out.put(this.output, INPUT_OFFSET, this.data[OUTPUT_OFFSET]);
        }
        this.output = null;
    }

    private void setPosition(CharBuffer in) {
        int position = (in.position() + this.data[INPUT_OFFSET]) - this.data[INVALID_CHAR_COUNT];
        if (position < 0) {
            position = INPUT_OFFSET;
        }
        in.position(position);
        this.input = null;
    }
}
