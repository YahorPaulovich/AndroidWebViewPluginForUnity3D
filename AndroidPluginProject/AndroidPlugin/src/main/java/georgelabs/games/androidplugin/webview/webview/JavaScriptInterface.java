package com.voxelbusters.android.essentialkit.features.webview;

import android.webkit.JavascriptInterface;
public class JavaScriptInterface
{
    String tag;

    public JavaScriptInterface(String tag)
    {
        this.tag = tag;
    }

    @JavascriptInterface
    public void sendMessage(String result)
    {
        WebkitWebView webView = NativeWebViewStore.getInstance().get(tag);
        if (webView != null)
        {
            webView.sendJsEvaluationMessage(result);
        }
    }

    @JavascriptInterface
    public void passToUnity(String result) //Backward compatibility
    {
        sendMessage(result);
    }
}