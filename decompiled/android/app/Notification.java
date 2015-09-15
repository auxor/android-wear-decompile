package android.app;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.session.MediaSession.Token;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.os.UserHandle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.MathUtils;
import android.view.WindowManager.LayoutParams;
import android.widget.RemoteViews;
import com.android.internal.util.NotificationColorUtil;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Notification implements Parcelable {
    public static final AudioAttributes AUDIO_ATTRIBUTES_DEFAULT;
    public static final String CATEGORY_ALARM = "alarm";
    public static final String CATEGORY_CALL = "call";
    public static final String CATEGORY_EMAIL = "email";
    public static final String CATEGORY_ERROR = "err";
    public static final String CATEGORY_EVENT = "event";
    public static final String CATEGORY_MESSAGE = "msg";
    public static final String CATEGORY_PROGRESS = "progress";
    public static final String CATEGORY_PROMO = "promo";
    public static final String CATEGORY_RECOMMENDATION = "recommendation";
    public static final String CATEGORY_SERVICE = "service";
    public static final String CATEGORY_SOCIAL = "social";
    public static final String CATEGORY_STATUS = "status";
    public static final String CATEGORY_SYSTEM = "sys";
    public static final String CATEGORY_TRANSPORT = "transport";
    public static final int COLOR_DEFAULT = 0;
    public static final Creator<Notification> CREATOR;
    public static final int DEFAULT_ALL = -1;
    public static final int DEFAULT_LIGHTS = 4;
    public static final int DEFAULT_SOUND = 1;
    public static final int DEFAULT_VIBRATE = 2;
    public static final String EXTRA_ALLOW_DURING_SETUP = "android.allowDuringSetup";
    public static final String EXTRA_AS_HEADS_UP = "headsup";
    public static final String EXTRA_BACKGROUND_IMAGE_URI = "android.backgroundImageUri";
    public static final String EXTRA_BIG_TEXT = "android.bigText";
    public static final String EXTRA_COMPACT_ACTIONS = "android.compactActions";
    public static final String EXTRA_INFO_TEXT = "android.infoText";
    public static final String EXTRA_LARGE_ICON = "android.largeIcon";
    public static final String EXTRA_LARGE_ICON_BIG = "android.largeIcon.big";
    public static final String EXTRA_MEDIA_SESSION = "android.mediaSession";
    public static final String EXTRA_ORIGINATING_USERID = "android.originatingUserId";
    public static final String EXTRA_PEOPLE = "android.people";
    public static final String EXTRA_PICTURE = "android.picture";
    public static final String EXTRA_PROGRESS = "android.progress";
    public static final String EXTRA_PROGRESS_INDETERMINATE = "android.progressIndeterminate";
    public static final String EXTRA_PROGRESS_MAX = "android.progressMax";
    public static final String EXTRA_SHOW_CHRONOMETER = "android.showChronometer";
    public static final String EXTRA_SHOW_WHEN = "android.showWhen";
    public static final String EXTRA_SMALL_ICON = "android.icon";
    public static final String EXTRA_SUB_TEXT = "android.subText";
    public static final String EXTRA_SUMMARY_TEXT = "android.summaryText";
    public static final String EXTRA_TEMPLATE = "android.template";
    public static final String EXTRA_TEXT = "android.text";
    public static final String EXTRA_TEXT_LINES = "android.textLines";
    public static final String EXTRA_TITLE = "android.title";
    public static final String EXTRA_TITLE_BIG = "android.title.big";
    public static final int FLAG_AUTO_CANCEL = 16;
    public static final int FLAG_FOREGROUND_SERVICE = 64;
    public static final int FLAG_GROUP_SUMMARY = 512;
    public static final int FLAG_HIGH_PRIORITY = 128;
    public static final int FLAG_INSISTENT = 4;
    public static final int FLAG_LOCAL_ONLY = 256;
    public static final int FLAG_NO_CLEAR = 32;
    public static final int FLAG_ONGOING_EVENT = 2;
    public static final int FLAG_ONLY_ALERT_ONCE = 8;
    public static final int FLAG_SHOW_LIGHTS = 1;
    public static final int HEADS_UP_ALLOWED = 1;
    public static final int HEADS_UP_NEVER = 0;
    public static final int HEADS_UP_REQUESTED = 2;
    public static final String INTENT_CATEGORY_NOTIFICATION_PREFERENCES = "android.intent.category.NOTIFICATION_PREFERENCES";
    private static final int MAX_CHARSEQUENCE_LENGTH = 5120;
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_MAX = 2;
    public static final int PRIORITY_MIN = -2;
    @Deprecated
    public static final int STREAM_DEFAULT = -1;
    private static final String TAG = "Notification";
    public static final int VISIBILITY_PRIVATE = 0;
    public static final int VISIBILITY_PUBLIC = 1;
    public static final int VISIBILITY_SECRET = -1;
    public Action[] actions;
    public AudioAttributes audioAttributes;
    @Deprecated
    public int audioStreamType;
    public RemoteViews bigContentView;
    public String category;
    public int color;
    public PendingIntent contentIntent;
    public RemoteViews contentView;
    public int defaults;
    public PendingIntent deleteIntent;
    public Bundle extras;
    public int flags;
    public PendingIntent fullScreenIntent;
    public RemoteViews headsUpContentView;
    public int icon;
    public int iconLevel;
    public Bitmap largeIcon;
    public int ledARGB;
    public int ledOffMS;
    public int ledOnMS;
    private String mGroupKey;
    private String mSortKey;
    public int number;
    public int priority;
    public Notification publicVersion;
    public Uri sound;
    public CharSequence tickerText;
    @Deprecated
    public RemoteViews tickerView;
    public long[] vibrate;
    public int visibility;
    public long when;

    public static class Action implements Parcelable {
        public static final Creator<Action> CREATOR;
        public PendingIntent actionIntent;
        public int icon;
        private final Bundle mExtras;
        private final RemoteInput[] mRemoteInputs;
        public CharSequence title;

        public static final class Builder {
            private final Bundle mExtras;
            private final int mIcon;
            private final PendingIntent mIntent;
            private ArrayList<RemoteInput> mRemoteInputs;
            private final CharSequence mTitle;

            public Builder(int icon, CharSequence title, PendingIntent intent) {
                this(icon, title, intent, new Bundle(), null);
            }

            public Builder(Action action) {
                this(action.icon, action.title, action.actionIntent, new Bundle(action.mExtras), action.getRemoteInputs());
            }

            private Builder(int icon, CharSequence title, PendingIntent intent, Bundle extras, RemoteInput[] remoteInputs) {
                this.mIcon = icon;
                this.mTitle = title;
                this.mIntent = intent;
                this.mExtras = extras;
                if (remoteInputs != null) {
                    this.mRemoteInputs = new ArrayList(remoteInputs.length);
                    Collections.addAll(this.mRemoteInputs, remoteInputs);
                }
            }

            public Builder addExtras(Bundle extras) {
                if (extras != null) {
                    this.mExtras.putAll(extras);
                }
                return this;
            }

            public Bundle getExtras() {
                return this.mExtras;
            }

            public Builder addRemoteInput(RemoteInput remoteInput) {
                if (this.mRemoteInputs == null) {
                    this.mRemoteInputs = new ArrayList();
                }
                this.mRemoteInputs.add(remoteInput);
                return this;
            }

            public Builder extend(Extender extender) {
                extender.extend(this);
                return this;
            }

            public Action build() {
                RemoteInput[] remoteInputs;
                if (this.mRemoteInputs != null) {
                    remoteInputs = (RemoteInput[]) this.mRemoteInputs.toArray(new RemoteInput[this.mRemoteInputs.size()]);
                } else {
                    remoteInputs = null;
                }
                return new Action(this.mTitle, this.mIntent, this.mExtras, remoteInputs, null);
            }
        }

        public interface Extender {
            Builder extend(Builder builder);
        }

        public static final class WearableExtender implements Extender {
            private static final int DEFAULT_FLAGS = 1;
            private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
            private static final int FLAG_AVAILABLE_OFFLINE = 1;
            private static final String KEY_CANCEL_LABEL = "cancelLabel";
            private static final String KEY_CONFIRM_LABEL = "confirmLabel";
            private static final String KEY_FLAGS = "flags";
            private static final String KEY_IN_PROGRESS_LABEL = "inProgressLabel";
            private CharSequence mCancelLabel;
            private CharSequence mConfirmLabel;
            private int mFlags;
            private CharSequence mInProgressLabel;

            public WearableExtender() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.<init>():void");
            }

            public WearableExtender(android.app.Notification.Action r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.<init>(android.app.Notification$Action):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.<init>(android.app.Notification$Action):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.<init>(android.app.Notification$Action):void");
            }

            private void setFlag(int r1, boolean r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.setFlag(int, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.setFlag(int, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.setFlag(int, boolean):void");
            }

            public android.app.Notification.Action.WearableExtender clone() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.clone():android.app.Notification$Action$WearableExtender
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.clone():android.app.Notification$Action$WearableExtender
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.clone():android.app.Notification$Action$WearableExtender");
            }

            public android.app.Notification.Action.Builder extend(android.app.Notification.Action.Builder r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.extend(android.app.Notification$Action$Builder):android.app.Notification$Action$Builder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.extend(android.app.Notification$Action$Builder):android.app.Notification$Action$Builder
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.extend(android.app.Notification$Action$Builder):android.app.Notification$Action$Builder");
            }

            public java.lang.CharSequence getCancelLabel() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.getCancelLabel():java.lang.CharSequence
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.getCancelLabel():java.lang.CharSequence
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.getCancelLabel():java.lang.CharSequence");
            }

            public java.lang.CharSequence getConfirmLabel() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.getConfirmLabel():java.lang.CharSequence
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.getConfirmLabel():java.lang.CharSequence
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.getConfirmLabel():java.lang.CharSequence");
            }

            public java.lang.CharSequence getInProgressLabel() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.getInProgressLabel():java.lang.CharSequence
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.getInProgressLabel():java.lang.CharSequence
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.getInProgressLabel():java.lang.CharSequence");
            }

            public boolean isAvailableOffline() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.isAvailableOffline():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.isAvailableOffline():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.isAvailableOffline():boolean");
            }

            public android.app.Notification.Action.WearableExtender setCancelLabel(java.lang.CharSequence r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.setCancelLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.setCancelLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.setCancelLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender");
            }

            public android.app.Notification.Action.WearableExtender setConfirmLabel(java.lang.CharSequence r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.setConfirmLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.setConfirmLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.setConfirmLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender");
            }

            public android.app.Notification.Action.WearableExtender setInProgressLabel(java.lang.CharSequence r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.Notification.Action.WearableExtender.setInProgressLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.Notification.Action.WearableExtender.setInProgressLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.app.Notification.Action.WearableExtender.setInProgressLabel(java.lang.CharSequence):android.app.Notification$Action$WearableExtender");
            }

            public WearableExtender setAvailableOffline(boolean availableOffline) {
                setFlag(FLAG_AVAILABLE_OFFLINE, availableOffline);
                return this;
            }
        }

        private Action(Parcel in) {
            this.icon = in.readInt();
            this.title = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            if (in.readInt() == Notification.VISIBILITY_PUBLIC) {
                this.actionIntent = (PendingIntent) PendingIntent.CREATOR.createFromParcel(in);
            }
            this.mExtras = in.readBundle();
            this.mRemoteInputs = (RemoteInput[]) in.createTypedArray(RemoteInput.CREATOR);
        }

        public Action(int icon, CharSequence title, PendingIntent intent) {
            this(icon, title, intent, new Bundle(), null);
        }

        private Action(int icon, CharSequence title, PendingIntent intent, Bundle extras, RemoteInput[] remoteInputs) {
            this.icon = icon;
            this.title = title;
            this.actionIntent = intent;
            if (extras == null) {
                extras = new Bundle();
            }
            this.mExtras = extras;
            this.mRemoteInputs = remoteInputs;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public RemoteInput[] getRemoteInputs() {
            return this.mRemoteInputs;
        }

        public Action clone() {
            return new Action(this.icon, this.title, this.actionIntent, new Bundle(this.mExtras), getRemoteInputs());
        }

        public int describeContents() {
            return Notification.VISIBILITY_PRIVATE;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.icon);
            TextUtils.writeToParcel(this.title, out, flags);
            if (this.actionIntent != null) {
                out.writeInt(Notification.VISIBILITY_PUBLIC);
                this.actionIntent.writeToParcel(out, flags);
            } else {
                out.writeInt(Notification.VISIBILITY_PRIVATE);
            }
            out.writeBundle(this.mExtras);
            out.writeTypedArray(this.mRemoteInputs, flags);
        }

        static {
            CREATOR = new Creator<Action>() {
                public Action createFromParcel(Parcel in) {
                    return new Action(null);
                }

                public Action[] newArray(int size) {
                    return new Action[size];
                }
            };
        }
    }

    public static abstract class Style {
        private CharSequence mBigContentTitle;
        protected Builder mBuilder;
        protected CharSequence mSummaryText;
        protected boolean mSummaryTextSet;

        public Style() {
            this.mSummaryText = null;
            this.mSummaryTextSet = false;
        }

        protected void internalSetBigContentTitle(CharSequence title) {
            this.mBigContentTitle = title;
        }

        protected void internalSetSummaryText(CharSequence cs) {
            this.mSummaryText = cs;
            this.mSummaryTextSet = true;
        }

        public void setBuilder(Builder builder) {
            if (this.mBuilder != builder) {
                this.mBuilder = builder;
                if (this.mBuilder != null) {
                    this.mBuilder.setStyle(this);
                }
            }
        }

        protected void checkBuilder() {
            if (this.mBuilder == null) {
                throw new IllegalArgumentException("Style requires a valid Builder object");
            }
        }

        protected RemoteViews getStandardView(int layoutId) {
            checkBuilder();
            CharSequence oldBuilderContentTitle = this.mBuilder.mContentTitle;
            if (this.mBigContentTitle != null) {
                this.mBuilder.setContentTitle(this.mBigContentTitle);
            }
            RemoteViews contentView = this.mBuilder.applyStandardTemplateWithActions(layoutId);
            this.mBuilder.mContentTitle = oldBuilderContentTitle;
            if (this.mBigContentTitle == null || !this.mBigContentTitle.equals(ProxyInfo.LOCAL_EXCL_LIST)) {
                contentView.setViewVisibility(16909125, Notification.VISIBILITY_PRIVATE);
            } else {
                contentView.setViewVisibility(16909125, Notification.FLAG_ONLY_ALERT_ONCE);
            }
            CharSequence overflowText = this.mSummaryTextSet ? this.mSummaryText : this.mBuilder.mSubText;
            if (overflowText != null) {
                contentView.setTextViewText(16908392, this.mBuilder.processLegacyText(overflowText));
                contentView.setViewVisibility(16909114, Notification.VISIBILITY_PRIVATE);
                contentView.setViewVisibility(16909127, Notification.VISIBILITY_PRIVATE);
            } else {
                contentView.setTextViewText(16908392, ProxyInfo.LOCAL_EXCL_LIST);
                contentView.setViewVisibility(16909114, Notification.FLAG_ONLY_ALERT_ONCE);
                contentView.setViewVisibility(16909127, Notification.FLAG_ONLY_ALERT_ONCE);
            }
            return contentView;
        }

        protected void applyTopPadding(RemoteViews contentView) {
            contentView.setViewPadding(16909125, Notification.VISIBILITY_PRIVATE, Builder.calculateTopPadding(this.mBuilder.mContext, this.mBuilder.mHasThreeLines, this.mBuilder.mContext.getResources().getConfiguration().fontScale), Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE);
        }

        public void addExtras(Bundle extras) {
            if (this.mSummaryTextSet) {
                extras.putCharSequence(Notification.EXTRA_SUMMARY_TEXT, this.mSummaryText);
            }
            if (this.mBigContentTitle != null) {
                extras.putCharSequence(Notification.EXTRA_TITLE_BIG, this.mBigContentTitle);
            }
            extras.putString(Notification.EXTRA_TEMPLATE, getClass().getName());
        }

        protected void restoreFromExtras(Bundle extras) {
            if (extras.containsKey(Notification.EXTRA_SUMMARY_TEXT)) {
                this.mSummaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT);
                this.mSummaryTextSet = true;
            }
            if (extras.containsKey(Notification.EXTRA_TITLE_BIG)) {
                this.mBigContentTitle = extras.getCharSequence(Notification.EXTRA_TITLE_BIG);
            }
        }

        public Notification buildStyled(Notification wip) {
            populateTickerView(wip);
            populateContentView(wip);
            populateBigContentView(wip);
            populateHeadsUpContentView(wip);
            return wip;
        }

        protected void populateTickerView(Notification wip) {
        }

        protected void populateContentView(Notification wip) {
        }

        protected void populateBigContentView(Notification wip) {
        }

        protected void populateHeadsUpContentView(Notification wip) {
        }

        public Notification build() {
            checkBuilder();
            return this.mBuilder.build();
        }

        protected boolean hasProgress() {
            return true;
        }
    }

    public static class BigPictureStyle extends Style {
        private Bitmap mBigLargeIcon;
        private boolean mBigLargeIconSet;
        private Bitmap mPicture;

        public BigPictureStyle() {
            this.mBigLargeIconSet = false;
        }

        public BigPictureStyle(Builder builder) {
            this.mBigLargeIconSet = false;
            setBuilder(builder);
        }

        public BigPictureStyle setBigContentTitle(CharSequence title) {
            internalSetBigContentTitle(Notification.safeCharSequence(title));
            return this;
        }

        public BigPictureStyle setSummaryText(CharSequence cs) {
            internalSetSummaryText(Notification.safeCharSequence(cs));
            return this;
        }

        public BigPictureStyle bigPicture(Bitmap b) {
            this.mPicture = b;
            return this;
        }

        public BigPictureStyle bigLargeIcon(Bitmap b) {
            this.mBigLargeIconSet = true;
            this.mBigLargeIcon = b;
            return this;
        }

        private RemoteViews makeBigContentView() {
            Bitmap oldLargeIcon = null;
            if (this.mBigLargeIconSet) {
                oldLargeIcon = this.mBuilder.mLargeIcon;
                this.mBuilder.mLargeIcon = this.mBigLargeIcon;
            }
            RemoteViews contentView = getStandardView(this.mBuilder.getBigPictureLayoutResource());
            if (this.mBigLargeIconSet) {
                this.mBuilder.mLargeIcon = oldLargeIcon;
            }
            contentView.setImageViewBitmap(16909113, this.mPicture);
            applyTopPadding(contentView);
            boolean twoTextLines = (this.mBuilder.mSubText == null || this.mBuilder.mContentText == null) ? false : true;
            this.mBuilder.addProfileBadge(contentView, twoTextLines ? 16909126 : 16909129);
            return contentView;
        }

        public void addExtras(Bundle extras) {
            super.addExtras(extras);
            if (this.mBigLargeIconSet) {
                extras.putParcelable(Notification.EXTRA_LARGE_ICON_BIG, this.mBigLargeIcon);
            }
            extras.putParcelable(Notification.EXTRA_PICTURE, this.mPicture);
        }

        protected void restoreFromExtras(Bundle extras) {
            super.restoreFromExtras(extras);
            if (extras.containsKey(Notification.EXTRA_LARGE_ICON_BIG)) {
                this.mBigLargeIconSet = true;
                this.mBigLargeIcon = (Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON_BIG);
            }
            this.mPicture = (Bitmap) extras.getParcelable(Notification.EXTRA_PICTURE);
        }

        public void populateBigContentView(Notification wip) {
            this.mBuilder.setBuilderBigContentView(wip, makeBigContentView());
        }
    }

    public static class BigTextStyle extends Style {
        private static final int LINES_CONSUMED_BY_ACTIONS = 3;
        private static final int LINES_CONSUMED_BY_SUMMARY = 2;
        private static final int MAX_LINES = 13;
        private CharSequence mBigText;

        public BigTextStyle(Builder builder) {
            setBuilder(builder);
        }

        public BigTextStyle setBigContentTitle(CharSequence title) {
            internalSetBigContentTitle(Notification.safeCharSequence(title));
            return this;
        }

        public BigTextStyle setSummaryText(CharSequence cs) {
            internalSetSummaryText(Notification.safeCharSequence(cs));
            return this;
        }

        public BigTextStyle bigText(CharSequence cs) {
            this.mBigText = Notification.safeCharSequence(cs);
            return this;
        }

        public void addExtras(Bundle extras) {
            super.addExtras(extras);
            extras.putCharSequence(Notification.EXTRA_BIG_TEXT, this.mBigText);
        }

        protected void restoreFromExtras(Bundle extras) {
            super.restoreFromExtras(extras);
            this.mBigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);
        }

        private RemoteViews makeBigContentView() {
            CharSequence oldBuilderContentText = this.mBuilder.mContentText;
            this.mBuilder.mContentText = null;
            RemoteViews contentView = getStandardView(this.mBuilder.getBigTextLayoutResource());
            this.mBuilder.mContentText = oldBuilderContentText;
            contentView.setTextViewText(16909109, this.mBuilder.processLegacyText(this.mBigText));
            contentView.setViewVisibility(16909109, Notification.VISIBILITY_PRIVATE);
            contentView.setInt(16909109, "setMaxLines", calculateMaxLines());
            contentView.setViewVisibility(R.id.text2, Notification.FLAG_ONLY_ALERT_ONCE);
            applyTopPadding(contentView);
            this.mBuilder.shrinkLine3Text(contentView);
            this.mBuilder.addProfileBadge(contentView, 16909110);
            return contentView;
        }

        private int calculateMaxLines() {
            boolean hasActions;
            int lineCount = MAX_LINES;
            if (this.mBuilder.mActions.size() > 0) {
                hasActions = true;
            } else {
                hasActions = false;
            }
            boolean hasSummary = (this.mSummaryTextSet ? this.mSummaryText : this.mBuilder.mSubText) != null;
            if (hasActions) {
                lineCount = MAX_LINES - 3;
            }
            if (hasSummary) {
                lineCount += Notification.PRIORITY_MIN;
            }
            if (this.mBuilder.mHasThreeLines) {
                return lineCount;
            }
            return lineCount + Notification.VISIBILITY_SECRET;
        }

        public void populateBigContentView(Notification wip) {
            this.mBuilder.setBuilderBigContentView(wip, makeBigContentView());
        }
    }

    public static class Builder {
        public static final String EXTRA_NEEDS_REBUILD = "android.rebuild";
        public static final String EXTRA_REBUILD_BIG_CONTENT_VIEW = "android.rebuild.bigView";
        public static final String EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.bigViewActionCount";
        public static final String EXTRA_REBUILD_CONTENT_VIEW = "android.rebuild.contentView";
        public static final String EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.contentViewActionCount";
        private static final String EXTRA_REBUILD_CONTEXT_APPLICATION_INFO = "android.rebuild.applicationInfo";
        public static final String EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW = "android.rebuild.hudView";
        public static final String EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT = "android.rebuild.hudViewActionCount";
        public static final String EXTRA_REBUILD_LARGE_ICON = "android.rebuild.largeIcon";
        private static final float LARGE_TEXT_SCALE = 1.3f;
        private static final int MAX_ACTION_BUTTONS = 3;
        private static final boolean STRIP_AND_REBUILD = true;
        private ArrayList<Action> mActions;
        private AudioAttributes mAudioAttributes;
        private int mAudioStreamType;
        private String mCategory;
        private int mColor;
        private final NotificationColorUtil mColorUtil;
        private CharSequence mContentInfo;
        private PendingIntent mContentIntent;
        private CharSequence mContentText;
        private CharSequence mContentTitle;
        private RemoteViews mContentView;
        private Context mContext;
        private int mDefaults;
        private PendingIntent mDeleteIntent;
        private Bundle mExtras;
        private int mFlags;
        private PendingIntent mFullScreenIntent;
        private String mGroupKey;
        private boolean mHasThreeLines;
        private Bitmap mLargeIcon;
        private int mLedArgb;
        private int mLedOffMs;
        private int mLedOnMs;
        private int mNumber;
        private int mOriginatingUserId;
        private ArrayList<String> mPeople;
        private int mPriority;
        private int mProgress;
        private boolean mProgressIndeterminate;
        private int mProgressMax;
        private Notification mPublicVersion;
        private Bundle mRebuildBundle;
        private Notification mRebuildNotification;
        private boolean mShowWhen;
        private int mSmallIcon;
        private int mSmallIconLevel;
        private String mSortKey;
        private Uri mSound;
        private Style mStyle;
        private CharSequence mSubText;
        private CharSequence mTickerText;
        private RemoteViews mTickerView;
        private boolean mUseChronometer;
        private long[] mVibrate;
        private int mVisibility;
        private long mWhen;

        public Builder(Context context) {
            NotificationColorUtil notificationColorUtil = null;
            this.mActions = new ArrayList(MAX_ACTION_BUTTONS);
            this.mShowWhen = STRIP_AND_REBUILD;
            this.mVisibility = Notification.VISIBILITY_PRIVATE;
            this.mPublicVersion = null;
            this.mColor = Notification.VISIBILITY_PRIVATE;
            this.mRebuildBundle = new Bundle();
            this.mRebuildNotification = null;
            this.mContext = context;
            this.mWhen = System.currentTimeMillis();
            this.mAudioStreamType = Notification.VISIBILITY_SECRET;
            this.mAudioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
            this.mPriority = Notification.VISIBILITY_PRIVATE;
            this.mPeople = new ArrayList();
            if (context.getApplicationInfo().targetSdkVersion < 21) {
                notificationColorUtil = NotificationColorUtil.getInstance(this.mContext);
            }
            this.mColorUtil = notificationColorUtil;
        }

        private Builder(Context context, Notification n) {
            this(context);
            this.mRebuildNotification = n;
            restoreFromNotification(n);
            Style style = null;
            Bundle extras = n.extras;
            String templateClass = extras.getString(Notification.EXTRA_TEMPLATE);
            if (!TextUtils.isEmpty(templateClass)) {
                Class<? extends Style> styleClass = getNotificationStyleClass(templateClass);
                if (styleClass == null) {
                    Log.d(Notification.TAG, "Unknown style class: " + styleClass);
                    return;
                }
                try {
                    style = (Style) styleClass.getConstructor(new Class[Notification.VISIBILITY_PRIVATE]).newInstance(new Object[Notification.VISIBILITY_PRIVATE]);
                    style.restoreFromExtras(extras);
                } catch (Throwable t) {
                    Log.e(Notification.TAG, "Could not create Style", t);
                    return;
                }
            }
            if (style != null) {
                setStyle(style);
            }
        }

        public Builder setWhen(long when) {
            this.mWhen = when;
            return this;
        }

        public Builder setShowWhen(boolean show) {
            this.mShowWhen = show;
            return this;
        }

        public Builder setUsesChronometer(boolean b) {
            this.mUseChronometer = b;
            return this;
        }

        public Builder setSmallIcon(int icon) {
            this.mSmallIcon = icon;
            return this;
        }

        public Builder setSmallIcon(int icon, int level) {
            this.mSmallIcon = icon;
            this.mSmallIconLevel = level;
            return this;
        }

        public Builder setContentTitle(CharSequence title) {
            this.mContentTitle = Notification.safeCharSequence(title);
            return this;
        }

        public Builder setContentText(CharSequence text) {
            this.mContentText = Notification.safeCharSequence(text);
            return this;
        }

        public Builder setSubText(CharSequence text) {
            this.mSubText = Notification.safeCharSequence(text);
            return this;
        }

        public Builder setNumber(int number) {
            this.mNumber = number;
            return this;
        }

        public Builder setContentInfo(CharSequence info) {
            this.mContentInfo = Notification.safeCharSequence(info);
            return this;
        }

        public Builder setProgress(int max, int progress, boolean indeterminate) {
            this.mProgressMax = max;
            this.mProgress = progress;
            this.mProgressIndeterminate = indeterminate;
            return this;
        }

        public Builder setContent(RemoteViews views) {
            this.mContentView = views;
            return this;
        }

        public Builder setContentIntent(PendingIntent intent) {
            this.mContentIntent = intent;
            return this;
        }

        public Builder setDeleteIntent(PendingIntent intent) {
            this.mDeleteIntent = intent;
            return this;
        }

        public Builder setFullScreenIntent(PendingIntent intent, boolean highPriority) {
            this.mFullScreenIntent = intent;
            setFlag(Notification.FLAG_HIGH_PRIORITY, highPriority);
            return this;
        }

        public Builder setTicker(CharSequence tickerText) {
            this.mTickerText = Notification.safeCharSequence(tickerText);
            return this;
        }

        @Deprecated
        public Builder setTicker(CharSequence tickerText, RemoteViews views) {
            this.mTickerText = Notification.safeCharSequence(tickerText);
            this.mTickerView = views;
            return this;
        }

        public Builder setLargeIcon(Bitmap icon) {
            this.mLargeIcon = icon;
            return this;
        }

        public Builder setSound(Uri sound) {
            this.mSound = sound;
            this.mAudioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
            return this;
        }

        @Deprecated
        public Builder setSound(Uri sound, int streamType) {
            this.mSound = sound;
            this.mAudioStreamType = streamType;
            return this;
        }

        public Builder setSound(Uri sound, AudioAttributes audioAttributes) {
            this.mSound = sound;
            this.mAudioAttributes = audioAttributes;
            return this;
        }

        public Builder setVibrate(long[] pattern) {
            this.mVibrate = pattern;
            return this;
        }

        public Builder setLights(int argb, int onMs, int offMs) {
            this.mLedArgb = argb;
            this.mLedOnMs = onMs;
            this.mLedOffMs = offMs;
            return this;
        }

        public Builder setOngoing(boolean ongoing) {
            setFlag(Notification.PRIORITY_MAX, ongoing);
            return this;
        }

        public Builder setOnlyAlertOnce(boolean onlyAlertOnce) {
            setFlag(Notification.FLAG_ONLY_ALERT_ONCE, onlyAlertOnce);
            return this;
        }

        public Builder setAutoCancel(boolean autoCancel) {
            setFlag(Notification.FLAG_AUTO_CANCEL, autoCancel);
            return this;
        }

        public Builder setLocalOnly(boolean localOnly) {
            setFlag(Notification.FLAG_LOCAL_ONLY, localOnly);
            return this;
        }

        public Builder setDefaults(int defaults) {
            this.mDefaults = defaults;
            return this;
        }

        public Builder setPriority(int pri) {
            this.mPriority = pri;
            return this;
        }

        public Builder setCategory(String category) {
            this.mCategory = category;
            return this;
        }

        public Builder addPerson(String uri) {
            this.mPeople.add(uri);
            return this;
        }

        public Builder setGroup(String groupKey) {
            this.mGroupKey = groupKey;
            return this;
        }

        public Builder setGroupSummary(boolean isGroupSummary) {
            setFlag(Notification.FLAG_GROUP_SUMMARY, isGroupSummary);
            return this;
        }

        public Builder setSortKey(String sortKey) {
            this.mSortKey = sortKey;
            return this;
        }

        public Builder addExtras(Bundle extras) {
            if (extras != null) {
                if (this.mExtras == null) {
                    this.mExtras = new Bundle(extras);
                } else {
                    this.mExtras.putAll(extras);
                }
            }
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Bundle getExtras() {
            if (this.mExtras == null) {
                this.mExtras = new Bundle();
            }
            return this.mExtras;
        }

        public Builder addAction(int icon, CharSequence title, PendingIntent intent) {
            this.mActions.add(new Action(icon, Notification.safeCharSequence(title), intent));
            return this;
        }

        public Builder addAction(Action action) {
            this.mActions.add(action);
            return this;
        }

        public Builder setStyle(Style style) {
            if (this.mStyle != style) {
                this.mStyle = style;
                if (this.mStyle != null) {
                    this.mStyle.setBuilder(this);
                }
            }
            return this;
        }

        public Builder setVisibility(int visibility) {
            this.mVisibility = visibility;
            return this;
        }

        public Builder setPublicVersion(Notification n) {
            this.mPublicVersion = n;
            return this;
        }

        public Builder extend(Extender extender) {
            extender.extend(this);
            return this;
        }

        private void setFlag(int mask, boolean value) {
            if (value) {
                this.mFlags |= mask;
            } else {
                this.mFlags &= mask ^ Notification.VISIBILITY_SECRET;
            }
        }

        public Builder setColor(int argb) {
            this.mColor = argb;
            return this;
        }

        private Drawable getProfileBadgeDrawable() {
            return this.mContext.getPackageManager().getUserBadgeForDensity(new UserHandle(this.mOriginatingUserId), Notification.VISIBILITY_PRIVATE);
        }

        private Bitmap getProfileBadge() {
            Drawable badge = getProfileBadgeDrawable();
            if (badge == null) {
                return null;
            }
            int size = this.mContext.getResources().getDimensionPixelSize(17104989);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            badge.setBounds(Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE, size, size);
            badge.draw(canvas);
            return bitmap;
        }

        private boolean addProfileBadge(RemoteViews contentView, int resId) {
            Bitmap profileBadge = getProfileBadge();
            contentView.setViewVisibility(16909110, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909126, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909129, Notification.FLAG_ONLY_ALERT_ONCE);
            if (profileBadge == null) {
                return false;
            }
            contentView.setImageViewBitmap(resId, profileBadge);
            contentView.setViewVisibility(resId, Notification.VISIBILITY_PRIVATE);
            if (resId == 16909129) {
                contentView.setViewVisibility(16909127, Notification.VISIBILITY_PRIVATE);
            }
            return STRIP_AND_REBUILD;
        }

        private void shrinkLine3Text(RemoteViews contentView) {
            contentView.setTextViewTextSize(16908392, Notification.VISIBILITY_PRIVATE, (float) this.mContext.getResources().getDimensionPixelSize(17104983));
        }

        private void unshrinkLine3Text(RemoteViews contentView) {
            contentView.setTextViewTextSize(16908392, Notification.VISIBILITY_PRIVATE, (float) this.mContext.getResources().getDimensionPixelSize(17104981));
        }

        private void resetStandardTemplate(RemoteViews contentView) {
            removeLargeIconBackground(contentView);
            contentView.setViewPadding(R.id.icon, Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE);
            contentView.setImageViewResource(R.id.icon, Notification.VISIBILITY_PRIVATE);
            contentView.setInt(R.id.icon, "setBackgroundResource", Notification.VISIBILITY_PRIVATE);
            contentView.setViewVisibility(16908340, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setInt(16908340, "setBackgroundResource", Notification.VISIBILITY_PRIVATE);
            contentView.setImageViewResource(16908340, Notification.VISIBILITY_PRIVATE);
            contentView.setImageViewResource(R.id.icon, Notification.VISIBILITY_PRIVATE);
            contentView.setTextViewText(R.id.title, null);
            contentView.setTextViewText(16908392, null);
            unshrinkLine3Text(contentView);
            contentView.setTextViewText(R.id.text2, null);
            contentView.setViewVisibility(R.id.text2, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909128, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16908415, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909127, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909114, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(R.id.progress, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909124, Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16908415, Notification.FLAG_ONLY_ALERT_ONCE);
        }

        private RemoteViews applyStandardTemplate(int resId) {
            return applyStandardTemplate(resId, STRIP_AND_REBUILD);
        }

        private RemoteViews applyStandardTemplate(int resId, boolean hasProgress) {
            RemoteViews contentView = new BuilderRemoteViews(this.mContext.getApplicationInfo(), resId);
            resetStandardTemplate(contentView);
            boolean showLine3 = false;
            boolean showLine2 = false;
            boolean contentTextInLine2 = false;
            if (this.mLargeIcon != null) {
                contentView.setImageViewBitmap(R.id.icon, this.mLargeIcon);
                processLargeLegacyIcon(this.mLargeIcon, contentView);
                contentView.setImageViewResource(16908340, this.mSmallIcon);
                contentView.setViewVisibility(16908340, Notification.VISIBILITY_PRIVATE);
                processSmallRightIcon(this.mSmallIcon, contentView);
            } else {
                contentView.setImageViewResource(R.id.icon, this.mSmallIcon);
                contentView.setViewVisibility(R.id.icon, Notification.VISIBILITY_PRIVATE);
                processSmallIconAsLarge(this.mSmallIcon, contentView);
            }
            if (this.mContentTitle != null) {
                contentView.setTextViewText(R.id.title, processLegacyText(this.mContentTitle));
            }
            if (this.mContentText != null) {
                contentView.setTextViewText(16908392, processLegacyText(this.mContentText));
                showLine3 = STRIP_AND_REBUILD;
            }
            if (this.mContentInfo != null) {
                contentView.setTextViewText(16909128, processLegacyText(this.mContentInfo));
                contentView.setViewVisibility(16909128, Notification.VISIBILITY_PRIVATE);
                showLine3 = STRIP_AND_REBUILD;
            } else if (this.mNumber > 0) {
                if (this.mNumber > this.mContext.getResources().getInteger(R.integer.status_bar_notification_info_maxnum)) {
                    contentView.setTextViewText(16909128, processLegacyText(this.mContext.getResources().getString(R.string.status_bar_notification_info_overflow)));
                } else {
                    contentView.setTextViewText(16909128, processLegacyText(NumberFormat.getIntegerInstance().format((long) this.mNumber)));
                }
                contentView.setViewVisibility(16909128, Notification.VISIBILITY_PRIVATE);
                showLine3 = STRIP_AND_REBUILD;
            } else {
                contentView.setViewVisibility(16909128, Notification.FLAG_ONLY_ALERT_ONCE);
            }
            if (this.mSubText != null) {
                contentView.setTextViewText(16908392, processLegacyText(this.mSubText));
                if (this.mContentText != null) {
                    contentView.setTextViewText(R.id.text2, processLegacyText(this.mContentText));
                    contentView.setViewVisibility(R.id.text2, Notification.VISIBILITY_PRIVATE);
                    showLine2 = STRIP_AND_REBUILD;
                    contentTextInLine2 = STRIP_AND_REBUILD;
                } else {
                    contentView.setViewVisibility(R.id.text2, Notification.FLAG_ONLY_ALERT_ONCE);
                }
            } else {
                contentView.setViewVisibility(R.id.text2, Notification.FLAG_ONLY_ALERT_ONCE);
                if (!hasProgress || (this.mProgressMax == 0 && !this.mProgressIndeterminate)) {
                    contentView.setViewVisibility(R.id.progress, Notification.FLAG_ONLY_ALERT_ONCE);
                } else {
                    contentView.setViewVisibility(R.id.progress, Notification.VISIBILITY_PRIVATE);
                    contentView.setProgressBar(R.id.progress, this.mProgressMax, this.mProgress, this.mProgressIndeterminate);
                    contentView.setProgressBackgroundTintList(R.id.progress, ColorStateList.valueOf(this.mContext.getResources().getColor(17170515)));
                    if (this.mColor != 0) {
                        ColorStateList colorStateList = ColorStateList.valueOf(this.mColor);
                        contentView.setProgressTintList(R.id.progress, colorStateList);
                        contentView.setProgressIndeterminateTintList(R.id.progress, colorStateList);
                    }
                    showLine2 = STRIP_AND_REBUILD;
                }
            }
            if (showLine2) {
                shrinkLine3Text(contentView);
            }
            if (showsTimeOrChronometer()) {
                if (this.mUseChronometer) {
                    contentView.setViewVisibility(16909124, Notification.VISIBILITY_PRIVATE);
                    contentView.setLong(16909124, "setBase", this.mWhen + (SystemClock.elapsedRealtime() - System.currentTimeMillis()));
                    contentView.setBoolean(16909124, "setStarted", STRIP_AND_REBUILD);
                } else {
                    contentView.setViewVisibility(16908415, Notification.VISIBILITY_PRIVATE);
                    contentView.setLong(16908415, "setTime", this.mWhen);
                }
            }
            contentView.setViewPadding(16909125, Notification.VISIBILITY_PRIVATE, calculateTopPadding(this.mContext, this.mHasThreeLines, this.mContext.getResources().getConfiguration().fontScale), Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE);
            if (addProfileBadge(contentView, contentTextInLine2 ? 16909126 : 16909129) && !contentTextInLine2) {
                showLine3 = STRIP_AND_REBUILD;
            }
            contentView.setViewVisibility(16909127, showLine3 ? Notification.VISIBILITY_PRIVATE : Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909114, showLine3 ? Notification.VISIBILITY_PRIVATE : Notification.FLAG_ONLY_ALERT_ONCE);
            return contentView;
        }

        private boolean showsTimeOrChronometer() {
            return (this.mWhen == 0 || !this.mShowWhen) ? false : STRIP_AND_REBUILD;
        }

        private boolean hasThreeLines() {
            boolean hasLine2;
            boolean contentTextInLine2;
            if (this.mSubText == null || this.mContentText == null) {
                contentTextInLine2 = false;
            } else {
                contentTextInLine2 = STRIP_AND_REBUILD;
            }
            boolean hasProgress;
            if (this.mStyle == null || this.mStyle.hasProgress()) {
                hasProgress = STRIP_AND_REBUILD;
            } else {
                hasProgress = false;
            }
            boolean badgeInLine3;
            if (getProfileBadgeDrawable() == null || contentTextInLine2) {
                badgeInLine3 = false;
            } else {
                badgeInLine3 = STRIP_AND_REBUILD;
            }
            boolean hasLine3;
            if (this.mContentText != null || this.mContentInfo != null || this.mNumber > 0 || badgeInLine3) {
                hasLine3 = STRIP_AND_REBUILD;
            } else {
                hasLine3 = false;
            }
            if ((this.mSubText == null || this.mContentText == null) && !(hasProgress && this.mSubText == null && (this.mProgressMax != 0 || this.mProgressIndeterminate))) {
                hasLine2 = false;
            } else {
                hasLine2 = STRIP_AND_REBUILD;
            }
            if (hasLine2 && hasLine3) {
                return STRIP_AND_REBUILD;
            }
            return false;
        }

        public static int calculateTopPadding(Context ctx, boolean hasThreeLines, float fontScale) {
            float largeFactor = (MathUtils.constrain(fontScale, LayoutParams.BRIGHTNESS_OVERRIDE_FULL, LARGE_TEXT_SCALE) - LayoutParams.BRIGHTNESS_OVERRIDE_FULL) / 0.29999995f;
            return Math.round(((LayoutParams.BRIGHTNESS_OVERRIDE_FULL - largeFactor) * ((float) ctx.getResources().getDimensionPixelSize(hasThreeLines ? 17104985 : 17104984))) + (((float) ctx.getResources().getDimensionPixelSize(hasThreeLines ? 17104987 : 17104986)) * largeFactor));
        }

        private void resetStandardTemplateWithActions(RemoteViews big) {
            big.setViewVisibility(16909102, Notification.FLAG_ONLY_ALERT_ONCE);
            big.setViewVisibility(16909111, Notification.FLAG_ONLY_ALERT_ONCE);
            big.removeAllViews(16909102);
        }

        private RemoteViews applyStandardTemplateWithActions(int layoutId) {
            RemoteViews big = applyStandardTemplate(layoutId);
            resetStandardTemplateWithActions(big);
            int N = this.mActions.size();
            if (N > 0) {
                big.setViewVisibility(16909102, Notification.VISIBILITY_PRIVATE);
                big.setViewVisibility(16909111, Notification.VISIBILITY_PRIVATE);
                if (N > MAX_ACTION_BUTTONS) {
                    N = MAX_ACTION_BUTTONS;
                }
                for (int i = Notification.VISIBILITY_PRIVATE; i < N; i += Notification.VISIBILITY_PUBLIC) {
                    big.addView(16909102, generateActionButton((Action) this.mActions.get(i)));
                }
            }
            return big;
        }

        private RemoteViews makeContentView() {
            if (this.mContentView != null) {
                return this.mContentView;
            }
            return applyStandardTemplate(getBaseLayoutResource());
        }

        private RemoteViews makeTickerView() {
            if (this.mTickerView != null) {
                return this.mTickerView;
            }
            return null;
        }

        private RemoteViews makeBigContentView() {
            if (this.mActions.size() == 0) {
                return null;
            }
            return applyStandardTemplateWithActions(getBigBaseLayoutResource());
        }

        private RemoteViews makeHeadsUpContentView() {
            if (this.mActions.size() == 0) {
                return null;
            }
            return applyStandardTemplateWithActions(getBigBaseLayoutResource());
        }

        private RemoteViews generateActionButton(Action action) {
            boolean tombstone;
            if (action.actionIntent == null) {
                tombstone = STRIP_AND_REBUILD;
            } else {
                tombstone = false;
            }
            RemoteViews button = new RemoteViews(this.mContext.getPackageName(), tombstone ? getActionTombstoneLayoutResource() : getActionLayoutResource());
            button.setTextViewCompoundDrawablesRelative(16909103, action.icon, Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE, Notification.VISIBILITY_PRIVATE);
            button.setTextViewText(16909103, processLegacyText(action.title));
            if (!tombstone) {
                button.setOnClickPendingIntent(16909103, action.actionIntent);
            }
            button.setContentDescription(16909103, action.title);
            processLegacyAction(action, button);
            return button;
        }

        private boolean isLegacy() {
            return this.mColorUtil != null ? STRIP_AND_REBUILD : false;
        }

        private void processLegacyAction(Action action, RemoteViews button) {
            if (!isLegacy() || this.mColorUtil.isGrayscaleIcon(this.mContext, action.icon)) {
                button.setTextViewCompoundDrawablesRelativeColorFilter(16909103, Notification.VISIBILITY_PRIVATE, this.mContext.getResources().getColor(17170512), Mode.MULTIPLY);
            }
        }

        private CharSequence processLegacyText(CharSequence charSequence) {
            if (isLegacy()) {
                return this.mColorUtil.invertCharSequenceColors(charSequence);
            }
            return charSequence;
        }

        private void processSmallIconAsLarge(int largeIconId, RemoteViews contentView) {
            if (!isLegacy()) {
                contentView.setDrawableParameters(R.id.icon, false, Notification.VISIBILITY_SECRET, Notification.VISIBILITY_SECRET, Mode.SRC_ATOP, Notification.VISIBILITY_SECRET);
            }
            if (!isLegacy() || this.mColorUtil.isGrayscaleIcon(this.mContext, largeIconId)) {
                applyLargeIconBackground(contentView);
            }
        }

        private void processLargeLegacyIcon(Bitmap largeIcon, RemoteViews contentView) {
            if (isLegacy() && this.mColorUtil.isGrayscaleIcon(largeIcon)) {
                applyLargeIconBackground(contentView);
            } else {
                removeLargeIconBackground(contentView);
            }
        }

        private void applyLargeIconBackground(RemoteViews contentView) {
            contentView.setInt(R.id.icon, "setBackgroundResource", 17302762);
            contentView.setDrawableParameters(R.id.icon, STRIP_AND_REBUILD, Notification.VISIBILITY_SECRET, resolveColor(), Mode.SRC_ATOP, Notification.VISIBILITY_SECRET);
            int padding = this.mContext.getResources().getDimensionPixelSize(17104988);
            contentView.setViewPadding(R.id.icon, padding, padding, padding, padding);
        }

        private void removeLargeIconBackground(RemoteViews contentView) {
            contentView.setInt(R.id.icon, "setBackgroundResource", Notification.VISIBILITY_PRIVATE);
        }

        private void processSmallRightIcon(int smallIconDrawableId, RemoteViews contentView) {
            if (!isLegacy()) {
                contentView.setDrawableParameters(16908340, false, Notification.VISIBILITY_SECRET, Notification.VISIBILITY_SECRET, Mode.SRC_ATOP, Notification.VISIBILITY_SECRET);
            }
            if (!isLegacy() || this.mColorUtil.isGrayscaleIcon(this.mContext, smallIconDrawableId)) {
                contentView.setInt(16908340, "setBackgroundResource", 17302762);
                contentView.setDrawableParameters(16908340, STRIP_AND_REBUILD, Notification.VISIBILITY_SECRET, resolveColor(), Mode.SRC_ATOP, Notification.VISIBILITY_SECRET);
            }
        }

        private int sanitizeColor() {
            if (this.mColor != 0) {
                this.mColor |= Color.BLACK;
            }
            return this.mColor;
        }

        private int resolveColor() {
            if (this.mColor == 0) {
                return this.mContext.getResources().getColor(17170511);
            }
            return this.mColor;
        }

        public Notification buildUnstyled() {
            Notification n = new Notification();
            n.when = this.mWhen;
            n.icon = this.mSmallIcon;
            n.iconLevel = this.mSmallIconLevel;
            n.number = this.mNumber;
            n.color = sanitizeColor();
            setBuilderContentView(n, makeContentView());
            n.contentIntent = this.mContentIntent;
            n.deleteIntent = this.mDeleteIntent;
            n.fullScreenIntent = this.mFullScreenIntent;
            n.tickerText = this.mTickerText;
            n.tickerView = makeTickerView();
            n.largeIcon = this.mLargeIcon;
            n.sound = this.mSound;
            n.audioStreamType = this.mAudioStreamType;
            n.audioAttributes = this.mAudioAttributes;
            n.vibrate = this.mVibrate;
            n.ledARGB = this.mLedArgb;
            n.ledOnMS = this.mLedOnMs;
            n.ledOffMS = this.mLedOffMs;
            n.defaults = this.mDefaults;
            n.flags = this.mFlags;
            setBuilderBigContentView(n, makeBigContentView());
            setBuilderHeadsUpContentView(n, makeHeadsUpContentView());
            if (!(this.mLedOnMs == 0 && this.mLedOffMs == 0)) {
                n.flags |= Notification.VISIBILITY_PUBLIC;
            }
            if ((this.mDefaults & Notification.FLAG_INSISTENT) != 0) {
                n.flags |= Notification.VISIBILITY_PUBLIC;
            }
            n.category = this.mCategory;
            n.mGroupKey = this.mGroupKey;
            n.mSortKey = this.mSortKey;
            n.priority = this.mPriority;
            if (this.mActions.size() > 0) {
                n.actions = new Action[this.mActions.size()];
                this.mActions.toArray(n.actions);
            }
            n.visibility = this.mVisibility;
            if (this.mPublicVersion != null) {
                n.publicVersion = new Notification();
                this.mPublicVersion.cloneInto(n.publicVersion, STRIP_AND_REBUILD);
            }
            return n;
        }

        public void populateExtras(Bundle extras) {
            extras.putInt(Notification.EXTRA_ORIGINATING_USERID, this.mOriginatingUserId);
            extras.putParcelable(EXTRA_REBUILD_CONTEXT_APPLICATION_INFO, this.mContext.getApplicationInfo());
            extras.putCharSequence(Notification.EXTRA_TITLE, this.mContentTitle);
            extras.putCharSequence(Notification.EXTRA_TEXT, this.mContentText);
            extras.putCharSequence(Notification.EXTRA_SUB_TEXT, this.mSubText);
            extras.putCharSequence(Notification.EXTRA_INFO_TEXT, this.mContentInfo);
            extras.putInt(Notification.EXTRA_SMALL_ICON, this.mSmallIcon);
            extras.putInt(Notification.EXTRA_PROGRESS, this.mProgress);
            extras.putInt(Notification.EXTRA_PROGRESS_MAX, this.mProgressMax);
            extras.putBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, this.mProgressIndeterminate);
            extras.putBoolean(Notification.EXTRA_SHOW_CHRONOMETER, this.mUseChronometer);
            extras.putBoolean(Notification.EXTRA_SHOW_WHEN, this.mShowWhen);
            if (this.mLargeIcon != null) {
                extras.putParcelable(Notification.EXTRA_LARGE_ICON, this.mLargeIcon);
            }
            if (!this.mPeople.isEmpty()) {
                extras.putStringArray(Notification.EXTRA_PEOPLE, (String[]) this.mPeople.toArray(new String[this.mPeople.size()]));
            }
        }

        public static void stripForDelivery(Notification n) {
            String templateClass = n.extras.getString(Notification.EXTRA_TEMPLATE);
            boolean stripViews = (TextUtils.isEmpty(templateClass) || getNotificationStyleClass(templateClass) != null) ? STRIP_AND_REBUILD : false;
            boolean isStripped = false;
            if (n.largeIcon != null && n.extras.containsKey(Notification.EXTRA_LARGE_ICON)) {
                n.largeIcon = null;
                n.extras.putBoolean(EXTRA_REBUILD_LARGE_ICON, STRIP_AND_REBUILD);
                isStripped = STRIP_AND_REBUILD;
            }
            if (stripViews && (n.contentView instanceof BuilderRemoteViews) && n.extras.getInt(EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT, Notification.VISIBILITY_SECRET) == n.contentView.getSequenceNumber()) {
                n.contentView = null;
                n.extras.putBoolean(EXTRA_REBUILD_CONTENT_VIEW, STRIP_AND_REBUILD);
                n.extras.remove(EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT);
                isStripped = STRIP_AND_REBUILD;
            }
            if (stripViews && (n.bigContentView instanceof BuilderRemoteViews) && n.extras.getInt(EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT, Notification.VISIBILITY_SECRET) == n.bigContentView.getSequenceNumber()) {
                n.bigContentView = null;
                n.extras.putBoolean(EXTRA_REBUILD_BIG_CONTENT_VIEW, STRIP_AND_REBUILD);
                n.extras.remove(EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT);
                isStripped = STRIP_AND_REBUILD;
            }
            if (stripViews && (n.headsUpContentView instanceof BuilderRemoteViews) && n.extras.getInt(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT, Notification.VISIBILITY_SECRET) == n.headsUpContentView.getSequenceNumber()) {
                n.headsUpContentView = null;
                n.extras.putBoolean(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW, STRIP_AND_REBUILD);
                n.extras.remove(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT);
                isStripped = STRIP_AND_REBUILD;
            }
            if (isStripped) {
                n.extras.putBoolean(EXTRA_NEEDS_REBUILD, STRIP_AND_REBUILD);
            }
        }

        public static Notification rebuild(Context context, Notification n) {
            Bundle extras = n.extras;
            if (!extras.getBoolean(EXTRA_NEEDS_REBUILD)) {
                return n;
            }
            Context builderContext;
            extras.remove(EXTRA_NEEDS_REBUILD);
            ApplicationInfo applicationInfo = (ApplicationInfo) extras.getParcelable(EXTRA_REBUILD_CONTEXT_APPLICATION_INFO);
            try {
                builderContext = context.createApplicationContext(applicationInfo, Notification.FLAG_INSISTENT);
            } catch (NameNotFoundException e) {
                Log.e(Notification.TAG, "ApplicationInfo " + applicationInfo + " not found");
                builderContext = context;
            }
            return new Builder(builderContext, n).rebuild();
        }

        private Notification rebuild() {
            if (this.mRebuildNotification == null) {
                throw new IllegalStateException("rebuild() only valid when in 'rebuild' mode.");
            }
            this.mHasThreeLines = hasThreeLines();
            Bundle extras = this.mRebuildNotification.extras;
            if (extras.getBoolean(EXTRA_REBUILD_LARGE_ICON)) {
                this.mRebuildNotification.largeIcon = (Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON);
            }
            extras.remove(EXTRA_REBUILD_LARGE_ICON);
            if (extras.getBoolean(EXTRA_REBUILD_CONTENT_VIEW)) {
                setBuilderContentView(this.mRebuildNotification, makeContentView());
                if (this.mStyle != null) {
                    this.mStyle.populateContentView(this.mRebuildNotification);
                }
            }
            extras.remove(EXTRA_REBUILD_CONTENT_VIEW);
            if (extras.getBoolean(EXTRA_REBUILD_BIG_CONTENT_VIEW)) {
                setBuilderBigContentView(this.mRebuildNotification, makeBigContentView());
                if (this.mStyle != null) {
                    this.mStyle.populateBigContentView(this.mRebuildNotification);
                }
            }
            extras.remove(EXTRA_REBUILD_BIG_CONTENT_VIEW);
            if (extras.getBoolean(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW)) {
                setBuilderHeadsUpContentView(this.mRebuildNotification, makeHeadsUpContentView());
                if (this.mStyle != null) {
                    this.mStyle.populateHeadsUpContentView(this.mRebuildNotification);
                }
            }
            extras.remove(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW);
            this.mHasThreeLines = false;
            return this.mRebuildNotification;
        }

        private static Class<? extends Style> getNotificationStyleClass(String templateClass) {
            Class<? extends Style>[] classes = new Class[Notification.FLAG_INSISTENT];
            classes[Notification.VISIBILITY_PRIVATE] = BigTextStyle.class;
            classes[Notification.VISIBILITY_PUBLIC] = BigPictureStyle.class;
            classes[Notification.PRIORITY_MAX] = InboxStyle.class;
            classes[MAX_ACTION_BUTTONS] = MediaStyle.class;
            Class<? extends Style>[] arr$ = classes;
            int len$ = arr$.length;
            for (int i$ = Notification.VISIBILITY_PRIVATE; i$ < len$; i$ += Notification.VISIBILITY_PUBLIC) {
                Class<? extends Style> innerClass = arr$[i$];
                if (templateClass.equals(innerClass.getName())) {
                    return innerClass;
                }
            }
            return null;
        }

        private void setBuilderContentView(Notification n, RemoteViews contentView) {
            n.contentView = contentView;
            if (contentView instanceof BuilderRemoteViews) {
                this.mRebuildBundle.putInt(EXTRA_REBUILD_CONTENT_VIEW_ACTION_COUNT, contentView.getSequenceNumber());
            }
        }

        private void setBuilderBigContentView(Notification n, RemoteViews bigContentView) {
            n.bigContentView = bigContentView;
            if (bigContentView instanceof BuilderRemoteViews) {
                this.mRebuildBundle.putInt(EXTRA_REBUILD_BIG_CONTENT_VIEW_ACTION_COUNT, bigContentView.getSequenceNumber());
            }
        }

        private void setBuilderHeadsUpContentView(Notification n, RemoteViews headsUpContentView) {
            n.headsUpContentView = headsUpContentView;
            if (headsUpContentView instanceof BuilderRemoteViews) {
                this.mRebuildBundle.putInt(EXTRA_REBUILD_HEADS_UP_CONTENT_VIEW_ACTION_COUNT, headsUpContentView.getSequenceNumber());
            }
        }

        private void restoreFromNotification(Notification n) {
            this.mWhen = n.when;
            this.mSmallIcon = n.icon;
            this.mSmallIconLevel = n.iconLevel;
            this.mNumber = n.number;
            this.mColor = n.color;
            this.mContentView = n.contentView;
            this.mDeleteIntent = n.deleteIntent;
            this.mFullScreenIntent = n.fullScreenIntent;
            this.mTickerText = n.tickerText;
            this.mTickerView = n.tickerView;
            this.mLargeIcon = n.largeIcon;
            this.mSound = n.sound;
            this.mAudioStreamType = n.audioStreamType;
            this.mAudioAttributes = n.audioAttributes;
            this.mVibrate = n.vibrate;
            this.mLedArgb = n.ledARGB;
            this.mLedOnMs = n.ledOnMS;
            this.mLedOffMs = n.ledOffMS;
            this.mDefaults = n.defaults;
            this.mFlags = n.flags;
            this.mCategory = n.category;
            this.mGroupKey = n.mGroupKey;
            this.mSortKey = n.mSortKey;
            this.mPriority = n.priority;
            this.mActions.clear();
            if (n.actions != null) {
                Collections.addAll(this.mActions, n.actions);
            }
            this.mVisibility = n.visibility;
            this.mPublicVersion = n.publicVersion;
            Bundle extras = n.extras;
            this.mOriginatingUserId = extras.getInt(Notification.EXTRA_ORIGINATING_USERID);
            this.mContentTitle = extras.getCharSequence(Notification.EXTRA_TITLE);
            this.mContentText = extras.getCharSequence(Notification.EXTRA_TEXT);
            this.mSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
            this.mContentInfo = extras.getCharSequence(Notification.EXTRA_INFO_TEXT);
            this.mSmallIcon = extras.getInt(Notification.EXTRA_SMALL_ICON);
            this.mProgress = extras.getInt(Notification.EXTRA_PROGRESS);
            this.mProgressMax = extras.getInt(Notification.EXTRA_PROGRESS_MAX);
            this.mProgressIndeterminate = extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE);
            this.mUseChronometer = extras.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER);
            this.mShowWhen = extras.getBoolean(Notification.EXTRA_SHOW_WHEN);
            if (extras.containsKey(Notification.EXTRA_LARGE_ICON)) {
                this.mLargeIcon = (Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON);
            }
            if (extras.containsKey(Notification.EXTRA_PEOPLE)) {
                this.mPeople.clear();
                Collections.addAll(this.mPeople, extras.getStringArray(Notification.EXTRA_PEOPLE));
            }
        }

        @Deprecated
        public Notification getNotification() {
            return build();
        }

        public Notification build() {
            this.mOriginatingUserId = this.mContext.getUserId();
            this.mHasThreeLines = hasThreeLines();
            Notification n = buildUnstyled();
            if (this.mStyle != null) {
                n = this.mStyle.buildStyled(n);
            }
            if (this.mExtras != null) {
                n.extras.putAll(this.mExtras);
            }
            if (this.mRebuildBundle.size() > 0) {
                n.extras.putAll(this.mRebuildBundle);
                this.mRebuildBundle.clear();
            }
            populateExtras(n.extras);
            if (this.mStyle != null) {
                this.mStyle.addExtras(n.extras);
            }
            this.mHasThreeLines = false;
            return n;
        }

        public Notification buildInto(Notification n) {
            build().cloneInto(n, STRIP_AND_REBUILD);
            return n;
        }

        private int getBaseLayoutResource() {
            return 17367159;
        }

        private int getBigBaseLayoutResource() {
            return 17367160;
        }

        private int getBigPictureLayoutResource() {
            return 17367163;
        }

        private int getBigTextLayoutResource() {
            return 17367164;
        }

        private int getInboxLayoutResource() {
            return 17367165;
        }

        private int getActionLayoutResource() {
            return 17367154;
        }

        private int getActionTombstoneLayoutResource() {
            return 17367156;
        }
    }

    private static class BuilderRemoteViews extends RemoteViews {
        public BuilderRemoteViews(Parcel parcel) {
            super(parcel);
        }

        public BuilderRemoteViews(ApplicationInfo appInfo, int layoutId) {
            super(appInfo, layoutId);
        }

        public BuilderRemoteViews clone() {
            Parcel p = Parcel.obtain();
            writeToParcel(p, Notification.VISIBILITY_PRIVATE);
            p.setDataPosition(Notification.VISIBILITY_PRIVATE);
            BuilderRemoteViews brv = new BuilderRemoteViews(p);
            p.recycle();
            return brv;
        }
    }

    public interface Extender {
        Builder extend(Builder builder);
    }

    public static class InboxStyle extends Style {
        private ArrayList<CharSequence> mTexts;

        public InboxStyle() {
            this.mTexts = new ArrayList(5);
        }

        public InboxStyle(Builder builder) {
            this.mTexts = new ArrayList(5);
            setBuilder(builder);
        }

        public InboxStyle setBigContentTitle(CharSequence title) {
            internalSetBigContentTitle(Notification.safeCharSequence(title));
            return this;
        }

        public InboxStyle setSummaryText(CharSequence cs) {
            internalSetSummaryText(Notification.safeCharSequence(cs));
            return this;
        }

        public InboxStyle addLine(CharSequence cs) {
            this.mTexts.add(Notification.safeCharSequence(cs));
            return this;
        }

        public void addExtras(Bundle extras) {
            super.addExtras(extras);
            extras.putCharSequenceArray(Notification.EXTRA_TEXT_LINES, (CharSequence[]) this.mTexts.toArray(new CharSequence[this.mTexts.size()]));
        }

        protected void restoreFromExtras(Bundle extras) {
            super.restoreFromExtras(extras);
            this.mTexts.clear();
            if (extras.containsKey(Notification.EXTRA_TEXT_LINES)) {
                Collections.addAll(this.mTexts, extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES));
            }
        }

        private RemoteViews makeBigContentView() {
            CharSequence oldBuilderContentText = this.mBuilder.mContentText;
            this.mBuilder.mContentText = null;
            RemoteViews contentView = getStandardView(this.mBuilder.getInboxLayoutResource());
            this.mBuilder.mContentText = oldBuilderContentText;
            contentView.setViewVisibility(R.id.text2, Notification.FLAG_ONLY_ALERT_ONCE);
            int[] rowIds = new int[]{16909115, 16909116, 16909117, 16909118, 16909119, 16909120, 16909121};
            int[] arr$ = rowIds;
            int len$ = arr$.length;
            for (int i$ = Notification.VISIBILITY_PRIVATE; i$ < len$; i$ += Notification.VISIBILITY_PUBLIC) {
                contentView.setViewVisibility(arr$[i$], Notification.FLAG_ONLY_ALERT_ONCE);
            }
            boolean largeText = this.mBuilder.mContext.getResources().getConfiguration().fontScale > LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            float subTextSize = (float) this.mBuilder.mContext.getResources().getDimensionPixelSize(17104983);
            int i = Notification.VISIBILITY_PRIVATE;
            while (i < this.mTexts.size() && i < rowIds.length) {
                CharSequence str = (CharSequence) this.mTexts.get(i);
                if (!(str == null || str.equals(ProxyInfo.LOCAL_EXCL_LIST))) {
                    contentView.setViewVisibility(rowIds[i], Notification.VISIBILITY_PRIVATE);
                    contentView.setTextViewText(rowIds[i], this.mBuilder.processLegacyText(str));
                    if (largeText) {
                        contentView.setTextViewTextSize(rowIds[i], Notification.VISIBILITY_PRIVATE, subTextSize);
                    }
                }
                i += Notification.VISIBILITY_PUBLIC;
            }
            contentView.setViewVisibility(16909123, this.mTexts.size() > 0 ? Notification.VISIBILITY_PRIVATE : Notification.FLAG_ONLY_ALERT_ONCE);
            contentView.setViewVisibility(16909122, this.mTexts.size() > rowIds.length ? Notification.VISIBILITY_PRIVATE : Notification.FLAG_ONLY_ALERT_ONCE);
            applyTopPadding(contentView);
            this.mBuilder.shrinkLine3Text(contentView);
            this.mBuilder.addProfileBadge(contentView, 16909110);
            return contentView;
        }

        public void populateBigContentView(Notification wip) {
            this.mBuilder.setBuilderBigContentView(wip, makeBigContentView());
        }
    }

    public static class MediaStyle extends Style {
        static final int MAX_MEDIA_BUTTONS = 5;
        static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
        private int[] mActionsToShowInCompact;
        private Token mToken;

        public MediaStyle() {
            this.mActionsToShowInCompact = null;
        }

        public MediaStyle(Builder builder) {
            this.mActionsToShowInCompact = null;
            setBuilder(builder);
        }

        public MediaStyle setShowActionsInCompactView(int... actions) {
            this.mActionsToShowInCompact = actions;
            return this;
        }

        public MediaStyle setMediaSession(Token token) {
            this.mToken = token;
            return this;
        }

        public Notification buildStyled(Notification wip) {
            super.buildStyled(wip);
            if (wip.category == null) {
                wip.category = Notification.CATEGORY_TRANSPORT;
            }
            return wip;
        }

        public void populateContentView(Notification wip) {
            this.mBuilder.setBuilderContentView(wip, makeMediaContentView());
        }

        public void populateBigContentView(Notification wip) {
            this.mBuilder.setBuilderBigContentView(wip, makeMediaBigContentView());
        }

        public void addExtras(Bundle extras) {
            super.addExtras(extras);
            if (this.mToken != null) {
                extras.putParcelable(Notification.EXTRA_MEDIA_SESSION, this.mToken);
            }
            if (this.mActionsToShowInCompact != null) {
                extras.putIntArray(Notification.EXTRA_COMPACT_ACTIONS, this.mActionsToShowInCompact);
            }
        }

        protected void restoreFromExtras(Bundle extras) {
            super.restoreFromExtras(extras);
            if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) {
                this.mToken = (Token) extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
            }
            if (extras.containsKey(Notification.EXTRA_COMPACT_ACTIONS)) {
                this.mActionsToShowInCompact = extras.getIntArray(Notification.EXTRA_COMPACT_ACTIONS);
            }
        }

        private RemoteViews generateMediaActionButton(Action action) {
            boolean tombstone;
            if (action.actionIntent == null) {
                tombstone = true;
            } else {
                tombstone = false;
            }
            RemoteViews button = new RemoteViews(this.mBuilder.mContext.getPackageName(), 17367157);
            button.setImageViewResource(16909103, action.icon);
            button.setDrawableParameters(16909103, false, Notification.VISIBILITY_SECRET, Notification.VISIBILITY_SECRET, Mode.SRC_ATOP, Notification.VISIBILITY_SECRET);
            if (!tombstone) {
                button.setOnClickPendingIntent(16909103, action.actionIntent);
            }
            button.setContentDescription(16909103, action.title);
            return button;
        }

        private RemoteViews makeMediaContentView() {
            int N;
            RemoteViews view = this.mBuilder.applyStandardTemplate(17367166, false);
            int numActions = this.mBuilder.mActions.size();
            if (this.mActionsToShowInCompact == null) {
                N = Notification.VISIBILITY_PRIVATE;
            } else {
                N = Math.min(this.mActionsToShowInCompact.length, MAX_MEDIA_BUTTONS_IN_COMPACT);
            }
            if (N > 0) {
                view.removeAllViews(16909112);
                for (int i = Notification.VISIBILITY_PRIVATE; i < N; i += Notification.VISIBILITY_PUBLIC) {
                    if (i >= numActions) {
                        Object[] objArr = new Object[Notification.PRIORITY_MAX];
                        objArr[Notification.VISIBILITY_PRIVATE] = Integer.valueOf(i);
                        objArr[Notification.VISIBILITY_PUBLIC] = Integer.valueOf(numActions + Notification.VISIBILITY_SECRET);
                        throw new IllegalArgumentException(String.format("setShowActionsInCompactView: action %d out of bounds (max %d)", objArr));
                    }
                    view.addView(16909112, generateMediaActionButton((Action) this.mBuilder.mActions.get(this.mActionsToShowInCompact[i])));
                }
            }
            styleText(view);
            hideRightIcon(view);
            return view;
        }

        private RemoteViews makeMediaBigContentView() {
            int actionCount = Math.min(this.mBuilder.mActions.size(), MAX_MEDIA_BUTTONS);
            RemoteViews big = this.mBuilder.applyStandardTemplate(getBigLayoutResource(actionCount), false);
            if (actionCount > 0) {
                big.removeAllViews(16909112);
                for (int i = Notification.VISIBILITY_PRIVATE; i < actionCount; i += Notification.VISIBILITY_PUBLIC) {
                    big.addView(16909112, generateMediaActionButton((Action) this.mBuilder.mActions.get(i)));
                }
            }
            styleText(big);
            hideRightIcon(big);
            applyTopPadding(big);
            big.setViewVisibility(R.id.progress, Notification.FLAG_ONLY_ALERT_ONCE);
            return big;
        }

        private int getBigLayoutResource(int actionCount) {
            if (actionCount <= MAX_MEDIA_BUTTONS_IN_COMPACT) {
                return 17367162;
            }
            return 17367161;
        }

        private void hideRightIcon(RemoteViews contentView) {
            contentView.setViewVisibility(16908340, Notification.FLAG_ONLY_ALERT_ONCE);
        }

        private void styleText(RemoteViews contentView) {
            int primaryColor = this.mBuilder.mContext.getResources().getColor(17170513);
            int secondaryColor = this.mBuilder.mContext.getResources().getColor(17170514);
            contentView.setTextColor(R.id.title, primaryColor);
            if (this.mBuilder.showsTimeOrChronometer()) {
                if (this.mBuilder.mUseChronometer) {
                    contentView.setTextColor(16909124, secondaryColor);
                } else {
                    contentView.setTextColor(16908415, secondaryColor);
                }
            }
            contentView.setTextColor(R.id.text2, secondaryColor);
            contentView.setTextColor(16908392, secondaryColor);
            contentView.setTextColor(16909128, secondaryColor);
        }

        protected boolean hasProgress() {
            return false;
        }
    }

    public static final class WearableExtender implements Extender {
        private static final int DEFAULT_CONTENT_ICON_GRAVITY = 8388613;
        private static final int DEFAULT_FLAGS = 1;
        private static final int DEFAULT_GRAVITY = 80;
        private static final String EXTRA_WEARABLE_EXTENSIONS = "android.wearable.EXTENSIONS";
        private static final int FLAG_CONTENT_INTENT_AVAILABLE_OFFLINE = 1;
        private static final int FLAG_HINT_AVOID_BACKGROUND_CLIPPING = 16;
        private static final int FLAG_HINT_HIDE_ICON = 2;
        private static final int FLAG_HINT_SHOW_BACKGROUND_ONLY = 4;
        private static final int FLAG_START_SCROLL_BOTTOM = 8;
        private static final String KEY_ACTIONS = "actions";
        private static final String KEY_BACKGROUND = "background";
        private static final String KEY_CONTENT_ACTION_INDEX = "contentActionIndex";
        private static final String KEY_CONTENT_ICON = "contentIcon";
        private static final String KEY_CONTENT_ICON_GRAVITY = "contentIconGravity";
        private static final String KEY_CUSTOM_CONTENT_HEIGHT = "customContentHeight";
        private static final String KEY_CUSTOM_SIZE_PRESET = "customSizePreset";
        private static final String KEY_DISPLAY_INTENT = "displayIntent";
        private static final String KEY_FLAGS = "flags";
        private static final String KEY_GRAVITY = "gravity";
        private static final String KEY_HINT_SCREEN_TIMEOUT = "hintScreenTimeout";
        private static final String KEY_PAGES = "pages";
        public static final int SCREEN_TIMEOUT_LONG = -1;
        public static final int SCREEN_TIMEOUT_SHORT = 0;
        public static final int SIZE_DEFAULT = 0;
        public static final int SIZE_FULL_SCREEN = 5;
        public static final int SIZE_LARGE = 4;
        public static final int SIZE_MEDIUM = 3;
        public static final int SIZE_SMALL = 2;
        public static final int SIZE_XSMALL = 1;
        public static final int UNSET_ACTION_INDEX = -1;
        private ArrayList<Action> mActions;
        private Bitmap mBackground;
        private int mContentActionIndex;
        private int mContentIcon;
        private int mContentIconGravity;
        private int mCustomContentHeight;
        private int mCustomSizePreset;
        private PendingIntent mDisplayIntent;
        private int mFlags;
        private int mGravity;
        private int mHintScreenTimeout;
        private ArrayList<Notification> mPages;

        public WearableExtender() {
            this.mActions = new ArrayList();
            this.mFlags = SIZE_XSMALL;
            this.mPages = new ArrayList();
            this.mContentIconGravity = DEFAULT_CONTENT_ICON_GRAVITY;
            this.mContentActionIndex = UNSET_ACTION_INDEX;
            this.mCustomSizePreset = SIZE_DEFAULT;
            this.mGravity = DEFAULT_GRAVITY;
        }

        public WearableExtender(Notification notif) {
            this.mActions = new ArrayList();
            this.mFlags = SIZE_XSMALL;
            this.mPages = new ArrayList();
            this.mContentIconGravity = DEFAULT_CONTENT_ICON_GRAVITY;
            this.mContentActionIndex = UNSET_ACTION_INDEX;
            this.mCustomSizePreset = SIZE_DEFAULT;
            this.mGravity = DEFAULT_GRAVITY;
            Bundle wearableBundle = notif.extras.getBundle(EXTRA_WEARABLE_EXTENSIONS);
            if (wearableBundle != null) {
                List<Action> actions = wearableBundle.getParcelableArrayList(KEY_ACTIONS);
                if (actions != null) {
                    this.mActions.addAll(actions);
                }
                this.mFlags = wearableBundle.getInt(KEY_FLAGS, SIZE_XSMALL);
                this.mDisplayIntent = (PendingIntent) wearableBundle.getParcelable(KEY_DISPLAY_INTENT);
                Notification[] pages = Notification.getNotificationArrayFromBundle(wearableBundle, KEY_PAGES);
                if (pages != null) {
                    Collections.addAll(this.mPages, pages);
                }
                this.mBackground = (Bitmap) wearableBundle.getParcelable(KEY_BACKGROUND);
                this.mContentIcon = wearableBundle.getInt(KEY_CONTENT_ICON);
                this.mContentIconGravity = wearableBundle.getInt(KEY_CONTENT_ICON_GRAVITY, DEFAULT_CONTENT_ICON_GRAVITY);
                this.mContentActionIndex = wearableBundle.getInt(KEY_CONTENT_ACTION_INDEX, UNSET_ACTION_INDEX);
                this.mCustomSizePreset = wearableBundle.getInt(KEY_CUSTOM_SIZE_PRESET, SIZE_DEFAULT);
                this.mCustomContentHeight = wearableBundle.getInt(KEY_CUSTOM_CONTENT_HEIGHT);
                this.mGravity = wearableBundle.getInt(KEY_GRAVITY, DEFAULT_GRAVITY);
                this.mHintScreenTimeout = wearableBundle.getInt(KEY_HINT_SCREEN_TIMEOUT);
            }
        }

        public Builder extend(Builder builder) {
            Bundle wearableBundle = new Bundle();
            if (!this.mActions.isEmpty()) {
                wearableBundle.putParcelableArrayList(KEY_ACTIONS, this.mActions);
            }
            if (this.mFlags != SIZE_XSMALL) {
                wearableBundle.putInt(KEY_FLAGS, this.mFlags);
            }
            if (this.mDisplayIntent != null) {
                wearableBundle.putParcelable(KEY_DISPLAY_INTENT, this.mDisplayIntent);
            }
            if (!this.mPages.isEmpty()) {
                wearableBundle.putParcelableArray(KEY_PAGES, (Parcelable[]) this.mPages.toArray(new Notification[this.mPages.size()]));
            }
            if (this.mBackground != null) {
                wearableBundle.putParcelable(KEY_BACKGROUND, this.mBackground);
            }
            if (this.mContentIcon != 0) {
                wearableBundle.putInt(KEY_CONTENT_ICON, this.mContentIcon);
            }
            if (this.mContentIconGravity != DEFAULT_CONTENT_ICON_GRAVITY) {
                wearableBundle.putInt(KEY_CONTENT_ICON_GRAVITY, this.mContentIconGravity);
            }
            if (this.mContentActionIndex != UNSET_ACTION_INDEX) {
                wearableBundle.putInt(KEY_CONTENT_ACTION_INDEX, this.mContentActionIndex);
            }
            if (this.mCustomSizePreset != 0) {
                wearableBundle.putInt(KEY_CUSTOM_SIZE_PRESET, this.mCustomSizePreset);
            }
            if (this.mCustomContentHeight != 0) {
                wearableBundle.putInt(KEY_CUSTOM_CONTENT_HEIGHT, this.mCustomContentHeight);
            }
            if (this.mGravity != DEFAULT_GRAVITY) {
                wearableBundle.putInt(KEY_GRAVITY, this.mGravity);
            }
            if (this.mHintScreenTimeout != 0) {
                wearableBundle.putInt(KEY_HINT_SCREEN_TIMEOUT, this.mHintScreenTimeout);
            }
            builder.getExtras().putBundle(EXTRA_WEARABLE_EXTENSIONS, wearableBundle);
            return builder;
        }

        public WearableExtender clone() {
            WearableExtender that = new WearableExtender();
            that.mActions = new ArrayList(this.mActions);
            that.mFlags = this.mFlags;
            that.mDisplayIntent = this.mDisplayIntent;
            that.mPages = new ArrayList(this.mPages);
            that.mBackground = this.mBackground;
            that.mContentIcon = this.mContentIcon;
            that.mContentIconGravity = this.mContentIconGravity;
            that.mContentActionIndex = this.mContentActionIndex;
            that.mCustomSizePreset = this.mCustomSizePreset;
            that.mCustomContentHeight = this.mCustomContentHeight;
            that.mGravity = this.mGravity;
            that.mHintScreenTimeout = this.mHintScreenTimeout;
            return that;
        }

        public WearableExtender addAction(Action action) {
            this.mActions.add(action);
            return this;
        }

        public WearableExtender addActions(List<Action> actions) {
            this.mActions.addAll(actions);
            return this;
        }

        public WearableExtender clearActions() {
            this.mActions.clear();
            return this;
        }

        public List<Action> getActions() {
            return this.mActions;
        }

        public WearableExtender setDisplayIntent(PendingIntent intent) {
            this.mDisplayIntent = intent;
            return this;
        }

        public PendingIntent getDisplayIntent() {
            return this.mDisplayIntent;
        }

        public WearableExtender addPage(Notification page) {
            this.mPages.add(page);
            return this;
        }

        public WearableExtender addPages(List<Notification> pages) {
            this.mPages.addAll(pages);
            return this;
        }

        public WearableExtender clearPages() {
            this.mPages.clear();
            return this;
        }

        public List<Notification> getPages() {
            return this.mPages;
        }

        public WearableExtender setBackground(Bitmap background) {
            this.mBackground = background;
            return this;
        }

        public Bitmap getBackground() {
            return this.mBackground;
        }

        public WearableExtender setContentIcon(int icon) {
            this.mContentIcon = icon;
            return this;
        }

        public int getContentIcon() {
            return this.mContentIcon;
        }

        public WearableExtender setContentIconGravity(int contentIconGravity) {
            this.mContentIconGravity = contentIconGravity;
            return this;
        }

        public int getContentIconGravity() {
            return this.mContentIconGravity;
        }

        public WearableExtender setContentAction(int actionIndex) {
            this.mContentActionIndex = actionIndex;
            return this;
        }

        public int getContentAction() {
            return this.mContentActionIndex;
        }

        public WearableExtender setGravity(int gravity) {
            this.mGravity = gravity;
            return this;
        }

        public int getGravity() {
            return this.mGravity;
        }

        public WearableExtender setCustomSizePreset(int sizePreset) {
            this.mCustomSizePreset = sizePreset;
            return this;
        }

        public int getCustomSizePreset() {
            return this.mCustomSizePreset;
        }

        public WearableExtender setCustomContentHeight(int height) {
            this.mCustomContentHeight = height;
            return this;
        }

        public int getCustomContentHeight() {
            return this.mCustomContentHeight;
        }

        public WearableExtender setStartScrollBottom(boolean startScrollBottom) {
            setFlag(FLAG_START_SCROLL_BOTTOM, startScrollBottom);
            return this;
        }

        public boolean getStartScrollBottom() {
            return (this.mFlags & FLAG_START_SCROLL_BOTTOM) != 0;
        }

        public WearableExtender setContentIntentAvailableOffline(boolean contentIntentAvailableOffline) {
            setFlag(SIZE_XSMALL, contentIntentAvailableOffline);
            return this;
        }

        public boolean getContentIntentAvailableOffline() {
            return (this.mFlags & SIZE_XSMALL) != 0;
        }

        public WearableExtender setHintHideIcon(boolean hintHideIcon) {
            setFlag(SIZE_SMALL, hintHideIcon);
            return this;
        }

        public boolean getHintHideIcon() {
            return (this.mFlags & SIZE_SMALL) != 0;
        }

        public WearableExtender setHintShowBackgroundOnly(boolean hintShowBackgroundOnly) {
            setFlag(SIZE_LARGE, hintShowBackgroundOnly);
            return this;
        }

        public boolean getHintShowBackgroundOnly() {
            return (this.mFlags & SIZE_LARGE) != 0;
        }

        public WearableExtender setHintAvoidBackgroundClipping(boolean hintAvoidBackgroundClipping) {
            setFlag(FLAG_HINT_AVOID_BACKGROUND_CLIPPING, hintAvoidBackgroundClipping);
            return this;
        }

        public boolean getHintAvoidBackgroundClipping() {
            return (this.mFlags & FLAG_HINT_AVOID_BACKGROUND_CLIPPING) != 0;
        }

        public WearableExtender setHintScreenTimeout(int timeout) {
            this.mHintScreenTimeout = timeout;
            return this;
        }

        public int getHintScreenTimeout() {
            return this.mHintScreenTimeout;
        }

        private void setFlag(int mask, boolean value) {
            if (value) {
                this.mFlags |= mask;
            } else {
                this.mFlags &= mask ^ UNSET_ACTION_INDEX;
            }
        }
    }

    static {
        AUDIO_ATTRIBUTES_DEFAULT = new android.media.AudioAttributes.Builder().setContentType(FLAG_INSISTENT).setUsage(5).build();
        CREATOR = new Creator<Notification>() {
            public Notification createFromParcel(Parcel parcel) {
                return new Notification(parcel);
            }

            public Notification[] newArray(int size) {
                return new Notification[size];
            }
        };
    }

    public String getGroup() {
        return this.mGroupKey;
    }

    public String getSortKey() {
        return this.mSortKey;
    }

    public Notification() {
        this.audioStreamType = VISIBILITY_SECRET;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = VISIBILITY_PRIVATE;
        this.extras = new Bundle();
        this.when = System.currentTimeMillis();
        this.priority = VISIBILITY_PRIVATE;
    }

    public Notification(Context context, int icon, CharSequence tickerText, long when, CharSequence contentTitle, CharSequence contentText, Intent contentIntent) {
        this.audioStreamType = VISIBILITY_SECRET;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = VISIBILITY_PRIVATE;
        this.extras = new Bundle();
        this.when = when;
        this.icon = icon;
        this.tickerText = tickerText;
        setLatestEventInfo(context, contentTitle, contentText, PendingIntent.getActivity(context, VISIBILITY_PRIVATE, contentIntent, VISIBILITY_PRIVATE));
    }

    @Deprecated
    public Notification(int icon, CharSequence tickerText, long when) {
        this.audioStreamType = VISIBILITY_SECRET;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = VISIBILITY_PRIVATE;
        this.extras = new Bundle();
        this.icon = icon;
        this.tickerText = tickerText;
        this.when = when;
    }

    public Notification(Parcel parcel) {
        this.audioStreamType = VISIBILITY_SECRET;
        this.audioAttributes = AUDIO_ATTRIBUTES_DEFAULT;
        this.color = VISIBILITY_PRIVATE;
        this.extras = new Bundle();
        int version = parcel.readInt();
        this.when = parcel.readLong();
        this.icon = parcel.readInt();
        this.number = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.contentIntent = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.deleteIntent = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.tickerView = (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.contentView = (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.largeIcon = (Bitmap) Bitmap.CREATOR.createFromParcel(parcel);
        }
        this.defaults = parcel.readInt();
        this.flags = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.sound = (Uri) Uri.CREATOR.createFromParcel(parcel);
        }
        this.audioStreamType = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.audioAttributes = (AudioAttributes) AudioAttributes.CREATOR.createFromParcel(parcel);
        }
        this.vibrate = parcel.createLongArray();
        this.ledARGB = parcel.readInt();
        this.ledOnMS = parcel.readInt();
        this.ledOffMS = parcel.readInt();
        this.iconLevel = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.fullScreenIntent = (PendingIntent) PendingIntent.CREATOR.createFromParcel(parcel);
        }
        this.priority = parcel.readInt();
        this.category = parcel.readString();
        this.mGroupKey = parcel.readString();
        this.mSortKey = parcel.readString();
        this.extras = parcel.readBundle();
        this.actions = (Action[]) parcel.createTypedArray(Action.CREATOR);
        if (parcel.readInt() != 0) {
            this.bigContentView = (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readInt() != 0) {
            this.headsUpContentView = (RemoteViews) RemoteViews.CREATOR.createFromParcel(parcel);
        }
        this.visibility = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.publicVersion = (Notification) CREATOR.createFromParcel(parcel);
        }
        this.color = parcel.readInt();
    }

    public Notification clone() {
        Notification that = new Notification();
        cloneInto(that, true);
        return that;
    }

    public void cloneInto(Notification that, boolean heavy) {
        that.when = this.when;
        that.icon = this.icon;
        that.number = this.number;
        that.contentIntent = this.contentIntent;
        that.deleteIntent = this.deleteIntent;
        that.fullScreenIntent = this.fullScreenIntent;
        if (this.tickerText != null) {
            that.tickerText = this.tickerText.toString();
        }
        if (heavy && this.tickerView != null) {
            that.tickerView = this.tickerView.clone();
        }
        if (heavy && this.contentView != null) {
            that.contentView = this.contentView.clone();
        }
        if (heavy && this.largeIcon != null) {
            that.largeIcon = Bitmap.createBitmap(this.largeIcon);
        }
        that.iconLevel = this.iconLevel;
        that.sound = this.sound;
        that.audioStreamType = this.audioStreamType;
        if (this.audioAttributes != null) {
            that.audioAttributes = new android.media.AudioAttributes.Builder(this.audioAttributes).build();
        }
        long[] vibrate = this.vibrate;
        if (vibrate != null) {
            int N = vibrate.length;
            long[] vib = new long[N];
            that.vibrate = vib;
            System.arraycopy(vibrate, VISIBILITY_PRIVATE, vib, VISIBILITY_PRIVATE, N);
        }
        that.ledARGB = this.ledARGB;
        that.ledOnMS = this.ledOnMS;
        that.ledOffMS = this.ledOffMS;
        that.defaults = this.defaults;
        that.flags = this.flags;
        that.priority = this.priority;
        that.category = this.category;
        that.mGroupKey = this.mGroupKey;
        that.mSortKey = this.mSortKey;
        if (this.extras != null) {
            try {
                that.extras = new Bundle(this.extras);
                that.extras.size();
            } catch (BadParcelableException e) {
                Log.e(TAG, "could not unparcel extras from notification: " + this, e);
                that.extras = null;
            }
        }
        if (this.actions != null) {
            that.actions = new Action[this.actions.length];
            for (int i = VISIBILITY_PRIVATE; i < this.actions.length; i += VISIBILITY_PUBLIC) {
                that.actions[i] = this.actions[i].clone();
            }
        }
        if (heavy && this.bigContentView != null) {
            that.bigContentView = this.bigContentView.clone();
        }
        if (heavy && this.headsUpContentView != null) {
            that.headsUpContentView = this.headsUpContentView.clone();
        }
        that.visibility = this.visibility;
        if (this.publicVersion != null) {
            that.publicVersion = new Notification();
            this.publicVersion.cloneInto(that.publicVersion, heavy);
        }
        that.color = this.color;
        if (!heavy) {
            that.lightenPayload();
        }
    }

    public final void lightenPayload() {
        this.tickerView = null;
        this.contentView = null;
        this.bigContentView = null;
        this.headsUpContentView = null;
        this.largeIcon = null;
        if (this.extras != null) {
            this.extras.remove(EXTRA_LARGE_ICON);
            this.extras.remove(EXTRA_LARGE_ICON_BIG);
            this.extras.remove(EXTRA_PICTURE);
            this.extras.remove(EXTRA_BIG_TEXT);
            this.extras.remove(Builder.EXTRA_NEEDS_REBUILD);
        }
    }

    public static CharSequence safeCharSequence(CharSequence cs) {
        if (cs == null) {
            return cs;
        }
        if (cs.length() > MAX_CHARSEQUENCE_LENGTH) {
            cs = cs.subSequence(VISIBILITY_PRIVATE, MAX_CHARSEQUENCE_LENGTH);
        }
        if (!(cs instanceof Parcelable)) {
            return cs;
        }
        Log.e(TAG, "warning: " + cs.getClass().getCanonicalName() + " instance is a custom Parcelable and not allowed in Notification");
        return cs.toString();
    }

    public int describeContents() {
        return VISIBILITY_PRIVATE;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(VISIBILITY_PUBLIC);
        parcel.writeLong(this.when);
        parcel.writeInt(this.icon);
        parcel.writeInt(this.number);
        if (this.contentIntent != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.contentIntent.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        if (this.deleteIntent != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.deleteIntent.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        if (this.tickerText != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            TextUtils.writeToParcel(this.tickerText, parcel, flags);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        if (this.tickerView != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.tickerView.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        if (this.contentView != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.contentView.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        if (this.largeIcon != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.largeIcon.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        parcel.writeInt(this.defaults);
        parcel.writeInt(this.flags);
        if (this.sound != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.sound.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        parcel.writeInt(this.audioStreamType);
        if (this.audioAttributes != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.audioAttributes.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        parcel.writeLongArray(this.vibrate);
        parcel.writeInt(this.ledARGB);
        parcel.writeInt(this.ledOnMS);
        parcel.writeInt(this.ledOffMS);
        parcel.writeInt(this.iconLevel);
        if (this.fullScreenIntent != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.fullScreenIntent.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        parcel.writeInt(this.priority);
        parcel.writeString(this.category);
        parcel.writeString(this.mGroupKey);
        parcel.writeString(this.mSortKey);
        parcel.writeBundle(this.extras);
        parcel.writeTypedArray(this.actions, VISIBILITY_PRIVATE);
        if (this.bigContentView != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.bigContentView.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        if (this.headsUpContentView != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.headsUpContentView.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        parcel.writeInt(this.visibility);
        if (this.publicVersion != null) {
            parcel.writeInt(VISIBILITY_PUBLIC);
            this.publicVersion.writeToParcel(parcel, VISIBILITY_PRIVATE);
        } else {
            parcel.writeInt(VISIBILITY_PRIVATE);
        }
        parcel.writeInt(this.color);
    }

    @Deprecated
    public void setLatestEventInfo(Context context, CharSequence contentTitle, CharSequence contentText, PendingIntent contentIntent) {
        Builder builder = new Builder(context);
        builder.setWhen(this.when);
        builder.setSmallIcon(this.icon);
        builder.setPriority(this.priority);
        builder.setTicker(this.tickerText);
        builder.setNumber(this.number);
        builder.setColor(this.color);
        builder.mFlags = this.flags;
        builder.setSound(this.sound, this.audioStreamType);
        builder.setDefaults(this.defaults);
        builder.setVibrate(this.vibrate);
        if (contentTitle != null) {
            builder.setContentTitle(contentTitle);
        }
        if (contentText != null) {
            builder.setContentText(contentText);
        }
        builder.setContentIntent(contentIntent);
        builder.buildInto(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notification(pri=");
        sb.append(this.priority);
        sb.append(" contentView=");
        if (this.contentView != null) {
            sb.append(this.contentView.getPackage());
            sb.append("/0x");
            sb.append(Integer.toHexString(this.contentView.getLayoutId()));
        } else {
            sb.append("null");
        }
        sb.append(" vibrate=");
        if ((this.defaults & PRIORITY_MAX) != 0) {
            sb.append("default");
        } else if (this.vibrate != null) {
            int N = this.vibrate.length + VISIBILITY_SECRET;
            sb.append("[");
            for (int i = VISIBILITY_PRIVATE; i < N; i += VISIBILITY_PUBLIC) {
                sb.append(this.vibrate[i]);
                sb.append(PhoneNumberUtils.PAUSE);
            }
            if (N != VISIBILITY_SECRET) {
                sb.append(this.vibrate[N]);
            }
            sb.append("]");
        } else {
            sb.append("null");
        }
        sb.append(" sound=");
        if ((this.defaults & VISIBILITY_PUBLIC) != 0) {
            sb.append("default");
        } else if (this.sound != null) {
            sb.append(this.sound.toString());
        } else {
            sb.append("null");
        }
        sb.append(" defaults=0x");
        sb.append(Integer.toHexString(this.defaults));
        sb.append(" flags=0x");
        sb.append(Integer.toHexString(this.flags));
        Object[] objArr = new Object[VISIBILITY_PUBLIC];
        objArr[VISIBILITY_PRIVATE] = Integer.valueOf(this.color);
        sb.append(String.format(" color=0x%08x", objArr));
        if (this.category != null) {
            sb.append(" category=");
            sb.append(this.category);
        }
        if (this.mGroupKey != null) {
            sb.append(" groupKey=");
            sb.append(this.mGroupKey);
        }
        if (this.mSortKey != null) {
            sb.append(" sortKey=");
            sb.append(this.mSortKey);
        }
        if (this.actions != null) {
            sb.append(" actions=");
            sb.append(this.actions.length);
        }
        sb.append(" vis=");
        sb.append(visibilityToString(this.visibility));
        if (this.publicVersion != null) {
            sb.append(" publicVersion=");
            sb.append(this.publicVersion.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public static String visibilityToString(int vis) {
        switch (vis) {
            case VISIBILITY_SECRET /*-1*/:
                return "SECRET";
            case VISIBILITY_PRIVATE /*0*/:
                return "PRIVATE";
            case VISIBILITY_PUBLIC /*1*/:
                return "PUBLIC";
            default:
                return "UNKNOWN(" + String.valueOf(vis) + ")";
        }
    }

    public boolean isValid() {
        return this.contentView != null || this.extras.getBoolean(Builder.EXTRA_REBUILD_CONTENT_VIEW);
    }

    public boolean isGroupSummary() {
        return (this.mGroupKey == null || (this.flags & FLAG_GROUP_SUMMARY) == 0) ? false : true;
    }

    public boolean isGroupChild() {
        return this.mGroupKey != null && (this.flags & FLAG_GROUP_SUMMARY) == 0;
    }

    private static Notification[] getNotificationArrayFromBundle(Bundle bundle, String key) {
        Parcelable[] array = bundle.getParcelableArray(key);
        if ((array instanceof Notification[]) || array == null) {
            return (Notification[]) array;
        }
        Notification[] typedArray = (Notification[]) Arrays.copyOf(array, array.length, Notification[].class);
        bundle.putParcelableArray(key, typedArray);
        return typedArray;
    }
}
