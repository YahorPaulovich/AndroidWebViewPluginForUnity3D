package georgelabs.games.androidplugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;


public class WebViewActivity extends Activity {

    protected static final String LOGTAG = "WebViewActivity";
    public Activity webViewActivity;

    private LinearLayout webLayout;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // call UnityPlayerActivity.onCreate()
        super.onCreate(savedInstanceState);

        // print debug message to logcat
        Log.d("OverrideActivity", "onCreate called!");

        showWebView(AndroidWebViewPlugin.webUrl);
    }

    public void onBackPressed() {
        // instead of calling UnityPlayerActivity.onBackPressed() we just ignore the back button event
        // super.onBackPressed();

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            //AndroidWebViewPlugin.CallBackToUnity();
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeWebView();
    }

    public void showWebView(final String webURL) {
        webViewActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOGTAG,"Want to open WebView for " + webURL);

                if (webLayout==null)
                    webLayout = new LinearLayout(webViewActivity);
                webLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                webViewActivity.addContentView(webLayout,layoutParams);
                if (webView==null)
                    webView = new WebView(webViewActivity);
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
        webViewActivity.runOnUiThread(new Runnable() {
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
}
