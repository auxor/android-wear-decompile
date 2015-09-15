package java.nio;

final class NIOAccess {
    NIOAccess() {
    }

    static long getBasePointer(Buffer b) {
        long address = b.effectiveDirectAddress;
        if (address == 0) {
            return 0;
        }
        return ((long) (b.position << b._elementSizeShift)) + address;
    }

    static Object getBaseArray(Buffer b) {
        return b.hasArray() ? b.array() : null;
    }

    static int getBaseArrayOffset(Buffer b) {
        return b.hasArray() ? (b.arrayOffset() + b.position) << b._elementSizeShift : 0;
    }
}
