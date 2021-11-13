package com.voxelbusters.android.essentialkit.features.webview;

import android.webkit.ValueCallback;

import java.io.Serializable;
public class SerialisedValueCallback<T> implements Serializable
{

    ValueCallback<T> cachedCallback = null;

    public SerialisedValueCallback(ValueCallback<T> callback)
    {
        cachedCallback = callback;
    }

    public void onReceiveValue(T value)
    {
        cachedCallback.onReceiveValue(value);
    }
}
