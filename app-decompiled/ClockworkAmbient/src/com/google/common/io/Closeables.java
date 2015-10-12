package com.google.common.io;

import java.util.logging.Logger;

public final class Closeables {
    static final Logger logger;

    static {
        logger = Logger.getLogger(Closeables.class.getName());
    }

    private Closeables() {
    }
}
