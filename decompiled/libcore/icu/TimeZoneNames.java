package libcore.icu;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import libcore.util.BasicLruCache;
import libcore.util.ZoneInfoDB;

public final class TimeZoneNames {
    public static final int LONG_NAME = 1;
    public static final int LONG_NAME_DST = 3;
    public static final int NAME_COUNT = 5;
    public static final int OLSON_NAME = 0;
    public static final int SHORT_NAME = 2;
    public static final int SHORT_NAME_DST = 4;
    private static final Comparator<String[]> ZONE_STRINGS_COMPARATOR;
    private static final String[] availableTimeZoneIds;
    private static final ZoneStringsCache cachedZoneStrings;

    public static class ZoneStringsCache extends BasicLruCache<Locale, String[][]> {
        public ZoneStringsCache() {
            super(TimeZoneNames.NAME_COUNT);
        }

        protected String[][] create(Locale locale) {
            long start = System.currentTimeMillis();
            String[][] result = (String[][]) Array.newInstance(String.class, TimeZoneNames.availableTimeZoneIds.length, TimeZoneNames.NAME_COUNT);
            int i = TimeZoneNames.OLSON_NAME;
            while (true) {
                int length = TimeZoneNames.availableTimeZoneIds.length;
                if (i < r0) {
                    result[i][TimeZoneNames.OLSON_NAME] = TimeZoneNames.availableTimeZoneIds[i];
                    i += TimeZoneNames.LONG_NAME;
                } else {
                    long nativeStart = System.currentTimeMillis();
                    TimeZoneNames.fillZoneStrings(locale.toString(), result);
                    long nativeEnd = System.currentTimeMillis();
                    internStrings(result);
                    long nativeDuration = nativeEnd - nativeStart;
                    long duration = System.currentTimeMillis() - start;
                    System.logI("Loaded time zone names for \"" + locale + "\" in " + duration + "ms" + " (" + nativeDuration + "ms in ICU)");
                    return result;
                }
            }
        }

        private synchronized void internStrings(String[][] result) {
            HashMap<String, String> internTable = new HashMap();
            for (int i = TimeZoneNames.OLSON_NAME; i < result.length; i += TimeZoneNames.LONG_NAME) {
                for (int j = TimeZoneNames.LONG_NAME; j < TimeZoneNames.NAME_COUNT; j += TimeZoneNames.LONG_NAME) {
                    String original = result[i][j];
                    String nonDuplicate = (String) internTable.get(original);
                    if (nonDuplicate == null) {
                        internTable.put(original, original);
                    } else {
                        result[i][j] = nonDuplicate;
                    }
                }
            }
        }
    }

    private static native void fillZoneStrings(String str, String[][] strArr);

    public static native String getExemplarLocation(String str, String str2);

    static {
        availableTimeZoneIds = TimeZone.getAvailableIDs();
        cachedZoneStrings = new ZoneStringsCache();
        cachedZoneStrings.get(Locale.ROOT);
        cachedZoneStrings.get(Locale.US);
        cachedZoneStrings.get(Locale.getDefault());
        ZONE_STRINGS_COMPARATOR = new Comparator<String[]>() {
            public int compare(String[] lhs, String[] rhs) {
                return lhs[TimeZoneNames.OLSON_NAME].compareTo(rhs[TimeZoneNames.OLSON_NAME]);
            }
        };
    }

    private TimeZoneNames() {
    }

    public static String getDisplayName(String[][] zoneStrings, String id, boolean daylight, int style) {
        String[] needle = new String[LONG_NAME];
        needle[OLSON_NAME] = id;
        int index = Arrays.binarySearch(zoneStrings, needle, ZONE_STRINGS_COMPARATOR);
        if (index < 0) {
            return null;
        }
        String[] row = zoneStrings[index];
        return daylight ? style == LONG_NAME ? row[LONG_NAME_DST] : row[SHORT_NAME_DST] : style == LONG_NAME ? row[LONG_NAME] : row[SHORT_NAME];
    }

    public static String[][] getZoneStrings(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return (String[][]) cachedZoneStrings.get(locale);
    }

    public static String[] forLocale(Locale locale) {
        String countryCode = locale.getCountry();
        ArrayList<String> ids = new ArrayList();
        String[] arr$ = ZoneInfoDB.getInstance().getZoneTab().split("\n");
        int len$ = arr$.length;
        for (int i$ = OLSON_NAME; i$ < len$; i$ += LONG_NAME) {
            String line = arr$[i$];
            if (line.startsWith(countryCode)) {
                int olsonIdStart = line.indexOf(9, (int) SHORT_NAME_DST) + LONG_NAME;
                int olsonIdEnd = line.indexOf(9, olsonIdStart);
                if (olsonIdEnd == -1) {
                    olsonIdEnd = line.length();
                }
                ids.add(line.substring(olsonIdStart, olsonIdEnd));
            }
        }
        return (String[]) ids.toArray(new String[ids.size()]);
    }
}
