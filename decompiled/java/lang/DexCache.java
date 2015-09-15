package java.lang;

import com.android.dex.Dex;
import java.lang.reflect.ArtField;
import java.lang.reflect.ArtMethod;

final class DexCache {
    private volatile Dex dex;
    private long dexFile;
    String location;
    ArtField[] resolvedFields;
    ArtMethod[] resolvedMethods;
    Class[] resolvedTypes;
    String[] strings;

    private native Dex getDexNative();

    private DexCache() {
    }

    Dex getDex() {
        Dex result = this.dex;
        if (result == null) {
            synchronized (this) {
                result = this.dex;
                if (result == null) {
                    result = getDexNative();
                    this.dex = result;
                }
            }
        }
        return result;
    }
}
