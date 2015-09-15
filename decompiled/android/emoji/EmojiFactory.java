package android.emoji;

import android.graphics.Bitmap;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EmojiFactory {
    private Map<Integer, WeakReference<Bitmap>> mCache;
    private String mName;
    private long mNativeEmojiFactory;
    private int sCacheSize;

    private class CustomLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        final /* synthetic */ EmojiFactory this$0;

        public CustomLinkedHashMap(android.emoji.EmojiFactory r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.emoji.EmojiFactory.CustomLinkedHashMap.<init>(android.emoji.EmojiFactory):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.emoji.EmojiFactory.CustomLinkedHashMap.<init>(android.emoji.EmojiFactory):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.emoji.EmojiFactory.CustomLinkedHashMap.<init>(android.emoji.EmojiFactory):void");
        }

        protected boolean removeEldestEntry(java.util.Map.Entry<K, V> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.emoji.EmojiFactory.CustomLinkedHashMap.removeEldestEntry(java.util.Map$Entry):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.emoji.EmojiFactory.CustomLinkedHashMap.removeEldestEntry(java.util.Map$Entry):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: android.emoji.EmojiFactory.CustomLinkedHashMap.removeEldestEntry(java.util.Map$Entry):boolean");
        }
    }

    private native void nativeDestructor(long j);

    private native int nativeGetAndroidPuaFromVendorSpecificPua(long j, int i);

    private native int nativeGetAndroidPuaFromVendorSpecificSjis(long j, char c);

    private native Bitmap nativeGetBitmapFromAndroidPua(long j, int i);

    private native int nativeGetMaximumAndroidPua(long j);

    private native int nativeGetMaximumVendorSpecificPua(long j);

    private native int nativeGetMinimumAndroidPua(long j);

    private native int nativeGetMinimumVendorSpecificPua(long j);

    private native int nativeGetVendorSpecificPuaFromAndroidPua(long j, int i);

    private native int nativeGetVendorSpecificSjisFromAndroidPua(long j, int i);

    public static native EmojiFactory newAvailableInstance();

    public static native EmojiFactory newInstance(String str);

    private EmojiFactory(long nativeEmojiFactory, String name) {
        this.sCacheSize = 100;
        this.mNativeEmojiFactory = nativeEmojiFactory;
        this.mName = name;
        this.mCache = new CustomLinkedHashMap(this);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeEmojiFactory);
        } finally {
            super.finalize();
        }
    }

    public String name() {
        return this.mName;
    }

    public synchronized Bitmap getBitmapFromAndroidPua(int pua) {
        Bitmap ret;
        WeakReference<Bitmap> cache = (WeakReference) this.mCache.get(Integer.valueOf(pua));
        if (cache == null) {
            ret = nativeGetBitmapFromAndroidPua(this.mNativeEmojiFactory, pua);
            if (ret != null) {
                this.mCache.put(Integer.valueOf(pua), new WeakReference(ret));
            }
        } else {
            Bitmap tmp = (Bitmap) cache.get();
            if (tmp == null) {
                ret = nativeGetBitmapFromAndroidPua(this.mNativeEmojiFactory, pua);
                this.mCache.put(Integer.valueOf(pua), new WeakReference(ret));
            } else {
                ret = tmp;
            }
        }
        return ret;
    }

    public synchronized Bitmap getBitmapFromVendorSpecificSjis(char sjis) {
        return getBitmapFromAndroidPua(getAndroidPuaFromVendorSpecificSjis(sjis));
    }

    public synchronized Bitmap getBitmapFromVendorSpecificPua(int vsp) {
        return getBitmapFromAndroidPua(getAndroidPuaFromVendorSpecificPua(vsp));
    }

    public int getAndroidPuaFromVendorSpecificSjis(char sjis) {
        return nativeGetAndroidPuaFromVendorSpecificSjis(this.mNativeEmojiFactory, sjis);
    }

    public int getVendorSpecificSjisFromAndroidPua(int pua) {
        return nativeGetVendorSpecificSjisFromAndroidPua(this.mNativeEmojiFactory, pua);
    }

    public int getAndroidPuaFromVendorSpecificPua(int vsp) {
        return nativeGetAndroidPuaFromVendorSpecificPua(this.mNativeEmojiFactory, vsp);
    }

    public String getAndroidPuaFromVendorSpecificPua(String vspString) {
        if (vspString == null) {
            return null;
        }
        int minVsp = nativeGetMinimumVendorSpecificPua(this.mNativeEmojiFactory);
        int maxVsp = nativeGetMaximumVendorSpecificPua(this.mNativeEmojiFactory);
        int len = vspString.length();
        int[] codePoints = new int[vspString.codePointCount(0, len)];
        int new_len = 0;
        int i = 0;
        while (i < len) {
            int codePoint = vspString.codePointAt(i);
            if (minVsp <= codePoint && codePoint <= maxVsp) {
                int newCodePoint = getAndroidPuaFromVendorSpecificPua(codePoint);
                if (newCodePoint > 0) {
                    codePoints[new_len] = newCodePoint;
                    i = vspString.offsetByCodePoints(i, 1);
                    new_len++;
                }
            }
            codePoints[new_len] = codePoint;
            i = vspString.offsetByCodePoints(i, 1);
            new_len++;
        }
        return new String(codePoints, 0, new_len);
    }

    public int getVendorSpecificPuaFromAndroidPua(int pua) {
        return nativeGetVendorSpecificPuaFromAndroidPua(this.mNativeEmojiFactory, pua);
    }

    public String getVendorSpecificPuaFromAndroidPua(String puaString) {
        if (puaString == null) {
            return null;
        }
        int minVsp = nativeGetMinimumAndroidPua(this.mNativeEmojiFactory);
        int maxVsp = nativeGetMaximumAndroidPua(this.mNativeEmojiFactory);
        int len = puaString.length();
        int[] codePoints = new int[puaString.codePointCount(0, len)];
        int new_len = 0;
        int i = 0;
        while (i < len) {
            int codePoint = puaString.codePointAt(i);
            if (minVsp <= codePoint && codePoint <= maxVsp) {
                int newCodePoint = getVendorSpecificPuaFromAndroidPua(codePoint);
                if (newCodePoint > 0) {
                    codePoints[new_len] = newCodePoint;
                    i = puaString.offsetByCodePoints(i, 1);
                    new_len++;
                }
            }
            codePoints[new_len] = codePoint;
            i = puaString.offsetByCodePoints(i, 1);
            new_len++;
        }
        return new String(codePoints, 0, new_len);
    }

    public int getMinimumAndroidPua() {
        return nativeGetMinimumAndroidPua(this.mNativeEmojiFactory);
    }

    public int getMaximumAndroidPua() {
        return nativeGetMaximumAndroidPua(this.mNativeEmojiFactory);
    }
}
