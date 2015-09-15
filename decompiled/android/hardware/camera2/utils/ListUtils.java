package android.hardware.camera2.utils;

import java.util.List;

public class ListUtils {
    public static <T> java.lang.String listToString(java.util.List<T> r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.camera2.utils.ListUtils.listToString(java.util.List):java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.camera2.utils.ListUtils.listToString(java.util.List):java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.camera2.utils.ListUtils.listToString(java.util.List):java.lang.String");
    }

    public static <T> boolean listContains(List<T> list, T needle) {
        if (list == null) {
            return false;
        }
        return list.contains(needle);
    }

    public static <T> boolean listElementsEqualTo(List<T> list, T single) {
        boolean z = true;
        if (list == null) {
            return false;
        }
        if (!(list.size() == 1 && list.contains(single))) {
            z = false;
        }
        return z;
    }

    public static <T> T listSelectFirstFrom(List<T> list, T[] choices) {
        if (list == null) {
            return null;
        }
        for (T choice : choices) {
            if (list.contains(choice)) {
                return choice;
            }
        }
        return null;
    }

    private ListUtils() {
        throw new AssertionError();
    }
}
