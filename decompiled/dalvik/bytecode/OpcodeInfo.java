package dalvik.bytecode;

import com.android.dex.DexFormat;

public final class OpcodeInfo {
    public static final int MAXIMUM_PACKED_VALUE;
    public static final int MAXIMUM_VALUE;

    static {
        MAXIMUM_VALUE = DexFormat.MAX_TYPE_IDX;
        MAXIMUM_PACKED_VALUE = Opcodes.OP_CONST_CLASS_JUMBO;
    }

    public static boolean isInvoke(int packedOpcode) {
        return false;
    }

    private OpcodeInfo() {
    }
}
