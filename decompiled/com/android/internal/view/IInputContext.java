package com.android.internal.view;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedTextRequest;

public interface IInputContext extends IInterface {

    public static abstract class Stub extends Binder implements IInputContext {
        private static final String DESCRIPTOR = "com.android.internal.view.IInputContext";
        static final int TRANSACTION_beginBatchEdit = 14;
        static final int TRANSACTION_clearMetaKeyStates = 18;
        static final int TRANSACTION_commitCompletion = 9;
        static final int TRANSACTION_commitCorrection = 10;
        static final int TRANSACTION_commitText = 8;
        static final int TRANSACTION_deleteSurroundingText = 5;
        static final int TRANSACTION_endBatchEdit = 15;
        static final int TRANSACTION_finishComposingText = 7;
        static final int TRANSACTION_getCursorCapsMode = 3;
        static final int TRANSACTION_getExtractedText = 4;
        static final int TRANSACTION_getSelectedText = 21;
        static final int TRANSACTION_getTextAfterCursor = 2;
        static final int TRANSACTION_getTextBeforeCursor = 1;
        static final int TRANSACTION_performContextMenuAction = 13;
        static final int TRANSACTION_performEditorAction = 12;
        static final int TRANSACTION_performPrivateCommand = 19;
        static final int TRANSACTION_reportFullscreenMode = 16;
        static final int TRANSACTION_requestUpdateCursorAnchorInfo = 22;
        static final int TRANSACTION_sendKeyEvent = 17;
        static final int TRANSACTION_setComposingRegion = 20;
        static final int TRANSACTION_setComposingText = 6;
        static final int TRANSACTION_setSelection = 11;

