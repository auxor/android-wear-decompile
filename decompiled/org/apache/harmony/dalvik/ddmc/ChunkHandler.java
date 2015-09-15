package org.apache.harmony.dalvik.ddmc;

import dalvik.bytecode.Opcodes;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.xmlpull.v1.XmlPullParser;

public abstract class ChunkHandler {
    public static final int CHUNK_FAIL;
    public static final ByteOrder CHUNK_ORDER;

    public abstract void connected();

    public abstract void disconnected();

    public abstract Chunk handleChunk(Chunk chunk);

    static {
        CHUNK_ORDER = ByteOrder.BIG_ENDIAN;
        CHUNK_FAIL = type("FAIL");
    }

    public static Chunk createFailChunk(int errorCode, String msg) {
        if (msg == null) {
            msg = XmlPullParser.NO_NAMESPACE;
        }
        ByteBuffer out = ByteBuffer.allocate((msg.length() * 2) + 8);
        out.order(CHUNK_ORDER);
        out.putInt(errorCode);
        out.putInt(msg.length());
        putString(out, msg);
        return new Chunk(CHUNK_FAIL, out);
    }

    public static ByteBuffer wrapChunk(Chunk request) {
        ByteBuffer in = ByteBuffer.wrap(request.data, request.offset, request.length);
        in.order(CHUNK_ORDER);
        return in;
    }

    public static String getString(ByteBuffer buf, int len) {
        char[] data = new char[len];
        for (int i = 0; i < len; i++) {
            data[i] = buf.getChar();
        }
        return new String(data);
    }

    public static void putString(ByteBuffer buf, String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            buf.putChar(str.charAt(i));
        }
    }

    public static int type(String typeName) {
        if (typeName.length() != 4) {
            throw new IllegalArgumentException("Bad type name: " + typeName);
        }
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | (typeName.charAt(i) & Opcodes.OP_CONST_CLASS_JUMBO);
        }
        return result;
    }

    public static String name(int type) {
        return new String(new char[]{(char) ((type >> 24) & Opcodes.OP_CONST_CLASS_JUMBO), (char) ((type >> 16) & Opcodes.OP_CONST_CLASS_JUMBO), (char) ((type >> 8) & Opcodes.OP_CONST_CLASS_JUMBO), (char) (type & Opcodes.OP_CONST_CLASS_JUMBO)});
    }
}
