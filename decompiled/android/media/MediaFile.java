package android.media;

import android.media.DecoderCapabilities.AudioDecoder;
import android.media.DecoderCapabilities.VideoDecoder;
import com.android.internal.http.multipart.StringPart;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL11;

public class MediaFile {
    public static final int FILE_TYPE_3GPP = 23;
    public static final int FILE_TYPE_3GPP2 = 24;
    public static final int FILE_TYPE_AAC = 8;
    public static final int FILE_TYPE_AMR = 4;
    public static final int FILE_TYPE_ASF = 26;
    public static final int FILE_TYPE_AVI = 29;
    public static final int FILE_TYPE_AWB = 5;
    public static final int FILE_TYPE_BMP = 34;
    public static final int FILE_TYPE_FL = 51;
    public static final int FILE_TYPE_FLAC = 10;
    public static final int FILE_TYPE_GIF = 32;
    public static final int FILE_TYPE_HTML = 101;
    public static final int FILE_TYPE_HTTPLIVE = 44;
    public static final int FILE_TYPE_IMY = 13;
    public static final int FILE_TYPE_JPEG = 31;
    public static final int FILE_TYPE_M3U = 41;
    public static final int FILE_TYPE_M4A = 2;
    public static final int FILE_TYPE_M4V = 22;
    public static final int FILE_TYPE_MID = 11;
    public static final int FILE_TYPE_MKA = 9;
    public static final int FILE_TYPE_MKV = 27;
    public static final int FILE_TYPE_MP2PS = 200;
    public static final int FILE_TYPE_MP2TS = 28;
    public static final int FILE_TYPE_MP3 = 1;
    public static final int FILE_TYPE_MP4 = 21;
    public static final int FILE_TYPE_MS_EXCEL = 105;
    public static final int FILE_TYPE_MS_POWERPOINT = 106;
    public static final int FILE_TYPE_MS_WORD = 104;
    public static final int FILE_TYPE_OGG = 7;
    public static final int FILE_TYPE_PDF = 102;
    public static final int FILE_TYPE_PLS = 42;
    public static final int FILE_TYPE_PNG = 33;
    public static final int FILE_TYPE_SMF = 12;
    public static final int FILE_TYPE_TEXT = 100;
    public static final int FILE_TYPE_WAV = 3;
    public static final int FILE_TYPE_WBMP = 35;
    public static final int FILE_TYPE_WEBM = 30;
    public static final int FILE_TYPE_WEBP = 36;
    public static final int FILE_TYPE_WMA = 6;
    public static final int FILE_TYPE_WMV = 25;
    public static final int FILE_TYPE_WPL = 43;
    public static final int FILE_TYPE_XML = 103;
    public static final int FILE_TYPE_ZIP = 107;
    private static final int FIRST_AUDIO_FILE_TYPE = 1;
    private static final int FIRST_DRM_FILE_TYPE = 51;
    private static final int FIRST_IMAGE_FILE_TYPE = 31;
    private static final int FIRST_MIDI_FILE_TYPE = 11;
    private static final int FIRST_PLAYLIST_FILE_TYPE = 41;
    private static final int FIRST_VIDEO_FILE_TYPE = 21;
    private static final int FIRST_VIDEO_FILE_TYPE2 = 200;
    private static final int LAST_AUDIO_FILE_TYPE = 10;
    private static final int LAST_DRM_FILE_TYPE = 51;
    private static final int LAST_IMAGE_FILE_TYPE = 36;
    private static final int LAST_MIDI_FILE_TYPE = 13;
    private static final int LAST_PLAYLIST_FILE_TYPE = 44;
    private static final int LAST_VIDEO_FILE_TYPE = 30;
    private static final int LAST_VIDEO_FILE_TYPE2 = 200;
    private static final HashMap<String, MediaFileType> sFileTypeMap;
    private static final HashMap<String, Integer> sFileTypeToFormatMap;
    private static final HashMap<Integer, String> sFormatToMimeTypeMap;
    private static final HashMap<String, Integer> sMimeTypeMap;
    private static final HashMap<String, Integer> sMimeTypeToFormatMap;

    public static class MediaFileType {
        public final int fileType;
        public final String mimeType;

        MediaFileType(int fileType, String mimeType) {
            this.fileType = fileType;
            this.mimeType = mimeType;
        }
    }

