package android.text.format;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import com.android.internal.R;
import com.android.internal.widget.LockPatternUtils;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import libcore.icu.DateIntervalFormat;
import libcore.icu.LocaleData;

public class DateUtils {
    @Deprecated
    public static final String ABBREV_MONTH_FORMAT = "%b";
    public static final String ABBREV_WEEKDAY_FORMAT = "%a";
    public static final long DAY_IN_MILLIS = 86400000;
    @Deprecated
    public static final int FORMAT_12HOUR = 64;
    @Deprecated
    public static final int FORMAT_24HOUR = 128;
    public static final int FORMAT_ABBREV_ALL = 524288;
    public static final int FORMAT_ABBREV_MONTH = 65536;
    public static final int FORMAT_ABBREV_RELATIVE = 262144;
    public static final int FORMAT_ABBREV_TIME = 16384;
    public static final int FORMAT_ABBREV_WEEKDAY = 32768;
    @Deprecated
    public static final int FORMAT_CAP_AMPM = 256;
    @Deprecated
    public static final int FORMAT_CAP_MIDNIGHT = 4096;
    @Deprecated
    public static final int FORMAT_CAP_NOON = 1024;
    @Deprecated
    public static final int FORMAT_CAP_NOON_MIDNIGHT = 5120;
    public static final int FORMAT_NO_MIDNIGHT = 2048;
    public static final int FORMAT_NO_MONTH_DAY = 32;
    public static final int FORMAT_NO_NOON = 512;
    @Deprecated
    public static final int FORMAT_NO_NOON_MIDNIGHT = 2560;
    public static final int FORMAT_NO_YEAR = 8;
    public static final int FORMAT_NUMERIC_DATE = 131072;
    public static final int FORMAT_SHOW_DATE = 16;
    public static final int FORMAT_SHOW_TIME = 1;
    public static final int FORMAT_SHOW_WEEKDAY = 2;
    public static final int FORMAT_SHOW_YEAR = 4;
    @Deprecated
    public static final int FORMAT_UTC = 8192;
    public static final long HOUR_IN_MILLIS = 3600000;
    @Deprecated
    public static final String HOUR_MINUTE_24 = "%H:%M";
    @Deprecated
    public static final int LENGTH_LONG = 10;
    @Deprecated
    public static final int LENGTH_MEDIUM = 20;
    @Deprecated
    public static final int LENGTH_SHORT = 30;
    @Deprecated
    public static final int LENGTH_SHORTER = 40;
    @Deprecated
    public static final int LENGTH_SHORTEST = 50;
    public static final long MINUTE_IN_MILLIS = 60000;
    public static final String MONTH_DAY_FORMAT = "%-d";
    public static final String MONTH_FORMAT = "%B";
    public static final String NUMERIC_MONTH_FORMAT = "%m";
    public static final long SECOND_IN_MILLIS = 1000;
    public static final String WEEKDAY_FORMAT = "%A";
    public static final long WEEK_IN_MILLIS = 604800000;
    public static final String YEAR_FORMAT = "%Y";
    public static final String YEAR_FORMAT_TWO_DIGITS = "%g";
    public static final long YEAR_IN_MILLIS = 31449600000L;
    private static String sElapsedFormatHMMSS;
    private static String sElapsedFormatMMSS;
    private static Configuration sLastConfig;
    private static final Object sLock;
    private static Time sNowTime;
    private static Time sThenTime;
    public static final int[] sameMonthTable;
    public static final int[] sameYearTable;

    static {
        sLock = new Object();
        sameYearTable = null;
        sameMonthTable = null;
    }

    @Deprecated
    public static String getDayOfWeekString(int dayOfWeek, int abbrev) {
        String[] names;
        LocaleData d = LocaleData.get(Locale.getDefault());
        switch (abbrev) {
            case LENGTH_LONG /*10*/:
                names = d.longWeekdayNames;
                break;
            case LENGTH_MEDIUM /*20*/:
                names = d.shortWeekdayNames;
                break;
            case LENGTH_SHORT /*30*/:
                names = d.shortWeekdayNames;
                break;
            case LENGTH_SHORTER /*40*/:
                names = d.shortWeekdayNames;
                break;
            case LENGTH_SHORTEST /*50*/:
                names = d.tinyWeekdayNames;
                break;
            default:
                names = d.shortWeekdayNames;
                break;
        }
        return names[dayOfWeek];
    }

