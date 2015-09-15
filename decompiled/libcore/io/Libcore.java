package libcore.io;

public final class Libcore {
    public static Os os;

    private Libcore() {
    }

    static {
        os = new BlockGuardOs(new Posix());
    }
}
