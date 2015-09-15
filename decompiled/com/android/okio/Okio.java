package com.android.okio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Okio {

    /* renamed from: com.android.okio.Okio.1 */
    static class AnonymousClass1 implements Sink {
        private Deadline deadline;
        final /* synthetic */ OutputStream val$out;

        AnonymousClass1(OutputStream outputStream) {
            this.val$out = outputStream;
            this.deadline = Deadline.NONE;
        }

        public void write(OkBuffer source, long byteCount) throws IOException {
            Util.checkOffsetAndCount(source.size, 0, byteCount);
            while (byteCount > 0) {
                this.deadline.throwIfReached();
                Segment head = source.head;
                int toCopy = (int) Math.min(byteCount, (long) (head.limit - head.pos));
                this.val$out.write(head.data, head.pos, toCopy);
                head.pos += toCopy;
                byteCount -= (long) toCopy;
                source.size -= (long) toCopy;
                if (head.pos == head.limit) {
                    source.head = head.pop();
                    SegmentPool.INSTANCE.recycle(head);
                }
            }
        }

        public void flush() throws IOException {
            this.val$out.flush();
        }

        public void close() throws IOException {
            this.val$out.close();
        }

        public Sink deadline(Deadline deadline) {
            if (deadline == null) {
                throw new IllegalArgumentException("deadline == null");
            }
            this.deadline = deadline;
            return this;
        }

        public String toString() {
            return "sink(" + this.val$out + ")";
        }
    }

    /* renamed from: com.android.okio.Okio.2 */
    static class AnonymousClass2 implements Source {
        private Deadline deadline;
        final /* synthetic */ InputStream val$in;

        AnonymousClass2(InputStream inputStream) {
            this.val$in = inputStream;
            this.deadline = Deadline.NONE;
        }

        public long read(OkBuffer sink, long byteCount) throws IOException {
            if (byteCount < 0) {
                throw new IllegalArgumentException("byteCount < 0: " + byteCount);
            }
            this.deadline.throwIfReached();
            Segment tail = sink.writableSegment(1);
            int bytesRead = this.val$in.read(tail.data, tail.limit, (int) Math.min(byteCount, (long) (2048 - tail.limit)));
            if (bytesRead == -1) {
                return -1;
            }
            tail.limit += bytesRead;
            sink.size += (long) bytesRead;
            return (long) bytesRead;
        }

        public void close() throws IOException {
            this.val$in.close();
        }

        public Source deadline(Deadline deadline) {
            if (deadline == null) {
                throw new IllegalArgumentException("deadline == null");
            }
            this.deadline = deadline;
            return this;
        }

        public String toString() {
            return "source(" + this.val$in + ")";
        }
    }

    private Okio() {
    }

    public static BufferedSource buffer(Source source) {
        return new RealBufferedSource(source);
    }

    public static BufferedSink buffer(Sink sink) {
        return new RealBufferedSink(sink);
    }

    public static void copy(OkBuffer source, long offset, long byteCount, OutputStream sink) throws IOException {
        Util.checkOffsetAndCount(source.size, offset, byteCount);
        Segment s = source.head;
        while (offset >= ((long) (s.limit - s.pos))) {
            offset -= (long) (s.limit - s.pos);
            s = s.next;
        }
        while (byteCount > 0) {
            int pos = (int) (((long) s.pos) + offset);
            int toWrite = (int) Math.min((long) (s.limit - pos), byteCount);
            sink.write(s.data, pos, toWrite);
            byteCount -= (long) toWrite;
            offset = 0;
        }
    }

    public static Sink sink(OutputStream out) {
        return new AnonymousClass1(out);
    }

    public static Source source(InputStream in) {
        return new AnonymousClass2(in);
    }
}
