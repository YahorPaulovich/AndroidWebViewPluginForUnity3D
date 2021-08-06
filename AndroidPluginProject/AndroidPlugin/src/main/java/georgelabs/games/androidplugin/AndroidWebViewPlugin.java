package georgelabs.games.androidplugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class AndroidWebViewPlugin {
    private static final AndroidWebViewPlugin ourInstance = new AndroidWebViewPlugin();

    protected static final String LOGTAG = "AndroidWebViewPlugin";

    public static AndroidWebViewPlugin getInstance() {
        return ourInstance;
    }
    //public static Activity mainActivity;

    private long startTime;
//    private LinearLayout webLayout;
//    private WebView webView;

    private AndroidWebViewPlugin() {
        Log.i(LOGTAG,"Created Android WebView Plugin!");
        startTime = System.currentTimeMillis();
    }

    public double getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }

//    public static void CallWebViewActivity(Activity activity)
//    {
//        // Creating an intent with the current activity and the activity we wish to start
//        Intent myIntent = new Intent(activity, WebViewActivity.class);
//        activity.startActivity(myIntent);
//    }

//    public void showWebView(final String webURL) {
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(LOGTAG,"Want to open WebView for " + webURL);
//                if (webLayout==null)
//                    webLayout = new LinearLayout(mainActivity);
//                webLayout.setOrientation(LinearLayout.VERTICAL);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                mainActivity.addContentView(webLayout,layoutParams);
//                if (webView==null)
//                    webView = new WebView(mainActivity);
//                webView.setWebViewClient(new WebViewClient());
//                layoutParams.weight = 1.0f;
//                webView.setLayoutParams(layoutParams);
//                webView.loadUrl(webURL);
//
//                webView.setWebChromeClient(new WebChromeClient());
//                WebSettings webSettings = webView.getSettings();
//                webSettings.setJavaScriptEnabled(true);
//                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//                webSettings.setPluginState(WebSettings.PluginState.ON);
//                webSettings.setMediaPlaybackRequiresUserGesture(true);
//
//                webLayout.addView(webView);
//            }
//        });
//    }
//
//    public void closeWebView() {
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (webLayout!=null) {
//                    webLayout.removeAllViews();
//                    webLayout.setVisibility(View.GONE);
//                    webLayout = null;
//                    webView = null;
//                }
//            }
//        });
//    }

}