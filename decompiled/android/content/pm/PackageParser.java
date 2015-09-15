package android.content.pm;

import android.Manifest.permission;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.UserHandle;
import android.service.notification.ZenModeConfig;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import com.android.internal.R;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.StrictJarFile;
import java.util.zip.ZipEntry;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PackageParser {
    private static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";
    private static final String ANDROID_RESOURCES = "http://schemas.android.com/apk/res/android";
    private static final boolean DEBUG_BACKUP = false;
    private static final boolean DEBUG_JAR = false;
    private static final boolean DEBUG_PARSER = false;
    public static final NewPermissionInfo[] NEW_PERMISSIONS;
    public static final int PARSE_CHATTY = 2;
    public static final int PARSE_COLLECT_CERTIFICATES = 256;
    private static final int PARSE_DEFAULT_INSTALL_LOCATION = -1;
    public static final int PARSE_FORWARD_LOCK = 16;
    public static final int PARSE_IGNORE_PROCESSES = 8;
    public static final int PARSE_IS_PRIVILEGED = 128;
    public static final int PARSE_IS_SYSTEM = 1;
    public static final int PARSE_IS_SYSTEM_DIR = 64;
    public static final int PARSE_MUST_BE_APK = 4;
    public static final int PARSE_ON_SDCARD = 32;
    public static final int PARSE_TRUSTED_OVERLAY = 512;
    private static final boolean RIGID_PARSER = false;
    private static final String[] SDK_CODENAMES;
    private static final int SDK_VERSION;
    public static final SplitPermissionInfo[] SPLIT_PERMISSIONS;
    private static final String TAG = "PackageParser";
    private static AtomicReference<byte[]> sBuffer;
    private static boolean sCompatibilityModeEnabled;
    private static final Comparator<String> sSplitNameComparator;
    @Deprecated
    private String mArchiveSourcePath;
    private DisplayMetrics mMetrics;
    private boolean mOnlyCoreApps;
    private ParseComponentArgs mParseActivityAliasArgs;
    private ParseComponentArgs mParseActivityArgs;
    private int mParseError;
    private ParsePackageItemArgs mParseInstrumentationArgs;
    private ParseComponentArgs mParseProviderArgs;
    private ParseComponentArgs mParseServiceArgs;
    private String[] mSeparateProcesses;

    public static class Component<II extends IntentInfo> {
        public final String className;
        ComponentName componentName;
        String componentShortName;
        public final ArrayList<II> intents;
        public Bundle metaData;
        public final Package owner;

        public Component(Package _owner) {
            this.owner = _owner;
            this.intents = null;
            this.className = null;
        }

        public Component(ParsePackageItemArgs args, PackageItemInfo outInfo) {
            this.owner = args.owner;
            this.intents = new ArrayList(PackageParser.SDK_VERSION);
            String name = args.sa.getNonConfigurationString(args.nameRes, PackageParser.SDK_VERSION);
            if (name == null) {
                this.className = null;
                args.outError[PackageParser.SDK_VERSION] = args.tag + " does not specify android:name";
                return;
            }
            outInfo.name = PackageParser.buildClassName(this.owner.applicationInfo.packageName, name, args.outError);
            if (outInfo.name == null) {
                this.className = null;
                args.outError[PackageParser.SDK_VERSION] = args.tag + " does not have valid android:name";
                return;
            }
            this.className = outInfo.name;
            int iconVal = args.sa.getResourceId(args.iconRes, PackageParser.SDK_VERSION);
            if (iconVal != 0) {
                outInfo.icon = iconVal;
                outInfo.nonLocalizedLabel = null;
            }
            int logoVal = args.sa.getResourceId(args.logoRes, PackageParser.SDK_VERSION);
            if (logoVal != 0) {
                outInfo.logo = logoVal;
            }
            int bannerVal = args.sa.getResourceId(args.bannerRes, PackageParser.SDK_VERSION);
            if (bannerVal != 0) {
                outInfo.banner = bannerVal;
            }
            TypedValue v = args.sa.peekValue(args.labelRes);
            if (v != null) {
                int i = v.resourceId;
                outInfo.labelRes = i;
                if (i == 0) {
                    outInfo.nonLocalizedLabel = v.coerceToString();
                }
            }
            outInfo.packageName = this.owner.packageName;
        }

        public Component(ParseComponentArgs args, ComponentInfo outInfo) {
            this((ParsePackageItemArgs) args, (PackageItemInfo) outInfo);
            if (args.outError[PackageParser.SDK_VERSION] == null) {
                if (args.processRes != 0) {
                    CharSequence pname;
                    if (this.owner.applicationInfo.targetSdkVersion >= PackageParser.PARSE_IGNORE_PROCESSES) {
                        pname = args.sa.getNonConfigurationString(args.processRes, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
                    } else {
                        pname = args.sa.getNonResourceString(args.processRes);
                    }
                    outInfo.processName = PackageParser.buildProcessName(this.owner.applicationInfo.packageName, this.owner.applicationInfo.processName, pname, args.flags, args.sepProcesses, args.outError);
                }
                if (args.descriptionRes != 0) {
                    outInfo.descriptionRes = args.sa.getResourceId(args.descriptionRes, PackageParser.SDK_VERSION);
                }
                outInfo.enabled = args.sa.getBoolean(args.enabledRes, true);
            }
        }

        public Component(Component<II> clone) {
            this.owner = clone.owner;
            this.intents = clone.intents;
            this.className = clone.className;
            this.componentName = clone.componentName;
            this.componentShortName = clone.componentShortName;
        }

        public ComponentName getComponentName() {
            if (this.componentName != null) {
                return this.componentName;
            }
            if (this.className != null) {
                this.componentName = new ComponentName(this.owner.applicationInfo.packageName, this.className);
            }
            return this.componentName;
        }

        public void appendComponentShortName(StringBuilder sb) {
            ComponentName.appendShortString(sb, this.owner.applicationInfo.packageName, this.className);
        }

        public void printComponentShortName(PrintWriter pw) {
            ComponentName.printShortString(pw, this.owner.applicationInfo.packageName, this.className);
        }

        public void setPackageName(String packageName) {
            this.componentName = null;
            this.componentShortName = null;
        }
    }

    public static final class Activity extends Component<ActivityIntentInfo> {
        public final ActivityInfo info;

        public Activity(ParseComponentArgs args, ActivityInfo _info) {
            super(args, (ComponentInfo) _info);
            this.info = _info;
            this.info.applicationInfo = args.owner.applicationInfo;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(PackageParser.PARSE_IS_PRIVILEGED);
            sb.append("Activity{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class IntentInfo extends IntentFilter {
        public int banner;
        public boolean hasDefault;
        public int icon;
        public int labelRes;
        public int logo;
        public CharSequence nonLocalizedLabel;
        public int preferred;
    }

    public static final class ActivityIntentInfo extends IntentInfo {
        public final Activity activity;

        public ActivityIntentInfo(Activity _activity) {
            this.activity = _activity;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(PackageParser.PARSE_IS_PRIVILEGED);
            sb.append("ActivityIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.activity.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static class ApkLite {
        public final String codePath;
        public final boolean coreApp;
        public final int installLocation;
        public final boolean multiArch;
        public final String packageName;
        public final int revisionCode;
        public final Signature[] signatures;
        public final String splitName;
        public final VerifierInfo[] verifiers;
        public final int versionCode;

        public ApkLite(String codePath, String packageName, String splitName, int versionCode, int revisionCode, int installLocation, List<VerifierInfo> verifiers, Signature[] signatures, boolean coreApp, boolean multiArch) {
            this.codePath = codePath;
            this.packageName = packageName;
            this.splitName = splitName;
            this.versionCode = versionCode;
            this.revisionCode = revisionCode;
            this.installLocation = installLocation;
            this.verifiers = (VerifierInfo[]) verifiers.toArray(new VerifierInfo[verifiers.size()]);
            this.signatures = signatures;
            this.coreApp = coreApp;
            this.multiArch = multiArch;
        }
    }

    public static final class Instrumentation extends Component {
        public final InstrumentationInfo info;

        public Instrumentation(android.content.pm.PackageParser.ParsePackageItemArgs r1, android.content.pm.InstrumentationInfo r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.content.pm.PackageParser.Instrumentation.<init>(android.content.pm.PackageParser$ParsePackageItemArgs, android.content.pm.InstrumentationInfo):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.content.pm.PackageParser.Instrumentation.<init>(android.content.pm.PackageParser$ParsePackageItemArgs, android.content.pm.InstrumentationInfo):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.Instrumentation.<init>(android.content.pm.PackageParser$ParsePackageItemArgs, android.content.pm.InstrumentationInfo):void");
        }

        public void setPackageName(java.lang.String r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.content.pm.PackageParser.Instrumentation.setPackageName(java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.content.pm.PackageParser.Instrumentation.setPackageName(java.lang.String):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.Instrumentation.setPackageName(java.lang.String):void");
        }

        public java.lang.String toString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.content.pm.PackageParser.Instrumentation.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.content.pm.PackageParser.Instrumentation.toString():java.lang.String
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
            throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.Instrumentation.toString():java.lang.String");
        }
    }

    public static class NewPermissionInfo {
        public final int fileVersion;
        public final String name;
        public final int sdkVersion;

        public NewPermissionInfo(String name, int sdkVersion, int fileVersion) {
            this.name = name;
            this.sdkVersion = sdkVersion;
            this.fileVersion = fileVersion;
        }
    }

    public static final class Package {
        public final ArrayList<Activity> activities;
        public final ApplicationInfo applicationInfo;
        public String baseCodePath;
        public boolean baseHardwareAccelerated;
        public int baseRevisionCode;
        public String codePath;
        public ArrayList<ConfigurationInfo> configPreferences;
        public boolean coreApp;
        public String cpuAbiOverride;
        public ArrayList<FeatureGroupInfo> featureGroups;
        public int installLocation;
        public final ArrayList<Instrumentation> instrumentation;
        public ArrayList<String> libraryNames;
        public ArrayList<String> mAdoptPermissions;
        public Bundle mAppMetaData;
        public Certificate[][] mCertificates;
        public final ArraySet<String> mDexOptPerformed;
        public Object mExtras;
        public ArrayMap<String, ArraySet<PublicKey>> mKeySetMapping;
        public long mLastPackageUsageTimeInMills;
        public boolean mOperationPending;
        public ArrayList<String> mOriginalPackages;
        public int mOverlayPriority;
        public String mOverlayTarget;
        public int mPreferredOrder;
        public String mRealPackage;
        public String mRequiredAccountType;
        public boolean mRequiredForAllUsers;
        public String mRestrictedAccountType;
        public String mSharedUserId;
        public int mSharedUserLabel;
        public Signature[] mSignatures;
        public ArraySet<PublicKey> mSigningKeys;
        public boolean mTrustedOverlay;
        public ArraySet<String> mUpgradeKeySets;
        public int mVersionCode;
        public String mVersionName;
        public ManifestDigest manifestDigest;
        public String packageName;
        public final ArrayList<PermissionGroup> permissionGroups;
        public final ArrayList<Permission> permissions;
        public ArrayList<ActivityIntentInfo> preferredActivityFilters;
        public ArrayList<String> protectedBroadcasts;
        public final ArrayList<Provider> providers;
        public final ArrayList<Activity> receivers;
        public ArrayList<FeatureInfo> reqFeatures;
        public final ArrayList<String> requestedPermissions;
        public final ArrayList<Boolean> requestedPermissionsRequired;
        public final ArrayList<Service> services;
        public String[] splitCodePaths;
        public int[] splitFlags;
        public String[] splitNames;
        public int[] splitRevisionCodes;
        public ArrayList<String> usesLibraries;
        public String[] usesLibraryFiles;
        public ArrayList<String> usesOptionalLibraries;

        public Package(String packageName) {
            this.applicationInfo = new ApplicationInfo();
            this.permissions = new ArrayList(PackageParser.SDK_VERSION);
            this.permissionGroups = new ArrayList(PackageParser.SDK_VERSION);
            this.activities = new ArrayList(PackageParser.SDK_VERSION);
            this.receivers = new ArrayList(PackageParser.SDK_VERSION);
            this.providers = new ArrayList(PackageParser.SDK_VERSION);
            this.services = new ArrayList(PackageParser.SDK_VERSION);
            this.instrumentation = new ArrayList(PackageParser.SDK_VERSION);
            this.requestedPermissions = new ArrayList();
            this.requestedPermissionsRequired = new ArrayList();
            this.libraryNames = null;
            this.usesLibraries = null;
            this.usesOptionalLibraries = null;
            this.usesLibraryFiles = null;
            this.preferredActivityFilters = null;
            this.mOriginalPackages = null;
            this.mRealPackage = null;
            this.mAdoptPermissions = null;
            this.mAppMetaData = null;
            this.mPreferredOrder = PackageParser.SDK_VERSION;
            this.mDexOptPerformed = new ArraySet(PackageParser.PARSE_MUST_BE_APK);
            this.configPreferences = null;
            this.reqFeatures = null;
            this.featureGroups = null;
            this.packageName = packageName;
            this.applicationInfo.packageName = packageName;
            this.applicationInfo.uid = PackageParser.PARSE_DEFAULT_INSTALL_LOCATION;
        }

        public List<String> getAllCodePaths() {
            ArrayList<String> paths = new ArrayList();
            paths.add(this.baseCodePath);
            if (!ArrayUtils.isEmpty(this.splitCodePaths)) {
                Collections.addAll(paths, this.splitCodePaths);
            }
            return paths;
        }

        public List<String> getAllCodePathsExcludingResourceOnly() {
            ArrayList<String> paths = new ArrayList();
            if ((this.applicationInfo.flags & PackageParser.PARSE_MUST_BE_APK) != 0) {
                paths.add(this.baseCodePath);
            }
            if (!ArrayUtils.isEmpty(this.splitCodePaths)) {
                for (int i = PackageParser.SDK_VERSION; i < this.splitCodePaths.length; i += PackageParser.PARSE_IS_SYSTEM) {
                    if ((this.splitFlags[i] & PackageParser.PARSE_MUST_BE_APK) != 0) {
                        paths.add(this.splitCodePaths[i]);
                    }
                }
            }
            return paths;
        }

        public void setPackageName(String newName) {
            int i;
            this.packageName = newName;
            this.applicationInfo.packageName = newName;
            for (i = this.permissions.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                ((Permission) this.permissions.get(i)).setPackageName(newName);
            }
            for (i = this.permissionGroups.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                ((PermissionGroup) this.permissionGroups.get(i)).setPackageName(newName);
            }
            for (i = this.activities.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                ((Activity) this.activities.get(i)).setPackageName(newName);
            }
            for (i = this.receivers.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                ((Activity) this.receivers.get(i)).setPackageName(newName);
            }
            for (i = this.providers.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                ((Provider) this.providers.get(i)).setPackageName(newName);
            }
            for (i = this.services.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                ((Service) this.services.get(i)).setPackageName(newName);
            }
            for (i = this.instrumentation.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                ((Instrumentation) this.instrumentation.get(i)).setPackageName(newName);
            }
        }

        public boolean hasComponentClassName(String name) {
            int i;
            for (i = this.activities.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                if (name.equals(((Activity) this.activities.get(i)).className)) {
                    return true;
                }
            }
            for (i = this.receivers.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                if (name.equals(((Activity) this.receivers.get(i)).className)) {
                    return true;
                }
            }
            for (i = this.providers.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                if (name.equals(((Provider) this.providers.get(i)).className)) {
                    return true;
                }
            }
            for (i = this.services.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                if (name.equals(((Service) this.services.get(i)).className)) {
                    return true;
                }
            }
            for (i = this.instrumentation.size() + PackageParser.PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PackageParser.PARSE_DEFAULT_INSTALL_LOCATION) {
                if (name.equals(((Instrumentation) this.instrumentation.get(i)).className)) {
                    return true;
                }
            }
            return PackageParser.RIGID_PARSER;
        }

        public String toString() {
            return "Package{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
        }
    }

    public static class PackageLite {
        public final String baseCodePath;
        public final int baseRevisionCode;
        public final String codePath;
        public final boolean coreApp;
        public final int installLocation;
        public final boolean multiArch;
        public final String packageName;
        public final String[] splitCodePaths;
        public final String[] splitNames;
        public final int[] splitRevisionCodes;
        public final VerifierInfo[] verifiers;
        public final int versionCode;

        public PackageLite(String codePath, ApkLite baseApk, String[] splitNames, String[] splitCodePaths, int[] splitRevisionCodes) {
            this.packageName = baseApk.packageName;
            this.versionCode = baseApk.versionCode;
            this.installLocation = baseApk.installLocation;
            this.verifiers = baseApk.verifiers;
            this.splitNames = splitNames;
            this.codePath = codePath;
            this.baseCodePath = baseApk.codePath;
            this.splitCodePaths = splitCodePaths;
            this.baseRevisionCode = baseApk.revisionCode;
            this.splitRevisionCodes = splitRevisionCodes;
            this.coreApp = baseApk.coreApp;
            this.multiArch = baseApk.multiArch;
        }

        public List<String> getAllCodePaths() {
            ArrayList<String> paths = new ArrayList();
            paths.add(this.baseCodePath);
            if (!ArrayUtils.isEmpty(this.splitCodePaths)) {
                Collections.addAll(paths, this.splitCodePaths);
            }
            return paths;
        }
    }

    public static class PackageParserException extends Exception {
        public final int error;

        public PackageParserException(int error, String detailMessage) {
            super(detailMessage);
            this.error = error;
        }

        public PackageParserException(int error, String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
            this.error = error;
        }
    }

    static class ParsePackageItemArgs {
        final int bannerRes;
        final int iconRes;
        final int labelRes;
        final int logoRes;
        final int nameRes;
        final String[] outError;
        final Package owner;
        TypedArray sa;
        String tag;

        ParsePackageItemArgs(Package _owner, String[] _outError, int _nameRes, int _labelRes, int _iconRes, int _logoRes, int _bannerRes) {
            this.owner = _owner;
            this.outError = _outError;
            this.nameRes = _nameRes;
            this.labelRes = _labelRes;
            this.iconRes = _iconRes;
            this.logoRes = _logoRes;
            this.bannerRes = _bannerRes;
        }
    }

    static class ParseComponentArgs extends ParsePackageItemArgs {
        final int descriptionRes;
        final int enabledRes;
        int flags;
        final int processRes;
        final String[] sepProcesses;

        ParseComponentArgs(Package _owner, String[] _outError, int _nameRes, int _labelRes, int _iconRes, int _logoRes, int _bannerRes, String[] _sepProcesses, int _processRes, int _descriptionRes, int _enabledRes) {
            super(_owner, _outError, _nameRes, _labelRes, _iconRes, _logoRes, _bannerRes);
            this.sepProcesses = _sepProcesses;
            this.processRes = _processRes;
            this.descriptionRes = _descriptionRes;
            this.enabledRes = _enabledRes;
        }
    }

    public static final class Permission extends Component<IntentInfo> {
        public PermissionGroup group;
        public final PermissionInfo info;
        public boolean tree;

        public Permission(Package _owner) {
            super(_owner);
            this.info = new PermissionInfo();
        }

        public Permission(Package _owner, PermissionInfo _info) {
            super(_owner);
            this.info = _info;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            return "Permission{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.info.name + "}";
        }
    }

    public static final class PermissionGroup extends Component<IntentInfo> {
        public final PermissionGroupInfo info;

        public PermissionGroup(Package _owner) {
            super(_owner);
            this.info = new PermissionGroupInfo();
        }

        public PermissionGroup(Package _owner, PermissionGroupInfo _info) {
            super(_owner);
            this.info = _info;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            return "PermissionGroup{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.info.name + "}";
        }
    }

    public static final class Provider extends Component<ProviderIntentInfo> {
        public final ProviderInfo info;
        public boolean syncable;

        public Provider(ParseComponentArgs args, ProviderInfo _info) {
            super(args, (ComponentInfo) _info);
            this.info = _info;
            this.info.applicationInfo = args.owner.applicationInfo;
            this.syncable = PackageParser.RIGID_PARSER;
        }

        public Provider(Provider existingProvider) {
            super((Component) existingProvider);
            this.info = existingProvider.info;
            this.syncable = existingProvider.syncable;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(PackageParser.PARSE_IS_PRIVILEGED);
            sb.append("Provider{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final class ProviderIntentInfo extends IntentInfo {
        public final Provider provider;

        public ProviderIntentInfo(Provider provider) {
            this.provider = provider;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(PackageParser.PARSE_IS_PRIVILEGED);
            sb.append("ProviderIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.provider.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final class Service extends Component<ServiceIntentInfo> {
        public final ServiceInfo info;

        public Service(ParseComponentArgs args, ServiceInfo _info) {
            super(args, (ComponentInfo) _info);
            this.info = _info;
            this.info.applicationInfo = args.owner.applicationInfo;
        }

        public void setPackageName(String packageName) {
            super.setPackageName(packageName);
            this.info.packageName = packageName;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(PackageParser.PARSE_IS_PRIVILEGED);
            sb.append("Service{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    public static final class ServiceIntentInfo extends IntentInfo {
        public final Service service;

        public ServiceIntentInfo(Service _service) {
            this.service = _service;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(PackageParser.PARSE_IS_PRIVILEGED);
            sb.append("ServiceIntentInfo{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(' ');
            this.service.appendComponentShortName(sb);
            sb.append('}');
            return sb.toString();
        }
    }

    private static class SplitNameComparator implements Comparator<String> {
        private SplitNameComparator() {
        }

        public int compare(String lhs, String rhs) {
            if (lhs == null) {
                return PackageParser.PARSE_DEFAULT_INSTALL_LOCATION;
            }
            if (rhs == null) {
                return PackageParser.PARSE_IS_SYSTEM;
            }
            return lhs.compareTo(rhs);
        }
    }

    public static class SplitPermissionInfo {
        public final String[] newPerms;
        public final String rootPerm;
        public final int targetSdk;

        public SplitPermissionInfo(String rootPerm, String[] newPerms, int targetSdk) {
            this.rootPerm = rootPerm;
            this.newPerms = newPerms;
            this.targetSdk = targetSdk;
        }
    }

    static {
        NewPermissionInfo[] newPermissionInfoArr = new NewPermissionInfo[PARSE_CHATTY];
        newPermissionInfoArr[SDK_VERSION] = new NewPermissionInfo(permission.WRITE_EXTERNAL_STORAGE, PARSE_MUST_BE_APK, SDK_VERSION);
        newPermissionInfoArr[PARSE_IS_SYSTEM] = new NewPermissionInfo(OnSubscriptionsChangedListener.PERMISSION_ON_SUBSCRIPTIONS_CHANGED, PARSE_MUST_BE_APK, SDK_VERSION);
        NEW_PERMISSIONS = newPermissionInfoArr;
        r0 = new SplitPermissionInfo[3];
        String str = permission.WRITE_EXTERNAL_STORAGE;
        String[] strArr = new String[PARSE_IS_SYSTEM];
        strArr[SDK_VERSION] = permission.READ_EXTERNAL_STORAGE;
        r0[SDK_VERSION] = new SplitPermissionInfo(str, strArr, HapticFeedbackConstants.SAFE_MODE_ENABLED);
        str = permission.READ_CONTACTS;
        strArr = new String[PARSE_IS_SYSTEM];
        strArr[SDK_VERSION] = permission.READ_CALL_LOG;
        r0[PARSE_IS_SYSTEM] = new SplitPermissionInfo(str, strArr, PARSE_FORWARD_LOCK);
        str = permission.WRITE_CONTACTS;
        strArr = new String[PARSE_IS_SYSTEM];
        strArr[SDK_VERSION] = permission.WRITE_CALL_LOG;
        r0[PARSE_CHATTY] = new SplitPermissionInfo(str, strArr, PARSE_FORWARD_LOCK);
        SPLIT_PERMISSIONS = r0;
        SDK_VERSION = VERSION.SDK_INT;
        SDK_CODENAMES = VERSION.ACTIVE_CODENAMES;
        sCompatibilityModeEnabled = true;
        sSplitNameComparator = new SplitNameComparator();
        sBuffer = new AtomicReference();
    }

    public PackageParser() {
        this.mParseError = PARSE_IS_SYSTEM;
        this.mMetrics = new DisplayMetrics();
        this.mMetrics.setToDefaults();
    }

    public void setSeparateProcesses(String[] procs) {
        this.mSeparateProcesses = procs;
    }

    public void setOnlyCoreApps(boolean onlyCoreApps) {
        this.mOnlyCoreApps = onlyCoreApps;
    }

    public void setDisplayMetrics(DisplayMetrics metrics) {
        this.mMetrics = metrics;
    }

    public static final boolean isApkFile(File file) {
        return isApkPath(file.getName());
    }

    private static boolean isApkPath(String path) {
        return path.endsWith(".apk");
    }

    public static PackageInfo generatePackageInfo(Package p, int[] gids, int flags, long firstInstallTime, long lastUpdateTime, ArraySet<String> grantedPermissions, PackageUserState state) {
        return generatePackageInfo(p, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, state, UserHandle.getCallingUserId());
    }

    private static boolean checkUseInstalledOrHidden(int flags, PackageUserState state) {
        return ((!state.installed || state.hidden) && (flags & AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) == 0) ? RIGID_PARSER : true;
    }

    public static boolean isAvailable(PackageUserState state) {
        return checkUseInstalledOrHidden(SDK_VERSION, state);
    }

    public static PackageInfo generatePackageInfo(Package p, int[] gids, int flags, long firstInstallTime, long lastUpdateTime, ArraySet<String> grantedPermissions, PackageUserState state, int userId) {
        if (!checkUseInstalledOrHidden(flags, state)) {
            return null;
        }
        int N;
        int num;
        int i;
        int j;
        int j2;
        PackageInfo pi = new PackageInfo();
        pi.packageName = p.packageName;
        pi.splitNames = p.splitNames;
        pi.versionCode = p.mVersionCode;
        pi.baseRevisionCode = p.baseRevisionCode;
        pi.splitRevisionCodes = p.splitRevisionCodes;
        pi.versionName = p.mVersionName;
        pi.sharedUserId = p.mSharedUserId;
        pi.sharedUserLabel = p.mSharedUserLabel;
        pi.applicationInfo = generateApplicationInfo(p, flags, state, userId);
        pi.installLocation = p.installLocation;
        pi.coreApp = p.coreApp;
        if (!((pi.applicationInfo.flags & PARSE_IS_SYSTEM) == 0 && (pi.applicationInfo.flags & PARSE_IS_PRIVILEGED) == 0)) {
            pi.requiredForAllUsers = p.mRequiredForAllUsers;
        }
        pi.restrictedAccountType = p.mRestrictedAccountType;
        pi.requiredAccountType = p.mRequiredAccountType;
        pi.overlayTarget = p.mOverlayTarget;
        pi.firstInstallTime = firstInstallTime;
        pi.lastUpdateTime = lastUpdateTime;
        if ((flags & PARSE_COLLECT_CERTIFICATES) != 0) {
            pi.gids = gids;
        }
        if ((flags & AccessibilityNodeInfo.ACTION_COPY) != 0) {
            N = p.configPreferences != null ? p.configPreferences.size() : SDK_VERSION;
            if (N > 0) {
                pi.configPreferences = new ConfigurationInfo[N];
                p.configPreferences.toArray(pi.configPreferences);
            }
            N = p.reqFeatures != null ? p.reqFeatures.size() : SDK_VERSION;
            if (N > 0) {
                pi.reqFeatures = new FeatureInfo[N];
                p.reqFeatures.toArray(pi.reqFeatures);
            }
            N = p.featureGroups != null ? p.featureGroups.size() : SDK_VERSION;
            if (N > 0) {
                pi.featureGroups = new FeatureGroupInfo[N];
                p.featureGroups.toArray(pi.featureGroups);
            }
        }
        if ((flags & PARSE_IS_SYSTEM) != 0) {
            N = p.activities.size();
            if (N > 0) {
                if ((flags & PARSE_TRUSTED_OVERLAY) != 0) {
                    pi.activities = new ActivityInfo[N];
                } else {
                    num = SDK_VERSION;
                    for (i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
                        if (((Activity) p.activities.get(i)).info.enabled) {
                            num += PARSE_IS_SYSTEM;
                        }
                    }
                    pi.activities = new ActivityInfo[num];
                }
                i = SDK_VERSION;
                j = SDK_VERSION;
                while (i < N) {
                    if (((Activity) p.activities.get(i)).info.enabled || (flags & PARSE_TRUSTED_OVERLAY) != 0) {
                        j2 = j + PARSE_IS_SYSTEM;
                        pi.activities[j] = generateActivityInfo((Activity) p.activities.get(i), flags, state, userId);
                    } else {
                        j2 = j;
                    }
                    i += PARSE_IS_SYSTEM;
                    j = j2;
                }
            }
        }
        if ((flags & PARSE_CHATTY) != 0) {
            N = p.receivers.size();
            if (N > 0) {
                if ((flags & PARSE_TRUSTED_OVERLAY) != 0) {
                    pi.receivers = new ActivityInfo[N];
                } else {
                    num = SDK_VERSION;
                    for (i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
                        if (((Activity) p.receivers.get(i)).info.enabled) {
                            num += PARSE_IS_SYSTEM;
                        }
                    }
                    pi.receivers = new ActivityInfo[num];
                }
                i = SDK_VERSION;
                j = SDK_VERSION;
                while (i < N) {
                    if (((Activity) p.receivers.get(i)).info.enabled || (flags & PARSE_TRUSTED_OVERLAY) != 0) {
                        j2 = j + PARSE_IS_SYSTEM;
                        pi.receivers[j] = generateActivityInfo((Activity) p.receivers.get(i), flags, state, userId);
                    } else {
                        j2 = j;
                    }
                    i += PARSE_IS_SYSTEM;
                    j = j2;
                }
            }
        }
        if ((flags & PARSE_MUST_BE_APK) != 0) {
            N = p.services.size();
            if (N > 0) {
                if ((flags & PARSE_TRUSTED_OVERLAY) != 0) {
                    pi.services = new ServiceInfo[N];
                } else {
                    num = SDK_VERSION;
                    for (i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
                        if (((Service) p.services.get(i)).info.enabled) {
                            num += PARSE_IS_SYSTEM;
                        }
                    }
                    pi.services = new ServiceInfo[num];
                }
                i = SDK_VERSION;
                j = SDK_VERSION;
                while (i < N) {
                    if (((Service) p.services.get(i)).info.enabled || (flags & PARSE_TRUSTED_OVERLAY) != 0) {
                        j2 = j + PARSE_IS_SYSTEM;
                        pi.services[j] = generateServiceInfo((Service) p.services.get(i), flags, state, userId);
                    } else {
                        j2 = j;
                    }
                    i += PARSE_IS_SYSTEM;
                    j = j2;
                }
            }
        }
        if ((flags & PARSE_IGNORE_PROCESSES) != 0) {
            N = p.providers.size();
            if (N > 0) {
                if ((flags & PARSE_TRUSTED_OVERLAY) != 0) {
                    pi.providers = new ProviderInfo[N];
                } else {
                    num = SDK_VERSION;
                    for (i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
                        if (((Provider) p.providers.get(i)).info.enabled) {
                            num += PARSE_IS_SYSTEM;
                        }
                    }
                    pi.providers = new ProviderInfo[num];
                }
                i = SDK_VERSION;
                j = SDK_VERSION;
                while (i < N) {
                    if (((Provider) p.providers.get(i)).info.enabled || (flags & PARSE_TRUSTED_OVERLAY) != 0) {
                        j2 = j + PARSE_IS_SYSTEM;
                        pi.providers[j] = generateProviderInfo((Provider) p.providers.get(i), flags, state, userId);
                    } else {
                        j2 = j;
                    }
                    i += PARSE_IS_SYSTEM;
                    j = j2;
                }
            }
        }
        if ((flags & PARSE_FORWARD_LOCK) != 0) {
            N = p.instrumentation.size();
            if (N > 0) {
                pi.instrumentation = new InstrumentationInfo[N];
                for (i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
                    pi.instrumentation[i] = generateInstrumentationInfo((Instrumentation) p.instrumentation.get(i), flags);
                }
            }
        }
        if ((flags & AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) != 0) {
            N = p.permissions.size();
            if (N > 0) {
                pi.permissions = new PermissionInfo[N];
                for (i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
                    pi.permissions[i] = generatePermissionInfo((Permission) p.permissions.get(i), flags);
                }
            }
            N = p.requestedPermissions.size();
            if (N > 0) {
                pi.requestedPermissions = new String[N];
                pi.requestedPermissionsFlags = new int[N];
                for (i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
                    int[] iArr;
                    String perm = (String) p.requestedPermissions.get(i);
                    pi.requestedPermissions[i] = perm;
                    if (((Boolean) p.requestedPermissionsRequired.get(i)).booleanValue()) {
                        iArr = pi.requestedPermissionsFlags;
                        iArr[i] = iArr[i] | PARSE_IS_SYSTEM;
                    }
                    if (grantedPermissions != null && grantedPermissions.contains(perm)) {
                        iArr = pi.requestedPermissionsFlags;
                        iArr[i] = iArr[i] | PARSE_CHATTY;
                    }
                }
            }
        }
        if ((flags & PARSE_IS_SYSTEM_DIR) == 0) {
            return pi;
        }
        N = p.mSignatures != null ? p.mSignatures.length : SDK_VERSION;
        if (N <= 0) {
            return pi;
        }
        pi.signatures = new Signature[N];
        System.arraycopy(p.mSignatures, SDK_VERSION, pi.signatures, SDK_VERSION, N);
        return pi;
    }

    private static Certificate[][] loadCertificates(StrictJarFile jarFile, ZipEntry entry) throws PackageParserException {
        Exception e;
        InputStream inputStream = null;
        try {
            inputStream = jarFile.getInputStream(entry);
            readFullyIgnoringContents(inputStream);
            Certificate[][] certificateChains = jarFile.getCertificateChains(entry);
            IoUtils.closeQuietly(inputStream);
            return certificateChains;
        } catch (Exception e2) {
            e = e2;
            try {
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed reading " + entry.getName() + " in " + jarFile, e);
            } catch (Throwable th) {
                IoUtils.closeQuietly(inputStream);
            }
        } catch (Exception e22) {
            e = e22;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed reading " + entry.getName() + " in " + jarFile, e);
        }
    }

    public static PackageLite parsePackageLite(File packageFile, int flags) throws PackageParserException {
        if (packageFile.isDirectory()) {
            return parseClusterPackageLite(packageFile, flags);
        }
        return parseMonolithicPackageLite(packageFile, flags);
    }

    private static PackageLite parseMonolithicPackageLite(File packageFile, int flags) throws PackageParserException {
        return new PackageLite(packageFile.getAbsolutePath(), parseApkLite(packageFile, flags), null, null, null);
    }

    private static PackageLite parseClusterPackageLite(File packageDir, int flags) throws PackageParserException {
        File[] files = packageDir.listFiles();
        if (ArrayUtils.isEmpty(files)) {
            throw new PackageParserException(-100, "No packages found in split");
        }
        String packageName = null;
        int versionCode = SDK_VERSION;
        ArrayMap<String, ApkLite> apks = new ArrayMap();
        File[] arr$ = files;
        int len$ = arr$.length;
        for (int i$ = SDK_VERSION; i$ < len$; i$ += PARSE_IS_SYSTEM) {
            File file = arr$[i$];
            if (isApkFile(file)) {
                ApkLite lite = parseApkLite(file, flags);
                if (packageName == null) {
                    packageName = lite.packageName;
                    versionCode = lite.versionCode;
                } else {
                    if (!packageName.equals(lite.packageName)) {
                        throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST, "Inconsistent package " + lite.packageName + " in " + file + "; expected " + packageName);
                    } else if (versionCode != lite.versionCode) {
                        throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST, "Inconsistent version " + lite.versionCode + " in " + file + "; expected " + versionCode);
                    }
                }
                if (apks.put(lite.splitName, lite) != null) {
                    throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST, "Split name " + lite.splitName + " defined more than once; most recent was " + file);
                }
            }
        }
        ApkLite baseApk = (ApkLite) apks.remove(null);
        if (baseApk == null) {
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST, "Missing base APK in " + packageDir);
        }
        int size = apks.size();
        String[] splitNames = null;
        String[] splitCodePaths = null;
        int[] splitRevisionCodes = null;
        if (size > 0) {
            splitCodePaths = new String[size];
            splitRevisionCodes = new int[size];
            splitNames = (String[]) apks.keySet().toArray(new String[size]);
            Arrays.sort(splitNames, sSplitNameComparator);
            for (int i = SDK_VERSION; i < size; i += PARSE_IS_SYSTEM) {
                splitCodePaths[i] = ((ApkLite) apks.get(splitNames[i])).codePath;
                splitRevisionCodes[i] = ((ApkLite) apks.get(splitNames[i])).revisionCode;
            }
        }
        return new PackageLite(packageDir.getAbsolutePath(), baseApk, splitNames, splitCodePaths, splitRevisionCodes);
    }

    public Package parsePackage(File packageFile, int flags) throws PackageParserException {
        if (packageFile.isDirectory()) {
            return parseClusterPackage(packageFile, flags);
        }
        return parseMonolithicPackage(packageFile, flags);
    }

    private Package parseClusterPackage(File packageDir, int flags) throws PackageParserException {
        PackageLite lite = parseClusterPackageLite(packageDir, SDK_VERSION);
        if (!this.mOnlyCoreApps || lite.coreApp) {
            AssetManager assets = new AssetManager();
            try {
                loadApkIntoAssetManager(assets, lite.baseCodePath, flags);
                if (!ArrayUtils.isEmpty(lite.splitCodePaths)) {
                    String[] arr$ = lite.splitCodePaths;
                    int len$ = arr$.length;
                    for (int i$ = SDK_VERSION; i$ < len$; i$ += PARSE_IS_SYSTEM) {
                        loadApkIntoAssetManager(assets, arr$[i$], flags);
                    }
                }
                File baseApk = new File(lite.baseCodePath);
                Package pkg = parseBaseApk(baseApk, assets, flags);
                if (pkg == null) {
                    throw new PackageParserException(-100, "Failed to parse base APK: " + baseApk);
                }
                if (!ArrayUtils.isEmpty(lite.splitNames)) {
                    int num = lite.splitNames.length;
                    pkg.splitNames = lite.splitNames;
                    pkg.splitCodePaths = lite.splitCodePaths;
                    pkg.splitRevisionCodes = lite.splitRevisionCodes;
                    pkg.splitFlags = new int[num];
                    for (int i = SDK_VERSION; i < num; i += PARSE_IS_SYSTEM) {
                        parseSplitApk(pkg, i, assets, flags);
                    }
                }
                pkg.codePath = packageDir.getAbsolutePath();
                return pkg;
            } finally {
                IoUtils.closeQuietly(assets);
            }
        } else {
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "Not a coreApp: " + packageDir);
        }
    }

    @Deprecated
    public Package parseMonolithicPackage(File apkFile, int flags) throws PackageParserException {
        if (!this.mOnlyCoreApps || parseMonolithicPackageLite(apkFile, flags).coreApp) {
            AssetManager assets = new AssetManager();
            try {
                Package pkg = parseBaseApk(apkFile, assets, flags);
                pkg.codePath = apkFile.getAbsolutePath();
                return pkg;
            } finally {
                IoUtils.closeQuietly(assets);
            }
        } else {
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "Not a coreApp: " + apkFile);
        }
    }

    private static int loadApkIntoAssetManager(AssetManager assets, String apkPath, int flags) throws PackageParserException {
        if ((flags & PARSE_MUST_BE_APK) == 0 || isApkPath(apkPath)) {
            int cookie = assets.addAssetPath(apkPath);
            if (cookie != 0) {
                return cookie;
            }
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST, "Failed adding asset path: " + apkPath);
        }
        throw new PackageParserException(-100, "Invalid package file: " + apkPath);
    }

    private Package parseBaseApk(File apkFile, AssetManager assets, int flags) throws PackageParserException {
        PackageParserException e;
        Resources resources;
        Throwable th;
        Throwable e2;
        String apkPath = apkFile.getAbsolutePath();
        this.mParseError = PARSE_IS_SYSTEM;
        this.mArchiveSourcePath = apkFile.getAbsolutePath();
        int cookie = loadApkIntoAssetManager(assets, apkPath, flags);
        XmlResourceParser parser = null;
        try {
            Resources resources2 = new Resources(assets, this.mMetrics, null);
            try {
                assets.setConfiguration(SDK_VERSION, SDK_VERSION, null, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, VERSION.RESOURCES_SDK_INT);
                parser = assets.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
                String[] outError = new String[PARSE_IS_SYSTEM];
                Package pkg = parseBaseApk(resources2, parser, flags, outError);
                if (pkg == null) {
                    throw new PackageParserException(this.mParseError, apkPath + " (at " + parser.getPositionDescription() + "): " + outError[SDK_VERSION]);
                }
                pkg.baseCodePath = apkPath;
                pkg.mSignatures = null;
                IoUtils.closeQuietly(parser);
                return pkg;
            } catch (PackageParserException e3) {
                e = e3;
                resources = resources2;
                try {
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Exception e4) {
                e2 = e4;
                resources = resources2;
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to read manifest from " + apkPath, e2);
            } catch (Throwable th3) {
                th = th3;
                resources = resources2;
                IoUtils.closeQuietly(parser);
                throw th;
            }
        } catch (PackageParserException e5) {
            e = e5;
            throw e;
        } catch (Exception e6) {
            e2 = e6;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to read manifest from " + apkPath, e2);
        }
    }

    private void parseSplitApk(Package pkg, int splitIndex, AssetManager assets, int flags) throws PackageParserException {
        XmlResourceParser parser;
        PackageParserException e;
        Resources resources;
        Throwable th;
        Throwable e2;
        String apkPath = pkg.splitCodePaths[splitIndex];
        File apkFile = new File(apkPath);
        this.mParseError = PARSE_IS_SYSTEM;
        this.mArchiveSourcePath = apkPath;
        int cookie = loadApkIntoAssetManager(assets, apkPath, flags);
        try {
            Resources resources2 = new Resources(assets, this.mMetrics, null);
            try {
                assets.setConfiguration(SDK_VERSION, SDK_VERSION, null, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, VERSION.RESOURCES_SDK_INT);
                parser = assets.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
                try {
                    String[] outError = new String[PARSE_IS_SYSTEM];
                    if (parseSplitApk(pkg, resources2, parser, flags, splitIndex, outError) == null) {
                        throw new PackageParserException(this.mParseError, apkPath + " (at " + parser.getPositionDescription() + "): " + outError[SDK_VERSION]);
                    }
                    IoUtils.closeQuietly(parser);
                } catch (PackageParserException e3) {
                    e = e3;
                    resources = resources2;
                    try {
                        throw e;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (Exception e4) {
                    e2 = e4;
                    resources = resources2;
                    throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to read manifest from " + apkPath, e2);
                } catch (Throwable th3) {
                    th = th3;
                    resources = resources2;
                    IoUtils.closeQuietly(parser);
                    throw th;
                }
            } catch (PackageParserException e5) {
                e = e5;
                parser = null;
                resources = resources2;
                throw e;
            } catch (Exception e6) {
                e2 = e6;
                parser = null;
                resources = resources2;
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to read manifest from " + apkPath, e2);
            } catch (Throwable th4) {
                th = th4;
                parser = null;
                resources = resources2;
                IoUtils.closeQuietly(parser);
                throw th;
            }
        } catch (PackageParserException e7) {
            e = e7;
            parser = null;
            throw e;
        } catch (Exception e8) {
            e2 = e8;
            parser = null;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to read manifest from " + apkPath, e2);
        } catch (Throwable th5) {
            th = th5;
            parser = null;
            IoUtils.closeQuietly(parser);
            throw th;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.content.pm.PackageParser.Package parseSplitApk(android.content.pm.PackageParser.Package r15, android.content.res.Resources r16, android.content.res.XmlResourceParser r17, int r18, int r19, java.lang.String[] r20) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException, android.content.pm.PackageParser.PackageParserException {
        /*
        r14 = this;
        r6 = r17;
        r0 = r17;
        r1 = r18;
        parsePackageSplitNames(r0, r6, r1);
        r2 = 0;
        r14.mParseInstrumentationArgs = r2;
        r2 = 0;
        r14.mParseActivityArgs = r2;
        r2 = 0;
        r14.mParseServiceArgs = r2;
        r2 = 0;
        r14.mParseProviderArgs = r2;
        r10 = 0;
        r11 = r17.getDepth();
    L_0x001a:
        r13 = r17.next();
        r2 = 1;
        if (r13 == r2) goto L_0x0098;
    L_0x0021:
        r2 = 3;
        if (r13 != r2) goto L_0x002a;
    L_0x0024:
        r2 = r17.getDepth();
        if (r2 <= r11) goto L_0x0098;
    L_0x002a:
        r2 = 3;
        if (r13 == r2) goto L_0x001a;
    L_0x002d:
        r2 = 4;
        if (r13 == r2) goto L_0x001a;
    L_0x0030:
        r12 = r17.getName();
        r2 = "application";
        r2 = r12.equals(r2);
        if (r2 == 0) goto L_0x005e;
    L_0x003c:
        if (r10 == 0) goto L_0x0049;
    L_0x003e:
        r2 = "PackageParser";
        r3 = "<manifest> has more than one <application>";
        android.util.Slog.w(r2, r3);
        com.android.internal.util.XmlUtils.skipCurrentTag(r17);
        goto L_0x001a;
    L_0x0049:
        r10 = 1;
        r2 = r14;
        r3 = r15;
        r4 = r16;
        r5 = r17;
        r7 = r18;
        r8 = r19;
        r9 = r20;
        r2 = r2.parseSplitApplication(r3, r4, r5, r6, r7, r8, r9);
        if (r2 != 0) goto L_0x001a;
    L_0x005c:
        r15 = 0;
    L_0x005d:
        return r15;
    L_0x005e:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unknown element under <manifest>: ";
        r3 = r3.append(r4);
        r4 = r17.getName();
        r3 = r3.append(r4);
        r4 = " at ";
        r3 = r3.append(r4);
        r4 = r14.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r17.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        com.android.internal.util.XmlUtils.skipCurrentTag(r17);
        goto L_0x001a;
    L_0x0098:
        if (r10 != 0) goto L_0x005d;
    L_0x009a:
        r2 = 0;
        r3 = "<manifest> does not contain an <application>";
        r20[r2] = r3;
        r2 = -109; // 0xffffffffffffff93 float:NaN double:NaN;
        r14.mParseError = r2;
        goto L_0x005d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseSplitApk(android.content.pm.PackageParser$Package, android.content.res.Resources, android.content.res.XmlResourceParser, int, int, java.lang.String[]):android.content.pm.PackageParser$Package");
    }

    public void collectManifestDigest(Package pkg) throws PackageParserException {
        pkg.manifestDigest = null;
        StrictJarFile jarFile;
        try {
            jarFile = new StrictJarFile(pkg.baseCodePath);
            ZipEntry je = jarFile.findEntry(ANDROID_MANIFEST_FILENAME);
            if (je != null) {
                pkg.manifestDigest = ManifestDigest.fromInputStream(jarFile.getInputStream(je));
            }
            jarFile.close();
        } catch (IOException e) {
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "Failed to collect manifest digest");
        } catch (RuntimeException e2) {
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "Failed to collect manifest digest");
        } catch (Throwable th) {
            jarFile.close();
        }
    }

    public void collectCertificates(Package pkg, int flags) throws PackageParserException {
        pkg.mCertificates = (Certificate[][]) null;
        pkg.mSignatures = null;
        pkg.mSigningKeys = null;
        collectCertificates(pkg, new File(pkg.baseCodePath), flags);
        if (!ArrayUtils.isEmpty(pkg.splitCodePaths)) {
            String[] arr$ = pkg.splitCodePaths;
            int len$ = arr$.length;
            for (int i$ = SDK_VERSION; i$ < len$; i$ += PARSE_IS_SYSTEM) {
                collectCertificates(pkg, new File(arr$[i$]), flags);
            }
        }
    }

    private static void collectCertificates(Package pkg, File apkFile, int flags) throws PackageParserException {
        GeneralSecurityException e;
        Throwable th;
        Exception e2;
        Exception e3;
        String apkPath = apkFile.getAbsolutePath();
        StrictJarFile strictJarFile = null;
        try {
            StrictJarFile jarFile = new StrictJarFile(apkPath);
            try {
                ZipEntry manifestEntry = jarFile.findEntry(ANDROID_MANIFEST_FILENAME);
                if (manifestEntry == null) {
                    throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_MANIFEST, "Package " + apkPath + " has no manifest");
                }
                ZipEntry entry;
                List<ZipEntry> toVerify = new ArrayList();
                toVerify.add(manifestEntry);
                if ((flags & PARSE_IS_SYSTEM) == 0) {
                    Iterator<ZipEntry> i = jarFile.iterator();
                    while (i.hasNext()) {
                        entry = (ZipEntry) i.next();
                        if (!(entry.isDirectory() || entry.getName().startsWith("META-INF/") || entry.getName().equals(ANDROID_MANIFEST_FILENAME))) {
                            toVerify.add(entry);
                        }
                    }
                }
                for (ZipEntry entry2 : toVerify) {
                    Certificate[][] entryCerts = loadCertificates(jarFile, entry2);
                    if (ArrayUtils.isEmpty(entryCerts)) {
                        throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES, "Package " + apkPath + " has no certificates at entry " + entry2.getName());
                    }
                    Signature[] entrySignatures = convertToSignatures(entryCerts);
                    if (pkg.mCertificates == null) {
                        pkg.mCertificates = entryCerts;
                        pkg.mSignatures = entrySignatures;
                        pkg.mSigningKeys = new ArraySet();
                        for (int i2 = SDK_VERSION; i2 < entryCerts.length; i2 += PARSE_IS_SYSTEM) {
                            pkg.mSigningKeys.add(entryCerts[i2][SDK_VERSION].getPublicKey());
                        }
                    } else if (!Signature.areExactMatch(pkg.mSignatures, entrySignatures)) {
                        throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES, "Package " + apkPath + " has mismatched certificates at entry " + entry2.getName());
                    }
                }
                closeQuietly(jarFile);
            } catch (GeneralSecurityException e4) {
                e = e4;
                strictJarFile = jarFile;
                try {
                    throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING, "Failed to collect certificates from " + apkPath, e);
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(strictJarFile);
                    throw th;
                }
            } catch (IOException e5) {
                e2 = e5;
                strictJarFile = jarFile;
                e3 = e2;
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES, "Failed to collect certificates from " + apkPath, e3);
            } catch (RuntimeException e6) {
                e2 = e6;
                strictJarFile = jarFile;
                e3 = e2;
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES, "Failed to collect certificates from " + apkPath, e3);
            } catch (Throwable th3) {
                th = th3;
                strictJarFile = jarFile;
                closeQuietly(strictJarFile);
                throw th;
            }
        } catch (GeneralSecurityException e7) {
            e = e7;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING, "Failed to collect certificates from " + apkPath, e);
        } catch (IOException e8) {
            e2 = e8;
            e3 = e2;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES, "Failed to collect certificates from " + apkPath, e3);
        } catch (RuntimeException e9) {
            e2 = e9;
            e3 = e2;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_NO_CERTIFICATES, "Failed to collect certificates from " + apkPath, e3);
        }
    }

    private static Signature[] convertToSignatures(Certificate[][] certs) throws CertificateEncodingException {
        Signature[] res = new Signature[certs.length];
        for (int i = SDK_VERSION; i < certs.length; i += PARSE_IS_SYSTEM) {
            res[i] = new Signature(certs[i]);
        }
        return res;
    }

    public static ApkLite parseApkLite(File apkFile, int flags) throws PackageParserException {
        XmlResourceParser parser;
        Throwable e;
        Throwable e2;
        String apkPath = apkFile.getAbsolutePath();
        AssetManager assets;
        try {
            assets = new AssetManager();
            try {
                assets.setConfiguration(SDK_VERSION, SDK_VERSION, null, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, SDK_VERSION, VERSION.RESOURCES_SDK_INT);
                int cookie = assets.addAssetPath(apkPath);
                if (cookie == 0) {
                    throw new PackageParserException(-100, "Failed to parse " + apkPath);
                }
                Signature[] signatures;
                DisplayMetrics metrics = new DisplayMetrics();
                metrics.setToDefaults();
                Resources res = new Resources(assets, metrics, null);
                parser = assets.openXmlResourceParser(cookie, ANDROID_MANIFEST_FILENAME);
                if ((flags & PARSE_COLLECT_CERTIFICATES) != 0) {
                    try {
                        Package packageR = new Package(null);
                        collectCertificates(packageR, apkFile, SDK_VERSION);
                        signatures = packageR.mSignatures;
                    } catch (XmlPullParserException e3) {
                        e = e3;
                        e2 = e;
                        try {
                            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
                        } catch (Throwable th) {
                            e = th;
                            IoUtils.closeQuietly(parser);
                            IoUtils.closeQuietly(assets);
                            throw e;
                        }
                    } catch (IOException e4) {
                        e = e4;
                        e2 = e;
                        throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
                    } catch (RuntimeException e5) {
                        e = e5;
                        e2 = e;
                        throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
                    }
                }
                signatures = null;
                ApkLite parseApkLite = parseApkLite(apkPath, res, parser, parser, flags, signatures);
                IoUtils.closeQuietly(parser);
                IoUtils.closeQuietly(assets);
                return parseApkLite;
            } catch (XmlPullParserException e6) {
                e = e6;
                parser = null;
                e2 = e;
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
            } catch (IOException e7) {
                e = e7;
                parser = null;
                e2 = e;
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
            } catch (RuntimeException e8) {
                e = e8;
                parser = null;
                e2 = e;
                throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
            } catch (Throwable th2) {
                e = th2;
                parser = null;
                IoUtils.closeQuietly(parser);
                IoUtils.closeQuietly(assets);
                throw e;
            }
        } catch (XmlPullParserException e9) {
            e = e9;
            parser = null;
            assets = null;
            e2 = e;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
        } catch (IOException e10) {
            e = e10;
            parser = null;
            assets = null;
            e2 = e;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
        } catch (RuntimeException e11) {
            e = e11;
            parser = null;
            assets = null;
            e2 = e;
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION, "Failed to parse " + apkPath, e2);
        } catch (Throwable th3) {
            e = th3;
            parser = null;
            assets = null;
            IoUtils.closeQuietly(parser);
            IoUtils.closeQuietly(assets);
            throw e;
        }
    }

    private static String validateName(String name, boolean requiresSeparator) {
        int N = name.length();
        boolean hasSep = RIGID_PARSER;
        boolean front = true;
        for (int i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
            char c = name.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                front = RIGID_PARSER;
            } else if (front || ((c < '0' || c > '9') && c != '_')) {
                if (c != '.') {
                    return "bad character '" + c + "'";
                }
                hasSep = true;
                front = true;
            }
        }
        return (hasSep || !requiresSeparator) ? null : "must have at least one '.' separator";
    }

    private static Pair<String, String> parsePackageSplitNames(XmlPullParser parser, AttributeSet attrs, int flags) throws IOException, XmlPullParserException, PackageParserException {
        int type;
        do {
            type = parser.next();
            if (type == PARSE_CHATTY) {
                break;
            }
        } while (type != PARSE_IS_SYSTEM);
        if (type != PARSE_CHATTY) {
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "No start tag found");
        } else if (parser.getName().equals("manifest")) {
            String error;
            String packageName = attrs.getAttributeValue(null, "package");
            if (!ZenModeConfig.SYSTEM_AUTHORITY.equals(packageName)) {
                error = validateName(packageName, true);
                if (error != null) {
                    throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME, "Invalid manifest package: " + error);
                }
            }
            String splitName = attrs.getAttributeValue(null, "split");
            if (splitName != null) {
                if (splitName.length() == 0) {
                    splitName = null;
                } else {
                    error = validateName(splitName, RIGID_PARSER);
                    if (error != null) {
                        throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME, "Invalid manifest split: " + error);
                    }
                }
            }
            String intern = packageName.intern();
            if (splitName != null) {
                splitName = splitName.intern();
            }
            return Pair.create(intern, splitName);
        } else {
            throw new PackageParserException(PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED, "No <manifest> tag");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.content.pm.PackageParser.ApkLite parseApkLite(java.lang.String r18, android.content.res.Resources r19, org.xmlpull.v1.XmlPullParser r20, android.util.AttributeSet r21, int r22, android.content.pm.Signature[] r23) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException, android.content.pm.PackageParser.PackageParserException {
        /*
        r14 = parsePackageSplitNames(r20, r21, r22);
        r7 = -1;
        r5 = 0;
        r6 = 0;
        r10 = 0;
        r11 = 0;
        r13 = 0;
    L_0x000a:
        r1 = r21.getAttributeCount();
        if (r13 >= r1) goto L_0x005a;
    L_0x0010:
        r0 = r21;
        r12 = r0.getAttributeName(r13);
        r1 = "installLocation";
        r1 = r12.equals(r1);
        if (r1 == 0) goto L_0x0028;
    L_0x001e:
        r1 = -1;
        r0 = r21;
        r7 = r0.getAttributeIntValue(r13, r1);
    L_0x0025:
        r13 = r13 + 1;
        goto L_0x000a;
    L_0x0028:
        r1 = "versionCode";
        r1 = r12.equals(r1);
        if (r1 == 0) goto L_0x0039;
    L_0x0031:
        r1 = 0;
        r0 = r21;
        r5 = r0.getAttributeIntValue(r13, r1);
        goto L_0x0025;
    L_0x0039:
        r1 = "revisionCode";
        r1 = r12.equals(r1);
        if (r1 == 0) goto L_0x004a;
    L_0x0042:
        r1 = 0;
        r0 = r21;
        r6 = r0.getAttributeIntValue(r13, r1);
        goto L_0x0025;
    L_0x004a:
        r1 = "coreApp";
        r1 = r12.equals(r1);
        if (r1 == 0) goto L_0x0025;
    L_0x0052:
        r1 = 0;
        r0 = r21;
        r10 = r0.getAttributeBooleanValue(r13, r1);
        goto L_0x0025;
    L_0x005a:
        r1 = r20.getDepth();
        r15 = r1 + 1;
        r8 = new java.util.ArrayList;
        r8.<init>();
    L_0x0065:
        r16 = r20.next();
        r1 = 1;
        r0 = r16;
        if (r0 == r1) goto L_0x00d4;
    L_0x006e:
        r1 = 3;
        r0 = r16;
        if (r0 != r1) goto L_0x0079;
    L_0x0073:
        r1 = r20.getDepth();
        if (r1 < r15) goto L_0x00d4;
    L_0x0079:
        r1 = 3;
        r0 = r16;
        if (r0 == r1) goto L_0x0065;
    L_0x007e:
        r1 = 4;
        r0 = r16;
        if (r0 == r1) goto L_0x0065;
    L_0x0083:
        r1 = r20.getDepth();
        if (r1 != r15) goto L_0x00a1;
    L_0x0089:
        r1 = "package-verifier";
        r2 = r20.getName();
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x00a1;
    L_0x0096:
        r17 = parseVerifier(r19, r20, r21, r22);
        if (r17 == 0) goto L_0x00a1;
    L_0x009c:
        r0 = r17;
        r8.add(r0);
    L_0x00a1:
        r1 = r20.getDepth();
        if (r1 != r15) goto L_0x0065;
    L_0x00a7:
        r1 = "application";
        r2 = r20.getName();
        r1 = r1.equals(r2);
        if (r1 == 0) goto L_0x0065;
    L_0x00b3:
        r13 = 0;
    L_0x00b4:
        r1 = r21.getAttributeCount();
        if (r13 >= r1) goto L_0x0065;
    L_0x00ba:
        r0 = r21;
        r12 = r0.getAttributeName(r13);
        r1 = "multiArch";
        r1 = r1.equals(r12);
        if (r1 == 0) goto L_0x00d1;
    L_0x00c9:
        r1 = 0;
        r0 = r21;
        r11 = r0.getAttributeBooleanValue(r13, r1);
        goto L_0x0065;
    L_0x00d1:
        r13 = r13 + 1;
        goto L_0x00b4;
    L_0x00d4:
        r1 = new android.content.pm.PackageParser$ApkLite;
        r3 = r14.first;
        r3 = (java.lang.String) r3;
        r4 = r14.second;
        r4 = (java.lang.String) r4;
        r2 = r18;
        r9 = r23;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseApkLite(java.lang.String, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, android.content.pm.Signature[]):android.content.pm.PackageParser$ApkLite");
    }

    public static Signature stringToSignature(String str) {
        int N = str.length();
        byte[] sig = new byte[N];
        for (int i = SDK_VERSION; i < N; i += PARSE_IS_SYSTEM) {
            sig[i] = (byte) str.charAt(i);
        }
        return new Signature(sig);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.content.pm.PackageParser.Package parseBaseApk(android.content.res.Resources r62, android.content.res.XmlResourceParser r63, int r64, java.lang.String[] r65) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r61 = this;
        r0 = r64;
        r2 = r0 & 512;
        if (r2 == 0) goto L_0x005c;
    L_0x0006:
        r58 = 1;
    L_0x0008:
        r6 = r63;
        r2 = 0;
        r0 = r61;
        r0.mParseInstrumentationArgs = r2;
        r2 = 0;
        r0 = r61;
        r0.mParseActivityArgs = r2;
        r2 = 0;
        r0 = r61;
        r0.mParseServiceArgs = r2;
        r2 = 0;
        r0 = r61;
        r0.mParseProviderArgs = r2;
        r0 = r63;
        r1 = r64;
        r43 = parsePackageSplitNames(r0, r6, r1);	 Catch:{ PackageParserException -> 0x005f }
        r0 = r43;
        r0 = r0.first;	 Catch:{ PackageParserException -> 0x005f }
        r45 = r0;
        r45 = (java.lang.String) r45;	 Catch:{ PackageParserException -> 0x005f }
        r0 = r43;
        r0 = r0.second;	 Catch:{ PackageParserException -> 0x005f }
        r49 = r0;
        r49 = (java.lang.String) r49;	 Catch:{ PackageParserException -> 0x005f }
        r2 = android.text.TextUtils.isEmpty(r49);
        if (r2 != 0) goto L_0x0068;
    L_0x003c:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Expected base APK, but found split ";
        r4 = r4.append(r5);
        r0 = r49;
        r4 = r4.append(r0);
        r4 = r4.toString();
        r65[r2] = r4;
        r2 = -106; // 0xffffffffffffff96 float:NaN double:NaN;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
    L_0x005b:
        return r3;
    L_0x005c:
        r58 = 0;
        goto L_0x0008;
    L_0x005f:
        r21 = move-exception;
        r2 = -106; // 0xffffffffffffff96 float:NaN double:NaN;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
        goto L_0x005b;
    L_0x0068:
        r3 = new android.content.pm.PackageParser$Package;
        r0 = r45;
        r3.<init>(r0);
        r25 = 0;
        r2 = com.android.internal.R.styleable.AndroidManifest;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r2 = r3.applicationInfo;
        r4 = 1;
        r5 = 0;
        r0 = r47;
        r4 = r0.getInteger(r4, r5);
        r2.versionCode = r4;
        r3.mVersionCode = r4;
        r2 = 5;
        r4 = 0;
        r0 = r47;
        r2 = r0.getInteger(r2, r4);
        r3.baseRevisionCode = r2;
        r2 = 2;
        r4 = 0;
        r0 = r47;
        r2 = r0.getNonConfigurationString(r2, r4);
        r3.mVersionName = r2;
        r2 = r3.mVersionName;
        if (r2 == 0) goto L_0x00a7;
    L_0x009f:
        r2 = r3.mVersionName;
        r2 = r2.intern();
        r3.mVersionName = r2;
    L_0x00a7:
        r2 = 0;
        r4 = 0;
        r0 = r47;
        r50 = r0.getNonConfigurationString(r2, r4);
        if (r50 == 0) goto L_0x0107;
    L_0x00b1:
        r2 = r50.length();
        if (r2 <= 0) goto L_0x0107;
    L_0x00b7:
        r2 = 1;
        r0 = r50;
        r39 = validateName(r0, r2);
        if (r39 == 0) goto L_0x00f7;
    L_0x00c0:
        r2 = "android";
        r0 = r45;
        r2 = r2.equals(r0);
        if (r2 != 0) goto L_0x00f7;
    L_0x00ca:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "<manifest> specifies bad sharedUserId name \"";
        r4 = r4.append(r5);
        r0 = r50;
        r4 = r4.append(r0);
        r5 = "\": ";
        r4 = r4.append(r5);
        r0 = r39;
        r4 = r4.append(r0);
        r4 = r4.toString();
        r65[r2] = r4;
        r2 = -107; // 0xffffffffffffff95 float:NaN double:NaN;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
        goto L_0x005b;
    L_0x00f7:
        r2 = r50.intern();
        r3.mSharedUserId = r2;
        r2 = 3;
        r4 = 0;
        r0 = r47;
        r2 = r0.getResourceId(r2, r4);
        r3.mSharedUserLabel = r2;
    L_0x0107:
        r2 = 4;
        r4 = -1;
        r0 = r47;
        r2 = r0.getInteger(r2, r4);
        r3.installLocation = r2;
        r2 = r3.applicationInfo;
        r4 = r3.installLocation;
        r2.installLocation = r4;
        r2 = 0;
        r4 = "coreApp";
        r5 = 0;
        r2 = r6.getAttributeBooleanValue(r2, r4, r5);
        r3.coreApp = r2;
        r47.recycle();
        r2 = r64 & 16;
        if (r2 == 0) goto L_0x0131;
    L_0x0128:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r5 = 536870912; // 0x20000000 float:1.0842022E-19 double:2.652494739E-315;
        r4 = r4 | r5;
        r2.flags = r4;
    L_0x0131:
        r2 = r64 & 32;
        if (r2 == 0) goto L_0x013e;
    L_0x0135:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r5 = 262144; // 0x40000 float:3.67342E-40 double:1.295163E-318;
        r4 = r4 | r5;
        r2.flags = r4;
    L_0x013e:
        r53 = 1;
        r52 = 1;
        r51 = 1;
        r54 = 1;
        r46 = 1;
        r17 = 1;
        r42 = r63.getDepth();
    L_0x014e:
        r59 = r63.next();
        r2 = 1;
        r0 = r59;
        if (r0 == r2) goto L_0x078a;
    L_0x0157:
        r2 = 3;
        r0 = r59;
        if (r0 != r2) goto L_0x0164;
    L_0x015c:
        r2 = r63.getDepth();
        r0 = r42;
        if (r2 <= r0) goto L_0x078a;
    L_0x0164:
        r2 = 3;
        r0 = r59;
        if (r0 == r2) goto L_0x014e;
    L_0x0169:
        r2 = 4;
        r0 = r59;
        if (r0 == r2) goto L_0x014e;
    L_0x016e:
        r55 = r63.getName();
        r2 = "application";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x019e;
    L_0x017c:
        if (r25 == 0) goto L_0x0189;
    L_0x017e:
        r2 = "PackageParser";
        r4 = "<manifest> has more than one <application>";
        android.util.Slog.w(r2, r4);
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x0189:
        r25 = 1;
        r2 = r61;
        r4 = r62;
        r5 = r63;
        r7 = r64;
        r8 = r65;
        r2 = r2.parseBaseApplication(r3, r4, r5, r6, r7, r8);
        if (r2 != 0) goto L_0x014e;
    L_0x019b:
        r3 = 0;
        goto L_0x005b;
    L_0x019e:
        r2 = "overlay";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x01fa;
    L_0x01a9:
        r0 = r58;
        r3.mTrustedOverlay = r0;
        r2 = com.android.internal.R.styleable.AndroidManifestResourceOverlay;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r2 = 1;
        r0 = r47;
        r2 = r0.getString(r2);
        r3.mOverlayTarget = r2;
        r2 = 0;
        r4 = -1;
        r0 = r47;
        r2 = r0.getInt(r2, r4);
        r3.mOverlayPriority = r2;
        r47.recycle();
        r2 = r3.mOverlayTarget;
        if (r2 != 0) goto L_0x01dd;
    L_0x01cf:
        r2 = 0;
        r4 = "<overlay> does not specify a target package";
        r65[r2] = r4;
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
        goto L_0x005b;
    L_0x01dd:
        r2 = r3.mOverlayPriority;
        if (r2 < 0) goto L_0x01e7;
    L_0x01e1:
        r2 = r3.mOverlayPriority;
        r4 = 9999; // 0x270f float:1.4012E-41 double:4.94E-320;
        if (r2 <= r4) goto L_0x01f5;
    L_0x01e7:
        r2 = 0;
        r4 = "<overlay> priority must be between 0 and 9999";
        r65[r2] = r4;
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
        goto L_0x005b;
    L_0x01f5:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x01fa:
        r2 = "key-sets";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0215;
    L_0x0204:
        r2 = r61;
        r4 = r62;
        r5 = r63;
        r7 = r65;
        r2 = r2.parseKeySets(r3, r4, r5, r6, r7);
        if (r2 != 0) goto L_0x014e;
    L_0x0212:
        r3 = 0;
        goto L_0x005b;
    L_0x0215:
        r2 = "permission-group";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0235;
    L_0x0220:
        r7 = r61;
        r8 = r3;
        r9 = r64;
        r10 = r62;
        r11 = r63;
        r12 = r6;
        r13 = r65;
        r2 = r7.parsePermissionGroup(r8, r9, r10, r11, r12, r13);
        if (r2 != 0) goto L_0x014e;
    L_0x0232:
        r3 = 0;
        goto L_0x005b;
    L_0x0235:
        r2 = "permission";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0251;
    L_0x0240:
        r2 = r61;
        r4 = r62;
        r5 = r63;
        r7 = r65;
        r2 = r2.parsePermission(r3, r4, r5, r6, r7);
        if (r2 != 0) goto L_0x014e;
    L_0x024e:
        r3 = 0;
        goto L_0x005b;
    L_0x0251:
        r2 = "permission-tree";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x026d;
    L_0x025c:
        r2 = r61;
        r4 = r62;
        r5 = r63;
        r7 = r65;
        r2 = r2.parsePermissionTree(r3, r4, r5, r6, r7);
        if (r2 != 0) goto L_0x014e;
    L_0x026a:
        r3 = 0;
        goto L_0x005b;
    L_0x026d:
        r2 = "uses-permission";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0289;
    L_0x0278:
        r2 = r61;
        r4 = r62;
        r5 = r63;
        r7 = r65;
        r2 = r2.parseUsesPermission(r3, r4, r5, r6, r7);
        if (r2 != 0) goto L_0x014e;
    L_0x0286:
        r3 = 0;
        goto L_0x005b;
    L_0x0289:
        r2 = "uses-configuration";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x02ff;
    L_0x0294:
        r19 = new android.content.pm.ConfigurationInfo;
        r19.<init>();
        r2 = com.android.internal.R.styleable.AndroidManifestUsesConfiguration;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r2 = 0;
        r4 = 0;
        r0 = r47;
        r2 = r0.getInt(r2, r4);
        r0 = r19;
        r0.reqTouchScreen = r2;
        r2 = 1;
        r4 = 0;
        r0 = r47;
        r2 = r0.getInt(r2, r4);
        r0 = r19;
        r0.reqKeyboardType = r2;
        r2 = 2;
        r4 = 0;
        r0 = r47;
        r2 = r0.getBoolean(r2, r4);
        if (r2 == 0) goto L_0x02cd;
    L_0x02c3:
        r0 = r19;
        r2 = r0.reqInputFeatures;
        r2 = r2 | 1;
        r0 = r19;
        r0.reqInputFeatures = r2;
    L_0x02cd:
        r2 = 3;
        r4 = 0;
        r0 = r47;
        r2 = r0.getInt(r2, r4);
        r0 = r19;
        r0.reqNavigation = r2;
        r2 = 4;
        r4 = 0;
        r0 = r47;
        r2 = r0.getBoolean(r2, r4);
        if (r2 == 0) goto L_0x02ed;
    L_0x02e3:
        r0 = r19;
        r2 = r0.reqInputFeatures;
        r2 = r2 | 2;
        r0 = r19;
        r0.reqInputFeatures = r2;
    L_0x02ed:
        r47.recycle();
        r2 = r3.configPreferences;
        r0 = r19;
        r2 = com.android.internal.util.ArrayUtils.add(r2, r0);
        r3.configPreferences = r2;
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x02ff:
        r2 = "uses-feature";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x033e;
    L_0x030a:
        r0 = r61;
        r1 = r62;
        r24 = r0.parseUsesFeature(r1, r6);
        r2 = r3.reqFeatures;
        r0 = r24;
        r2 = com.android.internal.util.ArrayUtils.add(r2, r0);
        r3.reqFeatures = r2;
        r0 = r24;
        r2 = r0.name;
        if (r2 != 0) goto L_0x0339;
    L_0x0322:
        r19 = new android.content.pm.ConfigurationInfo;
        r19.<init>();
        r0 = r24;
        r2 = r0.reqGlEsVersion;
        r0 = r19;
        r0.reqGlEsVersion = r2;
        r2 = r3.configPreferences;
        r0 = r19;
        r2 = com.android.internal.util.ArrayUtils.add(r2, r0);
        r3.configPreferences = r2;
    L_0x0339:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x033e:
        r2 = "feature-group";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x03ff;
    L_0x0348:
        r26 = new android.content.pm.FeatureGroupInfo;
        r26.<init>();
        r23 = 0;
        r31 = r63.getDepth();
    L_0x0353:
        r59 = r63.next();
        r2 = 1;
        r0 = r59;
        if (r0 == r2) goto L_0x03d7;
    L_0x035c:
        r2 = 3;
        r0 = r59;
        if (r0 != r2) goto L_0x0369;
    L_0x0361:
        r2 = r63.getDepth();
        r0 = r31;
        if (r2 <= r0) goto L_0x03d7;
    L_0x0369:
        r2 = 3;
        r0 = r59;
        if (r0 == r2) goto L_0x0353;
    L_0x036e:
        r2 = 4;
        r0 = r59;
        if (r0 == r2) goto L_0x0353;
    L_0x0373:
        r32 = r63.getName();
        r2 = "uses-feature";
        r0 = r32;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x03a0;
    L_0x0382:
        r0 = r61;
        r1 = r62;
        r22 = r0.parseUsesFeature(r1, r6);
        r0 = r22;
        r2 = r0.flags;
        r2 = r2 | 1;
        r0 = r22;
        r0.flags = r2;
        r0 = r23;
        r1 = r22;
        r23 = com.android.internal.util.ArrayUtils.add(r0, r1);
    L_0x039c:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x0353;
    L_0x03a0:
        r2 = "PackageParser";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Unknown element under <feature-group>: ";
        r4 = r4.append(r5);
        r0 = r32;
        r4 = r4.append(r0);
        r5 = " at ";
        r4 = r4.append(r5);
        r0 = r61;
        r5 = r0.mArchiveSourcePath;
        r4 = r4.append(r5);
        r5 = " ";
        r4 = r4.append(r5);
        r5 = r63.getPositionDescription();
        r4 = r4.append(r5);
        r4 = r4.toString();
        android.util.Slog.w(r2, r4);
        goto L_0x039c;
    L_0x03d7:
        if (r23 == 0) goto L_0x03f3;
    L_0x03d9:
        r2 = r23.size();
        r2 = new android.content.pm.FeatureInfo[r2];
        r0 = r26;
        r0.features = r2;
        r0 = r26;
        r2 = r0.features;
        r0 = r23;
        r2 = r0.toArray(r2);
        r2 = (android.content.pm.FeatureInfo[]) r2;
        r0 = r26;
        r0.features = r2;
    L_0x03f3:
        r2 = r3.featureGroups;
        r0 = r26;
        r2 = com.android.internal.util.ArrayUtils.add(r2, r0);
        r3.featureGroups = r2;
        goto L_0x014e;
    L_0x03ff:
        r2 = "uses-sdk";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x05bf;
    L_0x040a:
        r2 = SDK_VERSION;
        if (r2 <= 0) goto L_0x05b3;
    L_0x040e:
        r2 = com.android.internal.R.styleable.AndroidManifestUsesSdk;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r37 = 0;
        r36 = 0;
        r57 = 0;
        r56 = 0;
        r2 = 0;
        r0 = r47;
        r60 = r0.peekValue(r2);
        if (r60 == 0) goto L_0x043e;
    L_0x0427:
        r0 = r60;
        r2 = r0.type;
        r4 = 3;
        if (r2 != r4) goto L_0x04c0;
    L_0x042e:
        r0 = r60;
        r2 = r0.string;
        if (r2 == 0) goto L_0x04c0;
    L_0x0434:
        r0 = r60;
        r2 = r0.string;
        r36 = r2.toString();
        r56 = r36;
    L_0x043e:
        r2 = 1;
        r0 = r47;
        r60 = r0.peekValue(r2);
        if (r60 == 0) goto L_0x045e;
    L_0x0447:
        r0 = r60;
        r2 = r0.type;
        r4 = 3;
        if (r2 != r4) goto L_0x04ca;
    L_0x044e:
        r0 = r60;
        r2 = r0.string;
        if (r2 == 0) goto L_0x04ca;
    L_0x0454:
        r0 = r60;
        r2 = r0.string;
        r36 = r2.toString();
        r56 = r36;
    L_0x045e:
        r47.recycle();
        if (r36 == 0) goto L_0x04f3;
    L_0x0463:
        r16 = 0;
        r18 = SDK_CODENAMES;
        r0 = r18;
        r0 = r0.length;
        r35 = r0;
        r28 = 0;
    L_0x046e:
        r0 = r28;
        r1 = r35;
        if (r0 >= r1) goto L_0x0482;
    L_0x0474:
        r20 = r18[r28];
        r0 = r36;
        r1 = r20;
        r2 = r0.equals(r1);
        if (r2 == 0) goto L_0x04d1;
    L_0x0480:
        r16 = 1;
    L_0x0482:
        if (r16 != 0) goto L_0x052c;
    L_0x0484:
        r2 = SDK_CODENAMES;
        r2 = r2.length;
        if (r2 <= 0) goto L_0x04d4;
    L_0x0489:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Requires development platform ";
        r4 = r4.append(r5);
        r0 = r36;
        r4 = r4.append(r0);
        r5 = " (current platform is any of ";
        r4 = r4.append(r5);
        r5 = SDK_CODENAMES;
        r5 = java.util.Arrays.toString(r5);
        r4 = r4.append(r5);
        r5 = ")";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r65[r2] = r4;
    L_0x04b7:
        r2 = -12;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
        goto L_0x005b;
    L_0x04c0:
        r0 = r60;
        r0 = r0.data;
        r37 = r0;
        r57 = r37;
        goto L_0x043e;
    L_0x04ca:
        r0 = r60;
        r0 = r0.data;
        r57 = r0;
        goto L_0x045e;
    L_0x04d1:
        r28 = r28 + 1;
        goto L_0x046e;
    L_0x04d4:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Requires development platform ";
        r4 = r4.append(r5);
        r0 = r36;
        r4 = r4.append(r0);
        r5 = " but this is a release platform.";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r65[r2] = r4;
        goto L_0x04b7;
    L_0x04f3:
        r2 = SDK_VERSION;
        r0 = r37;
        if (r0 <= r2) goto L_0x052c;
    L_0x04f9:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Requires newer sdk version #";
        r4 = r4.append(r5);
        r0 = r37;
        r4 = r4.append(r0);
        r5 = " (current version is #";
        r4 = r4.append(r5);
        r5 = SDK_VERSION;
        r4 = r4.append(r5);
        r5 = ")";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r65[r2] = r4;
        r2 = -12;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
        goto L_0x005b;
    L_0x052c:
        if (r56 == 0) goto L_0x05b8;
    L_0x052e:
        r16 = 0;
        r18 = SDK_CODENAMES;
        r0 = r18;
        r0 = r0.length;
        r35 = r0;
        r28 = 0;
    L_0x0539:
        r0 = r28;
        r1 = r35;
        if (r0 >= r1) goto L_0x054d;
    L_0x053f:
        r20 = r18[r28];
        r0 = r56;
        r1 = r20;
        r2 = r0.equals(r1);
        if (r2 == 0) goto L_0x058b;
    L_0x054b:
        r16 = 1;
    L_0x054d:
        if (r16 != 0) goto L_0x05ad;
    L_0x054f:
        r2 = SDK_CODENAMES;
        r2 = r2.length;
        if (r2 <= 0) goto L_0x058e;
    L_0x0554:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Requires development platform ";
        r4 = r4.append(r5);
        r0 = r56;
        r4 = r4.append(r0);
        r5 = " (current platform is any of ";
        r4 = r4.append(r5);
        r5 = SDK_CODENAMES;
        r5 = java.util.Arrays.toString(r5);
        r4 = r4.append(r5);
        r5 = ")";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r65[r2] = r4;
    L_0x0582:
        r2 = -12;
        r0 = r61;
        r0.mParseError = r2;
        r3 = 0;
        goto L_0x005b;
    L_0x058b:
        r28 = r28 + 1;
        goto L_0x0539;
    L_0x058e:
        r2 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Requires development platform ";
        r4 = r4.append(r5);
        r0 = r56;
        r4 = r4.append(r0);
        r5 = " but this is a release platform.";
        r4 = r4.append(r5);
        r4 = r4.toString();
        r65[r2] = r4;
        goto L_0x0582;
    L_0x05ad:
        r2 = r3.applicationInfo;
        r4 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
        r2.targetSdkVersion = r4;
    L_0x05b3:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x05b8:
        r2 = r3.applicationInfo;
        r0 = r57;
        r2.targetSdkVersion = r0;
        goto L_0x05b3;
    L_0x05bf:
        r2 = "supports-screens";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0635;
    L_0x05ca:
        r2 = com.android.internal.R.styleable.AndroidManifestSupportsScreens;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r2 = r3.applicationInfo;
        r4 = 6;
        r5 = 0;
        r0 = r47;
        r4 = r0.getInteger(r4, r5);
        r2.requiresSmallestWidthDp = r4;
        r2 = r3.applicationInfo;
        r4 = 7;
        r5 = 0;
        r0 = r47;
        r4 = r0.getInteger(r4, r5);
        r2.compatibleWidthLimitDp = r4;
        r2 = r3.applicationInfo;
        r4 = 8;
        r5 = 0;
        r0 = r47;
        r4 = r0.getInteger(r4, r5);
        r2.largestWidthLimitDp = r4;
        r2 = 1;
        r0 = r47;
        r1 = r53;
        r53 = r0.getInteger(r2, r1);
        r2 = 2;
        r0 = r47;
        r1 = r52;
        r52 = r0.getInteger(r2, r1);
        r2 = 3;
        r0 = r47;
        r1 = r51;
        r51 = r0.getInteger(r2, r1);
        r2 = 5;
        r0 = r47;
        r1 = r54;
        r54 = r0.getInteger(r2, r1);
        r2 = 4;
        r0 = r47;
        r1 = r46;
        r46 = r0.getInteger(r2, r1);
        r2 = 0;
        r0 = r47;
        r1 = r17;
        r17 = r0.getInteger(r2, r1);
        r47.recycle();
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x0635:
        r2 = "protected-broadcast";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x067b;
    L_0x0640:
        r2 = com.android.internal.R.styleable.AndroidManifestProtectedBroadcast;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r2 = 0;
        r0 = r47;
        r38 = r0.getNonResourceString(r2);
        r47.recycle();
        if (r38 == 0) goto L_0x0676;
    L_0x0654:
        r2 = r64 & 1;
        if (r2 == 0) goto L_0x0676;
    L_0x0658:
        r2 = r3.protectedBroadcasts;
        if (r2 != 0) goto L_0x0663;
    L_0x065c:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3.protectedBroadcasts = r2;
    L_0x0663:
        r2 = r3.protectedBroadcasts;
        r0 = r38;
        r2 = r2.contains(r0);
        if (r2 != 0) goto L_0x0676;
    L_0x066d:
        r2 = r3.protectedBroadcasts;
        r4 = r38.intern();
        r2.add(r4);
    L_0x0676:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x067b:
        r2 = "instrumentation";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0696;
    L_0x0685:
        r2 = r61;
        r4 = r62;
        r5 = r63;
        r7 = r65;
        r2 = r2.parseInstrumentation(r3, r4, r5, r6, r7);
        if (r2 != 0) goto L_0x014e;
    L_0x0693:
        r3 = 0;
        goto L_0x005b;
    L_0x0696:
        r2 = "original-package";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x06d9;
    L_0x06a1:
        r2 = com.android.internal.R.styleable.AndroidManifestOriginalPackage;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r2 = 0;
        r4 = 0;
        r0 = r47;
        r41 = r0.getNonConfigurationString(r2, r4);
        r2 = r3.packageName;
        r0 = r41;
        r2 = r2.equals(r0);
        if (r2 != 0) goto L_0x06d1;
    L_0x06bb:
        r2 = r3.mOriginalPackages;
        if (r2 != 0) goto L_0x06ca;
    L_0x06bf:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3.mOriginalPackages = r2;
        r2 = r3.packageName;
        r3.mRealPackage = r2;
    L_0x06ca:
        r2 = r3.mOriginalPackages;
        r0 = r41;
        r2.add(r0);
    L_0x06d1:
        r47.recycle();
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x06d9:
        r2 = "adopt-permissions";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x070f;
    L_0x06e3:
        r2 = com.android.internal.R.styleable.AndroidManifestOriginalPackage;
        r0 = r62;
        r47 = r0.obtainAttributes(r6, r2);
        r2 = 0;
        r4 = 0;
        r0 = r47;
        r38 = r0.getNonConfigurationString(r2, r4);
        r47.recycle();
        if (r38 == 0) goto L_0x070a;
    L_0x06f8:
        r2 = r3.mAdoptPermissions;
        if (r2 != 0) goto L_0x0703;
    L_0x06fc:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3.mAdoptPermissions = r2;
    L_0x0703:
        r2 = r3.mAdoptPermissions;
        r0 = r38;
        r2.add(r0);
    L_0x070a:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x070f:
        r2 = "uses-gl-texture";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x071f;
    L_0x071a:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x071f:
        r2 = "compatible-screens";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x072e;
    L_0x0729:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x072e:
        r2 = "supports-input";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x073e;
    L_0x0739:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x073e:
        r2 = "eat-comment";
        r0 = r55;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x074d;
    L_0x0748:
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x074d:
        r2 = "PackageParser";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Unknown element under <manifest>: ";
        r4 = r4.append(r5);
        r5 = r63.getName();
        r4 = r4.append(r5);
        r5 = " at ";
        r4 = r4.append(r5);
        r0 = r61;
        r5 = r0.mArchiveSourcePath;
        r4 = r4.append(r5);
        r5 = " ";
        r4 = r4.append(r5);
        r5 = r63.getPositionDescription();
        r4 = r4.append(r5);
        r4 = r4.toString();
        android.util.Slog.w(r2, r4);
        com.android.internal.util.XmlUtils.skipCurrentTag(r63);
        goto L_0x014e;
    L_0x078a:
        if (r25 != 0) goto L_0x079f;
    L_0x078c:
        r2 = r3.instrumentation;
        r2 = r2.size();
        if (r2 != 0) goto L_0x079f;
    L_0x0794:
        r2 = 0;
        r4 = "<manifest> does not contain an <application> or <instrumentation>";
        r65[r2] = r4;
        r2 = -109; // 0xffffffffffffff93 float:NaN double:NaN;
        r0 = r61;
        r0.mParseError = r2;
    L_0x079f:
        r2 = NEW_PERMISSIONS;
        r14 = r2.length;
        r29 = 0;
        r33 = 0;
    L_0x07a6:
        r0 = r33;
        if (r0 >= r14) goto L_0x07b8;
    L_0x07aa:
        r2 = NEW_PERMISSIONS;
        r40 = r2[r33];
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r0 = r40;
        r4 = r0.sdkVersion;
        if (r2 < r4) goto L_0x07e9;
    L_0x07b8:
        if (r29 == 0) goto L_0x07c3;
    L_0x07ba:
        r2 = "PackageParser";
        r4 = r29.toString();
        android.util.Slog.i(r2, r4);
    L_0x07c3:
        r2 = SPLIT_PERMISSIONS;
        r15 = r2.length;
        r34 = 0;
    L_0x07c8:
        r0 = r34;
        if (r0 >= r15) goto L_0x085f;
    L_0x07cc:
        r2 = SPLIT_PERMISSIONS;
        r48 = r2[r34];
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r0 = r48;
        r4 = r0.targetSdk;
        if (r2 >= r4) goto L_0x07e6;
    L_0x07da:
        r2 = r3.requestedPermissions;
        r0 = r48;
        r4 = r0.rootPerm;
        r2 = r2.contains(r4);
        if (r2 != 0) goto L_0x0833;
    L_0x07e6:
        r34 = r34 + 1;
        goto L_0x07c8;
    L_0x07e9:
        r2 = r3.requestedPermissions;
        r0 = r40;
        r4 = r0.name;
        r2 = r2.contains(r4);
        if (r2 != 0) goto L_0x0827;
    L_0x07f5:
        if (r29 != 0) goto L_0x082b;
    L_0x07f7:
        r29 = new java.lang.StringBuilder;
        r2 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r0 = r29;
        r0.<init>(r2);
        r2 = r3.packageName;
        r0 = r29;
        r0.append(r2);
        r2 = ": compat added ";
        r0 = r29;
        r0.append(r2);
    L_0x080e:
        r0 = r40;
        r2 = r0.name;
        r0 = r29;
        r0.append(r2);
        r2 = r3.requestedPermissions;
        r0 = r40;
        r4 = r0.name;
        r2.add(r4);
        r2 = r3.requestedPermissionsRequired;
        r4 = java.lang.Boolean.TRUE;
        r2.add(r4);
    L_0x0827:
        r33 = r33 + 1;
        goto L_0x07a6;
    L_0x082b:
        r2 = 32;
        r0 = r29;
        r0.append(r2);
        goto L_0x080e;
    L_0x0833:
        r30 = 0;
    L_0x0835:
        r0 = r48;
        r2 = r0.newPerms;
        r2 = r2.length;
        r0 = r30;
        if (r0 >= r2) goto L_0x07e6;
    L_0x083e:
        r0 = r48;
        r2 = r0.newPerms;
        r44 = r2[r30];
        r2 = r3.requestedPermissions;
        r0 = r44;
        r2 = r2.contains(r0);
        if (r2 != 0) goto L_0x085c;
    L_0x084e:
        r2 = r3.requestedPermissions;
        r0 = r44;
        r2.add(r0);
        r2 = r3.requestedPermissionsRequired;
        r4 = java.lang.Boolean.TRUE;
        r2.add(r4);
    L_0x085c:
        r30 = r30 + 1;
        goto L_0x0835;
    L_0x085f:
        if (r53 < 0) goto L_0x086a;
    L_0x0861:
        if (r53 <= 0) goto L_0x0872;
    L_0x0863:
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r4 = 4;
        if (r2 < r4) goto L_0x0872;
    L_0x086a:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r4 = r4 | 512;
        r2.flags = r4;
    L_0x0872:
        if (r52 == 0) goto L_0x087c;
    L_0x0874:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r4 = r4 | 1024;
        r2.flags = r4;
    L_0x087c:
        if (r51 < 0) goto L_0x0887;
    L_0x087e:
        if (r51 <= 0) goto L_0x088f;
    L_0x0880:
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r4 = 4;
        if (r2 < r4) goto L_0x088f;
    L_0x0887:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r4 = r4 | 2048;
        r2.flags = r4;
    L_0x088f:
        if (r54 < 0) goto L_0x089b;
    L_0x0891:
        if (r54 <= 0) goto L_0x08a4;
    L_0x0893:
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r4 = 9;
        if (r2 < r4) goto L_0x08a4;
    L_0x089b:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r5 = 524288; // 0x80000 float:7.34684E-40 double:2.590327E-318;
        r4 = r4 | r5;
        r2.flags = r4;
    L_0x08a4:
        if (r46 < 0) goto L_0x08af;
    L_0x08a6:
        if (r46 <= 0) goto L_0x08b7;
    L_0x08a8:
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r4 = 4;
        if (r2 < r4) goto L_0x08b7;
    L_0x08af:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r4 = r4 | 4096;
        r2.flags = r4;
    L_0x08b7:
        if (r17 < 0) goto L_0x08c2;
    L_0x08b9:
        if (r17 <= 0) goto L_0x08ca;
    L_0x08bb:
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r4 = 4;
        if (r2 < r4) goto L_0x08ca;
    L_0x08c2:
        r2 = r3.applicationInfo;
        r4 = r2.flags;
        r4 = r4 | 8192;
        r2.flags = r4;
    L_0x08ca:
        r2 = r3.applicationInfo;
        r2 = r2.targetSdkVersion;
        r4 = 18;
        if (r2 >= r4) goto L_0x005b;
    L_0x08d2:
        r27 = 0;
    L_0x08d4:
        r2 = r3.requestedPermissionsRequired;
        r2 = r2.size();
        r0 = r27;
        if (r0 >= r2) goto L_0x005b;
    L_0x08de:
        r2 = r3.requestedPermissionsRequired;
        r4 = java.lang.Boolean.TRUE;
        r0 = r27;
        r2.set(r0, r4);
        r27 = r27 + 1;
        goto L_0x08d4;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseBaseApk(android.content.res.Resources, android.content.res.XmlResourceParser, int, java.lang.String[]):android.content.pm.PackageParser$Package");
    }

    private FeatureInfo parseUsesFeature(Resources res, AttributeSet attrs) throws XmlPullParserException, IOException {
        FeatureInfo fi = new FeatureInfo();
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestUsesFeature);
        fi.name = sa.getNonResourceString(SDK_VERSION);
        if (fi.name == null) {
            fi.reqGlEsVersion = sa.getInt(PARSE_IS_SYSTEM, SDK_VERSION);
        }
        if (sa.getBoolean(PARSE_CHATTY, true)) {
            fi.flags |= PARSE_IS_SYSTEM;
        }
        sa.recycle();
        return fi;
    }

    private boolean parseUsesPermission(Package pkg, Resources res, XmlResourceParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestUsesPermission);
        String name = sa.getNonResourceString(SDK_VERSION);
        int maxSdkVersion = SDK_VERSION;
        TypedValue val = sa.peekValue(PARSE_IS_SYSTEM);
        if (val != null && val.type >= PARSE_FORWARD_LOCK && val.type <= 31) {
            maxSdkVersion = val.data;
        }
        sa.recycle();
        if ((maxSdkVersion == 0 || maxSdkVersion >= VERSION.RESOURCES_SDK_INT) && name != null) {
            int index = pkg.requestedPermissions.indexOf(name);
            if (index == PARSE_DEFAULT_INSTALL_LOCATION) {
                Object obj;
                pkg.requestedPermissions.add(name.intern());
                ArrayList arrayList = pkg.requestedPermissionsRequired;
                if (PARSE_IS_SYSTEM != null) {
                    obj = Boolean.TRUE;
                } else {
                    obj = Boolean.FALSE;
                }
                arrayList.add(obj);
            } else if (!((Boolean) pkg.requestedPermissionsRequired.get(index)).booleanValue()) {
                outError[SDK_VERSION] = "conflicting <uses-permission> entries";
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                return RIGID_PARSER;
            }
        }
        XmlUtils.skipCurrentTag(parser);
        return true;
    }

    private static String buildClassName(String pkg, CharSequence clsSeq, String[] outError) {
        if (clsSeq == null || clsSeq.length() <= 0) {
            outError[SDK_VERSION] = "Empty class name in package " + pkg;
            return null;
        }
        String cls = clsSeq.toString();
        char c = cls.charAt(SDK_VERSION);
        if (c == '.') {
            return (pkg + cls).intern();
        }
        if (cls.indexOf(46) < 0) {
            StringBuilder b = new StringBuilder(pkg);
            b.append('.');
            b.append(cls);
            return b.toString().intern();
        } else if (c >= 'a' && c <= 'z') {
            return cls.intern();
        } else {
            outError[SDK_VERSION] = "Bad class name " + cls + " in package " + pkg;
            return null;
        }
    }

    private static String buildCompoundName(String pkg, CharSequence procSeq, String type, String[] outError) {
        String proc = procSeq.toString();
        char c = proc.charAt(SDK_VERSION);
        String nameError;
        if (pkg == null || c != ':') {
            nameError = validateName(proc, true);
            if (nameError == null || "system".equals(proc)) {
                return proc.intern();
            }
            outError[SDK_VERSION] = "Invalid " + type + " name " + proc + " in package " + pkg + ": " + nameError;
            return null;
        } else if (proc.length() < PARSE_CHATTY) {
            outError[SDK_VERSION] = "Bad " + type + " name " + proc + " in package " + pkg + ": must be at least two characters";
            return null;
        } else {
            nameError = validateName(proc.substring(PARSE_IS_SYSTEM), RIGID_PARSER);
            if (nameError == null) {
                return (pkg + proc).intern();
            }
            outError[SDK_VERSION] = "Invalid " + type + " name " + proc + " in package " + pkg + ": " + nameError;
            return null;
        }
    }

    private static String buildProcessName(String pkg, String defProc, CharSequence procSeq, int flags, String[] separateProcesses, String[] outError) {
        if ((flags & PARSE_IGNORE_PROCESSES) != 0 && !"system".equals(procSeq)) {
            return defProc != null ? defProc : pkg;
        } else {
            if (separateProcesses != null) {
                for (int i = separateProcesses.length + PARSE_DEFAULT_INSTALL_LOCATION; i >= 0; i += PARSE_DEFAULT_INSTALL_LOCATION) {
                    String sp = separateProcesses[i];
                    if (sp.equals(pkg) || sp.equals(defProc) || sp.equals(procSeq)) {
                        return pkg;
                    }
                }
            }
            return (procSeq == null || procSeq.length() <= 0) ? defProc : buildCompoundName(pkg, procSeq, "process", outError);
        }
    }

    private static String buildTaskAffinityName(String pkg, String defProc, CharSequence procSeq, String[] outError) {
        if (procSeq == null) {
            return defProc;
        }
        if (procSeq.length() <= 0) {
            return null;
        }
        return buildCompoundName(pkg, procSeq, "taskAffinity", outError);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean parseKeySets(android.content.pm.PackageParser.Package r28, android.content.res.Resources r29, org.xmlpull.v1.XmlPullParser r30, android.util.AttributeSet r31, java.lang.String[] r32) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r27 = this;
        r15 = r30.getDepth();
        r5 = -1;
        r4 = 0;
        r18 = new android.util.ArrayMap;
        r18.<init>();
        r23 = new android.util.ArraySet;
        r23.<init>();
        r6 = new android.util.ArrayMap;
        r6.<init>();
        r11 = new android.util.ArraySet;
        r11.<init>();
    L_0x001a:
        r22 = r30.next();
        r24 = 1;
        r0 = r22;
        r1 = r24;
        if (r0 == r1) goto L_0x024e;
    L_0x0026:
        r24 = 3;
        r0 = r22;
        r1 = r24;
        if (r0 != r1) goto L_0x0036;
    L_0x002e:
        r24 = r30.getDepth();
        r0 = r24;
        if (r0 <= r15) goto L_0x024e;
    L_0x0036:
        r24 = 3;
        r0 = r22;
        r1 = r24;
        if (r0 != r1) goto L_0x0049;
    L_0x003e:
        r24 = r30.getDepth();
        r0 = r24;
        if (r0 != r5) goto L_0x001a;
    L_0x0046:
        r4 = 0;
        r5 = -1;
        goto L_0x001a;
    L_0x0049:
        r21 = r30.getName();
        r24 = "key-set";
        r0 = r21;
        r1 = r24;
        r24 = r0.equals(r1);
        if (r24 == 0) goto L_0x00a4;
    L_0x0059:
        if (r4 == 0) goto L_0x007a;
    L_0x005b:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Improperly nested 'key-set' tag at ";
        r25 = r25.append(r26);
        r26 = r30.getPositionDescription();
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        r24 = 0;
    L_0x0079:
        return r24;
    L_0x007a:
        r24 = com.android.internal.R.styleable.AndroidManifestKeySet;
        r0 = r29;
        r1 = r31;
        r2 = r24;
        r20 = r0.obtainAttributes(r1, r2);
        r24 = 0;
        r0 = r20;
        r1 = r24;
        r13 = r0.getNonResourceString(r1);
        r24 = new android.util.ArraySet;
        r24.<init>();
        r0 = r24;
        r6.put(r13, r0);
        r4 = r13;
        r5 = r30.getDepth();
        r20.recycle();
        goto L_0x001a;
    L_0x00a4:
        r24 = "public-key";
        r0 = r21;
        r1 = r24;
        r24 = r0.equals(r1);
        if (r24 == 0) goto L_0x01df;
    L_0x00b1:
        if (r4 != 0) goto L_0x00d2;
    L_0x00b3:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Improperly nested 'public-key' tag at ";
        r25 = r25.append(r26);
        r26 = r30.getPositionDescription();
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        r24 = 0;
        goto L_0x0079;
    L_0x00d2:
        r24 = com.android.internal.R.styleable.AndroidManifestPublicKey;
        r0 = r29;
        r1 = r31;
        r2 = r24;
        r20 = r0.obtainAttributes(r1, r2);
        r24 = 0;
        r0 = r20;
        r1 = r24;
        r16 = r0.getNonResourceString(r1);
        r24 = 1;
        r0 = r20;
        r1 = r24;
        r8 = r0.getNonResourceString(r1);
        if (r8 != 0) goto L_0x0135;
    L_0x00f4:
        r0 = r18;
        r1 = r16;
        r24 = r0.get(r1);
        if (r24 != 0) goto L_0x0135;
    L_0x00fe:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "'public-key' ";
        r25 = r25.append(r26);
        r0 = r25;
        r1 = r16;
        r25 = r0.append(r1);
        r26 = " must define a public-key value";
        r25 = r25.append(r26);
        r26 = " on first use at ";
        r25 = r25.append(r26);
        r26 = r30.getPositionDescription();
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        r20.recycle();
        r24 = 0;
        goto L_0x0079;
    L_0x0135:
        if (r8 == 0) goto L_0x0199;
    L_0x0137:
        r3 = parsePublicKey(r8);
        if (r3 != 0) goto L_0x0176;
    L_0x013d:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "No recognized valid key in 'public-key' tag at ";
        r25 = r25.append(r26);
        r26 = r30.getPositionDescription();
        r25 = r25.append(r26);
        r26 = " key-set ";
        r25 = r25.append(r26);
        r0 = r25;
        r25 = r0.append(r4);
        r26 = " will not be added to the package's defined key-sets.";
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        r20.recycle();
        r11.add(r4);
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x001a;
    L_0x0176:
        r0 = r18;
        r1 = r16;
        r24 = r0.get(r1);
        if (r24 == 0) goto L_0x0192;
    L_0x0180:
        r0 = r18;
        r1 = r16;
        r24 = r0.get(r1);
        r24 = (java.security.PublicKey) r24;
        r0 = r24;
        r24 = r0.equals(r3);
        if (r24 == 0) goto L_0x01ae;
    L_0x0192:
        r0 = r18;
        r1 = r16;
        r0.put(r1, r3);
    L_0x0199:
        r24 = r6.get(r4);
        r24 = (android.util.ArraySet) r24;
        r0 = r24;
        r1 = r16;
        r0.add(r1);
        r20.recycle();
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x001a;
    L_0x01ae:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Value of 'public-key' ";
        r25 = r25.append(r26);
        r0 = r25;
        r1 = r16;
        r25 = r0.append(r1);
        r26 = " conflicts with previously defined value at ";
        r25 = r25.append(r26);
        r26 = r30.getPositionDescription();
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        r20.recycle();
        r24 = 0;
        goto L_0x0079;
    L_0x01df:
        r24 = "upgrade-key-set";
        r0 = r21;
        r1 = r24;
        r24 = r0.equals(r1);
        if (r24 == 0) goto L_0x020f;
    L_0x01ec:
        r24 = com.android.internal.R.styleable.AndroidManifestUpgradeKeySet;
        r0 = r29;
        r1 = r31;
        r2 = r24;
        r20 = r0.obtainAttributes(r1, r2);
        r24 = 0;
        r0 = r20;
        r1 = r24;
        r14 = r0.getNonResourceString(r1);
        r0 = r23;
        r0.add(r14);
        r20.recycle();
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x001a;
    L_0x020f:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Unknown element under <key-sets>: ";
        r25 = r25.append(r26);
        r26 = r30.getName();
        r25 = r25.append(r26);
        r26 = " at ";
        r25 = r25.append(r26);
        r0 = r27;
        r0 = r0.mArchiveSourcePath;
        r26 = r0;
        r25 = r25.append(r26);
        r26 = " ";
        r25 = r25.append(r26);
        r26 = r30.getPositionDescription();
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x001a;
    L_0x024e:
        r17 = r18.keySet();
        r24 = r6.keySet();
        r0 = r17;
        r1 = r24;
        r24 = r0.removeAll(r1);
        if (r24 == 0) goto L_0x028e;
    L_0x0260:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Package";
        r25 = r25.append(r26);
        r0 = r28;
        r0 = r0.packageName;
        r26 = r0;
        r25 = r25.append(r26);
        r26 = " AndroidManifext.xml ";
        r25 = r25.append(r26);
        r26 = "'key-set' and 'public-key' names must be distinct.";
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        r24 = 0;
        goto L_0x0079;
    L_0x028e:
        r24 = new android.util.ArrayMap;
        r24.<init>();
        r0 = r24;
        r1 = r28;
        r1.mKeySetMapping = r0;
        r24 = r6.entrySet();
        r9 = r24.iterator();
    L_0x02a1:
        r24 = r9.hasNext();
        if (r24 == 0) goto L_0x037e;
    L_0x02a7:
        r7 = r9.next();
        r7 = (java.util.Map.Entry) r7;
        r12 = r7.getKey();
        r12 = (java.lang.String) r12;
        r24 = r7.getValue();
        r24 = (android.util.ArraySet) r24;
        r24 = r24.size();
        if (r24 != 0) goto L_0x02fc;
    L_0x02bf:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Package";
        r25 = r25.append(r26);
        r0 = r28;
        r0 = r0.packageName;
        r26 = r0;
        r25 = r25.append(r26);
        r26 = " AndroidManifext.xml ";
        r25 = r25.append(r26);
        r26 = "'key-set' ";
        r25 = r25.append(r26);
        r0 = r25;
        r25 = r0.append(r12);
        r26 = " has no valid associated 'public-key'.";
        r25 = r25.append(r26);
        r26 = " Not including in package's defined key-sets.";
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        goto L_0x02a1;
    L_0x02fc:
        r24 = r11.contains(r12);
        if (r24 == 0) goto L_0x0340;
    L_0x0302:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Package";
        r25 = r25.append(r26);
        r0 = r28;
        r0 = r0.packageName;
        r26 = r0;
        r25 = r25.append(r26);
        r26 = " AndroidManifext.xml ";
        r25 = r25.append(r26);
        r26 = "'key-set' ";
        r25 = r25.append(r26);
        r0 = r25;
        r25 = r0.append(r12);
        r26 = " contained improper 'public-key'";
        r25 = r25.append(r26);
        r26 = " tags. Not including in package's defined key-sets.";
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        goto L_0x02a1;
    L_0x0340:
        r0 = r28;
        r0 = r0.mKeySetMapping;
        r24 = r0;
        r25 = new android.util.ArraySet;
        r25.<init>();
        r0 = r24;
        r1 = r25;
        r0.put(r12, r1);
        r24 = r7.getValue();
        r24 = (android.util.ArraySet) r24;
        r10 = r24.iterator();
    L_0x035c:
        r24 = r10.hasNext();
        if (r24 == 0) goto L_0x02a1;
    L_0x0362:
        r19 = r10.next();
        r19 = (java.lang.String) r19;
        r0 = r28;
        r0 = r0.mKeySetMapping;
        r24 = r0;
        r0 = r24;
        r24 = r0.get(r12);
        r24 = (android.util.ArraySet) r24;
        r25 = r18.get(r19);
        r24.add(r25);
        goto L_0x035c;
    L_0x037e:
        r0 = r28;
        r0 = r0.mKeySetMapping;
        r24 = r0;
        r24 = r24.keySet();
        r0 = r24;
        r1 = r23;
        r24 = r0.containsAll(r1);
        if (r24 == 0) goto L_0x039c;
    L_0x0392:
        r0 = r23;
        r1 = r28;
        r1.mUpgradeKeySets = r0;
        r24 = 1;
        goto L_0x0079;
    L_0x039c:
        r24 = "PackageParser";
        r25 = new java.lang.StringBuilder;
        r25.<init>();
        r26 = "Package";
        r25 = r25.append(r26);
        r0 = r28;
        r0 = r0.packageName;
        r26 = r0;
        r25 = r25.append(r26);
        r26 = " AndroidManifext.xml ";
        r25 = r25.append(r26);
        r26 = "does not define all 'upgrade-key-set's .";
        r25 = r25.append(r26);
        r25 = r25.toString();
        android.util.Slog.w(r24, r25);
        r24 = 0;
        goto L_0x0079;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseKeySets(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, java.lang.String[]):boolean");
    }

    private PermissionGroup parsePermissionGroup(Package owner, int flags, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        PermissionGroup perm = new PermissionGroup(owner);
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPermissionGroup);
        if (parsePackageItemInfo(owner, perm.info, outError, "<permission-group>", sa, PARSE_CHATTY, SDK_VERSION, PARSE_IS_SYSTEM, 5, 7)) {
            perm.info.descriptionRes = sa.getResourceId(PARSE_MUST_BE_APK, SDK_VERSION);
            perm.info.flags = sa.getInt(6, SDK_VERSION);
            perm.info.priority = sa.getInt(3, SDK_VERSION);
            if (perm.info.priority > 0 && (flags & PARSE_IS_SYSTEM) == 0) {
                perm.info.priority = SDK_VERSION;
            }
            sa.recycle();
            if (parseAllMetaData(res, parser, attrs, "<permission-group>", perm, outError)) {
                owner.permissionGroups.add(perm);
                return perm;
            }
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        sa.recycle();
        this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
        return null;
    }

    private Permission parsePermission(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        Permission perm = new Permission(owner);
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPermission);
        if (parsePackageItemInfo(owner, perm.info, outError, "<permission>", sa, PARSE_CHATTY, SDK_VERSION, PARSE_IS_SYSTEM, 6, PARSE_IGNORE_PROCESSES)) {
            perm.info.group = sa.getNonResourceString(PARSE_MUST_BE_APK);
            if (perm.info.group != null) {
                perm.info.group = perm.info.group.intern();
            }
            perm.info.descriptionRes = sa.getResourceId(5, SDK_VERSION);
            perm.info.protectionLevel = sa.getInt(3, SDK_VERSION);
            perm.info.flags = sa.getInt(7, SDK_VERSION);
            sa.recycle();
            if (perm.info.protectionLevel == PARSE_DEFAULT_INSTALL_LOCATION) {
                outError[SDK_VERSION] = "<permission> does not specify protectionLevel";
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                return null;
            }
            perm.info.protectionLevel = PermissionInfo.fixProtectionLevel(perm.info.protectionLevel);
            if ((perm.info.protectionLevel & LayoutParams.SOFT_INPUT_MASK_ADJUST) == 0 || (perm.info.protectionLevel & 15) == PARSE_CHATTY) {
                if (parseAllMetaData(res, parser, attrs, "<permission>", perm, outError)) {
                    owner.permissions.add(perm);
                    return perm;
                }
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                return null;
            }
            outError[SDK_VERSION] = "<permission>  protectionLevel specifies a flag but is not based on signature type";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        sa.recycle();
        this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
        return null;
    }

    private Permission parsePermissionTree(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        Permission perm = new Permission(owner);
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPermissionTree);
        if (parsePackageItemInfo(owner, perm.info, outError, "<permission-tree>", sa, PARSE_CHATTY, SDK_VERSION, PARSE_IS_SYSTEM, 3, PARSE_MUST_BE_APK)) {
            sa.recycle();
            int index = perm.info.name.indexOf(46);
            if (index > 0) {
                index = perm.info.name.indexOf(46, index + PARSE_IS_SYSTEM);
            }
            if (index < 0) {
                outError[SDK_VERSION] = "<permission-tree> name has less than three segments: " + perm.info.name;
                this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
                return null;
            }
            perm.info.descriptionRes = SDK_VERSION;
            perm.info.protectionLevel = SDK_VERSION;
            perm.tree = true;
            if (parseAllMetaData(res, parser, attrs, "<permission-tree>", perm, outError)) {
                owner.permissions.add(perm);
                return perm;
            }
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        sa.recycle();
        this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
        return null;
    }

    private Instrumentation parseInstrumentation(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestInstrumentation);
        if (this.mParseInstrumentationArgs == null) {
            this.mParseInstrumentationArgs = new ParsePackageItemArgs(owner, outError, PARSE_CHATTY, SDK_VERSION, PARSE_IS_SYSTEM, 6, 7);
            this.mParseInstrumentationArgs.tag = "<instrumentation>";
        }
        this.mParseInstrumentationArgs.sa = sa;
        Instrumentation a = new Instrumentation(this.mParseInstrumentationArgs, new InstrumentationInfo());
        if (outError[SDK_VERSION] != null) {
            sa.recycle();
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        String str = sa.getNonResourceString(3);
        a.info.targetPackage = str != null ? str.intern() : null;
        a.info.handleProfiling = sa.getBoolean(PARSE_MUST_BE_APK, RIGID_PARSER);
        a.info.functionalTest = sa.getBoolean(5, RIGID_PARSER);
        sa.recycle();
        if (a.info.targetPackage == null) {
            outError[SDK_VERSION] = "<instrumentation> does not specify targetPackage";
            this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
            return null;
        }
        if (parseAllMetaData(res, parser, attrs, "<instrumentation>", a, outError)) {
            owner.instrumentation.add(a);
            return a;
        }
        this.mParseError = PackageManager.INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean parseBaseApplication(android.content.pm.PackageParser.Package r35, android.content.res.Resources r36, org.xmlpull.v1.XmlPullParser r37, android.util.AttributeSet r38, int r39, java.lang.String[] r40) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r34 = this;
        r0 = r35;
        r0 = r0.applicationInfo;
        r16 = r0;
        r0 = r35;
        r3 = r0.applicationInfo;
        r0 = r3.packageName;
        r24 = r0;
        r3 = com.android.internal.R.styleable.AndroidManifestApplication;
        r0 = r36;
        r1 = r38;
        r29 = r0.obtainAttributes(r1, r3);
        r3 = 3;
        r4 = 0;
        r0 = r29;
        r22 = r0.getNonConfigurationString(r3, r4);
        if (r22 == 0) goto L_0x0041;
    L_0x0022:
        r0 = r24;
        r1 = r22;
        r2 = r40;
        r3 = buildClassName(r0, r1, r2);
        r0 = r16;
        r0.className = r3;
        r0 = r16;
        r3 = r0.className;
        if (r3 != 0) goto L_0x0041;
    L_0x0036:
        r29.recycle();
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
    L_0x0040:
        return r3;
    L_0x0041:
        r3 = 4;
        r4 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r29;
        r21 = r0.getNonConfigurationString(r3, r4);
        if (r21 == 0) goto L_0x005a;
    L_0x004c:
        r0 = r24;
        r1 = r21;
        r2 = r40;
        r3 = buildClassName(r0, r1, r2);
        r0 = r16;
        r0.manageSpaceActivityName = r3;
    L_0x005a:
        r3 = 17;
        r4 = 1;
        r0 = r29;
        r17 = r0.getBoolean(r3, r4);
        if (r17 == 0) goto L_0x00cd;
    L_0x0065:
        r0 = r16;
        r3 = r0.flags;
        r4 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
        r3 = 16;
        r4 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r29;
        r18 = r0.getNonConfigurationString(r3, r4);
        if (r18 == 0) goto L_0x00cd;
    L_0x007d:
        r0 = r24;
        r1 = r18;
        r2 = r40;
        r3 = buildClassName(r0, r1, r2);
        r0 = r16;
        r0.backupAgentName = r3;
        r3 = 18;
        r4 = 1;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x00a1;
    L_0x0096:
        r0 = r16;
        r3 = r0.flags;
        r4 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
    L_0x00a1:
        r3 = 21;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x00b7;
    L_0x00ac:
        r0 = r16;
        r3 = r0.flags;
        r4 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
    L_0x00b7:
        r3 = 32;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x00cd;
    L_0x00c2:
        r0 = r16;
        r3 = r0.flags;
        r4 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
    L_0x00cd:
        r3 = 1;
        r0 = r29;
        r33 = r0.peekValue(r3);
        if (r33 == 0) goto L_0x00e8;
    L_0x00d6:
        r0 = r33;
        r3 = r0.resourceId;
        r0 = r16;
        r0.labelRes = r3;
        if (r3 != 0) goto L_0x00e8;
    L_0x00e0:
        r3 = r33.coerceToString();
        r0 = r16;
        r0.nonLocalizedLabel = r3;
    L_0x00e8:
        r3 = 2;
        r4 = 0;
        r0 = r29;
        r3 = r0.getResourceId(r3, r4);
        r0 = r16;
        r0.icon = r3;
        r3 = 22;
        r4 = 0;
        r0 = r29;
        r3 = r0.getResourceId(r3, r4);
        r0 = r16;
        r0.logo = r3;
        r3 = 30;
        r4 = 0;
        r0 = r29;
        r3 = r0.getResourceId(r3, r4);
        r0 = r16;
        r0.banner = r3;
        r3 = 0;
        r4 = 0;
        r0 = r29;
        r3 = r0.getResourceId(r3, r4);
        r0 = r16;
        r0.theme = r3;
        r3 = 13;
        r4 = 0;
        r0 = r29;
        r3 = r0.getResourceId(r3, r4);
        r0 = r16;
        r0.descriptionRes = r3;
        r3 = r39 & 1;
        if (r3 == 0) goto L_0x0140;
    L_0x012b:
        r3 = 8;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x0140;
    L_0x0136:
        r0 = r16;
        r3 = r0.flags;
        r3 = r3 | 8;
        r0 = r16;
        r0.flags = r3;
    L_0x0140:
        r3 = 27;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x0150;
    L_0x014b:
        r3 = 1;
        r0 = r35;
        r0.mRequiredForAllUsers = r3;
    L_0x0150:
        r3 = 28;
        r0 = r29;
        r27 = r0.getString(r3);
        if (r27 == 0) goto L_0x0166;
    L_0x015a:
        r3 = r27.length();
        if (r3 <= 0) goto L_0x0166;
    L_0x0160:
        r0 = r27;
        r1 = r35;
        r1.mRestrictedAccountType = r0;
    L_0x0166:
        r3 = 29;
        r0 = r29;
        r26 = r0.getString(r3);
        if (r26 == 0) goto L_0x017c;
    L_0x0170:
        r3 = r26.length();
        if (r3 <= 0) goto L_0x017c;
    L_0x0176:
        r0 = r26;
        r1 = r35;
        r1.mRequiredAccountType = r0;
    L_0x017c:
        r3 = 10;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x0191;
    L_0x0187:
        r0 = r16;
        r3 = r0.flags;
        r3 = r3 | 2;
        r0 = r16;
        r0.flags = r3;
    L_0x0191:
        r3 = 20;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x01a6;
    L_0x019c:
        r0 = r16;
        r3 = r0.flags;
        r3 = r3 | 16384;
        r0 = r16;
        r0.flags = r3;
    L_0x01a6:
        r4 = 23;
        r0 = r35;
        r3 = r0.applicationInfo;
        r3 = r3.targetSdkVersion;
        r6 = 14;
        if (r3 < r6) goto L_0x0300;
    L_0x01b2:
        r3 = 1;
    L_0x01b3:
        r0 = r29;
        r3 = r0.getBoolean(r4, r3);
        r0 = r35;
        r0.baseHardwareAccelerated = r3;
        r3 = 7;
        r4 = 1;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x01d1;
    L_0x01c7:
        r0 = r16;
        r3 = r0.flags;
        r3 = r3 | 4;
        r0 = r16;
        r0.flags = r3;
    L_0x01d1:
        r3 = 14;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x01e6;
    L_0x01dc:
        r0 = r16;
        r3 = r0.flags;
        r3 = r3 | 32;
        r0 = r16;
        r0.flags = r3;
    L_0x01e6:
        r3 = 5;
        r4 = 1;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x01fa;
    L_0x01f0:
        r0 = r16;
        r3 = r0.flags;
        r3 = r3 | 64;
        r0 = r16;
        r0.flags = r3;
    L_0x01fa:
        r3 = 15;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x020f;
    L_0x0205:
        r0 = r16;
        r3 = r0.flags;
        r3 = r3 | 256;
        r0 = r16;
        r0.flags = r3;
    L_0x020f:
        r3 = 24;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x0225;
    L_0x021a:
        r0 = r16;
        r3 = r0.flags;
        r4 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
    L_0x0225:
        r3 = 26;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x023b;
    L_0x0230:
        r0 = r16;
        r3 = r0.flags;
        r4 = 4194304; // 0x400000 float:5.877472E-39 double:2.0722615E-317;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
    L_0x023b:
        r3 = 33;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x0251;
    L_0x0246:
        r0 = r16;
        r3 = r0.flags;
        r4 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
    L_0x0251:
        r3 = 6;
        r4 = 0;
        r0 = r29;
        r30 = r0.getNonConfigurationString(r3, r4);
        if (r30 == 0) goto L_0x0303;
    L_0x025b:
        r3 = r30.length();
        if (r3 <= 0) goto L_0x0303;
    L_0x0261:
        r3 = r30.intern();
    L_0x0265:
        r0 = r16;
        r0.permission = r3;
        r0 = r35;
        r3 = r0.applicationInfo;
        r3 = r3.targetSdkVersion;
        r4 = 8;
        if (r3 < r4) goto L_0x0306;
    L_0x0273:
        r3 = 12;
        r4 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r29;
        r30 = r0.getNonConfigurationString(r3, r4);
    L_0x027d:
        r0 = r16;
        r3 = r0.packageName;
        r0 = r16;
        r4 = r0.packageName;
        r0 = r30;
        r1 = r40;
        r3 = buildTaskAffinityName(r3, r4, r0, r1);
        r0 = r16;
        r0.taskAffinity = r3;
        r3 = 0;
        r3 = r40[r3];
        if (r3 != 0) goto L_0x02e2;
    L_0x0296:
        r0 = r35;
        r3 = r0.applicationInfo;
        r3 = r3.targetSdkVersion;
        r4 = 8;
        if (r3 < r4) goto L_0x0310;
    L_0x02a0:
        r3 = 11;
        r4 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r29;
        r5 = r0.getNonConfigurationString(r3, r4);
    L_0x02aa:
        r0 = r16;
        r3 = r0.packageName;
        r4 = 0;
        r0 = r34;
        r7 = r0.mSeparateProcesses;
        r6 = r39;
        r8 = r40;
        r3 = buildProcessName(r3, r4, r5, r6, r7, r8);
        r0 = r16;
        r0.processName = r3;
        r3 = 9;
        r4 = 1;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        r0 = r16;
        r0.enabled = r3;
        r3 = 31;
        r4 = 0;
        r0 = r29;
        r3 = r0.getBoolean(r3, r4);
        if (r3 == 0) goto L_0x02e2;
    L_0x02d7:
        r0 = r16;
        r3 = r0.flags;
        r4 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r3 = r3 | r4;
        r0 = r16;
        r0.flags = r3;
    L_0x02e2:
        r3 = 25;
        r4 = 0;
        r0 = r29;
        r3 = r0.getInt(r3, r4);
        r0 = r16;
        r0.uiOptions = r3;
        r29.recycle();
        r3 = 0;
        r3 = r40[r3];
        if (r3 == 0) goto L_0x0319;
    L_0x02f7:
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
        goto L_0x0040;
    L_0x0300:
        r3 = 0;
        goto L_0x01b3;
    L_0x0303:
        r3 = 0;
        goto L_0x0265;
    L_0x0306:
        r3 = 12;
        r0 = r29;
        r30 = r0.getNonResourceString(r3);
        goto L_0x027d;
    L_0x0310:
        r3 = 11;
        r0 = r29;
        r5 = r0.getNonResourceString(r3);
        goto L_0x02aa;
    L_0x0319:
        r19 = r37.getDepth();
    L_0x031d:
        r32 = r37.next();
        r3 = 1;
        r0 = r32;
        if (r0 == r3) goto L_0x0521;
    L_0x0326:
        r3 = 3;
        r0 = r32;
        if (r0 != r3) goto L_0x0333;
    L_0x032b:
        r3 = r37.getDepth();
        r0 = r19;
        if (r3 <= r0) goto L_0x0521;
    L_0x0333:
        r3 = 3;
        r0 = r32;
        if (r0 == r3) goto L_0x031d;
    L_0x0338:
        r3 = 4;
        r0 = r32;
        if (r0 == r3) goto L_0x031d;
    L_0x033d:
        r31 = r37.getName();
        r3 = "activity";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x0375;
    L_0x034b:
        r13 = 0;
        r0 = r35;
        r14 = r0.baseHardwareAccelerated;
        r6 = r34;
        r7 = r35;
        r8 = r36;
        r9 = r37;
        r10 = r38;
        r11 = r39;
        r12 = r40;
        r15 = r6.parseActivity(r7, r8, r9, r10, r11, r12, r13, r14);
        if (r15 != 0) goto L_0x036d;
    L_0x0364:
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
        goto L_0x0040;
    L_0x036d:
        r0 = r35;
        r3 = r0.activities;
        r3.add(r15);
        goto L_0x031d;
    L_0x0375:
        r3 = "receiver";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x03a8;
    L_0x0380:
        r13 = 1;
        r14 = 0;
        r6 = r34;
        r7 = r35;
        r8 = r36;
        r9 = r37;
        r10 = r38;
        r11 = r39;
        r12 = r40;
        r15 = r6.parseActivity(r7, r8, r9, r10, r11, r12, r13, r14);
        if (r15 != 0) goto L_0x039f;
    L_0x0396:
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
        goto L_0x0040;
    L_0x039f:
        r0 = r35;
        r3 = r0.receivers;
        r3.add(r15);
        goto L_0x031d;
    L_0x03a8:
        r3 = "service";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x03cd;
    L_0x03b3:
        r28 = r34.parseService(r35, r36, r37, r38, r39, r40);
        if (r28 != 0) goto L_0x03c2;
    L_0x03b9:
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
        goto L_0x0040;
    L_0x03c2:
        r0 = r35;
        r3 = r0.services;
        r0 = r28;
        r3.add(r0);
        goto L_0x031d;
    L_0x03cd:
        r3 = "provider";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x03f2;
    L_0x03d8:
        r23 = r34.parseProvider(r35, r36, r37, r38, r39, r40);
        if (r23 != 0) goto L_0x03e7;
    L_0x03de:
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
        goto L_0x0040;
    L_0x03e7:
        r0 = r35;
        r3 = r0.providers;
        r0 = r23;
        r3.add(r0);
        goto L_0x031d;
    L_0x03f2:
        r3 = "activity-alias";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x0414;
    L_0x03fc:
        r15 = r34.parseActivityAlias(r35, r36, r37, r38, r39, r40);
        if (r15 != 0) goto L_0x040b;
    L_0x0402:
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
        goto L_0x0040;
    L_0x040b:
        r0 = r35;
        r3 = r0.activities;
        r3.add(r15);
        goto L_0x031d;
    L_0x0414:
        r3 = r37.getName();
        r4 = "meta-data";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0442;
    L_0x0421:
        r0 = r35;
        r10 = r0.mAppMetaData;
        r6 = r34;
        r7 = r36;
        r8 = r37;
        r9 = r38;
        r11 = r40;
        r3 = r6.parseMetaData(r7, r8, r9, r10, r11);
        r0 = r35;
        r0.mAppMetaData = r3;
        if (r3 != 0) goto L_0x031d;
    L_0x0439:
        r3 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r34;
        r0.mParseError = r3;
        r3 = 0;
        goto L_0x0040;
    L_0x0442:
        r3 = "library";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x0485;
    L_0x044c:
        r3 = com.android.internal.R.styleable.AndroidManifestLibrary;
        r0 = r36;
        r1 = r38;
        r29 = r0.obtainAttributes(r1, r3);
        r3 = 0;
        r0 = r29;
        r20 = r0.getNonResourceString(r3);
        r29.recycle();
        if (r20 == 0) goto L_0x0480;
    L_0x0462:
        r20 = r20.intern();
        r0 = r35;
        r3 = r0.libraryNames;
        r0 = r20;
        r3 = com.android.internal.util.ArrayUtils.contains(r3, r0);
        if (r3 != 0) goto L_0x0480;
    L_0x0472:
        r0 = r35;
        r3 = r0.libraryNames;
        r0 = r20;
        r3 = com.android.internal.util.ArrayUtils.add(r3, r0);
        r0 = r35;
        r0.libraryNames = r3;
    L_0x0480:
        com.android.internal.util.XmlUtils.skipCurrentTag(r37);
        goto L_0x031d;
    L_0x0485:
        r3 = "uses-library";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x04d6;
    L_0x0490:
        r3 = com.android.internal.R.styleable.AndroidManifestUsesLibrary;
        r0 = r36;
        r1 = r38;
        r29 = r0.obtainAttributes(r1, r3);
        r3 = 0;
        r0 = r29;
        r20 = r0.getNonResourceString(r3);
        r3 = 1;
        r4 = 1;
        r0 = r29;
        r25 = r0.getBoolean(r3, r4);
        r29.recycle();
        if (r20 == 0) goto L_0x04c2;
    L_0x04ae:
        r20 = r20.intern();
        if (r25 == 0) goto L_0x04c7;
    L_0x04b4:
        r0 = r35;
        r3 = r0.usesLibraries;
        r0 = r20;
        r3 = com.android.internal.util.ArrayUtils.add(r3, r0);
        r0 = r35;
        r0.usesLibraries = r3;
    L_0x04c2:
        com.android.internal.util.XmlUtils.skipCurrentTag(r37);
        goto L_0x031d;
    L_0x04c7:
        r0 = r35;
        r3 = r0.usesOptionalLibraries;
        r0 = r20;
        r3 = com.android.internal.util.ArrayUtils.add(r3, r0);
        r0 = r35;
        r0.usesOptionalLibraries = r3;
        goto L_0x04c2;
    L_0x04d6:
        r3 = "uses-package";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x04e6;
    L_0x04e1:
        com.android.internal.util.XmlUtils.skipCurrentTag(r37);
        goto L_0x031d;
    L_0x04e6:
        r3 = "PackageParser";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "Unknown element under <application>: ";
        r4 = r4.append(r6);
        r0 = r31;
        r4 = r4.append(r0);
        r6 = " at ";
        r4 = r4.append(r6);
        r0 = r34;
        r6 = r0.mArchiveSourcePath;
        r4 = r4.append(r6);
        r6 = " ";
        r4 = r4.append(r6);
        r6 = r37.getPositionDescription();
        r4 = r4.append(r6);
        r4 = r4.toString();
        android.util.Slog.w(r3, r4);
        com.android.internal.util.XmlUtils.skipCurrentTag(r37);
        goto L_0x031d;
    L_0x0521:
        r3 = 1;
        goto L_0x0040;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseBaseApplication(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean parseSplitApplication(android.content.pm.PackageParser.Package r21, android.content.res.Resources r22, org.xmlpull.v1.XmlPullParser r23, android.util.AttributeSet r24, int r25, int r26, java.lang.String[] r27) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r20 = this;
        r2 = com.android.internal.R.styleable.AndroidManifestApplication;
        r0 = r22;
        r1 = r24;
        r17 = r0.obtainAttributes(r1, r2);
        r2 = 7;
        r3 = 1;
        r0 = r17;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x001e;
    L_0x0014:
        r0 = r21;
        r2 = r0.splitFlags;
        r3 = r2[r26];
        r3 = r3 | 4;
        r2[r26] = r3;
    L_0x001e:
        r12 = r23.getDepth();
    L_0x0022:
        r19 = r23.next();
        r2 = 1;
        r0 = r19;
        if (r0 == r2) goto L_0x0218;
    L_0x002b:
        r2 = 3;
        r0 = r19;
        if (r0 != r2) goto L_0x0036;
    L_0x0030:
        r2 = r23.getDepth();
        if (r2 <= r12) goto L_0x0218;
    L_0x0036:
        r2 = 3;
        r0 = r19;
        if (r0 == r2) goto L_0x0022;
    L_0x003b:
        r2 = 4;
        r0 = r19;
        if (r0 == r2) goto L_0x0022;
    L_0x0040:
        r18 = r23.getName();
        r2 = "activity";
        r0 = r18;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0077;
    L_0x004e:
        r9 = 0;
        r0 = r21;
        r10 = r0.baseHardwareAccelerated;
        r2 = r20;
        r3 = r21;
        r4 = r22;
        r5 = r23;
        r6 = r24;
        r7 = r25;
        r8 = r27;
        r11 = r2.parseActivity(r3, r4, r5, r6, r7, r8, r9, r10);
        if (r11 != 0) goto L_0x006f;
    L_0x0067:
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r20;
        r0.mParseError = r2;
        r2 = 0;
    L_0x006e:
        return r2;
    L_0x006f:
        r0 = r21;
        r2 = r0.activities;
        r2.add(r11);
        goto L_0x0022;
    L_0x0077:
        r2 = "receiver";
        r0 = r18;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x00a9;
    L_0x0082:
        r9 = 1;
        r10 = 0;
        r2 = r20;
        r3 = r21;
        r4 = r22;
        r5 = r23;
        r6 = r24;
        r7 = r25;
        r8 = r27;
        r11 = r2.parseActivity(r3, r4, r5, r6, r7, r8, r9, r10);
        if (r11 != 0) goto L_0x00a0;
    L_0x0098:
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r20;
        r0.mParseError = r2;
        r2 = 0;
        goto L_0x006e;
    L_0x00a0:
        r0 = r21;
        r2 = r0.receivers;
        r2.add(r11);
        goto L_0x0022;
    L_0x00a9:
        r2 = "service";
        r0 = r18;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x00db;
    L_0x00b4:
        r2 = r20;
        r3 = r21;
        r4 = r22;
        r5 = r23;
        r6 = r24;
        r7 = r25;
        r8 = r27;
        r16 = r2.parseService(r3, r4, r5, r6, r7, r8);
        if (r16 != 0) goto L_0x00d0;
    L_0x00c8:
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r20;
        r0.mParseError = r2;
        r2 = 0;
        goto L_0x006e;
    L_0x00d0:
        r0 = r21;
        r2 = r0.services;
        r0 = r16;
        r2.add(r0);
        goto L_0x0022;
    L_0x00db:
        r2 = "provider";
        r0 = r18;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x010c;
    L_0x00e6:
        r2 = r20;
        r3 = r21;
        r4 = r22;
        r5 = r23;
        r6 = r24;
        r7 = r25;
        r8 = r27;
        r14 = r2.parseProvider(r3, r4, r5, r6, r7, r8);
        if (r14 != 0) goto L_0x0103;
    L_0x00fa:
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r20;
        r0.mParseError = r2;
        r2 = 0;
        goto L_0x006e;
    L_0x0103:
        r0 = r21;
        r2 = r0.providers;
        r2.add(r14);
        goto L_0x0022;
    L_0x010c:
        r2 = "activity-alias";
        r0 = r18;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x013c;
    L_0x0116:
        r2 = r20;
        r3 = r21;
        r4 = r22;
        r5 = r23;
        r6 = r24;
        r7 = r25;
        r8 = r27;
        r11 = r2.parseActivityAlias(r3, r4, r5, r6, r7, r8);
        if (r11 != 0) goto L_0x0133;
    L_0x012a:
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r20;
        r0.mParseError = r2;
        r2 = 0;
        goto L_0x006e;
    L_0x0133:
        r0 = r21;
        r2 = r0.activities;
        r2.add(r11);
        goto L_0x0022;
    L_0x013c:
        r2 = r23.getName();
        r3 = "meta-data";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x016a;
    L_0x0149:
        r0 = r21;
        r6 = r0.mAppMetaData;
        r2 = r20;
        r3 = r22;
        r4 = r23;
        r5 = r24;
        r7 = r27;
        r2 = r2.parseMetaData(r3, r4, r5, r6, r7);
        r0 = r21;
        r0.mAppMetaData = r2;
        if (r2 != 0) goto L_0x0022;
    L_0x0161:
        r2 = -108; // 0xffffffffffffff94 float:NaN double:NaN;
        r0 = r20;
        r0.mParseError = r2;
        r2 = 0;
        goto L_0x006e;
    L_0x016a:
        r2 = "uses-library";
        r0 = r18;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x01cd;
    L_0x0175:
        r2 = com.android.internal.R.styleable.AndroidManifestUsesLibrary;
        r0 = r22;
        r1 = r24;
        r17 = r0.obtainAttributes(r1, r2);
        r2 = 0;
        r0 = r17;
        r13 = r0.getNonResourceString(r2);
        r2 = 1;
        r3 = 1;
        r0 = r17;
        r15 = r0.getBoolean(r2, r3);
        r17.recycle();
        if (r13 == 0) goto L_0x01b1;
    L_0x0193:
        r13 = r13.intern();
        if (r15 == 0) goto L_0x01b6;
    L_0x0199:
        r0 = r21;
        r2 = r0.usesLibraries;
        r2 = com.android.internal.util.ArrayUtils.add(r2, r13);
        r0 = r21;
        r0.usesLibraries = r2;
        r0 = r21;
        r2 = r0.usesOptionalLibraries;
        r2 = com.android.internal.util.ArrayUtils.remove(r2, r13);
        r0 = r21;
        r0.usesOptionalLibraries = r2;
    L_0x01b1:
        com.android.internal.util.XmlUtils.skipCurrentTag(r23);
        goto L_0x0022;
    L_0x01b6:
        r0 = r21;
        r2 = r0.usesLibraries;
        r2 = com.android.internal.util.ArrayUtils.contains(r2, r13);
        if (r2 != 0) goto L_0x01b1;
    L_0x01c0:
        r0 = r21;
        r2 = r0.usesOptionalLibraries;
        r2 = com.android.internal.util.ArrayUtils.add(r2, r13);
        r0 = r21;
        r0.usesOptionalLibraries = r2;
        goto L_0x01b1;
    L_0x01cd:
        r2 = "uses-package";
        r0 = r18;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x01dd;
    L_0x01d8:
        com.android.internal.util.XmlUtils.skipCurrentTag(r23);
        goto L_0x0022;
    L_0x01dd:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unknown element under <application>: ";
        r3 = r3.append(r4);
        r0 = r18;
        r3 = r3.append(r0);
        r4 = " at ";
        r3 = r3.append(r4);
        r0 = r20;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r23.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        com.android.internal.util.XmlUtils.skipCurrentTag(r23);
        goto L_0x0022;
    L_0x0218:
        r2 = 1;
        goto L_0x006e;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseSplitApplication(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, int, java.lang.String[]):boolean");
    }

    private boolean parsePackageItemInfo(Package owner, PackageItemInfo outInfo, String[] outError, String tag, TypedArray sa, int nameRes, int labelRes, int iconRes, int logoRes, int bannerRes) {
        String name = sa.getNonConfigurationString(nameRes, SDK_VERSION);
        if (name == null) {
            outError[SDK_VERSION] = tag + " does not specify android:name";
            return RIGID_PARSER;
        }
        outInfo.name = buildClassName(owner.applicationInfo.packageName, name, outError);
        if (outInfo.name == null) {
            return RIGID_PARSER;
        }
        int iconVal = sa.getResourceId(iconRes, SDK_VERSION);
        if (iconVal != 0) {
            outInfo.icon = iconVal;
            outInfo.nonLocalizedLabel = null;
        }
        int logoVal = sa.getResourceId(logoRes, SDK_VERSION);
        if (logoVal != 0) {
            outInfo.logo = logoVal;
        }
        int bannerVal = sa.getResourceId(bannerRes, SDK_VERSION);
        if (bannerVal != 0) {
            outInfo.banner = bannerVal;
        }
        TypedValue v = sa.peekValue(labelRes);
        if (v != null) {
            int i = v.resourceId;
            outInfo.labelRes = i;
            if (i == 0) {
                outInfo.nonLocalizedLabel = v.coerceToString();
            }
        }
        outInfo.packageName = owner.packageName;
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.content.pm.PackageParser.Activity parseActivity(android.content.pm.PackageParser.Package r23, android.content.res.Resources r24, org.xmlpull.v1.XmlPullParser r25, android.util.AttributeSet r26, int r27, java.lang.String[] r28, boolean r29, boolean r30) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r22 = this;
        r2 = com.android.internal.R.styleable.AndroidManifestActivity;
        r0 = r24;
        r1 = r26;
        r18 = r0.obtainAttributes(r1, r2);
        r0 = r22;
        r2 = r0.mParseActivityArgs;
        if (r2 != 0) goto L_0x002c;
    L_0x0010:
        r2 = new android.content.pm.PackageParser$ParseComponentArgs;
        r5 = 3;
        r6 = 1;
        r7 = 2;
        r8 = 23;
        r9 = 30;
        r0 = r22;
        r10 = r0.mSeparateProcesses;
        r11 = 7;
        r12 = 17;
        r13 = 5;
        r3 = r23;
        r4 = r28;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r0 = r22;
        r0.mParseActivityArgs = r2;
    L_0x002c:
        r0 = r22;
        r3 = r0.mParseActivityArgs;
        if (r29 == 0) goto L_0x005e;
    L_0x0032:
        r2 = "<receiver>";
    L_0x0034:
        r3.tag = r2;
        r0 = r22;
        r2 = r0.mParseActivityArgs;
        r0 = r18;
        r2.sa = r0;
        r0 = r22;
        r2 = r0.mParseActivityArgs;
        r0 = r27;
        r2.flags = r0;
        r14 = new android.content.pm.PackageParser$Activity;
        r0 = r22;
        r2 = r0.mParseActivityArgs;
        r3 = new android.content.pm.ActivityInfo;
        r3.<init>();
        r14.<init>(r2, r3);
        r2 = 0;
        r2 = r28[r2];
        if (r2 == 0) goto L_0x0061;
    L_0x0059:
        r18.recycle();
        r14 = 0;
    L_0x005d:
        return r14;
    L_0x005e:
        r2 = "<activity>";
        goto L_0x0034;
    L_0x0061:
        r2 = 6;
        r0 = r18;
        r19 = r0.hasValue(r2);
        if (r19 == 0) goto L_0x0076;
    L_0x006a:
        r2 = r14.info;
        r3 = 6;
        r4 = 0;
        r0 = r18;
        r3 = r0.getBoolean(r3, r4);
        r2.exported = r3;
    L_0x0076:
        r2 = r14.info;
        r3 = 0;
        r4 = 0;
        r0 = r18;
        r3 = r0.getResourceId(r3, r4);
        r2.theme = r3;
        r2 = r14.info;
        r3 = 26;
        r4 = r14.info;
        r4 = r4.applicationInfo;
        r4 = r4.uiOptions;
        r0 = r18;
        r3 = r0.getInt(r3, r4);
        r2.uiOptions = r3;
        r2 = 27;
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r18;
        r17 = r0.getNonConfigurationString(r2, r3);
        if (r17 == 0) goto L_0x00b7;
    L_0x00a0:
        r2 = r14.info;
        r2 = r2.packageName;
        r0 = r17;
        r1 = r28;
        r16 = buildClassName(r2, r0, r1);
        r2 = 0;
        r2 = r28[r2];
        if (r2 != 0) goto L_0x0328;
    L_0x00b1:
        r2 = r14.info;
        r0 = r16;
        r2.parentActivityName = r0;
    L_0x00b7:
        r2 = 4;
        r3 = 0;
        r0 = r18;
        r20 = r0.getNonConfigurationString(r2, r3);
        if (r20 != 0) goto L_0x0356;
    L_0x00c1:
        r2 = r14.info;
        r0 = r23;
        r3 = r0.applicationInfo;
        r3 = r3.permission;
        r2.permission = r3;
    L_0x00cb:
        r2 = 8;
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r18;
        r20 = r0.getNonConfigurationString(r2, r3);
        r2 = r14.info;
        r0 = r23;
        r3 = r0.applicationInfo;
        r3 = r3.packageName;
        r0 = r23;
        r4 = r0.applicationInfo;
        r4 = r4.taskAffinity;
        r0 = r20;
        r1 = r28;
        r3 = buildTaskAffinityName(r3, r4, r0, r1);
        r2.taskAffinity = r3;
        r2 = r14.info;
        r3 = 0;
        r2.flags = r3;
        r2 = 9;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0105;
    L_0x00fd:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 1;
        r2.flags = r3;
    L_0x0105:
        r2 = 10;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0118;
    L_0x0110:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 2;
        r2.flags = r3;
    L_0x0118:
        r2 = 11;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x012b;
    L_0x0123:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 4;
        r2.flags = r3;
    L_0x012b:
        r2 = 21;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x013e;
    L_0x0136:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 128;
        r2.flags = r3;
    L_0x013e:
        r2 = 18;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0151;
    L_0x0149:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 8;
        r2.flags = r3;
    L_0x0151:
        r2 = 12;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0164;
    L_0x015c:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 16;
        r2.flags = r3;
    L_0x0164:
        r2 = 13;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0177;
    L_0x016f:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 32;
        r2.flags = r3;
    L_0x0177:
        r3 = 19;
        r0 = r23;
        r2 = r0.applicationInfo;
        r2 = r2.flags;
        r2 = r2 & 32;
        if (r2 == 0) goto L_0x036c;
    L_0x0183:
        r2 = 1;
    L_0x0184:
        r0 = r18;
        r2 = r0.getBoolean(r3, r2);
        if (r2 == 0) goto L_0x0194;
    L_0x018c:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 64;
        r2.flags = r3;
    L_0x0194:
        r2 = 22;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x01a7;
    L_0x019f:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 256;
        r2.flags = r3;
    L_0x01a7:
        r2 = 29;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x01ba;
    L_0x01b2:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 1024;
        r2.flags = r3;
    L_0x01ba:
        r2 = 24;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x01cd;
    L_0x01c5:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 2048;
        r2.flags = r3;
    L_0x01cd:
        if (r29 != 0) goto L_0x036f;
    L_0x01cf:
        r2 = 25;
        r0 = r18;
        r1 = r30;
        r2 = r0.getBoolean(r2, r1);
        if (r2 == 0) goto L_0x01e3;
    L_0x01db:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 512;
        r2.flags = r3;
    L_0x01e3:
        r2 = r14.info;
        r3 = 14;
        r4 = 0;
        r0 = r18;
        r3 = r0.getInt(r3, r4);
        r2.launchMode = r3;
        r2 = r14.info;
        r3 = 33;
        r4 = 0;
        r0 = r18;
        r3 = r0.getInt(r3, r4);
        r2.documentLaunchMode = r3;
        r2 = r14.info;
        r3 = 34;
        r4 = android.app.ActivityManager.getDefaultAppRecentsLimitStatic();
        r0 = r18;
        r3 = r0.getInt(r3, r4);
        r2.maxRecents = r3;
        r2 = r14.info;
        r3 = 15;
        r4 = -1;
        r0 = r18;
        r3 = r0.getInt(r3, r4);
        r2.screenOrientation = r3;
        r2 = r14.info;
        r3 = 16;
        r4 = 0;
        r0 = r18;
        r3 = r0.getInt(r3, r4);
        r2.configChanges = r3;
        r2 = r14.info;
        r3 = 20;
        r4 = 0;
        r0 = r18;
        r3 = r0.getInt(r3, r4);
        r2.softInputMode = r3;
        r2 = r14.info;
        r3 = 32;
        r4 = 0;
        r0 = r18;
        r3 = r0.getInteger(r3, r4);
        r2.persistableMode = r3;
        r2 = 31;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0255;
    L_0x024c:
        r2 = r14.info;
        r3 = r2.flags;
        r4 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r3 = r3 | r4;
        r2.flags = r3;
    L_0x0255:
        r2 = 35;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0268;
    L_0x0260:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 8192;
        r2.flags = r3;
    L_0x0268:
        r2 = 36;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x027b;
    L_0x0273:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 4096;
        r2.flags = r3;
    L_0x027b:
        r2 = 37;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x028e;
    L_0x0286:
        r2 = r14.info;
        r3 = r2.flags;
        r3 = r3 | 16384;
        r2.flags = r3;
    L_0x028e:
        if (r29 == 0) goto L_0x0301;
    L_0x0290:
        r2 = 28;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x02ed;
    L_0x029b:
        r2 = r14.info;
        r3 = r2.flags;
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = r3 | r4;
        r2.flags = r3;
        r2 = r14.info;
        r2 = r2.exported;
        if (r2 == 0) goto L_0x02ed;
    L_0x02aa:
        r0 = r27;
        r2 = r0 & 128;
        if (r2 != 0) goto L_0x02ed;
    L_0x02b0:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Activity exported request ignored due to singleUser: ";
        r3 = r3.append(r4);
        r4 = r14.className;
        r3 = r3.append(r4);
        r4 = " at ";
        r3 = r3.append(r4);
        r0 = r22;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r25.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        r2 = r14.info;
        r3 = 0;
        r2.exported = r3;
        r19 = 1;
    L_0x02ed:
        r2 = 38;
        r3 = 0;
        r0 = r18;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x0301;
    L_0x02f8:
        r2 = r14.info;
        r3 = r2.flags;
        r4 = 536870912; // 0x20000000 float:1.0842022E-19 double:2.652494739E-315;
        r3 = r3 | r4;
        r2.flags = r3;
    L_0x0301:
        r18.recycle();
        if (r29 == 0) goto L_0x0320;
    L_0x0306:
        r0 = r23;
        r2 = r0.applicationInfo;
        r2 = r2.flags;
        r3 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r2 = r2 & r3;
        if (r2 == 0) goto L_0x0320;
    L_0x0311:
        r2 = r14.info;
        r2 = r2.processName;
        r0 = r23;
        r3 = r0.packageName;
        if (r2 != r3) goto L_0x0320;
    L_0x031b:
        r2 = 0;
        r3 = "Heavy-weight applications can not have receivers in main process";
        r28[r2] = r3;
    L_0x0320:
        r2 = 0;
        r2 = r28[r2];
        if (r2 == 0) goto L_0x037b;
    L_0x0325:
        r14 = 0;
        goto L_0x005d;
    L_0x0328:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Activity ";
        r3 = r3.append(r4);
        r4 = r14.info;
        r4 = r4.name;
        r3 = r3.append(r4);
        r4 = " specified invalid parentActivityName ";
        r3 = r3.append(r4);
        r0 = r17;
        r3 = r3.append(r0);
        r3 = r3.toString();
        android.util.Log.e(r2, r3);
        r2 = 0;
        r3 = 0;
        r28[r2] = r3;
        goto L_0x00b7;
    L_0x0356:
        r3 = r14.info;
        r2 = r20.length();
        if (r2 <= 0) goto L_0x036a;
    L_0x035e:
        r2 = r20.toString();
        r2 = r2.intern();
    L_0x0366:
        r3.permission = r2;
        goto L_0x00cb;
    L_0x036a:
        r2 = 0;
        goto L_0x0366;
    L_0x036c:
        r2 = 0;
        goto L_0x0184;
    L_0x036f:
        r2 = r14.info;
        r3 = 0;
        r2.launchMode = r3;
        r2 = r14.info;
        r3 = 0;
        r2.configChanges = r3;
        goto L_0x028e;
    L_0x037b:
        r15 = r25.getDepth();
    L_0x037f:
        r21 = r25.next();
        r2 = 1;
        r0 = r21;
        if (r0 == r2) goto L_0x0529;
    L_0x0388:
        r2 = 3;
        r0 = r21;
        if (r0 != r2) goto L_0x0393;
    L_0x038d:
        r2 = r25.getDepth();
        if (r2 <= r15) goto L_0x0529;
    L_0x0393:
        r2 = 3;
        r0 = r21;
        if (r0 == r2) goto L_0x037f;
    L_0x0398:
        r2 = 4;
        r0 = r21;
        if (r0 == r2) goto L_0x037f;
    L_0x039d:
        r2 = r25.getName();
        r3 = "intent-filter";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x03f9;
    L_0x03a9:
        r7 = new android.content.pm.PackageParser$ActivityIntentInfo;
        r7.<init>(r14);
        r6 = 1;
        r2 = r22;
        r3 = r24;
        r4 = r25;
        r5 = r26;
        r8 = r28;
        r2 = r2.parseIntent(r3, r4, r5, r6, r7, r8);
        if (r2 != 0) goto L_0x03c2;
    L_0x03bf:
        r14 = 0;
        goto L_0x005d;
    L_0x03c2:
        r2 = r7.countActions();
        if (r2 != 0) goto L_0x03f3;
    L_0x03c8:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "No actions in intent filter at ";
        r3 = r3.append(r4);
        r0 = r22;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r25.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        goto L_0x037f;
    L_0x03f3:
        r2 = r14.intents;
        r2.add(r7);
        goto L_0x037f;
    L_0x03f9:
        if (r29 != 0) goto L_0x046b;
    L_0x03fb:
        r2 = r25.getName();
        r3 = "preferred";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x046b;
    L_0x0408:
        r7 = new android.content.pm.PackageParser$ActivityIntentInfo;
        r7.<init>(r14);
        r6 = 0;
        r2 = r22;
        r3 = r24;
        r4 = r25;
        r5 = r26;
        r8 = r28;
        r2 = r2.parseIntent(r3, r4, r5, r6, r7, r8);
        if (r2 != 0) goto L_0x0421;
    L_0x041e:
        r14 = 0;
        goto L_0x005d;
    L_0x0421:
        r2 = r7.countActions();
        if (r2 != 0) goto L_0x0453;
    L_0x0427:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "No actions in preferred at ";
        r3 = r3.append(r4);
        r0 = r22;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r25.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        goto L_0x037f;
    L_0x0453:
        r0 = r23;
        r2 = r0.preferredActivityFilters;
        if (r2 != 0) goto L_0x0462;
    L_0x0459:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0 = r23;
        r0.preferredActivityFilters = r2;
    L_0x0462:
        r0 = r23;
        r2 = r0.preferredActivityFilters;
        r2.add(r7);
        goto L_0x037f;
    L_0x046b:
        r2 = r25.getName();
        r3 = "meta-data";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x048f;
    L_0x0478:
        r12 = r14.metaData;
        r8 = r22;
        r9 = r24;
        r10 = r25;
        r11 = r26;
        r13 = r28;
        r2 = r8.parseMetaData(r9, r10, r11, r12, r13);
        r14.metaData = r2;
        if (r2 != 0) goto L_0x037f;
    L_0x048c:
        r14 = 0;
        goto L_0x005d;
    L_0x048f:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Problem in package ";
        r3 = r3.append(r4);
        r0 = r22;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = ":";
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        if (r29 == 0) goto L_0x04f0;
    L_0x04b3:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unknown element under <receiver>: ";
        r3 = r3.append(r4);
        r4 = r25.getName();
        r3 = r3.append(r4);
        r4 = " at ";
        r3 = r3.append(r4);
        r0 = r22;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r25.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
    L_0x04eb:
        com.android.internal.util.XmlUtils.skipCurrentTag(r25);
        goto L_0x037f;
    L_0x04f0:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unknown element under <activity>: ";
        r3 = r3.append(r4);
        r4 = r25.getName();
        r3 = r3.append(r4);
        r4 = " at ";
        r3 = r3.append(r4);
        r0 = r22;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r25.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        goto L_0x04eb;
    L_0x0529:
        if (r19 != 0) goto L_0x005d;
    L_0x052b:
        r3 = r14.info;
        r2 = r14.intents;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x053a;
    L_0x0535:
        r2 = 1;
    L_0x0536:
        r3.exported = r2;
        goto L_0x005d;
    L_0x053a:
        r2 = 0;
        goto L_0x0536;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseActivity(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[], boolean, boolean):android.content.pm.PackageParser$Activity");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.content.pm.PackageParser.Activity parseActivityAlias(android.content.pm.PackageParser.Package r29, android.content.res.Resources r30, org.xmlpull.v1.XmlPullParser r31, android.util.AttributeSet r32, int r33, java.lang.String[] r34) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r28 = this;
        r2 = com.android.internal.R.styleable.AndroidManifestActivityAlias;
        r0 = r30;
        r1 = r32;
        r21 = r0.obtainAttributes(r1, r2);
        r2 = 7;
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r21;
        r26 = r0.getNonConfigurationString(r2, r3);
        if (r26 != 0) goto L_0x001f;
    L_0x0015:
        r2 = 0;
        r3 = "<activity-alias> does not specify android:targetActivity";
        r34[r2] = r3;
        r21.recycle();
        r15 = 0;
    L_0x001e:
        return r15;
    L_0x001f:
        r0 = r29;
        r2 = r0.applicationInfo;
        r2 = r2.packageName;
        r0 = r26;
        r1 = r34;
        r26 = buildClassName(r2, r0, r1);
        if (r26 != 0) goto L_0x0034;
    L_0x002f:
        r21.recycle();
        r15 = 0;
        goto L_0x001e;
    L_0x0034:
        r0 = r28;
        r2 = r0.mParseActivityAliasArgs;
        if (r2 != 0) goto L_0x005d;
    L_0x003a:
        r2 = new android.content.pm.PackageParser$ParseComponentArgs;
        r5 = 2;
        r6 = 0;
        r7 = 1;
        r8 = 8;
        r9 = 10;
        r0 = r28;
        r10 = r0.mSeparateProcesses;
        r11 = 0;
        r12 = 6;
        r13 = 4;
        r3 = r29;
        r4 = r34;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r0 = r28;
        r0.mParseActivityAliasArgs = r2;
        r0 = r28;
        r2 = r0.mParseActivityAliasArgs;
        r3 = "<activity-alias>";
        r2.tag = r3;
    L_0x005d:
        r0 = r28;
        r2 = r0.mParseActivityAliasArgs;
        r0 = r21;
        r2.sa = r0;
        r0 = r28;
        r2 = r0.mParseActivityAliasArgs;
        r0 = r33;
        r2.flags = r0;
        r25 = 0;
        r0 = r29;
        r2 = r0.activities;
        r14 = r2.size();
        r16 = 0;
    L_0x0079:
        r0 = r16;
        if (r0 >= r14) goto L_0x0099;
    L_0x007d:
        r0 = r29;
        r2 = r0.activities;
        r0 = r16;
        r24 = r2.get(r0);
        r24 = (android.content.pm.PackageParser.Activity) r24;
        r0 = r24;
        r2 = r0.info;
        r2 = r2.name;
        r0 = r26;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x00bf;
    L_0x0097:
        r25 = r24;
    L_0x0099:
        if (r25 != 0) goto L_0x00c2;
    L_0x009b:
        r2 = 0;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "<activity-alias> target activity ";
        r3 = r3.append(r4);
        r0 = r26;
        r3 = r3.append(r0);
        r4 = " not found in manifest";
        r3 = r3.append(r4);
        r3 = r3.toString();
        r34[r2] = r3;
        r21.recycle();
        r15 = 0;
        goto L_0x001e;
    L_0x00bf:
        r16 = r16 + 1;
        goto L_0x0079;
    L_0x00c2:
        r17 = new android.content.pm.ActivityInfo;
        r17.<init>();
        r0 = r26;
        r1 = r17;
        r1.targetActivity = r0;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.configChanges;
        r0 = r17;
        r0.configChanges = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.flags;
        r0 = r17;
        r0.flags = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.icon;
        r0 = r17;
        r0.icon = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.logo;
        r0 = r17;
        r0.logo = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.banner;
        r0 = r17;
        r0.banner = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.labelRes;
        r0 = r17;
        r0.labelRes = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.nonLocalizedLabel;
        r0 = r17;
        r0.nonLocalizedLabel = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.launchMode;
        r0 = r17;
        r0.launchMode = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.processName;
        r0 = r17;
        r0.processName = r2;
        r0 = r17;
        r2 = r0.descriptionRes;
        if (r2 != 0) goto L_0x0137;
    L_0x012d:
        r0 = r25;
        r2 = r0.info;
        r2 = r2.descriptionRes;
        r0 = r17;
        r0.descriptionRes = r2;
    L_0x0137:
        r0 = r25;
        r2 = r0.info;
        r2 = r2.screenOrientation;
        r0 = r17;
        r0.screenOrientation = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.taskAffinity;
        r0 = r17;
        r0.taskAffinity = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.theme;
        r0 = r17;
        r0.theme = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.softInputMode;
        r0 = r17;
        r0.softInputMode = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.uiOptions;
        r0 = r17;
        r0.uiOptions = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.parentActivityName;
        r0 = r17;
        r0.parentActivityName = r2;
        r0 = r25;
        r2 = r0.info;
        r2 = r2.maxRecents;
        r0 = r17;
        r0.maxRecents = r2;
        r15 = new android.content.pm.PackageParser$Activity;
        r0 = r28;
        r2 = r0.mParseActivityAliasArgs;
        r0 = r17;
        r15.<init>(r2, r0);
        r2 = 0;
        r2 = r34[r2];
        if (r2 == 0) goto L_0x0193;
    L_0x018d:
        r21.recycle();
        r15 = 0;
        goto L_0x001e;
    L_0x0193:
        r2 = 5;
        r0 = r21;
        r22 = r0.hasValue(r2);
        if (r22 == 0) goto L_0x01a8;
    L_0x019c:
        r2 = r15.info;
        r3 = 5;
        r4 = 0;
        r0 = r21;
        r3 = r0.getBoolean(r3, r4);
        r2.exported = r3;
    L_0x01a8:
        r2 = 3;
        r3 = 0;
        r0 = r21;
        r23 = r0.getNonConfigurationString(r2, r3);
        if (r23 == 0) goto L_0x01c4;
    L_0x01b2:
        r3 = r15.info;
        r2 = r23.length();
        if (r2 <= 0) goto L_0x01f2;
    L_0x01ba:
        r2 = r23.toString();
        r2 = r2.intern();
    L_0x01c2:
        r3.permission = r2;
    L_0x01c4:
        r2 = 9;
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r21;
        r20 = r0.getNonConfigurationString(r2, r3);
        if (r20 == 0) goto L_0x01e7;
    L_0x01d0:
        r2 = r15.info;
        r2 = r2.packageName;
        r0 = r20;
        r1 = r34;
        r19 = buildClassName(r2, r0, r1);
        r2 = 0;
        r2 = r34[r2];
        if (r2 != 0) goto L_0x01f4;
    L_0x01e1:
        r2 = r15.info;
        r0 = r19;
        r2.parentActivityName = r0;
    L_0x01e7:
        r21.recycle();
        r2 = 0;
        r2 = r34[r2];
        if (r2 == 0) goto L_0x0221;
    L_0x01ef:
        r15 = 0;
        goto L_0x001e;
    L_0x01f2:
        r2 = 0;
        goto L_0x01c2;
    L_0x01f4:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Activity alias ";
        r3 = r3.append(r4);
        r4 = r15.info;
        r4 = r4.name;
        r3 = r3.append(r4);
        r4 = " specified invalid parentActivityName ";
        r3 = r3.append(r4);
        r0 = r20;
        r3 = r3.append(r0);
        r3 = r3.toString();
        android.util.Log.e(r2, r3);
        r2 = 0;
        r3 = 0;
        r34[r2] = r3;
        goto L_0x01e7;
    L_0x0221:
        r18 = r31.getDepth();
    L_0x0225:
        r27 = r31.next();
        r2 = 1;
        r0 = r27;
        if (r0 == r2) goto L_0x0302;
    L_0x022e:
        r2 = 3;
        r0 = r27;
        if (r0 != r2) goto L_0x023b;
    L_0x0233:
        r2 = r31.getDepth();
        r0 = r18;
        if (r2 <= r0) goto L_0x0302;
    L_0x023b:
        r2 = 3;
        r0 = r27;
        if (r0 == r2) goto L_0x0225;
    L_0x0240:
        r2 = 4;
        r0 = r27;
        if (r0 == r2) goto L_0x0225;
    L_0x0245:
        r2 = r31.getName();
        r3 = "intent-filter";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x02a1;
    L_0x0251:
        r7 = new android.content.pm.PackageParser$ActivityIntentInfo;
        r7.<init>(r15);
        r6 = 1;
        r2 = r28;
        r3 = r30;
        r4 = r31;
        r5 = r32;
        r8 = r34;
        r2 = r2.parseIntent(r3, r4, r5, r6, r7, r8);
        if (r2 != 0) goto L_0x026a;
    L_0x0267:
        r15 = 0;
        goto L_0x001e;
    L_0x026a:
        r2 = r7.countActions();
        if (r2 != 0) goto L_0x029b;
    L_0x0270:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "No actions in intent filter at ";
        r3 = r3.append(r4);
        r0 = r28;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r31.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        goto L_0x0225;
    L_0x029b:
        r2 = r15.intents;
        r2.add(r7);
        goto L_0x0225;
    L_0x02a1:
        r2 = r31.getName();
        r3 = "meta-data";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x02c5;
    L_0x02ae:
        r12 = r15.metaData;
        r8 = r28;
        r9 = r30;
        r10 = r31;
        r11 = r32;
        r13 = r34;
        r2 = r8.parseMetaData(r9, r10, r11, r12, r13);
        r15.metaData = r2;
        if (r2 != 0) goto L_0x0225;
    L_0x02c2:
        r15 = 0;
        goto L_0x001e;
    L_0x02c5:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unknown element under <activity-alias>: ";
        r3 = r3.append(r4);
        r4 = r31.getName();
        r3 = r3.append(r4);
        r4 = " at ";
        r3 = r3.append(r4);
        r0 = r28;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r31.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        com.android.internal.util.XmlUtils.skipCurrentTag(r31);
        goto L_0x0225;
    L_0x0302:
        if (r22 != 0) goto L_0x001e;
    L_0x0304:
        r3 = r15.info;
        r2 = r15.intents;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x0313;
    L_0x030e:
        r2 = 1;
    L_0x030f:
        r3.exported = r2;
        goto L_0x001e;
    L_0x0313:
        r2 = 0;
        goto L_0x030f;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseActivityAlias(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):android.content.pm.PackageParser$Activity");
    }

    private Provider parseProvider(Package owner, Resources res, XmlPullParser parser, AttributeSet attrs, int flags, String[] outError) throws XmlPullParserException, IOException {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestProvider);
        if (this.mParseProviderArgs == null) {
            this.mParseProviderArgs = new ParseComponentArgs(owner, outError, PARSE_CHATTY, SDK_VERSION, PARSE_IS_SYSTEM, 15, 17, this.mSeparateProcesses, PARSE_IGNORE_PROCESSES, 14, 6);
            this.mParseProviderArgs.tag = "<provider>";
        }
        this.mParseProviderArgs.sa = sa;
        this.mParseProviderArgs.flags = flags;
        Provider p = new Provider(this.mParseProviderArgs, new ProviderInfo());
        if (outError[SDK_VERSION] != null) {
            sa.recycle();
            return null;
        }
        boolean providerExportedDefault = RIGID_PARSER;
        if (owner.applicationInfo.targetSdkVersion < 17) {
            providerExportedDefault = true;
        }
        p.info.exported = sa.getBoolean(7, providerExportedDefault);
        String cpname = sa.getNonConfigurationString(10, SDK_VERSION);
        p.info.isSyncable = sa.getBoolean(11, RIGID_PARSER);
        String permission = sa.getNonConfigurationString(3, SDK_VERSION);
        String str = sa.getNonConfigurationString(PARSE_MUST_BE_APK, SDK_VERSION);
        if (str == null) {
            str = permission;
        }
        if (str == null) {
            p.info.readPermission = owner.applicationInfo.permission;
        } else {
            p.info.readPermission = str.length() > 0 ? str.toString().intern() : null;
        }
        str = sa.getNonConfigurationString(5, SDK_VERSION);
        if (str == null) {
            str = permission;
        }
        if (str == null) {
            p.info.writePermission = owner.applicationInfo.permission;
        } else {
            p.info.writePermission = str.length() > 0 ? str.toString().intern() : null;
        }
        p.info.grantUriPermissions = sa.getBoolean(13, RIGID_PARSER);
        p.info.multiprocess = sa.getBoolean(9, RIGID_PARSER);
        p.info.initOrder = sa.getInt(12, SDK_VERSION);
        p.info.flags = SDK_VERSION;
        if (sa.getBoolean(PARSE_FORWARD_LOCK, RIGID_PARSER)) {
            ProviderInfo providerInfo = p.info;
            providerInfo.flags |= EditorInfo.IME_FLAG_NO_ENTER_ACTION;
            if (p.info.exported && (flags & PARSE_IS_PRIVILEGED) == 0) {
                Slog.w(TAG, "Provider exported request ignored due to singleUser: " + p.className + " at " + this.mArchiveSourcePath + " " + parser.getPositionDescription());
                p.info.exported = RIGID_PARSER;
            }
        }
        sa.recycle();
        if ((owner.applicationInfo.flags & EditorInfo.IME_FLAG_NO_EXTRACT_UI) != 0 && p.info.processName == owner.packageName) {
            outError[SDK_VERSION] = "Heavy-weight applications can not have providers in main process";
            return null;
        } else if (cpname == null) {
            outError[SDK_VERSION] = "<provider> does not include authorities attribute";
            return null;
        } else if (cpname.length() <= 0) {
            outError[SDK_VERSION] = "<provider> has empty authorities attribute";
            return null;
        } else {
            p.info.authority = cpname.intern();
            if (parseProviderTags(res, parser, attrs, p, outError)) {
                return p;
            }
            return null;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean parseProviderTags(android.content.res.Resources r29, org.xmlpull.v1.XmlPullParser r30, android.util.AttributeSet r31, android.content.pm.PackageParser.Provider r32, java.lang.String[] r33) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r28 = this;
        r19 = r30.getDepth();
    L_0x0004:
        r26 = r30.next();
        r4 = 1;
        r0 = r26;
        if (r0 == r4) goto L_0x02f7;
    L_0x000d:
        r4 = 3;
        r0 = r26;
        if (r0 != r4) goto L_0x001a;
    L_0x0012:
        r4 = r30.getDepth();
        r0 = r19;
        if (r4 <= r0) goto L_0x02f7;
    L_0x001a:
        r4 = 3;
        r0 = r26;
        if (r0 == r4) goto L_0x0004;
    L_0x001f:
        r4 = 4;
        r0 = r26;
        if (r0 == r4) goto L_0x0004;
    L_0x0024:
        r4 = r30.getName();
        r5 = "intent-filter";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0052;
    L_0x0030:
        r9 = new android.content.pm.PackageParser$ProviderIntentInfo;
        r0 = r32;
        r9.<init>(r0);
        r8 = 1;
        r4 = r28;
        r5 = r29;
        r6 = r30;
        r7 = r31;
        r10 = r33;
        r4 = r4.parseIntent(r5, r6, r7, r8, r9, r10);
        if (r4 != 0) goto L_0x004a;
    L_0x0048:
        r4 = 0;
    L_0x0049:
        return r4;
    L_0x004a:
        r0 = r32;
        r4 = r0.intents;
        r4.add(r9);
        goto L_0x0004;
    L_0x0052:
        r4 = r30.getName();
        r5 = "meta-data";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x0079;
    L_0x005f:
        r0 = r32;
        r14 = r0.metaData;
        r10 = r28;
        r11 = r29;
        r12 = r30;
        r13 = r31;
        r15 = r33;
        r4 = r10.parseMetaData(r11, r12, r13, r14, r15);
        r0 = r32;
        r0.metaData = r4;
        if (r4 != 0) goto L_0x0004;
    L_0x0077:
        r4 = 0;
        goto L_0x0049;
    L_0x0079:
        r4 = r30.getName();
        r5 = "grant-uri-permission";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x015e;
    L_0x0085:
        r4 = com.android.internal.R.styleable.AndroidManifestGrantUriPermission;
        r0 = r29;
        r1 = r31;
        r24 = r0.obtainAttributes(r1, r4);
        r20 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r24;
        r25 = r0.getNonConfigurationString(r4, r5);
        if (r25 == 0) goto L_0x00a5;
    L_0x009b:
        r20 = new android.os.PatternMatcher;
        r4 = 0;
        r0 = r20;
        r1 = r25;
        r0.<init>(r1, r4);
    L_0x00a5:
        r4 = 1;
        r5 = 0;
        r0 = r24;
        r25 = r0.getNonConfigurationString(r4, r5);
        if (r25 == 0) goto L_0x00b9;
    L_0x00af:
        r20 = new android.os.PatternMatcher;
        r4 = 1;
        r0 = r20;
        r1 = r25;
        r0.<init>(r1, r4);
    L_0x00b9:
        r4 = 2;
        r5 = 0;
        r0 = r24;
        r25 = r0.getNonConfigurationString(r4, r5);
        if (r25 == 0) goto L_0x00cd;
    L_0x00c3:
        r20 = new android.os.PatternMatcher;
        r4 = 2;
        r0 = r20;
        r1 = r25;
        r0.<init>(r1, r4);
    L_0x00cd:
        r24.recycle();
        if (r20 == 0) goto L_0x0121;
    L_0x00d2:
        r0 = r32;
        r4 = r0.info;
        r4 = r4.uriPermissionPatterns;
        if (r4 != 0) goto L_0x00f8;
    L_0x00da:
        r0 = r32;
        r4 = r0.info;
        r5 = 1;
        r5 = new android.os.PatternMatcher[r5];
        r4.uriPermissionPatterns = r5;
        r0 = r32;
        r4 = r0.info;
        r4 = r4.uriPermissionPatterns;
        r5 = 0;
        r4[r5] = r20;
    L_0x00ec:
        r0 = r32;
        r4 = r0.info;
        r5 = 1;
        r4.grantUriPermissions = r5;
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x0004;
    L_0x00f8:
        r0 = r32;
        r4 = r0.info;
        r4 = r4.uriPermissionPatterns;
        r0 = r4.length;
        r16 = r0;
        r4 = r16 + 1;
        r0 = new android.os.PatternMatcher[r4];
        r18 = r0;
        r0 = r32;
        r4 = r0.info;
        r4 = r4.uriPermissionPatterns;
        r5 = 0;
        r6 = 0;
        r0 = r18;
        r1 = r16;
        java.lang.System.arraycopy(r4, r5, r0, r6, r1);
        r18[r16] = r20;
        r0 = r32;
        r4 = r0.info;
        r0 = r18;
        r4.uriPermissionPatterns = r0;
        goto L_0x00ec;
    L_0x0121:
        r4 = "PackageParser";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unknown element under <path-permission>: ";
        r5 = r5.append(r6);
        r6 = r30.getName();
        r5 = r5.append(r6);
        r6 = " at ";
        r5 = r5.append(r6);
        r0 = r28;
        r6 = r0.mArchiveSourcePath;
        r5 = r5.append(r6);
        r6 = " ";
        r5 = r5.append(r6);
        r6 = r30.getPositionDescription();
        r5 = r5.append(r6);
        r5 = r5.toString();
        android.util.Slog.w(r4, r5);
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x0004;
    L_0x015e:
        r4 = r30.getName();
        r5 = "path-permission";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x02ba;
    L_0x016b:
        r4 = com.android.internal.R.styleable.AndroidManifestPathPermission;
        r0 = r29;
        r1 = r31;
        r24 = r0.obtainAttributes(r1, r4);
        r20 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r24;
        r22 = r0.getNonConfigurationString(r4, r5);
        r4 = 1;
        r5 = 0;
        r0 = r24;
        r23 = r0.getNonConfigurationString(r4, r5);
        if (r23 != 0) goto L_0x018b;
    L_0x0189:
        r23 = r22;
    L_0x018b:
        r4 = 2;
        r5 = 0;
        r0 = r24;
        r27 = r0.getNonConfigurationString(r4, r5);
        if (r27 != 0) goto L_0x0197;
    L_0x0195:
        r27 = r22;
    L_0x0197:
        r17 = 0;
        if (r23 == 0) goto L_0x01a1;
    L_0x019b:
        r23 = r23.intern();
        r17 = 1;
    L_0x01a1:
        if (r27 == 0) goto L_0x01a9;
    L_0x01a3:
        r27 = r27.intern();
        r17 = 1;
    L_0x01a9:
        if (r17 != 0) goto L_0x01e8;
    L_0x01ab:
        r4 = "PackageParser";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "No readPermission or writePermssion for <path-permission>: ";
        r5 = r5.append(r6);
        r6 = r30.getName();
        r5 = r5.append(r6);
        r6 = " at ";
        r5 = r5.append(r6);
        r0 = r28;
        r6 = r0.mArchiveSourcePath;
        r5 = r5.append(r6);
        r6 = " ";
        r5 = r5.append(r6);
        r6 = r30.getPositionDescription();
        r5 = r5.append(r6);
        r5 = r5.toString();
        android.util.Slog.w(r4, r5);
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x0004;
    L_0x01e8:
        r4 = 3;
        r5 = 0;
        r0 = r24;
        r21 = r0.getNonConfigurationString(r4, r5);
        if (r21 == 0) goto L_0x0200;
    L_0x01f2:
        r20 = new android.content.pm.PathPermission;
        r4 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r23;
        r3 = r27;
        r0.<init>(r1, r4, r2, r3);
    L_0x0200:
        r4 = 4;
        r5 = 0;
        r0 = r24;
        r21 = r0.getNonConfigurationString(r4, r5);
        if (r21 == 0) goto L_0x0218;
    L_0x020a:
        r20 = new android.content.pm.PathPermission;
        r4 = 1;
        r0 = r20;
        r1 = r21;
        r2 = r23;
        r3 = r27;
        r0.<init>(r1, r4, r2, r3);
    L_0x0218:
        r4 = 5;
        r5 = 0;
        r0 = r24;
        r21 = r0.getNonConfigurationString(r4, r5);
        if (r21 == 0) goto L_0x0230;
    L_0x0222:
        r20 = new android.content.pm.PathPermission;
        r4 = 2;
        r0 = r20;
        r1 = r21;
        r2 = r23;
        r3 = r27;
        r0.<init>(r1, r4, r2, r3);
    L_0x0230:
        r24.recycle();
        if (r20 == 0) goto L_0x027d;
    L_0x0235:
        r0 = r32;
        r4 = r0.info;
        r4 = r4.pathPermissions;
        if (r4 != 0) goto L_0x0254;
    L_0x023d:
        r0 = r32;
        r4 = r0.info;
        r5 = 1;
        r5 = new android.content.pm.PathPermission[r5];
        r4.pathPermissions = r5;
        r0 = r32;
        r4 = r0.info;
        r4 = r4.pathPermissions;
        r5 = 0;
        r4[r5] = r20;
    L_0x024f:
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x0004;
    L_0x0254:
        r0 = r32;
        r4 = r0.info;
        r4 = r4.pathPermissions;
        r0 = r4.length;
        r16 = r0;
        r4 = r16 + 1;
        r0 = new android.content.pm.PathPermission[r4];
        r18 = r0;
        r0 = r32;
        r4 = r0.info;
        r4 = r4.pathPermissions;
        r5 = 0;
        r6 = 0;
        r0 = r18;
        r1 = r16;
        java.lang.System.arraycopy(r4, r5, r0, r6, r1);
        r18[r16] = r20;
        r0 = r32;
        r4 = r0.info;
        r0 = r18;
        r4.pathPermissions = r0;
        goto L_0x024f;
    L_0x027d:
        r4 = "PackageParser";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "No path, pathPrefix, or pathPattern for <path-permission>: ";
        r5 = r5.append(r6);
        r6 = r30.getName();
        r5 = r5.append(r6);
        r6 = " at ";
        r5 = r5.append(r6);
        r0 = r28;
        r6 = r0.mArchiveSourcePath;
        r5 = r5.append(r6);
        r6 = " ";
        r5 = r5.append(r6);
        r6 = r30.getPositionDescription();
        r5 = r5.append(r6);
        r5 = r5.toString();
        android.util.Slog.w(r4, r5);
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x0004;
    L_0x02ba:
        r4 = "PackageParser";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unknown element under <provider>: ";
        r5 = r5.append(r6);
        r6 = r30.getName();
        r5 = r5.append(r6);
        r6 = " at ";
        r5 = r5.append(r6);
        r0 = r28;
        r6 = r0.mArchiveSourcePath;
        r5 = r5.append(r6);
        r6 = " ";
        r5 = r5.append(r6);
        r6 = r30.getPositionDescription();
        r5 = r5.append(r6);
        r5 = r5.toString();
        android.util.Slog.w(r4, r5);
        com.android.internal.util.XmlUtils.skipCurrentTag(r30);
        goto L_0x0004;
    L_0x02f7:
        r4 = 1;
        goto L_0x0049;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseProviderTags(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.pm.PackageParser$Provider, java.lang.String[]):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.content.pm.PackageParser.Service parseService(android.content.pm.PackageParser.Package r21, android.content.res.Resources r22, org.xmlpull.v1.XmlPullParser r23, android.util.AttributeSet r24, int r25, java.lang.String[] r26) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r20 = this;
        r2 = com.android.internal.R.styleable.AndroidManifestService;
        r0 = r22;
        r1 = r24;
        r16 = r0.obtainAttributes(r1, r2);
        r0 = r20;
        r2 = r0.mParseServiceArgs;
        if (r2 != 0) goto L_0x0033;
    L_0x0010:
        r2 = new android.content.pm.PackageParser$ParseComponentArgs;
        r5 = 2;
        r6 = 0;
        r7 = 1;
        r8 = 8;
        r9 = 12;
        r0 = r20;
        r10 = r0.mSeparateProcesses;
        r11 = 6;
        r12 = 7;
        r13 = 4;
        r3 = r21;
        r4 = r26;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r0 = r20;
        r0.mParseServiceArgs = r2;
        r0 = r20;
        r2 = r0.mParseServiceArgs;
        r3 = "<service>";
        r2.tag = r3;
    L_0x0033:
        r0 = r20;
        r2 = r0.mParseServiceArgs;
        r0 = r16;
        r2.sa = r0;
        r0 = r20;
        r2 = r0.mParseServiceArgs;
        r0 = r25;
        r2.flags = r0;
        r15 = new android.content.pm.PackageParser$Service;
        r0 = r20;
        r2 = r0.mParseServiceArgs;
        r3 = new android.content.pm.ServiceInfo;
        r3.<init>();
        r15.<init>(r2, r3);
        r2 = 0;
        r2 = r26[r2];
        if (r2 == 0) goto L_0x005b;
    L_0x0056:
        r16.recycle();
        r15 = 0;
    L_0x005a:
        return r15;
    L_0x005b:
        r2 = 5;
        r0 = r16;
        r17 = r0.hasValue(r2);
        if (r17 == 0) goto L_0x0070;
    L_0x0064:
        r2 = r15.info;
        r3 = 5;
        r4 = 0;
        r0 = r16;
        r3 = r0.getBoolean(r3, r4);
        r2.exported = r3;
    L_0x0070:
        r2 = 3;
        r3 = 0;
        r0 = r16;
        r18 = r0.getNonConfigurationString(r2, r3);
        if (r18 != 0) goto L_0x012c;
    L_0x007a:
        r2 = r15.info;
        r0 = r21;
        r3 = r0.applicationInfo;
        r3 = r3.permission;
        r2.permission = r3;
    L_0x0084:
        r2 = r15.info;
        r3 = 0;
        r2.flags = r3;
        r2 = 9;
        r3 = 0;
        r0 = r16;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x009c;
    L_0x0094:
        r2 = r15.info;
        r3 = r2.flags;
        r3 = r3 | 1;
        r2.flags = r3;
    L_0x009c:
        r2 = 10;
        r3 = 0;
        r0 = r16;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x00af;
    L_0x00a7:
        r2 = r15.info;
        r3 = r2.flags;
        r3 = r3 | 2;
        r2.flags = r3;
    L_0x00af:
        r2 = 11;
        r3 = 0;
        r0 = r16;
        r2 = r0.getBoolean(r2, r3);
        if (r2 == 0) goto L_0x010c;
    L_0x00ba:
        r2 = r15.info;
        r3 = r2.flags;
        r4 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r3 = r3 | r4;
        r2.flags = r3;
        r2 = r15.info;
        r2 = r2.exported;
        if (r2 == 0) goto L_0x010c;
    L_0x00c9:
        r0 = r25;
        r2 = r0 & 128;
        if (r2 != 0) goto L_0x010c;
    L_0x00cf:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Service exported request ignored due to singleUser: ";
        r3 = r3.append(r4);
        r4 = r15.className;
        r3 = r3.append(r4);
        r4 = " at ";
        r3 = r3.append(r4);
        r0 = r20;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r23.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        r2 = r15.info;
        r3 = 0;
        r2.exported = r3;
        r17 = 1;
    L_0x010c:
        r16.recycle();
        r0 = r21;
        r2 = r0.applicationInfo;
        r2 = r2.flags;
        r3 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r2 = r2 & r3;
        if (r2 == 0) goto L_0x0142;
    L_0x011a:
        r2 = r15.info;
        r2 = r2.processName;
        r0 = r21;
        r3 = r0.packageName;
        if (r2 != r3) goto L_0x0142;
    L_0x0124:
        r2 = 0;
        r3 = "Heavy-weight applications can not have services in main process";
        r26[r2] = r3;
        r15 = 0;
        goto L_0x005a;
    L_0x012c:
        r3 = r15.info;
        r2 = r18.length();
        if (r2 <= 0) goto L_0x0140;
    L_0x0134:
        r2 = r18.toString();
        r2 = r2.intern();
    L_0x013c:
        r3.permission = r2;
        goto L_0x0084;
    L_0x0140:
        r2 = 0;
        goto L_0x013c;
    L_0x0142:
        r14 = r23.getDepth();
    L_0x0146:
        r19 = r23.next();
        r2 = 1;
        r0 = r19;
        if (r0 == r2) goto L_0x01f0;
    L_0x014f:
        r2 = 3;
        r0 = r19;
        if (r0 != r2) goto L_0x015a;
    L_0x0154:
        r2 = r23.getDepth();
        if (r2 <= r14) goto L_0x01f0;
    L_0x015a:
        r2 = 3;
        r0 = r19;
        if (r0 == r2) goto L_0x0146;
    L_0x015f:
        r2 = 4;
        r0 = r19;
        if (r0 == r2) goto L_0x0146;
    L_0x0164:
        r2 = r23.getName();
        r3 = "intent-filter";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x018f;
    L_0x0170:
        r7 = new android.content.pm.PackageParser$ServiceIntentInfo;
        r7.<init>(r15);
        r6 = 1;
        r2 = r20;
        r3 = r22;
        r4 = r23;
        r5 = r24;
        r8 = r26;
        r2 = r2.parseIntent(r3, r4, r5, r6, r7, r8);
        if (r2 != 0) goto L_0x0189;
    L_0x0186:
        r15 = 0;
        goto L_0x005a;
    L_0x0189:
        r2 = r15.intents;
        r2.add(r7);
        goto L_0x0146;
    L_0x018f:
        r2 = r23.getName();
        r3 = "meta-data";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x01b3;
    L_0x019c:
        r12 = r15.metaData;
        r8 = r20;
        r9 = r22;
        r10 = r23;
        r11 = r24;
        r13 = r26;
        r2 = r8.parseMetaData(r9, r10, r11, r12, r13);
        r15.metaData = r2;
        if (r2 != 0) goto L_0x0146;
    L_0x01b0:
        r15 = 0;
        goto L_0x005a;
    L_0x01b3:
        r2 = "PackageParser";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Unknown element under <service>: ";
        r3 = r3.append(r4);
        r4 = r23.getName();
        r3 = r3.append(r4);
        r4 = " at ";
        r3 = r3.append(r4);
        r0 = r20;
        r4 = r0.mArchiveSourcePath;
        r3 = r3.append(r4);
        r4 = " ";
        r3 = r3.append(r4);
        r4 = r23.getPositionDescription();
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Slog.w(r2, r3);
        com.android.internal.util.XmlUtils.skipCurrentTag(r23);
        goto L_0x0146;
    L_0x01f0:
        if (r17 != 0) goto L_0x005a;
    L_0x01f2:
        r3 = r15.info;
        r2 = r15.intents;
        r2 = r2.size();
        if (r2 <= 0) goto L_0x0201;
    L_0x01fc:
        r2 = 1;
    L_0x01fd:
        r3.exported = r2;
        goto L_0x005a;
    L_0x0201:
        r2 = 0;
        goto L_0x01fd;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseService(android.content.pm.PackageParser$Package, android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, int, java.lang.String[]):android.content.pm.PackageParser$Service");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean parseAllMetaData(android.content.res.Resources r9, org.xmlpull.v1.XmlPullParser r10, android.util.AttributeSet r11, java.lang.String r12, android.content.pm.PackageParser.Component r13, java.lang.String[] r14) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r8 = this;
        r6 = r10.getDepth();
    L_0x0004:
        r7 = r10.next();
        r0 = 1;
        if (r7 == r0) goto L_0x007c;
    L_0x000b:
        r0 = 3;
        if (r7 != r0) goto L_0x0014;
    L_0x000e:
        r0 = r10.getDepth();
        if (r0 <= r6) goto L_0x007c;
    L_0x0014:
        r0 = 3;
        if (r7 == r0) goto L_0x0004;
    L_0x0017:
        r0 = 4;
        if (r7 == r0) goto L_0x0004;
    L_0x001a:
        r0 = r10.getName();
        r1 = "meta-data";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0038;
    L_0x0027:
        r4 = r13.metaData;
        r0 = r8;
        r1 = r9;
        r2 = r10;
        r3 = r11;
        r5 = r14;
        r0 = r0.parseMetaData(r1, r2, r3, r4, r5);
        r13.metaData = r0;
        if (r0 != 0) goto L_0x0004;
    L_0x0036:
        r0 = 0;
    L_0x0037:
        return r0;
    L_0x0038:
        r0 = "PackageParser";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Unknown element under ";
        r1 = r1.append(r2);
        r1 = r1.append(r12);
        r2 = ": ";
        r1 = r1.append(r2);
        r2 = r10.getName();
        r1 = r1.append(r2);
        r2 = " at ";
        r1 = r1.append(r2);
        r2 = r8.mArchiveSourcePath;
        r1 = r1.append(r2);
        r2 = " ";
        r1 = r1.append(r2);
        r2 = r10.getPositionDescription();
        r1 = r1.append(r2);
        r1 = r1.toString();
        android.util.Slog.w(r0, r1);
        com.android.internal.util.XmlUtils.skipCurrentTag(r10);
        goto L_0x0004;
    L_0x007c:
        r0 = 1;
        goto L_0x0037;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseAllMetaData(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, java.lang.String, android.content.pm.PackageParser$Component, java.lang.String[]):boolean");
    }

    private Bundle parseMetaData(Resources res, XmlPullParser parser, AttributeSet attrs, Bundle data, String[] outError) throws XmlPullParserException, IOException {
        String str = null;
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestMetaData);
        if (data == null) {
            data = new Bundle();
        }
        String name = sa.getNonConfigurationString(SDK_VERSION, SDK_VERSION);
        if (name == null) {
            outError[SDK_VERSION] = "<meta-data> requires an android:name attribute";
            sa.recycle();
            return null;
        }
        name = name.intern();
        TypedValue v = sa.peekValue(PARSE_CHATTY);
        if (v == null || v.resourceId == 0) {
            v = sa.peekValue(PARSE_IS_SYSTEM);
            if (v == null) {
                outError[SDK_VERSION] = "<meta-data> requires an android:value or android:resource attribute";
                data = null;
            } else if (v.type == 3) {
                CharSequence cs = v.coerceToString();
                if (cs != null) {
                    str = cs.toString().intern();
                }
                data.putString(name, str);
            } else if (v.type == 18) {
                data.putBoolean(name, v.data != 0 ? true : RIGID_PARSER);
            } else if (v.type >= PARSE_FORWARD_LOCK && v.type <= 31) {
                data.putInt(name, v.data);
            } else if (v.type == PARSE_MUST_BE_APK) {
                data.putFloat(name, v.getFloat());
            } else {
                Slog.w(TAG, "<meta-data> only supports string, integer, float, color, boolean, and resource reference types: " + parser.getName() + " at " + this.mArchiveSourcePath + " " + parser.getPositionDescription());
            }
        } else {
            data.putInt(name, v.resourceId);
        }
        sa.recycle();
        XmlUtils.skipCurrentTag(parser);
        return data;
    }

    private static VerifierInfo parseVerifier(Resources res, XmlPullParser parser, AttributeSet attrs, int flags) {
        TypedArray sa = res.obtainAttributes(attrs, R.styleable.AndroidManifestPackageVerifier);
        String packageName = sa.getNonResourceString(SDK_VERSION);
        String encodedPublicKey = sa.getNonResourceString(PARSE_IS_SYSTEM);
        sa.recycle();
        if (packageName == null || packageName.length() == 0) {
            Slog.i(TAG, "verifier package name was null; skipping");
            return null;
        }
        PublicKey publicKey = parsePublicKey(encodedPublicKey);
        if (publicKey != null) {
            return new VerifierInfo(packageName, publicKey);
        }
        Slog.i(TAG, "Unable to parse verifier public key for " + packageName);
        return null;
    }

    public static final PublicKey parsePublicKey(String encodedPublicKey) {
        PublicKey publicKey = null;
        if (encodedPublicKey == null) {
            Slog.i(TAG, "Could not parse null public key");
        } else {
            try {
                EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(encodedPublicKey, SDK_VERSION));
                try {
                    publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
                } catch (NoSuchAlgorithmException e) {
                    Log.wtf(TAG, "Could not parse public key because RSA isn't included in build");
                } catch (InvalidKeySpecException e2) {
                    try {
                        publicKey = KeyFactory.getInstance("DSA").generatePublic(keySpec);
                    } catch (NoSuchAlgorithmException e3) {
                        Log.wtf(TAG, "Could not parse public key because DSA isn't included in build");
                    } catch (InvalidKeySpecException e4) {
                    }
                }
            } catch (IllegalArgumentException e5) {
                Slog.i(TAG, "Could not parse verifier public key; invalid Base64");
            }
        }
        return publicKey;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean parseIntent(android.content.res.Resources r17, org.xmlpull.v1.XmlPullParser r18, android.util.AttributeSet r19, boolean r20, android.content.pm.PackageParser.IntentInfo r21, java.lang.String[] r22) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r16 = this;
        r13 = com.android.internal.R.styleable.AndroidManifestIntentFilter;
        r0 = r17;
        r1 = r19;
        r8 = r0.obtainAttributes(r1, r13);
        r13 = 2;
        r14 = 0;
        r7 = r8.getInt(r13, r14);
        r0 = r21;
        r0.setPriority(r7);
        r13 = 0;
        r11 = r8.peekValue(r13);
        if (r11 == 0) goto L_0x002c;
    L_0x001c:
        r13 = r11.resourceId;
        r0 = r21;
        r0.labelRes = r13;
        if (r13 != 0) goto L_0x002c;
    L_0x0024:
        r13 = r11.coerceToString();
        r0 = r21;
        r0.nonLocalizedLabel = r13;
    L_0x002c:
        r13 = 1;
        r14 = 0;
        r13 = r8.getResourceId(r13, r14);
        r0 = r21;
        r0.icon = r13;
        r13 = 3;
        r14 = 0;
        r13 = r8.getResourceId(r13, r14);
        r0 = r21;
        r0.logo = r13;
        r13 = 4;
        r14 = 0;
        r13 = r8.getResourceId(r13, r14);
        r0 = r21;
        r0.banner = r13;
        r8.recycle();
        r5 = r18.getDepth();
    L_0x0051:
        r10 = r18.next();
        r13 = 1;
        if (r10 == r13) goto L_0x01bb;
    L_0x0058:
        r13 = 3;
        if (r10 != r13) goto L_0x0061;
    L_0x005b:
        r13 = r18.getDepth();
        if (r13 <= r5) goto L_0x01bb;
    L_0x0061:
        r13 = 3;
        if (r10 == r13) goto L_0x0051;
    L_0x0064:
        r13 = 4;
        if (r10 == r13) goto L_0x0051;
    L_0x0067:
        r4 = r18.getName();
        r13 = "action";
        r13 = r4.equals(r13);
        if (r13 == 0) goto L_0x0094;
    L_0x0073:
        r13 = "http://schemas.android.com/apk/res/android";
        r14 = "name";
        r0 = r19;
        r12 = r0.getAttributeValue(r13, r14);
        if (r12 == 0) goto L_0x0084;
    L_0x0080:
        r13 = "";
        if (r12 != r13) goto L_0x008b;
    L_0x0084:
        r13 = 0;
        r14 = "No value supplied for <android:name>";
        r22[r13] = r14;
        r13 = 0;
    L_0x008a:
        return r13;
    L_0x008b:
        com.android.internal.util.XmlUtils.skipCurrentTag(r18);
        r0 = r21;
        r0.addAction(r12);
        goto L_0x0051;
    L_0x0094:
        r13 = "category";
        r13 = r4.equals(r13);
        if (r13 == 0) goto L_0x00bd;
    L_0x009c:
        r13 = "http://schemas.android.com/apk/res/android";
        r14 = "name";
        r0 = r19;
        r12 = r0.getAttributeValue(r13, r14);
        if (r12 == 0) goto L_0x00ad;
    L_0x00a9:
        r13 = "";
        if (r12 != r13) goto L_0x00b4;
    L_0x00ad:
        r13 = 0;
        r14 = "No value supplied for <android:name>";
        r22[r13] = r14;
        r13 = 0;
        goto L_0x008a;
    L_0x00b4:
        com.android.internal.util.XmlUtils.skipCurrentTag(r18);
        r0 = r21;
        r0.addCategory(r12);
        goto L_0x0051;
    L_0x00bd:
        r13 = "data";
        r13 = r4.equals(r13);
        if (r13 == 0) goto L_0x017e;
    L_0x00c5:
        r13 = com.android.internal.R.styleable.AndroidManifestData;
        r0 = r17;
        r1 = r19;
        r8 = r0.obtainAttributes(r1, r13);
        r13 = 0;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x00dc;
    L_0x00d7:
        r0 = r21;
        r0.addDataType(r9);	 Catch:{ MalformedMimeTypeException -> 0x011a }
    L_0x00dc:
        r13 = 1;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x00e9;
    L_0x00e4:
        r0 = r21;
        r0.addDataScheme(r9);
    L_0x00e9:
        r13 = 7;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x00f7;
    L_0x00f1:
        r13 = 0;
        r0 = r21;
        r0.addDataSchemeSpecificPart(r9, r13);
    L_0x00f7:
        r13 = 8;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x0106;
    L_0x0100:
        r13 = 1;
        r0 = r21;
        r0.addDataSchemeSpecificPart(r9, r13);
    L_0x0106:
        r13 = 9;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x012e;
    L_0x010f:
        if (r20 != 0) goto L_0x0128;
    L_0x0111:
        r13 = 0;
        r14 = "sspPattern not allowed here; ssp must be literal";
        r22[r13] = r14;
        r13 = 0;
        goto L_0x008a;
    L_0x011a:
        r2 = move-exception;
        r13 = 0;
        r14 = r2.toString();
        r22[r13] = r14;
        r8.recycle();
        r13 = 0;
        goto L_0x008a;
    L_0x0128:
        r13 = 2;
        r0 = r21;
        r0.addDataSchemeSpecificPart(r9, r13);
    L_0x012e:
        r13 = 2;
        r14 = 0;
        r3 = r8.getNonConfigurationString(r13, r14);
        r13 = 3;
        r14 = 0;
        r6 = r8.getNonConfigurationString(r13, r14);
        if (r3 == 0) goto L_0x0141;
    L_0x013c:
        r0 = r21;
        r0.addDataAuthority(r3, r6);
    L_0x0141:
        r13 = 4;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x014f;
    L_0x0149:
        r13 = 0;
        r0 = r21;
        r0.addDataPath(r9, r13);
    L_0x014f:
        r13 = 5;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x015d;
    L_0x0157:
        r13 = 1;
        r0 = r21;
        r0.addDataPath(r9, r13);
    L_0x015d:
        r13 = 6;
        r14 = 0;
        r9 = r8.getNonConfigurationString(r13, r14);
        if (r9 == 0) goto L_0x0176;
    L_0x0165:
        if (r20 != 0) goto L_0x0170;
    L_0x0167:
        r13 = 0;
        r14 = "pathPattern not allowed here; path must be literal";
        r22[r13] = r14;
        r13 = 0;
        goto L_0x008a;
    L_0x0170:
        r13 = 2;
        r0 = r21;
        r0.addDataPath(r9, r13);
    L_0x0176:
        r8.recycle();
        com.android.internal.util.XmlUtils.skipCurrentTag(r18);
        goto L_0x0051;
    L_0x017e:
        r13 = "PackageParser";
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "Unknown element under <intent-filter>: ";
        r14 = r14.append(r15);
        r15 = r18.getName();
        r14 = r14.append(r15);
        r15 = " at ";
        r14 = r14.append(r15);
        r0 = r16;
        r15 = r0.mArchiveSourcePath;
        r14 = r14.append(r15);
        r15 = " ";
        r14 = r14.append(r15);
        r15 = r18.getPositionDescription();
        r14 = r14.append(r15);
        r14 = r14.toString();
        android.util.Slog.w(r13, r14);
        com.android.internal.util.XmlUtils.skipCurrentTag(r18);
        goto L_0x0051;
    L_0x01bb:
        r13 = "android.intent.category.DEFAULT";
        r0 = r21;
        r13 = r0.hasCategory(r13);
        r0 = r21;
        r0.hasDefault = r13;
        r13 = 1;
        goto L_0x008a;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageParser.parseIntent(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, boolean, android.content.pm.PackageParser$IntentInfo, java.lang.String[]):boolean");
    }

    private static boolean copyNeeded(int flags, Package p, PackageUserState state, Bundle metaData, int userId) {
        if (userId != 0) {
            return true;
        }
        if (state.enabled != 0) {
            if (p.applicationInfo.enabled != (state.enabled == PARSE_IS_SYSTEM ? true : RIGID_PARSER)) {
                return true;
            }
        }
        if (!state.installed || state.hidden || state.stopped) {
            return true;
        }
        if ((flags & PARSE_IS_PRIVILEGED) != 0 && (metaData != null || p.mAppMetaData != null)) {
            return true;
        }
        if ((flags & AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT) == 0 || p.usesLibraryFiles == null) {
            return RIGID_PARSER;
        }
        return true;
    }

    public static ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state) {
        return generateApplicationInfo(p, flags, state, UserHandle.getCallingUserId());
    }

    private static void updateApplicationInfo(ApplicationInfo ai, int flags, PackageUserState state) {
        boolean z = true;
        if (!sCompatibilityModeEnabled) {
            ai.disableCompatibilityMode();
        }
        if (state.installed) {
            ai.flags |= LayoutParams.FLAG_SPLIT_TOUCH;
        } else {
            ai.flags &= -8388609;
        }
        if (state.hidden) {
            ai.flags |= EditorInfo.IME_FLAG_NAVIGATE_NEXT;
        } else {
            ai.flags &= -134217729;
        }
        if (state.enabled == PARSE_IS_SYSTEM) {
            ai.enabled = true;
        } else if (state.enabled == PARSE_MUST_BE_APK) {
            if ((AccessibilityNodeInfo.ACTION_PASTE & flags) == 0) {
                z = RIGID_PARSER;
            }
            ai.enabled = z;
        } else if (state.enabled == PARSE_CHATTY || state.enabled == 3) {
            ai.enabled = RIGID_PARSER;
        }
        ai.enabledSetting = state.enabled;
    }

    public static ApplicationInfo generateApplicationInfo(Package p, int flags, PackageUserState state, int userId) {
        if (p == null || !checkUseInstalledOrHidden(flags, state)) {
            return null;
        }
        if (copyNeeded(flags, p, state, null, userId) || ((AccessibilityNodeInfo.ACTION_PASTE & flags) != 0 && state.enabled == PARSE_MUST_BE_APK)) {
            ApplicationInfo ai = new ApplicationInfo(p.applicationInfo);
            if (userId != 0) {
                ai.uid = UserHandle.getUid(userId, ai.uid);
                ai.dataDir = PackageManager.getDataDirForUser(userId, ai.packageName);
            }
            if ((flags & PARSE_IS_PRIVILEGED) != 0) {
                ai.metaData = p.mAppMetaData;
            }
            if ((flags & AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT) != 0) {
                ai.sharedLibraryFiles = p.usesLibraryFiles;
            }
            if (state.stopped) {
                ai.flags |= AccessibilityNodeInfo.ACTION_SET_TEXT;
            } else {
                ai.flags &= -2097153;
            }
            updateApplicationInfo(ai, flags, state);
            return ai;
        }
        updateApplicationInfo(p.applicationInfo, flags, state);
        return p.applicationInfo;
    }

    public static ApplicationInfo generateApplicationInfo(ApplicationInfo ai, int flags, PackageUserState state, int userId) {
        ApplicationInfo applicationInfo = null;
        if (ai != null && checkUseInstalledOrHidden(flags, state)) {
            applicationInfo = new ApplicationInfo(ai);
            if (userId != 0) {
                applicationInfo.uid = UserHandle.getUid(userId, applicationInfo.uid);
                applicationInfo.dataDir = PackageManager.getDataDirForUser(userId, applicationInfo.packageName);
            }
            if (state.stopped) {
                applicationInfo.flags |= AccessibilityNodeInfo.ACTION_SET_TEXT;
            } else {
                applicationInfo.flags &= -2097153;
            }
            updateApplicationInfo(applicationInfo, flags, state);
        }
        return applicationInfo;
    }

    public static final PermissionInfo generatePermissionInfo(Permission p, int flags) {
        if (p == null) {
            return null;
        }
        if ((flags & PARSE_IS_PRIVILEGED) == 0) {
            return p.info;
        }
        PermissionInfo pi = new PermissionInfo(p.info);
        pi.metaData = p.metaData;
        return pi;
    }

    public static final PermissionGroupInfo generatePermissionGroupInfo(PermissionGroup pg, int flags) {
        if (pg == null) {
            return null;
        }
        if ((flags & PARSE_IS_PRIVILEGED) == 0) {
            return pg.info;
        }
        PermissionGroupInfo pgi = new PermissionGroupInfo(pg.info);
        pgi.metaData = pg.metaData;
        return pgi;
    }

    public static final ActivityInfo generateActivityInfo(Activity a, int flags, PackageUserState state, int userId) {
        if (a == null || !checkUseInstalledOrHidden(flags, state)) {
            return null;
        }
        if (!copyNeeded(flags, a.owner, state, a.metaData, userId)) {
            return a.info;
        }
        ActivityInfo ai = new ActivityInfo(a.info);
        ai.metaData = a.metaData;
        ai.applicationInfo = generateApplicationInfo(a.owner, flags, state, userId);
        return ai;
    }

    public static final ActivityInfo generateActivityInfo(ActivityInfo ai, int flags, PackageUserState state, int userId) {
        if (ai == null || !checkUseInstalledOrHidden(flags, state)) {
            return null;
        }
        ActivityInfo ai2 = new ActivityInfo(ai);
        ai2.applicationInfo = generateApplicationInfo(ai2.applicationInfo, flags, state, userId);
        return ai2;
    }

    public static final ServiceInfo generateServiceInfo(Service s, int flags, PackageUserState state, int userId) {
        if (s == null || !checkUseInstalledOrHidden(flags, state)) {
            return null;
        }
        if (!copyNeeded(flags, s.owner, state, s.metaData, userId)) {
            return s.info;
        }
        ServiceInfo si = new ServiceInfo(s.info);
        si.metaData = s.metaData;
        si.applicationInfo = generateApplicationInfo(s.owner, flags, state, userId);
        return si;
    }

    public static final ProviderInfo generateProviderInfo(Provider p, int flags, PackageUserState state, int userId) {
        if (p == null) {
            return null;
        }
        if (!checkUseInstalledOrHidden(flags, state)) {
            return null;
        }
        if (!copyNeeded(flags, p.owner, state, p.metaData, userId) && ((flags & AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT) != 0 || p.info.uriPermissionPatterns == null)) {
            return p.info;
        }
        ProviderInfo pi = new ProviderInfo(p.info);
        pi.metaData = p.metaData;
        if ((flags & AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT) == 0) {
            pi.uriPermissionPatterns = null;
        }
        pi.applicationInfo = generateApplicationInfo(p.owner, flags, state, userId);
        return pi;
    }

    public static final InstrumentationInfo generateInstrumentationInfo(Instrumentation i, int flags) {
        if (i == null) {
            return null;
        }
        if ((flags & PARSE_IS_PRIVILEGED) == 0) {
            return i.info;
        }
        InstrumentationInfo ii = new InstrumentationInfo(i.info);
        ii.metaData = i.metaData;
        return ii;
    }

    public static void setCompatibilityModeEnabled(boolean compatibilityModeEnabled) {
        sCompatibilityModeEnabled = compatibilityModeEnabled;
    }

    public static long readFullyIgnoringContents(InputStream in) throws IOException {
        byte[] buffer = (byte[]) sBuffer.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[AccessibilityNodeInfo.ACTION_SCROLL_FORWARD];
        }
        int count = SDK_VERSION;
        while (true) {
            int n = in.read(buffer, SDK_VERSION, buffer.length);
            if (n != PARSE_DEFAULT_INSTALL_LOCATION) {
                count += n;
            } else {
                sBuffer.set(buffer);
                return (long) count;
            }
        }
    }

    public static void closeQuietly(StrictJarFile jarFile) {
        if (jarFile != null) {
            try {
                jarFile.close();
            } catch (Exception e) {
            }
        }
    }
}
