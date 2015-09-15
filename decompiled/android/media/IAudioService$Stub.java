package android.media;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.media.IAudioFocusDispatcher.Stub;
import android.media.audiopolicy.AudioPolicyConfig;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class IAudioService$Stub extends Binder implements IAudioService {
    private static final String DESCRIPTOR = "android.media.IAudioService";
    static final int TRANSACTION_abandonAudioFocus = 43;
    static final int TRANSACTION_adjustMasterVolume = 3;
    static final int TRANSACTION_adjustStreamVolume = 2;
    static final int TRANSACTION_adjustSuggestedStreamVolume = 1;
    static final int TRANSACTION_avrcpSupportsAbsoluteVolume = 35;
    static final int TRANSACTION_disableSafeMediaVolume = 65;
    static final int TRANSACTION_forceRemoteSubmixFullVolume = 10;
    static final int TRANSACTION_forceVolumeControlStream = 54;
    static final int TRANSACTION_getCurrentAudioFocus = 45;
    static final int TRANSACTION_getLastAudibleMasterVolume = 18;
    static final int TRANSACTION_getLastAudibleStreamVolume = 17;
    static final int TRANSACTION_getMasterMaxVolume = 16;
    static final int TRANSACTION_getMasterStreamType = 57;
    static final int TRANSACTION_getMasterVolume = 14;
    static final int TRANSACTION_getMode = 29;
    static final int TRANSACTION_getRingerModeExternal = 22;
    static final int TRANSACTION_getRingerModeInternal = 23;
    static final int TRANSACTION_getRingtonePlayer = 56;
    static final int TRANSACTION_getStreamMaxVolume = 15;
    static final int TRANSACTION_getStreamVolume = 13;
    static final int TRANSACTION_getVibrateSetting = 26;
    static final int TRANSACTION_isBluetoothA2dpOn = 41;
    static final int TRANSACTION_isBluetoothScoOn = 39;
    static final int TRANSACTION_isCameraSoundForced = 61;
    static final int TRANSACTION_isHdmiSystemAudioSupported = 67;
    static final int TRANSACTION_isMasterMute = 12;
    static final int TRANSACTION_isSpeakerphoneOn = 37;
    static final int TRANSACTION_isStreamAffectedByRingerMode = 64;
    static final int TRANSACTION_isStreamMute = 9;
    static final int TRANSACTION_isValidRingerMode = 24;
    static final int TRANSACTION_loadSoundEffects = 32;
    static final int TRANSACTION_notifyVolumeControllerVisible = 63;
    static final int TRANSACTION_playSoundEffect = 30;
    static final int TRANSACTION_playSoundEffectVolume = 31;
    static final int TRANSACTION_registerAudioPolicy = 68;
    static final int TRANSACTION_registerRemoteControlDisplay = 46;
    static final int TRANSACTION_registerRemoteController = 47;
    static final int TRANSACTION_reloadAudioSettings = 34;
    static final int TRANSACTION_remoteControlDisplayUsesBitmapSize = 49;
    static final int TRANSACTION_remoteControlDisplayWantsPlaybackPositionSync = 50;
    static final int TRANSACTION_requestAudioFocus = 42;
    static final int TRANSACTION_setBluetoothA2dpDeviceConnectionState = 59;
    static final int TRANSACTION_setBluetoothA2dpOn = 40;
    static final int TRANSACTION_setBluetoothScoOn = 38;
    static final int TRANSACTION_setFocusPropertiesForPolicy = 70;
    static final int TRANSACTION_setHdmiSystemAudioSupported = 66;
    static final int TRANSACTION_setMasterMute = 11;
    static final int TRANSACTION_setMasterVolume = 6;
    static final int TRANSACTION_setMicrophoneMute = 19;
    static final int TRANSACTION_setMode = 28;
    static final int TRANSACTION_setRemoteStreamVolume = 5;
    static final int TRANSACTION_setRingerModeExternal = 20;
    static final int TRANSACTION_setRingerModeInternal = 21;
    static final int TRANSACTION_setRingtonePlayer = 55;
    static final int TRANSACTION_setSpeakerphoneOn = 36;
    static final int TRANSACTION_setStreamMute = 8;
    static final int TRANSACTION_setStreamSolo = 7;
    static final int TRANSACTION_setStreamVolume = 4;
    static final int TRANSACTION_setVibrateSetting = 25;
    static final int TRANSACTION_setVolumeController = 62;
    static final int TRANSACTION_setWiredDeviceConnectionState = 58;
    static final int TRANSACTION_shouldVibrate = 27;
    static final int TRANSACTION_startBluetoothSco = 51;
    static final int TRANSACTION_startBluetoothScoVirtualCall = 52;
    static final int TRANSACTION_startWatchingRoutes = 60;
    static final int TRANSACTION_stopBluetoothSco = 53;
    static final int TRANSACTION_unloadSoundEffects = 33;
    static final int TRANSACTION_unregisterAudioFocusClient = 44;
    static final int TRANSACTION_unregisterAudioPolicyAsync = 69;
    static final int TRANSACTION_unregisterRemoteControlDisplay = 48;

    private static class Proxy implements IAudioService {
        private IBinder mRemote;

        Proxy(IBinder remote) {
            this.mRemote = remote;
        }

        public IBinder asBinder() {
            return this.mRemote;
        }

        public String getInterfaceDescriptor() {
            return IAudioService$Stub.DESCRIPTOR;
        }

        public void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags, String callingPackage) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(direction);
                _data.writeInt(suggestedStreamType);
                _data.writeInt(flags);
                _data.writeString(callingPackage);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                _data.writeInt(direction);
                _data.writeInt(flags);
                _data.writeString(callingPackage);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_adjustStreamVolume, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void adjustMasterVolume(int direction, int flags, String callingPackage) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(direction);
                _data.writeInt(flags);
                _data.writeString(callingPackage);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_adjustMasterVolume, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setStreamVolume(int streamType, int index, int flags, String callingPackage) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                _data.writeInt(index);
                _data.writeInt(flags);
                _data.writeString(callingPackage);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setStreamVolume, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setRemoteStreamVolume(int index) throws RemoteException {
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(index);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setRemoteStreamVolume, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void setMasterVolume(int index, int flags, String callingPackage) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(index);
                _data.writeInt(flags);
                _data.writeString(callingPackage);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setMasterVolume, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setStreamSolo(int streamType, boolean state, IBinder cb) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                if (state) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setStreamSolo, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setStreamMute(int streamType, boolean state, IBinder cb) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                if (state) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setStreamMute, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isStreamMute(int streamType) throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isStreamMute, _data, _reply, 0);
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

        public void forceRemoteSubmixFullVolume(boolean startForcing, IBinder cb) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (startForcing) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_forceRemoteSubmixFullVolume, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setMasterMute(boolean state, int flags, String callingPackage, IBinder cb) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (state) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                _data.writeInt(flags);
                _data.writeString(callingPackage);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setMasterMute, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isMasterMute() throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isMasterMute, _data, _reply, 0);
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

        public int getStreamVolume(int streamType) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getStreamVolume, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getMasterVolume() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getMasterVolume, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getStreamMaxVolume(int streamType) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getStreamMaxVolume, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getMasterMaxVolume() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getMasterMaxVolume, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getLastAudibleStreamVolume(int streamType) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getLastAudibleStreamVolume, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getLastAudibleMasterVolume() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getLastAudibleMasterVolume, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setMicrophoneMute(boolean on, String callingPackage) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (on) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                _data.writeString(callingPackage);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setMicrophoneMute, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setRingerModeExternal(int ringerMode, String caller) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(ringerMode);
                _data.writeString(caller);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setRingerModeExternal, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setRingerModeInternal(int ringerMode, String caller) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(ringerMode);
                _data.writeString(caller);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setRingerModeInternal, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getRingerModeExternal() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getRingerModeExternal, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getRingerModeInternal() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getRingerModeInternal, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isValidRingerMode(int ringerMode) throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(ringerMode);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isValidRingerMode, _data, _reply, 0);
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

        public void setVibrateSetting(int vibrateType, int vibrateSetting) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(vibrateType);
                _data.writeInt(vibrateSetting);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setVibrateSetting, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getVibrateSetting(int vibrateType) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(vibrateType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getVibrateSetting, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean shouldVibrate(int vibrateType) throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(vibrateType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_shouldVibrate, _data, _reply, 0);
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

        public void setMode(int mode, IBinder cb) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(mode);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setMode, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getMode() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getMode, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void playSoundEffect(int effectType) throws RemoteException {
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(effectType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_playSoundEffect, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void playSoundEffectVolume(int effectType, float volume) throws RemoteException {
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(effectType);
                _data.writeFloat(volume);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_playSoundEffectVolume, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public boolean loadSoundEffects() throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_loadSoundEffects, _data, _reply, 0);
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

        public void unloadSoundEffects() throws RemoteException {
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_unloadSoundEffects, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void reloadAudioSettings() throws RemoteException {
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_reloadAudioSettings, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void avrcpSupportsAbsoluteVolume(String address, boolean support) throws RemoteException {
            int i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeString(address);
                if (!support) {
                    i = 0;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_avrcpSupportsAbsoluteVolume, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void setSpeakerphoneOn(boolean on) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (on) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setSpeakerphoneOn, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isSpeakerphoneOn() throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isSpeakerphoneOn, _data, _reply, 0);
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

        public void setBluetoothScoOn(boolean on) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (on) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setBluetoothScoOn, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isBluetoothScoOn() throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isBluetoothScoOn, _data, _reply, 0);
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

        public void setBluetoothA2dpOn(boolean on) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (on) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setBluetoothA2dpOn, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isBluetoothA2dpOn() throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isBluetoothA2dpOn, _data, _reply, 0);
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

        public int requestAudioFocus(AudioAttributes aa, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName, int flags, IAudioPolicyCallback pcb) throws RemoteException {
            IBinder iBinder = null;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (aa != null) {
                    _data.writeInt(IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
                    aa.writeToParcel(_data, 0);
                } else {
                    _data.writeInt(0);
                }
                _data.writeInt(durationHint);
                _data.writeStrongBinder(cb);
                _data.writeStrongBinder(fd != null ? fd.asBinder() : null);
                _data.writeString(clientId);
                _data.writeString(callingPackageName);
                _data.writeInt(flags);
                if (pcb != null) {
                    iBinder = pcb.asBinder();
                }
                _data.writeStrongBinder(iBinder);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_requestAudioFocus, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int abandonAudioFocus(IAudioFocusDispatcher fd, String clientId, AudioAttributes aa) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(fd != null ? fd.asBinder() : null);
                _data.writeString(clientId);
                if (aa != null) {
                    _data.writeInt(IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
                    aa.writeToParcel(_data, 0);
                } else {
                    _data.writeInt(0);
                }
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_abandonAudioFocus, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void unregisterAudioFocusClient(String clientId) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeString(clientId);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_unregisterAudioFocusClient, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getCurrentAudioFocus() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getCurrentAudioFocus, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean registerRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(rcd != null ? rcd.asBinder() : null);
                _data.writeInt(w);
                _data.writeInt(h);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_registerRemoteControlDisplay, _data, _reply, 0);
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

        public boolean registerRemoteController(IRemoteControlDisplay rcd, int w, int h, ComponentName listenerComp) throws RemoteException {
            boolean _result = true;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(rcd != null ? rcd.asBinder() : null);
                _data.writeInt(w);
                _data.writeInt(h);
                if (listenerComp != null) {
                    _data.writeInt(IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
                    listenerComp.writeToParcel(_data, 0);
                } else {
                    _data.writeInt(0);
                }
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_registerRemoteController, _data, _reply, 0);
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

        public void unregisterRemoteControlDisplay(IRemoteControlDisplay rcd) throws RemoteException {
            IBinder iBinder = null;
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (rcd != null) {
                    iBinder = rcd.asBinder();
                }
                _data.writeStrongBinder(iBinder);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_unregisterRemoteControlDisplay, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay rcd, int w, int h) throws RemoteException {
            IBinder iBinder = null;
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (rcd != null) {
                    iBinder = rcd.asBinder();
                }
                _data.writeStrongBinder(iBinder);
                _data.writeInt(w);
                _data.writeInt(h);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_remoteControlDisplayUsesBitmapSize, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay rcd, boolean wantsSync) throws RemoteException {
            IBinder iBinder = null;
            int i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (rcd != null) {
                    iBinder = rcd.asBinder();
                }
                _data.writeStrongBinder(iBinder);
                if (!wantsSync) {
                    i = 0;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_remoteControlDisplayWantsPlaybackPositionSync, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public void startBluetoothSco(IBinder cb, int targetSdkVersion) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(cb);
                _data.writeInt(targetSdkVersion);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_startBluetoothSco, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void startBluetoothScoVirtualCall(IBinder cb) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_startBluetoothScoVirtualCall, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void stopBluetoothSco(IBinder cb) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_stopBluetoothSco, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void forceVolumeControlStream(int streamType, IBinder cb) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                _data.writeStrongBinder(cb);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_forceVolumeControlStream, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setRingtonePlayer(IRingtonePlayer player) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(player != null ? player.asBinder() : null);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setRingtonePlayer, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public IRingtonePlayer getRingtonePlayer() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getRingtonePlayer, _data, _reply, 0);
                _reply.readException();
                IRingtonePlayer _result = IRingtonePlayer$Stub.asInterface(_reply.readStrongBinder());
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int getMasterStreamType() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_getMasterStreamType, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void setWiredDeviceConnectionState(int device, int state, String name) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(device);
                _data.writeInt(state);
                _data.writeString(name);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setWiredDeviceConnectionState, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice device, int state, int profile) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (device != null) {
                    _data.writeInt(IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
                    device.writeToParcel(_data, 0);
                } else {
                    _data.writeInt(0);
                }
                _data.writeInt(state);
                _data.writeInt(profile);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setBluetoothA2dpDeviceConnectionState, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                AudioRoutesInfo _result;
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(observer != null ? observer.asBinder() : null);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_startWatchingRoutes, _data, _reply, 0);
                _reply.readException();
                if (_reply.readInt() != 0) {
                    _result = (AudioRoutesInfo) AudioRoutesInfo.CREATOR.createFromParcel(_reply);
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

        public boolean isCameraSoundForced() throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isCameraSoundForced, _data, _reply, 0);
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

        public void setVolumeController(IVolumeController controller) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(controller != null ? controller.asBinder() : null);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setVolumeController, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void notifyVolumeControllerVisible(IVolumeController controller, boolean visible) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeStrongBinder(controller != null ? controller.asBinder() : null);
                if (visible) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_notifyVolumeControllerVisible, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isStreamAffectedByRingerMode(int streamType) throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(streamType);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isStreamAffectedByRingerMode, _data, _reply, 0);
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

        public void disableSafeMediaVolume() throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_disableSafeMediaVolume, _data, _reply, 0);
                _reply.readException();
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public int setHdmiSystemAudioSupported(boolean on) throws RemoteException {
            int i = 0;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (on) {
                    i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setHdmiSystemAudioSupported, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public boolean isHdmiSystemAudioSupported() throws RemoteException {
            boolean _result = false;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_isHdmiSystemAudioSupported, _data, _reply, 0);
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

        public String registerAudioPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb, boolean hasFocusListener) throws RemoteException {
            int i = IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume;
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (policyConfig != null) {
                    _data.writeInt(IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
                    policyConfig.writeToParcel(_data, 0);
                } else {
                    _data.writeInt(0);
                }
                _data.writeStrongBinder(pcb != null ? pcb.asBinder() : null);
                if (!hasFocusListener) {
                    i = 0;
                }
                _data.writeInt(i);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_registerAudioPolicy, _data, _reply, 0);
                _reply.readException();
                String _result = _reply.readString();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }

        public void unregisterAudioPolicyAsync(IAudioPolicyCallback pcb) throws RemoteException {
            IBinder iBinder = null;
            Parcel _data = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                if (pcb != null) {
                    iBinder = pcb.asBinder();
                }
                _data.writeStrongBinder(iBinder);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_unregisterAudioPolicyAsync, _data, null, IAudioService$Stub.TRANSACTION_adjustSuggestedStreamVolume);
            } finally {
                _data.recycle();
            }
        }

        public int setFocusPropertiesForPolicy(int duckingBehavior, IAudioPolicyCallback pcb) throws RemoteException {
            Parcel _data = Parcel.obtain();
            Parcel _reply = Parcel.obtain();
            try {
                _data.writeInterfaceToken(IAudioService$Stub.DESCRIPTOR);
                _data.writeInt(duckingBehavior);
                _data.writeStrongBinder(pcb != null ? pcb.asBinder() : null);
                this.mRemote.transact(IAudioService$Stub.TRANSACTION_setFocusPropertiesForPolicy, _data, _reply, 0);
                _reply.readException();
                int _result = _reply.readInt();
                return _result;
            } finally {
                _reply.recycle();
                _data.recycle();
            }
        }
    }

    public IAudioService$Stub() {
        attachInterface(this, DESCRIPTOR);
    }

    public static IAudioService asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
        if (iin == null || !(iin instanceof IAudioService)) {
            return new Proxy(obj);
        }
        return (IAudioService) iin;
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        boolean _result;
        int _result2;
        switch (code) {
            case TRANSACTION_adjustSuggestedStreamVolume /*1*/:
                data.enforceInterface(DESCRIPTOR);
                adjustSuggestedStreamVolume(data.readInt(), data.readInt(), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_adjustStreamVolume /*2*/:
                data.enforceInterface(DESCRIPTOR);
                adjustStreamVolume(data.readInt(), data.readInt(), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_adjustMasterVolume /*3*/:
                data.enforceInterface(DESCRIPTOR);
                adjustMasterVolume(data.readInt(), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_setStreamVolume /*4*/:
                data.enforceInterface(DESCRIPTOR);
                setStreamVolume(data.readInt(), data.readInt(), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_setRemoteStreamVolume /*5*/:
                data.enforceInterface(DESCRIPTOR);
                setRemoteStreamVolume(data.readInt());
                return true;
            case TRANSACTION_setMasterVolume /*6*/:
                data.enforceInterface(DESCRIPTOR);
                setMasterVolume(data.readInt(), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_setStreamSolo /*7*/:
                data.enforceInterface(DESCRIPTOR);
                setStreamSolo(data.readInt(), data.readInt() != 0, data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_setStreamMute /*8*/:
                data.enforceInterface(DESCRIPTOR);
                setStreamMute(data.readInt(), data.readInt() != 0, data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_isStreamMute /*9*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isStreamMute(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_forceRemoteSubmixFullVolume /*10*/:
                data.enforceInterface(DESCRIPTOR);
                forceRemoteSubmixFullVolume(data.readInt() != 0, data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_setMasterMute /*11*/:
                data.enforceInterface(DESCRIPTOR);
                setMasterMute(data.readInt() != 0, data.readInt(), data.readString(), data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_isMasterMute /*12*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isMasterMute();
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_getStreamVolume /*13*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getStreamVolume(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_getMasterVolume /*14*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getMasterVolume();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_getStreamMaxVolume /*15*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getStreamMaxVolume(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_getMasterMaxVolume /*16*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getMasterMaxVolume();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_getLastAudibleStreamVolume /*17*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getLastAudibleStreamVolume(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_getLastAudibleMasterVolume /*18*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getLastAudibleMasterVolume();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_setMicrophoneMute /*19*/:
                data.enforceInterface(DESCRIPTOR);
                setMicrophoneMute(data.readInt() != 0, data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_setRingerModeExternal /*20*/:
                data.enforceInterface(DESCRIPTOR);
                setRingerModeExternal(data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_setRingerModeInternal /*21*/:
                data.enforceInterface(DESCRIPTOR);
                setRingerModeInternal(data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_getRingerModeExternal /*22*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getRingerModeExternal();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_getRingerModeInternal /*23*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getRingerModeInternal();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_isValidRingerMode /*24*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isValidRingerMode(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_setVibrateSetting /*25*/:
                data.enforceInterface(DESCRIPTOR);
                setVibrateSetting(data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case TRANSACTION_getVibrateSetting /*26*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getVibrateSetting(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_shouldVibrate /*27*/:
                data.enforceInterface(DESCRIPTOR);
                _result = shouldVibrate(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_setMode /*28*/:
                data.enforceInterface(DESCRIPTOR);
                setMode(data.readInt(), data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_getMode /*29*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getMode();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_playSoundEffect /*30*/:
                data.enforceInterface(DESCRIPTOR);
                playSoundEffect(data.readInt());
                return true;
            case TRANSACTION_playSoundEffectVolume /*31*/:
                data.enforceInterface(DESCRIPTOR);
                playSoundEffectVolume(data.readInt(), data.readFloat());
                return true;
            case TRANSACTION_loadSoundEffects /*32*/:
                data.enforceInterface(DESCRIPTOR);
                _result = loadSoundEffects();
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_unloadSoundEffects /*33*/:
                data.enforceInterface(DESCRIPTOR);
                unloadSoundEffects();
                return true;
            case TRANSACTION_reloadAudioSettings /*34*/:
                data.enforceInterface(DESCRIPTOR);
                reloadAudioSettings();
                return true;
            case TRANSACTION_avrcpSupportsAbsoluteVolume /*35*/:
                data.enforceInterface(DESCRIPTOR);
                avrcpSupportsAbsoluteVolume(data.readString(), data.readInt() != 0);
                return true;
            case TRANSACTION_setSpeakerphoneOn /*36*/:
                data.enforceInterface(DESCRIPTOR);
                setSpeakerphoneOn(data.readInt() != 0);
                reply.writeNoException();
                return true;
            case TRANSACTION_isSpeakerphoneOn /*37*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isSpeakerphoneOn();
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_setBluetoothScoOn /*38*/:
                data.enforceInterface(DESCRIPTOR);
                setBluetoothScoOn(data.readInt() != 0);
                reply.writeNoException();
                return true;
            case TRANSACTION_isBluetoothScoOn /*39*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isBluetoothScoOn();
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_setBluetoothA2dpOn /*40*/:
                data.enforceInterface(DESCRIPTOR);
                setBluetoothA2dpOn(data.readInt() != 0);
                reply.writeNoException();
                return true;
            case TRANSACTION_isBluetoothA2dpOn /*41*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isBluetoothA2dpOn();
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_requestAudioFocus /*42*/:
                AudioAttributes _arg0;
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg0 = (AudioAttributes) AudioAttributes.CREATOR.createFromParcel(data);
                } else {
                    _arg0 = null;
                }
                _result2 = requestAudioFocus(_arg0, data.readInt(), data.readStrongBinder(), Stub.asInterface(data.readStrongBinder()), data.readString(), data.readString(), data.readInt(), IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_abandonAudioFocus /*43*/:
                AudioAttributes _arg2;
                data.enforceInterface(DESCRIPTOR);
                IAudioFocusDispatcher _arg02 = Stub.asInterface(data.readStrongBinder());
                String _arg1 = data.readString();
                if (data.readInt() != 0) {
                    _arg2 = (AudioAttributes) AudioAttributes.CREATOR.createFromParcel(data);
                } else {
                    _arg2 = null;
                }
                _result2 = abandonAudioFocus(_arg02, _arg1, _arg2);
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_unregisterAudioFocusClient /*44*/:
                data.enforceInterface(DESCRIPTOR);
                unregisterAudioFocusClient(data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_getCurrentAudioFocus /*45*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getCurrentAudioFocus();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_registerRemoteControlDisplay /*46*/:
                data.enforceInterface(DESCRIPTOR);
                _result = registerRemoteControlDisplay(IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_registerRemoteController /*47*/:
                ComponentName _arg3;
                data.enforceInterface(DESCRIPTOR);
                IRemoteControlDisplay _arg03 = IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder());
                int _arg12 = data.readInt();
                int _arg22 = data.readInt();
                if (data.readInt() != 0) {
                    _arg3 = (ComponentName) ComponentName.CREATOR.createFromParcel(data);
                } else {
                    _arg3 = null;
                }
                _result = registerRemoteController(_arg03, _arg12, _arg22, _arg3);
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_unregisterRemoteControlDisplay /*48*/:
                data.enforceInterface(DESCRIPTOR);
                unregisterRemoteControlDisplay(IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder()));
                return true;
            case TRANSACTION_remoteControlDisplayUsesBitmapSize /*49*/:
                data.enforceInterface(DESCRIPTOR);
                remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readInt());
                return true;
            case TRANSACTION_remoteControlDisplayWantsPlaybackPositionSync /*50*/:
                data.enforceInterface(DESCRIPTOR);
                remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay.Stub.asInterface(data.readStrongBinder()), data.readInt() != 0);
                return true;
            case TRANSACTION_startBluetoothSco /*51*/:
                data.enforceInterface(DESCRIPTOR);
                startBluetoothSco(data.readStrongBinder(), data.readInt());
                reply.writeNoException();
                return true;
            case TRANSACTION_startBluetoothScoVirtualCall /*52*/:
                data.enforceInterface(DESCRIPTOR);
                startBluetoothScoVirtualCall(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_stopBluetoothSco /*53*/:
                data.enforceInterface(DESCRIPTOR);
                stopBluetoothSco(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_forceVolumeControlStream /*54*/:
                data.enforceInterface(DESCRIPTOR);
                forceVolumeControlStream(data.readInt(), data.readStrongBinder());
                reply.writeNoException();
                return true;
            case TRANSACTION_setRingtonePlayer /*55*/:
                data.enforceInterface(DESCRIPTOR);
                setRingtonePlayer(IRingtonePlayer$Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case TRANSACTION_getRingtonePlayer /*56*/:
                data.enforceInterface(DESCRIPTOR);
                IRingtonePlayer _result3 = getRingtonePlayer();
                reply.writeNoException();
                reply.writeStrongBinder(_result3 != null ? _result3.asBinder() : null);
                return true;
            case TRANSACTION_getMasterStreamType /*57*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = getMasterStreamType();
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_setWiredDeviceConnectionState /*58*/:
                data.enforceInterface(DESCRIPTOR);
                setWiredDeviceConnectionState(data.readInt(), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case TRANSACTION_setBluetoothA2dpDeviceConnectionState /*59*/:
                BluetoothDevice _arg04;
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg04 = (BluetoothDevice) BluetoothDevice.CREATOR.createFromParcel(data);
                } else {
                    _arg04 = null;
                }
                _result2 = setBluetoothA2dpDeviceConnectionState(_arg04, data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_startWatchingRoutes /*60*/:
                data.enforceInterface(DESCRIPTOR);
                AudioRoutesInfo _result4 = startWatchingRoutes(IAudioRoutesObserver.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                if (_result4 != null) {
                    reply.writeInt(TRANSACTION_adjustSuggestedStreamVolume);
                    _result4.writeToParcel(reply, TRANSACTION_adjustSuggestedStreamVolume);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case TRANSACTION_isCameraSoundForced /*61*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isCameraSoundForced();
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_setVolumeController /*62*/:
                data.enforceInterface(DESCRIPTOR);
                setVolumeController(IVolumeController.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case TRANSACTION_notifyVolumeControllerVisible /*63*/:
                data.enforceInterface(DESCRIPTOR);
                notifyVolumeControllerVisible(IVolumeController.Stub.asInterface(data.readStrongBinder()), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case TRANSACTION_isStreamAffectedByRingerMode /*64*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isStreamAffectedByRingerMode(data.readInt());
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_disableSafeMediaVolume /*65*/:
                data.enforceInterface(DESCRIPTOR);
                disableSafeMediaVolume();
                reply.writeNoException();
                return true;
            case TRANSACTION_setHdmiSystemAudioSupported /*66*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = setHdmiSystemAudioSupported(data.readInt() != 0);
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case TRANSACTION_isHdmiSystemAudioSupported /*67*/:
                data.enforceInterface(DESCRIPTOR);
                _result = isHdmiSystemAudioSupported();
                reply.writeNoException();
                reply.writeInt(_result ? TRANSACTION_adjustSuggestedStreamVolume : 0);
                return true;
            case TRANSACTION_registerAudioPolicy /*68*/:
                AudioPolicyConfig _arg05;
                data.enforceInterface(DESCRIPTOR);
                if (data.readInt() != 0) {
                    _arg05 = (AudioPolicyConfig) AudioPolicyConfig.CREATOR.createFromParcel(data);
                } else {
                    _arg05 = null;
                }
                String _result5 = registerAudioPolicy(_arg05, IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder()), data.readInt() != 0);
                reply.writeNoException();
                reply.writeString(_result5);
                return true;
            case TRANSACTION_unregisterAudioPolicyAsync /*69*/:
                data.enforceInterface(DESCRIPTOR);
                unregisterAudioPolicyAsync(IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder()));
                return true;
            case TRANSACTION_setFocusPropertiesForPolicy /*70*/:
                data.enforceInterface(DESCRIPTOR);
                _result2 = setFocusPropertiesForPolicy(data.readInt(), IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(_result2);
                return true;
            case 1598968902:
                reply.writeString(DESCRIPTOR);
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }
}
