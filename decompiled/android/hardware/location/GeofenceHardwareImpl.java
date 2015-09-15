package android.hardware.location;

import android.Manifest.permission;
import android.content.Context;
import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;

public final class GeofenceHardwareImpl {
    private static final int ADD_GEOFENCE_CALLBACK = 2;
    private static final int CALLBACK_ADD = 2;
    private static final int CALLBACK_REMOVE = 3;
    private static final boolean DEBUG = false;
    private static final int GEOFENCE_CALLBACK_BINDER_DIED = 6;
    private static final int GEOFENCE_STATUS = 1;
    private static final int GEOFENCE_TRANSITION_CALLBACK = 1;
    private static final int LOCATION_HAS_ACCURACY = 16;
    private static final int LOCATION_HAS_ALTITUDE = 2;
    private static final int LOCATION_HAS_BEARING = 8;
    private static final int LOCATION_HAS_LAT_LONG = 1;
    private static final int LOCATION_HAS_SPEED = 4;
    private static final int LOCATION_INVALID = 0;
    private static final int MONITOR_CALLBACK_BINDER_DIED = 4;
    private static final int PAUSE_GEOFENCE_CALLBACK = 4;
    private static final int REAPER_GEOFENCE_ADDED = 1;
    private static final int REAPER_MONITOR_CALLBACK_ADDED = 2;
    private static final int REAPER_REMOVED = 3;
    private static final int REMOVE_GEOFENCE_CALLBACK = 3;
    private static final int RESOLUTION_LEVEL_COARSE = 2;
    private static final int RESOLUTION_LEVEL_FINE = 3;
    private static final int RESOLUTION_LEVEL_NONE = 1;
    private static final int RESUME_GEOFENCE_CALLBACK = 5;
    private static final String TAG = "GeofenceHardwareImpl";
    private static GeofenceHardwareImpl sInstance;
    private final ArrayList<IGeofenceHardwareMonitorCallback>[] mCallbacks;
    private Handler mCallbacksHandler;
    private final Context mContext;
    private IFusedGeofenceHardware mFusedService;
    private Handler mGeofenceHandler;
    private final SparseArray<IGeofenceHardwareCallback> mGeofences;
    private IGpsGeofenceHardware mGpsService;
    private Handler mReaperHandler;
    private final ArrayList<Reaper> mReapers;
    private int[] mSupportedMonitorTypes;
    private WakeLock mWakeLock;

    private class GeofenceTransition {
        private int mGeofenceId;
        private Location mLocation;
        private int mMonitoringType;
        private int mSourcesUsed;
        private long mTimestamp;
        private int mTransition;
        final /* synthetic */ GeofenceHardwareImpl this$0;

