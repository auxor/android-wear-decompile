package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.net.HttpURLConnection;

public class SimpleTimeZone extends TimeZone {
    private static final int DOM_MODE = 1;
    private static final int DOW_GE_DOM_MODE = 3;
    private static final int DOW_IN_MONTH_MODE = 2;
    private static final int DOW_LE_DOM_MODE = 4;
    public static final int STANDARD_TIME = 1;
    public static final int UTC_TIME = 2;
    public static final int WALL_TIME = 0;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = -403250971215465050L;
    private int dstSavings;
    private int endDay;
    private int endDayOfWeek;
    private int endMode;
    private int endMonth;
    private int endTime;
    private int rawOffset;
    private int startDay;
    private int startDayOfWeek;
    private int startMode;
    private int startMonth;
    private int startTime;
    private int startYear;
    private boolean useDaylight;

    public SimpleTimeZone(int offset, String name) {
        this.dstSavings = Grego.MILLIS_PER_HOUR;
        setID(name);
        this.rawOffset = offset;
    }

    public SimpleTimeZone(int offset, String name, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime) {
        this(offset, name, startMonth, startDay, startDayOfWeek, startTime, endMonth, endDay, endDayOfWeek, endTime, Grego.MILLIS_PER_HOUR);
    }

    public SimpleTimeZone(int offset, String name, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime, int daylightSavings) {
        this(offset, name);
        if (daylightSavings <= 0) {
            throw new IllegalArgumentException("Invalid daylightSavings: " + daylightSavings);
        }
        this.dstSavings = daylightSavings;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.startDayOfWeek = startDayOfWeek;
        this.startTime = startTime;
        setStartMode();
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.endDayOfWeek = endDayOfWeek;
        this.endTime = endTime;
        setEndMode();
    }

    public SimpleTimeZone(int offset, String name, int startMonth, int startDay, int startDayOfWeek, int startTime, int startTimeMode, int endMonth, int endDay, int endDayOfWeek, int endTime, int endTimeMode, int daylightSavings) {
        this(offset, name, startMonth, startDay, startDayOfWeek, startTime, endMonth, endDay, endDayOfWeek, endTime, daylightSavings);
        this.startMode = startTimeMode;
        this.endMode = endTimeMode;
    }

    public Object clone() {
        return (SimpleTimeZone) super.clone();
    }

    public boolean equals(Object object) {
        if (!(object instanceof SimpleTimeZone)) {
            return false;
        }
        SimpleTimeZone tz = (SimpleTimeZone) object;
        if (!getID().equals(tz.getID()) || this.rawOffset != tz.rawOffset || this.useDaylight != tz.useDaylight) {
            return false;
        }
        if (!this.useDaylight || (this.startYear == tz.startYear && this.startMonth == tz.startMonth && this.startDay == tz.startDay && this.startMode == tz.startMode && this.startDayOfWeek == tz.startDayOfWeek && this.startTime == tz.startTime && this.endMonth == tz.endMonth && this.endDay == tz.endDay && this.endDayOfWeek == tz.endDayOfWeek && this.endTime == tz.endTime && this.endMode == tz.endMode && this.dstSavings == tz.dstSavings)) {
            return true;
        }
        return false;
    }

