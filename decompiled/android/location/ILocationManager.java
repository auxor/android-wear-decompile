package android.location;

import android.app.PendingIntent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.internal.location.ProviderProperties;
import java.util.ArrayList;
import java.util.List;

public interface ILocationManager extends IInterface {

    public static abstract class Stub extends Binder implements ILocationManager {
        private static final String DESCRIPTOR = "android.location.ILocationManager";
        static final int TRANSACTION_addGpsMeasurementsListener = 12;
        static final int TRANSACTION_addGpsNavigationMessageListener = 14;
        static final int TRANSACTION_addGpsStatusListener = 6;
        static final int TRANSACTION_addTestProvider = 22;
        static final int TRANSACTION_clearTestProviderEnabled = 27;
        static final int TRANSACTION_clearTestProviderLocation = 25;
        static final int TRANSACTION_clearTestProviderStatus = 29;
        static final int TRANSACTION_geocoderIsPresent = 8;
        static final int TRANSACTION_getAllProviders = 16;
        static final int TRANSACTION_getBestProvider = 18;
        static final int TRANSACTION_getFromLocation = 9;
        static final int TRANSACTION_getFromLocationName = 10;
        static final int TRANSACTION_getLastLocation = 5;
        static final int TRANSACTION_getProviderProperties = 20;
        static final int TRANSACTION_getProviders = 17;
        static final int TRANSACTION_isProviderEnabled = 21;
        static final int TRANSACTION_locationCallbackFinished = 32;
        static final int TRANSACTION_providerMeetsCriteria = 19;
        static final int TRANSACTION_removeGeofence = 4;
        static final int TRANSACTION_removeGpsMeasurementsListener = 13;
        static final int TRANSACTION_removeGpsNavigationMessageListener = 15;
        static final int TRANSACTION_removeGpsStatusListener = 7;
        static final int TRANSACTION_removeTestProvider = 23;
        static final int TRANSACTION_removeUpdates = 2;
        static final int TRANSACTION_reportLocation = 31;
        static final int TRANSACTION_requestGeofence = 3;
        static final int TRANSACTION_requestLocationUpdates = 1;
        static final int TRANSACTION_sendExtraCommand = 30;
        static final int TRANSACTION_sendNiResponse = 11;
        static final int TRANSACTION_setTestProviderEnabled = 26;
        static final int TRANSACTION_setTestProviderLocation = 24;
        static final int TRANSACTION_setTestProviderStatus = 28;

        private static class Proxy implements ILocationManager {
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

