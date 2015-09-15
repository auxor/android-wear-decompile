package android.media;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.util.List;

class AudioService$2 implements ServiceListener {
    final /* synthetic */ AudioService this$0;

    AudioService$2(AudioService audioService) {
        this.this$0 = audioService;
    }

    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        List<BluetoothDevice> deviceList;
        BluetoothDevice btDevice;
        switch (profile) {
            case Toast.LENGTH_LONG /*1*/:
                synchronized (AudioService.access$2400(this.this$0)) {
                    AudioService.access$000(this.this$0).removeMessages(9);
                    AudioService.access$3102(this.this$0, (BluetoothHeadset) proxy);
                    deviceList = AudioService.access$3100(this.this$0).getConnectedDevices();
                    if (deviceList.size() <= 0) {
                        AudioService.access$2902(this.this$0, null);
                        break;
                    }
                    AudioService.access$2902(this.this$0, (BluetoothDevice) deviceList.get(0));
                    AudioService.access$2500(this.this$0);
                    if (AudioService.access$2700(this.this$0) == 1 || AudioService.access$2700(this.this$0) == 5 || AudioService.access$2700(this.this$0) == 4) {
                        boolean status = false;
                        if (AudioService.access$2900(this.this$0) != null) {
                            switch (AudioService.access$2700(this.this$0)) {
                                case Toast.LENGTH_LONG /*1*/:
                                    AudioService.access$2702(this.this$0, 3);
                                    if (AudioService.access$2800(this.this$0) != 1) {
                                        if (AudioService.access$2800(this.this$0) != 0) {
                                            if (AudioService.access$2800(this.this$0) == 2) {
                                                status = AudioService.access$3100(this.this$0).startVoiceRecognition(AudioService.access$2900(this.this$0));
                                                break;
                                            }
                                        }
                                        status = AudioService.access$3100(this.this$0).startScoUsingVirtualVoiceCall(AudioService.access$2900(this.this$0));
                                        break;
                                    }
                                    status = AudioService.access$3100(this.this$0).connectAudio();
                                    break;
                                    break;
                                case ViewGroupAction.TAG /*4*/:
                                    status = AudioService.access$3100(this.this$0).stopVoiceRecognition(AudioService.access$2900(this.this$0));
                                    break;
                                case ReflectionActionWithoutParams.TAG /*5*/:
                                    if (AudioService.access$2800(this.this$0) != 1) {
                                        if (AudioService.access$2800(this.this$0) != 0) {
                                            if (AudioService.access$2800(this.this$0) == 2) {
                                                status = AudioService.access$3100(this.this$0).stopVoiceRecognition(AudioService.access$2900(this.this$0));
                                                break;
                                            }
                                        }
                                        status = AudioService.access$3100(this.this$0).stopScoUsingVirtualVoiceCall(AudioService.access$2900(this.this$0));
                                        break;
                                    }
                                    status = AudioService.access$3100(this.this$0).disconnectAudio();
                                    break;
                                    break;
                            }
                        }
                        if (!status) {
                            AudioService.access$100(AudioService.access$000(this.this$0), 9, 0, 0, 0, null, 0);
                        }
                    }
                    break;
                }
            case Action.MERGE_IGNORE /*2*/:
                synchronized (AudioService.access$3300(this.this$0)) {
                    AudioService.access$3402(this.this$0, (BluetoothA2dp) proxy);
                    deviceList = AudioService.access$3400(this.this$0).getConnectedDevices();
                    if (deviceList.size() > 0) {
                        btDevice = (BluetoothDevice) deviceList.get(0);
                        synchronized (AudioService.access$3500(this.this$0)) {
                            int state = AudioService.access$3400(this.this$0).getConnectionState(btDevice);
                            AudioService.access$3700(this.this$0, AudioService.access$000(this.this$0), KeyEvent.KEYCODE_BUTTON_L1, state, 0, btDevice, AudioService.access$3600(this.this$0, AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS, state == 2 ? 1 : 0));
                            break;
                        }
                    }
                    break;
                }
            case SetRemoteViewsAdapterIntent.TAG /*10*/:
                deviceList = proxy.getConnectedDevices();
                if (deviceList.size() > 0) {
                    btDevice = (BluetoothDevice) deviceList.get(0);
                    synchronized (AudioService.access$3500(this.this$0)) {
                        AudioService.access$3700(this.this$0, AudioService.access$000(this.this$0), KeyEvent.KEYCODE_BUTTON_Z, proxy.getConnectionState(btDevice), 0, btDevice, 0);
                        break;
                    }
                }
            default:
        }
    }

    public void onServiceDisconnected(int profile) {
        switch (profile) {
            case Toast.LENGTH_LONG /*1*/:
                synchronized (AudioService.access$2400(this.this$0)) {
                    AudioService.access$3102(this.this$0, null);
                    break;
                }
            case Action.MERGE_IGNORE /*2*/:
                synchronized (AudioService.access$3300(this.this$0)) {
                    AudioService.access$3402(this.this$0, null);
                    synchronized (AudioService.access$3500(this.this$0)) {
                        if (AudioService.access$3500(this.this$0).containsKey(Integer.valueOf(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS))) {
                            AudioService.access$3800(this.this$0, (String) AudioService.access$3500(this.this$0).get(Integer.valueOf(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS)));
                        }
                        break;
                    }
                    break;
                }
            case SetRemoteViewsAdapterIntent.TAG /*10*/:
                synchronized (AudioService.access$3500(this.this$0)) {
                    if (AudioService.access$3500(this.this$0).containsKey(Integer.valueOf(-2147352576))) {
                        AudioService.access$3900(this.this$0, (String) AudioService.access$3500(this.this$0).get(Integer.valueOf(-2147352576)));
                    }
                    break;
                }
            default:
        }
    }
}
