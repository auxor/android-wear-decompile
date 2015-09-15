package android.service.notification;

import android.app.INotificationManager;
import android.app.Notification.Builder;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.INotificationListener.Stub;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import java.util.Collections;
import java.util.List;

public abstract class NotificationListenerService extends Service {
    public static final int HINT_HOST_DISABLE_EFFECTS = 1;
    public static final int INTERRUPTION_FILTER_ALL = 1;
    public static final int INTERRUPTION_FILTER_NONE = 3;
    public static final int INTERRUPTION_FILTER_PRIORITY = 2;
    public static final String SERVICE_INTERFACE = "android.service.notification.NotificationListenerService";
    public static final int TRIM_FULL = 0;
    public static final int TRIM_LIGHT = 1;
    private final String TAG;
    private int mCurrentUser;
    private INotificationManager mNoMan;
    private RankingMap mRankingMap;
    private Context mSystemContext;
    private INotificationListenerWrapper mWrapper;

    private class INotificationListenerWrapper extends Stub {
        private INotificationListenerWrapper() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNotificationPosted(android.service.notification.IStatusBarNotificationHolder r7, android.service.notification.NotificationRankingUpdate r8) {
            /*
            r6 = this;
            r1 = r7.get();	 Catch:{ RemoteException -> 0x002a }
            r3 = android.service.notification.NotificationListenerService.this;
            r3 = r3.getContext();
            r4 = r1.getNotification();
            android.app.Notification.Builder.rebuild(r3, r4);
            r3 = android.service.notification.NotificationListenerService.this;
            r4 = r3.mWrapper;
            monitor-enter(r4);
            r3 = android.service.notification.NotificationListenerService.this;	 Catch:{ all -> 0x0045 }
            r3.applyUpdate(r8);	 Catch:{ all -> 0x0045 }
            r3 = android.service.notification.NotificationListenerService.this;	 Catch:{ Throwable -> 0x0038 }
            r5 = android.service.notification.NotificationListenerService.this;	 Catch:{ Throwable -> 0x0038 }
            r5 = r5.mRankingMap;	 Catch:{ Throwable -> 0x0038 }
            r3.onNotificationPosted(r1, r5);	 Catch:{ Throwable -> 0x0038 }
        L_0x0028:
            monitor-exit(r4);	 Catch:{ all -> 0x0045 }
        L_0x0029:
            return;
        L_0x002a:
            r0 = move-exception;
            r3 = android.service.notification.NotificationListenerService.this;
            r3 = r3.TAG;
            r4 = "onNotificationPosted: Error receiving StatusBarNotification";
            android.util.Log.w(r3, r4, r0);
            goto L_0x0029;
        L_0x0038:
            r2 = move-exception;
            r3 = android.service.notification.NotificationListenerService.this;	 Catch:{ all -> 0x0045 }
            r3 = r3.TAG;	 Catch:{ all -> 0x0045 }
            r5 = "Error running onNotificationPosted";
            android.util.Log.w(r3, r5, r2);	 Catch:{ all -> 0x0045 }
            goto L_0x0028;
        L_0x0045:
            r3 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0045 }
            throw r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.service.notification.NotificationListenerService.INotificationListenerWrapper.onNotificationPosted(android.service.notification.IStatusBarNotificationHolder, android.service.notification.NotificationRankingUpdate):void");
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNotificationRemoved(android.service.notification.IStatusBarNotificationHolder r7, android.service.notification.NotificationRankingUpdate r8) {
            /*
            r6 = this;
            r1 = r7.get();	 Catch:{ RemoteException -> 0x001d }
            r3 = android.service.notification.NotificationListenerService.this;
            r4 = r3.mWrapper;
            monitor-enter(r4);
            r3 = android.service.notification.NotificationListenerService.this;	 Catch:{ all -> 0x0038 }
            r3.applyUpdate(r8);	 Catch:{ all -> 0x0038 }
            r3 = android.service.notification.NotificationListenerService.this;	 Catch:{ Throwable -> 0x002b }
            r5 = android.service.notification.NotificationListenerService.this;	 Catch:{ Throwable -> 0x002b }
            r5 = r5.mRankingMap;	 Catch:{ Throwable -> 0x002b }
            r3.onNotificationRemoved(r1, r5);	 Catch:{ Throwable -> 0x002b }
        L_0x001b:
            monitor-exit(r4);	 Catch:{ all -> 0x0038 }
        L_0x001c:
            return;
        L_0x001d:
            r0 = move-exception;
            r3 = android.service.notification.NotificationListenerService.this;
            r3 = r3.TAG;
            r4 = "onNotificationRemoved: Error receiving StatusBarNotification";
            android.util.Log.w(r3, r4, r0);
            goto L_0x001c;
        L_0x002b:
            r2 = move-exception;
            r3 = android.service.notification.NotificationListenerService.this;	 Catch:{ all -> 0x0038 }
            r3 = r3.TAG;	 Catch:{ all -> 0x0038 }
            r5 = "Error running onNotificationRemoved";
            android.util.Log.w(r3, r5, r2);	 Catch:{ all -> 0x0038 }
            goto L_0x001b;
        L_0x0038:
            r3 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0038 }
            throw r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.service.notification.NotificationListenerService.INotificationListenerWrapper.onNotificationRemoved(android.service.notification.IStatusBarNotificationHolder, android.service.notification.NotificationRankingUpdate):void");
        }

        public void onListenerConnected(NotificationRankingUpdate update) {
            synchronized (NotificationListenerService.this.mWrapper) {
                NotificationListenerService.this.applyUpdate(update);
                try {
                    NotificationListenerService.this.onListenerConnected();
                } catch (Throwable t) {
                    Log.w(NotificationListenerService.this.TAG, "Error running onListenerConnected", t);
                }
            }
        }

        public void onNotificationRankingUpdate(NotificationRankingUpdate update) throws RemoteException {
            synchronized (NotificationListenerService.this.mWrapper) {
                NotificationListenerService.this.applyUpdate(update);
                try {
                    NotificationListenerService.this.onNotificationRankingUpdate(NotificationListenerService.this.mRankingMap);
                } catch (Throwable t) {
                    Log.w(NotificationListenerService.this.TAG, "Error running onNotificationRankingUpdate", t);
                }
            }
        }

        public void onListenerHintsChanged(int hints) throws RemoteException {
            try {
                NotificationListenerService.this.onListenerHintsChanged(hints);
            } catch (Throwable t) {
                Log.w(NotificationListenerService.this.TAG, "Error running onListenerHintsChanged", t);
            }
        }

        public void onInterruptionFilterChanged(int interruptionFilter) throws RemoteException {
            try {
                NotificationListenerService.this.onInterruptionFilterChanged(interruptionFilter);
            } catch (Throwable t) {
                Log.w(NotificationListenerService.this.TAG, "Error running onInterruptionFilterChanged", t);
            }
        }
    }

    public static class Ranking {
        public static final int VISIBILITY_NO_OVERRIDE = -1000;
        private boolean mIsAmbient;
        private String mKey;
        private boolean mMatchesInterruptionFilter;
        private int mRank;
        private int mVisibilityOverride;

        public Ranking() {
            this.mRank = -1;
        }

        public String getKey() {
            return this.mKey;
        }

        public int getRank() {
            return this.mRank;
        }

        public boolean isAmbient() {
            return this.mIsAmbient;
        }

        public int getVisibilityOverride() {
            return this.mVisibilityOverride;
        }

        public boolean matchesInterruptionFilter() {
            return this.mMatchesInterruptionFilter;
        }

        private void populate(String key, int rank, boolean isAmbient, boolean matchesInterruptionFilter, int visibilityOverride) {
            this.mKey = key;
            this.mRank = rank;
            this.mIsAmbient = isAmbient;
            this.mMatchesInterruptionFilter = matchesInterruptionFilter;
            this.mVisibilityOverride = visibilityOverride;
        }
    }

    public static class RankingMap implements Parcelable {
        public static final Creator<RankingMap> CREATOR;
        private ArraySet<Object> mIntercepted;
        private final NotificationRankingUpdate mRankingUpdate;
        private ArrayMap<String, Integer> mRanks;
        private ArrayMap<String, Integer> mVisibilityOverrides;

        private RankingMap(NotificationRankingUpdate rankingUpdate) {
            this.mRankingUpdate = rankingUpdate;
        }

        public String[] getOrderedKeys() {
            return this.mRankingUpdate.getOrderedKeys();
        }

        public boolean getRanking(String key, Ranking outRanking) {
            boolean z;
            int rank = getRank(key);
            boolean isAmbient = isAmbient(key);
            if (isIntercepted(key)) {
                z = false;
            } else {
                z = true;
            }
            outRanking.populate(key, rank, isAmbient, z, getVisibilityOverride(key));
            if (rank >= 0) {
                return true;
            }
            return false;
        }

        private int getRank(String key) {
            synchronized (this) {
                if (this.mRanks == null) {
                    buildRanksLocked();
                }
            }
            Integer rank = (Integer) this.mRanks.get(key);
            return rank != null ? rank.intValue() : -1;
        }

        private boolean isAmbient(String key) {
            int firstAmbientIndex = this.mRankingUpdate.getFirstAmbientIndex();
            if (firstAmbientIndex < 0) {
                return false;
            }
            int rank = getRank(key);
            if (rank < 0 || rank < firstAmbientIndex) {
                return false;
            }
            return true;
        }

        private boolean isIntercepted(String key) {
            synchronized (this) {
                if (this.mIntercepted == null) {
                    buildInterceptedSetLocked();
                }
            }
            return this.mIntercepted.contains(key);
        }

        private int getVisibilityOverride(String key) {
            synchronized (this) {
                if (this.mVisibilityOverrides == null) {
                    buildVisibilityOverridesLocked();
                }
            }
            Integer overide = (Integer) this.mVisibilityOverrides.get(key);
            if (overide == null) {
                return Ranking.VISIBILITY_NO_OVERRIDE;
            }
            return overide.intValue();
        }

        private void buildRanksLocked() {
            String[] orderedKeys = this.mRankingUpdate.getOrderedKeys();
            this.mRanks = new ArrayMap(orderedKeys.length);
            for (int i = NotificationListenerService.TRIM_FULL; i < orderedKeys.length; i += NotificationListenerService.TRIM_LIGHT) {
                this.mRanks.put(orderedKeys[i], Integer.valueOf(i));
            }
        }

        private void buildInterceptedSetLocked() {
            String[] dndInterceptedKeys = this.mRankingUpdate.getInterceptedKeys();
            this.mIntercepted = new ArraySet(dndInterceptedKeys.length);
            Collections.addAll(this.mIntercepted, dndInterceptedKeys);
        }

        private void buildVisibilityOverridesLocked() {
            Bundle visibilityBundle = this.mRankingUpdate.getVisibilityOverrides();
            this.mVisibilityOverrides = new ArrayMap(visibilityBundle.size());
            for (String key : visibilityBundle.keySet()) {
                this.mVisibilityOverrides.put(key, Integer.valueOf(visibilityBundle.getInt(key)));
            }
        }

        public int describeContents() {
            return NotificationListenerService.TRIM_FULL;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.mRankingUpdate, flags);
        }

        static {
            CREATOR = new Creator<RankingMap>() {
                public RankingMap createFromParcel(Parcel source) {
                    return new RankingMap(null);
                }

                public RankingMap[] newArray(int size) {
                    return new RankingMap[size];
                }
            };
        }
    }

    public NotificationListenerService() {
        this.TAG = NotificationListenerService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";
        this.mWrapper = null;
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
    }

    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        onNotificationPosted(sbn);
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        onNotificationRemoved(sbn);
    }

    public void onListenerConnected() {
    }

    public void onNotificationRankingUpdate(RankingMap rankingMap) {
    }

    public void onListenerHintsChanged(int hints) {
    }

    public void onInterruptionFilterChanged(int interruptionFilter) {
    }

    private final INotificationManager getNotificationInterface() {
        if (this.mNoMan == null) {
            this.mNoMan = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        }
        return this.mNoMan;
    }

    public final void cancelNotification(String pkg, String tag, int id) {
        if (isBound()) {
            try {
                getNotificationInterface().cancelNotificationFromListener(this.mWrapper, pkg, tag, id);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
    }

    public final void cancelNotification(String key) {
        if (isBound()) {
            try {
                INotificationManager notificationInterface = getNotificationInterface();
                INotificationListener iNotificationListener = this.mWrapper;
                String[] strArr = new String[TRIM_LIGHT];
                strArr[TRIM_FULL] = key;
                notificationInterface.cancelNotificationsFromListener(iNotificationListener, strArr);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
    }

    public final void cancelAllNotifications() {
        cancelNotifications(null);
    }

    public final void cancelNotifications(String[] keys) {
        if (isBound()) {
            try {
                getNotificationInterface().cancelNotificationsFromListener(this.mWrapper, keys);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
    }

    public final void setOnNotificationPostedTrim(int trim) {
        if (isBound()) {
            try {
                getNotificationInterface().setOnNotificationPostedTrimFromListener(this.mWrapper, trim);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
    }

    public StatusBarNotification[] getActiveNotifications() {
        return getActiveNotifications(null, TRIM_FULL);
    }

    public StatusBarNotification[] getActiveNotifications(int trim) {
        return getActiveNotifications(null, trim);
    }

    public StatusBarNotification[] getActiveNotifications(String[] keys) {
        return getActiveNotifications(keys, TRIM_FULL);
    }

    public StatusBarNotification[] getActiveNotifications(String[] keys, int trim) {
        if (!isBound()) {
            return null;
        }
        try {
            List<StatusBarNotification> list = getNotificationInterface().getActiveNotificationsFromListener(this.mWrapper, keys, trim).getList();
            int N = list.size();
            for (int i = TRIM_FULL; i < N; i += TRIM_LIGHT) {
                Builder.rebuild(getContext(), ((StatusBarNotification) list.get(i)).getNotification());
            }
            return (StatusBarNotification[]) list.toArray(new StatusBarNotification[N]);
        } catch (RemoteException ex) {
            Log.v(this.TAG, "Unable to contact notification manager", ex);
            return null;
        }
    }

    public final int getCurrentListenerHints() {
        int i = TRIM_FULL;
        if (isBound()) {
            try {
                i = getNotificationInterface().getHintsFromListener(this.mWrapper);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
        return i;
    }

    public final int getCurrentInterruptionFilter() {
        int i = TRIM_FULL;
        if (isBound()) {
            try {
                i = getNotificationInterface().getInterruptionFilterFromListener(this.mWrapper);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
        return i;
    }

    public final void requestListenerHints(int hints) {
        if (isBound()) {
            try {
                getNotificationInterface().requestHintsFromListener(this.mWrapper, hints);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
    }

    public final void requestInterruptionFilter(int interruptionFilter) {
        if (isBound()) {
            try {
                getNotificationInterface().requestInterruptionFilterFromListener(this.mWrapper, interruptionFilter);
            } catch (RemoteException ex) {
                Log.v(this.TAG, "Unable to contact notification manager", ex);
            }
        }
    }

    public RankingMap getCurrentRanking() {
        return this.mRankingMap;
    }

    public IBinder onBind(Intent intent) {
        if (this.mWrapper == null) {
            this.mWrapper = new INotificationListenerWrapper();
        }
        return this.mWrapper;
    }

    private boolean isBound() {
        if (this.mWrapper != null) {
            return true;
        }
        Log.w(this.TAG, "Notification listener service not yet bound.");
        return false;
    }

    public void registerAsSystemService(Context context, ComponentName componentName, int currentUser) throws RemoteException {
        this.mSystemContext = context;
        if (this.mWrapper == null) {
            this.mWrapper = new INotificationListenerWrapper();
        }
        getNotificationInterface().registerListener(this.mWrapper, componentName, currentUser);
        this.mCurrentUser = currentUser;
    }

    public void unregisterAsSystemService() throws RemoteException {
        if (this.mWrapper != null) {
            getNotificationInterface().unregisterListener(this.mWrapper, this.mCurrentUser);
        }
    }

    private void applyUpdate(NotificationRankingUpdate update) {
        this.mRankingMap = new RankingMap(null);
    }

    private Context getContext() {
        if (this.mSystemContext != null) {
            return this.mSystemContext;
        }
        return this;
    }
}
