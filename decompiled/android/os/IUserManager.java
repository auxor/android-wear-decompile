package android.os;

import android.content.pm.UserInfo;
import android.graphics.Bitmap;
import java.util.List;

public interface IUserManager extends IInterface {

    public static abstract class Stub extends Binder implements IUserManager {
        private static final String DESCRIPTOR = "android.os.IUserManager";
        static final int TRANSACTION_checkRestrictionsChallenge = 22;
        static final int TRANSACTION_createProfileForUser = 2;
        static final int TRANSACTION_createUser = 1;
        static final int TRANSACTION_getApplicationRestrictions = 19;
        static final int TRANSACTION_getApplicationRestrictionsForUser = 20;
        static final int TRANSACTION_getDefaultGuestRestrictions = 26;
        static final int TRANSACTION_getProfileParent = 10;
        static final int TRANSACTION_getProfiles = 9;
        static final int TRANSACTION_getUserHandle = 14;
        static final int TRANSACTION_getUserIcon = 7;
        static final int TRANSACTION_getUserInfo = 11;
        static final int TRANSACTION_getUserRestrictions = 15;
        static final int TRANSACTION_getUserSerialNumber = 13;
        static final int TRANSACTION_getUsers = 8;
        static final int TRANSACTION_hasRestrictionsChallenge = 23;
        static final int TRANSACTION_hasUserRestriction = 16;
        static final int TRANSACTION_isRestricted = 12;
        static final int TRANSACTION_markGuestForDeletion = 27;
        static final int TRANSACTION_removeRestrictions = 24;
        static final int TRANSACTION_removeUser = 4;
        static final int TRANSACTION_setApplicationRestrictions = 18;
        static final int TRANSACTION_setDefaultGuestRestrictions = 25;
        static final int TRANSACTION_setRestrictionsChallenge = 21;
        static final int TRANSACTION_setUserEnabled = 3;
        static final int TRANSACTION_setUserIcon = 6;
        static final int TRANSACTION_setUserName = 5;
        static final int TRANSACTION_setUserRestrictions = 17;

        private static class Proxy implements IUserManager {
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

