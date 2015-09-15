package android.ddm;

import android.opengl.GLUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityNodeInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

public class DdmHandleViewDebug extends ChunkHandler {
    public static final int CHUNK_VUGL = 0;
    private static final int CHUNK_VULW = 0;
    private static final int CHUNK_VUOP = 0;
    private static final int CHUNK_VURT = 0;
    private static final int ERR_EXCEPTION = -3;
    private static final int ERR_INVALID_OP = -1;
    private static final int ERR_INVALID_PARAM = -2;
    private static final String TAG = "DdmViewDebug";
    private static final int VUOP_CAPTURE_VIEW = 1;
    private static final int VUOP_DUMP_DISPLAYLIST = 2;
    private static final int VUOP_INVOKE_VIEW_METHOD = 4;
    private static final int VUOP_PROFILE_VIEW = 3;
    private static final int VUOP_SET_LAYOUT_PARAMETER = 5;
    private static final int VURT_CAPTURE_LAYERS = 2;
    private static final int VURT_DUMP_HIERARCHY = 1;
    private static final int VURT_DUMP_THEME = 3;
    private static final DdmHandleViewDebug sInstance = null;

    /* renamed from: android.ddm.DdmHandleViewDebug.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ DdmHandleViewDebug this$0;
        final /* synthetic */ View val$rootView;
        final /* synthetic */ View val$targetView;

        AnonymousClass1(android.ddm.DdmHandleViewDebug r1, android.view.View r2, android.view.View r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.ddm.DdmHandleViewDebug.1.<init>(android.ddm.DdmHandleViewDebug, android.view.View, android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.ddm.DdmHandleViewDebug.1.<init>(android.ddm.DdmHandleViewDebug, android.view.View, android.view.View):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.ddm.DdmHandleViewDebug.1.<init>(android.ddm.DdmHandleViewDebug, android.view.View, android.view.View):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.ddm.DdmHandleViewDebug.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.ddm.DdmHandleViewDebug.1.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.ddm.DdmHandleViewDebug.1.run():void");
        }
    }

    static {
        CHUNK_VUGL = type("VUGL");
        CHUNK_VULW = type("VULW");
        CHUNK_VURT = type("VURT");
        CHUNK_VUOP = type("VUOP");
        sInstance = new DdmHandleViewDebug();
    }

