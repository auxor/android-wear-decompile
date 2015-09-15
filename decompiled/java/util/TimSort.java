package java.util;

import dalvik.bytecode.Opcodes;
import org.w3c.dom.traversal.NodeFilter;

class TimSort<T> {
    private static final boolean DEBUG = false;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private static final int MIN_GALLOP = 7;
    private static final int MIN_MERGE = 32;
    private final T[] a;
    private final Comparator<? super T> c;
    private int minGallop;
    private final int[] runBase;
    private final int[] runLen;
    private int stackSize;
    private T[] tmp;

    private TimSort(T[] a, Comparator<? super T> c) {
        this.minGallop = MIN_GALLOP;
        this.stackSize = 0;
        this.a = a;
        this.c = c;
        int len = a.length;
        this.tmp = (Object[]) new Object[(len < NodeFilter.SHOW_DOCUMENT_TYPE ? len >>> 1 : INITIAL_TMP_STORAGE_LENGTH)];
        int stackLen = len < Opcodes.OP_INVOKE_INTERFACE_RANGE ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40;
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    static <T> void sort(T[] a, Comparator<? super T> c) {
        sort(a, 0, a.length, c);
    }

    static <T> void sort(T[] a, int lo, int hi, Comparator<? super T> c) {
        if (c == null) {
            Arrays.sort((Object[]) a, lo, hi);
            return;
        }
        Arrays.checkStartAndEnd(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining < 2) {
            return;
        }
        if (nRemaining < MIN_MERGE) {
            binarySort(a, lo, hi, lo + countRunAndMakeAscending(a, lo, hi, c), c);
            return;
        }
        TimSort<T> ts = new TimSort(a, c);
        int minRun = minRunLength(nRemaining);
        do {
            int runLen = countRunAndMakeAscending(a, lo, hi, c);
            if (runLen < minRun) {
                int force = nRemaining <= minRun ? nRemaining : minRun;
                binarySort(a, lo, lo + force, lo + runLen, c);
                runLen = force;
            }
            ts.pushRun(lo, runLen);
            ts.mergeCollapse();
            lo += runLen;
            nRemaining -= runLen;
        } while (nRemaining != 0);
        ts.mergeForceCollapse();
    }

    private static <T> void binarySort(T[] a, int lo, int hi, int start, Comparator<? super T> c) {
        if (start == lo) {
            start++;
        }
        while (start < hi) {
            T pivot = a[start];
            int left = lo;
            int right = start;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (c.compare(pivot, a[mid]) < 0) {
                    right = mid;
                } else {
                    left = mid + 1;
                }
            }
            int n = start - left;
            switch (n) {
                case NodeFilter.SHOW_ELEMENT /*1*/:
                    break;
                case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                    a[left + 2] = a[left + 1];
                    break;
                default:
                    System.arraycopy((Object) a, left, (Object) a, left + 1, n);
                    continue;
            }
            a[left + 1] = a[left];
            a[left] = pivot;
            start++;
        }
    }

