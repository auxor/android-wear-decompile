package com.google.common.collect;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

class ComputingConcurrentHashMap<K, V> extends MapMakerInternalMap<K, V> {
    private static final long serialVersionUID = 4;
    final Function<? super K, ? extends V> computingFunction;

    private static final class ComputationExceptionReference<K, V> implements ValueReference<K, V> {
        final Throwable t;

        ComputationExceptionReference(Throwable th) {
            this.t = th;
            throw new VerifyError("bad dex opcode");
        }

        public void clear(ValueReference<K, V> valueReference) {
        }

        public ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            return this;
        }

        public V get() {
            return null;
        }

        public ReferenceEntry<K, V> getEntry() {
            return null;
        }

        public boolean isComputingReference() {
            return false;
        }

        public V waitForValue() throws ExecutionException {
            throw new ExecutionException(this.t);
        }
    }

    private static final class ComputedReference<K, V> implements ValueReference<K, V> {
        final V value;

        ComputedReference(V v) {
            this.value = v;
            throw new VerifyError("bad dex opcode");
        }

        public void clear(ValueReference<K, V> valueReference) {
        }

        public ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            return this;
        }

        public V get() {
            return this.value;
        }

        public ReferenceEntry<K, V> getEntry() {
            return null;
        }

        public boolean isComputingReference() {
            return false;
        }

        public V waitForValue() {
            return get();
        }
    }

    static final class ComputingSegment<K, V> extends Segment<K, V> {
        ComputingSegment(MapMakerInternalMap<K, V> mapMakerInternalMap, int i, int i2) {
            super(mapMakerInternalMap, i, i2);
        }

        V compute(K k, int i, ReferenceEntry<K, V> referenceEntry, ComputingValueReference<K, V> computingValueReference) throws ExecutionException {
            long nanoTime;
            Throwable th;
            V v = null;
            System.nanoTime();
            try {
                synchronized (referenceEntry) {
                    try {
                        v = computingValueReference.compute(k, i);
                        nanoTime = System.nanoTime();
                        try {
                            if (v != null) {
                                try {
                                    if (put(k, i, v, true) != null) {
                                        enqueueNotification(k, i, v, RemovalCause.REPLACED);
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                }
                            }
                            if (nanoTime == 0) {
                                System.nanoTime();
                            }
                            if (v == null) {
                                clearValue(k, i, computingValueReference);
                            }
                            return v;
                        } catch (Throwable th3) {
                            th = th3;
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        nanoTime = 0;
                        throw th;
                    }
                }
            } catch (Throwable th5) {
                th = th5;
                nanoTime = 0;
                if (nanoTime == 0) {
                    System.nanoTime();
                }
                if (v == null) {
                    clearValue(k, i, computingValueReference);
                }
                throw th;
            }
        }

        V getOrCompute(K k, int i, Function<? super K, ? extends V> function) throws ExecutionException {
            try {
                throw new VerifyError("bad dex opcode");
            } catch (Throwable th) {
                VerifyError verifyError = new VerifyError("bad dex opcode");
            }
        }
    }

    static final class ComputingSerializationProxy<K, V> extends AbstractSerializationProxy<K, V> {
        private static final long serialVersionUID = 4;
        final Function<? super K, ? extends V> computingFunction;

        ComputingSerializationProxy(Strength strength, Strength strength2, Equivalence<Object> equivalence, Equivalence<Object> equivalence2, long j, long j2, int i, int i2, RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> concurrentMap, Function<? super K, ? extends V> function) {
            super(strength, strength2, equivalence, equivalence2, j, j2, i, i2, removalListener, concurrentMap);
            this.computingFunction = function;
            throw new VerifyError("bad dex opcode");
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.delegate = readMapMaker(objectInputStream).makeComputingMap(this.computingFunction);
            readEntries(objectInputStream);
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            writeMapTo(objectOutputStream);
        }

        Object readResolve() {
            return this.delegate;
        }
    }

    private static final class ComputingValueReference<K, V> implements ValueReference<K, V> {
        volatile ValueReference<K, V> computedReference;
        final Function<? super K, ? extends V> computingFunction;

        public ComputingValueReference(Function<? super K, ? extends V> function) {
            this.computedReference = MapMakerInternalMap.unset();
            this.computingFunction = function;
            throw new VerifyError("bad dex opcode");
        }

        public void clear(ValueReference<K, V> valueReference) {
            setValueReference(valueReference);
        }

        V compute(K k, int i) throws ExecutionException {
            try {
                V apply = this.computingFunction.apply(k);
                setValueReference(new ComputedReference(apply));
                return apply;
            } catch (Throwable th) {
                setValueReference(new ComputationExceptionReference(th));
                ExecutionException executionException = new ExecutionException(th);
            }
        }

        public ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            return this;
        }

        public V get() {
            return null;
        }

        public ReferenceEntry<K, V> getEntry() {
            return null;
        }

        public boolean isComputingReference() {
            return true;
        }

        void setValueReference(ValueReference<K, V> valueReference) {
            synchronized (this) {
                if (this.computedReference == MapMakerInternalMap.UNSET) {
                    this.computedReference = valueReference;
                    notifyAll();
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public V waitForValue() throws java.util.concurrent.ExecutionException {
            /*
            r3 = this;
            r1 = 0;
            r0 = r3.computedReference;
            r2 = com.google.common.collect.MapMakerInternalMap.UNSET;
            if (r0 != r2) goto L_0x001f;
        L_0x0007:
            monitor-enter(r3);	 Catch:{ all -> 0x0029 }
        L_0x0008:
            r0 = r3.computedReference;	 Catch:{ all -> 0x0026 }
            r2 = com.google.common.collect.MapMakerInternalMap.UNSET;	 Catch:{ all -> 0x0026 }
            if (r0 != r2) goto L_0x0015;
        L_0x000e:
            r3.wait();	 Catch:{ InterruptedException -> 0x0012 }
            goto L_0x0008;
        L_0x0012:
            r0 = move-exception;
            r1 = 1;
            goto L_0x0008;
        L_0x0015:
            monitor-exit(r3);	 Catch:{ all -> 0x0026 }
            if (r1 == 0) goto L_0x001f;
        L_0x0018:
            r0 = java.lang.Thread.currentThread();
            r0.interrupt();
        L_0x001f:
            r0 = r3.computedReference;
            r0 = r0.waitForValue();
            return r0;
        L_0x0026:
            r0 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0026 }
            throw r0;	 Catch:{ all -> 0x0029 }
        L_0x0029:
            r0 = move-exception;
            if (r1 == 0) goto L_0x0033;
        L_0x002c:
            r1 = java.lang.Thread.currentThread();
            r1.interrupt();
        L_0x0033:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.ComputingConcurrentHashMap.ComputingValueReference.waitForValue():V");
        }
    }

    ComputingConcurrentHashMap(MapMaker mapMaker, Function<? super K, ? extends V> function) {
        super(mapMaker);
        Function function2 = (Function) Preconditions.checkNotNull(function);
        throw new VerifyError("bad dex opcode");
    }

    Segment<K, V> createSegment(int i, int i2) {
        return new ComputingSegment(this, i, i2);
    }

    V getOrCompute(K k) throws ExecutionException {
        int hash = hash(Preconditions.checkNotNull(k));
        return segmentFor(hash).getOrCompute(k, hash, this.computingFunction);
    }

    ComputingSegment<K, V> segmentFor(int i) {
        return (ComputingSegment) super.segmentFor(i);
    }

    Object writeReplace() {
        return new ComputingSerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this, this.computingFunction);
    }
}
