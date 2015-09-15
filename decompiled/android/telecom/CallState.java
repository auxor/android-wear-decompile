package android.telecom;

public final class CallState {
    public static final int ABORTED = 8;
    public static final int ACTIVE = 5;
    public static final int CONNECTING = 1;
    public static final int DIALING = 3;
    public static final int DISCONNECTED = 7;
    public static final int DISCONNECTING = 9;
    public static final int NEW = 0;
    public static final int ON_HOLD = 6;
    public static final int PRE_DIAL_WAIT = 2;
    public static final int RINGING = 4;

    private CallState() {
    }

    public static String toString(int callState) {
        switch (callState) {
            case NEW /*0*/:
                return "NEW";
            case CONNECTING /*1*/:
                return "CONNECTING";
            case PRE_DIAL_WAIT /*2*/:
                return "PRE_DIAL_WAIT";
            case DIALING /*3*/:
                return "DIALING";
            case RINGING /*4*/:
                return "RINGING";
            case ACTIVE /*5*/:
                return "ACTIVE";
            case ON_HOLD /*6*/:
                return "ON_HOLD";
            case DISCONNECTED /*7*/:
                return "DISCONNECTED";
            case ABORTED /*8*/:
                return "ABORTED";
            case DISCONNECTING /*9*/:
                return "DISCONNECTING";
            default:
                return "UNKNOWN";
        }
    }
}
