package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;

public final class SearchableInfo implements Parcelable {
    public static final Creator<SearchableInfo> CREATOR = null;
    private static final boolean DBG = false;
    private static final String LOG_TAG = "SearchableInfo";
    private static final String MD_LABEL_SEARCHABLE = "android.app.searchable";
    private static final String MD_XML_ELEMENT_SEARCHABLE = "searchable";
    private static final String MD_XML_ELEMENT_SEARCHABLE_ACTION_KEY = "actionkey";
    private static final int SEARCH_MODE_BADGE_ICON = 8;
    private static final int SEARCH_MODE_BADGE_LABEL = 4;
    private static final int SEARCH_MODE_QUERY_REWRITE_FROM_DATA = 16;
    private static final int SEARCH_MODE_QUERY_REWRITE_FROM_TEXT = 32;
    private static final int VOICE_SEARCH_LAUNCH_RECOGNIZER = 4;
    private static final int VOICE_SEARCH_LAUNCH_WEB_SEARCH = 2;
    private static final int VOICE_SEARCH_SHOW_BUTTON = 1;
    private HashMap<Integer, ActionKeyInfo> mActionKeys;
    private final boolean mAutoUrlDetect;
    private final int mHintId;
    private final int mIconId;
    private final boolean mIncludeInGlobalSearch;
    private final int mLabelId;
    private final boolean mQueryAfterZeroResults;
    private final ComponentName mSearchActivity;
    private final int mSearchButtonText;
    private final int mSearchImeOptions;
    private final int mSearchInputType;
    private final int mSearchMode;
    private final int mSettingsDescriptionId;
    private final String mSuggestAuthority;
    private final String mSuggestIntentAction;
    private final String mSuggestIntentData;
    private final String mSuggestPath;
    private final String mSuggestProviderPackage;
    private final String mSuggestSelection;
    private final int mSuggestThreshold;
    private final int mVoiceLanguageId;
    private final int mVoiceLanguageModeId;
    private final int mVoiceMaxResults;
    private final int mVoicePromptTextId;
    private final int mVoiceSearchMode;

    public static class ActionKeyInfo implements Parcelable {
        private final int mKeyCode;
        private final String mQueryActionMsg;
        private final String mSuggestActionMsg;
        private final String mSuggestActionMsgColumn;

        ActionKeyInfo(android.content.Context r1, android.util.AttributeSet r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.<init>(android.content.Context, android.util.AttributeSet):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.<init>(android.content.Context, android.util.AttributeSet):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.<init>(android.content.Context, android.util.AttributeSet):void");
        }

        private ActionKeyInfo(android.os.Parcel r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.<init>(android.os.Parcel):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.<init>(android.os.Parcel):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.<init>(android.os.Parcel):void");
        }

        /* synthetic */ ActionKeyInfo(android.os.Parcel r1, android.app.SearchableInfo.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.<init>(android.os.Parcel, android.app.SearchableInfo$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.<init>(android.os.Parcel, android.app.SearchableInfo$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.<init>(android.os.Parcel, android.app.SearchableInfo$1):void");
        }

        public int getKeyCode() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.getKeyCode():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.getKeyCode():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.getKeyCode():int");
        }

        public java.lang.String getQueryActionMsg() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.getQueryActionMsg():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.getQueryActionMsg():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.getQueryActionMsg():java.lang.String");
        }

        public java.lang.String getSuggestActionMsg() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.getSuggestActionMsg():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.getSuggestActionMsg():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.getSuggestActionMsg():java.lang.String");
        }

