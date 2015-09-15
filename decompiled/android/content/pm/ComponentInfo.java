package android.content.pm;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.util.Printer;

public class ComponentInfo extends PackageItemInfo {
    public ApplicationInfo applicationInfo;
    public int descriptionRes;
    public boolean enabled;
    public boolean exported;
    public String processName;

    public ComponentInfo() {
        this.enabled = true;
        this.exported = false;
    }

    public ComponentInfo(ComponentInfo orig) {
        super((PackageItemInfo) orig);
        this.enabled = true;
        this.exported = false;
        this.applicationInfo = orig.applicationInfo;
        this.processName = orig.processName;
        this.descriptionRes = orig.descriptionRes;
        this.enabled = orig.enabled;
        this.exported = orig.exported;
    }

    public CharSequence loadLabel(PackageManager pm) {
        if (this.nonLocalizedLabel != null) {
            return this.nonLocalizedLabel;
        }
        CharSequence label;
        ApplicationInfo ai = this.applicationInfo;
        if (this.labelRes != 0) {
            label = pm.getText(this.packageName, this.labelRes, ai);
            if (label != null) {
                return label;
            }
        }
        if (ai.nonLocalizedLabel != null) {
            return ai.nonLocalizedLabel;
        }
        if (ai.labelRes != 0) {
            label = pm.getText(this.packageName, ai.labelRes, ai);
            if (label != null) {
                return label;
            }
        }
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled && this.applicationInfo.enabled;
    }

    public final int getIconResource() {
        return this.icon != 0 ? this.icon : this.applicationInfo.icon;
    }

    public final int getLogoResource() {
        return this.logo != 0 ? this.logo : this.applicationInfo.logo;
    }

    public final int getBannerResource() {
        return this.banner != 0 ? this.banner : this.applicationInfo.banner;
    }

    protected void dumpFront(Printer pw, String prefix) {
        super.dumpFront(pw, prefix);
        pw.println(prefix + "enabled=" + this.enabled + " exported=" + this.exported + " processName=" + this.processName);
        if (this.descriptionRes != 0) {
            pw.println(prefix + "description=" + this.descriptionRes);
        }
    }

    protected void dumpBack(Printer pw, String prefix) {
        if (this.applicationInfo != null) {
            pw.println(prefix + "ApplicationInfo:");
            this.applicationInfo.dump(pw, prefix + "  ");
        } else {
            pw.println(prefix + "ApplicationInfo: null");
        }
        super.dumpBack(pw, prefix);
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        int i;
        int i2 = 1;
        super.writeToParcel(dest, parcelableFlags);
        this.applicationInfo.writeToParcel(dest, parcelableFlags);
        dest.writeString(this.processName);
        dest.writeInt(this.descriptionRes);
        if (this.enabled) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.exported) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    protected ComponentInfo(Parcel source) {
        boolean z;
        boolean z2 = true;
        super(source);
        this.enabled = true;
        this.exported = false;
        this.applicationInfo = (ApplicationInfo) ApplicationInfo.CREATOR.createFromParcel(source);
        this.processName = source.readString();
        this.descriptionRes = source.readInt();
        if (source.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.enabled = z;
        if (source.readInt() == 0) {
            z2 = false;
        }
        this.exported = z2;
    }

    public Drawable loadDefaultIcon(PackageManager pm) {
        return this.applicationInfo.loadIcon(pm);
    }

    protected Drawable loadDefaultBanner(PackageManager pm) {
        return this.applicationInfo.loadBanner(pm);
    }

    protected Drawable loadDefaultLogo(PackageManager pm) {
        return this.applicationInfo.loadLogo(pm);
    }

    protected ApplicationInfo getApplicationInfo() {
        return this.applicationInfo;
    }
}
