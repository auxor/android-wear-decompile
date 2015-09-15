package java.net;

public class Proxy {
    public static final Proxy NO_PROXY;
    private SocketAddress address;
    private Type type;

    public enum Type {
        DIRECT,
        HTTP,
        SOCKS
    }

    static {
        NO_PROXY = new Proxy();
    }

    public Proxy(Type type, SocketAddress sa) {
        if (type == Type.DIRECT || sa == null) {
            throw new IllegalArgumentException("Illegal Proxy.Type or SocketAddress argument");
        }
        this.type = type;
        this.address = sa;
    }

    private Proxy() {
        this.type = Type.DIRECT;
        this.address = null;
    }

    public Type type() {
        return this.type;
    }

    public SocketAddress address() {
        return this.address;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.type != null) {
            builder.append(this.type.toString());
        }
        builder.append("@");
        if (!(this.type == Type.DIRECT || this.address == null)) {
            builder.append(this.address.toString());
        }
        return builder.toString();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Proxy)) {
            return false;
        }
        Proxy another = (Proxy) obj;
        if (this.type == another.type && this.address.equals(another.address)) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int ret = 0 + this.type.hashCode();
        if (this.address != null) {
            return ret + this.address.hashCode();
        }
        return ret;
    }
}
