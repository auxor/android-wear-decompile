package android.webkit;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Message;
import android.print.PrintDocumentAdapter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView.FindListener;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebView.PictureListener;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Map;

public interface WebViewProvider {

    public interface ScrollDelegate {
        int computeHorizontalScrollOffset();

        int computeHorizontalScrollRange();

        void computeScroll();

        int computeVerticalScrollExtent();

        int computeVerticalScrollOffset();

        int computeVerticalScrollRange();
    }

    public interface ViewDelegate {
        boolean dispatchKeyEvent(KeyEvent keyEvent);

        AccessibilityNodeProvider getAccessibilityNodeProvider();

        void onAttachedToWindow();

        void onConfigurationChanged(Configuration configuration);

        InputConnection onCreateInputConnection(EditorInfo editorInfo);

        void onDetachedFromWindow();

        void onDraw(Canvas canvas);

        void onDrawVerticalScrollBar(Canvas canvas, Drawable drawable, int i, int i2, int i3, int i4);

        void onFinishTemporaryDetach();

        void onFocusChanged(boolean z, int i, Rect rect);

        boolean onGenericMotionEvent(MotionEvent motionEvent);

        boolean onHoverEvent(MotionEvent motionEvent);

        void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo);

        boolean onKeyDown(int i, KeyEvent keyEvent);

        boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent);

        boolean onKeyUp(int i, KeyEvent keyEvent);

        void onMeasure(int i, int i2);

        void onOverScrolled(int i, int i2, boolean z, boolean z2);

        void onScrollChanged(int i, int i2, int i3, int i4);

        void onSizeChanged(int i, int i2, int i3, int i4);

        void onStartTemporaryDetach();

        boolean onTouchEvent(MotionEvent motionEvent);

        boolean onTrackballEvent(MotionEvent motionEvent);

        void onVisibilityChanged(View view, int i);

        void onWindowFocusChanged(boolean z);

        void onWindowVisibilityChanged(int i);

        boolean performAccessibilityAction(int i, Bundle bundle);

        boolean performLongClick();

        void preDispatchDraw(Canvas canvas);

        boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z);

        boolean requestFocus(int i, Rect rect);

        void setBackgroundColor(int i);

        boolean setFrame(int i, int i2, int i3, int i4);

        void setLayerType(int i, Paint paint);

        void setLayoutParams(LayoutParams layoutParams);

        void setOverScrollMode(int i);

        void setScrollBarStyle(int i);

        boolean shouldDelayChildPressedState();
    }

    void addJavascriptInterface(Object obj, String str);

    boolean canGoBack();

    boolean canGoBackOrForward(int i);

    boolean canGoForward();

    boolean canZoomIn();

    boolean canZoomOut();

    Picture capturePicture();

    void clearCache(boolean z);

    void clearFormData();

    void clearHistory();

    void clearMatches();

    void clearSslPreferences();

    void clearView();

    WebBackForwardList copyBackForwardList();

    PrintDocumentAdapter createPrintDocumentAdapter(String str);

    void destroy();

    void documentHasImages(Message message);

    void dumpViewHierarchyWithProperties(BufferedWriter bufferedWriter, int i);

    void evaluateJavaScript(String str, ValueCallback<String> valueCallback);

    int findAll(String str);

    void findAllAsync(String str);

    View findHierarchyView(String str, int i);

    void findNext(boolean z);

    void flingScroll(int i, int i2);

    void freeMemory();

    SslCertificate getCertificate();

    int getContentHeight();

    int getContentWidth();

    Bitmap getFavicon();

    HitTestResult getHitTestResult();

    String[] getHttpAuthUsernamePassword(String str, String str2);

    String getOriginalUrl();

    int getProgress();

    float getScale();

    ScrollDelegate getScrollDelegate();

    WebSettings getSettings();

    String getTitle();

    String getTouchIconUrl();

    String getUrl();

    ViewDelegate getViewDelegate();

    int getVisibleTitleHeight();

    View getZoomControls();

    void goBack();

    void goBackOrForward(int i);

    void goForward();

    void init(Map<String, Object> map, boolean z);

    void invokeZoomPicker();

    boolean isPaused();

    boolean isPrivateBrowsingEnabled();

    void loadData(String str, String str2, String str3);

    void loadDataWithBaseURL(String str, String str2, String str3, String str4, String str5);

    void loadUrl(String str);

    void loadUrl(String str, Map<String, String> map);

    void notifyFindDialogDismissed();

    void onPause();

    void onResume();

    boolean overlayHorizontalScrollbar();

    boolean overlayVerticalScrollbar();

    boolean pageDown(boolean z);

    boolean pageUp(boolean z);

    void pauseTimers();

    void postUrl(String str, byte[] bArr);

    void reload();

    void removeJavascriptInterface(String str);

    void requestFocusNodeHref(Message message);

    void requestImageRef(Message message);

    boolean restorePicture(Bundle bundle, File file);

    WebBackForwardList restoreState(Bundle bundle);

    void resumeTimers();

    void savePassword(String str, String str2, String str3);

    boolean savePicture(Bundle bundle, File file);

    WebBackForwardList saveState(Bundle bundle);

    void saveWebArchive(String str);

    void saveWebArchive(String str, boolean z, ValueCallback<String> valueCallback);

    void setCertificate(SslCertificate sslCertificate);

    void setDownloadListener(DownloadListener downloadListener);

    void setFindListener(FindListener findListener);

    void setHorizontalScrollbarOverlay(boolean z);

    void setHttpAuthUsernamePassword(String str, String str2, String str3, String str4);

    void setInitialScale(int i);

    void setMapTrackballToArrowKeys(boolean z);

    void setNetworkAvailable(boolean z);

    void setPictureListener(PictureListener pictureListener);

    void setVerticalScrollbarOverlay(boolean z);

    void setWebChromeClient(WebChromeClient webChromeClient);

    void setWebViewClient(WebViewClient webViewClient);

    boolean showFindDialog(String str, boolean z);

    void stopLoading();

    boolean zoomBy(float f);

    boolean zoomIn();

    boolean zoomOut();
}
