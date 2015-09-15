package java.math;

import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public enum RoundingMode {
    UP(0),
    DOWN(1),
    CEILING(2),
    FLOOR(3),
    HALF_UP(4),
    HALF_DOWN(5),
    HALF_EVEN(6),
    UNNECESSARY(7);
    
    private final int bigDecimalRM;

    private RoundingMode(int rm) {
        this.bigDecimalRM = rm;
    }

    public static RoundingMode valueOf(int mode) {
        switch (mode) {
            case XmlPullParser.START_DOCUMENT /*0*/:
                return UP;
            case NodeFilter.SHOW_ELEMENT /*1*/:
                return DOWN;
            case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                return CEILING;
            case XmlPullParser.END_TAG /*3*/:
                return FLOOR;
            case NodeFilter.SHOW_TEXT /*4*/:
                return HALF_UP;
            case XmlPullParser.CDSECT /*5*/:
                return HALF_DOWN;
            case XmlPullParser.ENTITY_REF /*6*/:
                return HALF_EVEN;
            case XmlPullParser.IGNORABLE_WHITESPACE /*7*/:
                return UNNECESSARY;
            default:
                throw new IllegalArgumentException("Invalid rounding mode");
        }
    }
}
