package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParser;

public class Throwable implements Serializable {
    private static final long serialVersionUID = -3042686055658047285L;
    private Throwable cause;
    private String detailMessage;
    private volatile transient Object stackState;
    private StackTraceElement[] stackTrace;
    private List<Throwable> suppressedExceptions;

    private static native Object nativeFillInStackTrace();

    private static native StackTraceElement[] nativeGetStackTrace(Object obj);

    public Throwable() {
        this.cause = this;
        this.suppressedExceptions = Collections.emptyList();
        this.stackTrace = EmptyArray.STACK_TRACE_ELEMENT;
        fillInStackTrace();
    }

    public Throwable(String detailMessage) {
        this.cause = this;
        this.suppressedExceptions = Collections.emptyList();
        this.detailMessage = detailMessage;
        this.stackTrace = EmptyArray.STACK_TRACE_ELEMENT;
        fillInStackTrace();
    }

    public Throwable(String detailMessage, Throwable cause) {
        this.cause = this;
        this.suppressedExceptions = Collections.emptyList();
        this.detailMessage = detailMessage;
        this.cause = cause;
        this.stackTrace = EmptyArray.STACK_TRACE_ELEMENT;
        fillInStackTrace();
    }

    public Throwable(Throwable cause) {
        this.cause = this;
        this.suppressedExceptions = Collections.emptyList();
        this.detailMessage = cause == null ? null : cause.toString();
        this.cause = cause;
        this.stackTrace = EmptyArray.STACK_TRACE_ELEMENT;
        fillInStackTrace();
    }

    protected Throwable(String detailMessage, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        this.cause = this;
        this.suppressedExceptions = Collections.emptyList();
        this.detailMessage = detailMessage;
        this.cause = cause;
        if (!enableSuppression) {
            this.suppressedExceptions = null;
        }
        if (writableStackTrace) {
            this.stackTrace = EmptyArray.STACK_TRACE_ELEMENT;
            fillInStackTrace();
            return;
        }
        this.stackTrace = null;
    }

    public Throwable fillInStackTrace() {
        if (this.stackTrace != null) {
            this.stackState = nativeFillInStackTrace();
            this.stackTrace = EmptyArray.STACK_TRACE_ELEMENT;
        }
        return this;
    }

    public String getMessage() {
        return this.detailMessage;
    }

    public String getLocalizedMessage() {
        return getMessage();
    }

    public StackTraceElement[] getStackTrace() {
        return (StackTraceElement[]) getInternalStackTrace().clone();
    }

    public void setStackTrace(StackTraceElement[] trace) {
        if (this.stackTrace != null) {
            StackTraceElement[] newTrace = (StackTraceElement[]) trace.clone();
            for (int i = 0; i < newTrace.length; i++) {
                if (newTrace[i] == null) {
                    throw new NullPointerException("trace[" + i + "] == null");
                }
            }
            this.stackTrace = newTrace;
        }
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int countDuplicates(java.lang.StackTraceElement[] r5, java.lang.StackTraceElement[] r6) {
        /*
        r0 = 0;
        r3 = r6.length;
        r1 = r5.length;
    L_0x0003:
        r1 = r1 + -1;
        if (r1 < 0) goto L_0x0018;
    L_0x0007:
        r3 = r3 + -1;
        if (r3 < 0) goto L_0x0018;
    L_0x000b:
        r2 = r6[r3];
        r4 = r5[r1];
        r4 = r2.equals(r4);
        if (r4 == 0) goto L_0x0018;
    L_0x0015:
        r0 = r0 + 1;
        goto L_0x0003;
    L_0x0018:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Throwable.countDuplicates(java.lang.StackTraceElement[], java.lang.StackTraceElement[]):int");
    }

    private StackTraceElement[] getInternalStackTrace() {
        if (this.stackTrace == EmptyArray.STACK_TRACE_ELEMENT) {
            this.stackTrace = nativeGetStackTrace(this.stackState);
            this.stackState = null;
            return this.stackTrace;
        } else if (this.stackTrace == null) {
            return EmptyArray.STACK_TRACE_ELEMENT;
        } else {
            return this.stackTrace;
        }
    }

    public void printStackTrace(PrintStream err) {
        try {
            printStackTrace(err, XmlPullParser.NO_NAMESPACE, null);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    public void printStackTrace(PrintWriter err) {
        try {
            printStackTrace(err, XmlPullParser.NO_NAMESPACE, null);
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    private void printStackTrace(Appendable err, String indent, StackTraceElement[] parentStack) throws IOException {
        err.append(toString());
        err.append((CharSequence) "\n");
        StackTraceElement[] stack = getInternalStackTrace();
        if (stack != null) {
            int duplicates = parentStack != null ? countDuplicates(stack, parentStack) : 0;
            for (int i = 0; i < stack.length - duplicates; i++) {
                err.append((CharSequence) indent);
                err.append((CharSequence) "\tat ");
                err.append(stack[i].toString());
                err.append((CharSequence) "\n");
            }
            if (duplicates > 0) {
                err.append((CharSequence) indent);
                err.append((CharSequence) "\t... ");
                err.append(Integer.toString(duplicates));
                err.append((CharSequence) " more\n");
            }
        }
        if (this.suppressedExceptions != null) {
            for (Throwable throwable : this.suppressedExceptions) {
                err.append((CharSequence) indent);
                err.append((CharSequence) "\tSuppressed: ");
                throwable.printStackTrace(err, indent + "\t", stack);
            }
        }
        Throwable cause = getCause();
        if (cause != null) {
            err.append((CharSequence) indent);
            err.append((CharSequence) "Caused by: ");
            cause.printStackTrace(err, indent, stack);
        }
    }

    public String toString() {
        String msg = getLocalizedMessage();
        String name = getClass().getName();
        return msg == null ? name : name + ": " + msg;
    }

    public Throwable initCause(Throwable throwable) {
        if (this.cause != this) {
            throw new IllegalStateException("Cause already initialized");
        } else if (throwable == this) {
            throw new IllegalArgumentException("throwable == this");
        } else {
            this.cause = throwable;
            return this;
        }
    }

    public Throwable getCause() {
        if (this.cause == this) {
            return null;
        }
        return this.cause;
    }

    public final void addSuppressed(Throwable throwable) {
        if (throwable == this) {
            throw new IllegalArgumentException("throwable == this");
        } else if (throwable == null) {
            throw new NullPointerException("throwable == null");
        } else if (this.suppressedExceptions != null) {
            if (this.suppressedExceptions.isEmpty()) {
                this.suppressedExceptions = new ArrayList(1);
            }
            this.suppressedExceptions.add(throwable);
        }
    }

    public final Throwable[] getSuppressed() {
        return (this.suppressedExceptions == null || this.suppressedExceptions.isEmpty()) ? EmptyArray.THROWABLE : (Throwable[]) this.suppressedExceptions.toArray(new Throwable[this.suppressedExceptions.size()]);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        getInternalStackTrace();
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.suppressedExceptions != null) {
            this.suppressedExceptions = new ArrayList(this.suppressedExceptions);
        }
    }
}
