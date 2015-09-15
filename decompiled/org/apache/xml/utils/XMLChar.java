package org.apache.xml.utils;

import org.apache.xpath.VariableStack;
import org.apache.xpath.axes.WalkerFactory;

public class XMLChar {
    private static final byte[] CHARS;
    public static final int MASK_CONTENT = 32;
    public static final int MASK_NAME = 8;
    public static final int MASK_NAME_START = 4;
    public static final int MASK_NCNAME = 128;
    public static final int MASK_NCNAME_START = 64;
    public static final int MASK_PUBID = 16;
    public static final int MASK_SPACE = 2;
    public static final int MASK_VALID = 1;

    public static boolean isValidIANAEncoding(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XMLChar.isValidIANAEncoding(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XMLChar.isValidIANAEncoding(java.lang.String):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XMLChar.isValidIANAEncoding(java.lang.String):boolean");
    }

    public static boolean isValidJavaEncoding(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XMLChar.isValidJavaEncoding(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XMLChar.isValidJavaEncoding(java.lang.String):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XMLChar.isValidJavaEncoding(java.lang.String):boolean");
    }

    public static boolean isValidNCName(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XMLChar.isValidNCName(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XMLChar.isValidNCName(java.lang.String):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XMLChar.isValidNCName(java.lang.String):boolean");
    }

    public static boolean isValidName(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XMLChar.isValidName(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XMLChar.isValidName(java.lang.String):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XMLChar.isValidName(java.lang.String):boolean");
    }

    public static boolean isValidNmtoken(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XMLChar.isValidNmtoken(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XMLChar.isValidNmtoken(java.lang.String):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XMLChar.isValidNmtoken(java.lang.String):boolean");
    }

    public static boolean isValidQName(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XMLChar.isValidQName(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XMLChar.isValidQName(java.lang.String):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XMLChar.isValidQName(java.lang.String):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static {
        /*
        r17 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r0 = r17;
        r0 = new byte[r0];
        r17 = r0;
        CHARS = r17;
        r17 = 8;
        r0 = r17;
        r1 = new int[r0];
        r1 = {9, 10, 13, 13, 32, 55295, 57344, 65533};
        r17 = 4;
        r0 = r17;
        r15 = new int[r0];
        r15 = {32, 9, 13, 10};
        r17 = 2;
        r0 = r17;
        r11 = new int[r0];
        r11 = {45, 46};
        r17 = 2;
        r0 = r17;
        r12 = new int[r0];
        r12 = {58, 95};
        r17 = 9;
        r0 = r17;
        r13 = new int[r0];
        r13 = {10, 13, 32, 33, 35, 36, 37, 61, 95};
        r17 = 6;
        r0 = r17;
        r14 = new int[r0];
        r14 = {39, 59, 63, 90, 97, 122};
        r17 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        r0 = r17;
        r10 = new int[r0];
        r10 = {65, 90, 97, 122, 192, 214, 216, 246, 248, 305, 308, 318, 321, 328, 330, 382, 384, 451, 461, 496, 500, 501, 506, 535, 592, 680, 699, 705, 904, 906, 910, 929, 931, 974, 976, 982, 994, 1011, 1025, 1036, 1038, 1103, 1105, 1116, 1118, 1153, 1168, 1220, 1223, 1224, 1227, 1228, 1232, 1259, 1262, 1269, 1272, 1273, 1329, 1366, 1377, 1414, 1488, 1514, 1520, 1522, 1569, 1594, 1601, 1610, 1649, 1719, 1722, 1726, 1728, 1742, 1744, 1747, 1765, 1766, 2309, 2361, 2392, 2401, 2437, 2444, 2447, 2448, 2451, 2472, 2474, 2480, 2486, 2489, 2524, 2525, 2527, 2529, 2544, 2545, 2565, 2570, 2575, 2576, 2579, 2600, 2602, 2608, 2610, 2611, 2613, 2614, 2616, 2617, 2649, 2652, 2674, 2676, 2693, 2699, 2703, 2705, 2707, 2728, 2730, 2736, 2738, 2739, 2741, 2745, 2821, 2828, 2831, 2832, 2835, 2856, 2858, 2864, 2866, 2867, 2870, 2873, 2908, 2909, 2911, 2913, 2949, 2954, 2958, 2960, 2962, 2965, 2969, 2970, 2974, 2975, 2979, 2980, 2984, 2986, 2990, 2997, 2999, 3001, 3077, 3084, 3086, 3088, 3090, 3112, 3114, 3123, 3125, 3129, 3168, 3169, 3205, 3212, 3214, 3216, 3218, 3240, 3242, 3251, 3253, 3257, 3296, 3297, 3333, 3340, 3342, 3344, 3346, 3368, 3370, 3385, 3424, 3425, 3585, 3630, 3634, 3635, 3648, 3653, 3713, 3714, 3719, 3720, 3732, 3735, 3737, 3743, 3745, 3747, 3754, 3755, 3757, 3758, 3762, 3763, 3776, 3780, 3904, 3911, 3913, 3945, 4256, 4293, 4304, 4342, 4354, 4355, 4357, 4359, 4363, 4364, 4366, 4370, 4436, 4437, 4447, 4449, 4461, 4462, 4466, 4467, 4526, 4527, 4535, 4536, 4540, 4546, 7680, 7835, 7840, 7929, 7936, 7957, 7960, 7965, 7968, 8005, 8008, 8013, 8016, 8023, 8031, 8061, 8064, 8116, 8118, 8124, 8130, 8132, 8134, 8140, 8144, 8147, 8150, 8155, 8160, 8172, 8178, 8180, 8182, 8188, 8490, 8491, 8576, 8578, 12353, 12436, 12449, 12538, 12549, 12588, 44032, 55203, 12321, 12329, 19968, 40869};
        r17 = 53;
        r0 = r17;
        r9 = new int[r0];
        r9 = {902, 908, 986, 988, 990, 992, 1369, 1749, 2365, 2482, 2654, 2701, 2749, 2784, 2877, 2972, 3294, 3632, 3716, 3722, 3725, 3749, 3751, 3760, 3773, 4352, 4361, 4412, 4414, 4416, 4428, 4430, 4432, 4441, 4451, 4453, 4455, 4457, 4469, 4510, 4520, 4523, 4538, 4587, 4592, 4601, 8025, 8027, 8029, 8126, 8486, 8494, 12295};
        r17 = 132; // 0x84 float:1.85E-43 double:6.5E-322;
        r0 = r17;
        r3 = new int[r0];
        r3 = {768, 837, 864, 865, 1155, 1158, 1425, 1441, 1443, 1465, 1467, 1469, 1473, 1474, 1611, 1618, 1750, 1756, 1757, 1759, 1760, 1764, 1767, 1768, 1770, 1773, 2305, 2307, 2366, 2380, 2385, 2388, 2402, 2403, 2433, 2435, 2496, 2500, 2503, 2504, 2507, 2509, 2530, 2531, 2624, 2626, 2631, 2632, 2635, 2637, 2672, 2673, 2689, 2691, 2750, 2757, 2759, 2761, 2763, 2765, 2817, 2819, 2878, 2883, 2887, 2888, 2891, 2893, 2902, 2903, 2946, 2947, 3006, 3010, 3014, 3016, 3018, 3021, 3073, 3075, 3134, 3140, 3142, 3144, 3146, 3149, 3157, 3158, 3202, 3203, 3262, 3268, 3270, 3272, 3274, 3277, 3285, 3286, 3330, 3331, 3390, 3395, 3398, 3400, 3402, 3405, 3636, 3642, 3655, 3662, 3764, 3769, 3771, 3772, 3784, 3789, 3864, 3865, 3953, 3972, 3974, 3979, 3984, 3989, 3993, 4013, 4017, 4023, 8400, 8412, 12330, 12335};
        r17 = 29;
        r0 = r17;
        r2 = new int[r0];
        r2 = {1471, 1476, 1648, 2364, 2381, 2492, 2494, 2495, 2519, 2562, 2620, 2622, 2623, 2748, 2876, 3031, 3415, 3633, 3761, 3893, 3895, 3897, 3902, 3903, 3991, 4025, 8417, 12441, 12442};
        r17 = 30;
        r0 = r17;
        r4 = new int[r0];
        r4 = {48, 57, 1632, 1641, 1776, 1785, 2406, 2415, 2534, 2543, 2662, 2671, 2790, 2799, 2918, 2927, 3047, 3055, 3174, 3183, 3302, 3311, 3430, 3439, 3664, 3673, 3792, 3801, 3872, 3881};
        r17 = 6;
        r0 = r17;
        r6 = new int[r0];
        r6 = {12337, 12341, 12445, 12446, 12540, 12542};
        r17 = 8;
        r0 = r17;
        r5 = new int[r0];
        r5 = {183, 720, 721, 903, 1600, 3654, 3782, 12293};
        r17 = 5;
        r0 = r17;
        r0 = new int[r0];
        r16 = r0;
        r16 = {60, 38, 10, 13, 93};
        r7 = 0;
    L_0x008b:
        r0 = r1.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x00af;
    L_0x0092:
        r8 = r1[r7];
    L_0x0094:
        r17 = r7 + 1;
        r17 = r1[r17];
        r0 = r17;
        if (r8 > r0) goto L_0x00ac;
    L_0x009c:
        r17 = CHARS;
        r18 = r17[r8];
        r18 = r18 | 33;
        r0 = r18;
        r0 = (byte) r0;
        r18 = r0;
        r17[r8] = r18;
        r8 = r8 + 1;
        goto L_0x0094;
    L_0x00ac:
        r7 = r7 + 2;
        goto L_0x008b;
    L_0x00af:
        r7 = 0;
    L_0x00b0:
        r0 = r16;
        r0 = r0.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x00cf;
    L_0x00b9:
        r17 = CHARS;
        r18 = r16[r7];
        r19 = CHARS;
        r20 = r16[r7];
        r19 = r19[r20];
        r19 = r19 & -33;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x00b0;
    L_0x00cf:
        r7 = 0;
    L_0x00d0:
        r0 = r15.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x00e9;
    L_0x00d7:
        r17 = CHARS;
        r18 = r15[r7];
        r19 = r17[r18];
        r19 = r19 | 2;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x00d0;
    L_0x00e9:
        r7 = 0;
    L_0x00ea:
        r0 = r12.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0107;
    L_0x00f1:
        r17 = CHARS;
        r18 = r12[r7];
        r19 = r17[r18];
        r0 = r19;
        r0 = r0 | 204;
        r19 = r0;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x00ea;
    L_0x0107:
        r7 = 0;
    L_0x0108:
        r0 = r10.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0130;
    L_0x010f:
        r8 = r10[r7];
    L_0x0111:
        r17 = r7 + 1;
        r17 = r10[r17];
        r0 = r17;
        if (r8 > r0) goto L_0x012d;
    L_0x0119:
        r17 = CHARS;
        r18 = r17[r8];
        r0 = r18;
        r0 = r0 | 204;
        r18 = r0;
        r0 = r18;
        r0 = (byte) r0;
        r18 = r0;
        r17[r8] = r18;
        r8 = r8 + 1;
        goto L_0x0111;
    L_0x012d:
        r7 = r7 + 2;
        goto L_0x0108;
    L_0x0130:
        r7 = 0;
    L_0x0131:
        r0 = r9.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x014e;
    L_0x0138:
        r17 = CHARS;
        r18 = r9[r7];
        r19 = r17[r18];
        r0 = r19;
        r0 = r0 | 204;
        r19 = r0;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x0131;
    L_0x014e:
        r7 = 0;
    L_0x014f:
        r0 = r11.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x016c;
    L_0x0156:
        r17 = CHARS;
        r18 = r11[r7];
        r19 = r17[r18];
        r0 = r19;
        r0 = r0 | 136;
        r19 = r0;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x014f;
    L_0x016c:
        r7 = 0;
    L_0x016d:
        r0 = r4.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0195;
    L_0x0174:
        r8 = r4[r7];
    L_0x0176:
        r17 = r7 + 1;
        r17 = r4[r17];
        r0 = r17;
        if (r8 > r0) goto L_0x0192;
    L_0x017e:
        r17 = CHARS;
        r18 = r17[r8];
        r0 = r18;
        r0 = r0 | 136;
        r18 = r0;
        r0 = r18;
        r0 = (byte) r0;
        r18 = r0;
        r17[r8] = r18;
        r8 = r8 + 1;
        goto L_0x0176;
    L_0x0192:
        r7 = r7 + 2;
        goto L_0x016d;
    L_0x0195:
        r7 = 0;
    L_0x0196:
        r0 = r3.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x01be;
    L_0x019d:
        r8 = r3[r7];
    L_0x019f:
        r17 = r7 + 1;
        r17 = r3[r17];
        r0 = r17;
        if (r8 > r0) goto L_0x01bb;
    L_0x01a7:
        r17 = CHARS;
        r18 = r17[r8];
        r0 = r18;
        r0 = r0 | 136;
        r18 = r0;
        r0 = r18;
        r0 = (byte) r0;
        r18 = r0;
        r17[r8] = r18;
        r8 = r8 + 1;
        goto L_0x019f;
    L_0x01bb:
        r7 = r7 + 2;
        goto L_0x0196;
    L_0x01be:
        r7 = 0;
    L_0x01bf:
        r0 = r2.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x01dc;
    L_0x01c6:
        r17 = CHARS;
        r18 = r2[r7];
        r19 = r17[r18];
        r0 = r19;
        r0 = r0 | 136;
        r19 = r0;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x01bf;
    L_0x01dc:
        r7 = 0;
    L_0x01dd:
        r0 = r6.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0205;
    L_0x01e4:
        r8 = r6[r7];
    L_0x01e6:
        r17 = r7 + 1;
        r17 = r6[r17];
        r0 = r17;
        if (r8 > r0) goto L_0x0202;
    L_0x01ee:
        r17 = CHARS;
        r18 = r17[r8];
        r0 = r18;
        r0 = r0 | 136;
        r18 = r0;
        r0 = r18;
        r0 = (byte) r0;
        r18 = r0;
        r17[r8] = r18;
        r8 = r8 + 1;
        goto L_0x01e6;
    L_0x0202:
        r7 = r7 + 2;
        goto L_0x01dd;
    L_0x0205:
        r7 = 0;
    L_0x0206:
        r0 = r5.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0223;
    L_0x020d:
        r17 = CHARS;
        r18 = r5[r7];
        r19 = r17[r18];
        r0 = r19;
        r0 = r0 | 136;
        r19 = r0;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x0206;
    L_0x0223:
        r17 = CHARS;
        r18 = 58;
        r19 = r17[r18];
        r0 = r19;
        r0 = r0 & -193;
        r19 = r0;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = 0;
    L_0x0237:
        r0 = r13.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0250;
    L_0x023e:
        r17 = CHARS;
        r18 = r13[r7];
        r19 = r17[r18];
        r19 = r19 | 16;
        r0 = r19;
        r0 = (byte) r0;
        r19 = r0;
        r17[r18] = r19;
        r7 = r7 + 1;
        goto L_0x0237;
    L_0x0250:
        r7 = 0;
    L_0x0251:
        r0 = r14.length;
        r17 = r0;
        r0 = r17;
        if (r7 >= r0) goto L_0x0275;
    L_0x0258:
        r8 = r14[r7];
    L_0x025a:
        r17 = r7 + 1;
        r17 = r14[r17];
        r0 = r17;
        if (r8 > r0) goto L_0x0272;
    L_0x0262:
        r17 = CHARS;
        r18 = r17[r8];
        r18 = r18 | 16;
        r0 = r18;
        r0 = (byte) r0;
        r18 = r0;
        r17[r8] = r18;
        r8 = r8 + 1;
        goto L_0x025a;
    L_0x0272:
        r7 = r7 + 2;
        goto L_0x0251;
    L_0x0275:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XMLChar.<clinit>():void");
    }

    public static boolean isSupplemental(int c) {
        return c >= WalkerFactory.BIT_CHILD && c <= 1114111;
    }

    public static int supplemental(char h, char l) {
        return (((h - 55296) * VariableStack.CLEARLIMITATION) + (l - 56320)) + WalkerFactory.BIT_CHILD;
    }

    public static char highSurrogate(int c) {
        return (char) (((c - WalkerFactory.BIT_CHILD) >> 10) + 55296);
    }

    public static char lowSurrogate(int c) {
        return (char) (((c - WalkerFactory.BIT_CHILD) & 1023) + 56320);
    }

    public static boolean isHighSurrogate(int c) {
        return 55296 <= c && c <= 56319;
    }

    public static boolean isLowSurrogate(int c) {
        return 56320 <= c && c <= 57343;
    }

    public static boolean isValid(int c) {
        return (c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_VALID) != 0) || (WalkerFactory.BIT_CHILD <= c && c <= 1114111);
    }

    public static boolean isInvalid(int c) {
        return !isValid(c);
    }

    public static boolean isContent(int c) {
        return (c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_CONTENT) != 0) || (WalkerFactory.BIT_CHILD <= c && c <= 1114111);
    }

    public static boolean isMarkup(int c) {
        return c == 60 || c == 38 || c == 37;
    }

    public static boolean isSpace(int c) {
        return c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_SPACE) != 0;
    }

    public static boolean isNameStart(int c) {
        return c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_NAME_START) != 0;
    }

    public static boolean isName(int c) {
        return c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_NAME) != 0;
    }

    public static boolean isNCNameStart(int c) {
        return c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_NCNAME_START) != 0;
    }

    public static boolean isNCName(int c) {
        return c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_NCNAME) != 0;
    }

    public static boolean isPubid(int c) {
        return c < WalkerFactory.BIT_CHILD && (CHARS[c] & MASK_PUBID) != 0;
    }
}
