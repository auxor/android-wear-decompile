package android.util;

import android.os.SystemClock;
import android.text.format.DateFormat;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import libcore.util.ZoneInfoDB;

public class TimeUtils {
    private static final boolean DBG = false;
    public static final int HUNDRED_DAY_FIELD_LEN = 19;
    private static final long LARGEST_DURATION = 86399999999L;
    public static final long NANOS_PER_MS = 1000000;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final String TAG = "TimeUtils";
    private static char[] sFormatStr;
    private static final Object sFormatSync;
    private static String sLastCountry;
    private static final Object sLastLockObj;
    private static String sLastUniqueCountry;
    private static final Object sLastUniqueLockObj;
    private static ArrayList<TimeZone> sLastUniqueZoneOffsets;
    private static ArrayList<TimeZone> sLastZones;

    static {
        sLastLockObj = new Object();
        sLastZones = null;
        sLastCountry = null;
        sLastUniqueLockObj = new Object();
        sLastUniqueZoneOffsets = null;
        sLastUniqueCountry = null;
        sFormatSync = new Object();
        sFormatStr = new char[24];
    }

    public static TimeZone getTimeZone(int offset, boolean dst, long when, String country) {
        TimeZone best = null;
        Date d = new Date(when);
        TimeZone current = TimeZone.getDefault();
        String currentName = current.getID();
        int currentOffset = current.getOffset(when);
        boolean currentDst = current.inDaylightTime(d);
        Iterator i$ = getTimeZones(country).iterator();
        while (i$.hasNext()) {
            TimeZone tz = (TimeZone) i$.next();
            if (tz.getID().equals(currentName) && currentOffset == offset && currentDst == dst) {
                return current;
            }
            if (best == null && tz.getOffset(when) == offset && tz.inDaylightTime(d) == dst) {
                best = tz;
            }
        }
        return best;
    }