            public void requestLocationUpdates(LocationRequest request, ILocationListener listener, PendingIntent intent, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_requestLocationUpdates, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeUpdates(ILocationListener listener, PendingIntent intent, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_removeUpdates, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void requestGeofence(LocationRequest request, Geofence geofence, PendingIntent intent, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (geofence != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        geofence.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_requestGeofence, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeGeofence(Geofence fence, PendingIntent intent, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (fence != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        fence.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (intent != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_removeGeofence, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Location getLastLocation(LocationRequest request, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Location _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (request != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_getLastLocation, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (Location) Location.CREATOR.createFromParcel(_reply);
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

            public boolean addGpsStatusListener(IGpsStatusListener listener, String packageName) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_addGpsStatusListener, _data, _reply, 0);
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

            public void removeGpsStatusListener(IGpsStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_removeGpsStatusListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean geocoderIsPresent() throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_geocoderIsPresent, _data, _reply, 0);
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

            public String getFromLocation(double latitude, double longitude, int maxResults, GeocoderParams params, List<Address> addrs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeDouble(latitude);
                    _data.writeDouble(longitude);
                    _data.writeInt(maxResults);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getFromLocation, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.readTypedList(addrs, Address.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getFromLocationName(String locationName, double lowerLeftLatitude, double lowerLeftLongitude, double upperRightLatitude, double upperRightLongitude, int maxResults, GeocoderParams params, List<Address> addrs) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(locationName);
                    _data.writeDouble(lowerLeftLatitude);
                    _data.writeDouble(lowerLeftLongitude);
                    _data.writeDouble(upperRightLatitude);
                    _data.writeDouble(upperRightLongitude);
                    _data.writeInt(maxResults);
                    if (params != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        params.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_getFromLocationName, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    _reply.readTypedList(addrs, Address.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean sendNiResponse(int notifId, int userResponse) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(notifId);
                    _data.writeInt(userResponse);
                    this.mRemote.transact(Stub.TRANSACTION_sendNiResponse, _data, _reply, 0);
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

            public boolean addGpsMeasurementsListener(IGpsMeasurementsListener listener, String packageName) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_addGpsMeasurementsListener, _data, _reply, 0);
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

            public void removeGpsMeasurementsListener(IGpsMeasurementsListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_removeGpsMeasurementsListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean addGpsNavigationMessageListener(IGpsNavigationMessageListener listener, String packageName) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    _data.writeString(packageName);
                    this.mRemote.transact(Stub.TRANSACTION_addGpsNavigationMessageListener, _data, _reply, 0);
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

            public void removeGpsNavigationMessageListener(IGpsNavigationMessageListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_removeGpsNavigationMessageListener, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<String> getAllProviders() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_getAllProviders, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<String> getProviders(Criteria criteria, boolean enabledOnly) throws RemoteException {
                int i = Stub.TRANSACTION_requestLocationUpdates;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (criteria != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        criteria.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (!enabledOnly) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_getProviders, _data, _reply, 0);
                    _reply.readException();
                    List<String> _result = _reply.createStringArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String getBestProvider(Criteria criteria, boolean enabledOnly) throws RemoteException {
                int i = Stub.TRANSACTION_requestLocationUpdates;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (criteria != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        criteria.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (!enabledOnly) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_getBestProvider, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean providerMeetsCriteria(String provider, Criteria criteria) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    if (criteria != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        criteria.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_providerMeetsCriteria, _data, _reply, 0);
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

            public ProviderProperties getProviderProperties(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    ProviderProperties _result;
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(Stub.TRANSACTION_getProviderProperties, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = (ProviderProperties) ProviderProperties.CREATOR.createFromParcel(_reply);
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

            public boolean isProviderEnabled(String provider) throws RemoteException {
                boolean _result = false;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(Stub.TRANSACTION_isProviderEnabled, _data, _reply, 0);
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

            public void addTestProvider(String name, ProviderProperties properties) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    if (properties != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        properties.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_addTestProvider, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void removeTestProvider(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(Stub.TRANSACTION_removeTestProvider, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setTestProviderLocation(String provider, Location loc) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    if (loc != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        loc.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_setTestProviderLocation, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearTestProviderLocation(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(Stub.TRANSACTION_clearTestProviderLocation, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setTestProviderEnabled(String provider, boolean enabled) throws RemoteException {
                int i = 0;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    if (enabled) {
                        i = Stub.TRANSACTION_requestLocationUpdates;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_setTestProviderEnabled, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearTestProviderEnabled(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(Stub.TRANSACTION_clearTestProviderEnabled, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setTestProviderStatus(String provider, int status, Bundle extras, long updateTime) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeInt(status);
                    if (extras != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeLong(updateTime);
                    this.mRemote.transact(Stub.TRANSACTION_setTestProviderStatus, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void clearTestProviderStatus(String provider) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    this.mRemote.transact(Stub.TRANSACTION_clearTestProviderStatus, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean sendExtraCommand(String provider, String command, Bundle extras) throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(provider);
                    _data.writeString(command);
                    if (extras != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(Stub.TRANSACTION_sendExtraCommand, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    if (_reply.readInt() != 0) {
                        extras.readFromParcel(_reply);
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void reportLocation(Location location, boolean passive) throws RemoteException {
                int i = Stub.TRANSACTION_requestLocationUpdates;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (location != null) {
                        _data.writeInt(Stub.TRANSACTION_requestLocationUpdates);
                        location.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (!passive) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(Stub.TRANSACTION_reportLocation, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void locationCallbackFinished(ILocationListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_locationCallbackFinished, _data, _reply, 0);
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

        public static ILocationManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ILocationManager)) {
                return new Proxy(obj);
            }
            return (ILocationManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            LocationRequest _arg0;
            PendingIntent _arg2;
            PendingIntent _arg1;
            boolean _result;
            double _arg12;
            String _result2;
            String _arg02;
            List<String> _result3;
            Criteria _arg03;
            Bundle _arg22;
            switch (code) {
                case TRANSACTION_requestLocationUpdates /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (LocationRequest) LocationRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    ILocationListener _arg13 = android.location.ILocationListener.Stub.asInterface(data.readStrongBinder());
                    if (data.readInt() != 0) {
                        _arg2 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    requestLocationUpdates(_arg0, _arg13, _arg2, data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_removeUpdates /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    ILocationListener _arg04 = android.location.ILocationListener.Stub.asInterface(data.readStrongBinder());
                    if (data.readInt() != 0) {
                        _arg1 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    removeUpdates(_arg04, _arg1, data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_requestGeofence /*3*/:
                    Geofence _arg14;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (LocationRequest) LocationRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg14 = (Geofence) Geofence.CREATOR.createFromParcel(data);
                    } else {
                        _arg14 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg2 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    requestGeofence(_arg0, _arg14, _arg2, data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_removeGeofence /*4*/:
                    Geofence _arg05;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg05 = (Geofence) Geofence.CREATOR.createFromParcel(data);
                    } else {
                        _arg05 = null;
                    }
                    if (data.readInt() != 0) {
                        _arg1 = (PendingIntent) PendingIntent.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    removeGeofence(_arg05, _arg1, data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getLastLocation /*5*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (LocationRequest) LocationRequest.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Location _result4 = getLastLocation(_arg0, data.readString());
                    reply.writeNoException();
                    if (_result4 != null) {
                        reply.writeInt(TRANSACTION_requestLocationUpdates);
                        _result4.writeToParcel(reply, TRANSACTION_requestLocationUpdates);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_addGpsStatusListener /*6*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = addGpsStatusListener(android.location.IGpsStatusListener.Stub.asInterface(data.readStrongBinder()), data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    return true;
                case TRANSACTION_removeGpsStatusListener /*7*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeGpsStatusListener(android.location.IGpsStatusListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_geocoderIsPresent /*8*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = geocoderIsPresent();
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    return true;
                case TRANSACTION_getFromLocation /*9*/:
                    GeocoderParams _arg3;
                    data.enforceInterface(DESCRIPTOR);
                    double _arg06 = data.readDouble();
                    _arg12 = data.readDouble();
                    int _arg23 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg3 = (GeocoderParams) GeocoderParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    List<Address> _arg4 = new ArrayList();
                    _result2 = getFromLocation(_arg06, _arg12, _arg23, _arg3, _arg4);
                    reply.writeNoException();
                    reply.writeString(_result2);
                    reply.writeTypedList(_arg4);
                    return true;
                case TRANSACTION_getFromLocationName /*10*/:
                    GeocoderParams _arg6;
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readString();
                    _arg12 = data.readDouble();
                    double _arg24 = data.readDouble();
                    double _arg32 = data.readDouble();
                    double _arg42 = data.readDouble();
                    int _arg5 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg6 = (GeocoderParams) GeocoderParams.CREATOR.createFromParcel(data);
                    } else {
                        _arg6 = null;
                    }
                    List<Address> _arg7 = new ArrayList();
                    _result2 = getFromLocationName(_arg02, _arg12, _arg24, _arg32, _arg42, _arg5, _arg6, _arg7);
                    reply.writeNoException();
                    reply.writeString(_result2);
                    reply.writeTypedList(_arg7);
                    return true;
                case TRANSACTION_sendNiResponse /*11*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = sendNiResponse(data.readInt(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    return true;
                case TRANSACTION_addGpsMeasurementsListener /*12*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = addGpsMeasurementsListener(android.location.IGpsMeasurementsListener.Stub.asInterface(data.readStrongBinder()), data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    return true;
                case TRANSACTION_removeGpsMeasurementsListener /*13*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeGpsMeasurementsListener(android.location.IGpsMeasurementsListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_addGpsNavigationMessageListener /*14*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = addGpsNavigationMessageListener(android.location.IGpsNavigationMessageListener.Stub.asInterface(data.readStrongBinder()), data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    return true;
                case TRANSACTION_removeGpsNavigationMessageListener /*15*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeGpsNavigationMessageListener(android.location.IGpsNavigationMessageListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getAllProviders /*16*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result3 = getAllProviders();
                    reply.writeNoException();
                    reply.writeStringList(_result3);
                    return true;
                case TRANSACTION_getProviders /*17*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Criteria) Criteria.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result3 = getProviders(_arg03, data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeStringList(_result3);
                    return true;
                case TRANSACTION_getBestProvider /*18*/:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg03 = (Criteria) Criteria.CREATOR.createFromParcel(data);
                    } else {
                        _arg03 = null;
                    }
                    _result2 = getBestProvider(_arg03, data.readInt() != 0);
                    reply.writeNoException();
                    reply.writeString(_result2);
                    return true;
                case TRANSACTION_providerMeetsCriteria /*19*/:
                    Criteria _arg15;
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readString();
                    if (data.readInt() != 0) {
                        _arg15 = (Criteria) Criteria.CREATOR.createFromParcel(data);
                    } else {
                        _arg15 = null;
                    }
                    _result = providerMeetsCriteria(_arg02, _arg15);
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    return true;
                case TRANSACTION_getProviderProperties /*20*/:
                    data.enforceInterface(DESCRIPTOR);
                    ProviderProperties _result5 = getProviderProperties(data.readString());
                    reply.writeNoException();
                    if (_result5 != null) {
                        reply.writeInt(TRANSACTION_requestLocationUpdates);
                        _result5.writeToParcel(reply, TRANSACTION_requestLocationUpdates);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_isProviderEnabled /*21*/:
                    data.enforceInterface(DESCRIPTOR);
                    _result = isProviderEnabled(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    return true;
                case TRANSACTION_addTestProvider /*22*/:
                    ProviderProperties _arg16;
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readString();
                    if (data.readInt() != 0) {
                        _arg16 = (ProviderProperties) ProviderProperties.CREATOR.createFromParcel(data);
                    } else {
                        _arg16 = null;
                    }
                    addTestProvider(_arg02, _arg16);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_removeTestProvider /*23*/:
                    data.enforceInterface(DESCRIPTOR);
                    removeTestProvider(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setTestProviderLocation /*24*/:
                    Location _arg17;
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readString();
                    if (data.readInt() != 0) {
                        _arg17 = (Location) Location.CREATOR.createFromParcel(data);
                    } else {
                        _arg17 = null;
                    }
                    setTestProviderLocation(_arg02, _arg17);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_clearTestProviderLocation /*25*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearTestProviderLocation(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setTestProviderEnabled /*26*/:
                    data.enforceInterface(DESCRIPTOR);
                    setTestProviderEnabled(data.readString(), data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_clearTestProviderEnabled /*27*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearTestProviderEnabled(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_setTestProviderStatus /*28*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readString();
                    int _arg18 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg22 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    setTestProviderStatus(_arg02, _arg18, _arg22, data.readLong());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_clearTestProviderStatus /*29*/:
                    data.enforceInterface(DESCRIPTOR);
                    clearTestProviderStatus(data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_sendExtraCommand /*30*/:
                    data.enforceInterface(DESCRIPTOR);
                    _arg02 = data.readString();
                    String _arg19 = data.readString();
                    if (data.readInt() != 0) {
                        _arg22 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg22 = null;
                    }
                    _result = sendExtraCommand(_arg02, _arg19, _arg22);
                    reply.writeNoException();
                    reply.writeInt(_result ? TRANSACTION_requestLocationUpdates : 0);
                    if (_arg22 != null) {
                        reply.writeInt(TRANSACTION_requestLocationUpdates);
                        _arg22.writeToParcel(reply, TRANSACTION_requestLocationUpdates);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                case TRANSACTION_reportLocation /*31*/:
                    Location _arg07;
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg07 = (Location) Location.CREATOR.createFromParcel(data);
                    } else {
                        _arg07 = null;
                    }
                    reportLocation(_arg07, data.readInt() != 0);
                    reply.writeNoException();
                    return true;
                case TRANSACTION_locationCallbackFinished /*32*/:
                    data.enforceInterface(DESCRIPTOR);
                    locationCallbackFinished(android.location.ILocationListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    boolean addGpsMeasurementsListener(IGpsMeasurementsListener iGpsMeasurementsListener, String str) throws RemoteException;

    boolean addGpsNavigationMessageListener(IGpsNavigationMessageListener iGpsNavigationMessageListener, String str) throws RemoteException;

    boolean addGpsStatusListener(IGpsStatusListener iGpsStatusListener, String str) throws RemoteException;

    void addTestProvider(String str, ProviderProperties providerProperties) throws RemoteException;

    void clearTestProviderEnabled(String str) throws RemoteException;

    void clearTestProviderLocation(String str) throws RemoteException;

    void clearTestProviderStatus(String str) throws RemoteException;

    boolean geocoderIsPresent() throws RemoteException;

    List<String> getAllProviders() throws RemoteException;

    String getBestProvider(Criteria criteria, boolean z) throws RemoteException;

    String getFromLocation(double d, double d2, int i, GeocoderParams geocoderParams, List<Address> list) throws RemoteException;

    String getFromLocationName(String str, double d, double d2, double d3, double d4, int i, GeocoderParams geocoderParams, List<Address> list) throws RemoteException;

    Location getLastLocation(LocationRequest locationRequest, String str) throws RemoteException;

    ProviderProperties getProviderProperties(String str) throws RemoteException;

    List<String> getProviders(Criteria criteria, boolean z) throws RemoteException;

    boolean isProviderEnabled(String str) throws RemoteException;

    void locationCallbackFinished(ILocationListener iLocationListener) throws RemoteException;

    boolean providerMeetsCriteria(String str, Criteria criteria) throws RemoteException;

    void removeGeofence(Geofence geofence, PendingIntent pendingIntent, String str) throws RemoteException;

    void removeGpsMeasurementsListener(IGpsMeasurementsListener iGpsMeasurementsListener) throws RemoteException;

    void removeGpsNavigationMessageListener(IGpsNavigationMessageListener iGpsNavigationMessageListener) throws RemoteException;

    void removeGpsStatusListener(IGpsStatusListener iGpsStatusListener) throws RemoteException;

    void removeTestProvider(String str) throws RemoteException;

    void removeUpdates(ILocationListener iLocationListener, PendingIntent pendingIntent, String str) throws RemoteException;

    void reportLocation(Location location, boolean z) throws RemoteException;

    void requestGeofence(LocationRequest locationRequest, Geofence geofence, PendingIntent pendingIntent, String str) throws RemoteException;

    void requestLocationUpdates(LocationRequest locationRequest, ILocationListener iLocationListener, PendingIntent pendingIntent, String str) throws RemoteException;

    boolean sendExtraCommand(String str, String str2, Bundle bundle) throws RemoteException;

    boolean sendNiResponse(int i, int i2) throws RemoteException;

    void setTestProviderEnabled(String str, boolean z) throws RemoteException;

    void setTestProviderLocation(String str, Location location) throws RemoteException;

    void setTestProviderStatus(String str, int i, Bundle bundle, long j) throws RemoteException;
}
