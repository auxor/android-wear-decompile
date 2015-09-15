package java.util.logging;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javax.xml.transform.OutputKeys;

public abstract class Handler {
    private static final Level DEFAULT_LEVEL;
    private String encoding;
    private ErrorManager errorMan;
    private Filter filter;
    private Formatter formatter;
    private Level level;
    private String prefix;

    public abstract void close();

    public abstract void flush();

    public abstract void publish(LogRecord logRecord);

    static {
        DEFAULT_LEVEL = Level.ALL;
    }

    protected Handler() {
        this.errorMan = new ErrorManager();
        this.level = DEFAULT_LEVEL;
        this.encoding = null;
        this.filter = null;
        this.formatter = null;
        this.prefix = getClass().getName();
    }

    private Object getDefaultInstance(String className) {
        Object result = null;
        if (className != null) {
            try {
                result = Class.forName(className).newInstance();
            } catch (Exception e) {
            }
        }
        return result;
    }

    private Object getCustomizeInstance(String className) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        return loader.loadClass(className).newInstance();
    }

    void printInvalidPropMessage(String key, String value, Exception e) {
        this.errorMan.error("Invalid property value for " + this.prefix + ":" + key + "/" + value, e, 0);
    }

    void initProperties(String defaultLevel, String defaultFilter, String defaultFormatter, String defaultEncoding) {
        LogManager manager = LogManager.getLogManager();
        String filterName = manager.getProperty(this.prefix + ".filter");
        if (filterName != null) {
            try {
                this.filter = (Filter) getCustomizeInstance(filterName);
            } catch (Exception e1) {
                printInvalidPropMessage("filter", filterName, e1);
                this.filter = (Filter) getDefaultInstance(defaultFilter);
            }
        } else {
            this.filter = (Filter) getDefaultInstance(defaultFilter);
        }
        String levelName = manager.getProperty(this.prefix + ".level");
        if (levelName != null) {
            try {
                this.level = Level.parse(levelName);
            } catch (Exception e) {
                printInvalidPropMessage("level", levelName, e);
                this.level = Level.parse(defaultLevel);
            }
        } else {
            this.level = Level.parse(defaultLevel);
        }
        String formatterName = manager.getProperty(this.prefix + ".formatter");
        if (formatterName != null) {
            try {
                this.formatter = (Formatter) getCustomizeInstance(formatterName);
            } catch (Exception e2) {
                printInvalidPropMessage("formatter", formatterName, e2);
                this.formatter = (Formatter) getDefaultInstance(defaultFormatter);
            }
        } else {
            this.formatter = (Formatter) getDefaultInstance(defaultFormatter);
        }
        String encodingName = manager.getProperty(this.prefix + ".encoding");
        try {
            internalSetEncoding(encodingName);
        } catch (UnsupportedEncodingException e3) {
            printInvalidPropMessage(OutputKeys.ENCODING, encodingName, e3);
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public ErrorManager getErrorManager() {
        LogManager.getLogManager().checkAccess();
        return this.errorMan;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public Formatter getFormatter() {
        return this.formatter;
    }

    public Level getLevel() {
        return this.level;
    }

    public boolean isLoggable(LogRecord record) {
        if (record == null) {
            throw new NullPointerException("record == null");
        } else if (this.level.intValue() == Level.OFF.intValue() || record.getLevel().intValue() < this.level.intValue()) {
            return false;
        } else {
            if (this.filter == null || this.filter.isLoggable(record)) {
                return true;
            }
            return false;
        }
    }

    protected void reportError(String msg, Exception ex, int code) {
        this.errorMan.error(msg, ex, code);
    }

    void internalSetEncoding(String newEncoding) throws UnsupportedEncodingException {
        if (newEncoding == null) {
            this.encoding = null;
        } else if (Charset.isSupported(newEncoding)) {
            this.encoding = newEncoding;
        } else {
            throw new UnsupportedEncodingException(newEncoding);
        }
    }

    public void setEncoding(String charsetName) throws UnsupportedEncodingException {
        LogManager.getLogManager().checkAccess();
        internalSetEncoding(charsetName);
    }

    public void setErrorManager(ErrorManager newErrorManager) {
        LogManager.getLogManager().checkAccess();
        if (newErrorManager == null) {
            throw new NullPointerException("newErrorManager == null");
        }
        this.errorMan = newErrorManager;
    }

    public void setFilter(Filter newFilter) {
        LogManager.getLogManager().checkAccess();
        this.filter = newFilter;
    }

    void internalSetFormatter(Formatter newFormatter) {
        if (newFormatter == null) {
            throw new NullPointerException("newFormatter == null");
        }
        this.formatter = newFormatter;
    }

    public void setFormatter(Formatter newFormatter) {
        LogManager.getLogManager().checkAccess();
        internalSetFormatter(newFormatter);
    }

    public void setLevel(Level newLevel) {
        if (newLevel == null) {
            throw new NullPointerException("newLevel == null");
        }
        LogManager.getLogManager().checkAccess();
        this.level = newLevel;
    }
}