        public java.lang.String getSuggestActionMsgColumn() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.getSuggestActionMsgColumn():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.getSuggestActionMsgColumn():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.getSuggestActionMsgColumn():java.lang.String");
        }

        public void writeToParcel(android.os.Parcel r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.SearchableInfo.ActionKeyInfo.writeToParcel(android.os.Parcel, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.SearchableInfo.ActionKeyInfo.writeToParcel(android.os.Parcel, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.ActionKeyInfo.writeToParcel(android.os.Parcel, int):void");
        }

        public int describeContents() {
            return 0;
        }
    }

    public String getSuggestAuthority() {
        return this.mSuggestAuthority;
    }

    public String getSuggestPackage() {
        return this.mSuggestProviderPackage;
    }

    public ComponentName getSearchActivity() {
        return this.mSearchActivity;
    }

    public boolean useBadgeLabel() {
        return (this.mSearchMode & VOICE_SEARCH_LAUNCH_RECOGNIZER) != 0 ? true : DBG;
    }

    public boolean useBadgeIcon() {
        return ((this.mSearchMode & SEARCH_MODE_BADGE_ICON) == 0 || this.mIconId == 0) ? DBG : true;
    }

    public boolean shouldRewriteQueryFromData() {
        return (this.mSearchMode & SEARCH_MODE_QUERY_REWRITE_FROM_DATA) != 0 ? true : DBG;
    }

    public boolean shouldRewriteQueryFromText() {
        return (this.mSearchMode & SEARCH_MODE_QUERY_REWRITE_FROM_TEXT) != 0 ? true : DBG;
    }

    public int getSettingsDescriptionId() {
        return this.mSettingsDescriptionId;
    }

    public String getSuggestPath() {
        return this.mSuggestPath;
    }

    public String getSuggestSelection() {
        return this.mSuggestSelection;
    }

    public String getSuggestIntentAction() {
        return this.mSuggestIntentAction;
    }

    public String getSuggestIntentData() {
        return this.mSuggestIntentData;
    }

    public int getSuggestThreshold() {
        return this.mSuggestThreshold;
    }

    public Context getActivityContext(Context context) {
        return createActivityContext(context, this.mSearchActivity);
    }

    private static Context createActivityContext(Context context, ComponentName activity) {
        Context theirContext = null;
        try {
            theirContext = context.createPackageContext(activity.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.e(LOG_TAG, "Package not found " + activity.getPackageName());
        } catch (SecurityException e2) {
            Log.e(LOG_TAG, "Can't make context for " + activity.getPackageName(), e2);
        }
        return theirContext;
    }

    public Context getProviderContext(Context context, Context activityContext) {
        Context theirContext = null;
        if (this.mSearchActivity.getPackageName().equals(this.mSuggestProviderPackage)) {
            return activityContext;
        }
        if (this.mSuggestProviderPackage != null) {
            try {
                theirContext = context.createPackageContext(this.mSuggestProviderPackage, 0);
            } catch (NameNotFoundException e) {
            } catch (SecurityException e2) {
            }
        }
        return theirContext;
    }

    private SearchableInfo(Context activityContext, AttributeSet attr, ComponentName cName) {
        this.mActionKeys = null;
        this.mSearchActivity = cName;
        TypedArray a = activityContext.obtainStyledAttributes(attr, R.styleable.Searchable);
        this.mSearchMode = a.getInt(3, 0);
        this.mLabelId = a.getResourceId(0, 0);
        this.mHintId = a.getResourceId(VOICE_SEARCH_LAUNCH_WEB_SEARCH, 0);
        this.mIconId = a.getResourceId(VOICE_SEARCH_SHOW_BUTTON, 0);
        this.mSearchButtonText = a.getResourceId(9, 0);
        this.mSearchInputType = a.getInt(10, VOICE_SEARCH_SHOW_BUTTON);
        this.mSearchImeOptions = a.getInt(SEARCH_MODE_QUERY_REWRITE_FROM_DATA, VOICE_SEARCH_LAUNCH_WEB_SEARCH);
        this.mIncludeInGlobalSearch = a.getBoolean(18, DBG);
        this.mQueryAfterZeroResults = a.getBoolean(19, DBG);
        this.mAutoUrlDetect = a.getBoolean(21, DBG);
        this.mSettingsDescriptionId = a.getResourceId(20, 0);
        this.mSuggestAuthority = a.getString(VOICE_SEARCH_LAUNCH_RECOGNIZER);
        this.mSuggestPath = a.getString(5);
        this.mSuggestSelection = a.getString(6);
        this.mSuggestIntentAction = a.getString(7);
        this.mSuggestIntentData = a.getString(SEARCH_MODE_BADGE_ICON);
        this.mSuggestThreshold = a.getInt(17, 0);
        this.mVoiceSearchMode = a.getInt(11, 0);
        this.mVoiceLanguageModeId = a.getResourceId(12, 0);
        this.mVoicePromptTextId = a.getResourceId(13, 0);
        this.mVoiceLanguageId = a.getResourceId(14, 0);
        this.mVoiceMaxResults = a.getInt(15, 0);
        a.recycle();
        String suggestProviderPackage = null;
        if (this.mSuggestAuthority != null) {
            ProviderInfo pi = activityContext.getPackageManager().resolveContentProvider(this.mSuggestAuthority, 0);
            if (pi != null) {
                suggestProviderPackage = pi.packageName;
            }
        }
        this.mSuggestProviderPackage = suggestProviderPackage;
        if (this.mLabelId == 0) {
            throw new IllegalArgumentException("Search label must be a resource reference.");
        }
    }

    public ActionKeyInfo findActionKey(int keyCode) {
        if (this.mActionKeys == null) {
            return null;
        }
        return (ActionKeyInfo) this.mActionKeys.get(Integer.valueOf(keyCode));
    }

    private void addActionKey(ActionKeyInfo keyInfo) {
        if (this.mActionKeys == null) {
            this.mActionKeys = new HashMap();
        }
        this.mActionKeys.put(Integer.valueOf(keyInfo.getKeyCode()), keyInfo);
    }

    public static SearchableInfo getActivityMetaData(Context context, ActivityInfo activityInfo, int userId) {
        try {
            Context userContext = context.createPackageContextAsUser("system", 0, new UserHandle(userId));
            XmlPullParser xml = activityInfo.loadXmlMetaData(userContext.getPackageManager(), MD_LABEL_SEARCHABLE);
            if (xml == null) {
                return null;
            }
            SearchableInfo searchable = getActivityMetaData(userContext, xml, new ComponentName(activityInfo.packageName, activityInfo.name));
            xml.close();
            return searchable;
        } catch (NameNotFoundException e) {
            Log.e(LOG_TAG, "Couldn't create package context for user " + userId);
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.app.SearchableInfo getActivityMetaData(android.content.Context r11, org.xmlpull.v1.XmlPullParser r12, android.content.ComponentName r13) {
        /*
        r7 = 0;
        r4 = 0;
        r0 = createActivityContext(r11, r13);
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        r5 = r7;
    L_0x0009:
        return r5;
    L_0x000a:
        r6 = r12.next();	 Catch:{ XmlPullParserException -> 0x00b1, IOException -> 0x00d1 }
        r5 = r4;
    L_0x000f:
        r8 = 1;
        if (r6 == r8) goto L_0x00f1;
    L_0x0012:
        r8 = 2;
        if (r6 != r8) goto L_0x00fa;
    L_0x0015:
        r8 = r12.getName();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = "searchable";
        r8 = r8.equals(r9);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        if (r8 == 0) goto L_0x0061;
    L_0x0022:
        r1 = android.util.Xml.asAttributeSet(r12);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        if (r1 == 0) goto L_0x00fa;
    L_0x0028:
        r4 = new android.app.SearchableInfo;	 Catch:{ IllegalArgumentException -> 0x0033 }
        r4.<init>(r0, r1, r13);	 Catch:{ IllegalArgumentException -> 0x0033 }
    L_0x002d:
        r6 = r12.next();	 Catch:{ XmlPullParserException -> 0x00b1, IOException -> 0x00d1 }
        r5 = r4;
        goto L_0x000f;
    L_0x0033:
        r3 = move-exception;
        r8 = "SearchableInfo";
        r9 = new java.lang.StringBuilder;	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9.<init>();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = "Invalid searchable metadata for ";
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = r13.flattenToShortString();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = ": ";
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = r3.getMessage();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = r9.toString();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        android.util.Log.w(r8, r9);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r4 = r5;
        r5 = r7;
        goto L_0x0009;
    L_0x0061:
        r8 = r12.getName();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = "actionkey";
        r8 = r8.equals(r9);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        if (r8 == 0) goto L_0x00fa;
    L_0x006d:
        if (r5 != 0) goto L_0x0072;
    L_0x006f:
        r4 = r5;
        r5 = r7;
        goto L_0x0009;
    L_0x0072:
        r1 = android.util.Xml.asAttributeSet(r12);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        if (r1 == 0) goto L_0x00fa;
    L_0x0078:
        r8 = new android.app.SearchableInfo$ActionKeyInfo;	 Catch:{ IllegalArgumentException -> 0x0082 }
        r8.<init>(r0, r1);	 Catch:{ IllegalArgumentException -> 0x0082 }
        r5.addActionKey(r8);	 Catch:{ IllegalArgumentException -> 0x0082 }
        r4 = r5;
        goto L_0x002d;
    L_0x0082:
        r3 = move-exception;
        r8 = "SearchableInfo";
        r9 = new java.lang.StringBuilder;	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9.<init>();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = "Invalid action key for ";
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = r13.flattenToShortString();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = ": ";
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r10 = r3.getMessage();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r9 = r9.toString();	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        android.util.Log.w(r8, r9);	 Catch:{ XmlPullParserException -> 0x00f7, IOException -> 0x00f4 }
        r4 = r5;
        r5 = r7;
        goto L_0x0009;
    L_0x00b1:
        r2 = move-exception;
    L_0x00b2:
        r8 = "SearchableInfo";
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Reading searchable metadata for ";
        r9 = r9.append(r10);
        r10 = r13.flattenToShortString();
        r9 = r9.append(r10);
        r9 = r9.toString();
        android.util.Log.w(r8, r9, r2);
        r5 = r7;
        goto L_0x0009;
    L_0x00d1:
        r2 = move-exception;
    L_0x00d2:
        r8 = "SearchableInfo";
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Reading searchable metadata for ";
        r9 = r9.append(r10);
        r10 = r13.flattenToShortString();
        r9 = r9.append(r10);
        r9 = r9.toString();
        android.util.Log.w(r8, r9, r2);
        r5 = r7;
        goto L_0x0009;
    L_0x00f1:
        r4 = r5;
        goto L_0x0009;
    L_0x00f4:
        r2 = move-exception;
        r4 = r5;
        goto L_0x00d2;
    L_0x00f7:
        r2 = move-exception;
        r4 = r5;
        goto L_0x00b2;
    L_0x00fa:
        r4 = r5;
        goto L_0x002d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.SearchableInfo.getActivityMetaData(android.content.Context, org.xmlpull.v1.XmlPullParser, android.content.ComponentName):android.app.SearchableInfo");
    }

    public int getLabelId() {
        return this.mLabelId;
    }

    public int getHintId() {
        return this.mHintId;
    }

    public int getIconId() {
        return this.mIconId;
    }

    public boolean getVoiceSearchEnabled() {
        return (this.mVoiceSearchMode & VOICE_SEARCH_SHOW_BUTTON) != 0 ? true : DBG;
    }

    public boolean getVoiceSearchLaunchWebSearch() {
        return (this.mVoiceSearchMode & VOICE_SEARCH_LAUNCH_WEB_SEARCH) != 0 ? true : DBG;
    }

    public boolean getVoiceSearchLaunchRecognizer() {
        return (this.mVoiceSearchMode & VOICE_SEARCH_LAUNCH_RECOGNIZER) != 0 ? true : DBG;
    }

    public int getVoiceLanguageModeId() {
        return this.mVoiceLanguageModeId;
    }

    public int getVoicePromptTextId() {
        return this.mVoicePromptTextId;
    }

    public int getVoiceLanguageId() {
        return this.mVoiceLanguageId;
    }

    public int getVoiceMaxResults() {
        return this.mVoiceMaxResults;
    }

    public int getSearchButtonText() {
        return this.mSearchButtonText;
    }

    public int getInputType() {
        return this.mSearchInputType;
    }

    public int getImeOptions() {
        return this.mSearchImeOptions;
    }

    public boolean shouldIncludeInGlobalSearch() {
        return this.mIncludeInGlobalSearch;
    }

    public boolean queryAfterZeroResults() {
        return this.mQueryAfterZeroResults;
    }

    public boolean autoUrlDetect() {
        return this.mAutoUrlDetect;
    }

    static {
        CREATOR = new Creator<SearchableInfo>() {
            public SearchableInfo createFromParcel(Parcel in) {
                return new SearchableInfo(in);
            }

            public SearchableInfo[] newArray(int size) {
                return new SearchableInfo[size];
            }
        };
    }

    SearchableInfo(Parcel in) {
        boolean z;
        boolean z2 = true;
        this.mActionKeys = null;
        this.mLabelId = in.readInt();
        this.mSearchActivity = ComponentName.readFromParcel(in);
        this.mHintId = in.readInt();
        this.mSearchMode = in.readInt();
        this.mIconId = in.readInt();
        this.mSearchButtonText = in.readInt();
        this.mSearchInputType = in.readInt();
        this.mSearchImeOptions = in.readInt();
        if (in.readInt() != 0) {
            z = true;
        } else {
            z = DBG;
        }
        this.mIncludeInGlobalSearch = z;
        if (in.readInt() != 0) {
            z = true;
        } else {
            z = DBG;
        }
        this.mQueryAfterZeroResults = z;
        if (in.readInt() == 0) {
            z2 = DBG;
        }
        this.mAutoUrlDetect = z2;
        this.mSettingsDescriptionId = in.readInt();
        this.mSuggestAuthority = in.readString();
        this.mSuggestPath = in.readString();
        this.mSuggestSelection = in.readString();
        this.mSuggestIntentAction = in.readString();
        this.mSuggestIntentData = in.readString();
        this.mSuggestThreshold = in.readInt();
        for (int count = in.readInt(); count > 0; count--) {
            addActionKey(new ActionKeyInfo(in, null));
        }
        this.mSuggestProviderPackage = in.readString();
        this.mVoiceSearchMode = in.readInt();
        this.mVoiceLanguageModeId = in.readInt();
        this.mVoicePromptTextId = in.readInt();
        this.mVoiceLanguageId = in.readInt();
        this.mVoiceMaxResults = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = VOICE_SEARCH_SHOW_BUTTON;
        dest.writeInt(this.mLabelId);
        this.mSearchActivity.writeToParcel(dest, flags);
        dest.writeInt(this.mHintId);
        dest.writeInt(this.mSearchMode);
        dest.writeInt(this.mIconId);
        dest.writeInt(this.mSearchButtonText);
        dest.writeInt(this.mSearchInputType);
        dest.writeInt(this.mSearchImeOptions);
        dest.writeInt(this.mIncludeInGlobalSearch ? VOICE_SEARCH_SHOW_BUTTON : 0);
        if (this.mQueryAfterZeroResults) {
            i = VOICE_SEARCH_SHOW_BUTTON;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.mAutoUrlDetect) {
            i2 = 0;
        }
        dest.writeInt(i2);
        dest.writeInt(this.mSettingsDescriptionId);
        dest.writeString(this.mSuggestAuthority);
        dest.writeString(this.mSuggestPath);
        dest.writeString(this.mSuggestSelection);
        dest.writeString(this.mSuggestIntentAction);
        dest.writeString(this.mSuggestIntentData);
        dest.writeInt(this.mSuggestThreshold);
        if (this.mActionKeys == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(this.mActionKeys.size());
            for (ActionKeyInfo actionKey : this.mActionKeys.values()) {
                actionKey.writeToParcel(dest, flags);
            }
        }
        dest.writeString(this.mSuggestProviderPackage);
        dest.writeInt(this.mVoiceSearchMode);
        dest.writeInt(this.mVoiceLanguageModeId);
        dest.writeInt(this.mVoicePromptTextId);
        dest.writeInt(this.mVoiceLanguageId);
        dest.writeInt(this.mVoiceMaxResults);
    }
}
