package com.android.internal.transition;

import android.transition.ChangeBounds;
import android.transition.ChangeText;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.ViewGroup;

public class ActionBarTransition {
    private static boolean TRANSITIONS_ENABLED = false;
    private static final int TRANSITION_DURATION = 120;
    private static final Transition sTransition;

    static {
        TRANSITIONS_ENABLED = false;
        if (TRANSITIONS_ENABLED) {
            ChangeText tc = new ChangeText();
            tc.setChangeBehavior(3);
            TransitionSet inner = new TransitionSet();
            inner.addTransition(tc).addTransition(new ChangeBounds());
            TransitionSet tg = new TransitionSet();
            tg.addTransition(new Fade(2)).addTransition(inner).addTransition(new Fade(1));
            tg.setOrdering(1);
            tg.setDuration(120);
            sTransition = tg;
            return;
        }
        sTransition = null;
    }

    public static void beginDelayedTransition(ViewGroup sceneRoot) {
        if (TRANSITIONS_ENABLED) {
            TransitionManager.beginDelayedTransition(sceneRoot, sTransition);
        }
    }
}
