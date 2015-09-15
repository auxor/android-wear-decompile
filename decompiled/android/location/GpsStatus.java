package android.location;

import java.util.Iterator;

public final class GpsStatus {
    public static final int GPS_EVENT_FIRST_FIX = 3;
    public static final int GPS_EVENT_SATELLITE_STATUS = 4;
    public static final int GPS_EVENT_STARTED = 1;
    public static final int GPS_EVENT_STOPPED = 2;
    private static final int NUM_SATELLITES = 255;
    private Iterable<GpsSatellite> mSatelliteList;
    private GpsSatellite[] mSatellites;
    private int mTimeToFirstFix;

    public interface Listener {
        void onGpsStatusChanged(int i);
    }

    public interface NmeaListener {
        void onNmeaReceived(long j, String str);
    }

    private final class SatelliteIterator implements Iterator<GpsSatellite> {
        int mIndex;
        private GpsSatellite[] mSatellites;
        final /* synthetic */ GpsStatus this$0;

        SatelliteIterator(android.location.GpsStatus r1, android.location.GpsSatellite[] r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsStatus.SatelliteIterator.<init>(android.location.GpsStatus, android.location.GpsSatellite[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsStatus.SatelliteIterator.<init>(android.location.GpsStatus, android.location.GpsSatellite[]):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.location.GpsStatus.SatelliteIterator.<init>(android.location.GpsStatus, android.location.GpsSatellite[]):void");
        }

        public boolean hasNext() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsStatus.SatelliteIterator.hasNext():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsStatus.SatelliteIterator.hasNext():boolean
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
            throw new UnsupportedOperationException("Method not decompiled: android.location.GpsStatus.SatelliteIterator.hasNext():boolean");
        }

        public android.location.GpsSatellite next() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsStatus.SatelliteIterator.next():android.location.GpsSatellite
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsStatus.SatelliteIterator.next():android.location.GpsSatellite
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
            throw new UnsupportedOperationException("Method not decompiled: android.location.GpsStatus.SatelliteIterator.next():android.location.GpsSatellite");
        }

        public /* bridge */ /* synthetic */ java.lang.Object m0next() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.location.GpsStatus.SatelliteIterator.next():java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.location.GpsStatus.SatelliteIterator.next():java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.location.GpsStatus.SatelliteIterator.next():java.lang.Object");
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    GpsStatus() {
        this.mSatellites = new GpsSatellite[NUM_SATELLITES];
        this.mSatelliteList = new Iterable<GpsSatellite>() {
            public Iterator<GpsSatellite> iterator() {
                return new SatelliteIterator(GpsStatus.this, GpsStatus.this.mSatellites);
            }
        };
        for (int i = 0; i < this.mSatellites.length; i += GPS_EVENT_STARTED) {
            this.mSatellites[i] = new GpsSatellite(i + GPS_EVENT_STARTED);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    synchronized void setStatus(int r7, int[] r8, float[] r9, float[] r10, float[] r11, int r12, int r13, int r14) {
        /*
        r6 = this;
        monitor-enter(r6);
        r0 = 0;
    L_0x0002:
        r4 = r6.mSatellites;	 Catch:{ all -> 0x0055 }
        r4 = r4.length;	 Catch:{ all -> 0x0055 }
        if (r0 >= r4) goto L_0x0011;
    L_0x0007:
        r4 = r6.mSatellites;	 Catch:{ all -> 0x0055 }
        r4 = r4[r0];	 Catch:{ all -> 0x0055 }
        r5 = 0;
        r4.mValid = r5;	 Catch:{ all -> 0x0055 }
        r0 = r0 + 1;
        goto L_0x0002;
    L_0x0011:
        r0 = 0;
    L_0x0012:
        if (r0 >= r7) goto L_0x0053;
    L_0x0014:
        r4 = r8[r0];	 Catch:{ all -> 0x0055 }
        r1 = r4 + -1;
        r4 = 1;
        r2 = r4 << r1;
        if (r1 < 0) goto L_0x004a;
    L_0x001d:
        r4 = r6.mSatellites;	 Catch:{ all -> 0x0055 }
        r4 = r4.length;	 Catch:{ all -> 0x0055 }
        if (r1 >= r4) goto L_0x004a;
    L_0x0022:
        r4 = r6.mSatellites;	 Catch:{ all -> 0x0055 }
        r3 = r4[r1];	 Catch:{ all -> 0x0055 }
        r4 = 1;
        r3.mValid = r4;	 Catch:{ all -> 0x0055 }
        r4 = r9[r0];	 Catch:{ all -> 0x0055 }
        r3.mSnr = r4;	 Catch:{ all -> 0x0055 }
        r4 = r10[r0];	 Catch:{ all -> 0x0055 }
        r3.mElevation = r4;	 Catch:{ all -> 0x0055 }
        r4 = r11[r0];	 Catch:{ all -> 0x0055 }
        r3.mAzimuth = r4;	 Catch:{ all -> 0x0055 }
        r4 = r12 & r2;
        if (r4 == 0) goto L_0x004d;
    L_0x0039:
        r4 = 1;
    L_0x003a:
        r3.mHasEphemeris = r4;	 Catch:{ all -> 0x0055 }
        r4 = r13 & r2;
        if (r4 == 0) goto L_0x004f;
    L_0x0040:
        r4 = 1;
    L_0x0041:
        r3.mHasAlmanac = r4;	 Catch:{ all -> 0x0055 }
        r4 = r14 & r2;
        if (r4 == 0) goto L_0x0051;
    L_0x0047:
        r4 = 1;
    L_0x0048:
        r3.mUsedInFix = r4;	 Catch:{ all -> 0x0055 }
    L_0x004a:
        r0 = r0 + 1;
        goto L_0x0012;
    L_0x004d:
        r4 = 0;
        goto L_0x003a;
    L_0x004f:
        r4 = 0;
        goto L_0x0041;
    L_0x0051:
        r4 = 0;
        goto L_0x0048;
    L_0x0053:
        monitor-exit(r6);
        return;
    L_0x0055:
        r4 = move-exception;
        monitor-exit(r6);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.location.GpsStatus.setStatus(int, int[], float[], float[], float[], int, int, int):void");
    }

    void setStatus(GpsStatus status) {
        this.mTimeToFirstFix = status.getTimeToFirstFix();
        for (int i = 0; i < this.mSatellites.length; i += GPS_EVENT_STARTED) {
            this.mSatellites[i].setStatus(status.mSatellites[i]);
        }
    }

    void setTimeToFirstFix(int ttff) {
        this.mTimeToFirstFix = ttff;
    }

    public int getTimeToFirstFix() {
        return this.mTimeToFirstFix;
    }

    public Iterable<GpsSatellite> getSatellites() {
        return this.mSatelliteList;
    }

    public int getMaxSatellites() {
        return NUM_SATELLITES;
    }
}
