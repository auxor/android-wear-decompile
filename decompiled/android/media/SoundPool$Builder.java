package android.media;

import android.media.AudioAttributes.Builder;

public class SoundPool$Builder {
    private AudioAttributes mAudioAttributes;
    private int mMaxStreams;

    public SoundPool$Builder() {
        this.mMaxStreams = 1;
    }

    public SoundPool$Builder setMaxStreams(int maxStreams) throws IllegalArgumentException {
        if (maxStreams <= 0) {
            throw new IllegalArgumentException("Strictly positive value required for the maximum number of streams");
        }
        this.mMaxStreams = maxStreams;
        return this;
    }

    public SoundPool$Builder setAudioAttributes(AudioAttributes attributes) throws IllegalArgumentException {
        if (attributes == null) {
            throw new IllegalArgumentException("Invalid null AudioAttributes");
        }
        this.mAudioAttributes = attributes;
        return this;
    }

    public SoundPool build() {
        if (this.mAudioAttributes == null) {
            this.mAudioAttributes = new Builder().setUsage(1).build();
        }
        return new SoundPool(this.mMaxStreams, this.mAudioAttributes, null);
    }
}
