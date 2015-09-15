package android.view;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;

public abstract class ViewOutlineProvider {
    public static final ViewOutlineProvider BACKGROUND;
    public static final ViewOutlineProvider BOUNDS;
    public static final ViewOutlineProvider PADDED_BOUNDS;

    public abstract void getOutline(View view, Outline outline);

    static {
        BACKGROUND = new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                Drawable background = view.getBackground();
                if (background != null) {
                    background.getOutline(outline);
                    return;
                }
                outline.setRect(0, 0, view.getWidth(), view.getHeight());
                outline.setAlpha(0.0f);
            }
        };
        BOUNDS = new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRect(0, 0, view.getWidth(), view.getHeight());
            }
        };
        PADDED_BOUNDS = new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRect(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
            }
        };
    }
}
