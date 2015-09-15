package java.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Date;
import libcore.io.IoUtils;

public class SimpleFormatter extends Formatter {
    public String format(LogRecord r) {
        Throwable th;
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("{0, date} {0, time} ", new Date(r.getMillis())));
        sb.append(r.getSourceClassName()).append(" ");
        sb.append(r.getSourceMethodName()).append(System.lineSeparator());
        sb.append(r.getLevel().getName()).append(": ");
        sb.append(formatMessage(r)).append(System.lineSeparator());
        if (r.getThrown() != null) {
            sb.append("Throwable occurred: ");
            Throwable t = r.getThrown();
            AutoCloseable pw = null;
            try {
                Writer sw = new StringWriter();
                AutoCloseable pw2 = new PrintWriter(sw);
                try {
                    t.printStackTrace((PrintWriter) pw2);
                    sb.append(sw.toString());
                    IoUtils.closeQuietly(pw2);
                } catch (Throwable th2) {
                    th = th2;
                    pw = pw2;
                    IoUtils.closeQuietly(pw);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                IoUtils.closeQuietly(pw);
                throw th;
            }
        }
        return sb.toString();
    }
}
