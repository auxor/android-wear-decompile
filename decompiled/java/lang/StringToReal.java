package java.lang;

import dalvik.bytecode.Opcodes;

final class StringToReal {

    private static final class StringExponentPair {
        long e;
        boolean infinity;
        boolean negative;
        String s;
        boolean zero;

        private StringExponentPair() {
        }

        public float specialValue() {
            return this.infinity ? this.negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY : this.negative ? -0.0f : 0.0f;
        }
    }

    private static native double parseDblImpl(String str, int i);

    private static native float parseFltImpl(String str, int i);

    StringToReal() {
    }

    private static NumberFormatException invalidReal(String s, boolean isDouble) {
        throw new NumberFormatException("Invalid " + (isDouble ? "double" : "float") + ": \"" + s + "\"");
    }

    private static StringExponentPair initialParse(String s, int length, boolean isDouble) {
        StringExponentPair stringExponentPair = new StringExponentPair();
        if (length == 0) {
            throw invalidReal(s, isDouble);
        }
        int i;
        stringExponentPair.negative = s.charAt(0) == '-';
        char c = s.charAt(length - 1);
        if (c == 'D' || c == 'd' || c == 'F' || c == 'f') {
            length--;
            if (length == 0) {
                throw invalidReal(s, isDouble);
            }
        }
        int end = Math.max(s.indexOf(69), s.indexOf(Opcodes.OP_SGET_CHAR));
        if (end == -1) {
            end = length;
        } else if (end + 1 == length) {
            throw invalidReal(s, isDouble);
        } else {
            int exponentOffset = end + 1;
            boolean negativeExponent = false;
            char firstExponentChar = s.charAt(exponentOffset);
            if (firstExponentChar == '+' || firstExponentChar == '-') {
                negativeExponent = firstExponentChar == '-';
                exponentOffset++;
            }
            String exponentString = s.substring(exponentOffset, length);
            if (exponentString.isEmpty()) {
                throw invalidReal(s, isDouble);
            }
            for (i = 0; i < exponentString.length(); i++) {
                char ch = exponentString.charAt(i);
                if (ch < '0' || ch > '9') {
                    throw invalidReal(s, isDouble);
                }
            }
            try {
                stringExponentPair.e = (long) Integer.parseInt(exponentString);
                if (negativeExponent) {
                    stringExponentPair.e = -stringExponentPair.e;
                }
            } catch (NumberFormatException e) {
                if (negativeExponent) {
                    stringExponentPair.zero = true;
                } else {
                    stringExponentPair.infinity = true;
                }
            }
        }
        int start = 0;
        c = s.charAt(0);
        if (c == '-') {
            start = 0 + 1;
            length--;
            stringExponentPair.negative = true;
        } else if (c == '+') {
            start = 0 + 1;
            length--;
        }
        if (length == 0) {
            throw invalidReal(s, isDouble);
        }
        int decimal = -1;
        for (i = start; i < end; i++) {
            char mc = s.charAt(i);
            if (mc == '.') {
                if (decimal != -1) {
                    throw invalidReal(s, isDouble);
                }
                decimal = i;
            } else if (mc < '0' || mc > '9') {
                throw invalidReal(s, isDouble);
            }
        }
        if (decimal > -1) {
            stringExponentPair.e -= (long) ((end - decimal) - 1);
            s = s.substring(start, decimal) + s.substring(decimal + 1, end);
        } else {
            s = s.substring(start, end);
        }
        length = s.length();
        if (length == 0) {
            throw invalidReal(s, isDouble);
        }
        if (!(stringExponentPair.infinity || stringExponentPair.zero)) {
            end = length;
            while (end > 1) {
                if (s.charAt(end - 1) != '0') {
                    break;
                }
                end--;
            }
            start = 0;
            while (start < end - 1 && s.charAt(start) == '0') {
                start++;
            }
            if (!(end == length && start == 0)) {
                stringExponentPair.e += (long) (length - end);
                s = s.substring(start, end);
            }
            length = s.length();
            if (length > 52) {
                if (stringExponentPair.e < -359) {
                    int d = Math.min(-359 - ((int) stringExponentPair.e), length - 1);
                    s = s.substring(0, length - d);
                    stringExponentPair.e += (long) d;
                }
            }
            if (stringExponentPair.e < -1024) {
                stringExponentPair.zero = true;
            } else {
                if (stringExponentPair.e > 1024) {
                    stringExponentPair.infinity = true;
                } else {
                    stringExponentPair.s = s;
                }
            }
        }
        return stringExponentPair;
    }

    private static float parseName(String name, boolean isDouble) {
        boolean negative = false;
        int i = 0;
        int length = name.length();
        char firstChar = name.charAt(0);
        if (firstChar == '-') {
            negative = true;
            i = 0 + 1;
            length--;
        } else if (firstChar == '+') {
            i = 0 + 1;
            length--;
        }
        if (length == 8) {
            if (name.regionMatches(false, i, "Infinity", 0, 8)) {
                return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            }
        }
        if (length == 3) {
            if (name.regionMatches(false, i, "NaN", 0, 3)) {
                return Float.NaN;
            }
        }
        throw invalidReal(name, isDouble);
    }

    public static double parseDouble(String s) {
        s = s.trim();
        int length = s.length();
        if (length == 0) {
            throw invalidReal(s, true);
        }
        char last = s.charAt(length - 1);
        if (last == 'y' || last == 'N') {
            return (double) parseName(s, true);
        }
        if (s.indexOf("0x") != -1 || s.indexOf("0X") != -1) {
            return HexStringParser.parseDouble(s);
        }
        StringExponentPair info = initialParse(s, length, true);
        if (info.infinity || info.zero) {
            return (double) info.specialValue();
        }
        double result = parseDblImpl(info.s, (int) info.e);
        if (Double.doubleToRawLongBits(result) != -1) {
            return info.negative ? -result : result;
        } else {
            throw invalidReal(s, true);
        }
    }

    public static float parseFloat(String s) {
        s = s.trim();
        int length = s.length();
        if (length == 0) {
            throw invalidReal(s, false);
        }
        char last = s.charAt(length - 1);
        if (last == 'y' || last == 'N') {
            return parseName(s, false);
        }
        if (s.indexOf("0x") != -1 || s.indexOf("0X") != -1) {
            return HexStringParser.parseFloat(s);
        }
        StringExponentPair info = initialParse(s, length, false);
        if (info.infinity || info.zero) {
            return info.specialValue();
        }
        float result = parseFltImpl(info.s, (int) info.e);
        if (Float.floatToRawIntBits(result) != -1) {
            return info.negative ? -result : result;
        } else {
            throw invalidReal(s, false);
        }
    }
}
