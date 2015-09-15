package android.media;

import android.media.MediaCodecInfo.CodecCapabilities;
import java.util.Arrays;

public final class MediaCodecList {
    public static final int ALL_CODECS = 1;
    public static final int REGULAR_CODECS = 0;
    private static final String TAG = "MediaCodecList";
    private static MediaCodecInfo[] sAllCodecInfos;
    private static Object sInitLock;
    private static MediaCodecInfo[] sRegularCodecInfos;
    private MediaCodecInfo[] mCodecInfos;

    static final native int findCodecByName(String str);

    static final native CodecCapabilities getCodecCapabilities(int i, String str);

    static final native String getCodecName(int i);

    static final native String[] getSupportedTypes(int i);

    static final native boolean isEncoder(int i);

    private static final native int native_getCodecCount();

    private static final native void native_init();

    public static final int getCodecCount() {
        initCodecList();
        return sRegularCodecInfos.length;
    }

    public static final MediaCodecInfo getCodecInfoAt(int index) {
        initCodecList();
        if (index >= 0 && index <= sRegularCodecInfos.length) {
            return sRegularCodecInfos[index];
        }
        throw new IllegalArgumentException();
    }

    static {
        sInitLock = new Object();
        System.loadLibrary("media_jni");
        native_init();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final void initCodecList() {
        /*
        r7 = sInitLock;
        monitor-enter(r7);
        r6 = sRegularCodecInfos;	 Catch:{ all -> 0x0034 }
        if (r6 != 0) goto L_0x0053;
    L_0x0007:
        r1 = native_getCodecCount();	 Catch:{ all -> 0x0034 }
        r5 = new java.util.ArrayList;	 Catch:{ all -> 0x0034 }
        r5.<init>();	 Catch:{ all -> 0x0034 }
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0034 }
        r0.<init>();	 Catch:{ all -> 0x0034 }
        r3 = 0;
    L_0x0016:
        if (r3 >= r1) goto L_0x0037;
    L_0x0018:
        r4 = getNewCodecInfoAt(r3);	 Catch:{ Exception -> 0x002b }
        r0.add(r4);	 Catch:{ Exception -> 0x002b }
        r4 = r4.makeRegular();	 Catch:{ Exception -> 0x002b }
        if (r4 == 0) goto L_0x0028;
    L_0x0025:
        r5.add(r4);	 Catch:{ Exception -> 0x002b }
    L_0x0028:
        r3 = r3 + 1;
        goto L_0x0016;
    L_0x002b:
        r2 = move-exception;
        r6 = "MediaCodecList";
        r8 = "Could not get codec capabilities";
        android.util.Log.e(r6, r8, r2);	 Catch:{ all -> 0x0034 }
        goto L_0x0028;
    L_0x0034:
        r6 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x0034 }
        throw r6;
    L_0x0037:
        r6 = r5.size();	 Catch:{ all -> 0x0034 }
        r6 = new android.media.MediaCodecInfo[r6];	 Catch:{ all -> 0x0034 }
        r6 = r5.toArray(r6);	 Catch:{ all -> 0x0034 }
        r6 = (android.media.MediaCodecInfo[]) r6;	 Catch:{ all -> 0x0034 }
        sRegularCodecInfos = r6;	 Catch:{ all -> 0x0034 }
        r6 = r0.size();	 Catch:{ all -> 0x0034 }
        r6 = new android.media.MediaCodecInfo[r6];	 Catch:{ all -> 0x0034 }
        r6 = r0.toArray(r6);	 Catch:{ all -> 0x0034 }
        r6 = (android.media.MediaCodecInfo[]) r6;	 Catch:{ all -> 0x0034 }
        sAllCodecInfos = r6;	 Catch:{ all -> 0x0034 }
    L_0x0053:
        monitor-exit(r7);	 Catch:{ all -> 0x0034 }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaCodecList.initCodecList():void");
    }

    private static MediaCodecInfo getNewCodecInfoAt(int index) {
        String[] supportedTypes = getSupportedTypes(index);
        CodecCapabilities[] caps = new CodecCapabilities[supportedTypes.length];
        String[] arr$ = supportedTypes;
        int len$ = arr$.length;
        int i$ = REGULAR_CODECS;
        int typeIx = REGULAR_CODECS;
        while (i$ < len$) {
            int typeIx2 = typeIx + ALL_CODECS;
            caps[typeIx] = getCodecCapabilities(index, arr$[i$]);
            i$ += ALL_CODECS;
            typeIx = typeIx2;
        }
        return new MediaCodecInfo(getCodecName(index), isEncoder(index), caps);
    }

    public static MediaCodecInfo getInfoFor(String codec) {
        initCodecList();
        return sAllCodecInfos[findCodecByName(codec)];
    }

    private MediaCodecList() {
        this(REGULAR_CODECS);
    }

    public MediaCodecList(int kind) {
        initCodecList();
        if (kind == 0) {
            this.mCodecInfos = sRegularCodecInfos;
        } else {
            this.mCodecInfos = sAllCodecInfos;
        }
    }

    public final MediaCodecInfo[] getCodecInfos() {
        return (MediaCodecInfo[]) Arrays.copyOf(this.mCodecInfos, this.mCodecInfos.length);
    }

    public final String findDecoderForFormat(MediaFormat format) {
        return findCodecForFormat(false, format);
    }

    public final String findEncoderForFormat(MediaFormat format) {
        return findCodecForFormat(true, format);
    }

    private String findCodecForFormat(boolean encoder, MediaFormat format) {
        String mime = format.getString("mime");
        MediaCodecInfo[] arr$ = this.mCodecInfos;
        int len$ = arr$.length;
        for (int i$ = REGULAR_CODECS; i$ < len$; i$ += ALL_CODECS) {
            MediaCodecInfo info = arr$[i$];
            if (info.isEncoder() == encoder) {
                try {
                    CodecCapabilities caps = info.getCapabilitiesForType(mime);
                    if (caps != null && caps.isFormatSupported(format)) {
                        return info.getName();
                    }
                } catch (IllegalArgumentException e) {
                }
            }
        }
        return null;
    }
}
