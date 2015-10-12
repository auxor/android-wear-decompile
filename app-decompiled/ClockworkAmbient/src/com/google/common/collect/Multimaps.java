package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.Map.Entry;

public final class Multimaps {

    static abstract class Entries<K, V> extends AbstractCollection<Entry<K, V>> {
        Entries() {
        }

        public void clear() {
            multimap().clear();
        }

        public boolean contains(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            throw new VerifyError("bad dex opcode");
        }

        abstract Multimap<K, V> multimap();

        public boolean remove(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            throw new VerifyError("bad dex opcode");
        }

        public int size() {
            return multimap().size();
        }
    }

    static boolean equalsImpl(Multimap<?, ?> multimap, Object obj) {
        if (obj == multimap) {
            return true;
        }
        if (!(obj instanceof Multimap)) {
            return false;
        }
        return multimap.asMap().equals(((Multimap) obj).asMap());
    }
}
