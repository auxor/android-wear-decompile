package android.media;

public class AudioFormat {
    public static final int AUDIO_FORMAT_HAS_PROPERTY_CHANNEL_MASK = 4;
    public static final int AUDIO_FORMAT_HAS_PROPERTY_ENCODING = 1;
    public static final int AUDIO_FORMAT_HAS_PROPERTY_NONE = 0;
    public static final int AUDIO_FORMAT_HAS_PROPERTY_SAMPLE_RATE = 2;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_DEFAULT = 1;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_INVALID = 0;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_MONO = 2;
    @Deprecated
    public static final int CHANNEL_CONFIGURATION_STEREO = 3;
    public static final int CHANNEL_INVALID = 0;
    public static final int CHANNEL_IN_BACK = 32;
    public static final int CHANNEL_IN_BACK_PROCESSED = 512;
    public static final int CHANNEL_IN_DEFAULT = 1;
    public static final int CHANNEL_IN_FRONT = 16;
    public static final int CHANNEL_IN_FRONT_BACK = 48;
    public static final int CHANNEL_IN_FRONT_PROCESSED = 256;
    public static final int CHANNEL_IN_LEFT = 4;
    public static final int CHANNEL_IN_LEFT_PROCESSED = 64;
    public static final int CHANNEL_IN_MONO = 16;
    public static final int CHANNEL_IN_PRESSURE = 1024;
    public static final int CHANNEL_IN_RIGHT = 8;
    public static final int CHANNEL_IN_RIGHT_PROCESSED = 128;
    public static final int CHANNEL_IN_STEREO = 12;
    public static final int CHANNEL_IN_VOICE_DNLINK = 32768;
    public static final int CHANNEL_IN_VOICE_UPLINK = 16384;
    public static final int CHANNEL_IN_X_AXIS = 2048;
    public static final int CHANNEL_IN_Y_AXIS = 4096;
    public static final int CHANNEL_IN_Z_AXIS = 8192;
    public static final int CHANNEL_OUT_5POINT1 = 252;
    public static final int CHANNEL_OUT_5POINT1_SIDE = 6204;
    public static final int CHANNEL_OUT_7POINT1 = 1020;
    public static final int CHANNEL_OUT_7POINT1_SURROUND = 6396;
    public static final int CHANNEL_OUT_BACK_CENTER = 1024;
    public static final int CHANNEL_OUT_BACK_LEFT = 64;
    public static final int CHANNEL_OUT_BACK_RIGHT = 128;
    public static final int CHANNEL_OUT_DEFAULT = 1;
    public static final int CHANNEL_OUT_FRONT_CENTER = 16;
    public static final int CHANNEL_OUT_FRONT_LEFT = 4;
    public static final int CHANNEL_OUT_FRONT_LEFT_OF_CENTER = 256;
    public static final int CHANNEL_OUT_FRONT_RIGHT = 8;
    public static final int CHANNEL_OUT_FRONT_RIGHT_OF_CENTER = 512;
    public static final int CHANNEL_OUT_LOW_FREQUENCY = 32;
    public static final int CHANNEL_OUT_MONO = 4;
    public static final int CHANNEL_OUT_QUAD = 204;
    public static final int CHANNEL_OUT_QUAD_SIDE = 6156;
    public static final int CHANNEL_OUT_SIDE_LEFT = 2048;
    public static final int CHANNEL_OUT_SIDE_RIGHT = 4096;
    public static final int CHANNEL_OUT_STEREO = 12;
    public static final int CHANNEL_OUT_SURROUND = 1052;
    public static final int CHANNEL_OUT_TOP_BACK_CENTER = 262144;
    public static final int CHANNEL_OUT_TOP_BACK_LEFT = 131072;
    public static final int CHANNEL_OUT_TOP_BACK_RIGHT = 524288;
    public static final int CHANNEL_OUT_TOP_CENTER = 8192;
    public static final int CHANNEL_OUT_TOP_FRONT_CENTER = 32768;
    public static final int CHANNEL_OUT_TOP_FRONT_LEFT = 16384;
    public static final int CHANNEL_OUT_TOP_FRONT_RIGHT = 65536;
    public static final int ENCODING_AC3 = 5;
    public static final int ENCODING_DEFAULT = 1;
    public static final int ENCODING_E_AC3 = 6;
    public static final int ENCODING_INVALID = 0;
    public static final int ENCODING_PCM_16BIT = 2;
    public static final int ENCODING_PCM_8BIT = 3;
    public static final int ENCODING_PCM_FLOAT = 4;
    private int mChannelMask;
    private int mEncoding;
    private int mPropertySetMask;
    private int mSampleRate;

    public static class Builder {
        private int mChannelMask;
        private int mEncoding;
        private int mPropertySetMask;
        private int mSampleRate;

        public Builder() {
            this.mEncoding = AudioFormat.ENCODING_INVALID;
            this.mSampleRate = AudioFormat.ENCODING_INVALID;
            this.mChannelMask = AudioFormat.ENCODING_INVALID;
            this.mPropertySetMask = AudioFormat.ENCODING_INVALID;
        }

        public Builder(AudioFormat af) {
            this.mEncoding = AudioFormat.ENCODING_INVALID;
            this.mSampleRate = AudioFormat.ENCODING_INVALID;
            this.mChannelMask = AudioFormat.ENCODING_INVALID;
            this.mPropertySetMask = AudioFormat.ENCODING_INVALID;
            this.mEncoding = af.mEncoding;
            this.mSampleRate = af.mSampleRate;
            this.mChannelMask = af.mChannelMask;
            this.mPropertySetMask = af.mPropertySetMask;
        }

