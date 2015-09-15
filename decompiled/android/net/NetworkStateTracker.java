package android.net;

import android.content.Context;
import android.net.SamplingDataTracker.SamplingSnapshot;
import android.os.Handler;
import android.os.Messenger;

public interface NetworkStateTracker {
    public static final int EVENT_CONFIGURATION_CHANGED = 458753;
    public static final int EVENT_NETWORK_CONNECTED = 458756;
    public static final int EVENT_NETWORK_DISCONNECTED = 458757;
    public static final int EVENT_NETWORK_SUBTYPE_CHANGED = 458755;
    public static final int EVENT_RESTORE_DEFAULT_NETWORK = 458754;
    public static final int EVENT_STATE_CHANGED = 458752;

    void captivePortalCheckCompleted(boolean z);

    void defaultRouteSet(boolean z);

    LinkProperties getLinkProperties();

    LinkQualityInfo getLinkQualityInfo();

    Network getNetwork();

    NetworkCapabilities getNetworkCapabilities();

    NetworkInfo getNetworkInfo();

    String getNetworkInterfaceName();

    String getTcpBufferSizesPropName();

    boolean isAvailable();

    boolean isDefaultRouteSet();

    boolean isPrivateDnsRouteSet();

    boolean isTeardownRequested();

    void privateDnsRouteSet(boolean z);

    boolean reconnect();

    void setDependencyMet(boolean z);

    void setNetId(int i);

    void setPolicyDataEnable(boolean z);

    boolean setRadio(boolean z);

    void setTeardownRequested(boolean z);

    void setUserDataEnable(boolean z);

    void startMonitoring(Context context, Handler handler);

    void startSampling(SamplingSnapshot samplingSnapshot);

    void stopSampling(SamplingSnapshot samplingSnapshot);

    void supplyMessenger(Messenger messenger);

    boolean teardown();
}
