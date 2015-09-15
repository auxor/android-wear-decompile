package android.app;

import android.content.ComponentName;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

public interface IWallpaperManager extends IInterface {

    public static abstract class Stub extends Binder implements IWallpaperManager {
        private static final String DESCRIPTOR = "android.app.IWallpaperManager";
        static final int TRANSACTION_clearWallpaper = 5;
        static final int TRANSACTION_getHeightHint = 9;
        static final int TRANSACTION_getName = 11;
        static final int TRANSACTION_getWallpaper = 3;
        static final int TRANSACTION_getWallpaperInfo = 4;
        static final int TRANSACTION_getWidthHint = 8;
        static final int TRANSACTION_hasNamedWallpaper = 6;
        static final int TRANSACTION_setDimensionHints = 7;
        static final int TRANSACTION_setDisplayPadding = 10;
        static final int TRANSACTION_setWallpaper = 1;
        static final int TRANSACTION_setWallpaperComponent = 2;
        static final int TRANSACTION_settingsRestored = 12;

        private static class Proxy implements IWallpaperManager {
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

            public ParcelFileDescriptor setWallpaper(String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ParcelFileDescriptor _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(Stub.TRANSACTION_setWallpaper, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
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

            public void setWallpaperComponent(ComponentName name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (name != null) {
                        _data.writeInt(Stub.TRANSACTION_setWallpaper);
                        name.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setWallpaperComponent, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParcelFileDescriptor getWallpaper(IWallpaperManagerCallback cb, Bundle outParams) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ParcelFileDescriptor _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_getWallpaper, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    if (_reply.readInt() != 0) {
                        outParams.readFromParcel(_reply);
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public WallpaperInfo getWallpaperInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    WallpaperInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getWallpaperInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (WallpaperInfo) WallpaperInfo.CREATOR.createFromParcel(_reply);
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

            public void clearWallpaper() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_clearWallpaper, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean hasNamedWallpaper(String name) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    this.mRemote.transact(Stub.TRANSACTION_hasNamedWallpaper, _data, _reply, 0);
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

            public void setDimensionHints(int width, int height) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(width);
                    _data.writeInt(height);
                    this.mRemote.transact(Stub.TRANSACTION_setDimensionHints, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getWidthHint() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getWidthHint, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getHeightHint() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getHeightHint, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDisplayPadding(Rect padding) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (padding != null) {
                        _data.writeInt(Stub.TRANSACTION_setWallpaper);
                        padding.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setDisplayPadding, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getName() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void settingsRestored() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_settingsRestored, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IWallpaperManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IWallpaperManager)) {
                return new Proxy(obj);
            }
            return (IWallpaperManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = 0;
            ParcelFileDescriptor _result;
            int _result2;
            switch (code) {
                case TRANSACTION_setWallpaper /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = setWallpaper(data.readString());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_setWallpaper);
                        _result.writeToParcel(reply, TRANSACTION_setWallpaper);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_setWallpaperComponent /*2*/:
                    ComponentName _arg0;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    setWallpaperComponent(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getWallpaper /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    IWallpaperManagerCallback _arg02 = android.app.IWallpaperManagerCallback.Stub.asInterface(data.readStrongBinder());
                    Bundle _arg1 = new Bundle();
                    _result = getWallpaper(_arg02, _arg1);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_setWallpaper);
                        _result.writeToParcel(reply, TRANSACTION_setWallpaper);
                    } else {
                        reply.writeInt(0);
                    }
                    if (_arg1 != null) {
                        reply.writeInt(TRANSACTION_setWallpaper);
                        _arg1.writeToParcel(reply, TRANSACTION_setWallpaper);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getWallpaperInfo /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    WallpaperInfo _result3 = getWallpaperInfo();
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(TRANSACTION_setWallpaper);
                        _result3.writeToParcel(reply, TRANSACTION_setWallpaper);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_clearWallpaper /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearWallpaper();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_hasNamedWallpaper /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result4 = hasNamedWallpaper(data.readString());
                    reply.writeNoException();
                    if (_result4) {
                        i = TRANSACTION_setWallpaper;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_setDimensionHints /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    setDimensionHints(data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getWidthHint /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getWidthHint();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_getHeightHint /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = getHeightHint();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case TRANSACTION_setDisplayPadding /*10*/:
                    Rect _arg03;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Rect) Rect.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    setDisplayPadding(_arg03);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getName /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    String _result5 = getName();
                    reply.writeNoException();
                    reply.writeString(_result5);
                    return true;
                case TRANSACTION_settingsRestored /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    settingsRestored();
                    reply.writeNoException();
                    return true;
                case IBinder.INTERFACE_TRANSACTION /*1598968902*/:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void clearWallpaper() throws RemoteException;

    int getHeightHint() throws RemoteException;

    String getName() throws RemoteException;

    ParcelFileDescriptor getWallpaper(IWallpaperManagerCallback iWallpaperManagerCallback, Bundle bundle) throws RemoteException;

    WallpaperInfo getWallpaperInfo() throws RemoteException;

    int getWidthHint() throws RemoteException;

    boolean hasNamedWallpaper(String str) throws RemoteException;

    void setDimensionHints(int i, int i2) throws RemoteException;

    void setDisplayPadding(Rect rect) throws RemoteException;

    ParcelFileDescriptor setWallpaper(String str) throws RemoteException;

    void setWallpaperComponent(ComponentName componentName) throws RemoteException;

    void settingsRestored() throws RemoteException;
}