    private DdmHandleViewDebug() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_VUGL, sInstance);
        DdmServer.registerHandler(CHUNK_VULW, sInstance);
        DdmServer.registerHandler(CHUNK_VURT, sInstance);
        DdmServer.registerHandler(CHUNK_VUOP, sInstance);
    }

    public void connected() {
    }

    public void disconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        int type = request.type;
        if (type == CHUNK_VUGL) {
            return handleOpenGlTrace(request);
        }
        if (type == CHUNK_VULW) {
            return listWindows();
        }
        ByteBuffer in = wrapChunk(request);
        int op = in.getInt();
        View rootView = getRootView(in);
        if (rootView == null) {
            return createFailChunk(ERR_INVALID_PARAM, "Invalid View Root");
        }
        if (type != CHUNK_VURT) {
            View targetView = getTargetView(rootView, in);
            if (targetView == null) {
                return createFailChunk(ERR_INVALID_PARAM, "Invalid target view");
            }
            if (type == CHUNK_VUOP) {
                switch (op) {
                    case VURT_DUMP_HIERARCHY /*1*/:
                        return captureView(rootView, targetView);
                    case VURT_CAPTURE_LAYERS /*2*/:
                        return dumpDisplayLists(rootView, targetView);
                    case VURT_DUMP_THEME /*3*/:
                        return profileView(rootView, targetView);
                    case VUOP_INVOKE_VIEW_METHOD /*4*/:
                        return invokeViewMethod(rootView, targetView, in);
                    case VUOP_SET_LAYOUT_PARAMETER /*5*/:
                        return setLayoutParameter(rootView, targetView, in);
                    default:
                        return createFailChunk(ERR_INVALID_OP, "Unknown view operation: " + op);
                }
            }
            throw new RuntimeException("Unknown packet " + ChunkHandler.name(type));
        } else if (op == VURT_DUMP_HIERARCHY) {
            return dumpHierarchy(rootView, in);
        } else {
            if (op == VURT_CAPTURE_LAYERS) {
                return captureLayers(rootView);
            }
            if (op == VURT_DUMP_THEME) {
                return dumpTheme(rootView);
            }
            return createFailChunk(ERR_INVALID_OP, "Unknown view root operation: " + op);
        }
    }

    private Chunk handleOpenGlTrace(Chunk request) {
        GLUtils.setTracingLevel(wrapChunk(request).getInt());
        return null;
    }

    private Chunk listWindows() {
        int i$;
        String[] windowNames = WindowManagerGlobal.getInstance().getViewRootNames();
        int responseLength = VUOP_INVOKE_VIEW_METHOD;
        String[] arr$ = windowNames;
        int len$ = arr$.length;
        for (i$ = CHUNK_VURT; i$ < len$; i$ += VURT_DUMP_HIERARCHY) {
            responseLength = (responseLength + VUOP_INVOKE_VIEW_METHOD) + (arr$[i$].length() * VURT_CAPTURE_LAYERS);
        }
        ByteBuffer out = ByteBuffer.allocate(responseLength);
        out.order(ChunkHandler.CHUNK_ORDER);
        out.putInt(windowNames.length);
        arr$ = windowNames;
        len$ = arr$.length;
        for (i$ = CHUNK_VURT; i$ < len$; i$ += VURT_DUMP_HIERARCHY) {
            String name = arr$[i$];
            out.putInt(name.length());
            putString(out, name);
        }
        return new Chunk(CHUNK_VULW, out);
    }

    private View getRootView(ByteBuffer in) {
        try {
            return WindowManagerGlobal.getInstance().getRootView(getString(in, in.getInt()));
        } catch (BufferUnderflowException e) {
            return null;
        }
    }

    private View getTargetView(View root, ByteBuffer in) {
        try {
            return ViewDebug.findView(root, getString(in, in.getInt()));
        } catch (BufferUnderflowException e) {
            return null;
        }
    }

    private Chunk dumpHierarchy(View rootView, ByteBuffer in) {
        boolean skipChildren;
        boolean includeProperties;
        if (in.getInt() > 0) {
            skipChildren = true;
        } else {
            skipChildren = false;
        }
        if (in.getInt() > 0) {
            includeProperties = true;
        } else {
            includeProperties = false;
        }
        ByteArrayOutputStream b = new ByteArrayOutputStream(AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
        try {
            ViewDebug.dump(rootView, skipChildren, includeProperties, b);
            byte[] data = b.toByteArray();
            return new Chunk(CHUNK_VURT, data, CHUNK_VURT, data.length);
        } catch (IOException e) {
            return createFailChunk(VURT_DUMP_HIERARCHY, "Unexpected error while obtaining view hierarchy: " + e.getMessage());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.harmony.dalvik.ddmc.Chunk captureLayers(android.view.View r9) {
        /*
        r8 = this;
        r0 = new java.io.ByteArrayOutputStream;
        r4 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0.<init>(r4);
        r2 = new java.io.DataOutputStream;
        r2.<init>(r0);
        android.view.ViewDebug.captureLayers(r9, r2);	 Catch:{ IOException -> 0x0020 }
        r2.close();	 Catch:{ IOException -> 0x0048 }
    L_0x0012:
        r1 = r0.toByteArray();
        r4 = new org.apache.harmony.dalvik.ddmc.Chunk;
        r5 = CHUNK_VURT;
        r6 = 0;
        r7 = r1.length;
        r4.<init>(r5, r1, r6, r7);
    L_0x001f:
        return r4;
    L_0x0020:
        r3 = move-exception;
        r4 = 1;
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0043 }
        r5.<init>();	 Catch:{ all -> 0x0043 }
        r6 = "Unexpected error while obtaining view hierarchy: ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0043 }
        r6 = r3.getMessage();	 Catch:{ all -> 0x0043 }
        r5 = r5.append(r6);	 Catch:{ all -> 0x0043 }
        r5 = r5.toString();	 Catch:{ all -> 0x0043 }
        r4 = createFailChunk(r4, r5);	 Catch:{ all -> 0x0043 }
        r2.close();	 Catch:{ IOException -> 0x0041 }
        goto L_0x001f;
    L_0x0041:
        r5 = move-exception;
        goto L_0x001f;
    L_0x0043:
        r4 = move-exception;
        r2.close();	 Catch:{ IOException -> 0x004a }
    L_0x0047:
        throw r4;
    L_0x0048:
        r4 = move-exception;
        goto L_0x0012;
    L_0x004a:
        r5 = move-exception;
        goto L_0x0047;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ddm.DdmHandleViewDebug.captureLayers(android.view.View):org.apache.harmony.dalvik.ddmc.Chunk");
    }

    private Chunk dumpTheme(View rootView) {
        ByteArrayOutputStream b = new ByteArrayOutputStream(AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
        try {
            ViewDebug.dumpTheme(rootView, b);
            byte[] data = b.toByteArray();
            return new Chunk(CHUNK_VURT, data, CHUNK_VURT, data.length);
        } catch (IOException e) {
            return createFailChunk(VURT_DUMP_HIERARCHY, "Unexpected error while dumping the theme: " + e.getMessage());
        }
    }

    private Chunk captureView(View rootView, View targetView) {
        OutputStream b = new ByteArrayOutputStream(AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
        try {
            ViewDebug.capture(rootView, b, targetView);
            byte[] data = b.toByteArray();
            return new Chunk(CHUNK_VUOP, data, CHUNK_VURT, data.length);
        } catch (IOException e) {
            return createFailChunk(VURT_DUMP_HIERARCHY, "Unexpected error while capturing view: " + e.getMessage());
        }
    }

    private Chunk dumpDisplayLists(View rootView, View targetView) {
        rootView.post(new AnonymousClass1(this, rootView, targetView));
        return null;
    }

    private Chunk invokeViewMethod(View rootView, View targetView, ByteBuffer in) {
        Class<?>[] argTypes;
        Object[] args;
        String methodName = getString(in, in.getInt());
        if (in.hasRemaining()) {
            int nArgs = in.getInt();
            argTypes = new Class[nArgs];
            args = new Object[nArgs];
            for (int i = CHUNK_VURT; i < nArgs; i += VURT_DUMP_HIERARCHY) {
                char c = in.getChar();
                switch (c) {
                    case KeyEvent.KEYCODE_ENTER /*66*/:
                        argTypes[i] = Byte.TYPE;
                        args[i] = Byte.valueOf(in.get());
                        break;
                    case KeyEvent.KEYCODE_DEL /*67*/:
                        argTypes[i] = Character.TYPE;
                        args[i] = Character.valueOf(in.getChar());
                        break;
                    case KeyEvent.KEYCODE_GRAVE /*68*/:
                        argTypes[i] = Double.TYPE;
                        args[i] = Double.valueOf(in.getDouble());
                        break;
                    case KeyEvent.KEYCODE_EQUALS /*70*/:
                        argTypes[i] = Float.TYPE;
                        args[i] = Float.valueOf(in.getFloat());
                        break;
                    case KeyEvent.KEYCODE_BACKSLASH /*73*/:
                        argTypes[i] = Integer.TYPE;
                        args[i] = Integer.valueOf(in.getInt());
                        break;
                    case KeyEvent.KEYCODE_SEMICOLON /*74*/:
                        argTypes[i] = Long.TYPE;
                        args[i] = Long.valueOf(in.getLong());
                        break;
                    case KeyEvent.KEYCODE_NOTIFICATION /*83*/:
                        argTypes[i] = Short.TYPE;
                        args[i] = Short.valueOf(in.getShort());
                        break;
                    case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD /*90*/:
                        boolean z;
                        argTypes[i] = Boolean.TYPE;
                        if (in.get() == null) {
                            z = false;
                        } else {
                            z = true;
                        }
                        args[i] = Boolean.valueOf(z);
                        break;
                    default:
                        Log.e(TAG, "arg " + i + ", unrecognized type: " + c);
                        return createFailChunk(ERR_INVALID_PARAM, "Unsupported parameter type (" + c + ") to invoke view method.");
                }
            }
        } else {
            argTypes = new Class[CHUNK_VURT];
            args = new Object[CHUNK_VURT];
        }
        try {
            try {
                ViewDebug.invokeViewMethod(targetView, targetView.getClass().getMethod(methodName, argTypes), args);
                return null;
            } catch (Exception e) {
                Log.e(TAG, "Exception while invoking method: " + e.getCause().getMessage());
                String msg = e.getCause().getMessage();
                if (msg == null) {
                    msg = e.getCause().toString();
                }
                return createFailChunk(ERR_EXCEPTION, msg);
            }
        } catch (NoSuchMethodException e2) {
            Log.e(TAG, "No such method: " + e2.getMessage());
            return createFailChunk(ERR_INVALID_PARAM, "No such method: " + e2.getMessage());
        }
    }

    private Chunk setLayoutParameter(View rootView, View targetView, ByteBuffer in) {
        String param = getString(in, in.getInt());
        try {
            ViewDebug.setLayoutParameter(targetView, param, in.getInt());
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Exception setting layout parameter: " + e);
            return createFailChunk(ERR_EXCEPTION, "Error accessing field " + param + ":" + e.getMessage());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.harmony.dalvik.ddmc.Chunk profileView(android.view.View r9, android.view.View r10) {
        /*
        r8 = this;
        r5 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r0 = new java.io.ByteArrayOutputStream;
        r0.<init>(r5);
        r1 = new java.io.BufferedWriter;
        r4 = new java.io.OutputStreamWriter;
        r4.<init>(r0);
        r1.<init>(r4, r5);
        android.view.ViewDebug.profileViewAndChildren(r10, r1);	 Catch:{ IOException -> 0x0026 }
        r1.close();	 Catch:{ IOException -> 0x004e }
    L_0x0018:
        r2 = r0.toByteArray();
        r4 = new org.apache.harmony.dalvik.ddmc.Chunk;
        r5 = CHUNK_VUOP;
        r6 = 0;
        r7 = r2.length;
        r4.<init>(r5, r2, r6, r7);
    L_0x0025:
        return r4;
    L_0x0026:
        r3 = move-exception;
        r4 = 1;
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0049 }
        r5.<init>();	 Catch:{ all -> 0x0049 }
        r6 = "Unexpected error while profiling view: ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0049 }
        r6 = r3.getMessage();	 Catch:{ all -> 0x0049 }
        r5 = r5.append(r6);	 Catch:{ all -> 0x0049 }
        r5 = r5.toString();	 Catch:{ all -> 0x0049 }
        r4 = createFailChunk(r4, r5);	 Catch:{ all -> 0x0049 }
        r1.close();	 Catch:{ IOException -> 0x0047 }
        goto L_0x0025;
    L_0x0047:
        r5 = move-exception;
        goto L_0x0025;
    L_0x0049:
        r4 = move-exception;
        r1.close();	 Catch:{ IOException -> 0x0050 }
    L_0x004d:
        throw r4;
    L_0x004e:
        r4 = move-exception;
        goto L_0x0018;
    L_0x0050:
        r5 = move-exception;
        goto L_0x004d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.ddm.DdmHandleViewDebug.profileView(android.view.View, android.view.View):org.apache.harmony.dalvik.ddmc.Chunk");
    }
}