        public AudioFormat build() {
            AudioFormat af = new AudioFormat(null);
            af.mEncoding = this.mEncoding;
            af.mSampleRate = this.mSampleRate;
            af.mChannelMask = this.mChannelMask;
            af.mPropertySetMask = this.mPropertySetMask;
            return af;
        }

        public Builder setEncoding(int encoding) throws IllegalArgumentException {
            switch (encoding) {
                case AudioFormat.ENCODING_DEFAULT /*1*/:
                    this.mEncoding = AudioFormat.ENCODING_PCM_16BIT;
                    break;
                case AudioFormat.ENCODING_PCM_16BIT /*2*/:
                case AudioFormat.ENCODING_PCM_8BIT /*3*/:
                case AudioFormat.ENCODING_PCM_FLOAT /*4*/:
                case AudioFormat.ENCODING_AC3 /*5*/:
                case AudioFormat.ENCODING_E_AC3 /*6*/:
                    this.mEncoding = encoding;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid encoding " + encoding);
            }
            this.mPropertySetMask |= AudioFormat.ENCODING_DEFAULT;
            return this;
        }

        public Builder setChannelMask(int channelMask) {
            this.mChannelMask = channelMask;
            this.mPropertySetMask |= AudioFormat.ENCODING_PCM_FLOAT;
            return this;
        }

        public Builder setSampleRate(int sampleRate) throws IllegalArgumentException {
            if (sampleRate <= 0 || sampleRate > 192000) {
                throw new IllegalArgumentException("Invalid sample rate " + sampleRate);
            }
            this.mSampleRate = sampleRate;
            this.mPropertySetMask |= AudioFormat.ENCODING_PCM_16BIT;
            return this;
        }
    }

    public static int inChannelMaskFromOutChannelMask(int outMask) throws IllegalArgumentException {
        if (outMask == ENCODING_DEFAULT) {
            throw new IllegalArgumentException("Illegal CHANNEL_OUT_DEFAULT channel mask for input.");
        }
        switch (channelCountFromOutChannelMask(outMask)) {
            case ENCODING_DEFAULT /*1*/:
                return CHANNEL_OUT_FRONT_CENTER;
            case ENCODING_PCM_16BIT /*2*/:
                return CHANNEL_OUT_STEREO;
            default:
                throw new IllegalArgumentException("Unsupported channel configuration for input.");
        }
    }

    public static int channelCountFromInChannelMask(int mask) {
        return Integer.bitCount(mask);
    }

    public static int channelCountFromOutChannelMask(int mask) {
        return Integer.bitCount(mask);
    }

    public static int convertChannelOutMaskToNativeMask(int javaMask) {
        return javaMask >> ENCODING_PCM_16BIT;
    }

    public static int convertNativeChannelMaskToOutMask(int nativeMask) {
        return nativeMask << ENCODING_PCM_16BIT;
    }

    public static int getBytesPerSample(int audioFormat) {
        switch (audioFormat) {
            case ENCODING_DEFAULT /*1*/:
            case ENCODING_PCM_16BIT /*2*/:
                return ENCODING_PCM_16BIT;
            case ENCODING_PCM_8BIT /*3*/:
                return ENCODING_DEFAULT;
            case ENCODING_PCM_FLOAT /*4*/:
                return ENCODING_PCM_FLOAT;
            default:
                throw new IllegalArgumentException("Bad audio format " + audioFormat);
        }
    }

    public static boolean isValidEncoding(int audioFormat) {
        switch (audioFormat) {
            case ENCODING_PCM_16BIT /*2*/:
            case ENCODING_PCM_8BIT /*3*/:
            case ENCODING_PCM_FLOAT /*4*/:
            case ENCODING_AC3 /*5*/:
            case ENCODING_E_AC3 /*6*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isEncodingLinearPcm(int audioFormat) {
        switch (audioFormat) {
            case ENCODING_DEFAULT /*1*/:
            case ENCODING_PCM_16BIT /*2*/:
            case ENCODING_PCM_8BIT /*3*/:
            case ENCODING_PCM_FLOAT /*4*/:
                return true;
            case ENCODING_AC3 /*5*/:
            case ENCODING_E_AC3 /*6*/:
                return false;
            default:
                throw new IllegalArgumentException("Bad audio format " + audioFormat);
        }
    }

    public AudioFormat() {
        throw new UnsupportedOperationException("There is no valid usage of this constructor");
    }

    private AudioFormat(int ignoredArgument) {
    }

    private AudioFormat(int encoding, int sampleRate, int channelMask) {
        this.mEncoding = encoding;
        this.mSampleRate = sampleRate;
        this.mChannelMask = channelMask;
        this.mPropertySetMask = 7;
    }

    public int getEncoding() {
        if ((this.mPropertySetMask & ENCODING_DEFAULT) == 0) {
            return ENCODING_INVALID;
        }
        return this.mEncoding;
    }

    public int getSampleRate() {
        if ((this.mPropertySetMask & ENCODING_PCM_16BIT) == 0) {
            return ENCODING_INVALID;
        }
        return this.mSampleRate;
    }

    public int getChannelMask() {
        if ((this.mPropertySetMask & ENCODING_PCM_FLOAT) == 0) {
            return ENCODING_INVALID;
        }
        return this.mChannelMask;
    }

    public int getPropertySetMask() {
        return this.mPropertySetMask;
    }

    public String toString() {
        return new String("AudioFormat: props=" + this.mPropertySetMask + " enc=" + this.mEncoding + " chan=0x" + Integer.toHexString(this.mChannelMask) + " rate=" + this.mSampleRate);
    }
}
