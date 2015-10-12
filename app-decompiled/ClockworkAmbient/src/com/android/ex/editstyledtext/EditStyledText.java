package com.android.ex.editstyledtext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.NoCopySpan.Concrete;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class EditStyledText extends EditText {
    private static final Concrete SELECTING;
    private static CharSequence STR_CLEARSTYLES;
    private static CharSequence STR_HORIZONTALLINE;
    private static CharSequence STR_PASTE;
    private Drawable mDefaultBackground;
    private ArrayList<EditStyledTextNotifier> mESTNotifiers;
    private InputConnection mInputConnection;
    private EditorManager mManager;

    public class EditModeActions {
        private HashMap<Integer, EditModeActionBase> mActionMap;
        private EditorManager mManager;
        private int mMode;

        public class EditModeActionBase {
            private Object[] mParams;

            protected void addParams(Object[] objArr) {
                this.mParams = objArr;
            }

            protected boolean doEndPosIsSelected() {
                return doStartPosIsSelected();
            }

            protected boolean doNotSelected() {
                return false;
            }

            protected boolean doSelectionIsFixed() {
                return doEndPosIsSelected();
            }

            protected boolean doSelectionIsFixedAndWaitingInput() {
                return doEndPosIsSelected();
            }

            protected boolean doStartPosIsSelected() {
                return doNotSelected();
            }
        }

        private EditModeActionBase getAction(int i) {
            throw new VerifyError("bad dex opcode");
        }

        public boolean doNext(int i) {
            Log.d("EditModeActions", "--- do the next action: " + i + "," + this.mManager.getSelectState());
            EditModeActionBase action = getAction(i);
            if (action == null) {
                Log.e("EditModeActions", "--- invalid action error.");
                return false;
            }
            switch (this.mManager.getSelectState()) {
                case 0:
                    return action.doNotSelected();
                case 1:
                    return action.doStartPosIsSelected();
                case 2:
                    return action.doEndPosIsSelected();
                case 3:
                    return this.mManager.isWaitInput() ? action.doSelectionIsFixedAndWaitingInput() : action.doSelectionIsFixed();
                default:
                    return false;
            }
        }

        public void onAction(int i) {
            onAction(i, null);
        }

        public void onAction(int i, Object[] objArr) {
            getAction(i).addParams(objArr);
            this.mMode = i;
            doNext(i);
        }

        public void onSelectAction() {
            doNext(5);
        }
    }

    public interface EditStyledTextNotifier {
        boolean isButtonsFocused();

        void onStateChanged(int i, int i2);

        boolean sendOnTouchEvent(MotionEvent motionEvent);
    }

    public static class EditStyledTextSpans {

        public static class HorizontalLineDrawable extends ShapeDrawable {
            private static boolean DBG_HL;
            private Spannable mSpannable;
            private int mWidth;

            static {
                DBG_HL = false;
            }

            private HorizontalLineSpan getParentSpan() {
                throw new VerifyError("bad dex opcode");
            }

            private void renewColor() {
                getParentSpan();
                throw new VerifyError("bad dex opcode");
            }

            private void renewColor(int i) {
                if (DBG_HL) {
                    Log.d("EditStyledTextSpan", "--- renewColor:" + i);
                }
                getPaint().setColor(i);
            }

            public void draw(Canvas canvas) {
                renewColor();
                canvas.drawRect(new Rect(0, 9, this.mWidth, 11), getPaint());
            }

            public void renewBounds(int i) {
                if (DBG_HL) {
                    Log.d("EditStyledTextSpan", "--- renewBounds:" + i);
                }
                if (i > 20) {
                    i -= 20;
                }
                this.mWidth = i;
                setBounds(0, 0, i, 20);
            }
        }

        public static class HorizontalLineSpan extends DynamicDrawableSpan {
            HorizontalLineDrawable mDrawable;

            public Drawable getDrawable() {
                return this.mDrawable;
            }

            public void resetWidth(int i) {
                this.mDrawable.renewBounds(i);
            }
        }

        public static class MarqueeSpan extends CharacterStyle {
            private int mMarqueeColor;
            private int mType;

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            private int getMarqueeColor(int r6, int r7) {
                /*
                r5 = this;
                r3 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
                r2 = android.graphics.Color.alpha(r7);
                r0 = android.graphics.Color.red(r7);
                r1 = android.graphics.Color.green(r7);
                r4 = android.graphics.Color.blue(r7);
                if (r2 != 0) goto L_0x0015;
            L_0x0014:
                r2 = r3;
            L_0x0015:
                switch(r6) {
                    case 0: goto L_0x0023;
                    case 1: goto L_0x0031;
                    case 2: goto L_0x001f;
                    default: goto L_0x0018;
                };
            L_0x0018:
                r0 = "EditStyledText";
                r1 = "--- getMarqueeColor: got illigal marquee ID.";
                android.util.Log.e(r0, r1);
            L_0x001f:
                r0 = 16777215; // 0xffffff float:2.3509886E-38 double:8.2890456E-317;
            L_0x0022:
                return r0;
            L_0x0023:
                if (r0 <= r3) goto L_0x002c;
            L_0x0025:
                r0 = r0 / 2;
            L_0x0027:
                r0 = android.graphics.Color.argb(r2, r0, r1, r4);
                goto L_0x0022;
            L_0x002c:
                r0 = 255 - r0;
                r0 = r0 / 2;
                goto L_0x0027;
            L_0x0031:
                if (r1 <= r3) goto L_0x0036;
            L_0x0033:
                r1 = r1 / 2;
                goto L_0x0027;
            L_0x0036:
                r1 = 255 - r1;
                r1 = r1 / 2;
                goto L_0x0027;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.ex.editstyledtext.EditStyledText.EditStyledTextSpans.MarqueeSpan.getMarqueeColor(int, int):int");
            }

            public void resetColor(int i) {
                this.mMarqueeColor = getMarqueeColor(this.mType, i);
            }

            public void updateDrawState(TextPaint textPaint) {
                textPaint.bgColor = this.mMarqueeColor;
            }
        }

        public static class RescalableImageSpan extends ImageSpan {
            private final int MAXWIDTH;
            Uri mContentUri;
            private Context mContext;
            private Drawable mDrawable;
            public int mIntrinsicHeight;
            public int mIntrinsicWidth;

            private void rescaleBigImage(Drawable drawable) {
                Log.d("EditStyledTextSpan", "--- rescaleBigImage:");
                if (this.MAXWIDTH >= 0) {
                    int intrinsicWidth = drawable.getIntrinsicWidth();
                    int intrinsicHeight = drawable.getIntrinsicHeight();
                    Log.d("EditStyledTextSpan", "--- rescaleBigImage:" + intrinsicWidth + "," + intrinsicHeight + "," + this.MAXWIDTH);
                    if (intrinsicWidth > this.MAXWIDTH) {
                        intrinsicWidth = this.MAXWIDTH;
                        intrinsicHeight = (intrinsicHeight * this.MAXWIDTH) / intrinsicWidth;
                    }
                    drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                }
            }

            public Drawable getDrawable() {
                if (this.mDrawable != null) {
                    return this.mDrawable;
                }
                if (this.mContentUri != null) {
                    System.gc();
                    try {
                        Bitmap decodeStream;
                        InputStream openInputStream = this.mContext.getContentResolver().openInputStream(this.mContentUri);
                        Options options = new Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(openInputStream, null, options);
                        openInputStream.close();
                        InputStream openInputStream2 = this.mContext.getContentResolver().openInputStream(this.mContentUri);
                        int i = options.outWidth;
                        int i2 = options.outHeight;
                        this.mIntrinsicWidth = i;
                        this.mIntrinsicHeight = i2;
                        if (options.outWidth > this.MAXWIDTH) {
                            i = this.MAXWIDTH;
                            i2 = (i2 * this.MAXWIDTH) / options.outWidth;
                            decodeStream = BitmapFactory.decodeStream(openInputStream2, new Rect(0, 0, i, i2), null);
                        } else {
                            decodeStream = BitmapFactory.decodeStream(openInputStream2);
                        }
                        this.mDrawable = new BitmapDrawable(this.mContext.getResources(), decodeStream);
                        this.mDrawable.setBounds(0, 0, i, i2);
                        openInputStream2.close();
                    } catch (Throwable e) {
                        Log.e("EditStyledTextSpan", "Failed to loaded content " + this.mContentUri, e);
                        return null;
                    } catch (OutOfMemoryError e2) {
                        Log.e("EditStyledTextSpan", "OutOfMemoryError");
                        return null;
                    }
                }
                this.mDrawable = super.getDrawable();
                rescaleBigImage(this.mDrawable);
                this.mIntrinsicWidth = this.mDrawable.getIntrinsicWidth();
                this.mIntrinsicHeight = this.mDrawable.getIntrinsicHeight();
                return this.mDrawable;
            }
        }
    }

    private class EditorManager {
        private EditModeActions mActions;
        private int mBackgroundColor;
        private int mColorWaitInput;
        private BackgroundColorSpan mComposingTextMask;
        private SpannableStringBuilder mCopyBuffer;
        private int mCurEnd;
        private int mCurStart;
        private EditStyledText mEST;
        private boolean mEditFlag;
        private boolean mKeepNonLineSpan;
        private int mMode;
        private int mSizeWaitInput;
        private SoftKeyReceiver mSkr;
        private boolean mSoftKeyBlockFlag;
        private int mState;
        private boolean mTextIsFinishedFlag;
        private boolean mWaitInputFlag;
        final /* synthetic */ EditStyledText this$0;

        private void endEdit() {
            Log.d("EditStyledText.EditorManager", "--- handleCancel");
            this.mMode = 0;
            this.mState = 0;
            this.mEditFlag = false;
            this.mColorWaitInput = 16777215;
            this.mSizeWaitInput = 0;
            this.mWaitInputFlag = false;
            this.mSoftKeyBlockFlag = false;
            this.mKeepNonLineSpan = false;
            this.mTextIsFinishedFlag = false;
            unsetSelect();
            this.mEST.setOnClickListener(null);
            unblockSoftKey();
        }

        private int findLineEnd(Editable editable, int i) {
            int i2 = i;
            while (i2 < editable.length()) {
                if (editable.charAt(i2) == '\n') {
                    i2++;
                    break;
                }
                i2++;
            }
            Log.d("EditStyledText.EditorManager", "--- findLineEnd:" + i + "," + editable.length() + "," + i2);
            return i2;
        }

        private int findLineStart(Editable editable, int i) {
            int i2 = i;
            while (i2 > 0 && editable.charAt(i2 - 1) != '\n') {
                i2--;
            }
            Log.d("EditStyledText.EditorManager", "--- findLineStart:" + i + "," + editable.length() + "," + i2);
            return i2;
        }

        private void fixSelectionAndDoNextAction() {
            Log.d("EditStyledText.EditorManager", "--- handleComplete:" + this.mCurStart + "," + this.mCurEnd);
            if (!this.mEditFlag) {
                return;
            }
            if (this.mCurStart == this.mCurEnd) {
                Log.d("EditStyledText.EditorManager", "--- cancel handle complete:" + this.mCurStart);
                resetEdit();
                return;
            }
            if (this.mState == 2) {
                this.mState = 3;
            }
            this.mActions.doNext(this.mMode);
            EditStyledText.stopSelecting(this.mEST, this.mEST.getText());
        }

        private void handleSelectAll() {
            if (this.mEditFlag) {
                this.mActions.onAction(11);
            }
        }

        private SpannableStringBuilder removeImageChar(SpannableStringBuilder spannableStringBuilder) {
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(spannableStringBuilder);
            throw new VerifyError("bad dex opcode");
        }

        private void resetEdit() {
            endEdit();
            this.mEditFlag = true;
            this.mEST.notifyStateChanged(this.mMode, this.mState);
        }

        private void unsetSelect() {
            Log.d("EditStyledText.EditorManager", "--- offSelect");
            EditStyledText.stopSelecting(this.mEST, this.mEST.getText());
            int selectionStart = this.mEST.getSelectionStart();
            this.mEST.setSelection(selectionStart, selectionStart);
            this.mState = 0;
        }

        public void blockSoftKey() {
            Log.d("EditStyledText.EditorManager", "--- blockSoftKey:");
            hideSoftKey();
            this.mSoftKeyBlockFlag = true;
        }

        public boolean canPaste() {
            return this.mCopyBuffer != null && this.mCopyBuffer.length() > 0 && removeImageChar(this.mCopyBuffer).length() == 0;
        }

        public int getBackgroundColor() {
            return this.mBackgroundColor;
        }

        public int getSelectState() {
            return this.mState;
        }

        public void hideSoftKey() {
            Log.d("EditStyledText.EditorManager", "--- hidesoftkey");
            throw new VerifyError("bad dex opcode");
        }

        public boolean isEditting() {
            return this.mEditFlag;
        }

        public boolean isSoftKeyBlocked() {
            return this.mSoftKeyBlockFlag;
        }

        public boolean isStyledText() {
            throw new VerifyError("bad dex opcode");
        }

        public boolean isWaitInput() {
            return this.mWaitInputFlag;
        }

        public void onAction(int i) {
            onAction(i, true);
        }

        public void onAction(int i, boolean z) {
            this.mActions.onAction(i);
            if (z) {
                this.mEST.notifyStateChanged(this.mMode, this.mState);
            }
        }

        public void onClearStyles() {
            this.mActions.onAction(14);
        }

        public void onCursorMoved() {
            Log.d("EditStyledText.EditorManager", "--- onClickView");
            if (this.mState == 1 || this.mState == 2) {
                this.mActions.onSelectAction();
                this.mEST.notifyStateChanged(this.mMode, this.mState);
            }
        }

        public void onFixSelectedItem() {
            Log.d("EditStyledText.EditorManager", "--- onFixSelectedItem");
            fixSelectionAndDoNextAction();
            this.mEST.notifyStateChanged(this.mMode, this.mState);
        }

        public void onRefreshStyles() {
            Log.d("EditStyledText.EditorManager", "--- onRefreshStyles");
            throw new VerifyError("bad dex opcode");
        }

        public void onStartSelect(boolean z) {
            Log.d("EditStyledText.EditorManager", "--- onClickSelect");
            this.mMode = 5;
            if (this.mState == 0) {
                this.mActions.onSelectAction();
            } else {
                unsetSelect();
                this.mActions.onSelectAction();
            }
            if (z) {
                this.mEST.notifyStateChanged(this.mMode, this.mState);
            }
        }

        public void onStartSelectAll(boolean z) {
            Log.d("EditStyledText.EditorManager", "--- onClickSelectAll");
            handleSelectAll();
            if (z) {
                this.mEST.notifyStateChanged(this.mMode, this.mState);
            }
        }

        public void setBackgroundColor(int i) {
            this.mBackgroundColor = i;
        }

        public void setTextComposingMask(int i, int i2) {
            Log.d("EditStyledText", "--- setTextComposingMask:" + i + "," + i2);
            int min = Math.min(i, i2);
            int max = Math.max(i, i2);
            int foregroundColor = (!isWaitInput() || this.mColorWaitInput == 16777215) ? this.mEST.getForegroundColor(min) : this.mColorWaitInput;
            int backgroundColor = this.mEST.getBackgroundColor();
            Log.d("EditStyledText", "--- fg:" + Integer.toHexString(foregroundColor) + ",bg:" + Integer.toHexString(backgroundColor) + "," + isWaitInput() + "," + "," + this.mMode);
            if (foregroundColor == backgroundColor) {
                foregroundColor = Integer.MIN_VALUE | ((backgroundColor | -16777216) ^ -1);
                if (this.mComposingTextMask == null || this.mComposingTextMask.getBackgroundColor() != foregroundColor) {
                    this.mComposingTextMask = new BackgroundColorSpan(foregroundColor);
                }
                this.mEST.getText().setSpan(this.mComposingTextMask, min, max, 33);
            }
        }

        public void showSoftKey(int i, int i2) {
            Log.d("EditStyledText.EditorManager", "--- showsoftkey");
            throw new VerifyError("bad dex opcode");
        }

        public void unblockSoftKey() {
            Log.d("EditStyledText.EditorManager", "--- unblockSoftKey:");
            this.mSoftKeyBlockFlag = false;
        }

        public void unsetTextComposingMask() {
            Log.d("EditStyledText", "--- unsetTextComposingMask");
            if (this.mComposingTextMask != null) {
                this.mEST.getText().removeSpan(this.mComposingTextMask);
                this.mComposingTextMask = null;
            }
        }

        public void updateSpanNextToCursor(Editable editable, int i, int i2, int i3) {
            Log.d("EditStyledText.EditorManager", "updateSpanNext:" + i + "," + i2 + "," + i3);
            int i4 = i + i3;
            int min = Math.min(i, i4);
            int max = Math.max(i, i4);
            for (Object obj : editable.getSpans(max, max, Object.class)) {
                if ((obj instanceof MarqueeSpan) || (obj instanceof AlignmentSpan)) {
                    int spanStart = editable.getSpanStart(obj);
                    int spanEnd = editable.getSpanEnd(obj);
                    Log.d("EditStyledText.EditorManager", "spantype:" + obj.getClass() + "," + spanEnd);
                    max = ((obj instanceof MarqueeSpan) || (obj instanceof AlignmentSpan)) ? findLineStart(this.mEST.getText(), min) : min;
                    if (max < spanStart && i2 > i3) {
                        editable.removeSpan(obj);
                    } else if (spanStart > min) {
                        editable.setSpan(obj, min, spanEnd, 33);
                    }
                } else if ((obj instanceof HorizontalLineSpan) && editable.getSpanStart(obj) == i4 && i4 > 0 && this.mEST.getText().charAt(i4 - 1) != '\n') {
                    this.mEST.getText().insert(i4, "\n");
                    this.mEST.setSelection(i4);
                }
            }
        }

        public void updateSpanPreviousFromCursor(Editable editable, int i, int i2, int i3) {
            Log.d("EditStyledText.EditorManager", "updateSpanPrevious:" + i + "," + i2 + "," + i3);
            int i4 = i + i3;
            int min = Math.min(i, i4);
            int max = Math.max(i, i4);
            for (Object obj : editable.getSpans(min, min, Object.class)) {
                int spanEnd;
                if ((obj instanceof ForegroundColorSpan) || (obj instanceof AbsoluteSizeSpan) || (obj instanceof MarqueeSpan) || (obj instanceof AlignmentSpan)) {
                    int spanStart = editable.getSpanStart(obj);
                    spanEnd = editable.getSpanEnd(obj);
                    Log.d("EditStyledText.EditorManager", "spantype:" + obj.getClass() + "," + spanStart);
                    min = ((obj instanceof MarqueeSpan) || (obj instanceof AlignmentSpan)) ? findLineEnd(this.mEST.getText(), max) : this.mKeepNonLineSpan ? spanEnd : max;
                    if (spanEnd < min) {
                        Log.d("EditStyledText.EditorManager", "updateSpanPrevious: extend span");
                        editable.setSpan(obj, spanStart, min, 33);
                    }
                } else if (obj instanceof HorizontalLineSpan) {
                    min = editable.getSpanStart(obj);
                    spanEnd = editable.getSpanEnd(obj);
                    if (i2 > i3) {
                        editable.replace(min, spanEnd, "");
                        editable.removeSpan(obj);
                    } else if (spanEnd == i4 && i4 < editable.length() && this.mEST.getText().charAt(i4) != '\n') {
                        this.mEST.getText().insert(i4, "\n");
                    }
                }
            }
        }
    }

    private class MenuHandler implements OnMenuItemClickListener {
        private MenuHandler() {
            throw new VerifyError("bad dex opcode");
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            return EditStyledText.this.onTextContextMenuItem(menuItem.getItemId());
        }
    }

    public static class SavedStyledTextState extends BaseSavedState {
        public int mBackgroundColor;

        SavedStyledTextState(Parcelable parcelable) {
            super(parcelable);
        }

        public String toString() {
            return "EditStyledText.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " bgcolor=" + this.mBackgroundColor + "}";
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mBackgroundColor);
        }
    }

    private static class SoftKeyReceiver extends ResultReceiver {
        EditStyledText mEST;
        int mNewEnd;
        int mNewStart;

        protected void onReceiveResult(int i, Bundle bundle) {
            if (i != 2) {
                Selection.setSelection(this.mEST.getText(), this.mNewStart, this.mNewEnd);
            }
        }
    }

    public static class StyledTextInputConnection extends InputConnectionWrapper {
        EditStyledText mEST;

        public StyledTextInputConnection(InputConnection inputConnection, EditStyledText editStyledText) {
            super(inputConnection, true);
            this.mEST = editStyledText;
        }

        public boolean commitText(CharSequence charSequence, int i) {
            Log.d("EditStyledText", "--- commitText:");
            this.mEST.mManager.unsetTextComposingMask();
            return super.commitText(charSequence, i);
        }

        public boolean finishComposingText() {
            Log.d("EditStyledText", "--- finishcomposing:");
            if (!(this.mEST.isSoftKeyBlocked() || this.mEST.isButtonsFocused() || this.mEST.isEditting())) {
                this.mEST.onEndEdit();
            }
            return super.finishComposingText();
        }
    }

    static {
        SELECTING = new Concrete();
    }

    private void notifyStateChanged(int i, int i2) {
        throw new VerifyError("bad dex opcode");
    }

    private void onRefreshStyles() {
        this.mManager.onRefreshStyles();
    }

    private void sendOnTouchEvent(MotionEvent motionEvent) {
        throw new VerifyError("bad dex opcode");
    }

    private static void stopSelecting(View view, Spannable spannable) {
        spannable.removeSpan(SELECTING);
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mManager != null) {
            this.mManager.onRefreshStyles();
        }
    }

    public int getBackgroundColor() {
        return this.mManager.getBackgroundColor();
    }

    public int getForegroundColor(int i) {
        if (i < 0) {
            return -16777216;
        }
        throw new VerifyError("bad dex opcode");
    }

    public int getSelectState() {
        return this.mManager.getSelectState();
    }

    public boolean isButtonsFocused() {
        throw new VerifyError("bad dex opcode");
    }

    public boolean isEditting() {
        return this.mManager.isEditting();
    }

    public boolean isSoftKeyBlocked() {
        return this.mManager.isSoftKeyBlocked();
    }

    public boolean isStyledText() {
        return this.mManager.isStyledText();
    }

    public void onClearStyles() {
        this.mManager.onClearStyles();
    }

    protected void onCreateContextMenu(ContextMenu contextMenu) {
        super.onCreateContextMenu(contextMenu);
        OnMenuItemClickListener menuHandler = new MenuHandler();
        if (STR_HORIZONTALLINE != null) {
            contextMenu.add(0, 16776961, 0, STR_HORIZONTALLINE).setOnMenuItemClickListener(menuHandler);
        }
        if (isStyledText() && STR_CLEARSTYLES != null) {
            contextMenu.add(0, 16776962, 0, STR_CLEARSTYLES).setOnMenuItemClickListener(menuHandler);
        }
        if (this.mManager.canPaste()) {
            contextMenu.add(0, 16908322, 0, STR_PASTE).setOnMenuItemClickListener(menuHandler).setAlphabeticShortcut('v');
        }
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        this.mInputConnection = new StyledTextInputConnection(super.onCreateInputConnection(editorInfo), this);
        return this.mInputConnection;
    }

    public void onEndEdit() {
        this.mManager.onAction(21);
    }

    public void onFixSelectedItem() {
        this.mManager.onFixSelectedItem();
    }

    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (z) {
            onStartEdit();
        } else if (!isButtonsFocused()) {
            onEndEdit();
        }
    }

    public void onInsertHorizontalLine() {
        this.mManager.onAction(12);
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedStyledTextState) {
            throw new VerifyError("bad dex opcode");
        }
        super.onRestoreInstanceState(parcelable);
    }

    public Parcelable onSaveInstanceState() {
        Parcelable savedStyledTextState = new SavedStyledTextState(super.onSaveInstanceState());
        savedStyledTextState.mBackgroundColor = this.mManager.getBackgroundColor();
        return savedStyledTextState;
    }

    public void onStartCopy() {
        this.mManager.onAction(1);
    }

    public void onStartCut() {
        this.mManager.onAction(7);
    }

    public void onStartEdit() {
        this.mManager.onAction(20);
    }

    public void onStartPaste() {
        this.mManager.onAction(2);
    }

    public void onStartSelect() {
        this.mManager.onStartSelect(true);
    }

    public void onStartSelectAll() {
        this.mManager.onStartSelectAll(true);
    }

    protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (this.mManager != null) {
            this.mManager.updateSpanNextToCursor(getText(), i, i2, i3);
            this.mManager.updateSpanPreviousFromCursor(getText(), i, i2, i3);
            if (i3 > i2) {
                this.mManager.setTextComposingMask(i, i + i3);
            } else if (i2 < i3) {
                this.mManager.unsetTextComposingMask();
            }
            if (this.mManager.isWaitInput()) {
                if (i3 > i2) {
                    this.mManager.onCursorMoved();
                    onFixSelectedItem();
                } else if (i3 < i2) {
                    this.mManager.onAction(22);
                }
            }
        }
        super.onTextChanged(charSequence, i, i2, i3);
    }

    public boolean onTextContextMenuItem(int i) {
        boolean z = getSelectionStart() != getSelectionEnd();
        switch (i) {
            case 16776961:
                onInsertHorizontalLine();
                return true;
            case 16776962:
                onClearStyles();
                return true;
            case 16776963:
                onStartEdit();
                return true;
            case 16776964:
                onEndEdit();
                return true;
            case 16908319:
                onStartSelectAll();
                return true;
            case 16908320:
                if (z) {
                    onStartCut();
                    return true;
                }
                this.mManager.onStartSelectAll(false);
                onStartCut();
                return true;
            case 16908321:
                if (z) {
                    onStartCopy();
                    return true;
                }
                this.mManager.onStartSelectAll(false);
                onStartCopy();
                return true;
            case 16908322:
                onStartPaste();
                return true;
            case 16908328:
                onStartSelect();
                this.mManager.blockSoftKey();
                break;
            case 16908329:
                onFixSelectedItem();
                break;
        }
        return super.onTextContextMenuItem(i);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent;
        if (motionEvent.getAction() == 1) {
            cancelLongPress();
            boolean isEditting = isEditting();
            if (!isEditting) {
                onStartEdit();
            }
            int selectionStart = Selection.getSelectionStart(getText());
            int selectionEnd = Selection.getSelectionEnd(getText());
            onTouchEvent = super.onTouchEvent(motionEvent);
            if (isFocused() && getSelectState() == 0) {
                if (isEditting) {
                    this.mManager.showSoftKey(Selection.getSelectionStart(getText()), Selection.getSelectionEnd(getText()));
                } else {
                    this.mManager.showSoftKey(selectionStart, selectionEnd);
                }
            }
            this.mManager.onCursorMoved();
            this.mManager.unsetTextComposingMask();
        } else {
            onTouchEvent = super.onTouchEvent(motionEvent);
        }
        sendOnTouchEvent(motionEvent);
        return onTouchEvent;
    }

    public void setBackgroundColor(int i) {
        if (i != 16777215) {
            super.setBackgroundColor(i);
        } else {
            setBackgroundDrawable(this.mDefaultBackground);
        }
        this.mManager.setBackgroundColor(i);
        onRefreshStyles();
    }
}
