package android.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;

public class LevelListDrawable extends DrawableContainer {
    private LevelListState mLevelListState;
    private boolean mMutated;

    private static final class LevelListState extends DrawableContainerState {
        private int[] mHighs;
        private int[] mLows;

        LevelListState(LevelListState orig, LevelListDrawable owner, Resources res) {
            super(orig, owner, res);
            if (orig != null) {
                this.mLows = orig.mLows;
                this.mHighs = orig.mHighs;
                return;
            }
            this.mLows = new int[getCapacity()];
            this.mHighs = new int[getCapacity()];
        }

        private void mutate() {
            this.mLows = (int[]) this.mLows.clone();
            this.mHighs = (int[]) this.mHighs.clone();
        }

        public void addLevel(int low, int high, Drawable drawable) {
            int pos = addChild(drawable);
            this.mLows[pos] = low;
            this.mHighs[pos] = high;
        }

        public int indexOfLevel(int level) {
            int[] lows = this.mLows;
            int[] highs = this.mHighs;
            int N = getChildCount();
            int i = 0;
            while (i < N) {
                if (level >= lows[i] && level <= highs[i]) {
                    return i;
                }
                i++;
            }
            return -1;
        }

        public Drawable newDrawable() {
            return new LevelListDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new LevelListDrawable(res, null);
        }

        public void growArray(int oldSize, int newSize) {
            super.growArray(oldSize, newSize);
            int[] newInts = new int[newSize];
            System.arraycopy(this.mLows, 0, newInts, 0, oldSize);
            this.mLows = newInts;
            newInts = new int[newSize];
            System.arraycopy(this.mHighs, 0, newInts, 0, oldSize);
            this.mHighs = newInts;
        }
    }

    public LevelListDrawable() {
        this(null, null);
    }

    public void addLevel(int low, int high, Drawable drawable) {
        if (drawable != null) {
            this.mLevelListState.addLevel(low, high, drawable);
            onLevelChange(getLevel());
        }
    }

    protected boolean onLevelChange(int level) {
        if (selectDrawable(this.mLevelListState.indexOfLevel(level))) {
            return true;
        }
        return super.onLevelChange(level);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void inflate(android.content.res.Resources r12, org.xmlpull.v1.XmlPullParser r13, android.util.AttributeSet r14, android.content.res.Resources.Theme r15) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r11 = this;
        super.inflate(r12, r13, r14, r15);
        r6 = 0;
        r8 = r13.getDepth();
        r5 = r8 + 1;
    L_0x000a:
        r7 = r13.next();
        r8 = 1;
        if (r7 == r8) goto L_0x00a3;
    L_0x0011:
        r1 = r13.getDepth();
        if (r1 >= r5) goto L_0x001a;
    L_0x0017:
        r8 = 3;
        if (r7 == r8) goto L_0x00a3;
    L_0x001a:
        r8 = 2;
        if (r7 != r8) goto L_0x000a;
    L_0x001d:
        if (r1 > r5) goto L_0x000a;
    L_0x001f:
        r8 = r13.getName();
        r9 = "item";
        r8 = r8.equals(r9);
        if (r8 == 0) goto L_0x000a;
    L_0x002b:
        r8 = com.android.internal.R.styleable.LevelListDrawableItem;
        r0 = android.graphics.drawable.Drawable.obtainAttributes(r12, r15, r14, r8);
        r8 = 1;
        r9 = 0;
        r6 = r0.getInt(r8, r9);
        r8 = 2;
        r9 = 0;
        r4 = r0.getInt(r8, r9);
        r8 = 0;
        r9 = 0;
        r3 = r0.getResourceId(r8, r9);
        r0.recycle();
        if (r4 >= 0) goto L_0x0065;
    L_0x0048:
        r8 = new org.xmlpull.v1.XmlPullParserException;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = r13.getPositionDescription();
        r9 = r9.append(r10);
        r10 = ": <item> tag requires a 'maxLevel' attribute";
        r9 = r9.append(r10);
        r9 = r9.toString();
        r8.<init>(r9);
        throw r8;
    L_0x0065:
        if (r3 == 0) goto L_0x0071;
    L_0x0067:
        r2 = r12.getDrawable(r3, r15);
    L_0x006b:
        r8 = r11.mLevelListState;
        r8.addLevel(r6, r4, r2);
        goto L_0x000a;
    L_0x0071:
        r7 = r13.next();
        r8 = 4;
        if (r7 == r8) goto L_0x0071;
    L_0x0078:
        r8 = 2;
        if (r7 == r8) goto L_0x009e;
    L_0x007b:
        r8 = new org.xmlpull.v1.XmlPullParserException;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = r13.getPositionDescription();
        r9 = r9.append(r10);
        r10 = ": <item> tag requires a 'drawable' attribute or ";
        r9 = r9.append(r10);
        r10 = "child tag defining a drawable";
        r9 = r9.append(r10);
        r9 = r9.toString();
        r8.<init>(r9);
        throw r8;
    L_0x009e:
        r2 = android.graphics.drawable.Drawable.createFromXmlInner(r12, r13, r14, r15);
        goto L_0x006b;
    L_0x00a3:
        r8 = r11.getLevel();
        r11.onLevelChange(r8);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.LevelListDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mLevelListState.mutate();
            this.mMutated = true;
        }
        return this;
    }

    LevelListState cloneConstantState() {
        return new LevelListState(this.mLevelListState, this, null);
    }

    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    protected void setConstantState(DrawableContainerState state) {
        super.setConstantState(state);
        if (state instanceof LevelListState) {
            this.mLevelListState = (LevelListState) state;
        }
    }

    private LevelListDrawable(LevelListState state, Resources res) {
        setConstantState(new LevelListState(state, this, res));
        onLevelChange(getLevel());
    }
}
