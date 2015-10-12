package com.google.common.util.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UncaughtExceptionHandlers {

    static final class Exiter implements UncaughtExceptionHandler {
        private static final Logger logger;
        private final Runtime runtime;

        static {
            logger = Logger.getLogger(Exiter.class.getName());
        }

        public void uncaughtException(Thread thread, Throwable th) {
            VerifyError verifyError;
            try {
                Logger logger = logger;
                Level level = Level.SEVERE;
                String.format("Caught an exception in %s.  Shutting down.", new Object[]{thread});
                throw new VerifyError("bad dex opcode");
            } catch (Throwable th2) {
                verifyError = new VerifyError("bad dex opcode");
            }
        }
    }
}
