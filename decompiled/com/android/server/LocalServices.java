package com.android.server;

import android.util.ArrayMap;

public final class LocalServices {
    private static final ArrayMap<Class<?>, Object> sLocalServiceObjects;

    private LocalServices() {
    }

    static {
        sLocalServiceObjects = new ArrayMap();
    }

    public static <T> T getService(Class<T> type) {
        T t;
        synchronized (sLocalServiceObjects) {
            t = sLocalServiceObjects.get(type);
        }
        return t;
    }

    public static <T> void addService(Class<T> type, T service) {
        synchronized (sLocalServiceObjects) {
            if (sLocalServiceObjects.containsKey(type)) {
                throw new IllegalStateException("Overriding service registration");
            }
            sLocalServiceObjects.put(type, service);
        }
    }
}
