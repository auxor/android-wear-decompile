package android.net;

import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SntpClient {
    private static final int NTP_MODE_CLIENT = 3;
    private static final int NTP_PACKET_SIZE = 48;
    private static final int NTP_PORT = 123;
    private static final int NTP_VERSION = 3;
    private static final long OFFSET_1900_TO_1970 = 2208988800L;
    private static final int ORIGINATE_TIME_OFFSET = 24;
    private static final int RECEIVE_TIME_OFFSET = 32;
    private static final int REFERENCE_TIME_OFFSET = 16;
    private static final String TAG = "SntpClient";
    private static final int TRANSMIT_TIME_OFFSET = 40;
    private long mNtpTime;
    private long mNtpTimeReference;
    private long mRoundTripTime;

    public boolean requestTime(String host, int timeout) {
        Throwable th;
        DatagramSocket socket = null;
        try {
            DatagramSocket socket2 = new DatagramSocket();
            try {
                socket2.setSoTimeout(timeout);
                InetAddress address = InetAddress.getByName(host);
                byte[] buffer = new byte[NTP_PACKET_SIZE];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, NTP_PORT);
                buffer[0] = (byte) 27;
                long requestTime = System.currentTimeMillis();
                long requestTicks = SystemClock.elapsedRealtime();
                writeTimeStamp(buffer, TRANSMIT_TIME_OFFSET, requestTime);
                socket2.send(request);
                socket2.receive(new DatagramPacket(buffer, buffer.length));
                long responseTicks = SystemClock.elapsedRealtime();
                long responseTime = requestTime + (responseTicks - requestTicks);
                long originateTime = readTimeStamp(buffer, ORIGINATE_TIME_OFFSET);
                long receiveTime = readTimeStamp(buffer, RECEIVE_TIME_OFFSET);
                long transmitTime = readTimeStamp(buffer, TRANSMIT_TIME_OFFSET);
                long roundTripTime = (responseTicks - requestTicks) - (transmitTime - receiveTime);
                this.mNtpTime = responseTime + (((receiveTime - originateTime) + (transmitTime - responseTime)) / 2);
                this.mNtpTimeReference = responseTicks;
                this.mRoundTripTime = roundTripTime;
                if (socket2 != null) {
                    socket2.close();
                }
                socket = socket2;
                return true;
            } catch (Exception e) {
                socket = socket2;
                if (socket != null) {
                    return false;
                }
                socket.close();
                return false;
            } catch (Throwable th2) {
                th = th2;
                socket = socket2;
                if (socket != null) {
                    socket.close();
                }
                throw th;
            }
        } catch (Exception e2) {
            if (socket != null) {
                return false;
            }
            socket.close();
            return false;
        } catch (Throwable th3) {
            th = th3;
            if (socket != null) {
                socket.close();
            }
            throw th;
        }
    }

    public long getNtpTime() {
        return this.mNtpTime;
    }

    public long getNtpTimeReference() {
        return this.mNtpTimeReference;
    }

    public long getRoundTripTime() {
        return this.mRoundTripTime;
    }

    private long read32(byte[] buffer, int offset) {
        int i0;
        int i1;
        int i2;
        int i3;
        byte b0 = buffer[offset];
        byte b1 = buffer[offset + 1];
        byte b2 = buffer[offset + 2];
        byte b3 = buffer[offset + NTP_VERSION];
        if ((b0 & AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) == AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) {
            i0 = (b0 & KeyEvent.KEYCODE_MEDIA_PAUSE) + AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
        } else {
            byte i02 = b0;
        }
        if ((b1 & AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) == AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) {
            i1 = (b1 & KeyEvent.KEYCODE_MEDIA_PAUSE) + AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
        } else {
            byte i12 = b1;
        }
        if ((b2 & AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) == AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) {
            i2 = (b2 & KeyEvent.KEYCODE_MEDIA_PAUSE) + AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
        } else {
            byte i22 = b2;
        }
        if ((b3 & AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) == AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS) {
            i3 = (b3 & KeyEvent.KEYCODE_MEDIA_PAUSE) + AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
        } else {
            byte i32 = b3;
        }
        return (((((long) i0) << ORIGINATE_TIME_OFFSET) + (((long) i1) << REFERENCE_TIME_OFFSET)) + (((long) i2) << 8)) + ((long) i3);
    }

    private long readTimeStamp(byte[] buffer, int offset) {
        return ((read32(buffer, offset) - OFFSET_1900_TO_1970) * 1000) + ((1000 * read32(buffer, offset + 4)) / 4294967296L);
    }

    private void writeTimeStamp(byte[] buffer, int offset, long time) {
        long seconds = time / 1000;
        long milliseconds = time - (1000 * seconds);
        seconds += OFFSET_1900_TO_1970;
        int i = offset + 1;
        buffer[offset] = (byte) ((int) (seconds >> ORIGINATE_TIME_OFFSET));
        offset = i + 1;
        buffer[i] = (byte) ((int) (seconds >> REFERENCE_TIME_OFFSET));
        i = offset + 1;
        buffer[offset] = (byte) ((int) (seconds >> 8));
        offset = i + 1;
        buffer[i] = (byte) ((int) (seconds >> null));
        long fraction = (4294967296L * milliseconds) / 1000;
        i = offset + 1;
        buffer[offset] = (byte) ((int) (fraction >> ORIGINATE_TIME_OFFSET));
        offset = i + 1;
        buffer[i] = (byte) ((int) (fraction >> REFERENCE_TIME_OFFSET));
        i = offset + 1;
        buffer[offset] = (byte) ((int) (fraction >> 8));
        offset = i + 1;
        buffer[i] = (byte) ((int) (Math.random() * 255.0d));
    }
}
