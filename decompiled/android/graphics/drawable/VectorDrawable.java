package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.ProxyInfo;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.PathParser;
import android.util.PathParser.PathDataNode;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VectorDrawable extends Drawable {
    private static final boolean DBG_VECTOR_DRAWABLE = false;
    private static final int LINECAP_BUTT = 0;
    private static final int LINECAP_ROUND = 1;
    private static final int LINECAP_SQUARE = 2;
    private static final int LINEJOIN_BEVEL = 2;
    private static final int LINEJOIN_MITER = 0;
    private static final int LINEJOIN_ROUND = 1;
    private static final String LOGTAG;
    private static final String SHAPE_CLIP_PATH = "clip-path";
    private static final String SHAPE_GROUP = "group";
    private static final String SHAPE_PATH = "path";
    private static final String SHAPE_VECTOR = "vector";
    private boolean mAllowCaching;
    private ColorFilter mColorFilter;
    private boolean mMutated;
    private PorterDuffColorFilter mTintFilter;
    private VectorDrawableState mVectorState;

    private static class VPath {
        int mChangingConfigurations;
        protected PathDataNode[] mNodes;
        String mPathName;

        public VPath() {
            this.mNodes = null;
        }

        public VPath(VPath copy) {
            this.mNodes = null;
            this.mPathName = copy.mPathName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            this.mNodes = PathParser.deepCopyNodes(copy.mNodes);
        }

        public void toPath(Path path) {
            path.reset();
            if (this.mNodes != null) {
                PathDataNode.nodesToPath(this.mNodes, path);
            }
        }

        public String getPathName() {
            return this.mPathName;
        }

        public boolean canApplyTheme() {
            return VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public void applyTheme(Theme t) {
        }

        public boolean isClipPath() {
            return VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public PathDataNode[] getPathData() {
            return this.mNodes;
        }

        public void setPathData(PathDataNode[] nodes) {
            if (PathParser.canMorph(this.mNodes, nodes)) {
                PathParser.updateNodes(this.mNodes, nodes);
            } else {
                this.mNodes = PathParser.deepCopyNodes(nodes);
            }
        }
    }

    private static class VClipPath extends VPath {
        private void updateStateFromTypedArray(android.content.res.TypedArray r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.graphics.drawable.VectorDrawable.VClipPath.updateStateFromTypedArray(android.content.res.TypedArray):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.graphics.drawable.VectorDrawable.VClipPath.updateStateFromTypedArray(android.content.res.TypedArray):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.VectorDrawable.VClipPath.updateStateFromTypedArray(android.content.res.TypedArray):void");
        }

        public void inflate(android.content.res.Resources r1, android.util.AttributeSet r2, android.content.res.Resources.Theme r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.graphics.drawable.VectorDrawable.VClipPath.inflate(android.content.res.Resources, android.util.AttributeSet, android.content.res.Resources$Theme):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.graphics.drawable.VectorDrawable.VClipPath.inflate(android.content.res.Resources, android.util.AttributeSet, android.content.res.Resources$Theme):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.VectorDrawable.VClipPath.inflate(android.content.res.Resources, android.util.AttributeSet, android.content.res.Resources$Theme):void");
        }

        public VClipPath(VClipPath copy) {
            super(copy);
        }

        public boolean isClipPath() {
            return true;
        }
    }

    private static class VFullPath extends VPath {
        float mFillAlpha;
        int mFillColor;
        int mFillRule;
        float mStrokeAlpha;
        int mStrokeColor;
        Cap mStrokeLineCap;
        Join mStrokeLineJoin;
        float mStrokeMiterlimit;
        float mStrokeWidth;
        private int[] mThemeAttrs;
        float mTrimPathEnd;
        float mTrimPathOffset;
        float mTrimPathStart;

        public VFullPath() {
            this.mStrokeColor = VectorDrawable.LINEJOIN_MITER;
            this.mStrokeWidth = 0.0f;
            this.mFillColor = VectorDrawable.LINEJOIN_MITER;
            this.mStrokeAlpha = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mFillAlpha = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mTrimPathStart = 0.0f;
            this.mTrimPathEnd = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mTrimPathOffset = 0.0f;
            this.mStrokeLineCap = Cap.BUTT;
            this.mStrokeLineJoin = Join.MITER;
            this.mStrokeMiterlimit = 4.0f;
        }

        public VFullPath(VFullPath copy) {
            super(copy);
            this.mStrokeColor = VectorDrawable.LINEJOIN_MITER;
            this.mStrokeWidth = 0.0f;
            this.mFillColor = VectorDrawable.LINEJOIN_MITER;
            this.mStrokeAlpha = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mFillAlpha = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mTrimPathStart = 0.0f;
            this.mTrimPathEnd = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mTrimPathOffset = 0.0f;
            this.mStrokeLineCap = Cap.BUTT;
            this.mStrokeLineJoin = Join.MITER;
            this.mStrokeMiterlimit = 4.0f;
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mStrokeColor = copy.mStrokeColor;
            this.mStrokeWidth = copy.mStrokeWidth;
            this.mStrokeAlpha = copy.mStrokeAlpha;
            this.mFillColor = copy.mFillColor;
            this.mFillRule = copy.mFillRule;
            this.mFillAlpha = copy.mFillAlpha;
            this.mTrimPathStart = copy.mTrimPathStart;
            this.mTrimPathEnd = copy.mTrimPathEnd;
            this.mTrimPathOffset = copy.mTrimPathOffset;
            this.mStrokeLineCap = copy.mStrokeLineCap;
            this.mStrokeLineJoin = copy.mStrokeLineJoin;
            this.mStrokeMiterlimit = copy.mStrokeMiterlimit;
        }

        private Cap getStrokeLineCap(int id, Cap defValue) {
            switch (id) {
                case VectorDrawable.LINEJOIN_MITER /*0*/:
                    return Cap.BUTT;
                case VectorDrawable.LINEJOIN_ROUND /*1*/:
                    return Cap.ROUND;
                case VectorDrawable.LINEJOIN_BEVEL /*2*/:
                    return Cap.SQUARE;
                default:
                    return defValue;
            }
        }

        private Join getStrokeLineJoin(int id, Join defValue) {
            switch (id) {
                case VectorDrawable.LINEJOIN_MITER /*0*/:
                    return Join.MITER;
                case VectorDrawable.LINEJOIN_ROUND /*1*/:
                    return Join.ROUND;
                case VectorDrawable.LINEJOIN_BEVEL /*2*/:
                    return Join.BEVEL;
                default:
                    return defValue;
            }
        }

        public boolean canApplyTheme() {
            return this.mThemeAttrs != null ? true : VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public void inflate(Resources r, AttributeSet attrs, Theme theme) {
            TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.VectorDrawablePath);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        private void updateStateFromTypedArray(TypedArray a) {
            this.mChangingConfigurations |= a.getChangingConfigurations();
            this.mThemeAttrs = a.extractThemeAttrs();
            String pathName = a.getString(VectorDrawable.LINEJOIN_MITER);
            if (pathName != null) {
                this.mPathName = pathName;
            }
            String pathData = a.getString(VectorDrawable.LINEJOIN_BEVEL);
            if (pathData != null) {
                this.mNodes = PathParser.createNodesFromPathData(pathData);
            }
            this.mFillColor = a.getColor(VectorDrawable.LINEJOIN_ROUND, this.mFillColor);
            this.mFillAlpha = a.getFloat(12, this.mFillAlpha);
            this.mStrokeLineCap = getStrokeLineCap(a.getInt(8, -1), this.mStrokeLineCap);
            this.mStrokeLineJoin = getStrokeLineJoin(a.getInt(9, -1), this.mStrokeLineJoin);
            this.mStrokeMiterlimit = a.getFloat(10, this.mStrokeMiterlimit);
            this.mStrokeColor = a.getColor(3, this.mStrokeColor);
            this.mStrokeAlpha = a.getFloat(11, this.mStrokeAlpha);
            this.mStrokeWidth = a.getFloat(4, this.mStrokeWidth);
            this.mTrimPathEnd = a.getFloat(6, this.mTrimPathEnd);
            this.mTrimPathOffset = a.getFloat(7, this.mTrimPathOffset);
            this.mTrimPathStart = a.getFloat(5, this.mTrimPathStart);
        }

        public void applyTheme(Theme t) {
            if (this.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(this.mThemeAttrs, R.styleable.VectorDrawablePath);
                updateStateFromTypedArray(a);
                a.recycle();
            }
        }

        int getStrokeColor() {
            return this.mStrokeColor;
        }

        void setStrokeColor(int strokeColor) {
            this.mStrokeColor = strokeColor;
        }

        float getStrokeWidth() {
            return this.mStrokeWidth;
        }

        void setStrokeWidth(float strokeWidth) {
            this.mStrokeWidth = strokeWidth;
        }

        float getStrokeAlpha() {
            return this.mStrokeAlpha;
        }

        void setStrokeAlpha(float strokeAlpha) {
            this.mStrokeAlpha = strokeAlpha;
        }

        int getFillColor() {
            return this.mFillColor;
        }

        void setFillColor(int fillColor) {
            this.mFillColor = fillColor;
        }

        float getFillAlpha() {
            return this.mFillAlpha;
        }

        void setFillAlpha(float fillAlpha) {
            this.mFillAlpha = fillAlpha;
        }

        float getTrimPathStart() {
            return this.mTrimPathStart;
        }

        void setTrimPathStart(float trimPathStart) {
            this.mTrimPathStart = trimPathStart;
        }

        float getTrimPathEnd() {
            return this.mTrimPathEnd;
        }

        void setTrimPathEnd(float trimPathEnd) {
            this.mTrimPathEnd = trimPathEnd;
        }

        float getTrimPathOffset() {
            return this.mTrimPathOffset;
        }

        void setTrimPathOffset(float trimPathOffset) {
            this.mTrimPathOffset = trimPathOffset;
        }
    }

    private static class VGroup {
        private int mChangingConfigurations;
        final ArrayList<Object> mChildren;
        private String mGroupName;
        private final Matrix mLocalMatrix;
        private float mPivotX;
        private float mPivotY;
        private float mRotate;
        private float mScaleX;
        private float mScaleY;
        private final Matrix mStackedMatrix;
        private int[] mThemeAttrs;
        private float mTranslateX;
        private float mTranslateY;

        public VGroup(VGroup copy, ArrayMap<String, Object> targetsMap) {
            this.mStackedMatrix = new Matrix();
            this.mChildren = new ArrayList();
            this.mRotate = 0.0f;
            this.mPivotX = 0.0f;
            this.mPivotY = 0.0f;
            this.mScaleX = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mScaleY = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mTranslateX = 0.0f;
            this.mTranslateY = 0.0f;
            this.mLocalMatrix = new Matrix();
            this.mGroupName = null;
            this.mRotate = copy.mRotate;
            this.mPivotX = copy.mPivotX;
            this.mPivotY = copy.mPivotY;
            this.mScaleX = copy.mScaleX;
            this.mScaleY = copy.mScaleY;
            this.mTranslateX = copy.mTranslateX;
            this.mTranslateY = copy.mTranslateY;
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mGroupName = copy.mGroupName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            if (this.mGroupName != null) {
                targetsMap.put(this.mGroupName, this);
            }
            this.mLocalMatrix.set(copy.mLocalMatrix);
            ArrayList<Object> children = copy.mChildren;
            for (int i = VectorDrawable.LINEJOIN_MITER; i < children.size(); i += VectorDrawable.LINEJOIN_ROUND) {
                VGroup copyChild = children.get(i);
                if (copyChild instanceof VGroup) {
                    this.mChildren.add(new VGroup(copyChild, targetsMap));
                } else {
                    VPath newPath;
                    if (copyChild instanceof VFullPath) {
                        newPath = new VFullPath((VFullPath) copyChild);
                    } else if (copyChild instanceof VClipPath) {
                        newPath = new VClipPath((VClipPath) copyChild);
                    } else {
                        throw new IllegalStateException("Unknown object in the tree!");
                    }
                    this.mChildren.add(newPath);
                    if (newPath.mPathName != null) {
                        targetsMap.put(newPath.mPathName, newPath);
                    }
                }
            }
        }

        public VGroup() {
            this.mStackedMatrix = new Matrix();
            this.mChildren = new ArrayList();
            this.mRotate = 0.0f;
            this.mPivotX = 0.0f;
            this.mPivotY = 0.0f;
            this.mScaleX = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mScaleY = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mTranslateX = 0.0f;
            this.mTranslateY = 0.0f;
            this.mLocalMatrix = new Matrix();
            this.mGroupName = null;
        }

        public String getGroupName() {
            return this.mGroupName;
        }

        public Matrix getLocalMatrix() {
            return this.mLocalMatrix;
        }

        public void inflate(Resources res, AttributeSet attrs, Theme theme) {
            TypedArray a = Drawable.obtainAttributes(res, theme, attrs, R.styleable.VectorDrawableGroup);
            updateStateFromTypedArray(a);
            a.recycle();
        }

        private void updateStateFromTypedArray(TypedArray a) {
            this.mChangingConfigurations |= a.getChangingConfigurations();
            this.mThemeAttrs = a.extractThemeAttrs();
            this.mRotate = a.getFloat(5, this.mRotate);
            this.mPivotX = a.getFloat(VectorDrawable.LINEJOIN_ROUND, this.mPivotX);
            this.mPivotY = a.getFloat(VectorDrawable.LINEJOIN_BEVEL, this.mPivotY);
            this.mScaleX = a.getFloat(3, this.mScaleX);
            this.mScaleY = a.getFloat(4, this.mScaleY);
            this.mTranslateX = a.getFloat(6, this.mTranslateX);
            this.mTranslateY = a.getFloat(7, this.mTranslateY);
            String groupName = a.getString(VectorDrawable.LINEJOIN_MITER);
            if (groupName != null) {
                this.mGroupName = groupName;
            }
            updateLocalMatrix();
        }

        public boolean canApplyTheme() {
            return this.mThemeAttrs != null ? true : VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public void applyTheme(Theme t) {
            if (this.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(this.mThemeAttrs, R.styleable.VectorDrawableGroup);
                updateStateFromTypedArray(a);
                a.recycle();
            }
        }

        private void updateLocalMatrix() {
            this.mLocalMatrix.reset();
            this.mLocalMatrix.postTranslate(-this.mPivotX, -this.mPivotY);
            this.mLocalMatrix.postScale(this.mScaleX, this.mScaleY);
            this.mLocalMatrix.postRotate(this.mRotate, 0.0f, 0.0f);
            this.mLocalMatrix.postTranslate(this.mTranslateX + this.mPivotX, this.mTranslateY + this.mPivotY);
        }

        public float getRotation() {
            return this.mRotate;
        }

        public void setRotation(float rotation) {
            if (rotation != this.mRotate) {
                this.mRotate = rotation;
                updateLocalMatrix();
            }
        }

        public float getPivotX() {
            return this.mPivotX;
        }

        public void setPivotX(float pivotX) {
            if (pivotX != this.mPivotX) {
                this.mPivotX = pivotX;
                updateLocalMatrix();
            }
        }

        public float getPivotY() {
            return this.mPivotY;
        }

        public void setPivotY(float pivotY) {
            if (pivotY != this.mPivotY) {
                this.mPivotY = pivotY;
                updateLocalMatrix();
            }
        }

        public float getScaleX() {
            return this.mScaleX;
        }

        public void setScaleX(float scaleX) {
            if (scaleX != this.mScaleX) {
                this.mScaleX = scaleX;
                updateLocalMatrix();
            }
        }

        public float getScaleY() {
            return this.mScaleY;
        }

        public void setScaleY(float scaleY) {
            if (scaleY != this.mScaleY) {
                this.mScaleY = scaleY;
                updateLocalMatrix();
            }
        }

        public float getTranslateX() {
            return this.mTranslateX;
        }

        public void setTranslateX(float translateX) {
            if (translateX != this.mTranslateX) {
                this.mTranslateX = translateX;
                updateLocalMatrix();
            }
        }

        public float getTranslateY() {
            return this.mTranslateY;
        }

        public void setTranslateY(float translateY) {
            if (translateY != this.mTranslateY) {
                this.mTranslateY = translateY;
                updateLocalMatrix();
            }
        }
    }

    private static class VPathRenderer {
        private static final Matrix IDENTITY_MATRIX;
        float mBaseHeight;
        float mBaseWidth;
        private int mChangingConfigurations;
        private Paint mFillPaint;
        private final Matrix mFinalPathMatrix;
        private final Path mPath;
        private PathMeasure mPathMeasure;
        private final Path mRenderPath;
        int mRootAlpha;
        private final VGroup mRootGroup;
        String mRootName;
        private Paint mStrokePaint;
        final ArrayMap<String, Object> mVGTargetsMap;
        float mViewportHeight;
        float mViewportWidth;

        static {
            IDENTITY_MATRIX = new Matrix();
        }

        public VPathRenderer() {
            this.mFinalPathMatrix = new Matrix();
            this.mBaseWidth = 0.0f;
            this.mBaseHeight = 0.0f;
            this.mViewportWidth = 0.0f;
            this.mViewportHeight = 0.0f;
            this.mRootAlpha = EditorInfo.IME_MASK_ACTION;
            this.mRootName = null;
            this.mVGTargetsMap = new ArrayMap();
            this.mRootGroup = new VGroup();
            this.mPath = new Path();
            this.mRenderPath = new Path();
        }

        public void setRootAlpha(int alpha) {
            this.mRootAlpha = alpha;
        }

        public int getRootAlpha() {
            return this.mRootAlpha;
        }

        public void setAlpha(float alpha) {
            setRootAlpha((int) (255.0f * alpha));
        }

        public float getAlpha() {
            return ((float) getRootAlpha()) / 255.0f;
        }

        public VPathRenderer(VPathRenderer copy) {
            this.mFinalPathMatrix = new Matrix();
            this.mBaseWidth = 0.0f;
            this.mBaseHeight = 0.0f;
            this.mViewportWidth = 0.0f;
            this.mViewportHeight = 0.0f;
            this.mRootAlpha = EditorInfo.IME_MASK_ACTION;
            this.mRootName = null;
            this.mVGTargetsMap = new ArrayMap();
            this.mRootGroup = new VGroup(copy.mRootGroup, this.mVGTargetsMap);
            this.mPath = new Path(copy.mPath);
            this.mRenderPath = new Path(copy.mRenderPath);
            this.mBaseWidth = copy.mBaseWidth;
            this.mBaseHeight = copy.mBaseHeight;
            this.mViewportWidth = copy.mViewportWidth;
            this.mViewportHeight = copy.mViewportHeight;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            this.mRootAlpha = copy.mRootAlpha;
            this.mRootName = copy.mRootName;
            if (copy.mRootName != null) {
                this.mVGTargetsMap.put(copy.mRootName, this);
            }
        }

        public boolean canApplyTheme() {
            return recursiveCanApplyTheme(this.mRootGroup);
        }

        private boolean recursiveCanApplyTheme(VGroup currentGroup) {
            ArrayList<Object> children = currentGroup.mChildren;
            for (int i = VectorDrawable.LINEJOIN_MITER; i < children.size(); i += VectorDrawable.LINEJOIN_ROUND) {
                VGroup child = children.get(i);
                if (child instanceof VGroup) {
                    VGroup childGroup = child;
                    if (childGroup.canApplyTheme() || recursiveCanApplyTheme(childGroup)) {
                        return true;
                    }
                } else if ((child instanceof VPath) && ((VPath) child).canApplyTheme()) {
                    return true;
                }
            }
            return VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public void applyTheme(Theme t) {
            recursiveApplyTheme(this.mRootGroup, t);
        }

        private void recursiveApplyTheme(VGroup currentGroup, Theme t) {
            ArrayList<Object> children = currentGroup.mChildren;
            for (int i = VectorDrawable.LINEJOIN_MITER; i < children.size(); i += VectorDrawable.LINEJOIN_ROUND) {
                VGroup child = children.get(i);
                if (child instanceof VGroup) {
                    VGroup childGroup = child;
                    if (childGroup.canApplyTheme()) {
                        childGroup.applyTheme(t);
                    }
                    recursiveApplyTheme(childGroup, t);
                } else if (child instanceof VPath) {
                    VPath childPath = (VPath) child;
                    if (childPath.canApplyTheme()) {
                        childPath.applyTheme(t);
                    }
                }
            }
        }

        private void drawGroupTree(VGroup currentGroup, Matrix currentMatrix, Canvas canvas, int w, int h, ColorFilter filter) {
            currentGroup.mStackedMatrix.set(currentMatrix);
            currentGroup.mStackedMatrix.preConcat(currentGroup.mLocalMatrix);
            for (int i = VectorDrawable.LINEJOIN_MITER; i < currentGroup.mChildren.size(); i += VectorDrawable.LINEJOIN_ROUND) {
                VGroup child = currentGroup.mChildren.get(i);
                if (child instanceof VGroup) {
                    drawGroupTree(child, currentGroup.mStackedMatrix, canvas, w, h, filter);
                } else if (child instanceof VPath) {
                    drawPath(currentGroup, (VPath) child, canvas, w, h, filter);
                }
            }
        }

        public void draw(Canvas canvas, int w, int h, ColorFilter filter) {
            drawGroupTree(this.mRootGroup, IDENTITY_MATRIX, canvas, w, h, filter);
        }

        private void drawPath(VGroup vGroup, VPath vPath, Canvas canvas, int w, int h, ColorFilter filter) {
            float scaleX = ((float) w) / this.mViewportWidth;
            float scaleY = ((float) h) / this.mViewportHeight;
            float minScale = Math.min(scaleX, scaleY);
            this.mFinalPathMatrix.set(vGroup.mStackedMatrix);
            this.mFinalPathMatrix.postScale(scaleX, scaleY);
            vPath.toPath(this.mPath);
            Path path = this.mPath;
            this.mRenderPath.reset();
            if (vPath.isClipPath()) {
                this.mRenderPath.addPath(path, this.mFinalPathMatrix);
                canvas.clipPath(this.mRenderPath, Op.REPLACE);
                return;
            }
            VFullPath fullPath = (VFullPath) vPath;
            if (!(fullPath.mTrimPathStart == 0.0f && fullPath.mTrimPathEnd == LayoutParams.BRIGHTNESS_OVERRIDE_FULL)) {
                float start = (fullPath.mTrimPathStart + fullPath.mTrimPathOffset) % LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                float end = (fullPath.mTrimPathEnd + fullPath.mTrimPathOffset) % LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                if (this.mPathMeasure == null) {
                    this.mPathMeasure = new PathMeasure();
                }
                this.mPathMeasure.setPath(this.mPath, VectorDrawable.DBG_VECTOR_DRAWABLE);
                float len = this.mPathMeasure.getLength();
                start *= len;
                end *= len;
                path.reset();
                if (start > end) {
                    this.mPathMeasure.getSegment(start, len, path, true);
                    this.mPathMeasure.getSegment(0.0f, end, path, true);
                } else {
                    this.mPathMeasure.getSegment(start, end, path, true);
                }
                path.rLineTo(0.0f, 0.0f);
            }
            this.mRenderPath.addPath(path, this.mFinalPathMatrix);
            if (fullPath.mFillColor != 0) {
                if (this.mFillPaint == null) {
                    this.mFillPaint = new Paint();
                    this.mFillPaint.setStyle(Style.FILL);
                    this.mFillPaint.setAntiAlias(true);
                }
                Paint fillPaint = this.mFillPaint;
                fillPaint.setColor(VectorDrawable.applyAlpha(fullPath.mFillColor, fullPath.mFillAlpha));
                fillPaint.setColorFilter(filter);
                canvas.drawPath(this.mRenderPath, fillPaint);
            }
            if (fullPath.mStrokeColor != 0) {
                if (this.mStrokePaint == null) {
                    this.mStrokePaint = new Paint();
                    this.mStrokePaint.setStyle(Style.STROKE);
                    this.mStrokePaint.setAntiAlias(true);
                }
                Paint strokePaint = this.mStrokePaint;
                if (fullPath.mStrokeLineJoin != null) {
                    strokePaint.setStrokeJoin(fullPath.mStrokeLineJoin);
                }
                if (fullPath.mStrokeLineCap != null) {
                    strokePaint.setStrokeCap(fullPath.mStrokeLineCap);
                }
                strokePaint.setStrokeMiter(fullPath.mStrokeMiterlimit);
                strokePaint.setColor(VectorDrawable.applyAlpha(fullPath.mStrokeColor, fullPath.mStrokeAlpha));
                strokePaint.setColorFilter(filter);
                strokePaint.setStrokeWidth(fullPath.mStrokeWidth * minScale);
                canvas.drawPath(this.mRenderPath, strokePaint);
            }
        }
    }

    private static class VectorDrawableState extends ConstantState {
        boolean mAutoMirrored;
        boolean mCacheDirty;
        boolean mCachedAutoMirrored;
        Bitmap mCachedBitmap;
        int mCachedRootAlpha;
        int[] mCachedThemeAttrs;
        ColorStateList mCachedTint;
        Mode mCachedTintMode;
        int mChangingConfigurations;
        Paint mTempPaint;
        int[] mThemeAttrs;
        ColorStateList mTint;
        Mode mTintMode;
        VPathRenderer mVPathRenderer;

        public VectorDrawableState(VectorDrawableState copy) {
            this.mTint = null;
            this.mTintMode = Drawable.DEFAULT_TINT_MODE;
            if (copy != null) {
                this.mThemeAttrs = copy.mThemeAttrs;
                this.mChangingConfigurations = copy.mChangingConfigurations;
                this.mVPathRenderer = new VPathRenderer(copy.mVPathRenderer);
                if (copy.mVPathRenderer.mFillPaint != null) {
                    this.mVPathRenderer.mFillPaint = new Paint(copy.mVPathRenderer.mFillPaint);
                }
                if (copy.mVPathRenderer.mStrokePaint != null) {
                    this.mVPathRenderer.mStrokePaint = new Paint(copy.mVPathRenderer.mStrokePaint);
                }
                this.mTint = copy.mTint;
                this.mTintMode = copy.mTintMode;
                this.mAutoMirrored = copy.mAutoMirrored;
            }
        }

        public void drawCachedBitmapWithRootAlpha(Canvas canvas, ColorFilter filter) {
            canvas.drawBitmap(this.mCachedBitmap, 0.0f, 0.0f, getPaint(filter));
        }

        public boolean hasTranslucentRoot() {
            return this.mVPathRenderer.getRootAlpha() < EditorInfo.IME_MASK_ACTION ? true : VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public Paint getPaint(ColorFilter filter) {
            if (!hasTranslucentRoot() && filter == null) {
                return null;
            }
            if (this.mTempPaint == null) {
                this.mTempPaint = new Paint();
                this.mTempPaint.setFilterBitmap(true);
            }
            this.mTempPaint.setAlpha(this.mVPathRenderer.getRootAlpha());
            this.mTempPaint.setColorFilter(filter);
            return this.mTempPaint;
        }

        public void updateCachedBitmap(Rect bounds) {
            this.mCachedBitmap.eraseColor(VectorDrawable.LINEJOIN_MITER);
            this.mVPathRenderer.draw(new Canvas(this.mCachedBitmap), bounds.width(), bounds.height(), null);
        }

        public void createCachedBitmapIfNeeded(Rect bounds) {
            if (this.mCachedBitmap == null || !canReuseBitmap(bounds.width(), bounds.height())) {
                this.mCachedBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Config.ARGB_8888);
                this.mCacheDirty = true;
            }
        }

        public boolean canReuseBitmap(int width, int height) {
            if (width == this.mCachedBitmap.getWidth() && height == this.mCachedBitmap.getHeight()) {
                return true;
            }
            return VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public boolean canReuseCache() {
            if (!this.mCacheDirty && this.mCachedThemeAttrs == this.mThemeAttrs && this.mCachedTint == this.mTint && this.mCachedTintMode == this.mTintMode && this.mCachedAutoMirrored == this.mAutoMirrored && this.mCachedRootAlpha == this.mVPathRenderer.getRootAlpha()) {
                return true;
            }
            return VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public void updateCacheStates() {
            this.mCachedThemeAttrs = this.mThemeAttrs;
            this.mCachedTint = this.mTint;
            this.mCachedTintMode = this.mTintMode;
            this.mCachedRootAlpha = this.mVPathRenderer.getRootAlpha();
            this.mCachedAutoMirrored = this.mAutoMirrored;
            this.mCacheDirty = VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public boolean canApplyTheme() {
            return (this.mThemeAttrs != null || ((this.mVPathRenderer != null && this.mVPathRenderer.canApplyTheme()) || super.canApplyTheme())) ? true : VectorDrawable.DBG_VECTOR_DRAWABLE;
        }

        public VectorDrawableState() {
            this.mTint = null;
            this.mTintMode = Drawable.DEFAULT_TINT_MODE;
            this.mVPathRenderer = new VPathRenderer();
        }

        public Drawable newDrawable() {
            return new VectorDrawable();
        }

        public Drawable newDrawable(Resources res) {
            return new VectorDrawable();
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    static {
        LOGTAG = VectorDrawable.class.getSimpleName();
    }

    public VectorDrawable() {
        this.mAllowCaching = true;
        this.mVectorState = new VectorDrawableState();
    }

    private VectorDrawable(VectorDrawableState state) {
        this.mAllowCaching = true;
        this.mVectorState = state;
        this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mVectorState = new VectorDrawableState(this.mVectorState);
            this.mMutated = true;
        }
        return this;
    }

    public void clearMutated() {
        super.clearMutated();
        this.mMutated = DBG_VECTOR_DRAWABLE;
    }

    Object getTargetByName(String name) {
        return this.mVectorState.mVPathRenderer.mVGTargetsMap.get(name);
    }

    public ConstantState getConstantState() {
        this.mVectorState.mChangingConfigurations = getChangingConfigurations();
        return this.mVectorState;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.width() != 0 && bounds.height() != 0) {
            int saveCount = canvas.save();
            boolean needMirroring = needMirroring();
            canvas.translate((float) bounds.left, (float) bounds.top);
            if (needMirroring) {
                canvas.translate((float) bounds.width(), 0.0f);
                canvas.scale(LayoutParams.BRIGHTNESS_OVERRIDE_NONE, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
            }
            ColorFilter colorFilter = this.mColorFilter == null ? this.mTintFilter : this.mColorFilter;
            if (this.mAllowCaching) {
                this.mVectorState.createCachedBitmapIfNeeded(bounds);
                if (!this.mVectorState.canReuseCache()) {
                    this.mVectorState.updateCachedBitmap(bounds);
                    this.mVectorState.updateCacheStates();
                }
                this.mVectorState.drawCachedBitmapWithRootAlpha(canvas, colorFilter);
            } else if (this.mVectorState.hasTranslucentRoot()) {
                this.mVectorState.createCachedBitmapIfNeeded(bounds);
                this.mVectorState.updateCachedBitmap(bounds);
                this.mVectorState.drawCachedBitmapWithRootAlpha(canvas, colorFilter);
            } else {
                this.mVectorState.mVPathRenderer.draw(canvas, bounds.width(), bounds.height(), colorFilter);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    public int getAlpha() {
        return this.mVectorState.mVPathRenderer.getRootAlpha();
    }

    public void setAlpha(int alpha) {
        if (this.mVectorState.mVPathRenderer.getRootAlpha() != alpha) {
            this.mVectorState.mVPathRenderer.setRootAlpha(alpha);
            invalidateSelf();
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mColorFilter = colorFilter;
        invalidateSelf();
    }

    public void setTintList(ColorStateList tint) {
        VectorDrawableState state = this.mVectorState;
        if (state.mTint != tint) {
            state.mTint = tint;
            this.mTintFilter = updateTintFilter(this.mTintFilter, tint, state.mTintMode);
            invalidateSelf();
        }
    }

    public void setTintMode(Mode tintMode) {
        VectorDrawableState state = this.mVectorState;
        if (state.mTintMode != tintMode) {
            state.mTintMode = tintMode;
            this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, tintMode);
            invalidateSelf();
        }
    }

    public boolean isStateful() {
        return (super.isStateful() || !(this.mVectorState == null || this.mVectorState.mTint == null || !this.mVectorState.mTint.isStateful())) ? true : DBG_VECTOR_DRAWABLE;
    }

    protected boolean onStateChange(int[] stateSet) {
        VectorDrawableState state = this.mVectorState;
        if (state.mTint == null || state.mTintMode == null) {
            return DBG_VECTOR_DRAWABLE;
        }
        this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
        invalidateSelf();
        return true;
    }

    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicWidth() {
        return (int) this.mVectorState.mVPathRenderer.mBaseWidth;
    }

    public int getIntrinsicHeight() {
        return (int) this.mVectorState.mVPathRenderer.mBaseHeight;
    }

    public boolean canApplyTheme() {
        return ((this.mVectorState == null || !this.mVectorState.canApplyTheme()) && !super.canApplyTheme()) ? DBG_VECTOR_DRAWABLE : true;
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        VectorDrawableState state = this.mVectorState;
        if (!(state == null || state.mThemeAttrs == null)) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.VectorDrawable);
            try {
                state.mCacheDirty = true;
                updateStateFromTypedArray(a);
                a.recycle();
                this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
            } catch (XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (Throwable th) {
                a.recycle();
            }
        }
        VPathRenderer path = state.mVPathRenderer;
        if (path != null && path.canApplyTheme()) {
            path.applyTheme(t);
        }
    }

    public float getPixelSize() {
        if ((this.mVectorState == null && this.mVectorState.mVPathRenderer == null) || this.mVectorState.mVPathRenderer.mBaseWidth == 0.0f || this.mVectorState.mVPathRenderer.mBaseHeight == 0.0f || this.mVectorState.mVPathRenderer.mViewportHeight == 0.0f || this.mVectorState.mVPathRenderer.mViewportWidth == 0.0f) {
            return LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        }
        float intrinsicWidth = this.mVectorState.mVPathRenderer.mBaseWidth;
        float intrinsicHeight = this.mVectorState.mVPathRenderer.mBaseHeight;
        return Math.min(this.mVectorState.mVPathRenderer.mViewportWidth / intrinsicWidth, this.mVectorState.mVPathRenderer.mViewportHeight / intrinsicHeight);
    }

    public static VectorDrawable create(Resources resources, int rid) {
        try {
            int type;
            XmlPullParser parser = resources.getXml(rid);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            do {
                type = parser.next();
                if (type == LINEJOIN_BEVEL) {
                    break;
                }
            } while (type != LINEJOIN_ROUND);
            if (type != LINEJOIN_BEVEL) {
                throw new XmlPullParserException("No start tag found");
            }
            VectorDrawable drawable = new VectorDrawable();
            drawable.inflate(resources, parser, attrs);
            return drawable;
        } catch (XmlPullParserException e) {
            Log.e(LOGTAG, "parser error", e);
            return null;
        } catch (IOException e2) {
            Log.e(LOGTAG, "parser error", e2);
            return null;
        }
    }

    private static int applyAlpha(int color, float alpha) {
        return (color & View.MEASURED_SIZE_MASK) | (((int) (((float) Color.alpha(color)) * alpha)) << 24);
    }

    public void inflate(Resources res, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        VectorDrawableState state = this.mVectorState;
        state.mVPathRenderer = new VPathRenderer();
        TypedArray a = Drawable.obtainAttributes(res, theme, attrs, R.styleable.VectorDrawable);
        updateStateFromTypedArray(a);
        a.recycle();
        state.mCacheDirty = true;
        inflateInternal(res, parser, attrs, theme);
        this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
    }

    private void updateStateFromTypedArray(TypedArray a) throws XmlPullParserException {
        VectorDrawableState state = this.mVectorState;
        VPathRenderer pathRenderer = state.mVPathRenderer;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        int tintMode = a.getInt(6, -1);
        if (tintMode != -1) {
            state.mTintMode = Drawable.parseTintMode(tintMode, Mode.SRC_IN);
        }
        ColorStateList tint = a.getColorStateList(LINEJOIN_ROUND);
        if (tint != null) {
            state.mTint = tint;
        }
        state.mAutoMirrored = a.getBoolean(5, state.mAutoMirrored);
        pathRenderer.mViewportWidth = a.getFloat(7, pathRenderer.mViewportWidth);
        pathRenderer.mViewportHeight = a.getFloat(8, pathRenderer.mViewportHeight);
        if (pathRenderer.mViewportWidth <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
        } else if (pathRenderer.mViewportHeight <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
        } else {
            pathRenderer.mBaseWidth = a.getDimension(3, pathRenderer.mBaseWidth);
            pathRenderer.mBaseHeight = a.getDimension(LINEJOIN_BEVEL, pathRenderer.mBaseHeight);
            if (pathRenderer.mBaseWidth <= 0.0f) {
                throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires width > 0");
            } else if (pathRenderer.mBaseHeight <= 0.0f) {
                throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires height > 0");
            } else {
                pathRenderer.setAlpha(a.getFloat(4, pathRenderer.getAlpha()));
                String name = a.getString(LINEJOIN_MITER);
                if (name != null) {
                    pathRenderer.mRootName = name;
                    pathRenderer.mVGTargetsMap.put(name, pathRenderer);
                }
            }
        }
    }

    private void inflateInternal(Resources res, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        VectorDrawableState state = this.mVectorState;
        VPathRenderer pathRenderer = state.mVPathRenderer;
        boolean noPathTag = true;
        Stack<VGroup> groupStack = new Stack();
        groupStack.push(pathRenderer.mRootGroup);
        int eventType = parser.getEventType();
        while (eventType != LINEJOIN_ROUND) {
            if (eventType == LINEJOIN_BEVEL) {
                String tagName = parser.getName();
                VGroup currentGroup = (VGroup) groupStack.peek();
                if (SHAPE_PATH.equals(tagName)) {
                    VFullPath path = new VFullPath();
                    path.inflate(res, attrs, theme);
                    currentGroup.mChildren.add(path);
                    if (path.getPathName() != null) {
                        pathRenderer.mVGTargetsMap.put(path.getPathName(), path);
                    }
                    noPathTag = DBG_VECTOR_DRAWABLE;
                    state.mChangingConfigurations |= path.mChangingConfigurations;
                } else if (SHAPE_CLIP_PATH.equals(tagName)) {
                    VClipPath path2 = new VClipPath();
                    path2.inflate(res, attrs, theme);
                    currentGroup.mChildren.add(path2);
                    if (path2.getPathName() != null) {
                        pathRenderer.mVGTargetsMap.put(path2.getPathName(), path2);
                    }
                    state.mChangingConfigurations |= path2.mChangingConfigurations;
                } else if (SHAPE_GROUP.equals(tagName)) {
                    VGroup newChildGroup = new VGroup();
                    newChildGroup.inflate(res, attrs, theme);
                    currentGroup.mChildren.add(newChildGroup);
                    groupStack.push(newChildGroup);
                    if (newChildGroup.getGroupName() != null) {
                        pathRenderer.mVGTargetsMap.put(newChildGroup.getGroupName(), newChildGroup);
                    }
                    state.mChangingConfigurations |= newChildGroup.mChangingConfigurations;
                }
            } else if (eventType == 3) {
                if (SHAPE_GROUP.equals(parser.getName())) {
                    groupStack.pop();
                }
            }
            eventType = parser.next();
        }
        if (noPathTag) {
            StringBuffer tag = new StringBuffer();
            if (tag.length() > 0) {
                tag.append(" or ");
            }
            tag.append(SHAPE_PATH);
            throw new XmlPullParserException("no " + tag + " defined");
        }
    }

    private void printGroupTree(VGroup currentGroup, int level) {
        int i;
        String indent = ProxyInfo.LOCAL_EXCL_LIST;
        for (i = LINEJOIN_MITER; i < level; i += LINEJOIN_ROUND) {
            indent = indent + "    ";
        }
        Log.v(LOGTAG, indent + "current group is :" + currentGroup.getGroupName() + " rotation is " + currentGroup.mRotate);
        Log.v(LOGTAG, indent + "matrix is :" + currentGroup.getLocalMatrix().toString());
        for (i = LINEJOIN_MITER; i < currentGroup.mChildren.size(); i += LINEJOIN_ROUND) {
            Object child = currentGroup.mChildren.get(i);
            if (child instanceof VGroup) {
                printGroupTree((VGroup) child, level + LINEJOIN_ROUND);
            }
        }
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mVectorState.mChangingConfigurations;
    }

    void setAllowCaching(boolean allowCaching) {
        this.mAllowCaching = allowCaching;
    }

    private boolean needMirroring() {
        return (isAutoMirrored() && getLayoutDirection() == LINEJOIN_ROUND) ? true : DBG_VECTOR_DRAWABLE;
    }

    public void setAutoMirrored(boolean mirrored) {
        if (this.mVectorState.mAutoMirrored != mirrored) {
            this.mVectorState.mAutoMirrored = mirrored;
            invalidateSelf();
        }
    }

    public boolean isAutoMirrored() {
        return this.mVectorState.mAutoMirrored;
    }
}
