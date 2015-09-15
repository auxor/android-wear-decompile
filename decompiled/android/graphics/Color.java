package android.graphics;

import android.hardware.Camera.Parameters;
import android.util.MathUtils;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import com.android.internal.util.XmlUtils;
import java.util.HashMap;
import java.util.Locale;

public class Color {
    public static final int BLACK = -16777216;
    public static final int BLUE = -16776961;
    public static final int CYAN = -16711681;
    public static final int DKGRAY = -12303292;
    public static final int GRAY = -7829368;
    public static final int GREEN = -16711936;
    public static final int LTGRAY = -3355444;
    public static final int MAGENTA = -65281;
    public static final int RED = -65536;
    public static final int TRANSPARENT = 0;
    public static final int WHITE = -1;
    public static final int YELLOW = -256;
    private static final HashMap<String, Integer> sColorNameMap;

    private static native int nativeHSVToColor(int i, float[] fArr);

    private static native void nativeRGBToHSV(int i, int i2, int i3, float[] fArr);

    public static int alpha(int color) {
        return color >>> 24;
    }

    public static int red(int color) {
        return (color >> 16) & EditorInfo.IME_MASK_ACTION;
    }

    public static int green(int color) {
        return (color >> 8) & EditorInfo.IME_MASK_ACTION;
    }

    public static int blue(int color) {
        return color & EditorInfo.IME_MASK_ACTION;
    }

    public static int rgb(int red, int green, int blue) {
        return ((BLACK | (red << 16)) | (green << 8)) | blue;
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (((alpha << 24) | (red << 16)) | (green << 8)) | blue;
    }

