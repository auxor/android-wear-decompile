package android.util;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

public final class LocalLog {
    private LinkedList<String> mLog;
    private int mMaxLines;
    private long mNow;

    public LocalLog(int maxLines) {
        this.mLog = new LinkedList();
        this.mMaxLines = maxLines;
    }

    public synchronized void log(String msg) {
        if (this.mMaxLines > 0) {
            this.mNow = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            Calendar.getInstance().setTimeInMillis(this.mNow);
            sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c}));
            this.mLog.add(sb.toString() + " - " + msg);
            while (this.mLog.size() > this.mMaxLines) {
                this.mLog.remove();
            }
        }
    }

    public synchronized void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        Iterator<String> itr = this.mLog.listIterator(0);
        while (itr.hasNext()) {
            pw.println((String) itr.next());
        }
    }
}
