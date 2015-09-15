package android.media.session;

import android.content.Intent;
import android.media.Rating;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ResultReceiver;

public interface ISessionCallback extends IInterface {

    public static abstract class Stub extends Binder implements ISessionCallback {
        private static final String DESCRIPTOR = "android.media.session.ISessionCallback";
        static final int TRANSACTION_onAdjustVolume = 16;
        static final int TRANSACTION_onCommand = 1;
        static final int TRANSACTION_onCustomAction = 15;
        static final int TRANSACTION_onFastForward = 11;
        static final int TRANSACTION_onMediaButton = 2;
        static final int TRANSACTION_onNext = 9;
        static final int TRANSACTION_onPause = 7;
        static final int TRANSACTION_onPlay = 3;
        static final int TRANSACTION_onPlayFromMediaId = 4;
        static final int TRANSACTION_onPlayFromSearch = 5;
        static final int TRANSACTION_onPrevious = 10;
        static final int TRANSACTION_onRate = 14;
        static final int TRANSACTION_onRewind = 12;
        static final int TRANSACTION_onSeekTo = 13;
        static final int TRANSACTION_onSetVolumeTo = 17;
        static final int TRANSACTION_onSkipToTrack = 6;
        static final int TRANSACTION_onStop = 8;

        private static class Proxy implements ISessionCallback {
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

            public void onCommand(String command, Bundle args, ResultReceiver cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(command);
                    if (args != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (cb != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        cb.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onCommand, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onMediaButton(Intent mediaButtonIntent, int sequenceNumber, ResultReceiver cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (mediaButtonIntent != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        mediaButtonIntent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(sequenceNumber);
                    if (cb != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        cb.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onMediaButton, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onPlay() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onPlay, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onPlayFromMediaId(String uri, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(uri);
                    if (extras != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onPlayFromMediaId, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onPlayFromSearch(String query, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(query);
                    if (extras != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onPlayFromSearch, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onSkipToTrack(long id) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(id);
                    this.mRemote.transact(Stub.TRANSACTION_onSkipToTrack, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onPause() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onPause, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onStop() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onStop, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onNext() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onNext, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onPrevious() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onPrevious, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onFastForward() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onFastForward, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onRewind() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_onRewind, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onSeekTo(long pos) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(pos);
                    this.mRemote.transact(Stub.TRANSACTION_onSeekTo, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onRate(Rating rating) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (rating != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        rating.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onRate, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onCustomAction(String action, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(action);
                    if (args != null) {
                        _data.writeInt(Stub.TRANSACTION_onCommand);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_onCustomAction, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onAdjustVolume(int direction) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(direction);
                    this.mRemote.transact(Stub.TRANSACTION_onAdjustVolume, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }

            public void onSetVolumeTo(int value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(value);
                    this.mRemote.transact(Stub.TRANSACTION_onSetVolumeTo, _data, null, Stub.TRANSACTION_onCommand);
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ISessionCallback)) {
                return new Proxy(obj);
            }
            return (ISessionCallback) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String _arg0;
            Bundle _arg1;
            ResultReceiver _arg2;
            switch (code) {
                case TRANSACTION_onCommand /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg2 = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    onCommand(_arg0, _arg1, _arg2);
                    return true;
                case TRANSACTION_onMediaButton /*2*/:
                    Intent _arg02;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = (Intent) Intent.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    int _arg12 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg2 = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    onMediaButton(_arg02, _arg12, _arg2);
                    return true;
                case TRANSACTION_onPlay /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    onPlay();
                    return true;
                case TRANSACTION_onPlayFromMediaId /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    onPlayFromMediaId(_arg0, _arg1);
                    return true;
                case TRANSACTION_onPlayFromSearch /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    onPlayFromSearch(_arg0, _arg1);
                    return true;
                case TRANSACTION_onSkipToTrack /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    onSkipToTrack(data.readLong());
                    return true;
                case TRANSACTION_onPause /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    onPause();
                    return true;
                case TRANSACTION_onStop /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    onStop();
                    return true;
                case TRANSACTION_onNext /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    onNext();
                    return true;
                case TRANSACTION_onPrevious /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    onPrevious();
                    return true;
                case TRANSACTION_onFastForward /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    onFastForward();
                    return true;
                case TRANSACTION_onRewind /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    onRewind();
                    return true;
                case TRANSACTION_onSeekTo /*13*/:
                    data.enforceInterface(DESCRIPTOR);
                    onSeekTo(data.readLong());
                    return true;
                case TRANSACTION_onRate /*14*/:
                    Rating _arg03;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Rating) Rating.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    onRate(_arg03);
                    return true;
                case TRANSACTION_onCustomAction /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readString();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    onCustomAction(_arg0, _arg1);
                    return true;
                case TRANSACTION_onAdjustVolume /*16*/:
                    data.enforceInterface(DESCRIPTOR);
                    onAdjustVolume(data.readInt());
                    return true;
                case TRANSACTION_onSetVolumeTo /*17*/:
                    data.enforceInterface(DESCRIPTOR);
                    onSetVolumeTo(data.readInt());
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void onAdjustVolume(int i) throws RemoteException;

    void onCommand(String str, Bundle bundle, ResultReceiver resultReceiver) throws RemoteException;

    void onCustomAction(String str, Bundle bundle) throws RemoteException;

    void onFastForward() throws RemoteException;

    void onMediaButton(Intent intent, int i, ResultReceiver resultReceiver) throws RemoteException;

    void onNext() throws RemoteException;

    void onPause() throws RemoteException;

    void onPlay() throws RemoteException;

    void onPlayFromMediaId(String str, Bundle bundle) throws RemoteException;

    void onPlayFromSearch(String str, Bundle bundle) throws RemoteException;

    void onPrevious() throws RemoteException;

    void onRate(Rating rating) throws RemoteException;

    void onRewind() throws RemoteException;

    void onSeekTo(long j) throws RemoteException;

    void onSetVolumeTo(int i) throws RemoteException;

    void onSkipToTrack(long j) throws RemoteException;

    void onStop() throws RemoteException;
}
