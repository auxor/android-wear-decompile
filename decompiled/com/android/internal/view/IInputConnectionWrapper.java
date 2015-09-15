package com.android.internal.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import com.android.internal.view.IInputContext.Stub;
import java.lang.ref.WeakReference;

public class IInputConnectionWrapper extends Stub {
    private static final int DO_BEGIN_BATCH_EDIT = 90;
    private static final int DO_CLEAR_META_KEY_STATES = 130;
    private static final int DO_COMMIT_COMPLETION = 55;
    private static final int DO_COMMIT_CORRECTION = 56;
    private static final int DO_COMMIT_TEXT = 50;
    private static final int DO_DELETE_SURROUNDING_TEXT = 80;
    private static final int DO_END_BATCH_EDIT = 95;
    private static final int DO_FINISH_COMPOSING_TEXT = 65;
    private static final int DO_GET_CURSOR_CAPS_MODE = 30;
    private static final int DO_GET_EXTRACTED_TEXT = 40;
    private static final int DO_GET_SELECTED_TEXT = 25;
    private static final int DO_GET_TEXT_AFTER_CURSOR = 10;
    private static final int DO_GET_TEXT_BEFORE_CURSOR = 20;
    private static final int DO_PERFORM_CONTEXT_MENU_ACTION = 59;
    private static final int DO_PERFORM_EDITOR_ACTION = 58;
    private static final int DO_PERFORM_PRIVATE_COMMAND = 120;
    private static final int DO_REPORT_FULLSCREEN_MODE = 100;
    private static final int DO_REQUEST_UPDATE_CURSOR_ANCHOR_INFO = 140;
    private static final int DO_SEND_KEY_EVENT = 70;
    private static final int DO_SET_COMPOSING_REGION = 63;
    private static final int DO_SET_COMPOSING_TEXT = 60;
    private static final int DO_SET_SELECTION = 57;
    static final String TAG = "IInputConnectionWrapper";
    private Handler mH;
    private WeakReference<InputConnection> mInputConnection;
    private Looper mMainLooper;

