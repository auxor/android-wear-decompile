package android.content.pm;

import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Printer;
import java.text.Collator;
import java.util.Comparator;

public class PackageItemInfo {
    public int banner;
    public int icon;
    public int labelRes;
    public int logo;
    public Bundle metaData;
    public String name;
    public CharSequence nonLocalizedLabel;
    public String packageName;
    public int showUserIcon;

    public static class DisplayNameComparator implements Comparator<PackageItemInfo> {
        private PackageManager mPM;
        private final Collator sCollator;

        public DisplayNameComparator(android.content.pm.PackageManager r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.content.pm.PackageItemInfo.DisplayNameComparator.<init>(android.content.pm.PackageManager):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.content.pm.PackageItemInfo.DisplayNameComparator.<init>(android.content.pm.PackageManager):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageItemInfo.DisplayNameComparator.<init>(android.content.pm.PackageManager):void");
        }

        public final int compare(android.content.pm.PackageItemInfo r1, android.content.pm.PackageItemInfo r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.content.pm.PackageItemInfo.DisplayNameComparator.compare(android.content.pm.PackageItemInfo, android.content.pm.PackageItemInfo):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.content.pm.PackageItemInfo.DisplayNameComparator.compare(android.content.pm.PackageItemInfo, android.content.pm.PackageItemInfo):int
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
            throw new UnsupportedOperationException("Method not decompiled: android.content.pm.PackageItemInfo.DisplayNameComparator.compare(android.content.pm.PackageItemInfo, android.content.pm.PackageItemInfo):int");
        }
    }

    public PackageItemInfo() {
        this.showUserIcon = UserHandle.USER_NULL;
    }

    public PackageItemInfo(PackageItemInfo orig) {
        this.name = orig.name;
        if (this.name != null) {
            this.name = this.name.trim();
        }
        this.packageName = orig.packageName;
        this.labelRes = orig.labelRes;
        this.nonLocalizedLabel = orig.nonLocalizedLabel;
        if (this.nonLocalizedLabel != null) {
            this.nonLocalizedLabel = this.nonLocalizedLabel.toString().trim();
        }
        this.icon = orig.icon;
        this.banner = orig.banner;
        this.logo = orig.logo;
        this.metaData = orig.metaData;
        this.showUserIcon = orig.showUserIcon;
    }

    public CharSequence loadLabel(PackageManager pm) {
        if (this.nonLocalizedLabel != null) {
            return this.nonLocalizedLabel;
        }
        if (this.labelRes != 0) {
            CharSequence label = pm.getText(this.packageName, this.labelRes, getApplicationInfo());
            if (label != null) {
                return label.toString().trim();
            }
        }
        if (this.name != null) {
            return this.name;
        }
        return this.packageName;
    }

    public Drawable loadIcon(PackageManager pm) {
        return pm.loadItemIcon(this, getApplicationInfo());
    }

    public Drawable loadUnbadgedIcon(PackageManager pm) {
        return pm.loadUnbadgedItemIcon(this, getApplicationInfo());
    }

    public Drawable loadBanner(PackageManager pm) {
        if (this.banner != 0) {
            Drawable dr = pm.getDrawable(this.packageName, this.banner, getApplicationInfo());
            if (dr != null) {
                return dr;
            }
        }
        return loadDefaultBanner(pm);
    }

    public Drawable loadDefaultIcon(PackageManager pm) {
        return pm.getDefaultActivityIcon();
    }

    protected Drawable loadDefaultBanner(PackageManager pm) {
        return null;
    }

    public Drawable loadLogo(PackageManager pm) {
        if (this.logo != 0) {
            Drawable d = pm.getDrawable(this.packageName, this.logo, getApplicationInfo());
            if (d != null) {
                return d;
            }
        }
        return loadDefaultLogo(pm);
    }

    protected Drawable loadDefaultLogo(PackageManager pm) {
        return null;
    }

    public XmlResourceParser loadXmlMetaData(PackageManager pm, String name) {
        if (this.metaData != null) {
            int resid = this.metaData.getInt(name);
            if (resid != 0) {
                return pm.getXml(this.packageName, resid, getApplicationInfo());
            }
        }
        return null;
    }

    protected void dumpFront(Printer pw, String prefix) {
        if (this.name != null) {
            pw.println(prefix + "name=" + this.name);
        }
        pw.println(prefix + "packageName=" + this.packageName);
        if (this.labelRes != 0 || this.nonLocalizedLabel != null || this.icon != 0 || this.banner != 0) {
            pw.println(prefix + "labelRes=0x" + Integer.toHexString(this.labelRes) + " nonLocalizedLabel=" + this.nonLocalizedLabel + " icon=0x" + Integer.toHexString(this.icon) + " banner=0x" + Integer.toHexString(this.banner));
        }
    }

    protected void dumpBack(Printer pw, String prefix) {
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeString(this.name);
        dest.writeString(this.packageName);
        dest.writeInt(this.labelRes);
        TextUtils.writeToParcel(this.nonLocalizedLabel, dest, parcelableFlags);
        dest.writeInt(this.icon);
        dest.writeInt(this.logo);
        dest.writeBundle(this.metaData);
        dest.writeInt(this.banner);
        dest.writeInt(this.showUserIcon);
    }

    protected PackageItemInfo(Parcel source) {
        this.name = source.readString();
        this.packageName = source.readString();
        this.labelRes = source.readInt();
        this.nonLocalizedLabel = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        this.icon = source.readInt();
        this.logo = source.readInt();
        this.metaData = source.readBundle();
        this.banner = source.readInt();
        this.showUserIcon = source.readInt();
    }

    protected ApplicationInfo getApplicationInfo() {
        return null;
    }
}
