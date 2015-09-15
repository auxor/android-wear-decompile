package java.util;

final class DualPivotQuicksort {
    private static final int COUNTING_SORT_THRESHOLD_FOR_BYTE = 128;
    private static final int COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR = 32768;
    private static final int INSERTION_SORT_THRESHOLD = 32;
    private static final int NUM_BYTE_VALUES = 256;
    private static final int NUM_CHAR_VALUES = 65536;
    private static final int NUM_SHORT_VALUES = 65536;

    private DualPivotQuicksort() {
    }

    public static void sort(int[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(int[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(int[] a, int left, int right) {
        if ((right - left) + 1 < INSERTION_SORT_THRESHOLD) {
            for (int i = left + 1; i <= right; i++) {
                int ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(int[] a, int left, int right) {
        int k;
        int less;
        int ak;
        int great;
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        int ae1 = a[e1];
        int ae2 = a[e2];
        int ae3 = a[e3];
        int ae4 = a[e4];
        int ae5 = a[e5];
        if (ae1 > ae2) {
            int t = ae1;
            ae1 = ae2;
            ae2 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        if (ae1 > ae3) {
            t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae1 > ae4) {
            t = ae1;
            ae1 = ae4;
            ae4 = t;
        }
        if (ae3 > ae4) {
            t = ae3;
            ae3 = ae4;
            ae4 = t;
        }
        if (ae2 > ae5) {
            t = ae2;
            ae2 = ae5;
            ae5 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        int pivot1 = ae2;
        a[e2] = a[left];
        int pivot2 = ae4;
        a[e4] = a[right];
        int less2 = left + 1;
        int great2 = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            k = less2;
            less = less2;
            loop0:
            while (k <= great2) {
                ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else if (ak > pivot2) {
                    while (a[great2] > pivot2) {
                        great = great2 - 1;
                        if (great2 == k) {
                            great2 = great;
                            less2 = less;
                            break loop0;
                        }
                        great2 = great;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                } else {
                    less2 = less;
                }
                k++;
                less = less2;
            }
            less2 = less;
        } else {
            k = less2;
            less = less2;
            while (k <= great2) {
                ak = a[k];
                if (ak == pivot1) {
                    less2 = less;
                } else if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else {
                    while (a[great2] > pivot1) {
                        great2--;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = pivot1;
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                }
                k++;
                less = less2;
            }
            less2 = less;
        }
        a[left] = a[less2 - 1];
        a[less2 - 1] = pivot1;
        a[right] = a[great2 + 1];
        a[great2 + 1] = pivot2;
        doSort(a, left, less2 - 2);
        doSort(a, great2 + 2, right);
        if (pivotsDiffer) {
            if (less2 < e1 && great2 > e5) {
                while (a[less2] == pivot1) {
                    less2++;
                }
                while (a[great2] == pivot2) {
                    great2--;
                }
                k = less2;
                less = less2;
                loop6:
                while (k <= great2) {
                    ak = a[k];
                    if (ak == pivot2) {
                        while (a[great2] == pivot2) {
                            great = great2 - 1;
                            if (great2 == k) {
                                great2 = great;
                                less2 = less;
                                break loop6;
                            }
                            great2 = great;
                        }
                        if (a[great2] == pivot1) {
                            a[k] = a[less];
                            less2 = less + 1;
                            a[less] = pivot1;
                        } else {
                            a[k] = a[great2];
                            less2 = less;
                        }
                        great = great2 - 1;
                        a[great2] = pivot2;
                        great2 = great;
                    } else if (ak == pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = pivot1;
                    } else {
                        less2 = less;
                    }
                    k++;
                    less = less2;
                }
                less2 = less;
            }
            doSort(a, less2, great2);
        }
    }

    public static void sort(long[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(long[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(long[] a, int left, int right) {
        if ((right - left) + 1 < INSERTION_SORT_THRESHOLD) {
            for (int i = left + 1; i <= right; i++) {
                long ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(long[] a, int left, int right) {
        int k;
        int less;
        long ak;
        int great;
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        long ae1 = a[e1];
        long ae2 = a[e2];
        long ae3 = a[e3];
        long ae4 = a[e4];
        long ae5 = a[e5];
        if (ae1 > ae2) {
            long t = ae1;
            ae1 = ae2;
            ae2 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        if (ae1 > ae3) {
            t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae1 > ae4) {
            t = ae1;
            ae1 = ae4;
            ae4 = t;
        }
        if (ae3 > ae4) {
            t = ae3;
            ae3 = ae4;
            ae4 = t;
        }
        if (ae2 > ae5) {
            t = ae2;
            ae2 = ae5;
            ae5 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        long pivot1 = ae2;
        a[e2] = a[left];
        long pivot2 = ae4;
        a[e4] = a[right];
        int less2 = left + 1;
        int great2 = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            k = less2;
            less = less2;
            loop0:
            while (k <= great2) {
                ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else if (ak > pivot2) {
                    while (a[great2] > pivot2) {
                        great = great2 - 1;
                        if (great2 == k) {
                            great2 = great;
                            less2 = less;
                            break loop0;
                        }
                        great2 = great;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                } else {
                    less2 = less;
                }
                k++;
                less = less2;
            }
            less2 = less;
        } else {
            k = less2;
            less = less2;
            while (k <= great2) {
                ak = a[k];
                if (ak == pivot1) {
                    less2 = less;
                } else if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else {
                    while (a[great2] > pivot1) {
                        great2--;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = pivot1;
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                }
                k++;
                less = less2;
            }
            less2 = less;
        }
        a[left] = a[less2 - 1];
        a[less2 - 1] = pivot1;
        a[right] = a[great2 + 1];
        a[great2 + 1] = pivot2;
        doSort(a, left, less2 - 2);
        doSort(a, great2 + 2, right);
        if (pivotsDiffer) {
            if (less2 < e1 && great2 > e5) {
                while (a[less2] == pivot1) {
                    less2++;
                }
                while (a[great2] == pivot2) {
                    great2--;
                }
                k = less2;
                less = less2;
                loop6:
                while (k <= great2) {
                    ak = a[k];
                    if (ak == pivot2) {
                        while (a[great2] == pivot2) {
                            great = great2 - 1;
                            if (great2 == k) {
                                great2 = great;
                                less2 = less;
                                break loop6;
                            }
                            great2 = great;
                        }
                        if (a[great2] == pivot1) {
                            a[k] = a[less];
                            less2 = less + 1;
                            a[less] = pivot1;
                        } else {
                            a[k] = a[great2];
                            less2 = less;
                        }
                        great = great2 - 1;
                        a[great2] = pivot2;
                        great2 = great;
                    } else if (ak == pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = pivot1;
                    } else {
                        less2 = less;
                    }
                    k++;
                    less = less2;
                }
                less2 = less;
            }
            doSort(a, less2, great2);
        }
    }

    public static void sort(short[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(short[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(short[] a, int left, int right) {
        int i;
        if ((right - left) + 1 < INSERTION_SORT_THRESHOLD) {
            for (i = left + 1; i <= right; i++) {
                short ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
        } else if ((right - left) + 1 > COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR) {
            int[] count = new int[NUM_SHORT_VALUES];
            for (i = left; i <= right; i++) {
                int i2 = a[i] - -32768;
                count[i2] = count[i2] + 1;
            }
            i = 0;
            int k = left;
            while (i < count.length && k <= right) {
                short value = (short) (i - 32768);
                int s = count[i];
                int k2 = k;
                while (s > 0) {
                    k = k2 + 1;
                    a[k2] = value;
                    s--;
                    k2 = k;
                }
                i++;
                k = k2;
            }
        } else {
            dualPivotQuicksort(a, left, right);
        }
    }

    private static void dualPivotQuicksort(short[] a, int left, int right) {
        int k;
        int less;
        short ak;
        int great;
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        short ae1 = a[e1];
        short ae2 = a[e2];
        short ae3 = a[e3];
        short ae4 = a[e4];
        short ae5 = a[e5];
        if (ae1 > ae2) {
            short t = ae1;
            ae1 = ae2;
            ae2 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        if (ae1 > ae3) {
            t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae1 > ae4) {
            t = ae1;
            ae1 = ae4;
            ae4 = t;
        }
        if (ae3 > ae4) {
            t = ae3;
            ae3 = ae4;
            ae4 = t;
        }
        if (ae2 > ae5) {
            t = ae2;
            ae2 = ae5;
            ae5 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        short pivot1 = ae2;
        a[e2] = a[left];
        short pivot2 = ae4;
        a[e4] = a[right];
        int less2 = left + 1;
        int great2 = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            k = less2;
            less = less2;
            loop0:
            while (k <= great2) {
                ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else if (ak > pivot2) {
                    while (a[great2] > pivot2) {
                        great = great2 - 1;
                        if (great2 == k) {
                            great2 = great;
                            less2 = less;
                            break loop0;
                        }
                        great2 = great;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                } else {
                    less2 = less;
                }
                k++;
                less = less2;
            }
            less2 = less;
        } else {
            k = less2;
            less = less2;
            while (k <= great2) {
                ak = a[k];
                if (ak == pivot1) {
                    less2 = less;
                } else if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else {
                    while (a[great2] > pivot1) {
                        great2--;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = pivot1;
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                }
                k++;
                less = less2;
            }
            less2 = less;
        }
        a[left] = a[less2 - 1];
        a[less2 - 1] = pivot1;
        a[right] = a[great2 + 1];
        a[great2 + 1] = pivot2;
        doSort(a, left, less2 - 2);
        doSort(a, great2 + 2, right);
        if (pivotsDiffer) {
            if (less2 < e1 && great2 > e5) {
                while (a[less2] == pivot1) {
                    less2++;
                }
                while (a[great2] == pivot2) {
                    great2--;
                }
                k = less2;
                less = less2;
                loop6:
                while (k <= great2) {
                    ak = a[k];
                    if (ak == pivot2) {
                        while (a[great2] == pivot2) {
                            great = great2 - 1;
                            if (great2 == k) {
                                great2 = great;
                                less2 = less;
                                break loop6;
                            }
                            great2 = great;
                        }
                        if (a[great2] == pivot1) {
                            a[k] = a[less];
                            less2 = less + 1;
                            a[less] = pivot1;
                        } else {
                            a[k] = a[great2];
                            less2 = less;
                        }
                        great = great2 - 1;
                        a[great2] = pivot2;
                        great2 = great;
                    } else if (ak == pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = pivot1;
                    } else {
                        less2 = less;
                    }
                    k++;
                    less = less2;
                }
                less2 = less;
            }
            doSort(a, less2, great2);
        }
    }

    public static void sort(char[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(char[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(char[] a, int left, int right) {
        int i;
        if ((right - left) + 1 < INSERTION_SORT_THRESHOLD) {
            for (i = left + 1; i <= right; i++) {
                char ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
        } else if ((right - left) + 1 > COUNTING_SORT_THRESHOLD_FOR_SHORT_OR_CHAR) {
            int[] count = new int[NUM_SHORT_VALUES];
            for (i = left; i <= right; i++) {
                char c = a[i];
                count[c] = count[c] + 1;
            }
            i = 0;
            int k = left;
            while (i < count.length && k <= right) {
                int s = count[i];
                int k2 = k;
                while (s > 0) {
                    k = k2 + 1;
                    a[k2] = (char) i;
                    s--;
                    k2 = k;
                }
                i++;
                k = k2;
            }
        } else {
            dualPivotQuicksort(a, left, right);
        }
    }

    private static void dualPivotQuicksort(char[] a, int left, int right) {
        int k;
        int less;
        char ak;
        int great;
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        char ae1 = a[e1];
        char ae2 = a[e2];
        char ae3 = a[e3];
        char ae4 = a[e4];
        char ae5 = a[e5];
        if (ae1 > ae2) {
            char t = ae1;
            ae1 = ae2;
            ae2 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        if (ae1 > ae3) {
            t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae1 > ae4) {
            t = ae1;
            ae1 = ae4;
            ae4 = t;
        }
        if (ae3 > ae4) {
            t = ae3;
            ae3 = ae4;
            ae4 = t;
        }
        if (ae2 > ae5) {
            t = ae2;
            ae2 = ae5;
            ae5 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        char pivot1 = ae2;
        a[e2] = a[left];
        char pivot2 = ae4;
        a[e4] = a[right];
        int less2 = left + 1;
        int great2 = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            k = less2;
            less = less2;
            loop0:
            while (k <= great2) {
                ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else if (ak > pivot2) {
                    while (a[great2] > pivot2) {
                        great = great2 - 1;
                        if (great2 == k) {
                            great2 = great;
                            less2 = less;
                            break loop0;
                        }
                        great2 = great;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                } else {
                    less2 = less;
                }
                k++;
                less = less2;
            }
            less2 = less;
        } else {
            k = less2;
            less = less2;
            while (k <= great2) {
                ak = a[k];
                if (ak == pivot1) {
                    less2 = less;
                } else if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else {
                    while (a[great2] > pivot1) {
                        great2--;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = pivot1;
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                }
                k++;
                less = less2;
            }
            less2 = less;
        }
        a[left] = a[less2 - 1];
        a[less2 - 1] = pivot1;
        a[right] = a[great2 + 1];
        a[great2 + 1] = pivot2;
        doSort(a, left, less2 - 2);
        doSort(a, great2 + 2, right);
        if (pivotsDiffer) {
            if (less2 < e1 && great2 > e5) {
                while (a[less2] == pivot1) {
                    less2++;
                }
                while (a[great2] == pivot2) {
                    great2--;
                }
                k = less2;
                less = less2;
                loop6:
                while (k <= great2) {
                    ak = a[k];
                    if (ak == pivot2) {
                        while (a[great2] == pivot2) {
                            great = great2 - 1;
                            if (great2 == k) {
                                great2 = great;
                                less2 = less;
                                break loop6;
                            }
                            great2 = great;
                        }
                        if (a[great2] == pivot1) {
                            a[k] = a[less];
                            less2 = less + 1;
                            a[less] = pivot1;
                        } else {
                            a[k] = a[great2];
                            less2 = less;
                        }
                        great = great2 - 1;
                        a[great2] = pivot2;
                        great2 = great;
                    } else if (ak == pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = pivot1;
                    } else {
                        less2 = less;
                    }
                    k++;
                    less = less2;
                }
                less2 = less;
            }
            doSort(a, less2, great2);
        }
    }

    public static void sort(byte[] a) {
        doSort(a, 0, a.length - 1);
    }

    public static void sort(byte[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        doSort(a, fromIndex, toIndex - 1);
    }

    private static void doSort(byte[] a, int left, int right) {
        int i;
        if ((right - left) + 1 < INSERTION_SORT_THRESHOLD) {
            for (i = left + 1; i <= right; i++) {
                byte ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
        } else if ((right - left) + 1 > COUNTING_SORT_THRESHOLD_FOR_BYTE) {
            int[] count = new int[NUM_BYTE_VALUES];
            for (i = left; i <= right; i++) {
                int i2 = a[i] + COUNTING_SORT_THRESHOLD_FOR_BYTE;
                count[i2] = count[i2] + 1;
            }
            i = 0;
            int k = left;
            while (i < count.length && k <= right) {
                byte value = (byte) (i - 128);
                int s = count[i];
                int k2 = k;
                while (s > 0) {
                    k = k2 + 1;
                    a[k2] = value;
                    s--;
                    k2 = k;
                }
                i++;
                k = k2;
            }
        } else {
            dualPivotQuicksort(a, left, right);
        }
    }

    private static void dualPivotQuicksort(byte[] a, int left, int right) {
        int k;
        int less;
        byte ak;
        int great;
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        byte ae1 = a[e1];
        byte ae2 = a[e2];
        byte ae3 = a[e3];
        byte ae4 = a[e4];
        byte ae5 = a[e5];
        if (ae1 > ae2) {
            byte t = ae1;
            ae1 = ae2;
            ae2 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        if (ae1 > ae3) {
            t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae1 > ae4) {
            t = ae1;
            ae1 = ae4;
            ae4 = t;
        }
        if (ae3 > ae4) {
            t = ae3;
            ae3 = ae4;
            ae4 = t;
        }
        if (ae2 > ae5) {
            t = ae2;
            ae2 = ae5;
            ae5 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        byte pivot1 = ae2;
        a[e2] = a[left];
        byte pivot2 = ae4;
        a[e4] = a[right];
        int less2 = left + 1;
        int great2 = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            k = less2;
            less = less2;
            loop0:
            while (k <= great2) {
                ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else if (ak > pivot2) {
                    while (a[great2] > pivot2) {
                        great = great2 - 1;
                        if (great2 == k) {
                            great2 = great;
                            less2 = less;
                            break loop0;
                        }
                        great2 = great;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                } else {
                    less2 = less;
                }
                k++;
                less = less2;
            }
            less2 = less;
        } else {
            k = less2;
            less = less2;
            while (k <= great2) {
                ak = a[k];
                if (ak == pivot1) {
                    less2 = less;
                } else if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else {
                    while (a[great2] > pivot1) {
                        great2--;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = pivot1;
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                }
                k++;
                less = less2;
            }
            less2 = less;
        }
        a[left] = a[less2 - 1];
        a[less2 - 1] = pivot1;
        a[right] = a[great2 + 1];
        a[great2 + 1] = pivot2;
        doSort(a, left, less2 - 2);
        doSort(a, great2 + 2, right);
        if (pivotsDiffer) {
            if (less2 < e1 && great2 > e5) {
                while (a[less2] == pivot1) {
                    less2++;
                }
                while (a[great2] == pivot2) {
                    great2--;
                }
                k = less2;
                less = less2;
                loop6:
                while (k <= great2) {
                    ak = a[k];
                    if (ak == pivot2) {
                        while (a[great2] == pivot2) {
                            great = great2 - 1;
                            if (great2 == k) {
                                great2 = great;
                                less2 = less;
                                break loop6;
                            }
                            great2 = great;
                        }
                        if (a[great2] == pivot1) {
                            a[k] = a[less];
                            less2 = less + 1;
                            a[less] = pivot1;
                        } else {
                            a[k] = a[great2];
                            less2 = less;
                        }
                        great = great2 - 1;
                        a[great2] = pivot2;
                        great2 = great;
                    } else if (ak == pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = pivot1;
                    } else {
                        less2 = less;
                    }
                    k++;
                    less = less2;
                }
                less2 = less;
            }
            doSort(a, less2, great2);
        }
    }

    public static void sort(float[] a) {
        sortNegZeroAndNaN(a, 0, a.length - 1);
    }

    public static void sort(float[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        sortNegZeroAndNaN(a, fromIndex, toIndex - 1);
    }

    private static void sortNegZeroAndNaN(float[] a, int left, int right) {
        int NEGATIVE_ZERO = Float.floatToIntBits(-0.0f);
        int numNegativeZeros = 0;
        int k = left;
        int n = right;
        while (k <= n) {
            int k2;
            int n2;
            float ak = a[k];
            if (ak == 0.0f && NEGATIVE_ZERO == Float.floatToRawIntBits(ak)) {
                a[k] = 0.0f;
                numNegativeZeros++;
                k2 = k;
                n2 = n;
            } else if (ak != ak) {
                k2 = k - 1;
                a[k] = a[n];
                n2 = n - 1;
                a[n] = Float.NaN;
            } else {
                k2 = k;
                n2 = n;
            }
            k = k2 + 1;
            n = n2;
        }
        doSort(a, left, n);
        if (numNegativeZeros != 0) {
            int zeroIndex = findAnyZero(a, left, n);
            int i = zeroIndex - 1;
            while (i >= left && a[i] == 0.0f) {
                zeroIndex = i;
                i--;
            }
            int m = zeroIndex + numNegativeZeros;
            for (i = zeroIndex; i < m; i++) {
                a[i] = -0.0f;
            }
        }
    }

    private static int findAnyZero(float[] a, int low, int high) {
        while (true) {
            int middle = (low + high) >>> 1;
            float middleValue = a[middle];
            if (middleValue < 0.0f) {
                low = middle + 1;
            } else if (middleValue <= 0.0f) {
                return middle;
            } else {
                high = middle - 1;
            }
        }
    }

    private static void doSort(float[] a, int left, int right) {
        if ((right - left) + 1 < INSERTION_SORT_THRESHOLD) {
            for (int i = left + 1; i <= right; i++) {
                float ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(float[] a, int left, int right) {
        int k;
        int less;
        float ak;
        int great;
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        float ae1 = a[e1];
        float ae2 = a[e2];
        float ae3 = a[e3];
        float ae4 = a[e4];
        float ae5 = a[e5];
        if (ae1 > ae2) {
            float t = ae1;
            ae1 = ae2;
            ae2 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        if (ae1 > ae3) {
            t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae1 > ae4) {
            t = ae1;
            ae1 = ae4;
            ae4 = t;
        }
        if (ae3 > ae4) {
            t = ae3;
            ae3 = ae4;
            ae4 = t;
        }
        if (ae2 > ae5) {
            t = ae2;
            ae2 = ae5;
            ae5 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        float pivot1 = ae2;
        a[e2] = a[left];
        float pivot2 = ae4;
        a[e4] = a[right];
        int less2 = left + 1;
        int great2 = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            k = less2;
            less = less2;
            loop0:
            while (k <= great2) {
                ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else if (ak > pivot2) {
                    while (a[great2] > pivot2) {
                        great = great2 - 1;
                        if (great2 == k) {
                            great2 = great;
                            less2 = less;
                            break loop0;
                        }
                        great2 = great;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                } else {
                    less2 = less;
                }
                k++;
                less = less2;
            }
            less2 = less;
        } else {
            k = less2;
            less = less2;
            while (k <= great2) {
                ak = a[k];
                if (ak == pivot1) {
                    less2 = less;
                } else if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else {
                    while (a[great2] > pivot1) {
                        great2--;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = pivot1;
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                }
                k++;
                less = less2;
            }
            less2 = less;
        }
        a[left] = a[less2 - 1];
        a[less2 - 1] = pivot1;
        a[right] = a[great2 + 1];
        a[great2 + 1] = pivot2;
        doSort(a, left, less2 - 2);
        doSort(a, great2 + 2, right);
        if (pivotsDiffer) {
            if (less2 < e1 && great2 > e5) {
                while (a[less2] == pivot1) {
                    less2++;
                }
                while (a[great2] == pivot2) {
                    great2--;
                }
                k = less2;
                less = less2;
                loop6:
                while (k <= great2) {
                    ak = a[k];
                    if (ak == pivot2) {
                        while (a[great2] == pivot2) {
                            great = great2 - 1;
                            if (great2 == k) {
                                great2 = great;
                                less2 = less;
                                break loop6;
                            }
                            great2 = great;
                        }
                        if (a[great2] == pivot1) {
                            a[k] = a[less];
                            less2 = less + 1;
                            a[less] = pivot1;
                        } else {
                            a[k] = a[great2];
                            less2 = less;
                        }
                        great = great2 - 1;
                        a[great2] = pivot2;
                        great2 = great;
                    } else if (ak == pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = pivot1;
                    } else {
                        less2 = less;
                    }
                    k++;
                    less = less2;
                }
                less2 = less;
            }
            doSort(a, less2, great2);
        }
    }

    public static void sort(double[] a) {
        sortNegZeroAndNaN(a, 0, a.length - 1);
    }

    public static void sort(double[] a, int fromIndex, int toIndex) {
        Arrays.checkStartAndEnd(a.length, fromIndex, toIndex);
        sortNegZeroAndNaN(a, fromIndex, toIndex - 1);
    }

    private static void sortNegZeroAndNaN(double[] a, int left, int right) {
        long NEGATIVE_ZERO = Double.doubleToLongBits(-0.0d);
        int numNegativeZeros = 0;
        int k = left;
        int n = right;
        while (k <= n) {
            int k2;
            int n2;
            double ak = a[k];
            if (ak == 0.0d && NEGATIVE_ZERO == Double.doubleToRawLongBits(ak)) {
                a[k] = 0.0d;
                numNegativeZeros++;
                k2 = k;
                n2 = n;
            } else if (ak != ak) {
                k2 = k - 1;
                a[k] = a[n];
                n2 = n - 1;
                a[n] = Double.NaN;
            } else {
                k2 = k;
                n2 = n;
            }
            k = k2 + 1;
            n = n2;
        }
        doSort(a, left, n);
        if (numNegativeZeros != 0) {
            int zeroIndex = findAnyZero(a, left, n);
            int i = zeroIndex - 1;
            while (i >= left && a[i] == 0.0d) {
                zeroIndex = i;
                i--;
            }
            int m = zeroIndex + numNegativeZeros;
            for (i = zeroIndex; i < m; i++) {
                a[i] = -0.0d;
            }
        }
    }

    private static int findAnyZero(double[] a, int low, int high) {
        while (true) {
            int middle = (low + high) >>> 1;
            double middleValue = a[middle];
            if (middleValue < 0.0d) {
                low = middle + 1;
            } else if (middleValue <= 0.0d) {
                return middle;
            } else {
                high = middle - 1;
            }
        }
    }

    private static void doSort(double[] a, int left, int right) {
        if ((right - left) + 1 < INSERTION_SORT_THRESHOLD) {
            for (int i = left + 1; i <= right; i++) {
                double ai = a[i];
                int j = i - 1;
                while (j >= left && ai < a[j]) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = ai;
            }
            return;
        }
        dualPivotQuicksort(a, left, right);
    }

    private static void dualPivotQuicksort(double[] a, int left, int right) {
        int k;
        int less;
        double ak;
        int great;
        int sixth = ((right - left) + 1) / 6;
        int e1 = left + sixth;
        int e5 = right - sixth;
        int e3 = (left + right) >>> 1;
        int e4 = e3 + sixth;
        int e2 = e3 - sixth;
        double ae1 = a[e1];
        double ae2 = a[e2];
        double ae3 = a[e3];
        double ae4 = a[e4];
        double ae5 = a[e5];
        if (ae1 > ae2) {
            double t = ae1;
            ae1 = ae2;
            ae2 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        if (ae1 > ae3) {
            t = ae1;
            ae1 = ae3;
            ae3 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae1 > ae4) {
            t = ae1;
            ae1 = ae4;
            ae4 = t;
        }
        if (ae3 > ae4) {
            t = ae3;
            ae3 = ae4;
            ae4 = t;
        }
        if (ae2 > ae5) {
            t = ae2;
            ae2 = ae5;
            ae5 = t;
        }
        if (ae2 > ae3) {
            t = ae2;
            ae2 = ae3;
            ae3 = t;
        }
        if (ae4 > ae5) {
            t = ae4;
            ae4 = ae5;
            ae5 = t;
        }
        a[e1] = ae1;
        a[e3] = ae3;
        a[e5] = ae5;
        double pivot1 = ae2;
        a[e2] = a[left];
        double pivot2 = ae4;
        a[e4] = a[right];
        int less2 = left + 1;
        int great2 = right - 1;
        boolean pivotsDiffer = pivot1 != pivot2;
        if (pivotsDiffer) {
            k = less2;
            less = less2;
            loop0:
            while (k <= great2) {
                ak = a[k];
                if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else if (ak > pivot2) {
                    while (a[great2] > pivot2) {
                        great = great2 - 1;
                        if (great2 == k) {
                            great2 = great;
                            less2 = less;
                            break loop0;
                        }
                        great2 = great;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                } else {
                    less2 = less;
                }
                k++;
                less = less2;
            }
            less2 = less;
        } else {
            k = less2;
            less = less2;
            while (k <= great2) {
                ak = a[k];
                if (ak == pivot1) {
                    less2 = less;
                } else if (ak < pivot1) {
                    if (k != less) {
                        a[k] = a[less];
                        a[less] = ak;
                    }
                    less2 = less + 1;
                } else {
                    while (a[great2] > pivot1) {
                        great2--;
                    }
                    if (a[great2] < pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = a[great2];
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                    } else {
                        a[k] = pivot1;
                        great = great2 - 1;
                        a[great2] = ak;
                        great2 = great;
                        less2 = less;
                    }
                }
                k++;
                less = less2;
            }
            less2 = less;
        }
        a[left] = a[less2 - 1];
        a[less2 - 1] = pivot1;
        a[right] = a[great2 + 1];
        a[great2 + 1] = pivot2;
        doSort(a, left, less2 - 2);
        doSort(a, great2 + 2, right);
        if (pivotsDiffer) {
            if (less2 < e1 && great2 > e5) {
                while (a[less2] == pivot1) {
                    less2++;
                }
                while (a[great2] == pivot2) {
                    great2--;
                }
                k = less2;
                less = less2;
                loop6:
                while (k <= great2) {
                    ak = a[k];
                    if (ak == pivot2) {
                        while (a[great2] == pivot2) {
                            great = great2 - 1;
                            if (great2 == k) {
                                great2 = great;
                                less2 = less;
                                break loop6;
                            }
                            great2 = great;
                        }
                        if (a[great2] == pivot1) {
                            a[k] = a[less];
                            less2 = less + 1;
                            a[less] = pivot1;
                        } else {
                            a[k] = a[great2];
                            less2 = less;
                        }
                        great = great2 - 1;
                        a[great2] = pivot2;
                        great2 = great;
                    } else if (ak == pivot1) {
                        a[k] = a[less];
                        less2 = less + 1;
                        a[less] = pivot1;
                    } else {
                        less2 = less;
                    }
                    k++;
                    less = less2;
                }
                less2 = less;
            }
            doSort(a, less2, great2);
        }
    }
}
