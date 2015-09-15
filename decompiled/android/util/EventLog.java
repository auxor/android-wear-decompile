package android.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.microedition.khronos.opengles.GL10;

public class EventLog {
    private static final String COMMENT_PATTERN = "^\\s*(#.*)?$";
    private static final String TAG = "EventLog";
    private static final String TAGS_FILE = "/system/etc/event-log-tags";
    private static final String TAG_PATTERN = "^\\s*(\\d+)\\s+(\\w+)\\s*(\\(.*\\))?\\s*$";
    private static HashMap<String, Integer> sTagCodes;
    private static HashMap<Integer, String> sTagNames;

    public static final class Event {
        private static final int DATA_OFFSET = 4;
        private static final int HEADER_SIZE_OFFSET = 2;
        private static final byte INT_TYPE = (byte) 0;
        private static final int LENGTH_OFFSET = 0;
        private static final byte LIST_TYPE = (byte) 3;
        private static final byte LONG_TYPE = (byte) 1;
        private static final int NANOSECONDS_OFFSET = 16;
        private static final int PROCESS_OFFSET = 4;
        private static final int SECONDS_OFFSET = 12;
        private static final byte STRING_TYPE = (byte) 2;
        private static final int THREAD_OFFSET = 8;
        private static final int V1_PAYLOAD_START = 20;
        private final ByteBuffer mBuffer;

        Event(byte[] data) {
            this.mBuffer = ByteBuffer.wrap(data);
            this.mBuffer.order(ByteOrder.nativeOrder());
        }

        public int getProcessId() {
            return this.mBuffer.getInt(PROCESS_OFFSET);
        }

        public int getThreadId() {
            return this.mBuffer.getInt(THREAD_OFFSET);
        }

        public long getTimeNanos() {
            return (((long) this.mBuffer.getInt(SECONDS_OFFSET)) * 1000000000) + ((long) this.mBuffer.getInt(NANOSECONDS_OFFSET));
        }

        public int getTag() {
            int offset = this.mBuffer.getShort(HEADER_SIZE_OFFSET);
            if (offset == 0) {
                offset = V1_PAYLOAD_START;
            }
            return this.mBuffer.getInt(offset);
        }

        public synchronized Object getData() {
            Object obj = null;
            synchronized (this) {
                try {
                    int offset = this.mBuffer.getShort(HEADER_SIZE_OFFSET);
                    if (offset == 0) {
                        offset = V1_PAYLOAD_START;
                    }
                    this.mBuffer.limit(this.mBuffer.getShort(LENGTH_OFFSET) + offset);
                    this.mBuffer.position(offset + PROCESS_OFFSET);
                    obj = decodeObject();
                } catch (IllegalArgumentException e) {
                    Log.wtf(EventLog.TAG, "Illegal entry payload: tag=" + getTag(), e);
                } catch (BufferUnderflowException e2) {
                    Log.wtf(EventLog.TAG, "Truncated entry payload: tag=" + getTag(), e2);
                }
            }
            return obj;
        }

        private Object decodeObject() {
            byte type = this.mBuffer.get();
            int length;
            switch (type) {
                case LENGTH_OFFSET /*0*/:
                    return Integer.valueOf(this.mBuffer.getInt());
                case GL10.GL_TRUE /*1*/:
                    return Long.valueOf(this.mBuffer.getLong());
                case HEADER_SIZE_OFFSET /*2*/:
                    try {
                        length = this.mBuffer.getInt();
                        int start = this.mBuffer.position();
                        this.mBuffer.position(start + length);
                        return new String(this.mBuffer.array(), start, length, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.wtf(EventLog.TAG, "UTF-8 is not supported", e);
                        return null;
                    }
                case GL10.GL_LINE_STRIP /*3*/:
                    length = this.mBuffer.get();
                    if (length < 0) {
                        length += GL10.GL_DEPTH_BUFFER_BIT;
                    }
                    Object array = new Object[length];
                    for (int i = LENGTH_OFFSET; i < length; i++) {
                        array[i] = decodeObject();
                    }
                    return array;
                default:
                    throw new IllegalArgumentException("Unknown entry type: " + type);
            }
        }
    }

    public static native void readEvents(int[] iArr, Collection<Event> collection) throws IOException;

    public static native int writeEvent(int i, int i2);

    public static native int writeEvent(int i, long j);

    public static native int writeEvent(int i, String str);

    public static native int writeEvent(int i, Object... objArr);

    static {
        sTagCodes = null;
        sTagNames = null;
    }

    public static String getTagName(int tag) {
        readTagsFile();
        return (String) sTagNames.get(Integer.valueOf(tag));
    }

    public static int getTagCode(String name) {
        readTagsFile();
        Integer code = (Integer) sTagCodes.get(name);
        return code != null ? code.intValue() : -1;
    }

    private static synchronized void readTagsFile() {
        IOException e;
        Throwable th;
        synchronized (EventLog.class) {
            if (sTagCodes == null || sTagNames == null) {
                sTagCodes = new HashMap();
                sTagNames = new HashMap();
                Pattern comment = Pattern.compile(COMMENT_PATTERN);
                Pattern tag = Pattern.compile(TAG_PATTERN);
                BufferedReader bufferedReader = null;
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(TAGS_FILE), GL10.GL_DEPTH_BUFFER_BIT);
                    while (true) {
                        try {
                            String line = reader.readLine();
                            if (line == null) {
                                break;
                            } else if (!comment.matcher(line).matches()) {
                                Matcher m = tag.matcher(line);
                                if (m.matches()) {
                                    try {
                                        int num = Integer.parseInt(m.group(1));
                                        String name = m.group(2);
                                        sTagCodes.put(name, Integer.valueOf(num));
                                        sTagNames.put(Integer.valueOf(num), name);
                                    } catch (NumberFormatException e2) {
                                        Log.wtf(TAG, "Error in /system/etc/event-log-tags: " + line, e2);
                                    }
                                } else {
                                    Log.wtf(TAG, "Bad entry in /system/etc/event-log-tags: " + line);
                                }
                            }
                        } catch (IOException e3) {
                            e = e3;
                            bufferedReader = reader;
                        } catch (Throwable th2) {
                            th = th2;
                            bufferedReader = reader;
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e4) {
                            bufferedReader = reader;
                        }
                    }
                    bufferedReader = reader;
                } catch (IOException e5) {
                    e = e5;
                    try {
                        Log.wtf(TAG, "Error reading /system/etc/event-log-tags", e);
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e6) {
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (IOException e7) {
                            }
                        }
                        throw th;
                    }
                }
            }
        }
    }
}
