package android.view.animation;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.XmlResourceParser;
import android.os.SystemClock;
import android.util.Xml;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimationUtils {
    private static final int SEQUENTIALLY = 1;
    private static final int TOGETHER = 0;

    public static long currentAnimationTimeMillis() {
        return SystemClock.uptimeMillis();
    }

    public static Animation loadAnimation(Context context, int id) throws NotFoundException {
        NotFoundException rnf;
        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            Animation createAnimationFromXml = createAnimationFromXml(context, parser);
            if (parser != null) {
                parser.close();
            }
            return createAnimationFromXml;
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

    private static Animation createAnimationFromXml(Context c, XmlPullParser parser) throws XmlPullParserException, IOException {
        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.view.animation.Animation createAnimationFromXml(android.content.Context r7, org.xmlpull.v1.XmlPullParser r8, android.view.animation.AnimationSet r9, android.util.AttributeSet r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r0 = 0;
        r1 = r8.getDepth();
    L_0x0005:
        r3 = r8.next();
        r4 = 3;
        if (r3 != r4) goto L_0x0012;
    L_0x000c:
        r4 = r8.getDepth();
        if (r4 <= r1) goto L_0x008e;
    L_0x0012:
        r4 = 1;
        if (r3 == r4) goto L_0x008e;
    L_0x0015:
        r4 = 2;
        if (r3 != r4) goto L_0x0005;
    L_0x0018:
        r2 = r8.getName();
        r4 = "set";
        r4 = r2.equals(r4);
        if (r4 == 0) goto L_0x0036;
    L_0x0025:
        r0 = new android.view.animation.AnimationSet;
        r0.<init>(r7, r10);
        r4 = r0;
        r4 = (android.view.animation.AnimationSet) r4;
        createAnimationFromXml(r7, r8, r4, r10);
    L_0x0030:
        if (r9 == 0) goto L_0x0005;
    L_0x0032:
        r9.addAnimation(r0);
        goto L_0x0005;
    L_0x0036:
        r4 = "alpha";
        r4 = r2.equals(r4);
        if (r4 == 0) goto L_0x0044;
    L_0x003e:
        r0 = new android.view.animation.AlphaAnimation;
        r0.<init>(r7, r10);
        goto L_0x0030;
    L_0x0044:
        r4 = "scale";
        r4 = r2.equals(r4);
        if (r4 == 0) goto L_0x0053;
    L_0x004d:
        r0 = new android.view.animation.ScaleAnimation;
        r0.<init>(r7, r10);
        goto L_0x0030;
    L_0x0053:
        r4 = "rotate";
        r4 = r2.equals(r4);
        if (r4 == 0) goto L_0x0062;
    L_0x005c:
        r0 = new android.view.animation.RotateAnimation;
        r0.<init>(r7, r10);
        goto L_0x0030;
    L_0x0062:
        r4 = "translate";
        r4 = r2.equals(r4);
        if (r4 == 0) goto L_0x0071;
    L_0x006b:
        r0 = new android.view.animation.TranslateAnimation;
        r0.<init>(r7, r10);
        goto L_0x0030;
    L_0x0071:
        r4 = new java.lang.RuntimeException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unknown animation name: ";
        r5 = r5.append(r6);
        r6 = r8.getName();
        r5 = r5.append(r6);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x008e:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.animation.AnimationUtils.createAnimationFromXml(android.content.Context, org.xmlpull.v1.XmlPullParser, android.view.animation.AnimationSet, android.util.AttributeSet):android.view.animation.Animation");
    }

    public static LayoutAnimationController loadLayoutAnimation(Context context, int id) throws NotFoundException {
        NotFoundException rnf;
        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            LayoutAnimationController createLayoutAnimationFromXml = createLayoutAnimationFromXml(context, parser);
            if (parser != null) {
                parser.close();
            }
            return createLayoutAnimationFromXml;
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

    private static LayoutAnimationController createLayoutAnimationFromXml(Context c, XmlPullParser parser) throws XmlPullParserException, IOException {
        return createLayoutAnimationFromXml(c, parser, Xml.asAttributeSet(parser));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.view.animation.LayoutAnimationController createLayoutAnimationFromXml(android.content.Context r7, org.xmlpull.v1.XmlPullParser r8, android.util.AttributeSet r9) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r0 = 0;
        r1 = r8.getDepth();
    L_0x0005:
        r3 = r8.next();
        r4 = 3;
        if (r3 != r4) goto L_0x0012;
    L_0x000c:
        r4 = r8.getDepth();
        if (r4 <= r1) goto L_0x0051;
    L_0x0012:
        r4 = 1;
        if (r3 == r4) goto L_0x0051;
    L_0x0015:
        r4 = 2;
        if (r3 != r4) goto L_0x0005;
    L_0x0018:
        r2 = r8.getName();
        r4 = "layoutAnimation";
        r4 = r4.equals(r2);
        if (r4 == 0) goto L_0x002a;
    L_0x0024:
        r0 = new android.view.animation.LayoutAnimationController;
        r0.<init>(r7, r9);
        goto L_0x0005;
    L_0x002a:
        r4 = "gridLayoutAnimation";
        r4 = r4.equals(r2);
        if (r4 == 0) goto L_0x0038;
    L_0x0032:
        r0 = new android.view.animation.GridLayoutAnimationController;
        r0.<init>(r7, r9);
        goto L_0x0005;
    L_0x0038:
        r4 = new java.lang.RuntimeException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unknown layout animation name: ";
        r5 = r5.append(r6);
        r5 = r5.append(r2);
        r5 = r5.toString();
        r4.<init>(r5);
        throw r4;
    L_0x0051:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.animation.AnimationUtils.createLayoutAnimationFromXml(android.content.Context, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet):android.view.animation.LayoutAnimationController");
    }

    public static Animation makeInAnimation(Context c, boolean fromLeft) {
        Animation a;
        if (fromLeft) {
            a = loadAnimation(c, R.anim.slide_in_left);
        } else {
            a = loadAnimation(c, 17432673);
        }
        a.setInterpolator(new DecelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        return a;
    }

    public static Animation makeOutAnimation(Context c, boolean toRight) {
        Animation a;
        if (toRight) {
            a = loadAnimation(c, R.anim.slide_out_right);
        } else {
            a = loadAnimation(c, 17432676);
        }
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        return a;
    }

    public static Animation makeInChildBottomAnimation(Context c) {
        Animation a = loadAnimation(c, 17432671);
        a.setInterpolator(new AccelerateInterpolator());
        a.setStartTime(currentAnimationTimeMillis());
        return a;
    }

    public static Interpolator loadInterpolator(Context context, int id) throws NotFoundException {
        NotFoundException rnf;
        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            Interpolator createInterpolatorFromXml = createInterpolatorFromXml(context.getResources(), context.getTheme(), parser);
            if (parser != null) {
                parser.close();
            }
            return createInterpolatorFromXml;
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

    public static Interpolator loadInterpolator(Resources res, Theme theme, int id) throws NotFoundException {
        NotFoundException rnf;
        XmlResourceParser xmlResourceParser = null;
        try {
            xmlResourceParser = res.getAnimation(id);
            Interpolator createInterpolatorFromXml = createInterpolatorFromXml(res, theme, xmlResourceParser);
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            return createInterpolatorFromXml;
        } catch (XmlPullParserException ex) {
            rnf = new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex2) {
            rnf = new NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(ex2);
            throw rnf;
        } catch (Throwable th) {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.view.animation.Interpolator createInterpolatorFromXml(android.content.res.Resources r8, android.content.res.Resources.Theme r9, org.xmlpull.v1.XmlPullParser r10) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r2 = 0;
        r1 = r10.getDepth();
    L_0x0005:
        r4 = r10.next();
        r5 = 3;
        if (r4 != r5) goto L_0x0012;
    L_0x000c:
        r5 = r10.getDepth();
        if (r5 <= r1) goto L_0x00ce;
    L_0x0012:
        r5 = 1;
        if (r4 == r5) goto L_0x00ce;
    L_0x0015:
        r5 = 2;
        if (r4 != r5) goto L_0x0005;
    L_0x0018:
        r0 = android.util.Xml.asAttributeSet(r10);
        r3 = r10.getName();
        r5 = "linearInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x002e;
    L_0x0028:
        r2 = new android.view.animation.LinearInterpolator;
        r2.<init>();
        goto L_0x0005;
    L_0x002e:
        r5 = "accelerateInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x003c;
    L_0x0036:
        r2 = new android.view.animation.AccelerateInterpolator;
        r2.<init>(r8, r9, r0);
        goto L_0x0005;
    L_0x003c:
        r5 = "decelerateInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x004a;
    L_0x0044:
        r2 = new android.view.animation.DecelerateInterpolator;
        r2.<init>(r8, r9, r0);
        goto L_0x0005;
    L_0x004a:
        r5 = "accelerateDecelerateInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0058;
    L_0x0052:
        r2 = new android.view.animation.AccelerateDecelerateInterpolator;
        r2.<init>();
        goto L_0x0005;
    L_0x0058:
        r5 = "cycleInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0066;
    L_0x0060:
        r2 = new android.view.animation.CycleInterpolator;
        r2.<init>(r8, r9, r0);
        goto L_0x0005;
    L_0x0066:
        r5 = "anticipateInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0074;
    L_0x006e:
        r2 = new android.view.animation.AnticipateInterpolator;
        r2.<init>(r8, r9, r0);
        goto L_0x0005;
    L_0x0074:
        r5 = "overshootInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0083;
    L_0x007d:
        r2 = new android.view.animation.OvershootInterpolator;
        r2.<init>(r8, r9, r0);
        goto L_0x0005;
    L_0x0083:
        r5 = "anticipateOvershootInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x0092;
    L_0x008b:
        r2 = new android.view.animation.AnticipateOvershootInterpolator;
        r2.<init>(r8, r9, r0);
        goto L_0x0005;
    L_0x0092:
        r5 = "bounceInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x00a1;
    L_0x009a:
        r2 = new android.view.animation.BounceInterpolator;
        r2.<init>();
        goto L_0x0005;
    L_0x00a1:
        r5 = "pathInterpolator";
        r5 = r3.equals(r5);
        if (r5 == 0) goto L_0x00b1;
    L_0x00aa:
        r2 = new android.view.animation.PathInterpolator;
        r2.<init>(r8, r9, r0);
        goto L_0x0005;
    L_0x00b1:
        r5 = new java.lang.RuntimeException;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Unknown interpolator name: ";
        r6 = r6.append(r7);
        r7 = r10.getName();
        r6 = r6.append(r7);
        r6 = r6.toString();
        r5.<init>(r6);
        throw r5;
    L_0x00ce:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.animation.AnimationUtils.createInterpolatorFromXml(android.content.res.Resources, android.content.res.Resources$Theme, org.xmlpull.v1.XmlPullParser):android.view.animation.Interpolator");
    }
}
