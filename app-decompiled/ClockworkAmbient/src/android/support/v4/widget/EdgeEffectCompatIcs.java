package android.support.v4.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.EdgeEffect;

class EdgeEffectCompatIcs {
    public static boolean draw(Object obj, Canvas canvas) {
        EdgeEffect edgeEffect = (EdgeEffect) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void finish(Object obj) {
        EdgeEffect edgeEffect = (EdgeEffect) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static boolean isFinished(Object obj) {
        EdgeEffect edgeEffect = (EdgeEffect) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static Object newEdgeEffect(Context context) {
        return new EdgeEffect(context);
    }

    public static boolean onAbsorb(Object obj, int i) {
        EdgeEffect edgeEffect = (EdgeEffect) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static boolean onPull(Object obj, float f) {
        EdgeEffect edgeEffect = (EdgeEffect) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static boolean onRelease(Object obj) {
        EdgeEffect edgeEffect = (EdgeEffect) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void setSize(Object obj, int i, int i2) {
        EdgeEffect edgeEffect = (EdgeEffect) obj;
        throw new VerifyError("bad dex opcode");
    }
}
