package android.widget;

import android.R;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class ScrollBarDrawable extends Drawable {
    private static final int[] STATE_ENABLED;
    private boolean mAlwaysDrawHorizontalTrack;
    private boolean mAlwaysDrawVerticalTrack;
    private boolean mChanged;
    private int mExtent;
    private Drawable mHorizontalThumb;
    private Drawable mHorizontalTrack;
    private boolean mMutated;
    private int mOffset;
    private int mRange;
    private boolean mRangeChanged;
    private final Rect mTempBounds;
    private boolean mVertical;
    private Drawable mVerticalThumb;
    private Drawable mVerticalTrack;

    static {
        STATE_ENABLED = new int[]{R.attr.state_enabled};
    }

    public ScrollBarDrawable() {
        this.mTempBounds = new Rect();
    }

    public void setAlwaysDrawHorizontalTrack(boolean alwaysDrawTrack) {
        this.mAlwaysDrawHorizontalTrack = alwaysDrawTrack;
    }

    public void setAlwaysDrawVerticalTrack(boolean alwaysDrawTrack) {
        this.mAlwaysDrawVerticalTrack = alwaysDrawTrack;
    }

    public boolean getAlwaysDrawVerticalTrack() {
        return this.mAlwaysDrawVerticalTrack;
    }

    public boolean getAlwaysDrawHorizontalTrack() {
        return this.mAlwaysDrawHorizontalTrack;
    }

    public void setParameters(int range, int offset, int extent, boolean vertical) {
        if (this.mVertical != vertical) {
            this.mChanged = true;
        }
        if (!(this.mRange == range && this.mOffset == offset && this.mExtent == extent)) {
            this.mRangeChanged = true;
        }
        this.mRange = range;
        this.mOffset = offset;
        this.mExtent = extent;
        this.mVertical = vertical;
    }

    public void draw(Canvas canvas) {
        boolean vertical = this.mVertical;
        int extent = this.mExtent;
        int range = this.mRange;
        boolean drawTrack = true;
        boolean drawThumb = true;
        if (extent <= 0 || range <= extent) {
            drawTrack = vertical ? this.mAlwaysDrawVerticalTrack : this.mAlwaysDrawHorizontalTrack;
            drawThumb = false;
        }
        Rect r = getBounds();
        if (!canvas.quickReject((float) r.left, (float) r.top, (float) r.right, (float) r.bottom, EdgeType.AA)) {
            if (drawTrack) {
                drawTrack(canvas, r, vertical);
            }
            if (drawThumb) {
                int size = vertical ? r.height() : r.width();
                int thickness = vertical ? r.width() : r.height();
                int length = Math.round((((float) size) * ((float) extent)) / ((float) range));
                int offset = Math.round((((float) (size - length)) * ((float) this.mOffset)) / ((float) (range - extent)));
                int minLength = thickness * 2;
                if (length < minLength) {
                    length = minLength;
                }
                if (offset + length > size) {
                    offset = size - length;
                }
                drawThumb(canvas, r, offset, length, vertical);
            }
        }
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mChanged = true;
    }

    protected void drawTrack(Canvas canvas, Rect bounds, boolean vertical) {
        Drawable track;
        if (vertical) {
            track = this.mVerticalTrack;
        } else {
            track = this.mHorizontalTrack;
        }
        if (track != null) {
            if (this.mChanged) {
                track.setBounds(bounds);
            }
            track.draw(canvas);
        }
    }

    protected void drawThumb(Canvas canvas, Rect bounds, int offset, int length, boolean vertical) {
        Rect thumbRect = this.mTempBounds;
        boolean changed = this.mRangeChanged || this.mChanged;
        if (changed) {
            if (vertical) {
                thumbRect.set(bounds.left, bounds.top + offset, bounds.right, (bounds.top + offset) + length);
            } else {
                thumbRect.set(bounds.left + offset, bounds.top, (bounds.left + offset) + length, bounds.bottom);
            }
        }
        Drawable thumb;
        if (vertical) {
            if (this.mVerticalThumb != null) {
                thumb = this.mVerticalThumb;
                if (changed) {
                    thumb.setBounds(thumbRect);
                }
                thumb.draw(canvas);
            }
        } else if (this.mHorizontalThumb != null) {
            thumb = this.mHorizontalThumb;
            if (changed) {
                thumb.setBounds(thumbRect);
            }
            thumb.draw(canvas);
        }
    }

    public void setVerticalThumbDrawable(Drawable thumb) {
        if (thumb != null) {
            if (this.mMutated) {
                thumb.mutate();
            }
            thumb.setState(STATE_ENABLED);
            this.mVerticalThumb = thumb;
        }
    }

    public void setVerticalTrackDrawable(Drawable track) {
        if (track != null) {
            if (this.mMutated) {
                track.mutate();
            }
            track.setState(STATE_ENABLED);
        }
        this.mVerticalTrack = track;
    }

    public void setHorizontalThumbDrawable(Drawable thumb) {
        if (thumb != null) {
            if (this.mMutated) {
                thumb.mutate();
            }
            thumb.setState(STATE_ENABLED);
            this.mHorizontalThumb = thumb;
        }
    }

    public void setHorizontalTrackDrawable(Drawable track) {
        if (track != null) {
            if (this.mMutated) {
                track.mutate();
            }
            track.setState(STATE_ENABLED);
        }
        this.mHorizontalTrack = track;
    }

    public int getSize(boolean vertical) {
        if (vertical) {
            if (this.mVerticalTrack != null) {
                return this.mVerticalTrack.getIntrinsicWidth();
            }
            if (this.mVerticalThumb != null) {
                return this.mVerticalThumb.getIntrinsicWidth();
            }
            return 0;
        } else if (this.mHorizontalTrack != null) {
            return this.mHorizontalTrack.getIntrinsicHeight();
        } else {
            if (this.mHorizontalThumb != null) {
                return this.mHorizontalThumb.getIntrinsicHeight();
            }
            return 0;
        }
    }

    public ScrollBarDrawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            if (this.mVerticalTrack != null) {
                this.mVerticalTrack.mutate();
            }
            if (this.mVerticalThumb != null) {
                this.mVerticalThumb.mutate();
            }
            if (this.mHorizontalTrack != null) {
                this.mHorizontalTrack.mutate();
            }
            if (this.mHorizontalThumb != null) {
                this.mHorizontalThumb.mutate();
            }
            this.mMutated = true;
        }
        return this;
    }

    public void setAlpha(int alpha) {
        if (this.mVerticalTrack != null) {
            this.mVerticalTrack.setAlpha(alpha);
        }
        if (this.mVerticalThumb != null) {
            this.mVerticalThumb.setAlpha(alpha);
        }
        if (this.mHorizontalTrack != null) {
            this.mHorizontalTrack.setAlpha(alpha);
        }
        if (this.mHorizontalThumb != null) {
            this.mHorizontalThumb.setAlpha(alpha);
        }
    }

    public int getAlpha() {
        return this.mVerticalThumb.getAlpha();
    }

    public void setColorFilter(ColorFilter cf) {
        if (this.mVerticalTrack != null) {
            this.mVerticalTrack.setColorFilter(cf);
        }
        if (this.mVerticalThumb != null) {
            this.mVerticalThumb.setColorFilter(cf);
        }
        if (this.mHorizontalTrack != null) {
            this.mHorizontalTrack.setColorFilter(cf);
        }
        if (this.mHorizontalThumb != null) {
            this.mHorizontalThumb.setColorFilter(cf);
        }
    }

    public int getOpacity() {
        return -3;
    }

    public String toString() {
        return "ScrollBarDrawable: range=" + this.mRange + " offset=" + this.mOffset + " extent=" + this.mExtent + (this.mVertical ? " V" : " H");
    }
}
