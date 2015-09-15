package android.net;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Messenger;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;

public interface IConnectivityManager extends IInterface {

    public static abstract class Stub extends Binder implements IConnectivityManager {
        private static final String DESCRIPTOR = "android.net.IConnectivityManager";
        static final int TRANSACTION_addVpnAddress = 62;
        static final int TRANSACTION_captivePortalCheckCompleted = 44;
        static final int TRANSACTION_checkMobileProvisioning = 47;
        static final int TRANSACTION_establishVpn = 39;
        static final int TRANSACTION_findConnectionTypeForIface = 46;
        static final int TRANSACTION_getActiveLinkProperties = 11;
        static final int TRANSACTION_getActiveNetworkInfo = 1;
        static final int TRANSACTION_getActiveNetworkInfoForUid = 2;
        static final int TRANSACTION_getActiveNetworkQuotaInfo = 16;
        static final int TRANSACTION_getAllNetworkInfo = 5;
        static final int TRANSACTION_getAllNetworkState = 15;
        static final int TRANSACTION_getAllNetworks = 7;
        static final int TRANSACTION_getDefaultNetworkCapabilitiesForUser = 8;
        static final int TRANSACTION_getDefaultProxy = 35;
        static final int TRANSACTION_getGlobalProxy = 33;
        static final int TRANSACTION_getLastTetherError = 21;
        static final int TRANSACTION_getLegacyVpnInfo = 42;
        static final int TRANSACTION_getLinkProperties = 13;
        static final int TRANSACTION_getLinkPropertiesForType = 12;
        static final int TRANSACTION_getMobileProvisioningUrl = 48;
        static final int TRANSACTION_getMobileRedirectedProvisioningUrl = 49;
        static final int TRANSACTION_getNetworkCapabilities = 14;
        static final int TRANSACTION_getNetworkForType = 6;
        static final int TRANSACTION_getNetworkInfo = 3;
        static final int TRANSACTION_getNetworkInfoForNetwork = 4;
        static final int TRANSACTION_getProvisioningOrActiveNetworkInfo = 9;
        static final int TRANSACTION_getRestoreDefaultNetworkDelay = 61;
        static final int TRANSACTION_getTetherableBluetoothRegexs = 29;
        static final int TRANSACTION_getTetherableIfaces = 23;
        static final int TRANSACTION_getTetherableUsbRegexs = 27;
        static final int TRANSACTION_getTetherableWifiRegexs = 28;
        static final int TRANSACTION_getTetheredDhcpRanges = 26;
        static final int TRANSACTION_getTetheredIfaces = 24;
        static final int TRANSACTION_getTetheringErroredIfaces = 25;
        static final int TRANSACTION_getVpnConfig = 40;
        static final int TRANSACTION_isActiveNetworkMetered = 17;
        static final int TRANSACTION_isNetworkSupported = 10;
        static final int TRANSACTION_isTetheringSupported = 22;
        static final int TRANSACTION_listenForNetwork = 58;
        static final int TRANSACTION_pendingListenForNetwork = 59;
        static final int TRANSACTION_pendingRequestForNetwork = 56;
        static final int TRANSACTION_prepareVpn = 37;
        static final int TRANSACTION_registerNetworkAgent = 54;
        static final int TRANSACTION_registerNetworkFactory = 52;
        static final int TRANSACTION_releaseNetworkRequest = 60;
        static final int TRANSACTION_releasePendingNetworkRequest = 57;
        static final int TRANSACTION_removeVpnAddress = 63;
        static final int TRANSACTION_reportBadNetwork = 32;
        static final int TRANSACTION_reportInetCondition = 31;
        static final int TRANSACTION_requestNetwork = 55;
        static final int TRANSACTION_requestRouteToHostAddress = 18;
        static final int TRANSACTION_setAirplaneMode = 51;
        static final int TRANSACTION_setDataDependency = 36;
        static final int TRANSACTION_setGlobalProxy = 34;
        static final int TRANSACTION_setProvisioningNotificationVisible = 50;
        static final int TRANSACTION_setUnderlyingNetworksForVpn = 64;
        static final int TRANSACTION_setUsbTethering = 30;
        static final int TRANSACTION_setVpnPackageAuthorization = 38;
        static final int TRANSACTION_startLegacyVpn = 41;
        static final int TRANSACTION_supplyMessenger = 45;
        static final int TRANSACTION_tether = 19;
        static final int TRANSACTION_unregisterNetworkFactory = 53;
        static final int TRANSACTION_untether = 20;
        static final int TRANSACTION_updateLockdownVpn = 43;

