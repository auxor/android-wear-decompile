package android.text.style;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.text.ParcelableSpan;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R;
import java.util.Arrays;
import java.util.Locale;

public class SuggestionSpan extends CharacterStyle implements ParcelableSpan {
    public static final String ACTION_SUGGESTION_PICKED = "android.text.style.SUGGESTION_PICKED";
    public static final Creator<SuggestionSpan> CREATOR;
    public static final int FLAG_AUTO_CORRECTION = 4;
    public static final int FLAG_EASY_CORRECT = 1;
    public static final int FLAG_MISSPELLED = 2;
    public static final int SUGGESTIONS_MAX_SIZE = 5;
    public static final String SUGGESTION_SPAN_PICKED_AFTER = "after";
    public static final String SUGGESTION_SPAN_PICKED_BEFORE = "before";
    public static final String SUGGESTION_SPAN_PICKED_HASHCODE = "hashcode";
    private static final String TAG = "SuggestionSpan";
    private int mAutoCorrectionUnderlineColor;
    private float mAutoCorrectionUnderlineThickness;
    private int mEasyCorrectUnderlineColor;
    private float mEasyCorrectUnderlineThickness;
    private int mFlags;
    private final int mHashCode;
    private final String mLocaleString;
    private int mMisspelledUnderlineColor;
    private float mMisspelledUnderlineThickness;
    private final String mNotificationTargetClassName;
    private final String mNotificationTargetPackageName;
    private final String[] mSuggestions;

    public SuggestionSpan(Context context, String[] suggestions, int flags) {
        this(context, null, suggestions, flags, null);
    }

    public SuggestionSpan(Locale locale, String[] suggestions, int flags) {
        this(null, locale, suggestions, flags, null);
    }

    public SuggestionSpan(Context context, Locale locale, String[] suggestions, int flags, Class<?> notificationTargetClass) {
        this.mSuggestions = (String[]) Arrays.copyOf(suggestions, Math.min(SUGGESTIONS_MAX_SIZE, suggestions.length));
        this.mFlags = flags;
        if (locale != null) {
            this.mLocaleString = locale.toString();
        } else if (context != null) {
            this.mLocaleString = context.getResources().getConfiguration().locale.toString();
        } else {
            Log.e(TAG, "No locale or context specified in SuggestionSpan constructor");
            this.mLocaleString = "";
        }
        if (context != null) {
            this.mNotificationTargetPackageName = context.getPackageName();
        } else {
            this.mNotificationTargetPackageName = null;
        }
        if (notificationTargetClass != null) {
            this.mNotificationTargetClassName = notificationTargetClass.getCanonicalName();
        } else {
            this.mNotificationTargetClassName = "";
        }
        this.mHashCode = hashCodeInternal(this.mSuggestions, this.mLocaleString, this.mNotificationTargetClassName);
        initStyle(context);
    }

