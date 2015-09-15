package android.hardware.camera2.utils;

import android.hardware.camera2.utils.Decorator.DecoratorListener;
import android.os.DeadObjectException;
import android.os.RemoteException;
import android.view.WindowManager.LayoutParams;
import java.lang.reflect.Method;

public class CameraBinderDecorator {
    public static final int ALREADY_EXISTS = -17;
    public static final int BAD_VALUE = -22;
    public static final int DEAD_OBJECT = -32;
    public static final int EACCES = -13;
    public static final int EBUSY = -16;
    public static final int ENODEV = -19;
    public static final int EOPNOTSUPP = -95;
    public static final int EUSERS = -87;
    public static final int INVALID_OPERATION = -38;
    public static final int NO_ERROR = 0;
    public static final int PERMISSION_DENIED = -1;
    public static final int TIMED_OUT = -110;

    static class CameraBinderDecoratorListener implements DecoratorListener {
        public void onAfterInvocation(java.lang.reflect.Method r1, java.lang.Object[] r2, java.lang.Object r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.camera2.utils.CameraBinderDecorator.CameraBinderDecoratorListener.onAfterInvocation(java.lang.reflect.Method, java.lang.Object[], java.lang.Object):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.camera2.utils.CameraBinderDecorator.CameraBinderDecoratorListener.onAfterInvocation(java.lang.reflect.Method, java.lang.Object[], java.lang.Object):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.camera2.utils.CameraBinderDecorator.CameraBinderDecoratorListener.onAfterInvocation(java.lang.reflect.Method, java.lang.Object[], java.lang.Object):void");
        }

        CameraBinderDecoratorListener() {
        }

        public void onBeforeInvocation(Method m, Object[] args) {
        }

        public boolean onCatchException(Method m, Object[] args, Throwable t) {
            if (t instanceof DeadObjectException) {
                throw new CameraRuntimeException(2, "Process hosting the camera service has died unexpectedly", t);
            } else if (!(t instanceof RemoteException)) {
                return false;
            } else {
                throw new UnsupportedOperationException("An unknown RemoteException was thrown which should never happen.", t);
            }
        }

        public void onFinally(Method m, Object[] args) {
        }
    }

    public static void throwOnError(int errorFlag) {
        switch (errorFlag) {
            case TIMED_OUT /*-110*/:
                throw new CameraRuntimeException(3, "Operation timed out in camera service");
            case EOPNOTSUPP /*-95*/:
                throw new CameraRuntimeException(LayoutParams.TYPE_APPLICATION_PANEL);
            case EUSERS /*-87*/:
                throw new CameraRuntimeException(5);
            case INVALID_OPERATION /*-38*/:
                throw new CameraRuntimeException(3, "Illegal state encountered in camera service.");
            case DEAD_OBJECT /*-32*/:
                throw new CameraRuntimeException(2);
            case BAD_VALUE /*-22*/:
                throw new IllegalArgumentException("Bad argument passed to camera service");
            case ENODEV /*-19*/:
                throw new CameraRuntimeException(2);
            case ALREADY_EXISTS /*-17*/:
            case NO_ERROR /*0*/:
            case EBUSY /*-16*/:
                throw new CameraRuntimeException(4);
            case EACCES /*-13*/:
                throw new CameraRuntimeException(1);
            case PERMISSION_DENIED /*-1*/:
                throw new SecurityException("Lacking privileges to access camera service");
            default:
                if (errorFlag < 0) {
                    throw new UnsupportedOperationException(String.format("Unknown error %d", new Object[]{Integer.valueOf(errorFlag)}));
                }
        }
    }

    public static <T> T newInstance(T obj) {
        return Decorator.newInstance(obj, new CameraBinderDecoratorListener());
    }
}
