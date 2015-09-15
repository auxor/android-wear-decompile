package com.android.internal.inputmethod;

import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.text.format.DateUtils;
import android.util.Pair;
import android.util.Slog;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public class InputMethodUtils {
    public static final boolean DEBUG = false;
    private static final Locale ENGLISH_LOCALE = null;
    public static final int NOT_A_SUBTYPE_ID = -1;
    private static final String NOT_A_SUBTYPE_ID_STR = null;
    private static final Locale[] SEARCH_ORDER_OF_FALLBACK_LOCALES = null;
    public static final String SUBTYPE_MODE_ANY = null;
    public static final String SUBTYPE_MODE_KEYBOARD = "keyboard";
    public static final String SUBTYPE_MODE_VOICE = "voice";
    private static final String TAG = "InputMethodUtils";
    private static final String TAG_ASCII_CAPABLE = "AsciiCapable";
    private static final String TAG_ENABLED_WHEN_DEFAULT_IS_NOT_ASCII_CAPABLE = "EnabledWhenDefaultIsNotAsciiCapable";

    private static final class InputMethodListBuilder {
        private final LinkedHashSet<InputMethodInfo> mInputMethodSet;

        private InputMethodListBuilder() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.<init>():void");
        }

        /* synthetic */ InputMethodListBuilder(com.android.internal.inputmethod.InputMethodUtils.AnonymousClass1 r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.<init>(com.android.internal.inputmethod.InputMethodUtils$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.<init>(com.android.internal.inputmethod.InputMethodUtils$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.<init>(com.android.internal.inputmethod.InputMethodUtils$1):void");
        }

        public java.util.ArrayList<android.view.inputmethod.InputMethodInfo> build() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.build():java.util.ArrayList<android.view.inputmethod.InputMethodInfo>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.build():java.util.ArrayList<android.view.inputmethod.InputMethodInfo>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.build():java.util.ArrayList<android.view.inputmethod.InputMethodInfo>");
        }

        public com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder fillAuxiliaryImes(java.util.ArrayList<android.view.inputmethod.InputMethodInfo> r1, android.content.Context r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.fillAuxiliaryImes(java.util.ArrayList, android.content.Context):com.android.internal.inputmethod.InputMethodUtils$InputMethodListBuilder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.fillAuxiliaryImes(java.util.ArrayList, android.content.Context):com.android.internal.inputmethod.InputMethodUtils$InputMethodListBuilder
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.fillAuxiliaryImes(java.util.ArrayList, android.content.Context):com.android.internal.inputmethod.InputMethodUtils$InputMethodListBuilder");
        }

        public com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder fillImes(java.util.ArrayList<android.view.inputmethod.InputMethodInfo> r1, android.content.Context r2, boolean r3, java.util.Locale r4, boolean r5, java.lang.String r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.fillImes(java.util.ArrayList, android.content.Context, boolean, java.util.Locale, boolean, java.lang.String):com.android.internal.inputmethod.InputMethodUtils$InputMethodListBuilder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.fillImes(java.util.ArrayList, android.content.Context, boolean, java.util.Locale, boolean, java.lang.String):com.android.internal.inputmethod.InputMethodUtils$InputMethodListBuilder
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.fillImes(java.util.ArrayList, android.content.Context, boolean, java.util.Locale, boolean, java.lang.String):com.android.internal.inputmethod.InputMethodUtils$InputMethodListBuilder");
        }

        public boolean isEmpty() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.isEmpty():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.isEmpty():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.inputmethod.InputMethodUtils.InputMethodListBuilder.isEmpty():boolean");
        }
    }

    public static class InputMethodSettings {
        private static final char INPUT_METHOD_SEPARATER = ':';
        private static final char INPUT_METHOD_SUBTYPE_SEPARATER = ';';
        private int[] mCurrentProfileIds;
        private int mCurrentUserId;
        private String mEnabledInputMethodsStrCache;
        private final SimpleStringSplitter mInputMethodSplitter;
        private final ArrayList<InputMethodInfo> mMethodList;
        private final HashMap<String, InputMethodInfo> mMethodMap;
        private final Resources mRes;
        private final ContentResolver mResolver;
        private final SimpleStringSplitter mSubtypeSplitter;

        private static void buildEnabledInputMethodsSettingString(StringBuilder builder, Pair<String, ArrayList<String>> pair) {
            ArrayList<String> subtypes = pair.second;
            builder.append(pair.first);
            Iterator i$ = subtypes.iterator();
            while (i$.hasNext()) {
                builder.append(INPUT_METHOD_SUBTYPE_SEPARATER).append((String) i$.next());
            }
        }

        public InputMethodSettings(Resources res, ContentResolver resolver, HashMap<String, InputMethodInfo> methodMap, ArrayList<InputMethodInfo> methodList, int userId) {
            this.mInputMethodSplitter = new SimpleStringSplitter(INPUT_METHOD_SEPARATER);
            this.mSubtypeSplitter = new SimpleStringSplitter(INPUT_METHOD_SUBTYPE_SEPARATER);
            this.mCurrentProfileIds = new int[0];
            setCurrentUserId(userId);
            this.mRes = res;
            this.mResolver = resolver;
            this.mMethodMap = methodMap;
            this.mMethodList = methodList;
        }

        public void setCurrentUserId(int userId) {
            this.mCurrentUserId = userId;
        }

        public void setCurrentProfileIds(int[] currentProfileIds) {
            synchronized (this) {
                this.mCurrentProfileIds = currentProfileIds;
            }
        }

        public boolean isCurrentProfile(int userId) {
            boolean z = true;
            synchronized (this) {
                if (userId == this.mCurrentUserId) {
                } else {
                    for (int i : this.mCurrentProfileIds) {
                        if (userId == i) {
                            break;
                        }
                    }
                    z = InputMethodUtils.DEBUG;
                }
            }
            return z;
        }

        public List<InputMethodInfo> getEnabledInputMethodListLocked() {
            return createEnabledInputMethodListLocked(getEnabledInputMethodsAndSubtypeListLocked());
        }

        public List<Pair<InputMethodInfo, ArrayList<String>>> getEnabledInputMethodAndSubtypeHashCodeListLocked() {
            return createEnabledInputMethodAndSubtypeHashCodeListLocked(getEnabledInputMethodsAndSubtypeListLocked());
        }

        public List<InputMethodSubtype> getEnabledInputMethodSubtypeListLocked(Context context, InputMethodInfo imi, boolean allowsImplicitlySelectedSubtypes) {
            List<InputMethodSubtype> enabledSubtypes = getEnabledInputMethodSubtypeListLocked(imi);
            if (allowsImplicitlySelectedSubtypes && enabledSubtypes.isEmpty()) {
                enabledSubtypes = InputMethodUtils.getImplicitlyApplicableSubtypesLocked(context.getResources(), imi);
            }
            return InputMethodSubtype.sort(context, 0, imi, enabledSubtypes);
        }

        public List<InputMethodSubtype> getEnabledInputMethodSubtypeListLocked(InputMethodInfo imi) {
            List<Pair<String, ArrayList<String>>> imsList = getEnabledInputMethodsAndSubtypeListLocked();
            ArrayList<InputMethodSubtype> enabledSubtypes = new ArrayList();
            if (imi != null) {
                Iterator i$;
                for (Pair<String, ArrayList<String>> imsPair : imsList) {
                    InputMethodInfo info = (InputMethodInfo) this.mMethodMap.get(imsPair.first);
                    if (info != null && info.getId().equals(imi.getId())) {
                        int subtypeCount = info.getSubtypeCount();
                        for (int i = 0; i < subtypeCount; i++) {
                            InputMethodSubtype ims = info.getSubtypeAt(i);
                            i$ = ((ArrayList) imsPair.second).iterator();
                            while (i$.hasNext()) {
                                if (String.valueOf(ims.hashCode()).equals((String) i$.next())) {
                                    enabledSubtypes.add(ims);
                                }
                            }
                        }
                    }
                }
            }
            return enabledSubtypes;
        }

        public void enableAllIMEsIfThereIsNoEnabledIME() {
            if (TextUtils.isEmpty(getEnabledInputMethodsStr())) {
                StringBuilder sb = new StringBuilder();
                int N = this.mMethodList.size();
                for (int i = 0; i < N; i++) {
                    InputMethodInfo imi = (InputMethodInfo) this.mMethodList.get(i);
                    Slog.i(InputMethodUtils.TAG, "Adding: " + imi.getId());
                    if (i > 0) {
                        sb.append(INPUT_METHOD_SEPARATER);
                    }
                    sb.append(imi.getId());
                }
                putEnabledInputMethodsStr(sb.toString());
            }
        }

        public List<Pair<String, ArrayList<String>>> getEnabledInputMethodsAndSubtypeListLocked() {
            ArrayList<Pair<String, ArrayList<String>>> imsList = new ArrayList();
            String enabledInputMethodsStr = getEnabledInputMethodsStr();
            if (!TextUtils.isEmpty(enabledInputMethodsStr)) {
                this.mInputMethodSplitter.setString(enabledInputMethodsStr);
                while (this.mInputMethodSplitter.hasNext()) {
                    this.mSubtypeSplitter.setString(this.mInputMethodSplitter.next());
                    if (this.mSubtypeSplitter.hasNext()) {
                        ArrayList<String> subtypeHashes = new ArrayList();
                        String imeId = this.mSubtypeSplitter.next();
                        while (this.mSubtypeSplitter.hasNext()) {
                            subtypeHashes.add(this.mSubtypeSplitter.next());
                        }
                        imsList.add(new Pair(imeId, subtypeHashes));
                    }
                }
            }
            return imsList;
        }

        public void appendAndPutEnabledInputMethodLocked(String id, boolean reloadInputMethodStr) {
            if (reloadInputMethodStr) {
                getEnabledInputMethodsStr();
            }
            if (TextUtils.isEmpty(this.mEnabledInputMethodsStrCache)) {
                putEnabledInputMethodsStr(id);
            } else {
                putEnabledInputMethodsStr(this.mEnabledInputMethodsStrCache + INPUT_METHOD_SEPARATER + id);
            }
        }

        public boolean buildAndPutEnabledInputMethodsStrRemovingIdLocked(StringBuilder builder, List<Pair<String, ArrayList<String>>> imsList, String id) {
            boolean isRemoved = InputMethodUtils.DEBUG;
            boolean needsAppendSeparator = InputMethodUtils.DEBUG;
            for (Pair<String, ArrayList<String>> ims : imsList) {
                if (ims.first.equals(id)) {
                    isRemoved = true;
                } else {
                    if (needsAppendSeparator) {
                        builder.append(INPUT_METHOD_SEPARATER);
                    } else {
                        needsAppendSeparator = true;
                    }
                    buildEnabledInputMethodsSettingString(builder, ims);
                }
            }
            if (isRemoved) {
                putEnabledInputMethodsStr(builder.toString());
            }
            return isRemoved;
        }

        private List<InputMethodInfo> createEnabledInputMethodListLocked(List<Pair<String, ArrayList<String>>> imsList) {
            ArrayList<InputMethodInfo> res = new ArrayList();
            for (Pair<String, ArrayList<String>> ims : imsList) {
                InputMethodInfo info = (InputMethodInfo) this.mMethodMap.get(ims.first);
                if (info != null) {
                    res.add(info);
                }
            }
            return res;
        }

        private List<Pair<InputMethodInfo, ArrayList<String>>> createEnabledInputMethodAndSubtypeHashCodeListLocked(List<Pair<String, ArrayList<String>>> imsList) {
            ArrayList<Pair<InputMethodInfo, ArrayList<String>>> res = new ArrayList();
            for (Pair<String, ArrayList<String>> ims : imsList) {
                InputMethodInfo info = (InputMethodInfo) this.mMethodMap.get(ims.first);
                if (info != null) {
                    res.add(new Pair(info, ims.second));
                }
            }
            return res;
        }

        private void putEnabledInputMethodsStr(String str) {
            Secure.putStringForUser(this.mResolver, "enabled_input_methods", str, this.mCurrentUserId);
            this.mEnabledInputMethodsStrCache = str;
        }

        public String getEnabledInputMethodsStr() {
            this.mEnabledInputMethodsStrCache = Secure.getStringForUser(this.mResolver, "enabled_input_methods", this.mCurrentUserId);
            return this.mEnabledInputMethodsStrCache;
        }

        private void saveSubtypeHistory(List<Pair<String, String>> savedImes, String newImeId, String newSubtypeId) {
            StringBuilder builder = new StringBuilder();
            boolean isImeAdded = InputMethodUtils.DEBUG;
            if (!(TextUtils.isEmpty(newImeId) || TextUtils.isEmpty(newSubtypeId))) {
                builder.append(newImeId).append(INPUT_METHOD_SUBTYPE_SEPARATER).append(newSubtypeId);
                isImeAdded = true;
            }
            for (Pair<String, String> ime : savedImes) {
                String imeId = ime.first;
                String subtypeId = ime.second;
                if (TextUtils.isEmpty(subtypeId)) {
                    subtypeId = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                }
                if (isImeAdded) {
                    builder.append(INPUT_METHOD_SEPARATER);
                } else {
                    isImeAdded = true;
                }
                builder.append(imeId).append(INPUT_METHOD_SUBTYPE_SEPARATER).append(subtypeId);
            }
            putSubtypeHistoryStr(builder.toString());
        }

        private void addSubtypeToHistory(String imeId, String subtypeId) {
            List<Pair<String, String>> subtypeHistory = loadInputMethodAndSubtypeHistoryLocked();
            for (Pair<String, String> ime : subtypeHistory) {
                if (((String) ime.first).equals(imeId)) {
                    subtypeHistory.remove(ime);
                    break;
                }
            }
            saveSubtypeHistory(subtypeHistory, imeId, subtypeId);
        }

        private void putSubtypeHistoryStr(String str) {
            Secure.putStringForUser(this.mResolver, "input_methods_subtype_history", str, this.mCurrentUserId);
        }

        public Pair<String, String> getLastInputMethodAndSubtypeLocked() {
            return getLastSubtypeForInputMethodLockedInternal(null);
        }

        public String getLastSubtypeForInputMethodLocked(String imeId) {
            Pair<String, String> ime = getLastSubtypeForInputMethodLockedInternal(imeId);
            if (ime != null) {
                return (String) ime.second;
            }
            return null;
        }

        private Pair<String, String> getLastSubtypeForInputMethodLockedInternal(String imeId) {
            List<Pair<String, ArrayList<String>>> enabledImes = getEnabledInputMethodsAndSubtypeListLocked();
            for (Pair<String, String> imeAndSubtype : loadInputMethodAndSubtypeHistoryLocked()) {
                String imeInTheHistory = imeAndSubtype.first;
                if (TextUtils.isEmpty(imeId) || imeInTheHistory.equals(imeId)) {
                    String subtypeHashCode = getEnabledSubtypeHashCodeForInputMethodAndSubtypeLocked(enabledImes, imeInTheHistory, imeAndSubtype.second);
                    if (!TextUtils.isEmpty(subtypeHashCode)) {
                        return new Pair(imeInTheHistory, subtypeHashCode);
                    }
                }
            }
            return null;
        }

        private String getEnabledSubtypeHashCodeForInputMethodAndSubtypeLocked(List<Pair<String, ArrayList<String>>> enabledImes, String imeId, String subtypeHashCode) {
            Iterator i$;
            for (Pair<String, ArrayList<String>> enabledIme : enabledImes) {
                if (((String) enabledIme.first).equals(imeId)) {
                    ArrayList<String> explicitlyEnabledSubtypes = enabledIme.second;
                    InputMethodInfo imi = (InputMethodInfo) this.mMethodMap.get(imeId);
                    if (explicitlyEnabledSubtypes.size() != 0) {
                        i$ = explicitlyEnabledSubtypes.iterator();
                        while (i$.hasNext()) {
                            String s = (String) i$.next();
                            if (s.equals(subtypeHashCode)) {
                                try {
                                    if (InputMethodUtils.isValidSubtypeId(imi, Integer.valueOf(subtypeHashCode).intValue())) {
                                        return s;
                                    }
                                    return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                                } catch (NumberFormatException e) {
                                    return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                                }
                            }
                        }
                    } else if (imi != null && imi.getSubtypeCount() > 0) {
                        List<InputMethodSubtype> implicitlySelectedSubtypes = InputMethodUtils.getImplicitlyApplicableSubtypesLocked(this.mRes, imi);
                        if (implicitlySelectedSubtypes != null) {
                            int N = implicitlySelectedSubtypes.size();
                            for (int i = 0; i < N; i++) {
                                if (String.valueOf(((InputMethodSubtype) implicitlySelectedSubtypes.get(i)).hashCode()).equals(subtypeHashCode)) {
                                    return subtypeHashCode;
                                }
                            }
                        }
                    }
                    return InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                }
            }
            return null;
        }

        private List<Pair<String, String>> loadInputMethodAndSubtypeHistoryLocked() {
            ArrayList<Pair<String, String>> imsList = new ArrayList();
            String subtypeHistoryStr = getSubtypeHistoryStr();
            if (!TextUtils.isEmpty(subtypeHistoryStr)) {
                this.mInputMethodSplitter.setString(subtypeHistoryStr);
                while (this.mInputMethodSplitter.hasNext()) {
                    this.mSubtypeSplitter.setString(this.mInputMethodSplitter.next());
                    if (this.mSubtypeSplitter.hasNext()) {
                        String subtypeId = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
                        String imeId = this.mSubtypeSplitter.next();
                        if (this.mSubtypeSplitter.hasNext()) {
                            subtypeId = this.mSubtypeSplitter.next();
                        }
                        imsList.add(new Pair(imeId, subtypeId));
                    }
                }
            }
            return imsList;
        }

        private String getSubtypeHistoryStr() {
            return Secure.getStringForUser(this.mResolver, "input_methods_subtype_history", this.mCurrentUserId);
        }

        public void putSelectedInputMethod(String imeId) {
            Secure.putStringForUser(this.mResolver, "default_input_method", imeId, this.mCurrentUserId);
        }

        public void putSelectedSubtype(int subtypeId) {
            Secure.putIntForUser(this.mResolver, "selected_input_method_subtype", subtypeId, this.mCurrentUserId);
        }

        public String getDisabledSystemInputMethods() {
            return Secure.getStringForUser(this.mResolver, "disabled_system_input_methods", this.mCurrentUserId);
        }

        public String getSelectedInputMethod() {
            return Secure.getStringForUser(this.mResolver, "default_input_method", this.mCurrentUserId);
        }

        public boolean isSubtypeSelected() {
            return getSelectedInputMethodSubtypeHashCode() != InputMethodUtils.NOT_A_SUBTYPE_ID ? true : InputMethodUtils.DEBUG;
        }

        private int getSelectedInputMethodSubtypeHashCode() {
            try {
                return Secure.getIntForUser(this.mResolver, "selected_input_method_subtype", this.mCurrentUserId);
            } catch (SettingNotFoundException e) {
                return InputMethodUtils.NOT_A_SUBTYPE_ID;
            }
        }

        public boolean isShowImeWithHardKeyboardEnabled() {
            return Secure.getIntForUser(this.mResolver, "show_ime_with_hard_keyboard", 0, this.mCurrentUserId) == 1 ? true : InputMethodUtils.DEBUG;
        }

        public void setShowImeWithHardKeyboard(boolean show) {
            Secure.putIntForUser(this.mResolver, "show_ime_with_hard_keyboard", show ? 1 : 0, this.mCurrentUserId);
        }

        public int getCurrentUserId() {
            return this.mCurrentUserId;
        }

        public int getSelectedInputMethodSubtypeId(String selectedImiId) {
            InputMethodInfo imi = (InputMethodInfo) this.mMethodMap.get(selectedImiId);
            if (imi == null) {
                return InputMethodUtils.NOT_A_SUBTYPE_ID;
            }
            return InputMethodUtils.getSubtypeIdFromHashCode(imi, getSelectedInputMethodSubtypeHashCode());
        }

        public void saveCurrentInputMethodAndSubtypeToHistory(String curMethodId, InputMethodSubtype currentSubtype) {
            String subtypeId = InputMethodUtils.NOT_A_SUBTYPE_ID_STR;
            if (currentSubtype != null) {
                subtypeId = String.valueOf(currentSubtype.hashCode());
            }
            if (InputMethodUtils.canAddToLastInputMethod(currentSubtype)) {
                addSubtypeToHistory(curMethodId, subtypeId);
            }
        }

        public HashMap<InputMethodInfo, List<InputMethodSubtype>> getExplicitlyOrImplicitlyEnabledInputMethodsAndSubtypeListLocked(Context context) {
            HashMap<InputMethodInfo, List<InputMethodSubtype>> enabledInputMethodAndSubtypes = new HashMap();
            for (InputMethodInfo imi : getEnabledInputMethodListLocked()) {
                enabledInputMethodAndSubtypes.put(imi, getEnabledInputMethodSubtypeListLocked(context, imi, true));
            }
            return enabledInputMethodAndSubtypes;
        }
    }

    static {
        SUBTYPE_MODE_ANY = null;
        ENGLISH_LOCALE = new Locale("en");
        NOT_A_SUBTYPE_ID_STR = String.valueOf(NOT_A_SUBTYPE_ID);
        SEARCH_ORDER_OF_FALLBACK_LOCALES = new Locale[]{Locale.ENGLISH, Locale.US, Locale.UK};
    }

    private InputMethodUtils() {
    }

    public static String getStackTrace() {
        StringBuilder sb = new StringBuilder();
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            StackTraceElement[] frames = e.getStackTrace();
            for (int j = 1; j < frames.length; j++) {
                sb.append(frames[j].toString() + "\n");
            }
            return sb.toString();
        }
    }

    public static String getApiCallStack() {
        String apiCallStack = "";
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            StackTraceElement[] frames = e.getStackTrace();
            for (int j = 1; j < frames.length; j++) {
                String tempCallStack = frames[j].toString();
                if (!TextUtils.isEmpty(apiCallStack)) {
                    if (tempCallStack.indexOf("Transact(") >= 0) {
                        break;
                    }
                    apiCallStack = tempCallStack;
                } else {
                    apiCallStack = tempCallStack;
                }
            }
            return apiCallStack;
        }
    }

    public static boolean isSystemIme(InputMethodInfo inputMethod) {
        return (inputMethod.getServiceInfo().applicationInfo.flags & 1) != 0 ? true : DEBUG;
    }

    @Deprecated
    public static boolean isSystemImeThatHasEnglishKeyboardSubtype(InputMethodInfo imi) {
        if (isSystemIme(imi)) {
            return containsSubtypeOf(imi, ENGLISH_LOCALE.getLanguage(), SUBTYPE_MODE_KEYBOARD);
        }
        return DEBUG;
    }

    private static boolean isSystemImeThatHasSubtypeOf(InputMethodInfo imi, Context context, boolean checkDefaultAttribute, Locale requiredLocale, boolean checkCountry, String requiredSubtypeMode) {
        if (!isSystemIme(imi)) {
            return DEBUG;
        }
        if ((!checkDefaultAttribute || imi.isDefault(context)) && containsSubtypeOf(imi, requiredLocale, checkCountry, requiredSubtypeMode)) {
            return true;
        }
        return DEBUG;
    }

    public static Locale getFallbackLocaleForDefaultIme(ArrayList<InputMethodInfo> imis, Context context) {
        for (Locale fallbackLocale : SEARCH_ORDER_OF_FALLBACK_LOCALES) {
            int i;
            for (i = 0; i < imis.size(); i++) {
                if (isSystemImeThatHasSubtypeOf((InputMethodInfo) imis.get(i), context, true, fallbackLocale, true, SUBTYPE_MODE_KEYBOARD)) {
                    return fallbackLocale;
                }
            }
        }
        for (Locale fallbackLocale2 : SEARCH_ORDER_OF_FALLBACK_LOCALES) {
            for (i = 0; i < imis.size(); i++) {
                if (isSystemImeThatHasSubtypeOf((InputMethodInfo) imis.get(i), context, DEBUG, fallbackLocale2, true, SUBTYPE_MODE_KEYBOARD)) {
                    return fallbackLocale2;
                }
            }
        }
        Slog.w(TAG, "Found no fallback locale. imis=" + Arrays.toString(imis.toArray()));
        return null;
    }

    private static boolean isSystemAuxilialyImeThatHasAutomaticSubtype(InputMethodInfo imi, Context context, boolean checkDefaultAttribute) {
        if (!isSystemIme(imi)) {
            return DEBUG;
        }
        if ((checkDefaultAttribute && !imi.isDefault(context)) || !imi.isAuxiliaryIme()) {
            return DEBUG;
        }
        int subtypeCount = imi.getSubtypeCount();
        for (int i = 0; i < subtypeCount; i++) {
            if (imi.getSubtypeAt(i).overridesImplicitlyEnabledSubtype()) {
                return true;
            }
        }
        return DEBUG;
    }

    public static Locale getSystemLocaleFromContext(Context context) {
        try {
            return context.getResources().getConfiguration().locale;
        } catch (NotFoundException e) {
            return null;
        }
    }

    private static InputMethodListBuilder getMinimumKeyboardSetWithoutSystemLocale(ArrayList<InputMethodInfo> imis, Context context, Locale fallbackLocale) {
        InputMethodListBuilder builder = new InputMethodListBuilder();
        builder.fillImes(imis, context, true, fallbackLocale, true, SUBTYPE_MODE_KEYBOARD);
        if (builder.isEmpty()) {
            builder.fillImes(imis, context, DEBUG, fallbackLocale, true, SUBTYPE_MODE_KEYBOARD);
            if (builder.isEmpty()) {
                builder.fillImes(imis, context, true, fallbackLocale, DEBUG, SUBTYPE_MODE_KEYBOARD);
                if (builder.isEmpty()) {
                    builder.fillImes(imis, context, DEBUG, fallbackLocale, DEBUG, SUBTYPE_MODE_KEYBOARD);
                    if (builder.isEmpty()) {
                        Slog.w(TAG, "No software keyboard is found. imis=" + Arrays.toString(imis.toArray()) + " fallbackLocale=" + fallbackLocale);
                    }
                }
            }
        }
        return builder;
    }

    private static InputMethodListBuilder getMinimumKeyboardSetWithSystemLocale(ArrayList<InputMethodInfo> imis, Context context, Locale systemLocale, Locale fallbackLocale) {
        InputMethodListBuilder builder = new InputMethodListBuilder();
        builder.fillImes(imis, context, true, systemLocale, true, SUBTYPE_MODE_KEYBOARD);
        if (builder.isEmpty()) {
            builder.fillImes(imis, context, true, systemLocale, DEBUG, SUBTYPE_MODE_KEYBOARD);
            if (builder.isEmpty()) {
                builder.fillImes(imis, context, true, fallbackLocale, true, SUBTYPE_MODE_KEYBOARD);
                if (builder.isEmpty()) {
                    builder.fillImes(imis, context, true, fallbackLocale, DEBUG, SUBTYPE_MODE_KEYBOARD);
                    if (builder.isEmpty()) {
                        builder.fillImes(imis, context, DEBUG, fallbackLocale, true, SUBTYPE_MODE_KEYBOARD);
                        if (builder.isEmpty()) {
                            builder.fillImes(imis, context, DEBUG, fallbackLocale, DEBUG, SUBTYPE_MODE_KEYBOARD);
                            if (builder.isEmpty()) {
                                Slog.w(TAG, "No software keyboard is found. imis=" + Arrays.toString(imis.toArray()) + " systemLocale=" + systemLocale + " fallbackLocale=" + fallbackLocale);
                            }
                        }
                    }
                }
            }
        }
        return builder;
    }

    public static ArrayList<InputMethodInfo> getDefaultEnabledImes(Context context, boolean isSystemReady, ArrayList<InputMethodInfo> imis) {
        Locale fallbackLocale = getFallbackLocaleForDefaultIme(imis, context);
        if (isSystemReady) {
            Locale systemLocale = getSystemLocaleFromContext(context);
            return getMinimumKeyboardSetWithSystemLocale(imis, context, systemLocale, fallbackLocale).fillImes(imis, context, true, systemLocale, true, SUBTYPE_MODE_ANY).fillAuxiliaryImes(imis, context).build();
        }
        return getMinimumKeyboardSetWithoutSystemLocale(imis, context, fallbackLocale).fillImes(imis, context, true, fallbackLocale, true, SUBTYPE_MODE_ANY).build();
    }

    @Deprecated
    public static boolean isValidSystemDefaultIme(boolean isSystemReady, InputMethodInfo imi, Context context) {
        if (!isSystemReady || !isSystemIme(imi)) {
            return DEBUG;
        }
        if (imi.getIsDefaultResourceId() != 0) {
            try {
                if (imi.isDefault(context) && containsSubtypeOf(imi, context.getResources().getConfiguration().locale.getLanguage(), SUBTYPE_MODE_ANY)) {
                    return true;
                }
            } catch (NotFoundException e) {
            }
        }
        if (imi.getSubtypeCount() != 0) {
            return DEBUG;
        }
        Slog.w(TAG, "Found no subtypes in a system IME: " + imi.getPackageName());
        return DEBUG;
    }

    public static boolean containsSubtypeOf(InputMethodInfo imi, Locale locale, boolean checkCountry, String mode) {
        if (locale == null) {
            return DEBUG;
        }
        int N = imi.getSubtypeCount();
        for (int i = 0; i < N; i++) {
            InputMethodSubtype subtype = imi.getSubtypeAt(i);
            if (checkCountry) {
                if (!TextUtils.equals(subtype.getLocale(), locale.toString())) {
                    continue;
                }
                if (mode != SUBTYPE_MODE_ANY || TextUtils.isEmpty(mode) || mode.equalsIgnoreCase(subtype.getMode())) {
                    return true;
                }
            } else {
                if (!new Locale(getLanguageFromLocaleString(subtype.getLocale())).getLanguage().equals(locale.getLanguage())) {
                    continue;
                }
                if (mode != SUBTYPE_MODE_ANY) {
                }
                return true;
            }
        }
        return DEBUG;
    }

    @Deprecated
    public static boolean containsSubtypeOf(InputMethodInfo imi, String language, String mode) {
        int N = imi.getSubtypeCount();
        for (int i = 0; i < N; i++) {
            InputMethodSubtype subtype = imi.getSubtypeAt(i);
            if (subtype.getLocale().startsWith(language) && (mode == SUBTYPE_MODE_ANY || TextUtils.isEmpty(mode) || mode.equalsIgnoreCase(subtype.getMode()))) {
                return true;
            }
        }
        return DEBUG;
    }

    public static ArrayList<InputMethodSubtype> getSubtypes(InputMethodInfo imi) {
        ArrayList<InputMethodSubtype> subtypes = new ArrayList();
        int subtypeCount = imi.getSubtypeCount();
        for (int i = 0; i < subtypeCount; i++) {
            subtypes.add(imi.getSubtypeAt(i));
        }
        return subtypes;
    }

    public static ArrayList<InputMethodSubtype> getOverridingImplicitlyEnabledSubtypes(InputMethodInfo imi, String mode) {
        ArrayList<InputMethodSubtype> subtypes = new ArrayList();
        int subtypeCount = imi.getSubtypeCount();
        for (int i = 0; i < subtypeCount; i++) {
            InputMethodSubtype subtype = imi.getSubtypeAt(i);
            if (subtype.overridesImplicitlyEnabledSubtype() && subtype.getMode().equals(mode)) {
                subtypes.add(subtype);
            }
        }
        return subtypes;
    }

    public static InputMethodInfo getMostApplicableDefaultIME(List<InputMethodInfo> enabledImes) {
        if (enabledImes == null || enabledImes.isEmpty()) {
            return null;
        }
        int i = enabledImes.size();
        int firstFoundSystemIme = NOT_A_SUBTYPE_ID;
        while (i > 0) {
            i += NOT_A_SUBTYPE_ID;
            InputMethodInfo imi = (InputMethodInfo) enabledImes.get(i);
            if (isSystemImeThatHasEnglishKeyboardSubtype(imi) && !imi.isAuxiliaryIme()) {
                return imi;
            }
            if (firstFoundSystemIme < 0 && isSystemIme(imi) && !imi.isAuxiliaryIme()) {
                firstFoundSystemIme = i;
            }
        }
        return (InputMethodInfo) enabledImes.get(Math.max(firstFoundSystemIme, 0));
    }

    public static boolean isValidSubtypeId(InputMethodInfo imi, int subtypeHashCode) {
        return getSubtypeIdFromHashCode(imi, subtypeHashCode) != NOT_A_SUBTYPE_ID ? true : DEBUG;
    }

    public static int getSubtypeIdFromHashCode(InputMethodInfo imi, int subtypeHashCode) {
        if (imi != null) {
            int subtypeCount = imi.getSubtypeCount();
            for (int i = 0; i < subtypeCount; i++) {
                if (subtypeHashCode == imi.getSubtypeAt(i).hashCode()) {
                    return i;
                }
            }
        }
        return NOT_A_SUBTYPE_ID;
    }

    private static ArrayList<InputMethodSubtype> getImplicitlyApplicableSubtypesLocked(Resources res, InputMethodInfo imi) {
        List<InputMethodSubtype> subtypes = getSubtypes(imi);
        String systemLocale = res.getConfiguration().locale.toString();
        if (TextUtils.isEmpty(systemLocale)) {
            return new ArrayList();
        }
        int i;
        String mode;
        String systemLanguage = res.getConfiguration().locale.getLanguage();
        HashMap<String, InputMethodSubtype> applicableModeAndSubtypesMap = new HashMap();
        int N = subtypes.size();
        for (i = 0; i < N; i++) {
            InputMethodSubtype subtype = (InputMethodSubtype) subtypes.get(i);
            if (subtype.overridesImplicitlyEnabledSubtype()) {
                mode = subtype.getMode();
                if (!applicableModeAndSubtypesMap.containsKey(mode)) {
                    applicableModeAndSubtypesMap.put(mode, subtype);
                }
            }
        }
        if (applicableModeAndSubtypesMap.size() > 0) {
            return new ArrayList(applicableModeAndSubtypesMap.values());
        }
        for (i = 0; i < N; i++) {
            subtype = (InputMethodSubtype) subtypes.get(i);
            String locale = subtype.getLocale();
            mode = subtype.getMode();
            if (getLanguageFromLocaleString(locale).equals(systemLanguage) && systemLocale.startsWith(locale)) {
                InputMethodSubtype applicableSubtype = (InputMethodSubtype) applicableModeAndSubtypesMap.get(mode);
                if (applicableSubtype == null || (!systemLocale.equals(applicableSubtype.getLocale()) && systemLocale.equals(locale))) {
                    applicableModeAndSubtypesMap.put(mode, subtype);
                }
            }
        }
        InputMethodSubtype keyboardSubtype = (InputMethodSubtype) applicableModeAndSubtypesMap.get(SUBTYPE_MODE_KEYBOARD);
        ArrayList<InputMethodSubtype> applicableSubtypes = new ArrayList(applicableModeAndSubtypesMap.values());
        if (keyboardSubtype != null) {
            if (!keyboardSubtype.containsExtraValueKey(TAG_ASCII_CAPABLE)) {
                for (i = 0; i < N; i++) {
                    subtype = (InputMethodSubtype) subtypes.get(i);
                    mode = subtype.getMode();
                    if (SUBTYPE_MODE_KEYBOARD.equals(mode)) {
                        if (subtype.containsExtraValueKey(TAG_ENABLED_WHEN_DEFAULT_IS_NOT_ASCII_CAPABLE)) {
                            applicableSubtypes.add(subtype);
                        }
                    }
                }
            }
        }
        if (keyboardSubtype != null) {
            return applicableSubtypes;
        }
        InputMethodSubtype lastResortKeyboardSubtype = findLastResortApplicableSubtypeLocked(res, subtypes, SUBTYPE_MODE_KEYBOARD, systemLocale, true);
        if (lastResortKeyboardSubtype == null) {
            return applicableSubtypes;
        }
        applicableSubtypes.add(lastResortKeyboardSubtype);
        return applicableSubtypes;
    }

    public static String getLanguageFromLocaleString(String locale) {
        int idx = locale.indexOf(95);
        return idx < 0 ? locale : locale.substring(0, idx);
    }

    public static InputMethodSubtype findLastResortApplicableSubtypeLocked(Resources res, List<InputMethodSubtype> subtypes, String mode, String locale, boolean canIgnoreLocaleAsLastResort) {
        if (subtypes == null || subtypes.size() == 0) {
            return null;
        }
        if (TextUtils.isEmpty(locale)) {
            locale = res.getConfiguration().locale.toString();
        }
        String language = getLanguageFromLocaleString(locale);
        boolean partialMatchFound = DEBUG;
        InputMethodSubtype applicableSubtype = null;
        InputMethodSubtype firstMatchedModeSubtype = null;
        int N = subtypes.size();
        int i = 0;
        while (i < N) {
            InputMethodSubtype subtype = (InputMethodSubtype) subtypes.get(i);
            String subtypeLocale = subtype.getLocale();
            String subtypeLanguage = getLanguageFromLocaleString(subtypeLocale);
            if (mode == null || ((InputMethodSubtype) subtypes.get(i)).getMode().equalsIgnoreCase(mode)) {
                if (firstMatchedModeSubtype == null) {
                    firstMatchedModeSubtype = subtype;
                }
                if (locale.equals(subtypeLocale)) {
                    applicableSubtype = subtype;
                    break;
                } else if (!partialMatchFound && language.equals(subtypeLanguage)) {
                    applicableSubtype = subtype;
                    partialMatchFound = true;
                }
            }
            i++;
        }
        return (applicableSubtype == null && canIgnoreLocaleAsLastResort) ? firstMatchedModeSubtype : applicableSubtype;
    }

    public static boolean canAddToLastInputMethod(InputMethodSubtype subtype) {
        if (subtype != null && subtype.isAuxiliary()) {
            return DEBUG;
        }
        return true;
    }

    public static void setNonSelectedSystemImesDisabledUntilUsed(PackageManager packageManager, List<InputMethodInfo> enabledImis) {
        String[] systemImesDisabledUntilUsed = Resources.getSystem().getStringArray(R.array.config_disabledUntilUsedPreinstalledImes);
        if (systemImesDisabledUntilUsed != null && systemImesDisabledUntilUsed.length != 0) {
            SpellCheckerInfo currentSpellChecker = TextServicesManager.getInstance().getCurrentSpellChecker();
            for (String packageName : systemImesDisabledUntilUsed) {
                boolean enabledIme = DEBUG;
                for (int j = 0; j < enabledImis.size(); j++) {
                    if (packageName.equals(((InputMethodInfo) enabledImis.get(j)).getPackageName())) {
                        enabledIme = true;
                        break;
                    }
                }
                if (!enabledIme && (currentSpellChecker == null || !packageName.equals(currentSpellChecker.getPackageName()))) {
                    ApplicationInfo ai = null;
                    try {
                        ai = packageManager.getApplicationInfo(packageName, DateUtils.FORMAT_ABBREV_WEEKDAY);
                    } catch (NameNotFoundException e) {
                        Slog.w(TAG, "NameNotFoundException: " + packageName, e);
                    }
                    if (ai != null) {
                        if ((ai.flags & 1) != 0 ? true : DEBUG) {
                            setDisabledUntilUsed(packageManager, packageName);
                        }
                    }
                }
            }
        }
    }

    private static void setDisabledUntilUsed(PackageManager packageManager, String packageName) {
        int state = packageManager.getApplicationEnabledSetting(packageName);
        if (state == 0 || state == 1) {
            packageManager.setApplicationEnabledSetting(packageName, 4, 0);
        }
    }

    public static CharSequence getImeAndSubtypeDisplayName(Context context, InputMethodInfo imi, InputMethodSubtype subtype) {
        CharSequence imiLabel = imi.loadLabel(context.getPackageManager());
        if (subtype == null) {
            return imiLabel;
        }
        CharSequence[] charSequenceArr = new CharSequence[2];
        charSequenceArr[0] = subtype.getDisplayName(context, imi.getPackageName(), imi.getServiceInfo().applicationInfo);
        charSequenceArr[1] = TextUtils.isEmpty(imiLabel) ? "" : " - " + imiLabel;
        return TextUtils.concat(charSequenceArr);
    }

    public static boolean checkIfPackageBelongsToUid(AppOpsManager appOpsManager, int uid, String packageName) {
        try {
            appOpsManager.checkPackage(uid, packageName);
            return true;
        } catch (SecurityException e) {
            return DEBUG;
        }
    }
}
