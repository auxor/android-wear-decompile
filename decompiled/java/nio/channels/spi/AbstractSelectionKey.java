package java.nio.channels.spi;

import java.nio.channels.SelectionKey;

public abstract class AbstractSelectionKey extends SelectionKey {
    boolean isValid;

    protected AbstractSelectionKey() {
        this.isValid = true;
    }

    public final boolean isValid() {
        return this.isValid;
    }

    public final void cancel() {
        if (this.isValid) {
            this.isValid = false;
            ((AbstractSelector) selector()).cancel(this);
        }
    }
}