    @Deprecated
    public static String getAMPMString(int ampm) {
        return LocaleData.get(Locale.getDefault()).amPm[ampm + 0];
    }

    @Deprecated
    public static String getMonthString(int month, int abbrev) {
        String[] names;
        LocaleData d = LocaleData.get(Locale.getDefault());
        switch (abbrev) {
            case LENGTH_LONG /*10*/:
                names = d.longMonthNames;
                break;
            case LENGTH_MEDIUM /*20*/:
                names = d.shortMonthNames;
                break;
            case LENGTH_SHORT /*30*/:
                names = d.shortMonthNames;
                break;
            case LENGTH_SHORTER /*40*/:
                names = d.shortMonthNames;
                break;
            case LENGTH_SHORTEST /*50*/:
                names = d.tinyMonthNames;
                break;
            default:
                names = d.shortMonthNames;
                break;
        }
        return names[month];
    }

    public static CharSequence getRelativeTimeSpanString(long startTime) {
        return getRelativeTimeSpanString(startTime, System.currentTimeMillis(), (long) MINUTE_IN_MILLIS);
    }

    public static CharSequence getRelativeTimeSpanString(long time, long now, long minResolution) {
        return getRelativeTimeSpanString(time, now, minResolution, 65556);
    }

    public static CharSequence getRelativeTimeSpanString(long time, long now, long minResolution, int flags) {
        long count;
        int resId;
        Resources r = Resources.getSystem();
        boolean abbrevRelative = (786432 & flags) != 0;
        boolean past = now >= time;
        long duration = Math.abs(now - time);
        if (duration < MINUTE_IN_MILLIS && minResolution < MINUTE_IN_MILLIS) {
            count = duration / SECOND_IN_MILLIS;
            if (past) {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_num_seconds_ago;
                } else {
                    resId = R.plurals.num_seconds_ago;
                }
            } else if (abbrevRelative) {
                resId = R.plurals.abbrev_in_num_seconds;
            } else {
                resId = R.plurals.in_num_seconds;
            }
        } else if (duration < HOUR_IN_MILLIS && minResolution < HOUR_IN_MILLIS) {
            count = duration / MINUTE_IN_MILLIS;
            if (past) {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_num_minutes_ago;
                } else {
                    resId = R.plurals.num_minutes_ago;
                }
            } else if (abbrevRelative) {
                resId = R.plurals.abbrev_in_num_minutes;
            } else {
                resId = R.plurals.in_num_minutes;
            }
        } else if (duration < DAY_IN_MILLIS && minResolution < DAY_IN_MILLIS) {
            count = duration / HOUR_IN_MILLIS;
            if (past) {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_num_hours_ago;
                } else {
                    resId = R.plurals.num_hours_ago;
                }
            } else if (abbrevRelative) {
                resId = R.plurals.abbrev_in_num_hours;
            } else {
                resId = R.plurals.in_num_hours;
            }
        } else if (duration >= WEEK_IN_MILLIS || minResolution >= WEEK_IN_MILLIS) {
            return formatDateRange(null, time, time, flags);
        } else {
            return getRelativeDayString(r, time, now);
        }
        String format = r.getQuantityString(resId, (int) count);
        Object[] objArr = new Object[FORMAT_SHOW_TIME];
        objArr[0] = Long.valueOf(count);
        return String.format(format, objArr);
    }

    public static CharSequence getRelativeDateTimeString(Context c, long time, long minResolution, long transitionResolution, int flags) {
        Resources r = Resources.getSystem();
        long now = System.currentTimeMillis();
        long duration = Math.abs(now - time);
        if (transitionResolution > WEEK_IN_MILLIS) {
            transitionResolution = WEEK_IN_MILLIS;
        } else if (transitionResolution < DAY_IN_MILLIS) {
            transitionResolution = DAY_IN_MILLIS;
        }
        CharSequence timeClause = formatDateRange(c, time, time, FORMAT_SHOW_TIME);
        if (duration < transitionResolution) {
            Object[] objArr = new Object[FORMAT_SHOW_WEEKDAY];
            objArr[0] = getRelativeTimeSpanString(time, now, minResolution, flags);
            objArr[FORMAT_SHOW_TIME] = timeClause;
            return r.getString(R.string.relative_time, objArr);
        }
        objArr = new Object[FORMAT_SHOW_WEEKDAY];
        objArr[0] = getRelativeTimeSpanString(c, time, false);
        objArr[FORMAT_SHOW_TIME] = timeClause;
        return r.getString(R.string.date_time, objArr);
    }

    private static final String getRelativeDayString(Resources r, long day, long today) {
        Locale locale = r.getConfiguration().locale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Time startTime = new Time();
        startTime.set(day);
        int startDay = Time.getJulianDay(day, startTime.gmtoff);
        Time currentTime = new Time();
        currentTime.set(today);
        int days = Math.abs(Time.getJulianDay(today, currentTime.gmtoff) - startDay);
        boolean past = today > day;
        if (days == FORMAT_SHOW_TIME) {
            if (past) {
                return LocaleData.get(locale).yesterday;
            }
            return LocaleData.get(locale).tomorrow;
        } else if (days == 0) {
            return LocaleData.get(locale).today;
        } else {
            int resId;
            if (past) {
                resId = R.plurals.num_days_ago;
            } else {
                resId = R.plurals.in_num_days;
            }
            String format = r.getQuantityString(resId, days);
            Object[] objArr = new Object[FORMAT_SHOW_TIME];
            objArr[0] = Integer.valueOf(days);
            return String.format(format, objArr);
        }
    }

    private static void initFormatStrings() {
        synchronized (sLock) {
            initFormatStringsLocked();
        }
    }

    private static void initFormatStringsLocked() {
        Resources r = Resources.getSystem();
        Configuration cfg = r.getConfiguration();
        if (sLastConfig == null || !sLastConfig.equals(cfg)) {
            sLastConfig = cfg;
            sElapsedFormatMMSS = r.getString(R.string.elapsed_time_short_format_mm_ss);
            sElapsedFormatHMMSS = r.getString(R.string.elapsed_time_short_format_h_mm_ss);
        }
    }

    public static CharSequence formatDuration(long millis) {
        Resources res = Resources.getSystem();
        Object[] objArr;
        if (millis >= HOUR_IN_MILLIS) {
            int hours = (int) ((1800000 + millis) / HOUR_IN_MILLIS);
            objArr = new Object[FORMAT_SHOW_TIME];
            objArr[0] = Integer.valueOf(hours);
            return res.getQuantityString(R.plurals.duration_hours, hours, objArr);
        } else if (millis >= MINUTE_IN_MILLIS) {
            int minutes = (int) ((LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + millis) / MINUTE_IN_MILLIS);
            objArr = new Object[FORMAT_SHOW_TIME];
            objArr[0] = Integer.valueOf(minutes);
            return res.getQuantityString(R.plurals.duration_minutes, minutes, objArr);
        } else {
            int seconds = (int) ((500 + millis) / SECOND_IN_MILLIS);
            objArr = new Object[FORMAT_SHOW_TIME];
            objArr[0] = Integer.valueOf(seconds);
            return res.getQuantityString(R.plurals.duration_seconds, seconds, objArr);
        }
    }

    public static String formatElapsedTime(long elapsedSeconds) {
        return formatElapsedTime(null, elapsedSeconds);
    }

    public static String formatElapsedTime(StringBuilder recycle, long elapsedSeconds) {
        long hours = 0;
        long minutes = 0;
        if (elapsedSeconds >= 3600) {
            hours = elapsedSeconds / 3600;
            elapsedSeconds -= 3600 * hours;
        }
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= 60 * minutes;
        }
        long seconds = elapsedSeconds;
        StringBuilder sb = recycle;
        if (sb == null) {
            sb = new StringBuilder(FORMAT_NO_YEAR);
        } else {
            sb.setLength(0);
        }
        Formatter f = new Formatter(sb, Locale.getDefault());
        initFormatStrings();
        if (hours > 0) {
            return f.format(sElapsedFormatHMMSS, new Object[]{Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)}).toString();
        }
        String str = sElapsedFormatMMSS;
        Object[] objArr = new Object[FORMAT_SHOW_WEEKDAY];
        objArr[0] = Long.valueOf(minutes);
        objArr[FORMAT_SHOW_TIME] = Long.valueOf(seconds);
        return f.format(str, objArr).toString();
    }

    public static final CharSequence formatSameDayTime(long then, long now, int dateStyle, int timeStyle) {
        DateFormat f;
        Calendar thenCal = new GregorianCalendar();
        thenCal.setTimeInMillis(then);
        Date thenDate = thenCal.getTime();
        Calendar nowCal = new GregorianCalendar();
        nowCal.setTimeInMillis(now);
        if (thenCal.get(FORMAT_SHOW_TIME) == nowCal.get(FORMAT_SHOW_TIME) && thenCal.get(FORMAT_SHOW_WEEKDAY) == nowCal.get(FORMAT_SHOW_WEEKDAY) && thenCal.get(5) == nowCal.get(5)) {
            f = DateFormat.getTimeInstance(timeStyle);
        } else {
            f = DateFormat.getDateInstance(dateStyle);
        }
        return f.format(thenDate);
    }

    public static boolean isToday(long when) {
        Time time = new Time();
        time.set(when);
        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;
        time.set(System.currentTimeMillis());
        return thenYear == time.year && thenMonth == time.month && thenMonthDay == time.monthDay;
    }

    public static String formatDateRange(Context context, long startMillis, long endMillis, int flags) {
        return formatDateRange(context, new Formatter(new StringBuilder(LENGTH_SHORTEST), Locale.getDefault()), startMillis, endMillis, flags).toString();
    }

    public static Formatter formatDateRange(Context context, Formatter formatter, long startMillis, long endMillis, int flags) {
        return formatDateRange(context, formatter, startMillis, endMillis, flags, null);
    }

    public static Formatter formatDateRange(Context context, Formatter formatter, long startMillis, long endMillis, int flags, String timeZone) {
        if ((flags & R.styleable.Theme_listPreferredItemHeightLarge) == FORMAT_SHOW_TIME) {
            flags |= DateFormat.is24HourFormat(context) ? FORMAT_24HOUR : FORMAT_12HOUR;
        }
        try {
            formatter.out().append(DateIntervalFormat.formatDateRange(startMillis, endMillis, flags, timeZone));
            return formatter;
        } catch (IOException impossible) {
            throw new AssertionError(impossible);
        }
    }

    public static String formatDateTime(Context context, long millis, int flags) {
        return formatDateRange(context, millis, millis, flags);
    }

    public static CharSequence getRelativeTimeSpanString(Context c, long millis, boolean withPreposition) {
        String result;
        long now = System.currentTimeMillis();
        long span = Math.abs(now - millis);
        synchronized (DateUtils.class) {
            int prepositionId;
            if (sNowTime == null) {
                sNowTime = new Time();
            }
            if (sThenTime == null) {
                sThenTime = new Time();
            }
            sNowTime.set(now);
            sThenTime.set(millis);
            if (span < DAY_IN_MILLIS && sNowTime.weekDay == sThenTime.weekDay) {
                result = formatDateRange(c, millis, millis, FORMAT_SHOW_TIME);
                prepositionId = R.string.preposition_for_time;
            } else if (sNowTime.year != sThenTime.year) {
                result = formatDateRange(c, millis, millis, 131092);
                prepositionId = R.string.preposition_for_date;
            } else {
                result = formatDateRange(c, millis, millis, 65552);
                prepositionId = R.string.preposition_for_date;
            }
            if (withPreposition) {
                Resources res = c.getResources();
                Object[] objArr = new Object[FORMAT_SHOW_TIME];
                objArr[0] = result;
                result = res.getString(prepositionId, objArr);
            }
        }
        return result;
    }

    public static CharSequence getRelativeTimeSpanString(Context c, long millis) {
        return getRelativeTimeSpanString(c, millis, false);
    }
}
