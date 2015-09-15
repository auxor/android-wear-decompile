package android.app;

import android.app.IWallpaperManagerCallback.Stub;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityNodeInfo;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WallpaperManager {
    public static final String ACTION_CHANGE_LIVE_WALLPAPER = "android.service.wallpaper.CHANGE_LIVE_WALLPAPER";
    public static final String ACTION_CROP_AND_SET_WALLPAPER = "android.service.wallpaper.CROP_AND_SET_WALLPAPER";
    public static final String ACTION_LIVE_WALLPAPER_CHOOSER = "android.service.wallpaper.LIVE_WALLPAPER_CHOOSER";
    public static final String COMMAND_DROP = "android.home.drop";
    public static final String COMMAND_SECONDARY_TAP = "android.wallpaper.secondaryTap";
    public static final String COMMAND_TAP = "android.wallpaper.tap";
    private static boolean DEBUG = false;
    public static final String EXTRA_LIVE_WALLPAPER_COMPONENT = "android.service.wallpaper.extra.LIVE_WALLPAPER_COMPONENT";
    private static final String PROP_WALLPAPER = "ro.config.wallpaper";
    private static final String PROP_WALLPAPER_COMPONENT = "ro.config.wallpaper_component";
    private static String TAG = null;
    public static final String WALLPAPER_PREVIEW_META_DATA = "android.wallpaper.preview";
    private static Globals sGlobals;
    private static final Object sSync = null;
    private final Context mContext;
    private float mWallpaperXStep;
    private float mWallpaperYStep;

    static class FastBitmapDrawable extends Drawable {
        private final Bitmap mBitmap;
        private int mDrawLeft;
        private int mDrawTop;
        private final int mHeight;
        private final Paint mPaint;
        private final int mWidth;

        private FastBitmapDrawable(android.graphics.Bitmap r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.<init>(android.graphics.Bitmap):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.<init>(android.graphics.Bitmap):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.<init>(android.graphics.Bitmap):void");
        }

        /* synthetic */ FastBitmapDrawable(android.graphics.Bitmap r1, android.app.WallpaperManager.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.<init>(android.graphics.Bitmap, android.app.WallpaperManager$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.<init>(android.graphics.Bitmap, android.app.WallpaperManager$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.<init>(android.graphics.Bitmap, android.app.WallpaperManager$1):void");
        }

        public void draw(android.graphics.Canvas r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.draw(android.graphics.Canvas):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.draw(android.graphics.Canvas):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.draw(android.graphics.Canvas):void");
        }

        public int getIntrinsicHeight() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.getIntrinsicHeight():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.getIntrinsicHeight():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.getIntrinsicHeight():int");
        }

        public int getIntrinsicWidth() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.getIntrinsicWidth():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.getIntrinsicWidth():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.getIntrinsicWidth():int");
        }

        public int getMinimumHeight() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.getMinimumHeight():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.getMinimumHeight():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.getMinimumHeight():int");
        }

        public int getMinimumWidth() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.getMinimumWidth():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.getMinimumWidth():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.getMinimumWidth():int");
        }

        public void setBounds(int r1, int r2, int r3, int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.WallpaperManager.FastBitmapDrawable.setBounds(int, int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.WallpaperManager.FastBitmapDrawable.setBounds(int, int, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.FastBitmapDrawable.setBounds(int, int, int, int):void");
        }

        public int getOpacity() {
            return -1;
        }

        public void setAlpha(int alpha) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        public void setColorFilter(ColorFilter cf) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        public void setDither(boolean dither) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }

        public void setFilterBitmap(boolean filter) {
            throw new UnsupportedOperationException("Not supported with this drawable");
        }
    }

    static class Globals extends Stub {
        private static final int MSG_CLEAR_WALLPAPER = 1;
        private Bitmap mDefaultWallpaper;
        private IWallpaperManager mService;
        private Bitmap mWallpaper;

        Globals(Looper looper) {
            this.mService = IWallpaperManager.Stub.asInterface(ServiceManager.getService(Context.WALLPAPER_SERVICE));
        }

        public void onWallpaperChanged() {
            synchronized (this) {
                this.mWallpaper = null;
                this.mDefaultWallpaper = null;
            }
        }

        public Bitmap peekWallpaperBitmap(Context context, boolean returnDefault) {
            Bitmap bitmap;
            synchronized (this) {
                if (this.mWallpaper != null) {
                    bitmap = this.mWallpaper;
                } else if (this.mDefaultWallpaper != null) {
                    bitmap = this.mDefaultWallpaper;
                } else {
                    this.mWallpaper = null;
                    try {
                        this.mWallpaper = getCurrentWallpaperLocked(context);
                    } catch (OutOfMemoryError e) {
                        Log.w(WallpaperManager.TAG, "No memory load current wallpaper", e);
                    }
                    if (returnDefault) {
                        if (this.mWallpaper == null) {
                            this.mDefaultWallpaper = getDefaultWallpaperLocked(context);
                            bitmap = this.mDefaultWallpaper;
                        } else {
                            this.mDefaultWallpaper = null;
                        }
                    }
                    bitmap = this.mWallpaper;
                }
            }
            return bitmap;
        }

        public void forgetLoadedWallpaper() {
            synchronized (this) {
                this.mWallpaper = null;
                this.mDefaultWallpaper = null;
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private android.graphics.Bitmap getCurrentWallpaperLocked(android.content.Context r8) {
            /*
            r7 = this;
            r4 = 0;
            r5 = r7.mService;
            if (r5 != 0) goto L_0x000f;
        L_0x0005:
            r5 = android.app.WallpaperManager.TAG;
            r6 = "WallpaperService not running";
            android.util.Log.w(r5, r6);
        L_0x000e:
            return r4;
        L_0x000f:
            r3 = new android.os.Bundle;	 Catch:{ RemoteException -> 0x0044 }
            r3.<init>();	 Catch:{ RemoteException -> 0x0044 }
            r5 = r7.mService;	 Catch:{ RemoteException -> 0x0044 }
            r1 = r5.getWallpaper(r7, r3);	 Catch:{ RemoteException -> 0x0044 }
            if (r1 == 0) goto L_0x000e;
        L_0x001c:
            r2 = new android.graphics.BitmapFactory$Options;	 Catch:{ OutOfMemoryError -> 0x002f }
            r2.<init>();	 Catch:{ OutOfMemoryError -> 0x002f }
            r5 = r1.getFileDescriptor();	 Catch:{ OutOfMemoryError -> 0x002f }
            r6 = 0;
            r5 = android.graphics.BitmapFactory.decodeFileDescriptor(r5, r6, r2);	 Catch:{ OutOfMemoryError -> 0x002f }
            r1.close();	 Catch:{ IOException -> 0x0046 }
        L_0x002d:
            r4 = r5;
            goto L_0x000e;
        L_0x002f:
            r0 = move-exception;
            r5 = android.app.WallpaperManager.TAG;	 Catch:{ all -> 0x003f }
            r6 = "Can't decode file";
            android.util.Log.w(r5, r6, r0);	 Catch:{ all -> 0x003f }
            r1.close();	 Catch:{ IOException -> 0x003d }
            goto L_0x000e;
        L_0x003d:
            r5 = move-exception;
            goto L_0x000e;
        L_0x003f:
            r5 = move-exception;
            r1.close();	 Catch:{ IOException -> 0x0048 }
        L_0x0043:
            throw r5;	 Catch:{ RemoteException -> 0x0044 }
        L_0x0044:
            r5 = move-exception;
            goto L_0x000e;
        L_0x0046:
            r4 = move-exception;
            goto L_0x002d;
        L_0x0048:
            r6 = move-exception;
            goto L_0x0043;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.Globals.getCurrentWallpaperLocked(android.content.Context):android.graphics.Bitmap");
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private android.graphics.Bitmap getDefaultWallpaperLocked(android.content.Context r7) {
            /*
            r6 = this;
            r3 = 0;
            r1 = android.app.WallpaperManager.openDefaultWallpaper(r7);
            if (r1 == 0) goto L_0x0014;
        L_0x0007:
            r2 = new android.graphics.BitmapFactory$Options;	 Catch:{ OutOfMemoryError -> 0x0015 }
            r2.<init>();	 Catch:{ OutOfMemoryError -> 0x0015 }
            r4 = 0;
            r3 = android.graphics.BitmapFactory.decodeStream(r1, r4, r2);	 Catch:{ OutOfMemoryError -> 0x0015 }
            r1.close();	 Catch:{ IOException -> 0x002a }
        L_0x0014:
            return r3;
        L_0x0015:
            r0 = move-exception;
            r4 = android.app.WallpaperManager.TAG;	 Catch:{ all -> 0x0025 }
            r5 = "Can't decode stream";
            android.util.Log.w(r4, r5, r0);	 Catch:{ all -> 0x0025 }
            r1.close();	 Catch:{ IOException -> 0x0023 }
            goto L_0x0014;
        L_0x0023:
            r4 = move-exception;
            goto L_0x0014;
        L_0x0025:
            r3 = move-exception;
            r1.close();	 Catch:{ IOException -> 0x002c }
        L_0x0029:
            throw r3;
        L_0x002a:
            r4 = move-exception;
            goto L_0x0014;
        L_0x002c:
            r4 = move-exception;
            goto L_0x0029;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.Globals.getDefaultWallpaperLocked(android.content.Context):android.graphics.Bitmap");
        }
    }

    static {
        TAG = "WallpaperManager";
        DEBUG = false;
        sSync = new Object[0];
    }

    static void initGlobals(Looper looper) {
        synchronized (sSync) {
            if (sGlobals == null) {
                sGlobals = new Globals(looper);
            }
        }
    }

    WallpaperManager(Context context, Handler handler) {
        this.mWallpaperXStep = LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        this.mWallpaperYStep = LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        this.mContext = context;
        initGlobals(context.getMainLooper());
    }

    public static WallpaperManager getInstance(Context context) {
        return (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
    }

    public IWallpaperManager getIWallpaperManager() {
        return sGlobals.mService;
    }

    public Drawable getDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, true);
        if (bm == null) {
            return null;
        }
        Drawable dr = new BitmapDrawable(this.mContext.getResources(), bm);
        dr.setDither(false);
        return dr;
    }

    public Drawable getBuiltInDrawable() {
        return getBuiltInDrawable(0, 0, false, 0.0f, 0.0f);
    }

    public Drawable getBuiltInDrawable(int outWidth, int outHeight, boolean scaleToFit, float horizontalAlignment, float verticalAlignment) {
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return null;
        }
        Resources resources = this.mContext.getResources();
        horizontalAlignment = Math.max(0.0f, Math.min(LayoutParams.BRIGHTNESS_OVERRIDE_FULL, horizontalAlignment));
        verticalAlignment = Math.max(0.0f, Math.min(LayoutParams.BRIGHTNESS_OVERRIDE_FULL, verticalAlignment));
        InputStream bufferedInputStream = new BufferedInputStream(openDefaultWallpaper(this.mContext));
        if (bufferedInputStream == null) {
            Log.e(TAG, "default wallpaper input stream is null");
            return null;
        } else if (outWidth <= 0 || outHeight <= 0) {
            return new BitmapDrawable(resources, BitmapFactory.decodeStream(bufferedInputStream, null, null));
        } else {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(bufferedInputStream, null, options);
            if (options.outWidth == 0 || options.outHeight == 0) {
                Log.e(TAG, "default wallpaper dimensions are 0");
                return null;
            }
            RectF cropRectF;
            int inWidth = options.outWidth;
            int inHeight = options.outHeight;
            bufferedInputStream = new BufferedInputStream(openDefaultWallpaper(this.mContext));
            outWidth = Math.min(inWidth, outWidth);
            outHeight = Math.min(inHeight, outHeight);
            if (scaleToFit) {
                cropRectF = getMaxCropRect(inWidth, inHeight, outWidth, outHeight, horizontalAlignment, verticalAlignment);
            } else {
                float left = ((float) (inWidth - outWidth)) * horizontalAlignment;
                float top = ((float) (inHeight - outHeight)) * verticalAlignment;
                float f = left + ((float) outWidth);
                cropRectF = new RectF(left, top, right, top + ((float) outHeight));
            }
            Rect roundedTrueCrop = new Rect();
            cropRectF.roundOut(roundedTrueCrop);
            if (roundedTrueCrop.width() <= 0 || roundedTrueCrop.height() <= 0) {
                Log.w(TAG, "crop has bad values for full size image");
                return null;
            }
            int scaleDownSampleSize = Math.min(roundedTrueCrop.width() / outWidth, roundedTrueCrop.height() / outHeight);
            BitmapRegionDecoder decoder = null;
            try {
                decoder = BitmapRegionDecoder.newInstance(bufferedInputStream, true);
            } catch (IOException e) {
                Log.w(TAG, "cannot open region decoder for default wallpaper");
            }
            Bitmap crop = null;
            if (decoder != null) {
                options = new Options();
                if (scaleDownSampleSize > 1) {
                    options.inSampleSize = scaleDownSampleSize;
                }
                crop = decoder.decodeRegion(roundedTrueCrop, options);
                decoder.recycle();
            }
            if (crop == null) {
                bufferedInputStream = new BufferedInputStream(openDefaultWallpaper(this.mContext));
                Bitmap fullSize = null;
                if (bufferedInputStream != null) {
                    options = new Options();
                    if (scaleDownSampleSize > 1) {
                        options.inSampleSize = scaleDownSampleSize;
                    }
                    fullSize = BitmapFactory.decodeStream(bufferedInputStream, null, options);
                }
                if (fullSize != null) {
                    crop = Bitmap.createBitmap(fullSize, roundedTrueCrop.left, roundedTrueCrop.top, roundedTrueCrop.width(), roundedTrueCrop.height());
                }
            }
            if (crop == null) {
                Log.w(TAG, "cannot decode default wallpaper");
                return null;
            }
            if (outWidth > 0 && outHeight > 0 && !(crop.getWidth() == outWidth && crop.getHeight() == outHeight)) {
                Matrix m = new Matrix();
                RectF cropRect = new RectF(0.0f, 0.0f, (float) crop.getWidth(), (float) crop.getHeight());
                RectF rectF = new RectF(0.0f, 0.0f, (float) outWidth, (float) outHeight);
                m.setRectToRect(cropRect, rectF, ScaleToFit.FILL);
                Bitmap tmp = Bitmap.createBitmap((int) rectF.width(), (int) rectF.height(), Config.ARGB_8888);
                if (tmp != null) {
                    Canvas c = new Canvas(tmp);
                    Paint p = new Paint();
                    p.setFilterBitmap(true);
                    c.drawBitmap(crop, m, p);
                    crop = tmp;
                }
            }
            return new BitmapDrawable(resources, crop);
        }
    }

    private static RectF getMaxCropRect(int inWidth, int inHeight, int outWidth, int outHeight, float horizontalAlignment, float verticalAlignment) {
        RectF cropRect = new RectF();
        if (((float) inWidth) / ((float) inHeight) > ((float) outWidth) / ((float) outHeight)) {
            cropRect.top = 0.0f;
            cropRect.bottom = (float) inHeight;
            float cropWidth = ((float) outWidth) * (((float) inHeight) / ((float) outHeight));
            cropRect.left = (((float) inWidth) - cropWidth) * horizontalAlignment;
            cropRect.right = cropRect.left + cropWidth;
        } else {
            cropRect.left = 0.0f;
            cropRect.right = (float) inWidth;
            float cropHeight = ((float) outHeight) * (((float) inWidth) / ((float) outWidth));
            cropRect.top = (((float) inHeight) - cropHeight) * verticalAlignment;
            cropRect.bottom = cropRect.top + cropHeight;
        }
        return cropRect;
    }

    public Drawable peekDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, false);
        if (bm == null) {
            return null;
        }
        Drawable dr = new BitmapDrawable(this.mContext.getResources(), bm);
        dr.setDither(false);
        return dr;
    }

    public Drawable getFastDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, true);
        if (bm != null) {
            return new FastBitmapDrawable(bm, null);
        }
        return null;
    }

    public Drawable peekFastDrawable() {
        Bitmap bm = sGlobals.peekWallpaperBitmap(this.mContext, false);
        if (bm != null) {
            return new FastBitmapDrawable(bm, null);
        }
        return null;
    }

    public Bitmap getBitmap() {
        return sGlobals.peekWallpaperBitmap(this.mContext, true);
    }

    public void forgetLoadedWallpaper() {
        sGlobals.forgetLoadedWallpaper();
    }

    public WallpaperInfo getWallpaperInfo() {
        WallpaperInfo wallpaperInfo = null;
        try {
            if (sGlobals.mService == null) {
                Log.w(TAG, "WallpaperService not running");
            } else {
                wallpaperInfo = sGlobals.mService.getWallpaperInfo();
            }
        } catch (RemoteException e) {
        }
        return wallpaperInfo;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.Intent getCropAndSetWallpaperIntent(android.net.Uri r9) {
        /*
        r8 = this;
        r7 = 0;
        if (r9 != 0) goto L_0x000b;
    L_0x0003:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "Image URI must not be null";
        r5.<init>(r6);
        throw r5;
    L_0x000b:
        r5 = "content";
        r6 = r9.getScheme();
        r5 = r5.equals(r6);
        if (r5 != 0) goto L_0x001f;
    L_0x0017:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "Image URI must be of the content scheme type";
        r5.<init>(r6);
        throw r5;
    L_0x001f:
        r5 = r8.mContext;
        r3 = r5.getPackageManager();
        r0 = new android.content.Intent;
        r5 = "android.service.wallpaper.CROP_AND_SET_WALLPAPER";
        r0.<init>(r5, r9);
        r5 = 1;
        r0.addFlags(r5);
        r5 = new android.content.Intent;
        r6 = "android.intent.action.MAIN";
        r5.<init>(r6);
        r6 = "android.intent.category.HOME";
        r2 = r5.addCategory(r6);
        r5 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r4 = r3.resolveActivity(r2, r5);
        if (r4 == 0) goto L_0x0057;
    L_0x0045:
        r5 = r4.activityInfo;
        r5 = r5.packageName;
        r0.setPackage(r5);
        r1 = r3.queryIntentActivities(r0, r7);
        r5 = r1.size();
        if (r5 <= 0) goto L_0x0057;
    L_0x0056:
        return r0;
    L_0x0057:
        r5 = "com.android.wallpapercropper";
        r0.setPackage(r5);
        r1 = r3.queryIntentActivities(r0, r7);
        r5 = r1.size();
        if (r5 > 0) goto L_0x0056;
    L_0x0066:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "Cannot use passed URI to set wallpaper; check that the type returned by ContentProvider matches image/*";
        r5.<init>(r6);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.WallpaperManager.getCropAndSetWallpaperIntent(android.net.Uri):android.content.Intent");
    }

    public void setResource(int resid) throws IOException {
        Throwable th;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return;
        }
        try {
            Resources resources = this.mContext.getResources();
            ParcelFileDescriptor fd = sGlobals.mService.setWallpaper("res:" + resources.getResourceName(resid));
            if (fd != null) {
                FileOutputStream fos = null;
                try {
                    FileOutputStream fos2 = new AutoCloseOutputStream(fd);
                    try {
                        setWallpaper(resources.openRawResource(resid), fos2);
                        if (fos2 != null) {
                            fos2.close();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        fos = fos2;
                        if (fos != null) {
                            fos.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            }
        } catch (RemoteException e) {
        }
    }

    public void setBitmap(Bitmap bitmap) throws IOException {
        Throwable th;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return;
        }
        try {
            ParcelFileDescriptor fd = sGlobals.mService.setWallpaper(null);
            if (fd != null) {
                FileOutputStream fos = null;
                try {
                    FileOutputStream fos2 = new AutoCloseOutputStream(fd);
                    try {
                        bitmap.compress(CompressFormat.PNG, 90, fos2);
                        if (fos2 != null) {
                            fos2.close();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        fos = fos2;
                        if (fos != null) {
                            fos.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            }
        } catch (RemoteException e) {
        }
    }

    public void setStream(InputStream data) throws IOException {
        Throwable th;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return;
        }
        try {
            ParcelFileDescriptor fd = sGlobals.mService.setWallpaper(null);
            if (fd != null) {
                FileOutputStream fos = null;
                try {
                    FileOutputStream fos2 = new AutoCloseOutputStream(fd);
                    try {
                        setWallpaper(data, fos2);
                        if (fos2 != null) {
                            fos2.close();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        fos = fos2;
                        if (fos != null) {
                            fos.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            }
        } catch (RemoteException e) {
        }
    }

    private void setWallpaper(InputStream data, FileOutputStream fos) throws IOException {
        byte[] buffer = new byte[AccessibilityNodeInfo.ACTION_PASTE];
        while (true) {
            int amt = data.read(buffer);
            if (amt > 0) {
                fos.write(buffer, 0, amt);
            } else {
                return;
            }
        }
    }

    public boolean hasResourceWallpaper(int resid) {
        boolean z = false;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
        } else {
            try {
                z = sGlobals.mService.hasNamedWallpaper("res:" + this.mContext.getResources().getResourceName(resid));
            } catch (RemoteException e) {
            }
        }
        return z;
    }

    public int getDesiredMinimumWidth() {
        int i = 0;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
        } else {
            try {
                i = sGlobals.mService.getWidthHint();
            } catch (RemoteException e) {
            }
        }
        return i;
    }

    public int getDesiredMinimumHeight() {
        int i = 0;
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
        } else {
            try {
                i = sGlobals.mService.getHeightHint();
            } catch (RemoteException e) {
            }
        }
        return i;
    }

    public void suggestDesiredDimensions(int minimumWidth, int minimumHeight) {
        int maximumTextureSize;
        try {
            maximumTextureSize = SystemProperties.getInt("sys.max_texture_size", 0);
        } catch (Exception e) {
            maximumTextureSize = 0;
        }
        if (maximumTextureSize > 0 && (minimumWidth > maximumTextureSize || minimumHeight > maximumTextureSize)) {
            float aspect = ((float) minimumHeight) / ((float) minimumWidth);
            if (minimumWidth > minimumHeight) {
                minimumWidth = maximumTextureSize;
                minimumHeight = (int) (((double) (((float) minimumWidth) * aspect)) + 0.5d);
            } else {
                minimumHeight = maximumTextureSize;
                minimumWidth = (int) (((double) (((float) minimumHeight) / aspect)) + 0.5d);
            }
        }
        try {
            if (sGlobals.mService == null) {
                Log.w(TAG, "WallpaperService not running");
            } else {
                sGlobals.mService.setDimensionHints(minimumWidth, minimumHeight);
            }
        } catch (RemoteException e2) {
        }
    }

    public void setDisplayPadding(Rect padding) {
        try {
            if (sGlobals.mService == null) {
                Log.w(TAG, "WallpaperService not running");
            } else {
                sGlobals.mService.setDisplayPadding(padding);
            }
        } catch (RemoteException e) {
        }
    }

    public void setDisplayOffset(IBinder windowToken, int x, int y) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperDisplayOffset(windowToken, x, y);
        } catch (RemoteException e) {
        }
    }

    public void clearWallpaper() {
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return;
        }
        try {
            sGlobals.mService.clearWallpaper();
        } catch (RemoteException e) {
        }
    }

    public boolean setWallpaperComponent(ComponentName name) {
        if (sGlobals.mService == null) {
            Log.w(TAG, "WallpaperService not running");
            return false;
        }
        try {
            sGlobals.mService.setWallpaperComponent(name);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setWallpaperOffsets(IBinder windowToken, float xOffset, float yOffset) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(windowToken, xOffset, yOffset, this.mWallpaperXStep, this.mWallpaperYStep);
        } catch (RemoteException e) {
        }
    }

    public void setWallpaperOffsetSteps(float xStep, float yStep) {
        this.mWallpaperXStep = xStep;
        this.mWallpaperYStep = yStep;
    }

    public void sendWallpaperCommand(IBinder windowToken, String action, int x, int y, int z, Bundle extras) {
        try {
            WindowManagerGlobal.getWindowSession().sendWallpaperCommand(windowToken, action, x, y, z, extras, false);
        } catch (RemoteException e) {
        }
    }

    public void clearWallpaperOffsets(IBinder windowToken) {
        try {
            WindowManagerGlobal.getWindowSession().setWallpaperPosition(windowToken, LayoutParams.BRIGHTNESS_OVERRIDE_NONE, LayoutParams.BRIGHTNESS_OVERRIDE_NONE, LayoutParams.BRIGHTNESS_OVERRIDE_NONE, LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
        } catch (RemoteException e) {
        }
    }

    public void clear() throws IOException {
        setStream(openDefaultWallpaper(this.mContext));
    }

    public static InputStream openDefaultWallpaper(Context context) {
        String path = SystemProperties.get(PROP_WALLPAPER);
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    return new FileInputStream(file);
                } catch (IOException e) {
                }
            }
        }
        return context.getResources().openRawResource(17302155);
    }

    public static ComponentName getDefaultWallpaperComponent(Context context) {
        ComponentName cn;
        String flat = SystemProperties.get(PROP_WALLPAPER_COMPONENT);
        if (!TextUtils.isEmpty(flat)) {
            cn = ComponentName.unflattenFromString(flat);
            if (cn != null) {
                return cn;
            }
        }
        flat = context.getString(17039392);
        if (!TextUtils.isEmpty(flat)) {
            cn = ComponentName.unflattenFromString(flat);
            if (cn != null) {
                return cn;
            }
        }
        return null;
    }
}
