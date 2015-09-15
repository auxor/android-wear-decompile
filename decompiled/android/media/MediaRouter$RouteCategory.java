package android.media;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaRouter.RouteInfo;
import java.util.ArrayList;
import java.util.List;

public class MediaRouter$RouteCategory {
    final boolean mGroupable;
    boolean mIsSystem;
    CharSequence mName;
    int mNameResId;
    int mTypes;

    MediaRouter$RouteCategory(CharSequence name, int types, boolean groupable) {
        this.mName = name;
        this.mTypes = types;
        this.mGroupable = groupable;
    }

    MediaRouter$RouteCategory(int nameResId, int types, boolean groupable) {
        this.mNameResId = nameResId;
        this.mTypes = types;
        this.mGroupable = groupable;
    }

    public CharSequence getName() {
        return getName(MediaRouter.sStatic.mResources);
    }

    public CharSequence getName(Context context) {
        return getName(context.getResources());
    }

    CharSequence getName(Resources res) {
        if (this.mNameResId != 0) {
            return res.getText(this.mNameResId);
        }
        return this.mName;
    }

    public List<RouteInfo> getRoutes(List<RouteInfo> out) {
        if (out == null) {
            out = new ArrayList();
        } else {
            out.clear();
        }
        int count = MediaRouter.getRouteCountStatic();
        for (int i = 0; i < count; i++) {
            RouteInfo route = MediaRouter.getRouteAtStatic(i);
            if (route.mCategory == this) {
                out.add(route);
            }
        }
        return out;
    }

    public int getSupportedTypes() {
        return this.mTypes;
    }

    public boolean isGroupable() {
        return this.mGroupable;
    }

    public boolean isSystem() {
        return this.mIsSystem;
    }

    public String toString() {
        return "RouteCategory{ name=" + this.mName + " types=" + MediaRouter.typesToString(this.mTypes) + " groupable=" + this.mGroupable + " }";
    }
}
