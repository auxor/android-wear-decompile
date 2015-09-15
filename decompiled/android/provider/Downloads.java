package android.provider;

import android.content.Context;
import android.net.Uri;
import android.speech.tts.Voice;
import android.view.accessibility.AccessibilityEvent;

public final class Downloads {
    private static final String QUERY_WHERE_CLAUSE = "notificationpackage=? AND notificationclass=?";

    public static final class Impl implements BaseColumns {
        public static final String ACTION_DOWNLOAD_COMPLETED = "android.intent.action.DOWNLOAD_COMPLETED";
        public static final String ACTION_NOTIFICATION_CLICKED = "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED";
        public static final Uri ALL_DOWNLOADS_CONTENT_URI;
        public static final String COLUMN_ALLOWED_NETWORK_TYPES = "allowed_network_types";
        public static final String COLUMN_ALLOW_METERED = "allow_metered";
        public static final String COLUMN_ALLOW_ROAMING = "allow_roaming";
        public static final String COLUMN_ALLOW_WRITE = "allow_write";
        public static final String COLUMN_APP_DATA = "entity";
        public static final String COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT = "bypass_recommended_size_limit";
        public static final String COLUMN_CONTROL = "control";
        public static final String COLUMN_COOKIE_DATA = "cookiedata";
        public static final String COLUMN_CURRENT_BYTES = "current_bytes";
        public static final String COLUMN_DELETED = "deleted";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DESTINATION = "destination";
        public static final String COLUMN_ERROR_MSG = "errorMsg";
        public static final String COLUMN_FAILED_CONNECTIONS = "numfailed";
        public static final String COLUMN_FILE_NAME_HINT = "hint";
        public static final String COLUMN_IS_PUBLIC_API = "is_public_api";
        public static final String COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI = "is_visible_in_downloads_ui";
        public static final String COLUMN_LAST_MODIFICATION = "lastmod";
        public static final String COLUMN_LAST_UPDATESRC = "lastUpdateSrc";
        public static final String COLUMN_MEDIAPROVIDER_URI = "mediaprovider_uri";
        public static final String COLUMN_MEDIA_SCANNED = "scanned";
        public static final String COLUMN_MIME_TYPE = "mimetype";
        public static final String COLUMN_NOTIFICATION_CLASS = "notificationclass";
        public static final String COLUMN_NOTIFICATION_EXTRAS = "notificationextras";
        public static final String COLUMN_NOTIFICATION_PACKAGE = "notificationpackage";
        public static final String COLUMN_NO_INTEGRITY = "no_integrity";
        public static final String COLUMN_OTHER_UID = "otheruid";
        public static final String COLUMN_REFERER = "referer";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TOTAL_BYTES = "total_bytes";
        public static final String COLUMN_URI = "uri";
        public static final String COLUMN_USER_AGENT = "useragent";
        public static final String COLUMN_VISIBILITY = "visibility";
        public static final Uri CONTENT_URI;
        public static final int CONTROL_PAUSED = 1;
        public static final int CONTROL_RUN = 0;
        public static final int DESTINATION_CACHE_PARTITION = 1;
        public static final int DESTINATION_CACHE_PARTITION_NOROAMING = 3;
        public static final int DESTINATION_CACHE_PARTITION_PURGEABLE = 2;
        public static final int DESTINATION_EXTERNAL = 0;
        public static final int DESTINATION_FILE_URI = 4;
        public static final int DESTINATION_NON_DOWNLOADMANAGER_DOWNLOAD = 6;
        public static final int DESTINATION_SYSTEMCACHE_PARTITION = 5;
        public static final int LAST_UPDATESRC_DONT_NOTIFY_DOWNLOADSVC = 1;
        public static final int LAST_UPDATESRC_NOT_RELEVANT = 0;
        public static final int MIN_ARTIFICIAL_ERROR_STATUS = 488;
        public static final String PERMISSION_ACCESS = "android.permission.ACCESS_DOWNLOAD_MANAGER";
        public static final String PERMISSION_ACCESS_ADVANCED = "android.permission.ACCESS_DOWNLOAD_MANAGER_ADVANCED";
        public static final String PERMISSION_ACCESS_ALL = "android.permission.ACCESS_ALL_DOWNLOADS";
        public static final String PERMISSION_CACHE = "android.permission.ACCESS_CACHE_FILESYSTEM";
        public static final String PERMISSION_CACHE_NON_PURGEABLE = "android.permission.DOWNLOAD_CACHE_NON_PURGEABLE";
        public static final String PERMISSION_NO_NOTIFICATION = "android.permission.DOWNLOAD_WITHOUT_NOTIFICATION";
        public static final String PERMISSION_SEND_INTENTS = "android.permission.SEND_DOWNLOAD_COMPLETED_INTENTS";
        public static final Uri PUBLICLY_ACCESSIBLE_DOWNLOADS_URI;
        public static final String PUBLICLY_ACCESSIBLE_DOWNLOADS_URI_SEGMENT = "public_downloads";
        public static final int STATUS_BAD_REQUEST = 400;
        @Deprecated
        public static final int STATUS_BLOCKED = 498;
        public static final int STATUS_CANCELED = 490;
        public static final int STATUS_CANNOT_RESUME = 489;
        public static final int STATUS_DEVICE_NOT_FOUND_ERROR = 199;
        public static final int STATUS_FILE_ALREADY_EXISTS_ERROR = 488;
        public static final int STATUS_FILE_ERROR = 492;
        public static final int STATUS_HTTP_DATA_ERROR = 495;
        public static final int STATUS_HTTP_EXCEPTION = 496;
        public static final int STATUS_INSUFFICIENT_SPACE_ERROR = 198;
        public static final int STATUS_LENGTH_REQUIRED = 411;
        public static final int STATUS_NOT_ACCEPTABLE = 406;
        public static final int STATUS_PAUSED_BY_APP = 193;
        public static final int STATUS_PENDING = 190;
        public static final int STATUS_PRECONDITION_FAILED = 412;
        public static final int STATUS_QUEUED_FOR_WIFI = 196;
        public static final int STATUS_RUNNING = 192;
        public static final int STATUS_SUCCESS = 200;
        public static final int STATUS_TOO_MANY_REDIRECTS = 497;
        public static final int STATUS_UNHANDLED_HTTP_CODE = 494;
        public static final int STATUS_UNHANDLED_REDIRECT = 493;
        public static final int STATUS_UNKNOWN_ERROR = 491;
        public static final int STATUS_WAITING_FOR_NETWORK = 195;
        public static final int STATUS_WAITING_TO_RETRY = 194;
        public static final int VISIBILITY_HIDDEN = 2;
        public static final int VISIBILITY_VISIBLE = 0;
        public static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1;
        public static final String _DATA = "_data";

