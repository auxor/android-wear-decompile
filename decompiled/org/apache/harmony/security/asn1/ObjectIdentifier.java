package org.apache.harmony.security.asn1;

import java.util.Arrays;

public final class ObjectIdentifier {
    private final int[] oid;
    private String soid;

    public ObjectIdentifier(int[] oid) {
        validate(oid);
        this.oid = oid;
    }

    public ObjectIdentifier(String strOid) {
        this.oid = toIntArray(strOid);
        this.soid = strOid;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Arrays.equals(this.oid, ((ObjectIdentifier) o).oid);
    }

    public String toString() {
        if (this.soid == null) {
            this.soid = toString(this.oid);
        }
        return this.soid;
    }

    public int hashCode() {
        int intHash = 0;
        int i = 0;
        while (i < this.oid.length && i < 4) {
            intHash += this.oid[i] << (i * 8);
            i++;
        }
        return Integer.MAX_VALUE & intHash;
    }

    public static void validate(int[] oid) {
        if (oid == null) {
            throw new IllegalArgumentException("oid == null");
        } else if (oid.length < 2) {
            throw new IllegalArgumentException("OID MUST have at least 2 subidentifiers");
        } else if (oid[0] > 2) {
            throw new IllegalArgumentException("Valid values for first subidentifier are 0, 1 and 2");
        } else if (oid[0] == 2 || oid[1] <= 39) {
            for (int anOid : oid) {
                if (anOid < 0) {
                    throw new IllegalArgumentException("Subidentifier MUST have positive value");
                }
            }
        } else {
            throw new IllegalArgumentException("If the first subidentifier has 0 or 1 value the second subidentifier value MUST be less than 40");
        }
    }

    public static String toString(int[] oid) {
        StringBuilder sb = new StringBuilder(oid.length * 3);
        for (int i = 0; i < oid.length - 1; i++) {
            sb.append(oid[i]);
            sb.append('.');
        }
        sb.append(oid[oid.length - 1]);
        return sb.toString();
    }

    public static int[] toIntArray(String str) {
        return toIntArray(str, true);
    }

    public static boolean isOID(String str) {
        return toIntArray(str, false) != null;
    }

    private static int[] toIntArray(String str, boolean shouldThrow) {
        if (str != null) {
            int length = str.length();
            if (length != 0) {
                int i;
                char c;
                int count = 1;
                boolean wasDot = true;
                for (i = 0; i < length; i++) {
                    c = str.charAt(i);
                    if (c == '.') {
                        if (!wasDot) {
                            wasDot = true;
                            count++;
                        } else if (!shouldThrow) {
                            return null;
                        } else {
                            throw new IllegalArgumentException("Incorrect syntax");
                        }
                    } else if (c >= '0' && c <= '9') {
                        wasDot = false;
                    } else if (!shouldThrow) {
                        return null;
                    } else {
                        throw new IllegalArgumentException("Incorrect syntax");
                    }
                }
                if (wasDot) {
                    if (!shouldThrow) {
                        return null;
                    }
                    throw new IllegalArgumentException("Incorrect syntax");
                } else if (count >= 2) {
                    int[] oid = new int[count];
                    int j = 0;
                    for (i = 0; i < length; i++) {
                        c = str.charAt(i);
                        if (c == '.') {
                            j++;
                        } else {
                            oid[j] = ((oid[j] * 10) + c) - 48;
                        }
                    }
                    if (oid[0] > 2) {
                        if (!shouldThrow) {
                            return null;
                        }
                        throw new IllegalArgumentException("Incorrect syntax");
                    } else if (oid[0] == 2 || oid[1] <= 39) {
                        return oid;
                    } else {
                        if (!shouldThrow) {
                            return null;
                        }
                        throw new IllegalArgumentException("Incorrect syntax");
                    }
                } else if (!shouldThrow) {
                    return null;
                } else {
                    throw new IllegalArgumentException("Incorrect syntax");
                }
            } else if (!shouldThrow) {
                return null;
            } else {
                throw new IllegalArgumentException("Incorrect syntax");
            }
        } else if (!shouldThrow) {
            return null;
        } else {
            throw new IllegalArgumentException("str == null");
        }
    }
}
