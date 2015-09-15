package libcore.util;

import dalvik.bytecode.Opcodes;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConstants;
import libcore.io.BufferIterator;

public final class ZoneInfo extends TimeZone {
    private static final int[] LEAP;
    private static final long MILLISECONDS_PER_400_YEARS = 12622780800000L;
    private static final long MILLISECONDS_PER_DAY = 86400000;
    private static final int[] NORMAL;
    private static final long UNIX_OFFSET = 62167219200000L;
    private final int mDstSavings;
    private final int mEarliestRawOffset;
    private final byte[] mIsDsts;
    private final int[] mOffsets;
    private int mRawOffset;
    private final int[] mTransitions;
    private final byte[] mTypes;
    private final boolean mUseDst;

    private static class CheckedArithmeticException extends Exception {
        private CheckedArithmeticException() {
        }
    }

    static class OffsetInterval {
        private final int endWallTimeSeconds;
        private final int isDst;
        private final int startWallTimeSeconds;
        private final int totalOffsetSeconds;

        public static OffsetInterval create(ZoneInfo timeZone, int transitionIndex) throws CheckedArithmeticException {
            if (transitionIndex < -1 || transitionIndex >= timeZone.mTransitions.length) {
                return null;
            }
            int rawOffsetSeconds = timeZone.mRawOffset / Grego.MILLIS_PER_SECOND;
            if (transitionIndex == -1) {
                return new OffsetInterval(DatatypeConstants.FIELD_UNDEFINED, ZoneInfo.checkedAdd(timeZone.mTransitions[0], rawOffsetSeconds), 0, rawOffsetSeconds);
            }
            int endWallTimeSeconds;
            byte type = timeZone.mTypes[transitionIndex];
            int totalOffsetSeconds = timeZone.mOffsets[type] + rawOffsetSeconds;
            if (transitionIndex == timeZone.mTransitions.length - 1) {
                endWallTimeSeconds = Integer.MAX_VALUE;
            } else {
                endWallTimeSeconds = ZoneInfo.checkedAdd(timeZone.mTransitions[transitionIndex + 1], totalOffsetSeconds);
            }
            return new OffsetInterval(ZoneInfo.checkedAdd(timeZone.mTransitions[transitionIndex], totalOffsetSeconds), endWallTimeSeconds, timeZone.mIsDsts[type], totalOffsetSeconds);
        }

        private OffsetInterval(int startWallTimeSeconds, int endWallTimeSeconds, int isDst, int totalOffsetSeconds) {
            this.startWallTimeSeconds = startWallTimeSeconds;
            this.endWallTimeSeconds = endWallTimeSeconds;
            this.isDst = isDst;
            this.totalOffsetSeconds = totalOffsetSeconds;
        }

        public boolean containsWallTime(long wallTimeSeconds) {
            return wallTimeSeconds >= ((long) this.startWallTimeSeconds) && wallTimeSeconds < ((long) this.endWallTimeSeconds);
        }

        public int getIsDst() {
            return this.isDst;
        }

        public int getTotalOffsetSeconds() {
            return this.totalOffsetSeconds;
        }

        public long getEndWallTimeSeconds() {
            return (long) this.endWallTimeSeconds;
        }

        public long getStartWallTimeSeconds() {
            return (long) this.startWallTimeSeconds;
        }
    }

    public static class WallTime {
        private final GregorianCalendar calendar;
        private int gmtOffsetSeconds;
        private int hour;
        private int isDst;
        private int minute;
        private int month;
        private int monthDay;
        private int second;
        private int weekDay;
        private int year;
        private int yearDay;

