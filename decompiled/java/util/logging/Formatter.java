package java.util.logging;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.xmlpull.v1.XmlPullParser;

public abstract class Formatter {
    public abstract String format(LogRecord logRecord);

    protected Formatter() {
    }

    public String formatMessage(LogRecord r) {
        String pattern = r.getMessage();
        ResourceBundle rb = r.getResourceBundle();
        if (rb != null) {
            try {
                pattern = rb.getString(pattern);
            } catch (Exception e) {
                pattern = r.getMessage();
            }
        }
        if (pattern == null) {
            return pattern;
        }
        Object[] params = r.getParameters();
        if (pattern.indexOf("{0") < 0 || params == null || params.length <= 0) {
            return pattern;
        }
        try {
            return MessageFormat.format(pattern, params);
        } catch (IllegalArgumentException e2) {
            return r.getMessage();
        }
    }

    public String getHead(Handler h) {
        return XmlPullParser.NO_NAMESPACE;
    }

    public String getTail(Handler h) {
        return XmlPullParser.NO_NAMESPACE;
    }
}
