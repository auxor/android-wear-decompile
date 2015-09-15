package android.media;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.database.Cursor;
import android.database.SQLException;
import android.drm.DrmManagerClient;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaFile.MediaFileType;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video;
import android.provider.Settings.System;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.telephony.RILConstants;
import com.android.internal.telephony.SmsConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import javax.microedition.khronos.opengles.GL11;
import org.xml.sax.SAXException;

public class MediaScanner {
    private static final String ALARMS_DIR = "/alarms/";
    private static final int DATE_MODIFIED_PLAYLISTS_COLUMN_INDEX = 2;
    private static final String DEFAULT_RINGTONE_PROPERTY_PREFIX = "ro.config.";
    private static final boolean ENABLE_BULK_INSERTS = true;
    private static final int FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX = 3;
    private static final int FILES_PRESCAN_FORMAT_COLUMN_INDEX = 2;
    private static final int FILES_PRESCAN_ID_COLUMN_INDEX = 0;
    private static final int FILES_PRESCAN_PATH_COLUMN_INDEX = 1;
    private static final String[] FILES_PRESCAN_PROJECTION;
    private static final String[] ID3_GENRES;
    private static final int ID_PLAYLISTS_COLUMN_INDEX = 0;
    private static final String[] ID_PROJECTION;
    private static final String MUSIC_DIR = "/music/";
    private static final String NOTIFICATIONS_DIR = "/notifications/";
    private static final int PATH_PLAYLISTS_COLUMN_INDEX = 1;
    private static final String[] PLAYLIST_MEMBERS_PROJECTION;
    private static final String PODCAST_DIR = "/podcasts/";
    private static final String RINGTONES_DIR = "/ringtones/";
    private static final String TAG = "MediaScanner";
    private static HashMap<String, String> mMediaPaths;
    private static HashMap<String, String> mNoMediaPaths;
    private Uri mAudioUri;
    private final Options mBitmapOptions;
    private boolean mCaseInsensitivePaths;
    private final MyMediaScannerClient mClient;
    private Context mContext;
    private String mDefaultAlarmAlertFilename;
    private boolean mDefaultAlarmSet;
    private String mDefaultNotificationFilename;
    private boolean mDefaultNotificationSet;
    private String mDefaultRingtoneFilename;
    private boolean mDefaultRingtoneSet;
    private DrmManagerClient mDrmManagerClient;
    private final boolean mExternalIsEmulated;
    private final String mExternalStoragePath;
    private Uri mFilesUri;
    private Uri mFilesUriNoNotify;
    private Uri mImagesUri;
    private MediaInserter mMediaInserter;
    private IContentProvider mMediaProvider;
    private int mMtpObjectHandle;
    private long mNativeContext;
    private int mOriginalCount;
    private String mPackageName;
    private ArrayList<FileEntry> mPlayLists;
    private ArrayList<PlaylistEntry> mPlaylistEntries;
    private Uri mPlaylistsUri;
    private boolean mProcessGenres;
    private boolean mProcessPlaylists;
    private Uri mThumbsUri;
    private Uri mVideoUri;
    private boolean mWasEmptyPriorToScan;

    private static class FileEntry {
        int mFormat;
        long mLastModified;
        boolean mLastModifiedChanged;
        String mPath;
        long mRowId;

        FileEntry(long rowId, String path, long lastModified, int format) {
            this.mRowId = rowId;
            this.mPath = path;
            this.mLastModified = lastModified;
            this.mFormat = format;
            this.mLastModifiedChanged = false;
        }

        public String toString() {
            return this.mPath + " mRowId: " + this.mRowId;
        }
    }

    static class MediaBulkDeleter {
        final Uri mBaseUri;
        final String mPackageName;
        final IContentProvider mProvider;
        ArrayList<String> whereArgs;
        StringBuilder whereClause;

        public MediaBulkDeleter(IContentProvider provider, String packageName, Uri baseUri) {
            this.whereClause = new StringBuilder();
            this.whereArgs = new ArrayList(100);
            this.mProvider = provider;
            this.mPackageName = packageName;
            this.mBaseUri = baseUri;
        }

        public void delete(long id) throws RemoteException {
            if (this.whereClause.length() != 0) {
                this.whereClause.append(",");
            }
            this.whereClause.append("?");
            this.whereArgs.add("" + id);
            if (this.whereArgs.size() > 100) {
                flush();
            }
        }

        public void flush() throws RemoteException {
            int size = this.whereArgs.size();
            if (size > 0) {
                int numrows = this.mProvider.delete(this.mPackageName, this.mBaseUri, "_id IN (" + this.whereClause.toString() + ")", (String[]) this.whereArgs.toArray(new String[size]));
                this.whereClause.setLength(MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
                this.whereArgs.clear();
            }
        }
    }

    private class MyMediaScannerClient implements MediaScannerClient {
        private String mAlbum;
        private String mAlbumArtist;
        private String mArtist;
        private int mCompilation;
        private String mComposer;
        private int mDuration;
        private long mFileSize;
        private int mFileType;
        private String mGenre;
        private int mHeight;
        private boolean mIsDrm;
        private long mLastModified;
        private String mMimeType;
        private boolean mNoMedia;
        private String mPath;
        private String mTitle;
        private int mTrack;
        private int mWidth;
        private String mWriter;
        private int mYear;

        private MyMediaScannerClient() {
        }

        public FileEntry beginFile(String path, String mimeType, long lastModified, long fileSize, boolean isDirectory, boolean noMedia) {
            this.mMimeType = mimeType;
            this.mFileType = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            this.mFileSize = fileSize;
            this.mIsDrm = false;
            if (!isDirectory) {
                if (!noMedia && MediaScanner.isNoMediaFile(path)) {
                    noMedia = MediaScanner.ENABLE_BULK_INSERTS;
                }
                this.mNoMedia = noMedia;
                if (mimeType != null) {
                    this.mFileType = MediaFile.getFileTypeForMimeType(mimeType);
                }
                if (this.mFileType == 0) {
                    MediaFileType mediaFileType = MediaFile.getFileType(path);
                    if (mediaFileType != null) {
                        this.mFileType = mediaFileType.fileType;
                        if (this.mMimeType == null) {
                            this.mMimeType = mediaFileType.mimeType;
                        }
                    }
                }
                if (MediaScanner.this.isDrmEnabled() && MediaFile.isDrmFileType(this.mFileType)) {
                    this.mFileType = getFileTypeFromDrm(path);
                }
            }
            FileEntry entry = MediaScanner.this.makeEntryFor(path);
            long delta = entry != null ? lastModified - entry.mLastModified : 0;
            boolean wasModified = (delta > 1 || delta < -1) ? MediaScanner.ENABLE_BULK_INSERTS : false;
            if (entry == null || wasModified) {
                if (wasModified) {
                    entry.mLastModified = lastModified;
                } else {
                    entry = new FileEntry(0, path, lastModified, isDirectory ? GL11.GL_CLIP_PLANE1 : MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
                }
                entry.mLastModifiedChanged = MediaScanner.ENABLE_BULK_INSERTS;
            }
            if (MediaScanner.this.mProcessPlaylists && MediaFile.isPlayListFileType(this.mFileType)) {
                MediaScanner.this.mPlayLists.add(entry);
                return null;
            }
            this.mArtist = null;
            this.mAlbumArtist = null;
            this.mAlbum = null;
            this.mTitle = null;
            this.mComposer = null;
            this.mGenre = null;
            this.mTrack = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            this.mYear = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            this.mDuration = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            this.mPath = path;
            this.mLastModified = lastModified;
            this.mWriter = null;
            this.mCompilation = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            this.mWidth = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            this.mHeight = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            return entry;
        }

        public void scanFile(String path, long lastModified, long fileSize, boolean isDirectory, boolean noMedia) {
            doScanFile(path, null, lastModified, fileSize, isDirectory, false, noMedia);
        }

        public Uri doScanFile(String path, String mimeType, long lastModified, long fileSize, boolean isDirectory, boolean scanAlways, boolean noMedia) {
            try {
                FileEntry entry = beginFile(path, mimeType, lastModified, fileSize, isDirectory, noMedia);
                if (MediaScanner.this.mMtpObjectHandle != 0) {
                    entry.mRowId = 0;
                }
                if (entry == null) {
                    return null;
                }
                if (!entry.mLastModifiedChanged && !scanAlways) {
                    return null;
                }
                if (noMedia) {
                    return endFile(entry, false, false, false, false, false);
                }
                String lowpath = path.toLowerCase(Locale.ROOT);
                boolean ringtones = lowpath.indexOf(MediaScanner.RINGTONES_DIR) > 0 ? MediaScanner.ENABLE_BULK_INSERTS : false;
                boolean notifications = lowpath.indexOf(MediaScanner.NOTIFICATIONS_DIR) > 0 ? MediaScanner.ENABLE_BULK_INSERTS : false;
                boolean alarms = lowpath.indexOf(MediaScanner.ALARMS_DIR) > 0 ? MediaScanner.ENABLE_BULK_INSERTS : false;
                boolean podcasts = lowpath.indexOf(MediaScanner.PODCAST_DIR) > 0 ? MediaScanner.ENABLE_BULK_INSERTS : false;
                boolean music = (lowpath.indexOf(MediaScanner.MUSIC_DIR) > 0 || !(ringtones || notifications || alarms || podcasts)) ? MediaScanner.ENABLE_BULK_INSERTS : false;
                boolean isaudio = MediaFile.isAudioFileType(this.mFileType);
                boolean isvideo = MediaFile.isVideoFileType(this.mFileType);
                boolean isimage = MediaFile.isImageFileType(this.mFileType);
                if ((isaudio || isvideo || isimage) && MediaScanner.this.mExternalIsEmulated) {
                    if (path.startsWith(MediaScanner.this.mExternalStoragePath)) {
                        String directPath = Environment.getMediaStorageDirectory() + path.substring(MediaScanner.this.mExternalStoragePath.length());
                        if (new File(directPath).exists()) {
                            path = directPath;
                        }
                    }
                }
                if (isaudio || isvideo) {
                    MediaScanner.this.processFile(path, mimeType, this);
                }
                if (isimage) {
                    processImageFile(path);
                }
                return endFile(entry, ringtones, notifications, alarms, music, podcasts);
            } catch (RemoteException e) {
                Log.e(MediaScanner.TAG, "RemoteException in MediaScanner.scanFile()", e);
                return null;
            }
        }

        private int parseSubstring(String s, int start, int defaultValue) {
            int length = s.length();
            if (start == length) {
                return defaultValue;
            }
            int start2 = start + MediaScanner.PATH_PLAYLISTS_COLUMN_INDEX;
            char ch = s.charAt(start);
            if (ch < '0' || ch > '9') {
                start = start2;
                return defaultValue;
            }
            int result = ch - 48;
            while (start2 < length) {
                start = start2 + MediaScanner.PATH_PLAYLISTS_COLUMN_INDEX;
                ch = s.charAt(start2);
                if (ch < '0' || ch > '9') {
                    return result;
                }
                result = (result * 10) + (ch - 48);
                start2 = start;
            }
            start = start2;
            return result;
        }

        public void handleStringTag(String name, String value) {
            boolean z = MediaScanner.ENABLE_BULK_INSERTS;
            if (name.equalsIgnoreCase(GpsNetInitiatedHandler.NI_INTENT_KEY_TITLE) || name.startsWith("title;")) {
                this.mTitle = value;
            } else if (name.equalsIgnoreCase("artist") || name.startsWith("artist;")) {
                this.mArtist = value.trim();
            } else if (name.equalsIgnoreCase("albumartist") || name.startsWith("albumartist;") || name.equalsIgnoreCase("band") || name.startsWith("band;")) {
                this.mAlbumArtist = value.trim();
            } else if (name.equalsIgnoreCase("album") || name.startsWith("album;")) {
                this.mAlbum = value.trim();
            } else if (name.equalsIgnoreCase("composer") || name.startsWith("composer;")) {
                this.mComposer = value.trim();
            } else if (MediaScanner.this.mProcessGenres && (name.equalsIgnoreCase("genre") || name.startsWith("genre;"))) {
                this.mGenre = getGenreName(value);
            } else if (name.equalsIgnoreCase("year") || name.startsWith("year;")) {
                this.mYear = parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
            } else if (name.equalsIgnoreCase("tracknumber") || name.startsWith("tracknumber;")) {
                this.mTrack = ((this.mTrack / RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED) * RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED) + parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
            } else if (name.equalsIgnoreCase("discnumber") || name.equals("set") || name.startsWith("set;")) {
                this.mTrack = (parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX) * RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED) + (this.mTrack % RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED);
            } else if (name.equalsIgnoreCase("duration")) {
                this.mDuration = parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
            } else if (name.equalsIgnoreCase("writer") || name.startsWith("writer;")) {
                this.mWriter = value.trim();
            } else if (name.equalsIgnoreCase("compilation")) {
                this.mCompilation = parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
            } else if (name.equalsIgnoreCase("isdrm")) {
                if (parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX) != MediaScanner.PATH_PLAYLISTS_COLUMN_INDEX) {
                    z = false;
                }
                this.mIsDrm = z;
            } else if (name.equalsIgnoreCase("width")) {
                this.mWidth = parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
            } else if (name.equalsIgnoreCase("height")) {
                this.mHeight = parseSubstring(value, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX);
            }
        }

