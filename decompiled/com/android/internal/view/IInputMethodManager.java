package com.android.internal.view;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.text.style.SuggestionSpan;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import java.util.List;

public interface IInputMethodManager extends IInterface {

    public static abstract class Stub extends Binder implements IInputMethodManager {
        private static final String DESCRIPTOR = "com.android.internal.view.IInputMethodManager";
        static final int TRANSACTION_addClient = 6;
        static final int TRANSACTION_finishInput = 9;
        static final int TRANSACTION_getCurrentInputMethodSubtype = 23;
        static final int TRANSACTION_getEnabledInputMethodList = 2;
        static final int TRANSACTION_getEnabledInputMethodSubtypeList = 3;
        static final int TRANSACTION_getInputMethodList = 1;
        static final int TRANSACTION_getInputMethodWindowVisibleHeight = 30;
        static final int TRANSACTION_getLastInputMethodSubtype = 4;
        static final int TRANSACTION_getShortcutInputMethodsAndSubtypes = 5;
        static final int TRANSACTION_hideMySoftInput = 17;
        static final int TRANSACTION_hideSoftInput = 11;
        static final int TRANSACTION_notifySuggestionPicked = 22;
        static final int TRANSACTION_notifyUserAction = 31;
        static final int TRANSACTION_registerSuggestionSpansForNotification = 21;
        static final int TRANSACTION_removeClient = 7;
        static final int TRANSACTION_setAdditionalInputMethodSubtypes = 29;
        static final int TRANSACTION_setCurrentInputMethodSubtype = 24;
        static final int TRANSACTION_setImeWindowStatus = 20;
        static final int TRANSACTION_setInputMethod = 15;
        static final int TRANSACTION_setInputMethodAndSubtype = 16;
        static final int TRANSACTION_setInputMethodEnabled = 28;
        static final int TRANSACTION_shouldOfferSwitchingToNextInputMethod = 27;
        static final int TRANSACTION_showInputMethodAndSubtypeEnablerFromClient = 14;
        static final int TRANSACTION_showInputMethodPickerFromClient = 13;
        static final int TRANSACTION_showMySoftInput = 18;
        static final int TRANSACTION_showSoftInput = 10;
        static final int TRANSACTION_startInput = 8;
        static final int TRANSACTION_switchToLastInputMethod = 25;
        static final int TRANSACTION_switchToNextInputMethod = 26;
        static final int TRANSACTION_updateStatusIcon = 19;
        static final int TRANSACTION_windowGainedFocus = 12;

        private static class Proxy implements IInputMethodManager {
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

