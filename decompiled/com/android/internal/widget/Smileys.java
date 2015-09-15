package com.android.internal.widget;

import com.android.internal.R;

public class Smileys {
    public static int ANGEL;
    public static int COOL;
    public static int CRYING;
    public static int EMBARRASSED;
    public static int FOOT_IN_MOUTH;
    public static int HAPPY;
    public static int KISSING;
    public static int LAUGHING;
    public static int LIPS_ARE_SEALED;
    public static int MONEY_MOUTH;
    public static int SAD;
    public static int SURPRISED;
    public static int TONGUE_STICKING_OUT;
    public static int UNDECIDED;
    public static int WINKING;
    public static int WTF;
    public static int YELLING;
    private static final int[] sIconIds;

    static {
        sIconIds = new int[]{R.drawable.emo_im_happy, R.drawable.emo_im_sad, R.drawable.emo_im_winking, R.drawable.emo_im_tongue_sticking_out, R.drawable.emo_im_surprised, R.drawable.emo_im_kissing, R.drawable.emo_im_yelling, R.drawable.emo_im_cool, R.drawable.emo_im_money_mouth, R.drawable.emo_im_foot_in_mouth, R.drawable.emo_im_embarrassed, R.drawable.emo_im_angel, R.drawable.emo_im_undecided, R.drawable.emo_im_crying, R.drawable.emo_im_lips_are_sealed, R.drawable.emo_im_laughing, R.drawable.emo_im_wtf};
        HAPPY = 0;
        SAD = 1;
        WINKING = 2;
        TONGUE_STICKING_OUT = 3;
        SURPRISED = 4;
        KISSING = 5;
        YELLING = 6;
        COOL = 7;
        MONEY_MOUTH = 8;
        FOOT_IN_MOUTH = 9;
        EMBARRASSED = 10;
        ANGEL = 11;
        UNDECIDED = 12;
        CRYING = 13;
        LIPS_ARE_SEALED = 14;
        LAUGHING = 15;
        WTF = 16;
    }

    public static int getSmileyResource(int which) {
        return sIconIds[which];
    }
}
