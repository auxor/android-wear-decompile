package java.io;

import java.util.Formatter;
import java.util.Locale;

public class PrintWriter extends Writer {
    private boolean autoFlush;
    private boolean ioError;
    protected Writer out;

    public PrintWriter(OutputStream out) {
        this(new OutputStreamWriter(out), false);
    }

    public PrintWriter(OutputStream out, boolean autoFlush) {
        this(new OutputStreamWriter(out), autoFlush);
    }

    public PrintWriter(Writer wr) {
        this(wr, false);
    }

    public PrintWriter(Writer wr, boolean autoFlush) {
        super(wr);
        this.autoFlush = autoFlush;
        this.out = wr;
    }

    public PrintWriter(File file) throws FileNotFoundException {
        this(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file))), false);
    }

    public PrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        this(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file)), csn), false);
    }

    public PrintWriter(String fileName) throws FileNotFoundException {
        this(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(fileName))), false);
    }

    public PrintWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        this(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(fileName)), csn), false);
    }

    public boolean checkError() {
        Writer delegate = this.out;
        if (delegate == null) {
            return this.ioError;
        }
        flush();
        return this.ioError || delegate.checkError();
    }

    protected void clearError() {
        synchronized (this.lock) {
            this.ioError = false;
        }
    }

    public void close() {
        synchronized (this.lock) {
            if (this.out != null) {
                try {
                    this.out.close();
                } catch (IOException e) {
                    setError();
                }
                this.out = null;
            }
        }
    }

    public void flush() {
        synchronized (this.lock) {
            if (this.out != null) {
                try {
                    this.out.flush();
                } catch (IOException e) {
                    setError();
                }
            } else {
                setError();
            }
        }
    }

    public PrintWriter format(String format, Object... args) {
        return format(Locale.getDefault(), format, args);
    }

    public PrintWriter format(Locale l, String format, Object... args) {
        if (format == null) {
            throw new NullPointerException("format == null");
        }
        new Formatter((Appendable) this, l).format(format, args);
        if (this.autoFlush) {
            flush();
        }
        return this;
    }

    public PrintWriter printf(String format, Object... args) {
        return format(format, args);
    }

    public PrintWriter printf(Locale l, String format, Object... args) {
        return format(l, format, args);
    }

    public void print(char[] charArray) {
        print(new String(charArray, 0, charArray.length));
    }

    public void print(char ch) {
        print(String.valueOf(ch));
    }

    public void print(double dnum) {
        print(String.valueOf(dnum));
    }

    public void print(float fnum) {
        print(String.valueOf(fnum));
    }

    public void print(int inum) {
        print(String.valueOf(inum));
    }

    public void print(long lnum) {
        print(String.valueOf(lnum));
    }

    public void print(Object obj) {
        print(String.valueOf(obj));
    }

    public void print(String str) {
        if (str == null) {
            str = String.valueOf(null);
        }
        write(str);
    }

    public void print(boolean bool) {
        print(String.valueOf(bool));
    }

    public void println() {
        synchronized (this.lock) {
            print(System.lineSeparator());
            if (this.autoFlush) {
                flush();
            }
        }
    }

    public void println(char[] chars) {
        println(new String(chars, 0, chars.length));
    }

    public void println(char c) {
        println(String.valueOf(c));
    }

    public void println(double d) {
        println(String.valueOf(d));
    }

    public void println(float f) {
        println(String.valueOf(f));
    }

    public void println(int i) {
        println(String.valueOf(i));
    }

    public void println(long l) {
        println(String.valueOf(l));
    }

    public void println(Object obj) {
        println(String.valueOf(obj));
    }

    public void println(String str) {
        synchronized (this.lock) {
            print(str);
            println();
        }
    }

    public void println(boolean b) {
        println(String.valueOf(b));
    }

    protected void setError() {
        synchronized (this.lock) {
            this.ioError = true;
        }
    }

    public void write(char[] buf) {
        write(buf, 0, buf.length);
    }

    public void write(char[] buf, int offset, int count) {
        doWrite(buf, offset, count);
    }

    public void write(int oneChar) {
        doWrite(new char[]{(char) oneChar}, 0, 1);
    }

    private final void doWrite(char[] buf, int offset, int count) {
        synchronized (this.lock) {
            if (this.out != null) {
                try {
                    this.out.write(buf, offset, count);
                } catch (IOException e) {
                    setError();
                }
            } else {
                setError();
            }
        }
    }

    public void write(String str) {
        write(str.toCharArray());
    }

    public void write(String str, int offset, int count) {
        write(str.substring(offset, offset + count).toCharArray());
    }

    public PrintWriter append(char c) {
        write((int) c);
        return this;
    }

    public PrintWriter append(CharSequence csq) {
        if (csq == null) {
            csq = "null";
        }
        append(csq, 0, csq.length());
        return this;
    }

    public PrintWriter append(CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        String output = csq.subSequence(start, end).toString();
        write(output, 0, output.length());
        return this;
    }
}