    static {
        sFileTypeMap = new HashMap();
        sMimeTypeMap = new HashMap();
        sFileTypeToFormatMap = new HashMap();
        sMimeTypeToFormatMap = new HashMap();
        sFormatToMimeTypeMap = new HashMap();
        addFileType("MP3", FIRST_AUDIO_FILE_TYPE, "audio/mpeg", EGL10.EGL_BAD_MATCH);
        addFileType("MPGA", FIRST_AUDIO_FILE_TYPE, "audio/mpeg", EGL10.EGL_BAD_MATCH);
        addFileType("M4A", FILE_TYPE_M4A, "audio/mp4", EGL10.EGL_BAD_NATIVE_WINDOW);
        addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav", EGL10.EGL_BAD_DISPLAY);
        addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
        addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");
        if (isWMAEnabled()) {
            addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma", 47361);
        }
        addFileType("OGG", FILE_TYPE_OGG, "audio/ogg", 47362);
        addFileType("OGG", FILE_TYPE_OGG, "application/ogg", 47362);
        addFileType("OGA", FILE_TYPE_OGG, "application/ogg", 47362);
        addFileType("AAC", FILE_TYPE_AAC, "audio/aac", 47363);
        addFileType("AAC", FILE_TYPE_AAC, "audio/aac-adts", 47363);
        addFileType("MKA", FILE_TYPE_MKA, "audio/x-matroska");
        addFileType("MID", FIRST_MIDI_FILE_TYPE, "audio/midi");
        addFileType("MIDI", FIRST_MIDI_FILE_TYPE, "audio/midi");
        addFileType("XMF", FIRST_MIDI_FILE_TYPE, "audio/midi");
        addFileType("RTTTL", FIRST_MIDI_FILE_TYPE, "audio/midi");
        addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi");
        addFileType("IMY", LAST_MIDI_FILE_TYPE, "audio/imelody");
        addFileType("RTX", FIRST_MIDI_FILE_TYPE, "audio/midi");
        addFileType("OTA", FIRST_MIDI_FILE_TYPE, "audio/midi");
        addFileType("MXMF", FIRST_MIDI_FILE_TYPE, "audio/midi");
        addFileType("MPEG", FIRST_VIDEO_FILE_TYPE, "video/mpeg", EGL10.EGL_BAD_NATIVE_WINDOW);
        addFileType("MPG", FIRST_VIDEO_FILE_TYPE, "video/mpeg", EGL10.EGL_BAD_NATIVE_WINDOW);
        addFileType("MP4", FIRST_VIDEO_FILE_TYPE, "video/mp4", EGL10.EGL_BAD_NATIVE_WINDOW);
        addFileType("M4V", FILE_TYPE_M4V, "video/mp4", EGL10.EGL_BAD_NATIVE_WINDOW);
        addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp", 47492);
        addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp", 47492);
        addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2", 47492);
        addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2", 47492);
        addFileType("MKV", FILE_TYPE_MKV, "video/x-matroska");
        addFileType("WEBM", LAST_VIDEO_FILE_TYPE, "video/webm");
        addFileType("TS", FILE_TYPE_MP2TS, "video/mp2ts");
        addFileType("AVI", FILE_TYPE_AVI, "video/avi");
        if (isWMVEnabled()) {
            addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv", 47489);
            addFileType("ASF", FILE_TYPE_ASF, "video/x-ms-asf");
        }
        addFileType("JPG", FIRST_IMAGE_FILE_TYPE, "image/jpeg", 14337);
        addFileType("JPEG", FIRST_IMAGE_FILE_TYPE, "image/jpeg", 14337);
        addFileType("GIF", FILE_TYPE_GIF, "image/gif", 14343);
        addFileType("PNG", FILE_TYPE_PNG, "image/png", 14347);
        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp", 14340);
        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");
        addFileType("WEBP", LAST_IMAGE_FILE_TYPE, "image/webp");
        addFileType("M3U", FIRST_PLAYLIST_FILE_TYPE, "audio/x-mpegurl", 47633);
        addFileType("M3U", FIRST_PLAYLIST_FILE_TYPE, "application/x-mpegurl", 47633);
        addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls", 47636);
        addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl", 47632);
        addFileType("M3U8", LAST_PLAYLIST_FILE_TYPE, "application/vnd.apple.mpegurl");
        addFileType("M3U8", LAST_PLAYLIST_FILE_TYPE, "audio/mpegurl");
        addFileType("M3U8", LAST_PLAYLIST_FILE_TYPE, "audio/x-mpegurl");
        addFileType("FL", LAST_DRM_FILE_TYPE, "application/x-android-drm-fl");
        addFileType("TXT", FILE_TYPE_TEXT, StringPart.DEFAULT_CONTENT_TYPE, GL11.GL_CLIP_PLANE4);
        addFileType("HTM", FILE_TYPE_HTML, "text/html", GL11.GL_CLIP_PLANE5);
        addFileType("HTML", FILE_TYPE_HTML, "text/html", GL11.GL_CLIP_PLANE5);
        addFileType("PDF", FILE_TYPE_PDF, "application/pdf");
        addFileType("DOC", FILE_TYPE_MS_WORD, "application/msword", 47747);
        addFileType("XLS", FILE_TYPE_MS_EXCEL, "application/vnd.ms-excel", 47749);
        addFileType("PPT", FILE_TYPE_MS_POWERPOINT, "application/mspowerpoint", 47750);
        addFileType("FLAC", LAST_AUDIO_FILE_TYPE, "audio/flac", 47366);
        addFileType("ZIP", FILE_TYPE_ZIP, "application/zip");
        addFileType("MPG", LAST_VIDEO_FILE_TYPE2, "video/mp2p");
        addFileType("MPEG", LAST_VIDEO_FILE_TYPE2, "video/mp2p");
    }

