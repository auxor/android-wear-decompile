package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.telephony.PhoneNumberUtils;
import android.view.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;

public class VerifierDeviceIdentity implements Parcelable {
    public static final Creator<VerifierDeviceIdentity> CREATOR;
    private static final char[] ENCODE;
    private static final int GROUP_SIZE = 4;
    private static final int LONG_SIZE = 13;
    private static final char SEPARATOR = '-';
    private final long mIdentity;
    private final String mIdentityString;

    public VerifierDeviceIdentity(long identity) {
        this.mIdentity = identity;
        this.mIdentityString = encodeBase32(identity);
    }

    private VerifierDeviceIdentity(Parcel source) {
        long identity = source.readLong();
        this.mIdentity = identity;
        this.mIdentityString = encodeBase32(identity);
    }

    public static VerifierDeviceIdentity generate() {
        return generate(new SecureRandom());
    }

    static VerifierDeviceIdentity generate(Random rng) {
        return new VerifierDeviceIdentity(rng.nextLong());
    }

    static {
        ENCODE = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', PhoneNumberUtils.WILD, 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7'};
        CREATOR = new Creator<VerifierDeviceIdentity>() {
            public VerifierDeviceIdentity createFromParcel(Parcel source) {
                return new VerifierDeviceIdentity(null);
            }

            public VerifierDeviceIdentity[] newArray(int size) {
                return new VerifierDeviceIdentity[size];
            }
        };
    }

    private static final String encodeBase32(long input) {
        char[] alphabet = ENCODE;
        char[] encoded = new char[16];
        int index = encoded.length;
        int i = 0;
        while (i < LONG_SIZE) {
            if (i > 0 && i % GROUP_SIZE == 1) {
                index--;
                encoded[index] = SEPARATOR;
            }
            input >>>= 5;
            index--;
            encoded[index] = alphabet[(int) (31 & input)];
            i++;
        }
        return String.valueOf(encoded);
    }

    private static final long decodeBase32(byte[] input) throws IllegalArgumentException {
        long output = 0;
        int numParsed = 0;
        for (int group : input) {
            int value;
            if (65 <= group && group <= 90) {
                value = group - 65;
            } else if (50 <= group && group <= 55) {
                value = group - 24;
            } else if (group == 45) {
                continue;
            } else if (97 <= group && group <= KeyEvent.KEYCODE_MOVE_HOME) {
                value = group - 97;
            } else if (group == 48) {
                value = 14;
            } else if (group == 49) {
                value = 8;
            } else {
                throw new IllegalArgumentException("base base-32 character: " + group);
            }
            output = (output << 5) | ((long) value);
            numParsed++;
            if (numParsed == 1) {
                if ((value & 15) != value) {
                    throw new IllegalArgumentException("illegal start character; will overflow");
                }
            } else if (numParsed > LONG_SIZE) {
                throw new IllegalArgumentException("too long; should have 13 characters");
            }
        }
        if (numParsed == LONG_SIZE) {
            return output;
        }
        throw new IllegalArgumentException("too short; should have 13 characters");
    }

    public int hashCode() {
        return (int) this.mIdentity;
    }

    public boolean equals(Object other) {
        if (!(other instanceof VerifierDeviceIdentity)) {
            return false;
        }
        if (this.mIdentity == ((VerifierDeviceIdentity) other).mIdentity) {
            return true;
        }
        return false;
    }

    public String toString() {
        return this.mIdentityString;
    }

    public static VerifierDeviceIdentity parse(String deviceIdentity) throws IllegalArgumentException {
        try {
            return new VerifierDeviceIdentity(decodeBase32(deviceIdentity.getBytes("US-ASCII")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("bad base-32 characters in input");
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mIdentity);
    }
}