    class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            IInputConnectionWrapper.this.executeMessage(msg);
        }
    }

    static class SomeArgs {
        Object arg1;
        Object arg2;
        IInputContextCallback callback;
        int seq;

        SomeArgs() {
        }
    }

    public IInputConnectionWrapper(Looper mainLooper, InputConnection conn) {
        this.mInputConnection = new WeakReference(conn);
        this.mMainLooper = mainLooper;
        this.mH = new MyHandler(this.mMainLooper);
    }

    public boolean isActive() {
        return true;
    }

    public void getTextAfterCursor(int length, int flags, int seq, IInputContextCallback callback) {
        dispatchMessage(obtainMessageIISC(DO_GET_TEXT_AFTER_CURSOR, length, flags, seq, callback));
    }

    public void getTextBeforeCursor(int length, int flags, int seq, IInputContextCallback callback) {
        dispatchMessage(obtainMessageIISC(DO_GET_TEXT_BEFORE_CURSOR, length, flags, seq, callback));
    }

    public void getSelectedText(int flags, int seq, IInputContextCallback callback) {
        dispatchMessage(obtainMessageISC(DO_GET_SELECTED_TEXT, flags, seq, callback));
    }

    public void getCursorCapsMode(int reqModes, int seq, IInputContextCallback callback) {
        dispatchMessage(obtainMessageISC(DO_GET_CURSOR_CAPS_MODE, reqModes, seq, callback));
    }

    public void getExtractedText(ExtractedTextRequest request, int flags, int seq, IInputContextCallback callback) {
        dispatchMessage(obtainMessageIOSC(DO_GET_EXTRACTED_TEXT, flags, request, seq, callback));
    }

    public void commitText(CharSequence text, int newCursorPosition) {
        dispatchMessage(obtainMessageIO(DO_COMMIT_TEXT, newCursorPosition, text));
    }

    public void commitCompletion(CompletionInfo text) {
        dispatchMessage(obtainMessageO(DO_COMMIT_COMPLETION, text));
    }

    public void commitCorrection(CorrectionInfo info) {
        dispatchMessage(obtainMessageO(DO_COMMIT_CORRECTION, info));
    }

    public void setSelection(int start, int end) {
        dispatchMessage(obtainMessageII(DO_SET_SELECTION, start, end));
    }

    public void performEditorAction(int id) {
        dispatchMessage(obtainMessageII(DO_PERFORM_EDITOR_ACTION, id, 0));
    }

    public void performContextMenuAction(int id) {
        dispatchMessage(obtainMessageII(DO_PERFORM_CONTEXT_MENU_ACTION, id, 0));
    }

    public void setComposingRegion(int start, int end) {
        dispatchMessage(obtainMessageII(DO_SET_COMPOSING_REGION, start, end));
    }

    public void setComposingText(CharSequence text, int newCursorPosition) {
        dispatchMessage(obtainMessageIO(DO_SET_COMPOSING_TEXT, newCursorPosition, text));
    }

    public void finishComposingText() {
        dispatchMessage(obtainMessage(DO_FINISH_COMPOSING_TEXT));
    }

    public void sendKeyEvent(KeyEvent event) {
        dispatchMessage(obtainMessageO(DO_SEND_KEY_EVENT, event));
    }

    public void clearMetaKeyStates(int states) {
        dispatchMessage(obtainMessageII(DO_CLEAR_META_KEY_STATES, states, 0));
    }

    public void deleteSurroundingText(int leftLength, int rightLength) {
        dispatchMessage(obtainMessageII(DO_DELETE_SURROUNDING_TEXT, leftLength, rightLength));
    }

    public void beginBatchEdit() {
        dispatchMessage(obtainMessage(DO_BEGIN_BATCH_EDIT));
    }

    public void endBatchEdit() {
        dispatchMessage(obtainMessage(DO_END_BATCH_EDIT));
    }

    public void reportFullscreenMode(boolean enabled) {
        int i;
        if (enabled) {
            i = 1;
        } else {
            i = 0;
        }
        dispatchMessage(obtainMessageII(DO_REPORT_FULLSCREEN_MODE, i, 0));
    }

    public void performPrivateCommand(String action, Bundle data) {
        dispatchMessage(obtainMessageOO(DO_PERFORM_PRIVATE_COMMAND, action, data));
    }

    public void requestUpdateCursorAnchorInfo(int cursorUpdateMode, int seq, IInputContextCallback callback) {
        dispatchMessage(obtainMessageISC(DO_REQUEST_UPDATE_CURSOR_ANCHOR_INFO, cursorUpdateMode, seq, callback));
    }

    void dispatchMessage(Message msg) {
        if (Looper.myLooper() == this.mMainLooper) {
            executeMessage(msg);
            msg.recycle();
            return;
        }
        this.mH.sendMessage(msg);
    }

    void executeMessage(Message msg) {
        boolean z = true;
        SomeArgs args;
        InputConnection ic;
        switch (msg.what) {
            case DO_GET_TEXT_AFTER_CURSOR /*10*/:
                args = msg.obj;
                try {
                    ic = (InputConnection) this.mInputConnection.get();
                    if (ic == null || !isActive()) {
                        Log.w(TAG, "getTextAfterCursor on inactive InputConnection");
                        args.callback.setTextAfterCursor(null, args.seq);
                        return;
                    }
                    args.callback.setTextAfterCursor(ic.getTextAfterCursor(msg.arg1, msg.arg2), args.seq);
                } catch (RemoteException e) {
                    Log.w(TAG, "Got RemoteException calling setTextAfterCursor", e);
                }
            case DO_GET_TEXT_BEFORE_CURSOR /*20*/:
                args = (SomeArgs) msg.obj;
                try {
                    ic = (InputConnection) this.mInputConnection.get();
                    if (ic == null || !isActive()) {
                        Log.w(TAG, "getTextBeforeCursor on inactive InputConnection");
                        args.callback.setTextBeforeCursor(null, args.seq);
                        return;
                    }
                    args.callback.setTextBeforeCursor(ic.getTextBeforeCursor(msg.arg1, msg.arg2), args.seq);
                } catch (RemoteException e2) {
                    Log.w(TAG, "Got RemoteException calling setTextBeforeCursor", e2);
                }
            case DO_GET_SELECTED_TEXT /*25*/:
                args = (SomeArgs) msg.obj;
                try {
                    ic = (InputConnection) this.mInputConnection.get();
                    if (ic == null || !isActive()) {
                        Log.w(TAG, "getSelectedText on inactive InputConnection");
                        args.callback.setSelectedText(null, args.seq);
                        return;
                    }
                    args.callback.setSelectedText(ic.getSelectedText(msg.arg1), args.seq);
                } catch (RemoteException e22) {
                    Log.w(TAG, "Got RemoteException calling setSelectedText", e22);
                }
            case DO_GET_CURSOR_CAPS_MODE /*30*/:
                args = (SomeArgs) msg.obj;
                try {
                    ic = (InputConnection) this.mInputConnection.get();
                    if (ic == null || !isActive()) {
                        Log.w(TAG, "getCursorCapsMode on inactive InputConnection");
                        args.callback.setCursorCapsMode(0, args.seq);
                        return;
                    }
                    args.callback.setCursorCapsMode(ic.getCursorCapsMode(msg.arg1), args.seq);
                } catch (RemoteException e222) {
                    Log.w(TAG, "Got RemoteException calling setCursorCapsMode", e222);
                }
            case DO_GET_EXTRACTED_TEXT /*40*/:
                args = (SomeArgs) msg.obj;
                try {
                    ic = (InputConnection) this.mInputConnection.get();
                    if (ic == null || !isActive()) {
                        Log.w(TAG, "getExtractedText on inactive InputConnection");
                        args.callback.setExtractedText(null, args.seq);
                        return;
                    }
                    args.callback.setExtractedText(ic.getExtractedText((ExtractedTextRequest) args.arg1, msg.arg1), args.seq);
                } catch (RemoteException e2222) {
                    Log.w(TAG, "Got RemoteException calling setExtractedText", e2222);
                }
            case DO_COMMIT_TEXT /*50*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "commitText on inactive InputConnection");
                } else {
                    ic.commitText((CharSequence) msg.obj, msg.arg1);
                }
            case DO_COMMIT_COMPLETION /*55*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "commitCompletion on inactive InputConnection");
                } else {
                    ic.commitCompletion((CompletionInfo) msg.obj);
                }
            case DO_COMMIT_CORRECTION /*56*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "commitCorrection on inactive InputConnection");
                } else {
                    ic.commitCorrection((CorrectionInfo) msg.obj);
                }
            case DO_SET_SELECTION /*57*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "setSelection on inactive InputConnection");
                } else {
                    ic.setSelection(msg.arg1, msg.arg2);
                }
            case DO_PERFORM_EDITOR_ACTION /*58*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "performEditorAction on inactive InputConnection");
                } else {
                    ic.performEditorAction(msg.arg1);
                }
            case DO_PERFORM_CONTEXT_MENU_ACTION /*59*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "performContextMenuAction on inactive InputConnection");
                } else {
                    ic.performContextMenuAction(msg.arg1);
                }
            case DO_SET_COMPOSING_TEXT /*60*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "setComposingText on inactive InputConnection");
                } else {
                    ic.setComposingText((CharSequence) msg.obj, msg.arg1);
                }
            case DO_SET_COMPOSING_REGION /*63*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "setComposingRegion on inactive InputConnection");
                } else {
                    ic.setComposingRegion(msg.arg1, msg.arg2);
                }
            case DO_FINISH_COMPOSING_TEXT /*65*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null) {
                    Log.w(TAG, "finishComposingText on inactive InputConnection");
                } else {
                    ic.finishComposingText();
                }
            case DO_SEND_KEY_EVENT /*70*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "sendKeyEvent on inactive InputConnection");
                } else {
                    ic.sendKeyEvent((KeyEvent) msg.obj);
                }
            case DO_DELETE_SURROUNDING_TEXT /*80*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "deleteSurroundingText on inactive InputConnection");
                } else {
                    ic.deleteSurroundingText(msg.arg1, msg.arg2);
                }
            case DO_BEGIN_BATCH_EDIT /*90*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "beginBatchEdit on inactive InputConnection");
                } else {
                    ic.beginBatchEdit();
                }
            case DO_END_BATCH_EDIT /*95*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "endBatchEdit on inactive InputConnection");
                } else {
                    ic.endBatchEdit();
                }
            case DO_REPORT_FULLSCREEN_MODE /*100*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "showStatusIcon on inactive InputConnection");
                    return;
                }
                if (msg.arg1 != 1) {
                    z = false;
                }
                ic.reportFullscreenMode(z);
            case DO_PERFORM_PRIVATE_COMMAND /*120*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "performPrivateCommand on inactive InputConnection");
                    return;
                }
                args = (SomeArgs) msg.obj;
                ic.performPrivateCommand((String) args.arg1, (Bundle) args.arg2);
            case DO_CLEAR_META_KEY_STATES /*130*/:
                ic = (InputConnection) this.mInputConnection.get();
                if (ic == null || !isActive()) {
                    Log.w(TAG, "clearMetaKeyStates on inactive InputConnection");
                } else {
                    ic.clearMetaKeyStates(msg.arg1);
                }
            case DO_REQUEST_UPDATE_CURSOR_ANCHOR_INFO /*140*/:
                args = (SomeArgs) msg.obj;
                try {
                    ic = (InputConnection) this.mInputConnection.get();
                    if (ic == null || !isActive()) {
                        Log.w(TAG, "requestCursorAnchorInfo on inactive InputConnection");
                        args.callback.setRequestUpdateCursorAnchorInfoResult(false, args.seq);
                        return;
                    }
                    args.callback.setRequestUpdateCursorAnchorInfoResult(ic.requestCursorUpdates(msg.arg1), args.seq);
                } catch (RemoteException e22222) {
                    Log.w(TAG, "Got RemoteException calling requestCursorAnchorInfo", e22222);
                }
            default:
                Log.w(TAG, "Unhandled message code: " + msg.what);
        }
    }

    Message obtainMessage(int what) {
        return this.mH.obtainMessage(what);
    }

    Message obtainMessageII(int what, int arg1, int arg2) {
        return this.mH.obtainMessage(what, arg1, arg2);
    }

    Message obtainMessageO(int what, Object arg1) {
        return this.mH.obtainMessage(what, 0, 0, arg1);
    }

    Message obtainMessageISC(int what, int arg1, int seq, IInputContextCallback callback) {
        SomeArgs args = new SomeArgs();
        args.callback = callback;
        args.seq = seq;
        return this.mH.obtainMessage(what, arg1, 0, args);
    }

    Message obtainMessageIISC(int what, int arg1, int arg2, int seq, IInputContextCallback callback) {
        SomeArgs args = new SomeArgs();
        args.callback = callback;
        args.seq = seq;
        return this.mH.obtainMessage(what, arg1, arg2, args);
    }

    Message obtainMessageOSC(int what, Object arg1, int seq, IInputContextCallback callback) {
        SomeArgs args = new SomeArgs();
        args.arg1 = arg1;
        args.callback = callback;
        args.seq = seq;
        return this.mH.obtainMessage(what, 0, 0, args);
    }

    Message obtainMessageIOSC(int what, int arg1, Object arg2, int seq, IInputContextCallback callback) {
        SomeArgs args = new SomeArgs();
        args.arg1 = arg2;
        args.callback = callback;
        args.seq = seq;
        return this.mH.obtainMessage(what, arg1, 0, args);
    }

    Message obtainMessageIO(int what, int arg1, Object arg2) {
        return this.mH.obtainMessage(what, arg1, 0, arg2);
    }

    Message obtainMessageOO(int what, Object arg1, Object arg2) {
        SomeArgs args = new SomeArgs();
        args.arg1 = arg1;
        args.arg2 = arg2;
        return this.mH.obtainMessage(what, 0, 0, args);
    }
}