        public WallTime() {
            this.calendar = createGregorianCalendar();
            this.calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        private static GregorianCalendar createGregorianCalendar() {
            return new GregorianCalendar(false);
        }

        public void localtime(int timeSeconds, ZoneInfo zoneInfo) {
            try {
                byte isDst;
                int offsetSeconds = zoneInfo.mRawOffset / Grego.MILLIS_PER_SECOND;
                if (zoneInfo.mTransitions.length == 0) {
                    isDst = (byte) 0;
                } else {
                    int transitionIndex = findTransitionIndex(zoneInfo, timeSeconds);
                    if (transitionIndex < 0) {
                        isDst = (byte) 0;
                    } else {
                        byte transitionType = zoneInfo.mTypes[transitionIndex];
                        offsetSeconds += zoneInfo.mOffsets[transitionType];
                        isDst = zoneInfo.mIsDsts[transitionType];
                    }
                }
                this.calendar.setTimeInMillis(((long) ZoneInfo.checkedAdd(timeSeconds, offsetSeconds)) * 1000);
                copyFieldsFromCalendar();
                this.isDst = isDst;
                this.gmtOffsetSeconds = offsetSeconds;
            } catch (CheckedArithmeticException e) {
            }
        }

        public int mktime(ZoneInfo zoneInfo) {
            int i = 1;
            if (this.isDst > 0) {
                this.isDst = 1;
            } else if (this.isDst < 0) {
                this.isDst = -1;
                i = -1;
            } else {
                i = 0;
            }
            this.isDst = i;
            copyFieldsToCalendar();
            long longWallTimeSeconds = this.calendar.getTimeInMillis() / 1000;
            if (-2147483648L > longWallTimeSeconds || longWallTimeSeconds > 2147483647L) {
                return -1;
            }
            int wallTimeSeconds = (int) longWallTimeSeconds;
            try {
                int rawOffsetSeconds = zoneInfo.mRawOffset / Grego.MILLIS_PER_SECOND;
                int rawTimeSeconds = ZoneInfo.checkedSubtract(wallTimeSeconds, rawOffsetSeconds);
                if (zoneInfo.mTransitions.length != 0) {
                    int initialTransitionIndex = findTransitionIndex(zoneInfo, rawTimeSeconds);
                    Integer result;
                    if (this.isDst < 0) {
                        result = doWallTimeSearch(zoneInfo, initialTransitionIndex, wallTimeSeconds, true);
                        if (result != null) {
                            return result.intValue();
                        }
                        return -1;
                    }
                    result = doWallTimeSearch(zoneInfo, initialTransitionIndex, wallTimeSeconds, true);
                    if (result == null) {
                        result = doWallTimeSearch(zoneInfo, initialTransitionIndex, wallTimeSeconds, false);
                    }
                    if (result == null) {
                        result = Integer.valueOf(-1);
                    }
                    return result.intValue();
                } else if (this.isDst > 0) {
                    return -1;
                } else {
                    copyFieldsFromCalendar();
                    this.isDst = 0;
                    this.gmtOffsetSeconds = rawOffsetSeconds;
                    return rawTimeSeconds;
                }
            } catch (CheckedArithmeticException e) {
                return -1;
            }
        }

        private Integer tryOffsetAdjustments(ZoneInfo zoneInfo, int oldWallTimeSeconds, OffsetInterval targetInterval, int transitionIndex, int isDstToFind) throws CheckedArithmeticException {
            int[] offsetsToTry = getOffsetsOfType(zoneInfo, transitionIndex, isDstToFind);
            for (int access$000 : offsetsToTry) {
                int jOffsetSeconds = (zoneInfo.mRawOffset / Grego.MILLIS_PER_SECOND) + access$000;
                int targetIntervalOffsetSeconds = targetInterval.getTotalOffsetSeconds();
                int adjustedWallTimeSeconds = ZoneInfo.checkedAdd(oldWallTimeSeconds, targetIntervalOffsetSeconds - jOffsetSeconds);
                if (targetInterval.containsWallTime((long) adjustedWallTimeSeconds)) {
                    int returnValue = ZoneInfo.checkedSubtract(adjustedWallTimeSeconds, targetIntervalOffsetSeconds);
                    this.calendar.setTimeInMillis(((long) adjustedWallTimeSeconds) * 1000);
                    copyFieldsFromCalendar();
                    this.isDst = targetInterval.getIsDst();
                    this.gmtOffsetSeconds = targetIntervalOffsetSeconds;
                    return Integer.valueOf(returnValue);
                }
            }
            return null;
        }

        private static int[] getOffsetsOfType(ZoneInfo zoneInfo, int startIndex, int isDst) {
            int[] offsets = new int[(zoneInfo.mOffsets.length + 1)];
            boolean[] seen = new boolean[zoneInfo.mOffsets.length];
            int delta = 0;
            boolean clampTop = false;
            boolean clampBottom = false;
            int numFound = 0;
            while (true) {
                int numFound2;
                delta *= -1;
                if (delta >= 0) {
                    delta++;
                }
                int transitionIndex = startIndex + delta;
                if (delta < 0 && transitionIndex < -1) {
                    clampBottom = true;
                    numFound2 = numFound;
                } else if (delta <= 0 || transitionIndex < zoneInfo.mTypes.length) {
                    if (transitionIndex != -1) {
                        byte type = zoneInfo.mTypes[transitionIndex];
                        if (!seen[type]) {
                            if (zoneInfo.mIsDsts[type] == isDst) {
                                numFound2 = numFound + 1;
                                offsets[numFound] = zoneInfo.mOffsets[type];
                            } else {
                                numFound2 = numFound;
                            }
                            seen[type] = true;
                        }
                    } else if (isDst == 0) {
                        numFound2 = numFound + 1;
                        offsets[numFound] = 0;
                    }
                    numFound2 = numFound;
                } else {
                    clampTop = true;
                    numFound2 = numFound;
                }
                if (clampTop && clampBottom) {
                    int[] toReturn = new int[numFound2];
                    System.arraycopy(offsets, 0, toReturn, 0, numFound2);
                    return toReturn;
                }
                numFound = numFound2;
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.lang.Integer doWallTimeSearch(libcore.util.ZoneInfo r19, int r20, int r21, boolean r22) throws libcore.util.ZoneInfo.CheckedArithmeticException {
            /*
            r18 = this;
            r8 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
            r10 = 0;
            r9 = 0;
            r12 = 0;
        L_0x0006:
            r2 = r12 + 1;
            r15 = r2 / 2;
            r2 = r12 % 2;
            r3 = 1;
            if (r2 != r3) goto L_0x0011;
        L_0x000f:
            r15 = r15 * -1;
        L_0x0011:
            r12 = r12 + 1;
            if (r15 <= 0) goto L_0x0017;
        L_0x0015:
            if (r10 != 0) goto L_0x001b;
        L_0x0017:
            if (r15 >= 0) goto L_0x0021;
        L_0x0019:
            if (r9 == 0) goto L_0x0021;
        L_0x001b:
            if (r10 == 0) goto L_0x0006;
        L_0x001d:
            if (r9 == 0) goto L_0x0006;
        L_0x001f:
            r13 = 0;
        L_0x0020:
            return r13;
        L_0x0021:
            r6 = r20 + r15;
            r0 = r19;
            r5 = libcore.util.ZoneInfo.OffsetInterval.create(r0, r6);
            if (r5 != 0) goto L_0x0038;
        L_0x002b:
            if (r15 <= 0) goto L_0x0034;
        L_0x002d:
            r2 = 1;
        L_0x002e:
            r10 = r10 | r2;
            if (r15 >= 0) goto L_0x0036;
        L_0x0031:
            r2 = 1;
        L_0x0032:
            r9 = r9 | r2;
            goto L_0x001b;
        L_0x0034:
            r2 = 0;
            goto L_0x002e;
        L_0x0036:
            r2 = 0;
            goto L_0x0032;
        L_0x0038:
            if (r22 == 0) goto L_0x0072;
        L_0x003a:
            r0 = r21;
            r2 = (long) r0;
            r2 = r5.containsWallTime(r2);
            if (r2 == 0) goto L_0x008c;
        L_0x0043:
            r0 = r18;
            r2 = r0.isDst;
            r3 = -1;
            if (r2 == r3) goto L_0x0054;
        L_0x004a:
            r2 = r5.getIsDst();
            r0 = r18;
            r3 = r0.isDst;
            if (r2 != r3) goto L_0x008c;
        L_0x0054:
            r14 = r5.getTotalOffsetSeconds();
            r0 = r21;
            r13 = libcore.util.ZoneInfo.checkedSubtract(r0, r14);
            r18.copyFieldsFromCalendar();
            r2 = r5.getIsDst();
            r0 = r18;
            r0.isDst = r2;
            r0 = r18;
            r0.gmtOffsetSeconds = r14;
            r13 = java.lang.Integer.valueOf(r13);
            goto L_0x0020;
        L_0x0072:
            r0 = r18;
            r2 = r0.isDst;
            r3 = r5.getIsDst();
            if (r2 == r3) goto L_0x008c;
        L_0x007c:
            r0 = r18;
            r7 = r0.isDst;
            r2 = r18;
            r3 = r19;
            r4 = r21;
            r13 = r2.tryOffsetAdjustments(r3, r4, r5, r6, r7);
            if (r13 != 0) goto L_0x0020;
        L_0x008c:
            if (r15 <= 0) goto L_0x00a8;
        L_0x008e:
            r2 = r5.getEndWallTimeSeconds();
            r0 = r21;
            r0 = (long) r0;
            r16 = r0;
            r2 = r2 - r16;
            r16 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
            r2 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1));
            if (r2 <= 0) goto L_0x00a6;
        L_0x00a0:
            r11 = 1;
        L_0x00a1:
            if (r11 == 0) goto L_0x001b;
        L_0x00a3:
            r10 = 1;
            goto L_0x001b;
        L_0x00a6:
            r11 = 0;
            goto L_0x00a1;
        L_0x00a8:
            if (r15 >= 0) goto L_0x001b;
        L_0x00aa:
            r0 = r21;
            r2 = (long) r0;
            r16 = r5.getStartWallTimeSeconds();
            r2 = r2 - r16;
            r16 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
            r2 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1));
            if (r2 < 0) goto L_0x00c0;
        L_0x00ba:
            r11 = 1;
        L_0x00bb:
            if (r11 == 0) goto L_0x001b;
        L_0x00bd:
            r9 = 1;
            goto L_0x001b;
        L_0x00c0:
            r11 = 0;
            goto L_0x00bb;
            */
            throw new UnsupportedOperationException("Method not decompiled: libcore.util.ZoneInfo.WallTime.doWallTimeSearch(libcore.util.ZoneInfo, int, int, boolean):java.lang.Integer");
        }

        public void setYear(int year) {
            this.year = year;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public void setMonthDay(int monthDay) {
            this.monthDay = monthDay;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public void setSecond(int second) {
            this.second = second;
        }

        public void setWeekDay(int weekDay) {
            this.weekDay = weekDay;
        }

        public void setYearDay(int yearDay) {
            this.yearDay = yearDay;
        }

        public void setIsDst(int isDst) {
            this.isDst = isDst;
        }

        public void setGmtOffset(int gmtoff) {
            this.gmtOffsetSeconds = gmtoff;
        }

        public int getYear() {
            return this.year;
        }

        public int getMonth() {
            return this.month;
        }

        public int getMonthDay() {
            return this.monthDay;
        }

        public int getHour() {
            return this.hour;
        }

        public int getMinute() {
            return this.minute;
        }

        public int getSecond() {
            return this.second;
        }

        public int getWeekDay() {
            return this.weekDay;
        }

        public int getYearDay() {
            return this.yearDay;
        }

        public int getGmtOffset() {
            return this.gmtOffsetSeconds;
        }

        public int getIsDst() {
            return this.isDst;
        }

        private void copyFieldsToCalendar() {
            this.calendar.set(1, this.year);
            this.calendar.set(2, this.month);
            this.calendar.set(5, this.monthDay);
            this.calendar.set(11, this.hour);
            this.calendar.set(12, this.minute);
            this.calendar.set(13, this.second);
        }

        private void copyFieldsFromCalendar() {
            this.year = this.calendar.get(1);
            this.month = this.calendar.get(2);
            this.monthDay = this.calendar.get(5);
            this.hour = this.calendar.get(11);
            this.minute = this.calendar.get(12);
            this.second = this.calendar.get(13);
            this.weekDay = this.calendar.get(7) - 1;
            this.yearDay = this.calendar.get(6) - 1;
        }

        private static int findTransitionIndex(ZoneInfo timeZone, int timeSeconds) {
            int matchingRawTransition = Arrays.binarySearch(timeZone.mTransitions, timeSeconds);
            if (matchingRawTransition < 0) {
                return (matchingRawTransition ^ -1) - 1;
            }
            return matchingRawTransition;
        }
    }

    static {
        NORMAL = new int[]{0, 31, 59, 90, Opcodes.OP_INVOKE_INTERFACE_RANGE, Opcodes.OP_XOR_INT, Opcodes.OP_AND_INT_2ADDR, Opcodes.OP_REM_INT_LIT16, Opcodes.OP_IGET_WIDE_QUICK, 273, HttpURLConnection.HTTP_NOT_MODIFIED, 334};
        LEAP = new int[]{0, 31, 60, 91, 121, Opcodes.OP_SHL_INT, Opcodes.OP_OR_INT_2ADDR, Opcodes.OP_AND_INT_LIT16, Opcodes.OP_IGET_OBJECT_QUICK, 274, HttpURLConnection.HTTP_USE_PROXY, 335};
    }

    public static ZoneInfo makeTimeZone(String id, BufferIterator it) {
        if (it.readInt() != 1415211366) {
            return null;
        }
        it.skip(28);
        int tzh_timecnt = it.readInt();
        int tzh_typecnt = it.readInt();
        it.skip(4);
        int[] transitions = new int[tzh_timecnt];
        it.readIntArray(transitions, 0, transitions.length);
        byte[] type = new byte[tzh_timecnt];
        it.readByteArray(type, 0, type.length);
        int[] gmtOffsets = new int[tzh_typecnt];
        byte[] isDsts = new byte[tzh_typecnt];
        for (int i = 0; i < tzh_typecnt; i++) {
            gmtOffsets[i] = it.readInt();
            isDsts[i] = it.readByte();
            it.skip(1);
        }
        return new ZoneInfo(id, transitions, type, gmtOffsets, isDsts);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private ZoneInfo(java.lang.String r17, int[] r18, byte[] r19, int[] r20, byte[] r21) {
        /*
        r16 = this;
        r16.<init>();
        r0 = r18;
        r1 = r16;
        r1.mTransitions = r0;
        r0 = r19;
        r1 = r16;
        r1.mTypes = r0;
        r0 = r21;
        r1 = r16;
        r1.mIsDsts = r0;
        r16.setID(r17);
        r9 = 0;
        r6 = 0;
        r8 = 0;
        r5 = 0;
        r0 = r16;
        r12 = r0.mTransitions;
        r12 = r12.length;
        r7 = r12 + -1;
    L_0x0023:
        if (r6 == 0) goto L_0x0027;
    L_0x0025:
        if (r5 != 0) goto L_0x004c;
    L_0x0027:
        if (r7 < 0) goto L_0x004c;
    L_0x0029:
        r0 = r16;
        r12 = r0.mTypes;
        r12 = r12[r7];
        r10 = r12 & 255;
        if (r6 != 0) goto L_0x003d;
    L_0x0033:
        r0 = r16;
        r12 = r0.mIsDsts;
        r12 = r12[r10];
        if (r12 != 0) goto L_0x003d;
    L_0x003b:
        r6 = 1;
        r9 = r7;
    L_0x003d:
        if (r5 != 0) goto L_0x0049;
    L_0x003f:
        r0 = r16;
        r12 = r0.mIsDsts;
        r12 = r12[r10];
        if (r12 == 0) goto L_0x0049;
    L_0x0047:
        r5 = 1;
        r8 = r7;
    L_0x0049:
        r7 = r7 + -1;
        goto L_0x0023;
    L_0x004c:
        r0 = r16;
        r12 = r0.mTypes;
        r12 = r12.length;
        if (r9 < r12) goto L_0x00ab;
    L_0x0053:
        r12 = 0;
        r12 = r20[r12];
        r0 = r16;
        r0.mRawOffset = r12;
    L_0x005a:
        r0 = r16;
        r12 = r0.mTypes;
        r12 = r12.length;
        if (r8 < r12) goto L_0x00ba;
    L_0x0061:
        r12 = 0;
        r0 = r16;
        r0.mDstSavings = r12;
    L_0x0066:
        r4 = -1;
        r7 = 0;
    L_0x0068:
        r0 = r16;
        r12 = r0.mTransitions;
        r12 = r12.length;
        if (r7 >= r12) goto L_0x0080;
    L_0x006f:
        r0 = r16;
        r12 = r0.mIsDsts;
        r0 = r16;
        r13 = r0.mTypes;
        r13 = r13[r7];
        r13 = r13 & 255;
        r12 = r12[r13];
        if (r12 != 0) goto L_0x00da;
    L_0x007f:
        r4 = r7;
    L_0x0080:
        r12 = -1;
        if (r4 == r12) goto L_0x00dd;
    L_0x0083:
        r0 = r16;
        r12 = r0.mTypes;
        r12 = r12[r4];
        r12 = r12 & 255;
        r3 = r20[r12];
    L_0x008d:
        r0 = r20;
        r1 = r16;
        r1.mOffsets = r0;
        r7 = 0;
    L_0x0094:
        r0 = r16;
        r12 = r0.mOffsets;
        r12 = r12.length;
        if (r7 >= r12) goto L_0x00e2;
    L_0x009b:
        r0 = r16;
        r12 = r0.mOffsets;
        r13 = r12[r7];
        r0 = r16;
        r14 = r0.mRawOffset;
        r13 = r13 - r14;
        r12[r7] = r13;
        r7 = r7 + 1;
        goto L_0x0094;
    L_0x00ab:
        r0 = r16;
        r12 = r0.mTypes;
        r12 = r12[r9];
        r12 = r12 & 255;
        r12 = r20[r12];
        r0 = r16;
        r0.mRawOffset = r12;
        goto L_0x005a;
    L_0x00ba:
        r0 = r16;
        r12 = r0.mTypes;
        r12 = r12[r9];
        r12 = r12 & 255;
        r12 = r20[r12];
        r0 = r16;
        r13 = r0.mTypes;
        r13 = r13[r8];
        r13 = r13 & 255;
        r13 = r20[r13];
        r12 = r12 - r13;
        r12 = java.lang.Math.abs(r12);
        r12 = r12 * 1000;
        r0 = r16;
        r0.mDstSavings = r12;
        goto L_0x0066;
    L_0x00da:
        r7 = r7 + 1;
        goto L_0x0068;
    L_0x00dd:
        r0 = r16;
        r3 = r0.mRawOffset;
        goto L_0x008d;
    L_0x00e2:
        r11 = 0;
        r12 = java.lang.System.currentTimeMillis();
        r14 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r12 = r12 / r14;
        r2 = (int) r12;
        r0 = r16;
        r12 = r0.mTransitions;
        r12 = r12.length;
        r7 = r12 + -1;
    L_0x00f2:
        if (r7 < 0) goto L_0x010b;
    L_0x00f4:
        r0 = r16;
        r12 = r0.mTransitions;
        r12 = r12[r7];
        if (r12 < r2) goto L_0x010b;
    L_0x00fc:
        r0 = r16;
        r12 = r0.mIsDsts;
        r0 = r16;
        r13 = r0.mTypes;
        r13 = r13[r7];
        r12 = r12[r13];
        if (r12 <= 0) goto L_0x0120;
    L_0x010a:
        r11 = 1;
    L_0x010b:
        r0 = r16;
        r0.mUseDst = r11;
        r0 = r16;
        r12 = r0.mRawOffset;
        r12 = r12 * 1000;
        r0 = r16;
        r0.mRawOffset = r12;
        r12 = r3 * 1000;
        r0 = r16;
        r0.mEarliestRawOffset = r12;
        return;
    L_0x0120:
        r7 = r7 + -1;
        goto L_0x00f2;
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.util.ZoneInfo.<init>(java.lang.String, int[], byte[], int[], byte[]):void");
    }

    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
        year %= HttpURLConnection.HTTP_BAD_REQUEST;
        long calc = ((((long) (year / HttpURLConnection.HTTP_BAD_REQUEST)) * MILLISECONDS_PER_400_YEARS) + (((long) year) * 31536000000L)) + (((long) ((year + 3) / 4)) * MILLISECONDS_PER_DAY);
        if (year > 0) {
            calc -= ((long) ((year - 1) / 100)) * MILLISECONDS_PER_DAY;
        }
        boolean isLeap = year == 0 || (year % 4 == 0 && year % 100 != 0);
        return getOffset(((((calc + (((long) (isLeap ? LEAP : NORMAL)[month]) * MILLISECONDS_PER_DAY)) + (((long) (day - 1)) * MILLISECONDS_PER_DAY)) + ((long) millis)) - ((long) this.mRawOffset)) - UNIX_OFFSET);
    }

    public int getOffset(long when) {
        int transition = Arrays.binarySearch(this.mTransitions, (int) (when / 1000));
        if (transition < 0) {
            transition = (transition ^ -1) - 1;
            if (transition < 0) {
                return this.mEarliestRawOffset;
            }
        }
        return this.mRawOffset + (this.mOffsets[this.mTypes[transition] & Opcodes.OP_CONST_CLASS_JUMBO] * Grego.MILLIS_PER_SECOND);
    }

    public boolean inDaylightTime(Date time) {
        boolean z = true;
        int transition = Arrays.binarySearch(this.mTransitions, (int) (time.getTime() / 1000));
        if (transition < 0) {
            transition = (transition ^ -1) - 1;
            if (transition < 0) {
                return false;
            }
        }
        if (this.mIsDsts[this.mTypes[transition] & Opcodes.OP_CONST_CLASS_JUMBO] != (byte) 1) {
            z = false;
        }
        return z;
    }

    public int getRawOffset() {
        return this.mRawOffset;
    }

    public void setRawOffset(int off) {
        this.mRawOffset = off;
    }

    public int getDSTSavings() {
        return this.mUseDst ? this.mDstSavings : 0;
    }

    public boolean useDaylightTime() {
        return this.mUseDst;
    }

    public boolean hasSameRules(TimeZone timeZone) {
        boolean z = true;
        if (!(timeZone instanceof ZoneInfo)) {
            return false;
        }
        ZoneInfo other = (ZoneInfo) timeZone;
        if (this.mUseDst != other.mUseDst) {
            return false;
        }
        if (this.mUseDst) {
            if (!(this.mRawOffset == other.mRawOffset && Arrays.equals(this.mOffsets, other.mOffsets) && Arrays.equals(this.mIsDsts, other.mIsDsts) && Arrays.equals(this.mTypes, other.mTypes) && Arrays.equals(this.mTransitions, other.mTransitions))) {
                z = false;
            }
            return z;
        }
        if (this.mRawOffset != other.mRawOffset) {
            z = false;
        }
        return z;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ZoneInfo)) {
            return false;
        }
        ZoneInfo other = (ZoneInfo) obj;
        if (getID().equals(other.getID()) && hasSameRules(other)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((((((((getID().hashCode() + 31) * 31) + Arrays.hashCode(this.mOffsets)) * 31) + Arrays.hashCode(this.mIsDsts)) * 31) + this.mRawOffset) * 31) + Arrays.hashCode(this.mTransitions)) * 31) + Arrays.hashCode(this.mTypes)) * 31) + (this.mUseDst ? 1231 : 1237);
    }

    public String toString() {
        return getClass().getName() + "[id=\"" + getID() + "\"" + ",mRawOffset=" + this.mRawOffset + ",mEarliestRawOffset=" + this.mEarliestRawOffset + ",mUseDst=" + this.mUseDst + ",mDstSavings=" + this.mDstSavings + ",transitions=" + this.mTransitions.length + "]";
    }

    public Object clone() {
        return super.clone();
    }

    private static int checkedAdd(int a, int b) throws CheckedArithmeticException {
        long result = ((long) a) + ((long) b);
        if (result == ((long) ((int) result))) {
            return (int) result;
        }
        throw new CheckedArithmeticException();
    }

    private static int checkedSubtract(int a, int b) throws CheckedArithmeticException {
        long result = ((long) a) - ((long) b);
        if (result == ((long) ((int) result))) {
            return (int) result;
        }
        throw new CheckedArithmeticException();
    }
}
