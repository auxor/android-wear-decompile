package android.view;

import android.os.Build;
import android.util.Log;
import android.widget.SpellChecker;
import android.widget.Toast;

public final class InputEventConsistencyVerifier {
    private static final String EVENT_TYPE_GENERIC_MOTION = "GenericMotionEvent";
    private static final String EVENT_TYPE_KEY = "KeyEvent";
    private static final String EVENT_TYPE_TOUCH = "TouchEvent";
    private static final String EVENT_TYPE_TRACKBALL = "TrackballEvent";
    public static final int FLAG_RAW_DEVICE_INPUT = 1;
    private static final boolean IS_ENG_BUILD;
    private static final int RECENT_EVENTS_TO_LOG = 5;
    private final Object mCaller;
    private InputEvent mCurrentEvent;
    private String mCurrentEventType;
    private final int mFlags;
    private boolean mHoverEntered;
    private KeyState mKeyStateList;
    private int mLastEventSeq;
    private String mLastEventType;
    private int mLastNestingLevel;
    private final String mLogTag;
    private int mMostRecentEventIndex;
    private InputEvent[] mRecentEvents;
    private boolean[] mRecentEventsUnhandled;
    private int mTouchEventStreamDeviceId;
    private boolean mTouchEventStreamIsTainted;
    private int mTouchEventStreamPointers;
    private int mTouchEventStreamSource;
    private boolean mTouchEventStreamUnhandled;
    private boolean mTrackballDown;
    private boolean mTrackballUnhandled;
    private StringBuilder mViolationMessage;

    private static final class KeyState {
        private static KeyState mRecycledList;
        private static Object mRecycledListLock;
        public int deviceId;
        public int keyCode;
        public KeyState next;
        public int source;
        public boolean unhandled;

        public static android.view.InputEventConsistencyVerifier.KeyState obtain(int r1, int r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.InputEventConsistencyVerifier.KeyState.obtain(int, int, int):android.view.InputEventConsistencyVerifier$KeyState
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.InputEventConsistencyVerifier.KeyState.obtain(int, int, int):android.view.InputEventConsistencyVerifier$KeyState
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.InputEventConsistencyVerifier.KeyState.obtain(int, int, int):android.view.InputEventConsistencyVerifier$KeyState");
        }

        public void recycle() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.InputEventConsistencyVerifier.KeyState.recycle():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.InputEventConsistencyVerifier.KeyState.recycle():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.InputEventConsistencyVerifier.KeyState.recycle():void");
        }

        static {
            mRecycledListLock = new Object();
        }

