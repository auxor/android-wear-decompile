package com.google.common.util.concurrent;

import com.google.common.base.Supplier;
import java.util.concurrent.locks.ReadWriteLock;

public abstract class Striped<L> {
    private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER;

    private static abstract class PowerOfTwoStriped<L> extends Striped<L> {
    }

    static class LargeLazyStriped<L> extends PowerOfTwoStriped<L> {
    }

    static class SmallLazyStriped<L> extends PowerOfTwoStriped<L> {
    }

    static {
        READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
        };
    }
}
