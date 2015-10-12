package com.google.common.util.concurrent;

public final class MoreExecutors {

    static class Application {
        Application() {
        }

        void addShutdownHook(Thread thread) {
            Runtime.getRuntime().addShutdownHook(thread);
        }
    }
}