        private static class Proxy implements IInputContext {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void getTextBeforeCursor(int length, int flags, int seq, IInputContextCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(length);
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_getTextBeforeCursor, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void getTextAfterCursor(int length, int flags, int seq, IInputContextCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(length);
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_getTextAfterCursor, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void getCursorCapsMode(int reqModes, int seq, IInputContextCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reqModes);
                    _data.writeInt(seq);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_getCursorCapsMode, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void getExtractedText(ExtractedTextRequest request, int flags, int seq, IInputContextCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(Stub.TRANSACTION_getTextBeforeCursor);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_getExtractedText, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void deleteSurroundingText(int leftLength, int rightLength) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(leftLength);
                    _data.writeInt(rightLength);
                    this.mRemote.transact(Stub.TRANSACTION_deleteSurroundingText, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void setComposingText(CharSequence text, int newCursorPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (text != null) {
                        _data.writeInt(Stub.TRANSACTION_getTextBeforeCursor);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    this.mRemote.transact(Stub.TRANSACTION_setComposingText, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void finishComposingText() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_finishComposingText, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void commitText(CharSequence text, int newCursorPosition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (text != null) {
                        _data.writeInt(Stub.TRANSACTION_getTextBeforeCursor);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    this.mRemote.transact(Stub.TRANSACTION_commitText, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void commitCompletion(CompletionInfo completion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (completion != null) {
                        _data.writeInt(Stub.TRANSACTION_getTextBeforeCursor);
                        completion.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_commitCompletion, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void commitCorrection(CorrectionInfo correction) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (correction != null) {
                        _data.writeInt(Stub.TRANSACTION_getTextBeforeCursor);
                        correction.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_commitCorrection, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void setSelection(int start, int end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    this.mRemote.transact(Stub.TRANSACTION_setSelection, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void performEditorAction(int actionCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(actionCode);
                    this.mRemote.transact(Stub.TRANSACTION_performEditorAction, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void performContextMenuAction(int id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    this.mRemote.transact(Stub.TRANSACTION_performContextMenuAction, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void beginBatchEdit() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_beginBatchEdit, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void endBatchEdit() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_endBatchEdit, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void reportFullscreenMode(boolean enabled) throws RemoteException {
                int i = Stub.TRANSACTION_getTextBeforeCursor;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!enabled) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_reportFullscreenMode, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void sendKeyEvent(KeyEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (event != null) {
                        _data.writeInt(Stub.TRANSACTION_getTextBeforeCursor);
                        event.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_sendKeyEvent, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void clearMetaKeyStates(int states) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(states);
                    this.mRemote.transact(Stub.TRANSACTION_clearMetaKeyStates, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void performPrivateCommand(String action, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    if (data != null) {
                        _data.writeInt(Stub.TRANSACTION_getTextBeforeCursor);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_performPrivateCommand, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void setComposingRegion(int start, int end) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    this.mRemote.transact(Stub.TRANSACTION_setComposingRegion, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void getSelectedText(int flags, int seq, IInputContextCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(flags);
                    _data.writeInt(seq);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_getSelectedText, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }

            public void requestUpdateCursorAnchorInfo(int cursorUpdateMode, int seq, IInputContextCallback callback) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(cursorUpdateMode);
                    _data.writeInt(seq);
                    if (callback != null) {
                        iBinder = callback.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_requestUpdateCursorAnchorInfo, _data, null, Stub.TRANSACTION_getTextBeforeCursor);
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputContext asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IInputContext)) {
                return new Proxy(obj);
            }
            return (IInputContext) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            CharSequence _arg0;
            switch (code) {
                case TRANSACTION_getTextBeforeCursor /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    getTextBeforeCursor(data.readInt(), data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_getTextAfterCursor /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    getTextAfterCursor(data.readInt(), data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_getCursorCapsMode /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    getCursorCapsMode(data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_getExtractedText /*4*/:
                    ExtractedTextRequest _arg02;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = (ExtractedTextRequest) ExtractedTextRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    getExtractedText(_arg02, data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_deleteSurroundingText /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    deleteSurroundingText(data.readInt(), data.readInt());
                    return true;
                case TRANSACTION_setComposingText /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    setComposingText(_arg0, data.readInt());
                    return true;
                case TRANSACTION_finishComposingText /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    finishComposingText();
                    return true;
                case TRANSACTION_commitText /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    commitText(_arg0, data.readInt());
                    return true;
                case TRANSACTION_commitCompletion /*9*/:
                    CompletionInfo _arg03;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (CompletionInfo) CompletionInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    commitCompletion(_arg03);
                    return true;
                case TRANSACTION_commitCorrection /*10*/:
                    CorrectionInfo _arg04;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg04 = (CorrectionInfo) CorrectionInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    commitCorrection(_arg04);
                    return true;
                case TRANSACTION_setSelection /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    setSelection(data.readInt(), data.readInt());
                    return true;
                case TRANSACTION_performEditorAction /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    performEditorAction(data.readInt());
                    return true;
                case TRANSACTION_performContextMenuAction /*13*/:
                    data.enforceInterface(DESCRIPTOR);
                    performContextMenuAction(data.readInt());
                    return true;
                case TRANSACTION_beginBatchEdit /*14*/:
                    data.enforceInterface(DESCRIPTOR);
                    beginBatchEdit();
                    return true;
                case TRANSACTION_endBatchEdit /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    endBatchEdit();
                    return true;
                case TRANSACTION_reportFullscreenMode /*16*/:
                    data.enforceInterface(DESCRIPTOR);
                    reportFullscreenMode(data.readInt() != 0);
                    return true;
                case TRANSACTION_sendKeyEvent /*17*/:
                    KeyEvent _arg05;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (KeyEvent) KeyEvent.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    sendKeyEvent(_arg05);
                    return true;
                case TRANSACTION_clearMetaKeyStates /*18*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearMetaKeyStates(data.readInt());
                    return true;
                case TRANSACTION_performPrivateCommand /*19*/:
                    Bundle _arg1;
                    data.enforceInterface(DESCRIPTOR);
                    String _arg06 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    performPrivateCommand(_arg06, _arg1);
                    return true;
                case TRANSACTION_setComposingRegion /*20*/:
                    data.enforceInterface(DESCRIPTOR);
                    setComposingRegion(data.readInt(), data.readInt());
                    return true;
                case TRANSACTION_getSelectedText /*21*/:
                    data.enforceInterface(DESCRIPTOR);
                    getSelectedText(data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case TRANSACTION_requestUpdateCursorAnchorInfo /*22*/:
                    data.enforceInterface(DESCRIPTOR);
                    requestUpdateCursorAnchorInfo(data.readInt(), data.readInt(), com.android.internal.view.IInputContextCallback.Stub.asInterface(data.readStrongBinder()));
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void beginBatchEdit() throws RemoteException;

    void clearMetaKeyStates(int i) throws RemoteException;

    void commitCompletion(CompletionInfo completionInfo) throws RemoteException;

    void commitCorrection(CorrectionInfo correctionInfo) throws RemoteException;

    void commitText(CharSequence charSequence, int i) throws RemoteException;

    void deleteSurroundingText(int i, int i2) throws RemoteException;

    void endBatchEdit() throws RemoteException;

    void finishComposingText() throws RemoteException;

    void getCursorCapsMode(int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getExtractedText(ExtractedTextRequest extractedTextRequest, int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getSelectedText(int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getTextAfterCursor(int i, int i2, int i3, IInputContextCallback iInputContextCallback) throws RemoteException;

    void getTextBeforeCursor(int i, int i2, int i3, IInputContextCallback iInputContextCallback) throws RemoteException;

    void performContextMenuAction(int i) throws RemoteException;

    void performEditorAction(int i) throws RemoteException;

    void performPrivateCommand(String str, Bundle bundle) throws RemoteException;

    void reportFullscreenMode(boolean z) throws RemoteException;

    void requestUpdateCursorAnchorInfo(int i, int i2, IInputContextCallback iInputContextCallback) throws RemoteException;

    void sendKeyEvent(KeyEvent keyEvent) throws RemoteException;

    void setComposingRegion(int i, int i2) throws RemoteException;

    void setComposingText(CharSequence charSequence, int i) throws RemoteException;

    void setSelection(int i, int i2) throws RemoteException;
}
