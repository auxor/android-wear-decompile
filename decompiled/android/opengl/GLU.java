package android.opengl;

import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import javax.microedition.khronos.opengles.GL10;

public class GLU {
    private static final float[] sScratch;

    public static String gluErrorString(int error) {
        switch (error) {
            case Toast.LENGTH_SHORT /*0*/:
                return "no error";
            case GLES20.GL_INVALID_ENUM /*1280*/:
                return "invalid enum";
            case GLES20.GL_INVALID_VALUE /*1281*/:
                return "invalid value";
            case GLES20.GL_INVALID_OPERATION /*1282*/:
                return "invalid operation";
            case GLES31Ext.GL_STACK_OVERFLOW_KHR /*1283*/:
                return "stack overflow";
            case GLES31Ext.GL_STACK_UNDERFLOW_KHR /*1284*/:
                return "stack underflow";
            case GLES20.GL_OUT_OF_MEMORY /*1285*/:
                return "out of memory";
            default:
                return null;
        }
    }

    public static void gluLookAt(GL10 gl, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        float[] scratch = sScratch;
        synchronized (scratch) {
            Matrix.setLookAtM(scratch, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
            gl.glMultMatrixf(scratch, 0);
        }
    }

    public static void gluOrtho2D(GL10 gl, float left, float right, float bottom, float top) {
        gl.glOrthof(left, right, bottom, top, LayoutParams.BRIGHTNESS_OVERRIDE_NONE, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
    }

    public static void gluPerspective(GL10 gl, float fovy, float aspect, float zNear, float zFar) {
        float top = zNear * ((float) Math.tan(((double) fovy) * 0.008726646259971648d));
        float bottom = -top;
        gl.glFrustumf(bottom * aspect, top * aspect, bottom, top, zNear, zFar);
    }

    public static int gluProject(float objX, float objY, float objZ, float[] model, int modelOffset, float[] project, int projectOffset, int[] view, int viewOffset, float[] win, int winOffset) {
        float[] scratch = sScratch;
        synchronized (scratch) {
            Matrix.multiplyMM(scratch, 0, project, projectOffset, model, modelOffset);
            scratch[16] = objX;
            scratch[17] = objY;
            scratch[18] = objZ;
            scratch[19] = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            Matrix.multiplyMV(scratch, 20, scratch, 0, scratch, 16);
            float w = scratch[23];
            if (w == 0.0f) {
                return 0;
            }
            float rw = LayoutParams.BRIGHTNESS_OVERRIDE_FULL / w;
            win[winOffset] = ((float) view[viewOffset]) + ((((float) view[viewOffset + 2]) * ((scratch[20] * rw) + LayoutParams.BRIGHTNESS_OVERRIDE_FULL)) * 0.5f);
            win[winOffset + 1] = ((float) view[viewOffset + 1]) + ((((float) view[viewOffset + 3]) * ((scratch[21] * rw) + LayoutParams.BRIGHTNESS_OVERRIDE_FULL)) * 0.5f);
            win[winOffset + 2] = ((scratch[22] * rw) + LayoutParams.BRIGHTNESS_OVERRIDE_FULL) * 0.5f;
            return 1;
        }
    }

    public static int gluUnProject(float winX, float winY, float winZ, float[] model, int modelOffset, float[] project, int projectOffset, int[] view, int viewOffset, float[] obj, int objOffset) {
        float[] scratch = sScratch;
        synchronized (scratch) {
            Matrix.multiplyMM(scratch, 0, project, projectOffset, model, modelOffset);
            if (Matrix.invertM(scratch, 16, scratch, 0)) {
                scratch[0] = ((2.0f * (winX - ((float) view[viewOffset + 0]))) / ((float) view[viewOffset + 2])) - LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                scratch[1] = ((2.0f * (winY - ((float) view[viewOffset + 1]))) / ((float) view[viewOffset + 3])) - LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                scratch[2] = (2.0f * winZ) - LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                scratch[3] = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                Matrix.multiplyMV(obj, objOffset, scratch, 16, scratch, 0);
                return 1;
            }
            return 0;
        }
    }

    static {
        sScratch = new float[32];
    }
}
