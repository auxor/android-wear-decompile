package java.io;

import dalvik.bytecode.Opcodes;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

public class PrintStream extends FilterOutputStream implements Appendable, Closeable {
    private boolean autoFlush;
    private String encoding;
    private boolean ioError;

    public PrintStream(OutputStream out) {
        super(out);
        if (out == null) {
            throw new NullPointerException("out == null");
        }
    }

    public PrintStream(OutputStream out, boolean autoFlush) {
        super(out);
        if (out == null) {
            throw new NullPointerException("out == null");
        }
        this.autoFlush = autoFlush;
    }

    public PrintStream(OutputStream out, boolean autoFlush, String charsetName) throws UnsupportedEncodingException {
        super(out);
        if (out == null) {
            throw new NullPointerException("out == null");
        } else if (charsetName == null) {
            throw new NullPointerException("charsetName == null");
        } else {
            this.autoFlush = autoFlush;
            try {
                if (Charset.isSupported(charsetName)) {
                    this.encoding = charsetName;
                    return;
                }
                throw new UnsupportedEncodingException(charsetName);
            } catch (IllegalCharsetNameException e) {
                throw new UnsupportedEncodingException(charsetName);
            }
        }
    }

    public PrintStream(File file) throws FileNotFoundException {
        super(new FileOutputStream(file));
    }

    public PrintStream(File file, String charsetName) throws FileNotFoundException, UnsupportedEncodingException {
        super(new FileOutputStream(file));
        if (charsetName == null) {
            throw new NullPointerException("charsetName == null");
        } else if (Charset.isSupported(charsetName)) {
            this.encoding = charsetName;
        } else {
            throw new UnsupportedEncodingException(charsetName);
        }
    }

    public PrintStream(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }

    public PrintStream(String fileName, String charsetName) throws FileNotFoundException, UnsupportedEncodingException {
        this(new File(fileName), charsetName);
    }

    public boolean checkError() {
        OutputStream delegate = this.out;
        if (delegate == null) {
            return this.ioError;
        }
        flush();
        return this.ioError || delegate.checkError();
    }

    protected void clearError() {
        this.ioError = false;
    }

    public synchronized void close() {
        flush();
        if (this.out != null) {
            try {
                this.out.close();
                this.out = null;
            } catch (IOException e) {
                setError();
            }
        }
    }

    public synchronized void flush() {
        if (this.out != null) {
            try {
                this.out.flush();
            } catch (IOException e) {
            }
        }
        setError();
    }

    public PrintStream format(String format, Object... args) {
        return format(Locale.getDefault(), format, args);
    }

    public PrintStream format(Locale l, String format, Object... args) {
        if (format == null) {
            throw new NullPointerException("format == null");
        }
        new Formatter((Appendable) this, l).format(format, args);
        return this;
    }

    public PrintStream printf(String format, Object... args) {
        return format(format, args);
    }

    public PrintStream printf(Locale l, String format, Object... args) {
        return format(l, format, args);
    }

    private void newline() {
        print(System.lineSeparator());
    }

    public void print(char[] chars) {
        print(new String(chars, 0, chars.length));
    }

    public void print(char c) {
        print(String.valueOf(c));
    }

    public void print(double d) {
        print(String.valueOf(d));
    }

    public void print(float f) {
        print(String.valueOf(f));
    }

    public void print(int i) {
        print(String.valueOf(i));
    }

    public void print(long l) {
        print(String.valueOf(l));
    }

    public void print(Object o) {
        print(String.valueOf(o));
    }

    public synchronized void print(String str) {
        if (this.out == null) {
            setError();
        } else if (str == null) {
            print("null");
        } else {
            try {
                if (this.encoding == null) {
                    write(str.getBytes());
                } else {
                    write(str.getBytes(this.encoding));
                }
            } catch (IOException e) {
                setError();
            }
        }
    }

    public void print(boolean b) {
        print(String.valueOf(b));
    }

    public void println() {
        newline();
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

    public void println(Object o) {
        println(String.valueOf(o));
    }

    public synchronized void println(String str) {
        print(str);
        newline();
    }

    public void println(boolean b) {
        println(String.valueOf(b));
    }

    protected void setError() {
        this.ioError = true;
    }

    public void write(byte[] buffer, int offset, int length) {
        Arrays.checkOffsetAndCount(buffer.length, offset, length);
        synchronized (this) {
            if (this.out == null) {
                setError();
                return;
            }
            try {
                this.out.write(buffer, offset, length);
                if (this.autoFlush) {
                    flush();
                }
            } catch (IOException e) {
                setError();
            }
        }
    }

    public synchronized void write(int oneByte) {
        if (this.out == null) {
            setError();
        } else {
            try {
                this.out.write(oneByte);
                int b = oneByte & Opcodes.OP_CONST_CLASS_JUMBO;
                boolean isNewline = b == 10 || b == 21;
                if (this.autoFlush && isNewline) {
                    flush();
                }
            } catch (IOException e) {
                setError();
            }
        }
    }

    public PrintStream append(char c) {
        print(c);
        return this;
    }

    public PrintStream append(CharSequence charSequence) {
        if (charSequence == null) {
            print("null");
        } else {
            print(charSequence.toString());
        }
        return this;
    }

    public PrintStream append(CharSequence charSequence, int start, int end) {
        if (charSequence == null) {
            charSequence = "null";
        }
        print(charSequence.subSequence(start, end).toString());
        return this;
    }
}
