package android.service.fingerprint;

import android.content.ContentResolver;
import android.provider.Settings.Secure;
import java.util.Arrays;

public class FingerprintUtils {
    private static final boolean DEBUG = true;
    private static final String TAG = "FingerprintUtils";

    public static int[] getFingerprintIdsForUser(android.content.ContentResolver r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.service.fingerprint.FingerprintUtils.getFingerprintIdsForUser(android.content.ContentResolver, int):int[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.service.fingerprint.FingerprintUtils.getFingerprintIdsForUser(android.content.ContentResolver, int):int[]
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
        throw new UnsupportedOperationException("Method not decompiled: android.service.fingerprint.FingerprintUtils.getFingerprintIdsForUser(android.content.ContentResolver, int):int[]");
    }

    public static void addFingerprintIdForUser(int fingerId, ContentResolver res, int userId) {
        int[] fingerIds = getFingerprintIdsForUser(res, userId);
        if (fingerId != 0) {
            int i = 0;
            while (i < fingerIds.length) {
                if (fingerIds[i] != fingerId) {
                    i++;
                } else {
                    return;
                }
            }
            int[] newList = Arrays.copyOf(fingerIds, fingerIds.length + 1);
            newList[fingerIds.length] = fingerId;
            Secure.putStringForUser(res, Secure.USER_FINGERPRINT_IDS, Arrays.toString(newList), userId);
        }
    }

    public static boolean removeFingerprintIdForUser(int fingerId, ContentResolver res, int userId) {
        if (fingerId == 0) {
            throw new IllegalStateException("Bad fingerId");
        }
        int[] fingerIds = getFingerprintIdsForUser(res, userId);
        int[] resultIds = Arrays.copyOf(fingerIds, fingerIds.length);
        int resultCount = 0;
        for (int i = 0; i < fingerIds.length; i++) {
            if (fingerId != fingerIds[i]) {
                int resultCount2 = resultCount + 1;
                resultIds[resultCount] = fingerIds[i];
                resultCount = resultCount2;
            }
        }
        if (resultCount <= 0) {
            return false;
        }
        Secure.putStringForUser(res, Secure.USER_FINGERPRINT_IDS, Arrays.toString(Arrays.copyOf(resultIds, resultCount)), userId);
        return DEBUG;
    }
}