    private void initStyle(Context context) {
        if (context == null) {
            this.mMisspelledUnderlineThickness = 0.0f;
            this.mEasyCorrectUnderlineThickness = 0.0f;
            this.mAutoCorrectionUnderlineThickness = 0.0f;
            this.mMisspelledUnderlineColor = Spanned.SPAN_USER;
            this.mEasyCorrectUnderlineColor = Spanned.SPAN_USER;
            this.mAutoCorrectionUnderlineColor = Spanned.SPAN_USER;
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, R.attr.textAppearanceMisspelledSuggestion, 0);
        this.mMisspelledUnderlineThickness = typedArray.getDimension(FLAG_EASY_CORRECT, 0.0f);
        this.mMisspelledUnderlineColor = typedArray.getColor(0, Spanned.SPAN_USER);
        typedArray = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, R.attr.textAppearanceEasyCorrectSuggestion, 0);
        this.mEasyCorrectUnderlineThickness = typedArray.getDimension(FLAG_EASY_CORRECT, 0.0f);
        this.mEasyCorrectUnderlineColor = typedArray.getColor(0, Spanned.SPAN_USER);
        typedArray = context.obtainStyledAttributes(null, R.styleable.SuggestionSpan, R.attr.textAppearanceAutoCorrectionSuggestion, 0);
        this.mAutoCorrectionUnderlineThickness = typedArray.getDimension(FLAG_EASY_CORRECT, 0.0f);
        this.mAutoCorrectionUnderlineColor = typedArray.getColor(0, Spanned.SPAN_USER);
    }

    public SuggestionSpan(Parcel src) {
        this.mSuggestions = src.readStringArray();
        this.mFlags = src.readInt();
        this.mLocaleString = src.readString();
        this.mNotificationTargetClassName = src.readString();
        this.mNotificationTargetPackageName = src.readString();
        this.mHashCode = src.readInt();
        this.mEasyCorrectUnderlineColor = src.readInt();
        this.mEasyCorrectUnderlineThickness = src.readFloat();
        this.mMisspelledUnderlineColor = src.readInt();
        this.mMisspelledUnderlineThickness = src.readFloat();
        this.mAutoCorrectionUnderlineColor = src.readInt();
        this.mAutoCorrectionUnderlineThickness = src.readFloat();
    }

    public String[] getSuggestions() {
        return this.mSuggestions;
    }

    public String getLocale() {
        return this.mLocaleString;
    }

    public String getNotificationTargetClassName() {
        return this.mNotificationTargetClassName;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.mSuggestions);
        dest.writeInt(this.mFlags);
        dest.writeString(this.mLocaleString);
        dest.writeString(this.mNotificationTargetClassName);
        dest.writeString(this.mNotificationTargetPackageName);
        dest.writeInt(this.mHashCode);
        dest.writeInt(this.mEasyCorrectUnderlineColor);
        dest.writeFloat(this.mEasyCorrectUnderlineThickness);
        dest.writeInt(this.mMisspelledUnderlineColor);
        dest.writeFloat(this.mMisspelledUnderlineThickness);
        dest.writeInt(this.mAutoCorrectionUnderlineColor);
        dest.writeFloat(this.mAutoCorrectionUnderlineThickness);
    }

    public int getSpanTypeId() {
        return 19;
    }

    public boolean equals(Object o) {
        if ((o instanceof SuggestionSpan) && ((SuggestionSpan) o).hashCode() == this.mHashCode) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    private static int hashCodeInternal(String[] suggestions, String locale, String notificationTargetClassName) {
        Object[] objArr = new Object[FLAG_AUTO_CORRECTION];
        objArr[0] = Long.valueOf(SystemClock.uptimeMillis());
        objArr[FLAG_EASY_CORRECT] = suggestions;
        objArr[FLAG_MISSPELLED] = locale;
        objArr[3] = notificationTargetClassName;
        return Arrays.hashCode(objArr);
    }

    static {
        CREATOR = new Creator<SuggestionSpan>() {
            public SuggestionSpan createFromParcel(Parcel source) {
                return new SuggestionSpan(source);
            }

            public SuggestionSpan[] newArray(int size) {
                return new SuggestionSpan[size];
            }
        };
    }

    public void updateDrawState(TextPaint tp) {
        boolean misspelled;
        boolean easy;
        boolean autoCorrection;
        if ((this.mFlags & FLAG_MISSPELLED) != 0) {
            misspelled = true;
        } else {
            misspelled = false;
        }
        if ((this.mFlags & FLAG_EASY_CORRECT) != 0) {
            easy = true;
        } else {
            easy = false;
        }
        if ((this.mFlags & FLAG_AUTO_CORRECTION) != 0) {
            autoCorrection = true;
        } else {
            autoCorrection = false;
        }
        if (easy) {
            if (!misspelled) {
                tp.setUnderlineText(this.mEasyCorrectUnderlineColor, this.mEasyCorrectUnderlineThickness);
            } else if (tp.underlineColor == 0) {
                tp.setUnderlineText(this.mMisspelledUnderlineColor, this.mMisspelledUnderlineThickness);
            }
        } else if (autoCorrection) {
            tp.setUnderlineText(this.mAutoCorrectionUnderlineColor, this.mAutoCorrectionUnderlineThickness);
        }
    }

    public int getUnderlineColor() {
        boolean misspelled;
        boolean easy;
        boolean autoCorrection;
        if ((this.mFlags & FLAG_MISSPELLED) != 0) {
            misspelled = true;
        } else {
            misspelled = false;
        }
        if ((this.mFlags & FLAG_EASY_CORRECT) != 0) {
            easy = true;
        } else {
            easy = false;
        }
        if ((this.mFlags & FLAG_AUTO_CORRECTION) != 0) {
            autoCorrection = true;
        } else {
            autoCorrection = false;
        }
        if (easy) {
            if (misspelled) {
                return this.mMisspelledUnderlineColor;
            }
            return this.mEasyCorrectUnderlineColor;
        } else if (autoCorrection) {
            return this.mAutoCorrectionUnderlineColor;
        } else {
            return 0;
        }
    }

    public void notifySelection(Context context, String original, int index) {
        Intent intent = new Intent();
        if (context != null && this.mNotificationTargetClassName != null) {
            if (this.mSuggestions == null || index < 0 || index >= this.mSuggestions.length) {
                Log.w(TAG, "Unable to notify the suggestion as the index is out of range index=" + index + " length=" + this.mSuggestions.length);
            } else if (this.mNotificationTargetPackageName != null) {
                intent.setClassName(this.mNotificationTargetPackageName, this.mNotificationTargetClassName);
                intent.setAction(ACTION_SUGGESTION_PICKED);
                intent.putExtra(SUGGESTION_SPAN_PICKED_BEFORE, original);
                intent.putExtra(SUGGESTION_SPAN_PICKED_AFTER, this.mSuggestions[index]);
                intent.putExtra(SUGGESTION_SPAN_PICKED_HASHCODE, hashCode());
                context.sendBroadcast(intent);
            } else {
                InputMethodManager imm = InputMethodManager.peekInstance();
                if (imm != null) {
                    imm.notifySuggestionPicked(this, original, index);
                }
            }
        }
    }
}
