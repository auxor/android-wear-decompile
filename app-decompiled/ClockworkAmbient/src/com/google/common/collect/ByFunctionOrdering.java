package com.google.common.collect;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.Serializable;

final class ByFunctionOrdering<F, T> extends Ordering<F> implements Serializable {
    private static final long serialVersionUID = 0;
    final Function<F, ? extends T> function;
    final Ordering<T> ordering;

    ByFunctionOrdering(Function<F, ? extends T> function, Ordering<T> ordering) {
        Function function2 = (Function) Preconditions.checkNotNull(function);
        throw new VerifyError("bad dex opcode");
    }

    public int compare(F f, F f2) {
        return this.ordering.compare(this.function.apply(f), this.function.apply(f2));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ByFunctionOrdering)) {
            return false;
        }
        throw new VerifyError("bad dex opcode");
    }

    public int hashCode() {
        Object[] objArr = new Object[2];
        throw new VerifyError("bad dex opcode");
    }

    public String toString() {
        return this.ordering + ".onResultOf(" + this.function + ")";
    }
}