    public static float hue(int color) {
        int r = (color >> 16) & EditorInfo.IME_MASK_ACTION;
        int g = (color >> 8) & EditorInfo.IME_MASK_ACTION;
        int b = color & EditorInfo.IME_MASK_ACTION;
        int V = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));
        if (V == temp) {
            return 0.0f;
        }
        float H;
        float vtemp = (float) (V - temp);
        float cr = ((float) (V - r)) / vtemp;
        float cg = ((float) (V - g)) / vtemp;
        float cb = ((float) (V - b)) / vtemp;
        if (r == V) {
            H = cb - cg;
        } else if (g == V) {
            H = (2.0f + cr) - cb;
        } else {
            H = (4.0f + cg) - cr;
        }
        H /= 6.0f;
        if (H < 0.0f) {
            return H + LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        }
        return H;
    }

    public static float saturation(int color) {
        int r = (color >> 16) & EditorInfo.IME_MASK_ACTION;
        int g = (color >> 8) & EditorInfo.IME_MASK_ACTION;
        int b = color & EditorInfo.IME_MASK_ACTION;
        int V = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));
        if (V == temp) {
            return 0.0f;
        }
        return ((float) (V - temp)) / ((float) V);
    }

    public static float brightness(int color) {
        return ((float) Math.max(color & EditorInfo.IME_MASK_ACTION, Math.max((color >> 16) & EditorInfo.IME_MASK_ACTION, (color >> 8) & EditorInfo.IME_MASK_ACTION))) / 255.0f;
    }

    public static int parseColor(String colorString) {
        if (colorString.charAt(TRANSPARENT) == '#') {
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                color |= -16777216;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        }
        Integer color2 = (Integer) sColorNameMap.get(colorString.toLowerCase(Locale.ROOT));
        if (color2 != null) {
            return color2.intValue();
        }
        throw new IllegalArgumentException("Unknown color");
    }

    public static int HSBtoColor(float[] hsb) {
        return HSBtoColor(hsb[TRANSPARENT], hsb[1], hsb[2]);
    }

    public static int HSBtoColor(float h, float s, float b) {
        h = MathUtils.constrain(h, 0.0f, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        s = MathUtils.constrain(s, 0.0f, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        b = MathUtils.constrain(b, 0.0f, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        float red = 0.0f;
        float green = 0.0f;
        float blue = 0.0f;
        float hf = (h - ((float) ((int) h))) * 6.0f;
        int ihf = (int) hf;
        float f = hf - ((float) ihf);
        float pv = b * (LayoutParams.BRIGHTNESS_OVERRIDE_FULL - s);
        float qv = b * (LayoutParams.BRIGHTNESS_OVERRIDE_FULL - (s * f));
        float tv = b * (LayoutParams.BRIGHTNESS_OVERRIDE_FULL - ((LayoutParams.BRIGHTNESS_OVERRIDE_FULL - f) * s));
        switch (ihf) {
            case TRANSPARENT /*0*/:
                red = b;
                green = tv;
                blue = pv;
                break;
            case Toast.LENGTH_LONG /*1*/:
                red = qv;
                green = b;
                blue = pv;
                break;
            case Action.MERGE_IGNORE /*2*/:
                red = pv;
                green = b;
                blue = tv;
                break;
            case SetDrawableParameters.TAG /*3*/:
                red = pv;
                green = qv;
                blue = b;
                break;
            case ViewGroupAction.TAG /*4*/:
                red = tv;
                green = pv;
                blue = b;
                break;
            case ReflectionActionWithoutParams.TAG /*5*/:
                red = b;
                green = pv;
                blue = qv;
                break;
        }
        return ((BLACK | (((int) (red * 255.0f)) << 16)) | (((int) (green * 255.0f)) << 8)) | ((int) (blue * 255.0f));
    }

    public static void RGBToHSV(int red, int green, int blue, float[] hsv) {
        if (hsv.length < 3) {
            throw new RuntimeException("3 components required for hsv");
        }
        nativeRGBToHSV(red, green, blue, hsv);
    }

    public static void colorToHSV(int color, float[] hsv) {
        RGBToHSV((color >> 16) & EditorInfo.IME_MASK_ACTION, (color >> 8) & EditorInfo.IME_MASK_ACTION, color & EditorInfo.IME_MASK_ACTION, hsv);
    }

    public static int HSVToColor(float[] hsv) {
        return HSVToColor(EditorInfo.IME_MASK_ACTION, hsv);
    }

    public static int HSVToColor(int alpha, float[] hsv) {
        if (hsv.length >= 3) {
            return nativeHSVToColor(alpha, hsv);
        }
        throw new RuntimeException("3 components required for hsv");
    }

    public static int getHtmlColor(String color) {
        int i = WHITE;
        Integer i2 = (Integer) sColorNameMap.get(color.toLowerCase(Locale.ROOT));
        if (i2 != null) {
            return i2.intValue();
        }
        try {
            return XmlUtils.convertValueToInt(color, WHITE);
        } catch (NumberFormatException e) {
            return i;
        }
    }

    static {
        sColorNameMap = new HashMap();
        sColorNameMap.put("black", Integer.valueOf(BLACK));
        sColorNameMap.put("darkgray", Integer.valueOf(DKGRAY));
        sColorNameMap.put("gray", Integer.valueOf(GRAY));
        sColorNameMap.put("lightgray", Integer.valueOf(LTGRAY));
        sColorNameMap.put("white", Integer.valueOf(WHITE));
        sColorNameMap.put("red", Integer.valueOf(RED));
        sColorNameMap.put("green", Integer.valueOf(GREEN));
        sColorNameMap.put("blue", Integer.valueOf(BLUE));
        sColorNameMap.put("yellow", Integer.valueOf(YELLOW));
        sColorNameMap.put("cyan", Integer.valueOf(CYAN));
        sColorNameMap.put("magenta", Integer.valueOf(MAGENTA));
        sColorNameMap.put(Parameters.EFFECT_AQUA, Integer.valueOf(CYAN));
        sColorNameMap.put("fuchsia", Integer.valueOf(MAGENTA));
        sColorNameMap.put("darkgrey", Integer.valueOf(DKGRAY));
        sColorNameMap.put("grey", Integer.valueOf(GRAY));
        sColorNameMap.put("lightgrey", Integer.valueOf(LTGRAY));
        sColorNameMap.put("lime", Integer.valueOf(GREEN));
        sColorNameMap.put("maroon", Integer.valueOf(-8388608));
        sColorNameMap.put("navy", Integer.valueOf(-16777088));
        sColorNameMap.put("olive", Integer.valueOf(-8355840));
        sColorNameMap.put("purple", Integer.valueOf(-8388480));
        sColorNameMap.put("silver", Integer.valueOf(-4144960));
        sColorNameMap.put("teal", Integer.valueOf(-16744320));
    }
}
