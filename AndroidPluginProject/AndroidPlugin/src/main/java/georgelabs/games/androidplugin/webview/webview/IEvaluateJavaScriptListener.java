package com.voxelbusters.android.essentialkit.features.webview;

interface IEvaluateJavaScriptListener
{
    void onSuccess(String result);
    void onFailure(String error);
}
