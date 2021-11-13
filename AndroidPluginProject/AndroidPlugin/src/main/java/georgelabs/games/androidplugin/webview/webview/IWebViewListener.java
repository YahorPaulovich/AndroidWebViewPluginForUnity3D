package com.voxelbusters.android.essentialkit.features.webview;

public interface IWebViewListener
{
    void onPageLoadStarted();

    void onPageLoadFinished(String url);

    void onPageLoadError(String failingUrl, String description);

    void onMessageReceived(WebViewMessage message);

    void onShow();
    void onHide();
}
