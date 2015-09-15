package android.media;

import android.os.IBinder;

public abstract class AudioManagerInternal {

    public interface RingerModeDelegate {
        int onSetRingerModeExternal(int i, int i2, String str, int i3);

        int onSetRingerModeInternal(int i, int i2, String str, int i3);
    }

    public abstract void adjustMasterVolumeForUid(int i, int i2, String str, int i3);

    public abstract void adjustStreamVolumeForUid(int i, int i2, int i3, String str, int i4);

    public abstract void adjustSuggestedStreamVolumeForUid(int i, int i2, int i3, String str, int i4);

    public abstract int getRingerModeInternal();

    public abstract void setMasterMuteForUid(boolean z, int i, String str, IBinder iBinder, int i2);

    public abstract void setRingerModeDelegate(RingerModeDelegate ringerModeDelegate);

    public abstract void setRingerModeInternal(int i, String str);

    public abstract void setStreamVolumeForUid(int i, int i2, int i3, String str, int i4);
}
