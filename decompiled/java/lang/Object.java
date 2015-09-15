package java.lang;

import javax.xml.datatype.DatatypeConstants;

public class Object {
    private transient Class<?> shadow$_klass_;
    private transient int shadow$_monitor_;

    private native Object internalClone();

    public final native void notify();

    public final native void notifyAll();

    public final native void wait() throws InterruptedException;

    public final native void wait(long j, int i) throws InterruptedException;

    protected Object clone() throws CloneNotSupportedException {
        if (this instanceof Cloneable) {
            return internalClone();
        }
        throw new CloneNotSupportedException("Class " + getClass().getName() + " doesn't implement Cloneable");
    }

    public boolean equals(Object o) {
        return this == o;
    }

    @FindBugsSuppressWarnings({"FI_EMPTY"})
    protected void finalize() throws Throwable {
    }

    public final Class<?> getClass() {
        return this.shadow$_klass_;
    }

    public int hashCode() {
        int lockWord = this.shadow$_monitor_;
        if ((-1073741824 & lockWord) == DatatypeConstants.FIELD_UNDEFINED) {
            return 1073741823 & lockWord;
        }
        return System.identityHashCode(this);
    }

    public String toString() {
        return getClass().getName() + '@' + Integer.toHexString(hashCode());
    }

    public final void wait(long millis) throws InterruptedException {
        wait(millis, 0);
    }
}
