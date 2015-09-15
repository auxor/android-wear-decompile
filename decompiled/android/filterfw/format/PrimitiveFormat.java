package android.filterfw.format;

import android.filterfw.core.MutableFrameFormat;

public class PrimitiveFormat {
    private static android.filterfw.core.MutableFrameFormat createFormat(int r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterfw.format.PrimitiveFormat.createFormat(int, int):android.filterfw.core.MutableFrameFormat
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterfw.format.PrimitiveFormat.createFormat(int, int):android.filterfw.core.MutableFrameFormat
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterfw.format.PrimitiveFormat.createFormat(int, int):android.filterfw.core.MutableFrameFormat");
    }

    private static android.filterfw.core.MutableFrameFormat createFormat(int r1, int r2, int r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterfw.format.PrimitiveFormat.createFormat(int, int, int):android.filterfw.core.MutableFrameFormat
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterfw.format.PrimitiveFormat.createFormat(int, int, int):android.filterfw.core.MutableFrameFormat
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterfw.format.PrimitiveFormat.createFormat(int, int, int):android.filterfw.core.MutableFrameFormat");
    }

    public static MutableFrameFormat createByteFormat(int count, int target) {
        return createFormat(2, count, target);
    }

    public static MutableFrameFormat createInt16Format(int count, int target) {
        return createFormat(3, count, target);
    }

    public static MutableFrameFormat createInt32Format(int count, int target) {
        return createFormat(4, count, target);
    }

    public static MutableFrameFormat createFloatFormat(int count, int target) {
        return createFormat(5, count, target);
    }

    public static MutableFrameFormat createDoubleFormat(int count, int target) {
        return createFormat(6, count, target);
    }

    public static MutableFrameFormat createByteFormat(int target) {
        return createFormat(2, target);
    }

    public static MutableFrameFormat createInt16Format(int target) {
        return createFormat(3, target);
    }

    public static MutableFrameFormat createInt32Format(int target) {
        return createFormat(4, target);
    }

    public static MutableFrameFormat createFloatFormat(int target) {
        return createFormat(5, target);
    }

    public static MutableFrameFormat createDoubleFormat(int target) {
        return createFormat(6, target);
    }
}
