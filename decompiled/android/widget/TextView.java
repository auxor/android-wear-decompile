package android.widget;

import android.R;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.UndoManager;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.ExtractEditText;
import android.net.ProxyInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.text.BoringLayout;
import android.text.BoringLayout.Metrics;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.Editable.Factory;
import android.text.GetChars;
import android.text.GraphicsOperations;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.ParcelableSpan;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.DateKeyListener;
import android.text.method.DateTimeKeyListener;
import android.text.method.DialerKeyListener;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.LinkMovementMethod;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.text.method.TimeKeyListener;
import android.text.method.TransformationMethod;
import android.text.method.TransformationMethod2;
import android.text.method.WordIterator;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ParagraphStyle;
import android.text.style.SpellCheckSpan;
import android.text.style.SuggestionSpan;
import android.text.style.URLSpan;
import android.text.style.UpdateAppearance;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.AccessibilityIterators.TextSegmentIterator;
import android.view.ActionMode.Callback;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewDebug.CapturedViewProperty;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import android.widget.Editor.UndoInputFilter;
import android.widget.RemoteViews.RemoteView;
import com.android.internal.util.FastMath;
import com.android.internal.widget.EditableInputConnection;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;

@RemoteView
public class TextView extends View implements OnPreDrawListener {
    private static final int ANIMATED_SCROLL_GAP = 250;
    private static final int CHANGE_WATCHER_PRIORITY = 100;
    static final boolean DEBUG_EXTRACT = false;
    private static final int DECIMAL = 4;
    private static final Spanned EMPTY_SPANNED = null;
    private static final int EMS = 1;
    static final int ID_COPY = 16908321;
    static final int ID_CUT = 16908320;
    static final int ID_PASTE = 16908322;
    static final int ID_SELECT_ALL = 16908319;
    static long LAST_CUT_OR_COPY_TIME = 0;
    private static final int LINES = 1;
    static final String LOG_TAG = "TextView";
    private static final int MARQUEE_FADE_NORMAL = 0;
    private static final int MARQUEE_FADE_SWITCH_SHOW_ELLIPSIS = 1;
    private static final int MARQUEE_FADE_SWITCH_SHOW_FADE = 2;
    private static final int MONOSPACE = 3;
    private static final int[] MULTILINE_STATE_SET = null;
    private static final InputFilter[] NO_FILTERS = null;
    private static final int PIXELS = 2;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int SIGNED = 2;
    private static final RectF TEMP_RECTF = null;
    private static final Metrics UNKNOWN_BORING = null;
    private static final int VERY_WIDE = 1048576;
    private boolean mAllowTransformationLengthChange;
    private int mAutoLinkMask;
    private Metrics mBoring;
    private BufferType mBufferType;
    private ChangeWatcher mChangeWatcher;
    private CharWrapper mCharWrapper;
    private int mCurHintTextColor;
    @ExportedProperty(category = "text")
    private int mCurTextColor;
    private volatile Locale mCurrentSpellCheckerLocaleCache;
    int mCursorDrawableRes;
    private int mDeferScroll;
    private int mDesiredHeightAtMeasure;
    private boolean mDispatchTemporaryDetach;
    Drawables mDrawables;
    private Factory mEditableFactory;
    private Editor mEditor;
    private TruncateAt mEllipsize;
    private InputFilter[] mFilters;
    private boolean mFreezesText;
    private int mGravity;
    int mHighlightColor;
    private final Paint mHighlightPaint;
    private Path mHighlightPath;
    private boolean mHighlightPathBogus;
    private CharSequence mHint;
    private Metrics mHintBoring;
    private Layout mHintLayout;
    private ColorStateList mHintTextColor;
    private boolean mHorizontallyScrolling;
    private boolean mIncludePad;
    private int mLastLayoutDirection;
    private long mLastScroll;
    private Layout mLayout;
    private ColorStateList mLinkTextColor;
    private boolean mLinksClickable;
    private ArrayList<TextWatcher> mListeners;
    private Marquee mMarquee;
    private int mMarqueeFadeMode;
    private int mMarqueeRepeatLimit;
    private int mMaxMode;
    private int mMaxWidth;
    private int mMaxWidthMode;
    private int mMaximum;
    private int mMinMode;
    private int mMinWidth;
    private int mMinWidthMode;
    private int mMinimum;
    private MovementMethod mMovement;
    private int mOldMaxMode;
    private int mOldMaximum;
    private boolean mPreDrawListenerDetached;
    private boolean mPreDrawRegistered;
    private boolean mPreventDefaultMovement;
    private boolean mRestartMarquee;
    private BoringLayout mSavedHintLayout;
    private BoringLayout mSavedLayout;
    private Layout mSavedMarqueeModeLayout;
    private Scroller mScroller;
    private int mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private boolean mSingleLine;
    private float mSpacingAdd;
    private float mSpacingMult;
    private Spannable.Factory mSpannableFactory;
    private Rect mTempRect;
    boolean mTemporaryDetach;
    @ExportedProperty(category = "text")
    private CharSequence mText;
    private ColorStateList mTextColor;
    private TextDirectionHeuristic mTextDir;
    int mTextEditSuggestionItemLayout;
    private final TextPaint mTextPaint;
    int mTextSelectHandleLeftRes;
    int mTextSelectHandleRes;
    int mTextSelectHandleRightRes;
    private TransformationMethod mTransformation;
    private CharSequence mTransformed;
    private boolean mUserSetTextScaleX;

