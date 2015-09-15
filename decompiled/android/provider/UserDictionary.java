package android.provider;

import android.content.Context;
import android.net.Uri;
import java.util.Locale;

public class UserDictionary {
    public static final String AUTHORITY = "user_dictionary";
    public static final Uri CONTENT_URI;
    private static final int FREQUENCY_MAX = 255;
    private static final int FREQUENCY_MIN = 0;

    public static class Words implements BaseColumns {
        public static final String APP_ID = "appid";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.userword";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.userword";
        public static final Uri CONTENT_URI;
        public static final String DEFAULT_SORT_ORDER = "frequency DESC";
        public static final String FREQUENCY = "frequency";
        public static final String LOCALE = "locale";
        @Deprecated
        public static final int LOCALE_TYPE_ALL = 0;
        @Deprecated
        public static final int LOCALE_TYPE_CURRENT = 1;
        public static final String SHORTCUT = "shortcut";
        public static final String WORD = "word";
        public static final String _ID = "_id";

        public static void addWord(android.content.Context r1, java.lang.String r2, int r3, java.lang.String r4, java.util.Locale r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.provider.UserDictionary.Words.addWord(android.content.Context, java.lang.String, int, java.lang.String, java.util.Locale):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.provider.UserDictionary.Words.addWord(android.content.Context, java.lang.String, int, java.lang.String, java.util.Locale):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.provider.UserDictionary.Words.addWord(android.content.Context, java.lang.String, int, java.lang.String, java.util.Locale):void");
        }

        static {
            CONTENT_URI = Uri.parse("content://user_dictionary/words");
        }

        @Deprecated
        public static void addWord(Context context, String word, int frequency, int localeType) {
            if (localeType == 0 || localeType == LOCALE_TYPE_CURRENT) {
                Locale locale;
                if (localeType == LOCALE_TYPE_CURRENT) {
                    locale = Locale.getDefault();
                } else {
                    locale = null;
                }
                addWord(context, word, frequency, null, locale);
            }
        }
    }

    static {
        CONTENT_URI = Uri.parse("content://user_dictionary");
    }
}
