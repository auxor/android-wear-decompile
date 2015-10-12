package android.support.v4.view;

import android.view.View;

public class NestedScrollingParentHelper {
    private int mNestedScrollAxes;

    public int getNestedScrollAxes() {
        return this.mNestedScrollAxes;
    }

    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.mNestedScrollAxes = i;
    }
}
