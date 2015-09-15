package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.widget.SpellChecker;
import android.widget.Toast;

public enum SupplicantState implements Parcelable {
    DISCONNECTED,
    INTERFACE_DISABLED,
    INACTIVE,
    SCANNING,
    AUTHENTICATING,
    ASSOCIATING,
    ASSOCIATED,
    FOUR_WAY_HANDSHAKE,
    GROUP_HANDSHAKE,
    COMPLETED,
    DORMANT,
    UNINITIALIZED,
    INVALID;
    
    public static final Creator<SupplicantState> CREATOR;

    /* renamed from: android.net.wifi.SupplicantState.2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$net$wifi$SupplicantState;

        static {
            $SwitchMap$android$net$wifi$SupplicantState = new int[SupplicantState.values().length];
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.AUTHENTICATING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.ASSOCIATING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.ASSOCIATED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.FOUR_WAY_HANDSHAKE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.GROUP_HANDSHAKE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.COMPLETED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DISCONNECTED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.INTERFACE_DISABLED.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.INACTIVE.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.SCANNING.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.DORMANT.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.UNINITIALIZED.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$android$net$wifi$SupplicantState[SupplicantState.INVALID.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    static {
        CREATOR = new Creator<SupplicantState>() {
            public SupplicantState createFromParcel(Parcel in) {
                return SupplicantState.valueOf(in.readString());
            }

            public SupplicantState[] newArray(int size) {
                return new SupplicantState[size];
            }
        };
    }

    public static boolean isValidState(SupplicantState state) {
        return (state == UNINITIALIZED || state == INVALID) ? false : true;
    }

    public static boolean isHandshakeState(SupplicantState state) {
        switch (AnonymousClass2.$SwitchMap$android$net$wifi$SupplicantState[state.ordinal()]) {
            case Toast.LENGTH_LONG /*1*/:
            case Action.MERGE_IGNORE /*2*/:
            case SetDrawableParameters.TAG /*3*/:
            case ViewGroupAction.TAG /*4*/:
            case ReflectionActionWithoutParams.TAG /*5*/:
                return true;
            case SetEmptyView.TAG /*6*/:
            case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
            case SetPendingIntentTemplate.TAG /*8*/:
            case SetOnClickFillInIntent.TAG /*9*/:
            case SetRemoteViewsAdapterIntent.TAG /*10*/:
            case TextViewDrawableAction.TAG /*11*/:
            case BitmapReflectionAction.TAG /*12*/:
            case TextViewSizeAction.TAG /*13*/:
                return false;
            default:
                throw new IllegalArgumentException("Unknown supplicant state");
        }
    }

    public static boolean isConnecting(SupplicantState state) {
        switch (AnonymousClass2.$SwitchMap$android$net$wifi$SupplicantState[state.ordinal()]) {
            case Toast.LENGTH_LONG /*1*/:
            case Action.MERGE_IGNORE /*2*/:
            case SetDrawableParameters.TAG /*3*/:
            case ViewGroupAction.TAG /*4*/:
            case ReflectionActionWithoutParams.TAG /*5*/:
            case SetEmptyView.TAG /*6*/:
                return true;
            case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
            case SetPendingIntentTemplate.TAG /*8*/:
            case SetOnClickFillInIntent.TAG /*9*/:
            case SetRemoteViewsAdapterIntent.TAG /*10*/:
            case TextViewDrawableAction.TAG /*11*/:
            case BitmapReflectionAction.TAG /*12*/:
            case TextViewSizeAction.TAG /*13*/:
                return false;
            default:
                throw new IllegalArgumentException("Unknown supplicant state");
        }
    }

    public static boolean isDriverActive(SupplicantState state) {
        switch (AnonymousClass2.$SwitchMap$android$net$wifi$SupplicantState[state.ordinal()]) {
            case Toast.LENGTH_LONG /*1*/:
            case Action.MERGE_IGNORE /*2*/:
            case SetDrawableParameters.TAG /*3*/:
            case ViewGroupAction.TAG /*4*/:
            case ReflectionActionWithoutParams.TAG /*5*/:
            case SetEmptyView.TAG /*6*/:
            case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
            case SetOnClickFillInIntent.TAG /*9*/:
            case SetRemoteViewsAdapterIntent.TAG /*10*/:
            case TextViewDrawableAction.TAG /*11*/:
                return true;
            case SetPendingIntentTemplate.TAG /*8*/:
            case BitmapReflectionAction.TAG /*12*/:
            case TextViewSizeAction.TAG /*13*/:
                return false;
            default:
                throw new IllegalArgumentException("Unknown supplicant state");
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name());
    }
}
