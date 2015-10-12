package com.google.common.base;

import java.util.Iterator;

public final class Splitter {
    private final int limit;
    private final boolean omitEmptyStrings;
    private final Strategy strategy;
    private final CharMatcher trimmer;

    private static abstract class SplittingIterator extends AbstractIterator<String> {
        int limit;
        int offset;
        final boolean omitEmptyStrings;
        final CharSequence toSplit;
        final CharMatcher trimmer;

        protected SplittingIterator(Splitter splitter, CharSequence charSequence) {
            this.offset = 0;
            this.trimmer = splitter.trimmer;
            this.omitEmptyStrings = splitter.omitEmptyStrings;
            this.limit = splitter.limit;
            this.toSplit = charSequence;
            throw new VerifyError("bad dex opcode");
        }

        protected String computeNext() {
            throw new VerifyError("bad dex opcode");
        }

        abstract int separatorEnd(int i);

        abstract int separatorStart(int i);
    }

    private interface Strategy {
        Iterator<String> iterator(Splitter splitter, CharSequence charSequence);
    }

    /* renamed from: com.google.common.base.Splitter.2 */
    static final class AnonymousClass2 implements Strategy {
        final /* synthetic */ String val$separator;

        /* renamed from: com.google.common.base.Splitter.2.1 */
        class AnonymousClass1 extends SplittingIterator {
            AnonymousClass1(Splitter splitter, CharSequence charSequence) {
                super(splitter, charSequence);
                throw new VerifyError("bad dex opcode");
            }

            public int separatorEnd(int i) {
                return AnonymousClass2.this.val$separator.length() + i;
            }

            public int separatorStart(int i) {
                int length = AnonymousClass2.this.val$separator.length();
                int length2 = this.toSplit.length();
                int i2 = i;
                while (i2 <= length2 - length) {
                    int i3 = 0;
                    while (i3 < length) {
                        if (this.toSplit.charAt(i3 + i2) != AnonymousClass2.this.val$separator.charAt(i3)) {
                            i2++;
                        } else {
                            i3++;
                        }
                    }
                    return i2;
                }
                return -1;
            }
        }

        AnonymousClass2(String str) {
            this.val$separator = str;
            throw new VerifyError("bad dex opcode");
        }

        public SplittingIterator iterator(Splitter splitter, CharSequence charSequence) {
            return new AnonymousClass1(splitter, charSequence);
        }
    }

    /* renamed from: com.google.common.base.Splitter.5 */
    class AnonymousClass5 implements Iterable<String> {
        final /* synthetic */ CharSequence val$sequence;

        AnonymousClass5(CharSequence charSequence) {
            this.val$sequence = charSequence;
            throw new VerifyError("bad dex opcode");
        }

        public Iterator<String> iterator() {
            return Splitter.this.splittingIterator(this.val$sequence);
        }

        public String toString() {
            return Joiner.on(", ").appendTo(new StringBuilder().append('['), (Iterable) this).append(']').toString();
        }
    }

    private Splitter(Strategy strategy) {
        this(strategy, false, CharMatcher.NONE, Integer.MAX_VALUE);
        throw new VerifyError("bad dex opcode");
    }

    private Splitter(Strategy strategy, boolean z, CharMatcher charMatcher, int i) {
        this.strategy = strategy;
        this.omitEmptyStrings = z;
        this.trimmer = charMatcher;
        this.limit = i;
        throw new VerifyError("bad dex opcode");
    }

    public static Splitter on(String str) {
        Preconditions.checkArgument(str.length() != 0, "The separator may not be the empty string.");
        return new Splitter(new AnonymousClass2(str));
    }

    private Iterator<String> splittingIterator(CharSequence charSequence) {
        return this.strategy.iterator(this, charSequence);
    }

    public Splitter omitEmptyStrings() {
        return new Splitter(this.strategy, true, this.trimmer, this.limit);
    }

    public Iterable<String> split(CharSequence charSequence) {
        Preconditions.checkNotNull(charSequence);
        return new AnonymousClass5(charSequence);
    }
}
