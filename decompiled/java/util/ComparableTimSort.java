package java.util;

import dalvik.bytecode.Opcodes;
import org.w3c.dom.traversal.NodeFilter;

class ComparableTimSort {
    private static final boolean DEBUG = false;
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
    private static final int MIN_GALLOP = 7;
    private static final int MIN_MERGE = 32;
    private final Object[] a;
    private int minGallop;
    private final int[] runBase;
    private final int[] runLen;
    private int stackSize;
    private Object[] tmp;

    private ComparableTimSort(Object[] a) {
        this.minGallop = MIN_GALLOP;
        this.stackSize = 0;
        this.a = a;
        int len = a.length;
        this.tmp = new Object[(len < NodeFilter.SHOW_DOCUMENT_TYPE ? len >>> 1 : INITIAL_TMP_STORAGE_LENGTH)];
        int stackLen = len < Opcodes.OP_INVOKE_INTERFACE_RANGE ? 5 : len < 1542 ? 10 : len < 119151 ? 19 : 40;
        this.runBase = new int[stackLen];
        this.runLen = new int[stackLen];
    }

    static void sort(Object[] a) {
        sort(a, 0, a.length);
    }

    static void sort(Object[] a, int lo, int hi) {
        Arrays.checkStartAndEnd(a.length, lo, hi);
        int nRemaining = hi - lo;
        if (nRemaining >= 2) {
            if (nRemaining < MIN_MERGE) {
                binarySort(a, lo, hi, lo + countRunAndMakeAscending(a, lo, hi));
                return;
            }
            ComparableTimSort ts = new ComparableTimSort(a);
            int minRun = minRunLength(nRemaining);
            do {
                int runLen = countRunAndMakeAscending(a, lo, hi);
                if (runLen < minRun) {
                    int force = nRemaining <= minRun ? nRemaining : minRun;
                    binarySort(a, lo, lo + force, lo + runLen);
                    runLen = force;
                }
                ts.pushRun(lo, runLen);
                ts.mergeCollapse();
                lo += runLen;
                nRemaining -= runLen;
            } while (nRemaining != 0);
            ts.mergeForceCollapse();
        }
    }

