package android.media;

public class AudioDevicePort extends AudioPort {
    private final String mAddress;
    private final int mType;

    AudioDevicePort(AudioHandle handle, int[] samplingRates, int[] channelMasks, int[] formats, AudioGain[] gains, int type, String address) {
        int i = 1;
        if (!AudioManager.isInputDevice(type)) {
            i = 2;
        }
        super(handle, i, samplingRates, channelMasks, formats, gains);
        this.mType = type;
        this.mAddress = address;
    }

    public int type() {
        return this.mType;
    }

    public String address() {
        return this.mAddress;
    }

    public AudioDevicePortConfig buildConfig(int samplingRate, int channelMask, int format, AudioGainConfig gain) {
        return new AudioDevicePortConfig(this, samplingRate, channelMask, format, gain);
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof AudioDevicePort)) {
            return false;
        }
        return super.equals(o);
    }

    public String toString() {
        return "{" + super.toString() + ", mType: " + (this.mRole == 1 ? AudioSystem.getInputDeviceName(this.mType) : AudioSystem.getOutputDeviceName(this.mType)) + ", mAddress: " + this.mAddress + "}";
    }
}
