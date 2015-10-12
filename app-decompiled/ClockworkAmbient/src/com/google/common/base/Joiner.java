package com.google.common.base;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Joiner {
    private final String separator;

    /* renamed from: com.google.common.base.Joiner.1 */
    class AnonymousClass1 extends Joiner {
        final /* synthetic */ String val$nullText;

        AnonymousClass1(Joiner joiner, String str) {
            this.val$nullText = str;
            super(null);
            throw new VerifyError("bad dex opcode");
        }

        CharSequence toString(Object obj) {
            return obj == null ? this.val$nullText : Joiner.this.toString(obj);
        }

        public Joiner useForNull(String str) {
            throw new UnsupportedOperationException("already specified useForNull");
        }
    }

    public static final class MapJoiner {
        private final Joiner joiner;
        private final String keyValueSeparator;

        private MapJoiner(Joiner joiner, String str) {
            throw new VerifyError("bad dex opcode");
        }

        public <A extends Appendable> A appendTo(A a, Iterator<? extends Entry<?, ?>> it) throws IOException {
            Preconditions.checkNotNull(a);
            if (!it.hasNext()) {
                return a;
            }
            Entry entry = (Entry) it.next();
            throw new VerifyError("bad dex opcode");
        }

        public StringBuilder appendTo(StringBuilder stringBuilder, Iterable<? extends Entry<?, ?>> iterable) {
            return appendTo(stringBuilder, iterable.iterator());
        }

        public StringBuilder appendTo(StringBuilder stringBuilder, Iterator<? extends Entry<?, ?>> it) {
            try {
                appendTo((Appendable) stringBuilder, (Iterator) it);
                return stringBuilder;
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }

        public StringBuilder appendTo(StringBuilder stringBuilder, Map<?, ?> map) {
            return appendTo(stringBuilder, map.entrySet());
        }
    }

    private Joiner(Joiner joiner) {
        this.separator = joiner.separator;
        throw new VerifyError("bad dex opcode");
    }

    private Joiner(String str) {
        String str2 = (String) Preconditions.checkNotNull(str);
        throw new VerifyError("bad dex opcode");
    }

    public static Joiner on(char c) {
        return new Joiner(String.valueOf(c));
    }

    public static Joiner on(String str) {
        return new Joiner(str);
    }

    public <A extends Appendable> A appendTo(A a, Iterator<?> it) throws IOException {
        Preconditions.checkNotNull(a);
        if (it.hasNext()) {
            a.append(toString(it.next()));
            while (it.hasNext()) {
                a.append(this.separator);
                a.append(toString(it.next()));
            }
        }
        return a;
    }

    public final StringBuilder appendTo(StringBuilder stringBuilder, Iterable<?> iterable) {
        return appendTo(stringBuilder, iterable.iterator());
    }

    public final StringBuilder appendTo(StringBuilder stringBuilder, Iterator<?> it) {
        try {
            appendTo((Appendable) stringBuilder, (Iterator) it);
            return stringBuilder;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    CharSequence toString(Object obj) {
        Preconditions.checkNotNull(obj);
        if (obj instanceof CharSequence) {
            return (CharSequence) obj;
        }
        throw new VerifyError("bad dex opcode");
    }

    public Joiner useForNull(String str) {
        Preconditions.checkNotNull(str);
        return new AnonymousClass1(this, str);
    }

    public MapJoiner withKeyValueSeparator(String str) {
        return new MapJoiner(str, null);
    }
}
