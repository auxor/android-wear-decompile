package com.google.android.wearable.ambient;

import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.wearable.ambient.activity.IAmbientActivityService.Stub;
import java.lang.ref.WeakReference;

class AmbientServiceActivityStub extends Stub {
    private WeakReference<AmbientActivity> mActivity;

    AmbientServiceActivityStub(AmbientActivity ambientActivity) {
        this.mActivity = new WeakReference(ambientActivity);
    }

    public void detach() throws RemoteException {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.detach();
        }
    }

    public void onAmbientEntered() throws RemoteException {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.onAmbientEntered();
        }
    }

    public void onAmbientExited() throws RemoteException {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.onAmbientExited();
        }
    }

    public void onAmbientUpdated() throws RemoteException {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.onAmbientUpdated();
        }
    }

    public void onPaused() {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.onPaused();
        }
    }

    public void onResumed() {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.onResumed();
        }
    }

    public void onStopped() {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.onStopped();
        }
    }

    public void setConfiguration(Bundle bundle) {
        AmbientActivity ambientActivity = (AmbientActivity) this.mActivity.get();
        if (ambientActivity != null) {
            ambientActivity.setConfiguration(bundle);
        }
    }
}
