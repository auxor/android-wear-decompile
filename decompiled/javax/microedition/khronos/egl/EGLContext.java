package javax.microedition.khronos.egl;

import com.google.android.gles_jni.EGLImpl;
import javax.microedition.khronos.opengles.GL;

public abstract class EGLContext {
    private static final EGL EGL_INSTANCE;

    public abstract GL getGL();

    static {
        EGL_INSTANCE = new EGLImpl();
    }

    public static EGL getEGL() {
        return EGL_INSTANCE;
    }
}
