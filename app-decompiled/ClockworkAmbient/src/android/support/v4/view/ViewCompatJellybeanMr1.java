package android.support.v4.view;

import android.graphics.Paint;
import android.view.View;

class ViewCompatJellybeanMr1 {
    public static int getLayoutDirection(View view) {
        return view.getLayoutDirection();
    }

    public static void setLayerPaint(View view, Paint paint) {
        view.setLayerPaint(paint);
    }
}
