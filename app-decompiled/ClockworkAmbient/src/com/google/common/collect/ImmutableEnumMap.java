package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;

final class ImmutableEnumMap<K extends Enum<K>, V> extends ImmutableMap<K, V> {
    private final transient EnumMap<K, V> delegate;

    private static class EnumSerializedForm<K extends Enum<K>, V> implements Serializable {
        private static final long serialVersionUID = 0;
        final EnumMap<K, V> delegate;

        EnumSerializedForm(EnumMap<K, V> enumMap) {
            this.delegate = enumMap;
            throw new VerifyError("bad dex opcode");
        }

        Object readResolve() {
            return new ImmutableEnumMap(null);
        }
    }

    private ImmutableEnumMap(EnumMap<K, V> enumMap) {
        this.delegate = enumMap;
        Preconditions.checkArgument(!enumMap.isEmpty());
        throw new VerifyError("bad dex opcode");
    }

    static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> enumMap) {
        throw new VerifyError("bad dex opcode");
    }

    public boolean containsKey(Object obj) {
        return this.delegate.containsKey(obj);
    }

    ImmutableSet<Entry<K, V>> createEntrySet() {
        return new ImmutableMapEntrySet<K, V>() {
            {
                throw new VerifyError("bad dex opcode");
            }

            public UnmodifiableIterator<Entry<K, V>> iterator() {
                return new UnmodifiableIterator<Entry<K, V>>() {
                    private final Iterator<Entry<K, V>> backingIterator;

                    {
                        this.backingIterator = ImmutableEnumMap.this.delegate.entrySet().iterator();
                        throw new VerifyError("bad dex opcode");
                    }

                    public boolean hasNext() {
                        return this.backingIterator.hasNext();
                    }

                    public Entry<K, V> next() {
                        throw new VerifyError("bad dex opcode");
                    }
                };
            }

            ImmutableMap<K, V> map() {
                return ImmutableEnumMap.this;
            }
        };
    }

    ImmutableSet<K> createKeySet() {
        return new ImmutableSet<K>() {
            {
                throw new VerifyError("bad dex opcode");
            }

            public boolean contains(Object obj) {
                return ImmutableEnumMap.this.delegate.containsKey(obj);
            }

            boolean isPartialView() {
                return true;
            }

            public UnmodifiableIterator<K> iterator() {
                return Iterators.unmodifiableIterator(ImmutableEnumMap.this.delegate.keySet().iterator());
            }

            public int size() {
                return ImmutableEnumMap.this.size();
            }
        };
    }

    public V get(Object obj) {
        return this.delegate.get(obj);
    }

    boolean isPartialView() {
        return false;
    }

    public int size() {
        return this.delegate.size();
    }

    Object writeReplace() {
        return new EnumSerializedForm(this.delegate);
    }
}
