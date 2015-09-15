package android.os.storage;

public abstract class StorageEventListener {
    public void onUsbMassStorageConnectionChanged(boolean connected) {
    }

    public void onStorageStateChanged(String path, String oldState, String newState) {
    }
}
