package georgelabs.games.androidplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.unity3d.player.UnityPlayer;


public class AndroidWebViewPlugin extends UnityPlayerActivity {
    private static final AndroidWebViewPlugin ourInstance = new AndroidWebViewPlugin();

    protected static final String LOGTAG = "AndroidWebViewPlugin";
    public static String webUrl = "https://video-slots.live/liveslots";
    public static AndroidWebViewPlugin getInstance() {
        return ourInstance;
    }
    public static Activity mainActivity;

    private long startTime;
    private LinearLayout webLayout;
    private WebView webView;

    private AndroidWebViewPlugin() {
        Log.i(LOGTAG,"Created Android WebView Plugin!");
        startTime = System.currentTimeMillis();
    }

    public static String GameObject, MethodName, Message;

    public void SetupCallBack(String gameObject, String methodName, String message) {
        Log.d("AndroidWebViewPlugin", "SetupCallBack");
        GameObject = gameObject;
        MethodName = methodName;
        Message = message;
    }

    public static void CallBackToUnity() {
        Log.d("AndroidWebViewPlugin", "CallbackToUnity");
        UnityPlayer.UnitySendMessage(GameObject, MethodName, Message);
    }

    public double getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

//    public static void CallWebViewActivity(Activity activity) {
//        // Creating an intent with the current activity and the activity we wish to start
//        Intent myIntent = new Intent(activity, WebViewActivity.class);
//        activity.startActivity(myIntent);
//    }

    public void OpenWebView(final String webURL) {
        this.webUrl = webURL;
        AndroidWebViewPlugin mainActivity = this;
        Intent intent = new Intent(mainActivity, WebViewActivity.class);
        startActivity(intent);
    }

    public void showWebView(final String webURL) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOGTAG,"Want to open WebView for " + webURL);

                if (webLayout==null)
                    webLayout = new LinearLayout(mainActivity);
                webLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                webLayout.setFocusable(true);
                webLayout.setFocusableInTouchMode(true);
                mainActivity.addContentView(webLayout,layoutParams);

                if (webView==null)
                    webView = new WebView(mainActivity);
                webView.setWebViewClient(new WebViewClient());
                layoutParams.weight = 1.0f;
                webView.setLayoutParams(layoutParams);
                webView.loadUrl(webURL);

                webView.setWebChromeClient(new WebChromeClient());
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                webSettings.setPluginState(WebSettings.PluginState.ON);
                webSettings.setMediaPlaybackRequiresUserGesture(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setDomStorageEnabled(true);

                webLayout.addView(webView);
            }
        });
    }

    public void closeWebView() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (webLayout!=null) {
                    webLayout.removeAllViews();
                    webLayout.setVisibility(View.GONE);
                    webLayout = null;
                    webView = null;
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}