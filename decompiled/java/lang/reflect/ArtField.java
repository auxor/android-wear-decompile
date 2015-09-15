package java.lang.reflect;

import com.android.dex.Dex;

public final class ArtField {
    private int accessFlags;
    private Class<?> declaringClass;
    private int fieldDexIndex;
    private int offset;

    private ArtField() {
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    int getDexFieldIndex() {
        return this.fieldDexIndex;
    }

    int getOffset() {
        return this.offset;
    }

    public String getName() {
        if (this.fieldDexIndex != -1) {
            Dex dex = this.declaringClass.getDex();
            return this.declaringClass.getDexCacheString(dex, dex.nameIndexFromFieldIndex(this.fieldDexIndex));
        } else if (this.declaringClass.isProxy()) {
            return "throws";
        } else {
            throw new AssertionError();
        }
    }

    Class<?> getDeclaringClass() {
        return this.declaringClass;
    }

    Class<?> getType() {
        if (this.fieldDexIndex != -1) {
            Dex dex = this.declaringClass.getDex();
            return this.declaringClass.getDexCacheType(dex, dex.typeIndexFromFieldIndex(this.fieldDexIndex));
        } else if (this.declaringClass.isProxy()) {
            return Class[][].class;
        } else {
            throw new AssertionError();
        }
    }
}
