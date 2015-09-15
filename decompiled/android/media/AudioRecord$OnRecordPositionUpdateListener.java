package android.media;

public interface AudioRecord$OnRecordPositionUpdateListener {
    void onMarkerReached(AudioRecord audioRecord);

    void onPeriodicNotification(AudioRecord audioRecord);
}