    private static void binarySort(Object[] a, int lo, int hi, int start) {
        if (start == lo) {
            start++;
        }
        while (start < hi) {
            Comparable<Object> pivot = a[start];
            int left = lo;
            int right = start;
            while (left < right) {
                int mid = (left + right) >>> 1;
                if (pivot.compareTo(a[mid]) < 0) {
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

    private static int countRunAndMakeAscending(Object[] a, int lo, int hi) {
        int runHi = lo + 1;
        if (runHi == hi) {
            return 1;
        }
        int runHi2 = runHi + 1;
        if (((Comparable) a[runHi]).compareTo(a[lo]) < 0) {
            runHi = runHi2;
            while (runHi < hi && ((Comparable) a[runHi]).compareTo(a[runHi - 1]) < 0) {
                runHi++;
            }
            reverseRange(a, lo, runHi);
        } else {
            runHi = runHi2;
            while (runHi < hi && ((Comparable) a[runHi]).compareTo(a[runHi - 1]) >= 0) {
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
        int k = gallopRight((Comparable) this.a[base2], this.a, base1, len1, 0);
        base1 += k;
        len1 -= k;
        if (len1 != 0) {
            len2 = gallopLeft((Comparable) this.a[(base1 + len1) - 1], this.a, base2, len2, len2 - 1);
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

    private static int gallopLeft(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int lastOfs = 0;
        int ofs = 1;
        int maxOfs;
        if (key.compareTo(a[base + hint]) > 0) {
            maxOfs = len - hint;
            while (ofs < maxOfs && key.compareTo(a[(base + hint) + ofs]) > 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
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
            while (ofs < maxOfs && key.compareTo(a[(base + hint) - ofs]) <= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
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
            if (key.compareTo(a[base + m]) > 0) {
                lastOfs = m + 1;
            } else {
                ofs = m;
            }
        }
        return ofs;
    }

    private static int gallopRight(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int ofs = 1;
        int lastOfs = 0;
        int maxOfs;
        if (key.compareTo(a[base + hint]) < 0) {
            maxOfs = hint + 1;
            while (ofs < maxOfs && key.compareTo(a[(base + hint) - ofs]) < 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
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
            while (ofs < maxOfs && key.compareTo(a[(base + hint) + ofs]) >= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
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
            if (key.compareTo(a[base + m]) < 0) {
                ofs = m;
            } else {
                lastOfs = m + 1;
            }
        }
        return ofs;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeLo(int r15, int r16, int r17, int r18) {
        /*
        r14 = this;
        r1 = r14.a;
        r0 = r16;
        r11 = r14.ensureCapacity(r0);
        r12 = 0;
        r0 = r16;
        java.lang.System.arraycopy(r1, r15, r11, r12, r0);
        r4 = 0;
        r6 = r17;
        r8 = r15;
        r9 = r8 + 1;
        r7 = r6 + 1;
        r12 = r1[r6];
        r1[r8] = r12;
        r18 = r18 + -1;
        if (r18 != 0) goto L_0x0026;
    L_0x001e:
        r0 = r16;
        java.lang.System.arraycopy(r11, r4, r1, r9, r0);
        r8 = r9;
        r6 = r7;
    L_0x0025:
        return;
    L_0x0026:
        r12 = 1;
        r0 = r16;
        if (r0 != r12) goto L_0x0039;
    L_0x002b:
        r0 = r18;
        java.lang.System.arraycopy(r1, r7, r1, r9, r0);
        r12 = r9 + r18;
        r13 = r11[r4];
        r1[r12] = r13;
        r8 = r9;
        r6 = r7;
        goto L_0x0025;
    L_0x0039:
        r10 = r14.minGallop;
        r8 = r9;
        r6 = r7;
    L_0x003d:
        r2 = 0;
        r3 = 0;
    L_0x003f:
        r12 = r1[r6];
        r12 = (java.lang.Comparable) r12;
        r13 = r11[r4];
        r12 = r12.compareTo(r13);
        if (r12 >= 0) goto L_0x0073;
    L_0x004b:
        r9 = r8 + 1;
        r7 = r6 + 1;
        r12 = r1[r6];
        r1[r8] = r12;
        r3 = r3 + 1;
        r2 = 0;
        r18 = r18 + -1;
        if (r18 != 0) goto L_0x0115;
    L_0x005a:
        r8 = r9;
        r6 = r7;
    L_0x005c:
        r12 = 1;
        if (r10 >= r12) goto L_0x0060;
    L_0x005f:
        r10 = 1;
    L_0x0060:
        r14.minGallop = r10;
        r12 = 1;
        r0 = r16;
        if (r0 != r12) goto L_0x00fd;
    L_0x0067:
        r0 = r18;
        java.lang.System.arraycopy(r1, r6, r1, r8, r0);
        r12 = r8 + r18;
        r13 = r11[r4];
        r1[r12] = r13;
        goto L_0x0025;
    L_0x0073:
        r9 = r8 + 1;
        r5 = r4 + 1;
        r12 = r11[r4];
        r1[r8] = r12;
        r2 = r2 + 1;
        r3 = 0;
        r16 = r16 + -1;
        r12 = 1;
        r0 = r16;
        if (r0 != r12) goto L_0x0088;
    L_0x0085:
        r8 = r9;
        r4 = r5;
        goto L_0x005c;
    L_0x0088:
        r8 = r9;
        r4 = r5;
    L_0x008a:
        r12 = r2 | r3;
        if (r12 < r10) goto L_0x003f;
    L_0x008e:
        r12 = r1[r6];
        r12 = (java.lang.Comparable) r12;
        r13 = 0;
        r0 = r16;
        r2 = gallopRight(r12, r11, r4, r0, r13);
        if (r2 == 0) goto L_0x00a7;
    L_0x009b:
        java.lang.System.arraycopy(r11, r4, r1, r8, r2);
        r8 = r8 + r2;
        r4 = r4 + r2;
        r16 = r16 - r2;
        r12 = 1;
        r0 = r16;
        if (r0 <= r12) goto L_0x005c;
    L_0x00a7:
        r9 = r8 + 1;
        r7 = r6 + 1;
        r12 = r1[r6];
        r1[r8] = r12;
        r18 = r18 + -1;
        if (r18 != 0) goto L_0x00b6;
    L_0x00b3:
        r8 = r9;
        r6 = r7;
        goto L_0x005c;
    L_0x00b6:
        r12 = r11[r4];
        r12 = (java.lang.Comparable) r12;
        r13 = 0;
        r0 = r18;
        r3 = gallopLeft(r12, r1, r7, r0, r13);
        if (r3 == 0) goto L_0x0112;
    L_0x00c3:
        java.lang.System.arraycopy(r1, r7, r1, r9, r3);
        r8 = r9 + r3;
        r6 = r7 + r3;
        r18 = r18 - r3;
        if (r18 == 0) goto L_0x005c;
    L_0x00ce:
        r9 = r8 + 1;
        r5 = r4 + 1;
        r12 = r11[r4];
        r1[r8] = r12;
        r16 = r16 + -1;
        r12 = 1;
        r0 = r16;
        if (r0 != r12) goto L_0x00e1;
    L_0x00dd:
        r8 = r9;
        r4 = r5;
        goto L_0x005c;
    L_0x00e1:
        r10 = r10 + -1;
        r12 = 7;
        if (r2 < r12) goto L_0x00f8;
    L_0x00e6:
        r12 = 1;
        r13 = r12;
    L_0x00e8:
        r12 = 7;
        if (r3 < r12) goto L_0x00fb;
    L_0x00eb:
        r12 = 1;
    L_0x00ec:
        r12 = r12 | r13;
        if (r12 != 0) goto L_0x010e;
    L_0x00ef:
        if (r10 >= 0) goto L_0x00f2;
    L_0x00f1:
        r10 = 0;
    L_0x00f2:
        r10 = r10 + 2;
        r8 = r9;
        r4 = r5;
        goto L_0x003d;
    L_0x00f8:
        r12 = 0;
        r13 = r12;
        goto L_0x00e8;
    L_0x00fb:
        r12 = 0;
        goto L_0x00ec;
    L_0x00fd:
        if (r16 != 0) goto L_0x0107;
    L_0x00ff:
        r12 = new java.lang.IllegalArgumentException;
        r13 = "Comparison method violates its general contract!";
        r12.<init>(r13);
        throw r12;
    L_0x0107:
        r0 = r16;
        java.lang.System.arraycopy(r11, r4, r1, r8, r0);
        goto L_0x0025;
    L_0x010e:
        r8 = r9;
        r4 = r5;
        goto L_0x008e;
    L_0x0112:
        r8 = r9;
        r6 = r7;
        goto L_0x00ce;
    L_0x0115:
        r8 = r9;
        r6 = r7;
        goto L_0x008a;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.ComparableTimSort.mergeLo(int, int, int, int):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void mergeHi(int r17, int r18, int r19, int r20) {
        /*
        r16 = this;
        r0 = r16;
        r2 = r0.a;
        r0 = r16;
        r1 = r20;
        r12 = r0.ensureCapacity(r1);
        r13 = 0;
        r0 = r19;
        r1 = r20;
        java.lang.System.arraycopy(r2, r0, r12, r13, r1);
        r13 = r17 + r18;
        r5 = r13 + -1;
        r7 = r20 + -1;
        r13 = r19 + r20;
        r9 = r13 + -1;
        r10 = r9 + -1;
        r6 = r5 + -1;
        r13 = r2[r5];
        r2[r9] = r13;
        r18 = r18 + -1;
        if (r18 != 0) goto L_0x0037;
    L_0x002a:
        r13 = 0;
        r14 = r20 + -1;
        r14 = r10 - r14;
        r0 = r20;
        java.lang.System.arraycopy(r12, r13, r2, r14, r0);
        r9 = r10;
        r5 = r6;
    L_0x0036:
        return;
    L_0x0037:
        r13 = 1;
        r0 = r20;
        if (r0 != r13) goto L_0x004e;
    L_0x003c:
        r9 = r10 - r18;
        r5 = r6 - r18;
        r13 = r5 + 1;
        r14 = r9 + 1;
        r0 = r18;
        java.lang.System.arraycopy(r2, r13, r2, r14, r0);
        r13 = r12[r7];
        r2[r9] = r13;
        goto L_0x0036;
    L_0x004e:
        r0 = r16;
        r11 = r0.minGallop;
        r9 = r10;
        r5 = r6;
    L_0x0054:
        r3 = 0;
        r4 = 0;
    L_0x0056:
        r13 = r12[r7];
        r13 = (java.lang.Comparable) r13;
        r14 = r2[r5];
        r13 = r13.compareTo(r14);
        if (r13 >= 0) goto L_0x0092;
    L_0x0062:
        r10 = r9 + -1;
        r6 = r5 + -1;
        r13 = r2[r5];
        r2[r9] = r13;
        r3 = r3 + 1;
        r4 = 0;
        r18 = r18 + -1;
        if (r18 != 0) goto L_0x014a;
    L_0x0071:
        r9 = r10;
        r5 = r6;
    L_0x0073:
        r13 = 1;
        if (r11 >= r13) goto L_0x0077;
    L_0x0076:
        r11 = 1;
    L_0x0077:
        r0 = r16;
        r0.minGallop = r11;
        r13 = 1;
        r0 = r20;
        if (r0 != r13) goto L_0x012d;
    L_0x0080:
        r9 = r9 - r18;
        r5 = r5 - r18;
        r13 = r5 + 1;
        r14 = r9 + 1;
        r0 = r18;
        java.lang.System.arraycopy(r2, r13, r2, r14, r0);
        r13 = r12[r7];
        r2[r9] = r13;
        goto L_0x0036;
    L_0x0092:
        r10 = r9 + -1;
        r8 = r7 + -1;
        r13 = r12[r7];
        r2[r9] = r13;
        r4 = r4 + 1;
        r3 = 0;
        r20 = r20 + -1;
        r13 = 1;
        r0 = r20;
        if (r0 != r13) goto L_0x00a7;
    L_0x00a4:
        r9 = r10;
        r7 = r8;
        goto L_0x0073;
    L_0x00a7:
        r9 = r10;
        r7 = r8;
    L_0x00a9:
        r13 = r3 | r4;
        if (r13 < r11) goto L_0x0056;
    L_0x00ad:
        r13 = r12[r7];
        r13 = (java.lang.Comparable) r13;
        r14 = r18 + -1;
        r0 = r17;
        r1 = r18;
        r13 = gallopRight(r13, r2, r0, r1, r14);
        r3 = r18 - r13;
        if (r3 == 0) goto L_0x00cc;
    L_0x00bf:
        r9 = r9 - r3;
        r5 = r5 - r3;
        r18 = r18 - r3;
        r13 = r5 + 1;
        r14 = r9 + 1;
        java.lang.System.arraycopy(r2, r13, r2, r14, r3);
        if (r18 == 0) goto L_0x0073;
    L_0x00cc:
        r10 = r9 + -1;
        r8 = r7 + -1;
        r13 = r12[r7];
        r2[r9] = r13;
        r20 = r20 + -1;
        r13 = 1;
        r0 = r20;
        if (r0 != r13) goto L_0x00de;
    L_0x00db:
        r9 = r10;
        r7 = r8;
        goto L_0x0073;
    L_0x00de:
        r13 = r2[r5];
        r13 = (java.lang.Comparable) r13;
        r14 = 0;
        r15 = r20 + -1;
        r0 = r20;
        r13 = gallopLeft(r13, r12, r14, r0, r15);
        r4 = r20 - r13;
        if (r4 == 0) goto L_0x0147;
    L_0x00ef:
        r9 = r10 - r4;
        r7 = r8 - r4;
        r20 = r20 - r4;
        r13 = r7 + 1;
        r14 = r9 + 1;
        java.lang.System.arraycopy(r12, r13, r2, r14, r4);
        r13 = 1;
        r0 = r20;
        if (r0 <= r13) goto L_0x0073;
    L_0x0101:
        r10 = r9 + -1;
        r6 = r5 + -1;
        r13 = r2[r5];
        r2[r9] = r13;
        r18 = r18 + -1;
        if (r18 != 0) goto L_0x0111;
    L_0x010d:
        r9 = r10;
        r5 = r6;
        goto L_0x0073;
    L_0x0111:
        r11 = r11 + -1;
        r13 = 7;
        if (r3 < r13) goto L_0x0128;
    L_0x0116:
        r13 = 1;
        r14 = r13;
    L_0x0118:
        r13 = 7;
        if (r4 < r13) goto L_0x012b;
    L_0x011b:
        r13 = 1;
    L_0x011c:
        r13 = r13 | r14;
        if (r13 != 0) goto L_0x0143;
    L_0x011f:
        if (r11 >= 0) goto L_0x0122;
    L_0x0121:
        r11 = 0;
    L_0x0122:
        r11 = r11 + 2;
        r9 = r10;
        r5 = r6;
        goto L_0x0054;
    L_0x0128:
        r13 = 0;
        r14 = r13;
        goto L_0x0118;
    L_0x012b:
        r13 = 0;
        goto L_0x011c;
    L_0x012d:
        if (r20 != 0) goto L_0x0137;
    L_0x012f:
        r13 = new java.lang.IllegalArgumentException;
        r14 = "Comparison method violates its general contract!";
        r13.<init>(r14);
        throw r13;
    L_0x0137:
        r13 = 0;
        r14 = r20 + -1;
        r14 = r9 - r14;
        r0 = r20;
        java.lang.System.arraycopy(r12, r13, r2, r14, r0);
        goto L_0x0036;
    L_0x0143:
        r9 = r10;
        r5 = r6;
        goto L_0x00ad;
    L_0x0147:
        r9 = r10;
        r7 = r8;
        goto L_0x0101;
    L_0x014a:
        r9 = r10;
        r5 = r6;
        goto L_0x00a9;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.ComparableTimSort.mergeHi(int, int, int, int):void");
    }

    private Object[] ensureCapacity(int minCapacity) {
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
            this.tmp = new Object[newSize];
        }
        return this.tmp;
    }
}