            public UserInfo createUser(String name, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    UserInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    this.mRemote.transact(Stub.TRANSACTION_createUser, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (UserInfo) UserInfo.CREATOR.createFromParcel(_reply);
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

            public UserInfo createProfileForUser(String name, int flags, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    UserInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(flags);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_createProfileForUser, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (UserInfo) UserInfo.CREATOR.createFromParcel(_reply);
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

            public void setUserEnabled(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_setUserEnabled, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean removeUser(int userHandle) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_removeUser, _data, _reply, 0);
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

            public void setUserName(int userHandle, String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    _data.writeString(name);
                    this.mRemote.transact(Stub.TRANSACTION_setUserName, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setUserIcon(int userHandle, Bitmap icon) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    if (icon != null) {
                        _data.writeInt(Stub.TRANSACTION_createUser);
                        icon.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setUserIcon, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bitmap getUserIcon(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bitmap _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_getUserIcon, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bitmap) Bitmap.CREATOR.createFromParcel(_reply);
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

            public List<UserInfo> getUsers(boolean excludeDying) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (excludeDying) {
                        i = Stub.TRANSACTION_createUser;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_getUsers, _data, _reply, 0);
                    _reply.readException();
                    List<UserInfo> _result = _reply.createTypedArrayList(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<UserInfo> getProfiles(int userHandle, boolean enabledOnly) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    if (enabledOnly) {
                        i = Stub.TRANSACTION_createUser;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_getProfiles, _data, _reply, 0);
                    _reply.readException();
                    List<UserInfo> _result = _reply.createTypedArrayList(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public UserInfo getProfileParent(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    UserInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_getProfileParent, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (UserInfo) UserInfo.CREATOR.createFromParcel(_reply);
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

            public UserInfo getUserInfo(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    UserInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_getUserInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (UserInfo) UserInfo.CREATOR.createFromParcel(_reply);
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

            public boolean isRestricted() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isRestricted, _data, _reply, 0);
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

            public int getUserSerialNumber(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_getUserSerialNumber, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getUserHandle(int userSerialNumber) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userSerialNumber);
                    this.mRemote.transact(Stub.TRANSACTION_getUserHandle, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getUserRestrictions(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_getUserRestrictions, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
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

            public boolean hasUserRestriction(String restrictionKey, int userHandle) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(restrictionKey);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_hasUserRestriction, _data, _reply, 0);
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

            public void setUserRestrictions(Bundle restrictions, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (restrictions != null) {
                        _data.writeInt(Stub.TRANSACTION_createUser);
                        restrictions.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_setUserRestrictions, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setApplicationRestrictions(String packageName, Bundle restrictions, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    if (restrictions != null) {
                        _data.writeInt(Stub.TRANSACTION_createUser);
                        restrictions.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_setApplicationRestrictions, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getApplicationRestrictions(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_getApplicationRestrictions, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
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

            public Bundle getApplicationRestrictionsForUser(String packageName, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_getApplicationRestrictionsForUser, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
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

            public boolean setRestrictionsChallenge(String newPin) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(newPin);
                    this.mRemote.transact(Stub.TRANSACTION_setRestrictionsChallenge, _data, _reply, 0);
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

            public int checkRestrictionsChallenge(String pin) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pin);
                    this.mRemote.transact(Stub.TRANSACTION_checkRestrictionsChallenge, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean hasRestrictionsChallenge() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_hasRestrictionsChallenge, _data, _reply, 0);
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

            public void removeRestrictions() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_removeRestrictions, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setDefaultGuestRestrictions(Bundle restrictions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (restrictions != null) {
                        _data.writeInt(Stub.TRANSACTION_createUser);
                        restrictions.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setDefaultGuestRestrictions, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bundle getDefaultGuestRestrictions() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bundle _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDefaultGuestRestrictions, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Bundle) Bundle.CREATOR.createFromParcel(_reply);
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

            public boolean markGuestForDeletion(int userHandle) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(Stub.TRANSACTION_markGuestForDeletion, _data, _reply, 0);
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
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUserManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IUserManager)) {
                return new Proxy(obj);
            }
            return (IUserManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = 0;
            UserInfo _result;
            boolean _result2;
            int _arg0;
            List<UserInfo> _result3;
            int _result4;
            Bundle _result5;
            Bundle _arg02;
            switch (code) {
                case TRANSACTION_createUser /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = createUser(data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_createProfileForUser /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = createProfileForUser(data.readString(), data.readInt(), data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_setUserEnabled /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    setUserEnabled(data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_removeUser /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = removeUser(data.readInt());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_createUser;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_setUserName /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    setUserName(data.readInt(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setUserIcon /*6*/:
                    Bitmap _arg1;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg1 = (Bitmap) Bitmap.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    setUserIcon(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getUserIcon /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    Bitmap _result6 = getUserIcon(data.readInt());
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result6.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getUsers /*8*/:
                    boolean _arg03;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = true;
                    } else {
                        _arg03 = false;
                    }
                    _result3 = getUsers(_arg03);
                    reply.writeNoException();
                    reply.writeTypedList(_result3);
                    return true;
                case TRANSACTION_getProfiles /*9*/:
                    boolean _arg12;
                    data.enforceInterface(DESCRIPTOR);
                    _arg0 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg12 = true;
                    } else {
                        _arg12 = false;
                    }
                    _result3 = getProfiles(_arg0, _arg12);
                    reply.writeNoException();
                    reply.writeTypedList(_result3);
                    return true;
                case TRANSACTION_getProfileParent /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getProfileParent(data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getUserInfo /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getUserInfo(data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_isRestricted /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = isRestricted();
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_createUser;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_getUserSerialNumber /*13*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = getUserSerialNumber(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_getUserHandle /*14*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = getUserHandle(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_getUserRestrictions /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getUserRestrictions(data.readInt());
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result5.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_hasUserRestriction /*16*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = hasUserRestriction(data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_createUser;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_setUserRestrictions /*17*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    setUserRestrictions(_arg02, data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setApplicationRestrictions /*18*/:
                    Bundle _arg13;
                    data.enforceInterface(DESCRIPTOR);
                    String _arg04 = data.readString();
                    if (data.readInt() != 0) {
                        _arg13 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    setApplicationRestrictions(_arg04, _arg13, data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getApplicationRestrictions /*19*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getApplicationRestrictions(data.readString());
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result5.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getApplicationRestrictionsForUser /*20*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getApplicationRestrictionsForUser(data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result5.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_setRestrictionsChallenge /*21*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = setRestrictionsChallenge(data.readString());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_createUser;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_checkRestrictionsChallenge /*22*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = checkRestrictionsChallenge(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_hasRestrictionsChallenge /*23*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = hasRestrictionsChallenge();
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_createUser;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_removeRestrictions /*24*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeRestrictions();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setDefaultGuestRestrictions /*25*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg02 = null;
                    }
                    setDefaultGuestRestrictions(_arg02);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getDefaultGuestRestrictions /*26*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getDefaultGuestRestrictions();
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(TRANSACTION_createUser);
                        _result5.writeToParcel(reply, TRANSACTION_createUser);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_markGuestForDeletion /*27*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = markGuestForDeletion(data.readInt());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_createUser;
                    }
                    reply.writeInt(i);
                    return true;
                case IBinder.INTERFACE_TRANSACTION /*1598968902*/:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    int checkRestrictionsChallenge(String str) throws RemoteException;

    UserInfo createProfileForUser(String str, int i, int i2) throws RemoteException;

    UserInfo createUser(String str, int i) throws RemoteException;

    Bundle getApplicationRestrictions(String str) throws RemoteException;

    Bundle getApplicationRestrictionsForUser(String str, int i) throws RemoteException;

    Bundle getDefaultGuestRestrictions() throws RemoteException;

    UserInfo getProfileParent(int i) throws RemoteException;

    List<UserInfo> getProfiles(int i, boolean z) throws RemoteException;

    int getUserHandle(int i) throws RemoteException;

    Bitmap getUserIcon(int i) throws RemoteException;

    UserInfo getUserInfo(int i) throws RemoteException;

    Bundle getUserRestrictions(int i) throws RemoteException;

    int getUserSerialNumber(int i) throws RemoteException;

    List<UserInfo> getUsers(boolean z) throws RemoteException;

    boolean hasRestrictionsChallenge() throws RemoteException;

    boolean hasUserRestriction(String str, int i) throws RemoteException;

    boolean isRestricted() throws RemoteException;

    boolean markGuestForDeletion(int i) throws RemoteException;

    void removeRestrictions() throws RemoteException;

    boolean removeUser(int i) throws RemoteException;

    void setApplicationRestrictions(String str, Bundle bundle, int i) throws RemoteException;

    void setDefaultGuestRestrictions(Bundle bundle) throws RemoteException;

    boolean setRestrictionsChallenge(String str) throws RemoteException;

    void setUserEnabled(int i) throws RemoteException;

    void setUserIcon(int i, Bitmap bitmap) throws RemoteException;

    void setUserName(int i, String str) throws RemoteException;

    void setUserRestrictions(Bundle bundle, int i) throws RemoteException;
}
