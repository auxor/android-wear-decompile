package android.filterpacks.imageproc;

import android.bluetooth.BluetoothClass.Device;
import android.filterfw.core.Filter;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.Program;
import android.net.UrlQuerySanitizer.IllegalCharacterValueSanitizer;
import android.nfc.tech.MifareClassic;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.os.FileUtils;
import android.provider.Downloads.Impl;
import android.speech.tts.Voice;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SpellChecker;

public class AutoFixFilter extends Filter {
    private static final int[] normal_cdf;
    private final String mAutoFixShader;
    private Frame mDensityFrame;
    private int mHeight;
    private Frame mHistFrame;
    private Program mNativeProgram;
    @GenerateFieldPort(name = "scale")
    private float mScale;
    private Program mShaderProgram;
    private int mTarget;
    @GenerateFieldPort(hasDefault = true, name = "tile_size")
    private int mTileSize;
    private int mWidth;

    public AutoFixFilter(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.<init>(java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.<init>(java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.<init>(java.lang.String):void");
    }

    private void createHistogramFrame(android.filterfw.core.FilterContext r1, int r2, int r3, int[] r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.createHistogramFrame(android.filterfw.core.FilterContext, int, int, int[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.createHistogramFrame(android.filterfw.core.FilterContext, int, int, int[]):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.createHistogramFrame(android.filterfw.core.FilterContext, int, int, int[]):void");
    }

    private void initParameters() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.initParameters():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.initParameters():void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.initParameters():void");
    }

    public void fieldPortValueUpdated(java.lang.String r1, android.filterfw.core.FilterContext r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.fieldPortValueUpdated(java.lang.String, android.filterfw.core.FilterContext):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.fieldPortValueUpdated(java.lang.String, android.filterfw.core.FilterContext):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.fieldPortValueUpdated(java.lang.String, android.filterfw.core.FilterContext):void");
    }

    public void initProgram(android.filterfw.core.FilterContext r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.initProgram(android.filterfw.core.FilterContext, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.initProgram(android.filterfw.core.FilterContext, int):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.initProgram(android.filterfw.core.FilterContext, int):void");
    }

    protected void prepare(android.filterfw.core.FilterContext r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.prepare(android.filterfw.core.FilterContext):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.prepare(android.filterfw.core.FilterContext):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.prepare(android.filterfw.core.FilterContext):void");
    }

    public void process(android.filterfw.core.FilterContext r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.process(android.filterfw.core.FilterContext):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.process(android.filterfw.core.FilterContext):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.process(android.filterfw.core.FilterContext):void");
    }

    public void setupPorts() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.setupPorts():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.setupPorts():void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.setupPorts():void");
    }

    public void tearDown(android.filterfw.core.FilterContext r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.filterpacks.imageproc.AutoFixFilter.tearDown(android.filterfw.core.FilterContext):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.filterpacks.imageproc.AutoFixFilter.tearDown(android.filterfw.core.FilterContext):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.filterpacks.imageproc.AutoFixFilter.tearDown(android.filterfw.core.FilterContext):void");
    }

    static {
        normal_cdf = new int[]{9, 33, 50, 64, 75, 84, 92, 99, KeyEvent.KEYCODE_BUTTON_THUMBL, KeyEvent.KEYCODE_FORWARD_DEL, KeyEvent.KEYCODE_META_LEFT, KeyEvent.KEYCODE_MOVE_HOME, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_RECORD, KeyEvent.KEYCODE_F4, KeyEvent.KEYCODE_F8, KeyEvent.KEYCODE_F12, KeyEvent.KEYCODE_NUMPAD_1, KeyEvent.KEYCODE_NUMPAD_4, KeyEvent.KEYCODE_NUMPAD_6, KeyEvent.KEYCODE_NUMPAD_DIVIDE, KeyEvent.KEYCODE_NUMPAD_ADD, KeyEvent.KEYCODE_NUMPAD_COMMA, KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN, KeyEvent.KEYCODE_VOLUME_MUTE, KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_ZOOM_OUT, KeyEvent.KEYCODE_TV, KeyEvent.KEYCODE_DVR, KeyEvent.KEYCODE_CAPTIONS, KeyEvent.KEYCODE_TV_POWER, KeyEvent.KEYCODE_STB_POWER, KeyEvent.KEYCODE_STB_INPUT, KeyEvent.KEYCODE_AVR_INPUT, KeyEvent.KEYCODE_PROG_GREEN, KeyEvent.KEYCODE_PROG_BLUE, KeyEvent.KEYCODE_BUTTON_1, KeyEvent.KEYCODE_BUTTON_2, KeyEvent.KEYCODE_BUTTON_3, KeyEvent.KEYCODE_BUTTON_5, KeyEvent.KEYCODE_BUTTON_7, KeyEvent.KEYCODE_BUTTON_8, KeyEvent.KEYCODE_BUTTON_10, KeyEvent.KEYCODE_BUTTON_11, KeyEvent.KEYCODE_BUTTON_12, KeyEvent.KEYCODE_BUTTON_13, KeyEvent.KEYCODE_BUTTON_15, KeyEvent.KEYCODE_BUTTON_16, KeyEvent.KEYCODE_MANNER_MODE, KeyEvent.KEYCODE_3D_MODE, KeyEvent.KEYCODE_CONTACTS, KeyEvent.KEYCODE_CALENDAR, KeyEvent.KEYCODE_MUSIC, KeyEvent.KEYCODE_CALCULATOR, KeyEvent.KEYCODE_EISU, KeyEvent.KEYCODE_MUHENKAN, KeyEvent.KEYCODE_HENKAN, KeyEvent.KEYCODE_KATAKANA_HIRAGANA, KeyEvent.KEYCODE_YEN, KeyEvent.KEYCODE_RO, KeyEvent.KEYCODE_KANA, KeyEvent.KEYCODE_ASSIST, KeyEvent.KEYCODE_BRIGHTNESS_DOWN, KeyEvent.KEYCODE_BRIGHTNESS_UP, KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK, KeyEvent.KEYCODE_SLEEP, KeyEvent.KEYCODE_WAKEUP, KeyEvent.KEYCODE_PAIRING, KeyEvent.KEYCODE_MEDIA_TOP_MENU, KeyEvent.KEYCODE_11, KeyEvent.KEYCODE_12, KeyEvent.KEYCODE_LAST_CHANNEL, KeyEvent.KEYCODE_LAST_CHANNEL, KeyEvent.KEYCODE_TV_DATA_SERVICE, KeyEvent.KEYCODE_VOICE_ASSIST, KeyEvent.KEYCODE_TV_RADIO_SERVICE, KeyEvent.KEYCODE_TV_TELETEXT, KeyEvent.KEYCODE_TV_NUMBER_ENTRY, KeyEvent.KEYCODE_TV_TERRESTRIAL_ANALOG, KeyEvent.KEYCODE_TV_TERRESTRIAL_DIGITAL, KeyEvent.KEYCODE_TV_TERRESTRIAL_DIGITAL, KeyEvent.KEYCODE_TV_SATELLITE, KeyEvent.KEYCODE_TV_SATELLITE_BS, KeyEvent.KEYCODE_TV_SATELLITE_CS, KeyEvent.KEYCODE_TV_SATELLITE_CS, LayoutParams.SOFT_INPUT_MASK_ADJUST, LayoutParams.SOFT_INPUT_MASK_ADJUST, KeyEvent.KEYCODE_TV_ANTENNA_CABLE, KeyEvent.KEYCODE_TV_ANTENNA_CABLE, KeyEvent.KEYCODE_TV_INPUT_HDMI_1, KeyEvent.KEYCODE_TV_INPUT_HDMI_2, KeyEvent.KEYCODE_TV_INPUT_HDMI_3, KeyEvent.KEYCODE_TV_INPUT_HDMI_3, KeyEvent.KEYCODE_TV_INPUT_HDMI_4, KeyEvent.KEYCODE_TV_INPUT_COMPOSITE_1, KeyEvent.KEYCODE_TV_INPUT_COMPOSITE_1, KeyEvent.KEYCODE_TV_INPUT_COMPOSITE_2, KeyEvent.KEYCODE_TV_INPUT_COMPONENT_1, KeyEvent.KEYCODE_TV_INPUT_COMPONENT_1, KeyEvent.KEYCODE_TV_INPUT_COMPONENT_2, KeyEvent.KEYCODE_TV_INPUT_COMPONENT_2, KeyEvent.KEYCODE_TV_INPUT_VGA_1, KeyEvent.KEYCODE_TV_AUDIO_DESCRIPTION, KeyEvent.KEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP, KeyEvent.KEYCODE_TV_AUDIO_DESCRIPTION_MIX_UP, KeyEvent.KEYCODE_TV_AUDIO_DESCRIPTION_MIX_DOWN, EditorInfo.IME_MASK_ACTION, EditorInfo.IME_MASK_ACTION, InputMethodManager.CONTROL_START_INITIAL, InputMethodManager.CONTROL_START_INITIAL, KeyEvent.KEYCODE_TV_MEDIA_CONTEXT_MENU, KeyEvent.KEYCODE_TV_TIMER_PROGRAMMING, KeyEvent.KEYCODE_TV_TIMER_PROGRAMMING, KeyEvent.KEYCODE_HELP, KeyEvent.KEYCODE_HELP, KeyEvent.KEYCODE_HELP, GLES10.GL_ADD, MotionEvent.ACTION_POINTER_2_DOWN, MotionEvent.ACTION_POINTER_2_UP, MotionEvent.ACTION_POINTER_2_UP, 263, 263, Device.COMPUTER_SERVER, Device.COMPUTER_SERVER, 265, 265, 266, 267, 267, Device.COMPUTER_LAPTOP, Device.COMPUTER_LAPTOP, 269, 269, 269, 270, 270, 271, Device.COMPUTER_HANDHELD_PC_PDA, Device.COMPUTER_HANDHELD_PC_PDA, 273, 273, 274, 274, 275, 275, Device.COMPUTER_PALM_SIZE_PC_PDA, Device.COMPUTER_PALM_SIZE_PC_PDA, 277, 277, 277, 278, 278, 279, 279, 279, Device.COMPUTER_WEARABLE, Device.COMPUTER_WEARABLE, 281, 282, 282, 282, 283, 283, 284, 284, 285, 285, 285, 286, 286, 287, 287, 288, 288, 288, 289, 289, 289, 290, 290, 290, 291, 292, 292, 292, 293, 293, 294, 294, 294, 295, 295, 296, 296, 296, 297, 297, 297, 298, 298, 298, 299, 299, 299, 299, Voice.QUALITY_NORMAL, Voice.QUALITY_NORMAL, 301, 301, 302, 302, 302, 303, 303, 304, 304, 304, 305, 305, 305, 306, 306, 306, 307, 307, 307, 308, 308, 308, 309, 309, 309, 309, 310, 310, 310, 310, 311, 312, 312, 312, 313, 313, 313, 314, 314, 314, 315, 315, 315, 315, 316, 316, 316, 317, 317, 317, 318, 318, 318, 319, 319, 319, 319, 319, MifareClassic.SIZE_MINI, MifareClassic.SIZE_MINI, MifareClassic.SIZE_MINI, 321, 321, 322, 322, 322, 323, 323, 323, 323, 324, 324, 324, 325, 325, 325, 325, 326, 326, 326, 327, 327, 327, 327, 328, 328, 328, 329, 329, 329, 329, 329, 330, 330, 330, 330, 331, 331, 332, 332, 332, 333, 333, 333, 333, 334, 334, 334, 334, 335, 335, 335, 336, 336, 336, 336, 337, 337, 337, 337, 338, 338, 338, 339, 339, 339, 339, 339, 339, 340, 340, 340, 340, 341, 341, 342, 342, 342, 342, 343, 343, 343, 344, 344, 344, 344, 345, 345, 345, 345, 346, 346, 346, 346, 347, 347, 347, 347, 348, 348, 348, 348, 349, 349, 349, 349, 349, 349, SpellChecker.WORD_ITERATOR_INTERVAL, SpellChecker.WORD_ITERATOR_INTERVAL, SpellChecker.WORD_ITERATOR_INTERVAL, SpellChecker.WORD_ITERATOR_INTERVAL, 351, 351, 352, 352, 352, 352, 353, 353, 353, 353, 354, 354, 354, 354, 355, 355, 355, 355, 356, 356, 356, 356, 357, 357, 357, 357, 358, 358, 358, 358, 359, 359, 359, 359, 359, 359, 359, 360, 360, 360, 360, 361, 361, 362, 362, 362, 362, 363, 363, 363, 363, 364, 364, 364, 364, 365, 365, 365, 365, 366, 366, 366, 366, 366, 367, 367, 367, 367, 368, 368, 368, 368, 369, 369, 369, 369, 369, 369, 370, 370, 370, 370, 370, 371, 371, 372, 372, 372, 372, 373, 373, 373, 373, 374, 374, 374, 374, 374, 375, 375, 375, 375, 376, 376, 376, 376, 377, 377, 377, 377, 378, 378, 378, 378, 378, 379, 379, 379, 379, 379, 379, 380, 380, 380, 380, 381, 381, 381, 382, 382, 382, 382, 383, 383, 383, 383, 384, 384, 384, 384, 385, 385, 385, 385, 385, 386, 386, 386, 386, 387, 387, 387, 387, 388, 388, 388, 388, 388, 389, 389, 389, 389, 389, 389, 390, 390, 390, 390, 391, 391, 392, 392, 392, 392, 392, 393, 393, 393, 393, 394, 394, 394, 394, 395, 395, 395, 395, 396, 396, 396, 396, 396, 397, 397, 397, 397, 398, 398, 398, 398, 399, 399, 399, 399, 399, 399, Voice.QUALITY_HIGH, Voice.QUALITY_HIGH, Voice.QUALITY_HIGH, Voice.QUALITY_HIGH, Voice.QUALITY_HIGH, 401, 401, 402, 402, 402, 402, 403, 403, 403, 403, IllegalCharacterValueSanitizer.URL_LEGAL, IllegalCharacterValueSanitizer.URL_LEGAL, IllegalCharacterValueSanitizer.URL_LEGAL, IllegalCharacterValueSanitizer.URL_LEGAL, IllegalCharacterValueSanitizer.URL_AND_SPACE_LEGAL, IllegalCharacterValueSanitizer.URL_AND_SPACE_LEGAL, IllegalCharacterValueSanitizer.URL_AND_SPACE_LEGAL, IllegalCharacterValueSanitizer.URL_AND_SPACE_LEGAL, Impl.STATUS_NOT_ACCEPTABLE, Impl.STATUS_NOT_ACCEPTABLE, Impl.STATUS_NOT_ACCEPTABLE, Impl.STATUS_NOT_ACCEPTABLE, Impl.STATUS_NOT_ACCEPTABLE, 407, 407, 407, 407, 408, 408, 408, 408, 409, 409, 409, 409, 409, 409, 410, 410, 410, 410, Impl.STATUS_LENGTH_REQUIRED, Impl.STATUS_LENGTH_REQUIRED, Impl.STATUS_PRECONDITION_FAILED, Impl.STATUS_PRECONDITION_FAILED, Impl.STATUS_PRECONDITION_FAILED, Impl.STATUS_PRECONDITION_FAILED, 413, 413, 413, 413, 414, 414, 414, 414, 415, 415, 415, 415, 416, 416, 416, 416, 417, 417, 417, 417, 418, 418, 418, 418, 419, 419, 419, 419, 419, 419, 420, 420, 420, 420, 421, 421, 422, 422, 422, 422, 423, 423, 423, 423, 424, 424, 424, 425, 425, 425, 425, 426, 426, 426, 426, 427, 427, 427, 427, 428, 428, 428, 429, 429, 429, 429, 429, 429, 430, 430, 430, 430, 431, 431, 432, 432, 432, 433, 433, 433, 433, 434, 434, 434, 435, 435, 435, 435, 436, 436, 436, 436, 437, 437, 437, 438, 438, 438, 438, 439, 439, 439, 439, 439, 440, 440, 440, 441, 441, 442, 442, 442, 443, 443, 443, 443, 444, 444, 444, 445, 445, 445, 446, 446, 446, 446, 447, 447, 447, FileUtils.S_IRWXU, FileUtils.S_IRWXU, FileUtils.S_IRWXU, 449, 449, 449, 449, 449, 450, 450, 450, 451, 451, 452, 452, 452, 453, 453, 453, 454, 454, 454, 455, 455, 455, 456, 456, 456, 457, 457, 457, 458, 458, 458, 459, 459, 459, 459, 460, 460, 460, 461, 461, 462, 462, 462, 463, 463, 463, 464, 464, 465, 465, 465, 466, 466, 466, 467, 467, 467, 468, 468, 469, 469, 469, 469, 470, 470, 470, 471, 472, 472, 472, 473, 473, 474, 474, 474, 475, 475, 476, 476, 476, 477, 477, 478, 478, 478, 479, 479, 479, 480, 480, 480, 481, 482, 482, 483, 483, 484, 484, 484, 485, 485, 486, 486, 487, 487, Impl.STATUS_FILE_ALREADY_EXISTS_ERROR, Impl.STATUS_FILE_ALREADY_EXISTS_ERROR, Impl.STATUS_FILE_ALREADY_EXISTS_ERROR, Impl.STATUS_CANNOT_RESUME, Impl.STATUS_CANNOT_RESUME, Impl.STATUS_CANNOT_RESUME, Impl.STATUS_CANCELED, Impl.STATUS_CANCELED, Impl.STATUS_UNKNOWN_ERROR, Impl.STATUS_FILE_ERROR, Impl.STATUS_FILE_ERROR, Impl.STATUS_UNHANDLED_REDIRECT, Impl.STATUS_UNHANDLED_REDIRECT, Impl.STATUS_UNHANDLED_HTTP_CODE, Impl.STATUS_UNHANDLED_HTTP_CODE, Impl.STATUS_HTTP_DATA_ERROR, Impl.STATUS_HTTP_DATA_ERROR, Impl.STATUS_HTTP_EXCEPTION, Impl.STATUS_HTTP_EXCEPTION, Impl.STATUS_TOO_MANY_REDIRECTS, Impl.STATUS_TOO_MANY_REDIRECTS, Impl.STATUS_BLOCKED, Impl.STATUS_BLOCKED, 499, 499, 499, AccessibilityEvent.MAX_TEXT_LENGTH, 501, 502, 502, 503, 503, 504, 504, 505, 505, 506, 507, 507, 508, 508, 509, 509, 510, 510, 511, AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, InputDevice.SOURCE_DPAD, InputDevice.SOURCE_DPAD, GLES20.GL_EQUAL, GLES20.GL_LEQUAL, GLES20.GL_LEQUAL, GLES20.GL_GREATER, MotionEvent.ACTION_POINTER_3_DOWN, MotionEvent.ACTION_POINTER_3_DOWN, MotionEvent.ACTION_POINTER_3_UP, GLES20.GL_ALWAYS, GLES20.GL_ALWAYS, GLES20.GL_ALWAYS, Device.PHONE_CORDLESS, 521, 522, 523, Device.PHONE_SMART, Device.PHONE_SMART, 525, 526, 526, 527, Device.PHONE_MODEM_OR_GATEWAY, 529, 529, 530, 531, Device.PHONE_ISDN, 533, 534, 535, 535, 536, 537, 538, 539, 539, 540, 542, 543, 544, 545, 546, 547, 548, 549, 549, 550, 552, 553, 554, 555, 556, 558, 559, 559, 561, 562, 564, 565, 566, 568, 569, 570, 572, 574, 575, 577, 578, 579, 582, 583, 585, 587, 589, 590, 593, 595, 597, 599, 602, 604, 607, 609, 612, 615, 618, 620, 624, 628, 631, 635, 639, 644, 649, 654, 659, 666, 673, 680, 690, CalendarColumns.CAL_ACCESS_OWNER, 714};
    }

    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }
}