        private boolean convertGenreCode(String input, String expected) {
            String output = getGenreName(input);
            if (output.equals(expected)) {
                return MediaScanner.ENABLE_BULK_INSERTS;
            }
            Log.d(MediaScanner.TAG, "'" + input + "' -> '" + output + "', expected '" + expected + "'");
            return false;
        }

        private void testGenreNameConverter() {
            convertGenreCode("2", "Country");
            convertGenreCode("(2)", "Country");
            convertGenreCode("(2", "(2");
            convertGenreCode("2 Foo", "Country");
            convertGenreCode("(2) Foo", "Country");
            convertGenreCode("(2 Foo", "(2 Foo");
            convertGenreCode("2Foo", "2Foo");
            convertGenreCode("(2)Foo", "Country");
            convertGenreCode("200 Foo", "Foo");
            convertGenreCode("(200) Foo", "Foo");
            convertGenreCode("200Foo", "200Foo");
            convertGenreCode("(200)Foo", "Foo");
            convertGenreCode("200)Foo", "200)Foo");
            convertGenreCode("200) Foo", "200) Foo");
        }

        public String getGenreName(String genreTagValue) {
            if (genreTagValue == null) {
                return null;
            }
            int length = genreTagValue.length();
            if (length > 0) {
                boolean parenthesized = false;
                StringBuffer number = new StringBuffer();
                int i = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
                while (i < length) {
                    char c = genreTagValue.charAt(i);
                    if (i != 0 || c != '(') {
                        if (!Character.isDigit(c)) {
                            break;
                        }
                        number.append(c);
                    } else {
                        parenthesized = MediaScanner.ENABLE_BULK_INSERTS;
                    }
                    i += MediaScanner.PATH_PLAYLISTS_COLUMN_INDEX;
                }
                char charAfterNumber = i < length ? genreTagValue.charAt(i) : ' ';
                if ((parenthesized && charAfterNumber == ')') || (!parenthesized && Character.isWhitespace(charAfterNumber))) {
                    try {
                        short genreIndex = Short.parseShort(number.toString());
                        if (genreIndex >= (short) 0) {
                            if (genreIndex < MediaScanner.ID3_GENRES.length && MediaScanner.ID3_GENRES[genreIndex] != null) {
                                return MediaScanner.ID3_GENRES[genreIndex];
                            }
                            if (genreIndex == (short) 255) {
                                return null;
                            }
                            if (genreIndex >= (short) 255 || i + MediaScanner.PATH_PLAYLISTS_COLUMN_INDEX >= length) {
                                return number.toString();
                            }
                            if (parenthesized && charAfterNumber == ')') {
                                i += MediaScanner.PATH_PLAYLISTS_COLUMN_INDEX;
                            }
                            String ret = genreTagValue.substring(i).trim();
                            if (ret.length() != 0) {
                                return ret;
                            }
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return genreTagValue;
        }

        private void processImageFile(String path) {
            try {
                MediaScanner.this.mBitmapOptions.outWidth = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
                MediaScanner.this.mBitmapOptions.outHeight = MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
                BitmapFactory.decodeFile(path, MediaScanner.this.mBitmapOptions);
                this.mWidth = MediaScanner.this.mBitmapOptions.outWidth;
                this.mHeight = MediaScanner.this.mBitmapOptions.outHeight;
            } catch (Throwable th) {
            }
        }

        public void setMimeType(String mimeType) {
            if (!"audio/mp4".equals(this.mMimeType) || !mimeType.startsWith("video")) {
                this.mMimeType = mimeType;
                this.mFileType = MediaFile.getFileTypeForMimeType(mimeType);
            }
        }

        private ContentValues toValues() {
            ContentValues map = new ContentValues();
            map.put("_data", this.mPath);
            map.put(GpsNetInitiatedHandler.NI_INTENT_KEY_TITLE, this.mTitle);
            map.put("date_modified", Long.valueOf(this.mLastModified));
            map.put("_size", Long.valueOf(this.mFileSize));
            map.put("mime_type", this.mMimeType);
            map.put("is_drm", Boolean.valueOf(this.mIsDrm));
            String resolution = null;
            if (this.mWidth > 0 && this.mHeight > 0) {
                map.put("width", Integer.valueOf(this.mWidth));
                map.put("height", Integer.valueOf(this.mHeight));
                resolution = this.mWidth + "x" + this.mHeight;
            }
            if (!this.mNoMedia) {
                String str;
                String str2;
                if (MediaFile.isVideoFileType(this.mFileType)) {
                    str = "artist";
                    str2 = (this.mArtist == null || this.mArtist.length() <= 0) ? "<unknown>" : this.mArtist;
                    map.put(str, str2);
                    str = "album";
                    str2 = (this.mAlbum == null || this.mAlbum.length() <= 0) ? "<unknown>" : this.mAlbum;
                    map.put(str, str2);
                    map.put("duration", Integer.valueOf(this.mDuration));
                    if (resolution != null) {
                        map.put("resolution", resolution);
                    }
                } else if (!MediaFile.isImageFileType(this.mFileType) && MediaFile.isAudioFileType(this.mFileType)) {
                    str = "artist";
                    str2 = (this.mArtist == null || this.mArtist.length() <= 0) ? "<unknown>" : this.mArtist;
                    map.put(str, str2);
                    str = "album_artist";
                    str2 = (this.mAlbumArtist == null || this.mAlbumArtist.length() <= 0) ? null : this.mAlbumArtist;
                    map.put(str, str2);
                    str = "album";
                    str2 = (this.mAlbum == null || this.mAlbum.length() <= 0) ? "<unknown>" : this.mAlbum;
                    map.put(str, str2);
                    map.put("composer", this.mComposer);
                    map.put("genre", this.mGenre);
                    if (this.mYear != 0) {
                        map.put("year", Integer.valueOf(this.mYear));
                    }
                    map.put("track", Integer.valueOf(this.mTrack));
                    map.put("duration", Integer.valueOf(this.mDuration));
                    map.put("compilation", Integer.valueOf(this.mCompilation));
                }
            }
            return map;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private android.net.Uri endFile(android.media.MediaScanner.FileEntry r31, boolean r32, boolean r33, boolean r34, boolean r35, boolean r36) throws android.os.RemoteException {
            /*
            r30 = this;
            r0 = r30;
            r4 = r0.mArtist;
            if (r4 == 0) goto L_0x0010;
        L_0x0006:
            r0 = r30;
            r4 = r0.mArtist;
            r4 = r4.length();
            if (r4 != 0) goto L_0x0018;
        L_0x0010:
            r0 = r30;
            r4 = r0.mAlbumArtist;
            r0 = r30;
            r0.mArtist = r4;
        L_0x0018:
            r7 = r30.toValues();
            r4 = "title";
            r27 = r7.getAsString(r4);
            if (r27 == 0) goto L_0x002e;
        L_0x0024:
            r4 = r27.trim();
            r4 = android.text.TextUtils.isEmpty(r4);
            if (r4 == 0) goto L_0x003f;
        L_0x002e:
            r4 = "_data";
            r4 = r7.getAsString(r4);
            r27 = android.media.MediaFile.getFileTitle(r4);
            r4 = "title";
            r0 = r27;
            r7.put(r4, r0);
        L_0x003f:
            r4 = "album";
            r10 = r7.getAsString(r4);
            r4 = "<unknown>";
            r4 = r4.equals(r10);
            if (r4 == 0) goto L_0x007c;
        L_0x004d:
            r4 = "_data";
            r10 = r7.getAsString(r4);
            r4 = 47;
            r18 = r10.lastIndexOf(r4);
            if (r18 < 0) goto L_0x007c;
        L_0x005b:
            r23 = 0;
        L_0x005d:
            r4 = 47;
            r5 = r23 + 1;
            r16 = r10.indexOf(r4, r5);
            if (r16 < 0) goto L_0x006d;
        L_0x0067:
            r0 = r16;
            r1 = r18;
            if (r0 < r1) goto L_0x01b9;
        L_0x006d:
            if (r23 == 0) goto L_0x007c;
        L_0x006f:
            r4 = r23 + 1;
            r0 = r18;
            r10 = r10.substring(r4, r0);
            r4 = "album";
            r7.put(r4, r10);
        L_0x007c:
            r0 = r31;
            r0 = r0.mRowId;
            r24 = r0;
            r0 = r30;
            r4 = r0.mFileType;
            r4 = android.media.MediaFile.isAudioFileType(r4);
            if (r4 == 0) goto L_0x01bd;
        L_0x008c:
            r4 = 0;
            r4 = (r24 > r4 ? 1 : (r24 == r4 ? 0 : -1));
            if (r4 == 0) goto L_0x009c;
        L_0x0092:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mMtpObjectHandle;
            if (r4 == 0) goto L_0x01bd;
        L_0x009c:
            r4 = "is_ringtone";
            r5 = java.lang.Boolean.valueOf(r32);
            r7.put(r4, r5);
            r4 = "is_notification";
            r5 = java.lang.Boolean.valueOf(r33);
            r7.put(r4, r5);
            r4 = "is_alarm";
            r5 = java.lang.Boolean.valueOf(r34);
            r7.put(r4, r5);
            r4 = "is_music";
            r5 = java.lang.Boolean.valueOf(r35);
            r7.put(r4, r5);
            r4 = "is_podcast";
            r5 = java.lang.Boolean.valueOf(r36);
            r7.put(r4, r5);
        L_0x00c9:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r26 = r4.mFilesUri;
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r17 = r4.mMediaInserter;
            r0 = r30;
            r4 = r0.mNoMedia;
            if (r4 != 0) goto L_0x00f1;
        L_0x00df:
            r0 = r30;
            r4 = r0.mFileType;
            r4 = android.media.MediaFile.isVideoFileType(r4);
            if (r4 == 0) goto L_0x025c;
        L_0x00e9:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r26 = r4.mVideoUri;
        L_0x00f1:
            r6 = 0;
            r21 = 0;
            r4 = 0;
            r4 = (r24 > r4 ? 1 : (r24 == r4 ? 0 : -1));
            if (r4 != 0) goto L_0x0302;
        L_0x00fa:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mMtpObjectHandle;
            if (r4 == 0) goto L_0x0115;
        L_0x0104:
            r4 = "media_scanner_new_object_id";
            r0 = r30;
            r5 = android.media.MediaScanner.this;
            r5 = r5.mMtpObjectHandle;
            r5 = java.lang.Integer.valueOf(r5);
            r7.put(r4, r5);
        L_0x0115:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mFilesUri;
            r0 = r26;
            if (r0 != r4) goto L_0x013c;
        L_0x0121:
            r0 = r31;
            r15 = r0.mFormat;
            if (r15 != 0) goto L_0x0133;
        L_0x0127:
            r0 = r31;
            r4 = r0.mPath;
            r0 = r30;
            r5 = r0.mMimeType;
            r15 = android.media.MediaFile.getFormatCode(r4, r5);
        L_0x0133:
            r4 = "format";
            r5 = java.lang.Integer.valueOf(r15);
            r7.put(r4, r5);
        L_0x013c:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mWasEmptyPriorToScan;
            if (r4 == 0) goto L_0x0176;
        L_0x0146:
            if (r33 == 0) goto L_0x0284;
        L_0x0148:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mDefaultNotificationSet;
            if (r4 != 0) goto L_0x0284;
        L_0x0152:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mDefaultNotificationFilename;
            r4 = android.text.TextUtils.isEmpty(r4);
            if (r4 != 0) goto L_0x0174;
        L_0x0160:
            r0 = r31;
            r4 = r0.mPath;
            r0 = r30;
            r5 = android.media.MediaScanner.this;
            r5 = r5.mDefaultNotificationFilename;
            r0 = r30;
            r4 = r0.doesPathHaveFilename(r4, r5);
            if (r4 == 0) goto L_0x0176;
        L_0x0174:
            r21 = 1;
        L_0x0176:
            if (r17 == 0) goto L_0x017a;
        L_0x0178:
            if (r21 == 0) goto L_0x02e8;
        L_0x017a:
            if (r17 == 0) goto L_0x017f;
        L_0x017c:
            r17.flushAll();
        L_0x017f:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mMediaProvider;
            r0 = r30;
            r5 = android.media.MediaScanner.this;
            r5 = r5.mPackageName;
            r0 = r26;
            r6 = r4.insert(r5, r0, r7);
        L_0x0195:
            if (r6 == 0) goto L_0x01a1;
        L_0x0197:
            r24 = android.content.ContentUris.parseId(r6);
            r0 = r24;
            r2 = r31;
            r2.mRowId = r0;
        L_0x01a1:
            if (r21 == 0) goto L_0x01b8;
        L_0x01a3:
            if (r33 == 0) goto L_0x0366;
        L_0x01a5:
            r4 = "notification_sound";
            r0 = r30;
            r1 = r26;
            r2 = r24;
            r0.setSettingIfNotSet(r4, r1, r2);
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r5 = 1;
            r4.mDefaultNotificationSet = r5;
        L_0x01b8:
            return r6;
        L_0x01b9:
            r23 = r16;
            goto L_0x005d;
        L_0x01bd:
            r0 = r30;
            r4 = r0.mFileType;
            r5 = 31;
            if (r4 != r5) goto L_0x00c9;
        L_0x01c5:
            r0 = r30;
            r4 = r0.mNoMedia;
            if (r4 != 0) goto L_0x00c9;
        L_0x01cb:
            r12 = 0;
            r13 = new android.media.ExifInterface;	 Catch:{ IOException -> 0x0394 }
            r0 = r31;
            r4 = r0.mPath;	 Catch:{ IOException -> 0x0394 }
            r13.<init>(r4);	 Catch:{ IOException -> 0x0394 }
            r12 = r13;
        L_0x01d6:
            if (r12 == 0) goto L_0x00c9;
        L_0x01d8:
            r4 = 2;
            r0 = new float[r4];
            r19 = r0;
            r0 = r19;
            r4 = r12.getLatLong(r0);
            if (r4 == 0) goto L_0x01fd;
        L_0x01e5:
            r4 = "latitude";
            r5 = 0;
            r5 = r19[r5];
            r5 = java.lang.Float.valueOf(r5);
            r7.put(r4, r5);
            r4 = "longitude";
            r5 = 1;
            r5 = r19[r5];
            r5 = java.lang.Float.valueOf(r5);
            r7.put(r4, r5);
        L_0x01fd:
            r28 = r12.getGpsDateTime();
            r4 = -1;
            r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1));
            if (r4 == 0) goto L_0x022b;
        L_0x0207:
            r4 = "datetaken";
            r5 = java.lang.Long.valueOf(r28);
            r7.put(r4, r5);
        L_0x0210:
            r4 = "Orientation";
            r5 = -1;
            r22 = r12.getAttributeInt(r4, r5);
            r4 = -1;
            r0 = r22;
            if (r0 == r4) goto L_0x00c9;
        L_0x021c:
            switch(r22) {
                case 3: goto L_0x0256;
                case 4: goto L_0x021f;
                case 5: goto L_0x021f;
                case 6: goto L_0x0253;
                case 7: goto L_0x021f;
                case 8: goto L_0x0259;
                default: goto L_0x021f;
            };
        L_0x021f:
            r11 = 0;
        L_0x0220:
            r4 = "orientation";
            r5 = java.lang.Integer.valueOf(r11);
            r7.put(r4, r5);
            goto L_0x00c9;
        L_0x022b:
            r28 = r12.getDateTime();
            r4 = -1;
            r4 = (r28 > r4 ? 1 : (r28 == r4 ? 0 : -1));
            if (r4 == 0) goto L_0x0210;
        L_0x0235:
            r0 = r30;
            r4 = r0.mLastModified;
            r8 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r4 = r4 * r8;
            r4 = r4 - r28;
            r4 = java.lang.Math.abs(r4);
            r8 = 86400000; // 0x5265c00 float:7.82218E-36 double:4.2687272E-316;
            r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1));
            if (r4 < 0) goto L_0x0210;
        L_0x0249:
            r4 = "datetaken";
            r5 = java.lang.Long.valueOf(r28);
            r7.put(r4, r5);
            goto L_0x0210;
        L_0x0253:
            r11 = 90;
            goto L_0x0220;
        L_0x0256:
            r11 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x0220;
        L_0x0259:
            r11 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x0220;
        L_0x025c:
            r0 = r30;
            r4 = r0.mFileType;
            r4 = android.media.MediaFile.isImageFileType(r4);
            if (r4 == 0) goto L_0x0270;
        L_0x0266:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r26 = r4.mImagesUri;
            goto L_0x00f1;
        L_0x0270:
            r0 = r30;
            r4 = r0.mFileType;
            r4 = android.media.MediaFile.isAudioFileType(r4);
            if (r4 == 0) goto L_0x00f1;
        L_0x027a:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r26 = r4.mAudioUri;
            goto L_0x00f1;
        L_0x0284:
            if (r32 == 0) goto L_0x02b6;
        L_0x0286:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mDefaultRingtoneSet;
            if (r4 != 0) goto L_0x02b6;
        L_0x0290:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mDefaultRingtoneFilename;
            r4 = android.text.TextUtils.isEmpty(r4);
            if (r4 != 0) goto L_0x02b2;
        L_0x029e:
            r0 = r31;
            r4 = r0.mPath;
            r0 = r30;
            r5 = android.media.MediaScanner.this;
            r5 = r5.mDefaultRingtoneFilename;
            r0 = r30;
            r4 = r0.doesPathHaveFilename(r4, r5);
            if (r4 == 0) goto L_0x0176;
        L_0x02b2:
            r21 = 1;
            goto L_0x0176;
        L_0x02b6:
            if (r34 == 0) goto L_0x0176;
        L_0x02b8:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mDefaultAlarmSet;
            if (r4 != 0) goto L_0x0176;
        L_0x02c2:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mDefaultAlarmAlertFilename;
            r4 = android.text.TextUtils.isEmpty(r4);
            if (r4 != 0) goto L_0x02e4;
        L_0x02d0:
            r0 = r31;
            r4 = r0.mPath;
            r0 = r30;
            r5 = android.media.MediaScanner.this;
            r5 = r5.mDefaultAlarmAlertFilename;
            r0 = r30;
            r4 = r0.doesPathHaveFilename(r4, r5);
            if (r4 == 0) goto L_0x0176;
        L_0x02e4:
            r21 = 1;
            goto L_0x0176;
        L_0x02e8:
            r0 = r31;
            r4 = r0.mFormat;
            r5 = 12289; // 0x3001 float:1.722E-41 double:6.0716E-320;
            if (r4 != r5) goto L_0x02f9;
        L_0x02f0:
            r0 = r17;
            r1 = r26;
            r0.insertwithPriority(r1, r7);
            goto L_0x0195;
        L_0x02f9:
            r0 = r17;
            r1 = r26;
            r0.insert(r1, r7);
            goto L_0x0195;
        L_0x0302:
            r0 = r26;
            r1 = r24;
            r6 = android.content.ContentUris.withAppendedId(r0, r1);
            r4 = "_data";
            r7.remove(r4);
            r20 = 0;
            r0 = r31;
            r4 = r0.mPath;
            r4 = android.media.MediaScanner.isNoMediaPath(r4);
            if (r4 != 0) goto L_0x0334;
        L_0x031b:
            r0 = r30;
            r4 = r0.mMimeType;
            r14 = android.media.MediaFile.getFileTypeForMimeType(r4);
            r4 = android.media.MediaFile.isAudioFileType(r14);
            if (r4 == 0) goto L_0x034b;
        L_0x0329:
            r20 = 2;
        L_0x032b:
            r4 = "media_type";
            r5 = java.lang.Integer.valueOf(r20);
            r7.put(r4, r5);
        L_0x0334:
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r4 = r4.mMediaProvider;
            r0 = r30;
            r5 = android.media.MediaScanner.this;
            r5 = r5.mPackageName;
            r8 = 0;
            r9 = 0;
            r4.update(r5, r6, r7, r8, r9);
            goto L_0x01a1;
        L_0x034b:
            r4 = android.media.MediaFile.isVideoFileType(r14);
            if (r4 == 0) goto L_0x0354;
        L_0x0351:
            r20 = 3;
            goto L_0x032b;
        L_0x0354:
            r4 = android.media.MediaFile.isImageFileType(r14);
            if (r4 == 0) goto L_0x035d;
        L_0x035a:
            r20 = 1;
            goto L_0x032b;
        L_0x035d:
            r4 = android.media.MediaFile.isPlayListFileType(r14);
            if (r4 == 0) goto L_0x032b;
        L_0x0363:
            r20 = 4;
            goto L_0x032b;
        L_0x0366:
            if (r32 == 0) goto L_0x037d;
        L_0x0368:
            r4 = "ringtone";
            r0 = r30;
            r1 = r26;
            r2 = r24;
            r0.setSettingIfNotSet(r4, r1, r2);
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r5 = 1;
            r4.mDefaultRingtoneSet = r5;
            goto L_0x01b8;
        L_0x037d:
            if (r34 == 0) goto L_0x01b8;
        L_0x037f:
            r4 = "alarm_alert";
            r0 = r30;
            r1 = r26;
            r2 = r24;
            r0.setSettingIfNotSet(r4, r1, r2);
            r0 = r30;
            r4 = android.media.MediaScanner.this;
            r5 = 1;
            r4.mDefaultAlarmSet = r5;
            goto L_0x01b8;
        L_0x0394:
            r4 = move-exception;
            goto L_0x01d6;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScanner.MyMediaScannerClient.endFile(android.media.MediaScanner$FileEntry, boolean, boolean, boolean, boolean, boolean):android.net.Uri");
        }

        private boolean doesPathHaveFilename(String path, String filename) {
            int pathFilenameStart = path.lastIndexOf(File.separatorChar) + MediaScanner.PATH_PLAYLISTS_COLUMN_INDEX;
            int filenameLength = filename.length();
            if (path.regionMatches(pathFilenameStart, filename, MediaScanner.ID_PLAYLISTS_COLUMN_INDEX, filenameLength) && pathFilenameStart + filenameLength == path.length()) {
                return MediaScanner.ENABLE_BULK_INSERTS;
            }
            return false;
        }

        private void setSettingIfNotSet(String settingName, Uri uri, long rowId) {
            if (TextUtils.isEmpty(System.getString(MediaScanner.this.mContext.getContentResolver(), settingName))) {
                System.putString(MediaScanner.this.mContext.getContentResolver(), settingName, ContentUris.withAppendedId(uri, rowId).toString());
            }
        }

        private int getFileTypeFromDrm(String path) {
            if (!MediaScanner.this.isDrmEnabled()) {
                return MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            }
            if (MediaScanner.this.mDrmManagerClient == null) {
                MediaScanner.this.mDrmManagerClient = new DrmManagerClient(MediaScanner.this.mContext);
            }
            if (!MediaScanner.this.mDrmManagerClient.canHandle(path, null)) {
                return MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            }
            this.mIsDrm = MediaScanner.ENABLE_BULK_INSERTS;
            String drmMimetype = MediaScanner.this.mDrmManagerClient.getOriginalMimeType(path);
            if (drmMimetype == null) {
                return MediaScanner.ID_PLAYLISTS_COLUMN_INDEX;
            }
            this.mMimeType = drmMimetype;
            return MediaFile.getFileTypeForMimeType(drmMimetype);
        }
    }

    private static class PlaylistEntry {
        long bestmatchid;
        int bestmatchlevel;
        String path;

        private PlaylistEntry() {
        }
    }

    private final native void native_finalize();

    private static final native void native_init();

    private final native void native_setup();

    private native void processDirectory(String str, MediaScannerClient mediaScannerClient);

    private native void processFile(String str, String str2, MediaScannerClient mediaScannerClient);

    public native byte[] extractAlbumArt(FileDescriptor fileDescriptor);

    public void scanMtpFile(java.lang.String r28, java.lang.String r29, int r30, int r31) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x010d in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r27 = this;
        r0 = r27;
        r1 = r29;
        r0.initialize(r1);
        r26 = android.media.MediaFile.getFileType(r28);
        if (r26 != 0) goto L_0x0075;
    L_0x000d:
        r25 = 0;
    L_0x000f:
        r23 = new java.io.File;
        r0 = r23;
        r1 = r28;
        r0.<init>(r1);
        r4 = r23.lastModified();
        r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r14 = r4 / r10;
        r4 = android.media.MediaFile.isAudioFileType(r25);
        if (r4 != 0) goto L_0x0087;
    L_0x0026:
        r4 = android.media.MediaFile.isVideoFileType(r25);
        if (r4 != 0) goto L_0x0087;
    L_0x002c:
        r4 = android.media.MediaFile.isImageFileType(r25);
        if (r4 != 0) goto L_0x0087;
    L_0x0032:
        r4 = android.media.MediaFile.isPlayListFileType(r25);
        if (r4 != 0) goto L_0x0087;
    L_0x0038:
        r4 = android.media.MediaFile.isDrmFileType(r25);
        if (r4 != 0) goto L_0x0087;
    L_0x003e:
        r7 = new android.content.ContentValues;
        r7.<init>();
        r4 = "_size";
        r10 = r23.length();
        r5 = java.lang.Long.valueOf(r10);
        r7.put(r4, r5);
        r4 = "date_modified";
        r5 = java.lang.Long.valueOf(r14);
        r7.put(r4, r5);
        r4 = 1;
        r9 = new java.lang.String[r4];	 Catch:{ RemoteException -> 0x007c }
        r4 = 0;	 Catch:{ RemoteException -> 0x007c }
        r5 = java.lang.Integer.toString(r30);	 Catch:{ RemoteException -> 0x007c }
        r9[r4] = r5;	 Catch:{ RemoteException -> 0x007c }
        r0 = r27;	 Catch:{ RemoteException -> 0x007c }
        r4 = r0.mMediaProvider;	 Catch:{ RemoteException -> 0x007c }
        r0 = r27;	 Catch:{ RemoteException -> 0x007c }
        r5 = r0.mPackageName;	 Catch:{ RemoteException -> 0x007c }
        r6 = android.provider.MediaStore.Files.getMtpObjectsUri(r29);	 Catch:{ RemoteException -> 0x007c }
        r8 = "_id=?";	 Catch:{ RemoteException -> 0x007c }
        r4.update(r5, r6, r7, r8, r9);	 Catch:{ RemoteException -> 0x007c }
    L_0x0074:
        return;
    L_0x0075:
        r0 = r26;
        r0 = r0.fileType;
        r25 = r0;
        goto L_0x000f;
    L_0x007c:
        r21 = move-exception;
        r4 = "MediaScanner";
        r5 = "RemoteException in scanMtpFile";
        r0 = r21;
        android.util.Log.e(r4, r5, r0);
        goto L_0x0074;
    L_0x0087:
        r0 = r30;
        r1 = r27;
        r1.mMtpObjectHandle = r0;
        r24 = 0;
        r4 = android.media.MediaFile.isPlayListFileType(r25);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        if (r4 == 0) goto L_0x00d1;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
    L_0x0095:
        r4 = 0;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r5 = 1;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r27;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0.prescan(r4, r5);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r22 = r27.makeEntryFor(r28);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        if (r22 == 0) goto L_0x00c3;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
    L_0x00a2:
        r0 = r27;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r10 = r0.mMediaProvider;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r27;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r11 = r0.mPackageName;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r27;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r12 = r0.mFilesUri;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r13 = FILES_PRESCAN_PROJECTION;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r14 = 0;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r15 = 0;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r16 = 0;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r17 = 0;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r24 = r10.query(r11, r12, r13, r14, r15, r16, r17);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r27;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r1 = r22;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r2 = r24;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0.processPlayList(r1, r2);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
    L_0x00c3:
        r4 = 0;
        r0 = r27;
        r0.mMtpObjectHandle = r4;
        if (r24 == 0) goto L_0x00cd;
    L_0x00ca:
        r24.close();
    L_0x00cd:
        r27.releaseResources();
        goto L_0x0074;
    L_0x00d1:
        r4 = 0;
        r0 = r27;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r1 = r28;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0.prescan(r1, r4);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r27;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r11 = r0.mClient;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r26;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r13 = r0.mimeType;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r16 = r23.length();	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r4 = 12289; // 0x3001 float:1.722E-41 double:6.0716E-320;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r31;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        if (r0 != r4) goto L_0x0112;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
    L_0x00eb:
        r18 = 1;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
    L_0x00ed:
        r19 = 1;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r20 = isNoMediaPath(r28);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r12 = r28;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r11.doScanFile(r12, r13, r14, r16, r18, r19, r20);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        goto L_0x00c3;
    L_0x00f9:
        r21 = move-exception;
        r4 = "MediaScanner";	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r5 = "RemoteException in MediaScanner.scanFile()";	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r0 = r21;	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        android.util.Log.e(r4, r5, r0);	 Catch:{ RemoteException -> 0x00f9, all -> 0x0115 }
        r4 = 0;
        r0 = r27;
        r0.mMtpObjectHandle = r4;
        if (r24 == 0) goto L_0x010d;
    L_0x010a:
        r24.close();
    L_0x010d:
        r27.releaseResources();
        goto L_0x0074;
    L_0x0112:
        r18 = 0;
        goto L_0x00ed;
    L_0x0115:
        r4 = move-exception;
        r5 = 0;
        r0 = r27;
        r0.mMtpObjectHandle = r5;
        if (r24 == 0) goto L_0x0120;
    L_0x011d:
        r24.close();
    L_0x0120:
        r27.releaseResources();
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScanner.scanMtpFile(java.lang.String, java.lang.String, int, int):void");
    }

    public android.net.Uri scanSingleFile(java.lang.String r13, java.lang.String r14, java.lang.String r15) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:? in {12, 14, 15, 16, 18, 19} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r12 = this;
        r12.initialize(r14);	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r1 = 1;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r12.prescan(r13, r1);	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r11 = new java.io.File;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r11.<init>(r13);	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r1 = r11.exists();	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        if (r1 != 0) goto L_0x0017;
    L_0x0012:
        r1 = 0;
        r12.releaseResources();
    L_0x0016:
        return r1;
    L_0x0017:
        r2 = r11.lastModified();	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r4 = r2 / r6;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r1 = r12.mClient;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r6 = r11.length();	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r8 = 0;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r9 = 1;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r10 = isNoMediaPath(r13);	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r2 = r13;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r3 = r15;	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r1 = r1.doScanFile(r2, r3, r4, r6, r8, r9, r10);	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r12.releaseResources();
        goto L_0x0016;
    L_0x0035:
        r0 = move-exception;
        r1 = "MediaScanner";	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r2 = "RemoteException in MediaScanner.scanFile()";	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        android.util.Log.e(r1, r2, r0);	 Catch:{ RemoteException -> 0x0035, all -> 0x0042 }
        r1 = 0;
        r12.releaseResources();
        goto L_0x0016;
    L_0x0042:
        r1 = move-exception;
        r12.releaseResources();
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScanner.scanSingleFile(java.lang.String, java.lang.String, java.lang.String):android.net.Uri");
    }

    public native void setLocale(String str);

    static {
        System.loadLibrary("media_jni");
        native_init();
        FILES_PRESCAN_PROJECTION = new String[]{"_id", "_data", "format", "date_modified"};
        String[] strArr = new String[PATH_PLAYLISTS_COLUMN_INDEX];
        strArr[ID_PLAYLISTS_COLUMN_INDEX] = "_id";
        ID_PROJECTION = strArr;
        strArr = new String[PATH_PLAYLISTS_COLUMN_INDEX];
        strArr[ID_PLAYLISTS_COLUMN_INDEX] = "playlist_id";
        PLAYLIST_MEMBERS_PROJECTION = strArr;
        strArr = new String[R.styleable.Theme_popupMenuStyle];
        strArr[ID_PLAYLISTS_COLUMN_INDEX] = "Blues";
        strArr[PATH_PLAYLISTS_COLUMN_INDEX] = "Classic Rock";
        strArr[FILES_PRESCAN_FORMAT_COLUMN_INDEX] = "Country";
        strArr[FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX] = "Dance";
        strArr[4] = "Disco";
        strArr[5] = "Funk";
        strArr[6] = "Grunge";
        strArr[7] = "Hip-Hop";
        strArr[8] = "Jazz";
        strArr[9] = "Metal";
        strArr[10] = "New Age";
        strArr[11] = "Oldies";
        strArr[12] = "Other";
        strArr[13] = "Pop";
        strArr[14] = "R&B";
        strArr[15] = "Rap";
        strArr[16] = "Reggae";
        strArr[17] = "Rock";
        strArr[18] = "Techno";
        strArr[19] = "Industrial";
        strArr[20] = "Alternative";
        strArr[21] = "Ska";
        strArr[22] = "Death Metal";
        strArr[23] = "Pranks";
        strArr[24] = "Soundtrack";
        strArr[25] = "Euro-Techno";
        strArr[26] = "Ambient";
        strArr[27] = "Trip-Hop";
        strArr[28] = "Vocal";
        strArr[29] = "Jazz+Funk";
        strArr[30] = "Fusion";
        strArr[31] = "Trance";
        strArr[32] = "Classical";
        strArr[33] = "Instrumental";
        strArr[34] = "Acid";
        strArr[35] = "House";
        strArr[36] = "Game";
        strArr[37] = "Sound Clip";
        strArr[38] = "Gospel";
        strArr[39] = "Noise";
        strArr[40] = "AlternRock";
        strArr[41] = "Bass";
        strArr[42] = "Soul";
        strArr[43] = "Punk";
        strArr[44] = "Space";
        strArr[45] = "Meditative";
        strArr[46] = "Instrumental Pop";
        strArr[47] = "Instrumental Rock";
        strArr[48] = "Ethnic";
        strArr[49] = "Gothic";
        strArr[50] = "Darkwave";
        strArr[51] = "Techno-Industrial";
        strArr[52] = "Electronic";
        strArr[53] = "Pop-Folk";
        strArr[54] = "Eurodance";
        strArr[55] = "Dream";
        strArr[56] = "Southern Rock";
        strArr[57] = "Comedy";
        strArr[58] = "Cult";
        strArr[59] = "Gangsta";
        strArr[60] = "Top 40";
        strArr[61] = "Christian Rap";
        strArr[62] = "Pop/Funk";
        strArr[63] = "Jungle";
        strArr[64] = "Native American";
        strArr[65] = "Cabaret";
        strArr[66] = "New Wave";
        strArr[67] = "Psychadelic";
        strArr[68] = "Rave";
        strArr[69] = "Showtunes";
        strArr[70] = "Trailer";
        strArr[71] = "Lo-Fi";
        strArr[72] = "Tribal";
        strArr[73] = "Acid Punk";
        strArr[74] = "Acid Jazz";
        strArr[75] = "Polka";
        strArr[76] = "Retro";
        strArr[77] = "Musical";
        strArr[78] = "Rock & Roll";
        strArr[79] = "Hard Rock";
        strArr[80] = "Folk";
        strArr[81] = "Folk-Rock";
        strArr[82] = "National Folk";
        strArr[83] = "Swing";
        strArr[84] = "Fast Fusion";
        strArr[85] = "Bebob";
        strArr[86] = "Latin";
        strArr[87] = "Revival";
        strArr[88] = "Celtic";
        strArr[89] = "Bluegrass";
        strArr[90] = "Avantgarde";
        strArr[91] = "Gothic Rock";
        strArr[92] = "Progressive Rock";
        strArr[93] = "Psychedelic Rock";
        strArr[94] = "Symphonic Rock";
        strArr[95] = "Slow Rock";
        strArr[96] = "Big Band";
        strArr[97] = "Chorus";
        strArr[98] = "Easy Listening";
        strArr[99] = "Acoustic";
        strArr[100] = "Humour";
        strArr[RILConstants.RIL_REQUEST_SET_SMSC_ADDRESS] = "Speech";
        strArr[RILConstants.RIL_REQUEST_REPORT_SMS_MEMORY_STATUS] = "Chanson";
        strArr[RILConstants.RIL_REQUEST_REPORT_STK_SERVICE_IS_RUNNING] = "Opera";
        strArr[RILConstants.RIL_REQUEST_CDMA_GET_SUBSCRIPTION_SOURCE] = "Chamber Music";
        strArr[RILConstants.RIL_REQUEST_ISIM_AUTHENTICATION] = "Sonata";
        strArr[RILConstants.RIL_REQUEST_ACKNOWLEDGE_INCOMING_GSM_SMS_WITH_PDU] = "Symphony";
        strArr[RILConstants.RIL_REQUEST_STK_SEND_ENVELOPE_WITH_STATUS] = "Booty Bass";
        strArr[RILConstants.RIL_REQUEST_VOICE_RADIO_TECH] = "Primus";
        strArr[RILConstants.RIL_REQUEST_GET_CELL_INFO_LIST] = "Porn Groove";
        strArr[RILConstants.RIL_REQUEST_SET_UNSOL_CELL_INFO_LIST_RATE] = "Satire";
        strArr[RILConstants.RIL_REQUEST_SET_INITIAL_ATTACH_APN] = "Slow Jam";
        strArr[RILConstants.RIL_REQUEST_IMS_REGISTRATION_STATE] = "Club";
        strArr[RILConstants.RIL_REQUEST_IMS_SEND_SMS] = "Tango";
        strArr[RILConstants.RIL_REQUEST_SIM_TRANSMIT_APDU_BASIC] = "Samba";
        strArr[RILConstants.RIL_REQUEST_SIM_OPEN_CHANNEL] = "Folklore";
        strArr[RILConstants.RIL_REQUEST_SIM_CLOSE_CHANNEL] = "Ballad";
        strArr[RILConstants.RIL_REQUEST_SIM_TRANSMIT_APDU_CHANNEL] = "Power Ballad";
        strArr[RILConstants.RIL_REQUEST_NV_READ_ITEM] = "Rhythmic Soul";
        strArr[RILConstants.RIL_REQUEST_NV_WRITE_ITEM] = "Freestyle";
        strArr[RILConstants.RIL_REQUEST_NV_WRITE_CDMA_PRL] = "Duet";
        strArr[RILConstants.RIL_REQUEST_NV_RESET_CONFIG] = "Punk Rock";
        strArr[RILConstants.RIL_REQUEST_SET_UICC_SUBSCRIPTION] = "Drum Solo";
        strArr[RILConstants.RIL_REQUEST_ALLOW_DATA] = "A capella";
        strArr[RILConstants.RIL_REQUEST_GET_HARDWARE_CONFIG] = "Euro-House";
        strArr[RILConstants.RIL_REQUEST_SIM_AUTHENTICATION] = "Dance Hall";
        strArr[RILConstants.RIL_REQUEST_GET_DC_RT_INFO] = "Goa";
        strArr[RILConstants.RIL_REQUEST_SET_DC_RT_INFO_RATE] = "Drum & Bass";
        strArr[RILConstants.RIL_REQUEST_SET_DATA_PROFILE] = "Club-House";
        strArr[RILConstants.RIL_REQUEST_SHUTDOWN] = "Hardcore";
        strArr[RILConstants.RIL_REQUEST_GET_RADIO_CAPABILITY] = "Terror";
        strArr[RILConstants.RIL_REQUEST_SET_RADIO_CAPABILITY] = "Indie";
        strArr[R.styleable.Theme_dropDownSpinnerStyle] = "Britpop";
        strArr[R.styleable.Theme_actionDropDownStyle] = null;
        strArr[SmsConstants.MAX_USER_DATA_BYTES_WITH_HEADER] = "Polsk Punk";
        strArr[R.styleable.Theme_actionModeBackground] = "Beat";
        strArr[R.styleable.Theme_actionModeCloseDrawable] = "Christian Gangsta";
        strArr[R.styleable.Theme_windowActionModeOverlay] = "Heavy Metal";
        strArr[R.styleable.Theme_windowActionBarOverlay] = "Black Metal";
        strArr[R.styleable.Theme_actionBarSize] = "Crossover";
        strArr[SmsConstants.MAX_USER_DATA_BYTES] = "Contemporary Christian";
        strArr[R.styleable.Theme_actionBarTabStyle] = "Christian Rock";
        strArr[R.styleable.Theme_actionBarTabBarStyle] = "Merengue";
        strArr[R.styleable.Theme_actionBarTabTextStyle] = "Salsa";
        strArr[R.styleable.Theme_actionOverflowButtonStyle] = "Thrash Metal";
        strArr[R.styleable.Theme_actionModeCloseButtonStyle] = "Anime";
        strArr[R.styleable.Theme_activatedBackgroundIndicator] = "JPop";
        strArr[R.styleable.Theme_listPopupWindowStyle] = "Synthpop";
        ID3_GENRES = strArr;
        mNoMediaPaths = new HashMap();
        mMediaPaths = new HashMap();
    }

    public MediaScanner(Context c) {
        this.mWasEmptyPriorToScan = false;
        this.mBitmapOptions = new Options();
        this.mPlaylistEntries = new ArrayList();
        this.mDrmManagerClient = null;
        this.mClient = new MyMediaScannerClient();
        native_setup();
        this.mContext = c;
        this.mPackageName = c.getPackageName();
        this.mBitmapOptions.inSampleSize = PATH_PLAYLISTS_COLUMN_INDEX;
        this.mBitmapOptions.inJustDecodeBounds = ENABLE_BULK_INSERTS;
        setDefaultRingtoneFileNames();
        this.mExternalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.mExternalIsEmulated = Environment.isExternalStorageEmulated();
    }

    private void setDefaultRingtoneFileNames() {
        this.mDefaultRingtoneFilename = SystemProperties.get("ro.config.ringtone");
        this.mDefaultNotificationFilename = SystemProperties.get("ro.config.notification_sound");
        this.mDefaultAlarmAlertFilename = SystemProperties.get("ro.config.alarm_alert");
    }

    private boolean isDrmEnabled() {
        String prop = SystemProperties.get("drm.service.enabled");
        return (prop == null || !prop.equals("true")) ? false : ENABLE_BULK_INSERTS;
    }

    private void prescan(String filePath, boolean prescanFiles) throws RemoteException {
        String where;
        String[] selectionArgs;
        Cursor c = null;
        if (this.mPlayLists == null) {
            this.mPlayLists = new ArrayList();
        } else {
            this.mPlayLists.clear();
        }
        if (filePath != null) {
            where = "_id>? AND _data=?";
            selectionArgs = new String[FILES_PRESCAN_FORMAT_COLUMN_INDEX];
            selectionArgs[ID_PLAYLISTS_COLUMN_INDEX] = "";
            selectionArgs[PATH_PLAYLISTS_COLUMN_INDEX] = filePath;
        } else {
            where = "_id>?";
            selectionArgs = new String[PATH_PLAYLISTS_COLUMN_INDEX];
            selectionArgs[ID_PLAYLISTS_COLUMN_INDEX] = "";
        }
        Builder builder = this.mFilesUri.buildUpon();
        builder.appendQueryParameter("deletedata", "false");
        MediaBulkDeleter mediaBulkDeleter = new MediaBulkDeleter(this.mMediaProvider, this.mPackageName, builder.build());
        if (prescanFiles) {
            long lastId = Long.MIN_VALUE;
            Uri limitUri = this.mFilesUri.buildUpon().appendQueryParameter("limit", "1000").build();
            this.mWasEmptyPriorToScan = ENABLE_BULK_INSERTS;
            while (true) {
                selectionArgs[ID_PLAYLISTS_COLUMN_INDEX] = "" + lastId;
                if (c != null) {
                    c.close();
                }
                c = this.mMediaProvider.query(this.mPackageName, limitUri, FILES_PRESCAN_PROJECTION, where, selectionArgs, "_id", null);
                if (c == null) {
                    break;
                }
                try {
                    if (c.getCount() == 0) {
                        break;
                    }
                    this.mWasEmptyPriorToScan = false;
                    while (c.moveToNext()) {
                        long rowId = c.getLong(ID_PLAYLISTS_COLUMN_INDEX);
                        String path = c.getString(PATH_PLAYLISTS_COLUMN_INDEX);
                        int format = c.getInt(FILES_PRESCAN_FORMAT_COLUMN_INDEX);
                        long lastModified = c.getLong(FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX);
                        lastId = rowId;
                        if (path != null) {
                            if (path.startsWith("/")) {
                                boolean exists = false;
                                try {
                                    exists = Os.access(path, OsConstants.F_OK);
                                } catch (ErrnoException e) {
                                }
                                if (exists) {
                                    continue;
                                } else if (MtpConstants.isAbstractObject(format)) {
                                    continue;
                                } else {
                                    int fileType;
                                    MediaFileType mediaFileType = MediaFile.getFileType(path);
                                    if (mediaFileType == null) {
                                        fileType = ID_PLAYLISTS_COLUMN_INDEX;
                                    } else {
                                        fileType = mediaFileType.fileType;
                                    }
                                    if (!MediaFile.isPlayListFileType(fileType)) {
                                        mediaBulkDeleter.delete(rowId);
                                        if (path.toLowerCase(Locale.US).endsWith("/.nomedia")) {
                                            mediaBulkDeleter.flush();
                                            this.mMediaProvider.call(this.mPackageName, "unhide", new File(path).getParent(), null);
                                        }
                                    }
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                } catch (Throwable th) {
                    if (c != null) {
                        c.close();
                    }
                    mediaBulkDeleter.flush();
                }
            }
        }
        if (c != null) {
            c.close();
        }
        mediaBulkDeleter.flush();
        this.mOriginalCount = ID_PLAYLISTS_COLUMN_INDEX;
        c = this.mMediaProvider.query(this.mPackageName, this.mImagesUri, ID_PROJECTION, null, null, null, null);
        if (c != null) {
            this.mOriginalCount = c.getCount();
            c.close();
        }
    }

    private boolean inScanDirectory(String path, String[] directories) {
        for (int i = ID_PLAYLISTS_COLUMN_INDEX; i < directories.length; i += PATH_PLAYLISTS_COLUMN_INDEX) {
            if (path.startsWith(directories[i])) {
                return ENABLE_BULK_INSERTS;
            }
        }
        return false;
    }

    private void pruneDeadThumbnailFiles() {
        HashSet<String> existingFiles = new HashSet();
        String directory = "/sdcard/DCIM/.thumbnails";
        String[] files = new File(directory).list();
        Cursor c = null;
        if (files == null) {
            files = new String[ID_PLAYLISTS_COLUMN_INDEX];
        }
        for (int i = ID_PLAYLISTS_COLUMN_INDEX; i < files.length; i += PATH_PLAYLISTS_COLUMN_INDEX) {
            existingFiles.add(directory + "/" + files[i]);
        }
        try {
            IContentProvider iContentProvider = this.mMediaProvider;
            String str = this.mPackageName;
            Uri uri = this.mThumbsUri;
            String[] strArr = new String[PATH_PLAYLISTS_COLUMN_INDEX];
            strArr[ID_PLAYLISTS_COLUMN_INDEX] = "_data";
            c = iContentProvider.query(str, uri, strArr, null, null, null, null);
            Log.v(TAG, "pruneDeadThumbnailFiles... " + c);
            Iterator i$;
            if (c == null || !c.moveToFirst()) {
                i$ = existingFiles.iterator();
                while (i$.hasNext()) {
                    try {
                        new File((String) i$.next()).delete();
                    } catch (SecurityException e) {
                    }
                }
                Log.v(TAG, "/pruneDeadThumbnailFiles... " + c);
                if (c != null) {
                    c.close();
                }
            }
            do {
                existingFiles.remove(c.getString(ID_PLAYLISTS_COLUMN_INDEX));
            } while (c.moveToNext());
            i$ = existingFiles.iterator();
            while (i$.hasNext()) {
                new File((String) i$.next()).delete();
            }
            Log.v(TAG, "/pruneDeadThumbnailFiles... " + c);
            if (c != null) {
                c.close();
            }
        } catch (RemoteException e2) {
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private void postscan(String[] directories) throws RemoteException {
        if (this.mProcessPlaylists) {
            processPlayLists();
        }
        if (this.mOriginalCount == 0 && this.mImagesUri.equals(Media.getContentUri("external"))) {
            pruneDeadThumbnailFiles();
        }
        this.mPlayLists = null;
        this.mMediaProvider = null;
    }

    private void releaseResources() {
        if (this.mDrmManagerClient != null) {
            this.mDrmManagerClient.release();
            this.mDrmManagerClient = null;
        }
    }

    private void initialize(String volumeName) {
        this.mMediaProvider = this.mContext.getContentResolver().acquireProvider("media");
        this.mAudioUri = Audio.Media.getContentUri(volumeName);
        this.mVideoUri = Video.Media.getContentUri(volumeName);
        this.mImagesUri = Media.getContentUri(volumeName);
        this.mThumbsUri = Thumbnails.getContentUri(volumeName);
        this.mFilesUri = Files.getContentUri(volumeName);
        this.mFilesUriNoNotify = this.mFilesUri.buildUpon().appendQueryParameter("nonotify", "1").build();
        if (!volumeName.equals("internal")) {
            this.mProcessPlaylists = ENABLE_BULK_INSERTS;
            this.mProcessGenres = ENABLE_BULK_INSERTS;
            this.mPlaylistsUri = Playlists.getContentUri(volumeName);
            this.mCaseInsensitivePaths = ENABLE_BULK_INSERTS;
        }
    }

    public void scanDirectories(String[] directories, String volumeName) {
        try {
            long start = System.currentTimeMillis();
            initialize(volumeName);
            prescan(null, ENABLE_BULK_INSERTS);
            long prescan = System.currentTimeMillis();
            this.mMediaInserter = new MediaInserter(this.mMediaProvider, this.mPackageName, 500);
            for (int i = ID_PLAYLISTS_COLUMN_INDEX; i < directories.length; i += PATH_PLAYLISTS_COLUMN_INDEX) {
                processDirectory(directories[i], this.mClient);
            }
            this.mMediaInserter.flushAll();
            this.mMediaInserter = null;
            long scan = System.currentTimeMillis();
            postscan(directories);
            System.currentTimeMillis();
        } catch (SQLException e) {
            Log.e(TAG, "SQLException in MediaScanner.scan()", e);
        } catch (UnsupportedOperationException e2) {
            Log.e(TAG, "UnsupportedOperationException in MediaScanner.scan()", e2);
        } catch (RemoteException e3) {
            Log.e(TAG, "RemoteException in MediaScanner.scan()", e3);
        } finally {
            releaseResources();
        }
    }

    private static boolean isNoMediaFile(String path) {
        if (new File(path).isDirectory()) {
            return false;
        }
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash >= 0 && lastSlash + FILES_PRESCAN_FORMAT_COLUMN_INDEX < path.length()) {
            if (path.regionMatches(lastSlash + PATH_PLAYLISTS_COLUMN_INDEX, "._", ID_PLAYLISTS_COLUMN_INDEX, FILES_PRESCAN_FORMAT_COLUMN_INDEX)) {
                return ENABLE_BULK_INSERTS;
            }
            if (path.regionMatches(ENABLE_BULK_INSERTS, path.length() - 4, ".jpg", ID_PLAYLISTS_COLUMN_INDEX, 4)) {
                if (path.regionMatches(ENABLE_BULK_INSERTS, lastSlash + PATH_PLAYLISTS_COLUMN_INDEX, "AlbumArt_{", ID_PLAYLISTS_COLUMN_INDEX, 10)) {
                    return ENABLE_BULK_INSERTS;
                }
                if (path.regionMatches(ENABLE_BULK_INSERTS, lastSlash + PATH_PLAYLISTS_COLUMN_INDEX, "AlbumArt.", ID_PLAYLISTS_COLUMN_INDEX, 9)) {
                    return ENABLE_BULK_INSERTS;
                }
                int length = (path.length() - lastSlash) - 1;
                if (length == 17) {
                    if (path.regionMatches(ENABLE_BULK_INSERTS, lastSlash + PATH_PLAYLISTS_COLUMN_INDEX, "AlbumArtSmall", ID_PLAYLISTS_COLUMN_INDEX, 13)) {
                        return ENABLE_BULK_INSERTS;
                    }
                }
                if (length == 10) {
                    if (path.regionMatches(ENABLE_BULK_INSERTS, lastSlash + PATH_PLAYLISTS_COLUMN_INDEX, "Folder", ID_PLAYLISTS_COLUMN_INDEX, 6)) {
                        return ENABLE_BULK_INSERTS;
                    }
                }
            }
        }
        return false;
    }

    public static void clearMediaPathCache(boolean clearMediaPaths, boolean clearNoMediaPaths) {
        synchronized (MediaScanner.class) {
            if (clearMediaPaths) {
                mMediaPaths.clear();
            }
            if (clearNoMediaPaths) {
                mNoMediaPaths.clear();
            }
        }
    }

    public static boolean isNoMediaPath(String path) {
        if (path == null) {
            return false;
        }
        if (path.indexOf("/.") >= 0) {
            return ENABLE_BULK_INSERTS;
        }
        int firstSlash = path.lastIndexOf(47);
        if (firstSlash <= 0) {
            return false;
        }
        String parent = path.substring(ID_PLAYLISTS_COLUMN_INDEX, firstSlash);
        synchronized (MediaScanner.class) {
            if (mNoMediaPaths.containsKey(parent)) {
                return ENABLE_BULK_INSERTS;
            }
            if (!mMediaPaths.containsKey(parent)) {
                int offset = PATH_PLAYLISTS_COLUMN_INDEX;
                while (offset >= 0) {
                    int slashIndex = path.indexOf(47, offset);
                    if (slashIndex > offset) {
                        slashIndex += PATH_PLAYLISTS_COLUMN_INDEX;
                        if (new File(path.substring(ID_PLAYLISTS_COLUMN_INDEX, slashIndex) + ".nomedia").exists()) {
                            mNoMediaPaths.put(parent, "");
                            return ENABLE_BULK_INSERTS;
                        }
                    }
                    offset = slashIndex;
                }
                mMediaPaths.put(parent, "");
            }
            return isNoMediaFile(path);
        }
    }

    FileEntry makeEntryFor(String path) {
        Cursor c = null;
        try {
            String[] selectionArgs = new String[PATH_PLAYLISTS_COLUMN_INDEX];
            selectionArgs[ID_PLAYLISTS_COLUMN_INDEX] = path;
            c = this.mMediaProvider.query(this.mPackageName, this.mFilesUriNoNotify, FILES_PRESCAN_PROJECTION, "_data=?", selectionArgs, null, null);
            if (c.moveToFirst()) {
                String str = path;
                FileEntry fileEntry = new FileEntry(c.getLong(ID_PLAYLISTS_COLUMN_INDEX), str, c.getLong(FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX), c.getInt(FILES_PRESCAN_FORMAT_COLUMN_INDEX));
                if (c == null) {
                    return fileEntry;
                }
                c.close();
                return fileEntry;
            }
            if (c != null) {
                c.close();
            }
            return null;
        } catch (RemoteException e) {
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int matchPaths(java.lang.String r14, java.lang.String r15) {
        /*
        r13 = this;
        r10 = 0;
        r8 = r14.length();
        r9 = r15.length();
    L_0x0009:
        if (r8 <= 0) goto L_0x003f;
    L_0x000b:
        if (r9 <= 0) goto L_0x003f;
    L_0x000d:
        r0 = 47;
        r1 = r8 + -1;
        r11 = r14.lastIndexOf(r0, r1);
        r0 = 47;
        r1 = r9 + -1;
        r12 = r15.lastIndexOf(r0, r1);
        r0 = 92;
        r1 = r8 + -1;
        r6 = r14.lastIndexOf(r0, r1);
        r0 = 92;
        r1 = r9 + -1;
        r7 = r15.lastIndexOf(r0, r1);
        if (r11 <= r6) goto L_0x0040;
    L_0x002f:
        r2 = r11;
    L_0x0030:
        if (r12 <= r7) goto L_0x0042;
    L_0x0032:
        r4 = r12;
    L_0x0033:
        if (r2 >= 0) goto L_0x0044;
    L_0x0035:
        r2 = 0;
    L_0x0036:
        if (r4 >= 0) goto L_0x0047;
    L_0x0038:
        r4 = 0;
    L_0x0039:
        r5 = r8 - r2;
        r0 = r9 - r4;
        if (r0 == r5) goto L_0x004a;
    L_0x003f:
        return r10;
    L_0x0040:
        r2 = r6;
        goto L_0x0030;
    L_0x0042:
        r4 = r7;
        goto L_0x0033;
    L_0x0044:
        r2 = r2 + 1;
        goto L_0x0036;
    L_0x0047:
        r4 = r4 + 1;
        goto L_0x0039;
    L_0x004a:
        r1 = 1;
        r0 = r14;
        r3 = r15;
        r0 = r0.regionMatches(r1, r2, r3, r4, r5);
        if (r0 == 0) goto L_0x003f;
    L_0x0053:
        r10 = r10 + 1;
        r8 = r2 + -1;
        r9 = r4 + -1;
        goto L_0x0009;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaScanner.matchPaths(java.lang.String, java.lang.String):int");
    }

    private boolean matchEntries(long rowId, String data) {
        int len = this.mPlaylistEntries.size();
        boolean done = ENABLE_BULK_INSERTS;
        for (int i = ID_PLAYLISTS_COLUMN_INDEX; i < len; i += PATH_PLAYLISTS_COLUMN_INDEX) {
            PlaylistEntry entry = (PlaylistEntry) this.mPlaylistEntries.get(i);
            if (entry.bestmatchlevel != RILConstants.MAX_INT) {
                done = false;
                if (data.equalsIgnoreCase(entry.path)) {
                    entry.bestmatchid = rowId;
                    entry.bestmatchlevel = RILConstants.MAX_INT;
                } else {
                    int matchLength = matchPaths(data, entry.path);
                    if (matchLength > entry.bestmatchlevel) {
                        entry.bestmatchid = rowId;
                        entry.bestmatchlevel = matchLength;
                    }
                }
            }
        }
        return done;
    }

    private void cachePlaylistEntry(String line, String playListDirectory) {
        boolean fullPath = false;
        PlaylistEntry entry = new PlaylistEntry();
        int entryLength = line.length();
        while (entryLength > 0 && Character.isWhitespace(line.charAt(entryLength - 1))) {
            entryLength--;
        }
        if (entryLength >= FILES_PRESCAN_DATE_MODIFIED_COLUMN_INDEX) {
            if (entryLength < line.length()) {
                line = line.substring(ID_PLAYLISTS_COLUMN_INDEX, entryLength);
            }
            char ch1 = line.charAt(ID_PLAYLISTS_COLUMN_INDEX);
            if (ch1 == '/' || (Character.isLetter(ch1) && line.charAt(PATH_PLAYLISTS_COLUMN_INDEX) == ':' && line.charAt(FILES_PRESCAN_FORMAT_COLUMN_INDEX) == '\\')) {
                fullPath = ENABLE_BULK_INSERTS;
            }
            if (!fullPath) {
                line = playListDirectory + line;
            }
            entry.path = line;
            this.mPlaylistEntries.add(entry);
        }
    }

    private void processCachedPlaylist(Cursor fileList, ContentValues values, Uri playlistUri) {
        fileList.moveToPosition(-1);
        while (fileList.moveToNext()) {
            if (matchEntries(fileList.getLong(ID_PLAYLISTS_COLUMN_INDEX), fileList.getString(PATH_PLAYLISTS_COLUMN_INDEX))) {
                break;
            }
        }
        int len = this.mPlaylistEntries.size();
        int index = ID_PLAYLISTS_COLUMN_INDEX;
        for (int i = ID_PLAYLISTS_COLUMN_INDEX; i < len; i += PATH_PLAYLISTS_COLUMN_INDEX) {
            PlaylistEntry entry = (PlaylistEntry) this.mPlaylistEntries.get(i);
            if (entry.bestmatchlevel > 0) {
                try {
                    values.clear();
                    values.put("play_order", Integer.valueOf(index));
                    values.put("audio_id", Long.valueOf(entry.bestmatchid));
                    this.mMediaProvider.insert(this.mPackageName, playlistUri, values);
                    index += PATH_PLAYLISTS_COLUMN_INDEX;
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in MediaScanner.processCachedPlaylist()", e);
                    return;
                }
            }
        }
        this.mPlaylistEntries.clear();
    }

    private void processM3uPlayList(String path, String playListDirectory, Uri uri, ContentValues values, Cursor fileList) {
        IOException e;
        Throwable th;
        BufferedReader reader = null;
        try {
            File f = new File(path);
            if (f.exists()) {
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(f)), DateUtils.FORMAT_UTC);
                try {
                    String line = reader2.readLine();
                    this.mPlaylistEntries.clear();
                    while (line != null) {
                        if (line.length() > 0 && line.charAt(ID_PLAYLISTS_COLUMN_INDEX) != '#') {
                            cachePlaylistEntry(line, playListDirectory);
                        }
                        line = reader2.readLine();
                    }
                    processCachedPlaylist(fileList, values, uri);
                    reader = reader2;
                } catch (IOException e2) {
                    e = e2;
                    reader = reader2;
                    try {
                        Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e);
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e3) {
                                Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e3);
                                return;
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e32) {
                                Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e32);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    reader = reader2;
                    if (reader != null) {
                        reader.close();
                    }
                    throw th;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e322) {
                    Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e322);
                }
            }
        } catch (IOException e4) {
            e322 = e4;
            Log.e(TAG, "IOException in MediaScanner.processM3uPlayList()", e322);
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void processPlsPlayList(String path, String playListDirectory, Uri uri, ContentValues values, Cursor fileList) {
        IOException e;
        Throwable th;
        BufferedReader reader = null;
        try {
            File f = new File(path);
            if (f.exists()) {
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(f)), DateUtils.FORMAT_UTC);
                try {
                    this.mPlaylistEntries.clear();
                    for (String line = reader2.readLine(); line != null; line = reader2.readLine()) {
                        if (line.startsWith("File")) {
                            int equals = line.indexOf(61);
                            if (equals > 0) {
                                cachePlaylistEntry(line.substring(equals + PATH_PLAYLISTS_COLUMN_INDEX), playListDirectory);
                            }
                        }
                    }
                    processCachedPlaylist(fileList, values, uri);
                    reader = reader2;
                } catch (IOException e2) {
                    e = e2;
                    reader = reader2;
                    try {
                        Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e);
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e3) {
                                Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e3);
                                return;
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e32) {
                                Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e32);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    reader = reader2;
                    if (reader != null) {
                        reader.close();
                    }
                    throw th;
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e322) {
                    Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e322);
                }
            }
        } catch (IOException e4) {
            e322 = e4;
            Log.e(TAG, "IOException in MediaScanner.processPlsPlayList()", e322);
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void processWplPlayList(String path, String playListDirectory, Uri uri, ContentValues values, Cursor fileList) {
        SAXException e;
        IOException e2;
        Throwable th;
        FileInputStream fis = null;
        try {
            File f = new File(path);
            if (f.exists()) {
                FileInputStream fis2 = new FileInputStream(f);
                try {
                    this.mPlaylistEntries.clear();
                    Xml.parse(fis2, Xml.findEncodingByName("UTF-8"), new WplHandler(this, playListDirectory, uri, fileList).getContentHandler());
                    processCachedPlaylist(fileList, values, uri);
                    fis = fis2;
                } catch (SAXException e3) {
                    e = e3;
                    fis = fis2;
                    try {
                        e.printStackTrace();
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e22) {
                                Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e22);
                                return;
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e222) {
                                Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e222);
                            }
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    e222 = e4;
                    fis = fis2;
                    e222.printStackTrace();
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e2222) {
                            Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e2222);
                            return;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fis = fis2;
                    if (fis != null) {
                        fis.close();
                    }
                    throw th;
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e22222) {
                    Log.e(TAG, "IOException in MediaScanner.processWplPlayList()", e22222);
                }
            }
        } catch (SAXException e5) {
            e = e5;
            e.printStackTrace();
            if (fis != null) {
                fis.close();
            }
        } catch (IOException e6) {
            e22222 = e6;
            e22222.printStackTrace();
            if (fis != null) {
                fis.close();
            }
        }
    }

    private void processPlayList(FileEntry entry, Cursor fileList) throws RemoteException {
        String path = entry.mPath;
        ContentValues values = new ContentValues();
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash < 0) {
            throw new IllegalArgumentException("bad path " + path);
        }
        Uri membersUri;
        long rowId = entry.mRowId;
        String name = values.getAsString("name");
        if (name == null) {
            name = values.getAsString(GpsNetInitiatedHandler.NI_INTENT_KEY_TITLE);
            if (name == null) {
                int lastDot = path.lastIndexOf(46);
                if (lastDot < 0) {
                    name = path.substring(lastSlash + PATH_PLAYLISTS_COLUMN_INDEX);
                } else {
                    name = path.substring(lastSlash + PATH_PLAYLISTS_COLUMN_INDEX, lastDot);
                }
            }
        }
        values.put("name", name);
        values.put("date_modified", Long.valueOf(entry.mLastModified));
        Uri uri;
        if (rowId == 0) {
            values.put("_data", path);
            uri = this.mMediaProvider.insert(this.mPackageName, this.mPlaylistsUri, values);
            rowId = ContentUris.parseId(uri);
            membersUri = Uri.withAppendedPath(uri, "members");
        } else {
            uri = ContentUris.withAppendedId(this.mPlaylistsUri, rowId);
            this.mMediaProvider.update(this.mPackageName, uri, values, null, null);
            membersUri = Uri.withAppendedPath(uri, "members");
            this.mMediaProvider.delete(this.mPackageName, membersUri, null, null);
        }
        String playListDirectory = path.substring(ID_PLAYLISTS_COLUMN_INDEX, lastSlash + PATH_PLAYLISTS_COLUMN_INDEX);
        MediaFileType mediaFileType = MediaFile.getFileType(path);
        int fileType = mediaFileType == null ? ID_PLAYLISTS_COLUMN_INDEX : mediaFileType.fileType;
        if (fileType == 41) {
            processM3uPlayList(path, playListDirectory, membersUri, values, fileList);
        } else if (fileType == 42) {
            processPlsPlayList(path, playListDirectory, membersUri, values, fileList);
        } else if (fileType == 43) {
            processWplPlayList(path, playListDirectory, membersUri, values, fileList);
        }
    }

    private void processPlayLists() throws RemoteException {
        Iterator<FileEntry> iterator = this.mPlayLists.iterator();
        Cursor fileList = null;
        try {
            fileList = this.mMediaProvider.query(this.mPackageName, this.mFilesUri, FILES_PRESCAN_PROJECTION, "media_type=2", null, null, null);
            while (iterator.hasNext()) {
                FileEntry entry = (FileEntry) iterator.next();
                if (entry.mLastModifiedChanged) {
                    processPlayList(entry, fileList);
                }
            }
            if (fileList != null) {
                fileList.close();
            }
        } catch (RemoteException e) {
            if (fileList != null) {
                fileList.close();
            }
        } catch (Throwable th) {
            if (fileList != null) {
                fileList.close();
            }
        }
    }

    public void release() {
        native_finalize();
    }

    protected void finalize() {
        this.mContext.getContentResolver().releaseProvider(this.mMediaProvider);
        native_finalize();
    }
}
