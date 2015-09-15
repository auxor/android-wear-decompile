package java.util.logging;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class StreamHandler extends Handler {
    private OutputStream os;
    private Writer writer;
    private boolean writerNotInitialized;

    public StreamHandler() {
        initProperties("INFO", null, "java.util.logging.SimpleFormatter", null);
        this.os = null;
        this.writer = null;
        this.writerNotInitialized = true;
    }

    StreamHandler(OutputStream os) {
        this();
        this.os = os;
    }

    StreamHandler(String defaultLevel, String defaultFilter, String defaultFormatter, String defaultEncoding) {
        initProperties(defaultLevel, defaultFilter, defaultFormatter, defaultEncoding);
        this.os = null;
        this.writer = null;
        this.writerNotInitialized = true;
    }

    public StreamHandler(OutputStream os, Formatter formatter) {
        this();
        if (os == null) {
            throw new NullPointerException("os == null");
        } else if (formatter == null) {
            throw new NullPointerException("formatter == null");
        } else {
            this.os = os;
            internalSetFormatter(formatter);
        }
    }

    private void initializeWriter() {
        this.writerNotInitialized = false;
        if (getEncoding() == null) {
            this.writer = new OutputStreamWriter(this.os);
        } else {
            try {
                this.writer = new OutputStreamWriter(this.os, getEncoding());
            } catch (UnsupportedEncodingException e) {
            }
        }
        write(getFormatter().getHead(this));
    }

    private void write(String s) {
        try {
            this.writer.write(s);
        } catch (Exception e) {
            getErrorManager().error("Exception occurred when writing to the output stream", e, 1);
        }
    }

    void internalSetOutputStream(OutputStream newOs) {
        this.os = newOs;
    }

    protected void setOutputStream(OutputStream os) {
        if (os == null) {
            throw new NullPointerException("os == null");
        }
        LogManager.getLogManager().checkAccess();
        close(true);
        this.writer = null;
        this.os = os;
        this.writerNotInitialized = true;
    }

    public void setEncoding(String charsetName) throws UnsupportedEncodingException {
        flush();
        super.setEncoding(charsetName);
        if (this.writer == null) {
            return;
        }
        if (getEncoding() == null) {
            this.writer = new OutputStreamWriter(this.os);
            return;
        }
        try {
            this.writer = new OutputStreamWriter(this.os, getEncoding());
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    void close(boolean closeStream) {
        if (this.os != null) {
            if (this.writerNotInitialized) {
                initializeWriter();
            }
            write(getFormatter().getTail(this));
            try {
                this.writer.flush();
                if (closeStream) {
                    this.writer.close();
                    this.writer = null;
                    this.os = null;
                }
            } catch (Exception e) {
                getErrorManager().error("Exception occurred when closing the output stream", e, 3);
            }
        }
    }

    public void close() {
        LogManager.getLogManager().checkAccess();
        close(true);
    }

    public void flush() {
        if (this.os != null) {
            try {
                if (this.writer != null) {
                    this.writer.flush();
                } else {
                    this.os.flush();
                }
            } catch (Exception e) {
                getErrorManager().error("Exception occurred when flushing the output stream", e, 2);
            }
        }
    }

    public synchronized void publish(LogRecord record) {
        try {
            if (isLoggable(record)) {
                if (this.writerNotInitialized) {
                    initializeWriter();
                }
                String msg = null;
                try {
                    msg = getFormatter().format(record);
                } catch (Exception e) {
                    getErrorManager().error("Exception occurred when formatting the log record", e, 5);
                }
                write(msg);
            }
        } catch (Exception e2) {
            getErrorManager().error("Exception occurred when logging the record", e2, 0);
        }
    }

    public boolean isLoggable(LogRecord record) {
        if (record == null || this.os == null || !super.isLoggable(record)) {
            return false;
        }
        return true;
    }
}
