package android.media;

public interface AudioTrack$OnPlaybackPositionUpdateListener {
    void onMarkerReached(AudioTrack audioTrack);

    void onPeriodicNotification(AudioTrack audioTrack);
}