    public int getDSTSavings() {
        if (this.useDaylight) {
            return this.dstSavings;
        }
        return WALL_TIME;
    }

    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int time) {
        if (era == 0 || era == STANDARD_TIME) {
            checkRange(month, dayOfWeek, time);
            if (!(month == STANDARD_TIME && day == 29 && isLeapYear(year))) {
                checkDay(month, day);
            }
            if (!useDaylightTime() || era != STANDARD_TIME || year < this.startYear) {
                return this.rawOffset;
            }
            int daysInMonth;
            if (this.endMonth < this.startMonth) {
                if (month > this.endMonth && month < this.startMonth) {
                    return this.rawOffset;
                }
            } else if (month < this.startMonth || month > this.endMonth) {
                return this.rawOffset;
            }
            int ruleDay = WALL_TIME;
            int firstDayOfMonth = mod7(dayOfWeek - day);
            if (month == this.startMonth) {
                switch (this.startMode) {
                    case STANDARD_TIME /*1*/:
                        ruleDay = this.startDay;
                        break;
                    case UTC_TIME /*2*/:
                        if (this.startDay < 0) {
                            daysInMonth = GregorianCalendar.DaysInMonth[this.startMonth];
                            if (this.startMonth == STANDARD_TIME && isLeapYear(year)) {
                                daysInMonth += STANDARD_TIME;
                            }
                            ruleDay = ((daysInMonth + STANDARD_TIME) + mod7(this.startDayOfWeek - (firstDayOfMonth + daysInMonth))) + (this.startDay * 7);
                            break;
                        }
                        ruleDay = (mod7(this.startDayOfWeek - firstDayOfMonth) + STANDARD_TIME) + ((this.startDay - 1) * 7);
                        break;
                    case DOW_GE_DOM_MODE /*3*/:
                        ruleDay = this.startDay + mod7(this.startDayOfWeek - ((this.startDay + firstDayOfMonth) - 1));
                        break;
                    case DOW_LE_DOM_MODE /*4*/:
                        ruleDay = this.startDay + mod7(this.startDayOfWeek - ((this.startDay + firstDayOfMonth) - 1));
                        if (ruleDay != this.startDay) {
                            ruleDay -= 7;
                            break;
                        }
                        break;
                }
                if (ruleDay > day || (ruleDay == day && time < this.startTime)) {
                    return this.rawOffset;
                }
            }
            int ruleTime = this.endTime - this.dstSavings;
            int nextMonth = (month + STANDARD_TIME) % 12;
            if (month == this.endMonth || (ruleTime < 0 && nextMonth == this.endMonth)) {
                switch (this.endMode) {
                    case STANDARD_TIME /*1*/:
                        ruleDay = this.endDay;
                        break;
                    case UTC_TIME /*2*/:
                        if (this.endDay < 0) {
                            daysInMonth = GregorianCalendar.DaysInMonth[this.endMonth];
                            if (this.endMonth == STANDARD_TIME && isLeapYear(year)) {
                                daysInMonth += STANDARD_TIME;
                            }
                            ruleDay = ((daysInMonth + STANDARD_TIME) + mod7(this.endDayOfWeek - (firstDayOfMonth + daysInMonth))) + (this.endDay * 7);
                            break;
                        }
                        ruleDay = (mod7(this.endDayOfWeek - firstDayOfMonth) + STANDARD_TIME) + ((this.endDay - 1) * 7);
                        break;
                    case DOW_GE_DOM_MODE /*3*/:
                        ruleDay = this.endDay + mod7(this.endDayOfWeek - ((this.endDay + firstDayOfMonth) - 1));
                        break;
                    case DOW_LE_DOM_MODE /*4*/:
                        ruleDay = this.endDay + mod7(this.endDayOfWeek - ((this.endDay + firstDayOfMonth) - 1));
                        if (ruleDay != this.endDay) {
                            ruleDay -= 7;
                            break;
                        }
                        break;
                }
                int ruleMonth = this.endMonth;
                if (ruleTime < 0) {
                    int changeDays = 1 - (ruleTime / Grego.MILLIS_PER_DAY);
                    ruleTime = (ruleTime % Grego.MILLIS_PER_DAY) + Grego.MILLIS_PER_DAY;
                    ruleDay -= changeDays;
                    if (ruleDay <= 0) {
                        ruleMonth--;
                        if (ruleMonth < 0) {
                            ruleMonth = 11;
                        }
                        ruleDay += GregorianCalendar.DaysInMonth[ruleMonth];
                        if (ruleMonth == STANDARD_TIME && isLeapYear(year)) {
                            ruleDay += STANDARD_TIME;
                        }
                    }
                }
                if (month == ruleMonth) {
                    if (ruleDay < day || (ruleDay == day && time >= ruleTime)) {
                        return this.rawOffset;
                    }
                } else if (nextMonth != ruleMonth) {
                    return this.rawOffset;
                }
            }
            return this.rawOffset + this.dstSavings;
        }
        throw new IllegalArgumentException("Invalid era: " + era);
    }

    public int getOffset(long time) {
        if (!useDaylightTime()) {
            return this.rawOffset;
        }
        int[] fields = Grego.timeToFields(((long) this.rawOffset) + time, null);
        return getOffset(STANDARD_TIME, fields[WALL_TIME], fields[STANDARD_TIME], fields[UTC_TIME], fields[DOW_GE_DOM_MODE], fields[5]);
    }

    public int getRawOffset() {
        return this.rawOffset;
    }

    public synchronized int hashCode() {
        int hashCode;
        hashCode = getID().hashCode() + this.rawOffset;
        if (this.useDaylight) {
            hashCode += ((((((((((this.startYear + this.startMonth) + this.startDay) + this.startDayOfWeek) + this.startTime) + this.startMode) + this.endMonth) + this.endDay) + this.endDayOfWeek) + this.endTime) + this.endMode) + this.dstSavings;
        }
        return hashCode;
    }

    public boolean hasSameRules(TimeZone zone) {
        boolean z = true;
        if (!(zone instanceof SimpleTimeZone)) {
            return false;
        }
        SimpleTimeZone tz = (SimpleTimeZone) zone;
        if (this.useDaylight != tz.useDaylight) {
            return false;
        }
        if (this.useDaylight) {
            if (!(this.rawOffset == tz.rawOffset && this.dstSavings == tz.dstSavings && this.startYear == tz.startYear && this.startMonth == tz.startMonth && this.startDay == tz.startDay && this.startMode == tz.startMode && this.startDayOfWeek == tz.startDayOfWeek && this.startTime == tz.startTime && this.endMonth == tz.endMonth && this.endDay == tz.endDay && this.endDayOfWeek == tz.endDayOfWeek && this.endTime == tz.endTime && this.endMode == tz.endMode)) {
                z = false;
            }
            return z;
        }
        if (this.rawOffset != tz.rawOffset) {
            z = false;
        }
        return z;
    }

    public boolean inDaylightTime(Date time) {
        return useDaylightTime() && getOffset(time.getTime()) != getRawOffset();
    }

    private boolean isLeapYear(int year) {
        if (year > 1582) {
            if (year % DOW_LE_DOM_MODE != 0 || (year % 100 == 0 && year % HttpURLConnection.HTTP_BAD_REQUEST != 0)) {
                return false;
            }
            return true;
        } else if (year % DOW_LE_DOM_MODE != 0) {
            return false;
        } else {
            return true;
        }
    }

    private int mod7(int num1) {
        int rem = num1 % 7;
        return (num1 >= 0 || rem >= 0) ? rem : rem + 7;
    }

    public void setDSTSavings(int milliseconds) {
        if (milliseconds <= 0) {
            throw new IllegalArgumentException("milliseconds <= 0: " + milliseconds);
        }
        this.dstSavings = milliseconds;
    }

    private void checkRange(int month, int dayOfWeek, int time) {
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("Invalid month: " + month);
        } else if (dayOfWeek < STANDARD_TIME || dayOfWeek > 7) {
            throw new IllegalArgumentException("Invalid day of week: " + dayOfWeek);
        } else if (time < 0 || time >= Grego.MILLIS_PER_DAY) {
            throw new IllegalArgumentException("Invalid time: " + time);
        }
    }

    private void checkDay(int month, int day) {
        if (day <= 0 || day > GregorianCalendar.DaysInMonth[month]) {
            throw new IllegalArgumentException("Invalid day of month: " + day);
        }
    }

    private void setEndMode() {
        if (this.endDayOfWeek == 0) {
            this.endMode = STANDARD_TIME;
        } else if (this.endDayOfWeek < 0) {
            this.endDayOfWeek = -this.endDayOfWeek;
            if (this.endDay < 0) {
                this.endDay = -this.endDay;
                this.endMode = DOW_LE_DOM_MODE;
            } else {
                this.endMode = DOW_GE_DOM_MODE;
            }
        } else {
            this.endMode = UTC_TIME;
        }
        boolean z = (this.startDay == 0 || this.endDay == 0) ? false : true;
        this.useDaylight = z;
        if (this.endDay != 0) {
            checkRange(this.endMonth, this.endMode == STANDARD_TIME ? STANDARD_TIME : this.endDayOfWeek, this.endTime);
            if (this.endMode != UTC_TIME) {
                checkDay(this.endMonth, this.endDay);
            } else if (this.endDay < -5 || this.endDay > 5) {
                throw new IllegalArgumentException("Day of week in month: " + this.endDay);
            }
        }
        if (this.endMode != STANDARD_TIME) {
            this.endDayOfWeek--;
        }
    }

    public void setEndRule(int month, int dayOfMonth, int time) {
        this.endMonth = month;
        this.endDay = dayOfMonth;
        this.endDayOfWeek = WALL_TIME;
        this.endTime = time;
        setEndMode();
    }

    public void setEndRule(int month, int day, int dayOfWeek, int time) {
        this.endMonth = month;
        this.endDay = day;
        this.endDayOfWeek = dayOfWeek;
        this.endTime = time;
        setEndMode();
    }

    public void setEndRule(int month, int day, int dayOfWeek, int time, boolean after) {
        this.endMonth = month;
        if (!after) {
            day = -day;
        }
        this.endDay = day;
        this.endDayOfWeek = -dayOfWeek;
        this.endTime = time;
        setEndMode();
    }

    public void setRawOffset(int offset) {
        this.rawOffset = offset;
    }

    private void setStartMode() {
        if (this.startDayOfWeek == 0) {
            this.startMode = STANDARD_TIME;
        } else if (this.startDayOfWeek < 0) {
            this.startDayOfWeek = -this.startDayOfWeek;
            if (this.startDay < 0) {
                this.startDay = -this.startDay;
                this.startMode = DOW_LE_DOM_MODE;
            } else {
                this.startMode = DOW_GE_DOM_MODE;
            }
        } else {
            this.startMode = UTC_TIME;
        }
        boolean z = (this.startDay == 0 || this.endDay == 0) ? false : true;
        this.useDaylight = z;
        if (this.startDay != 0) {
            checkRange(this.startMonth, this.startMode == STANDARD_TIME ? STANDARD_TIME : this.startDayOfWeek, this.startTime);
            if (this.startMode != UTC_TIME) {
                checkDay(this.startMonth, this.startDay);
            } else if (this.startDay < -5 || this.startDay > 5) {
                throw new IllegalArgumentException("Day of week in month: " + this.startDay);
            }
        }
        if (this.startMode != STANDARD_TIME) {
            this.startDayOfWeek--;
        }
    }

    public void setStartRule(int month, int dayOfMonth, int time) {
        this.startMonth = month;
        this.startDay = dayOfMonth;
        this.startDayOfWeek = WALL_TIME;
        this.startTime = time;
        setStartMode();
    }

    public void setStartRule(int month, int day, int dayOfWeek, int time) {
        this.startMonth = month;
        this.startDay = day;
        this.startDayOfWeek = dayOfWeek;
        this.startTime = time;
        setStartMode();
    }

    public void setStartRule(int month, int day, int dayOfWeek, int time, boolean after) {
        this.startMonth = month;
        if (!after) {
            day = -day;
        }
        this.startDay = day;
        this.startDayOfWeek = -dayOfWeek;
        this.startTime = time;
        setStartMode();
    }

    public void setStartYear(int year) {
        this.startYear = year;
        this.useDaylight = true;
    }

    public String toString() {
        int i = WALL_TIME;
        StringBuilder append = new StringBuilder().append(getClass().getName()).append("[id=").append(getID()).append(",offset=").append(this.rawOffset).append(",dstSavings=").append(this.dstSavings).append(",useDaylight=").append(this.useDaylight).append(",startYear=").append(this.startYear).append(",startMode=").append(this.startMode).append(",startMonth=").append(this.startMonth).append(",startDay=").append(this.startDay).append(",startDayOfWeek=");
        int i2 = (!this.useDaylight || this.startMode == STANDARD_TIME) ? WALL_TIME : this.startDayOfWeek + STANDARD_TIME;
        StringBuilder append2 = append.append(i2).append(",startTime=").append(this.startTime).append(",endMode=").append(this.endMode).append(",endMonth=").append(this.endMonth).append(",endDay=").append(this.endDay).append(",endDayOfWeek=");
        if (this.useDaylight && this.endMode != STANDARD_TIME) {
            i = this.endDayOfWeek + STANDARD_TIME;
        }
        return append2.append(i).append(",endTime=").append(this.endTime).append("]").toString();
    }

    public boolean useDaylightTime() {
        return this.useDaylight;
    }

    static {
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("dstSavings", Integer.TYPE), new ObjectStreamField("endDay", Integer.TYPE), new ObjectStreamField("endDayOfWeek", Integer.TYPE), new ObjectStreamField("endMode", Integer.TYPE), new ObjectStreamField("endMonth", Integer.TYPE), new ObjectStreamField("endTime", Integer.TYPE), new ObjectStreamField("monthLength", byte[].class), new ObjectStreamField("rawOffset", Integer.TYPE), new ObjectStreamField("serialVersionOnStream", Integer.TYPE), new ObjectStreamField("startDay", Integer.TYPE), new ObjectStreamField("startDayOfWeek", Integer.TYPE), new ObjectStreamField("startMode", Integer.TYPE), new ObjectStreamField("startMonth", Integer.TYPE), new ObjectStreamField("startTime", Integer.TYPE), new ObjectStreamField("startYear", Integer.TYPE), new ObjectStreamField("useDaylight", Boolean.TYPE)};
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        int i = WALL_TIME;
        int sEndDay = this.endDay;
        int sEndDayOfWeek = this.endDayOfWeek + STANDARD_TIME;
        int sStartDay = this.startDay;
        int sStartDayOfWeek = this.startDayOfWeek + STANDARD_TIME;
        if (this.useDaylight && !(this.startMode == UTC_TIME && this.endMode == UTC_TIME)) {
            Calendar cal = new GregorianCalendar((TimeZone) this);
            if (this.endMode != UTC_TIME) {
                cal.set(UTC_TIME, this.endMonth);
                cal.set(5, this.endDay);
                sEndDay = cal.get(8);
                if (this.endMode == STANDARD_TIME) {
                    sEndDayOfWeek = cal.getFirstDayOfWeek();
                }
            }
            if (this.startMode != UTC_TIME) {
                cal.set(UTC_TIME, this.startMonth);
                cal.set(5, this.startDay);
                sStartDay = cal.get(8);
                if (this.startMode == STANDARD_TIME) {
                    sStartDayOfWeek = cal.getFirstDayOfWeek();
                }
            }
        }
        PutField fields = stream.putFields();
        fields.put("dstSavings", this.dstSavings);
        fields.put("endDay", sEndDay);
        fields.put("endDayOfWeek", sEndDayOfWeek);
        fields.put("endMode", this.endMode);
        fields.put("endMonth", this.endMonth);
        fields.put("endTime", this.endTime);
        fields.put("monthLength", GregorianCalendar.DaysInMonth);
        fields.put("rawOffset", this.rawOffset);
        fields.put("serialVersionOnStream", (int) STANDARD_TIME);
        fields.put("startDay", sStartDay);
        fields.put("startDayOfWeek", sStartDayOfWeek);
        fields.put("startMode", this.startMode);
        fields.put("startMonth", this.startMonth);
        fields.put("startTime", this.startTime);
        fields.put("startYear", this.startYear);
        fields.put("useDaylight", this.useDaylight);
        stream.writeFields();
        stream.writeInt(DOW_LE_DOM_MODE);
        byte[] values = new byte[DOW_LE_DOM_MODE];
        values[WALL_TIME] = (byte) this.startDay;
        values[STANDARD_TIME] = (byte) (this.startMode == STANDARD_TIME ? WALL_TIME : this.startDayOfWeek + STANDARD_TIME);
        values[UTC_TIME] = (byte) this.endDay;
        if (this.endMode != STANDARD_TIME) {
            i = this.endDayOfWeek + STANDARD_TIME;
        }
        values[DOW_GE_DOM_MODE] = (byte) i;
        stream.write(values);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        this.rawOffset = fields.get("rawOffset", (int) WALL_TIME);
        this.useDaylight = fields.get("useDaylight", false);
        if (this.useDaylight) {
            this.endMonth = fields.get("endMonth", (int) WALL_TIME);
            this.endTime = fields.get("endTime", (int) WALL_TIME);
            this.startMonth = fields.get("startMonth", (int) WALL_TIME);
            this.startTime = fields.get("startTime", (int) WALL_TIME);
            this.startYear = fields.get("startYear", (int) WALL_TIME);
        }
        if (fields.get("serialVersionOnStream", (int) WALL_TIME) != 0) {
            this.dstSavings = fields.get("dstSavings", (int) WALL_TIME);
            if (this.useDaylight) {
                this.endMode = fields.get("endMode", (int) WALL_TIME);
                this.startMode = fields.get("startMode", (int) WALL_TIME);
                int length = stream.readInt();
                byte[] values = new byte[length];
                stream.readFully(values);
                if (length >= DOW_LE_DOM_MODE) {
                    this.startDay = values[WALL_TIME];
                    this.startDayOfWeek = values[STANDARD_TIME];
                    if (this.startMode != STANDARD_TIME) {
                        this.startDayOfWeek--;
                    }
                    this.endDay = values[UTC_TIME];
                    this.endDayOfWeek = values[DOW_GE_DOM_MODE];
                    if (this.endMode != STANDARD_TIME) {
                        this.endDayOfWeek--;
                    }
                }
            }
        } else if (this.useDaylight) {
            this.endMode = UTC_TIME;
            this.startMode = UTC_TIME;
            this.endDay = fields.get("endDay", (int) WALL_TIME);
            this.endDayOfWeek = fields.get("endDayOfWeek", (int) WALL_TIME) - 1;
            this.startDay = fields.get("startDay", (int) WALL_TIME);
            this.startDayOfWeek = fields.get("startDayOfWeek", (int) WALL_TIME) - 1;
        }
    }
}
