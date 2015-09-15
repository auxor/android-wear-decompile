package com.google.android.gles_jni;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL;

public class EGLContextImpl extends EGLContext {
    long mEGLContext;
    private GLImpl mGLContext;

    public EGLContextImpl(long ctx) {
        this.mEGLContext = ctx;
        this.mGLContext = new GLImpl();
    }

    public GL getGL() {
        return this.mGLContext;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (this.mEGLContext != ((EGLContextImpl) o).mEGLContext) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((int) (this.mEGLContext ^ (this.mEGLContext >>> 32))) + 527;
    }
}
