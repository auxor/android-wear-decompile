package android.view.accessibility;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Pools.SynchronizedPool;
import java.util.ArrayList;
import java.util.List;

public final class AccessibilityEvent extends AccessibilityRecord implements Parcelable {
    public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 4;
    public static final int CONTENT_CHANGE_TYPE_SUBTREE = 1;
    public static final int CONTENT_CHANGE_TYPE_TEXT = 2;
    public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0;
    public static final Creator<AccessibilityEvent> CREATOR;
    private static final boolean DEBUG = false;
    public static final int INVALID_POSITION = -1;
    private static final int MAX_POOL_SIZE = 10;
    @Deprecated
    public static final int MAX_TEXT_LENGTH = 500;
    public static final int TYPES_ALL_MASK = -1;
    public static final int TYPE_ANNOUNCEMENT = 16384;
    public static final int TYPE_GESTURE_DETECTION_END = 524288;
    public static final int TYPE_GESTURE_DETECTION_START = 262144;
    public static final int TYPE_NOTIFICATION_STATE_CHANGED = 64;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 1024;
    public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 512;
    public static final int TYPE_TOUCH_INTERACTION_END = 2097152;
    public static final int TYPE_TOUCH_INTERACTION_START = 1048576;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 32768;
    public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 65536;
    public static final int TYPE_VIEW_CLICKED = 1;
    public static final int TYPE_VIEW_FOCUSED = 8;
    public static final int TYPE_VIEW_HOVER_ENTER = 128;
    public static final int TYPE_VIEW_HOVER_EXIT = 256;
    public static final int TYPE_VIEW_LONG_CLICKED = 2;
    public static final int TYPE_VIEW_SCROLLED = 4096;
    public static final int TYPE_VIEW_SELECTED = 4;
    public static final int TYPE_VIEW_TEXT_CHANGED = 16;
    public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 8192;
    public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 131072;
    public static final int TYPE_WINDOWS_CHANGED = 4194304;
    public static final int TYPE_WINDOW_CONTENT_CHANGED = 2048;
    public static final int TYPE_WINDOW_STATE_CHANGED = 32;
    private static final SynchronizedPool<AccessibilityEvent> sPool;
    int mAction;
    int mContentChangeTypes;
    private long mEventTime;
    private int mEventType;
    int mMovementGranularity;
    private CharSequence mPackageName;
    private ArrayList<AccessibilityRecord> mRecords;

    static {
        sPool = new SynchronizedPool(MAX_POOL_SIZE);
        CREATOR = new Creator<AccessibilityEvent>() {
            public AccessibilityEvent createFromParcel(Parcel parcel) {
                AccessibilityEvent event = AccessibilityEvent.obtain();
                event.initFromParcel(parcel);
                return event;
            }

            public AccessibilityEvent[] newArray(int size) {
                return new AccessibilityEvent[size];
            }
        };
    }

    private AccessibilityEvent() {
    }

    void init(AccessibilityEvent event) {
        super.init(event);
        this.mEventType = event.mEventType;
        this.mMovementGranularity = event.mMovementGranularity;
        this.mAction = event.mAction;
        this.mContentChangeTypes = event.mContentChangeTypes;
        this.mEventTime = event.mEventTime;
        this.mPackageName = event.mPackageName;
    }

    public void setSealed(boolean sealed) {
        super.setSealed(sealed);
        List<AccessibilityRecord> records = this.mRecords;
        if (records != null) {
            int recordCount = records.size();
            for (int i = CONTENT_CHANGE_TYPE_UNDEFINED; i < recordCount; i += TYPE_VIEW_CLICKED) {
                ((AccessibilityRecord) records.get(i)).setSealed(sealed);
            }
        }
    }

    public int getRecordCount() {
        return this.mRecords == null ? CONTENT_CHANGE_TYPE_UNDEFINED : this.mRecords.size();
    }

    public void appendRecord(AccessibilityRecord record) {
        enforceNotSealed();
        if (this.mRecords == null) {
            this.mRecords = new ArrayList();
        }
        this.mRecords.add(record);
    }

