package libcore.util;

public final class SneakyThrow {
    private SneakyThrow() {
    }

    public static void sneakyThrow(Throwable t) {
        sneakyThrow2(t);
    }

    private static <T extends Throwable> void sneakyThrow2(Throwable t) throws Throwable {
        throw t;
    }
}
