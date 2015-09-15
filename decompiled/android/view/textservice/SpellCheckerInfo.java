package android.view.textservice;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.net.ProxyInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public final class SpellCheckerInfo implements Parcelable {
    public static final Creator<SpellCheckerInfo> CREATOR;
    private static final String TAG;
    private final String mId;
    private final int mLabel;
    private final ResolveInfo mService;
    private final String mSettingsActivityName;
    private final ArrayList<SpellCheckerSubtype> mSubtypes;

    static {
        TAG = SpellCheckerInfo.class.getSimpleName();
        CREATOR = new Creator<SpellCheckerInfo>() {
            public SpellCheckerInfo createFromParcel(Parcel source) {
                return new SpellCheckerInfo(source);
            }

            public SpellCheckerInfo[] newArray(int size) {
                return new SpellCheckerInfo[size];
            }
        };
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public SpellCheckerInfo(android.content.Context r22, android.content.pm.ResolveInfo r23) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r21 = this;
        r21.<init>();
        r18 = new java.util.ArrayList;
        r18.<init>();
        r0 = r18;
        r1 = r21;
        r1.mSubtypes = r0;
        r0 = r23;
        r1 = r21;
        r1.mService = r0;
        r0 = r23;
        r14 = r0.serviceInfo;
        r18 = new android.content.ComponentName;
        r0 = r14.packageName;
        r19 = r0;
        r0 = r14.name;
        r20 = r0;
        r18.<init>(r19, r20);
        r18 = r18.flattenToShortString();
        r0 = r18;
        r1 = r21;
        r1.mId = r0;
        r10 = r22.getPackageManager();
        r7 = 0;
        r13 = 0;
        r9 = 0;
        r18 = "android.view.textservice.scs";
        r0 = r18;
        r9 = r14.loadXmlMetaData(r10, r0);	 Catch:{ Exception -> 0x0048 }
        if (r9 != 0) goto L_0x0087;
    L_0x0040:
        r18 = new org.xmlpull.v1.XmlPullParserException;	 Catch:{ Exception -> 0x0048 }
        r19 = "No android.view.textservice.scs meta-data";
        r18.<init>(r19);	 Catch:{ Exception -> 0x0048 }
        throw r18;	 Catch:{ Exception -> 0x0048 }
    L_0x0048:
        r6 = move-exception;
        r18 = TAG;	 Catch:{ all -> 0x0080 }
        r19 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0080 }
        r19.<init>();	 Catch:{ all -> 0x0080 }
        r20 = "Caught exception: ";
        r19 = r19.append(r20);	 Catch:{ all -> 0x0080 }
        r0 = r19;
        r19 = r0.append(r6);	 Catch:{ all -> 0x0080 }
        r19 = r19.toString();	 Catch:{ all -> 0x0080 }
        android.util.Slog.e(r18, r19);	 Catch:{ all -> 0x0080 }
        r18 = new org.xmlpull.v1.XmlPullParserException;	 Catch:{ all -> 0x0080 }
        r19 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0080 }
        r19.<init>();	 Catch:{ all -> 0x0080 }
        r20 = "Unable to create context for: ";
        r19 = r19.append(r20);	 Catch:{ all -> 0x0080 }
        r0 = r14.packageName;	 Catch:{ all -> 0x0080 }
        r20 = r0;
        r19 = r19.append(r20);	 Catch:{ all -> 0x0080 }
        r19 = r19.toString();	 Catch:{ all -> 0x0080 }
        r18.<init>(r19);	 Catch:{ all -> 0x0080 }
        throw r18;	 Catch:{ all -> 0x0080 }
    L_0x0080:
        r18 = move-exception;
        if (r9 == 0) goto L_0x0086;
    L_0x0083:
        r9.close();
    L_0x0086:
        throw r18;
    L_0x0087:
        r0 = r14.applicationInfo;	 Catch:{ Exception -> 0x0048 }
        r18 = r0;
        r0 = r18;
        r11 = r10.getResourcesForApplication(r0);	 Catch:{ Exception -> 0x0048 }
        r4 = android.util.Xml.asAttributeSet(r9);	 Catch:{ Exception -> 0x0048 }
    L_0x0095:
        r17 = r9.next();	 Catch:{ Exception -> 0x0048 }
        r18 = 1;
        r0 = r17;
        r1 = r18;
        if (r0 == r1) goto L_0x00a9;
    L_0x00a1:
        r18 = 2;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x0095;
    L_0x00a9:
        r8 = r9.getName();	 Catch:{ Exception -> 0x0048 }
        r18 = "spell-checker";
        r0 = r18;
        r18 = r0.equals(r8);	 Catch:{ Exception -> 0x0048 }
        if (r18 != 0) goto L_0x00c0;
    L_0x00b8:
        r18 = new org.xmlpull.v1.XmlPullParserException;	 Catch:{ Exception -> 0x0048 }
        r19 = "Meta-data does not start with spell-checker tag";
        r18.<init>(r19);	 Catch:{ Exception -> 0x0048 }
        throw r18;	 Catch:{ Exception -> 0x0048 }
    L_0x00c0:
        r18 = com.android.internal.R.styleable.SpellChecker;	 Catch:{ Exception -> 0x0048 }
        r0 = r18;
        r12 = r11.obtainAttributes(r4, r0);	 Catch:{ Exception -> 0x0048 }
        r18 = 0;
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r7 = r12.getResourceId(r0, r1);	 Catch:{ Exception -> 0x0048 }
        r18 = 1;
        r0 = r18;
        r13 = r12.getString(r0);	 Catch:{ Exception -> 0x0048 }
        r12.recycle();	 Catch:{ Exception -> 0x0048 }
        r5 = r9.getDepth();	 Catch:{ Exception -> 0x0048 }
    L_0x00e3:
        r17 = r9.next();	 Catch:{ Exception -> 0x0048 }
        r18 = 3;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x00f7;
    L_0x00ef:
        r18 = r9.getDepth();	 Catch:{ Exception -> 0x0048 }
        r0 = r18;
        if (r0 <= r5) goto L_0x015b;
    L_0x00f7:
        r18 = 1;
        r0 = r17;
        r1 = r18;
        if (r0 == r1) goto L_0x015b;
    L_0x00ff:
        r18 = 2;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x00e3;
    L_0x0107:
        r16 = r9.getName();	 Catch:{ Exception -> 0x0048 }
        r18 = "subtype";
        r0 = r18;
        r1 = r16;
        r18 = r0.equals(r1);	 Catch:{ Exception -> 0x0048 }
        if (r18 != 0) goto L_0x0120;
    L_0x0118:
        r18 = new org.xmlpull.v1.XmlPullParserException;	 Catch:{ Exception -> 0x0048 }
        r19 = "Meta-data in spell-checker does not start with subtype tag";
        r18.<init>(r19);	 Catch:{ Exception -> 0x0048 }
        throw r18;	 Catch:{ Exception -> 0x0048 }
    L_0x0120:
        r18 = com.android.internal.R.styleable.SpellChecker_Subtype;	 Catch:{ Exception -> 0x0048 }
        r0 = r18;
        r3 = r11.obtainAttributes(r4, r0);	 Catch:{ Exception -> 0x0048 }
        r15 = new android.view.textservice.SpellCheckerSubtype;	 Catch:{ Exception -> 0x0048 }
        r18 = 0;
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r18 = r3.getResourceId(r0, r1);	 Catch:{ Exception -> 0x0048 }
        r19 = 1;
        r0 = r19;
        r19 = r3.getString(r0);	 Catch:{ Exception -> 0x0048 }
        r20 = 2;
        r0 = r20;
        r20 = r3.getString(r0);	 Catch:{ Exception -> 0x0048 }
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r15.<init>(r0, r1, r2);	 Catch:{ Exception -> 0x0048 }
        r0 = r21;
        r0 = r0.mSubtypes;	 Catch:{ Exception -> 0x0048 }
        r18 = r0;
        r0 = r18;
        r0.add(r15);	 Catch:{ Exception -> 0x0048 }
        goto L_0x00e3;
    L_0x015b:
        if (r9 == 0) goto L_0x0160;
    L_0x015d:
        r9.close();
    L_0x0160:
        r0 = r21;
        r0.mLabel = r7;
        r0 = r21;
        r0.mSettingsActivityName = r13;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.textservice.SpellCheckerInfo.<init>(android.content.Context, android.content.pm.ResolveInfo):void");
    }

    public SpellCheckerInfo(Parcel source) {
        this.mSubtypes = new ArrayList();
        this.mLabel = source.readInt();
        this.mId = source.readString();
        this.mSettingsActivityName = source.readString();
        this.mService = (ResolveInfo) ResolveInfo.CREATOR.createFromParcel(source);
        source.readTypedList(this.mSubtypes, SpellCheckerSubtype.CREATOR);
    }

    public String getId() {
        return this.mId;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mLabel);
        dest.writeString(this.mId);
        dest.writeString(this.mSettingsActivityName);
        this.mService.writeToParcel(dest, flags);
        dest.writeTypedList(this.mSubtypes);
    }

    public CharSequence loadLabel(PackageManager pm) {
        if (this.mLabel == 0 || pm == null) {
            return ProxyInfo.LOCAL_EXCL_LIST;
        }
        return pm.getText(getPackageName(), this.mLabel, this.mService.serviceInfo.applicationInfo);
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mService.loadIcon(pm);
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public int getSubtypeCount() {
        return this.mSubtypes.size();
    }

    public SpellCheckerSubtype getSubtypeAt(int index) {
        return (SpellCheckerSubtype) this.mSubtypes.get(index);
    }

    public int describeContents() {
        return 0;
    }
}