        public static class RequestHeaders {
            public static final String COLUMN_DOWNLOAD_ID = "download_id";
            public static final String COLUMN_HEADER = "header";
            public static final String COLUMN_VALUE = "value";
            public static final String HEADERS_DB_TABLE = "request_headers";
            public static final String INSERT_KEY_PREFIX = "http_header_";
            public static final String URI_SEGMENT = "headers";
        }

        private Impl() {
        }

        static {
            CONTENT_URI = Uri.parse("content://downloads/my_downloads");
            ALL_DOWNLOADS_CONTENT_URI = Uri.parse("content://downloads/all_downloads");
            PUBLICLY_ACCESSIBLE_DOWNLOADS_URI = Uri.parse("content://downloads/public_downloads");
        }

        public static boolean isStatusInformational(int status) {
            return status >= 100 && status < STATUS_SUCCESS;
        }

        public static boolean isStatusSuccess(int status) {
            return status >= STATUS_SUCCESS && status < Voice.QUALITY_NORMAL;
        }

        public static boolean isStatusError(int status) {
            return status >= STATUS_BAD_REQUEST && status < CalendarColumns.CAL_ACCESS_EDITOR;
        }

        public static boolean isStatusClientError(int status) {
            return status >= STATUS_BAD_REQUEST && status < AccessibilityEvent.MAX_TEXT_LENGTH;
        }

