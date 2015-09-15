package com.android.okhttp;

public enum ResponseSource {
    CACHE,
    CONDITIONAL_CACHE,
    NETWORK,
    NONE;

    public boolean requiresConnection() {
        return this == CONDITIONAL_CACHE || this == NETWORK;
    }

    public boolean usesCache() {
        return this == CACHE || this == CONDITIONAL_CACHE;
    }
}
