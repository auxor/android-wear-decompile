package android.speech.srec;

import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import java.io.InputStream;

public final class UlawEncoderInputStream extends InputStream {
    private static final int MAX_ULAW = 8192;
    private static final int SCALE_BITS = 16;
    private static final String TAG = "UlawEncoderInputStream";
    private final byte[] mBuf;
    private int mBufCount;
    private InputStream mIn;
    private int mMax;
    private final byte[] mOneByte;

    public UlawEncoderInputStream(java.io.InputStream r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.speech.srec.UlawEncoderInputStream.<init>(java.io.InputStream, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.speech.srec.UlawEncoderInputStream.<init>(java.io.InputStream, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.speech.srec.UlawEncoderInputStream.<init>(java.io.InputStream, int):void");
    }

    public int available() throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.speech.srec.UlawEncoderInputStream.available():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.speech.srec.UlawEncoderInputStream.available():int
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
        throw new UnsupportedOperationException("Method not decompiled: android.speech.srec.UlawEncoderInputStream.available():int");
    }

    public void close() throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.speech.srec.UlawEncoderInputStream.close():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.speech.srec.UlawEncoderInputStream.close():void
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
        throw new UnsupportedOperationException("Method not decompiled: android.speech.srec.UlawEncoderInputStream.close():void");
    }

    public int read() throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.speech.srec.UlawEncoderInputStream.read():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.speech.srec.UlawEncoderInputStream.read():int
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
        throw new UnsupportedOperationException("Method not decompiled: android.speech.srec.UlawEncoderInputStream.read():int");
    }

    public int read(byte[] r1) throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.speech.srec.UlawEncoderInputStream.read(byte[]):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.speech.srec.UlawEncoderInputStream.read(byte[]):int
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
        throw new UnsupportedOperationException("Method not decompiled: android.speech.srec.UlawEncoderInputStream.read(byte[]):int");
    }

    public int read(byte[] r1, int r2, int r3) throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.speech.srec.UlawEncoderInputStream.read(byte[], int, int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.speech.srec.UlawEncoderInputStream.read(byte[], int, int):int
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
        throw new UnsupportedOperationException("Method not decompiled: android.speech.srec.UlawEncoderInputStream.read(byte[], int, int):int");
    }

    public static void encode(byte[] pcmBuf, int pcmOffset, byte[] ulawBuf, int ulawOffset, int length, int max) {
        if (max <= 0) {
            max = MAX_ULAW;
        }
        int coef = EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION / max;
        int i = 0;
        int ulawOffset2 = ulawOffset;
        int pcmOffset2 = pcmOffset;
        while (i < length) {
            int ulaw;
            pcmOffset = pcmOffset2 + 1;
            pcmOffset2 = pcmOffset + 1;
            int pcm = (((pcmBuf[pcmOffset2] & EditorInfo.IME_MASK_ACTION) + (pcmBuf[pcmOffset] << 8)) * coef) >> SCALE_BITS;
            if (pcm >= 0) {
                ulaw = pcm <= 0 ? EditorInfo.IME_MASK_ACTION : pcm <= 30 ? ((30 - pcm) >> 1) + LayoutParams.SOFT_INPUT_MASK_ADJUST : pcm <= 94 ? ((94 - pcm) >> 2) + KeyEvent.KEYCODE_WAKEUP : pcm <= KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK ? ((222 - pcm) >> 3) + KeyEvent.KEYCODE_CALENDAR : pcm <= 478 ? ((478 - pcm) >> 4) + KeyEvent.KEYCODE_BUTTON_5 : pcm <= 990 ? ((990 - pcm) >> 5) + KeyEvent.KEYCODE_SETTINGS : pcm <= LayoutParams.TYPE_STATUS_BAR_PANEL ? ((2014 - pcm) >> 6) + KeyEvent.KEYCODE_NUMPAD_ENTER : pcm <= 4062 ? ((4062 - pcm) >> 7) + KeyEvent.KEYCODE_NUMPAD_0 : pcm <= 8158 ? ((8158 - pcm) >> 8) + AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS : AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
            } else {
                ulaw = -1 <= pcm ? KeyEvent.KEYCODE_MEDIA_PAUSE : -31 <= pcm ? ((pcm + 31) >> 1) + KeyEvent.KEYCODE_FORWARD_DEL : -95 <= pcm ? ((pcm + 95) >> 2) + 96 : -223 <= pcm ? ((pcm + KeyEvent.KEYCODE_SLEEP) >> 3) + 80 : -479 <= pcm ? ((pcm + 479) >> 4) + 64 : -991 <= pcm ? ((pcm + 991) >> 5) + 48 : -2015 <= pcm ? ((pcm + LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY) >> 6) + 32 : -4063 <= pcm ? ((pcm + 4063) >> 7) + SCALE_BITS : -8159 <= pcm ? ((pcm + 8159) >> 8) + 0 : 0;
            }
            ulawOffset = ulawOffset2 + 1;
            ulawBuf[ulawOffset2] = (byte) ulaw;
            i++;
            ulawOffset2 = ulawOffset;
        }
    }

    public static int maxAbsPcm(byte[] pcmBuf, int offset, int length) {
        int max = 0;
        int offset2 = offset;
        for (int i = 0; i < length; i++) {
            offset = offset2 + 1;
            offset2 = offset + 1;
            int pcm = (pcmBuf[offset2] & EditorInfo.IME_MASK_ACTION) + (pcmBuf[offset] << 8);
            if (pcm < 0) {
                pcm = -pcm;
            }
            if (pcm > max) {
                max = pcm;
            }
        }
        return max;
    }
}