        private KeyState() {
        }
    }

    static {
        IS_ENG_BUILD = "eng".equals(Build.TYPE);
    }

    public InputEventConsistencyVerifier(Object caller, int flags) {
        this(caller, flags, null);
    }

    public InputEventConsistencyVerifier(Object caller, int flags, String logTag) {
        this.mTouchEventStreamDeviceId = -1;
        this.mCaller = caller;
        this.mFlags = flags;
        if (logTag == null) {
            logTag = "InputEventConsistencyVerifier";
        }
        this.mLogTag = logTag;
    }

    public static boolean isInstrumentationEnabled() {
        return IS_ENG_BUILD;
    }

    public void reset() {
        this.mLastEventSeq = -1;
        this.mLastNestingLevel = 0;
        this.mTrackballDown = IS_ENG_BUILD;
        this.mTrackballUnhandled = IS_ENG_BUILD;
        this.mTouchEventStreamPointers = 0;
        this.mTouchEventStreamIsTainted = IS_ENG_BUILD;
        this.mTouchEventStreamUnhandled = IS_ENG_BUILD;
        this.mHoverEntered = IS_ENG_BUILD;
        while (this.mKeyStateList != null) {
            KeyState state = this.mKeyStateList;
            this.mKeyStateList = state.next;
            state.recycle();
        }
    }

    public void onInputEvent(InputEvent event, int nestingLevel) {
        if (event instanceof KeyEvent) {
            onKeyEvent((KeyEvent) event, nestingLevel);
            return;
        }
        MotionEvent motionEvent = (MotionEvent) event;
        if (motionEvent.isTouchEvent()) {
            onTouchEvent(motionEvent, nestingLevel);
        } else if ((motionEvent.getSource() & 4) != 0) {
            onTrackballEvent(motionEvent, nestingLevel);
        } else {
            onGenericMotionEvent(motionEvent, nestingLevel);
        }
    }

    public void onKeyEvent(KeyEvent event, int nestingLevel) {
        if (startEvent(event, nestingLevel, EVENT_TYPE_KEY)) {
            try {
                ensureMetaStateIsNormalized(event.getMetaState());
                int action = event.getAction();
                int deviceId = event.getDeviceId();
                int source = event.getSource();
                int keyCode = event.getKeyCode();
                KeyState state;
                switch (action) {
                    case Toast.LENGTH_SHORT /*0*/:
                        state = findKeyState(deviceId, source, keyCode, IS_ENG_BUILD);
                        if (state != null) {
                            if (!state.unhandled) {
                                if ((this.mFlags & FLAG_RAW_DEVICE_INPUT) == 0 && event.getRepeatCount() == 0) {
                                    problem("ACTION_DOWN but key is already down and this event is not a key repeat.");
                                    break;
                                }
                            }
                            state.unhandled = IS_ENG_BUILD;
                            break;
                        }
                        addKeyState(deviceId, source, keyCode);
                        break;
                    case FLAG_RAW_DEVICE_INPUT /*1*/:
                        state = findKeyState(deviceId, source, keyCode, true);
                        if (state != null) {
                            state.recycle();
                            break;
                        } else {
                            problem("ACTION_UP but key was not down.");
                            break;
                        }
                    case Action.MERGE_IGNORE /*2*/:
                        break;
                    default:
                        problem("Invalid action " + KeyEvent.actionToString(action) + " for key event.");
                        break;
                }
                finishEvent();
            } catch (Throwable th) {
                finishEvent();
            }
        }
    }

    public void onTrackballEvent(MotionEvent event, int nestingLevel) {
        if (startEvent(event, nestingLevel, EVENT_TYPE_TRACKBALL)) {
            try {
                ensureMetaStateIsNormalized(event.getMetaState());
                int action = event.getAction();
                if ((event.getSource() & 4) != 0) {
                    switch (action) {
                        case Toast.LENGTH_SHORT /*0*/:
                            if (!this.mTrackballDown || this.mTrackballUnhandled) {
                                this.mTrackballDown = true;
                                this.mTrackballUnhandled = IS_ENG_BUILD;
                            } else {
                                problem("ACTION_DOWN but trackball is already down.");
                            }
                            ensureHistorySizeIsZeroForThisAction(event);
                            ensurePointerCountIsOneForThisAction(event);
                            break;
                        case FLAG_RAW_DEVICE_INPUT /*1*/:
                            if (this.mTrackballDown) {
                                this.mTrackballDown = IS_ENG_BUILD;
                                this.mTrackballUnhandled = IS_ENG_BUILD;
                            } else {
                                problem("ACTION_UP but trackball is not down.");
                            }
                            ensureHistorySizeIsZeroForThisAction(event);
                            ensurePointerCountIsOneForThisAction(event);
                            break;
                        case Action.MERGE_IGNORE /*2*/:
                            ensurePointerCountIsOneForThisAction(event);
                            break;
                        default:
                            problem("Invalid action " + MotionEvent.actionToString(action) + " for trackball event.");
                            break;
                    }
                    if (this.mTrackballDown && event.getPressure() <= 0.0f) {
                        problem("Trackball is down but pressure is not greater than 0.");
                    } else if (!(this.mTrackballDown || event.getPressure() == 0.0f)) {
                        problem("Trackball is up but pressure is not equal to 0.");
                    }
                } else {
                    problem("Source was not SOURCE_CLASS_TRACKBALL.");
                }
                finishEvent();
            } catch (Throwable th) {
                finishEvent();
            }
        }
    }

    public void onTouchEvent(MotionEvent event, int nestingLevel) {
        if (startEvent(event, nestingLevel, EVENT_TYPE_TOUCH)) {
            boolean newStream;
            int action = event.getAction();
            if (action == 0 || action == 3 || action == 4) {
                newStream = true;
            } else {
                newStream = IS_ENG_BUILD;
            }
            if (newStream && (this.mTouchEventStreamIsTainted || this.mTouchEventStreamUnhandled)) {
                this.mTouchEventStreamIsTainted = IS_ENG_BUILD;
                this.mTouchEventStreamUnhandled = IS_ENG_BUILD;
                this.mTouchEventStreamPointers = 0;
            }
            if (this.mTouchEventStreamIsTainted) {
                event.setTainted(true);
            }
            try {
                ensureMetaStateIsNormalized(event.getMetaState());
                int deviceId = event.getDeviceId();
                int source = event.getSource();
                if (!(newStream || this.mTouchEventStreamDeviceId == -1 || (this.mTouchEventStreamDeviceId == deviceId && this.mTouchEventStreamSource == source))) {
                    problem("Touch event stream contains events from multiple sources: previous device id " + this.mTouchEventStreamDeviceId + ", previous source " + Integer.toHexString(this.mTouchEventStreamSource) + ", new device id " + deviceId + ", new source " + Integer.toHexString(source));
                }
                this.mTouchEventStreamDeviceId = deviceId;
                this.mTouchEventStreamSource = source;
                int pointerCount = event.getPointerCount();
                if ((source & 2) != 0) {
                    switch (action) {
                        case Toast.LENGTH_SHORT /*0*/:
                            if (this.mTouchEventStreamPointers != 0) {
                                problem("ACTION_DOWN but pointers are already down.  Probably missing ACTION_UP from previous gesture.");
                            }
                            ensureHistorySizeIsZeroForThisAction(event);
                            ensurePointerCountIsOneForThisAction(event);
                            this.mTouchEventStreamPointers = FLAG_RAW_DEVICE_INPUT << event.getPointerId(0);
                            break;
                        case FLAG_RAW_DEVICE_INPUT /*1*/:
                            ensureHistorySizeIsZeroForThisAction(event);
                            ensurePointerCountIsOneForThisAction(event);
                            this.mTouchEventStreamPointers = 0;
                            this.mTouchEventStreamIsTainted = IS_ENG_BUILD;
                            break;
                        case Action.MERGE_IGNORE /*2*/:
                            int expectedPointerCount = Integer.bitCount(this.mTouchEventStreamPointers);
                            if (pointerCount != expectedPointerCount) {
                                problem("ACTION_MOVE contained " + pointerCount + " pointers but there are currently " + expectedPointerCount + " pointers down.");
                                this.mTouchEventStreamIsTainted = true;
                                break;
                            }
                            break;
                        case SetDrawableParameters.TAG /*3*/:
                            this.mTouchEventStreamPointers = 0;
                            this.mTouchEventStreamIsTainted = IS_ENG_BUILD;
                            break;
                        case ViewGroupAction.TAG /*4*/:
                            if (this.mTouchEventStreamPointers != 0) {
                                problem("ACTION_OUTSIDE but pointers are still down.");
                            }
                            ensureHistorySizeIsZeroForThisAction(event);
                            ensurePointerCountIsOneForThisAction(event);
                            this.mTouchEventStreamIsTainted = IS_ENG_BUILD;
                            break;
                        default:
                            int actionMasked = event.getActionMasked();
                            int actionIndex = event.getActionIndex();
                            int id;
                            int idBit;
                            if (actionMasked != RECENT_EVENTS_TO_LOG) {
                                if (actionMasked != 6) {
                                    problem("Invalid action " + MotionEvent.actionToString(action) + " for touch event.");
                                    break;
                                }
                                if (actionIndex < 0 || actionIndex >= pointerCount) {
                                    problem("ACTION_POINTER_UP index is " + actionIndex + " but the pointer count is " + pointerCount + ".");
                                    this.mTouchEventStreamIsTainted = true;
                                } else {
                                    id = event.getPointerId(actionIndex);
                                    idBit = FLAG_RAW_DEVICE_INPUT << id;
                                    if ((this.mTouchEventStreamPointers & idBit) == 0) {
                                        problem("ACTION_POINTER_UP specified pointer id " + id + " which is not currently down.");
                                        this.mTouchEventStreamIsTainted = true;
                                    } else {
                                        this.mTouchEventStreamPointers &= idBit ^ -1;
                                    }
                                }
                                ensureHistorySizeIsZeroForThisAction(event);
                                break;
                            }
                            if (this.mTouchEventStreamPointers == 0) {
                                problem("ACTION_POINTER_DOWN but no other pointers were down.");
                                this.mTouchEventStreamIsTainted = true;
                            }
                            if (actionIndex < 0 || actionIndex >= pointerCount) {
                                problem("ACTION_POINTER_DOWN index is " + actionIndex + " but the pointer count is " + pointerCount + ".");
                                this.mTouchEventStreamIsTainted = true;
                            } else {
                                id = event.getPointerId(actionIndex);
                                idBit = FLAG_RAW_DEVICE_INPUT << id;
                                if ((this.mTouchEventStreamPointers & idBit) != 0) {
                                    problem("ACTION_POINTER_DOWN specified pointer id " + id + " which is already down.");
                                    this.mTouchEventStreamIsTainted = true;
                                } else {
                                    this.mTouchEventStreamPointers |= idBit;
                                }
                            }
                            ensureHistorySizeIsZeroForThisAction(event);
                            break;
                            break;
                    }
                }
                problem("Source was not SOURCE_CLASS_POINTER.");
                finishEvent();
            } catch (Throwable th) {
                finishEvent();
            }
        }
    }

    public void onGenericMotionEvent(MotionEvent event, int nestingLevel) {
        if (startEvent(event, nestingLevel, EVENT_TYPE_GENERIC_MOTION)) {
            try {
                ensureMetaStateIsNormalized(event.getMetaState());
                int action = event.getAction();
                int source = event.getSource();
                if ((source & 2) == 0) {
                    if ((source & 16) != 0) {
                        switch (action) {
                            case Action.MERGE_IGNORE /*2*/:
                                ensurePointerCountIsOneForThisAction(event);
                                break;
                            default:
                                problem("Invalid action for generic joystick event.");
                                break;
                        }
                    }
                }
                switch (action) {
                    case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    case SetPendingIntentTemplate.TAG /*8*/:
                        ensureHistorySizeIsZeroForThisAction(event);
                        ensurePointerCountIsOneForThisAction(event);
                        break;
                    case SetOnClickFillInIntent.TAG /*9*/:
                        ensurePointerCountIsOneForThisAction(event);
                        this.mHoverEntered = true;
                        break;
                    case SetRemoteViewsAdapterIntent.TAG /*10*/:
                        ensurePointerCountIsOneForThisAction(event);
                        if (!this.mHoverEntered) {
                            problem("ACTION_HOVER_EXIT without prior ACTION_HOVER_ENTER");
                        }
                        this.mHoverEntered = IS_ENG_BUILD;
                        break;
                    default:
                        problem("Invalid action for generic pointer event.");
                        break;
                }
                finishEvent();
            } catch (Throwable th) {
                finishEvent();
            }
        }
    }

    public void onUnhandledEvent(InputEvent event, int nestingLevel) {
        if (nestingLevel == this.mLastNestingLevel) {
            if (this.mRecentEventsUnhandled != null) {
                this.mRecentEventsUnhandled[this.mMostRecentEventIndex] = true;
            }
            if (event instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent) event;
                KeyState state = findKeyState(keyEvent.getDeviceId(), keyEvent.getSource(), keyEvent.getKeyCode(), IS_ENG_BUILD);
                if (state != null) {
                    state.unhandled = true;
                    return;
                }
                return;
            }
            MotionEvent motionEvent = (MotionEvent) event;
            if (motionEvent.isTouchEvent()) {
                this.mTouchEventStreamUnhandled = true;
            } else if ((motionEvent.getSource() & 4) != 0 && this.mTrackballDown) {
                this.mTrackballUnhandled = true;
            }
        }
    }

    private void ensureMetaStateIsNormalized(int metaState) {
        if (KeyEvent.normalizeMetaState(metaState) != metaState) {
            problem(String.format("Metastate not normalized.  Was 0x%08x but expected 0x%08x.", new Object[]{Integer.valueOf(metaState), Integer.valueOf(KeyEvent.normalizeMetaState(metaState))}));
        }
    }

    private void ensurePointerCountIsOneForThisAction(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount != FLAG_RAW_DEVICE_INPUT) {
            problem("Pointer count is " + pointerCount + " but it should always be 1 for " + MotionEvent.actionToString(event.getAction()));
        }
    }

    private void ensureHistorySizeIsZeroForThisAction(MotionEvent event) {
        int historySize = event.getHistorySize();
        if (historySize != 0) {
            problem("History size is " + historySize + " but it should always be 0 for " + MotionEvent.actionToString(event.getAction()));
        }
    }

    private boolean startEvent(InputEvent event, int nestingLevel, String eventType) {
        int seq = event.getSequenceNumber();
        if (seq == this.mLastEventSeq && nestingLevel < this.mLastNestingLevel && eventType == this.mLastEventType) {
            return IS_ENG_BUILD;
        }
        if (nestingLevel > 0) {
            this.mLastEventSeq = seq;
            this.mLastEventType = eventType;
            this.mLastNestingLevel = nestingLevel;
        } else {
            this.mLastEventSeq = -1;
            this.mLastEventType = null;
            this.mLastNestingLevel = 0;
        }
        this.mCurrentEvent = event;
        this.mCurrentEventType = eventType;
        return true;
    }

    private void finishEvent() {
        int index;
        if (!(this.mViolationMessage == null || this.mViolationMessage.length() == 0)) {
            if (!this.mCurrentEvent.isTainted()) {
                this.mViolationMessage.append("\n  in ").append(this.mCaller);
                this.mViolationMessage.append("\n  ");
                appendEvent(this.mViolationMessage, 0, this.mCurrentEvent, IS_ENG_BUILD);
                if (this.mRecentEvents != null) {
                    this.mViolationMessage.append("\n  -- recent events --");
                    for (int i = 0; i < RECENT_EVENTS_TO_LOG; i += FLAG_RAW_DEVICE_INPUT) {
                        index = ((this.mMostRecentEventIndex + RECENT_EVENTS_TO_LOG) - i) % RECENT_EVENTS_TO_LOG;
                        InputEvent event = this.mRecentEvents[index];
                        if (event == null) {
                            break;
                        }
                        this.mViolationMessage.append("\n  ");
                        appendEvent(this.mViolationMessage, i + FLAG_RAW_DEVICE_INPUT, event, this.mRecentEventsUnhandled[index]);
                    }
                }
                Log.d(this.mLogTag, this.mViolationMessage.toString());
                this.mCurrentEvent.setTainted(true);
            }
            this.mViolationMessage.setLength(0);
        }
        if (this.mRecentEvents == null) {
            this.mRecentEvents = new InputEvent[RECENT_EVENTS_TO_LOG];
            this.mRecentEventsUnhandled = new boolean[RECENT_EVENTS_TO_LOG];
        }
        index = (this.mMostRecentEventIndex + FLAG_RAW_DEVICE_INPUT) % RECENT_EVENTS_TO_LOG;
        this.mMostRecentEventIndex = index;
        if (this.mRecentEvents[index] != null) {
            this.mRecentEvents[index].recycle();
        }
        this.mRecentEvents[index] = this.mCurrentEvent.copy();
        this.mRecentEventsUnhandled[index] = IS_ENG_BUILD;
        this.mCurrentEvent = null;
        this.mCurrentEventType = null;
    }

    private static void appendEvent(StringBuilder message, int index, InputEvent event, boolean unhandled) {
        message.append(index).append(": sent at ").append(event.getEventTimeNano());
        message.append(", ");
        if (unhandled) {
            message.append("(unhandled) ");
        }
        message.append(event);
    }

    private void problem(String message) {
        if (this.mViolationMessage == null) {
            this.mViolationMessage = new StringBuilder();
        }
        if (this.mViolationMessage.length() == 0) {
            this.mViolationMessage.append(this.mCurrentEventType).append(": ");
        } else {
            this.mViolationMessage.append("\n  ");
        }
        this.mViolationMessage.append(message);
    }

    private KeyState findKeyState(int deviceId, int source, int keyCode, boolean remove) {
        KeyState last = null;
        KeyState state = this.mKeyStateList;
        while (state != null) {
            if (state.deviceId != deviceId || state.source != source || state.keyCode != keyCode) {
                last = state;
                state = state.next;
            } else if (!remove) {
                return state;
            } else {
                if (last != null) {
                    last.next = state.next;
                } else {
                    this.mKeyStateList = state.next;
                }
                state.next = null;
                return state;
            }
        }
        return null;
    }

    private void addKeyState(int deviceId, int source, int keyCode) {
        KeyState state = KeyState.obtain(deviceId, source, keyCode);
        state.next = this.mKeyStateList;
        this.mKeyStateList = state;
    }
}
