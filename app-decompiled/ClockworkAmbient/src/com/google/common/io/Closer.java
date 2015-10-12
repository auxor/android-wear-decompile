package com.google.common.io;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.logging.Level;

public final class Closer implements Closeable {
    private static final Suppressor SUPPRESSOR;
    private final LinkedList<Closeable> stack;
    final Suppressor suppressor;
    private Throwable thrown;

    interface Suppressor {
        void suppress(Closeable closeable, Throwable th, Throwable th2);
    }

    static final class LoggingSuppressor implements Suppressor {
        static final LoggingSuppressor INSTANCE;

        static {
            INSTANCE = new LoggingSuppressor();
        }

        LoggingSuppressor() {
        }

        public void suppress(Closeable closeable, Throwable th, Throwable th2) {
            Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + closeable, th2);
        }
    }

    static final class SuppressingSuppressor implements Suppressor {
        static final SuppressingSuppressor INSTANCE;
        static final Method addSuppressed;

        static {
            INSTANCE = new SuppressingSuppressor();
            addSuppressed = getAddSuppressed();
        }

        SuppressingSuppressor() {
        }

        private static Method getAddSuppressed() {
            try {
                throw new VerifyError("bad dex opcode");
            } catch (Throwable th) {
                return null;
            }
        }

        static boolean isAvailable() {
            return addSuppressed != null;
        }

        public void suppress(Closeable closeable, Throwable th, Throwable th2) {
            if (th != th2) {
                try {
                    Method method = addSuppressed;
                    throw new VerifyError("bad dex opcode");
                } catch (Throwable th3) {
                    LoggingSuppressor loggingSuppressor = LoggingSuppressor.INSTANCE;
                    VerifyError verifyError = new VerifyError("bad dex opcode");
                }
            }
        }
    }

    static {
        SUPPRESSOR = SuppressingSuppressor.isAvailable() ? SuppressingSuppressor.INSTANCE : LoggingSuppressor.INSTANCE;
    }

    Closer(Suppressor suppressor) {
        LinkedList linkedList = new LinkedList();
        throw new VerifyError("bad dex opcode");
    }

    public void close() throws IOException {
        throw new VerifyError("bad dex opcode");
    }
}
