package android.hardware.camera2;

import android.os.Handler;
import java.util.List;

public abstract class CameraCaptureSession implements AutoCloseable {

    public static abstract class CaptureCallback {
        public static final int NO_FRAMES_CAPTURED = -1;

        public void onCaptureStarted(android.hardware.camera2.CameraCaptureSession r1, android.hardware.camera2.CaptureRequest r2, long r3, long r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.hardware.camera2.CameraCaptureSession.CaptureCallback.onCaptureStarted(android.hardware.camera2.CameraCaptureSession, android.hardware.camera2.CaptureRequest, long, long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.hardware.camera2.CameraCaptureSession.CaptureCallback.onCaptureStarted(android.hardware.camera2.CameraCaptureSession, android.hardware.camera2.CaptureRequest, long, long):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.hardware.camera2.CameraCaptureSession.CaptureCallback.onCaptureStarted(android.hardware.camera2.CameraCaptureSession, android.hardware.camera2.CaptureRequest, long, long):void");
        }

        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp) {
        }

        public void onCapturePartial(CameraCaptureSession session, CaptureRequest request, CaptureResult result) {
        }

        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
        }

        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
        }

        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
        }

        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
        }

        public void onCaptureSequenceAborted(CameraCaptureSession session, int sequenceId) {
        }
    }

    public static abstract class CaptureListener extends CaptureCallback {
    }

    public static abstract class StateCallback {
        public abstract void onConfigureFailed(CameraCaptureSession cameraCaptureSession);

        public abstract void onConfigured(CameraCaptureSession cameraCaptureSession);

        public void onReady(CameraCaptureSession session) {
        }

        public void onActive(CameraCaptureSession session) {
        }

        public void onClosed(CameraCaptureSession session) {
        }
    }

    public static abstract class StateListener extends StateCallback {
    }

    public abstract void abortCaptures() throws CameraAccessException;

    public abstract int capture(CaptureRequest captureRequest, CaptureCallback captureCallback, Handler handler) throws CameraAccessException;

    public abstract int captureBurst(List<CaptureRequest> list, CaptureCallback captureCallback, Handler handler) throws CameraAccessException;

    public abstract void close();

    public abstract CameraDevice getDevice();

    public abstract int setRepeatingBurst(List<CaptureRequest> list, CaptureCallback captureCallback, Handler handler) throws CameraAccessException;

    public abstract int setRepeatingRequest(CaptureRequest captureRequest, CaptureCallback captureCallback, Handler handler) throws CameraAccessException;

    public abstract void stopRepeating() throws CameraAccessException;
}