    public interface OnEditorActionListener {
        boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent);
    }

    /* renamed from: android.widget.TextView.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ TextView this$0;
        final /* synthetic */ CharSequence val$error;

        AnonymousClass1(android.widget.TextView r1, java.lang.CharSequence r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.1.<init>(android.widget.TextView, java.lang.CharSequence):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.1.<init>(android.widget.TextView, java.lang.CharSequence):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.1.<init>(android.widget.TextView, java.lang.CharSequence):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.1.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.1.run():void");
        }
    }

    /* renamed from: android.widget.TextView.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ TextView this$0;

        AnonymousClass2(android.widget.TextView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.2.<init>(android.widget.TextView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.2.<init>(android.widget.TextView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.2.<init>(android.widget.TextView):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.2.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.2.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.2.run():void");
        }
    }

    /* renamed from: android.widget.TextView.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ TextView this$0;

        AnonymousClass3(android.widget.TextView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.3.<init>(android.widget.TextView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.3.<init>(android.widget.TextView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.3.<init>(android.widget.TextView):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.3.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.3.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.3.run():void");
        }
    }

    /* renamed from: android.widget.TextView.4 */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment = null;

        static {
            $SwitchMap$android$text$Layout$Alignment = new int[Alignment.values().length];
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_LEFT.ordinal()] = TextView.SANS;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_RIGHT.ordinal()] = TextView.SIGNED;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_NORMAL.ordinal()] = TextView.MONOSPACE;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_OPPOSITE.ordinal()] = TextView.DECIMAL;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_CENTER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public enum BufferType {
        NORMAL,
        SPANNABLE,
        EDITABLE
    }

    private class ChangeWatcher implements TextWatcher, SpanWatcher {
        private CharSequence mBeforeText;
        final /* synthetic */ TextView this$0;

        private ChangeWatcher(TextView textView) {
            this.this$0 = textView;
        }

        /* synthetic */ ChangeWatcher(TextView x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void beforeTextChanged(CharSequence buffer, int start, int before, int after) {
            if (AccessibilityManager.getInstance(this.this$0.mContext).isEnabled() && (!(TextView.isPasswordInputType(this.this$0.getInputType()) || this.this$0.hasPasswordTransformationMethod()) || this.this$0.shouldSpeakPasswordsForAccessibility())) {
                this.mBeforeText = buffer.toString();
            }
            this.this$0.sendBeforeTextChanged(buffer, start, before, after);
        }

        public void onTextChanged(CharSequence buffer, int start, int before, int after) {
            this.this$0.handleTextChanged(buffer, start, before, after);
            if (!AccessibilityManager.getInstance(this.this$0.mContext).isEnabled()) {
                return;
            }
            if (this.this$0.isFocused() || (this.this$0.isSelected() && this.this$0.isShown())) {
                this.this$0.sendAccessibilityEventTypeViewTextChanged(this.mBeforeText, start, before, after);
                this.mBeforeText = null;
            }
        }

        public void afterTextChanged(Editable buffer) {
            this.this$0.sendAfterTextChanged(buffer);
            if (MetaKeyKeyListener.getMetaState(buffer, AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT) != 0) {
                MetaKeyKeyListener.stopSelecting(this.this$0, buffer);
            }
        }

        public void onSpanChanged(Spannable buf, Object what, int s, int e, int st, int en) {
            this.this$0.spanChange(buf, what, s, st, e, en);
        }

        public void onSpanAdded(Spannable buf, Object what, int s, int e) {
            this.this$0.spanChange(buf, what, -1, s, -1, e);
        }

        public void onSpanRemoved(Spannable buf, Object what, int s, int e) {
            this.this$0.spanChange(buf, what, s, -1, e, -1);
        }
    }

    private static class CharWrapper implements CharSequence, GetChars, GraphicsOperations {
        private char[] mChars;
        private int mLength;
        private int mStart;

        public CharWrapper(char[] chars, int start, int len) {
            this.mChars = chars;
            this.mStart = start;
            this.mLength = len;
        }

        void set(char[] chars, int start, int len) {
            this.mChars = chars;
            this.mStart = start;
            this.mLength = len;
        }

        public int length() {
            return this.mLength;
        }

        public char charAt(int off) {
            return this.mChars[this.mStart + off];
        }

        public String toString() {
            return new String(this.mChars, this.mStart, this.mLength);
        }

        public CharSequence subSequence(int start, int end) {
            if (start >= 0 && end >= 0 && start <= this.mLength && end <= this.mLength) {
                return new String(this.mChars, this.mStart + start, end - start);
            }
            throw new IndexOutOfBoundsException(start + ", " + end);
        }

        public void getChars(int start, int end, char[] buf, int off) {
            if (start < 0 || end < 0 || start > this.mLength || end > this.mLength) {
                throw new IndexOutOfBoundsException(start + ", " + end);
            }
            System.arraycopy(this.mChars, this.mStart + start, buf, off, end - start);
        }

        public void drawText(Canvas c, int start, int end, float x, float y, Paint p) {
            c.drawText(this.mChars, start + this.mStart, end - start, x, y, p);
        }

        public void drawTextRun(Canvas c, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, Paint p) {
            int count = end - start;
            int contextCount = contextEnd - contextStart;
            c.drawTextRun(this.mChars, start + this.mStart, count, contextStart + this.mStart, contextCount, x, y, isRtl, p);
        }

        public float measureText(int start, int end, Paint p) {
            return p.measureText(this.mChars, this.mStart + start, end - start);
        }

        public int getTextWidths(int start, int end, float[] widths, Paint p) {
            return p.getTextWidths(this.mChars, this.mStart + start, end - start, widths);
        }

        public float getTextRunAdvances(int start, int end, int contextStart, int contextEnd, boolean isRtl, float[] advances, int advancesIndex, Paint p) {
            int count = end - start;
            int contextCount = contextEnd - contextStart;
            return p.getTextRunAdvances(this.mChars, start + this.mStart, count, contextStart + this.mStart, contextCount, isRtl, advances, advancesIndex);
        }

        public int getTextRunCursor(int contextStart, int contextEnd, int dir, int offset, int cursorOpt, Paint p) {
            int contextCount = contextEnd - contextStart;
            return p.getTextRunCursor(this.mChars, contextStart + this.mStart, contextCount, dir, offset + this.mStart, cursorOpt);
        }
    }

    static class Drawables {
        static final int DRAWABLE_LEFT = 1;
        static final int DRAWABLE_NONE = -1;
        static final int DRAWABLE_RIGHT = 0;
        final Rect mCompoundRect;
        Drawable mDrawableBottom;
        Drawable mDrawableEnd;
        Drawable mDrawableError;
        int mDrawableHeightEnd;
        int mDrawableHeightError;
        int mDrawableHeightLeft;
        int mDrawableHeightRight;
        int mDrawableHeightStart;
        int mDrawableHeightTemp;
        Drawable mDrawableLeft;
        Drawable mDrawableLeftInitial;
        int mDrawablePadding;
        Drawable mDrawableRight;
        Drawable mDrawableRightInitial;
        int mDrawableSaved;
        int mDrawableSizeBottom;
        int mDrawableSizeEnd;
        int mDrawableSizeError;
        int mDrawableSizeLeft;
        int mDrawableSizeRight;
        int mDrawableSizeStart;
        int mDrawableSizeTemp;
        int mDrawableSizeTop;
        Drawable mDrawableStart;
        Drawable mDrawableTemp;
        Drawable mDrawableTop;
        int mDrawableWidthBottom;
        int mDrawableWidthTop;
        boolean mIsRtlCompatibilityMode;
        boolean mOverride;

        public Drawables(Context context) {
            boolean z;
            this.mCompoundRect = new Rect();
            this.mDrawableSaved = DRAWABLE_NONE;
            if (context.getApplicationInfo().targetSdkVersion < 17 || !context.getApplicationInfo().hasRtlSupport()) {
                z = true;
            } else {
                z = TextView.DEBUG_EXTRACT;
            }
            this.mIsRtlCompatibilityMode = z;
            this.mOverride = TextView.DEBUG_EXTRACT;
        }

        public void resolveWithLayoutDirection(int layoutDirection) {
            this.mDrawableLeft = this.mDrawableLeftInitial;
            this.mDrawableRight = this.mDrawableRightInitial;
            if (!this.mIsRtlCompatibilityMode) {
                switch (layoutDirection) {
                    case DRAWABLE_LEFT /*1*/:
                        if (this.mOverride) {
                            this.mDrawableRight = this.mDrawableStart;
                            this.mDrawableSizeRight = this.mDrawableSizeStart;
                            this.mDrawableHeightRight = this.mDrawableHeightStart;
                            this.mDrawableLeft = this.mDrawableEnd;
                            this.mDrawableSizeLeft = this.mDrawableSizeEnd;
                            this.mDrawableHeightLeft = this.mDrawableHeightEnd;
                            break;
                        }
                        break;
                    default:
                        if (this.mOverride) {
                            this.mDrawableLeft = this.mDrawableStart;
                            this.mDrawableSizeLeft = this.mDrawableSizeStart;
                            this.mDrawableHeightLeft = this.mDrawableHeightStart;
                            this.mDrawableRight = this.mDrawableEnd;
                            this.mDrawableSizeRight = this.mDrawableSizeEnd;
                            this.mDrawableHeightRight = this.mDrawableHeightEnd;
                            break;
                        }
                        break;
                }
            }
            if (this.mDrawableStart != null && this.mDrawableLeft == null) {
                this.mDrawableLeft = this.mDrawableStart;
                this.mDrawableSizeLeft = this.mDrawableSizeStart;
                this.mDrawableHeightLeft = this.mDrawableHeightStart;
            }
            if (this.mDrawableEnd != null && this.mDrawableRight == null) {
                this.mDrawableRight = this.mDrawableEnd;
                this.mDrawableSizeRight = this.mDrawableSizeEnd;
                this.mDrawableHeightRight = this.mDrawableHeightEnd;
            }
            applyErrorDrawableIfNeeded(layoutDirection);
            updateDrawablesLayoutDirection(layoutDirection);
        }

        private void updateDrawablesLayoutDirection(int layoutDirection) {
            if (this.mDrawableLeft != null) {
                this.mDrawableLeft.setLayoutDirection(layoutDirection);
            }
            if (this.mDrawableRight != null) {
                this.mDrawableRight.setLayoutDirection(layoutDirection);
            }
            if (this.mDrawableTop != null) {
                this.mDrawableTop.setLayoutDirection(layoutDirection);
            }
            if (this.mDrawableBottom != null) {
                this.mDrawableBottom.setLayoutDirection(layoutDirection);
            }
        }

        public void setErrorDrawable(Drawable dr, TextView tv) {
            if (!(this.mDrawableError == dr || this.mDrawableError == null)) {
                this.mDrawableError.setCallback(null);
            }
            this.mDrawableError = dr;
            Rect compoundRect = this.mCompoundRect;
            int[] state = tv.getDrawableState();
            if (this.mDrawableError != null) {
                this.mDrawableError.setState(state);
                this.mDrawableError.copyBounds(compoundRect);
                this.mDrawableError.setCallback(tv);
                this.mDrawableSizeError = compoundRect.width();
                this.mDrawableHeightError = compoundRect.height();
                return;
            }
            this.mDrawableHeightError = TextView.MARQUEE_FADE_NORMAL;
            this.mDrawableSizeError = TextView.MARQUEE_FADE_NORMAL;
        }

        private void applyErrorDrawableIfNeeded(int layoutDirection) {
            switch (this.mDrawableSaved) {
                case TextView.MARQUEE_FADE_NORMAL /*0*/:
                    this.mDrawableRight = this.mDrawableTemp;
                    this.mDrawableSizeRight = this.mDrawableSizeTemp;
                    this.mDrawableHeightRight = this.mDrawableHeightTemp;
                    break;
                case DRAWABLE_LEFT /*1*/:
                    this.mDrawableLeft = this.mDrawableTemp;
                    this.mDrawableSizeLeft = this.mDrawableSizeTemp;
                    this.mDrawableHeightLeft = this.mDrawableHeightTemp;
                    break;
            }
            if (this.mDrawableError != null) {
                switch (layoutDirection) {
                    case DRAWABLE_LEFT /*1*/:
                        this.mDrawableSaved = DRAWABLE_LEFT;
                        this.mDrawableTemp = this.mDrawableLeft;
                        this.mDrawableSizeTemp = this.mDrawableSizeLeft;
                        this.mDrawableHeightTemp = this.mDrawableHeightLeft;
                        this.mDrawableLeft = this.mDrawableError;
                        this.mDrawableSizeLeft = this.mDrawableSizeError;
                        this.mDrawableHeightLeft = this.mDrawableHeightError;
                    default:
                        this.mDrawableSaved = TextView.MARQUEE_FADE_NORMAL;
                        this.mDrawableTemp = this.mDrawableRight;
                        this.mDrawableSizeTemp = this.mDrawableSizeRight;
                        this.mDrawableHeightTemp = this.mDrawableHeightRight;
                        this.mDrawableRight = this.mDrawableError;
                        this.mDrawableSizeRight = this.mDrawableSizeError;
                        this.mDrawableHeightRight = this.mDrawableHeightError;
                }
            }
        }
    }

    private static final class Marquee {
        private static final int MARQUEE_DELAY = 1200;
        private static final float MARQUEE_DELTA_MAX = 0.07f;
        private static final int MARQUEE_DP_PER_SECOND = 30;
        private static final int MARQUEE_RESTART_DELAY = 1200;
        private static final byte MARQUEE_RUNNING = (byte) 2;
        private static final byte MARQUEE_STARTING = (byte) 1;
        private static final byte MARQUEE_STOPPED = (byte) 0;
        private final Choreographer mChoreographer;
        private float mFadeStop;
        private float mGhostOffset;
        private float mGhostStart;
        private long mLastAnimationMs;
        private float mMaxFadeScroll;
        private float mMaxScroll;
        private final float mPixelsPerSecond;
        private int mRepeatLimit;
        private FrameCallback mRestartCallback;
        private float mScroll;
        private FrameCallback mStartCallback;
        private byte mStatus;
        private FrameCallback mTickCallback;
        private final WeakReference<TextView> mView;

        /* renamed from: android.widget.TextView.Marquee.1 */
        class AnonymousClass1 implements FrameCallback {
            final /* synthetic */ Marquee this$0;

            AnonymousClass1(android.widget.TextView.Marquee r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.1.<init>(android.widget.TextView$Marquee):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.1.<init>(android.widget.TextView$Marquee):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.1.<init>(android.widget.TextView$Marquee):void");
            }

            public void doFrame(long r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.1.doFrame(long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.1.doFrame(long):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.1.doFrame(long):void");
            }
        }

        /* renamed from: android.widget.TextView.Marquee.2 */
        class AnonymousClass2 implements FrameCallback {
            final /* synthetic */ Marquee this$0;

            AnonymousClass2(android.widget.TextView.Marquee r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.2.<init>(android.widget.TextView$Marquee):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.2.<init>(android.widget.TextView$Marquee):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.2.<init>(android.widget.TextView$Marquee):void");
            }

            public void doFrame(long r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.2.doFrame(long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.2.doFrame(long):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.2.doFrame(long):void");
            }
        }

        /* renamed from: android.widget.TextView.Marquee.3 */
        class AnonymousClass3 implements FrameCallback {
            final /* synthetic */ Marquee this$0;

            AnonymousClass3(android.widget.TextView.Marquee r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.3.<init>(android.widget.TextView$Marquee):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.3.<init>(android.widget.TextView$Marquee):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.3.<init>(android.widget.TextView$Marquee):void");
            }

            public void doFrame(long r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.3.doFrame(long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.3.doFrame(long):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.3.doFrame(long):void");
            }
        }

        Marquee(android.widget.TextView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.<init>(android.widget.TextView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.<init>(android.widget.TextView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.<init>(android.widget.TextView):void");
        }

        static /* synthetic */ byte access$402(android.widget.TextView.Marquee r1, byte r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.access$402(android.widget.TextView$Marquee, byte):byte
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.access$402(android.widget.TextView$Marquee, byte):byte
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.access$402(android.widget.TextView$Marquee, byte):byte");
        }

        static /* synthetic */ long access$502(android.widget.TextView.Marquee r1, long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.access$502(android.widget.TextView$Marquee, long):long
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.access$502(android.widget.TextView$Marquee, long):long
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e7
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.access$502(android.widget.TextView$Marquee, long):long");
        }

        static /* synthetic */ android.view.Choreographer access$600(android.widget.TextView.Marquee r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.access$600(android.widget.TextView$Marquee):android.view.Choreographer
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.access$600(android.widget.TextView$Marquee):android.view.Choreographer
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.access$600(android.widget.TextView$Marquee):android.view.Choreographer");
        }

        static /* synthetic */ int access$700(android.widget.TextView.Marquee r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.access$700(android.widget.TextView$Marquee):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.access$700(android.widget.TextView$Marquee):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.access$700(android.widget.TextView$Marquee):int");
        }

        static /* synthetic */ int access$710(android.widget.TextView.Marquee r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.access$710(android.widget.TextView$Marquee):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.access$710(android.widget.TextView$Marquee):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.access$710(android.widget.TextView$Marquee):int");
        }

        private void resetScroll() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.resetScroll():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.resetScroll():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.resetScroll():void");
        }

        float getGhostOffset() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.getGhostOffset():float
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.getGhostOffset():float
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.getGhostOffset():float");
        }

        float getMaxFadeScroll() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.getMaxFadeScroll():float
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.getMaxFadeScroll():float
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.getMaxFadeScroll():float");
        }

        float getScroll() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.getScroll():float
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.getScroll():float
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.getScroll():float");
        }

        boolean shouldDrawGhost() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.shouldDrawGhost():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.shouldDrawGhost():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.shouldDrawGhost():boolean");
        }

        boolean shouldDrawLeftFade() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.shouldDrawLeftFade():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.shouldDrawLeftFade():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.shouldDrawLeftFade():boolean");
        }

        void start(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.start(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.start(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.start(int):void");
        }

        void stop() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.stop():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.stop():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.stop():void");
        }

        void tick() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.Marquee.tick():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.Marquee.tick():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.Marquee.tick():void");
        }

        boolean isRunning() {
            return this.mStatus == TextView.SIGNED ? true : TextView.DEBUG_EXTRACT;
        }

        boolean isStopped() {
            return this.mStatus == null ? true : TextView.DEBUG_EXTRACT;
        }
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = null;
        CharSequence error;
        boolean frozenWithFocus;
        int selEnd;
        int selStart;
        CharSequence text;

        /* renamed from: android.widget.TextView.SavedState.1 */
        static class AnonymousClass1 implements Creator<SavedState> {
            public /* bridge */ /* synthetic */ java.lang.Object m17createFromParcel(android.os.Parcel r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.SavedState.1.createFromParcel(android.os.Parcel):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.SavedState.1.createFromParcel(android.os.Parcel):java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.SavedState.1.createFromParcel(android.os.Parcel):java.lang.Object");
            }

            public /* bridge */ /* synthetic */ java.lang.Object[] m18newArray(int r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.TextView.SavedState.1.newArray(int):java.lang.Object[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.TextView.SavedState.1.newArray(int):java.lang.Object[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.TextView.SavedState.1.newArray(int):java.lang.Object[]");
            }

            AnonymousClass1() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        /* synthetic */ SavedState(Parcel x0, AnonymousClass1 x1) {
            this(x0);
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selStart);
            out.writeInt(this.selEnd);
            out.writeInt(this.frozenWithFocus ? TextView.SANS : TextView.MARQUEE_FADE_NORMAL);
            TextUtils.writeToParcel(this.text, out, flags);
            if (this.error == null) {
                out.writeInt(TextView.MARQUEE_FADE_NORMAL);
                return;
            }
            out.writeInt(TextView.SANS);
            TextUtils.writeToParcel(this.error, out, flags);
        }

        public String toString() {
            String str = "TextView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " start=" + this.selStart + " end=" + this.selEnd;
            if (this.text != null) {
                str = str + " text=" + this.text;
            }
            return str + "}";
        }

        static {
            CREATOR = new AnonymousClass1();
        }

        private SavedState(Parcel in) {
            super(in);
            this.selStart = in.readInt();
            this.selEnd = in.readInt();
            this.frozenWithFocus = in.readInt() != 0 ? true : TextView.DEBUG_EXTRACT;
            this.text = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            if (in.readInt() != 0) {
                this.error = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            }
        }
    }

    static {
        TEMP_RECTF = new RectF();
        NO_FILTERS = new InputFilter[MARQUEE_FADE_NORMAL];
        EMPTY_SPANNED = new SpannedString(ProxyInfo.LOCAL_EXCL_LIST);
        int[] iArr = new int[SANS];
        iArr[MARQUEE_FADE_NORMAL] = R.attr.state_multiline;
        MULTILINE_STATE_SET = iArr;
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.measureText("H");
        UNKNOWN_BORING = new Metrics();
    }

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, MARQUEE_FADE_NORMAL);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        int n;
        int i;
        int attr;
        int i2;
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mEditableFactory = Factory.getInstance();
        this.mSpannableFactory = Spannable.Factory.getInstance();
        this.mMarqueeRepeatLimit = MONOSPACE;
        this.mLastLayoutDirection = -1;
        this.mMarqueeFadeMode = MARQUEE_FADE_NORMAL;
        this.mBufferType = BufferType.NORMAL;
        this.mGravity = 8388659;
        this.mLinksClickable = true;
        this.mSpacingMult = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mSpacingAdd = 0.0f;
        this.mMaximum = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mMaxMode = SANS;
        this.mMinimum = MARQUEE_FADE_NORMAL;
        this.mMinMode = SANS;
        this.mOldMaximum = this.mMaximum;
        this.mOldMaxMode = this.mMaxMode;
        this.mMaxWidth = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mMaxWidthMode = SIGNED;
        this.mMinWidth = MARQUEE_FADE_NORMAL;
        this.mMinWidthMode = SIGNED;
        this.mDesiredHeightAtMeasure = -1;
        this.mIncludePad = true;
        this.mDeferScroll = -1;
        this.mFilters = NO_FILTERS;
        this.mHighlightColor = 1714664933;
        this.mHighlightPathBogus = true;
        this.mText = ProxyInfo.LOCAL_EXCL_LIST;
        Resources res = getResources();
        CompatibilityInfo compat = res.getCompatibilityInfo();
        this.mTextPaint = new TextPaint(SANS);
        this.mTextPaint.density = res.getDisplayMetrics().density;
        this.mTextPaint.setCompatibilityScaling(compat.applicationScale);
        this.mHighlightPaint = new Paint((int) SANS);
        this.mHighlightPaint.setCompatibilityScaling(compat.applicationScale);
        this.mMovement = getDefaultMovementMethod();
        this.mTransformation = null;
        int textColorHighlight = MARQUEE_FADE_NORMAL;
        ColorStateList textColor = null;
        ColorStateList textColorHint = null;
        ColorStateList textColorLink = null;
        int textSize = 15;
        String fontFamily = null;
        boolean fontFamilyExplicit = DEBUG_EXTRACT;
        int typefaceIndex = -1;
        int styleIndex = -1;
        boolean allCaps = DEBUG_EXTRACT;
        int shadowcolor = MARQUEE_FADE_NORMAL;
        float dx = 0.0f;
        float dy = 0.0f;
        float r = 0.0f;
        boolean elegant = DEBUG_EXTRACT;
        float letterSpacing = 0.0f;
        String fontFeatureSettings = null;
        Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, com.android.internal.R.styleable.TextViewAppearance, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(MARQUEE_FADE_NORMAL, -1);
        a.recycle();
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(ap, com.android.internal.R.styleable.TextAppearance);
        }
        if (appearance != null) {
            n = appearance.getIndexCount();
            for (i = MARQUEE_FADE_NORMAL; i < n; i += SANS) {
                attr = appearance.getIndex(i);
                switch (attr) {
                    case MARQUEE_FADE_NORMAL /*0*/:
                        textSize = appearance.getDimensionPixelSize(attr, textSize);
                        break;
                    case SANS /*1*/:
                        typefaceIndex = appearance.getInt(attr, -1);
                        break;
                    case SIGNED /*2*/:
                        styleIndex = appearance.getInt(attr, -1);
                        break;
                    case MONOSPACE /*3*/:
                        textColor = appearance.getColorStateList(attr);
                        break;
                    case DECIMAL /*4*/:
                        textColorHighlight = appearance.getColor(attr, textColorHighlight);
                        break;
                    case ReflectionActionWithoutParams.TAG /*5*/:
                        textColorHint = appearance.getColorStateList(attr);
                        break;
                    case SetEmptyView.TAG /*6*/:
                        textColorLink = appearance.getColorStateList(attr);
                        break;
                    case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                        shadowcolor = appearance.getInt(attr, MARQUEE_FADE_NORMAL);
                        break;
                    case SetPendingIntentTemplate.TAG /*8*/:
                        dx = appearance.getFloat(attr, 0.0f);
                        break;
                    case SetOnClickFillInIntent.TAG /*9*/:
                        dy = appearance.getFloat(attr, 0.0f);
                        break;
                    case SetRemoteViewsAdapterIntent.TAG /*10*/:
                        r = appearance.getFloat(attr, 0.0f);
                        break;
                    case TextViewDrawableAction.TAG /*11*/:
                        allCaps = appearance.getBoolean(attr, DEBUG_EXTRACT);
                        break;
                    case BitmapReflectionAction.TAG /*12*/:
                        fontFamily = appearance.getString(attr);
                        break;
                    case TextViewSizeAction.TAG /*13*/:
                        elegant = appearance.getBoolean(attr, DEBUG_EXTRACT);
                        break;
                    case ViewPaddingAction.TAG /*14*/:
                        letterSpacing = appearance.getFloat(attr, 0.0f);
                        break;
                    case SetRemoteViewsAdapterList.TAG /*15*/:
                        fontFeatureSettings = appearance.getString(attr);
                        break;
                    default:
                        break;
                }
            }
            appearance.recycle();
        }
        boolean editable = getDefaultEditable();
        CharSequence inputMethod = null;
        int numeric = MARQUEE_FADE_NORMAL;
        CharSequence digits = null;
        boolean phone = DEBUG_EXTRACT;
        boolean autotext = DEBUG_EXTRACT;
        int autocap = -1;
        int buffertype = MARQUEE_FADE_NORMAL;
        boolean selectallonfocus = DEBUG_EXTRACT;
        Drawable drawableLeft = null;
        Drawable drawableTop = null;
        Drawable drawableRight = null;
        Drawable drawableBottom = null;
        Drawable drawableStart = null;
        Drawable drawableEnd = null;
        int drawablePadding = MARQUEE_FADE_NORMAL;
        int ellipsize = -1;
        boolean singleLine = DEBUG_EXTRACT;
        int maxlength = -1;
        CharSequence text = ProxyInfo.LOCAL_EXCL_LIST;
        CharSequence hint = null;
        boolean password = DEBUG_EXTRACT;
        int inputType = MARQUEE_FADE_NORMAL;
        a = theme.obtainStyledAttributes(attrs, com.android.internal.R.styleable.TextView, defStyleAttr, defStyleRes);
        n = a.getIndexCount();
        for (i = MARQUEE_FADE_NORMAL; i < n; i += SANS) {
            attr = a.getIndex(i);
            switch (attr) {
                case MARQUEE_FADE_NORMAL /*0*/:
                    setEnabled(a.getBoolean(attr, isEnabled()));
                    break;
                case SIGNED /*2*/:
                    textSize = a.getDimensionPixelSize(attr, textSize);
                    break;
                case MONOSPACE /*3*/:
                    typefaceIndex = a.getInt(attr, typefaceIndex);
                    break;
                case DECIMAL /*4*/:
                    styleIndex = a.getInt(attr, styleIndex);
                    break;
                case ReflectionActionWithoutParams.TAG /*5*/:
                    textColor = a.getColorStateList(attr);
                    break;
                case SetEmptyView.TAG /*6*/:
                    textColorHighlight = a.getColor(attr, textColorHighlight);
                    break;
                case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                    textColorHint = a.getColorStateList(attr);
                    break;
                case SetPendingIntentTemplate.TAG /*8*/:
                    textColorLink = a.getColorStateList(attr);
                    break;
                case SetOnClickFillInIntent.TAG /*9*/:
                    ellipsize = a.getInt(attr, ellipsize);
                    break;
                case SetRemoteViewsAdapterIntent.TAG /*10*/:
                    setGravity(a.getInt(attr, -1));
                    break;
                case TextViewDrawableAction.TAG /*11*/:
                    this.mAutoLinkMask = a.getInt(attr, MARQUEE_FADE_NORMAL);
                    break;
                case BitmapReflectionAction.TAG /*12*/:
                    this.mLinksClickable = a.getBoolean(attr, true);
                    break;
                case TextViewSizeAction.TAG /*13*/:
                    setMaxWidth(a.getDimensionPixelSize(attr, -1));
                    break;
                case ViewPaddingAction.TAG /*14*/:
                    setMaxHeight(a.getDimensionPixelSize(attr, -1));
                    break;
                case SetRemoteViewsAdapterList.TAG /*15*/:
                    setMinWidth(a.getDimensionPixelSize(attr, -1));
                    break;
                case RelativeLayout.START_OF /*16*/:
                    setMinHeight(a.getDimensionPixelSize(attr, -1));
                    break;
                case TextViewDrawableColorFilterAction.TAG /*17*/:
                    buffertype = a.getInt(attr, buffertype);
                    break;
                case RelativeLayout.ALIGN_START /*18*/:
                    text = a.getText(attr);
                    break;
                case RelativeLayout.ALIGN_END /*19*/:
                    hint = a.getText(attr);
                    break;
                case RelativeLayout.ALIGN_PARENT_START /*20*/:
                    setTextScaleX(a.getFloat(attr, LayoutParams.BRIGHTNESS_OVERRIDE_FULL));
                    break;
                case RelativeLayout.ALIGN_PARENT_END /*21*/:
                    if (!a.getBoolean(attr, true)) {
                        setCursorVisible(DEBUG_EXTRACT);
                        break;
                    }
                    break;
                case MotionEvent.AXIS_GAS /*22*/:
                    setMaxLines(a.getInt(attr, -1));
                    break;
                case MotionEvent.AXIS_BRAKE /*23*/:
                    setLines(a.getInt(attr, -1));
                    break;
                case MotionEvent.AXIS_DISTANCE /*24*/:
                    setHeight(a.getDimensionPixelSize(attr, -1));
                    break;
                case MotionEvent.AXIS_TILT /*25*/:
                    setMinLines(a.getInt(attr, -1));
                    break;
                case KeyEvent.KEYCODE_POWER /*26*/:
                    setMaxEms(a.getInt(attr, -1));
                    break;
                case KeyEvent.KEYCODE_CAMERA /*27*/:
                    setEms(a.getInt(attr, -1));
                    break;
                case KeyEvent.KEYCODE_CLEAR /*28*/:
                    setWidth(a.getDimensionPixelSize(attr, -1));
                    break;
                case KeyEvent.KEYCODE_A /*29*/:
                    setMinEms(a.getInt(attr, -1));
                    break;
                case KeyEvent.KEYCODE_B /*30*/:
                    if (!a.getBoolean(attr, DEBUG_EXTRACT)) {
                        break;
                    }
                    setHorizontallyScrolling(true);
                    break;
                case KeyEvent.KEYCODE_C /*31*/:
                    password = a.getBoolean(attr, password);
                    break;
                case AccessibilityNodeInfo.ACTION_LONG_CLICK /*32*/:
                    singleLine = a.getBoolean(attr, singleLine);
                    break;
                case MotionEvent.AXIS_GENERIC_2 /*33*/:
                    selectallonfocus = a.getBoolean(attr, selectallonfocus);
                    break;
                case MotionEvent.AXIS_GENERIC_3 /*34*/:
                    if (!a.getBoolean(attr, true)) {
                        setIncludeFontPadding(DEBUG_EXTRACT);
                        break;
                    }
                    break;
                case MotionEvent.AXIS_GENERIC_4 /*35*/:
                    maxlength = a.getInt(attr, -1);
                    break;
                case MotionEvent.AXIS_GENERIC_5 /*36*/:
                    shadowcolor = a.getInt(attr, MARQUEE_FADE_NORMAL);
                    break;
                case MotionEvent.AXIS_GENERIC_6 /*37*/:
                    dx = a.getFloat(attr, 0.0f);
                    break;
                case MotionEvent.AXIS_GENERIC_7 /*38*/:
                    dy = a.getFloat(attr, 0.0f);
                    break;
                case MotionEvent.AXIS_GENERIC_8 /*39*/:
                    r = a.getFloat(attr, 0.0f);
                    break;
                case MotionEvent.AXIS_GENERIC_9 /*40*/:
                    numeric = a.getInt(attr, numeric);
                    break;
                case MotionEvent.AXIS_GENERIC_10 /*41*/:
                    digits = a.getText(attr);
                    break;
                case MotionEvent.AXIS_GENERIC_11 /*42*/:
                    phone = a.getBoolean(attr, phone);
                    break;
                case MotionEvent.AXIS_GENERIC_12 /*43*/:
                    inputMethod = a.getText(attr);
                    break;
                case MotionEvent.AXIS_GENERIC_13 /*44*/:
                    autocap = a.getInt(attr, autocap);
                    break;
                case MotionEvent.AXIS_GENERIC_14 /*45*/:
                    autotext = a.getBoolean(attr, autotext);
                    break;
                case MotionEvent.AXIS_GENERIC_15 /*46*/:
                    editable = a.getBoolean(attr, editable);
                    break;
                case MotionEvent.AXIS_GENERIC_16 /*47*/:
                    this.mFreezesText = a.getBoolean(attr, DEBUG_EXTRACT);
                    break;
                case LayoutParams.SOFT_INPUT_ADJUST_NOTHING /*48*/:
                    drawableTop = a.getDrawable(attr);
                    break;
                case KeyEvent.KEYCODE_U /*49*/:
                    drawableBottom = a.getDrawable(attr);
                    break;
                case SpellChecker.MAX_NUMBER_OF_WORDS /*50*/:
                    drawableLeft = a.getDrawable(attr);
                    break;
                case KeyEvent.KEYCODE_W /*51*/:
                    drawableRight = a.getDrawable(attr);
                    break;
                case KeyEvent.KEYCODE_X /*52*/:
                    drawablePadding = a.getDimensionPixelSize(attr, drawablePadding);
                    break;
                case KeyEvent.KEYCODE_Y /*53*/:
                    this.mSpacingAdd = (float) a.getDimensionPixelSize(attr, (int) this.mSpacingAdd);
                    break;
                case KeyEvent.KEYCODE_Z /*54*/:
                    this.mSpacingMult = a.getFloat(attr, this.mSpacingMult);
                    break;
                case KeyEvent.KEYCODE_COMMA /*55*/:
                    setMarqueeRepeatLimit(a.getInt(attr, this.mMarqueeRepeatLimit));
                    break;
                case KeyEvent.KEYCODE_PERIOD /*56*/:
                    inputType = a.getInt(attr, MARQUEE_FADE_NORMAL);
                    break;
                case KeyEvent.KEYCODE_ALT_LEFT /*57*/:
                    setPrivateImeOptions(a.getString(attr));
                    break;
                case KeyEvent.KEYCODE_ALT_RIGHT /*58*/:
                    try {
                        setInputExtras(a.getResourceId(attr, MARQUEE_FADE_NORMAL));
                        break;
                    } catch (Throwable e) {
                        Log.w(LOG_TAG, "Failure reading input extras", e);
                        break;
                    } catch (Throwable e2) {
                        Log.w(LOG_TAG, "Failure reading input extras", e2);
                        break;
                    }
                case KeyEvent.KEYCODE_SHIFT_LEFT /*59*/:
                    createEditorIfNeeded();
                    this.mEditor.createInputContentTypeIfNeeded();
                    this.mEditor.mInputContentType.imeOptions = a.getInt(attr, this.mEditor.mInputContentType.imeOptions);
                    break;
                case KeyEvent.KEYCODE_SHIFT_RIGHT /*60*/:
                    createEditorIfNeeded();
                    this.mEditor.createInputContentTypeIfNeeded();
                    this.mEditor.mInputContentType.imeActionLabel = a.getText(attr);
                    break;
                case KeyEvent.KEYCODE_TAB /*61*/:
                    createEditorIfNeeded();
                    this.mEditor.createInputContentTypeIfNeeded();
                    this.mEditor.mInputContentType.imeActionId = a.getInt(attr, this.mEditor.mInputContentType.imeActionId);
                    break;
                case KeyEvent.KEYCODE_SPACE /*62*/:
                    this.mTextSelectHandleLeftRes = a.getResourceId(attr, MARQUEE_FADE_NORMAL);
                    break;
                case KeyEvent.KEYCODE_SYM /*63*/:
                    this.mTextSelectHandleRightRes = a.getResourceId(attr, MARQUEE_FADE_NORMAL);
                    break;
                case AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS /*64*/:
                    this.mTextSelectHandleRes = a.getResourceId(attr, MARQUEE_FADE_NORMAL);
                    break;
                case KeyEvent.KEYCODE_DEL /*67*/:
                    setTextIsSelectable(a.getBoolean(attr, DEBUG_EXTRACT));
                    break;
                case KeyEvent.KEYCODE_EQUALS /*70*/:
                    this.mCursorDrawableRes = a.getResourceId(attr, MARQUEE_FADE_NORMAL);
                    break;
                case KeyEvent.KEYCODE_LEFT_BRACKET /*71*/:
                    this.mTextEditSuggestionItemLayout = a.getResourceId(attr, MARQUEE_FADE_NORMAL);
                    break;
                case KeyEvent.KEYCODE_RIGHT_BRACKET /*72*/:
                    allCaps = a.getBoolean(attr, DEBUG_EXTRACT);
                    break;
                case KeyEvent.KEYCODE_BACKSLASH /*73*/:
                    drawableStart = a.getDrawable(attr);
                    break;
                case KeyEvent.KEYCODE_SEMICOLON /*74*/:
                    drawableEnd = a.getDrawable(attr);
                    break;
                case KeyEvent.KEYCODE_APOSTROPHE /*75*/:
                    fontFamily = a.getString(attr);
                    fontFamilyExplicit = true;
                    break;
                case KeyEvent.KEYCODE_SLASH /*76*/:
                    elegant = a.getBoolean(attr, DEBUG_EXTRACT);
                    break;
                case KeyEvent.KEYCODE_AT /*77*/:
                    letterSpacing = a.getFloat(attr, 0.0f);
                    break;
                case KeyEvent.KEYCODE_NUM /*78*/:
                    fontFeatureSettings = a.getString(attr);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
        BufferType bufferType = BufferType.EDITABLE;
        int variation = inputType & FileObserver.ALL_EVENTS;
        boolean passwordInputType = variation == 129 ? true : DEBUG_EXTRACT;
        boolean webPasswordInputType = variation == 225 ? true : DEBUG_EXTRACT;
        boolean numberPasswordInputType = variation == 18 ? true : DEBUG_EXTRACT;
        if (inputMethod == null) {
            if (digits == null) {
                if (inputType == 0) {
                    if (!phone) {
                        if (numeric == 0) {
                            if (!autotext && autocap == -1) {
                                if (!isTextSelectable()) {
                                    if (!editable) {
                                        if (this.mEditor != null) {
                                            this.mEditor.mKeyListener = null;
                                        }
                                        switch (buffertype) {
                                            case MARQUEE_FADE_NORMAL /*0*/:
                                                bufferType = BufferType.NORMAL;
                                                break;
                                            case SANS /*1*/:
                                                bufferType = BufferType.SPANNABLE;
                                                break;
                                            case SIGNED /*2*/:
                                                bufferType = BufferType.EDITABLE;
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    createEditorIfNeeded();
                                    this.mEditor.mKeyListener = TextKeyListener.getInstance();
                                    this.mEditor.mInputType = SANS;
                                } else {
                                    if (this.mEditor != null) {
                                        this.mEditor.mKeyListener = null;
                                        this.mEditor.mInputType = MARQUEE_FADE_NORMAL;
                                    }
                                    bufferType = BufferType.SPANNABLE;
                                    setMovementMethod(ArrowKeyMovementMethod.getInstance());
                                }
                            } else {
                                Capitalize cap;
                                inputType = SANS;
                                switch (autocap) {
                                    case SANS /*1*/:
                                        cap = Capitalize.SENTENCES;
                                        inputType = SANS | AccessibilityNodeInfo.ACTION_COPY;
                                        break;
                                    case SIGNED /*2*/:
                                        cap = Capitalize.WORDS;
                                        inputType = SANS | AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
                                        break;
                                    case MONOSPACE /*3*/:
                                        cap = Capitalize.CHARACTERS;
                                        inputType = SANS | AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
                                        break;
                                    default:
                                        cap = Capitalize.NONE;
                                        break;
                                }
                                createEditorIfNeeded();
                                this.mEditor.mKeyListener = TextKeyListener.getInstance(autotext, cap);
                                this.mEditor.mInputType = inputType;
                            }
                        } else {
                            createEditorIfNeeded();
                            this.mEditor.mKeyListener = DigitsKeyListener.getInstance((numeric & SIGNED) != 0 ? SANS : MARQUEE_FADE_NORMAL, (numeric & DECIMAL) != 0 ? true : DEBUG_EXTRACT);
                            inputType = SIGNED;
                            if ((numeric & SIGNED) != 0) {
                                inputType = SIGNED | AccessibilityNodeInfo.ACTION_SCROLL_FORWARD;
                            }
                            if ((numeric & DECIMAL) != 0) {
                                inputType |= AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD;
                            }
                            this.mEditor.mInputType = inputType;
                        }
                    } else {
                        createEditorIfNeeded();
                        this.mEditor.mKeyListener = DialerKeyListener.getInstance();
                        this.mEditor.mInputType = MONOSPACE;
                    }
                } else {
                    setInputType(inputType, true);
                    singleLine = !isMultilineInputType(inputType) ? true : DEBUG_EXTRACT;
                }
            } else {
                createEditorIfNeeded();
                this.mEditor.mKeyListener = DigitsKeyListener.getInstance(digits.toString());
                this.mEditor.mInputType = inputType != 0 ? inputType : SANS;
            }
        } else {
            try {
                Class<?> c = Class.forName(inputMethod.toString());
                try {
                    createEditorIfNeeded();
                    this.mEditor.mKeyListener = (KeyListener) c.newInstance();
                    try {
                        Editor editor = this.mEditor;
                        if (inputType != 0) {
                            i2 = inputType;
                        } else {
                            i2 = this.mEditor.mKeyListener.getInputType();
                        }
                        editor.mInputType = i2;
                    } catch (IncompatibleClassChangeError e3) {
                        this.mEditor.mInputType = SANS;
                    }
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                } catch (Throwable ex2) {
                    throw new RuntimeException(ex2);
                }
            } catch (Throwable ex22) {
                throw new RuntimeException(ex22);
            }
        }
        if (this.mEditor != null) {
            this.mEditor.adjustInputType(password, passwordInputType, webPasswordInputType, numberPasswordInputType);
        }
        if (selectallonfocus) {
            createEditorIfNeeded();
            this.mEditor.mSelectAllOnFocus = true;
            if (bufferType == BufferType.NORMAL) {
                bufferType = BufferType.SPANNABLE;
            }
        }
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        setRelativeDrawablesIfNeeded(drawableStart, drawableEnd);
        setCompoundDrawablePadding(drawablePadding);
        setInputTypeSingleLine(singleLine);
        applySingleLine(singleLine, singleLine, singleLine);
        if (singleLine && getKeyListener() == null && ellipsize < 0) {
            ellipsize = MONOSPACE;
        }
        switch (ellipsize) {
            case SANS /*1*/:
                setEllipsize(TruncateAt.START);
                break;
            case SIGNED /*2*/:
                setEllipsize(TruncateAt.MIDDLE);
                break;
            case MONOSPACE /*3*/:
                setEllipsize(TruncateAt.END);
                break;
            case DECIMAL /*4*/:
                if (ViewConfiguration.get(context).isFadingMarqueeEnabled()) {
                    setHorizontalFadingEdgeEnabled(true);
                    this.mMarqueeFadeMode = MARQUEE_FADE_NORMAL;
                } else {
                    setHorizontalFadingEdgeEnabled(DEBUG_EXTRACT);
                    this.mMarqueeFadeMode = SANS;
                }
                setEllipsize(TruncateAt.MARQUEE);
                break;
        }
        if (textColor == null) {
            textColor = ColorStateList.valueOf(Color.BLACK);
        }
        setTextColor(textColor);
        setHintTextColor(textColorHint);
        setLinkTextColor(textColorLink);
        if (textColorHighlight != 0) {
            setHighlightColor(textColorHighlight);
        }
        setRawTextSize((float) textSize);
        setElegantTextHeight(elegant);
        setLetterSpacing(letterSpacing);
        setFontFeatureSettings(fontFeatureSettings);
        if (allCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        }
        if (password || passwordInputType || webPasswordInputType || numberPasswordInputType) {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            typefaceIndex = MONOSPACE;
        } else if (this.mEditor != null) {
            i2 = this.mEditor.mInputType & FileObserver.ALL_EVENTS;
            if (r0 == 129) {
                typefaceIndex = MONOSPACE;
            }
        }
        if (!(typefaceIndex == -1 || fontFamilyExplicit)) {
            fontFamily = null;
        }
        setTypefaceFromAttrs(fontFamily, typefaceIndex, styleIndex);
        if (shadowcolor != 0) {
            setShadowLayer(r, dx, dy, shadowcolor);
        }
        if (maxlength >= 0) {
            InputFilter[] inputFilterArr = new InputFilter[SANS];
            inputFilterArr[MARQUEE_FADE_NORMAL] = new LengthFilter(maxlength);
            setFilters(inputFilterArr);
        } else {
            setFilters(NO_FILTERS);
        }
        setText(text, bufferType);
        if (hint != null) {
            setHint(hint);
        }
        a = context.obtainStyledAttributes(attrs, com.android.internal.R.styleable.View, defStyleAttr, defStyleRes);
        boolean focusable = (this.mMovement == null && getKeyListener() == null) ? DEBUG_EXTRACT : true;
        boolean clickable = (focusable || isClickable()) ? true : DEBUG_EXTRACT;
        boolean longClickable = (focusable || isLongClickable()) ? true : DEBUG_EXTRACT;
        n = a.getIndexCount();
        for (i = MARQUEE_FADE_NORMAL; i < n; i += SANS) {
            attr = a.getIndex(i);
            switch (attr) {
                case RelativeLayout.ALIGN_END /*19*/:
                    focusable = a.getBoolean(attr, focusable);
                    break;
                case KeyEvent.KEYCODE_B /*30*/:
                    clickable = a.getBoolean(attr, clickable);
                    break;
                case KeyEvent.KEYCODE_C /*31*/:
                    longClickable = a.getBoolean(attr, longClickable);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
        setFocusable(focusable);
        setClickable(clickable);
        setLongClickable(longClickable);
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(SANS);
        }
    }

    private void setTypefaceFromAttrs(String familyName, int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        if (familyName != null) {
            tf = Typeface.create(familyName, styleIndex);
            if (tf != null) {
                setTypeface(tf);
                return;
            }
        }
        switch (typefaceIndex) {
            case SANS /*1*/:
                tf = Typeface.SANS_SERIF;
                break;
            case SIGNED /*2*/:
                tf = Typeface.SERIF;
                break;
            case MONOSPACE /*3*/:
                tf = Typeface.MONOSPACE;
                break;
        }
        setTypeface(tf, styleIndex);
    }

    private void setRelativeDrawablesIfNeeded(Drawable start, Drawable end) {
        boolean hasRelativeDrawables;
        if (start == null && end == null) {
            hasRelativeDrawables = DEBUG_EXTRACT;
        } else {
            hasRelativeDrawables = true;
        }
        if (hasRelativeDrawables) {
            Drawables dr = this.mDrawables;
            if (dr == null) {
                dr = new Drawables(getContext());
                this.mDrawables = dr;
            }
            this.mDrawables.mOverride = true;
            Rect compoundRect = dr.mCompoundRect;
            int[] state = getDrawableState();
            if (start != null) {
                start.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, start.getIntrinsicWidth(), start.getIntrinsicHeight());
                start.setState(state);
                start.copyBounds(compoundRect);
                start.setCallback(this);
                dr.mDrawableStart = start;
                dr.mDrawableSizeStart = compoundRect.width();
                dr.mDrawableHeightStart = compoundRect.height();
            } else {
                dr.mDrawableHeightStart = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeStart = MARQUEE_FADE_NORMAL;
            }
            if (end != null) {
                end.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, end.getIntrinsicWidth(), end.getIntrinsicHeight());
                end.setState(state);
                end.copyBounds(compoundRect);
                end.setCallback(this);
                dr.mDrawableEnd = end;
                dr.mDrawableSizeEnd = compoundRect.width();
                dr.mDrawableHeightEnd = compoundRect.height();
            } else {
                dr.mDrawableHeightEnd = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeEnd = MARQUEE_FADE_NORMAL;
            }
            resetResolvedDrawables();
            resolveDrawables();
        }
    }

    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            InputMethodManager imm;
            if (!enabled) {
                imm = InputMethodManager.peekInstance();
                if (imm != null && imm.isActive(this)) {
                    imm.hideSoftInputFromWindow(getWindowToken(), MARQUEE_FADE_NORMAL);
                }
            }
            super.setEnabled(enabled);
            if (enabled) {
                imm = InputMethodManager.peekInstance();
                if (imm != null) {
                    imm.restartInput(this);
                }
            }
            if (this.mEditor != null) {
                this.mEditor.invalidateTextDisplayList();
                this.mEditor.prepareCursorControllers();
                this.mEditor.makeBlink();
            }
        }
    }

    public void setTypeface(Typeface tf, int style) {
        boolean z = DEBUG_EXTRACT;
        if (style > 0) {
            int typefaceStyle;
            float f;
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }
            setTypeface(tf);
            if (tf != null) {
                typefaceStyle = tf.getStyle();
            } else {
                typefaceStyle = MARQUEE_FADE_NORMAL;
            }
            int need = style & (typefaceStyle ^ -1);
            TextPaint textPaint = this.mTextPaint;
            if ((need & SANS) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            textPaint = this.mTextPaint;
            if ((need & SIGNED) != 0) {
                f = -0.25f;
            } else {
                f = 0.0f;
            }
            textPaint.setTextSkewX(f);
            return;
        }
        this.mTextPaint.setFakeBoldText(DEBUG_EXTRACT);
        this.mTextPaint.setTextSkewX(0.0f);
        setTypeface(tf);
    }

    protected boolean getDefaultEditable() {
        return DEBUG_EXTRACT;
    }

    protected MovementMethod getDefaultMovementMethod() {
        return null;
    }

    @CapturedViewProperty
    public CharSequence getText() {
        return this.mText;
    }

    public int length() {
        return this.mText.length();
    }

    public Editable getEditableText() {
        return this.mText instanceof Editable ? (Editable) this.mText : null;
    }

    public int getLineHeight() {
        return FastMath.round((((float) this.mTextPaint.getFontMetricsInt(null)) * this.mSpacingMult) + this.mSpacingAdd);
    }

    public final Layout getLayout() {
        return this.mLayout;
    }

    final Layout getHintLayout() {
        return this.mHintLayout;
    }

    public final UndoManager getUndoManager() {
        return this.mEditor == null ? null : this.mEditor.mUndoManager;
    }

    public final void setUndoManager(UndoManager undoManager, String tag) {
        if (undoManager != null) {
            createEditorIfNeeded();
            this.mEditor.mUndoManager = undoManager;
            this.mEditor.mUndoOwner = undoManager.getOwner(tag, this);
            this.mEditor.mUndoInputFilter = new UndoInputFilter(this.mEditor);
            if (!(this.mText instanceof Editable)) {
                setText(this.mText, BufferType.EDITABLE);
            }
            setFilters((Editable) this.mText, this.mFilters);
        } else if (this.mEditor != null) {
            this.mEditor.mUndoManager = null;
            this.mEditor.mUndoOwner = null;
            this.mEditor.mUndoInputFilter = null;
        }
    }

    public final KeyListener getKeyListener() {
        return this.mEditor == null ? null : this.mEditor.mKeyListener;
    }

    public void setKeyListener(KeyListener input) {
        setKeyListenerOnly(input);
        fixFocusableAndClickableSettings();
        if (input != null) {
            createEditorIfNeeded();
            try {
                this.mEditor.mInputType = this.mEditor.mKeyListener.getInputType();
            } catch (IncompatibleClassChangeError e) {
                this.mEditor.mInputType = SANS;
            }
            setInputTypeSingleLine(this.mSingleLine);
        } else if (this.mEditor != null) {
            this.mEditor.mInputType = MARQUEE_FADE_NORMAL;
        }
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (imm != null) {
            imm.restartInput(this);
        }
    }

    private void setKeyListenerOnly(KeyListener input) {
        if (this.mEditor != null || input != null) {
            createEditorIfNeeded();
            if (this.mEditor.mKeyListener != input) {
                this.mEditor.mKeyListener = input;
                if (!(input == null || (this.mText instanceof Editable))) {
                    setText(this.mText);
                }
                setFilters((Editable) this.mText, this.mFilters);
            }
        }
    }

    public final MovementMethod getMovementMethod() {
        return this.mMovement;
    }

    public final void setMovementMethod(MovementMethod movement) {
        if (this.mMovement != movement) {
            this.mMovement = movement;
            if (!(movement == null || (this.mText instanceof Spannable))) {
                setText(this.mText);
            }
            fixFocusableAndClickableSettings();
            if (this.mEditor != null) {
                this.mEditor.prepareCursorControllers();
            }
        }
    }

    private void fixFocusableAndClickableSettings() {
        if (this.mMovement == null && (this.mEditor == null || this.mEditor.mKeyListener == null)) {
            setFocusable(DEBUG_EXTRACT);
            setClickable(DEBUG_EXTRACT);
            setLongClickable(DEBUG_EXTRACT);
            return;
        }
        setFocusable(true);
        setClickable(true);
        setLongClickable(true);
    }

    public final TransformationMethod getTransformationMethod() {
        return this.mTransformation;
    }

    public final void setTransformationMethod(TransformationMethod method) {
        if (method != this.mTransformation) {
            if (this.mTransformation != null && (this.mText instanceof Spannable)) {
                ((Spannable) this.mText).removeSpan(this.mTransformation);
            }
            this.mTransformation = method;
            if (method instanceof TransformationMethod2) {
                boolean z;
                TransformationMethod2 method2 = (TransformationMethod2) method;
                if (isTextSelectable() || (this.mText instanceof Editable)) {
                    z = DEBUG_EXTRACT;
                } else {
                    z = true;
                }
                this.mAllowTransformationLengthChange = z;
                method2.setLengthChangesAllowed(this.mAllowTransformationLengthChange);
            } else {
                this.mAllowTransformationLengthChange = DEBUG_EXTRACT;
            }
            setText(this.mText);
            if (hasPasswordTransformationMethod()) {
                notifyViewAccessibilityStateChangedIfNeeded(MARQUEE_FADE_NORMAL);
            }
        }
    }

    public int getCompoundPaddingTop() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mDrawableTop == null) {
            return this.mPaddingTop;
        }
        return (this.mPaddingTop + dr.mDrawablePadding) + dr.mDrawableSizeTop;
    }

    public int getCompoundPaddingBottom() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mDrawableBottom == null) {
            return this.mPaddingBottom;
        }
        return (this.mPaddingBottom + dr.mDrawablePadding) + dr.mDrawableSizeBottom;
    }

    public int getCompoundPaddingLeft() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mDrawableLeft == null) {
            return this.mPaddingLeft;
        }
        return (this.mPaddingLeft + dr.mDrawablePadding) + dr.mDrawableSizeLeft;
    }

    public int getCompoundPaddingRight() {
        Drawables dr = this.mDrawables;
        if (dr == null || dr.mDrawableRight == null) {
            return this.mPaddingRight;
        }
        return (this.mPaddingRight + dr.mDrawablePadding) + dr.mDrawableSizeRight;
    }

    public int getCompoundPaddingStart() {
        resolveDrawables();
        switch (getLayoutDirection()) {
            case SANS /*1*/:
                return getCompoundPaddingRight();
            default:
                return getCompoundPaddingLeft();
        }
    }

    public int getCompoundPaddingEnd() {
        resolveDrawables();
        switch (getLayoutDirection()) {
            case SANS /*1*/:
                return getCompoundPaddingLeft();
            default:
                return getCompoundPaddingRight();
        }
    }

    public int getExtendedPaddingTop() {
        if (this.mMaxMode != SANS) {
            return getCompoundPaddingTop();
        }
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingTop();
        }
        int top = getCompoundPaddingTop();
        int viewht = (getHeight() - top) - getCompoundPaddingBottom();
        int layoutht = this.mLayout.getLineTop(this.mMaximum);
        if (layoutht >= viewht) {
            return top;
        }
        int gravity = this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL;
        if (gravity == 48) {
            return top;
        }
        if (gravity == 80) {
            return (top + viewht) - layoutht;
        }
        return top + ((viewht - layoutht) / SIGNED);
    }

    public int getExtendedPaddingBottom() {
        if (this.mMaxMode != SANS) {
            return getCompoundPaddingBottom();
        }
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingBottom();
        }
        int top = getCompoundPaddingTop();
        int bottom = getCompoundPaddingBottom();
        int viewht = (getHeight() - top) - bottom;
        int layoutht = this.mLayout.getLineTop(this.mMaximum);
        if (layoutht >= viewht) {
            return bottom;
        }
        int gravity = this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL;
        if (gravity == 48) {
            return (bottom + viewht) - layoutht;
        }
        return gravity != 80 ? bottom + ((viewht - layoutht) / SIGNED) : bottom;
    }

    public int getTotalPaddingLeft() {
        return getCompoundPaddingLeft();
    }

    public int getTotalPaddingRight() {
        return getCompoundPaddingRight();
    }

    public int getTotalPaddingStart() {
        return getCompoundPaddingStart();
    }

    public int getTotalPaddingEnd() {
        return getCompoundPaddingEnd();
    }

    public int getTotalPaddingTop() {
        return getExtendedPaddingTop() + getVerticalOffset(true);
    }

    public int getTotalPaddingBottom() {
        return getExtendedPaddingBottom() + getBottomVerticalOffset(true);
    }

    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        boolean drawables;
        Drawables dr = this.mDrawables;
        if (dr != null) {
            if (dr.mDrawableStart != null) {
                dr.mDrawableStart.setCallback(null);
            }
            dr.mDrawableStart = null;
            if (dr.mDrawableEnd != null) {
                dr.mDrawableEnd.setCallback(null);
            }
            dr.mDrawableEnd = null;
            dr.mDrawableHeightStart = MARQUEE_FADE_NORMAL;
            dr.mDrawableSizeStart = MARQUEE_FADE_NORMAL;
            dr.mDrawableHeightEnd = MARQUEE_FADE_NORMAL;
            dr.mDrawableSizeEnd = MARQUEE_FADE_NORMAL;
        }
        if (left == null && top == null && right == null && bottom == null) {
            drawables = DEBUG_EXTRACT;
        } else {
            drawables = true;
        }
        if (drawables) {
            if (dr == null) {
                dr = new Drawables(getContext());
                this.mDrawables = dr;
            }
            this.mDrawables.mOverride = DEBUG_EXTRACT;
            if (!(dr.mDrawableLeft == left || dr.mDrawableLeft == null)) {
                dr.mDrawableLeft.setCallback(null);
            }
            dr.mDrawableLeft = left;
            if (!(dr.mDrawableTop == top || dr.mDrawableTop == null)) {
                dr.mDrawableTop.setCallback(null);
            }
            dr.mDrawableTop = top;
            if (!(dr.mDrawableRight == right || dr.mDrawableRight == null)) {
                dr.mDrawableRight.setCallback(null);
            }
            dr.mDrawableRight = right;
            if (!(dr.mDrawableBottom == bottom || dr.mDrawableBottom == null)) {
                dr.mDrawableBottom.setCallback(null);
            }
            dr.mDrawableBottom = bottom;
            Rect compoundRect = dr.mCompoundRect;
            int[] state = getDrawableState();
            if (left != null) {
                left.setState(state);
                left.copyBounds(compoundRect);
                left.setCallback(this);
                dr.mDrawableSizeLeft = compoundRect.width();
                dr.mDrawableHeightLeft = compoundRect.height();
            } else {
                dr.mDrawableHeightLeft = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeLeft = MARQUEE_FADE_NORMAL;
            }
            if (right != null) {
                right.setState(state);
                right.copyBounds(compoundRect);
                right.setCallback(this);
                dr.mDrawableSizeRight = compoundRect.width();
                dr.mDrawableHeightRight = compoundRect.height();
            } else {
                dr.mDrawableHeightRight = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeRight = MARQUEE_FADE_NORMAL;
            }
            if (top != null) {
                top.setState(state);
                top.copyBounds(compoundRect);
                top.setCallback(this);
                dr.mDrawableSizeTop = compoundRect.height();
                dr.mDrawableWidthTop = compoundRect.width();
            } else {
                dr.mDrawableWidthTop = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeTop = MARQUEE_FADE_NORMAL;
            }
            if (bottom != null) {
                bottom.setState(state);
                bottom.copyBounds(compoundRect);
                bottom.setCallback(this);
                dr.mDrawableSizeBottom = compoundRect.height();
                dr.mDrawableWidthBottom = compoundRect.width();
            } else {
                dr.mDrawableWidthBottom = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeBottom = MARQUEE_FADE_NORMAL;
            }
        } else if (dr != null) {
            if (dr.mDrawablePadding == 0) {
                this.mDrawables = null;
            } else {
                if (dr.mDrawableLeft != null) {
                    dr.mDrawableLeft.setCallback(null);
                }
                dr.mDrawableLeft = null;
                if (dr.mDrawableTop != null) {
                    dr.mDrawableTop.setCallback(null);
                }
                dr.mDrawableTop = null;
                if (dr.mDrawableRight != null) {
                    dr.mDrawableRight.setCallback(null);
                }
                dr.mDrawableRight = null;
                if (dr.mDrawableBottom != null) {
                    dr.mDrawableBottom.setCallback(null);
                }
                dr.mDrawableBottom = null;
                dr.mDrawableHeightLeft = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeLeft = MARQUEE_FADE_NORMAL;
                dr.mDrawableHeightRight = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeRight = MARQUEE_FADE_NORMAL;
                dr.mDrawableWidthTop = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeTop = MARQUEE_FADE_NORMAL;
                dr.mDrawableWidthBottom = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeBottom = MARQUEE_FADE_NORMAL;
            }
        }
        if (dr != null) {
            dr.mDrawableLeftInitial = left;
            dr.mDrawableRightInitial = right;
        }
        resetResolvedDrawables();
        resolveDrawables();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        Drawable drawable;
        Drawable drawable2 = null;
        Context context = getContext();
        if (left != 0) {
            drawable = context.getDrawable(left);
        } else {
            drawable = null;
        }
        Drawable drawable3 = top != 0 ? context.getDrawable(top) : null;
        Drawable drawable4 = right != 0 ? context.getDrawable(right) : null;
        if (bottom != 0) {
            drawable2 = context.getDrawable(bottom);
        }
        setCompoundDrawablesWithIntrinsicBounds(drawable, drawable3, drawable4, drawable2);
    }

    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        }
        if (right != null) {
            right.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, right.getIntrinsicWidth(), right.getIntrinsicHeight());
        }
        if (top != null) {
            top.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, top.getIntrinsicWidth(), top.getIntrinsicHeight());
        }
        if (bottom != null) {
            bottom.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
        }
        setCompoundDrawables(left, top, right, bottom);
    }

    public void setCompoundDrawablesRelative(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        boolean drawables;
        Drawables dr = this.mDrawables;
        if (dr != null) {
            if (dr.mDrawableLeft != null) {
                dr.mDrawableLeft.setCallback(null);
            }
            dr.mDrawableLeftInitial = null;
            dr.mDrawableLeft = null;
            if (dr.mDrawableRight != null) {
                dr.mDrawableRight.setCallback(null);
            }
            dr.mDrawableRightInitial = null;
            dr.mDrawableRight = null;
            dr.mDrawableHeightLeft = MARQUEE_FADE_NORMAL;
            dr.mDrawableSizeLeft = MARQUEE_FADE_NORMAL;
            dr.mDrawableHeightRight = MARQUEE_FADE_NORMAL;
            dr.mDrawableSizeRight = MARQUEE_FADE_NORMAL;
        }
        if (start == null && top == null && end == null && bottom == null) {
            drawables = DEBUG_EXTRACT;
        } else {
            drawables = true;
        }
        if (drawables) {
            if (dr == null) {
                dr = new Drawables(getContext());
                this.mDrawables = dr;
            }
            this.mDrawables.mOverride = true;
            if (!(dr.mDrawableStart == start || dr.mDrawableStart == null)) {
                dr.mDrawableStart.setCallback(null);
            }
            dr.mDrawableStart = start;
            if (!(dr.mDrawableTop == top || dr.mDrawableTop == null)) {
                dr.mDrawableTop.setCallback(null);
            }
            dr.mDrawableTop = top;
            if (!(dr.mDrawableEnd == end || dr.mDrawableEnd == null)) {
                dr.mDrawableEnd.setCallback(null);
            }
            dr.mDrawableEnd = end;
            if (!(dr.mDrawableBottom == bottom || dr.mDrawableBottom == null)) {
                dr.mDrawableBottom.setCallback(null);
            }
            dr.mDrawableBottom = bottom;
            Rect compoundRect = dr.mCompoundRect;
            int[] state = getDrawableState();
            if (start != null) {
                start.setState(state);
                start.copyBounds(compoundRect);
                start.setCallback(this);
                dr.mDrawableSizeStart = compoundRect.width();
                dr.mDrawableHeightStart = compoundRect.height();
            } else {
                dr.mDrawableHeightStart = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeStart = MARQUEE_FADE_NORMAL;
            }
            if (end != null) {
                end.setState(state);
                end.copyBounds(compoundRect);
                end.setCallback(this);
                dr.mDrawableSizeEnd = compoundRect.width();
                dr.mDrawableHeightEnd = compoundRect.height();
            } else {
                dr.mDrawableHeightEnd = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeEnd = MARQUEE_FADE_NORMAL;
            }
            if (top != null) {
                top.setState(state);
                top.copyBounds(compoundRect);
                top.setCallback(this);
                dr.mDrawableSizeTop = compoundRect.height();
                dr.mDrawableWidthTop = compoundRect.width();
            } else {
                dr.mDrawableWidthTop = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeTop = MARQUEE_FADE_NORMAL;
            }
            if (bottom != null) {
                bottom.setState(state);
                bottom.copyBounds(compoundRect);
                bottom.setCallback(this);
                dr.mDrawableSizeBottom = compoundRect.height();
                dr.mDrawableWidthBottom = compoundRect.width();
            } else {
                dr.mDrawableWidthBottom = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeBottom = MARQUEE_FADE_NORMAL;
            }
        } else if (dr != null) {
            if (dr.mDrawablePadding == 0) {
                this.mDrawables = null;
            } else {
                if (dr.mDrawableStart != null) {
                    dr.mDrawableStart.setCallback(null);
                }
                dr.mDrawableStart = null;
                if (dr.mDrawableTop != null) {
                    dr.mDrawableTop.setCallback(null);
                }
                dr.mDrawableTop = null;
                if (dr.mDrawableEnd != null) {
                    dr.mDrawableEnd.setCallback(null);
                }
                dr.mDrawableEnd = null;
                if (dr.mDrawableBottom != null) {
                    dr.mDrawableBottom.setCallback(null);
                }
                dr.mDrawableBottom = null;
                dr.mDrawableHeightStart = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeStart = MARQUEE_FADE_NORMAL;
                dr.mDrawableHeightEnd = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeEnd = MARQUEE_FADE_NORMAL;
                dr.mDrawableWidthTop = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeTop = MARQUEE_FADE_NORMAL;
                dr.mDrawableWidthBottom = MARQUEE_FADE_NORMAL;
                dr.mDrawableSizeBottom = MARQUEE_FADE_NORMAL;
            }
        }
        resetResolvedDrawables();
        resolveDrawables();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        Drawable drawable = null;
        Context context = getContext();
        Drawable drawable2 = start != 0 ? context.getDrawable(start) : null;
        Drawable drawable3 = top != 0 ? context.getDrawable(top) : null;
        Drawable drawable4 = end != 0 ? context.getDrawable(end) : null;
        if (bottom != 0) {
            drawable = context.getDrawable(bottom);
        }
        setCompoundDrawablesRelativeWithIntrinsicBounds(drawable2, drawable3, drawable4, drawable);
    }

    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (start != null) {
            start.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, start.getIntrinsicWidth(), start.getIntrinsicHeight());
        }
        if (end != null) {
            end.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, end.getIntrinsicWidth(), end.getIntrinsicHeight());
        }
        if (top != null) {
            top.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, top.getIntrinsicWidth(), top.getIntrinsicHeight());
        }
        if (bottom != null) {
            bottom.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
        }
        setCompoundDrawablesRelative(start, top, end, bottom);
    }

    public Drawable[] getCompoundDrawables() {
        Drawables dr = this.mDrawables;
        if (dr != null) {
            Drawable[] drawableArr = new Drawable[DECIMAL];
            drawableArr[MARQUEE_FADE_NORMAL] = dr.mDrawableLeft;
            drawableArr[SANS] = dr.mDrawableTop;
            drawableArr[SIGNED] = dr.mDrawableRight;
            drawableArr[MONOSPACE] = dr.mDrawableBottom;
            return drawableArr;
        }
        drawableArr = new Drawable[DECIMAL];
        drawableArr[MARQUEE_FADE_NORMAL] = null;
        drawableArr[SANS] = null;
        drawableArr[SIGNED] = null;
        drawableArr[MONOSPACE] = null;
        return drawableArr;
    }

    public Drawable[] getCompoundDrawablesRelative() {
        Drawables dr = this.mDrawables;
        if (dr != null) {
            Drawable[] drawableArr = new Drawable[DECIMAL];
            drawableArr[MARQUEE_FADE_NORMAL] = dr.mDrawableStart;
            drawableArr[SANS] = dr.mDrawableTop;
            drawableArr[SIGNED] = dr.mDrawableEnd;
            drawableArr[MONOSPACE] = dr.mDrawableBottom;
            return drawableArr;
        }
        drawableArr = new Drawable[DECIMAL];
        drawableArr[MARQUEE_FADE_NORMAL] = null;
        drawableArr[SANS] = null;
        drawableArr[SIGNED] = null;
        drawableArr[MONOSPACE] = null;
        return drawableArr;
    }

    @RemotableViewMethod
    public void setCompoundDrawablePadding(int pad) {
        Drawables dr = this.mDrawables;
        if (pad != 0) {
            if (dr == null) {
                dr = new Drawables(getContext());
                this.mDrawables = dr;
            }
            dr.mDrawablePadding = pad;
        } else if (dr != null) {
            dr.mDrawablePadding = pad;
        }
        invalidate();
        requestLayout();
    }

    public int getCompoundDrawablePadding() {
        Drawables dr = this.mDrawables;
        return dr != null ? dr.mDrawablePadding : MARQUEE_FADE_NORMAL;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        if (!(left == this.mPaddingLeft && right == this.mPaddingRight && top == this.mPaddingTop && bottom == this.mPaddingBottom)) {
            nullLayouts();
        }
        super.setPadding(left, top, right, bottom);
        invalidate();
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
        if (!(start == getPaddingStart() && end == getPaddingEnd() && top == this.mPaddingTop && bottom == this.mPaddingBottom)) {
            nullLayouts();
        }
        super.setPaddingRelative(start, top, end, bottom);
        invalidate();
    }

    public final int getAutoLinkMask() {
        return this.mAutoLinkMask;
    }

    public void setTextAppearance(Context context, int resid) {
        TypedArray appearance = context.obtainStyledAttributes(resid, com.android.internal.R.styleable.TextAppearance);
        int color = appearance.getColor(DECIMAL, MARQUEE_FADE_NORMAL);
        if (color != 0) {
            setHighlightColor(color);
        }
        ColorStateList colors = appearance.getColorStateList(MONOSPACE);
        if (colors != null) {
            setTextColor(colors);
        }
        int ts = appearance.getDimensionPixelSize(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL);
        if (ts != 0) {
            setRawTextSize((float) ts);
        }
        colors = appearance.getColorStateList(5);
        if (colors != null) {
            setHintTextColor(colors);
        }
        colors = appearance.getColorStateList(6);
        if (colors != null) {
            setLinkTextColor(colors);
        }
        setTypefaceFromAttrs(appearance.getString(12), appearance.getInt(SANS, -1), appearance.getInt(SIGNED, -1));
        int shadowcolor = appearance.getInt(7, MARQUEE_FADE_NORMAL);
        if (shadowcolor != 0) {
            setShadowLayer(appearance.getFloat(10, 0.0f), appearance.getFloat(8, 0.0f), appearance.getFloat(9, 0.0f), shadowcolor);
        }
        if (appearance.getBoolean(11, DEBUG_EXTRACT)) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        }
        if (appearance.hasValue(13)) {
            setElegantTextHeight(appearance.getBoolean(13, DEBUG_EXTRACT));
        }
        if (appearance.hasValue(14)) {
            setLetterSpacing(appearance.getFloat(14, 0.0f));
        }
        if (appearance.hasValue(15)) {
            setFontFeatureSettings(appearance.getString(15));
        }
        appearance.recycle();
    }

    public Locale getTextLocale() {
        return this.mTextPaint.getTextLocale();
    }

    public void setTextLocale(Locale locale) {
        this.mTextPaint.setTextLocale(locale);
    }

    @ExportedProperty(category = "text")
    public float getTextSize() {
        return this.mTextPaint.getTextSize();
    }

    @ExportedProperty(category = "text")
    public float getScaledTextSize() {
        return this.mTextPaint.getTextSize() / this.mTextPaint.density;
    }

    @ExportedProperty(category = "text", mapping = {@IntToString(from = 0, to = "NORMAL"), @IntToString(from = 1, to = "BOLD"), @IntToString(from = 2, to = "ITALIC"), @IntToString(from = 3, to = "BOLD_ITALIC")})
    public int getTypefaceStyle() {
        return this.mTextPaint.getTypeface().getStyle();
    }

    @RemotableViewMethod
    public void setTextSize(float size) {
        setTextSize(SIGNED, size);
    }

    public void setTextSize(int unit, float size) {
        Resources r;
        Context c = getContext();
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()));
    }

    private void setRawTextSize(float size) {
        if (size != this.mTextPaint.getTextSize()) {
            this.mTextPaint.setTextSize(size);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public float getTextScaleX() {
        return this.mTextPaint.getTextScaleX();
    }

    @RemotableViewMethod
    public void setTextScaleX(float size) {
        if (size != this.mTextPaint.getTextScaleX()) {
            this.mUserSetTextScaleX = true;
            this.mTextPaint.setTextScaleX(size);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setTypeface(Typeface tf) {
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public Typeface getTypeface() {
        return this.mTextPaint.getTypeface();
    }

    public void setElegantTextHeight(boolean elegant) {
        this.mTextPaint.setElegantTextHeight(elegant);
    }

    public float getLetterSpacing() {
        return this.mTextPaint.getLetterSpacing();
    }

    @RemotableViewMethod
    public void setLetterSpacing(float letterSpacing) {
        if (letterSpacing != this.mTextPaint.getLetterSpacing()) {
            this.mTextPaint.setLetterSpacing(letterSpacing);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public String getFontFeatureSettings() {
        return this.mTextPaint.getFontFeatureSettings();
    }

    @RemotableViewMethod
    public void setFontFeatureSettings(String fontFeatureSettings) {
        if (fontFeatureSettings != this.mTextPaint.getFontFeatureSettings()) {
            this.mTextPaint.setFontFeatureSettings(fontFeatureSettings);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    @RemotableViewMethod
    public void setTextColor(int color) {
        this.mTextColor = ColorStateList.valueOf(color);
        updateTextColors();
    }

    public void setTextColor(ColorStateList colors) {
        if (colors == null) {
            throw new NullPointerException();
        }
        this.mTextColor = colors;
        updateTextColors();
    }

    public final ColorStateList getTextColors() {
        return this.mTextColor;
    }

    public final int getCurrentTextColor() {
        return this.mCurTextColor;
    }

    @RemotableViewMethod
    public void setHighlightColor(int color) {
        if (this.mHighlightColor != color) {
            this.mHighlightColor = color;
            invalidate();
        }
    }

    public int getHighlightColor() {
        return this.mHighlightColor;
    }

    @RemotableViewMethod
    public final void setShowSoftInputOnFocus(boolean show) {
        createEditorIfNeeded();
        this.mEditor.mShowSoftInputOnFocus = show;
    }

    public final boolean getShowSoftInputOnFocus() {
        return (this.mEditor == null || this.mEditor.mShowSoftInputOnFocus) ? true : DEBUG_EXTRACT;
    }

    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.mTextPaint.setShadowLayer(radius, dx, dy, color);
        this.mShadowRadius = radius;
        this.mShadowDx = dx;
        this.mShadowDy = dy;
        this.mShadowColor = color;
        if (this.mEditor != null) {
            this.mEditor.invalidateTextDisplayList();
        }
        invalidate();
    }

    public float getShadowRadius() {
        return this.mShadowRadius;
    }

    public float getShadowDx() {
        return this.mShadowDx;
    }

    public float getShadowDy() {
        return this.mShadowDy;
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    public TextPaint getPaint() {
        return this.mTextPaint;
    }

    @RemotableViewMethod
    public final void setAutoLinkMask(int mask) {
        this.mAutoLinkMask = mask;
    }

    @RemotableViewMethod
    public final void setLinksClickable(boolean whether) {
        this.mLinksClickable = whether;
    }

    public final boolean getLinksClickable() {
        return this.mLinksClickable;
    }

    public URLSpan[] getUrls() {
        if (this.mText instanceof Spanned) {
            return (URLSpan[]) ((Spanned) this.mText).getSpans(MARQUEE_FADE_NORMAL, this.mText.length(), URLSpan.class);
        }
        return new URLSpan[MARQUEE_FADE_NORMAL];
    }

    @RemotableViewMethod
    public final void setHintTextColor(int color) {
        this.mHintTextColor = ColorStateList.valueOf(color);
        updateTextColors();
    }

    public final void setHintTextColor(ColorStateList colors) {
        this.mHintTextColor = colors;
        updateTextColors();
    }

    public final ColorStateList getHintTextColors() {
        return this.mHintTextColor;
    }

    public final int getCurrentHintTextColor() {
        return this.mHintTextColor != null ? this.mCurHintTextColor : this.mCurTextColor;
    }

    @RemotableViewMethod
    public final void setLinkTextColor(int color) {
        this.mLinkTextColor = ColorStateList.valueOf(color);
        updateTextColors();
    }

    public final void setLinkTextColor(ColorStateList colors) {
        this.mLinkTextColor = colors;
        updateTextColors();
    }

    public final ColorStateList getLinkTextColors() {
        return this.mLinkTextColor;
    }

    public void setGravity(int gravity) {
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.START;
        }
        if ((gravity & KeyEvent.KEYCODE_FORWARD_DEL) == 0) {
            gravity |= 48;
        }
        boolean newLayout = DEBUG_EXTRACT;
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) != (this.mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK)) {
            newLayout = true;
        }
        if (gravity != this.mGravity) {
            invalidate();
        }
        this.mGravity = gravity;
        if (this.mLayout != null && newLayout) {
            makeNewLayout(this.mLayout.getWidth(), this.mHintLayout == null ? MARQUEE_FADE_NORMAL : this.mHintLayout.getWidth(), UNKNOWN_BORING, UNKNOWN_BORING, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), true);
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public int getPaintFlags() {
        return this.mTextPaint.getFlags();
    }

    @RemotableViewMethod
    public void setPaintFlags(int flags) {
        if (this.mTextPaint.getFlags() != flags) {
            this.mTextPaint.setFlags(flags);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setHorizontallyScrolling(boolean whether) {
        if (this.mHorizontallyScrolling != whether) {
            this.mHorizontallyScrolling = whether;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean getHorizontallyScrolling() {
        return this.mHorizontallyScrolling;
    }

    @RemotableViewMethod
    public void setMinLines(int minlines) {
        this.mMinimum = minlines;
        this.mMinMode = SANS;
        requestLayout();
        invalidate();
    }

    public int getMinLines() {
        return this.mMinMode == SANS ? this.mMinimum : -1;
    }

    @RemotableViewMethod
    public void setMinHeight(int minHeight) {
        this.mMinimum = minHeight;
        this.mMinMode = SIGNED;
        requestLayout();
        invalidate();
    }

    public int getMinHeight() {
        return this.mMinMode == SIGNED ? this.mMinimum : -1;
    }

    @RemotableViewMethod
    public void setMaxLines(int maxlines) {
        this.mMaximum = maxlines;
        this.mMaxMode = SANS;
        requestLayout();
        invalidate();
    }

    public int getMaxLines() {
        return this.mMaxMode == SANS ? this.mMaximum : -1;
    }

    @RemotableViewMethod
    public void setMaxHeight(int maxHeight) {
        this.mMaximum = maxHeight;
        this.mMaxMode = SIGNED;
        requestLayout();
        invalidate();
    }

    public int getMaxHeight() {
        return this.mMaxMode == SIGNED ? this.mMaximum : -1;
    }

    @RemotableViewMethod
    public void setLines(int lines) {
        this.mMinimum = lines;
        this.mMaximum = lines;
        this.mMinMode = SANS;
        this.mMaxMode = SANS;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setHeight(int pixels) {
        this.mMinimum = pixels;
        this.mMaximum = pixels;
        this.mMinMode = SIGNED;
        this.mMaxMode = SIGNED;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMinEms(int minems) {
        this.mMinWidth = minems;
        this.mMinWidthMode = SANS;
        requestLayout();
        invalidate();
    }

    public int getMinEms() {
        return this.mMinWidthMode == SANS ? this.mMinWidth : -1;
    }

    @RemotableViewMethod
    public void setMinWidth(int minpixels) {
        this.mMinWidth = minpixels;
        this.mMinWidthMode = SIGNED;
        requestLayout();
        invalidate();
    }

    public int getMinWidth() {
        return this.mMinWidthMode == SIGNED ? this.mMinWidth : -1;
    }

    @RemotableViewMethod
    public void setMaxEms(int maxems) {
        this.mMaxWidth = maxems;
        this.mMaxWidthMode = SANS;
        requestLayout();
        invalidate();
    }

    public int getMaxEms() {
        return this.mMaxWidthMode == SANS ? this.mMaxWidth : -1;
    }

    @RemotableViewMethod
    public void setMaxWidth(int maxpixels) {
        this.mMaxWidth = maxpixels;
        this.mMaxWidthMode = SIGNED;
        requestLayout();
        invalidate();
    }

    public int getMaxWidth() {
        return this.mMaxWidthMode == SIGNED ? this.mMaxWidth : -1;
    }

    @RemotableViewMethod
    public void setEms(int ems) {
        this.mMinWidth = ems;
        this.mMaxWidth = ems;
        this.mMinWidthMode = SANS;
        this.mMaxWidthMode = SANS;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setWidth(int pixels) {
        this.mMinWidth = pixels;
        this.mMaxWidth = pixels;
        this.mMinWidthMode = SIGNED;
        this.mMaxWidthMode = SIGNED;
        requestLayout();
        invalidate();
    }

    public void setLineSpacing(float add, float mult) {
        if (this.mSpacingAdd != add || this.mSpacingMult != mult) {
            this.mSpacingAdd = add;
            this.mSpacingMult = mult;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public float getLineSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public float getLineSpacingExtra() {
        return this.mSpacingAdd;
    }

    public final void append(CharSequence text) {
        append(text, MARQUEE_FADE_NORMAL, text.length());
    }

    public void append(CharSequence text, int start, int end) {
        if (!(this.mText instanceof Editable)) {
            setText(this.mText, BufferType.EDITABLE);
        }
        ((Editable) this.mText).append(text, start, end);
    }

    private void updateTextColors() {
        boolean inval = DEBUG_EXTRACT;
        int color = this.mTextColor.getColorForState(getDrawableState(), MARQUEE_FADE_NORMAL);
        if (color != this.mCurTextColor) {
            this.mCurTextColor = color;
            inval = true;
        }
        if (this.mLinkTextColor != null) {
            color = this.mLinkTextColor.getColorForState(getDrawableState(), MARQUEE_FADE_NORMAL);
            if (color != this.mTextPaint.linkColor) {
                this.mTextPaint.linkColor = color;
                inval = true;
            }
        }
        if (this.mHintTextColor != null) {
            color = this.mHintTextColor.getColorForState(getDrawableState(), MARQUEE_FADE_NORMAL);
            if (color != this.mCurHintTextColor) {
                this.mCurHintTextColor = color;
                if (this.mText.length() == 0) {
                    inval = true;
                }
            }
        }
        if (inval) {
            if (this.mEditor != null) {
                this.mEditor.invalidateTextDisplayList();
            }
            invalidate();
        }
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if ((this.mTextColor != null && this.mTextColor.isStateful()) || ((this.mHintTextColor != null && this.mHintTextColor.isStateful()) || (this.mLinkTextColor != null && this.mLinkTextColor.isStateful()))) {
            updateTextColors();
        }
        Drawables dr = this.mDrawables;
        if (dr != null) {
            int[] state = getDrawableState();
            if (dr.mDrawableTop != null && dr.mDrawableTop.isStateful()) {
                dr.mDrawableTop.setState(state);
            }
            if (dr.mDrawableBottom != null && dr.mDrawableBottom.isStateful()) {
                dr.mDrawableBottom.setState(state);
            }
            if (dr.mDrawableLeft != null && dr.mDrawableLeft.isStateful()) {
                dr.mDrawableLeft.setState(state);
            }
            if (dr.mDrawableRight != null && dr.mDrawableRight.isStateful()) {
                dr.mDrawableRight.setState(state);
            }
            if (dr.mDrawableStart != null && dr.mDrawableStart.isStateful()) {
                dr.mDrawableStart.setState(state);
            }
            if (dr.mDrawableEnd != null && dr.mDrawableEnd.isStateful()) {
                dr.mDrawableEnd.setState(state);
            }
        }
    }

    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        Drawables dr = this.mDrawables;
        if (dr != null) {
            if (dr.mDrawableTop != null) {
                dr.mDrawableTop.setHotspot(x, y);
            }
            if (dr.mDrawableBottom != null) {
                dr.mDrawableBottom.setHotspot(x, y);
            }
            if (dr.mDrawableLeft != null) {
                dr.mDrawableLeft.setHotspot(x, y);
            }
            if (dr.mDrawableRight != null) {
                dr.mDrawableRight.setHotspot(x, y);
            }
            if (dr.mDrawableStart != null) {
                dr.mDrawableStart.setHotspot(x, y);
            }
            if (dr.mDrawableEnd != null) {
                dr.mDrawableEnd.setHotspot(x, y);
            }
        }
    }

    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        boolean save = this.mFreezesText;
        int start = MARQUEE_FADE_NORMAL;
        int end = MARQUEE_FADE_NORMAL;
        if (this.mText != null) {
            start = getSelectionStart();
            end = getSelectionEnd();
            if (start >= 0 || end >= 0) {
                save = true;
            }
        }
        if (!save) {
            return superState;
        }
        SavedState ss = new SavedState(superState);
        ss.selStart = start;
        ss.selEnd = end;
        if (this.mText instanceof Spanned) {
            Spannable sp = new SpannableStringBuilder(this.mText);
            if (this.mEditor != null) {
                removeMisspelledSpans(sp);
                sp.removeSpan(this.mEditor.mSuggestionRangeSpan);
            }
            ss.text = sp;
        } else {
            ss.text = this.mText.toString();
        }
        if (isFocused() && start >= 0 && end >= 0) {
            ss.frozenWithFocus = true;
        }
        ss.error = getError();
        return ss;
    }

    void removeMisspelledSpans(Spannable spannable) {
        SuggestionSpan[] suggestionSpans = (SuggestionSpan[]) spannable.getSpans(MARQUEE_FADE_NORMAL, spannable.length(), SuggestionSpan.class);
        for (int i = MARQUEE_FADE_NORMAL; i < suggestionSpans.length; i += SANS) {
            int flags = suggestionSpans[i].getFlags();
            if (!((flags & SANS) == 0 || (flags & SIGNED) == 0)) {
                spannable.removeSpan(suggestionSpans[i]);
            }
        }
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            if (ss.text != null) {
                setText(ss.text);
            }
            if (ss.selStart >= 0 && ss.selEnd >= 0 && (this.mText instanceof Spannable)) {
                int len = this.mText.length();
                if (ss.selStart > len || ss.selEnd > len) {
                    String restored = ProxyInfo.LOCAL_EXCL_LIST;
                    if (ss.text != null) {
                        restored = "(restored) ";
                    }
                    Log.e(LOG_TAG, "Saved cursor position " + ss.selStart + "/" + ss.selEnd + " out of range for " + restored + "text " + this.mText);
                } else {
                    Selection.setSelection((Spannable) this.mText, ss.selStart, ss.selEnd);
                    if (ss.frozenWithFocus) {
                        createEditorIfNeeded();
                        this.mEditor.mFrozenWithFocus = true;
                    }
                }
            }
            if (ss.error != null) {
                post(new AnonymousClass1(this, ss.error));
                return;
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @RemotableViewMethod
    public void setFreezesText(boolean freezesText) {
        this.mFreezesText = freezesText;
    }

    public boolean getFreezesText() {
        return this.mFreezesText;
    }

    public final void setEditableFactory(Factory factory) {
        this.mEditableFactory = factory;
        setText(this.mText);
    }

    public final void setSpannableFactory(Spannable.Factory factory) {
        this.mSpannableFactory = factory;
        setText(this.mText);
    }

    @RemotableViewMethod
    public final void setText(CharSequence text) {
        setText(text, this.mBufferType);
    }

    @RemotableViewMethod
    public final void setTextKeepState(CharSequence text) {
        setTextKeepState(text, this.mBufferType);
    }

    public void setText(CharSequence text, BufferType type) {
        setText(text, type, true, MARQUEE_FADE_NORMAL);
        if (this.mCharWrapper != null) {
            this.mCharWrapper.mChars = null;
        }
    }

    private void setText(CharSequence text, BufferType type, boolean notifyBefore, int oldlen) {
        int i;
        if (text == null) {
            text = ProxyInfo.LOCAL_EXCL_LIST;
        }
        if (!isSuggestionsEnabled()) {
            text = removeSuggestionSpans(text);
        }
        if (!this.mUserSetTextScaleX) {
            this.mTextPaint.setTextScaleX(LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        }
        if ((text instanceof Spanned) && ((Spanned) text).getSpanStart(TruncateAt.MARQUEE) >= 0) {
            if (ViewConfiguration.get(this.mContext).isFadingMarqueeEnabled()) {
                setHorizontalFadingEdgeEnabled(true);
                this.mMarqueeFadeMode = MARQUEE_FADE_NORMAL;
            } else {
                setHorizontalFadingEdgeEnabled(DEBUG_EXTRACT);
                this.mMarqueeFadeMode = SANS;
            }
            setEllipsize(TruncateAt.MARQUEE);
        }
        int n = this.mFilters.length;
        for (i = MARQUEE_FADE_NORMAL; i < n; i += SANS) {
            CharSequence out = this.mFilters[i].filter(text, MARQUEE_FADE_NORMAL, text.length(), EMPTY_SPANNED, MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL);
            if (out != null) {
                text = out;
            }
        }
        if (notifyBefore) {
            if (this.mText != null) {
                oldlen = this.mText.length();
                sendBeforeTextChanged(this.mText, MARQUEE_FADE_NORMAL, oldlen, text.length());
            } else {
                sendBeforeTextChanged(ProxyInfo.LOCAL_EXCL_LIST, MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, text.length());
            }
        }
        boolean needEditableForNotification = DEBUG_EXTRACT;
        if (!(this.mListeners == null || this.mListeners.size() == 0)) {
            needEditableForNotification = true;
        }
        if (type == BufferType.EDITABLE || getKeyListener() != null || needEditableForNotification) {
            createEditorIfNeeded();
            Editable t = this.mEditableFactory.newEditable(text);
            text = t;
            setFilters(t, this.mFilters);
            InputMethodManager imm = InputMethodManager.peekInstance();
            if (imm != null) {
                imm.restartInput(this);
            }
        } else if (type == BufferType.SPANNABLE || this.mMovement != null) {
            text = this.mSpannableFactory.newSpannable(text);
        } else if (!(text instanceof CharWrapper)) {
            text = TextUtils.stringOrSpannedString(text);
        }
        if (this.mAutoLinkMask != 0) {
            Spannable s2;
            if (type == BufferType.EDITABLE || (text instanceof Spannable)) {
                s2 = (Spannable) text;
            } else {
                s2 = this.mSpannableFactory.newSpannable(text);
            }
            if (Linkify.addLinks(s2, this.mAutoLinkMask)) {
                text = s2;
                type = type == BufferType.EDITABLE ? BufferType.EDITABLE : BufferType.SPANNABLE;
                this.mText = text;
                if (this.mLinksClickable && !textCanBeSelected()) {
                    setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        }
        this.mBufferType = type;
        this.mText = text;
        if (this.mTransformation == null) {
            this.mTransformed = text;
        } else {
            this.mTransformed = this.mTransformation.getTransformation(text, this);
        }
        int textLength = text.length();
        if ((text instanceof Spannable) && !this.mAllowTransformationLengthChange) {
            Spannable sp = (Spannable) text;
            ChangeWatcher[] watchers = (ChangeWatcher[]) sp.getSpans(MARQUEE_FADE_NORMAL, sp.length(), ChangeWatcher.class);
            int count = watchers.length;
            for (i = MARQUEE_FADE_NORMAL; i < count; i += SANS) {
                sp.removeSpan(watchers[i]);
            }
            if (this.mChangeWatcher == null) {
                TextView textView = this;
                this.mChangeWatcher = new ChangeWatcher();
            }
            sp.setSpan(this.mChangeWatcher, MARQUEE_FADE_NORMAL, textLength, 6553618);
            if (this.mEditor != null) {
                this.mEditor.addSpanWatchers(sp);
            }
            if (this.mTransformation != null) {
                sp.setSpan(this.mTransformation, MARQUEE_FADE_NORMAL, textLength, 18);
            }
            if (this.mMovement != null) {
                this.mMovement.initialize(this, (Spannable) text);
                if (this.mEditor != null) {
                    this.mEditor.mSelectionMoved = DEBUG_EXTRACT;
                }
            }
        }
        if (this.mLayout != null) {
            checkForRelayout();
        }
        sendOnTextChanged(text, MARQUEE_FADE_NORMAL, oldlen, textLength);
        onTextChanged(text, MARQUEE_FADE_NORMAL, oldlen, textLength);
        notifyViewAccessibilityStateChangedIfNeeded(SIGNED);
        if (needEditableForNotification) {
            sendAfterTextChanged((Editable) text);
        }
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
    }

    public final void setText(char[] text, int start, int len) {
        int oldlen = MARQUEE_FADE_NORMAL;
        if (start < 0 || len < 0 || start + len > text.length) {
            throw new IndexOutOfBoundsException(start + ", " + len);
        }
        if (this.mText != null) {
            oldlen = this.mText.length();
            sendBeforeTextChanged(this.mText, MARQUEE_FADE_NORMAL, oldlen, len);
        } else {
            sendBeforeTextChanged(ProxyInfo.LOCAL_EXCL_LIST, MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, len);
        }
        if (this.mCharWrapper == null) {
            this.mCharWrapper = new CharWrapper(text, start, len);
        } else {
            this.mCharWrapper.set(text, start, len);
        }
        setText(this.mCharWrapper, this.mBufferType, DEBUG_EXTRACT, oldlen);
    }

    public final void setTextKeepState(CharSequence text, BufferType type) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        int len = text.length();
        setText(text, type);
        if ((start >= 0 || end >= 0) && (this.mText instanceof Spannable)) {
            Selection.setSelection((Spannable) this.mText, Math.max(MARQUEE_FADE_NORMAL, Math.min(start, len)), Math.max(MARQUEE_FADE_NORMAL, Math.min(end, len)));
        }
    }

    @RemotableViewMethod
    public final void setText(int resid) {
        setText(getContext().getResources().getText(resid));
    }

    public final void setText(int resid, BufferType type) {
        setText(getContext().getResources().getText(resid), type);
    }

    @RemotableViewMethod
    public final void setHint(CharSequence hint) {
        this.mHint = TextUtils.stringOrSpannedString(hint);
        if (this.mLayout != null) {
            checkForRelayout();
        }
        if (this.mText.length() == 0) {
            invalidate();
        }
        if (this.mEditor != null && this.mText.length() == 0 && this.mHint != null) {
            this.mEditor.invalidateTextDisplayList();
        }
    }

    @RemotableViewMethod
    public final void setHint(int resid) {
        setHint(getContext().getResources().getText(resid));
    }

    @CapturedViewProperty
    public CharSequence getHint() {
        return this.mHint;
    }

    boolean isSingleLine() {
        return this.mSingleLine;
    }

    private static boolean isMultilineInputType(int type) {
        return (131087 & type) == 131073 ? true : DEBUG_EXTRACT;
    }

    CharSequence removeSuggestionSpans(CharSequence text) {
        if (text instanceof Spanned) {
            Spannable spannable;
            if (text instanceof Spannable) {
                spannable = (Spannable) text;
            } else {
                spannable = new SpannableString(text);
                Object text2 = spannable;
            }
            SuggestionSpan[] spans = (SuggestionSpan[]) spannable.getSpans(MARQUEE_FADE_NORMAL, text.length(), SuggestionSpan.class);
            for (int i = MARQUEE_FADE_NORMAL; i < spans.length; i += SANS) {
                spannable.removeSpan(spans[i]);
            }
        }
        return text;
    }

    public void setInputType(int type) {
        boolean singleLine;
        boolean z = DEBUG_EXTRACT;
        boolean wasPassword = isPasswordInputType(getInputType());
        boolean wasVisiblePassword = isVisiblePasswordInputType(getInputType());
        setInputType(type, DEBUG_EXTRACT);
        boolean isPassword = isPasswordInputType(type);
        boolean isVisiblePassword = isVisiblePasswordInputType(type);
        boolean forceUpdate = DEBUG_EXTRACT;
        if (isPassword) {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            setTypefaceFromAttrs(null, MONOSPACE, MARQUEE_FADE_NORMAL);
        } else if (isVisiblePassword) {
            if (this.mTransformation == PasswordTransformationMethod.getInstance()) {
                forceUpdate = true;
            }
            setTypefaceFromAttrs(null, MONOSPACE, MARQUEE_FADE_NORMAL);
        } else if (wasPassword || wasVisiblePassword) {
            setTypefaceFromAttrs(null, -1, -1);
            if (this.mTransformation == PasswordTransformationMethod.getInstance()) {
                forceUpdate = true;
            }
        }
        if (isMultilineInputType(type)) {
            singleLine = DEBUG_EXTRACT;
        } else {
            singleLine = true;
        }
        if (this.mSingleLine != singleLine || forceUpdate) {
            if (!isPassword) {
                z = true;
            }
            applySingleLine(singleLine, z, true);
        }
        if (!isSuggestionsEnabled()) {
            this.mText = removeSuggestionSpans(this.mText);
        }
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (imm != null) {
            imm.restartInput(this);
        }
    }

    private boolean hasPasswordTransformationMethod() {
        return this.mTransformation instanceof PasswordTransformationMethod;
    }

    private static boolean isPasswordInputType(int inputType) {
        int variation = inputType & FileObserver.ALL_EVENTS;
        return (variation == KeyEvent.KEYCODE_MEDIA_EJECT || variation == KeyEvent.KEYCODE_PAIRING || variation == 18) ? true : DEBUG_EXTRACT;
    }

    private static boolean isVisiblePasswordInputType(int inputType) {
        return (inputType & FileObserver.ALL_EVENTS) == KeyEvent.KEYCODE_NUMPAD_1 ? true : DEBUG_EXTRACT;
    }

    public void setRawInputType(int type) {
        if (type != 0 || this.mEditor != null) {
            createEditorIfNeeded();
            this.mEditor.mInputType = type;
        }
    }

    private void setInputType(int type, boolean direct) {
        KeyListener input;
        boolean z = true;
        int cls = type & 15;
        if (cls == SANS) {
            boolean autotext;
            Capitalize cap;
            if ((AccessibilityNodeInfo.ACTION_PASTE & type) != 0) {
                autotext = true;
            } else {
                autotext = DEBUG_EXTRACT;
            }
            if ((type & AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) != 0) {
                cap = Capitalize.CHARACTERS;
            } else if ((type & AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) != 0) {
                cap = Capitalize.WORDS;
            } else if ((type & AccessibilityNodeInfo.ACTION_COPY) != 0) {
                cap = Capitalize.SENTENCES;
            } else {
                cap = Capitalize.NONE;
            }
            input = TextKeyListener.getInstance(autotext, cap);
        } else if (cls == SIGNED) {
            boolean z2 = (type & AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) != 0 ? true : DEBUG_EXTRACT;
            if ((type & AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) == 0) {
                z = DEBUG_EXTRACT;
            }
            input = DigitsKeyListener.getInstance(z2, z);
        } else if (cls == DECIMAL) {
            switch (type & 4080) {
                case RelativeLayout.START_OF /*16*/:
                    input = DateKeyListener.getInstance();
                    break;
                case AccessibilityNodeInfo.ACTION_LONG_CLICK /*32*/:
                    input = TimeKeyListener.getInstance();
                    break;
                default:
                    input = DateTimeKeyListener.getInstance();
                    break;
            }
        } else if (cls == MONOSPACE) {
            input = DialerKeyListener.getInstance();
        } else {
            input = TextKeyListener.getInstance();
        }
        setRawInputType(type);
        if (direct) {
            createEditorIfNeeded();
            this.mEditor.mKeyListener = input;
            return;
        }
        setKeyListenerOnly(input);
    }

    public int getInputType() {
        return this.mEditor == null ? MARQUEE_FADE_NORMAL : this.mEditor.mInputType;
    }

    public void setImeOptions(int imeOptions) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeOptions = imeOptions;
    }

    public int getImeOptions() {
        return (this.mEditor == null || this.mEditor.mInputContentType == null) ? MARQUEE_FADE_NORMAL : this.mEditor.mInputContentType.imeOptions;
    }

    public void setImeActionLabel(CharSequence label, int actionId) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeActionLabel = label;
        this.mEditor.mInputContentType.imeActionId = actionId;
    }

    public CharSequence getImeActionLabel() {
        return (this.mEditor == null || this.mEditor.mInputContentType == null) ? null : this.mEditor.mInputContentType.imeActionLabel;
    }

    public int getImeActionId() {
        return (this.mEditor == null || this.mEditor.mInputContentType == null) ? MARQUEE_FADE_NORMAL : this.mEditor.mInputContentType.imeActionId;
    }

    public void setOnEditorActionListener(OnEditorActionListener l) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.onEditorActionListener = l;
    }

    public void onEditorAction(int actionCode) {
        InputContentType ict;
        if (this.mEditor == null) {
            ict = null;
        } else {
            ict = this.mEditor.mInputContentType;
        }
        if (ict != null) {
            if (ict.onEditorActionListener != null && ict.onEditorActionListener.onEditorAction(this, actionCode, null)) {
                return;
            }
            View v;
            if (actionCode == 5) {
                v = focusSearch(SIGNED);
                if (v != null && !v.requestFocus(SIGNED)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            } else if (actionCode == 7) {
                v = focusSearch(SANS);
                if (v != null && !v.requestFocus(SANS)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            } else if (actionCode == 6) {
                InputMethodManager imm = InputMethodManager.peekInstance();
                if (imm != null && imm.isActive(this)) {
                    imm.hideSoftInputFromWindow(getWindowToken(), MARQUEE_FADE_NORMAL);
                    return;
                }
                return;
            }
        }
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            long eventTime = SystemClock.uptimeMillis();
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(eventTime, eventTime, MARQUEE_FADE_NORMAL, 66, MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, -1, MARQUEE_FADE_NORMAL, 22));
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(SystemClock.uptimeMillis(), eventTime, SANS, 66, MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, -1, MARQUEE_FADE_NORMAL, 22));
        }
    }

    public void setPrivateImeOptions(String type) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.privateImeOptions = type;
    }

    public String getPrivateImeOptions() {
        return (this.mEditor == null || this.mEditor.mInputContentType == null) ? null : this.mEditor.mInputContentType.privateImeOptions;
    }

    public void setInputExtras(int xmlResId) throws XmlPullParserException, IOException {
        createEditorIfNeeded();
        XmlResourceParser parser = getResources().getXml(xmlResId);
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.extras = new Bundle();
        getResources().parseBundleExtras(parser, this.mEditor.mInputContentType.extras);
    }

    public Bundle getInputExtras(boolean create) {
        if (this.mEditor == null && !create) {
            return null;
        }
        createEditorIfNeeded();
        if (this.mEditor.mInputContentType == null) {
            if (!create) {
                return null;
            }
            this.mEditor.createInputContentTypeIfNeeded();
        }
        if (this.mEditor.mInputContentType.extras == null) {
            if (!create) {
                return null;
            }
            this.mEditor.mInputContentType.extras = new Bundle();
        }
        return this.mEditor.mInputContentType.extras;
    }

    public CharSequence getError() {
        return this.mEditor == null ? null : this.mEditor.mError;
    }

    @RemotableViewMethod
    public void setError(CharSequence error) {
        if (error == null) {
            setError(null, null);
            return;
        }
        Drawable dr = getContext().getDrawable(17302602);
        dr.setBounds(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
        setError(error, dr);
    }

    public void setError(CharSequence error, Drawable icon) {
        createEditorIfNeeded();
        this.mEditor.setError(error, icon);
        notifyViewAccessibilityStateChangedIfNeeded(MARQUEE_FADE_NORMAL);
    }

    protected boolean setFrame(int l, int t, int r, int b) {
        boolean result = super.setFrame(l, t, r, b);
        if (this.mEditor != null) {
            this.mEditor.setFrame();
        }
        restartMarqueeIfNeeded();
        return result;
    }

    private void restartMarqueeIfNeeded() {
        if (this.mRestartMarquee && this.mEllipsize == TruncateAt.MARQUEE) {
            this.mRestartMarquee = DEBUG_EXTRACT;
            startMarquee();
        }
    }

    public void setFilters(InputFilter[] filters) {
        if (filters == null) {
            throw new IllegalArgumentException();
        }
        this.mFilters = filters;
        if (this.mText instanceof Editable) {
            setFilters((Editable) this.mText, filters);
        }
    }

    private void setFilters(Editable e, InputFilter[] filters) {
        if (this.mEditor != null) {
            boolean undoFilter = this.mEditor.mUndoInputFilter != null ? true : DEBUG_EXTRACT;
            boolean keyFilter = this.mEditor.mKeyListener instanceof InputFilter;
            int num = MARQUEE_FADE_NORMAL;
            if (undoFilter) {
                num = MARQUEE_FADE_NORMAL + SANS;
            }
            if (keyFilter) {
                num += SANS;
            }
            if (num > 0) {
                InputFilter[] nf = new InputFilter[(filters.length + num)];
                System.arraycopy(filters, MARQUEE_FADE_NORMAL, nf, MARQUEE_FADE_NORMAL, filters.length);
                num = MARQUEE_FADE_NORMAL;
                if (undoFilter) {
                    nf[filters.length] = this.mEditor.mUndoInputFilter;
                    num = MARQUEE_FADE_NORMAL + SANS;
                }
                if (keyFilter) {
                    nf[filters.length + num] = (InputFilter) this.mEditor.mKeyListener;
                }
                e.setFilters(nf);
                return;
            }
        }
        e.setFilters(filters);
    }

    public InputFilter[] getFilters() {
        return this.mFilters;
    }

    private int getBoxHeight(Layout l) {
        Insets opticalInsets = View.isLayoutModeOptical(this.mParent) ? getOpticalInsets() : Insets.NONE;
        return ((getMeasuredHeight() - (l == this.mHintLayout ? getCompoundPaddingTop() + getCompoundPaddingBottom() : getExtendedPaddingTop() + getExtendedPaddingBottom())) + opticalInsets.top) + opticalInsets.bottom;
    }

    int getVerticalOffset(boolean forceNormal) {
        int gravity = this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL;
        Layout l = this.mLayout;
        if (!(forceNormal || this.mText.length() != 0 || this.mHintLayout == null)) {
            l = this.mHintLayout;
        }
        if (gravity == 48) {
            return MARQUEE_FADE_NORMAL;
        }
        int boxht = getBoxHeight(l);
        int textht = l.getHeight();
        if (textht >= boxht) {
            return MARQUEE_FADE_NORMAL;
        }
        if (gravity == 80) {
            return boxht - textht;
        }
        return (boxht - textht) >> SANS;
    }

    private int getBottomVerticalOffset(boolean forceNormal) {
        int gravity = this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL;
        Layout l = this.mLayout;
        if (!(forceNormal || this.mText.length() != 0 || this.mHintLayout == null)) {
            l = this.mHintLayout;
        }
        if (gravity == 80) {
            return MARQUEE_FADE_NORMAL;
        }
        int boxht = getBoxHeight(l);
        int textht = l.getHeight();
        if (textht >= boxht) {
            return MARQUEE_FADE_NORMAL;
        }
        if (gravity == 48) {
            return boxht - textht;
        }
        return (boxht - textht) >> SANS;
    }

    void invalidateCursorPath() {
        if (this.mHighlightPathBogus) {
            invalidateCursor();
            return;
        }
        int horizontalPadding = getCompoundPaddingLeft();
        int verticalPadding = getExtendedPaddingTop() + getVerticalOffset(true);
        if (this.mEditor.mCursorCount == 0) {
            synchronized (TEMP_RECTF) {
                float thick = FloatMath.ceil(this.mTextPaint.getStrokeWidth());
                if (thick < LayoutParams.BRIGHTNESS_OVERRIDE_FULL) {
                    thick = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                }
                thick /= 2.0f;
                this.mHighlightPath.computeBounds(TEMP_RECTF, DEBUG_EXTRACT);
                invalidate((int) FloatMath.floor((((float) horizontalPadding) + TEMP_RECTF.left) - thick), (int) FloatMath.floor((((float) verticalPadding) + TEMP_RECTF.top) - thick), (int) FloatMath.ceil((((float) horizontalPadding) + TEMP_RECTF.right) + thick), (int) FloatMath.ceil((((float) verticalPadding) + TEMP_RECTF.bottom) + thick));
            }
            return;
        }
        for (int i = MARQUEE_FADE_NORMAL; i < this.mEditor.mCursorCount; i += SANS) {
            Rect bounds = this.mEditor.mCursorDrawable[i].getBounds();
            invalidate(bounds.left + horizontalPadding, bounds.top + verticalPadding, bounds.right + horizontalPadding, bounds.bottom + verticalPadding);
        }
    }

    void invalidateCursor() {
        int where = getSelectionEnd();
        invalidateCursor(where, where, where);
    }

    private void invalidateCursor(int a, int b, int c) {
        if (a >= 0 || b >= 0 || c >= 0) {
            invalidateRegion(Math.min(Math.min(a, b), c), Math.max(Math.max(a, b), c), true);
        }
    }

    void invalidateRegion(int start, int end, boolean invalidateCursor) {
        if (this.mLayout == null) {
            invalidate();
            return;
        }
        int lineEnd;
        int left;
        int right;
        int lineStart = this.mLayout.getLineForOffset(start);
        int top = this.mLayout.getLineTop(lineStart);
        if (lineStart > 0) {
            top -= this.mLayout.getLineDescent(lineStart - 1);
        }
        if (start == end) {
            lineEnd = lineStart;
        } else {
            lineEnd = this.mLayout.getLineForOffset(end);
        }
        int bottom = this.mLayout.getLineBottom(lineEnd);
        if (invalidateCursor && this.mEditor != null) {
            for (int i = MARQUEE_FADE_NORMAL; i < this.mEditor.mCursorCount; i += SANS) {
                Rect bounds = this.mEditor.mCursorDrawable[i].getBounds();
                top = Math.min(top, bounds.top);
                bottom = Math.max(bottom, bounds.bottom);
            }
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int verticalPadding = getExtendedPaddingTop() + getVerticalOffset(true);
        if (lineStart != lineEnd || invalidateCursor) {
            left = compoundPaddingLeft;
            right = getWidth() - getCompoundPaddingRight();
        } else {
            left = ((int) this.mLayout.getPrimaryHorizontal(start)) + compoundPaddingLeft;
            right = ((int) (((double) this.mLayout.getPrimaryHorizontal(end)) + 1.0d)) + compoundPaddingLeft;
        }
        invalidate(this.mScrollX + left, verticalPadding + top, this.mScrollX + right, verticalPadding + bottom);
    }

    private void registerForPreDraw() {
        if (!this.mPreDrawRegistered) {
            getViewTreeObserver().addOnPreDrawListener(this);
            this.mPreDrawRegistered = true;
        }
    }

    private void unregisterForPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        this.mPreDrawRegistered = DEBUG_EXTRACT;
        this.mPreDrawListenerDetached = DEBUG_EXTRACT;
    }

    public boolean onPreDraw() {
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mMovement != null) {
            int curs = getSelectionEnd();
            if (!(this.mEditor == null || this.mEditor.mSelectionModifierCursorController == null || !this.mEditor.mSelectionModifierCursorController.isSelectionStartDragged())) {
                curs = getSelectionStart();
            }
            if (curs < 0 && (this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) == 80) {
                curs = this.mText.length();
            }
            if (curs >= 0) {
                bringPointIntoView(curs);
            }
        } else {
            bringTextIntoView();
        }
        if (this.mEditor != null && this.mEditor.mCreatedWithASelection) {
            this.mEditor.startSelectionActionMode();
            this.mEditor.mCreatedWithASelection = DEBUG_EXTRACT;
        }
        if ((this instanceof ExtractEditText) && hasSelection() && this.mEditor != null) {
            this.mEditor.startSelectionActionMode();
        }
        unregisterForPreDraw();
        return true;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mTemporaryDetach = DEBUG_EXTRACT;
        if (this.mEditor != null) {
            this.mEditor.onAttachedToWindow();
        }
        if (this.mPreDrawListenerDetached) {
            getViewTreeObserver().addOnPreDrawListener(this);
            this.mPreDrawListenerDetached = DEBUG_EXTRACT;
        }
    }

    protected void onDetachedFromWindowInternal() {
        if (this.mPreDrawRegistered) {
            getViewTreeObserver().removeOnPreDrawListener(this);
            this.mPreDrawListenerDetached = true;
        }
        resetResolvedDrawables();
        if (this.mEditor != null) {
            this.mEditor.onDetachedFromWindow();
        }
        super.onDetachedFromWindowInternal();
    }

    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        if (this.mEditor != null) {
            this.mEditor.onScreenStateChanged(screenState);
        }
    }

    protected boolean isPaddingOffsetRequired() {
        return (this.mShadowRadius == 0.0f && this.mDrawables == null) ? DEBUG_EXTRACT : true;
    }

    protected int getLeftPaddingOffset() {
        return (getCompoundPaddingLeft() - this.mPaddingLeft) + ((int) Math.min(0.0f, this.mShadowDx - this.mShadowRadius));
    }

    protected int getTopPaddingOffset() {
        return (int) Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
    }

    protected int getBottomPaddingOffset() {
        return (int) Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
    }

    private int getFudgedPaddingRight() {
        return Math.max(MARQUEE_FADE_NORMAL, getCompoundPaddingRight() - ((((int) this.mTextPaint.density) + SIGNED) - 1));
    }

    protected int getRightPaddingOffset() {
        return (-(getFudgedPaddingRight() - this.mPaddingRight)) + ((int) Math.max(0.0f, this.mShadowDx + this.mShadowRadius));
    }

    protected boolean verifyDrawable(Drawable who) {
        boolean verified = super.verifyDrawable(who);
        if (verified || this.mDrawables == null) {
            return verified;
        }
        return (who == this.mDrawables.mDrawableLeft || who == this.mDrawables.mDrawableTop || who == this.mDrawables.mDrawableRight || who == this.mDrawables.mDrawableBottom || who == this.mDrawables.mDrawableStart || who == this.mDrawables.mDrawableEnd) ? true : DEBUG_EXTRACT;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mDrawables != null) {
            if (this.mDrawables.mDrawableLeft != null) {
                this.mDrawables.mDrawableLeft.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableTop != null) {
                this.mDrawables.mDrawableTop.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableRight != null) {
                this.mDrawables.mDrawableRight.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableBottom != null) {
                this.mDrawables.mDrawableBottom.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableStart != null) {
                this.mDrawables.mDrawableStart.jumpToCurrentState();
            }
            if (this.mDrawables.mDrawableEnd != null) {
                this.mDrawables.mDrawableEnd.jumpToCurrentState();
            }
        }
    }

    public void invalidateDrawable(Drawable drawable) {
        boolean handled = DEBUG_EXTRACT;
        if (verifyDrawable(drawable)) {
            Rect dirty = drawable.getBounds();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            Drawables drawables = this.mDrawables;
            if (drawables != null) {
                int compoundPaddingTop;
                if (drawable == drawables.mDrawableLeft) {
                    compoundPaddingTop = getCompoundPaddingTop();
                    scrollX += this.mPaddingLeft;
                    scrollY += (((((this.mBottom - this.mTop) - getCompoundPaddingBottom()) - compoundPaddingTop) - drawables.mDrawableHeightLeft) / SIGNED) + compoundPaddingTop;
                    handled = true;
                } else if (drawable == drawables.mDrawableRight) {
                    compoundPaddingTop = getCompoundPaddingTop();
                    scrollX += ((this.mRight - this.mLeft) - this.mPaddingRight) - drawables.mDrawableSizeRight;
                    scrollY += (((((this.mBottom - this.mTop) - getCompoundPaddingBottom()) - compoundPaddingTop) - drawables.mDrawableHeightRight) / SIGNED) + compoundPaddingTop;
                    handled = true;
                } else if (drawable == drawables.mDrawableTop) {
                    compoundPaddingLeft = getCompoundPaddingLeft();
                    scrollX += (((((this.mRight - this.mLeft) - getCompoundPaddingRight()) - compoundPaddingLeft) - drawables.mDrawableWidthTop) / SIGNED) + compoundPaddingLeft;
                    scrollY += this.mPaddingTop;
                    handled = true;
                } else if (drawable == drawables.mDrawableBottom) {
                    compoundPaddingLeft = getCompoundPaddingLeft();
                    scrollX += (((((this.mRight - this.mLeft) - getCompoundPaddingRight()) - compoundPaddingLeft) - drawables.mDrawableWidthBottom) / SIGNED) + compoundPaddingLeft;
                    scrollY += ((this.mBottom - this.mTop) - this.mPaddingBottom) - drawables.mDrawableSizeBottom;
                    handled = true;
                }
            }
            if (handled) {
                invalidate(dirty.left + scrollX, dirty.top + scrollY, dirty.right + scrollX, dirty.bottom + scrollY);
            }
        }
        if (!handled) {
            super.invalidateDrawable(drawable);
        }
    }

    public boolean hasOverlappingRendering() {
        return ((getBackground() != null && getBackground().getCurrent() != null) || (this.mText instanceof Spannable) || hasSelection() || isHorizontalFadingEdgeEnabled()) ? true : DEBUG_EXTRACT;
    }

    public boolean isTextSelectable() {
        return this.mEditor == null ? DEBUG_EXTRACT : this.mEditor.mTextIsSelectable;
    }

    public void setTextIsSelectable(boolean selectable) {
        if (selectable || this.mEditor != null) {
            createEditorIfNeeded();
            if (this.mEditor.mTextIsSelectable != selectable) {
                this.mEditor.mTextIsSelectable = selectable;
                setFocusableInTouchMode(selectable);
                setFocusable(selectable);
                setClickable(selectable);
                setLongClickable(selectable);
                setMovementMethod(selectable ? ArrowKeyMovementMethod.getInstance() : null);
                setText(this.mText, selectable ? BufferType.SPANNABLE : BufferType.NORMAL);
                this.mEditor.prepareCursorControllers();
            }
        }
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState;
        if (this.mSingleLine) {
            drawableState = super.onCreateDrawableState(extraSpace);
        } else {
            drawableState = super.onCreateDrawableState(extraSpace + SANS);
            View.mergeDrawableStates(drawableState, MULTILINE_STATE_SET);
        }
        if (isTextSelectable()) {
            int length = drawableState.length;
            for (int i = MARQUEE_FADE_NORMAL; i < length; i += SANS) {
                if (drawableState[i] == 16842919) {
                    int[] nonPressedState = new int[(length - 1)];
                    System.arraycopy(drawableState, MARQUEE_FADE_NORMAL, nonPressedState, MARQUEE_FADE_NORMAL, i);
                    System.arraycopy(drawableState, i + SANS, nonPressedState, i, (length - i) - 1);
                    return nonPressedState;
                }
            }
        }
        return drawableState;
    }

    private Path getUpdatedHighlightPath() {
        Paint highlightPaint = this.mHighlightPaint;
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();
        if (this.mMovement == null) {
            return null;
        }
        if ((!isFocused() && !isPressed()) || selStart < 0) {
            return null;
        }
        if (selStart != selEnd) {
            if (this.mHighlightPathBogus) {
                if (this.mHighlightPath == null) {
                    this.mHighlightPath = new Path();
                }
                this.mHighlightPath.reset();
                this.mLayout.getSelectionPath(selStart, selEnd, this.mHighlightPath);
                this.mHighlightPathBogus = DEBUG_EXTRACT;
            }
            highlightPaint.setColor(this.mHighlightColor);
            highlightPaint.setStyle(Style.FILL);
            return this.mHighlightPath;
        } else if (this.mEditor == null || !this.mEditor.isCursorVisible() || (SystemClock.uptimeMillis() - this.mEditor.mShowCursor) % 1000 >= 500) {
            return null;
        } else {
            if (this.mHighlightPathBogus) {
                if (this.mHighlightPath == null) {
                    this.mHighlightPath = new Path();
                }
                this.mHighlightPath.reset();
                this.mLayout.getCursorPath(selStart, this.mHighlightPath, this.mText);
                this.mEditor.updateCursorsPositions();
                this.mHighlightPathBogus = DEBUG_EXTRACT;
            }
            highlightPaint.setColor(this.mCurTextColor);
            highlightPaint.setStyle(Style.STROKE);
            return this.mHighlightPath;
        }
    }

    public int getHorizontalOffsetForDrawables() {
        return MARQUEE_FADE_NORMAL;
    }

    protected void onDraw(Canvas canvas) {
        int leftOffset;
        float clipTop;
        restartMarqueeIfNeeded();
        super.onDraw(canvas);
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int compoundPaddingTop = getCompoundPaddingTop();
        int compoundPaddingRight = getCompoundPaddingRight();
        int compoundPaddingBottom = getCompoundPaddingBottom();
        int scrollX = this.mScrollX;
        int scrollY = this.mScrollY;
        int right = this.mRight;
        int left = this.mLeft;
        int bottom = this.mBottom;
        int top = this.mTop;
        boolean isLayoutRtl = isLayoutRtl();
        int offset = getHorizontalOffsetForDrawables();
        if (isLayoutRtl) {
            leftOffset = MARQUEE_FADE_NORMAL;
        } else {
            leftOffset = offset;
        }
        int rightOffset = isLayoutRtl ? offset : MARQUEE_FADE_NORMAL;
        Drawables dr = this.mDrawables;
        if (dr != null) {
            int vspace = ((bottom - top) - compoundPaddingBottom) - compoundPaddingTop;
            int hspace = ((right - left) - compoundPaddingRight) - compoundPaddingLeft;
            if (dr.mDrawableLeft != null) {
                canvas.save();
                canvas.translate((float) ((this.mPaddingLeft + scrollX) + leftOffset), (float) ((scrollY + compoundPaddingTop) + ((vspace - dr.mDrawableHeightLeft) / SIGNED)));
                dr.mDrawableLeft.draw(canvas);
                canvas.restore();
            }
            if (dr.mDrawableRight != null) {
                canvas.save();
                canvas.translate((float) (((((scrollX + right) - left) - this.mPaddingRight) - dr.mDrawableSizeRight) - rightOffset), (float) ((scrollY + compoundPaddingTop) + ((vspace - dr.mDrawableHeightRight) / SIGNED)));
                dr.mDrawableRight.draw(canvas);
                canvas.restore();
            }
            if (dr.mDrawableTop != null) {
                canvas.save();
                canvas.translate((float) ((scrollX + compoundPaddingLeft) + ((hspace - dr.mDrawableWidthTop) / SIGNED)), (float) (this.mPaddingTop + scrollY));
                dr.mDrawableTop.draw(canvas);
                canvas.restore();
            }
            if (dr.mDrawableBottom != null) {
                canvas.save();
                canvas.translate((float) ((scrollX + compoundPaddingLeft) + ((hspace - dr.mDrawableWidthBottom) / SIGNED)), (float) ((((scrollY + bottom) - top) - this.mPaddingBottom) - dr.mDrawableSizeBottom));
                dr.mDrawableBottom.draw(canvas);
                canvas.restore();
            }
        }
        int color = this.mCurTextColor;
        if (this.mLayout == null) {
            assumeLayout();
        }
        Layout layout = this.mLayout;
        if (this.mHint != null && this.mText.length() == 0) {
            if (this.mHintTextColor != null) {
                color = this.mCurHintTextColor;
            }
            layout = this.mHintLayout;
        }
        this.mTextPaint.setColor(color);
        this.mTextPaint.drawableState = getDrawableState();
        canvas.save();
        int extendedPaddingTop = getExtendedPaddingTop();
        int extendedPaddingBottom = getExtendedPaddingBottom();
        int maxScrollY = this.mLayout.getHeight() - (((this.mBottom - this.mTop) - compoundPaddingBottom) - compoundPaddingTop);
        float clipLeft = (float) (compoundPaddingLeft + scrollX);
        if (scrollY == 0) {
            clipTop = 0.0f;
        } else {
            clipTop = (float) (extendedPaddingTop + scrollY);
        }
        float clipRight = (float) (((right - left) - getFudgedPaddingRight()) + scrollX);
        int i = (bottom - top) + scrollY;
        if (scrollY == maxScrollY) {
            extendedPaddingBottom = MARQUEE_FADE_NORMAL;
        }
        float clipBottom = (float) (i - extendedPaddingBottom);
        if (this.mShadowRadius != 0.0f) {
            clipLeft += Math.min(0.0f, this.mShadowDx - this.mShadowRadius);
            clipRight += Math.max(0.0f, this.mShadowDx + this.mShadowRadius);
            clipTop += Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
            clipBottom += Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
        }
        canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom);
        int voffsetText = MARQUEE_FADE_NORMAL;
        int voffsetCursor = MARQUEE_FADE_NORMAL;
        if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) != 48) {
            voffsetText = getVerticalOffset(DEBUG_EXTRACT);
            voffsetCursor = getVerticalOffset(true);
        }
        canvas.translate((float) compoundPaddingLeft, (float) (extendedPaddingTop + voffsetText));
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection());
        if (this.mEllipsize == TruncateAt.MARQUEE && this.mMarqueeFadeMode != SANS) {
            if (!this.mSingleLine && getLineCount() == SANS && canMarquee() && (absoluteGravity & 7) != MONOSPACE) {
                canvas.translate(((float) layout.getParagraphDirection(MARQUEE_FADE_NORMAL)) * (this.mLayout.getLineRight(MARQUEE_FADE_NORMAL) - ((float) ((this.mRight - this.mLeft) - (getCompoundPaddingLeft() + getCompoundPaddingRight())))), 0.0f);
            }
            if (this.mMarquee != null && this.mMarquee.isRunning()) {
                canvas.translate(((float) layout.getParagraphDirection(MARQUEE_FADE_NORMAL)) * (-this.mMarquee.getScroll()), 0.0f);
            }
        }
        int cursorOffsetVertical = voffsetCursor - voffsetText;
        Path highlight = getUpdatedHighlightPath();
        if (this.mEditor != null) {
            this.mEditor.onDraw(canvas, layout, highlight, this.mHighlightPaint, cursorOffsetVertical);
        } else {
            layout.draw(canvas, highlight, this.mHighlightPaint, cursorOffsetVertical);
        }
        if (this.mMarquee != null && this.mMarquee.shouldDrawGhost()) {
            canvas.translate(((float) layout.getParagraphDirection(MARQUEE_FADE_NORMAL)) * this.mMarquee.getGhostOffset(), 0.0f);
            layout.draw(canvas, highlight, this.mHighlightPaint, cursorOffsetVertical);
        }
        canvas.restore();
    }

    public void getFocusedRect(Rect r) {
        if (this.mLayout == null) {
            super.getFocusedRect(r);
            return;
        }
        int selEnd = getSelectionEnd();
        if (selEnd < 0) {
            super.getFocusedRect(r);
            return;
        }
        int selStart = getSelectionStart();
        if (selStart < 0 || selStart >= selEnd) {
            int line = this.mLayout.getLineForOffset(selEnd);
            r.top = this.mLayout.getLineTop(line);
            r.bottom = this.mLayout.getLineBottom(line);
            r.left = ((int) this.mLayout.getPrimaryHorizontal(selEnd)) - 2;
            r.right = r.left + DECIMAL;
        } else {
            int lineStart = this.mLayout.getLineForOffset(selStart);
            int lineEnd = this.mLayout.getLineForOffset(selEnd);
            r.top = this.mLayout.getLineTop(lineStart);
            r.bottom = this.mLayout.getLineBottom(lineEnd);
            if (lineStart == lineEnd) {
                r.left = (int) this.mLayout.getPrimaryHorizontal(selStart);
                r.right = (int) this.mLayout.getPrimaryHorizontal(selEnd);
            } else {
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getSelectionPath(selStart, selEnd, this.mHighlightPath);
                    this.mHighlightPathBogus = DEBUG_EXTRACT;
                }
                synchronized (TEMP_RECTF) {
                    this.mHighlightPath.computeBounds(TEMP_RECTF, true);
                    r.left = ((int) TEMP_RECTF.left) - 1;
                    r.right = ((int) TEMP_RECTF.right) + SANS;
                }
            }
        }
        int paddingLeft = getCompoundPaddingLeft();
        int paddingTop = getExtendedPaddingTop();
        if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) != 48) {
            paddingTop += getVerticalOffset(DEBUG_EXTRACT);
        }
        r.offset(paddingLeft, paddingTop);
        r.bottom += getExtendedPaddingBottom();
    }

    public int getLineCount() {
        return this.mLayout != null ? this.mLayout.getLineCount() : MARQUEE_FADE_NORMAL;
    }

    public int getLineBounds(int line, Rect bounds) {
        if (this.mLayout != null) {
            int baseline = this.mLayout.getLineBounds(line, bounds);
            int voffset = getExtendedPaddingTop();
            if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) != 48) {
                voffset += getVerticalOffset(true);
            }
            if (bounds != null) {
                bounds.offset(getCompoundPaddingLeft(), voffset);
            }
            return baseline + voffset;
        } else if (bounds == null) {
            return MARQUEE_FADE_NORMAL;
        } else {
            bounds.set(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL);
            return MARQUEE_FADE_NORMAL;
        }
    }

    public int getBaseline() {
        if (this.mLayout == null) {
            return super.getBaseline();
        }
        int voffset = MARQUEE_FADE_NORMAL;
        if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) != 48) {
            voffset = getVerticalOffset(true);
        }
        if (View.isLayoutModeOptical(this.mParent)) {
            voffset -= getOpticalInsets().top;
        }
        return (getExtendedPaddingTop() + voffset) + this.mLayout.getLineBaseline(MARQUEE_FADE_NORMAL);
    }

    protected int getFadeTop(boolean offsetRequired) {
        if (this.mLayout == null) {
            return MARQUEE_FADE_NORMAL;
        }
        int voffset = MARQUEE_FADE_NORMAL;
        if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) != 48) {
            voffset = getVerticalOffset(true);
        }
        if (offsetRequired) {
            voffset += getTopPaddingOffset();
        }
        return getExtendedPaddingTop() + voffset;
    }

    protected int getFadeHeight(boolean offsetRequired) {
        return this.mLayout != null ? this.mLayout.getHeight() : MARQUEE_FADE_NORMAL;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == DECIMAL) {
            boolean isInSelectionMode = (this.mEditor == null || this.mEditor.mSelectionActionMode == null) ? DEBUG_EXTRACT : true;
            if (isInSelectionMode) {
                DispatcherState state;
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    state = getKeyDispatcherState();
                    if (state == null) {
                        return true;
                    }
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == SANS) {
                    state = getKeyDispatcherState();
                    if (state != null) {
                        state.handleUpEvent(event);
                    }
                    if (event.isTracking() && !event.isCanceled()) {
                        stopSelectionActionMode();
                        return true;
                    }
                }
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (doKeyDown(keyCode, event, null) == 0) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        KeyEvent down = KeyEvent.changeAction(event, MARQUEE_FADE_NORMAL);
        int which = doKeyDown(keyCode, down, event);
        if (which == 0) {
            return super.onKeyMultiple(keyCode, repeatCount, event);
        }
        if (which == -1) {
            return true;
        }
        repeatCount--;
        KeyEvent up = KeyEvent.changeAction(event, SANS);
        if (which != SANS) {
            if (which == SIGNED) {
                this.mMovement.onKeyUp(this, (Spannable) this.mText, keyCode, up);
                while (true) {
                    repeatCount--;
                    if (repeatCount <= 0) {
                        break;
                    }
                    this.mMovement.onKeyDown(this, (Spannable) this.mText, keyCode, down);
                    this.mMovement.onKeyUp(this, (Spannable) this.mText, keyCode, up);
                }
            }
        } else {
            this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, keyCode, up);
            while (true) {
                repeatCount--;
                if (repeatCount <= 0) {
                    break;
                }
                this.mEditor.mKeyListener.onKeyDown(this, (Editable) this.mText, keyCode, down);
                this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, keyCode, up);
            }
            hideErrorIfUnchanged();
        }
        return true;
    }

    private boolean shouldAdvanceFocusOnEnter() {
        if (getKeyListener() == null) {
            return DEBUG_EXTRACT;
        }
        if (this.mSingleLine) {
            return true;
        }
        if (this.mEditor == null || (this.mEditor.mInputType & 15) != SANS) {
            return DEBUG_EXTRACT;
        }
        int variation = this.mEditor.mInputType & 4080;
        if (variation == 32 || variation == 48) {
            return true;
        }
        return DEBUG_EXTRACT;
    }

    private boolean shouldAdvanceFocusOnTab() {
        if (getKeyListener() == null || this.mSingleLine || this.mEditor == null || (this.mEditor.mInputType & 15) != SANS) {
            return true;
        }
        int variation = this.mEditor.mInputType & 4080;
        if (variation == AccessibilityNodeInfo.ACTION_EXPAND || variation == AccessibilityNodeInfo.ACTION_SET_SELECTION) {
            return DEBUG_EXTRACT;
        }
        return true;
    }

    private int doKeyDown(int keyCode, KeyEvent event, KeyEvent otherEvent) {
        if (!isEnabled()) {
            return MARQUEE_FADE_NORMAL;
        }
        boolean doDown;
        boolean handled;
        if (event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyCode)) {
            this.mPreventDefaultMovement = DEBUG_EXTRACT;
        }
        switch (keyCode) {
            case DECIMAL /*4*/:
                if (!(this.mEditor == null || this.mEditor.mSelectionActionMode == null)) {
                    stopSelectionActionMode();
                    return -1;
                }
            case MotionEvent.AXIS_BRAKE /*23*/:
                if (event.hasNoModifiers() && shouldAdvanceFocusOnEnter()) {
                    return MARQUEE_FADE_NORMAL;
                }
            case KeyEvent.KEYCODE_TAB /*61*/:
                if ((event.hasNoModifiers() || event.hasModifiers(SANS)) && shouldAdvanceFocusOnTab()) {
                    return MARQUEE_FADE_NORMAL;
                }
            case KeyEvent.KEYCODE_ENTER /*66*/:
                if (event.hasNoModifiers()) {
                    if (this.mEditor != null && this.mEditor.mInputContentType != null && this.mEditor.mInputContentType.onEditorActionListener != null && this.mEditor.mInputContentType.onEditorActionListener.onEditorAction(this, MARQUEE_FADE_NORMAL, event)) {
                        this.mEditor.mInputContentType.enterDown = true;
                        return -1;
                    } else if ((event.getFlags() & 16) != 0 || shouldAdvanceFocusOnEnter()) {
                        if (hasOnClickListeners()) {
                            return MARQUEE_FADE_NORMAL;
                        }
                        return -1;
                    }
                }
                break;
        }
        if (!(this.mEditor == null || this.mEditor.mKeyListener == null)) {
            doDown = true;
            if (otherEvent != null) {
                try {
                    beginBatchEdit();
                    handled = this.mEditor.mKeyListener.onKeyOther(this, (Editable) this.mText, otherEvent);
                    hideErrorIfUnchanged();
                    doDown = DEBUG_EXTRACT;
                    if (handled) {
                        return -1;
                    }
                    endBatchEdit();
                } catch (AbstractMethodError e) {
                } finally {
                    endBatchEdit();
                }
            }
            if (doDown) {
                beginBatchEdit();
                handled = this.mEditor.mKeyListener.onKeyDown(this, (Editable) this.mText, keyCode, event);
                endBatchEdit();
                hideErrorIfUnchanged();
                if (handled) {
                    return SANS;
                }
            }
        }
        if (!(this.mMovement == null || this.mLayout == null)) {
            doDown = true;
            if (otherEvent != null) {
                try {
                    handled = this.mMovement.onKeyOther(this, (Spannable) this.mText, otherEvent);
                    doDown = DEBUG_EXTRACT;
                    if (handled) {
                        return -1;
                    }
                } catch (AbstractMethodError e2) {
                }
            }
            if (doDown && this.mMovement.onKeyDown(this, (Spannable) this.mText, keyCode, event)) {
                if (event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(keyCode)) {
                    this.mPreventDefaultMovement = true;
                }
                return SIGNED;
            }
        }
        int i = (!this.mPreventDefaultMovement || KeyEvent.isModifierKey(keyCode)) ? DEBUG_EXTRACT : -1;
        return i;
    }

    public void resetErrorChangedFlag() {
        if (this.mEditor != null) {
            this.mEditor.mErrorWasChanged = DEBUG_EXTRACT;
        }
    }

    public void hideErrorIfUnchanged() {
        if (this.mEditor != null && this.mEditor.mError != null && !this.mEditor.mErrorWasChanged) {
            setError(null, null);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!isEnabled()) {
            return super.onKeyUp(keyCode, event);
        }
        if (!KeyEvent.isModifierKey(keyCode)) {
            this.mPreventDefaultMovement = DEBUG_EXTRACT;
        }
        InputMethodManager imm;
        switch (keyCode) {
            case MotionEvent.AXIS_BRAKE /*23*/:
                if (event.hasNoModifiers() && !hasOnClickListeners() && this.mMovement != null && (this.mText instanceof Editable) && this.mLayout != null && onCheckIsTextEditor()) {
                    imm = InputMethodManager.peekInstance();
                    viewClicked(imm);
                    if (imm != null && getShowSoftInputOnFocus()) {
                        imm.showSoftInput(this, MARQUEE_FADE_NORMAL);
                    }
                }
                return super.onKeyUp(keyCode, event);
            case KeyEvent.KEYCODE_ENTER /*66*/:
                if (event.hasNoModifiers()) {
                    if (!(this.mEditor == null || this.mEditor.mInputContentType == null || this.mEditor.mInputContentType.onEditorActionListener == null || !this.mEditor.mInputContentType.enterDown)) {
                        this.mEditor.mInputContentType.enterDown = DEBUG_EXTRACT;
                        if (this.mEditor.mInputContentType.onEditorActionListener.onEditorAction(this, MARQUEE_FADE_NORMAL, event)) {
                            return true;
                        }
                    }
                    if (((event.getFlags() & 16) != 0 || shouldAdvanceFocusOnEnter()) && !hasOnClickListeners()) {
                        View v = focusSearch(KeyEvent.KEYCODE_MEDIA_RECORD);
                        if (v != null) {
                            if (v.requestFocus(KeyEvent.KEYCODE_MEDIA_RECORD)) {
                                super.onKeyUp(keyCode, event);
                                return true;
                            }
                            throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                        } else if ((event.getFlags() & 16) != 0) {
                            imm = InputMethodManager.peekInstance();
                            if (imm != null && imm.isActive(this)) {
                                imm.hideSoftInputFromWindow(getWindowToken(), MARQUEE_FADE_NORMAL);
                            }
                        }
                    }
                    return super.onKeyUp(keyCode, event);
                }
                break;
        }
        if (this.mEditor != null && this.mEditor.mKeyListener != null && this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, keyCode, event)) {
            return true;
        }
        if (this.mMovement == null || this.mLayout == null || !this.mMovement.onKeyUp(this, (Spannable) this.mText, keyCode, event)) {
            return super.onKeyUp(keyCode, event);
        }
        return true;
    }

    public boolean onCheckIsTextEditor() {
        return (this.mEditor == null || this.mEditor.mInputType == 0) ? DEBUG_EXTRACT : true;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (onCheckIsTextEditor() && isEnabled()) {
            this.mEditor.createInputMethodStateIfNeeded();
            outAttrs.inputType = getInputType();
            if (this.mEditor.mInputContentType != null) {
                outAttrs.imeOptions = this.mEditor.mInputContentType.imeOptions;
                outAttrs.privateImeOptions = this.mEditor.mInputContentType.privateImeOptions;
                outAttrs.actionLabel = this.mEditor.mInputContentType.imeActionLabel;
                outAttrs.actionId = this.mEditor.mInputContentType.imeActionId;
                outAttrs.extras = this.mEditor.mInputContentType.extras;
            } else {
                outAttrs.imeOptions = MARQUEE_FADE_NORMAL;
            }
            if (focusSearch(KeyEvent.KEYCODE_MEDIA_RECORD) != null) {
                outAttrs.imeOptions |= EditorInfo.IME_FLAG_NAVIGATE_NEXT;
            }
            if (focusSearch(33) != null) {
                outAttrs.imeOptions |= EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS;
            }
            if ((outAttrs.imeOptions & EditorInfo.IME_MASK_ACTION) == 0) {
                if ((outAttrs.imeOptions & EditorInfo.IME_FLAG_NAVIGATE_NEXT) != 0) {
                    outAttrs.imeOptions |= 5;
                } else {
                    outAttrs.imeOptions |= 6;
                }
                if (!shouldAdvanceFocusOnEnter()) {
                    outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_ENTER_ACTION;
                }
            }
            if (isMultilineInputType(outAttrs.inputType)) {
                outAttrs.imeOptions |= EditorInfo.IME_FLAG_NO_ENTER_ACTION;
            }
            outAttrs.hintText = this.mHint;
            if (this.mText instanceof Editable) {
                InputConnection ic = new EditableInputConnection(this);
                outAttrs.initialSelStart = getSelectionStart();
                outAttrs.initialSelEnd = getSelectionEnd();
                outAttrs.initialCapsMode = ic.getCursorCapsMode(getInputType());
                return ic;
            }
        }
        return null;
    }

    public boolean extractText(ExtractedTextRequest request, ExtractedText outText) {
        createEditorIfNeeded();
        return this.mEditor.extractText(request, outText);
    }

    static void removeParcelableSpans(Spannable spannable, int start, int end) {
        Object[] spans = spannable.getSpans(start, end, ParcelableSpan.class);
        int i = spans.length;
        while (i > 0) {
            i--;
            spannable.removeSpan(spans[i]);
        }
    }

    public void setExtractedText(ExtractedText text) {
        int N;
        int start;
        int end;
        Editable content = getEditableText();
        if (text.text != null) {
            if (content == null) {
                setText(text.text, BufferType.EDITABLE);
            } else if (text.partialStartOffset < 0) {
                removeParcelableSpans(content, MARQUEE_FADE_NORMAL, content.length());
                content.replace(MARQUEE_FADE_NORMAL, content.length(), text.text);
            } else {
                N = content.length();
                start = text.partialStartOffset;
                if (start > N) {
                    start = N;
                }
                end = text.partialEndOffset;
                if (end > N) {
                    end = N;
                }
                removeParcelableSpans(content, start, end);
                content.replace(start, end, text.text);
            }
        }
        Spannable sp = (Spannable) getText();
        N = sp.length();
        start = text.selectionStart;
        if (start < 0) {
            start = MARQUEE_FADE_NORMAL;
        } else if (start > N) {
            start = N;
        }
        end = text.selectionEnd;
        if (end < 0) {
            end = MARQUEE_FADE_NORMAL;
        } else if (end > N) {
            end = N;
        }
        Selection.setSelection(sp, start, end);
        if ((text.flags & SIGNED) != 0) {
            MetaKeyKeyListener.startSelecting(this, sp);
        } else {
            MetaKeyKeyListener.stopSelecting(this, sp);
        }
    }

    public void setExtracting(ExtractedTextRequest req) {
        if (this.mEditor.mInputMethodState != null) {
            this.mEditor.mInputMethodState.mExtractedTextRequest = req;
        }
        this.mEditor.hideControllers();
    }

    public void onCommitCompletion(CompletionInfo text) {
    }

    public void onCommitCorrection(CorrectionInfo info) {
        if (this.mEditor != null) {
            this.mEditor.onCommitCorrection(info);
        }
    }

    public void beginBatchEdit() {
        if (this.mEditor != null) {
            this.mEditor.beginBatchEdit();
        }
    }

    public void endBatchEdit() {
        if (this.mEditor != null) {
            this.mEditor.endBatchEdit();
        }
    }

    public void onBeginBatchEdit() {
    }

    public void onEndBatchEdit() {
    }

    public boolean onPrivateIMECommand(String action, Bundle data) {
        return DEBUG_EXTRACT;
    }

    private void nullLayouts() {
        if ((this.mLayout instanceof BoringLayout) && this.mSavedLayout == null) {
            this.mSavedLayout = (BoringLayout) this.mLayout;
        }
        if ((this.mHintLayout instanceof BoringLayout) && this.mSavedHintLayout == null) {
            this.mSavedHintLayout = (BoringLayout) this.mHintLayout;
        }
        this.mHintLayout = null;
        this.mLayout = null;
        this.mSavedMarqueeModeLayout = null;
        this.mHintBoring = null;
        this.mBoring = null;
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
    }

    private void assumeLayout() {
        int width = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (width < SANS) {
            width = MARQUEE_FADE_NORMAL;
        }
        int physicalWidth = width;
        if (this.mHorizontallyScrolling) {
            width = VERY_WIDE;
        }
        makeNewLayout(width, physicalWidth, UNKNOWN_BORING, UNKNOWN_BORING, physicalWidth, DEBUG_EXTRACT);
    }

    private Alignment getLayoutAlignment() {
        switch (getTextAlignment()) {
            case SANS /*1*/:
                switch (this.mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
                    case SANS /*1*/:
                        return Alignment.ALIGN_CENTER;
                    case MONOSPACE /*3*/:
                        return Alignment.ALIGN_LEFT;
                    case ReflectionActionWithoutParams.TAG /*5*/:
                        return Alignment.ALIGN_RIGHT;
                    case Gravity.START /*8388611*/:
                        return Alignment.ALIGN_NORMAL;
                    case Gravity.END /*8388613*/:
                        return Alignment.ALIGN_OPPOSITE;
                    default:
                        return Alignment.ALIGN_NORMAL;
                }
            case SIGNED /*2*/:
                return Alignment.ALIGN_NORMAL;
            case MONOSPACE /*3*/:
                return Alignment.ALIGN_OPPOSITE;
            case DECIMAL /*4*/:
                return Alignment.ALIGN_CENTER;
            case ReflectionActionWithoutParams.TAG /*5*/:
                return getLayoutDirection() == SANS ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_LEFT;
            case SetEmptyView.TAG /*6*/:
                return getLayoutDirection() == SANS ? Alignment.ALIGN_LEFT : Alignment.ALIGN_RIGHT;
            default:
                return Alignment.ALIGN_NORMAL;
        }
    }

    protected void makeNewLayout(int wantWidth, int hintWidth, Metrics boring, Metrics hintBoring, int ellipsisWidth, boolean bringIntoView) {
        stopMarquee();
        this.mOldMaximum = this.mMaximum;
        this.mOldMaxMode = this.mMaxMode;
        this.mHighlightPathBogus = true;
        if (wantWidth < 0) {
            wantWidth = MARQUEE_FADE_NORMAL;
        }
        if (hintWidth < 0) {
            hintWidth = MARQUEE_FADE_NORMAL;
        }
        Alignment alignment = getLayoutAlignment();
        boolean testDirChange = (this.mSingleLine && this.mLayout != null && (alignment == Alignment.ALIGN_NORMAL || alignment == Alignment.ALIGN_OPPOSITE)) ? true : DEBUG_EXTRACT;
        int oldDir = MARQUEE_FADE_NORMAL;
        if (testDirChange) {
            oldDir = this.mLayout.getParagraphDirection(MARQUEE_FADE_NORMAL);
        }
        boolean shouldEllipsize = (this.mEllipsize == null || getKeyListener() != null) ? DEBUG_EXTRACT : true;
        boolean switchEllipsize = (this.mEllipsize != TruncateAt.MARQUEE || this.mMarqueeFadeMode == 0) ? DEBUG_EXTRACT : true;
        TruncateAt effectiveEllipsize = this.mEllipsize;
        if (this.mEllipsize == TruncateAt.MARQUEE && this.mMarqueeFadeMode == SANS) {
            effectiveEllipsize = TruncateAt.END_SMALL;
        }
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        this.mLayout = makeSingleLayout(wantWidth, boring, ellipsisWidth, alignment, shouldEllipsize, effectiveEllipsize, effectiveEllipsize == this.mEllipsize ? true : DEBUG_EXTRACT);
        if (switchEllipsize) {
            this.mSavedMarqueeModeLayout = makeSingleLayout(wantWidth, boring, ellipsisWidth, alignment, shouldEllipsize, effectiveEllipsize == TruncateAt.MARQUEE ? TruncateAt.END : TruncateAt.MARQUEE, effectiveEllipsize != this.mEllipsize ? true : DEBUG_EXTRACT);
        }
        shouldEllipsize = this.mEllipsize != null ? true : DEBUG_EXTRACT;
        this.mHintLayout = null;
        if (this.mHint != null) {
            if (shouldEllipsize) {
                hintWidth = wantWidth;
            }
            if (hintBoring == UNKNOWN_BORING) {
                hintBoring = BoringLayout.isBoring(this.mHint, this.mTextPaint, this.mTextDir, this.mHintBoring);
                if (hintBoring != null) {
                    this.mHintBoring = hintBoring;
                }
            }
            if (hintBoring != null) {
                if (hintBoring.width <= hintWidth && (!shouldEllipsize || hintBoring.width <= ellipsisWidth)) {
                    if (this.mSavedHintLayout != null) {
                        this.mHintLayout = this.mSavedHintLayout.replaceOrMake(this.mHint, this.mTextPaint, hintWidth, alignment, this.mSpacingMult, this.mSpacingAdd, hintBoring, this.mIncludePad);
                    } else {
                        this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, hintWidth, alignment, this.mSpacingMult, this.mSpacingAdd, hintBoring, this.mIncludePad);
                    }
                    this.mSavedHintLayout = (BoringLayout) this.mHintLayout;
                } else if (!shouldEllipsize || hintBoring.width > hintWidth) {
                    if (shouldEllipsize) {
                        this.mHintLayout = new StaticLayout(this.mHint, MARQUEE_FADE_NORMAL, this.mHint.length(), this.mTextPaint, hintWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, this.mEllipsize, ellipsisWidth, this.mMaxMode == SANS ? this.mMaximum : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                    } else {
                        this.mHintLayout = new StaticLayout(this.mHint, this.mTextPaint, hintWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
                    }
                } else if (this.mSavedHintLayout != null) {
                    this.mHintLayout = this.mSavedHintLayout.replaceOrMake(this.mHint, this.mTextPaint, hintWidth, alignment, this.mSpacingMult, this.mSpacingAdd, hintBoring, this.mIncludePad, this.mEllipsize, ellipsisWidth);
                } else {
                    this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, hintWidth, alignment, this.mSpacingMult, this.mSpacingAdd, hintBoring, this.mIncludePad, this.mEllipsize, ellipsisWidth);
                }
            } else if (shouldEllipsize) {
                this.mHintLayout = new StaticLayout(this.mHint, MARQUEE_FADE_NORMAL, this.mHint.length(), this.mTextPaint, hintWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, this.mEllipsize, ellipsisWidth, this.mMaxMode == SANS ? this.mMaximum : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            } else {
                this.mHintLayout = new StaticLayout(this.mHint, this.mTextPaint, hintWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
            }
        }
        if (bringIntoView || (testDirChange && oldDir != this.mLayout.getParagraphDirection(MARQUEE_FADE_NORMAL))) {
            registerForPreDraw();
        }
        if (this.mEllipsize == TruncateAt.MARQUEE) {
            if (!compressText((float) ellipsisWidth)) {
                int height = this.mLayoutParams.height;
                if (height == -2 || height == -1) {
                    this.mRestartMarquee = true;
                } else {
                    startMarquee();
                }
            }
        }
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
    }

    private Layout makeSingleLayout(int wantWidth, Metrics boring, int ellipsisWidth, Alignment alignment, boolean shouldEllipsize, TruncateAt effectiveEllipsize, boolean useSaved) {
        if (this.mText instanceof Spannable) {
            TruncateAt truncateAt;
            CharSequence charSequence = this.mText;
            CharSequence charSequence2 = this.mTransformed;
            TextPaint textPaint = this.mTextPaint;
            TextDirectionHeuristic textDirectionHeuristic = this.mTextDir;
            float f = this.mSpacingMult;
            float f2 = this.mSpacingAdd;
            boolean z = this.mIncludePad;
            if (getKeyListener() == null) {
                truncateAt = effectiveEllipsize;
            } else {
                truncateAt = null;
            }
            return new DynamicLayout(charSequence, charSequence2, textPaint, wantWidth, alignment, textDirectionHeuristic, f, f2, z, truncateAt, ellipsisWidth);
        }
        if (boring == UNKNOWN_BORING) {
            boring = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, this.mBoring);
            if (boring != null) {
                this.mBoring = boring;
            }
        }
        if (boring != null) {
            if (boring.width <= wantWidth && (effectiveEllipsize == null || boring.width <= ellipsisWidth)) {
                Layout result;
                if (!useSaved || this.mSavedLayout == null) {
                    result = BoringLayout.make(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring, this.mIncludePad);
                } else {
                    result = this.mSavedLayout.replaceOrMake(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring, this.mIncludePad);
                }
                if (!useSaved) {
                    return result;
                }
                this.mSavedLayout = (BoringLayout) result;
                return result;
            } else if (!shouldEllipsize || boring.width > wantWidth) {
                if (shouldEllipsize) {
                    return new StaticLayout(this.mTransformed, MARQUEE_FADE_NORMAL, this.mTransformed.length(), this.mTextPaint, wantWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, effectiveEllipsize, ellipsisWidth, this.mMaxMode == SANS ? this.mMaximum : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                }
                return new StaticLayout(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
            } else if (!useSaved || this.mSavedLayout == null) {
                return BoringLayout.make(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring, this.mIncludePad, effectiveEllipsize, ellipsisWidth);
            } else {
                return this.mSavedLayout.replaceOrMake(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mSpacingMult, this.mSpacingAdd, boring, this.mIncludePad, effectiveEllipsize, ellipsisWidth);
            }
        } else if (shouldEllipsize) {
            return new StaticLayout(this.mTransformed, MARQUEE_FADE_NORMAL, this.mTransformed.length(), this.mTextPaint, wantWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad, effectiveEllipsize, ellipsisWidth, this.mMaxMode == SANS ? this.mMaximum : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        } else {
            return new StaticLayout(this.mTransformed, this.mTextPaint, wantWidth, alignment, this.mTextDir, this.mSpacingMult, this.mSpacingAdd, this.mIncludePad);
        }
    }

    private boolean compressText(float width) {
        if (isHardwareAccelerated() || width <= 0.0f || this.mLayout == null || getLineCount() != SANS || this.mUserSetTextScaleX || this.mTextPaint.getTextScaleX() != LayoutParams.BRIGHTNESS_OVERRIDE_FULL) {
            return DEBUG_EXTRACT;
        }
        float overflow = ((this.mLayout.getLineWidth(MARQUEE_FADE_NORMAL) + LayoutParams.BRIGHTNESS_OVERRIDE_FULL) - width) / width;
        if (overflow <= 0.0f || overflow > 0.07f) {
            return DEBUG_EXTRACT;
        }
        this.mTextPaint.setTextScaleX((LayoutParams.BRIGHTNESS_OVERRIDE_FULL - overflow) - 0.005f);
        post(new AnonymousClass2(this));
        return true;
    }

    private static int desired(Layout layout) {
        int i;
        int n = layout.getLineCount();
        CharSequence text = layout.getText();
        float max = 0.0f;
        for (i = MARQUEE_FADE_NORMAL; i < n - 1; i += SANS) {
            if (text.charAt(layout.getLineEnd(i) - 1) != '\n') {
                return -1;
            }
        }
        for (i = MARQUEE_FADE_NORMAL; i < n; i += SANS) {
            max = Math.max(max, layout.getLineWidth(i));
        }
        return (int) FloatMath.ceil(max);
    }

    public void setIncludeFontPadding(boolean includepad) {
        if (this.mIncludePad != includepad) {
            this.mIncludePad = includepad;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean getIncludeFontPadding() {
        return this.mIncludePad;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int hintWidth;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Metrics boring = UNKNOWN_BORING;
        Metrics hintBoring = UNKNOWN_BORING;
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        int des = -1;
        boolean fromexisting = DEBUG_EXTRACT;
        if (widthMode == 1073741824) {
            width = widthSize;
        } else {
            if (this.mLayout != null && this.mEllipsize == null) {
                des = desired(this.mLayout);
            }
            if (des < 0) {
                boring = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, this.mBoring);
                if (boring != null) {
                    this.mBoring = boring;
                }
            } else {
                fromexisting = true;
            }
            if (boring == null || boring == UNKNOWN_BORING) {
                if (des < 0) {
                    des = (int) FloatMath.ceil(Layout.getDesiredWidth(this.mTransformed, this.mTextPaint));
                }
                width = des;
            } else {
                width = boring.width;
            }
            Drawables dr = this.mDrawables;
            if (dr != null) {
                width = Math.max(Math.max(width, dr.mDrawableWidthTop), dr.mDrawableWidthBottom);
            }
            if (this.mHint != null) {
                int hintDes = -1;
                if (this.mHintLayout != null && this.mEllipsize == null) {
                    hintDes = desired(this.mHintLayout);
                }
                if (hintDes < 0) {
                    hintBoring = BoringLayout.isBoring(this.mHint, this.mTextPaint, this.mTextDir, this.mHintBoring);
                    if (hintBoring != null) {
                        this.mHintBoring = hintBoring;
                    }
                }
                if (hintBoring == null || hintBoring == UNKNOWN_BORING) {
                    if (hintDes < 0) {
                        hintDes = (int) FloatMath.ceil(Layout.getDesiredWidth(this.mHint, this.mTextPaint));
                    }
                    hintWidth = hintDes;
                } else {
                    hintWidth = hintBoring.width;
                }
                if (hintWidth > width) {
                    width = hintWidth;
                }
            }
            width += getCompoundPaddingLeft() + getCompoundPaddingRight();
            if (this.mMaxWidthMode == SANS) {
                width = Math.min(width, this.mMaxWidth * getLineHeight());
            } else {
                width = Math.min(width, this.mMaxWidth);
            }
            if (this.mMinWidthMode == SANS) {
                width = Math.max(width, this.mMinWidth * getLineHeight());
            } else {
                width = Math.max(width, this.mMinWidth);
            }
            width = Math.max(width, getSuggestedMinimumWidth());
            if (widthMode == Integer.MIN_VALUE) {
                width = Math.min(widthSize, width);
            }
        }
        int want = (width - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int unpaddedWidth = want;
        if (this.mHorizontallyScrolling) {
            want = VERY_WIDE;
        }
        int hintWant = want;
        hintWidth = this.mHintLayout == null ? hintWant : this.mHintLayout.getWidth();
        if (this.mLayout == null) {
            makeNewLayout(want, hintWant, boring, hintBoring, (width - getCompoundPaddingLeft()) - getCompoundPaddingRight(), DEBUG_EXTRACT);
        } else {
            boolean layoutChanged = (this.mLayout.getWidth() == want && hintWidth == hintWant && this.mLayout.getEllipsizedWidth() == (width - getCompoundPaddingLeft()) - getCompoundPaddingRight()) ? DEBUG_EXTRACT : true;
            boolean widthChanged = (this.mHint != null || this.mEllipsize != null || want <= this.mLayout.getWidth() || (!(this.mLayout instanceof BoringLayout) && (!fromexisting || des < 0 || des > want))) ? DEBUG_EXTRACT : true;
            boolean maximumChanged = (this.mMaxMode == this.mOldMaxMode && this.mMaximum == this.mOldMaximum) ? DEBUG_EXTRACT : true;
            if (layoutChanged || maximumChanged) {
                if (maximumChanged || !widthChanged) {
                    makeNewLayout(want, hintWant, boring, hintBoring, (width - getCompoundPaddingLeft()) - getCompoundPaddingRight(), DEBUG_EXTRACT);
                } else {
                    this.mLayout.increaseWidthTo(want);
                }
            }
        }
        if (heightMode == EditorInfo.IME_FLAG_NO_ENTER_ACTION) {
            height = heightSize;
            this.mDesiredHeightAtMeasure = -1;
        } else {
            int desired = getDesiredHeight();
            height = desired;
            this.mDesiredHeightAtMeasure = desired;
            if (heightMode == RtlSpacingHelper.UNDEFINED) {
                height = Math.min(desired, heightSize);
            }
        }
        int unpaddedHeight = (height - getCompoundPaddingTop()) - getCompoundPaddingBottom();
        if (this.mMaxMode == SANS && this.mLayout.getLineCount() > this.mMaximum) {
            unpaddedHeight = Math.min(unpaddedHeight, this.mLayout.getLineTop(this.mMaximum));
        }
        if (this.mMovement != null || this.mLayout.getWidth() > unpaddedWidth || this.mLayout.getHeight() > unpaddedHeight) {
            registerForPreDraw();
        } else {
            scrollTo(MARQUEE_FADE_NORMAL, MARQUEE_FADE_NORMAL);
        }
        setMeasuredDimension(width, height);
    }

    private int getDesiredHeight() {
        boolean z = true;
        int desiredHeight = getDesiredHeight(this.mLayout, true);
        Layout layout = this.mHintLayout;
        if (this.mEllipsize == null) {
            z = DEBUG_EXTRACT;
        }
        return Math.max(desiredHeight, getDesiredHeight(layout, z));
    }

    private int getDesiredHeight(Layout layout, boolean cap) {
        if (layout == null) {
            return MARQUEE_FADE_NORMAL;
        }
        int linecount = layout.getLineCount();
        int pad = getCompoundPaddingTop() + getCompoundPaddingBottom();
        int desired = layout.getLineTop(linecount);
        Drawables dr = this.mDrawables;
        if (dr != null) {
            desired = Math.max(Math.max(desired, dr.mDrawableHeightLeft), dr.mDrawableHeightRight);
        }
        desired += pad;
        if (this.mMaxMode != SANS) {
            desired = Math.min(desired, this.mMaximum);
        } else if (cap && linecount > this.mMaximum) {
            desired = layout.getLineTop(this.mMaximum);
            if (dr != null) {
                desired = Math.max(Math.max(desired, dr.mDrawableHeightLeft), dr.mDrawableHeightRight);
            }
            desired += pad;
            linecount = this.mMaximum;
        }
        if (this.mMinMode != SANS) {
            desired = Math.max(desired, this.mMinimum);
        } else if (linecount < this.mMinimum) {
            desired += getLineHeight() * (this.mMinimum - linecount);
        }
        return Math.max(desired, getSuggestedMinimumHeight());
    }

    private void checkForResize() {
        boolean sizeChanged = DEBUG_EXTRACT;
        if (this.mLayout != null) {
            if (this.mLayoutParams.width == -2) {
                sizeChanged = true;
                invalidate();
            }
            if (this.mLayoutParams.height == -2) {
                if (getDesiredHeight() != getHeight()) {
                    sizeChanged = true;
                }
            } else if (this.mLayoutParams.height == -1 && this.mDesiredHeightAtMeasure >= 0 && getDesiredHeight() != this.mDesiredHeightAtMeasure) {
                sizeChanged = true;
            }
        }
        if (sizeChanged) {
            requestLayout();
        }
    }

    private void checkForRelayout() {
        if ((this.mLayoutParams.width != -2 || (this.mMaxWidthMode == this.mMinWidthMode && this.mMaxWidth == this.mMinWidth)) && ((this.mHint == null || this.mHintLayout != null) && ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight() > 0)) {
            int hintWant;
            int oldht = this.mLayout.getHeight();
            int want = this.mLayout.getWidth();
            if (this.mHintLayout == null) {
                hintWant = MARQUEE_FADE_NORMAL;
            } else {
                hintWant = this.mHintLayout.getWidth();
            }
            makeNewLayout(want, hintWant, UNKNOWN_BORING, UNKNOWN_BORING, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), DEBUG_EXTRACT);
            if (this.mEllipsize != TruncateAt.MARQUEE) {
                if (this.mLayoutParams.height != -2 && this.mLayoutParams.height != -1) {
                    invalidate();
                    return;
                } else if (this.mLayout.getHeight() == oldht && (this.mHintLayout == null || this.mHintLayout.getHeight() == oldht)) {
                    invalidate();
                    return;
                }
            }
            requestLayout();
            invalidate();
            return;
        }
        nullLayouts();
        requestLayout();
        invalidate();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mDeferScroll >= 0) {
            int curs = this.mDeferScroll;
            this.mDeferScroll = -1;
            bringPointIntoView(Math.min(curs, this.mText.length()));
        }
    }

    private boolean isShowingHint() {
        return (!TextUtils.isEmpty(this.mText) || TextUtils.isEmpty(this.mHint)) ? DEBUG_EXTRACT : true;
    }

    private boolean bringTextIntoView() {
        int scrollx;
        int scrolly;
        Layout layout = isShowingHint() ? this.mHintLayout : this.mLayout;
        int line = MARQUEE_FADE_NORMAL;
        if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) == 80) {
            line = layout.getLineCount() - 1;
        }
        Alignment a = layout.getParagraphAlignment(line);
        int dir = layout.getParagraphDirection(line);
        int hspace = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int vspace = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        int ht = layout.getHeight();
        if (a == Alignment.ALIGN_NORMAL) {
            a = dir == SANS ? Alignment.ALIGN_LEFT : Alignment.ALIGN_RIGHT;
        } else if (a == Alignment.ALIGN_OPPOSITE) {
            a = dir == SANS ? Alignment.ALIGN_RIGHT : Alignment.ALIGN_LEFT;
        }
        if (a == Alignment.ALIGN_CENTER) {
            int left = (int) FloatMath.floor(layout.getLineLeft(line));
            int right = (int) FloatMath.ceil(layout.getLineRight(line));
            if (right - left < hspace) {
                scrollx = ((right + left) / SIGNED) - (hspace / SIGNED);
            } else if (dir < 0) {
                scrollx = right - hspace;
            } else {
                scrollx = left;
            }
        } else if (a == Alignment.ALIGN_RIGHT) {
            scrollx = ((int) FloatMath.ceil(layout.getLineRight(line))) - hspace;
        } else {
            scrollx = (int) FloatMath.floor(layout.getLineLeft(line));
        }
        if (ht < vspace) {
            scrolly = MARQUEE_FADE_NORMAL;
        } else if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) == 80) {
            scrolly = ht - vspace;
        } else {
            scrolly = MARQUEE_FADE_NORMAL;
        }
        if (scrollx == this.mScrollX && scrolly == this.mScrollY) {
            return DEBUG_EXTRACT;
        }
        scrollTo(scrollx, scrolly);
        return true;
    }

    public boolean bringPointIntoView(int offset) {
        if (isLayoutRequested()) {
            this.mDeferScroll = offset;
            return DEBUG_EXTRACT;
        }
        boolean changed = DEBUG_EXTRACT;
        Layout layout = isShowingHint() ? this.mHintLayout : this.mLayout;
        if (layout == null) {
            return DEBUG_EXTRACT;
        }
        int grav;
        int line = layout.getLineForOffset(offset);
        switch (AnonymousClass4.$SwitchMap$android$text$Layout$Alignment[layout.getParagraphAlignment(line).ordinal()]) {
            case SANS /*1*/:
                grav = SANS;
                break;
            case SIGNED /*2*/:
                grav = -1;
                break;
            case MONOSPACE /*3*/:
                grav = layout.getParagraphDirection(line);
                break;
            case DECIMAL /*4*/:
                grav = -layout.getParagraphDirection(line);
                break;
            default:
                grav = MARQUEE_FADE_NORMAL;
                break;
        }
        int x = (int) layout.getPrimaryHorizontal(offset, grav > 0 ? true : DEBUG_EXTRACT);
        int top = layout.getLineTop(line);
        int bottom = layout.getLineTop(line + SANS);
        int left = (int) FloatMath.floor(layout.getLineLeft(line));
        int right = (int) FloatMath.ceil(layout.getLineRight(line));
        int ht = layout.getHeight();
        int hspace = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int vspace = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        if (!this.mHorizontallyScrolling && right - left > hspace && right > x) {
            right = Math.max(x, left + hspace);
        }
        int hslack = (bottom - top) / SIGNED;
        int vslack = hslack;
        if (vslack > vspace / DECIMAL) {
            vslack = vspace / DECIMAL;
        }
        if (hslack > hspace / DECIMAL) {
            hslack = hspace / DECIMAL;
        }
        int hs = this.mScrollX;
        int vs = this.mScrollY;
        if (top - vs < vslack) {
            vs = top - vslack;
        }
        if (bottom - vs > vspace - vslack) {
            vs = bottom - (vspace - vslack);
        }
        if (ht - vs < vspace) {
            vs = ht - vspace;
        }
        if (0 - vs > 0) {
            vs = MARQUEE_FADE_NORMAL;
        }
        if (grav != 0) {
            if (x - hs < hslack) {
                hs = x - hslack;
            }
            if (x - hs > hspace - hslack) {
                hs = x - (hspace - hslack);
            }
        }
        if (grav < 0) {
            if (left - hs > 0) {
                hs = left;
            }
            if (right - hs < hspace) {
                hs = right - hspace;
            }
        } else if (grav > 0) {
            if (right - hs < hspace) {
                hs = right - hspace;
            }
            if (left - hs > 0) {
                hs = left;
            }
        } else if (right - left <= hspace) {
            hs = left - ((hspace - (right - left)) / SIGNED);
        } else if (x > right - hslack) {
            hs = right - hspace;
        } else if (x < left + hslack) {
            hs = left;
        } else if (left > hs) {
            hs = left;
        } else if (right < hs + hspace) {
            hs = right - hspace;
        } else {
            if (x - hs < hslack) {
                hs = x - hslack;
            }
            if (x - hs > hspace - hslack) {
                hs = x - (hspace - hslack);
            }
        }
        int i = this.mScrollX;
        if (!(hs == r0 && vs == this.mScrollY)) {
            if (this.mScroller == null) {
                scrollTo(hs, vs);
            } else {
                int dx = hs - this.mScrollX;
                int dy = vs - this.mScrollY;
                if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250) {
                    this.mScroller.startScroll(this.mScrollX, this.mScrollY, dx, dy);
                    awakenScrollBars(this.mScroller.getDuration());
                    invalidate();
                } else {
                    if (!this.mScroller.isFinished()) {
                        this.mScroller.abortAnimation();
                    }
                    scrollBy(dx, dy);
                }
                this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
            }
            changed = true;
        }
        if (!isFocused()) {
            return changed;
        }
        if (this.mTempRect == null) {
            this.mTempRect = new Rect();
        }
        this.mTempRect.set(x - 2, top, x + SIGNED, bottom);
        getInterestingRect(this.mTempRect, line);
        this.mTempRect.offset(this.mScrollX, this.mScrollY);
        if (requestRectangleOnScreen(this.mTempRect)) {
            return true;
        }
        return changed;
    }

    public boolean moveCursorToVisibleOffset() {
        if (!(this.mText instanceof Spannable)) {
            return DEBUG_EXTRACT;
        }
        int start = getSelectionStart();
        if (start != getSelectionEnd()) {
            return DEBUG_EXTRACT;
        }
        int lowChar;
        int highChar;
        int line = this.mLayout.getLineForOffset(start);
        int top = this.mLayout.getLineTop(line);
        int bottom = this.mLayout.getLineTop(line + SANS);
        int vspace = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        int vslack = (bottom - top) / SIGNED;
        if (vslack > vspace / DECIMAL) {
            vslack = vspace / DECIMAL;
        }
        int vs = this.mScrollY;
        if (top < vs + vslack) {
            line = this.mLayout.getLineForVertical((vs + vslack) + (bottom - top));
        } else if (bottom > (vspace + vs) - vslack) {
            line = this.mLayout.getLineForVertical(((vspace + vs) - vslack) - (bottom - top));
        }
        int hspace = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int hs = this.mScrollX;
        int leftChar = this.mLayout.getOffsetForHorizontal(line, (float) hs);
        int rightChar = this.mLayout.getOffsetForHorizontal(line, (float) (hspace + hs));
        if (leftChar < rightChar) {
            lowChar = leftChar;
        } else {
            lowChar = rightChar;
        }
        if (leftChar > rightChar) {
            highChar = leftChar;
        } else {
            highChar = rightChar;
        }
        int newStart = start;
        if (newStart < lowChar) {
            newStart = lowChar;
        } else if (newStart > highChar) {
            newStart = highChar;
        }
        if (newStart == start) {
            return DEBUG_EXTRACT;
        }
        Selection.setSelection((Spannable) this.mText, newStart);
        return true;
    }

    public void computeScroll() {
        if (this.mScroller != null && this.mScroller.computeScrollOffset()) {
            this.mScrollX = this.mScroller.getCurrX();
            this.mScrollY = this.mScroller.getCurrY();
            invalidateParentCaches();
            postInvalidate();
        }
    }

    private void getInterestingRect(Rect r, int line) {
        convertFromViewportToContentCoordinates(r);
        if (line == 0) {
            r.top -= getExtendedPaddingTop();
        }
        if (line == this.mLayout.getLineCount() - 1) {
            r.bottom += getExtendedPaddingBottom();
        }
    }

    private void convertFromViewportToContentCoordinates(Rect r) {
        int horizontalOffset = viewportToContentHorizontalOffset();
        r.left += horizontalOffset;
        r.right += horizontalOffset;
        int verticalOffset = viewportToContentVerticalOffset();
        r.top += verticalOffset;
        r.bottom += verticalOffset;
    }

    int viewportToContentHorizontalOffset() {
        return getCompoundPaddingLeft() - this.mScrollX;
    }

    int viewportToContentVerticalOffset() {
        int offset = getExtendedPaddingTop() - this.mScrollY;
        if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) != 48) {
            return offset + getVerticalOffset(DEBUG_EXTRACT);
        }
        return offset;
    }

    public void debug(int depth) {
        super.debug(depth);
        String output = View.debugIndent(depth) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ";
        if (this.mText != null) {
            output = output + "mText=\"" + this.mText + "\" ";
            if (this.mLayout != null) {
                output = output + "mLayout width=" + this.mLayout.getWidth() + " height=" + this.mLayout.getHeight();
            }
        } else {
            output = output + "mText=NULL";
        }
        Log.d("View", output);
    }

    @ExportedProperty(category = "text")
    public int getSelectionStart() {
        return Selection.getSelectionStart(getText());
    }

    @ExportedProperty(category = "text")
    public int getSelectionEnd() {
        return Selection.getSelectionEnd(getText());
    }

    public boolean hasSelection() {
        int selectionStart = getSelectionStart();
        return (selectionStart < 0 || selectionStart == getSelectionEnd()) ? DEBUG_EXTRACT : true;
    }

    public void setSingleLine() {
        setSingleLine(true);
    }

    public void setAllCaps(boolean allCaps) {
        if (allCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        } else {
            setTransformationMethod(null);
        }
    }

    @RemotableViewMethod
    public void setSingleLine(boolean singleLine) {
        setInputTypeSingleLine(singleLine);
        applySingleLine(singleLine, true, true);
    }

    private void setInputTypeSingleLine(boolean singleLine) {
        if (this.mEditor != null && (this.mEditor.mInputType & 15) == SANS) {
            Editor editor;
            if (singleLine) {
                editor = this.mEditor;
                editor.mInputType &= -131073;
                return;
            }
            editor = this.mEditor;
            editor.mInputType |= AccessibilityNodeInfo.ACTION_SET_SELECTION;
        }
    }

    private void applySingleLine(boolean singleLine, boolean applyTransformation, boolean changeMaxLines) {
        this.mSingleLine = singleLine;
        if (singleLine) {
            setLines(SANS);
            setHorizontallyScrolling(true);
            if (applyTransformation) {
                setTransformationMethod(SingleLineTransformationMethod.getInstance());
                return;
            }
            return;
        }
        if (changeMaxLines) {
            setMaxLines(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        }
        setHorizontallyScrolling(DEBUG_EXTRACT);
        if (applyTransformation) {
            setTransformationMethod(null);
        }
    }

    public void setEllipsize(TruncateAt where) {
        if (this.mEllipsize != where) {
            this.mEllipsize = where;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setMarqueeRepeatLimit(int marqueeLimit) {
        this.mMarqueeRepeatLimit = marqueeLimit;
    }

    public int getMarqueeRepeatLimit() {
        return this.mMarqueeRepeatLimit;
    }

    @ExportedProperty
    public TruncateAt getEllipsize() {
        return this.mEllipsize;
    }

    @RemotableViewMethod
    public void setSelectAllOnFocus(boolean selectAllOnFocus) {
        createEditorIfNeeded();
        this.mEditor.mSelectAllOnFocus = selectAllOnFocus;
        if (selectAllOnFocus && !(this.mText instanceof Spannable)) {
            setText(this.mText, BufferType.SPANNABLE);
        }
    }

    @RemotableViewMethod
    public void setCursorVisible(boolean visible) {
        if (!visible || this.mEditor != null) {
            createEditorIfNeeded();
            if (this.mEditor.mCursorVisible != visible) {
                this.mEditor.mCursorVisible = visible;
                invalidate();
                this.mEditor.makeBlink();
                this.mEditor.prepareCursorControllers();
            }
        }
    }

    public boolean isCursorVisible() {
        return this.mEditor == null ? true : this.mEditor.mCursorVisible;
    }

    private boolean canMarquee() {
        int width = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (width <= 0) {
            return DEBUG_EXTRACT;
        }
        if (this.mLayout.getLineWidth(MARQUEE_FADE_NORMAL) > ((float) width) || (this.mMarqueeFadeMode != 0 && this.mSavedMarqueeModeLayout != null && this.mSavedMarqueeModeLayout.getLineWidth(MARQUEE_FADE_NORMAL) > ((float) width))) {
            return true;
        }
        return DEBUG_EXTRACT;
    }

    private void startMarquee() {
        if (getKeyListener() != null || compressText((float) ((getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight()))) {
            return;
        }
        if (this.mMarquee != null && !this.mMarquee.isStopped()) {
            return;
        }
        if ((isFocused() || isSelected()) && getLineCount() == SANS && canMarquee()) {
            if (this.mMarqueeFadeMode == SANS) {
                this.mMarqueeFadeMode = SIGNED;
                Layout tmp = this.mLayout;
                this.mLayout = this.mSavedMarqueeModeLayout;
                this.mSavedMarqueeModeLayout = tmp;
                setHorizontalFadingEdgeEnabled(true);
                requestLayout();
                invalidate();
            }
            if (this.mMarquee == null) {
                this.mMarquee = new Marquee(this);
            }
            this.mMarquee.start(this.mMarqueeRepeatLimit);
        }
    }

    private void stopMarquee() {
        if (!(this.mMarquee == null || this.mMarquee.isStopped())) {
            this.mMarquee.stop();
        }
        if (this.mMarqueeFadeMode == SIGNED) {
            this.mMarqueeFadeMode = SANS;
            Layout tmp = this.mSavedMarqueeModeLayout;
            this.mSavedMarqueeModeLayout = this.mLayout;
            this.mLayout = tmp;
            setHorizontalFadingEdgeEnabled(DEBUG_EXTRACT);
            requestLayout();
            invalidate();
        }
    }

    private void startStopMarquee(boolean start) {
        if (this.mEllipsize != TruncateAt.MARQUEE) {
            return;
        }
        if (start) {
            startMarquee();
        } else {
            stopMarquee();
        }
    }

    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    }

    protected void onSelectionChanged(int selStart, int selEnd) {
        sendAccessibilityEvent(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList();
        }
        this.mListeners.add(watcher);
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        if (this.mListeners != null) {
            int i = this.mListeners.indexOf(watcher);
            if (i >= 0) {
                this.mListeners.remove(i);
            }
        }
    }

    private void sendBeforeTextChanged(CharSequence text, int start, int before, int after) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> list = this.mListeners;
            int count = list.size();
            for (int i = MARQUEE_FADE_NORMAL; i < count; i += SANS) {
                ((TextWatcher) list.get(i)).beforeTextChanged(text, start, before, after);
            }
        }
        removeIntersectingNonAdjacentSpans(start, start + before, SpellCheckSpan.class);
        removeIntersectingNonAdjacentSpans(start, start + before, SuggestionSpan.class);
    }

    private <T> void removeIntersectingNonAdjacentSpans(int start, int end, Class<T> type) {
        if (this.mText instanceof Editable) {
            Editable text = this.mText;
            T[] spans = text.getSpans(start, end, type);
            int length = spans.length;
            int i = MARQUEE_FADE_NORMAL;
            while (i < length) {
                int spanStart = text.getSpanStart(spans[i]);
                if (text.getSpanEnd(spans[i]) != start && spanStart != end) {
                    text.removeSpan(spans[i]);
                    i += SANS;
                } else {
                    return;
                }
            }
        }
    }

    void removeAdjacentSuggestionSpans(int pos) {
        if (this.mText instanceof Editable) {
            Editable text = this.mText;
            SuggestionSpan[] spans = (SuggestionSpan[]) text.getSpans(pos, pos, SuggestionSpan.class);
            int length = spans.length;
            for (int i = MARQUEE_FADE_NORMAL; i < length; i += SANS) {
                int spanStart = text.getSpanStart(spans[i]);
                int spanEnd = text.getSpanEnd(spans[i]);
                if ((spanEnd == pos || spanStart == pos) && SpellChecker.haveWordBoundariesChanged(text, pos, pos, spanStart, spanEnd)) {
                    text.removeSpan(spans[i]);
                }
            }
        }
    }

    void sendOnTextChanged(CharSequence text, int start, int before, int after) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> list = this.mListeners;
            int count = list.size();
            for (int i = MARQUEE_FADE_NORMAL; i < count; i += SANS) {
                ((TextWatcher) list.get(i)).onTextChanged(text, start, before, after);
            }
        }
        if (this.mEditor != null) {
            this.mEditor.sendOnTextChanged(start, after);
        }
    }

    void sendAfterTextChanged(Editable text) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> list = this.mListeners;
            int count = list.size();
            for (int i = MARQUEE_FADE_NORMAL; i < count; i += SANS) {
                ((TextWatcher) list.get(i)).afterTextChanged(text);
            }
        }
        hideErrorIfUnchanged();
    }

    void updateAfterEdit() {
        invalidate();
        int curs = getSelectionStart();
        if (curs >= 0 || (this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) == 80) {
            registerForPreDraw();
        }
        checkForResize();
        if (curs >= 0) {
            this.mHighlightPathBogus = true;
            if (this.mEditor != null) {
                this.mEditor.makeBlink();
            }
            bringPointIntoView(curs);
        }
    }

    void handleTextChanged(CharSequence buffer, int start, int before, int after) {
        InputMethodState ims = this.mEditor == null ? null : this.mEditor.mInputMethodState;
        if (ims == null || ims.mBatchEditNesting == 0) {
            updateAfterEdit();
        }
        if (ims != null) {
            ims.mContentChanged = true;
            if (ims.mChangedStart < 0) {
                ims.mChangedStart = start;
                ims.mChangedEnd = start + before;
            } else {
                ims.mChangedStart = Math.min(ims.mChangedStart, start);
                ims.mChangedEnd = Math.max(ims.mChangedEnd, (start + before) - ims.mChangedDelta);
            }
            ims.mChangedDelta += after - before;
        }
        resetErrorChangedFlag();
        sendOnTextChanged(buffer, start, before, after);
        onTextChanged(buffer, start, before, after);
    }

    void spanChange(Spanned buf, Object what, int oldStart, int newStart, int oldEnd, int newEnd) {
        InputMethodState ims;
        boolean selChanged = DEBUG_EXTRACT;
        int newSelStart = -1;
        int newSelEnd = -1;
        if (this.mEditor == null) {
            ims = null;
        } else {
            ims = this.mEditor.mInputMethodState;
        }
        if (what == Selection.SELECTION_END) {
            selChanged = true;
            newSelEnd = newStart;
            if (oldStart >= 0 || newStart >= 0) {
                invalidateCursor(Selection.getSelectionStart(buf), oldStart, newStart);
                checkForResize();
                registerForPreDraw();
                if (this.mEditor != null) {
                    this.mEditor.makeBlink();
                }
            }
        }
        if (what == Selection.SELECTION_START) {
            selChanged = true;
            newSelStart = newStart;
            if (oldStart >= 0 || newStart >= 0) {
                invalidateCursor(Selection.getSelectionEnd(buf), oldStart, newStart);
            }
        }
        if (selChanged) {
            this.mHighlightPathBogus = true;
            if (!(this.mEditor == null || isFocused())) {
                this.mEditor.mSelectionMoved = true;
            }
            if ((buf.getSpanFlags(what) & AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) == 0) {
                if (newSelStart < 0) {
                    newSelStart = Selection.getSelectionStart(buf);
                }
                if (newSelEnd < 0) {
                    newSelEnd = Selection.getSelectionEnd(buf);
                }
                onSelectionChanged(newSelStart, newSelEnd);
            }
        }
        if ((what instanceof UpdateAppearance) || (what instanceof ParagraphStyle) || (what instanceof CharacterStyle)) {
            if (ims == null || ims.mBatchEditNesting == 0) {
                invalidate();
                this.mHighlightPathBogus = true;
                checkForResize();
            } else {
                ims.mContentChanged = true;
            }
            if (this.mEditor != null) {
                if (oldStart >= 0) {
                    this.mEditor.invalidateTextDisplayList(this.mLayout, oldStart, oldEnd);
                }
                if (newStart >= 0) {
                    this.mEditor.invalidateTextDisplayList(this.mLayout, newStart, newEnd);
                }
            }
        }
        if (MetaKeyKeyListener.isMetaTracker(buf, what)) {
            this.mHighlightPathBogus = true;
            if (ims != null && MetaKeyKeyListener.isSelectingMetaTracker(buf, what)) {
                ims.mSelectionModeChanged = true;
            }
            if (Selection.getSelectionStart(buf) >= 0) {
                if (ims == null || ims.mBatchEditNesting == 0) {
                    invalidateCursor();
                } else {
                    ims.mCursorChanged = true;
                }
            }
        }
        if (!(!(what instanceof ParcelableSpan) || ims == null || ims.mExtractedTextRequest == null)) {
            if (ims.mBatchEditNesting != 0) {
                if (oldStart >= 0) {
                    if (ims.mChangedStart > oldStart) {
                        ims.mChangedStart = oldStart;
                    }
                    if (ims.mChangedStart > oldEnd) {
                        ims.mChangedStart = oldEnd;
                    }
                }
                if (newStart >= 0) {
                    if (ims.mChangedStart > newStart) {
                        ims.mChangedStart = newStart;
                    }
                    if (ims.mChangedStart > newEnd) {
                        ims.mChangedStart = newEnd;
                    }
                }
            } else {
                ims.mContentChanged = true;
            }
        }
        if (this.mEditor != null && this.mEditor.mSpellChecker != null && newStart < 0 && (what instanceof SpellCheckSpan)) {
            this.mEditor.mSpellChecker.onSpellCheckSpanRemoved((SpellCheckSpan) what);
        }
    }

    public void dispatchFinishTemporaryDetach() {
        this.mDispatchTemporaryDetach = true;
        super.dispatchFinishTemporaryDetach();
        this.mDispatchTemporaryDetach = DEBUG_EXTRACT;
    }

    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (!this.mDispatchTemporaryDetach) {
            this.mTemporaryDetach = true;
        }
        if (this.mEditor != null) {
            this.mEditor.mTemporaryDetach = true;
        }
    }

    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        if (!this.mDispatchTemporaryDetach) {
            this.mTemporaryDetach = DEBUG_EXTRACT;
        }
        if (this.mEditor != null) {
            this.mEditor.mTemporaryDetach = DEBUG_EXTRACT;
        }
    }

    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (this.mTemporaryDetach) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            return;
        }
        if (this.mEditor != null) {
            this.mEditor.onFocusChanged(focused, direction);
        }
        if (focused && (this.mText instanceof Spannable)) {
            MetaKeyKeyListener.resetMetaState(this.mText);
        }
        startStopMarquee(focused);
        if (this.mTransformation != null) {
            this.mTransformation.onFocusChanged(this, this.mText, focused, direction, previouslyFocusedRect);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (this.mEditor != null) {
            this.mEditor.onWindowFocusChanged(hasWindowFocus);
        }
        startStopMarquee(hasWindowFocus);
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (this.mEditor != null && visibility != 0) {
            this.mEditor.hideControllers();
        }
    }

    public void clearComposingText() {
        if (this.mText instanceof Spannable) {
            BaseInputConnection.removeComposingSpans((Spannable) this.mText);
        }
    }

    public void setSelected(boolean selected) {
        boolean wasSelected = isSelected();
        super.setSelected(selected);
        if (selected != wasSelected && this.mEllipsize == TruncateAt.MARQUEE) {
            if (selected) {
                startMarquee();
            } else {
                stopMarquee();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int i = MARQUEE_FADE_NORMAL;
        int action = event.getActionMasked();
        if (this.mEditor != null) {
            this.mEditor.onTouchEvent(event);
        }
        boolean superResult = super.onTouchEvent(event);
        if (this.mEditor != null && this.mEditor.mDiscardNextActionUp && action == SANS) {
            this.mEditor.mDiscardNextActionUp = DEBUG_EXTRACT;
            return superResult;
        }
        boolean touchIsFinished;
        if (action != SANS || ((this.mEditor != null && this.mEditor.mIgnoreActionUpEvent) || !isFocused())) {
            touchIsFinished = DEBUG_EXTRACT;
        } else {
            touchIsFinished = true;
        }
        if ((this.mMovement == null && !onCheckIsTextEditor()) || !isEnabled() || !(this.mText instanceof Spannable) || this.mLayout == null) {
            return superResult;
        }
        boolean handled = DEBUG_EXTRACT;
        if (this.mMovement != null) {
            handled = DEBUG_EXTRACT | this.mMovement.onTouchEvent(this, (Spannable) this.mText, event);
        }
        boolean textIsSelectable = isTextSelectable();
        if (touchIsFinished && this.mLinksClickable && this.mAutoLinkMask != 0 && textIsSelectable) {
            ClickableSpan[] links = (ClickableSpan[]) ((Spannable) this.mText).getSpans(getSelectionStart(), getSelectionEnd(), ClickableSpan.class);
            if (links.length > 0) {
                links[MARQUEE_FADE_NORMAL].onClick(this);
                handled = true;
            }
        }
        if (touchIsFinished && (isTextEditable() || textIsSelectable)) {
            InputMethodManager imm = InputMethodManager.peekInstance();
            viewClicked(imm);
            if (!textIsSelectable && this.mEditor.mShowSoftInputOnFocus) {
                if (imm != null && imm.showSoftInput(this, MARQUEE_FADE_NORMAL)) {
                    i = SANS;
                }
                handled |= i;
            }
            this.mEditor.onTouchUpEvent(event);
            handled = true;
        }
        if (handled) {
            return true;
        }
        return superResult;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if (!(this.mMovement == null || !(this.mText instanceof Spannable) || this.mLayout == null)) {
            try {
                if (this.mMovement.onGenericMotionEvent(this, (Spannable) this.mText, event)) {
                    return true;
                }
            } catch (AbstractMethodError e) {
            }
        }
        return super.onGenericMotionEvent(event);
    }

    boolean isTextEditable() {
        return ((this.mText instanceof Editable) && onCheckIsTextEditor() && isEnabled()) ? true : DEBUG_EXTRACT;
    }

    public boolean didTouchFocusSelect() {
        return (this.mEditor == null || !this.mEditor.mTouchFocusSelected) ? DEBUG_EXTRACT : true;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        if (this.mEditor != null) {
            this.mEditor.mIgnoreActionUpEvent = true;
        }
    }

    public boolean onTrackballEvent(MotionEvent event) {
        if (this.mMovement == null || !(this.mText instanceof Spannable) || this.mLayout == null || !this.mMovement.onTrackballEvent(this, (Spannable) this.mText, event)) {
            return super.onTrackballEvent(event);
        }
        return true;
    }

    public void setScroller(Scroller s) {
        this.mScroller = s;
    }

    protected float getLeftFadingEdgeStrength() {
        if (this.mEllipsize == TruncateAt.MARQUEE && this.mMarqueeFadeMode != SANS) {
            if (this.mMarquee != null && !this.mMarquee.isStopped()) {
                Marquee marquee = this.mMarquee;
                if (marquee.shouldDrawLeftFade()) {
                    return marquee.getScroll() / ((float) getHorizontalFadingEdgeLength());
                }
                return 0.0f;
            } else if (getLineCount() == SANS) {
                switch (Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection()) & 7) {
                    case SANS /*1*/:
                    case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                        if (this.mLayout.getParagraphDirection(MARQUEE_FADE_NORMAL) != SANS) {
                            return ((((this.mLayout.getLineRight(MARQUEE_FADE_NORMAL) - ((float) (this.mRight - this.mLeft))) - ((float) getCompoundPaddingLeft())) - ((float) getCompoundPaddingRight())) - this.mLayout.getLineLeft(MARQUEE_FADE_NORMAL)) / ((float) getHorizontalFadingEdgeLength());
                        }
                        return 0.0f;
                    case MONOSPACE /*3*/:
                        return 0.0f;
                    case ReflectionActionWithoutParams.TAG /*5*/:
                        return ((((this.mLayout.getLineRight(MARQUEE_FADE_NORMAL) - ((float) (this.mRight - this.mLeft))) - ((float) getCompoundPaddingLeft())) - ((float) getCompoundPaddingRight())) - this.mLayout.getLineLeft(MARQUEE_FADE_NORMAL)) / ((float) getHorizontalFadingEdgeLength());
                }
            }
        }
        return super.getLeftFadingEdgeStrength();
    }

    protected float getRightFadingEdgeStrength() {
        if (this.mEllipsize == TruncateAt.MARQUEE && this.mMarqueeFadeMode != SANS) {
            if (this.mMarquee != null && !this.mMarquee.isStopped()) {
                Marquee marquee = this.mMarquee;
                return (marquee.getMaxFadeScroll() - marquee.getScroll()) / ((float) getHorizontalFadingEdgeLength());
            } else if (getLineCount() == SANS) {
                switch (Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection()) & 7) {
                    case SANS /*1*/:
                    case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                        if (this.mLayout.getParagraphDirection(MARQUEE_FADE_NORMAL) != -1) {
                            return (this.mLayout.getLineWidth(MARQUEE_FADE_NORMAL) - ((float) (((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight()))) / ((float) getHorizontalFadingEdgeLength());
                        }
                        return 0.0f;
                    case MONOSPACE /*3*/:
                        return (this.mLayout.getLineWidth(MARQUEE_FADE_NORMAL) - ((float) (((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight()))) / ((float) getHorizontalFadingEdgeLength());
                    case ReflectionActionWithoutParams.TAG /*5*/:
                        return 0.0f;
                }
            }
        }
        return super.getRightFadingEdgeStrength();
    }

    protected int computeHorizontalScrollRange() {
        if (this.mLayout != null) {
            return (this.mSingleLine && (this.mGravity & 7) == MONOSPACE) ? (int) this.mLayout.getLineWidth(MARQUEE_FADE_NORMAL) : this.mLayout.getWidth();
        } else {
            return super.computeHorizontalScrollRange();
        }
    }

    protected int computeVerticalScrollRange() {
        if (this.mLayout != null) {
            return this.mLayout.getHeight();
        }
        return super.computeVerticalScrollRange();
    }

    protected int computeVerticalScrollExtent() {
        return (getHeight() - getCompoundPaddingTop()) - getCompoundPaddingBottom();
    }

    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        super.findViewsWithText(outViews, searched, flags);
        if (!outViews.contains(this) && (flags & SANS) != 0 && !TextUtils.isEmpty(searched) && !TextUtils.isEmpty(this.mText)) {
            if (this.mText.toString().toLowerCase().contains(searched.toString().toLowerCase())) {
                outViews.add(this);
            }
        }
    }

    public static ColorStateList getTextColors(Context context, TypedArray attrs) {
        if (attrs == null) {
            throw new NullPointerException();
        }
        TypedArray a = context.obtainStyledAttributes(R.styleable.TextView);
        ColorStateList colors = a.getColorStateList(5);
        if (colors == null) {
            int ap = a.getResourceId(SANS, MARQUEE_FADE_NORMAL);
            if (ap != 0) {
                TypedArray appearance = context.obtainStyledAttributes(ap, R.styleable.TextAppearance);
                colors = appearance.getColorStateList(MONOSPACE);
                appearance.recycle();
            }
        }
        a.recycle();
        return colors;
    }

    public static int getTextColor(Context context, TypedArray attrs, int def) {
        ColorStateList colors = getTextColors(context, attrs);
        return colors == null ? def : colors.getDefaultColor();
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        if (KeyEvent.metaStateHasNoModifiers(event.getMetaState() & -28673)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_A /*29*/:
                    if (canSelectText()) {
                        return onTextContextMenuItem(ID_SELECT_ALL);
                    }
                    break;
                case KeyEvent.KEYCODE_C /*31*/:
                    if (canCopy()) {
                        return onTextContextMenuItem(ID_COPY);
                    }
                    break;
                case SpellChecker.MAX_NUMBER_OF_WORDS /*50*/:
                    if (canPaste()) {
                        return onTextContextMenuItem(ID_PASTE);
                    }
                    break;
                case KeyEvent.KEYCODE_X /*52*/:
                    if (canCut()) {
                        return onTextContextMenuItem(ID_CUT);
                    }
                    break;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }

    private boolean canSelectText() {
        return (this.mText.length() == 0 || this.mEditor == null || !this.mEditor.hasSelectionController()) ? DEBUG_EXTRACT : true;
    }

    boolean textCanBeSelected() {
        if (this.mMovement == null || !this.mMovement.canSelectArbitrarily()) {
            return DEBUG_EXTRACT;
        }
        if (isTextEditable() || (isTextSelectable() && (this.mText instanceof Spannable) && isEnabled())) {
            return true;
        }
        return DEBUG_EXTRACT;
    }

    private Locale getTextServicesLocale(boolean allowNullLocale) {
        updateTextServicesLocaleAsync();
        return (this.mCurrentSpellCheckerLocaleCache != null || allowNullLocale) ? this.mCurrentSpellCheckerLocaleCache : Locale.getDefault();
    }

    public Locale getTextServicesLocale() {
        return getTextServicesLocale(DEBUG_EXTRACT);
    }

    public Locale getSpellCheckerLocale() {
        return getTextServicesLocale(true);
    }

    private void updateTextServicesLocaleAsync() {
        AsyncTask.execute(new AnonymousClass3(this));
    }

    private void updateTextServicesLocaleLocked() {
        Locale locale;
        SpellCheckerSubtype subtype = ((TextServicesManager) this.mContext.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE)).getCurrentSpellCheckerSubtype(true);
        if (subtype != null) {
            locale = SpellCheckerSubtype.constructLocaleFromString(subtype.getLocale());
        } else {
            locale = null;
        }
        this.mCurrentSpellCheckerLocaleCache = locale;
    }

    void onLocaleChanged() {
        this.mEditor.mWordIterator = null;
    }

    public WordIterator getWordIterator() {
        if (this.mEditor != null) {
            return this.mEditor.getWordIterator();
        }
        return null;
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        if (!hasPasswordTransformationMethod() || shouldSpeakPasswordsForAccessibility()) {
            CharSequence text = getTextForAccessibility();
            if (!TextUtils.isEmpty(text)) {
                event.getText().add(text);
            }
        }
    }

    private boolean shouldSpeakPasswordsForAccessibility() {
        return Secure.getIntForUser(this.mContext.getContentResolver(), Secure.ACCESSIBILITY_SPEAK_PASSWORD, MARQUEE_FADE_NORMAL, -3) == SANS ? true : DEBUG_EXTRACT;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TextView.class.getName());
        event.setPassword(hasPasswordTransformationMethod());
        if (event.getEventType() == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) {
            event.setFromIndex(Selection.getSelectionStart(this.mText));
            event.setToIndex(Selection.getSelectionEnd(this.mText));
            event.setItemCount(this.mText.length());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TextView.class.getName());
        boolean isPassword = hasPasswordTransformationMethod();
        info.setPassword(isPassword);
        if (!isPassword || shouldSpeakPasswordsForAccessibility()) {
            info.setText(getTextForAccessibility());
        }
        if (this.mBufferType == BufferType.EDITABLE) {
            info.setEditable(true);
        }
        if (this.mEditor != null) {
            info.setInputType(this.mEditor.mInputType);
            if (this.mEditor.mError != null) {
                info.setContentInvalid(true);
                info.setError(this.mEditor.mError);
            }
        }
        if (!TextUtils.isEmpty(this.mText)) {
            info.addAction((int) InputMethodManager.CONTROL_START_INITIAL);
            info.addAction((int) AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY);
            info.setMovementGranularities(31);
        }
        if (isFocused()) {
            if (canSelectText()) {
                info.addAction((int) AccessibilityNodeInfo.ACTION_SET_SELECTION);
            }
            if (canCopy()) {
                info.addAction((int) AccessibilityNodeInfo.ACTION_COPY);
            }
            if (canPaste()) {
                info.addAction((int) AccessibilityNodeInfo.ACTION_PASTE);
            }
            if (canCut()) {
                info.addAction((int) AccessibilityNodeInfo.ACTION_CUT);
            }
        }
        int numFilters = this.mFilters.length;
        for (int i = MARQUEE_FADE_NORMAL; i < numFilters; i += SANS) {
            InputFilter filter = this.mFilters[i];
            if (filter instanceof LengthFilter) {
                info.setMaxTextLength(((LengthFilter) filter).getMax());
            }
        }
        if (!isSingleLine()) {
            info.setMultiLine(true);
        }
    }

    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        switch (action) {
            case RelativeLayout.START_OF /*16*/:
                boolean handled = DEBUG_EXTRACT;
                if (isClickable() || isLongClickable()) {
                    if (isFocusable() && !isFocused()) {
                        requestFocus();
                    }
                    performClick();
                    handled = true;
                }
                if ((this.mMovement == null && !onCheckIsTextEditor()) || !isEnabled() || !(this.mText instanceof Spannable) || this.mLayout == null) {
                    return handled;
                }
                if ((!isTextEditable() && !isTextSelectable()) || !isFocused()) {
                    return handled;
                }
                InputMethodManager imm = InputMethodManager.peekInstance();
                viewClicked(imm);
                if (isTextSelectable() || !this.mEditor.mShowSoftInputOnFocus || imm == null) {
                    return handled;
                }
                return handled | imm.showSoftInput(this, MARQUEE_FADE_NORMAL);
            case InputMethodManager.CONTROL_START_INITIAL /*256*/:
            case AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY /*512*/:
                ensureIterableTextForAccessibilitySelectable();
                return super.performAccessibilityActionInternal(action, arguments);
            case AccessibilityNodeInfo.ACTION_COPY /*16384*/:
                if (isFocused() && canCopy() && onTextContextMenuItem(ID_COPY)) {
                    return true;
                }
                return DEBUG_EXTRACT;
            case AccessibilityNodeInfo.ACTION_PASTE /*32768*/:
                if (isFocused() && canPaste() && onTextContextMenuItem(ID_PASTE)) {
                    return true;
                }
                return DEBUG_EXTRACT;
            case AccessibilityNodeInfo.ACTION_CUT /*65536*/:
                if (isFocused() && canCut() && onTextContextMenuItem(ID_CUT)) {
                    return true;
                }
                return DEBUG_EXTRACT;
            case AccessibilityNodeInfo.ACTION_SET_SELECTION /*131072*/:
                if (isFocused() && canSelectText()) {
                    ensureIterableTextForAccessibilitySelectable();
                    CharSequence text = getIterableTextForAccessibility();
                    if (text == null) {
                        return DEBUG_EXTRACT;
                    }
                    int start;
                    int end;
                    if (arguments != null) {
                        start = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, -1);
                    } else {
                        start = -1;
                    }
                    if (arguments != null) {
                        end = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, -1);
                    } else {
                        end = -1;
                    }
                    if (!(getSelectionStart() == start && getSelectionEnd() == end)) {
                        if (start == end && end == -1) {
                            Selection.removeSelection((Spannable) text);
                            return true;
                        } else if (start >= 0 && start <= end && end <= text.length()) {
                            Selection.setSelection((Spannable) text, start, end);
                            if (this.mEditor == null) {
                                return true;
                            }
                            this.mEditor.startSelectionActionMode();
                            return true;
                        }
                    }
                }
                return DEBUG_EXTRACT;
            default:
                return super.performAccessibilityActionInternal(action, arguments);
        }
    }

    public void sendAccessibilityEvent(int eventType) {
        if (eventType != AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            super.sendAccessibilityEvent(eventType);
        }
    }

    public CharSequence getTextForAccessibility() {
        CharSequence text = getText();
        if (TextUtils.isEmpty(text)) {
            return getHint();
        }
        return text;
    }

    void sendAccessibilityEventTypeViewTextChanged(CharSequence beforeText, int fromIndex, int removedCount, int addedCount) {
        AccessibilityEvent event = AccessibilityEvent.obtain(16);
        event.setFromIndex(fromIndex);
        event.setRemovedCount(removedCount);
        event.setAddedCount(addedCount);
        event.setBeforeText(beforeText);
        sendAccessibilityEventUnchecked(event);
    }

    public boolean isInputMethodTarget() {
        InputMethodManager imm = InputMethodManager.peekInstance();
        return (imm == null || !imm.isActive(this)) ? DEBUG_EXTRACT : true;
    }

    public boolean onTextContextMenuItem(int id) {
        int min = MARQUEE_FADE_NORMAL;
        int max = this.mText.length();
        if (isFocused()) {
            int selStart = getSelectionStart();
            int selEnd = getSelectionEnd();
            min = Math.max(MARQUEE_FADE_NORMAL, Math.min(selStart, selEnd));
            max = Math.max(MARQUEE_FADE_NORMAL, Math.max(selStart, selEnd));
        }
        switch (id) {
            case ID_SELECT_ALL /*16908319*/:
                selectAllText();
                return true;
            case ID_CUT /*16908320*/:
                setPrimaryClip(ClipData.newPlainText(null, getTransformedText(min, max)));
                deleteText_internal(min, max);
                stopSelectionActionMode();
                return true;
            case ID_COPY /*16908321*/:
                setPrimaryClip(ClipData.newPlainText(null, getTransformedText(min, max)));
                stopSelectionActionMode();
                return true;
            case ID_PASTE /*16908322*/:
                paste(min, max);
                return true;
            default:
                return DEBUG_EXTRACT;
        }
    }

    CharSequence getTransformedText(int start, int end) {
        return removeSuggestionSpans(this.mTransformed.subSequence(start, end));
    }

    public boolean performLongClick() {
        boolean handled = DEBUG_EXTRACT;
        if (super.performLongClick()) {
            handled = true;
        }
        if (this.mEditor != null) {
            handled |= this.mEditor.performLongClick(handled);
        }
        if (handled) {
            performHapticFeedback(MARQUEE_FADE_NORMAL);
            if (this.mEditor != null) {
                this.mEditor.mDiscardNextActionUp = true;
            }
        }
        return handled;
    }

    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        if (this.mEditor != null) {
            this.mEditor.onScrollChanged();
        }
    }

    public boolean isSuggestionsEnabled() {
        if (this.mEditor == null || (this.mEditor.mInputType & 15) != SANS || (this.mEditor.mInputType & AccessibilityNodeInfo.ACTION_COLLAPSE) > 0) {
            return DEBUG_EXTRACT;
        }
        int variation = this.mEditor.mInputType & 4080;
        if (variation == 0 || variation == 48 || variation == 80 || variation == 64 || variation == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            return true;
        }
        return DEBUG_EXTRACT;
    }

    public void setCustomSelectionActionModeCallback(Callback actionModeCallback) {
        createEditorIfNeeded();
        this.mEditor.mCustomSelectionActionModeCallback = actionModeCallback;
    }

    public Callback getCustomSelectionActionModeCallback() {
        return this.mEditor == null ? null : this.mEditor.mCustomSelectionActionModeCallback;
    }

    protected void stopSelectionActionMode() {
        this.mEditor.stopSelectionActionMode();
    }

    boolean canCut() {
        if (!hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection() && (this.mText instanceof Editable) && this.mEditor != null && this.mEditor.mKeyListener != null) {
            return true;
        }
        return DEBUG_EXTRACT;
    }

    boolean canCopy() {
        if (!hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection() && this.mEditor != null) {
            return true;
        }
        return DEBUG_EXTRACT;
    }

    boolean canPaste() {
        return (!(this.mText instanceof Editable) || this.mEditor == null || this.mEditor.mKeyListener == null || getSelectionStart() < 0 || getSelectionEnd() < 0 || !((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).hasPrimaryClip()) ? DEBUG_EXTRACT : true;
    }

    boolean selectAllText() {
        int length = this.mText.length();
        Selection.setSelection((Spannable) this.mText, MARQUEE_FADE_NORMAL, length);
        return length > 0 ? true : DEBUG_EXTRACT;
    }

    private void paste(int min, int max) {
        ClipData clip = ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
        if (clip != null) {
            boolean didFirst = DEBUG_EXTRACT;
            for (int i = MARQUEE_FADE_NORMAL; i < clip.getItemCount(); i += SANS) {
                CharSequence paste = clip.getItemAt(i).coerceToStyledText(getContext());
                if (paste != null) {
                    if (didFirst) {
                        ((Editable) this.mText).insert(getSelectionEnd(), "\n");
                        ((Editable) this.mText).insert(getSelectionEnd(), paste);
                    } else {
                        Selection.setSelection((Spannable) this.mText, max);
                        ((Editable) this.mText).replace(min, max, paste);
                        didFirst = true;
                    }
                }
            }
            stopSelectionActionMode();
            LAST_CUT_OR_COPY_TIME = 0;
        }
    }

    private void setPrimaryClip(ClipData clip) {
        ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clip);
        LAST_CUT_OR_COPY_TIME = SystemClock.uptimeMillis();
    }

    public int getOffsetForPosition(float x, float y) {
        if (getLayout() == null) {
            return -1;
        }
        return getOffsetAtCoordinate(getLineAtCoordinate(y), x);
    }

    float convertToLocalHorizontalCoordinate(float x) {
        return Math.min((float) ((getWidth() - getTotalPaddingRight()) - 1), Math.max(0.0f, x - ((float) getTotalPaddingLeft()))) + ((float) getScrollX());
    }

    int getLineAtCoordinate(float y) {
        return getLayout().getLineForVertical((int) (Math.min((float) ((getHeight() - getTotalPaddingBottom()) - 1), Math.max(0.0f, y - ((float) getTotalPaddingTop()))) + ((float) getScrollY())));
    }

    private int getOffsetAtCoordinate(int line, float x) {
        return getLayout().getOffsetForHorizontal(line, convertToLocalHorizontalCoordinate(x));
    }

    public boolean onDragEvent(DragEvent event) {
        switch (event.getAction()) {
            case SANS /*1*/:
                boolean z = (this.mEditor == null || !this.mEditor.hasInsertionController()) ? DEBUG_EXTRACT : true;
                return z;
            case SIGNED /*2*/:
                Selection.setSelection((Spannable) this.mText, getOffsetForPosition(event.getX(), event.getY()));
                return true;
            case MONOSPACE /*3*/:
                if (this.mEditor == null) {
                    return true;
                }
                this.mEditor.onDrop(event);
                return true;
            case ReflectionActionWithoutParams.TAG /*5*/:
                requestFocus();
                return true;
            default:
                return true;
        }
    }

    boolean isInBatchEditMode() {
        if (this.mEditor == null) {
            return DEBUG_EXTRACT;
        }
        InputMethodState ims = this.mEditor.mInputMethodState;
        if (ims == null) {
            return this.mEditor.mInBatchEditControllers;
        }
        if (ims.mBatchEditNesting > 0) {
            return true;
        }
        return DEBUG_EXTRACT;
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        this.mTextDir = getTextDirectionHeuristic();
        if (this.mLayout != null) {
            checkForRelayout();
        }
    }

    TextDirectionHeuristic getTextDirectionHeuristic() {
        boolean defaultIsRtl = true;
        if (hasPasswordTransformationMethod()) {
            return TextDirectionHeuristics.LTR;
        }
        if (getLayoutDirection() != SANS) {
            defaultIsRtl = DEBUG_EXTRACT;
        }
        switch (getTextDirection()) {
            case SIGNED /*2*/:
                return TextDirectionHeuristics.ANYRTL_LTR;
            case MONOSPACE /*3*/:
                return TextDirectionHeuristics.LTR;
            case DECIMAL /*4*/:
                return TextDirectionHeuristics.RTL;
            case ReflectionActionWithoutParams.TAG /*5*/:
                return TextDirectionHeuristics.LOCALE;
            default:
                if (defaultIsRtl) {
                    return TextDirectionHeuristics.FIRSTSTRONG_RTL;
                }
                return TextDirectionHeuristics.FIRSTSTRONG_LTR;
        }
    }

    public void onResolveDrawables(int layoutDirection) {
        if (this.mLastLayoutDirection != layoutDirection) {
            this.mLastLayoutDirection = layoutDirection;
            if (this.mDrawables != null) {
                this.mDrawables.resolveWithLayoutDirection(layoutDirection);
            }
        }
    }

    protected void resetResolvedDrawables() {
        super.resetResolvedDrawables();
        this.mLastLayoutDirection = -1;
    }

    protected void viewClicked(InputMethodManager imm) {
        if (imm != null) {
            imm.viewClicked(this);
        }
    }

    protected void deleteText_internal(int start, int end) {
        ((Editable) this.mText).delete(start, end);
    }

    protected void replaceText_internal(int start, int end, CharSequence text) {
        ((Editable) this.mText).replace(start, end, text);
    }

    protected void setSpan_internal(Object span, int start, int end, int flags) {
        ((Editable) this.mText).setSpan(span, start, end, flags);
    }

    protected void setCursorPosition_internal(int start, int end) {
        Selection.setSelection((Editable) this.mText, start, end);
    }

    private void createEditorIfNeeded() {
        if (this.mEditor == null) {
            this.mEditor = new Editor(this);
        }
    }

    public CharSequence getIterableTextForAccessibility() {
        return this.mText;
    }

    private void ensureIterableTextForAccessibilitySelectable() {
        if (!(this.mText instanceof Spannable)) {
            setText(this.mText, BufferType.SPANNABLE);
        }
    }

    public TextSegmentIterator getIteratorForGranularity(int granularity) {
        TextSegmentIterator iterator;
        switch (granularity) {
            case DECIMAL /*4*/:
                Spannable text = (Spannable) getIterableTextForAccessibility();
                if (!(TextUtils.isEmpty(text) || getLayout() == null)) {
                    iterator = LineTextSegmentIterator.getInstance();
                    iterator.initialize(text, getLayout());
                    return iterator;
                }
            case RelativeLayout.START_OF /*16*/:
                if (!(TextUtils.isEmpty((Spannable) getIterableTextForAccessibility()) || getLayout() == null)) {
                    iterator = PageTextSegmentIterator.getInstance();
                    iterator.initialize(this);
                    return iterator;
                }
        }
        return super.getIteratorForGranularity(granularity);
    }

    public int getAccessibilitySelectionStart() {
        return getSelectionStart();
    }

    public boolean isAccessibilitySelectionExtendable() {
        return true;
    }

    public int getAccessibilitySelectionEnd() {
        return getSelectionEnd();
    }

    public void setAccessibilitySelection(int start, int end) {
        if (getAccessibilitySelectionStart() != start || getAccessibilitySelectionEnd() != end) {
            if (this.mEditor != null) {
                this.mEditor.hideControllers();
            }
            CharSequence text = getIterableTextForAccessibility();
            if (Math.min(start, end) < 0 || Math.max(start, end) > text.length()) {
                Selection.removeSelection((Spannable) text);
            } else {
                Selection.setSelection((Spannable) text, start, end);
            }
        }
    }
}
