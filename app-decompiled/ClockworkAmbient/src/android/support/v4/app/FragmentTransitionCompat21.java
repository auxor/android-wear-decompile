package android.support.v4.app;

import android.graphics.Rect;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class FragmentTransitionCompat21 {

    public interface ViewRetriever {
        View getView();
    }

    /* renamed from: android.support.v4.app.FragmentTransitionCompat21.1 */
    static final class AnonymousClass1 extends EpicenterCallback {
        final /* synthetic */ Rect val$epicenter;

        AnonymousClass1(Rect rect) {
            this.val$epicenter = rect;
            throw new VerifyError("bad dex opcode");
        }

        public Rect onGetEpicenter(Transition transition) {
            return this.val$epicenter;
        }
    }

    /* renamed from: android.support.v4.app.FragmentTransitionCompat21.2 */
    static final class AnonymousClass2 implements OnPreDrawListener {
        final /* synthetic */ View val$container;
        final /* synthetic */ Transition val$enterTransition;
        final /* synthetic */ ArrayList val$enteringViews;
        final /* synthetic */ ViewRetriever val$inFragment;
        final /* synthetic */ Map val$nameOverrides;
        final /* synthetic */ View val$nonExistentView;
        final /* synthetic */ Map val$renamedViews;

        AnonymousClass2(View view, ViewRetriever viewRetriever, Map map, Map map2, Transition transition, ArrayList arrayList, View view2) {
            this.val$container = view;
            this.val$inFragment = viewRetriever;
            this.val$nameOverrides = map;
            this.val$renamedViews = map2;
            this.val$enterTransition = transition;
            this.val$enteringViews = arrayList;
            this.val$nonExistentView = view2;
            throw new VerifyError("bad dex opcode");
        }

        public boolean onPreDraw() {
            throw new VerifyError("bad dex opcode");
        }
    }

    /* renamed from: android.support.v4.app.FragmentTransitionCompat21.3 */
    static final class AnonymousClass3 extends EpicenterCallback {
        private Rect mEpicenter;
        final /* synthetic */ EpicenterView val$epicenterView;

        AnonymousClass3(EpicenterView epicenterView) {
            this.val$epicenterView = epicenterView;
            throw new VerifyError("bad dex opcode");
        }

        public Rect onGetEpicenter(Transition transition) {
            if (this.mEpicenter == null && this.val$epicenterView.epicenter != null) {
                this.mEpicenter = FragmentTransitionCompat21.getBoundsOnScreen(this.val$epicenterView.epicenter);
            }
            return this.mEpicenter;
        }
    }

    /* renamed from: android.support.v4.app.FragmentTransitionCompat21.4 */
    static final class AnonymousClass4 implements OnPreDrawListener {
        final /* synthetic */ Transition val$enterTransition;
        final /* synthetic */ ArrayList val$enteringViews;
        final /* synthetic */ Transition val$exitTransition;
        final /* synthetic */ ArrayList val$exitingViews;
        final /* synthetic */ ArrayList val$hiddenViews;
        final /* synthetic */ View val$nonExistentView;
        final /* synthetic */ Transition val$overallTransition;
        final /* synthetic */ Map val$renamedViews;
        final /* synthetic */ View val$sceneRoot;
        final /* synthetic */ ArrayList val$sharedElementTargets;
        final /* synthetic */ Transition val$sharedElementTransition;

        AnonymousClass4(View view, Transition transition, View view2, ArrayList arrayList, Transition transition2, ArrayList arrayList2, Transition transition3, ArrayList arrayList3, Map map, ArrayList arrayList4, Transition transition4) {
            this.val$sceneRoot = view;
            this.val$enterTransition = transition;
            this.val$nonExistentView = view2;
            this.val$enteringViews = arrayList;
            this.val$exitTransition = transition2;
            this.val$exitingViews = arrayList2;
            this.val$sharedElementTransition = transition3;
            this.val$sharedElementTargets = arrayList3;
            this.val$renamedViews = map;
            this.val$hiddenViews = arrayList4;
            this.val$overallTransition = transition4;
            throw new VerifyError("bad dex opcode");
        }

        public boolean onPreDraw() {
            throw new VerifyError("bad dex opcode");
        }
    }

    public static class EpicenterView {
        public View epicenter;
    }

    public static void addTargets(Object obj, ArrayList<View> arrayList) {
        Transition transition = (Transition) obj;
        if (transition instanceof TransitionSet) {
            throw new VerifyError("bad dex opcode");
        } else if (!hasSimpleTarget(transition)) {
            throw new VerifyError("bad dex opcode");
        }
    }

    public static void addTransitionTargets(Object obj, Object obj2, View view, ViewRetriever viewRetriever, View view2, EpicenterView epicenterView, Map<String, String> map, ArrayList<View> arrayList, Map<String, View> map2, ArrayList<View> arrayList2) {
        if (obj != null || obj2 != null) {
            Transition transition = (Transition) obj;
            if (transition != null) {
                throw new VerifyError("bad dex opcode");
            }
            if (obj2 != null) {
                addTargets((Transition) obj2, arrayList2);
            }
            if (viewRetriever != null) {
                throw new VerifyError("bad dex opcode");
            }
            setSharedElementEpicenter(transition, epicenterView);
        }
    }

    public static void beginDelayedTransition(ViewGroup viewGroup, Object obj) {
        TransitionManager.beginDelayedTransition(viewGroup, (Transition) obj);
    }

    public static Object captureExitingViews(Object obj, View view, ArrayList<View> arrayList, Map<String, View> map, View view2) {
        if (obj == null) {
            return obj;
        }
        captureTransitioningViews(arrayList, view);
        if (map != null) {
            map.values();
            throw new VerifyError("bad dex opcode");
        }
        throw new VerifyError("bad dex opcode");
    }

    private static void captureTransitioningViews(ArrayList<View> arrayList, View view) {
        throw new VerifyError("bad dex opcode");
    }

    public static void cleanupTransitions(View view, View view2, Object obj, ArrayList<View> arrayList, Object obj2, ArrayList<View> arrayList2, Object obj3, ArrayList<View> arrayList3, Object obj4, ArrayList<View> arrayList4, Map<String, View> map) {
        Transition transition = (Transition) obj;
        Transition transition2 = (Transition) obj2;
        Transition transition3 = (Transition) obj3;
        if (((Transition) obj4) != null) {
            throw new VerifyError("bad dex opcode");
        }
    }

    public static Object cloneTransition(Object obj) {
        if (obj == null) {
            return obj;
        }
        Transition transition = (Transition) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void excludeTarget(Object obj, View view, boolean z) {
        Transition transition = (Transition) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void findNamedViews(Map<String, View> map, View view) {
        throw new VerifyError("bad dex opcode");
    }

    private static Rect getBoundsOnScreen(View view) {
        Rect rect = new Rect();
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        rect.set(iArr[0], iArr[1], iArr[0] + view.getWidth(), iArr[1] + view.getHeight());
        return rect;
    }

    public static String getTransitionName(View view) {
        return view.getTransitionName();
    }

    private static boolean hasSimpleTarget(Transition transition) {
        return (isNullOrEmpty(transition.getTargetIds()) && isNullOrEmpty(transition.getTargetNames()) && isNullOrEmpty(transition.getTargetTypes())) ? false : true;
    }

    private static boolean isNullOrEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static Object mergeTransitions(Object obj, Object obj2, Object obj3, boolean z) {
        Object transitionSet;
        Transition transition = (Transition) obj;
        Transition transition2 = (Transition) obj2;
        Transition transition3 = (Transition) obj3;
        if (transition == null || transition2 == null) {
            z = true;
        }
        if (z) {
            transitionSet = new TransitionSet();
            if (transition != null) {
                throw new VerifyError("bad dex opcode");
            } else if (transition2 != null) {
                throw new VerifyError("bad dex opcode");
            } else if (transition3 != null) {
                throw new VerifyError("bad dex opcode");
            }
        }
        transitionSet = null;
        if (transition2 == null || transition == null) {
            if (transition2 != null) {
                transitionSet = transition2;
            } else if (transition != null) {
                Transition transition4 = transition;
            }
            if (transition3 != null) {
                TransitionSet transitionSet2 = new TransitionSet();
                if (transitionSet != null) {
                    throw new VerifyError("bad dex opcode");
                }
                throw new VerifyError("bad dex opcode");
            }
        }
        TransitionSet transitionSet3 = new TransitionSet();
        throw new VerifyError("bad dex opcode");
        return transitionSet;
    }

    public static void removeTargets(Object obj, ArrayList<View> arrayList) {
        Transition transition = (Transition) obj;
        if (transition instanceof TransitionSet) {
            throw new VerifyError("bad dex opcode");
        } else if (!hasSimpleTarget(transition)) {
            throw new VerifyError("bad dex opcode");
        }
    }

    public static void setEpicenter(Object obj, View view) {
        Transition transition = (Transition) obj;
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(getBoundsOnScreen(view));
        throw new VerifyError("bad dex opcode");
    }

    private static void setSharedElementEpicenter(Transition transition, EpicenterView epicenterView) {
        if (transition != null) {
            transition.setEpicenterCallback(new AnonymousClass3(epicenterView));
        }
    }
}
