package java.util.concurrent;

import org.w3c.dom.traversal.NodeFilter;
import sun.misc.Unsafe;

public class Exchanger<V> {
    private static final int ABASE;
    private static final int ASHIFT = 7;
    private static final long BLOCKER;
    private static final long BOUND;
    static final int FULL;
    private static final long MATCH;
    private static final int MMASK = 255;
    private static final int NCPU;
    private static final Object NULL_ITEM;
    private static final int SEQ = 256;
    private static final long SLOT;
    private static final int SPINS = 1024;
    private static final Object TIMED_OUT;
    private static final Unsafe U;
    private volatile Node[] arena;
    private volatile int bound;
    private final Participant participant;
    private volatile Node slot;

    static final class Node {
        int bound;
        int collides;
        int hash;
        int index;
        Object item;
        volatile Object match;
        Object p0;
        Object p1;
        Object p2;
        Object p3;
        Object p4;
        Object p5;
        Object p6;
        Object p7;
        Object p8;
        Object p9;
        Object pa;
        volatile Thread parked;
        Object pb;
        Object pc;
        Object pd;
        Object pe;
        Object pf;
        Object q0;
        Object q1;
        Object q2;
        Object q3;
        Object q4;
        Object q5;
        Object q6;
        Object q7;
        Object q8;
        Object q9;
        Object qa;
        Object qb;
        Object qc;
        Object qd;
        Object qe;
        Object qf;

        Node() {
        }
    }

    static final class Participant extends ThreadLocal<Node> {
        Participant() {
        }

        public Node initialValue() {
            return new Node();
        }
    }

    public Exchanger() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.Exchanger.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.Exchanger.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Exchanger.<init>():void");
    }

    private final java.lang.Object arenaExchange(java.lang.Object r1, boolean r2, long r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.Exchanger.arenaExchange(java.lang.Object, boolean, long):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.Exchanger.arenaExchange(java.lang.Object, boolean, long):java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Exchanger.arenaExchange(java.lang.Object, boolean, long):java.lang.Object");
    }

    private final java.lang.Object slotExchange(java.lang.Object r1, boolean r2, long r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.Exchanger.slotExchange(java.lang.Object, boolean, long):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.Exchanger.slotExchange(java.lang.Object, boolean, long):java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Exchanger.slotExchange(java.lang.Object, boolean, long):java.lang.Object");
    }

    public V exchange(V r1, long r2, java.util.concurrent.TimeUnit r4) throws java.lang.InterruptedException, java.util.concurrent.TimeoutException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.Exchanger.exchange(java.lang.Object, long, java.util.concurrent.TimeUnit):V
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.Exchanger.exchange(java.lang.Object, long, java.util.concurrent.TimeUnit):V
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Exchanger.exchange(java.lang.Object, long, java.util.concurrent.TimeUnit):V");
    }

    static {
        int i;
        NCPU = Runtime.getRuntime().availableProcessors();
        if (NCPU >= 510) {
            i = MMASK;
        } else {
            i = NCPU >>> 1;
        }
        FULL = i;
        NULL_ITEM = new Object();
        TIMED_OUT = new Object();
        try {
            U = Unsafe.getUnsafe();
            Class<?> ek = Exchanger.class;
            Class<?> ak = Node[].class;
            BOUND = U.objectFieldOffset(ek.getDeclaredField("bound"));
            SLOT = U.objectFieldOffset(ek.getDeclaredField("slot"));
            MATCH = U.objectFieldOffset(Node.class.getDeclaredField("match"));
            BLOCKER = U.objectFieldOffset(Thread.class.getDeclaredField("parkBlocker"));
            int s = U.arrayIndexScale(ak);
            ABASE = U.arrayBaseOffset(ak) + NodeFilter.SHOW_COMMENT;
            if (((s - 1) & s) != 0 || s > NodeFilter.SHOW_COMMENT) {
                throw new Error("Unsupported array scale");
            }
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public V exchange(V r7) throws java.lang.InterruptedException {
        /*
        r6 = this;
        r4 = 0;
        r3 = 0;
        if (r7 != 0) goto L_0x0023;
    L_0x0005:
        r0 = NULL_ITEM;
    L_0x0007:
        r2 = r6.arena;
        if (r2 != 0) goto L_0x0011;
    L_0x000b:
        r1 = r6.slotExchange(r0, r3, r4);
        if (r1 != 0) goto L_0x0025;
    L_0x0011:
        r2 = java.lang.Thread.interrupted();
        if (r2 != 0) goto L_0x001d;
    L_0x0017:
        r1 = r6.arenaExchange(r0, r3, r4);
        if (r1 != 0) goto L_0x0025;
    L_0x001d:
        r2 = new java.lang.InterruptedException;
        r2.<init>();
        throw r2;
    L_0x0023:
        r0 = r7;
        goto L_0x0007;
    L_0x0025:
        r2 = NULL_ITEM;
        if (r1 != r2) goto L_0x002a;
    L_0x0029:
        r1 = 0;
    L_0x002a:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.Exchanger.exchange(java.lang.Object):V");
    }
}
