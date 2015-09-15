package android.util;

import com.android.internal.R;

public class StateSet {
    public static final int[] NOTHING;
    public static final int[] WILD_CARD;

    static {
        WILD_CARD = new int[0];
        NOTHING = new int[]{0};
    }

    public static boolean isWildCard(int[] stateSetOrSpec) {
        return stateSetOrSpec.length == 0 || stateSetOrSpec[0] == 0;
    }

    public static boolean stateSetMatches(int[] stateSpec, int[] stateSet) {
        if (stateSet != null) {
            int stateSetSize = stateSet.length;
            for (int stateSpecState : stateSpec) {
                int stateSpecState2;
                if (stateSpecState2 == 0) {
                    return true;
                }
                boolean mustMatch;
                if (stateSpecState2 > 0) {
                    mustMatch = true;
                } else {
                    mustMatch = false;
                    stateSpecState2 = -stateSpecState2;
                }
                boolean found = false;
                int j = 0;
                while (j < stateSetSize) {
                    int state = stateSet[j];
                    if (state == 0) {
                        if (mustMatch) {
                            return false;
                        }
                    } else if (state != stateSpecState2) {
                        j++;
                    } else if (!mustMatch) {
                        return false;
                    } else {
                        found = true;
                    }
                    if (!mustMatch && !found) {
                        return false;
                    }
                }
                if (!mustMatch) {
                }
            }
            return true;
        } else if (stateSpec == null || isWildCard(stateSpec)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean stateSetMatches(int[] stateSpec, int state) {
        for (int stateSpecState : stateSpec) {
            if (stateSpecState == 0) {
                return true;
            }
            if (stateSpecState > 0) {
                if (state != stateSpecState) {
                    return false;
                }
            } else if (state == (-stateSpecState)) {
                return false;
            }
        }
        return true;
    }

    public static int[] trimStateSet(int[] states, int newSize) {
        if (states.length == newSize) {
            return states;
        }
        int[] trimmedStates = new int[newSize];
        System.arraycopy(states, 0, trimmedStates, 0, newSize);
        return trimmedStates;
    }

    public static String dump(int[] states) {
        StringBuilder sb = new StringBuilder();
        for (int i : states) {
            switch (i) {
                case R.attr.state_focused /*16842908*/:
                    sb.append("F ");
                    break;
                case R.attr.state_window_focused /*16842909*/:
                    sb.append("W ");
                    break;
                case R.attr.state_enabled /*16842910*/:
                    sb.append("E ");
                    break;
                case R.attr.state_selected /*16842913*/:
                    sb.append("S ");
                    break;
                case R.attr.state_pressed /*16842919*/:
                    sb.append("P ");
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }
}