    public static ArrayList<TimeZone> getTimeZonesWithUniqueOffsets(String country) {
        ArrayList<TimeZone> arrayList;
        synchronized (sLastUniqueLockObj) {
            if (country != null) {
                if (country.equals(sLastUniqueCountry)) {
                    arrayList = sLastUniqueZoneOffsets;
                }
            }
            Collection<TimeZone> zones = getTimeZones(country);
            ArrayList<TimeZone> uniqueTimeZones = new ArrayList();
            for (TimeZone zone : zones) {
                boolean found = DBG;
                for (int i = 0; i < uniqueTimeZones.size(); i++) {
                    if (((TimeZone) uniqueTimeZones.get(i)).getRawOffset() == zone.getRawOffset()) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    uniqueTimeZones.add(zone);
                }
            }
            synchronized (sLastUniqueLockObj) {
                sLastUniqueZoneOffsets = uniqueTimeZones;
                sLastUniqueCountry = country;
                arrayList = sLastUniqueZoneOffsets;
            }
        }
        return arrayList;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<java.util.TimeZone> getTimeZones(java.lang.String r11) {
        /*
        r9 = sLastLockObj;
        monitor-enter(r9);
        if (r11 == 0) goto L_0x0011;
    L_0x0005:
        r8 = sLastCountry;	 Catch:{ all -> 0x004b }
        r8 = r11.equals(r8);	 Catch:{ all -> 0x004b }
        if (r8 == 0) goto L_0x0011;
    L_0x000d:
        r6 = sLastZones;	 Catch:{ all -> 0x004b }
        monitor-exit(r9);	 Catch:{ all -> 0x004b }
    L_0x0010:
        return r6;
    L_0x0011:
        monitor-exit(r9);	 Catch:{ all -> 0x004b }
        r6 = new java.util.ArrayList;
        r6.<init>();
        if (r11 == 0) goto L_0x0010;
    L_0x0019:
        r4 = android.content.res.Resources.getSystem();
        r8 = 17891347; // 0x1110013 float:2.6632347E-38 double:8.8395E-317;
        r3 = r4.getXml(r8);
        r8 = "timezones";
        com.android.internal.util.XmlUtils.beginDocument(r3, r8);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
    L_0x0029:
        com.android.internal.util.XmlUtils.nextElement(r3);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        r2 = r3.getName();	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        if (r2 == 0) goto L_0x003a;
    L_0x0032:
        r8 = "timezone";
        r8 = r2.equals(r8);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        if (r8 != 0) goto L_0x004e;
    L_0x003a:
        r3.close();
    L_0x003d:
        r9 = sLastLockObj;
        monitor-enter(r9);
        sLastZones = r6;	 Catch:{ all -> 0x0048 }
        sLastCountry = r11;	 Catch:{ all -> 0x0048 }
        r6 = sLastZones;	 Catch:{ all -> 0x0048 }
        monitor-exit(r9);	 Catch:{ all -> 0x0048 }
        goto L_0x0010;
    L_0x0048:
        r8 = move-exception;
        monitor-exit(r9);	 Catch:{ all -> 0x0048 }
        throw r8;
    L_0x004b:
        r8 = move-exception;
        monitor-exit(r9);	 Catch:{ all -> 0x004b }
        throw r8;
    L_0x004e:
        r8 = 0;
        r9 = "code";
        r0 = r3.getAttributeValue(r8, r9);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        r8 = r11.equals(r0);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        if (r8 == 0) goto L_0x0029;
    L_0x005b:
        r8 = r3.next();	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        r9 = 4;
        if (r8 != r9) goto L_0x0029;
    L_0x0062:
        r7 = r3.getText();	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        r5 = java.util.TimeZone.getTimeZone(r7);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        r8 = r5.getID();	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        r9 = "GMT";
        r8 = r8.startsWith(r9);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        if (r8 != 0) goto L_0x0029;
    L_0x0076:
        r6.add(r5);	 Catch:{ XmlPullParserException -> 0x007a, IOException -> 0x009d }
        goto L_0x0029;
    L_0x007a:
        r1 = move-exception;
        r8 = "TimeUtils";
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00c1 }
        r9.<init>();	 Catch:{ all -> 0x00c1 }
        r10 = "Got xml parser exception getTimeZone('";
        r9 = r9.append(r10);	 Catch:{ all -> 0x00c1 }
        r9 = r9.append(r11);	 Catch:{ all -> 0x00c1 }
        r10 = "'): e=";
        r9 = r9.append(r10);	 Catch:{ all -> 0x00c1 }
        r9 = r9.toString();	 Catch:{ all -> 0x00c1 }
        android.util.Log.e(r8, r9, r1);	 Catch:{ all -> 0x00c1 }
        r3.close();
        goto L_0x003d;
    L_0x009d:
        r1 = move-exception;
        r8 = "TimeUtils";
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00c1 }
        r9.<init>();	 Catch:{ all -> 0x00c1 }
        r10 = "Got IO exception getTimeZone('";
        r9 = r9.append(r10);	 Catch:{ all -> 0x00c1 }
        r9 = r9.append(r11);	 Catch:{ all -> 0x00c1 }
        r10 = "'): e=";
        r9 = r9.append(r10);	 Catch:{ all -> 0x00c1 }
        r9 = r9.toString();	 Catch:{ all -> 0x00c1 }
        android.util.Log.e(r8, r9, r1);	 Catch:{ all -> 0x00c1 }
        r3.close();
        goto L_0x003d;
    L_0x00c1:
        r8 = move-exception;
        r3.close();
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.util.TimeUtils.getTimeZones(java.lang.String):java.util.ArrayList<java.util.TimeZone>");
    }

    public static String getTimeZoneDatabaseVersion() {
        return ZoneInfoDB.getInstance().getVersion();
    }

    private static int accumField(int amt, int suffix, boolean always, int zeropad) {
        if (amt > 99 || (always && zeropad >= 3)) {
            return suffix + 3;
        }
        if (amt > 9 || (always && zeropad >= 2)) {
            return suffix + 2;
        }
        if (always || amt > 0) {
            return suffix + 1;
        }
        return 0;
    }

    private static int printField(char[] formatStr, int amt, char suffix, int pos, boolean always, int zeropad) {
        if (!always && amt <= 0) {
            return pos;
        }
        int startPos = pos;
        if ((always && zeropad >= 3) || amt > 99) {
            int dig = amt / 100;
            formatStr[pos] = (char) (dig + 48);
            pos++;
            amt -= dig * 100;
        }
        if ((always && zeropad >= 2) || amt > 9 || startPos != pos) {
            dig = amt / 10;
            formatStr[pos] = (char) (dig + 48);
            pos++;
            amt -= dig * 10;
        }
        formatStr[pos] = (char) (amt + 48);
        pos++;
        formatStr[pos] = suffix;
        return pos + 1;
    }

    private static int formatDurationLocked(long duration, int fieldLen) {
        if (sFormatStr.length < fieldLen) {
            sFormatStr = new char[fieldLen];
        }
        char[] formatStr = sFormatStr;
        if (duration == 0) {
            fieldLen--;
            int pos = 0;
            while (pos < fieldLen) {
                int pos2 = pos + 1;
                formatStr[pos] = ' ';
                pos = pos2;
            }
            formatStr[pos] = '0';
            return pos + 1;
        }
        char prefix;
        if (duration > 0) {
            prefix = '+';
        } else {
            prefix = '-';
            duration = -duration;
        }
        if (duration > LARGEST_DURATION) {
            duration = LARGEST_DURATION;
        }
        int millis = (int) (duration % 1000);
        int seconds = (int) Math.floor((double) (duration / 1000));
        int days = 0;
        int hours = 0;
        int minutes = 0;
        if (seconds > SECONDS_PER_DAY) {
            days = seconds / SECONDS_PER_DAY;
            seconds -= SECONDS_PER_DAY * days;
        }
        if (seconds > SECONDS_PER_HOUR) {
            hours = seconds / SECONDS_PER_HOUR;
            seconds -= hours * SECONDS_PER_HOUR;
        }
        if (seconds > SECONDS_PER_MINUTE) {
            minutes = seconds / SECONDS_PER_MINUTE;
            seconds -= minutes * SECONDS_PER_MINUTE;
        }
        pos2 = 0;
        if (fieldLen != 0) {
            int myLen = accumField(days, 1, DBG, 0);
            myLen += accumField(hours, 1, myLen > 0 ? true : DBG, 2);
            myLen += accumField(minutes, 1, myLen > 0 ? true : DBG, 2);
            myLen += accumField(seconds, 1, myLen > 0 ? true : DBG, 2);
            for (myLen += accumField(millis, 2, true, myLen > 0 ? 3 : 0) + 1; myLen < fieldLen; myLen++) {
                formatStr[pos2] = ' ';
                pos2++;
            }
        }
        formatStr[pos2] = prefix;
        pos2++;
        int start = pos2;
        boolean zeropad = fieldLen != 0 ? true : DBG;
        pos2 = printField(formatStr, days, DateFormat.DATE, pos2, DBG, 0);
        pos2 = printField(formatStr, hours, DateFormat.HOUR, pos2, pos2 != start ? true : DBG, zeropad ? 2 : 0);
        pos2 = printField(formatStr, minutes, DateFormat.MINUTE, pos2, pos2 != start ? true : DBG, zeropad ? 2 : 0);
        pos2 = printField(formatStr, seconds, DateFormat.SECONDS, pos2, pos2 != start ? true : DBG, zeropad ? 2 : 0);
        int i = (!zeropad || pos2 == start) ? 0 : 3;
        pos2 = printField(formatStr, millis, DateFormat.MINUTE, pos2, true, i);
        formatStr[pos2] = DateFormat.SECONDS;
        return pos2 + 1;
    }

    public static void formatDuration(long duration, StringBuilder builder) {
        synchronized (sFormatSync) {
            builder.append(sFormatStr, 0, formatDurationLocked(duration, 0));
        }
    }

    public static void formatDuration(long duration, PrintWriter pw, int fieldLen) {
        synchronized (sFormatSync) {
            pw.print(new String(sFormatStr, 0, formatDurationLocked(duration, fieldLen)));
        }
    }

    public static void formatDuration(long duration, PrintWriter pw) {
        formatDuration(duration, pw, 0);
    }

    public static void formatDuration(long time, long now, PrintWriter pw) {
        if (time == 0) {
            pw.print("--");
        } else {
            formatDuration(time - now, pw, 0);
        }
    }

    public static String formatUptime(long time) {
        long diff = time - SystemClock.uptimeMillis();
        if (diff > 0) {
            return time + " (in " + diff + " ms)";
        }
        if (diff < 0) {
            return time + " (" + (-diff) + " ms ago)";
        }
        return time + " (now)";
    }

    public static String logTimeOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        if (millis < 0) {
            return Long.toString(millis);
        }
        c.setTimeInMillis(millis);
        return String.format("%tm-%td %tH:%tM:%tS.%tL", new Object[]{c, c, c, c, c, c});
    }
}
