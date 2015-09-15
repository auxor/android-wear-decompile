package java.net;

public class SocketUtils {
    public static void setCreated(Socket s) {
        s.isCreated = true;
    }

    private SocketUtils() {
    }
}
