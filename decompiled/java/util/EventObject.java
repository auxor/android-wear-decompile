package java.util;

import java.io.Serializable;

public class EventObject implements Serializable {
    private static final long serialVersionUID = 5516075349620653480L;
    @FindBugsSuppressWarnings({"SE_TRANSIENT_FIELD_NOT_RESTORED"})
    protected transient Object source;

    public EventObject(Object source) {
        if (source == null) {
            throw new IllegalArgumentException("source == null");
        }
        this.source = source;
    }

    public Object getSource() {
        return this.source;
    }

    public String toString() {
        return getClass().getName() + "[source=" + this.source + ']';
    }
}
