package android.transition;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.transition.Transition.EpicenterCallback;
import android.transition.Transition.TransitionListener;
import android.transition.Transition.TransitionListenerAdapter;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.Iterator;

public class TransitionSet extends Transition {
    public static final int ORDERING_SEQUENTIAL = 1;
    public static final int ORDERING_TOGETHER = 0;
    int mCurrentListeners;
    private boolean mPlayTogether;
    boolean mStarted;
    ArrayList<Transition> mTransitions;

    /* renamed from: android.transition.TransitionSet.1 */
    class AnonymousClass1 extends TransitionListenerAdapter {
        final /* synthetic */ TransitionSet this$0;
        final /* synthetic */ Transition val$nextTransition;

        AnonymousClass1(android.transition.TransitionSet r1, android.transition.Transition r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.transition.TransitionSet.1.<init>(android.transition.TransitionSet, android.transition.Transition):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.transition.TransitionSet.1.<init>(android.transition.TransitionSet, android.transition.Transition):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionSet.1.<init>(android.transition.TransitionSet, android.transition.Transition):void");
        }

        public void onTransitionEnd(android.transition.Transition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.transition.TransitionSet.1.onTransitionEnd(android.transition.Transition):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.transition.TransitionSet.1.onTransitionEnd(android.transition.Transition):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionSet.1.onTransitionEnd(android.transition.Transition):void");
        }
    }

    static class TransitionSetListener extends TransitionListenerAdapter {
        TransitionSet mTransitionSet;

        TransitionSetListener(android.transition.TransitionSet r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.transition.TransitionSet.TransitionSetListener.<init>(android.transition.TransitionSet):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.transition.TransitionSet.TransitionSetListener.<init>(android.transition.TransitionSet):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionSet.TransitionSetListener.<init>(android.transition.TransitionSet):void");
        }

        public void onTransitionEnd(android.transition.Transition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.transition.TransitionSet.TransitionSetListener.onTransitionEnd(android.transition.Transition):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.transition.TransitionSet.TransitionSetListener.onTransitionEnd(android.transition.Transition):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionSet.TransitionSetListener.onTransitionEnd(android.transition.Transition):void");
        }

        public void onTransitionStart(android.transition.Transition r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.transition.TransitionSet.TransitionSetListener.onTransitionStart(android.transition.Transition):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.transition.TransitionSet.TransitionSetListener.onTransitionStart(android.transition.Transition):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.transition.TransitionSet.TransitionSetListener.onTransitionStart(android.transition.Transition):void");
        }
    }

    public /* bridge */ /* synthetic */ Transition addListener(TransitionListener x0) {
        return addListener(x0);
    }

