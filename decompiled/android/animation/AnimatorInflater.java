package android.animation;

import android.R;
import android.content.Context;
import android.content.res.ConfigurationBoundResourceCache;
import android.content.res.ConstantState;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.speech.tts.Voice;
import android.util.AttributeSet;
import android.util.PathParser;
import android.util.PathParser.PathDataNode;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.BaseInterpolator;
import android.view.animation.Interpolator;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimatorInflater {
    private static final boolean DBG_ANIMATOR_INFLATER = false;
    private static final int SEQUENTIALLY = 1;
    private static final String TAG = "AnimatorInflater";
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_COLOR = 4;
    private static final int VALUE_TYPE_CUSTOM = 5;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_PATH = 2;
    private static final TypedValue sTmpTypedValue;

    private static class PathDataEvaluator implements TypeEvaluator<PathDataNode[]> {
        private PathDataNode[] mNodeArray;

        public PathDataEvaluator(android.util.PathParser.PathDataNode[] r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.animation.AnimatorInflater.PathDataEvaluator.<init>(android.util.PathParser$PathDataNode[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.animation.AnimatorInflater.PathDataEvaluator.<init>(android.util.PathParser$PathDataNode[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorInflater.PathDataEvaluator.<init>(android.util.PathParser$PathDataNode[]):void");
        }

        public android.util.PathParser.PathDataNode[] evaluate(float r1, android.util.PathParser.PathDataNode[] r2, android.util.PathParser.PathDataNode[] r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.animation.AnimatorInflater.PathDataEvaluator.evaluate(float, android.util.PathParser$PathDataNode[], android.util.PathParser$PathDataNode[]):android.util.PathParser$PathDataNode[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.animation.AnimatorInflater.PathDataEvaluator.evaluate(float, android.util.PathParser$PathDataNode[], android.util.PathParser$PathDataNode[]):android.util.PathParser$PathDataNode[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorInflater.PathDataEvaluator.evaluate(float, android.util.PathParser$PathDataNode[], android.util.PathParser$PathDataNode[]):android.util.PathParser$PathDataNode[]");
        }

        private PathDataEvaluator() {
        }
    }

    static {
        sTmpTypedValue = new TypedValue();
    }

    public static Animator loadAnimator(Context context, int id) throws NotFoundException {
        return loadAnimator(context.getResources(), context.getTheme(), id);
    }

    public static Animator loadAnimator(Resources resources, Theme theme, int id) throws NotFoundException {
        return loadAnimator(resources, theme, id, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
    }

    public static Animator loadAnimator(Resources resources, Theme theme, int id, float pathErrorScale) throws NotFoundException {
        NotFoundException rnf;
        ConfigurationBoundResourceCache<Animator> animatorCache = resources.getAnimatorCache();
        Animator animator = (Animator) animatorCache.get((long) id, theme);
        if (animator != null) {
            return animator;
        }
        XmlResourceParser parser = null;
        try {
            parser = resources.getAnimation(id);
            animator = createAnimatorFromXml(resources, theme, parser, pathErrorScale);
            if (animator != null) {
                animator.appendChangingConfigurations(getChangingConfigs(resources, id));
                ConstantState<Animator> constantState = animator.createConstantState();
                if (constantState != null) {
                    animatorCache.put((long) id, theme, constantState);
                    animator = (Animator) constantState.newInstance(resources, theme);
                }
            }
            if (parser != null) {
                parser.close();
            }
            return animator;
        } catch (XmlPullParserException ex) {
            rnf = new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex2) {
            rnf = new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex2);
            throw rnf;
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
        }
    }

    public static StateListAnimator loadStateListAnimator(Context context, int id) throws NotFoundException {
        NotFoundException rnf;
        Resources resources = context.getResources();
        ConfigurationBoundResourceCache<StateListAnimator> cache = resources.getStateListAnimatorCache();
        Theme theme = context.getTheme();
        StateListAnimator animator = (StateListAnimator) cache.get((long) id, theme);
        if (animator != null) {
            return animator;
        }
        XmlResourceParser parser = null;
        try {
            parser = resources.getAnimation(id);
            animator = createStateListAnimatorFromXml(context, parser, Xml.asAttributeSet(parser));
            if (animator != null) {
                animator.appendChangingConfigurations(getChangingConfigs(resources, id));
                ConstantState<StateListAnimator> constantState = animator.createConstantState();
                if (constantState != null) {
                    cache.put((long) id, theme, constantState);
                    animator = (StateListAnimator) constantState.newInstance(resources, theme);
                }
            }
            if (parser != null) {
                parser.close();
            }
            return animator;
        } catch (XmlPullParserException ex) {
            rnf = new NotFoundException("Can't load state list animator resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex2) {
            rnf = new NotFoundException("Can't load state list animator resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex2);
            throw rnf;
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
        }
    }

    private static StateListAnimator createStateListAnimatorFromXml(Context context, XmlPullParser parser, AttributeSet attributeSet) throws IOException, XmlPullParserException {
        StateListAnimator stateListAnimator = new StateListAnimator();
        while (true) {
            switch (parser.next()) {
                case VALUE_TYPE_INT /*1*/:
                case SetDrawableParameters.TAG /*3*/:
                    return stateListAnimator;
                case VALUE_TYPE_PATH /*2*/:
                    Animator animator = null;
                    if ("item".equals(parser.getName())) {
                        int attributeCount = parser.getAttributeCount();
                        int[] states = new int[attributeCount];
                        int i = VALUE_TYPE_FLOAT;
                        int stateIndex = VALUE_TYPE_FLOAT;
                        while (i < attributeCount) {
                            int stateIndex2;
                            int attrName = attributeSet.getAttributeNameResource(i);
                            if (attrName == R.attr.animation) {
                                animator = loadAnimator(context, attributeSet.getAttributeResourceValue(i, VALUE_TYPE_FLOAT));
                                stateIndex2 = stateIndex;
                            } else {
                                stateIndex2 = stateIndex + VALUE_TYPE_INT;
                                if (!attributeSet.getAttributeBooleanValue(i, DBG_ANIMATOR_INFLATER)) {
                                    attrName = -attrName;
                                }
                                states[stateIndex] = attrName;
                            }
                            i += VALUE_TYPE_INT;
                            stateIndex = stateIndex2;
                        }
                        if (animator == null) {
                            animator = createAnimatorFromXml(context.getResources(), context.getTheme(), parser, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
                        }
                        if (animator != null) {
                            stateListAnimator.addState(StateSet.trimStateSet(states, stateIndex), animator);
                            break;
                        }
                        throw new NotFoundException("animation state item must have a valid animation");
                    }
                    continue;
                default:
                    break;
            }
        }
    }

    private static void parseAnimatorFromTypeArray(ValueAnimator anim, TypedArray arrayAnimator, TypedArray arrayObjectAnimator, float pixelSize) {
        long duration = (long) arrayAnimator.getInt(VALUE_TYPE_INT, Voice.QUALITY_NORMAL);
        long startDelay = (long) arrayAnimator.getInt(VALUE_TYPE_PATH, VALUE_TYPE_FLOAT);
        int valueType = arrayAnimator.getInt(7, VALUE_TYPE_FLOAT);
        TypeEvaluator evaluator = null;
        boolean getFloats = valueType == 0 ? true : DBG_ANIMATOR_INFLATER;
        TypedValue tvFrom = arrayAnimator.peekValue(VALUE_TYPE_CUSTOM);
        boolean hasFrom = tvFrom != null ? true : DBG_ANIMATOR_INFLATER;
        int fromType = hasFrom ? tvFrom.type : VALUE_TYPE_FLOAT;
        TypedValue tvTo = arrayAnimator.peekValue(6);
        boolean hasTo = tvTo != null ? true : DBG_ANIMATOR_INFLATER;
        int toType = hasTo ? tvTo.type : VALUE_TYPE_FLOAT;
        if (valueType == VALUE_TYPE_PATH) {
            evaluator = setupAnimatorForPath(anim, arrayAnimator);
        } else {
            if ((hasFrom && fromType >= 28 && fromType <= 31) || (hasTo && toType >= 28 && toType <= 31)) {
                getFloats = DBG_ANIMATOR_INFLATER;
                evaluator = ArgbEvaluator.getInstance();
            }
            setupValues(anim, arrayAnimator, getFloats, hasFrom, fromType, hasTo, toType);
        }
        anim.setDuration(duration);
        anim.setStartDelay(startDelay);
        if (arrayAnimator.hasValue(3)) {
            anim.setRepeatCount(arrayAnimator.getInt(3, VALUE_TYPE_FLOAT));
        }
        if (arrayAnimator.hasValue(VALUE_TYPE_COLOR)) {
            anim.setRepeatMode(arrayAnimator.getInt(VALUE_TYPE_COLOR, VALUE_TYPE_INT));
        }
        if (evaluator != null) {
            anim.setEvaluator(evaluator);
        }
        if (arrayObjectAnimator != null) {
            setupObjectAnimator(anim, arrayObjectAnimator, getFloats, pixelSize);
        }
    }

    private static TypeEvaluator setupAnimatorForPath(ValueAnimator anim, TypedArray arrayAnimator) {
        String fromString = arrayAnimator.getString(VALUE_TYPE_CUSTOM);
        String toString = arrayAnimator.getString(6);
        PathDataNode[] nodesFrom = PathParser.createNodesFromPathData(fromString);
        PathDataNode[] nodesTo = PathParser.createNodesFromPathData(toString);
        Object[] objArr;
        if (nodesFrom != null) {
            if (nodesTo != null) {
                objArr = new Object[VALUE_TYPE_PATH];
                objArr[VALUE_TYPE_FLOAT] = nodesFrom;
                objArr[VALUE_TYPE_INT] = nodesTo;
                anim.setObjectValues(objArr);
                if (!PathParser.canMorph(nodesFrom, nodesTo)) {
                    throw new InflateException(arrayAnimator.getPositionDescription() + " Can't morph from " + fromString + " to " + toString);
                }
            }
            objArr = new Object[VALUE_TYPE_INT];
            objArr[VALUE_TYPE_FLOAT] = nodesFrom;
            anim.setObjectValues(objArr);
            return new PathDataEvaluator(PathParser.deepCopyNodes(nodesFrom));
        } else if (nodesTo == null) {
            return null;
        } else {
            objArr = new Object[VALUE_TYPE_INT];
            objArr[VALUE_TYPE_FLOAT] = nodesTo;
            anim.setObjectValues(objArr);
            return new PathDataEvaluator(PathParser.deepCopyNodes(nodesTo));
        }
    }

    private static void setupObjectAnimator(ValueAnimator anim, TypedArray arrayObjectAnimator, boolean getFloats, float pixelSize) {
        ObjectAnimator oa = (ObjectAnimator) anim;
        String pathData = arrayObjectAnimator.getString(VALUE_TYPE_INT);
        if (pathData != null) {
            String propertyXName = arrayObjectAnimator.getString(VALUE_TYPE_PATH);
            String propertyYName = arrayObjectAnimator.getString(3);
            if (propertyXName == null && propertyYName == null) {
                throw new InflateException(arrayObjectAnimator.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
            }
            Keyframes xKeyframes;
            Keyframes yKeyframes;
            PathKeyframes keyframeSet = KeyframeSet.ofPath(PathParser.createPathFromPathData(pathData), 0.5f * pixelSize);
            if (getFloats) {
                xKeyframes = keyframeSet.createXFloatKeyframes();
                yKeyframes = keyframeSet.createYFloatKeyframes();
            } else {
                xKeyframes = keyframeSet.createXIntKeyframes();
                yKeyframes = keyframeSet.createYIntKeyframes();
            }
            PropertyValuesHolder x = null;
            PropertyValuesHolder y = null;
            if (propertyXName != null) {
                x = PropertyValuesHolder.ofKeyframes(propertyXName, xKeyframes);
            }
            if (propertyYName != null) {
                y = PropertyValuesHolder.ofKeyframes(propertyYName, yKeyframes);
            }
            PropertyValuesHolder[] propertyValuesHolderArr;
            if (x == null) {
                propertyValuesHolderArr = new PropertyValuesHolder[VALUE_TYPE_INT];
                propertyValuesHolderArr[VALUE_TYPE_FLOAT] = y;
                oa.setValues(propertyValuesHolderArr);
                return;
            } else if (y == null) {
                propertyValuesHolderArr = new PropertyValuesHolder[VALUE_TYPE_INT];
                propertyValuesHolderArr[VALUE_TYPE_FLOAT] = x;
                oa.setValues(propertyValuesHolderArr);
                return;
            } else {
                propertyValuesHolderArr = new PropertyValuesHolder[VALUE_TYPE_PATH];
                propertyValuesHolderArr[VALUE_TYPE_FLOAT] = x;
                propertyValuesHolderArr[VALUE_TYPE_INT] = y;
                oa.setValues(propertyValuesHolderArr);
                return;
            }
        }
        oa.setPropertyName(arrayObjectAnimator.getString(VALUE_TYPE_FLOAT));
    }

    private static void setupValues(ValueAnimator anim, TypedArray arrayAnimator, boolean getFloats, boolean hasFrom, int fromType, boolean hasTo, int toType) {
        if (getFloats) {
            float valueTo;
            float[] fArr;
            if (hasFrom) {
                float valueFrom;
                if (fromType == VALUE_TYPE_CUSTOM) {
                    valueFrom = arrayAnimator.getDimension(VALUE_TYPE_CUSTOM, 0.0f);
                } else {
                    valueFrom = arrayAnimator.getFloat(VALUE_TYPE_CUSTOM, 0.0f);
                }
                if (hasTo) {
                    if (toType == VALUE_TYPE_CUSTOM) {
                        valueTo = arrayAnimator.getDimension(6, 0.0f);
                    } else {
                        valueTo = arrayAnimator.getFloat(6, 0.0f);
                    }
                    fArr = new float[VALUE_TYPE_PATH];
                    fArr[VALUE_TYPE_FLOAT] = valueFrom;
                    fArr[VALUE_TYPE_INT] = valueTo;
                    anim.setFloatValues(fArr);
                    return;
                }
                fArr = new float[VALUE_TYPE_INT];
                fArr[VALUE_TYPE_FLOAT] = valueFrom;
                anim.setFloatValues(fArr);
                return;
            }
            if (toType == VALUE_TYPE_CUSTOM) {
                valueTo = arrayAnimator.getDimension(6, 0.0f);
            } else {
                valueTo = arrayAnimator.getFloat(6, 0.0f);
            }
            fArr = new float[VALUE_TYPE_INT];
            fArr[VALUE_TYPE_FLOAT] = valueTo;
            anim.setFloatValues(fArr);
        } else if (hasFrom) {
            int valueFrom2;
            if (fromType == VALUE_TYPE_CUSTOM) {
                valueFrom2 = (int) arrayAnimator.getDimension(VALUE_TYPE_CUSTOM, 0.0f);
            } else if (fromType < 28 || fromType > 31) {
                valueFrom2 = arrayAnimator.getInt(VALUE_TYPE_CUSTOM, VALUE_TYPE_FLOAT);
            } else {
                valueFrom2 = arrayAnimator.getColor(VALUE_TYPE_CUSTOM, VALUE_TYPE_FLOAT);
            }
            if (hasTo) {
                if (toType == VALUE_TYPE_CUSTOM) {
                    valueTo = (int) arrayAnimator.getDimension(6, 0.0f);
                } else if (toType < 28 || toType > 31) {
                    valueTo = arrayAnimator.getInt(6, VALUE_TYPE_FLOAT);
                } else {
                    valueTo = arrayAnimator.getColor(6, VALUE_TYPE_FLOAT);
                }
                r4 = new int[VALUE_TYPE_PATH];
                r4[VALUE_TYPE_FLOAT] = valueFrom2;
                r4[VALUE_TYPE_INT] = valueTo;
                anim.setIntValues(r4);
                return;
            }
            r4 = new int[VALUE_TYPE_INT];
            r4[VALUE_TYPE_FLOAT] = valueFrom2;
            anim.setIntValues(r4);
        } else if (hasTo) {
            if (toType == VALUE_TYPE_CUSTOM) {
                valueTo = (int) arrayAnimator.getDimension(6, 0.0f);
            } else if (toType < 28 || toType > 31) {
                valueTo = arrayAnimator.getInt(6, VALUE_TYPE_FLOAT);
            } else {
                valueTo = arrayAnimator.getColor(6, VALUE_TYPE_FLOAT);
            }
            r4 = new int[VALUE_TYPE_INT];
            r4[VALUE_TYPE_FLOAT] = valueTo;
            anim.setIntValues(r4);
        }
    }

    private static Animator createAnimatorFromXml(Resources res, Theme theme, XmlPullParser parser, float pixelSize) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(res, theme, parser, Xml.asAttributeSet(parser), null, VALUE_TYPE_FLOAT, pixelSize);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.animation.Animator createAnimatorFromXml(android.content.res.Resources r21, android.content.res.Resources.Theme r22, org.xmlpull.v1.XmlPullParser r23, android.util.AttributeSet r24, android.animation.AnimatorSet r25, int r26, float r27) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r12 = 0;
        r14 = 0;
        r15 = r23.getDepth();
    L_0x0006:
        r20 = r23.next();
        r4 = 3;
        r0 = r20;
        if (r0 != r4) goto L_0x0015;
    L_0x000f:
        r4 = r23.getDepth();
        if (r4 <= r15) goto L_0x00c6;
    L_0x0015:
        r4 = 1;
        r0 = r20;
        if (r0 == r4) goto L_0x00c6;
    L_0x001a:
        r4 = 2;
        r0 = r20;
        if (r0 != r4) goto L_0x0006;
    L_0x001f:
        r19 = r23.getName();
        r4 = "objectAnimator";
        r0 = r19;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0047;
    L_0x002e:
        r0 = r21;
        r1 = r22;
        r2 = r24;
        r3 = r27;
        r12 = loadObjectAnimator(r0, r1, r2, r3);
    L_0x003a:
        if (r25 == 0) goto L_0x0006;
    L_0x003c:
        if (r14 != 0) goto L_0x0043;
    L_0x003e:
        r14 = new java.util.ArrayList;
        r14.<init>();
    L_0x0043:
        r14.add(r12);
        goto L_0x0006;
    L_0x0047:
        r4 = "animator";
        r0 = r19;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x005f;
    L_0x0051:
        r4 = 0;
        r0 = r21;
        r1 = r22;
        r2 = r24;
        r3 = r27;
        r12 = loadAnimator(r0, r1, r2, r4, r3);
        goto L_0x003a;
    L_0x005f:
        r4 = "set";
        r0 = r19;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x00a9;
    L_0x006a:
        r12 = new android.animation.AnimatorSet;
        r12.<init>();
        if (r22 == 0) goto L_0x009e;
    L_0x0071:
        r4 = com.android.internal.R.styleable.AnimatorSet;
        r5 = 0;
        r6 = 0;
        r0 = r22;
        r1 = r24;
        r11 = r0.obtainStyledAttributes(r1, r4, r5, r6);
    L_0x007d:
        r4 = r11.getChangingConfigurations();
        r12.appendChangingConfigurations(r4);
        r4 = 0;
        r5 = 0;
        r9 = r11.getInt(r4, r5);
        r8 = r12;
        r8 = (android.animation.AnimatorSet) r8;
        r4 = r21;
        r5 = r22;
        r6 = r23;
        r7 = r24;
        r10 = r27;
        createAnimatorFromXml(r4, r5, r6, r7, r8, r9, r10);
        r11.recycle();
        goto L_0x003a;
    L_0x009e:
        r4 = com.android.internal.R.styleable.AnimatorSet;
        r0 = r21;
        r1 = r24;
        r11 = r0.obtainAttributes(r1, r4);
        goto L_0x007d;
    L_0x00a9:
        r4 = new java.lang.RuntimeException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unknown animator name: ";
        r5 = r5.append(r6);
        r6 = r23.getName();
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x00c6:
        if (r25 == 0) goto L_0x00f0;
    L_0x00c8:
        if (r14 == 0) goto L_0x00f0;
    L_0x00ca:
        r4 = r14.size();
        r13 = new android.animation.Animator[r4];
        r17 = 0;
        r16 = r14.iterator();
    L_0x00d6:
        r4 = r16.hasNext();
        if (r4 == 0) goto L_0x00e9;
    L_0x00dc:
        r11 = r16.next();
        r11 = (android.animation.Animator) r11;
        r18 = r17 + 1;
        r13[r17] = r11;
        r17 = r18;
        goto L_0x00d6;
    L_0x00e9:
        if (r26 != 0) goto L_0x00f1;
    L_0x00eb:
        r0 = r25;
        r0.playTogether(r13);
    L_0x00f0:
        return r12;
    L_0x00f1:
        r0 = r25;
        r0.playSequentially(r13);
        goto L_0x00f0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorInflater.createAnimatorFromXml(android.content.res.Resources, android.content.res.Resources$Theme, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.animation.AnimatorSet, int, float):android.animation.Animator");
    }

    private static ObjectAnimator loadObjectAnimator(Resources res, Theme theme, AttributeSet attrs, float pathErrorScale) throws NotFoundException {
        ObjectAnimator anim = new ObjectAnimator();
        loadAnimator(res, theme, attrs, anim, pathErrorScale);
        return anim;
    }

    private static ValueAnimator loadAnimator(Resources res, Theme theme, AttributeSet attrs, ValueAnimator anim, float pathErrorScale) throws NotFoundException {
        TypedArray arrayAnimator;
        TypedArray arrayObjectAnimator = null;
        if (theme != null) {
            arrayAnimator = theme.obtainStyledAttributes(attrs, com.android.internal.R.styleable.Animator, VALUE_TYPE_FLOAT, VALUE_TYPE_FLOAT);
        } else {
            arrayAnimator = res.obtainAttributes(attrs, com.android.internal.R.styleable.Animator);
        }
        if (anim != null) {
            if (theme != null) {
                arrayObjectAnimator = theme.obtainStyledAttributes(attrs, com.android.internal.R.styleable.PropertyAnimator, VALUE_TYPE_FLOAT, VALUE_TYPE_FLOAT);
            } else {
                arrayObjectAnimator = res.obtainAttributes(attrs, com.android.internal.R.styleable.PropertyAnimator);
            }
            anim.appendChangingConfigurations(arrayObjectAnimator.getChangingConfigurations());
        }
        if (anim == null) {
            anim = new ValueAnimator();
        }
        anim.appendChangingConfigurations(arrayAnimator.getChangingConfigurations());
        parseAnimatorFromTypeArray(anim, arrayAnimator, arrayObjectAnimator, pathErrorScale);
        int resID = arrayAnimator.getResourceId(VALUE_TYPE_FLOAT, VALUE_TYPE_FLOAT);
        if (resID > 0) {
            Interpolator interpolator = AnimationUtils.loadInterpolator(res, theme, resID);
            if (interpolator instanceof BaseInterpolator) {
                anim.appendChangingConfigurations(((BaseInterpolator) interpolator).getChangingConfiguration());
            }
            anim.setInterpolator(interpolator);
        }
        arrayAnimator.recycle();
        if (arrayObjectAnimator != null) {
            arrayObjectAnimator.recycle();
        }
        return anim;
    }

    private static int getChangingConfigs(Resources resources, int id) {
        int i;
        synchronized (sTmpTypedValue) {
            resources.getValue(id, sTmpTypedValue, true);
            i = sTmpTypedValue.changingConfigurations;
        }
        return i;
    }
}
