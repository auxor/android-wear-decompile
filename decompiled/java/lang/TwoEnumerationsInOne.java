package java.lang;

import java.net.URL;
import java.util.Enumeration;

/* compiled from: ClassLoader */
class TwoEnumerationsInOne implements Enumeration<URL> {
    private final Enumeration<URL> first;
    private final Enumeration<URL> second;

    public TwoEnumerationsInOne(Enumeration<URL> first, Enumeration<URL> second) {
        this.first = first;
        this.second = second;
    }

    public boolean hasMoreElements() {
        return this.first.hasMoreElements() || this.second.hasMoreElements();
    }

    public URL nextElement() {
        if (this.first.hasMoreElements()) {
            return (URL) this.first.nextElement();
        }
        return (URL) this.second.nextElement();
    }
}
