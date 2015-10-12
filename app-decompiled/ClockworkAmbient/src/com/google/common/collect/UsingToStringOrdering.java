package com.google.common.collect;

import java.io.Serializable;

final class UsingToStringOrdering extends Ordering<Object> implements Serializable {
    static final UsingToStringOrdering INSTANCE;
    private static final long serialVersionUID = 0;

    static {
        INSTANCE = new UsingToStringOrdering();
    }

    private UsingToStringOrdering() {
    }

    private Object readResolve() {
        return INSTANCE;
    }

    public int compare(Object obj, Object obj2) {
        return obj.toString().compareTo(obj2.toString());
    }

    public String toString() {
        return "Ordering.usingToString()";
    }
}