        public static boolean isStatusServerError(int status) {
            return status >= AccessibilityEvent.MAX_TEXT_LENGTH && status < CalendarColumns.CAL_ACCESS_EDITOR;
        }

        public static boolean isNotificationToBeDisplayed(int visibility) {
            return visibility == VISIBILITY_VISIBLE_NOTIFY_COMPLETED || visibility == DESTINATION_CACHE_PARTITION_NOROAMING;
        }

        public static boolean isStatusCompleted(int status) {
            return (status >= STATUS_SUCCESS && status < Voice.QUALITY_NORMAL) || (status >= STATUS_BAD_REQUEST && status < CalendarColumns.CAL_ACCESS_EDITOR);
        }

        public static String statusToString(int status) {
            switch (status) {
                case STATUS_PENDING /*190*/:
                    return "PENDING";
                case STATUS_RUNNING /*192*/:
                    return "RUNNING";
                case STATUS_PAUSED_BY_APP /*193*/:
                    return "PAUSED_BY_APP";
                case STATUS_WAITING_TO_RETRY /*194*/:
                    return "WAITING_TO_RETRY";
                case STATUS_WAITING_FOR_NETWORK /*195*/:
                    return "WAITING_FOR_NETWORK";
                case STATUS_QUEUED_FOR_WIFI /*196*/:
                    return "QUEUED_FOR_WIFI";
                case STATUS_INSUFFICIENT_SPACE_ERROR /*198*/:
                    return "INSUFFICIENT_SPACE_ERROR";
                case STATUS_DEVICE_NOT_FOUND_ERROR /*199*/:
                    return "DEVICE_NOT_FOUND_ERROR";
                case STATUS_SUCCESS /*200*/:
                    return "SUCCESS";
                case STATUS_BAD_REQUEST /*400*/:
                    return "BAD_REQUEST";
                case STATUS_NOT_ACCEPTABLE /*406*/:
                    return "NOT_ACCEPTABLE";
                case STATUS_LENGTH_REQUIRED /*411*/:
                    return "LENGTH_REQUIRED";
                case STATUS_PRECONDITION_FAILED /*412*/:
                    return "PRECONDITION_FAILED";
                case STATUS_FILE_ALREADY_EXISTS_ERROR /*488*/:
                    return "FILE_ALREADY_EXISTS_ERROR";
                case STATUS_CANNOT_RESUME /*489*/:
                    return "CANNOT_RESUME";
                case STATUS_CANCELED /*490*/:
                    return "CANCELED";
                case STATUS_UNKNOWN_ERROR /*491*/:
                    return "UNKNOWN_ERROR";
                case STATUS_FILE_ERROR /*492*/:
                    return "FILE_ERROR";
                case STATUS_UNHANDLED_REDIRECT /*493*/:
                    return "UNHANDLED_REDIRECT";
                case STATUS_UNHANDLED_HTTP_CODE /*494*/:
                    return "UNHANDLED_HTTP_CODE";
                case STATUS_HTTP_DATA_ERROR /*495*/:
                    return "HTTP_DATA_ERROR";
                case STATUS_HTTP_EXCEPTION /*496*/:
                    return "HTTP_EXCEPTION";
                case STATUS_TOO_MANY_REDIRECTS /*497*/:
                    return "TOO_MANY_REDIRECTS";
                case STATUS_BLOCKED /*498*/:
                    return "BLOCKED";
                default:
                    return Integer.toString(status);
            }
        }
    }

    private Downloads() {
    }

    public static final void removeAllDownloadsByPackage(Context context, String notification_package, String notification_class) {
        context.getContentResolver().delete(Impl.CONTENT_URI, QUERY_WHERE_CLAUSE, new String[]{notification_package, notification_class});
    }
}
