package android.text;

import android.graphics.Paint;
import android.media.RemoteControlClient;

public class TextPaint extends Paint {
    public int baselineShift;
    public int bgColor;
    public float density;
    public int[] drawableState;
    public int linkColor;
    public int underlineColor;
    public float underlineThickness;

    public TextPaint() {
        this.density = RemoteControlClient.PLAYBACK_SPEED_1X;
        this.underlineColor = 0;
    }

    public TextPaint(int flags) {
        super(flags);
        this.density = RemoteControlClient.PLAYBACK_SPEED_1X;
        this.underlineColor = 0;
    }

    public TextPaint(Paint p) {
        super(p);
        this.density = RemoteControlClient.PLAYBACK_SPEED_1X;
        this.underlineColor = 0;
    }

    public void set(TextPaint tp) {
        super.set(tp);
        this.bgColor = tp.bgColor;
        this.baselineShift = tp.baselineShift;
        this.linkColor = tp.linkColor;
        this.drawableState = tp.drawableState;
        this.density = tp.density;
        this.underlineColor = tp.underlineColor;
        this.underlineThickness = tp.underlineThickness;
    }

    public void setUnderlineText(int color, float thickness) {
        this.underlineColor = color;
        this.underlineThickness = thickness;
    }
}
