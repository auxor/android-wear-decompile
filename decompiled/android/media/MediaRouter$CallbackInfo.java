package android.media;

import android.media.MediaRouter.Callback;
import android.media.MediaRouter.RouteInfo;

class MediaRouter$CallbackInfo {
    public final Callback cb;
    public int flags;
    public final MediaRouter router;
    public int type;

    public MediaRouter$CallbackInfo(Callback cb, int type, int flags, MediaRouter router) {
        this.cb = cb;
        this.type = type;
        this.flags = flags;
        this.router = router;
    }

    public boolean filterRouteEvent(RouteInfo route) {
        return filterRouteEvent(route.mSupportedTypes);
    }

    public boolean filterRouteEvent(int supportedTypes) {
        return ((this.flags & 2) == 0 && (this.type & supportedTypes) == 0) ? false : true;
    }
}
