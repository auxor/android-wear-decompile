package com.android.internal.telephony;

public interface MmiCode {

    public enum State {
        PENDING,
        CANCELLED,
        COMPLETE,
        FAILED
    }

    void cancel();

    CharSequence getMessage();

    Phone getPhone();

    State getState();

    boolean isCancelable();

    boolean isUssdRequest();
}
