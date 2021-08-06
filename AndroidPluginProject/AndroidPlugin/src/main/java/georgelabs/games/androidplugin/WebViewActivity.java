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
    public void onCreate(Bundle savedInstanceState) {
        showWebView("https://video-slots.live/liveslots");
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG,"WebView Activity created!");
    }

    @Override
    protected void onDestroy() {
        closeWebView();
        super.onDestroy();
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
