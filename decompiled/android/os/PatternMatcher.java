package android.os;

import android.os.Parcelable.Creator;

public class PatternMatcher implements Parcelable {
    public static final Creator<PatternMatcher> CREATOR;
    public static final int PATTERN_LITERAL = 0;
    public static final int PATTERN_PREFIX = 1;
    public static final int PATTERN_SIMPLE_GLOB = 2;
    private final String mPattern;
    private final int mType;

    public PatternMatcher(String pattern, int type) {
        this.mPattern = pattern;
        this.mType = type;
    }

    public final String getPath() {
        return this.mPattern;
    }

    public final int getType() {
        return this.mType;
    }

    public boolean match(String str) {
        return matchPattern(this.mPattern, str, this.mType);
    }

    public String toString() {
        String type = "? ";
        switch (this.mType) {
            case PATTERN_LITERAL /*0*/:
                type = "LITERAL: ";
                break;
            case PATTERN_PREFIX /*1*/:
                type = "PREFIX: ";
                break;
            case PATTERN_SIMPLE_GLOB /*2*/:
                type = "GLOB: ";
                break;
        }
        return "PatternMatcher{" + type + this.mPattern + "}";
    }

    public int describeContents() {
        return PATTERN_LITERAL;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPattern);
        dest.writeInt(this.mType);
    }

    public PatternMatcher(Parcel src) {
        this.mPattern = src.readString();
        this.mType = src.readInt();
    }

    static {
        CREATOR = new Creator<PatternMatcher>() {
            public PatternMatcher createFromParcel(Parcel source) {
                return new PatternMatcher(source);
            }

            public PatternMatcher[] newArray(int size) {
                return new PatternMatcher[size];
            }
        };
    }

    static boolean matchPattern(String pattern, String match, int type) {
        boolean z = true;
        if (match == null) {
            return false;
        }
        if (type == 0) {
            return pattern.equals(match);
        }
        if (type == PATTERN_PREFIX) {
            return match.startsWith(pattern);
        }
        if (type != PATTERN_SIMPLE_GLOB) {
            return false;
        }
        int NP = pattern.length();
        if (NP <= 0) {
            if (match.length() > 0) {
                z = false;
            }
            return z;
        }
        int NM = match.length();
        int ip = PATTERN_LITERAL;
        int im = PATTERN_LITERAL;
        char nextChar = pattern.charAt(PATTERN_LITERAL);
        while (ip < NP && im < NM) {
            boolean escaped;
            char c = nextChar;
            ip += PATTERN_PREFIX;
            if (ip < NP) {
                nextChar = pattern.charAt(ip);
            } else {
                nextChar = '\u0000';
            }
            if (c == '\\') {
                escaped = true;
            } else {
                escaped = false;
            }
            if (escaped) {
                c = nextChar;
                ip += PATTERN_PREFIX;
                if (ip < NP) {
                    nextChar = pattern.charAt(ip);
                } else {
                    nextChar = '\u0000';
                }
            }
            if (nextChar == '*') {
                if (escaped || c != '.') {
                    while (match.charAt(im) == c) {
                        im += PATTERN_PREFIX;
                        if (im >= NM) {
                            break;
                        }
                    }
                    ip += PATTERN_PREFIX;
                    if (ip < NP) {
                        nextChar = pattern.charAt(ip);
                    } else {
                        nextChar = '\u0000';
                    }
                } else if (ip >= NP - 1) {
                    return true;
                } else {
                    ip += PATTERN_PREFIX;
                    nextChar = pattern.charAt(ip);
                    if (nextChar == '\\') {
                        ip += PATTERN_PREFIX;
                        nextChar = ip < NP ? pattern.charAt(ip) : '\u0000';
                    }
                    while (match.charAt(im) != nextChar) {
                        im += PATTERN_PREFIX;
                        if (im >= NM) {
                            break;
                        }
                    }
                    if (im == NM) {
                        return false;
                    }
                    ip += PATTERN_PREFIX;
                    if (ip < NP) {
                        nextChar = pattern.charAt(ip);
                    } else {
                        nextChar = '\u0000';
                    }
                    im += PATTERN_PREFIX;
                }
            } else if (c != '.' && match.charAt(im) != c) {
                return false;
            } else {
                im += PATTERN_PREFIX;
            }
        }
        if (ip >= NP && im >= NM) {
            return true;
        }
        if (ip == NP - 2 && pattern.charAt(ip) == '.' && pattern.charAt(ip + PATTERN_PREFIX) == '*') {
            return true;
        }
        return false;
    }
}
