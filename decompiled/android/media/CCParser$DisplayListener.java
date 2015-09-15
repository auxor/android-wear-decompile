package android.media;

import android.text.SpannableStringBuilder;
import android.view.accessibility.CaptioningManager.CaptionStyle;

/* compiled from: ClosedCaptionRenderer */
interface CCParser$DisplayListener {
    CaptionStyle getCaptionStyle();

    void onDisplayChanged(SpannableStringBuilder[] spannableStringBuilderArr);
}