            public List<InputMethodInfo> getInputMethodList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getInputMethodList, _data, _reply, 0);
                    _reply.readException();
                    List<InputMethodInfo> _result = _reply.createTypedArrayList(InputMethodInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<InputMethodInfo> getEnabledInputMethodList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getEnabledInputMethodList, _data, _reply, 0);
                    _reply.readException();
                    List<InputMethodInfo> _result = _reply.createTypedArrayList(InputMethodInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String imiId, boolean allowsImplicitlySelectedSubtypes) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(imiId);
                    if (allowsImplicitlySelectedSubtypes) {
                        i = Stub.TRANSACTION_getInputMethodList;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_getEnabledInputMethodSubtypeList, _data, _reply, 0);
                    _reply.readException();
                    List<InputMethodSubtype> _result = _reply.createTypedArrayList(InputMethodSubtype.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public InputMethodSubtype getLastInputMethodSubtype() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    InputMethodSubtype _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getLastInputMethodSubtype, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (InputMethodSubtype) InputMethodSubtype.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List getShortcutInputMethodsAndSubtypes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getShortcutInputMethodsAndSubtypes, _data, _reply, 0);
                    _reply.readException();
                    List _result = _reply.readArrayList(getClass().getClassLoader());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void addClient(IInputMethodClient client, IInputContext inputContext, int uid, int pid) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    if (inputContext != null) {
                        iBinder = inputContext.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    this.mRemote.transact(Stub.TRANSACTION_addClient, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeClient(IInputMethodClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_removeClient, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public InputBindResult startInput(IInputMethodClient client, IInputContext inputContext, EditorInfo attribute, int controlFlags) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    IBinder asBinder;
                    InputBindResult _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (client != null) {
                        asBinder = client.asBinder();
                    } else {
                        asBinder = null;
                    }
                    _data.writeStrongBinder(asBinder);
                    if (inputContext != null) {
                        iBinder = inputContext.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    if (attribute != null) {
                        _data.writeInt(Stub.TRANSACTION_getInputMethodList);
                        attribute.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(controlFlags);
                    this.mRemote.transact(Stub.TRANSACTION_startInput, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (InputBindResult) InputBindResult.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void finishInput(IInputMethodClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_finishInput, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean showSoftInput(IInputMethodClient client, int flags, ResultReceiver resultReceiver) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeInt(flags);
                    if (resultReceiver != null) {
                        _data.writeInt(Stub.TRANSACTION_getInputMethodList);
                        resultReceiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_showSoftInput, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean hideSoftInput(IInputMethodClient client, int flags, ResultReceiver resultReceiver) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeInt(flags);
                    if (resultReceiver != null) {
                        _data.writeInt(Stub.TRANSACTION_getInputMethodList);
                        resultReceiver.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_hideSoftInput, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public InputBindResult windowGainedFocus(IInputMethodClient client, IBinder windowToken, int controlFlags, int softInputMode, int windowFlags, EditorInfo attribute, IInputContext inputContext) throws RemoteException {
                IBinder iBinder = null;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    IBinder asBinder;
                    InputBindResult _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (client != null) {
                        asBinder = client.asBinder();
                    } else {
                        asBinder = null;
                    }
                    _data.writeStrongBinder(asBinder);
                    _data.writeStrongBinder(windowToken);
                    _data.writeInt(controlFlags);
                    _data.writeInt(softInputMode);
                    _data.writeInt(windowFlags);
                    if (attribute != null) {
                        _data.writeInt(Stub.TRANSACTION_getInputMethodList);
                        attribute.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (inputContext != null) {
                        iBinder = inputContext.asBinder();
                    }
                    _data.writeStrongBinder(iBinder);
                    this.mRemote.transact(Stub.TRANSACTION_windowGainedFocus, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (InputBindResult) InputBindResult.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showInputMethodPickerFromClient(IInputMethodClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_showInputMethodPickerFromClient, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showInputMethodAndSubtypeEnablerFromClient(IInputMethodClient client, String topId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeString(topId);
                    this.mRemote.transact(Stub.TRANSACTION_showInputMethodAndSubtypeEnablerFromClient, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setInputMethod(IBinder token, String id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(id);
                    this.mRemote.transact(Stub.TRANSACTION_setInputMethod, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setInputMethodAndSubtype(IBinder token, String id, InputMethodSubtype subtype) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(id);
                    if (subtype != null) {
                        _data.writeInt(Stub.TRANSACTION_getInputMethodList);
                        subtype.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setInputMethodAndSubtype, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void hideMySoftInput(IBinder token, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_hideMySoftInput, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void showMySoftInput(IBinder token, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_showMySoftInput, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void updateStatusIcon(IBinder token, String packageName, int iconId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeString(packageName);
                    _data.writeInt(iconId);
                    this.mRemote.transact(Stub.TRANSACTION_updateStatusIcon, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setImeWindowStatus(IBinder token, int vis, int backDisposition) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    _data.writeInt(vis);
                    _data.writeInt(backDisposition);
                    this.mRemote.transact(Stub.TRANSACTION_setImeWindowStatus, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerSuggestionSpansForNotification(SuggestionSpan[] spans) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(spans, 0);
                    this.mRemote.transact(Stub.TRANSACTION_registerSuggestionSpansForNotification, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean notifySuggestionPicked(SuggestionSpan span, String originalString, int index) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (span != null) {
                        _data.writeInt(Stub.TRANSACTION_getInputMethodList);
                        span.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(originalString);
                    _data.writeInt(index);
                    this.mRemote.transact(Stub.TRANSACTION_notifySuggestionPicked, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public InputMethodSubtype getCurrentInputMethodSubtype() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    InputMethodSubtype _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getCurrentInputMethodSubtype, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (InputMethodSubtype) InputMethodSubtype.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setCurrentInputMethodSubtype(InputMethodSubtype subtype) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (subtype != null) {
                        _data.writeInt(Stub.TRANSACTION_getInputMethodList);
                        subtype.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setCurrentInputMethodSubtype, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean switchToLastInputMethod(IBinder token) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(Stub.TRANSACTION_switchToLastInputMethod, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean switchToNextInputMethod(IBinder token, boolean onlyCurrentIme) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    int i;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    if (onlyCurrentIme) {
                        i = Stub.TRANSACTION_getInputMethodList;
                    } else {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_switchToNextInputMethod, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean shouldOfferSwitchingToNextInputMethod(IBinder token) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(token);
                    this.mRemote.transact(Stub.TRANSACTION_shouldOfferSwitchingToNextInputMethod, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = true;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean setInputMethodEnabled(String id, boolean enabled) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    int i;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    if (enabled) {
                        i = Stub.TRANSACTION_getInputMethodList;
                    } else {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_setInputMethodEnabled, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setAdditionalInputMethodSubtypes(String id, InputMethodSubtype[] subtypes) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(id);
                    _data.writeTypedArray(subtypes, 0);
                    this.mRemote.transact(Stub.TRANSACTION_setAdditionalInputMethodSubtypes, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getInputMethodWindowVisibleHeight() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getInputMethodWindowVisibleHeight, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void notifyUserAction(int sequenceNumber) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(sequenceNumber);
                    this.mRemote.transact(Stub.TRANSACTION_notifyUserAction, _data, null, Stub.TRANSACTION_getInputMethodList);
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInputMethodManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IInputMethodManager)) {
                return new Proxy(obj);
            }
            return (IInputMethodManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            List<InputMethodInfo> _result;
            InputMethodSubtype _result2;
            IInputMethodClient _arg0;
            InputBindResult _result3;
            int _arg1;
            ResultReceiver _arg2;
            boolean _result4;
            switch (code) {
                case TRANSACTION_getInputMethodList /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getInputMethodList();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                case TRANSACTION_getEnabledInputMethodList /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getEnabledInputMethodList();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                case TRANSACTION_getEnabledInputMethodSubtypeList /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    List<InputMethodSubtype> _result5 = getEnabledInputMethodSubtypeList(data.readString(), data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeTypedList(_result5);
                    return true;
                case TRANSACTION_getLastInputMethodSubtype /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getLastInputMethodSubtype();
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(TRANSACTION_getInputMethodList);
                        _result2.writeToParcel(reply, TRANSACTION_getInputMethodList);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_getShortcutInputMethodsAndSubtypes /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    List _result6 = getShortcutInputMethodsAndSubtypes();
                    reply.writeNoException();
                    reply.writeList(_result6);
                    return true;
                case TRANSACTION_addClient /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    addClient(com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder()), com.android.internal.view.IInputContext.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_removeClient /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeClient(com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_startInput /*8*/:
                    EditorInfo _arg22;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                    IInputContext _arg12 = com.android.internal.view.IInputContext.Stub.asInterface(data.readStrongBinder());
                    if (data.readInt() != 0) {
                        _arg22 = (EditorInfo) EditorInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    _result3 = startInput(_arg0, _arg12, _arg22, data.readInt());
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(TRANSACTION_getInputMethodList);
                        _result3.writeToParcel(reply, TRANSACTION_getInputMethodList);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_finishInput /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    finishInput(com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_showSoftInput /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                    _arg1 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg2 = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    _result4 = showSoftInput(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_hideSoftInput /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                    _arg1 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg2 = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    _result4 = hideSoftInput(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_windowGainedFocus /*12*/:
                    EditorInfo _arg5;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder());
                    IBinder _arg13 = data.readStrongBinder();
                    int _arg23 = data.readInt();
                    int _arg3 = data.readInt();
                    int _arg4 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg5 = (EditorInfo) EditorInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    _result3 = windowGainedFocus(_arg0, _arg13, _arg23, _arg3, _arg4, _arg5, com.android.internal.view.IInputContext.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(TRANSACTION_getInputMethodList);
                        _result3.writeToParcel(reply, TRANSACTION_getInputMethodList);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_showInputMethodPickerFromClient /*13*/:
                    data.enforceInterface(DESCRIPTOR);
                    showInputMethodPickerFromClient(com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_showInputMethodAndSubtypeEnablerFromClient /*14*/:
                    data.enforceInterface(DESCRIPTOR);
                    showInputMethodAndSubtypeEnablerFromClient(com.android.internal.view.IInputMethodClient.Stub.asInterface(data.readStrongBinder()), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setInputMethod /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    setInputMethod(data.readStrongBinder(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setInputMethodAndSubtype /*16*/:
                    InputMethodSubtype _arg24;
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg02 = data.readStrongBinder();
                    String _arg14 = data.readString();
                    if (data.readInt() != 0) {
                        _arg24 = (InputMethodSubtype) InputMethodSubtype.CREATOR.createFromParcel(data);
                    } else {
                        _arg24 = null;
                    }
                    setInputMethodAndSubtype(_arg02, _arg14, _arg24);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_hideMySoftInput /*17*/:
                    data.enforceInterface(DESCRIPTOR);
                    hideMySoftInput(data.readStrongBinder(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_showMySoftInput /*18*/:
                    data.enforceInterface(DESCRIPTOR);
                    showMySoftInput(data.readStrongBinder(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_updateStatusIcon /*19*/:
                    data.enforceInterface(DESCRIPTOR);
                    updateStatusIcon(data.readStrongBinder(), data.readString(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setImeWindowStatus /*20*/:
                    data.enforceInterface(DESCRIPTOR);
                    setImeWindowStatus(data.readStrongBinder(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_registerSuggestionSpansForNotification /*21*/:
                    data.enforceInterface(DESCRIPTOR);
                    registerSuggestionSpansForNotification((SuggestionSpan[]) data.createTypedArray(SuggestionSpan.CREATOR));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_notifySuggestionPicked /*22*/:
                    SuggestionSpan _arg03;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (SuggestionSpan) SuggestionSpan.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result4 = notifySuggestionPicked(_arg03, data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_getCurrentInputMethodSubtype /*23*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getCurrentInputMethodSubtype();
                    reply.writeNoException();
                    if (_result2 != null) {
                        reply.writeInt(TRANSACTION_getInputMethodList);
                        _result2.writeToParcel(reply, TRANSACTION_getInputMethodList);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_setCurrentInputMethodSubtype /*24*/:
                    InputMethodSubtype _arg04;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg04 = (InputMethodSubtype) InputMethodSubtype.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    _result4 = setCurrentInputMethodSubtype(_arg04);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_switchToLastInputMethod /*25*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = switchToLastInputMethod(data.readStrongBinder());
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_switchToNextInputMethod /*26*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = switchToNextInputMethod(data.readStrongBinder(), data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_shouldOfferSwitchingToNextInputMethod /*27*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = shouldOfferSwitchingToNextInputMethod(data.readStrongBinder());
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_setInputMethodEnabled /*28*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = setInputMethodEnabled(data.readString(), data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeInt(_result4 ? TRANSACTION_getInputMethodList : 0);
                    return true;
                case TRANSACTION_setAdditionalInputMethodSubtypes /*29*/:
                    data.enforceInterface(DESCRIPTOR);
                    setAdditionalInputMethodSubtypes(data.readString(), (InputMethodSubtype[]) data.createTypedArray(InputMethodSubtype.CREATOR));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getInputMethodWindowVisibleHeight /*30*/:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = getInputMethodWindowVisibleHeight();
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case TRANSACTION_notifyUserAction /*31*/:
                    data.enforceInterface(DESCRIPTOR);
                    notifyUserAction(data.readInt());
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void addClient(IInputMethodClient iInputMethodClient, IInputContext iInputContext, int i, int i2) throws RemoteException;

    void finishInput(IInputMethodClient iInputMethodClient) throws RemoteException;

    InputMethodSubtype getCurrentInputMethodSubtype() throws RemoteException;

    List<InputMethodInfo> getEnabledInputMethodList() throws RemoteException;

    List<InputMethodSubtype> getEnabledInputMethodSubtypeList(String str, boolean z) throws RemoteException;

    List<InputMethodInfo> getInputMethodList() throws RemoteException;

    int getInputMethodWindowVisibleHeight() throws RemoteException;

    InputMethodSubtype getLastInputMethodSubtype() throws RemoteException;

    List getShortcutInputMethodsAndSubtypes() throws RemoteException;

    void hideMySoftInput(IBinder iBinder, int i) throws RemoteException;

    boolean hideSoftInput(IInputMethodClient iInputMethodClient, int i, ResultReceiver resultReceiver) throws RemoteException;

    boolean notifySuggestionPicked(SuggestionSpan suggestionSpan, String str, int i) throws RemoteException;

    void notifyUserAction(int i) throws RemoteException;

    void registerSuggestionSpansForNotification(SuggestionSpan[] suggestionSpanArr) throws RemoteException;

    void removeClient(IInputMethodClient iInputMethodClient) throws RemoteException;

    void setAdditionalInputMethodSubtypes(String str, InputMethodSubtype[] inputMethodSubtypeArr) throws RemoteException;

    boolean setCurrentInputMethodSubtype(InputMethodSubtype inputMethodSubtype) throws RemoteException;

    void setImeWindowStatus(IBinder iBinder, int i, int i2) throws RemoteException;

    void setInputMethod(IBinder iBinder, String str) throws RemoteException;

    void setInputMethodAndSubtype(IBinder iBinder, String str, InputMethodSubtype inputMethodSubtype) throws RemoteException;

    boolean setInputMethodEnabled(String str, boolean z) throws RemoteException;

    boolean shouldOfferSwitchingToNextInputMethod(IBinder iBinder) throws RemoteException;

    void showInputMethodAndSubtypeEnablerFromClient(IInputMethodClient iInputMethodClient, String str) throws RemoteException;

    void showInputMethodPickerFromClient(IInputMethodClient iInputMethodClient) throws RemoteException;

    void showMySoftInput(IBinder iBinder, int i) throws RemoteException;

    boolean showSoftInput(IInputMethodClient iInputMethodClient, int i, ResultReceiver resultReceiver) throws RemoteException;

    InputBindResult startInput(IInputMethodClient iInputMethodClient, IInputContext iInputContext, EditorInfo editorInfo, int i) throws RemoteException;

    boolean switchToLastInputMethod(IBinder iBinder) throws RemoteException;

    boolean switchToNextInputMethod(IBinder iBinder, boolean z) throws RemoteException;

    void updateStatusIcon(IBinder iBinder, String str, int i) throws RemoteException;

    InputBindResult windowGainedFocus(IInputMethodClient iInputMethodClient, IBinder iBinder, int i, int i2, int i3, EditorInfo editorInfo, IInputContext iInputContext) throws RemoteException;
}