    public AccessibilityRecord getRecord(int index) {
        if (this.mRecords != null) {
            return (AccessibilityRecord) this.mRecords.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is 0");
    }

    public int getEventType() {
        return this.mEventType;
    }

    public int getContentChangeTypes() {
        return this.mContentChangeTypes;
    }

    public void setContentChangeTypes(int changeTypes) {
        enforceNotSealed();
        this.mContentChangeTypes = changeTypes;
    }

    public void setEventType(int eventType) {
        enforceNotSealed();
        this.mEventType = eventType;
    }

    public long getEventTime() {
        return this.mEventTime;
    }

    public void setEventTime(long eventTime) {
        enforceNotSealed();
        this.mEventTime = eventTime;
    }

    public CharSequence getPackageName() {
        return this.mPackageName;
    }

    public void setPackageName(CharSequence packageName) {
        enforceNotSealed();
        this.mPackageName = packageName;
    }

    public void setMovementGranularity(int granularity) {
        enforceNotSealed();
        this.mMovementGranularity = granularity;
    }

    public int getMovementGranularity() {
        return this.mMovementGranularity;
    }

    public void setAction(int action) {
        enforceNotSealed();
        this.mAction = action;
    }

    public int getAction() {
        return this.mAction;
    }

    public static AccessibilityEvent obtain(int eventType) {
        AccessibilityEvent event = obtain();
        event.setEventType(eventType);
        return event;
    }

    public static AccessibilityEvent obtain(AccessibilityEvent event) {
        AccessibilityEvent eventClone = obtain();
        eventClone.init(event);
        if (event.mRecords != null) {
            int recordCount = event.mRecords.size();
            eventClone.mRecords = new ArrayList(recordCount);
            for (int i = CONTENT_CHANGE_TYPE_UNDEFINED; i < recordCount; i += TYPE_VIEW_CLICKED) {
                eventClone.mRecords.add(AccessibilityRecord.obtain((AccessibilityRecord) event.mRecords.get(i)));
            }
        }
        return eventClone;
    }

    public static AccessibilityEvent obtain() {
        AccessibilityEvent event = (AccessibilityEvent) sPool.acquire();
        return event != null ? event : new AccessibilityEvent();
    }

    public void recycle() {
        clear();
        sPool.release(this);
    }

    protected void clear() {
        super.clear();
        this.mEventType = CONTENT_CHANGE_TYPE_UNDEFINED;
        this.mMovementGranularity = CONTENT_CHANGE_TYPE_UNDEFINED;
        this.mAction = CONTENT_CHANGE_TYPE_UNDEFINED;
        this.mContentChangeTypes = CONTENT_CHANGE_TYPE_UNDEFINED;
        this.mPackageName = null;
        this.mEventTime = 0;
        if (this.mRecords != null) {
            while (!this.mRecords.isEmpty()) {
                ((AccessibilityRecord) this.mRecords.remove(CONTENT_CHANGE_TYPE_UNDEFINED)).recycle();
            }
        }
    }

    public void initFromParcel(Parcel parcel) {
        boolean z = true;
        if (parcel.readInt() != TYPE_VIEW_CLICKED) {
            z = DEBUG;
        }
        this.mSealed = z;
        this.mEventType = parcel.readInt();
        this.mMovementGranularity = parcel.readInt();
        this.mAction = parcel.readInt();
        this.mContentChangeTypes = parcel.readInt();
        this.mPackageName = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mEventTime = parcel.readLong();
        this.mConnectionId = parcel.readInt();
        readAccessibilityRecordFromParcel(this, parcel);
        int recordCount = parcel.readInt();
        if (recordCount > 0) {
            this.mRecords = new ArrayList(recordCount);
            for (int i = CONTENT_CHANGE_TYPE_UNDEFINED; i < recordCount; i += TYPE_VIEW_CLICKED) {
                AccessibilityRecord record = AccessibilityRecord.obtain();
                readAccessibilityRecordFromParcel(record, parcel);
                record.mConnectionId = this.mConnectionId;
                this.mRecords.add(record);
            }
        }
    }

    private void readAccessibilityRecordFromParcel(AccessibilityRecord record, Parcel parcel) {
        record.mBooleanProperties = parcel.readInt();
        record.mCurrentItemIndex = parcel.readInt();
        record.mItemCount = parcel.readInt();
        record.mFromIndex = parcel.readInt();
        record.mToIndex = parcel.readInt();
        record.mScrollX = parcel.readInt();
        record.mScrollY = parcel.readInt();
        record.mMaxScrollX = parcel.readInt();
        record.mMaxScrollY = parcel.readInt();
        record.mAddedCount = parcel.readInt();
        record.mRemovedCount = parcel.readInt();
        record.mClassName = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        record.mContentDescription = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        record.mBeforeText = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        record.mParcelableData = parcel.readParcelable(null);
        parcel.readList(record.mText, null);
        record.mSourceWindowId = parcel.readInt();
        record.mSourceNodeId = parcel.readLong();
        record.mSealed = parcel.readInt() == TYPE_VIEW_CLICKED ? true : DEBUG;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        int i;
        if (isSealed()) {
            i = TYPE_VIEW_CLICKED;
        } else {
            i = CONTENT_CHANGE_TYPE_UNDEFINED;
        }
        parcel.writeInt(i);
        parcel.writeInt(this.mEventType);
        parcel.writeInt(this.mMovementGranularity);
        parcel.writeInt(this.mAction);
        parcel.writeInt(this.mContentChangeTypes);
        TextUtils.writeToParcel(this.mPackageName, parcel, CONTENT_CHANGE_TYPE_UNDEFINED);
        parcel.writeLong(this.mEventTime);
        parcel.writeInt(this.mConnectionId);
        writeAccessibilityRecordToParcel(this, parcel, flags);
        int recordCount = getRecordCount();
        parcel.writeInt(recordCount);
        for (int i2 = CONTENT_CHANGE_TYPE_UNDEFINED; i2 < recordCount; i2 += TYPE_VIEW_CLICKED) {
            writeAccessibilityRecordToParcel((AccessibilityRecord) this.mRecords.get(i2), parcel, flags);
        }
    }

    private void writeAccessibilityRecordToParcel(AccessibilityRecord record, Parcel parcel, int flags) {
        parcel.writeInt(record.mBooleanProperties);
        parcel.writeInt(record.mCurrentItemIndex);
        parcel.writeInt(record.mItemCount);
        parcel.writeInt(record.mFromIndex);
        parcel.writeInt(record.mToIndex);
        parcel.writeInt(record.mScrollX);
        parcel.writeInt(record.mScrollY);
        parcel.writeInt(record.mMaxScrollX);
        parcel.writeInt(record.mMaxScrollY);
        parcel.writeInt(record.mAddedCount);
        parcel.writeInt(record.mRemovedCount);
        TextUtils.writeToParcel(record.mClassName, parcel, flags);
        TextUtils.writeToParcel(record.mContentDescription, parcel, flags);
        TextUtils.writeToParcel(record.mBeforeText, parcel, flags);
        parcel.writeParcelable(record.mParcelableData, flags);
        parcel.writeList(record.mText);
        parcel.writeInt(record.mSourceWindowId);
        parcel.writeLong(record.mSourceNodeId);
        parcel.writeInt(record.mSealed ? TYPE_VIEW_CLICKED : CONTENT_CHANGE_TYPE_UNDEFINED);
    }

    public int describeContents() {
        return CONTENT_CHANGE_TYPE_UNDEFINED;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EventType: ").append(eventTypeToString(this.mEventType));
        builder.append("; EventTime: ").append(this.mEventTime);
        builder.append("; PackageName: ").append(this.mPackageName);
        builder.append("; MovementGranularity: ").append(this.mMovementGranularity);
        builder.append("; Action: ").append(this.mAction);
        builder.append(super.toString());
        builder.append("; recordCount: ").append(getRecordCount());
        return builder.toString();
    }

    public static String eventTypeToString(int eventType) {
        if (eventType == TYPES_ALL_MASK) {
            return "TYPES_ALL_MASK";
        }
        StringBuilder builder = new StringBuilder();
        int eventTypeCount = CONTENT_CHANGE_TYPE_UNDEFINED;
        while (eventType != 0) {
            int eventTypeFlag = TYPE_VIEW_CLICKED << Integer.numberOfTrailingZeros(eventType);
            eventType &= eventTypeFlag ^ TYPES_ALL_MASK;
            switch (eventTypeFlag) {
                case TYPE_VIEW_CLICKED /*1*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_CLICKED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_LONG_CLICKED /*2*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_LONG_CLICKED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_SELECTED /*4*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_SELECTED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_FOCUSED /*8*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_FOCUSED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_TEXT_CHANGED /*16*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_TEXT_CHANGED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_WINDOW_STATE_CHANGED /*32*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_WINDOW_STATE_CHANGED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_NOTIFICATION_STATE_CHANGED /*64*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_NOTIFICATION_STATE_CHANGED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_HOVER_ENTER /*128*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_HOVER_ENTER");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_HOVER_EXIT /*256*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_HOVER_EXIT");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_TOUCH_EXPLORATION_GESTURE_START /*512*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_TOUCH_EXPLORATION_GESTURE_START");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_TOUCH_EXPLORATION_GESTURE_END /*1024*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_TOUCH_EXPLORATION_GESTURE_END");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_WINDOW_CONTENT_CHANGED /*2048*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_WINDOW_CONTENT_CHANGED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_SCROLLED /*4096*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_SCROLLED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_TEXT_SELECTION_CHANGED /*8192*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_TEXT_SELECTION_CHANGED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_ANNOUNCEMENT /*16384*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_ANNOUNCEMENT");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_ACCESSIBILITY_FOCUSED /*32768*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED /*65536*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY /*131072*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_GESTURE_DETECTION_START /*262144*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_GESTURE_DETECTION_START");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_GESTURE_DETECTION_END /*524288*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_GESTURE_DETECTION_END");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_TOUCH_INTERACTION_START /*1048576*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_TOUCH_INTERACTION_START");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_TOUCH_INTERACTION_END /*2097152*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_TOUCH_INTERACTION_END");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                case TYPE_WINDOWS_CHANGED /*4194304*/:
                    if (eventTypeCount > 0) {
                        builder.append(", ");
                    }
                    builder.append("TYPE_WINDOWS_CHANGED");
                    eventTypeCount += TYPE_VIEW_CLICKED;
                    break;
                default:
                    break;
            }
        }
        if (eventTypeCount > TYPE_VIEW_CLICKED) {
            builder.insert(CONTENT_CHANGE_TYPE_UNDEFINED, '[');
            builder.append(']');
        }
        return builder.toString();
    }
}