    private static <T> int countRunAndMakeAscending(T[] a, int lo, int hi, Comparator<? super T> c) {
        int runHi = lo + 1;
        if (runHi == hi) {
            return 1;
        }
        int runHi2 = runHi + 1;
        if (c.compare(a[runHi], a[lo]) < 0) {
            runHi = runHi2;
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) < 0) {
                runHi++;
            }
            reverseRange(a, lo, runHi);
        } else {
            runHi = runHi2;
            while (runHi < hi && c.compare(a[runHi], a[runHi - 1]) >= 0) {
                runHi++;
            }
        }
        return runHi - lo;
    }

    private static void reverseRange(Object[] a, int lo, int hi) {
        int hi2 = hi - 1;
        int i = lo;
        while (i < hi2) {
            Object t = a[i];
            lo = i + 1;
            a[i] = a[hi2];
            hi = hi2 - 1;
            a[hi2] = t;
            hi2 = hi;
            i = lo;
        }
    }

    private static int minRunLength(int n) {
        int r = 0;
        while (n >= MIN_MERGE) {
            r |= n & 1;
            n >>= 1;
        }
        return n + r;
    }

    private void pushRun(int runBase, int runLen) {
        this.runBase[this.stackSize] = runBase;
        this.runLen[this.stackSize] = runLen;
        this.stackSize++;
    }

    private void mergeCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] <= this.runLen[n] + this.runLen[n + 1]) {
                if (this.runLen[n - 1] < this.runLen[n + 1]) {
                    n--;
                }
                mergeAt(n);
            } else if (this.runLen[n] <= this.runLen[n + 1]) {
                mergeAt(n);
            } else {
                return;
            }
        }
    }

    private void mergeForceCollapse() {
        while (this.stackSize > 1) {
            int n = this.stackSize - 2;
            if (n > 0 && this.runLen[n - 1] < this.runLen[n + 1]) {
                n--;
            }
            mergeAt(n);
        }
    }

    private void mergeAt(int i) {
        int base1 = this.runBase[i];
        int len1 = this.runLen[i];
        int base2 = this.runBase[i + 1];
        int len2 = this.runLen[i + 1];
        this.runLen[i] = len1 + len2;
        if (i == this.stackSize - 3) {
            this.runBase[i + 1] = this.runBase[i + 2];
            this.runLen[i + 1] = this.runLen[i + 2];
        }
        this.stackSize--;
        int k = gallopRight(this.a[base2], this.a, base1, len1, 0, this.c);
        base1 += k;
        len1 -= k;
        if (len1 != 0) {
            len2 = gallopLeft(this.a[(base1 + len1) - 1], this.a, base2, len2, len2 - 1, this.c);
            if (len2 == 0) {
                return;
            }
            if (len1 <= len2) {
                mergeLo(base1, len1, base2, len2);
            } else {
                mergeHi(base1, len1, base2, len2);
            }
        }
    }

    private static <T> int gallopLeft(T key, T[] a, int base, int len, int hint, Comparator<? super T> c) {
        int lastOfs = 0;
        int ofs = 1;
        int maxOfs;
        if (c.compare(key, a[base + hint]) > 0) {
            maxOfs = len - hint;
            while (ofs < maxOfs && c.compare(key, a[(base + hint) + ofs]) > 0) {
                lastOfs = ofs;
                ofs = (ofs * 2) + 1;
                if (ofs <= 0) {
                    ofs = maxOfs;
                }
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            lastOfs += hint;
            ofs += hint;
        } else {
            maxOfs = hint + 1;
            while (ofs < maxOfs && c.compare(key, a[(base + hint) - ofs]) <= 0) {
                lastOfs = ofs;
                ofs = (ofs * 2) + 1;
                if (ofs <= 0) {
                    ofs = maxOfs;
                }
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        }
        lastOfs++;
        while (lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);
            if (c.compare(key, a[base + m]) > 0) {
                lastOfs = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static <T> int gallopRight(T key, T[] a, int base, int len, int hint, Comparator<? super T> c) {
        int ofs = 1;
        int lastOfs = 0;
        int maxOfs;
        if (c.compare(key, a[base + hint]) < 0) {
            maxOfs = hint + 1;
            while (ofs < maxOfs && c.compare(key, a[(base + hint) - ofs]) < 0) {
                lastOfs = ofs;
                ofs = (ofs * 2) + 1;
                if (ofs <= 0) {
                    ofs = maxOfs;
                }
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        } else {
            maxOfs = len - hint;
            while (ofs < maxOfs && c.compare(key, a[(base + hint) + ofs]) >= 0) {
                lastOfs = ofs;
                ofs = (ofs * 2) + 1;
                if (ofs <= 0) {
                    ofs = maxOfs;
                }
            }
            if (ofs > maxOfs) {
                ofs = maxOfs;
            }
            lastOfs += hint;
            ofs += hint;
        }
        lastOfs++;
        while (lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);
            if (c.compare(key, a[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs = m + 1;
            }
        }
        return ofs;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeLo(int r23, int r24, int r25, int r26) {
        /*
        r22 = this;
        r0 = r22;
        r10 = r0.a;
        r0 = r22;
        r1 = r24;
        r4 = r0.ensureCapacity(r1);
        r3 = 0;
        r0 = r23;
        r1 = r24;
        java.lang.System.arraycopy(r10, r0, r4, r3, r1);
        r5 = 0;
        r11 = r25;
        r19 = r23;
        r20 = r19 + 1;
        r18 = r11 + 1;
        r3 = r10[r11];
        r10[r19] = r3;
        r26 = r26 + -1;
        if (r26 != 0) goto L_0x0031;
    L_0x0025:
        r0 = r20;
        r1 = r24;
        java.lang.System.arraycopy(r4, r5, r10, r0, r1);
        r19 = r20;
        r11 = r18;
    L_0x0030:
        return;
    L_0x0031:
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x004a;
    L_0x0036:
        r0 = r18;
        r1 = r20;
        r2 = r26;
        java.lang.System.arraycopy(r10, r0, r10, r1, r2);
        r3 = r20 + r26;
        r6 = r4[r5];
        r10[r3] = r6;
        r19 = r20;
        r11 = r18;
        goto L_0x0030;
    L_0x004a:
        r0 = r22;
        r8 = r0.c;
        r0 = r22;
        r0 = r0.minGallop;
        r21 = r0;
        r19 = r20;
        r11 = r18;
    L_0x0058:
        r15 = 0;
        r16 = 0;
    L_0x005b:
        r3 = r10[r11];
        r6 = r4[r5];
        r3 = r8.compare(r3, r6);
        if (r3 >= 0) goto L_0x0098;
    L_0x0065:
        r20 = r19 + 1;
        r18 = r11 + 1;
        r3 = r10[r11];
        r10[r19] = r3;
        r16 = r16 + 1;
        r15 = 0;
        r26 = r26 + -1;
        if (r26 != 0) goto L_0x0157;
    L_0x0074:
        r19 = r20;
        r11 = r18;
    L_0x0078:
        r3 = 1;
        r0 = r21;
        if (r0 >= r3) goto L_0x007f;
    L_0x007d:
        r21 = 1;
    L_0x007f:
        r0 = r21;
        r1 = r22;
        r1.minGallop = r0;
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x0139;
    L_0x008a:
        r0 = r19;
        r1 = r26;
        java.lang.System.arraycopy(r10, r11, r10, r0, r1);
        r3 = r19 + r26;
        r6 = r4[r5];
        r10[r3] = r6;
        goto L_0x0030;
    L_0x0098:
        r20 = r19 + 1;
        r17 = r5 + 1;
        r3 = r4[r5];
        r10[r19] = r3;
        r15 = r15 + 1;
        r16 = 0;
        r24 = r24 + -1;
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x00b0;
    L_0x00ab:
        r19 = r20;
        r5 = r17;
        goto L_0x0078;
    L_0x00b0:
        r19 = r20;
        r5 = r17;
    L_0x00b4:
        r3 = r15 | r16;
        r0 = r21;
        if (r3 < r0) goto L_0x005b;
    L_0x00ba:
        r18 = r11;
    L_0x00bc:
        r3 = r10[r18];
        r7 = 0;
        r6 = r24;
        r15 = gallopRight(r3, r4, r5, r6, r7, r8);
        if (r15 == 0) goto L_0x00d9;
    L_0x00c7:
        r0 = r19;
        java.lang.System.arraycopy(r4, r5, r10, r0, r15);
        r19 = r19 + r15;
        r5 = r5 + r15;
        r24 = r24 - r15;
        r3 = 1;
        r0 = r24;
        if (r0 > r3) goto L_0x00d9;
    L_0x00d6:
        r11 = r18;
        goto L_0x0078;
    L_0x00d9:
        r20 = r19 + 1;
        r11 = r18 + 1;
        r3 = r10[r18];
        r10[r19] = r3;
        r26 = r26 + -1;
        if (r26 != 0) goto L_0x00e8;
    L_0x00e5:
        r19 = r20;
        goto L_0x0078;
    L_0x00e8:
        r9 = r4[r5];
        r13 = 0;
        r12 = r26;
        r14 = r8;
        r16 = gallopLeft(r9, r10, r11, r12, r13, r14);
        if (r16 == 0) goto L_0x0154;
    L_0x00f4:
        r0 = r20;
        r1 = r16;
        java.lang.System.arraycopy(r10, r11, r10, r0, r1);
        r19 = r20 + r16;
        r11 = r11 + r16;
        r26 = r26 - r16;
        if (r26 == 0) goto L_0x0078;
    L_0x0103:
        r20 = r19 + 1;
        r17 = r5 + 1;
        r3 = r4[r5];
        r10[r19] = r3;
        r24 = r24 + -1;
        r3 = 1;
        r0 = r24;
        if (r0 != r3) goto L_0x0118;
    L_0x0112:
        r19 = r20;
        r5 = r17;
        goto L_0x0078;
    L_0x0118:
        r21 = r21 + -1;
        r3 = 7;
        if (r15 < r3) goto L_0x0134;
    L_0x011d:
        r3 = 1;
        r6 = r3;
    L_0x011f:
        r3 = 7;
        r0 = r16;
        if (r0 < r3) goto L_0x0137;
    L_0x0124:
        r3 = 1;
    L_0x0125:
        r3 = r3 | r6;
        if (r3 != 0) goto L_0x014c;
    L_0x0128:
        if (r21 >= 0) goto L_0x012c;
    L_0x012a:
        r21 = 0;
    L_0x012c:
        r21 = r21 + 2;
        r19 = r20;
        r5 = r17;
        goto L_0x0058;
    L_0x0134:
        r3 = 0;
        r6 = r3;
        goto L_0x011f;
    L_0x0137:
        r3 = 0;
        goto L_0x0125;
    L_0x0139:
        if (r24 != 0) goto L_0x0143;
    L_0x013b:
        r3 = new java.lang.IllegalArgumentException;
        r6 = "Comparison method violates its general contract!";
        r3.<init>(r6);
        throw r3;
    L_0x0143:
        r0 = r19;
        r1 = r24;
        java.lang.System.arraycopy(r4, r5, r10, r0, r1);
        goto L_0x0030;
    L_0x014c:
        r19 = r20;
        r18 = r11;
        r5 = r17;
        goto L_0x00bc;
    L_0x0154:
        r19 = r20;
        goto L_0x0103;
    L_0x0157:
        r19 = r20;
        r11 = r18;
        goto L_0x00b4;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.TimSort.mergeLo(int, int, int, int):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeHi(int r24, int r25, int r26, int r27) {
        /*
        r23 = this;
        r0 = r23;
        r3 = r0.a;
        r0 = r23;
        r1 = r27;
        r9 = r0.ensureCapacity(r1);
        r2 = 0;
        r0 = r26;
        r1 = r27;
        java.lang.System.arraycopy(r3, r0, r9, r2, r1);
        r2 = r24 + r25;
        r16 = r2 + -1;
        r18 = r27 + -1;
        r2 = r26 + r27;
        r20 = r2 + -1;
        r21 = r20 + -1;
        r17 = r16 + -1;
        r2 = r3[r16];
        r3[r20] = r2;
        r25 = r25 + -1;
        if (r25 != 0) goto L_0x0039;
    L_0x002a:
        r2 = 0;
        r4 = r27 + -1;
        r4 = r21 - r4;
        r0 = r27;
        java.lang.System.arraycopy(r9, r2, r3, r4, r0);
        r20 = r21;
        r16 = r17;
    L_0x0038:
        return;
    L_0x0039:
        r2 = 1;
        r0 = r27;
        if (r0 != r2) goto L_0x0050;
    L_0x003e:
        r20 = r21 - r25;
        r16 = r17 - r25;
        r2 = r16 + 1;
        r4 = r20 + 1;
        r0 = r25;
        java.lang.System.arraycopy(r3, r2, r3, r4, r0);
        r2 = r9[r18];
        r3[r20] = r2;
        goto L_0x0038;
    L_0x0050:
        r0 = r23;
        r7 = r0.c;
        r0 = r23;
        r0 = r0.minGallop;
        r22 = r0;
        r20 = r21;
        r16 = r17;
    L_0x005e:
        r14 = 0;
        r15 = 0;
    L_0x0060:
        r2 = r9[r18];
        r4 = r3[r16];
        r2 = r7.compare(r2, r4);
        if (r2 >= 0) goto L_0x00a1;
    L_0x006a:
        r21 = r20 + -1;
        r17 = r16 + -1;
        r2 = r3[r16];
        r3[r20] = r2;
        r14 = r14 + 1;
        r15 = 0;
        r25 = r25 + -1;
        if (r25 != 0) goto L_0x0169;
    L_0x0079:
        r20 = r21;
        r16 = r17;
    L_0x007d:
        r2 = 1;
        r0 = r22;
        if (r0 >= r2) goto L_0x0084;
    L_0x0082:
        r22 = 1;
    L_0x0084:
        r0 = r22;
        r1 = r23;
        r1.minGallop = r0;
        r2 = 1;
        r0 = r27;
        if (r0 != r2) goto L_0x0148;
    L_0x008f:
        r20 = r20 - r25;
        r16 = r16 - r25;
        r2 = r16 + 1;
        r4 = r20 + 1;
        r0 = r25;
        java.lang.System.arraycopy(r3, r2, r3, r4, r0);
        r2 = r9[r18];
        r3[r20] = r2;
        goto L_0x0038;
    L_0x00a1:
        r21 = r20 + -1;
        r19 = r18 + -1;
        r2 = r9[r18];
        r3[r20] = r2;
        r15 = r15 + 1;
        r14 = 0;
        r27 = r27 + -1;
        r2 = 1;
        r0 = r27;
        if (r0 != r2) goto L_0x00b8;
    L_0x00b3:
        r20 = r21;
        r18 = r19;
        goto L_0x007d;
    L_0x00b8:
        r20 = r21;
        r18 = r19;
    L_0x00bc:
        r2 = r14 | r15;
        r0 = r22;
        if (r2 < r0) goto L_0x0060;
    L_0x00c2:
        r2 = r9[r18];
        r6 = r25 + -1;
        r4 = r24;
        r5 = r25;
        r2 = gallopRight(r2, r3, r4, r5, r6, r7);
        r14 = r25 - r2;
        if (r14 == 0) goto L_0x00e1;
    L_0x00d2:
        r20 = r20 - r14;
        r16 = r16 - r14;
        r25 = r25 - r14;
        r2 = r16 + 1;
        r4 = r20 + 1;
        java.lang.System.arraycopy(r3, r2, r3, r4, r14);
        if (r25 == 0) goto L_0x007d;
    L_0x00e1:
        r21 = r20 + -1;
        r19 = r18 + -1;
        r2 = r9[r18];
        r3[r20] = r2;
        r27 = r27 + -1;
        r2 = 1;
        r0 = r27;
        if (r0 != r2) goto L_0x00f5;
    L_0x00f0:
        r20 = r21;
        r18 = r19;
        goto L_0x007d;
    L_0x00f5:
        r8 = r3[r16];
        r10 = 0;
        r12 = r27 + -1;
        r11 = r27;
        r13 = r7;
        r2 = gallopLeft(r8, r9, r10, r11, r12, r13);
        r15 = r27 - r2;
        if (r15 == 0) goto L_0x0164;
    L_0x0105:
        r20 = r21 - r15;
        r18 = r19 - r15;
        r27 = r27 - r15;
        r2 = r18 + 1;
        r4 = r20 + 1;
        java.lang.System.arraycopy(r9, r2, r3, r4, r15);
        r2 = 1;
        r0 = r27;
        if (r0 <= r2) goto L_0x007d;
    L_0x0117:
        r21 = r20 + -1;
        r17 = r16 + -1;
        r2 = r3[r16];
        r3[r20] = r2;
        r25 = r25 + -1;
        if (r25 != 0) goto L_0x0129;
    L_0x0123:
        r20 = r21;
        r16 = r17;
        goto L_0x007d;
    L_0x0129:
        r22 = r22 + -1;
        r2 = 7;
        if (r14 < r2) goto L_0x0143;
    L_0x012e:
        r2 = 1;
        r4 = r2;
    L_0x0130:
        r2 = 7;
        if (r15 < r2) goto L_0x0146;
    L_0x0133:
        r2 = 1;
    L_0x0134:
        r2 = r2 | r4;
        if (r2 != 0) goto L_0x015e;
    L_0x0137:
        if (r22 >= 0) goto L_0x013b;
    L_0x0139:
        r22 = 0;
    L_0x013b:
        r22 = r22 + 2;
        r20 = r21;
        r16 = r17;
        goto L_0x005e;
    L_0x0143:
        r2 = 0;
        r4 = r2;
        goto L_0x0130;
    L_0x0146:
        r2 = 0;
        goto L_0x0134;
    L_0x0148:
        if (r27 != 0) goto L_0x0152;
    L_0x014a:
        r2 = new java.lang.IllegalArgumentException;
        r4 = "Comparison method violates its general contract!";
        r2.<init>(r4);
        throw r2;
    L_0x0152:
        r2 = 0;
        r4 = r27 + -1;
        r4 = r20 - r4;
        r0 = r27;
        java.lang.System.arraycopy(r9, r2, r3, r4, r0);
        goto L_0x0038;
    L_0x015e:
        r20 = r21;
        r16 = r17;
        goto L_0x00c2;
    L_0x0164:
        r20 = r21;
        r18 = r19;
        goto L_0x0117;
    L_0x0169:
        r20 = r21;
        r16 = r17;
        goto L_0x00bc;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.TimSort.mergeHi(int, int, int, int):void");
    }

    private T[] ensureCapacity(int minCapacity) {
        if (this.tmp.length < minCapacity) {
            int newSize = minCapacity;
            newSize |= newSize >> 1;
            newSize |= newSize >> 2;
            newSize |= newSize >> 4;
            newSize |= newSize >> 8;
            newSize = (newSize | (newSize >> 16)) + 1;
            if (newSize < 0) {
                newSize = minCapacity;
            } else {
                newSize = Math.min(newSize, this.a.length >>> 1);
            }
            this.tmp = (Object[]) new Object[newSize];
        }
        return this.tmp;
    }
}
