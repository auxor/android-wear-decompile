package com.android.internal.util;

import android.util.Slog;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LocalLog {
    private final ArrayList<String> mLines;
    private final int mMaxLines;
    private final String mTag;

    public LocalLog(String tag) {
        this.mMaxLines = 20;
        this.mLines = new ArrayList(20);
        this.mTag = tag;
    }

    public void w(String msg) {
        synchronized (this.mLines) {
            Slog.w(this.mTag, msg);
            if (this.mLines.size() >= 20) {
                this.mLines.remove(0);
            }
            this.mLines.add(msg);
        }
    }

    public boolean dump(PrintWriter pw, String header, String prefix) {
        boolean z;
        synchronized (this.mLines) {
            if (this.mLines.size() <= 0) {
                z = false;
            } else {
                if (header != null) {
                    pw.println(header);
                }
                for (int i = 0; i < this.mLines.size(); i++) {
                    if (prefix != null) {
                        pw.print(prefix);
                    }
                    pw.println((String) this.mLines.get(i));
                }
                z = true;
            }
        }
        return z;
    }
}
