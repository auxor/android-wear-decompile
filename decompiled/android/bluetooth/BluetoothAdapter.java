package android.bluetooth;

import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.IBluetoothStateChangeCallback.Stub;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.net.ProxyInfo;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.SpellChecker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class BluetoothAdapter {
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED";
    public static final String ACTION_DISCOVERY_FINISHED = "android.bluetooth.adapter.action.DISCOVERY_FINISHED";
    public static final String ACTION_DISCOVERY_STARTED = "android.bluetooth.adapter.action.DISCOVERY_STARTED";
    public static final String ACTION_LOCAL_NAME_CHANGED = "android.bluetooth.adapter.action.LOCAL_NAME_CHANGED";
    public static final String ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE";
    public static final String ACTION_REQUEST_ENABLE = "android.bluetooth.adapter.action.REQUEST_ENABLE";
    public static final String ACTION_SCAN_MODE_CHANGED = "android.bluetooth.adapter.action.SCAN_MODE_CHANGED";
    public static final String ACTION_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
    public static final int ACTIVITY_ENERGY_INFO_CACHED = 0;
    public static final int ACTIVITY_ENERGY_INFO_REFRESHED = 1;
    private static final int ADDRESS_LENGTH = 17;
    public static final String BLUETOOTH_MANAGER_SERVICE = "bluetooth_manager";
    private static final int CONTROLLER_ENERGY_UPDATE_TIMEOUT_MILLIS = 30;
    private static final boolean DBG = true;
    public static final int ERROR = Integer.MIN_VALUE;
    public static final String EXTRA_CONNECTION_STATE = "android.bluetooth.adapter.extra.CONNECTION_STATE";
    public static final String EXTRA_DISCOVERABLE_DURATION = "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION";
    public static final String EXTRA_LOCAL_NAME = "android.bluetooth.adapter.extra.LOCAL_NAME";
    public static final String EXTRA_PREVIOUS_CONNECTION_STATE = "android.bluetooth.adapter.extra.PREVIOUS_CONNECTION_STATE";
    public static final String EXTRA_PREVIOUS_SCAN_MODE = "android.bluetooth.adapter.extra.PREVIOUS_SCAN_MODE";
    public static final String EXTRA_PREVIOUS_STATE = "android.bluetooth.adapter.extra.PREVIOUS_STATE";
    public static final String EXTRA_SCAN_MODE = "android.bluetooth.adapter.extra.SCAN_MODE";
    public static final String EXTRA_STATE = "android.bluetooth.adapter.extra.STATE";
    public static final int SCAN_MODE_CONNECTABLE = 21;
    public static final int SCAN_MODE_CONNECTABLE_DISCOVERABLE = 23;
    public static final int SCAN_MODE_NONE = 20;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_DISCONNECTING = 3;
    public static final int STATE_OFF = 10;
    public static final int STATE_ON = 12;
    public static final int STATE_TURNING_OFF = 13;
    public static final int STATE_TURNING_ON = 11;
    private static final String TAG = "BluetoothAdapter";
    private static final boolean VDBG = false;
    private static BluetoothAdapter sAdapter;
    private static BluetoothLeAdvertiser sBluetoothLeAdvertiser;
    private static BluetoothLeScanner sBluetoothLeScanner;
    private final Map<LeScanCallback, ScanCallback> mLeScanClients;
    private final Object mLock;
    private final IBluetoothManagerCallback mManagerCallback;
    private final IBluetoothManager mManagerService;
    private final ArrayList<IBluetoothManagerCallback> mProxyServiceStateCallbacks;
    private IBluetooth mService;

    /* renamed from: android.bluetooth.BluetoothAdapter.2 */
    class AnonymousClass2 extends ScanCallback {
        final /* synthetic */ BluetoothAdapter this$0;
        final /* synthetic */ LeScanCallback val$callback;
        final /* synthetic */ UUID[] val$serviceUuids;

        AnonymousClass2(android.bluetooth.BluetoothAdapter r1, java.util.UUID[] r2, android.bluetooth.BluetoothAdapter.LeScanCallback r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.bluetooth.BluetoothAdapter.2.<init>(android.bluetooth.BluetoothAdapter, java.util.UUID[], android.bluetooth.BluetoothAdapter$LeScanCallback):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.bluetooth.BluetoothAdapter.2.<init>(android.bluetooth.BluetoothAdapter, java.util.UUID[], android.bluetooth.BluetoothAdapter$LeScanCallback):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothAdapter.2.<init>(android.bluetooth.BluetoothAdapter, java.util.UUID[], android.bluetooth.BluetoothAdapter$LeScanCallback):void");
        }

        public void onScanResult(int r1, android.bluetooth.le.ScanResult r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.bluetooth.BluetoothAdapter.2.onScanResult(int, android.bluetooth.le.ScanResult):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.bluetooth.BluetoothAdapter.2.onScanResult(int, android.bluetooth.le.ScanResult):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothAdapter.2.onScanResult(int, android.bluetooth.le.ScanResult):void");
        }
    }

    public interface BluetoothStateChangeCallback {
        void onBluetoothStateChange(boolean z);
    }

    public interface LeScanCallback {
        void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr);
    }

    public class StateChangeCallbackWrapper extends Stub {
        private BluetoothStateChangeCallback mCallback;
        final /* synthetic */ BluetoothAdapter this$0;

        StateChangeCallbackWrapper(android.bluetooth.BluetoothAdapter r1, android.bluetooth.BluetoothAdapter.BluetoothStateChangeCallback r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.bluetooth.BluetoothAdapter.StateChangeCallbackWrapper.<init>(android.bluetooth.BluetoothAdapter, android.bluetooth.BluetoothAdapter$BluetoothStateChangeCallback):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.bluetooth.BluetoothAdapter.StateChangeCallbackWrapper.<init>(android.bluetooth.BluetoothAdapter, android.bluetooth.BluetoothAdapter$BluetoothStateChangeCallback):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothAdapter.StateChangeCallbackWrapper.<init>(android.bluetooth.BluetoothAdapter, android.bluetooth.BluetoothAdapter$BluetoothStateChangeCallback):void");
        }

        public void onBluetoothStateChange(boolean r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.bluetooth.BluetoothAdapter.StateChangeCallbackWrapper.onBluetoothStateChange(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.bluetooth.BluetoothAdapter.StateChangeCallbackWrapper.onBluetoothStateChange(boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothAdapter.StateChangeCallbackWrapper.onBluetoothStateChange(boolean):void");
        }
    }

    public static synchronized BluetoothAdapter getDefaultAdapter() {
        BluetoothAdapter bluetoothAdapter;
        synchronized (BluetoothAdapter.class) {
            if (sAdapter == null) {
                IBinder b = ServiceManager.getService(BLUETOOTH_MANAGER_SERVICE);
                if (b != null) {
                    sAdapter = new BluetoothAdapter(IBluetoothManager.Stub.asInterface(b));
                } else {
                    Log.e(TAG, "Bluetooth binder is null");
                }
            }
            bluetoothAdapter = sAdapter;
        }
        return bluetoothAdapter;
    }

    BluetoothAdapter(IBluetoothManager managerService) {
        this.mLock = new Object();
        this.mManagerCallback = new IBluetoothManagerCallback.Stub() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onBluetoothServiceUp(android.bluetooth.IBluetooth r8) {
                /*
                r7 = this;
                r3 = android.bluetooth.BluetoothAdapter.this;
                r4 = r3.mManagerCallback;
                monitor-enter(r4);
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x003b }
                r3.mService = r8;	 Catch:{ all -> 0x003b }
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x003b }
                r5 = r3.mProxyServiceStateCallbacks;	 Catch:{ all -> 0x003b }
                monitor-enter(r5);	 Catch:{ all -> 0x003b }
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x0038 }
                r3 = r3.mProxyServiceStateCallbacks;	 Catch:{ all -> 0x0038 }
                r2 = r3.iterator();	 Catch:{ all -> 0x0038 }
            L_0x001d:
                r3 = r2.hasNext();	 Catch:{ all -> 0x0038 }
                if (r3 == 0) goto L_0x0047;
            L_0x0023:
                r0 = r2.next();	 Catch:{ all -> 0x0038 }
                r0 = (android.bluetooth.IBluetoothManagerCallback) r0;	 Catch:{ all -> 0x0038 }
                if (r0 == 0) goto L_0x003e;
            L_0x002b:
                r0.onBluetoothServiceUp(r8);	 Catch:{ Exception -> 0x002f }
                goto L_0x001d;
            L_0x002f:
                r1 = move-exception;
                r3 = "BluetoothAdapter";
                r6 = "";
                android.util.Log.e(r3, r6, r1);	 Catch:{ all -> 0x0038 }
                goto L_0x001d;
            L_0x0038:
                r3 = move-exception;
                monitor-exit(r5);	 Catch:{ all -> 0x0038 }
                throw r3;	 Catch:{ all -> 0x003b }
            L_0x003b:
                r3 = move-exception;
                monitor-exit(r4);	 Catch:{ all -> 0x003b }
                throw r3;
            L_0x003e:
                r3 = "BluetoothAdapter";
                r6 = "onBluetoothServiceUp: cb is null!!!";
                android.util.Log.d(r3, r6);	 Catch:{ Exception -> 0x002f }
                goto L_0x001d;
            L_0x0047:
                monitor-exit(r5);	 Catch:{ all -> 0x0038 }
                monitor-exit(r4);	 Catch:{ all -> 0x003b }
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothAdapter.1.onBluetoothServiceUp(android.bluetooth.IBluetooth):void");
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onBluetoothServiceDown() {
                /*
                r7 = this;
                r3 = android.bluetooth.BluetoothAdapter.this;
                r4 = r3.mManagerCallback;
                monitor-enter(r4);
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x0067 }
                r5 = 0;
                r3.mService = r5;	 Catch:{ all -> 0x0067 }
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x0067 }
                r3 = r3.mLeScanClients;	 Catch:{ all -> 0x0067 }
                if (r3 == 0) goto L_0x001e;
            L_0x0015:
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x0067 }
                r3 = r3.mLeScanClients;	 Catch:{ all -> 0x0067 }
                r3.clear();	 Catch:{ all -> 0x0067 }
            L_0x001e:
                r3 = android.bluetooth.BluetoothAdapter.sBluetoothLeAdvertiser;	 Catch:{ all -> 0x0067 }
                if (r3 == 0) goto L_0x002b;
            L_0x0024:
                r3 = android.bluetooth.BluetoothAdapter.sBluetoothLeAdvertiser;	 Catch:{ all -> 0x0067 }
                r3.cleanup();	 Catch:{ all -> 0x0067 }
            L_0x002b:
                r3 = android.bluetooth.BluetoothAdapter.sBluetoothLeScanner;	 Catch:{ all -> 0x0067 }
                if (r3 == 0) goto L_0x0038;
            L_0x0031:
                r3 = android.bluetooth.BluetoothAdapter.sBluetoothLeScanner;	 Catch:{ all -> 0x0067 }
                r3.cleanup();	 Catch:{ all -> 0x0067 }
            L_0x0038:
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x0067 }
                r5 = r3.mProxyServiceStateCallbacks;	 Catch:{ all -> 0x0067 }
                monitor-enter(r5);	 Catch:{ all -> 0x0067 }
                r3 = android.bluetooth.BluetoothAdapter.this;	 Catch:{ all -> 0x0064 }
                r3 = r3.mProxyServiceStateCallbacks;	 Catch:{ all -> 0x0064 }
                r2 = r3.iterator();	 Catch:{ all -> 0x0064 }
            L_0x0049:
                r3 = r2.hasNext();	 Catch:{ all -> 0x0064 }
                if (r3 == 0) goto L_0x0073;
            L_0x004f:
                r0 = r2.next();	 Catch:{ all -> 0x0064 }
                r0 = (android.bluetooth.IBluetoothManagerCallback) r0;	 Catch:{ all -> 0x0064 }
                if (r0 == 0) goto L_0x006a;
            L_0x0057:
                r0.onBluetoothServiceDown();	 Catch:{ Exception -> 0x005b }
                goto L_0x0049;
            L_0x005b:
                r1 = move-exception;
                r3 = "BluetoothAdapter";
                r6 = "";
                android.util.Log.e(r3, r6, r1);	 Catch:{ all -> 0x0064 }
                goto L_0x0049;
            L_0x0064:
                r3 = move-exception;
                monitor-exit(r5);	 Catch:{ all -> 0x0064 }
                throw r3;	 Catch:{ all -> 0x0067 }
            L_0x0067:
                r3 = move-exception;
                monitor-exit(r4);	 Catch:{ all -> 0x0067 }
                throw r3;
            L_0x006a:
                r3 = "BluetoothAdapter";
                r6 = "onBluetoothServiceDown: cb is null!!!";
                android.util.Log.d(r3, r6);	 Catch:{ Exception -> 0x005b }
                goto L_0x0049;
            L_0x0073:
                monitor-exit(r5);	 Catch:{ all -> 0x0064 }
                monitor-exit(r4);	 Catch:{ all -> 0x0067 }
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothAdapter.1.onBluetoothServiceDown():void");
            }
        };
        this.mProxyServiceStateCallbacks = new ArrayList();
        if (managerService == null) {
            throw new IllegalArgumentException("bluetooth manager service is null");
        }
        try {
            this.mService = managerService.registerAdapter(this.mManagerCallback);
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
        }
        this.mManagerService = managerService;
        this.mLeScanClients = new HashMap();
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return new BluetoothDevice(address);
    }

    public BluetoothDevice getRemoteDevice(byte[] address) {
        if (address == null || address.length != 6) {
            throw new IllegalArgumentException("Bluetooth address must have 6 bytes");
        }
        return new BluetoothDevice(String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", new Object[]{Byte.valueOf(address[STATE_DISCONNECTED]), Byte.valueOf(address[STATE_CONNECTING]), Byte.valueOf(address[STATE_CONNECTED]), Byte.valueOf(address[STATE_DISCONNECTING]), Byte.valueOf(address[4]), Byte.valueOf(address[5])}));
    }

    public BluetoothLeAdvertiser getBluetoothLeAdvertiser() {
        if (getState() != STATE_ON) {
            return null;
        }
        if (isMultipleAdvertisementSupported() || isPeripheralModeSupported()) {
            synchronized (this.mLock) {
                if (sBluetoothLeAdvertiser == null) {
                    sBluetoothLeAdvertiser = new BluetoothLeAdvertiser(this.mManagerService);
                }
            }
            return sBluetoothLeAdvertiser;
        }
        Log.e(TAG, "bluetooth le advertising not supported");
        return null;
    }

    public BluetoothLeScanner getBluetoothLeScanner() {
        if (getState() != STATE_ON) {
            return null;
        }
        synchronized (this.mLock) {
            if (sBluetoothLeScanner == null) {
                sBluetoothLeScanner = new BluetoothLeScanner(this.mManagerService);
            }
        }
        return sBluetoothLeScanner;
    }

    public boolean isEnabled() {
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    boolean isEnabled = this.mService.isEnabled();
                    return isEnabled;
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
        }
    }

    public int getState() {
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    int state = this.mService.getState();
                    return state;
                }
                Log.d(TAG, ProxyInfo.LOCAL_EXCL_LIST + hashCode() + ": getState() :  mService = null. Returning STATE_OFF");
                return STATE_OFF;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
        }
    }

    public boolean enable() {
        if (isEnabled() == DBG) {
            Log.d(TAG, "enable(): BT is already enabled..!");
            return DBG;
        }
        try {
            return this.mManagerService.enable();
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public boolean disable() {
        try {
            return this.mManagerService.disable(DBG);
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public boolean disable(boolean persist) {
        try {
            return this.mManagerService.disable(persist);
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public String getAddress() {
        try {
            return this.mManagerService.getAddress();
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return null;
        }
    }

    public String getName() {
        try {
            return this.mManagerService.getName();
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return null;
        }
    }

    public boolean configHciSnoopLog(boolean enable) {
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    boolean configHciSnoopLog = this.mService.configHciSnoopLog(enable);
                    return configHciSnoopLog;
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
        }
    }

    public ParcelUuid[] getUuids() {
        if (getState() != STATE_ON) {
            return null;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    ParcelUuid[] uuids = this.mService.getUuids();
                    return uuids;
                }
                return null;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return null;
        }
    }

    public boolean setName(String name) {
        if (getState() != STATE_ON) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    boolean name2 = this.mService.setName(name);
                    return name2;
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public int getScanMode() {
        if (getState() != STATE_ON) {
            return SCAN_MODE_NONE;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    int scanMode = this.mService.getScanMode();
                    return scanMode;
                }
                return SCAN_MODE_NONE;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return SCAN_MODE_NONE;
        }
    }

    public boolean setScanMode(int mode, int duration) {
        if (getState() != STATE_ON) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    boolean scanMode = this.mService.setScanMode(mode, duration);
                    return scanMode;
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public boolean setScanMode(int mode) {
        if (getState() != STATE_ON) {
            return false;
        }
        return setScanMode(mode, getDiscoverableTimeout());
    }

    public int getDiscoverableTimeout() {
        if (getState() != STATE_ON) {
            return -1;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    int discoverableTimeout = this.mService.getDiscoverableTimeout();
                    return discoverableTimeout;
                }
                return -1;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return -1;
        }
    }

    public void setDiscoverableTimeout(int timeout) {
        if (getState() == STATE_ON) {
            try {
                synchronized (this.mManagerCallback) {
                    if (this.mService != null) {
                        this.mService.setDiscoverableTimeout(timeout);
                    }
                }
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            }
        }
    }

    public boolean startDiscovery() {
        if (getState() != STATE_ON) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    boolean startDiscovery = this.mService.startDiscovery();
                    return startDiscovery;
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public boolean cancelDiscovery() {
        if (getState() != STATE_ON) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    boolean cancelDiscovery = this.mService.cancelDiscovery();
                    return cancelDiscovery;
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public boolean isDiscovering() {
        if (getState() != STATE_ON) {
            return false;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    boolean isDiscovering = this.mService.isDiscovering();
                    return isDiscovering;
                }
                return false;
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public boolean isMultipleAdvertisementSupported() {
        boolean z = false;
        if (getState() == STATE_ON) {
            try {
                z = this.mService.isMultiAdvertisementSupported();
            } catch (RemoteException e) {
                Log.e(TAG, "failed to get isMultipleAdvertisementSupported, error: ", e);
            }
        }
        return z;
    }

    public boolean isPeripheralModeSupported() {
        boolean z = false;
        if (getState() == STATE_ON) {
            try {
                z = this.mService.isPeripheralModeSupported();
            } catch (RemoteException e) {
                Log.e(TAG, "failed to get peripheral mode capability: ", e);
            }
        }
        return z;
    }

    public boolean isOffloadedFilteringSupported() {
        boolean z = false;
        if (getState() == STATE_ON) {
            try {
                z = this.mService.isOffloadedFilteringSupported();
            } catch (RemoteException e) {
                Log.e(TAG, "failed to get isOffloadedFilteringSupported, error: ", e);
            }
        }
        return z;
    }

    public boolean isOffloadedScanBatchingSupported() {
        boolean z = false;
        if (getState() == STATE_ON) {
            try {
                z = this.mService.isOffloadedScanBatchingSupported();
            } catch (RemoteException e) {
                Log.e(TAG, "failed to get isOffloadedScanBatchingSupported, error: ", e);
            }
        }
        return z;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.bluetooth.BluetoothActivityEnergyInfo getControllerActivityEnergyInfo(int r7) {
        /*
        r6 = this;
        r2 = 0;
        r3 = r6.getState();
        r4 = 12;
        if (r3 == r4) goto L_0x000b;
    L_0x0009:
        r1 = r2;
    L_0x000a:
        return r1;
    L_0x000b:
        r3 = r6.mService;	 Catch:{ InterruptedException -> 0x0034, RemoteException -> 0x0052 }
        r3 = r3.isActivityAndEnergyReportingSupported();	 Catch:{ InterruptedException -> 0x0034, RemoteException -> 0x0052 }
        if (r3 != 0) goto L_0x0015;
    L_0x0013:
        r1 = r2;
        goto L_0x000a;
    L_0x0015:
        monitor-enter(r6);	 Catch:{ InterruptedException -> 0x0034, RemoteException -> 0x0052 }
        r3 = 1;
        if (r7 != r3) goto L_0x0023;
    L_0x0019:
        r3 = r6.mService;	 Catch:{ all -> 0x0031 }
        r3.getActivityEnergyInfoFromController();	 Catch:{ all -> 0x0031 }
        r4 = 30;
        r6.wait(r4);	 Catch:{ all -> 0x0031 }
    L_0x0023:
        r3 = r6.mService;	 Catch:{ all -> 0x0031 }
        r1 = r3.reportActivityInfo();	 Catch:{ all -> 0x0031 }
        r3 = r1.isValid();	 Catch:{ all -> 0x0031 }
        if (r3 == 0) goto L_0x004f;
    L_0x002f:
        monitor-exit(r6);	 Catch:{ all -> 0x0031 }
        goto L_0x000a;
    L_0x0031:
        r3 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x0031 }
        throw r3;	 Catch:{ InterruptedException -> 0x0034, RemoteException -> 0x0052 }
    L_0x0034:
        r0 = move-exception;
        r3 = "BluetoothAdapter";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "getControllerActivityEnergyInfoCallback wait interrupted: ";
        r4 = r4.append(r5);
        r4 = r4.append(r0);
        r4 = r4.toString();
        android.util.Log.e(r3, r4);
    L_0x004d:
        r1 = r2;
        goto L_0x000a;
    L_0x004f:
        monitor-exit(r6);	 Catch:{ all -> 0x0031 }
        r1 = r2;
        goto L_0x000a;
    L_0x0052:
        r0 = move-exception;
        r3 = "BluetoothAdapter";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "getControllerActivityEnergyInfoCallback: ";
        r4 = r4.append(r5);
        r4 = r4.append(r0);
        r4 = r4.toString();
        android.util.Log.e(r3, r4);
        goto L_0x004d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.bluetooth.BluetoothAdapter.getControllerActivityEnergyInfo(int):android.bluetooth.BluetoothActivityEnergyInfo");
    }

    public Set<BluetoothDevice> getBondedDevices() {
        if (getState() != STATE_ON) {
            return toDeviceSet(new BluetoothDevice[STATE_DISCONNECTED]);
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    Set<BluetoothDevice> toDeviceSet = toDeviceSet(this.mService.getBondedDevices());
                    return toDeviceSet;
                }
                return toDeviceSet(new BluetoothDevice[STATE_DISCONNECTED]);
            }
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return null;
        }
    }

    public int getConnectionState() {
        if (getState() != STATE_ON) {
            return STATE_DISCONNECTED;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    int adapterConnectionState = this.mService.getAdapterConnectionState();
                    return adapterConnectionState;
                }
                return STATE_DISCONNECTED;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getConnectionState:", e);
            return STATE_DISCONNECTED;
        }
    }

    public int getProfileConnectionState(int profile) {
        if (getState() != STATE_ON) {
            return STATE_DISCONNECTED;
        }
        try {
            synchronized (this.mManagerCallback) {
                if (this.mService != null) {
                    int profileConnectionState = this.mService.getProfileConnectionState(profile);
                    return profileConnectionState;
                }
                return STATE_DISCONNECTED;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "getProfileConnectionState:", e);
            return STATE_DISCONNECTED;
        }
    }

    public BluetoothServerSocket listenUsingRfcommOn(int channel) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket((int) STATE_CONNECTING, (boolean) DBG, (boolean) DBG, channel);
        int errno = socket.mSocket.bindListen();
        if (errno == 0) {
            return socket;
        }
        throw new IOException("Error: " + errno);
    }

    public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(name, uuid, DBG, DBG);
    }

    public BluetoothServerSocket listenUsingInsecureRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(name, uuid, false, false);
    }

    public BluetoothServerSocket listenUsingEncryptedRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return createNewRfcommSocketAndRecord(name, uuid, false, DBG);
    }

    private BluetoothServerSocket createNewRfcommSocketAndRecord(String name, UUID uuid, boolean auth, boolean encrypt) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket((int) STATE_CONNECTING, auth, encrypt, new ParcelUuid(uuid));
        socket.setServiceName(name);
        int errno = socket.mSocket.bindListen();
        if (errno == 0) {
            return socket;
        }
        throw new IOException("Error: " + errno);
    }

    public BluetoothServerSocket listenUsingInsecureRfcommOn(int port) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket((int) STATE_CONNECTING, false, false, port);
        int errno = socket.mSocket.bindListen();
        if (errno == 0) {
            return socket;
        }
        throw new IOException("Error: " + errno);
    }

    public BluetoothServerSocket listenUsingEncryptedRfcommOn(int port) throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket((int) STATE_CONNECTING, false, (boolean) DBG, port);
        int errno = socket.mSocket.bindListen();
        if (errno >= 0) {
            return socket;
        }
        throw new IOException("Error: " + errno);
    }

    public static BluetoothServerSocket listenUsingScoOn() throws IOException {
        BluetoothServerSocket socket = new BluetoothServerSocket((int) STATE_CONNECTED, false, false, -1);
        return socket.mSocket.bindListen() < 0 ? socket : socket;
    }

    public Pair<byte[], byte[]> readOutOfBandData() {
        return getState() != STATE_ON ? null : null;
    }

    public boolean getProfileProxy(Context context, ServiceListener listener, int profile) {
        if (context == null || listener == null) {
            return false;
        }
        if (profile == STATE_CONNECTING) {
            BluetoothHeadset headset = new BluetoothHeadset(context, listener);
            return DBG;
        } else if (profile == STATE_CONNECTED) {
            BluetoothA2dp a2dp = new BluetoothA2dp(context, listener);
            return DBG;
        } else if (profile == STATE_OFF) {
            BluetoothA2dpSink a2dpSink = new BluetoothA2dpSink(context, listener);
            return DBG;
        } else if (profile == STATE_TURNING_ON) {
            BluetoothAvrcpController avrcp = new BluetoothAvrcpController(context, listener);
            return DBG;
        } else if (profile == 4) {
            BluetoothInputDevice iDev = new BluetoothInputDevice(context, listener);
            return DBG;
        } else if (profile == 5) {
            BluetoothPan pan = new BluetoothPan(context, listener);
            return DBG;
        } else if (profile == STATE_DISCONNECTING) {
            BluetoothHealth health = new BluetoothHealth(context, listener);
            return DBG;
        } else if (profile == 9) {
            BluetoothMap map = new BluetoothMap(context, listener);
            return DBG;
        } else if (profile != 16) {
            return false;
        } else {
            BluetoothHeadsetClient headsetClient = new BluetoothHeadsetClient(context, listener);
            return DBG;
        }
    }

    public void closeProfileProxy(int profile, BluetoothProfile proxy) {
        if (proxy != null) {
            switch (profile) {
                case STATE_CONNECTING /*1*/:
                    ((BluetoothHeadset) proxy).close();
                case STATE_CONNECTED /*2*/:
                    ((BluetoothA2dp) proxy).close();
                case STATE_DISCONNECTING /*3*/:
                    ((BluetoothHealth) proxy).close();
                case ViewGroupAction.TAG /*4*/:
                    ((BluetoothInputDevice) proxy).close();
                case ReflectionActionWithoutParams.TAG /*5*/:
                    ((BluetoothPan) proxy).close();
                case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                    ((BluetoothGatt) proxy).close();
                case SetPendingIntentTemplate.TAG /*8*/:
                    ((BluetoothGattServer) proxy).close();
                case SetOnClickFillInIntent.TAG /*9*/:
                    ((BluetoothMap) proxy).close();
                case STATE_OFF /*10*/:
                    ((BluetoothA2dpSink) proxy).close();
                case STATE_TURNING_ON /*11*/:
                    ((BluetoothAvrcpController) proxy).close();
                case RelativeLayout.START_OF /*16*/:
                    ((BluetoothHeadsetClient) proxy).close();
                default:
            }
        }
    }

    public boolean enableNoAutoConnect() {
        if (isEnabled() == DBG) {
            Log.d(TAG, "enableNoAutoConnect(): BT is already enabled..!");
            return DBG;
        }
        try {
            return this.mManagerService.enableNoAutoConnect();
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
            return false;
        }
    }

    public boolean changeApplicationBluetoothState(boolean on, BluetoothStateChangeCallback callback) {
        return callback == null ? false : false;
    }

    private Set<BluetoothDevice> toDeviceSet(BluetoothDevice[] devices) {
        return Collections.unmodifiableSet(new HashSet(Arrays.asList(devices)));
    }

    protected void finalize() throws Throwable {
        try {
            this.mManagerService.unregisterAdapter(this.mManagerCallback);
        } catch (RemoteException e) {
            Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
        } finally {
            super.finalize();
        }
    }

    public static boolean checkBluetoothAddress(String address) {
        if (address == null || address.length() != ADDRESS_LENGTH) {
            return false;
        }
        for (int i = STATE_DISCONNECTED; i < ADDRESS_LENGTH; i += STATE_CONNECTING) {
            char c = address.charAt(i);
            switch (i % STATE_DISCONNECTING) {
                case STATE_DISCONNECTED /*0*/:
                case STATE_CONNECTING /*1*/:
                    if (c < '0' || c > '9') {
                        if (c >= 'A' && c <= 'F') {
                            break;
                        }
                        return false;
                    }
                    break;
                    break;
                case STATE_CONNECTED /*2*/:
                    if (c == ':') {
                        break;
                    }
                    return false;
                default:
                    break;
            }
        }
        return DBG;
    }

    IBluetoothManager getBluetoothManager() {
        return this.mManagerService;
    }

    IBluetooth getBluetoothService(IBluetoothManagerCallback cb) {
        synchronized (this.mProxyServiceStateCallbacks) {
            if (cb == null) {
                Log.w(TAG, "getBluetoothService() called with no BluetoothManagerCallback");
            } else if (!this.mProxyServiceStateCallbacks.contains(cb)) {
                this.mProxyServiceStateCallbacks.add(cb);
            }
        }
        return this.mService;
    }

    void removeServiceStateCallback(IBluetoothManagerCallback cb) {
        synchronized (this.mProxyServiceStateCallbacks) {
            this.mProxyServiceStateCallbacks.remove(cb);
        }
    }

    @Deprecated
    public boolean startLeScan(LeScanCallback callback) {
        return startLeScan(null, callback);
    }

    @Deprecated
    public boolean startLeScan(UUID[] serviceUuids, LeScanCallback callback) {
        Log.d(TAG, "startLeScan(): " + serviceUuids);
        if (callback == null) {
            Log.e(TAG, "startLeScan: null callback");
            return false;
        }
        BluetoothLeScanner scanner = getBluetoothLeScanner();
        if (scanner == null) {
            Log.e(TAG, "startLeScan: cannot get BluetoothLeScanner");
            return false;
        }
        synchronized (this.mLeScanClients) {
            if (this.mLeScanClients.containsKey(callback)) {
                Log.e(TAG, "LE Scan has already started");
                return false;
            }
            try {
                if (this.mManagerService.getBluetoothGatt() == null) {
                    return false;
                }
                ScanCallback scanCallback = new AnonymousClass2(this, serviceUuids, callback);
                ScanSettings settings = new Builder().setCallbackType(STATE_CONNECTING).setScanMode(STATE_CONNECTED).build();
                List<ScanFilter> filters = new ArrayList();
                if (serviceUuids != null && serviceUuids.length > 0) {
                    filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(serviceUuids[STATE_DISCONNECTED])).build());
                }
                scanner.startScan(filters, settings, scanCallback);
                this.mLeScanClients.put(callback, scanCallback);
                return DBG;
            } catch (RemoteException e) {
                Log.e(TAG, ProxyInfo.LOCAL_EXCL_LIST, e);
                return false;
            }
        }
    }

    @Deprecated
    public void stopLeScan(LeScanCallback callback) {
        Log.d(TAG, "stopLeScan()");
        BluetoothLeScanner scanner = getBluetoothLeScanner();
        if (scanner != null) {
            synchronized (this.mLeScanClients) {
                ScanCallback scanCallback = (ScanCallback) this.mLeScanClients.remove(callback);
                if (scanCallback == null) {
                    Log.d(TAG, "scan not started yet");
                    return;
                }
                scanner.stopScan(scanCallback);
            }
        }
    }
}
