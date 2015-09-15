package java.util.logging;

public class ConsoleHandler extends StreamHandler {
    public ConsoleHandler() {
        super(System.err);
    }

    public void close() {
        super.close(false);
    }

    public void publish(LogRecord record) {
        super.publish(record);
        super.flush();
    }
}
