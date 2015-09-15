package android.graphics;

public class Atlas {
    public static final int FLAG_ADD_PADDING = 2;
    public static final int FLAG_ALLOW_ROTATIONS = 1;
    public static final int FLAG_DEFAULTS = 2;
    private final Policy mPolicy;

    /* renamed from: android.graphics.Atlas.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Atlas$Type;

        static {
            $SwitchMap$android$graphics$Atlas$Type = new int[Type.values().length];
            try {
                $SwitchMap$android$graphics$Atlas$Type[Type.SliceMinArea.ordinal()] = Atlas.FLAG_ALLOW_ROTATIONS;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Atlas$Type[Type.SliceMaxArea.ordinal()] = Atlas.FLAG_DEFAULTS;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Atlas$Type[Type.SliceShortAxis.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Atlas$Type[Type.SliceLongAxis.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static class Entry {
        public boolean rotated;
        public int x;
        public int y;
    }

    private static abstract class Policy {
        abstract Entry pack(int i, int i2, Entry entry);

        private Policy() {
        }
    }

    private static class SlicePolicy extends Policy {
        private final boolean mAllowRotation;
        private final int mPadding;
        private final Cell mRoot;
        private final SplitDecision mSplitDecision;

        private static class Cell {
            int height;
            Cell next;
            int width;
            int x;
            int y;

            private Cell() {
            }

            public String toString() {
                return String.format("cell[x=%d y=%d width=%d height=%d", new Object[]{Integer.valueOf(this.x), Integer.valueOf(this.y), Integer.valueOf(this.width), Integer.valueOf(this.height)});
            }
        }

        private interface SplitDecision {
            boolean splitHorizontal(int i, int i2, int i3, int i4);
        }

        private static class LongerFreeAxisSplitDecision implements SplitDecision {
            private LongerFreeAxisSplitDecision() {
            }

            public boolean splitHorizontal(int freeWidth, int freeHeight, int rectWidth, int rectHeight) {
                return freeWidth > freeHeight;
            }
        }

        private static class MaxAreaSplitDecision implements SplitDecision {
            private MaxAreaSplitDecision() {
            }

            public boolean splitHorizontal(int freeWidth, int freeHeight, int rectWidth, int rectHeight) {
                return rectWidth * freeHeight <= freeWidth * rectHeight;
            }
        }

        private static class MinAreaSplitDecision implements SplitDecision {
            private MinAreaSplitDecision() {
            }

            public boolean splitHorizontal(int freeWidth, int freeHeight, int rectWidth, int rectHeight) {
                return rectWidth * freeHeight > freeWidth * rectHeight;
            }
        }

        private static class ShorterFreeAxisSplitDecision implements SplitDecision {
            private ShorterFreeAxisSplitDecision() {
            }

            public boolean splitHorizontal(int freeWidth, int freeHeight, int rectWidth, int rectHeight) {
                return freeWidth <= freeHeight;
            }
        }

        SlicePolicy(int width, int height, int flags, SplitDecision splitDecision) {
            boolean z;
            int i = Atlas.FLAG_ALLOW_ROTATIONS;
            super();
            this.mRoot = new Cell();
            if ((flags & Atlas.FLAG_ALLOW_ROTATIONS) != 0) {
                z = true;
            } else {
                z = false;
            }
            this.mAllowRotation = z;
            if ((flags & Atlas.FLAG_DEFAULTS) == 0) {
                i = 0;
            }
            this.mPadding = i;
            Cell first = new Cell();
            int i2 = this.mPadding;
            first.y = i2;
            first.x = i2;
            first.width = width - (this.mPadding * Atlas.FLAG_DEFAULTS);
            first.height = height - (this.mPadding * Atlas.FLAG_DEFAULTS);
            this.mRoot.next = first;
            this.mSplitDecision = splitDecision;
        }

        Entry pack(int width, int height, Entry entry) {
            Cell prev = this.mRoot;
            for (Cell cell = this.mRoot.next; cell != null; cell = cell.next) {
                if (insert(cell, prev, width, height, entry)) {
                    return entry;
                }
                prev = cell;
            }
            return null;
        }

        private boolean insert(Cell cell, Cell prev, int width, int height, Entry entry) {
            boolean rotated = false;
            if (cell.width < width || cell.height < height) {
                if (!this.mAllowRotation || cell.width < height || cell.height < width) {
                    return false;
                }
                int temp = width;
                width = height;
                height = temp;
                rotated = true;
            }
            int deltaWidth = cell.width - width;
            int deltaHeight = cell.height - height;
            Cell first = new Cell();
            Cell second = new Cell();
            first.x = (cell.x + width) + this.mPadding;
            first.y = cell.y;
            first.width = deltaWidth - this.mPadding;
            second.x = cell.x;
            second.y = (cell.y + height) + this.mPadding;
            second.height = deltaHeight - this.mPadding;
            if (this.mSplitDecision.splitHorizontal(deltaWidth, deltaHeight, width, height)) {
                first.height = height;
                second.width = cell.width;
            } else {
                first.height = cell.height;
                second.width = width;
                Cell temp2 = first;
                first = second;
                second = temp2;
            }
            if (first.width > 0 && first.height > 0) {
                prev.next = first;
                prev = first;
            }
            if (second.width <= 0 || second.height <= 0) {
                prev.next = cell.next;
            } else {
                prev.next = second;
                second.next = cell.next;
            }
            cell.next = null;
            entry.x = cell.x;
            entry.y = cell.y;
            entry.rotated = rotated;
            return true;
        }
    }

    public enum Type {
        SliceMinArea,
        SliceMaxArea,
        SliceShortAxis,
        SliceLongAxis
    }

    public Atlas(Type type, int width, int height) {
        this(type, width, height, FLAG_DEFAULTS);
    }

    public Atlas(Type type, int width, int height, int flags) {
        this.mPolicy = findPolicy(type, width, height, flags);
    }

    public Entry pack(int width, int height) {
        return pack(width, height, null);
    }

    public Entry pack(int width, int height, Entry entry) {
        if (entry == null) {
            entry = new Entry();
        }
        return this.mPolicy.pack(width, height, entry);
    }

    private static Policy findPolicy(Type type, int width, int height, int flags) {
        switch (AnonymousClass1.$SwitchMap$android$graphics$Atlas$Type[type.ordinal()]) {
            case FLAG_ALLOW_ROTATIONS /*1*/:
                return new SlicePolicy(width, height, flags, new MinAreaSplitDecision());
            case FLAG_DEFAULTS /*2*/:
                return new SlicePolicy(width, height, flags, new MaxAreaSplitDecision());
            case SetDrawableParameters.TAG /*3*/:
                return new SlicePolicy(width, height, flags, new ShorterFreeAxisSplitDecision());
            case ViewGroupAction.TAG /*4*/:
                return new SlicePolicy(width, height, flags, new LongerFreeAxisSplitDecision());
            default:
                return null;
        }
    }
}