    public /* bridge */ /* synthetic */ Transition addTarget(int x0) {
        return addTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition addTarget(View x0) {
        return addTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition addTarget(Class x0) {
        return addTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition addTarget(String x0) {
        return addTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition clone() {
        return clone();
    }

    public /* bridge */ /* synthetic */ Object m35clone() throws CloneNotSupportedException {
        return clone();
    }

    public /* bridge */ /* synthetic */ Transition removeListener(TransitionListener x0) {
        return removeListener(x0);
    }

    public /* bridge */ /* synthetic */ Transition removeTarget(int x0) {
        return removeTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition removeTarget(View x0) {
        return removeTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition removeTarget(Class x0) {
        return removeTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition removeTarget(String x0) {
        return removeTarget(x0);
    }

    public /* bridge */ /* synthetic */ Transition setDuration(long x0) {
        return setDuration(x0);
    }

    public /* bridge */ /* synthetic */ Transition setInterpolator(TimeInterpolator x0) {
        return setInterpolator(x0);
    }

    /* bridge */ /* synthetic */ Transition setSceneRoot(ViewGroup x0) {
        return setSceneRoot(x0);
    }

    public /* bridge */ /* synthetic */ Transition setStartDelay(long x0) {
        return setStartDelay(x0);
    }

    public TransitionSet() {
        this.mTransitions = new ArrayList();
        this.mPlayTogether = true;
        this.mStarted = false;
    }

    public TransitionSet(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTransitions = new ArrayList();
        this.mPlayTogether = true;
        this.mStarted = false;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TransitionSet);
        setOrdering(a.getInt(0, 0));
        a.recycle();
    }

    public TransitionSet setOrdering(int ordering) {
        switch (ordering) {
            case Toast.LENGTH_SHORT /*0*/:
                this.mPlayTogether = true;
                break;
            case ORDERING_SEQUENTIAL /*1*/:
                this.mPlayTogether = false;
                break;
            default:
                throw new AndroidRuntimeException("Invalid parameter for TransitionSet ordering: " + ordering);
        }
        return this;
    }

    public int getOrdering() {
        return this.mPlayTogether ? 0 : ORDERING_SEQUENTIAL;
    }

    public TransitionSet addTransition(Transition transition) {
        if (transition != null) {
            this.mTransitions.add(transition);
            transition.mParent = this;
            if (this.mDuration >= 0) {
                transition.setDuration(this.mDuration);
            }
        }
        return this;
    }

    public int getTransitionCount() {
        return this.mTransitions.size();
    }

    public Transition getTransitionAt(int index) {
        if (index < 0 || index >= this.mTransitions.size()) {
            return null;
        }
        return (Transition) this.mTransitions.get(index);
    }

    public TransitionSet m41setDuration(long duration) {
        super.setDuration(duration);
        if (this.mDuration >= 0 && this.mTransitions != null) {
            int numTransitions = this.mTransitions.size();
            for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
                ((Transition) this.mTransitions.get(i)).setDuration(duration);
            }
        }
        return this;
    }

    public TransitionSet m44setStartDelay(long startDelay) {
        return (TransitionSet) super.setStartDelay(startDelay);
    }

    public TransitionSet m42setInterpolator(TimeInterpolator interpolator) {
        return (TransitionSet) super.setInterpolator(interpolator);
    }

    public TransitionSet m31addTarget(View target) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).addTarget(target);
        }
        return (TransitionSet) super.addTarget(target);
    }

    public TransitionSet m30addTarget(int targetId) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).addTarget(targetId);
        }
        return (TransitionSet) super.addTarget(targetId);
    }

    public TransitionSet m33addTarget(String targetName) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).addTarget(targetName);
        }
        return (TransitionSet) super.addTarget(targetName);
    }

    public TransitionSet m32addTarget(Class targetType) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).addTarget(targetType);
        }
        return (TransitionSet) super.addTarget(targetType);
    }

    public TransitionSet m29addListener(TransitionListener listener) {
        return (TransitionSet) super.addListener(listener);
    }

    public TransitionSet m37removeTarget(int targetId) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).removeTarget(targetId);
        }
        return (TransitionSet) super.removeTarget(targetId);
    }

    public TransitionSet m38removeTarget(View target) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).removeTarget(target);
        }
        return (TransitionSet) super.removeTarget(target);
    }

    public TransitionSet m39removeTarget(Class target) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).removeTarget(target);
        }
        return (TransitionSet) super.removeTarget(target);
    }

    public TransitionSet m40removeTarget(String target) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).removeTarget(target);
        }
        return (TransitionSet) super.removeTarget(target);
    }

    public Transition excludeTarget(View target, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).excludeTarget(target, exclude);
        }
        return super.excludeTarget(target, exclude);
    }

    public Transition excludeTarget(String targetName, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).excludeTarget(targetName, exclude);
        }
        return super.excludeTarget(targetName, exclude);
    }

    public Transition excludeTarget(int targetId, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).excludeTarget(targetId, exclude);
        }
        return super.excludeTarget(targetId, exclude);
    }

    public Transition excludeTarget(Class type, boolean exclude) {
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).excludeTarget(type, exclude);
        }
        return super.excludeTarget(type, exclude);
    }

    public TransitionSet m36removeListener(TransitionListener listener) {
        return (TransitionSet) super.removeListener(listener);
    }

    public void setPathMotion(PathMotion pathMotion) {
        super.setPathMotion(pathMotion);
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).setPathMotion(pathMotion);
        }
    }

    public void forceVisibility(int visibility, boolean isStartValue) {
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).forceVisibility(visibility, isStartValue);
        }
    }

    public TransitionSet removeTransition(Transition transition) {
        this.mTransitions.remove(transition);
        transition.mParent = null;
        return this;
    }

    private void setupStartEndListeners() {
        TransitionSetListener listener = new TransitionSetListener(this);
        Iterator i$ = this.mTransitions.iterator();
        while (i$.hasNext()) {
            ((Transition) i$.next()).addListener(listener);
        }
        this.mCurrentListeners = this.mTransitions.size();
    }

    protected void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues, ArrayList<TransitionValues> startValuesList, ArrayList<TransitionValues> endValuesList) {
        long startDelay = getStartDelay();
        int numTransitions = this.mTransitions.size();
        int i = 0;
        while (i < numTransitions) {
            Transition childTransition = (Transition) this.mTransitions.get(i);
            if (startDelay > 0 && (this.mPlayTogether || i == 0)) {
                long childStartDelay = childTransition.getStartDelay();
                if (childStartDelay > 0) {
                    childTransition.setStartDelay(startDelay + childStartDelay);
                } else {
                    childTransition.setStartDelay(startDelay);
                }
            }
            childTransition.createAnimators(sceneRoot, startValues, endValues, startValuesList, endValuesList);
            i += ORDERING_SEQUENTIAL;
        }
    }

    protected void runAnimators() {
        if (this.mTransitions.isEmpty()) {
            start();
            end();
            return;
        }
        setupStartEndListeners();
        int numTransitions = this.mTransitions.size();
        int i;
        if (this.mPlayTogether) {
            for (i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
                ((Transition) this.mTransitions.get(i)).runAnimators();
            }
            return;
        }
        for (i = ORDERING_SEQUENTIAL; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i - 1)).addListener(new AnonymousClass1(this, (Transition) this.mTransitions.get(i)));
        }
        Transition firstTransition = (Transition) this.mTransitions.get(0);
        if (firstTransition != null) {
            firstTransition.runAnimators();
        }
    }

    public void captureStartValues(TransitionValues transitionValues) {
        if (isValidTarget(transitionValues.view)) {
            Iterator i$ = this.mTransitions.iterator();
            while (i$.hasNext()) {
                Transition childTransition = (Transition) i$.next();
                if (childTransition.isValidTarget(transitionValues.view)) {
                    childTransition.captureStartValues(transitionValues);
                    transitionValues.targetedTransitions.add(childTransition);
                }
            }
        }
    }

    public void captureEndValues(TransitionValues transitionValues) {
        if (isValidTarget(transitionValues.view)) {
            Iterator i$ = this.mTransitions.iterator();
            while (i$.hasNext()) {
                Transition childTransition = (Transition) i$.next();
                if (childTransition.isValidTarget(transitionValues.view)) {
                    childTransition.captureEndValues(transitionValues);
                    transitionValues.targetedTransitions.add(childTransition);
                }
            }
        }
    }

    void capturePropagationValues(TransitionValues transitionValues) {
        super.capturePropagationValues(transitionValues);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).capturePropagationValues(transitionValues);
        }
    }

    public void pause(View sceneRoot) {
        super.pause(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).pause(sceneRoot);
        }
    }

    public void resume(View sceneRoot) {
        super.resume(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).resume(sceneRoot);
        }
    }

    protected void cancel() {
        super.cancel();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).cancel();
        }
    }

    TransitionSet m43setSceneRoot(ViewGroup sceneRoot) {
        super.setSceneRoot(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).setSceneRoot(sceneRoot);
        }
        return this;
    }

    void setCanRemoveViews(boolean canRemoveViews) {
        super.setCanRemoveViews(canRemoveViews);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).setCanRemoveViews(canRemoveViews);
        }
    }

    public void setPropagation(TransitionPropagation propagation) {
        super.setPropagation(propagation);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).setPropagation(propagation);
        }
    }

    public void setEpicenterCallback(EpicenterCallback epicenterCallback) {
        super.setEpicenterCallback(epicenterCallback);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            ((Transition) this.mTransitions.get(i)).setEpicenterCallback(epicenterCallback);
        }
    }

    String toString(String indent) {
        String result = super.toString(indent);
        for (int i = 0; i < this.mTransitions.size(); i += ORDERING_SEQUENTIAL) {
            result = result + "\n" + ((Transition) this.mTransitions.get(i)).toString(indent + "  ");
        }
        return result;
    }

    public TransitionSet m34clone() {
        TransitionSet clone = (TransitionSet) super.clone();
        clone.mTransitions = new ArrayList();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i += ORDERING_SEQUENTIAL) {
            clone.addTransition(((Transition) this.mTransitions.get(i)).clone());
        }
        return clone;
    }
}