    static void addFileType(String extension, int fileType, String mimeType) {
        sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
        sMimeTypeMap.put(mimeType, Integer.valueOf(fileType));
    }

    static void addFileType(String extension, int fileType, String mimeType, int mtpFormatCode) {
        addFileType(extension, fileType, mimeType);
        sFileTypeToFormatMap.put(extension, Integer.valueOf(mtpFormatCode));
        sMimeTypeToFormatMap.put(mimeType, Integer.valueOf(mtpFormatCode));
        sFormatToMimeTypeMap.put(Integer.valueOf(mtpFormatCode), mimeType);
    }

    private static boolean isWMAEnabled() {
        List<AudioDecoder> decoders = DecoderCapabilities.getAudioDecoders();
        int count = decoders.size();
        for (int i = 0; i < count; i += FIRST_AUDIO_FILE_TYPE) {
            if (((AudioDecoder) decoders.get(i)) == AudioDecoder.AUDIO_DECODER_WMA) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWMVEnabled() {
        List<VideoDecoder> decoders = DecoderCapabilities.getVideoDecoders();
        int count = decoders.size();
        for (int i = 0; i < count; i += FIRST_AUDIO_FILE_TYPE) {
            if (((VideoDecoder) decoders.get(i)) == VideoDecoder.VIDEO_DECODER_WMV) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAudioFileType(int fileType) {
        if (fileType < FIRST_AUDIO_FILE_TYPE || fileType > LAST_AUDIO_FILE_TYPE) {
            return fileType >= FIRST_MIDI_FILE_TYPE && fileType <= LAST_MIDI_FILE_TYPE;
        } else {
            return true;
        }
    }

    public static boolean isVideoFileType(int fileType) {
        return (fileType >= FIRST_VIDEO_FILE_TYPE && fileType <= LAST_VIDEO_FILE_TYPE) || (fileType >= LAST_VIDEO_FILE_TYPE2 && fileType <= LAST_VIDEO_FILE_TYPE2);
    }

    public static boolean isImageFileType(int fileType) {
        return fileType >= FIRST_IMAGE_FILE_TYPE && fileType <= LAST_IMAGE_FILE_TYPE;
    }

    public static boolean isPlayListFileType(int fileType) {
        return fileType >= FIRST_PLAYLIST_FILE_TYPE && fileType <= LAST_PLAYLIST_FILE_TYPE;
    }

    public static boolean isDrmFileType(int fileType) {
        return fileType >= LAST_DRM_FILE_TYPE && fileType <= LAST_DRM_FILE_TYPE;
    }

    public static MediaFileType getFileType(String path) {
        int lastDot = path.lastIndexOf(46);
        if (lastDot < 0) {
            return null;
        }
        return (MediaFileType) sFileTypeMap.get(path.substring(lastDot + FIRST_AUDIO_FILE_TYPE).toUpperCase(Locale.ROOT));
    }

    public static boolean isMimeTypeMedia(String mimeType) {
        int fileType = getFileTypeForMimeType(mimeType);
        return isAudioFileType(fileType) || isVideoFileType(fileType) || isImageFileType(fileType) || isPlayListFileType(fileType);
    }

    public static String getFileTitle(String path) {
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash >= 0) {
            lastSlash += FIRST_AUDIO_FILE_TYPE;
            if (lastSlash < path.length()) {
                path = path.substring(lastSlash);
            }
        }
        int lastDot = path.lastIndexOf(46);
        if (lastDot > 0) {
            return path.substring(0, lastDot);
        }
        return path;
    }

    public static int getFileTypeForMimeType(String mimeType) {
        Integer value = (Integer) sMimeTypeMap.get(mimeType);
        return value == null ? 0 : value.intValue();
    }

    public static String getMimeTypeForFile(String path) {
        MediaFileType mediaFileType = getFileType(path);
        return mediaFileType == null ? null : mediaFileType.mimeType;
    }

    public static int getFormatCode(String fileName, String mimeType) {
        Integer value;
        if (mimeType != null) {
            value = (Integer) sMimeTypeToFormatMap.get(mimeType);
            if (value != null) {
                return value.intValue();
            }
        }
        int lastDot = fileName.lastIndexOf(46);
        if (lastDot > 0) {
            value = (Integer) sFileTypeToFormatMap.get(fileName.substring(lastDot + FIRST_AUDIO_FILE_TYPE).toUpperCase(Locale.ROOT));
            if (value != null) {
                return value.intValue();
            }
        }
        return GL11.GL_CLIP_PLANE0;
    }

    public static String getMimeTypeForFormatCode(int formatCode) {
        return (String) sFormatToMimeTypeMap.get(Integer.valueOf(formatCode));
    }
}
