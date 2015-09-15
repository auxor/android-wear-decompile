package android.animation;

import android.animation.Animator.AnimatorListener;
import android.view.WindowManager.LayoutParams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class AnimatorSet extends Animator {
    private ValueAnimator mDelayAnim;
    private long mDuration;
    private TimeInterpolator mInterpolator;
    private boolean mNeedsSort;
    private HashMap<Animator, Node> mNodeMap;
    private ArrayList<Node> mNodes;
    private ArrayList<Animator> mPlayingSet;
    private boolean mReversible;
    private AnimatorSetListener mSetListener;
    private ArrayList<Node> mSortedNodes;
    private long mStartDelay;
    private boolean mStarted;
    boolean mTerminated;

    /* renamed from: android.animation.AnimatorSet.1 */
    class AnonymousClass1 extends AnimatorListenerAdapter {
        boolean canceled;
        final /* synthetic */ AnimatorSet this$0;
        final /* synthetic */ ArrayList val$nodesToStart;

        AnonymousClass1(android.animation.AnimatorSet r1, java.util.ArrayList r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.animation.AnimatorSet.1.<init>(android.animation.AnimatorSet, java.util.ArrayList):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.animation.AnimatorSet.1.<init>(android.animation.AnimatorSet, java.util.ArrayList):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorSet.1.<init>(android.animation.AnimatorSet, java.util.ArrayList):void");
        }

        public void onAnimationCancel(android.animation.Animator r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.animation.AnimatorSet.1.onAnimationCancel(android.animation.Animator):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.animation.AnimatorSet.1.onAnimationCancel(android.animation.Animator):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorSet.1.onAnimationCancel(android.animation.Animator):void");
        }

        public void onAnimationEnd(android.animation.Animator r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.animation.AnimatorSet.1.onAnimationEnd(android.animation.Animator):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.animation.AnimatorSet.1.onAnimationEnd(android.animation.Animator):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorSet.1.onAnimationEnd(android.animation.Animator):void");
        }
    }

    private class AnimatorSetListener implements AnimatorListener {
        private AnimatorSet mAnimatorSet;
        final /* synthetic */ AnimatorSet this$0;

        AnimatorSetListener(AnimatorSet animatorSet, AnimatorSet animatorSet2) {
            this.this$0 = animatorSet;
            this.mAnimatorSet = animatorSet2;
        }

        public void onAnimationCancel(Animator animation) {
            if (!this.this$0.mTerminated && this.this$0.mPlayingSet.size() == 0 && this.this$0.mListeners != null) {
                int numListeners = this.this$0.mListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    ((AnimatorListener) this.this$0.mListeners.get(i)).onAnimationCancel(this.mAnimatorSet);
                }
            }
        }

        public void onAnimationEnd(Animator animation) {
            animation.removeListener(this);
            this.this$0.mPlayingSet.remove(animation);
            ((Node) this.mAnimatorSet.mNodeMap.get(animation)).done = true;
            if (!this.this$0.mTerminated) {
                int i;
                ArrayList<Node> sortedNodes = this.mAnimatorSet.mSortedNodes;
                boolean allDone = true;
                int numSortedNodes = sortedNodes.size();
                for (i = 0; i < numSortedNodes; i++) {
                    if (!((Node) sortedNodes.get(i)).done) {
                        allDone = false;
                        break;
                    }
                }
                if (allDone) {
                    if (this.this$0.mListeners != null) {
                        ArrayList<AnimatorListener> tmpListeners = (ArrayList) this.this$0.mListeners.clone();
                        int numListeners = tmpListeners.size();
                        for (i = 0; i < numListeners; i++) {
                            ((AnimatorListener) tmpListeners.get(i)).onAnimationEnd(this.mAnimatorSet);
                        }
                    }
                    this.mAnimatorSet.mStarted = false;
                    this.mAnimatorSet.mPaused = false;
                }
            }
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationStart(Animator animation) {
        }
    }

    public class Builder {
        private Node mCurrentNode;
        final /* synthetic */ AnimatorSet this$0;

        Builder(AnimatorSet animatorSet, Animator anim) {
            this.this$0 = animatorSet;
            this.mCurrentNode = (Node) animatorSet.mNodeMap.get(anim);
            if (this.mCurrentNode == null) {
                this.mCurrentNode = new Node(anim);
                animatorSet.mNodeMap.put(anim, this.mCurrentNode);
                animatorSet.mNodes.add(this.mCurrentNode);
            }
        }

        public Builder with(Animator anim) {
            Node node = (Node) this.this$0.mNodeMap.get(anim);
            if (node == null) {
                node = new Node(anim);
                this.this$0.mNodeMap.put(anim, node);
                this.this$0.mNodes.add(node);
            }
            node.addDependency(new Dependency(this.mCurrentNode, 0));
            return this;
        }

        public Builder before(Animator anim) {
            this.this$0.mReversible = false;
            Node node = (Node) this.this$0.mNodeMap.get(anim);
            if (node == null) {
                node = new Node(anim);
                this.this$0.mNodeMap.put(anim, node);
                this.this$0.mNodes.add(node);
            }
            node.addDependency(new Dependency(this.mCurrentNode, 1));
            return this;
        }

        public Builder after(Animator anim) {
            this.this$0.mReversible = false;
            Node node = (Node) this.this$0.mNodeMap.get(anim);
            if (node == null) {
                node = new Node(anim);
                this.this$0.mNodeMap.put(anim, node);
                this.this$0.mNodes.add(node);
            }
            this.mCurrentNode.addDependency(new Dependency(node, 1));
            return this;
        }

        public Builder after(long delay) {
            Animator anim = ValueAnimator.ofFloat(0.0f, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
            anim.setDuration(delay);
            after(anim);
            return this;
        }
    }

    private static class Dependency {
        static final int AFTER = 1;
        static final int WITH = 0;
        public Node node;
        public int rule;

        public Dependency(Node node, int rule) {
            this.node = node;
            this.rule = rule;
        }
    }

    private static class DependencyListener implements AnimatorListener {
        private AnimatorSet mAnimatorSet;
        private Node mNode;
        private int mRule;

        public DependencyListener(AnimatorSet animatorSet, Node node, int rule) {
            this.mAnimatorSet = animatorSet;
            this.mNode = node;
            this.mRule = rule;
        }

        public void onAnimationCancel(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            if (this.mRule == 1) {
                startIfReady(animation);
            }
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationStart(Animator animation) {
            if (this.mRule == 0) {
                startIfReady(animation);
            }
        }

        private void startIfReady(Animator dependencyAnimation) {
            if (!this.mAnimatorSet.mTerminated) {
                Dependency dependencyToRemove = null;
                int numDependencies = this.mNode.tmpDependencies.size();
                for (int i = 0; i < numDependencies; i++) {
                    Dependency dependency = (Dependency) this.mNode.tmpDependencies.get(i);
                    if (dependency.rule == this.mRule && dependency.node.animation == dependencyAnimation) {
                        dependencyToRemove = dependency;
                        dependencyAnimation.removeListener(this);
                        break;
                    }
                }
                this.mNode.tmpDependencies.remove(dependencyToRemove);
                if (this.mNode.tmpDependencies.size() == 0) {
                    this.mNode.animation.start();
                    this.mAnimatorSet.mPlayingSet.add(this.mNode.animation);
                }
            }
        }
    }

    private static class Node implements Cloneable {
        public Animator animation;
        public ArrayList<Dependency> dependencies;
        public boolean done;
        private Node mTmpClone;
        public ArrayList<Node> nodeDependencies;
        public ArrayList<Node> nodeDependents;
        public ArrayList<Dependency> tmpDependencies;

        public /* bridge */ /* synthetic */ Object m22clone() throws CloneNotSupportedException {
            return clone();
        }

        public Node(Animator animation) {
            this.dependencies = null;
            this.tmpDependencies = null;
            this.nodeDependencies = null;
            this.nodeDependents = null;
            this.done = false;
            this.mTmpClone = null;
            this.animation = animation;
        }

        public void addDependency(Dependency dependency) {
            if (this.dependencies == null) {
                this.dependencies = new ArrayList();
                this.nodeDependencies = new ArrayList();
            }
            this.dependencies.add(dependency);
            if (!this.nodeDependencies.contains(dependency.node)) {
                this.nodeDependencies.add(dependency.node);
            }
            Node dependencyNode = dependency.node;
            if (dependencyNode.nodeDependents == null) {
                dependencyNode.nodeDependents = new ArrayList();
            }
            dependencyNode.nodeDependents.add(this);
        }

        public Node clone() {
            try {
                Node node = (Node) super.clone();
                node.animation = this.animation.clone();
                node.done = false;
                return node;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public AnimatorSet() {
        this.mPlayingSet = new ArrayList();
        this.mNodeMap = new HashMap();
        this.mNodes = new ArrayList();
        this.mSortedNodes = new ArrayList();
        this.mNeedsSort = true;
        this.mSetListener = null;
        this.mTerminated = false;
        this.mStarted = false;
        this.mStartDelay = 0;
        this.mDelayAnim = null;
        this.mDuration = -1;
        this.mInterpolator = null;
        this.mReversible = true;
    }

    public /* bridge */ /* synthetic */ Animator clone() {
        return clone();
    }

    public /* bridge */ /* synthetic */ Object m20clone() throws CloneNotSupportedException {
        return clone();
    }

    public /* bridge */ /* synthetic */ Animator setDuration(long x0) {
        return setDuration(x0);
    }

    public void playTogether(Animator... items) {
        if (items != null) {
            this.mNeedsSort = true;
            Builder builder = play(items[0]);
            for (int i = 1; i < items.length; i++) {
                builder.with(items[i]);
            }
        }
    }

    public void playTogether(Collection<Animator> items) {
        if (items != null && items.size() > 0) {
            this.mNeedsSort = true;
            Builder builder = null;
            for (Animator anim : items) {
                if (builder == null) {
                    builder = play(anim);
                } else {
                    builder.with(anim);
                }
            }
        }
    }

    public void playSequentially(Animator... items) {
        if (items != null) {
            this.mNeedsSort = true;
            if (items.length == 1) {
                play(items[0]);
                return;
            }
            this.mReversible = false;
            for (int i = 0; i < items.length - 1; i++) {
                play(items[i]).before(items[i + 1]);
            }
        }
    }

    public void playSequentially(List<Animator> items) {
        if (items != null && items.size() > 0) {
            this.mNeedsSort = true;
            if (items.size() == 1) {
                play((Animator) items.get(0));
                return;
            }
            this.mReversible = false;
            for (int i = 0; i < items.size() - 1; i++) {
                play((Animator) items.get(i)).before((Animator) items.get(i + 1));
            }
        }
    }

    public ArrayList<Animator> getChildAnimations() {
        ArrayList<Animator> childList = new ArrayList();
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            childList.add(((Node) i$.next()).animation);
        }
        return childList;
    }

    public void setTarget(Object target) {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Animator animation = ((Node) i$.next()).animation;
            if (animation instanceof AnimatorSet) {
                ((AnimatorSet) animation).setTarget(target);
            } else if (animation instanceof ObjectAnimator) {
                ((ObjectAnimator) animation).setTarget(target);
            }
        }
    }

    public int getChangingConfigurations() {
        int conf = super.getChangingConfigurations();
        for (int i = 0; i < this.mNodes.size(); i++) {
            conf |= ((Node) this.mNodes.get(i)).animation.getChangingConfigurations();
        }
        return conf;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public Builder play(Animator anim) {
        if (anim == null) {
            return null;
        }
        this.mNeedsSort = true;
        return new Builder(this, anim);
    }

    public void cancel() {
        this.mTerminated = true;
        if (isStarted()) {
            Iterator i$;
            ArrayList<AnimatorListener> tmpListeners = null;
            if (this.mListeners != null) {
                tmpListeners = (ArrayList) this.mListeners.clone();
                i$ = tmpListeners.iterator();
                while (i$.hasNext()) {
                    ((AnimatorListener) i$.next()).onAnimationCancel(this);
                }
            }
            if (this.mDelayAnim != null && this.mDelayAnim.isRunning()) {
                this.mDelayAnim.cancel();
            } else if (this.mSortedNodes.size() > 0) {
                i$ = this.mSortedNodes.iterator();
                while (i$.hasNext()) {
                    ((Node) i$.next()).animation.cancel();
                }
            }
            if (tmpListeners != null) {
                i$ = tmpListeners.iterator();
                while (i$.hasNext()) {
                    ((AnimatorListener) i$.next()).onAnimationEnd(this);
                }
            }
            this.mStarted = false;
        }
    }

    public void end() {
        this.mTerminated = true;
        if (isStarted()) {
            Iterator i$;
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                sortNodes();
                i$ = this.mSortedNodes.iterator();
                while (i$.hasNext()) {
                    Node node = (Node) i$.next();
                    if (this.mSetListener == null) {
                        this.mSetListener = new AnimatorSetListener(this, this);
                    }
                    node.animation.addListener(this.mSetListener);
                }
            }
            if (this.mDelayAnim != null) {
                this.mDelayAnim.cancel();
            }
            if (this.mSortedNodes.size() > 0) {
                i$ = this.mSortedNodes.iterator();
                while (i$.hasNext()) {
                    ((Node) i$.next()).animation.end();
                }
            }
            if (this.mListeners != null) {
                i$ = ((ArrayList) this.mListeners.clone()).iterator();
                while (i$.hasNext()) {
                    ((AnimatorListener) i$.next()).onAnimationEnd(this);
                }
            }
            this.mStarted = false;
        }
    }

    public boolean isRunning() {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            if (((Node) i$.next()).animation.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public void setStartDelay(long startDelay) {
        if (this.mStartDelay > 0) {
            this.mReversible = false;
        }
        this.mStartDelay = startDelay;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public AnimatorSet m21setDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("duration must be a value of zero or greater");
        }
        this.mDuration = duration;
        return this;
    }

    public void setupStartValues() {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            ((Node) i$.next()).animation.setupStartValues();
        }
    }

    public void setupEndValues() {
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            ((Node) i$.next()).animation.setupEndValues();
        }
    }

    public void pause() {
        boolean previouslyPaused = this.mPaused;
        super.pause();
        if (!previouslyPaused && this.mPaused) {
            if (this.mDelayAnim != null) {
                this.mDelayAnim.pause();
                return;
            }
            Iterator i$ = this.mNodes.iterator();
            while (i$.hasNext()) {
                ((Node) i$.next()).animation.pause();
            }
        }
    }

    public void resume() {
        boolean previouslyPaused = this.mPaused;
        super.resume();
        if (previouslyPaused && !this.mPaused) {
            if (this.mDelayAnim != null) {
                this.mDelayAnim.resume();
                return;
            }
            Iterator i$ = this.mNodes.iterator();
            while (i$.hasNext()) {
                ((Node) i$.next()).animation.resume();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void start() {
        /*
        r22 = this;
        r17 = 0;
        r0 = r17;
        r1 = r22;
        r1.mTerminated = r0;
        r17 = 1;
        r0 = r17;
        r1 = r22;
        r1.mStarted = r0;
        r17 = 0;
        r0 = r17;
        r1 = r22;
        r1.mPaused = r0;
        r0 = r22;
        r0 = r0.mNodes;
        r17 = r0;
        r7 = r17.iterator();
    L_0x0022:
        r17 = r7.hasNext();
        if (r17 == 0) goto L_0x0038;
    L_0x0028:
        r10 = r7.next();
        r10 = (android.animation.AnimatorSet.Node) r10;
        r0 = r10.animation;
        r17 = r0;
        r18 = 0;
        r17.setAllowRunningAsynchronously(r18);
        goto L_0x0022;
    L_0x0038:
        r0 = r22;
        r0 = r0.mDuration;
        r18 = r0;
        r20 = 0;
        r17 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r17 < 0) goto L_0x0068;
    L_0x0044:
        r0 = r22;
        r0 = r0.mNodes;
        r17 = r0;
        r7 = r17.iterator();
    L_0x004e:
        r17 = r7.hasNext();
        if (r17 == 0) goto L_0x0068;
    L_0x0054:
        r10 = r7.next();
        r10 = (android.animation.AnimatorSet.Node) r10;
        r0 = r10.animation;
        r17 = r0;
        r0 = r22;
        r0 = r0.mDuration;
        r18 = r0;
        r17.setDuration(r18);
        goto L_0x004e;
    L_0x0068:
        r0 = r22;
        r0 = r0.mInterpolator;
        r17 = r0;
        if (r17 == 0) goto L_0x0094;
    L_0x0070:
        r0 = r22;
        r0 = r0.mNodes;
        r17 = r0;
        r7 = r17.iterator();
    L_0x007a:
        r17 = r7.hasNext();
        if (r17 == 0) goto L_0x0094;
    L_0x0080:
        r10 = r7.next();
        r10 = (android.animation.AnimatorSet.Node) r10;
        r0 = r10.animation;
        r17 = r0;
        r0 = r22;
        r0 = r0.mInterpolator;
        r18 = r0;
        r17.setInterpolator(r18);
        goto L_0x007a;
    L_0x0094:
        r22.sortNodes();
        r0 = r22;
        r0 = r0.mSortedNodes;
        r17 = r0;
        r14 = r17.size();
        r6 = 0;
    L_0x00a2:
        if (r6 >= r14) goto L_0x00f0;
    L_0x00a4:
        r0 = r22;
        r0 = r0.mSortedNodes;
        r17 = r0;
        r0 = r17;
        r10 = r0.get(r6);
        r10 = (android.animation.AnimatorSet.Node) r10;
        r0 = r10.animation;
        r17 = r0;
        r15 = r17.getListeners();
        if (r15 == 0) goto L_0x00ed;
    L_0x00bc:
        r17 = r15.size();
        if (r17 <= 0) goto L_0x00ed;
    L_0x00c2:
        r4 = new java.util.ArrayList;
        r4.<init>(r15);
        r7 = r4.iterator();
    L_0x00cb:
        r17 = r7.hasNext();
        if (r17 == 0) goto L_0x00ed;
    L_0x00d1:
        r9 = r7.next();
        r9 = (android.animation.Animator.AnimatorListener) r9;
        r0 = r9 instanceof android.animation.AnimatorSet.DependencyListener;
        r17 = r0;
        if (r17 != 0) goto L_0x00e3;
    L_0x00dd:
        r0 = r9 instanceof android.animation.AnimatorSet.AnimatorSetListener;
        r17 = r0;
        if (r17 == 0) goto L_0x00cb;
    L_0x00e3:
        r0 = r10.animation;
        r17 = r0;
        r0 = r17;
        r0.removeListener(r9);
        goto L_0x00cb;
    L_0x00ed:
        r6 = r6 + 1;
        goto L_0x00a2;
    L_0x00f0:
        r11 = new java.util.ArrayList;
        r11.<init>();
        r6 = 0;
    L_0x00f6:
        if (r6 >= r14) goto L_0x0187;
    L_0x00f8:
        r0 = r22;
        r0 = r0.mSortedNodes;
        r17 = r0;
        r0 = r17;
        r10 = r0.get(r6);
        r10 = (android.animation.AnimatorSet.Node) r10;
        r0 = r22;
        r0 = r0.mSetListener;
        r17 = r0;
        if (r17 != 0) goto L_0x011f;
    L_0x010e:
        r17 = new android.animation.AnimatorSet$AnimatorSetListener;
        r0 = r17;
        r1 = r22;
        r2 = r22;
        r0.<init>(r1, r2);
        r0 = r17;
        r1 = r22;
        r1.mSetListener = r0;
    L_0x011f:
        r0 = r10.dependencies;
        r17 = r0;
        if (r17 == 0) goto L_0x012f;
    L_0x0125:
        r0 = r10.dependencies;
        r17 = r0;
        r17 = r17.size();
        if (r17 != 0) goto L_0x0142;
    L_0x012f:
        r11.add(r10);
    L_0x0132:
        r0 = r10.animation;
        r17 = r0;
        r0 = r22;
        r0 = r0.mSetListener;
        r18 = r0;
        r17.addListener(r18);
        r6 = r6 + 1;
        goto L_0x00f6;
    L_0x0142:
        r0 = r10.dependencies;
        r17 = r0;
        r12 = r17.size();
        r8 = 0;
    L_0x014b:
        if (r8 >= r12) goto L_0x0178;
    L_0x014d:
        r0 = r10.dependencies;
        r17 = r0;
        r0 = r17;
        r5 = r0.get(r8);
        r5 = (android.animation.AnimatorSet.Dependency) r5;
        r0 = r5.node;
        r17 = r0;
        r0 = r17;
        r0 = r0.animation;
        r17 = r0;
        r18 = new android.animation.AnimatorSet$DependencyListener;
        r0 = r5.rule;
        r19 = r0;
        r0 = r18;
        r1 = r22;
        r2 = r19;
        r0.<init>(r1, r10, r2);
        r17.addListener(r18);
        r8 = r8 + 1;
        goto L_0x014b;
    L_0x0178:
        r0 = r10.dependencies;
        r17 = r0;
        r17 = r17.clone();
        r17 = (java.util.ArrayList) r17;
        r0 = r17;
        r10.tmpDependencies = r0;
        goto L_0x0132;
    L_0x0187:
        r0 = r22;
        r0 = r0.mStartDelay;
        r18 = r0;
        r20 = 0;
        r17 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r17 > 0) goto L_0x01b8;
    L_0x0193:
        r7 = r11.iterator();
    L_0x0197:
        r17 = r7.hasNext();
        if (r17 == 0) goto L_0x01f7;
    L_0x019d:
        r10 = r7.next();
        r10 = (android.animation.AnimatorSet.Node) r10;
        r0 = r10.animation;
        r17 = r0;
        r17.start();
        r0 = r22;
        r0 = r0.mPlayingSet;
        r17 = r0;
        r0 = r10.animation;
        r18 = r0;
        r17.add(r18);
        goto L_0x0197;
    L_0x01b8:
        r17 = 2;
        r0 = r17;
        r0 = new float[r0];
        r17 = r0;
        r17 = {0, 1065353216};
        r17 = android.animation.ValueAnimator.ofFloat(r17);
        r0 = r17;
        r1 = r22;
        r1.mDelayAnim = r0;
        r0 = r22;
        r0 = r0.mDelayAnim;
        r17 = r0;
        r0 = r22;
        r0 = r0.mStartDelay;
        r18 = r0;
        r17.setDuration(r18);
        r0 = r22;
        r0 = r0.mDelayAnim;
        r17 = r0;
        r18 = new android.animation.AnimatorSet$1;
        r0 = r18;
        r1 = r22;
        r0.<init>(r1, r11);
        r17.addListener(r18);
        r0 = r22;
        r0 = r0.mDelayAnim;
        r17 = r0;
        r17.start();
    L_0x01f7:
        r0 = r22;
        r0 = r0.mListeners;
        r17 = r0;
        if (r17 == 0) goto L_0x0224;
    L_0x01ff:
        r0 = r22;
        r0 = r0.mListeners;
        r17 = r0;
        r16 = r17.clone();
        r16 = (java.util.ArrayList) r16;
        r13 = r16.size();
        r6 = 0;
    L_0x0210:
        if (r6 >= r13) goto L_0x0224;
    L_0x0212:
        r0 = r16;
        r17 = r0.get(r6);
        r17 = (android.animation.Animator.AnimatorListener) r17;
        r0 = r17;
        r1 = r22;
        r0.onAnimationStart(r1);
        r6 = r6 + 1;
        goto L_0x0210;
    L_0x0224:
        r0 = r22;
        r0 = r0.mNodes;
        r17 = r0;
        r17 = r17.size();
        if (r17 != 0) goto L_0x0271;
    L_0x0230:
        r0 = r22;
        r0 = r0.mStartDelay;
        r18 = r0;
        r20 = 0;
        r17 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1));
        if (r17 != 0) goto L_0x0271;
    L_0x023c:
        r17 = 0;
        r0 = r17;
        r1 = r22;
        r1.mStarted = r0;
        r0 = r22;
        r0 = r0.mListeners;
        r17 = r0;
        if (r17 == 0) goto L_0x0271;
    L_0x024c:
        r0 = r22;
        r0 = r0.mListeners;
        r17 = r0;
        r16 = r17.clone();
        r16 = (java.util.ArrayList) r16;
        r13 = r16.size();
        r6 = 0;
    L_0x025d:
        if (r6 >= r13) goto L_0x0271;
    L_0x025f:
        r0 = r16;
        r17 = r0.get(r6);
        r17 = (android.animation.Animator.AnimatorListener) r17;
        r0 = r17;
        r1 = r22;
        r0.onAnimationEnd(r1);
        r6 = r6 + 1;
        goto L_0x025d;
    L_0x0271:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.animation.AnimatorSet.start():void");
    }

    public AnimatorSet m19clone() {
        int n;
        int i;
        AnimatorSet anim = (AnimatorSet) super.clone();
        int nodeCount = this.mNodes.size();
        anim.mNeedsSort = true;
        anim.mTerminated = false;
        anim.mStarted = false;
        anim.mPlayingSet = new ArrayList();
        anim.mNodeMap = new HashMap();
        anim.mNodes = new ArrayList(nodeCount);
        anim.mSortedNodes = new ArrayList(nodeCount);
        anim.mReversible = this.mReversible;
        anim.mSetListener = null;
        for (n = 0; n < nodeCount; n++) {
            Node node = (Node) this.mNodes.get(n);
            Node nodeClone = node.clone();
            node.mTmpClone = nodeClone;
            anim.mNodes.add(nodeClone);
            anim.mNodeMap.put(nodeClone.animation, nodeClone);
            nodeClone.dependencies = null;
            nodeClone.tmpDependencies = null;
            nodeClone.nodeDependents = null;
            nodeClone.nodeDependencies = null;
            ArrayList<AnimatorListener> cloneListeners = nodeClone.animation.getListeners();
            if (cloneListeners != null) {
                for (i = cloneListeners.size() - 1; i >= 0; i--) {
                    if (((AnimatorListener) cloneListeners.get(i)) instanceof AnimatorSetListener) {
                        cloneListeners.remove(i);
                    }
                }
            }
        }
        for (n = 0; n < nodeCount; n++) {
            Iterator i$;
            node = (Node) this.mNodes.get(n);
            Node clone = node.mTmpClone;
            if (node.dependencies != null) {
                clone.dependencies = new ArrayList(node.dependencies.size());
                int depSize = node.dependencies.size();
                for (i = 0; i < depSize; i++) {
                    Dependency dependency = (Dependency) node.dependencies.get(i);
                    clone.dependencies.add(new Dependency(dependency.node.mTmpClone, dependency.rule));
                }
            }
            if (node.nodeDependents != null) {
                clone.nodeDependents = new ArrayList(node.nodeDependents.size());
                i$ = node.nodeDependents.iterator();
                while (i$.hasNext()) {
                    clone.nodeDependents.add(((Node) i$.next()).mTmpClone);
                }
            }
            if (node.nodeDependencies != null) {
                clone.nodeDependencies = new ArrayList(node.nodeDependencies.size());
                i$ = node.nodeDependencies.iterator();
                while (i$.hasNext()) {
                    clone.nodeDependencies.add(((Node) i$.next()).mTmpClone);
                }
            }
        }
        for (n = 0; n < nodeCount; n++) {
            ((Node) this.mNodes.get(n)).mTmpClone = null;
        }
        return anim;
    }

    private void sortNodes() {
        int numNodes;
        int i;
        Node node;
        int j;
        if (this.mNeedsSort) {
            this.mSortedNodes.clear();
            ArrayList<Node> roots = new ArrayList();
            numNodes = this.mNodes.size();
            for (i = 0; i < numNodes; i++) {
                node = (Node) this.mNodes.get(i);
                if (node.dependencies == null || node.dependencies.size() == 0) {
                    roots.add(node);
                }
            }
            ArrayList<Node> tmpRoots = new ArrayList();
            while (roots.size() > 0) {
                int numRoots = roots.size();
                for (i = 0; i < numRoots; i++) {
                    Node root = (Node) roots.get(i);
                    this.mSortedNodes.add(root);
                    if (root.nodeDependents != null) {
                        int numDependents = root.nodeDependents.size();
                        for (j = 0; j < numDependents; j++) {
                            node = (Node) root.nodeDependents.get(j);
                            node.nodeDependencies.remove(root);
                            if (node.nodeDependencies.size() == 0) {
                                tmpRoots.add(node);
                            }
                        }
                    }
                }
                roots.clear();
                roots.addAll(tmpRoots);
                tmpRoots.clear();
            }
            this.mNeedsSort = false;
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                throw new IllegalStateException("Circular dependencies cannot exist in AnimatorSet");
            }
            return;
        }
        numNodes = this.mNodes.size();
        for (i = 0; i < numNodes; i++) {
            node = (Node) this.mNodes.get(i);
            if (node.dependencies != null && node.dependencies.size() > 0) {
                int numDependencies = node.dependencies.size();
                for (j = 0; j < numDependencies; j++) {
                    Dependency dependency = (Dependency) node.dependencies.get(j);
                    if (node.nodeDependencies == null) {
                        node.nodeDependencies = new ArrayList();
                    }
                    if (!node.nodeDependencies.contains(dependency.node)) {
                        node.nodeDependencies.add(dependency.node);
                    }
                }
            }
            node.done = false;
        }
    }

    public boolean canReverse() {
        if (!this.mReversible) {
            return false;
        }
        Iterator i$ = this.mNodes.iterator();
        while (i$.hasNext()) {
            Node node = (Node) i$.next();
            if (!node.animation.canReverse()) {
                return false;
            }
            if (node.animation.getStartDelay() > 0) {
                return false;
            }
        }
        return true;
    }

    public void reverse() {
        if (canReverse()) {
            Iterator i$ = this.mNodes.iterator();
            while (i$.hasNext()) {
                ((Node) i$.next()).animation.reverse();
            }
        }
    }
}
