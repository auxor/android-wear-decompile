package android.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.media.AudioAttributes.Builder;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Bookmarks;
import android.provider.VoicemailContract.Voicemails;
import android.telephony.SubscriptionManager;
import android.util.Log;
import java.io.IOException;

public class Ringtone {
    private static final boolean LOGD = true;
    private static final String[] MEDIA_COLUMNS;
    private static final String TAG = "Ringtone";
    private final boolean mAllowRemote;
    private AudioAttributes mAudioAttributes;
    private final AudioManager mAudioManager;
    private final Context mContext;
    private MediaPlayer mLocalPlayer;
    private final IRingtonePlayer mRemotePlayer;
    private final Binder mRemoteToken;
    private String mTitle;
    private Uri mUri;

    static {
        MEDIA_COLUMNS = new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, Voicemails._DATA, Bookmarks.TITLE};
    }

    public Ringtone(Context context, boolean allowRemote) {
        IRingtonePlayer ringtonePlayer;
        Binder binder = null;
        this.mAudioAttributes = new Builder().setUsage(6).setContentType(4).build();
        this.mContext = context;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        this.mAllowRemote = allowRemote;
        if (allowRemote) {
            ringtonePlayer = this.mAudioManager.getRingtonePlayer();
        } else {
            ringtonePlayer = null;
        }
        this.mRemotePlayer = ringtonePlayer;
        if (allowRemote) {
            binder = new Binder();
        }
        this.mRemoteToken = binder;
    }

    @Deprecated
    public void setStreamType(int streamType) {
        setAudioAttributes(new Builder().setInternalLegacyStreamType(streamType).build());
    }

    @Deprecated
    public int getStreamType() {
        return AudioAttributes.toLegacyStreamType(this.mAudioAttributes);
    }

    public void setAudioAttributes(AudioAttributes attributes) throws IllegalArgumentException {
        if (attributes == null) {
            throw new IllegalArgumentException("Invalid null AudioAttributes for Ringtone");
        }
        this.mAudioAttributes = attributes;
        setUri(this.mUri);
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAudioAttributes;
    }

    public String getTitle(Context context) {
        if (this.mTitle != null) {
            return this.mTitle;
        }
        String title = getTitle(context, this.mUri, LOGD);
        this.mTitle = title;
        return title;
    }

    private static String getTitle(Context context, Uri uri, boolean followSettingsUri) {
        Cursor cursor = null;
        ContentResolver res = context.getContentResolver();
        String title = null;
        if (uri != null) {
            String authority = uri.getAuthority();
            if (!Settings.AUTHORITY.equals(authority)) {
                try {
                    if (MediaStore.AUTHORITY.equals(authority)) {
                        cursor = res.query(uri, MEDIA_COLUMNS, null, null, null);
                    }
                } catch (SecurityException e) {
                }
                if (cursor != null) {
                    try {
                        if (cursor.getCount() == 1) {
                            cursor.moveToFirst();
                            String string = cursor.getString(2);
                            if (cursor == null) {
                                return string;
                            }
                            cursor.close();
                            return string;
                        }
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
                title = uri.getLastPathSegment();
                if (cursor != null) {
                    cursor.close();
                }
            } else if (followSettingsUri) {
                title = context.getString(17040570, getTitle(context, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.getDefaultType(uri)), false));
            }
        }
        if (title == null) {
            title = context.getString(17040573);
            if (title == null) {
                title = ProxyInfo.LOCAL_EXCL_LIST;
            }
        }
        return title;
    }

    public void setUri(Uri uri) {
        Exception e;
        destroyLocalPlayer();
        this.mUri = uri;
        if (this.mUri != null) {
            this.mLocalPlayer = new MediaPlayer();
            try {
                this.mLocalPlayer.setDataSource(this.mContext, this.mUri);
                this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
                this.mLocalPlayer.prepare();
            } catch (SecurityException e2) {
                e = e2;
                destroyLocalPlayer();
                if (!this.mAllowRemote) {
                    Log.w(TAG, "Remote playback not allowed: " + e);
                }
                if (this.mLocalPlayer != null) {
                    Log.d(TAG, "Problem opening; delegating to remote player");
                } else {
                    Log.d(TAG, "Successfully created local player");
                }
            } catch (IOException e3) {
                e = e3;
                destroyLocalPlayer();
                if (this.mAllowRemote) {
                    Log.w(TAG, "Remote playback not allowed: " + e);
                }
                if (this.mLocalPlayer != null) {
                    Log.d(TAG, "Successfully created local player");
                } else {
                    Log.d(TAG, "Problem opening; delegating to remote player");
                }
            }
            if (this.mLocalPlayer != null) {
                Log.d(TAG, "Successfully created local player");
            } else {
                Log.d(TAG, "Problem opening; delegating to remote player");
            }
        }
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void play() {
        if (this.mLocalPlayer != null) {
            if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) != 0) {
                this.mLocalPlayer.start();
            }
        } else if (this.mAllowRemote && this.mRemotePlayer != null) {
            try {
                this.mRemotePlayer.play(this.mRemoteToken, this.mUri.getCanonicalUri(), this.mAudioAttributes);
            } catch (RemoteException e) {
                if (!playFallbackRingtone()) {
                    Log.w(TAG, "Problem playing ringtone: " + e);
                }
            }
        } else if (!playFallbackRingtone()) {
            Log.w(TAG, "Neither local nor remote playback available");
        }
    }

    public void stop() {
        if (this.mLocalPlayer != null) {
            destroyLocalPlayer();
        } else if (this.mAllowRemote && this.mRemotePlayer != null) {
            try {
                this.mRemotePlayer.stop(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.w(TAG, "Problem stopping ringtone: " + e);
            }
        }
    }

    private void destroyLocalPlayer() {
        if (this.mLocalPlayer != null) {
            this.mLocalPlayer.reset();
            this.mLocalPlayer.release();
            this.mLocalPlayer = null;
        }
    }

    public boolean isPlaying() {
        boolean z = false;
        if (this.mLocalPlayer != null) {
            return this.mLocalPlayer.isPlaying();
        }
        if (!this.mAllowRemote || this.mRemotePlayer == null) {
            Log.w(TAG, "Neither local nor remote playback available");
            return z;
        }
        try {
            return this.mRemotePlayer.isPlaying(this.mRemoteToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Problem checking ringtone: " + e);
            return z;
        }
    }

    private boolean playFallbackRingtone() {
        if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) != 0) {
            int ringtoneType = RingtoneManager.getDefaultType(this.mUri);
            if (ringtoneType == -1 || RingtoneManager.getActualDefaultRingtoneUri(this.mContext, ringtoneType) != null) {
                try {
                    AssetFileDescriptor afd = this.mContext.getResources().openRawResourceFd(17825797);
                    if (afd != null) {
                        this.mLocalPlayer = new MediaPlayer();
                        if (afd.getDeclaredLength() < 0) {
                            this.mLocalPlayer.setDataSource(afd.getFileDescriptor());
                        } else {
                            this.mLocalPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
                        }
                        this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
                        this.mLocalPlayer.prepare();
                        this.mLocalPlayer.start();
                        afd.close();
                        return LOGD;
                    }
                    Log.e(TAG, "Could not load fallback ringtone");
                } catch (IOException e) {
                    destroyLocalPlayer();
                    Log.e(TAG, "Failed to open fallback ringtone");
                } catch (NotFoundException e2) {
                    Log.e(TAG, "Fallback ringtone does not exist");
                }
            } else {
                Log.w(TAG, "not playing fallback for " + this.mUri);
            }
        }
        return false;
    }

    void setTitle(String title) {
        this.mTitle = title;
    }
}