        GeofenceTransition(android.hardware.location.GeofenceHardwareImpl r1, int r2, int r3, long r4, android.location.Location r6, int r7, int r8) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.<init>(android.hardware.location.GeofenceHardwareImpl, int, int, long, android.location.Location, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.<init>(android.hardware.location.GeofenceHardwareImpl, int, int, long, android.location.Location, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.<init>(android.hardware.location.GeofenceHardwareImpl, int, int, long, android.location.Location, int, int):void");
        }

        static /* synthetic */ int access$1000(android.hardware.location.GeofenceHardwareImpl.GeofenceTransition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$1000(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$1000(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$1000(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int");
        }

        static /* synthetic */ int access$600(android.hardware.location.GeofenceHardwareImpl.GeofenceTransition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$600(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$600(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$600(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int");
        }

        static /* synthetic */ int access$700(android.hardware.location.GeofenceHardwareImpl.GeofenceTransition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$700(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$700(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$700(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):int");
        }

        static /* synthetic */ android.location.Location access$800(android.hardware.location.GeofenceHardwareImpl.GeofenceTransition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$800(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):android.location.Location
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$800(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):android.location.Location
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$800(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):android.location.Location");
        }

        static /* synthetic */ long access$900(android.hardware.location.GeofenceHardwareImpl.GeofenceTransition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$900(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):long
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$900(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):long
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.GeofenceTransition.access$900(android.hardware.location.GeofenceHardwareImpl$GeofenceTransition):long");
        }
    }

    class Reaper implements DeathRecipient {
        private IGeofenceHardwareCallback mCallback;
        private IGeofenceHardwareMonitorCallback mMonitorCallback;
        private int mMonitoringType;
        final /* synthetic */ GeofenceHardwareImpl this$0;

        Reaper(android.hardware.location.GeofenceHardwareImpl r1, android.hardware.location.IGeofenceHardwareCallback r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.<init>(android.hardware.location.GeofenceHardwareImpl, android.hardware.location.IGeofenceHardwareCallback, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.<init>(android.hardware.location.GeofenceHardwareImpl, android.hardware.location.IGeofenceHardwareCallback, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.<init>(android.hardware.location.GeofenceHardwareImpl, android.hardware.location.IGeofenceHardwareCallback, int):void");
        }

        Reaper(android.hardware.location.GeofenceHardwareImpl r1, android.hardware.location.IGeofenceHardwareMonitorCallback r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.<init>(android.hardware.location.GeofenceHardwareImpl, android.hardware.location.IGeofenceHardwareMonitorCallback, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.<init>(android.hardware.location.GeofenceHardwareImpl, android.hardware.location.IGeofenceHardwareMonitorCallback, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.<init>(android.hardware.location.GeofenceHardwareImpl, android.hardware.location.IGeofenceHardwareMonitorCallback, int):void");
        }

        static /* synthetic */ android.hardware.location.IGeofenceHardwareCallback access$300(android.hardware.location.GeofenceHardwareImpl.Reaper r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.access$300(android.hardware.location.GeofenceHardwareImpl$Reaper):android.hardware.location.IGeofenceHardwareCallback
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.access$300(android.hardware.location.GeofenceHardwareImpl$Reaper):android.hardware.location.IGeofenceHardwareCallback
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.access$300(android.hardware.location.GeofenceHardwareImpl$Reaper):android.hardware.location.IGeofenceHardwareCallback");
        }

        private boolean callbackEquals(android.hardware.location.IGeofenceHardwareCallback r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.callbackEquals(android.hardware.location.IGeofenceHardwareCallback):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.callbackEquals(android.hardware.location.IGeofenceHardwareCallback):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.callbackEquals(android.hardware.location.IGeofenceHardwareCallback):boolean");
        }

        private boolean unlinkToDeath() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.unlinkToDeath():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.unlinkToDeath():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.unlinkToDeath():boolean");
        }

        public void binderDied() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.binderDied():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.binderDied():void");
        }

        public boolean equals(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.equals(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.equals(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.location.GeofenceHardwareImpl.Reaper.hashCode():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.location.GeofenceHardwareImpl.Reaper.hashCode():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.Reaper.hashCode():int");
        }

        private boolean binderEquals(IInterface left, IInterface right) {
            boolean z = GeofenceHardwareImpl.DEBUG;
            if (left != null) {
                if (right != null && left.asBinder() == right.asBinder()) {
                    z = true;
                }
                return z;
            } else if (right == null) {
                return true;
            } else {
                return GeofenceHardwareImpl.DEBUG;
            }
        }
    }

    static {
        DEBUG = Log.isLoggable(TAG, RESOLUTION_LEVEL_FINE);
    }

    public static synchronized GeofenceHardwareImpl getInstance(Context context) {
        GeofenceHardwareImpl geofenceHardwareImpl;
        synchronized (GeofenceHardwareImpl.class) {
            if (sInstance == null) {
                sInstance = new GeofenceHardwareImpl(context);
            }
            geofenceHardwareImpl = sInstance;
        }
        return geofenceHardwareImpl;
    }

    private GeofenceHardwareImpl(Context context) {
        this.mGeofences = new SparseArray();
        this.mCallbacks = new ArrayList[RESOLUTION_LEVEL_COARSE];
        this.mReapers = new ArrayList();
        this.mSupportedMonitorTypes = new int[RESOLUTION_LEVEL_COARSE];
        this.mGeofenceHandler = new Handler() {
            public void handleMessage(Message msg) {
                IGeofenceHardwareCallback callback;
                int geofenceId;
                int i;
                switch (msg.what) {
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_NONE /*1*/:
                        GeofenceTransition geofenceTransition = (GeofenceTransition) msg.obj;
                        synchronized (GeofenceHardwareImpl.this.mGeofences) {
                            callback = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(GeofenceTransition.access$600(geofenceTransition));
                            if (GeofenceHardwareImpl.DEBUG) {
                                Log.d(GeofenceHardwareImpl.TAG, "GeofenceTransistionCallback: GPS : GeofenceId: " + GeofenceTransition.access$600(geofenceTransition) + " Transition: " + GeofenceTransition.access$700(geofenceTransition) + " Location: " + GeofenceTransition.access$800(geofenceTransition) + ":" + GeofenceHardwareImpl.this.mGeofences);
                            }
                            break;
                        }
                        if (callback != null) {
                            try {
                                callback.onGeofenceTransition(GeofenceTransition.access$600(geofenceTransition), GeofenceTransition.access$700(geofenceTransition), GeofenceTransition.access$800(geofenceTransition), GeofenceTransition.access$900(geofenceTransition), GeofenceTransition.access$1000(geofenceTransition));
                            } catch (RemoteException e) {
                            }
                        }
                        GeofenceHardwareImpl.this.releaseWakeLock();
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_COARSE /*2*/:
                        geofenceId = msg.arg1;
                        synchronized (GeofenceHardwareImpl.this.mGeofences) {
                            callback = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId);
                            break;
                        }
                        if (callback != null) {
                            try {
                                callback.onGeofenceAdd(geofenceId, msg.arg2);
                            } catch (RemoteException e2) {
                                Log.i(GeofenceHardwareImpl.TAG, "Remote Exception:" + e2);
                            }
                        }
                        GeofenceHardwareImpl.this.releaseWakeLock();
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_FINE /*3*/:
                        geofenceId = msg.arg1;
                        synchronized (GeofenceHardwareImpl.this.mGeofences) {
                            callback = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId);
                            break;
                        }
                        if (callback != null) {
                            try {
                                callback.onGeofenceRemove(geofenceId, msg.arg2);
                            } catch (RemoteException e3) {
                            }
                            IBinder callbackBinder = callback.asBinder();
                            boolean callbackInUse = GeofenceHardwareImpl.DEBUG;
                            synchronized (GeofenceHardwareImpl.this.mGeofences) {
                                GeofenceHardwareImpl.this.mGeofences.remove(geofenceId);
                                i = GeofenceHardwareImpl.LOCATION_INVALID;
                                while (i < GeofenceHardwareImpl.this.mGeofences.size()) {
                                    if (((IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.valueAt(i)).asBinder() == callbackBinder) {
                                        callbackInUse = true;
                                    } else {
                                        i += GeofenceHardwareImpl.RESOLUTION_LEVEL_NONE;
                                    }
                                }
                                break;
                            }
                            if (!callbackInUse) {
                                Iterator<Reaper> iterator = GeofenceHardwareImpl.this.mReapers.iterator();
                                while (iterator.hasNext()) {
                                    Reaper reaper = (Reaper) iterator.next();
                                    if (Reaper.access$300(reaper) != null && Reaper.access$300(reaper).asBinder() == callbackBinder) {
                                        iterator.remove();
                                        reaper.unlinkToDeath();
                                        if (GeofenceHardwareImpl.DEBUG) {
                                            String str = GeofenceHardwareImpl.TAG;
                                            Object[] objArr = new Object[GeofenceHardwareImpl.RESOLUTION_LEVEL_COARSE];
                                            objArr[GeofenceHardwareImpl.LOCATION_INVALID] = reaper;
                                            objArr[GeofenceHardwareImpl.RESOLUTION_LEVEL_NONE] = callbackBinder;
                                            Log.d(str, String.format("Removed reaper %s because binder %s is no longer needed.", objArr));
                                        }
                                    }
                                }
                            }
                        }
                        GeofenceHardwareImpl.this.releaseWakeLock();
                    case GeofenceHardwareImpl.PAUSE_GEOFENCE_CALLBACK /*4*/:
                        geofenceId = msg.arg1;
                        synchronized (GeofenceHardwareImpl.this.mGeofences) {
                            callback = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId);
                            break;
                        }
                        if (callback != null) {
                            try {
                                callback.onGeofencePause(geofenceId, msg.arg2);
                            } catch (RemoteException e4) {
                            }
                        }
                        GeofenceHardwareImpl.this.releaseWakeLock();
                    case GeofenceHardwareImpl.RESUME_GEOFENCE_CALLBACK /*5*/:
                        geofenceId = msg.arg1;
                        synchronized (GeofenceHardwareImpl.this.mGeofences) {
                            callback = (IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.get(geofenceId);
                            break;
                        }
                        if (callback != null) {
                            try {
                                callback.onGeofenceResume(geofenceId, msg.arg2);
                            } catch (RemoteException e5) {
                            }
                        }
                        GeofenceHardwareImpl.this.releaseWakeLock();
                    case GeofenceHardwareImpl.GEOFENCE_CALLBACK_BINDER_DIED /*6*/:
                        callback = (IGeofenceHardwareCallback) msg.obj;
                        if (GeofenceHardwareImpl.DEBUG) {
                            Log.d(GeofenceHardwareImpl.TAG, "Geofence callback reaped:" + callback);
                        }
                        int monitoringType = msg.arg1;
                        synchronized (GeofenceHardwareImpl.this.mGeofences) {
                            i = GeofenceHardwareImpl.LOCATION_INVALID;
                            while (true) {
                                if (i < GeofenceHardwareImpl.this.mGeofences.size()) {
                                    if (((IGeofenceHardwareCallback) GeofenceHardwareImpl.this.mGeofences.valueAt(i)).equals(callback)) {
                                        geofenceId = GeofenceHardwareImpl.this.mGeofences.keyAt(i);
                                        GeofenceHardwareImpl.this.removeGeofence(GeofenceHardwareImpl.this.mGeofences.keyAt(i), monitoringType);
                                        GeofenceHardwareImpl.this.mGeofences.remove(geofenceId);
                                    }
                                    i += GeofenceHardwareImpl.RESOLUTION_LEVEL_NONE;
                                }
                                break;
                            }
                        }
                    default:
                }
            }
        };
        this.mCallbacksHandler = new Handler() {
            public void handleMessage(Message msg) {
                ArrayList<IGeofenceHardwareMonitorCallback> callbackList;
                IGeofenceHardwareMonitorCallback callback;
                switch (msg.what) {
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_NONE /*1*/:
                        GeofenceHardwareMonitorEvent event = msg.obj;
                        callbackList = GeofenceHardwareImpl.this.mCallbacks[event.getMonitoringType()];
                        if (callbackList != null) {
                            if (GeofenceHardwareImpl.DEBUG) {
                                Log.d(GeofenceHardwareImpl.TAG, "MonitoringSystemChangeCallback: " + event);
                            }
                            Iterator i$ = callbackList.iterator();
                            while (i$.hasNext()) {
                                try {
                                    ((IGeofenceHardwareMonitorCallback) i$.next()).onMonitoringSystemChange(event);
                                } catch (RemoteException e) {
                                    Log.d(GeofenceHardwareImpl.TAG, "Error reporting onMonitoringSystemChange.", e);
                                }
                            }
                        }
                        GeofenceHardwareImpl.this.releaseWakeLock();
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_COARSE /*2*/:
                        int monitoringType = msg.arg1;
                        callback = msg.obj;
                        callbackList = GeofenceHardwareImpl.this.mCallbacks[monitoringType];
                        if (callbackList == null) {
                            callbackList = new ArrayList();
                            GeofenceHardwareImpl.this.mCallbacks[monitoringType] = callbackList;
                        }
                        if (!callbackList.contains(callback)) {
                            callbackList.add(callback);
                        }
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_FINE /*3*/:
                        callback = (IGeofenceHardwareMonitorCallback) msg.obj;
                        callbackList = GeofenceHardwareImpl.this.mCallbacks[msg.arg1];
                        if (callbackList != null) {
                            callbackList.remove(callback);
                        }
                    case GeofenceHardwareImpl.PAUSE_GEOFENCE_CALLBACK /*4*/:
                        callback = (IGeofenceHardwareMonitorCallback) msg.obj;
                        if (GeofenceHardwareImpl.DEBUG) {
                            Log.d(GeofenceHardwareImpl.TAG, "Monitor callback reaped:" + callback);
                        }
                        callbackList = GeofenceHardwareImpl.this.mCallbacks[msg.arg1];
                        if (callbackList != null && callbackList.contains(callback)) {
                            callbackList.remove(callback);
                        }
                    default:
                }
            }
        };
        this.mReaperHandler = new Handler() {
            public void handleMessage(Message msg) {
                Reaper r;
                switch (msg.what) {
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_NONE /*1*/:
                        IGeofenceHardwareCallback callback = msg.obj;
                        r = new Reaper(GeofenceHardwareImpl.this, callback, msg.arg1);
                        if (!GeofenceHardwareImpl.this.mReapers.contains(r)) {
                            GeofenceHardwareImpl.this.mReapers.add(r);
                            try {
                                callback.asBinder().linkToDeath(r, GeofenceHardwareImpl.LOCATION_INVALID);
                            } catch (RemoteException e) {
                            }
                        }
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_COARSE /*2*/:
                        IGeofenceHardwareMonitorCallback monitorCallback = msg.obj;
                        r = new Reaper(GeofenceHardwareImpl.this, monitorCallback, msg.arg1);
                        if (!GeofenceHardwareImpl.this.mReapers.contains(r)) {
                            GeofenceHardwareImpl.this.mReapers.add(r);
                            try {
                                monitorCallback.asBinder().linkToDeath(r, GeofenceHardwareImpl.LOCATION_INVALID);
                            } catch (RemoteException e2) {
                            }
                        }
                    case GeofenceHardwareImpl.RESOLUTION_LEVEL_FINE /*3*/:
                        GeofenceHardwareImpl.this.mReapers.remove((Reaper) msg.obj);
                    default:
                }
            }
        };
        this.mContext = context;
        setMonitorAvailability(LOCATION_INVALID, RESOLUTION_LEVEL_COARSE);
        setMonitorAvailability(RESOLUTION_LEVEL_NONE, RESOLUTION_LEVEL_COARSE);
    }

    private void acquireWakeLock() {
        if (this.mWakeLock == null) {
            this.mWakeLock = ((PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE)).newWakeLock(RESOLUTION_LEVEL_NONE, TAG);
        }
        this.mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
        }
    }

    private void updateGpsHardwareAvailability() {
        boolean gpsSupported;
        try {
            gpsSupported = this.mGpsService.isHardwareGeofenceSupported();
        } catch (RemoteException e) {
            Log.e(TAG, "Remote Exception calling LocationManagerService");
            gpsSupported = DEBUG;
        }
        if (gpsSupported) {
            setMonitorAvailability(LOCATION_INVALID, LOCATION_INVALID);
        }
    }

    private void updateFusedHardwareAvailability() {
        boolean fusedSupported;
        try {
            if (this.mFusedService != null) {
                fusedSupported = this.mFusedService.isSupported();
            } else {
                fusedSupported = DEBUG;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException calling LocationManagerService");
            fusedSupported = DEBUG;
        }
        if (fusedSupported) {
            setMonitorAvailability(RESOLUTION_LEVEL_NONE, LOCATION_INVALID);
        }
    }

    public void setGpsHardwareGeofence(IGpsGeofenceHardware service) {
        if (this.mGpsService == null) {
            this.mGpsService = service;
            updateGpsHardwareAvailability();
        } else if (service == null) {
            this.mGpsService = null;
            Log.w(TAG, "GPS Geofence Hardware service seems to have crashed");
        } else {
            Log.e(TAG, "Error: GpsService being set again.");
        }
    }

    public void setFusedGeofenceHardware(IFusedGeofenceHardware service) {
        if (this.mFusedService == null) {
            this.mFusedService = service;
            updateFusedHardwareAvailability();
        } else if (service == null) {
            this.mFusedService = null;
            Log.w(TAG, "Fused Geofence Hardware service seems to have crashed");
        } else {
            Log.e(TAG, "Error: FusedService being set again");
        }
    }

    public int[] getMonitoringTypes() {
        boolean gpsSupported;
        boolean fusedSupported;
        synchronized (this.mSupportedMonitorTypes) {
            if (this.mSupportedMonitorTypes[LOCATION_INVALID] != RESOLUTION_LEVEL_COARSE) {
                gpsSupported = true;
            } else {
                gpsSupported = DEBUG;
            }
            if (this.mSupportedMonitorTypes[RESOLUTION_LEVEL_NONE] != RESOLUTION_LEVEL_COARSE) {
                fusedSupported = true;
            } else {
                fusedSupported = DEBUG;
            }
        }
        int[] iArr;
        if (gpsSupported) {
            if (fusedSupported) {
                return new int[]{LOCATION_INVALID, RESOLUTION_LEVEL_NONE};
            }
            iArr = new int[RESOLUTION_LEVEL_NONE];
            iArr[LOCATION_INVALID] = LOCATION_INVALID;
            return iArr;
        } else if (!fusedSupported) {
            return new int[LOCATION_INVALID];
        } else {
            iArr = new int[RESOLUTION_LEVEL_NONE];
            iArr[LOCATION_INVALID] = RESOLUTION_LEVEL_NONE;
            return iArr;
        }
    }

    public int getStatusOfMonitoringType(int monitoringType) {
        int i;
        synchronized (this.mSupportedMonitorTypes) {
            if (monitoringType >= this.mSupportedMonitorTypes.length || monitoringType < 0) {
                throw new IllegalArgumentException("Unknown monitoring type");
            }
            i = this.mSupportedMonitorTypes[monitoringType];
        }
        return i;
    }

    public boolean addCircularFence(int monitoringType, GeofenceHardwareRequestParcelable request, IGeofenceHardwareCallback callback) {
        boolean result;
        int geofenceId = request.getId();
        if (DEBUG) {
            Object[] objArr = new Object[RESOLUTION_LEVEL_COARSE];
            objArr[LOCATION_INVALID] = Integer.valueOf(monitoringType);
            objArr[RESOLUTION_LEVEL_NONE] = request;
            Log.d(TAG, String.format("addCircularFence: monitoringType=%d, %s", objArr));
        }
        synchronized (this.mGeofences) {
            this.mGeofences.put(geofenceId, callback);
        }
        switch (monitoringType) {
            case LOCATION_INVALID /*0*/:
                if (this.mGpsService != null) {
                    try {
                        result = this.mGpsService.addCircularHardwareGeofence(request.getId(), request.getLatitude(), request.getLongitude(), request.getRadius(), request.getLastTransition(), request.getMonitorTransitions(), request.getNotificationResponsiveness(), request.getUnknownTimer());
                        break;
                    } catch (RemoteException e) {
                        Log.e(TAG, "AddGeofence: Remote Exception calling LocationManagerService");
                        result = DEBUG;
                        break;
                    }
                }
                return DEBUG;
            case RESOLUTION_LEVEL_NONE /*1*/:
                if (this.mFusedService != null) {
                    try {
                        IFusedGeofenceHardware iFusedGeofenceHardware = this.mFusedService;
                        GeofenceHardwareRequestParcelable[] geofenceHardwareRequestParcelableArr = new GeofenceHardwareRequestParcelable[RESOLUTION_LEVEL_NONE];
                        geofenceHardwareRequestParcelableArr[LOCATION_INVALID] = request;
                        iFusedGeofenceHardware.addGeofences(geofenceHardwareRequestParcelableArr);
                        result = true;
                        break;
                    } catch (RemoteException e2) {
                        Log.e(TAG, "AddGeofence: RemoteException calling LocationManagerService");
                        result = DEBUG;
                        break;
                    }
                }
                return DEBUG;
            default:
                result = DEBUG;
                break;
        }
        if (result) {
            Message m = this.mReaperHandler.obtainMessage(RESOLUTION_LEVEL_NONE, callback);
            m.arg1 = monitoringType;
            this.mReaperHandler.sendMessage(m);
        } else {
            synchronized (this.mGeofences) {
                this.mGeofences.remove(geofenceId);
            }
        }
        if (!DEBUG) {
            return result;
        }
        Log.d(TAG, "addCircularFence: Result is: " + result);
        return result;
    }

    public boolean removeGeofence(int geofenceId, int monitoringType) {
        boolean result;
        if (DEBUG) {
            Log.d(TAG, "Remove Geofence: GeofenceId: " + geofenceId);
        }
        synchronized (this.mGeofences) {
            if (this.mGeofences.get(geofenceId) == null) {
                throw new IllegalArgumentException("Geofence " + geofenceId + " not registered.");
            }
        }
        switch (monitoringType) {
            case LOCATION_INVALID /*0*/:
                if (this.mGpsService != null) {
                    try {
                        result = this.mGpsService.removeHardwareGeofence(geofenceId);
                        break;
                    } catch (RemoteException e) {
                        Log.e(TAG, "RemoveGeofence: Remote Exception calling LocationManagerService");
                        result = DEBUG;
                        break;
                    }
                }
                return DEBUG;
            case RESOLUTION_LEVEL_NONE /*1*/:
                if (this.mFusedService != null) {
                    try {
                        IFusedGeofenceHardware iFusedGeofenceHardware = this.mFusedService;
                        int[] iArr = new int[RESOLUTION_LEVEL_NONE];
                        iArr[LOCATION_INVALID] = geofenceId;
                        iFusedGeofenceHardware.removeGeofences(iArr);
                        result = true;
                        break;
                    } catch (RemoteException e2) {
                        Log.e(TAG, "RemoveGeofence: RemoteException calling LocationManagerService");
                        result = DEBUG;
                        break;
                    }
                }
                return DEBUG;
            default:
                result = DEBUG;
                break;
        }
        if (DEBUG) {
            Log.d(TAG, "removeGeofence: Result is: " + result);
        }
        return result;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean pauseGeofence(int r7, int r8) {
        /*
        r6 = this;
        r1 = 0;
        r2 = DEBUG;
        if (r2 == 0) goto L_0x001d;
    L_0x0005:
        r2 = "GeofenceHardwareImpl";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Pause Geofence: GeofenceId: ";
        r3 = r3.append(r4);
        r3 = r3.append(r7);
        r3 = r3.toString();
        android.util.Log.d(r2, r3);
    L_0x001d:
        r3 = r6.mGeofences;
        monitor-enter(r3);
        r2 = r6.mGeofences;	 Catch:{ all -> 0x0047 }
        r2 = r2.get(r7);	 Catch:{ all -> 0x0047 }
        if (r2 != 0) goto L_0x004a;
    L_0x0028:
        r2 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x0047 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0047 }
        r4.<init>();	 Catch:{ all -> 0x0047 }
        r5 = "Geofence ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0047 }
        r4 = r4.append(r7);	 Catch:{ all -> 0x0047 }
        r5 = " not registered.";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0047 }
        r4 = r4.toString();	 Catch:{ all -> 0x0047 }
        r2.<init>(r4);	 Catch:{ all -> 0x0047 }
        throw r2;	 Catch:{ all -> 0x0047 }
    L_0x0047:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0047 }
        throw r2;
    L_0x004a:
        monitor-exit(r3);	 Catch:{ all -> 0x0047 }
        switch(r8) {
            case 0: goto L_0x006d;
            case 1: goto L_0x0082;
            default: goto L_0x004e;
        };
    L_0x004e:
        r1 = 0;
    L_0x004f:
        r2 = DEBUG;
        if (r2 == 0) goto L_0x006c;
    L_0x0053:
        r2 = "GeofenceHardwareImpl";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "pauseGeofence: Result is: ";
        r3 = r3.append(r4);
        r3 = r3.append(r1);
        r3 = r3.toString();
        android.util.Log.d(r2, r3);
    L_0x006c:
        return r1;
    L_0x006d:
        r2 = r6.mGpsService;
        if (r2 == 0) goto L_0x006c;
    L_0x0071:
        r2 = r6.mGpsService;	 Catch:{ RemoteException -> 0x0078 }
        r1 = r2.pauseHardwareGeofence(r7);	 Catch:{ RemoteException -> 0x0078 }
        goto L_0x004f;
    L_0x0078:
        r0 = move-exception;
        r2 = "GeofenceHardwareImpl";
        r3 = "PauseGeofence: Remote Exception calling LocationManagerService";
        android.util.Log.e(r2, r3);
        r1 = 0;
        goto L_0x004f;
    L_0x0082:
        r2 = r6.mFusedService;
        if (r2 == 0) goto L_0x006c;
    L_0x0086:
        r2 = r6.mFusedService;	 Catch:{ RemoteException -> 0x008d }
        r2.pauseMonitoringGeofence(r7);	 Catch:{ RemoteException -> 0x008d }
        r1 = 1;
        goto L_0x004f;
    L_0x008d:
        r0 = move-exception;
        r2 = "GeofenceHardwareImpl";
        r3 = "PauseGeofence: RemoteException calling LocationManagerService";
        android.util.Log.e(r2, r3);
        r1 = 0;
        goto L_0x004f;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.pauseGeofence(int, int):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean resumeGeofence(int r7, int r8, int r9) {
        /*
        r6 = this;
        r1 = 0;
        r2 = DEBUG;
        if (r2 == 0) goto L_0x001d;
    L_0x0005:
        r2 = "GeofenceHardwareImpl";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Resume Geofence: GeofenceId: ";
        r3 = r3.append(r4);
        r3 = r3.append(r7);
        r3 = r3.toString();
        android.util.Log.d(r2, r3);
    L_0x001d:
        r3 = r6.mGeofences;
        monitor-enter(r3);
        r2 = r6.mGeofences;	 Catch:{ all -> 0x0047 }
        r2 = r2.get(r7);	 Catch:{ all -> 0x0047 }
        if (r2 != 0) goto L_0x004a;
    L_0x0028:
        r2 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x0047 }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0047 }
        r4.<init>();	 Catch:{ all -> 0x0047 }
        r5 = "Geofence ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0047 }
        r4 = r4.append(r7);	 Catch:{ all -> 0x0047 }
        r5 = " not registered.";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0047 }
        r4 = r4.toString();	 Catch:{ all -> 0x0047 }
        r2.<init>(r4);	 Catch:{ all -> 0x0047 }
        throw r2;	 Catch:{ all -> 0x0047 }
    L_0x0047:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0047 }
        throw r2;
    L_0x004a:
        monitor-exit(r3);	 Catch:{ all -> 0x0047 }
        switch(r8) {
            case 0: goto L_0x006d;
            case 1: goto L_0x0082;
            default: goto L_0x004e;
        };
    L_0x004e:
        r1 = 0;
    L_0x004f:
        r2 = DEBUG;
        if (r2 == 0) goto L_0x006c;
    L_0x0053:
        r2 = "GeofenceHardwareImpl";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "resumeGeofence: Result is: ";
        r3 = r3.append(r4);
        r3 = r3.append(r1);
        r3 = r3.toString();
        android.util.Log.d(r2, r3);
    L_0x006c:
        return r1;
    L_0x006d:
        r2 = r6.mGpsService;
        if (r2 == 0) goto L_0x006c;
    L_0x0071:
        r2 = r6.mGpsService;	 Catch:{ RemoteException -> 0x0078 }
        r1 = r2.resumeHardwareGeofence(r7, r9);	 Catch:{ RemoteException -> 0x0078 }
        goto L_0x004f;
    L_0x0078:
        r0 = move-exception;
        r2 = "GeofenceHardwareImpl";
        r3 = "ResumeGeofence: Remote Exception calling LocationManagerService";
        android.util.Log.e(r2, r3);
        r1 = 0;
        goto L_0x004f;
    L_0x0082:
        r2 = r6.mFusedService;
        if (r2 == 0) goto L_0x006c;
    L_0x0086:
        r2 = r6.mFusedService;	 Catch:{ RemoteException -> 0x008d }
        r2.resumeMonitoringGeofence(r7, r9);	 Catch:{ RemoteException -> 0x008d }
        r1 = 1;
        goto L_0x004f;
    L_0x008d:
        r0 = move-exception;
        r2 = "GeofenceHardwareImpl";
        r3 = "ResumeGeofence: RemoteException calling LocationManagerService";
        android.util.Log.e(r2, r3);
        r1 = 0;
        goto L_0x004f;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.location.GeofenceHardwareImpl.resumeGeofence(int, int, int):boolean");
    }

    public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
        Message reaperMessage = this.mReaperHandler.obtainMessage(RESOLUTION_LEVEL_COARSE, callback);
        reaperMessage.arg1 = monitoringType;
        this.mReaperHandler.sendMessage(reaperMessage);
        Message m = this.mCallbacksHandler.obtainMessage(RESOLUTION_LEVEL_COARSE, callback);
        m.arg1 = monitoringType;
        this.mCallbacksHandler.sendMessage(m);
        return true;
    }

    public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) {
        Message m = this.mCallbacksHandler.obtainMessage(RESOLUTION_LEVEL_FINE, callback);
        m.arg1 = monitoringType;
        this.mCallbacksHandler.sendMessage(m);
        return true;
    }

    public void reportGeofenceTransition(int geofenceId, Location location, int transition, long transitionTimestamp, int monitoringType, int sourcesUsed) {
        if (location == null) {
            String str = TAG;
            Object[] objArr = new Object[RESOLUTION_LEVEL_NONE];
            objArr[LOCATION_INVALID] = location;
            Log.e(str, String.format("Invalid Geofence Transition: location=%p", objArr));
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "GeofenceTransition| " + location + ", transition:" + transition + ", transitionTimestamp:" + transitionTimestamp + ", monitoringType:" + monitoringType + ", sourcesUsed:" + sourcesUsed);
        }
        GeofenceTransition geofenceTransition = new GeofenceTransition(this, geofenceId, transition, transitionTimestamp, location, monitoringType, sourcesUsed);
        acquireWakeLock();
        this.mGeofenceHandler.obtainMessage(RESOLUTION_LEVEL_NONE, geofenceTransition).sendToTarget();
    }

    public void reportGeofenceMonitorStatus(int monitoringType, int monitoringStatus, Location location, int source) {
        setMonitorAvailability(monitoringType, monitoringStatus);
        acquireWakeLock();
        this.mCallbacksHandler.obtainMessage(RESOLUTION_LEVEL_NONE, new GeofenceHardwareMonitorEvent(monitoringType, monitoringStatus, source, location)).sendToTarget();
    }

    private void reportGeofenceOperationStatus(int operation, int geofenceId, int operationStatus) {
        acquireWakeLock();
        Message message = this.mGeofenceHandler.obtainMessage(operation);
        message.arg1 = geofenceId;
        message.arg2 = operationStatus;
        message.sendToTarget();
    }

    public void reportGeofenceAddStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "AddCallback| id:" + geofenceId + ", status:" + status);
        }
        reportGeofenceOperationStatus(RESOLUTION_LEVEL_COARSE, geofenceId, status);
    }

    public void reportGeofenceRemoveStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "RemoveCallback| id:" + geofenceId + ", status:" + status);
        }
        reportGeofenceOperationStatus(RESOLUTION_LEVEL_FINE, geofenceId, status);
    }

    public void reportGeofencePauseStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "PauseCallbac| id:" + geofenceId + ", status" + status);
        }
        reportGeofenceOperationStatus(PAUSE_GEOFENCE_CALLBACK, geofenceId, status);
    }

    public void reportGeofenceResumeStatus(int geofenceId, int status) {
        if (DEBUG) {
            Log.d(TAG, "ResumeCallback| id:" + geofenceId + ", status:" + status);
        }
        reportGeofenceOperationStatus(RESUME_GEOFENCE_CALLBACK, geofenceId, status);
    }

    private void setMonitorAvailability(int monitor, int val) {
        synchronized (this.mSupportedMonitorTypes) {
            this.mSupportedMonitorTypes[monitor] = val;
        }
    }

    int getMonitoringResolutionLevel(int monitoringType) {
        switch (monitoringType) {
            case LOCATION_INVALID /*0*/:
            case RESOLUTION_LEVEL_NONE /*1*/:
                return RESOLUTION_LEVEL_FINE;
            default:
                return RESOLUTION_LEVEL_NONE;
        }
    }

    int getAllowedResolutionLevel(int pid, int uid) {
        if (this.mContext.checkPermission(permission.ACCESS_FINE_LOCATION, pid, uid) == 0) {
            return RESOLUTION_LEVEL_FINE;
        }
        if (this.mContext.checkPermission(permission.ACCESS_COARSE_LOCATION, pid, uid) == 0) {
            return RESOLUTION_LEVEL_COARSE;
        }
        return RESOLUTION_LEVEL_NONE;
    }
}