        private static class Proxy implements IConnectivityManager {
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

            public NetworkInfo getActiveNetworkInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getActiveNetworkInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(_reply);
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

            public NetworkInfo getActiveNetworkInfoForUid(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    this.mRemote.transact(Stub.TRANSACTION_getActiveNetworkInfoForUid, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(_reply);
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

            public NetworkInfo getNetworkInfo(int networkType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(Stub.TRANSACTION_getNetworkInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(_reply);
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

            public NetworkInfo getNetworkInfoForNetwork(Network network) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (network != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        network.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getNetworkInfoForNetwork, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(_reply);
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

            public NetworkInfo[] getAllNetworkInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAllNetworkInfo, _data, _reply, 0);
                    _reply.readException();
                    NetworkInfo[] _result = (NetworkInfo[]) _reply.createTypedArray(NetworkInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Network getNetworkForType(int networkType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Network _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(Stub.TRANSACTION_getNetworkForType, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Network) Network.CREATOR.createFromParcel(_reply);
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

            public Network[] getAllNetworks() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAllNetworks, _data, _reply, 0);
                    _reply.readException();
                    Network[] _result = (Network[]) _reply.createTypedArray(Network.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(Stub.TRANSACTION_getDefaultNetworkCapabilitiesForUser, _data, _reply, 0);
                    _reply.readException();
                    NetworkCapabilities[] _result = (NetworkCapabilities[]) _reply.createTypedArray(NetworkCapabilities.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NetworkInfo getProvisioningOrActiveNetworkInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getProvisioningOrActiveNetworkInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(_reply);
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

            public boolean isNetworkSupported(int networkType) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(Stub.TRANSACTION_isNetworkSupported, _data, _reply, 0);
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

            public LinkProperties getActiveLinkProperties() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    LinkProperties _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getActiveLinkProperties, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (LinkProperties) LinkProperties.CREATOR.createFromParcel(_reply);
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

            public LinkProperties getLinkPropertiesForType(int networkType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    LinkProperties _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(Stub.TRANSACTION_getLinkPropertiesForType, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (LinkProperties) LinkProperties.CREATOR.createFromParcel(_reply);
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

            public LinkProperties getLinkProperties(Network network) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    LinkProperties _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (network != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        network.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getLinkProperties, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (LinkProperties) LinkProperties.CREATOR.createFromParcel(_reply);
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

            public NetworkCapabilities getNetworkCapabilities(Network network) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkCapabilities _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (network != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        network.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getNetworkCapabilities, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkCapabilities) NetworkCapabilities.CREATOR.createFromParcel(_reply);
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

            public NetworkState[] getAllNetworkState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAllNetworkState, _data, _reply, 0);
                    _reply.readException();
                    NetworkState[] _result = (NetworkState[]) _reply.createTypedArray(NetworkState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NetworkQuotaInfo getActiveNetworkQuotaInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkQuotaInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getActiveNetworkQuotaInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkQuotaInfo) NetworkQuotaInfo.CREATOR.createFromParcel(_reply);
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

            public boolean isActiveNetworkMetered() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isActiveNetworkMetered, _data, _reply, 0);
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

            public boolean requestRouteToHostAddress(int networkType, byte[] hostAddress) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeByteArray(hostAddress);
                    this.mRemote.transact(Stub.TRANSACTION_requestRouteToHostAddress, _data, _reply, 0);
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

            public int tether(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(Stub.TRANSACTION_tether, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int untether(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(Stub.TRANSACTION_untether, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getLastTetherError(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(Stub.TRANSACTION_getLastTetherError, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean isTetheringSupported() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_isTetheringSupported, _data, _reply, 0);
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

            public String[] getTetherableIfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTetherableIfaces, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getTetheredIfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTetheredIfaces, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getTetheringErroredIfaces() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTetheringErroredIfaces, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getTetheredDhcpRanges() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTetheredDhcpRanges, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getTetherableUsbRegexs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTetherableUsbRegexs, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getTetherableWifiRegexs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTetherableWifiRegexs, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getTetherableBluetoothRegexs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getTetherableBluetoothRegexs, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int setUsbTethering(boolean enable) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (enable) {
                        i = Stub.TRANSACTION_getActiveNetworkInfo;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_setUsbTethering, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reportInetCondition(int networkType, int percentage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    _data.writeInt(percentage);
                    this.mRemote.transact(Stub.TRANSACTION_reportInetCondition, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reportBadNetwork(Network network) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (network != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        network.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_reportBadNetwork, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ProxyInfo getGlobalProxy() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ProxyInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getGlobalProxy, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ProxyInfo) ProxyInfo.CREATOR.createFromParcel(_reply);
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

            public void setGlobalProxy(ProxyInfo p) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (p != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        p.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setGlobalProxy, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ProxyInfo getDefaultProxy() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ProxyInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getDefaultProxy, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ProxyInfo) ProxyInfo.CREATOR.createFromParcel(_reply);
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

            public void setDataDependency(int networkType, boolean met) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    if (met) {
                        i = Stub.TRANSACTION_getActiveNetworkInfo;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_setDataDependency, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean prepareVpn(String oldPackage, String newPackage) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(oldPackage);
                    _data.writeString(newPackage);
                    this.mRemote.transact(Stub.TRANSACTION_prepareVpn, _data, _reply, 0);
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

            public void setVpnPackageAuthorization(boolean authorized) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (authorized) {
                        i = Stub.TRANSACTION_getActiveNetworkInfo;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_setVpnPackageAuthorization, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public ParcelFileDescriptor establishVpn(VpnConfig config) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ParcelFileDescriptor _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (config != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        config.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_establishVpn, _data, _reply, 0);
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

            public VpnConfig getVpnConfig() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    VpnConfig _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getVpnConfig, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (VpnConfig) VpnConfig.CREATOR.createFromParcel(_reply);
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

            public void startLegacyVpn(VpnProfile profile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (profile != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        profile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_startLegacyVpn, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public LegacyVpnInfo getLegacyVpnInfo() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    LegacyVpnInfo _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getLegacyVpnInfo, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (LegacyVpnInfo) LegacyVpnInfo.CREATOR.createFromParcel(_reply);
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

            public boolean updateLockdownVpn() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_updateLockdownVpn, _data, _reply, 0);
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

            public void captivePortalCheckCompleted(NetworkInfo info, boolean isCaptivePortal) throws RemoteException {
                int i = Stub.TRANSACTION_getActiveNetworkInfo;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (info != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        info.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (!isCaptivePortal) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_captivePortalCheckCompleted, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void supplyMessenger(int networkType, Messenger messenger) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    if (messenger != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        messenger.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_supplyMessenger, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int findConnectionTypeForIface(String iface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(iface);
                    this.mRemote.transact(Stub.TRANSACTION_findConnectionTypeForIface, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int checkMobileProvisioning(int suggestedTimeOutMs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(suggestedTimeOutMs);
                    this.mRemote.transact(Stub.TRANSACTION_checkMobileProvisioning, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getMobileProvisioningUrl() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMobileProvisioningUrl, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getMobileRedirectedProvisioningUrl() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getMobileRedirectedProvisioningUrl, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setProvisioningNotificationVisible(boolean visible, int networkType, String action) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (visible) {
                        i = Stub.TRANSACTION_getActiveNetworkInfo;
                    }
                    _data.writeInt(i);
                    _data.writeInt(networkType);
                    _data.writeString(action);
                    this.mRemote.transact(Stub.TRANSACTION_setProvisioningNotificationVisible, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setAirplaneMode(boolean enable) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (enable) {
                        i = Stub.TRANSACTION_getActiveNetworkInfo;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_setAirplaneMode, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerNetworkFactory(Messenger messenger, String name) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (messenger != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        messenger.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(name);
                    this.mRemote.transact(Stub.TRANSACTION_registerNetworkFactory, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterNetworkFactory(Messenger messenger) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (messenger != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        messenger.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_unregisterNetworkFactory, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerNetworkAgent(Messenger messenger, NetworkInfo ni, LinkProperties lp, NetworkCapabilities nc, int score, NetworkMisc misc) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (messenger != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        messenger.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (ni != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        ni.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (lp != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        lp.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (nc != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        nc.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(score);
                    if (misc != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        misc.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_registerNetworkAgent, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NetworkRequest requestNetwork(NetworkCapabilities networkCapabilities, Messenger messenger, int timeoutSec, IBinder binder, int legacy) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkRequest _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (networkCapabilities != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        networkCapabilities.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (messenger != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        messenger.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(timeoutSec);
                    _data.writeStrongBinder(binder);
                    _data.writeInt(legacy);
                    this.mRemote.transact(Stub.TRANSACTION_requestNetwork, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkRequest) NetworkRequest.CREATOR.createFromParcel(_reply);
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

            public NetworkRequest pendingRequestForNetwork(NetworkCapabilities networkCapabilities, PendingIntent operation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkRequest _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (networkCapabilities != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        networkCapabilities.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (operation != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        operation.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_pendingRequestForNetwork, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkRequest) NetworkRequest.CREATOR.createFromParcel(_reply);
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

            public void releasePendingNetworkRequest(PendingIntent operation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (operation != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        operation.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_releasePendingNetworkRequest, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public NetworkRequest listenForNetwork(NetworkCapabilities networkCapabilities, Messenger messenger, IBinder binder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    NetworkRequest _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (networkCapabilities != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        networkCapabilities.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (messenger != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        messenger.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(binder);
                    this.mRemote.transact(Stub.TRANSACTION_listenForNetwork, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (NetworkRequest) NetworkRequest.CREATOR.createFromParcel(_reply);
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

            public void pendingListenForNetwork(NetworkCapabilities networkCapabilities, PendingIntent operation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (networkCapabilities != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        networkCapabilities.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (operation != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        operation.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_pendingListenForNetwork, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void releaseNetworkRequest(NetworkRequest networkRequest) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (networkRequest != null) {
                        _data.writeInt(Stub.TRANSACTION_getActiveNetworkInfo);
                        networkRequest.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_releaseNetworkRequest, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getRestoreDefaultNetworkDelay(int networkType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(networkType);
                    this.mRemote.transact(Stub.TRANSACTION_getRestoreDefaultNetworkDelay, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean addVpnAddress(String address, int prefixLength) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(prefixLength);
                    this.mRemote.transact(Stub.TRANSACTION_addVpnAddress, _data, _reply, 0);
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

            public boolean removeVpnAddress(String address, int prefixLength) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(address);
                    _data.writeInt(prefixLength);
                    this.mRemote.transact(Stub.TRANSACTION_removeVpnAddress, _data, _reply, 0);
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

            public boolean setUnderlyingNetworksForVpn(Network[] networks) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(networks, 0);
                    this.mRemote.transact(Stub.TRANSACTION_setUnderlyingNetworksForVpn, _data, _reply, 0);
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

        public static IConnectivityManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IConnectivityManager)) {
                return new Proxy(obj);
            }
            return (IConnectivityManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int i = 0;
            NetworkInfo _result;
            Network _arg0;
            boolean _result2;
            LinkProperties _result3;
            int _result4;
            String[] _result5;
            boolean _arg02;
            ProxyInfo _result6;
            int _arg03;
            boolean _arg1;
            Messenger _arg12;
            String _result7;
            Messenger _arg04;
            NetworkCapabilities _arg05;
            NetworkRequest _result8;
            PendingIntent _arg13;
            switch (code) {
                case TRANSACTION_getActiveNetworkInfo /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getActiveNetworkInfo();
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getActiveNetworkInfoForUid /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getActiveNetworkInfoForUid(data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getNetworkInfo /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getNetworkInfo(data.readInt());
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getNetworkInfoForNetwork /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Network) Network.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result = getNetworkInfoForNetwork(_arg0);
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getAllNetworkInfo /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkInfo[] _result9 = getAllNetworkInfo();
                    reply.writeNoException();
                    reply.writeTypedArray(_result9, TRANSACTION_getActiveNetworkInfo);
                    return true;
                case TRANSACTION_getNetworkForType /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    Network _result10 = getNetworkForType(data.readInt());
                    reply.writeNoException();
                    if (_result10 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result10.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getAllNetworks /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    Network[] _result11 = getAllNetworks();
                    reply.writeNoException();
                    reply.writeTypedArray(_result11, TRANSACTION_getActiveNetworkInfo);
                    return true;
                case TRANSACTION_getDefaultNetworkCapabilitiesForUser /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkCapabilities[] _result12 = getDefaultNetworkCapabilitiesForUser(data.readInt());
                    reply.writeNoException();
                    reply.writeTypedArray(_result12, TRANSACTION_getActiveNetworkInfo);
                    return true;
                case TRANSACTION_getProvisioningOrActiveNetworkInfo /*9*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = getProvisioningOrActiveNetworkInfo();
                    reply.writeNoException();
                    if (_result != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_isNetworkSupported /*10*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = isNetworkSupported(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result2 ? TRANSACTION_getActiveNetworkInfo : 0);
                    return true;
                case TRANSACTION_getActiveLinkProperties /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = getActiveLinkProperties();
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result3.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getLinkPropertiesForType /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = getLinkPropertiesForType(data.readInt());
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result3.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getLinkProperties /*13*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Network) Network.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    _result3 = getLinkProperties(_arg0);
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result3.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getNetworkCapabilities /*14*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Network) Network.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    NetworkCapabilities _result13 = getNetworkCapabilities(_arg0);
                    reply.writeNoException();
                    if (_result13 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result13.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getAllNetworkState /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkState[] _result14 = getAllNetworkState();
                    reply.writeNoException();
                    reply.writeTypedArray(_result14, TRANSACTION_getActiveNetworkInfo);
                    return true;
                case TRANSACTION_getActiveNetworkQuotaInfo /*16*/:
                    data.enforceInterface(DESCRIPTOR);
                    NetworkQuotaInfo _result15 = getActiveNetworkQuotaInfo();
                    reply.writeNoException();
                    if (_result15 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result15.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_isActiveNetworkMetered /*17*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = isActiveNetworkMetered();
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_requestRouteToHostAddress /*18*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = requestRouteToHostAddress(data.readInt(), data.createByteArray());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_tether /*19*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = tether(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_untether /*20*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = untether(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_getLastTetherError /*21*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = getLastTetherError(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_isTetheringSupported /*22*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = isTetheringSupported();
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_getTetherableIfaces /*23*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getTetherableIfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case TRANSACTION_getTetheredIfaces /*24*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getTetheredIfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case TRANSACTION_getTetheringErroredIfaces /*25*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getTetheringErroredIfaces();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case TRANSACTION_getTetheredDhcpRanges /*26*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getTetheredDhcpRanges();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case TRANSACTION_getTetherableUsbRegexs /*27*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getTetherableUsbRegexs();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case TRANSACTION_getTetherableWifiRegexs /*28*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getTetherableWifiRegexs();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case TRANSACTION_getTetherableBluetoothRegexs /*29*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result5 = getTetherableBluetoothRegexs();
                    reply.writeNoException();
                    reply.writeStringArray(_result5);
                    return true;
                case TRANSACTION_setUsbTethering /*30*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = true;
                    } else {
                        _arg02 = false;
                    }
                    _result4 = setUsbTethering(_arg02);
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_reportInetCondition /*31*/:
                    data.enforceInterface(DESCRIPTOR);
                    reportInetCondition(data.readInt(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_reportBadNetwork /*32*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Network) Network.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    reportBadNetwork(_arg0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getGlobalProxy /*33*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result6 = getGlobalProxy();
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result6.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_setGlobalProxy /*34*/:
                    ProxyInfo _arg06;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg06 = (ProxyInfo) ProxyInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg06 = null;
                    }
                    setGlobalProxy(_arg06);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getDefaultProxy /*35*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result6 = getDefaultProxy();
                    reply.writeNoException();
                    if (_result6 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result6.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_setDataDependency /*36*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg03 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg1 = true;
                    } else {
                        _arg1 = false;
                    }
                    setDataDependency(_arg03, _arg1);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_prepareVpn /*37*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = prepareVpn(data.readString(), data.readString());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_setVpnPackageAuthorization /*38*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = true;
                    } else {
                        _arg02 = false;
                    }
                    setVpnPackageAuthorization(_arg02);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_establishVpn /*39*/:
                    VpnConfig _arg07;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg07 = (VpnConfig) VpnConfig.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    ParcelFileDescriptor _result16 = establishVpn(_arg07);
                    reply.writeNoException();
                    if (_result16 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result16.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_getVpnConfig /*40*/:
                    data.enforceInterface(DESCRIPTOR);
                    VpnConfig _result17 = getVpnConfig();
                    reply.writeNoException();
                    if (_result17 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result17.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_startLegacyVpn /*41*/:
                    VpnProfile _arg08;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg08 = (VpnProfile) VpnProfile.CREATOR.createFromParcel(data);
                    } else {
                        _arg08 = null;
                    }
                    startLegacyVpn(_arg08);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getLegacyVpnInfo /*42*/:
                    data.enforceInterface(DESCRIPTOR);
                    LegacyVpnInfo _result18 = getLegacyVpnInfo();
                    reply.writeNoException();
                    if (_result18 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result18.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_updateLockdownVpn /*43*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = updateLockdownVpn();
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_captivePortalCheckCompleted /*44*/:
                    NetworkInfo _arg09;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg09 = (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg09 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg1 = true;
                    } else {
                        _arg1 = false;
                    }
                    captivePortalCheckCompleted(_arg09, _arg1);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_supplyMessenger /*45*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg03 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg12 = (Messenger) Messenger.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    supplyMessenger(_arg03, _arg12);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_findConnectionTypeForIface /*46*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = findConnectionTypeForIface(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_checkMobileProvisioning /*47*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = checkMobileProvisioning(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_getMobileProvisioningUrl /*48*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result7 = getMobileProvisioningUrl();
                    reply.writeNoException();
                    reply.writeString(_result7);
                    return true;
                case TRANSACTION_getMobileRedirectedProvisioningUrl /*49*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result7 = getMobileRedirectedProvisioningUrl();
                    reply.writeNoException();
                    reply.writeString(_result7);
                    return true;
                case TRANSACTION_setProvisioningNotificationVisible /*50*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = true;
                    } else {
                        _arg02 = false;
                    }
                    setProvisioningNotificationVisible(_arg02, data.readInt(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setAirplaneMode /*51*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg02 = true;
                    } else {
                        _arg02 = false;
                    }
                    setAirplaneMode(_arg02);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_registerNetworkFactory /*52*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg04 = (Messenger) Messenger.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    registerNetworkFactory(_arg04, data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_unregisterNetworkFactory /*53*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg04 = (Messenger) Messenger.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    unregisterNetworkFactory(_arg04);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_registerNetworkAgent /*54*/:
                    NetworkInfo _arg14;
                    LinkProperties _arg2;
                    NetworkCapabilities _arg3;
                    NetworkMisc _arg5;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg04 = (Messenger) Messenger.CREATOR.createFromParcel(data);
                    } else {
                        _arg04 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg14 = (NetworkInfo) NetworkInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg2 = (LinkProperties) LinkProperties.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg3 = (NetworkCapabilities) NetworkCapabilities.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    int _arg4 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg5 = (NetworkMisc) NetworkMisc.CREATOR.createFromParcel(data);
                    } else {
                        _arg5 = null;
                    }
                    registerNetworkAgent(_arg04, _arg14, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_requestNetwork /*55*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (NetworkCapabilities) NetworkCapabilities.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg12 = (Messenger) Messenger.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    _result8 = requestNetwork(_arg05, _arg12, data.readInt(), data.readStrongBinder(), data.readInt());
                    reply.writeNoException();
                    if (_result8 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result8.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_pendingRequestForNetwork /*56*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (NetworkCapabilities) NetworkCapabilities.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg13 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    _result8 = pendingRequestForNetwork(_arg05, _arg13);
                    reply.writeNoException();
                    if (_result8 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result8.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_releasePendingNetworkRequest /*57*/:
                    PendingIntent _arg010;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg010 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg010 = null;
                    }
                    releasePendingNetworkRequest(_arg010);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_listenForNetwork /*58*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (NetworkCapabilities) NetworkCapabilities.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg12 = (Messenger) Messenger.CREATOR.createFromParcel(data);
                    } else {
                        _arg12 = null;
                    }
                    _result8 = listenForNetwork(_arg05, _arg12, data.readStrongBinder());
                    reply.writeNoException();
                    if (_result8 != null) {
                        reply.writeInt(TRANSACTION_getActiveNetworkInfo);
                        _result8.writeToParcel(reply, TRANSACTION_getActiveNetworkInfo);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case TRANSACTION_pendingListenForNetwork /*59*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (NetworkCapabilities) NetworkCapabilities.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg13 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg13 = null;
                    }
                    pendingListenForNetwork(_arg05, _arg13);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_releaseNetworkRequest /*60*/:
                    NetworkRequest _arg011;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg011 = (NetworkRequest) NetworkRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg011 = null;
                    }
                    releaseNetworkRequest(_arg011);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getRestoreDefaultNetworkDelay /*61*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result4 = getRestoreDefaultNetworkDelay(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result4);
                    return true;
                case TRANSACTION_addVpnAddress /*62*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = addVpnAddress(data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_removeVpnAddress /*63*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = removeVpnAddress(data.readString(), data.readInt());
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
                    }
                    reply.writeInt(i);
                    return true;
                case TRANSACTION_setUnderlyingNetworksForVpn /*64*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result2 = setUnderlyingNetworksForVpn((Network[]) data.createTypedArray(Network.CREATOR));
                    reply.writeNoException();
                    if (_result2) {
                        i = TRANSACTION_getActiveNetworkInfo;
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

    boolean addVpnAddress(String str, int i) throws RemoteException;

    void captivePortalCheckCompleted(NetworkInfo networkInfo, boolean z) throws RemoteException;

    int checkMobileProvisioning(int i) throws RemoteException;

    ParcelFileDescriptor establishVpn(VpnConfig vpnConfig) throws RemoteException;

    int findConnectionTypeForIface(String str) throws RemoteException;

    LinkProperties getActiveLinkProperties() throws RemoteException;

    NetworkInfo getActiveNetworkInfo() throws RemoteException;

    NetworkInfo getActiveNetworkInfoForUid(int i) throws RemoteException;

    NetworkQuotaInfo getActiveNetworkQuotaInfo() throws RemoteException;

    NetworkInfo[] getAllNetworkInfo() throws RemoteException;

    NetworkState[] getAllNetworkState() throws RemoteException;

    Network[] getAllNetworks() throws RemoteException;

    NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(int i) throws RemoteException;

    ProxyInfo getDefaultProxy() throws RemoteException;

    ProxyInfo getGlobalProxy() throws RemoteException;

    int getLastTetherError(String str) throws RemoteException;

    LegacyVpnInfo getLegacyVpnInfo() throws RemoteException;

    LinkProperties getLinkProperties(Network network) throws RemoteException;

    LinkProperties getLinkPropertiesForType(int i) throws RemoteException;

    String getMobileProvisioningUrl() throws RemoteException;

    String getMobileRedirectedProvisioningUrl() throws RemoteException;

    NetworkCapabilities getNetworkCapabilities(Network network) throws RemoteException;

    Network getNetworkForType(int i) throws RemoteException;

    NetworkInfo getNetworkInfo(int i) throws RemoteException;

    NetworkInfo getNetworkInfoForNetwork(Network network) throws RemoteException;

    NetworkInfo getProvisioningOrActiveNetworkInfo() throws RemoteException;

    int getRestoreDefaultNetworkDelay(int i) throws RemoteException;

    String[] getTetherableBluetoothRegexs() throws RemoteException;

    String[] getTetherableIfaces() throws RemoteException;

    String[] getTetherableUsbRegexs() throws RemoteException;

    String[] getTetherableWifiRegexs() throws RemoteException;

    String[] getTetheredDhcpRanges() throws RemoteException;

    String[] getTetheredIfaces() throws RemoteException;

    String[] getTetheringErroredIfaces() throws RemoteException;

    VpnConfig getVpnConfig() throws RemoteException;

    boolean isActiveNetworkMetered() throws RemoteException;

    boolean isNetworkSupported(int i) throws RemoteException;

    boolean isTetheringSupported() throws RemoteException;

    NetworkRequest listenForNetwork(NetworkCapabilities networkCapabilities, Messenger messenger, IBinder iBinder) throws RemoteException;

    void pendingListenForNetwork(NetworkCapabilities networkCapabilities, PendingIntent pendingIntent) throws RemoteException;

    NetworkRequest pendingRequestForNetwork(NetworkCapabilities networkCapabilities, PendingIntent pendingIntent) throws RemoteException;

    boolean prepareVpn(String str, String str2) throws RemoteException;

    void registerNetworkAgent(Messenger messenger, NetworkInfo networkInfo, LinkProperties linkProperties, NetworkCapabilities networkCapabilities, int i, NetworkMisc networkMisc) throws RemoteException;

    void registerNetworkFactory(Messenger messenger, String str) throws RemoteException;

    void releaseNetworkRequest(NetworkRequest networkRequest) throws RemoteException;

    void releasePendingNetworkRequest(PendingIntent pendingIntent) throws RemoteException;

    boolean removeVpnAddress(String str, int i) throws RemoteException;

    void reportBadNetwork(Network network) throws RemoteException;

    void reportInetCondition(int i, int i2) throws RemoteException;

    NetworkRequest requestNetwork(NetworkCapabilities networkCapabilities, Messenger messenger, int i, IBinder iBinder, int i2) throws RemoteException;

    boolean requestRouteToHostAddress(int i, byte[] bArr) throws RemoteException;

    void setAirplaneMode(boolean z) throws RemoteException;

    void setDataDependency(int i, boolean z) throws RemoteException;

    void setGlobalProxy(ProxyInfo proxyInfo) throws RemoteException;

    void setProvisioningNotificationVisible(boolean z, int i, String str) throws RemoteException;

    boolean setUnderlyingNetworksForVpn(Network[] networkArr) throws RemoteException;

    int setUsbTethering(boolean z) throws RemoteException;

    void setVpnPackageAuthorization(boolean z) throws RemoteException;

    void startLegacyVpn(VpnProfile vpnProfile) throws RemoteException;

    void supplyMessenger(int i, Messenger messenger) throws RemoteException;

    int tether(String str) throws RemoteException;

    void unregisterNetworkFactory(Messenger messenger) throws RemoteException;

    int untether(String str) throws RemoteException;

    boolean updateLockdownVpn() throws RemoteException;
}
